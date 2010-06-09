/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BlockMeterReadEvent extends MultispeakEvent {

    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    
    private Meter meter;
    private Block block;
    boolean populated = false;
    private FormattedBlockService<Block> formattedBlock;
    
    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public BlockMeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, Meter meter, 
            FormattedBlockService<Block> formattedBlock, int returnMessages_,
            String transactionID_) {
        super(mspVendor_, pilMessageID_, returnMessages_, transactionID_);
        this.meter = meter;
        this.formattedBlock = formattedBlock;
        this.block = formattedBlock.getNewBlock();
    }

    /**
     * @param mspVendor_
     * @param pilMessageID_
     */
    public BlockMeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, Meter meter, 
            FormattedBlockService<Block> formattedBlock, String transactionID_) {
        this(mspVendor_, pilMessageID_, meter, formattedBlock, 1, transactionID_);
    }
  
    public Meter getMeter() {
        return meter;
    }
    
    public void setMeter(Meter meter) {
        this.meter = meter;
    }
    
    /**
     * Send an FormattedBlockNotification to the EA webservice containing meterReadEvents 
     * @param odEvents
     */
    public void eventNotification() {
        
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.EA_Server_STR);
        CTILogger.info("Sending EA_Server_FormattedBlockNotification ("+ endpointURL+ ")");
        
        try {            
            EA_ServerSoap_BindingStub port = MultispeakPortFactory.getEA_ServerPort(getMspVendor());
            if (port != null) {
                ErrorObject[] errObjects = port.formattedBlockNotification( getMspFormattedBlock());
                if( errObjects != null)
                    ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).logErrorObjects(endpointURL, "ReadingChangedNotification", errObjects);
            } else {
                CTILogger.error("Port not found for EA_Server (" + getMspVendor().getCompanyName() + ")");
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - ReadingChangedNotification (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: " + e.getMessage());
        }           
    }

    public boolean messageReceived(Return returnMsg) {

        Meter meter = meterDao.getForId(returnMsg.getDeviceID());
        
        if( returnMsg.getStatus() != 0) {
            
            String result = "BlockMeterReadEvent(" + meter.getMeterNumber() + ") - Reading Failed (ERROR:" + returnMsg.getStatus() + ") " + returnMsg.getResultString();
            CTILogger.info(result);
            //TODO Should we send old data if a new reading fails?
            block = formattedBlock.getBlock(meter);
            //TODO - how can we return some sort of errorMessage with the FormattedBlock?
        }
        else {

            CTILogger.info("BlockMeterReadEvent(" + meter.getMeterNumber() + ") - Reading Successful" );
            if(returnMsg.getVector().size() > 0 ) {
                for (int i = 0; i < returnMsg.getVector().size(); i++) {
                    
                    Object o = returnMsg.getVector().elementAt(i);
                    //TODO SN - Hoping at this point that only one value comes back in the point data vector 
                    if (o instanceof PointData) {
                        PointData pointData = (PointData) o;
                        
                        LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(pointData.getId());
                        PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(litePoint, meter);
                        RichPointData richPointData = new RichPointData(pointData, paoPointIdentifier);
                        block.populate(meter, richPointData);
                    }
                }
            }
        }
        
        if(returnMsg.getExpectMore() == 0){
            updateReturnMessageCount();
            if( getReturnMessages() == 0) {
                populated = true;
                eventNotification();
                return true;
            }
        }
        return false;
    }

    public boolean isPopulated() {
        return populated; 
    }
    
    private FormattedBlock getMspFormattedBlock() {
        return formattedBlock.createFormattedBlock(block);
    }
}
