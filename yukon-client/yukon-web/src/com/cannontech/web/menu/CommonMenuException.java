package com.cannontech.web.menu;

public class CommonMenuException extends RuntimeException {
    public CommonMenuException(String why, Throwable cause) {
        super(why, cause);
    }
    public CommonMenuException(String why) {
        super(why);
    }

}
