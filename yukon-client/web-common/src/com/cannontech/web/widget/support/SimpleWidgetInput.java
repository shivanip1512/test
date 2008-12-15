package com.cannontech.web.widget.support;


public class SimpleWidgetInput implements WidgetInput {

    private String description = "";
    private String name = "";
    private boolean required = true;

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }

    public SimpleWidgetInput(String name) {
        this.name = name;
    }
    
    public SimpleWidgetInput() {
        
    }

}
