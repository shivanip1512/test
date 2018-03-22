package com.cannontech.dbtools.updater;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

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
import com.cannontech.dbtools.updater.dao.impl.DBupdatesDaoImpl;
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
        List<String> fileStrings = getFileStrings();

        DBupdatesDaoImpl dbupdatesDaoImpl = createNiceMock(DBupdatesDaoImpl.class);
        dbupdatesDaoImpl.getUpdateIds();
        expectLastCall().andAnswer(() -> {
            List<String> updateIds = new ArrayList<>();
            updateIds.addAll(Arrays.asList("YUK-111", "YUK-116", "YUK-16225", "YUK-1"));
            return updateIds;
        }).anyTimes();

        replay(dbupdatesDaoImpl);
        ReflectionTestUtils.setField(updateDB, "dBupdatesDaoImpl", dbupdatesDaoImpl);

        List<UpdateLine> result = updateDB.convertToUpdateLines(fileStrings, null, null);

        Assert.assertEquals(19, result.size());

        /* YUK-21 will be executed */
        Assert.assertEquals("YUK-21", result.get(0).getMetaProps().get("start"));

        Assert.assertEquals(true, result.get(1).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-21', '7.0.1', GETDATE())"));

        Assert.assertEquals("YUK-21", result.get(2).getMetaProps().get("end"));

        /* YUK-26 will be executed and contains metadata - @start-block and @end-block */
        Assert.assertEquals(true, result.get(2).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-26', '7.0.1', GETDATE())"));

        Assert.assertEquals(true, result.get(2).getValue().toString().contains("/* @start-block */")
            && result.get(2).getValue().toString().contains("/* @end-block */"));

        /* YUK-22 will be executed */
        Assert.assertEquals(true, result.get(4).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-22', '7.0.1', GETDATE())"));

        /* YUK-23 will not be executed and will be tagged with meta-data @skip-start */
        Assert.assertEquals("true", result.get(5).getMetaProps().get("skip-start"));

        /* YUK-24 will not be executed and will be tagged with meta-data @skip-start */
        Assert.assertEquals("true", result.get(8).getMetaProps().get("skip-start"));

        /* YUK-25 will be executed and contains metadata - @start-block and @end-block */
        Assert.assertEquals(true, result.get(9).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-25', '7.0.1', GETDATE())"));

        Assert.assertEquals(true, result.get(9).getValue().toString().contains("/* @start-block */")
            && result.get(9).getValue().toString().contains("/* @end-block */"));

        /* YUK-111 will not be executed and will be tagged with metadata @skip-start */
        Assert.assertEquals("true", result.get(10).getMetaProps().get("skip-start"));

        Assert.assertEquals(true, result.get(10).getValue().toString().contains(
            "UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1"));

        /* YUK-32 will be executed should contains metadata - @ignore-begin */
        Assert.assertEquals("YUK-32 IF YUK-16225", result.get(12).getMetaProps().get("start"));

        Assert.assertEquals("ignore-begin", result.get(12).getMetaProps().get("error"));

        /* YUK-30 will be executed and should contain metadata - @error warn-once */
        Assert.assertEquals("warn-once", result.get(17).getMetaProps().get("error"));

        Assert.assertEquals(true, result.get(17).getValue().toString().contains(
            "INSERT INTO DBUpdates VALUES ('YUK-30', '7.0.1', GETDATE())"));

        Assert.assertEquals(true, result.get(18).getValue().toString().contains(
            "INSERT INTO CTIDatabase VALUES ('7.0', '26-FEB-2018', 'Latest Update', 1, GETDATE())"));
    }

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
