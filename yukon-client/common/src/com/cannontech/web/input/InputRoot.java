package com.cannontech.web.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputRoot {
    private List<Input> inputList;

    public List<Input> getInputList() {
        return inputList;
    }
    
    public void setInputList(List<Input> inputList) {
        this.inputList = inputList;
    }
    
    public Map<String, ? extends InputSource> getInputMap() {
        Map<String, InputSource> result = new HashMap<String, InputSource>();
        for (Input input : inputList) {
            Map<String, ? extends InputSource> temp = input.getInputMap("");
            result.putAll(temp);
        }
        return result;
    }

}
