package com.cannontech.common.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.core.service.impl.DateFormattingServiceImpl;
import com.cannontech.user.SystemUserContext;

public class FormattingTemplateProcessorTest {
    private static final SystemUserContext userContext = new SystemUserContext();
  //Using this DateFormat so that the date and time are being parsed the same regardless of locale
    DateFormat dateTimeInstance = new SimpleDateFormat("MM/dd/yyyy");
    DateFormattingServiceImpl dateFormattingService = new DateFormattingServiceImpl();
    
    //Same test performed in multiple locales to ensure correct functionality
    @Test
    public void testNumberFormat_US() {
        Locale.setDefault(Locale.US);
        
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{name} is {age|####.000#}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29.11223234234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29.1122", result);
    }
    
    @Test
    public void testNumberFormat_fr_CA() {
        Locale.setDefault(Locale.CANADA_FRENCH);
        
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{name} is {age|####.000#}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29.11223234234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29,1122", result);
    }
    
    @Test
    public void testNumberFormat_pt_BR() {
        Locale.setDefault(new Locale("pt","BR"));
        System.out.println(Locale.getDefault().toString());
        
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{name} is {age|####.000#}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("age", 29.11223234234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack is 29,1122", result);
    }
    
    
    
    
    @Test
    public void testNumberFormat2_US() {
        Locale.setDefault(Locale.US);
       
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{age1|2} {age2|3} {age3|4}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("age1", 29.11223234234234234f);
        data.put("age2", 800.89623234234234234f);
        data.put("age3", -45.23237234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("29.11 800.896 -45.2324", result);
    }
    
    @Test
    public void testNumberFormat2_fr_CA() {
        Locale.setDefault(Locale.CANADA_FRENCH);
       
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{age1|2} {age2|3} {age3|4}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("age1", 29.11223234234234234f);
        data.put("age2", 800.89623234234234234f);
        data.put("age3", -45.23237234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("29,11 800,896 -45,2324", result);
    }
    
    @Test
    public void testNumberFormat2_pt_BR() {
        Locale.setDefault(new Locale("pt", "BR"));
       
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{age1|2} {age2|3} {age3|4}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("age1", 29.11223234234234234f);
        data.put("age2", 800.89623234234234234f);
        data.put("age3", -45.23237234234234f);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("29,11 800,896 -45,2324", result);
    }
    
    @Test
    public void testDateFormat() throws ParseException {
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{name} was born {birthDate|M/d/yyyy}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        data.put("birthDate", dateTimeInstance.parse("3/7/1978"));
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack was born 3/7/1978", result);
    }
    
    @Test
    public void testListJoinAdvanced2() throws ParseException {
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
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
    
    @Test
    public void testListJoinAdvanced3() throws ParseException {
        FormattingTemplateProcessor tp = new FormattingTemplateProcessor(dateFormattingService, userContext);
        String template = "{name}'s favorite days are\n{dayList|\n|it|  {it|M/d/yy}}";
        
        Map<String, Object> data = new HashMap<String, Object>();
        
        data.put("name", "Tom Mack");
        ArrayList<Date> dayList = new ArrayList<Date>();
        dayList.add(dateTimeInstance.parse("3/7/1978"));
        dayList.add(dateTimeInstance.parse("3/25/1980"));
        data.put("dayList", dayList);
        
        String result = tp.process(template, data);
        
        Assert.assertEquals("Tom Mack's favorite days are\n  3/7/78\n  3/25/80", result);
    }
}
