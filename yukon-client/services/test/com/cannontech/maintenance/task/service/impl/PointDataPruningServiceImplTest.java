package com.cannontech.maintenance.task.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;

import org.joda.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class PointDataPruningServiceImplTest {
    
    @Mock ConfigurationSource configurationSource;
    @Mock GlobalSettingDao globalSettingDao;
    @Mock SystemEventLogService systemEventLogService;
    @Mock PointDataPruningDao pointDataPruningDao;
    @InjectMocks PointDataPruningServiceImpl service;


    
    @Test
    public void deleteDuplicatePointData_37DayDuration_6RowsDeleted() {
        MockitoAnnotations.openMocks(this);
        
        when(configurationSource.getBoolean(MasterConfigBoolean.MAINTENANCE_DUPLICATE_POINT_DATA_NOLOCK_REQUIRED, true)).thenReturn(true);
        when(configurationSource.getInteger(MasterConfigInteger.MAINTENANCE_DUPLICATE_POINT_DATA_DELETE_DURATION, 60)).thenReturn(37);
        when(globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT)).thenReturn(1);
        when(pointDataPruningDao.deleteDuplicatePointData(any(), anyBoolean()))
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0)
            .thenReturn(1).thenReturn(0);
        
        int totalRowsDeleted = service.deleteDuplicatePointData(Instant.now().plus(600000));
        
        assertEquals(6, totalRowsDeleted);
    }
}
