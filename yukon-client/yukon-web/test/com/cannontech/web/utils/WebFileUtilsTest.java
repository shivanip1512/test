package com.cannontech.web.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
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
        assertThat(data.size(), is(equalTo(dataRows.size() + 1)));
        assertThat(data.get(0).length, is(equalTo(headerRow.length)));
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
        assertThat(data.size(), is(equalTo(dataRows.size() + 1)));
        assertThat(data.get(0).length, is(equalTo(headerRow.size())));
    }
    
    @Test
    public void validateFileNames() {
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename(""));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename(" "));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("\\"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("/"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename(":"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("*"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("\""));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("<"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename(">"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("|"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("contains*oneinvalid char"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename("contains*two*invalid chars"));
        Assert.assertFalse(WebFileUtils.isValidWindowsFilename(" start with space"));
        
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("mine,yours"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("."));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename(". "));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("123"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("1a2B"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("My Filename"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("my_filename"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("my fav filename"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("mine%"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("mi$e"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("mi$e"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("mine's"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("mine#1"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("foo("));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename(")bar"));
        Assert.assertTrue(WebFileUtils.isValidWindowsFilename("foo(bar)"));        
    }

    private void expectThatCsvDownloadHeaderIsSetProperly(MockHttpServletResponse response, String fileName) {
        assertThat(response.getHeader("Content-Type"), is(equalTo("application/force-download")));
        assertThat(response.getHeader("Content-Disposition"), is(equalTo("attachment; filename=\"" + fileName + "\"")));
    }

    private List<String[]> readCSVData(MockHttpServletResponse response) throws Exception {
        StringReader reader = new StringReader(response.getContentAsString());
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> data = csvReader.readAll();
        return data;
    }
}
