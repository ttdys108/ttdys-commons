package com.ttdys.common.xls;

import com.ttdys.common.Test;
import com.ttdys.common.exception.ErrorCode;
import com.ttdys.common.exception.ServiceException;
import com.ttdys.common.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Excel解析
 * 除非特殊说明，所有方法传递null参数都会抛出NullPointerException
 */
@Slf4j
public class ExcelUtil {
    /** 解析开始行 */
    private static final int content_row_idx = 1;
    /** 模板行 */
    private static final int template_row_idx = 1;
    /** 模板占位符 */
    private static final String template_match_pattern = "^\\{.+}$";

    /**
     * 判断Excel是否为xlsx类型
     * @param file excel
     * @return true/false
     */
    public static boolean isXlsx(File file) {
        try {
            FileMagic fm = FileMagic.valueOf(file);
            return fm == FileMagic.OOXML;
        } catch (IOException e) {
            log.error("获取Excel类型失败", e);
            throw new ServiceException(ErrorCode.EXCEL_PARSE_ERROR);
        }
    }

    /**
     * 解析excel
     * @param excel 解析文件
     * @param template 模板文件
     * @param clz 封装类
     * @param <T> 封装类
     * @return 解析结果
     */
    public static <T> List<T> parse(File excel, File template, Class<T> clz) {
        //解析结果
        List<T> result = new ArrayList<>();
        //模板信息
        List<TemplateDescriptor> descriptors = parseTemplate(template);
        Map<Integer, TemplateDescriptor> tplDescriptorMap = descriptors.stream().collect(Collectors.toMap(TemplateDescriptor::getIndex, Function.identity()));
        //解析文件
        Workbook workbook = getWorkbook(excel);
        Sheet sheet = workbook.getSheetAt(0);
        int maxRow = sheet.getLastRowNum();
        //文件为空判断
        if(maxRow < content_row_idx) {
            log.warn("Excel内容为空");
            return result;
        }
        //开始解析
        Map<String, Field> fieldMap = ClassUtil.declaredFieldMap(clz);
        for(int i = content_row_idx; i <= maxRow; i++) {
            T entity = parseRow(sheet.getRow(i), tplDescriptorMap, fieldMap, clz);
            result.add(entity);
        }
        return result;
    }




    /** 解析模板文件，返回idx-field map */
    private static List<TemplateDescriptor> parseTemplate(File template) {
        List<TemplateDescriptor> descriptors = new ArrayList<>();
        Workbook workbook = getWorkbook(template);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum();
        if(rows < template_row_idx)
            throw new ServiceException(ErrorCode.EXCEL_ROW_EMPTY);
        Row row = sheet.getRow(template_row_idx);
        for(Cell cell : row) {
            //获取属性名
            String fieldName = cell.getStringCellValue();
            if(isBlank(fieldName)) {
                log.warn("模板[{}]列配置为空，ignored！", cell.getColumnIndex());
                continue;
            }
            fieldName = fieldName.trim();
            if(!fieldName.matches(template_match_pattern)) {
                log.warn("模板[{}]列配置有误，请使用{field}格式，ignored！", cell.getColumnIndex());
                continue;
            }
            fieldName = fieldName.substring(1, fieldName.length() - 1).trim();
            //单元格格式
            String format = cell.getCellStyle().getDataFormatString();
            //descriptor
            TemplateDescriptor descriptor = new TemplateDescriptor(cell.getColumnIndex(), fieldName, format);
            descriptors.add(descriptor);
        }
        return descriptors;
    }

    /** 解析一行数据 */
    private static <T> T parseRow(Row row, Map<Integer, TemplateDescriptor> tplFieldMap, Map<String, Field> fieldMap, Class<T> clz) {
        T entity = ClassUtil.newInstance(clz);
        for(Cell cell : row) {
            String fieldName = tplFieldMap.get(cell.getColumnIndex()).getFieldName();
            if(isBlank(fieldName))
                continue;
            Field field = fieldMap.get(fieldName);
            if(field == null)
                continue;
            setFieldWithCell(entity, field, cell);
        }
        return entity;
    }

    /** 根据cell值设置对象属性值，该方法会吞掉解析异常，以保证后面列继续解析 */
    private static <T> void setFieldWithCell(T t, Field field, Cell cell) {
        try {
            Object val = getCellValue(cell);
            if(!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(t, val);
        } catch (Exception e) {
            log.error("解析[{}]列数据报错, continue", cell.getColumnIndex(), e);
        }
    }

    private static Object getCellValue(Cell cell) {
        switch(cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            default:
                return null;
        }
    }

    private static Workbook getWorkbook(File file) {
        try {
            return WorkbookFactory.create(file);
        } catch (IOException e) {
            log.error("创建Workbook失败", e);
            throw new ServiceException(ErrorCode.EXCEL_PARSE_ERROR);
        }
    }



    public static void main(String[] args) throws IOException, InvalidFormatException {
        File tmp = new File("E:\\tmp\\tmp.xlsx");
        File excel = new File("E:\\tmp\\import2.xls");
//        List<Test> res = parse(excel, tmp, Test.class);
//        System.out.println(res);
        String i = "2020-07-25T10:00:00";
        DefaultConversionService conversionService = new DefaultConversionService();
        System.out.println(conversionService.canConvert(String.class, Date.class));
        Date j = conversionService.convert(i, Date.class);
        System.out.println(j);
    }

}
