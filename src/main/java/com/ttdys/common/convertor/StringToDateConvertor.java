package com.ttdys.common.convertor;

import com.ttdys.common.consts.CommonConst;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * String到Date类型转换
 */
public class StringToDateConvertor implements GenericConverter {

    private static final Set<ConvertiblePair> convertiblePairs;

    private final DateTimeFormatter dateTimeFormatter;

    public StringToDateConvertor() {
        this(CommonConst.PATTERN_DATETIME);
    }

    public StringToDateConvertor(String datePattern) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
    }

    static {
        convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(String.class, Date.class));
//        convertiblePairs.add(new ConvertiblePair(String.class, LocalDate.class));
//        convertiblePairs.add(new ConvertiblePair(String.class, LocalDateTime.class));
//        convertiblePairs.add(new ConvertiblePair(String.class, LocalTime.class));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String dateStr = (String) source;
        if(targetType.getType() == Date.class) {
            LocalDateTime datetime = LocalDateTime.parse(dateStr, dateTimeFormatter);
            return Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            throw new UnsupportedOperationException("目前仅支持Date类型转换");
        }
    }

    public static void main(String[] args) {
        DefaultConversionService service = new DefaultConversionService();
//        service.addConverter(new StringToDateConvertor());
//        String date = "2021-07-25 16:10:00";
//        Date d = service.convert(date, Date.class);
        String s = "2222";
        char c = service.convert(s, char.class);
        System.out.println(c);
    }
}
