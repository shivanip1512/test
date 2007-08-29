package com.cannontech.common.util;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

public class FileUtilTest {

    private File file;

    @Before
    public void setUp() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("testLogForFileUtil.log");
        file = File.createTempFile("fileUtilTest", ".tmp");
        file.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(file);
        FileCopyUtils.copy(resourceAsStream, fos);
        
    }

    @Test
    public void testReadLinesFileIntLongTen() throws IOException {
        List<String> lines = FileUtil.readLines(file, 10, 0);
        
        if(!lines.get(0).equalsIgnoreCase("08/15/2007 20:26:11 Dispatch DB Writer Thread Active. TID:  3800")){
            fail("Line 1 did not matchup with test case!");
        }
        if(!lines.get(9).equalsIgnoreCase("08/15/2007 20:28:07 InThread  : 127.0.0.1 / 1515 connection is not valid. ")){
            fail("Line 10 did not matchup with test case!");
        }
        if(lines.size() != 10){
            fail("Size of the array did not matchup with the number of lines wanted!");
        }

    }
   
    @Test
    public void testReadLinesFileIntLongFifty() throws IOException {
        List<String> lines = FileUtil.readLines(file, 50, 0);
        
        if(!lines.get(0).equalsIgnoreCase("08/15/2007 20:16:06 InThread  : 127.0.0.1 / 1515 connection is not valid. ")){
            fail("Line 1 did not matchup with test case!");
        }
        if(!lines.get(49).equalsIgnoreCase("08/15/2007 20:28:07 InThread  : 127.0.0.1 / 1515 connection is not valid. ")){
            fail("Line 50 did not matchup with test case!");
        }
        if(lines.size() != 50){
            fail("Size of the array did not matchup with the number of lines wanted!");
        }

    }

    @Test
    public void testReadLinesFileIntLongOffSetTen() throws IOException {
        List<String> lines = FileUtil.readLines(file, 50, 225889);
        
        if(!lines.get(0).equalsIgnoreCase("08/15/2007 20:26:11 Dispatch DB Writer Thread Active. TID:  3800")){
            fail("Line 1 did not matchup with test case!");
        }
        if(!lines.get(9).equalsIgnoreCase("08/15/2007 20:28:07 InThread  : 127.0.0.1 / 1515 connection is not valid. ")){
            fail("Line 10 did not matchup with test case!");
        }
        if(lines.size() != 10){
            fail("Size of the array did not matchup with the number of lines wanted!");
        }

    }

    @Test
    public void testReadLinesFileIntLongOffSetFifty() throws IOException {
        List<String> lines = FileUtil.readLines(file, 100, 223021);
        
        if(!lines.get(0).equalsIgnoreCase("08/15/2007 20:16:06 InThread  : 127.0.0.1 / 1515 connection is not valid. ")){
            fail("Line 1 did not matchup with test case!");
        }
        if(!lines.get(49).equalsIgnoreCase("08/15/2007 20:28:07 InThread  : 127.0.0.1 / 1515 connection is not valid. ")){
            fail("Line 50 did not matchup with test case!");
        }
        if(lines.size() != 50){
            fail("Size of the array did not matchup with the number of lines wanted!");
        }

    }

}
