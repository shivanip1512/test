package com.cannontech.dbtools.updater;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.config.MockConfigurationSource;
public class UpdateDBTest {
    
    // Fake out a master.cfg entry to test CPARM code.
    private static class UpdateDBMockConfigurationSource extends MockConfigurationSource {
        @Override
        public String getString(String key) {
            if (key.equals("CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO_99")) {
                return "99";
            }
            return null;
        }
    }

    @Test
    public void testConvertToUpdateLines() {
        UpdateDB updateDB = new UpdateDB(null);
        UpdateDBMockConfigurationSource source = new UpdateDBMockConfigurationSource();
        
        // Mock out master.cfg values to test out the @start-cparm code.
        Class<?> c = com.cannontech.common.config.MasterConfigHelper.class;
        try {
            Field configSource = c.getDeclaredField("localConfig");
            configSource.setAccessible(true);
            configSource.set(this, source);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        
        List<String> fileStrings = getBasicFileStrings();
        List<UpdateLine> result = updateDB.convertToUpdateLines(fileStrings, null, null);
        
        // Note this is currently true, but is not necessary for correct behavior.
        // The error ignore begin and end lines are "meta" info but could be real lines
        Assert.assertEquals(8, result.size());
        
        Assert.assertEquals(result.get(0).getValue().toString(), "UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;");
        Assert.assertEquals(result.get(5).getValue().toString(), "UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;");
        
        // start/end block is all on one line in the output.
        Assert.assertEquals(result.get(6).getValue().toString(), "/* @start-block */ "
                                                               + "UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0; "
                                                               + "UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1; "
                                                               + "UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2; "
                                                               + "SELECT @regulatorCommReporting = '99'; " // Cparm set to 99, changed
                                                               + "SELECT @regulatorCommReportingTwo = '100'; " // Cparm does not exist, unchanged
                                                               + "/* @end-block */");
        
        Assert.assertEquals(result.get(7).getValue().toString(), "insert into CTIDatabase values('32.0', 'BobTheBuilder', '07-NOV-2120', 'Latest Update', 3 );");
    }
    
    private List<String> getBasicFileStrings() {
        List<String> fileStrings = new ArrayList<>();
        fileStrings.addAll(Arrays.asList("/******************************************/",
                                         "/**** SQL Server DBupdates             ****/",
                                         "/******************************************/",
                                         "/* This comment spans multiple lines ",
                                         "*  The parser should ignore it properly */",
                                         "",
                                         "/* Start YUK-16106 */",
                                         "UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;",
                                         "UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;",
                                         "UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;",
                                         "/* End YUK-16106 */",
                                         "",
                                         "/* Start YUK-16225 */",
                                         "/* @error ignore-begin */",
                                         "UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;",
                                         "UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;",
                                         "UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;",
                                         "/* @error ignore-end */",
                                         "/* End YUK-16225 */",
                                         "",
                                         "/* Start YUK-16502 */",
                                         "/* @start-block */",
                                         "UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;",
                                         "UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;",
                                         "UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;",
                                         "",
                                         "/* @start-cparm CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO_99 */", 
                                         "SELECT @regulatorCommReporting = '100';", 
                                         "/* @end-cparm */",
                                         "",
                                         "/* @start-cparm CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO */", 
                                         "SELECT @regulatorCommReportingTwo = '100';", 
                                         "/* @end-cparm */",
                                         "",
                                         "/* @end-block */",
                                         "/* End YUK-16502 */",
                                         "",
                                         "insert into CTIDatabase values('32.0', 'BobTheBuilder', '07-NOV-2120', 'Latest Update', 3 );"));
        return fileStrings;
    }

    @Test
    public void testUpgradeScript() {
        UpdateDB updateDB = new UpdateDB(null);
        DBUpdater dbUpdater = new DBUpdater();
        List<String> fileStrings = getFileStrings();
        List<String> updateIds = Arrays.asList("YUK-111", "YUK-116", "YUK-16225", "YUK-1");

        List<UpdateLine> result = updateDB.convertToUpdateLines(fileStrings, null, null);

        /* Validate total no of update lines returned */
        Assert.assertEquals(22, result.size());

        /* Validate some valid update lines with some meta properties */
        Assert.assertEquals("YUK-21", result.get(0).getMetaProps().get("start"));

        Assert.assertEquals(true, result.get(1).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-21', '7.0.1', GETDATE())"));

        Assert.assertEquals("YUK-21", result.get(2).getMetaProps().get("end"));

        /* YUK-26 will be executed and contains metadata - @start-block and @end-block */
        Assert.assertEquals(true, result.get(2).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-26', '7.0.1', GETDATE())"));

        Assert.assertEquals(true, result.get(2).getValue().toString().contains("/* @start-block */")
            && result.get(2).getValue().toString().contains("/* @end-block */"));

        Assert.assertEquals(true, result.get(4).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-111', '7.0.1', GETDATE())"));

        /* YUK-25 will be executed and contains metadata - @start-block and @end-block */
        Assert.assertEquals(true, result.get(12).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-25', '7.0.1', GETDATE())"));

        Assert.assertEquals(true, result.get(12).getValue().toString().contains("/* @start-block */")
            && result.get(12).getValue().toString().contains("/* @end-block */"));

        Assert.assertEquals(true, result.get(13).getValue().toString().contains(
            "UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1"));

        /* YUK-32 will be executed should contains metadata - @ignore-begin */
        Assert.assertEquals("YUK-32 IF YUK-16225", result.get(15).getMetaProps().get("start"));

        Assert.assertEquals("ignore-begin", result.get(15).getMetaProps().get("error"));

        /* YUK-30 will be executed and should contain metadata - @error warn-once */
        Assert.assertEquals("warn-once", result.get(20).getMetaProps().get("error"));

        Assert.assertEquals(true, result.get(20).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-30', '7.0.1', GETDATE())"));

        Assert.assertEquals(true, result.get(21).getValue().toString().contains(
            "INSERT INTO CTIDatabase VALUES ('7.0', '26-FEB-2018', 'Latest Update', 1, GETDATE())"));

        /*
         * Test shouldProcessLine() method in DBUpdater and validate each given update line if it should be
         * processed or executed
         */
        result.forEach(row -> {
            String rowValue = row.getValue().toString();
            boolean shouldProcess = ReflectionTestUtils.invokeMethod(dbUpdater, "shouldProcessLine", row, updateIds);
            if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-21', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-26', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-111', '7.0.1', GETDATE())")) {
                Assert.assertEquals(false, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-116', '7.0.1', GETDATE())")) {
                Assert.assertEquals(false, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-22', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-23', '7.0.1', GETDATE())")) {
                Assert.assertEquals(false, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-24', '7.0.1', GETDATE())")) {
                Assert.assertEquals(false, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-25', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-32', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-33', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains("INSERT INTO DBUpdates VALUES ('YUK-30', '7.0.1', GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            } else if (rowValue.contains(
                "INSERT INTO CTIDatabase VALUES ('7.0', '26-FEB-2018', 'Latest Update', 1, GETDATE())")) {
                Assert.assertEquals(true, shouldProcess);
            }
        });

    }

    /**
     * Reads every line in TestUpgradeScript.sql file
     * @return List of String statements
     */
    private List<String> getFileStrings() {
        List<String> fileStrings = new ArrayList<>();
        InputStream is = UpdateDBTest.class.getResourceAsStream("TestUpgradeScript.sql");
        try {
            fileStrings = IOUtils.readLines(is);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }
        return fileStrings;

    }
}
