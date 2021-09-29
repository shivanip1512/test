package com.cannontech.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class YukonUrlTest {
    
    @Test
    public void constructor_invalidUrl_missingSlash() {
        assertThrows(IllegalArgumentException.class, 
                     () -> new YukonUrl("http:/localhost"),
                     "Exception not thrown for invalid url.");
    }
    
    @Test
    public void constructor_invalidUrl_missingProtocol() {
        assertThrows(IllegalArgumentException.class, 
                     () -> new YukonUrl("//10.10.10.10"),
                     "Exception not thrown for invalid url.");
    }
    
    @Test
    public void constructor_validUrl() {
        String url = new YukonUrl("http://www.website.com").build();
        assertEquals("http://www.website.com", url, "Error building simple URL.");
    }
    
    @Test
    public void constructor_validUrl_endingSlash() {
        String url = new YukonUrl("http://www.website.com/").build();
        assertEquals("http://www.website.com/", url, "Error building URL with ending slash.");
    }
    
    @Test
    public void constructor_validUrl_withIp() {
        String url = new YukonUrl("http://111.111.111.111").build();
        assertEquals("http://111.111.111.111", url, "Error building URL with IP");
    }
    
    @Test
    public void constructor_validUrl_withHttps() {
        String url = new YukonUrl("https://111.111.111.111").build();
        assertEquals("https://111.111.111.111", url, "Error building URL with https.");
    }
    
    @Test
    public void constructor_validUrl_withPort() {
        String url = new YukonUrl("https://111.111.111.111:8080").build();
        assertEquals("https://111.111.111.111:8080", url, "Error building URL with port.");
    }
    
    @Test
    public void constructor_validUrl_directoryAndPage() {
        String url = new YukonUrl("https://111.111.111.111:8080/directory/page.html").build();
        assertEquals("https://111.111.111.111:8080/directory/page.html", url, "Error building URL with directory and page.");
    }
    
    private enum State {
        MN;
    }
    
    @Test
    public void appendParameters() {
        String url = YukonUrl.of("http://10.10.10.10")
                .appendParam("id", 123)
                .appendParam("state", State.MN)
                .build();
        
        assertEquals("http://10.10.10.10?id=123&state=MN", url, "Error appending parameter to URL.");
    }
    
    @Test
    public void appendSegment() {
        String url = new YukonUrl("http://10.10.10.10")
                .append("directory", "file.jsp#anchor")
                .build();
        
        assertEquals("http://10.10.10.10/directory/file.jsp#anchor", url, "Error appending segments to URL.");
    }
    
    @Test
    public void appendSegment_withInvalidSegments() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> new YukonUrl("http://10.10.10.10").append("directory", "\\ %", "file.csv").build(),
                "Exception not thrown for invalid url.");
        
        // Confirm that the error message shows full URL that we attempted to build and validation wasn't performed 
        // until all segments were appended.
        Assertions.assertTrue(exception.getMessage().contains("http://10.10.10.10/directory/\\ %/file.csv"),
                              "Exception doesn't show all appended segments.");
    }
    
    @Test
    public void appendSegmentsAndParams() {
        String url = YukonUrl.of("http://10.10.10.10")
                .append("directory1")
                .append("directory2", "file.html")
                .appendParam("id", 124)
                .appendParam("state", State.MN)
                .build();
        
        assertEquals("http://10.10.10.10/directory1/directory2/file.html?id=124&state=MN", url, "Error appending segments and parameters to URL.");
    }
}
