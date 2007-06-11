package com.cannontech.common.util;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleTemplateProcessorTest {
    TemplateProcessor tp;
    
    @Before
    public void setup() {
        tp = new SimpleTemplateProcessor();
    }

    @Test
    public void testBasic() {
        String template = "{name}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack", result);
    }

    @Test
    public void testTwoPart() {
        String template = "{name} is {age}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29", result);
    }
    
    @Test
    public void testNumberFormat() {
        String template = "{name} is {age|####.000#}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29.11223234234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29.1122", result);
    }
}
