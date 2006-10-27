package com.cannontech.web.menu;


/**
 * Represents a menu option that should execute a javascript function
 * when it is selected.
 */
public class SimpleMenuOptionAction extends SimpleMenuOption {
    private String script = "";

    public SimpleMenuOptionAction(String subOptionName) {
        super(subOptionName);
    }

    public SimpleMenuOptionAction(OptionNameFactory factory) {
        super(factory);
    }
    
    public void setScript(String jsEventName) {
        this.script = jsEventName;
    }
    
    public String getUrl() {
        return "javascript:" + script;
    }

}
