package com.cannontech.web.updater.multispeak.deviceGroupSync.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.multispeak.deviceGroupSync.MultispeakDeviceGroupSyncUpdaterTypeEnum;

public class MetersProcessCountMultispeakDeviceGroupSyncUpdaterHandler implements MultispeakDeviceGroupSyncUpdaterHandler {

	private MeterDao meterDao;
	
	@Override
	public String handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
		
		int meterCount = meterDao.getMeterCount();
		int processedCount = progress.getMetersProcessedCount();
		
		if (progress.isFinished()) {
			
			return String.valueOf(meterCount);
			
		} else {
			
			if (processedCount < meterCount) {
				
				return String.valueOf(progress.getMetersProcessedCount());
				
			} else {
				
				// processed count has reached the max number of meters yukon knows about, the cis vendor may have more
				// progress is not yet complete, "hang" the count at 99%
				return String.valueOf(Math.floor(meterCount * 0.99));
			}
		}
	}

	@Override
	public MultispeakDeviceGroupSyncUpdaterTypeEnum getUpdaterType() {
		return MultispeakDeviceGroupSyncUpdaterTypeEnum.METERS_PROCESSED_COUNT;
	}
	
	@Autowired
	public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
}
