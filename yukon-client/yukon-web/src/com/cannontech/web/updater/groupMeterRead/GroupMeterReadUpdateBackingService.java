package com.cannontech.web.updater.groupMeterRead;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class GroupMeterReadUpdateBackingService extends RecentResultUpdateBackingService {

    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

    	GroupMeterReadResult groupMeterReadResult = deviceAttributeReadService.getResult(resultId);
       
       if (groupMeterReadResult == null) {
           return "";
       }
       GroupMeterReadResultFieldEnum groupMeterReadResultFieldEnum = GroupMeterReadResultFieldEnum.valueOf(resultTypeStr);
       return groupMeterReadResultFieldEnum.getValue(groupMeterReadResult);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
    	return true;
    }

}
