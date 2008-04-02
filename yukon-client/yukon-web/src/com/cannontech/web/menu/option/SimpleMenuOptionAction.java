package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Represents a menu option that should execute a javascript function when it is
 * selected.
 */
public class SimpleMenuOptionAction extends SimpleMenuOption {

    public SimpleMenuOptionAction(String id, YukonMessageSourceResolvable menuText) {
        super(id, menuText);
    }

    public SimpleMenuOptionAction(String id, String menuTextKey) {
        super(id, menuTextKey);
    }
    
    private String script = "";

    public void setScript(String jsEventName) {
        this.script = jsEventName;
    }

    public String getUrl() {
        return "javascript:" + script;
    }

}
