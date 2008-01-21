package com.cannontech.web.menu;


/**
 * Represents a menu option that should execute a javascript function
 * when it is selected.
 */
public class SimpleMenuOptionAction extends SimpleMenuOption {
    private String script = "";

    public SimpleMenuOptionAction(String linkKey) {
        super(linkKey);
    }

    public void setScript(String jsEventName) {
        this.script = jsEventName;
    }
    
    public String getUrl() {
        return "javascript:" + script;
    }

}
