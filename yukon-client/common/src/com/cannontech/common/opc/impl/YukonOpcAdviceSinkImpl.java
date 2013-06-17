package com.cannontech.common.opc.impl;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.opc.YukonOpcConnection;
import com.cannontech.common.opc.model.YukonOpcItem;
import com.cannontech.common.opc.service.OpcService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.message.dispatch.message.PointData;
import com.netmodule.jpc.driver.opc.OpcAdviseSink;
import com.netmodule.jpc.driver.opc.OpcGroup;
import com.netmodule.jpc.driver.opc.OpcItem;

public class YukonOpcAdviceSinkImpl extends OpcAdviseSink {
	
	private YukonOpcConnection yukonOpcConnection;
	private Logger log = YukonLogManager.getLogger(OpcService.class);
	private DynamicDataSource dispatchConnection;
	
	public YukonOpcAdviceSinkImpl(OpcGroup group, YukonOpcConnection server, DynamicDataSource dispatchConn) {
		super(group);
		yukonOpcConnection = server;
		this.dispatchConnection = dispatchConn;
	}
	
	public void readAsyncCallback(OpcItem opcItem) {
		int handle = opcItem.getClientHandle();
		
		YukonOpcItem yOpcItem = yukonOpcConnection.getOpcReceiveItem(handle);
		
		if (yOpcItem == null) {
			yOpcItem = yukonOpcConnection.getOpcSendItem(handle);
			if (yOpcItem != null) {
			    log.error("OPC Item received that was configured for sending. Item: " + yOpcItem.getItemName() + " Yukon PointId: " + yOpcItem.getPointId());
			} else {
			    log.warn("OPC Item received from the Opc Server was not found in Yukon Receive List.");
			}
			return;
		}
		
		log.debug(" OPC Asynch event for Item: " + yOpcItem.getItemName());
        
    	Date timeStamp = opcItem.getTimestamp();
        int type = opcItem.getType();
        short opcQuality = opcItem.getQuality();
        int temp = opcQuality | OpcItem.OPC_QUALITY_MASK;
        
        boolean goodQuality = false;        
        if ( OpcItem.OPC_QUALITY_GOOD == temp) {
        	goodQuality = true;
        }
        
        double newValue;
        switch(type) {
            case OpcItem.DATA_TYPE_BOOL: {
                boolean b = opcItem.getBoolean();
                if(b) {
                    newValue = 1.0;
                }
                else {
                    newValue = 0.0;
                }
                break;
            }
            case OpcItem.DATA_TYPE_R4: {
                newValue = opcItem.getFloat();
                newValue *= yOpcItem.getMultiplier();
                newValue += yOpcItem.getOffset();
                break;
            }
            case OpcItem.DATA_TYPE_R8: {
                newValue = opcItem.getDouble();
                newValue *= yOpcItem.getMultiplier();
                newValue += yOpcItem.getOffset();
                break;
            }
            case OpcItem.DATA_TYPE_I4: {
                newValue = opcItem.getInt();
                newValue *= yOpcItem.getMultiplier();
                newValue += yOpcItem.getOffset();
                break;
            }
            case OpcItem.DATA_TYPE_I8: {
                newValue = opcItem.getLong();
                newValue *= yOpcItem.getMultiplier();
                newValue += yOpcItem.getOffset();
                break;
            }
            case OpcItem.DATA_TYPE_I1: {
                newValue = opcItem.getByte();
                newValue *= yOpcItem.getMultiplier();
                newValue += yOpcItem.getOffset();
                break;
            }
            case OpcItem.DATA_TYPE_UI1: {
                newValue = opcItem.getByte();
                newValue *= yOpcItem.getMultiplier();
                newValue += yOpcItem.getOffset();
                break;
            }
            default: {
                log.error("Received an unhandled data type from the OPC server. Type: " + type);
                return;
            }
        }

        PointData pointData = new PointData();
        pointData.setId(yOpcItem.getPointId());
        pointData.setPointQuality(goodQuality ? PointQuality.Normal: PointQuality.Unknown);
        pointData.setType(yOpcItem.getPointType());
        pointData.setValue(newValue);
        pointData.setTime(timeStamp);
        pointData.setTimeStamp(new Date());
        log.debug(" Sending update for " + yOpcItem.getItemName() + ". Value: " + newValue + " to PointID: " + yOpcItem.getPointId() );

    
		try{
		    dispatchConnection.putValue(pointData);
		}catch( DispatchNotConnectedException e) {
		    log.info(" Dispatch not connected. Point updates cannot go out.");
		}
	}
	
	public void writeAsynchCompleteNotification() {
		// Does nothing, this is where we would want to track asynch writes
	}
}
