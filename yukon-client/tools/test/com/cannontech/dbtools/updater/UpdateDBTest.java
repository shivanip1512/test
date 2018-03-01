package com.cannontech.dbtools.updater;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
    
}
