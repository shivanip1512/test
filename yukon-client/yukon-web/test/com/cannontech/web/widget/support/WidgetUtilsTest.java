package com.cannontech.web.widget.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;


public class WidgetUtilsTest {
    @Test
    public void testNull() throws JsonProcessingException {
        String json = WidgetUtils.generateJsonString(null);
        assertEquals(json, "null");
    }
    
    @Test
    public void testString() throws JsonProcessingException {
        String json = WidgetUtils.generateJsonString("Hello' World");
        assertEquals(json, "\"Hello' World\"");
    }
    
    @Test
    public void testMap() throws JsonProcessingException {
        Map<String, String> simpleMap = new HashMap<>();
        simpleMap.put("key1","value1");
        simpleMap.put("key2","value2");

        String json = WidgetUtils.generateJsonString(simpleMap);
        //"{\"key1\":\"value1\",\"key2\":\"value2\"}"
        assertEquals(json, "{\"key1\":\"value1\",\"key2\":\"value2\"}");
    }
    
    @Test
    public void testCollection() throws JsonProcessingException {
        List<String> simpleList = new ArrayList<>();
        simpleList.add("value1");
        simpleList.add("value2");

        String json = WidgetUtils.generateJsonString(simpleList);
        //[\"value1\",\"value2\"]
        assertEquals(json, "[\"value1\",\"value2\"]");
    }
    
    @Test
    public void testBoolean() throws JsonProcessingException {
        assertEquals(WidgetUtils.generateJsonString(true), "true");
        assertEquals(WidgetUtils.generateJsonString(false), "false");
    }

    @Test
    public void testArray() throws JsonProcessingException {
        int [] intArray = {2,3,878};
        String json = WidgetUtils.generateJsonString(intArray);
        assertEquals(json, "[2,3,878]");

        String [] strArray = {"String","3",""};
        json = WidgetUtils.generateJsonString(strArray);
        assertEquals(json, "[\"String\",\"3\",\"\"]");

        Object [] objArray = {Integer.valueOf(234), "string", Double.valueOf(123.4556)};
        json = WidgetUtils.generateJsonString(objArray);
        //[234,\"string\",123.4556]
        assertEquals(json, "[234,\"string\",123.4556]");
    }

    @Test
    public void testPojo() throws JsonProcessingException {
        TestPojo pojo = new TestPojo();
        String json = WidgetUtils.generateJsonString(pojo);
        //{"doubleNum":12.2,"intNum":12,"str":"private string","strArray":["string1","string2"]}
        assertTrue(json.contains("\"doubleNum\":12.2"));
        assertTrue(json.contains("\"intNum\":12"));
        assertTrue(json.contains("\"str\":\"private string\""));
        assertTrue(json.contains("\"strArray\":[\"string1\",\"string2\"]"));
    }

    public static class TestPojo {
        private String str = "private string";
        private Integer intNum = 12;
        private Double doubleNum = 12.2;
        private String [] strArray = {"string1", "string2"};

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public String [] getStrArray() {
            return strArray;
        }

        public void setStrArray(String [] strArray) {
            this.strArray = strArray;
        }

        public Integer getIntNum() {
            return intNum;
        }

        public void setIntNum(Integer intNum) {
            this.intNum = intNum;
        }

        public Double getDoubleNum() {
            return doubleNum;
        }

        public void setDoubleNum(Double doubleNum) {
            this.doubleNum = doubleNum;
        }
    }
}
