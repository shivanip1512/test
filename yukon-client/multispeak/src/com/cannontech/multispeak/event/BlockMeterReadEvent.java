/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.YukonFormattedBlock;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.service.FormattedBlock;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BlockMeterReadEvent extends MultispeakEvent {

    private Meter meter;
    private Block block;
    boolean populated = false;
    private YukonFormattedBlock<Block> formattedBlock;
    
    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public BlockMeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, Meter meter, 
            YukonFormattedBlock<Block> formattedBlock, int returnMessages_) {
        super(mspVendor_, pilMessageID_, returnMessages_);
        this.meter = meter;
        this.formattedBlock = formattedBlock;
        this.block = formattedBlock.getNewBlock();
    }

    /**
     * @param mspVendor_
     * @param pilMessageID_
     */
    public BlockMeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, Meter meter, 
            YukonFormattedBlock<Block> formattedBlock) {
        this(mspVendor_, pilMessageID_, meter, formattedBlock, 1);
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
        
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.EA_MR_STR);
        CTILogger.info("Sending EA_MR_FormattedBlockNotification ("+ endpointURL+ ")");
        
        try {            
            EA_MRSoap_BindingStub port = MultispeakPortFactory.getEA_MRPort(getMspVendor());            
            ArrayOfErrorObject errObjects = port.formattedBlockNotification( getMspFormattedBlock());
            if( errObjects != null)
                ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).logArrayOfErrorObjects(endpointURL, "ReadingChangedNotification", errObjects.getErrorObject());
            
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - ReadingChangedNotification (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: " + e.getMessage());
        }           
    }

    public boolean messageReceived(Return returnMsg) {

        String objectID = getObjectID(returnMsg.getDeviceID());
        
        if( returnMsg.getStatus() != 0) {
            
            String result = "BlockMeterReadEvent(" + objectID + ") - Reading Failed (ERROR:" + returnMsg.getStatus() + ") " + returnMsg.getResultString();
            CTILogger.info(result);
            //TODO Should we send old data if a new reading fails?
            block = formattedBlock.getBlock(meter);
            //TODO - how can we return some sort of errorMessage with the FormattedBlock?
        }
        else {

            CTILogger.info("BlockMeterReadEvent(" + objectID + ") - Reading Successful" );
            if(returnMsg.getVector().size() > 0 ) {
                for (int i = 0; i < returnMsg.getVector().size(); i++) {
                    
                    Object o = returnMsg.getVector().elementAt(i);
                    //TODO SN - Hoping at this point that only one value comes back in the point data vector 
                    if (o instanceof PointData) {
                        PointData pointData = (PointData) o;
                        block.populate(meter, pointData);
                    }
                }
            }
        }
        
        if(returnMsg.getExpectMore() == 0){
            updateReturnMessageCount();
            if( getReturnMessages() == 0) {
//                notify();
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
