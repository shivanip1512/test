package com.cannontech.common.device.config.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.web.input.Input;

public class CategoryTemplate {

    private String name = null;
    private String description = null;
    private List<Input<?>> inputList = new ArrayList<Input<?>>();

    public List<Input<?>> getInputList() {
        return inputList;
    }

    public void setInputList(List<Input<?>> inputList) {
        this.inputList = inputList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
