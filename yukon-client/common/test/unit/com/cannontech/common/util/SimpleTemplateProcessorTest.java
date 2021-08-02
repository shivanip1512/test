package com.cannontech.common.util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class SimpleTemplateProcessorTest {
    DateFormat dateTimeInstance = DateFormat.getDateInstance(DateFormat.SHORT);
    
    @Test
    public void testBasic() {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        
        String result = tp.process(template, data);
        
        assertEquals("Tom Mack", result);
    }

    @Test
    public void testTwoPart() {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} is {age}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29);
        
        String result = tp.process(template, data);
        
        assertEquals("Tom Mack is 29", result);
    }
    
    @Test
    public void testListJoin() {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} needs {groceryList|, }";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        ArrayList<String> groceryList = new ArrayList<String>();
        groceryList.add("carrots");
        groceryList.add("eggs");
        groceryList.add("salt");
        data.put("groceryList", groceryList);
        
        String result = tp.process(template, data);
        
        assertEquals("Tom Mack needs carrots, eggs, salt", result);
    }
    
    @Test
    public void testListJoinAdvanced() {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} needs {groceryList|, |it|<b>{it}</b>}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        ArrayList<String> groceryList = new ArrayList<String>();
        groceryList.add("carrots");
        groceryList.add("eggs");
        groceryList.add("salt");
        data.put("groceryList", groceryList);
        
        String result = tp.process(template, data);
        
        assertEquals("Tom Mack needs <b>carrots</b>, <b>eggs</b>, <b>salt</b>", result);
    }
    

}
