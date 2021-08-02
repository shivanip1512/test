package com.cannontech.web.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.cannontech.web.util.WebFileUtils;
import com.opencsv.CSVReader;

public class WebFileUtilsTest {
    @Test
    public void csvContentFromArrayIsWrittenProperly() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        String[] headerRow = {"A", "B", "C"};
        
        List<String[]> dataRows = new ArrayList<String[]>();
        dataRows.add(new String[] {"1", "2", "3"});
        dataRows.add(new String[] {"4", "5", "6"});
        
        String fileName = "myAwesomeData.csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
        
        expectThatCsvDownloadHeaderIsSetProperly(response, fileName);
        
        List<String[]> data = readCSVData(response);
        assertEquals(data.size(), dataRows.size() + 1);
        assertEquals(data.get(0).length, headerRow.length);
    }

    @Test
    public void csvContentFromListIsWrittenProperly() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        List<String> headerRow = Arrays.asList("A", "B", "C");
        
        List<List<String>> dataRows = new ArrayList<List<String>>();
        dataRows.add(Arrays.asList("1", "2", "3"));
        dataRows.add(Arrays.asList("4", "5", "6"));
        
        String fileName = "myAwesomeData.csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
        
        expectThatCsvDownloadHeaderIsSetProperly(response, fileName);
        
        List<String[]> data = readCSVData(response);
        assertEquals(data.size(), dataRows.size() + 1);
        assertEquals(data.get(0).length, headerRow.size());
    }
    
    @Test
    public void validateFileNames() {
        assertFalse(WebFileUtils.isValidWindowsFilename(""));
        assertFalse(WebFileUtils.isValidWindowsFilename(" "));
        assertFalse(WebFileUtils.isValidWindowsFilename("\\"));
        assertFalse(WebFileUtils.isValidWindowsFilename("/"));
        assertFalse(WebFileUtils.isValidWindowsFilename(":"));
        assertFalse(WebFileUtils.isValidWindowsFilename("*"));
        assertFalse(WebFileUtils.isValidWindowsFilename("\""));
        assertFalse(WebFileUtils.isValidWindowsFilename("<"));
        assertFalse(WebFileUtils.isValidWindowsFilename(">"));
        assertFalse(WebFileUtils.isValidWindowsFilename("|"));
        assertFalse(WebFileUtils.isValidWindowsFilename("contains*oneinvalid char"));
        assertFalse(WebFileUtils.isValidWindowsFilename("contains*two*invalid chars"));
        assertFalse(WebFileUtils.isValidWindowsFilename(" start with space"));
        
        assertTrue(WebFileUtils.isValidWindowsFilename("mine,yours"));
        assertTrue(WebFileUtils.isValidWindowsFilename("."));
        assertTrue(WebFileUtils.isValidWindowsFilename(". "));
        assertTrue(WebFileUtils.isValidWindowsFilename("123"));
        assertTrue(WebFileUtils.isValidWindowsFilename("1a2B"));
        assertTrue(WebFileUtils.isValidWindowsFilename("My Filename"));
        assertTrue(WebFileUtils.isValidWindowsFilename("my_filename"));
        assertTrue(WebFileUtils.isValidWindowsFilename("my fav filename"));
        assertTrue(WebFileUtils.isValidWindowsFilename("mine%"));
        assertTrue(WebFileUtils.isValidWindowsFilename("mi$e"));
        assertTrue(WebFileUtils.isValidWindowsFilename("mi$e"));
        assertTrue(WebFileUtils.isValidWindowsFilename("mine's"));
        assertTrue(WebFileUtils.isValidWindowsFilename("mine#1"));
        assertTrue(WebFileUtils.isValidWindowsFilename("foo("));
        assertTrue(WebFileUtils.isValidWindowsFilename(")bar"));
        assertTrue(WebFileUtils.isValidWindowsFilename("foo(bar)"));        
    }

    private void expectThatCsvDownloadHeaderIsSetProperly(MockHttpServletResponse response, String fileName) {
        assertEquals(response.getHeader("Content-Type"), "application/force-download");
        assertEquals(response.getHeader("Content-Disposition"), "attachment; filename=\"" + fileName + "\"");
    }

    private List<String[]> readCSVData(MockHttpServletResponse response) throws Exception {
        StringReader reader = new StringReader(response.getContentAsString());
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> data = csvReader.readAll();
        return data;
    }
}
