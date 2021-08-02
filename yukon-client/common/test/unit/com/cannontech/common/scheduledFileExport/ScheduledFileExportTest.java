package com.cannontech.common.scheduledFileExport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

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
        assertEquals(emailSingle, data.getNotificationEmailAddresses());
        List<String> emailSingleAddresses = data.getNotificationEmailAddressesAsList();
        assertEquals(1, emailSingleAddresses.size());
        assertEquals(emailSingle, emailSingleAddresses.get(0));
        
        ScheduledFileExportData data2 = new ScheduledFileExportData();
        data2.setNotificationEmailAddresses(emailDouble);
        assertEquals(emailDouble, data2.getNotificationEmailAddresses());
        List<String> emailDoubleAddresses = data2.getNotificationEmailAddressesAsList();
        assertEquals(2, emailDoubleAddresses.size());
        assertEquals(firstEmail, emailDoubleAddresses.get(0));
        assertEquals(secondEmail, emailDoubleAddresses.get(1));
        
        ScheduledFileExportData data3 = new ScheduledFileExportData();
        data3.setNotificationEmailAddresses(emailDoubleWithSpace);
        assertEquals(emailDouble, data3.getNotificationEmailAddresses());
        List<String> emailDoubleWithSpaceAddresses = data3.getNotificationEmailAddressesAsList();
        assertEquals(2, emailDoubleWithSpaceAddresses.size());
        assertEquals(firstEmail, emailDoubleWithSpaceAddresses.get(0));
        assertEquals(secondEmail, emailDoubleWithSpaceAddresses.get(1));
        
        ScheduledFileExportData data4 = new ScheduledFileExportData();
        data4.setNotificationEmailAddresses(" ");
        assertNull(data4.getNotificationEmailAddresses());
        assertEquals(0, data4.getNotificationEmailAddressesAsList().size());
    }
}
