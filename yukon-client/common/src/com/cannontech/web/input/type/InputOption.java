package com.cannontech.web.input.type;

/**
 * Class which represents an input option to be used with a drop down box
 */
public class InputOption implements InputOptionProvider {

    private String value = null;
    private String text = null;

    public String getText() {
        // You can specify a value only and the value will be used as the text
        // also
        if (text == null) {
            return value;
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
