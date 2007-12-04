package com.cannontech.common.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTemplateProcessorTest {
    DateFormat dateTimeInstance = DateFormat.getDateInstance(DateFormat.SHORT);
    
    @Test
    public void testBasic() {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack", result);
    }

    @Test
    public void testTwoPart() {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} is {age}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29", result);
    }
    
    @Test
    public void testNumberFormat() {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} is {age|####.000#}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29.11223234234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29.1122", result);
    }
    
    @Test
    public void testDateFormat() throws ParseException {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} was born {birthDate|M/d/yyyy}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("birthDate", dateTimeInstance.parse("3/7/1978"));
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack was born 3/7/1978", result);
    }
    
    @Test
    public void testListJoin() {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} needs {groceryList|, }";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        ArrayList<String> groceryList = new ArrayList<String>();
        groceryList.add("carrots");
        groceryList.add("eggs");
        groceryList.add("salt");
        data.put("groceryList", groceryList);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack needs carrots, eggs, salt", result);
    }
    
    @Test
    public void testListJoinAdvanced() {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name} needs {groceryList|, |it|<b>{it}</b>}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        ArrayList<String> groceryList = new ArrayList<String>();
        groceryList.add("carrots");
        groceryList.add("eggs");
        groceryList.add("salt");
        data.put("groceryList", groceryList);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack needs <b>carrots</b>, <b>eggs</b>, <b>salt</b>", result);
    }
    
    @Test
    public void testListJoinAdvanced2() throws ParseException {
        TemplateProcessor tp = new SimpleTemplateProcessor();
        String template = "{name}'s favorite days are {dayList|, |it|{it|M/d/yy}}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        ArrayList<Date> dayList = new ArrayList<Date>();
        dayList.add(dateTimeInstance.parse("3/7/1978"));
        dayList.add(dateTimeInstance.parse("3/25/1980"));
        data.put("dayList", dayList);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack's favorite days are 3/7/78, 3/25/80", result);
    }
}
