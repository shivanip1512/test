package com.cannontech.web.input.type;

public interface InputOptionProvider {
    public String getValue();
    public String getText();

    /*
     * Gets the actual enum representing this option.
     * 
     * This is useful to provide a cti:msg or i:inline tag an i18n version.
     */
    public Object getObj();
}
