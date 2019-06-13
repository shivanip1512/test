package com.cannontech.dr.itron.service.impl;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class ItronCommunicationServiceImplTest {
    private static ItronCommunicationServiceImpl itronCommunicationServiceImplTest;
    final int keepDays = 365;
    
    @Before
    public void init() {
          itronCommunicationServiceImplTest = new ItronCommunicationServiceImpl();
          
          GlobalSettingDao mockGlobalSettingDao = EasyMock.createNiceMock(GlobalSettingDao.class);
          ReflectionTestUtils.setField(itronCommunicationServiceImplTest, "settingDao", mockGlobalSettingDao);
          EasyMock.expect(mockGlobalSettingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_DAYS_TO_KEEP)).andReturn(keepDays);
          EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.HTTP_PROXY)).andReturn("none");
          EasyMock.replay(mockGlobalSettingDao);
          
          DateFormattingService mockDateFormattingService = EasyMock.createNiceMock(DateFormattingService.class);
          ReflectionTestUtils.setField(itronCommunicationServiceImplTest, "dateFormattingService", mockDateFormattingService);
          EasyMock.expect(mockDateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system)).andReturn("mockTimeString");
          EasyMock.replay(mockDateFormattingService);
    }
    
    @Test
    public void test_delete() throws IOException {
        final int expectedOldFilesLeft = 0;
        final int expectedYoungFilesLeft = 1;
        
        File youngText = new File(CtiUtilities.getItronDirPath() + System.getProperty("file.separator") + "young.txt");
        File oldText = new File(CtiUtilities.getItronDirPath() + System.getProperty("file.separator") + "old.txt");
        oldText.createNewFile();
        youngText.createNewFile();
        
        DateTime oldDate = new DateTime().minusDays(keepDays*2);
        oldText.setLastModified(oldDate.getMillis());
        DateTime retentionDate = new DateTime().minusDays(keepDays);
        
        ReflectionTestUtils.invokeMethod(itronCommunicationServiceImplTest, "deleteOldItronFiles");
        File dir = new File(CtiUtilities.getItronDirPath());
        File[] directoryListing = dir.listFiles();
        int actualOldFilesLeft = 0;
        int actualYoungFilesLeft = 0;
        
        for (File itronZip: directoryListing) {
            if (itronZip.isFile() && itronZip.exists()) {
                DateTime lastUsedDate = new DateTime(itronZip.lastModified());
                if (lastUsedDate.compareTo(retentionDate)<0) {
                    actualOldFilesLeft++;
                } else {
                    actualYoungFilesLeft++;
                }
            }
        }
        boolean undeletedFile = (actualYoungFilesLeft >= expectedYoungFilesLeft);
        Assert.assertEquals("Old files unsuccessfully deleted", expectedOldFilesLeft, actualOldFilesLeft);
        Assert.assertEquals("Young files left undeleted", true, undeletedFile);
        youngText.delete();
    }
}
    

