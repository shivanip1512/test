package com.cannontech.common.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Test
    public void testAreFilesEqual(){
        String path = "C:\\Yukon\\Server\\Log";
        File file1 = new File(path);
        File file2 = new File(path);
        File tsFile1 = new File(file1, "/");
        File tsFile2 = new File(file1, "/");
        File altPathFile = new File("C:\\Yukon\\Server");
        
        if(FileUtil.areFilesEqual(file1, file2) == false){
            fail("Basic file equality incorrect!");
        }
        if(FileUtil.areFilesEqual(file1, tsFile1) == false){
            fail("First file with trailing separator - equality incorrect!");
        }
        if(FileUtil.areFilesEqual(tsFile1, file1) == false){
            fail("Second file with trailing separator - equality incorrect!");
        }
        if(FileUtil.areFilesEqual(tsFile1, tsFile2) == false){
            fail("Both files with trailing separator - equality incorrect!");
        }
        if(FileUtil.areFilesEqual(file1, altPathFile)){
            fail("Different file paths - equality incorrect!");
        }
    }
    
    @Test
    public void testOldClientLogDate() throws ParseException {
        // Correct file name format
        assertTrue(FileUtil.getOldClientLogDate("DBEditor[10.24.26.137]20180531.log").equals(new SimpleDateFormat("yyyyMMdd").parse("20180531")));
        assertTrue(FileUtil.getOldClientLogDate("Commander[10.24.26.137]20180431.log").equals(new SimpleDateFormat("yyyyMMdd").parse("20180431")));
        // Invalid file name format. Expecting null.
        assertTrue(FileUtil.getOldClientLogDate("DBEditor[10.24.26.13720180531.log") == null);
        assertTrue(FileUtil.getOldClientLogDate("DBEditor_20180431.log") == null);
        assertTrue(FileUtil.getOldClientLogDate("DBEditor_[10.24.26.137].log") == null);
        assertTrue(FileUtil.getOldClientLogDate("DBEditor_[10.24.26.137]") == null);
        assertTrue(FileUtil.getOldClientLogDate("Webserver_20180827.zip") == null);
        assertTrue(FileUtil.getOldClientLogDate("Webserver_20180827.log.zip") == null);
        
    }
 
}
