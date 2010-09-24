package com.cannontech.common.util;

public class ExceptionHelper {

    public static void throwOrWrap(Exception e) throws RuntimeException {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }
    }

}
