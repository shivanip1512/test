package com.cannontech.common.pao;

public class ImportPaoType {
    public static PaoType valueOf(String string) {
        PaoType type = null;
        try {
            //first try to parse as db string
            type = PaoType.getForDbString(string);
        } catch(IllegalArgumentException e) {
            //next try to parse as enum name
            //if it's neither, throw the exception
            type = PaoType.valueOf(string);
        }
        return type;
    }
}
