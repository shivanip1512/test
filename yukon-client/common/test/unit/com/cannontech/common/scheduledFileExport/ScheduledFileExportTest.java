package com.cannontech.common.scheduledFileExport;

import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

public class ScheduledFileExportTest {
    private static final String emailSingle = "test@test.com";
    private static final String emailDouble = "test1@test.com,test2@test.com";
    private static final String firstEmail = "test1@test.com";
    private static final String secondEmail = "test2@test.com";
    private static final String emailDoubleWithSpace = "test1@test.com, test2@test.com";
    
    @Test
    public void scheduledFileExportDataTest() {
        ScheduledFileExportData data = new ScheduledFileExportData();
        data.setNotificationEmailAddresses(emailSingle);
        Assert.assertEquals(emailSingle, data.getNotificationEmailAddresses());
        List<String> emailSingleAddresses = data.getNotificationEmailAddressesAsList();
        Assert.assertEquals(1, emailSingleAddresses.size());
        Assert.assertEquals(emailSingle, emailSingleAddresses.get(0));
        
        ScheduledFileExportData data2 = new ScheduledFileExportData();
        data2.setNotificationEmailAddresses(emailDouble);
        Assert.assertEquals(emailDouble, data2.getNotificationEmailAddresses());
        List<String> emailDoubleAddresses = data2.getNotificationEmailAddressesAsList();
        Assert.assertEquals(2, emailDoubleAddresses.size());
        Assert.assertEquals(firstEmail, emailDoubleAddresses.get(0));
        Assert.assertEquals(secondEmail, emailDoubleAddresses.get(1));
        
        ScheduledFileExportData data3 = new ScheduledFileExportData();
        data3.setNotificationEmailAddresses(emailDoubleWithSpace);
        Assert.assertEquals(emailDouble, data3.getNotificationEmailAddresses());
        List<String> emailDoubleWithSpaceAddresses = data3.getNotificationEmailAddressesAsList();
        Assert.assertEquals(2, emailDoubleWithSpaceAddresses.size());
        Assert.assertEquals(firstEmail, emailDoubleWithSpaceAddresses.get(0));
        Assert.assertEquals(secondEmail, emailDoubleWithSpaceAddresses.get(1));
        
        ScheduledFileExportData data4 = new ScheduledFileExportData();
        data4.setNotificationEmailAddresses(" ");
        Assert.assertNull(data4.getNotificationEmailAddresses());
        Assert.assertEquals(0, data4.getNotificationEmailAddressesAsList().size());
    }
}
