package com.example.keuangan.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServiceUtil {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected void info(String msg, Object... args) {
        log.info(msg, args);
    }

    protected void error(String msg, Throwable e, Object... args) {
        log.error(msg, args, e);
    }
}
