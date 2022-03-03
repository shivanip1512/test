package com.cannontech.dr.itron.service.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.springframework.test.util.ReflectionTestUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ItronCommunicationServiceImplTest {
    private static ItronCommunicationServiceImpl itronCommunicationServiceImplTest;
    final int keepDays = 365;
    
    @BeforeEach
    public void init() {
          itronCommunicationServiceImplTest = new ItronCommunicationServiceImpl();
          
          GlobalSettingDao mockGlobalSettingDao = EasyMock.createNiceMock(GlobalSettingDao.class);
          ReflectionTestUtils.setField(itronCommunicationServiceImplTest, "settingDao", mockGlobalSettingDao);
          EasyMock.expect(mockGlobalSettingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_DAYS_TO_KEEP)).andReturn(keepDays);
          EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.HTTP_PROXY)).andReturn("none");
          EasyMock.replay(mockGlobalSettingDao);
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
        assertEquals(expectedOldFilesLeft, actualOldFilesLeft, "Old files unsuccessfully deleted");
        assertEquals(true, undeletedFile, "Young files left undeleted");
        youngText.delete();
    }
}
    

