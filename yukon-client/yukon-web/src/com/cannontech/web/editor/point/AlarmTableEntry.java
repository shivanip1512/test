package com.cannontech.web.editor.point;

public class AlarmTableEntry {

    private String condition = null;
    private String generate = null;
    private String excludeNotify = null;

    public String getCondition() {
        return condition;
    }

    public String getExcludeNotify() {
        return excludeNotify;
    }

    public String getGenerate() {
        return generate;
    }

    public void setCondition(String string) {
        condition = string;
    }

    public void setExcludeNotify(String string) {
        excludeNotify = string;
    }

    public void setGenerate(String string) {
        generate = string;
    }

}
