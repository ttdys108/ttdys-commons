package com.ttdys.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
public class CommonUtil {

    public static void quietClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            log.error("关闭<{}>异常", closeable, e);
        }
    }

}
