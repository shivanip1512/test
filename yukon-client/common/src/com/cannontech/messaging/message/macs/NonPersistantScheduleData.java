package com.cannontech.messaging.message.macs;

public class NonPersistantScheduleData {

    private ScriptFileMessage script = null;
    private java.util.Enumeration categories = null;

    public java.util.Enumeration getCategories() {
        return categories;
    }

    public void setCategories(java.util.Enumeration newCategories) {
        categories = newCategories;
    }

    public ScriptFileMessage getScript() {
        if (script == null) {
            script = new ScriptFileMessage();
        }

        return script;
    }

    public void setScript(ScriptFileMessage newScript) {
        script = newScript;
    }
}
