package com.ttdys.common.util;

import com.ttdys.common.exception.ErrorCode;
import com.ttdys.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClassUtil {

    private static final Class<?>[] primitiveNumberTypes = {
        byte.class,
        char.class,
        short.class,
        int.class,
        long.class,
        float.class,
        double.class
    };


    /**
     * 同Class.newInstance，只是将异常转为RuntimeException
     */
    public static <T> T newInstance(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            log.error("实例化类<{}>失败", clz, e);
            throw new ServiceException(ErrorCode.CLASS_CONSTRUCT_ERROR);
        }
    }

    /**
     * 获取类的属性map，key值为属性名
     * @param clz 类
     * @param setVisible 是否将Field设置为可见
     * @return Map<String, Field>
     */
    public static Map<String, Field> declaredFieldMap(Class<?> clz, boolean setVisible) {
        Map<String, Field> fieldMap = new HashMap<>();
        Field[] fields = clz.getDeclaredFields();
        for(Field field : fields) {
            if(!field.isAccessible() && setVisible) {
                field.setAccessible(true);
            }
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }

    public static Map<String, Field> declaredFieldMap(Class<?> clz) {
        return declaredFieldMap(clz, false);
    }

    public static boolean isPrimitiveNumber(Class<?> clz) {
        for(Class<?> pri : primitiveNumberTypes) {
            if(pri == clz)
                return true;
        }
        return false;
    }

    public static boolean isNumber(Class<?> clz) {
        return Number.class.isAssignableFrom(clz) || isPrimitiveNumber(clz);
    }

    private ClassUtil() {}
}
