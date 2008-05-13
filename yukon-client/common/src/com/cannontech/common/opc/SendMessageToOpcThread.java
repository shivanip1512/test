package com.cannontech.common.opc;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.SynchWriteException;
import javafish.clients.opc.variant.Variant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.opc.service.OpcService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointTypes;

public class SendMessageToOpcThread implements Runnable {
	private final PointValueQualityHolder pointData;
	private final OpcConnection conn;
	private final Set<PointQuality> goodQualitiesSet;
	private Logger log = YukonLogManager.getLogger(OpcService.class);
	
	public SendMessageToOpcThread(PointValueQualityHolder pointData, OpcConnection conn, Set<PointQuality> qualities) {
		this.pointData = pointData;
		this.conn = conn;
		this.goodQualitiesSet = qualities;
	}

	public void run() {
		OpcGroup[] groupList = conn.getGroups();
		OpcGroup group = null;
		OpcItem item = null;
	
		// Find the item/group this relates to.
		for (OpcGroup g : groupList) {
			List<OpcItem> itemList = g.getItems();
			boolean done = false;
			for (OpcItem i : itemList) {
				YukonOpcItem yOpcItem = (YukonOpcItem) i;
				if (yOpcItem.getPointId() == pointData.getId()) {
					done = true;
					group = g;
					item = i;
					break;
				}
			}
			if (done) {
				break;
			}
		}
	
		// Make sure we found one
		if (item == null || group == null) {
			log.error("Point Id does not have an OPC item created for it.");
			return;
		}
	
		// Set the new value of the item.
		switch (pointData.getType()) {
			// Catch Status points here, the rest all rely on a double value.
			case PointTypes.STATUS_POINT:
			case PointTypes.CALCULATED_STATUS_POINT: {
	
				// Determine the boolean of the point.
				boolean status = pointData.getValue() == 0 ? false : true;
	
				Variant itemValue = new Variant(status);
				item.setValue(itemValue);
				break;
			}
			default: {
				Variant itemValue = new Variant(pointData.getValue());
				item.setValue(itemValue);
				break;
			}
		}
	
		//In testing this appears to not change the value in the simulator... Is something else 
		boolean qual = goodQualitiesSet.contains(PointQuality.getPointQuality((int)pointData.getQuality()));
		item.setQuality(qual);
		
		try {
			if (conn != null && conn.isConnected()) {
				conn.writeOutPointData(group, item);
			}
		} catch (SynchWriteException e) {
			log.error("Error when writing to OPC Server", e);
		}
	}
}
