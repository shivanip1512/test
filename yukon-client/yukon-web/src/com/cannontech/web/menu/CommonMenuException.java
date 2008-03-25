package com.cannontech.web.menu;

public class CommonMenuException extends Exception {
    CommonMenuException(String why, Throwable cause) {
        super(why, cause);
    }
    CommonMenuException(String why) {
        super(why);
    }

}
