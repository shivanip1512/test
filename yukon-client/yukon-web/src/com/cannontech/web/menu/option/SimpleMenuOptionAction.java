package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Represents a menu option that should execute a javascript function when it is
 * selected.
 */
public class SimpleMenuOptionAction extends SimpleMenuOption {

    public SimpleMenuOptionAction(YukonMessageSourceResolvable menuText) {
        super(menuText);
    }

    private String script = "";

    public void setScript(String jsEventName) {
        this.script = jsEventName;
    }

    public String getUrl() {
        return "javascript:" + script;
    }

}
