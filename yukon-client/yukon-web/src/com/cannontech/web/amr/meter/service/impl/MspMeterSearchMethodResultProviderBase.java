package com.cannontech.web.amr.meter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;

public abstract class MspMeterSearchMethodResultProviderBase implements MspMeterSearchMethodResultProvider {

	protected MspObjectDao mspObjectDao;
	protected MultispeakFuncs multispeakFuncs;
	protected MultispeakDao multispeakDao;
	
	@Autowired
	public void setMspObjectDao(MspObjectDao mspObjectDao) {
		this.mspObjectDao = mspObjectDao;
	}
	@Autowired
	public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
		this.multispeakFuncs = multispeakFuncs;
	}
	@Autowired
	public void setMultispeakDao(MultispeakDao multispeakDao) {
		this.multispeakDao = multispeakDao;
	}
}
