package com.cannontech.web.menu;

public class ModuleConfigException extends RuntimeException {
    public ModuleConfigException(String why, Throwable cause) {
        super(why, cause);
    }
    public ModuleConfigException(String why) {
        super(why);
    }

}
