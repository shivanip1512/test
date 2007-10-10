package com.cannontech.cbc.web;

import java.util.Date;
import java.util.Vector;

import com.cannontech.clientutils.WebUpdatedPAObjectMap;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public class CBCWebUpdatedObjectMap extends WebUpdatedPAObjectMap{

	public CBCWebUpdatedObjectMap() {
		super();
	}
	
	public void handleCBCChangeEvent (SubBus subBus, Date d) {
		Vector feeders = subBus.getCcFeeders();
		for( int i = 0; i < feeders.size(); i++ )
		{		
			Feeder feeder = (Feeder)feeders.elementAt(i);
			updateMap(feeder.getCcId(), d);
			for( int j = 0; j < feeder.getCcCapBanks().size(); j++ )
			{
				CapBankDevice capBank = (CapBankDevice)feeder.getCcCapBanks().get(j);
				updateMap(capBank.getCcId(), d);
			}
		}
		updateMap(subBus.getCcId(), d);
	}
	public void handleCBCChangeEvent (SubStation sub, Date d)
	{
		updateMap(sub.getCcId(), d);
	}
}

