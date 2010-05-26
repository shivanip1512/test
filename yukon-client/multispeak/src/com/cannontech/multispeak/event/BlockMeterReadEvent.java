/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
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
import com.google.common.collect.Lists;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BlockMeterReadEvent extends MultispeakEvent implements CommandResultHolderHandlingEvent {

    private Meter meter;
    private Block block;
    boolean populated = false;
    private FormattedBlockService<Block> formattedBlock;
    
    private final PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
    
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

    	throw new UnsupportedOperationException("BlockMeterReadEvent commands are handled by the CommandRequestExecutor only.");
    }
    
    public void handleResult(Meter meter, CommandResultHolder result) {
    	
    	// error
    	if (result.isAnyErrorOrException()) {
    		
    		List<String> errors = Lists.newArrayList();
    		if (StringUtils.isNotBlank(result.getExceptionReason())) {
    			errors.add(result.getExceptionReason());
    		}
    		for (DeviceErrorDescription e : result.getErrors()) {
    			errors.add(e.getPorter());
    		}
    		
    		String errorString = "BlockMeterReadEvent(" + meter.getMeterNumber() + ") - Reading Failed (ERROR:" + StringUtils.join(errors, ", ") + ") " + StringUtils.join(result.getResultStrings(), ", ");
    		CTILogger.info(errorString);
    		//TODO Should we send old data if a new reading fails?
            block = formattedBlock.getBlock(meter);
            //TODO - how can we return some sort of errorMessage with the FormattedBlock?
    	
    	// success
    	} else {
    		
    		CTILogger.info("BlockMeterReadEvent(" + meter.getMeterNumber() + ") - Reading Successful" );
    		
    		for (PointValueHolder pvh : result.getValues()) {
    			
    			LitePoint litePoint = pointDao.getLitePoint(pvh.getId());
    			PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(litePoint, meter);
                RichPointData richPointData = new RichPointData((PointValueQualityHolder)pvh, paoPointIdentifier); // all point data from result is of type PointValueQualityHolder
                block.populate(meter, richPointData);
    		}
    	}
    	
    	// event notification - only once, after all results have come back
    	updateReturnMessageCount();
    	if(getReturnMessages() == 0) {
            populated = true;
            eventNotification();
        }
    }

    public boolean isPopulated() {
        return populated; 
    }
    
    private FormattedBlock getMspFormattedBlock() {
        return formattedBlock.createFormattedBlock(block);
    }
}
