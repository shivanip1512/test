package com.cannontech.common.opc.impl;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.opc.model.YukonOpcItem;
import com.cannontech.common.opc.service.OpcService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.netmodule.jpc.driver.opc.OpcGroup;
import com.netmodule.jpc.driver.opc.OpcItem;

public class SendMessageToOpcRunnable implements Runnable {
	private final PointValueQualityHolder pointData;
	private final OpcGroup opcGroup;
	private final YukonOpcItem opcItem;
	
	private Logger log = YukonLogManager.getLogger(OpcService.class);
	
	public SendMessageToOpcRunnable(PointValueQualityHolder pointData, OpcGroup group, YukonOpcItem item) {
		this.pointData = pointData;
		this.opcGroup = group;
		this.opcItem = item;
	}

	public void run() {

		OpcItem item = null;
		
		// Find the item Server Handle
		int serverHandle = opcItem.getServerHandle();
	
		// Make sure we found one
		if (serverHandle == 0) {
			log.error(" Invalid Item, cannot send update for " + opcItem.getItemName());
			return;
		}

		// Set the new value of the item.
		switch (pointData.getPointType()) {
			// Catch Status points here, the rest all rely on a double value.
			case Status:
			case CalcStatus: {
	
				// Determine the boolean of the point.
				boolean status = pointData.getValue() == 0 ? false : true;
				item = new OpcItem(status,OpcItem.DATA_TYPE_BOOL);
				break;
			}
			default: {
				item = new OpcItem(pointData.getValue(),OpcItem.DATA_TYPE_R8);
				break;
			}
		}
		
		int ret = opcGroup.writeSync(new int[]{serverHandle}, new OpcItem[]{item});
		
		if (ret != 0) {
			log.error("Error writing to OPC server. Error code: " + ret);
		}
	}
}
