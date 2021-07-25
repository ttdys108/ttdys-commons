package com.ttdys.common.util;

import com.ttdys.common.exception.ErrorCode;
import com.ttdys.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClassUtil {

    /** 通Class.newInstance，将异常转为RuntimeException */
    public static <T> T newInstance(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            log.error("实例化类<{}>失败", clz, e);
            throw new ServiceException(ErrorCode.CLASS_CONSTRUCT_ERROR);
        }
    }

    public static Map<String, Field> declaredFieldMap(Class<?> clz) {
        Map<String, Field> fieldMap = new HashMap<>();
        Field[] fields = clz.getDeclaredFields();
        for(Field field : fields) {
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }


    private ClassUtil() {}
}
