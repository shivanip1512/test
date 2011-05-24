package com.cannontech.analysis.tablemodel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.IterableUtils;
import com.google.common.collect.Lists;

/**
 * Only use this ModelBase when a list of SimpleDevices is required.
 * Otherwise, use DeviceReportModelBase.
 * @param <T>
 */
public abstract class SimpleMeterReportModelBase<T> extends SimpleDeviceReportModelBase<T> {

    private Logger log = YukonLogManager.getLogger(SimpleMeterReportModelBase.class);
    
    private List<String> meterNumberFilter;

	/** 
	 * Returns a set of SimpleDevices for the filter values. If filter values are empty, the DeviceTypes SystemGroup is used.
	 * Only use getDeviceList when a set of simpleDevices is required.
	 * Performance issues if using getDeviceList for a deviceGroup filter values (groupsFilter) as this returns all devices
	 *  from the group instead of "smart" sql where clause.
	 * @return
	 */
	protected Iterable<SimpleDevice> getDeviceList() {
		if (!IterableUtils.isEmpty(meterNumberFilter)) {
            List<SimpleDevice> devices = Lists.newArrayList();
            for(String meterNumber : meterNumberFilter){
                try {
                    devices.add(deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber));
                } catch (EmptyResultDataAccessException e) {
                    log.error("Unable to find device with meter number: " + meterNumber + ". This device will be skipped.");
                    continue;
                }
            }
            return devices;
        } else {
        	return super.getDeviceList();
        }
    }
	
	public void setMeterNumberFilter(List<String> meterNumberFilter) {
		this.meterNumberFilter = meterNumberFilter;
	}
}
