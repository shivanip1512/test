/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDEvent extends MultispeakEvent{

    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    
    private String meterNumber = null;
    private LoadActionCode loadActionCode = null;
    private String resultMessage = null;

    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public CDEvent(MultispeakVendor mspVendor_, long pilMessageID_, int returnMessages_, 
            String transactionID_, String responseUrl) {
        super(mspVendor_, pilMessageID_, returnMessages_, transactionID_ , responseUrl);
    }
    
	/**
	 * @param mspVendor_
	 * @param pilMessageID_
	 */
	public CDEvent(MultispeakVendor mspVendor_, long pilMessageID_, String transactionID_, String responseUrl) {
		this(mspVendor_, pilMessageID_, 1, transactionID_, responseUrl);
	}

    public LoadActionCode getLoadActionCode() {
        return loadActionCode;
    }

    public void setLoadActionCode(LoadActionCode loadActionCode) {
        this.loadActionCode = loadActionCode;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    
    private void setLoadActionCode(String string) {
        String resultString = string.toLowerCase();
        
        if( resultString.indexOf("unconfirmed disconnected") > -1)
            setLoadActionCode(LoadActionCode.Unknown);
        else if( resultString.indexOf("confirmed disconnected") > -1)
            setLoadActionCode(LoadActionCode.Disconnect);
        else if( resultString.indexOf("connected") > -1)
            setLoadActionCode(LoadActionCode.Connect);
        else if( resultString.indexOf("connect armed") > -1)
            setLoadActionCode(LoadActionCode.Armed);
        else //Some other text?
            setLoadActionCode(LoadActionCode.Unknown);
    }
    
    private void setLoadActionCode(double pointValue) {
        
        if( pointValue == 0)        // Confirmed Disconnected
            setLoadActionCode(LoadActionCode.Disconnect);
        else if( pointValue == 1)   // Connected
            setLoadActionCode(LoadActionCode.Connect);
        else if( pointValue == 2)   //Uncofirmed Disconnected
            setLoadActionCode(LoadActionCode.Disconnect);
        else if( pointValue == 3)   //Connect Armed
            setLoadActionCode(LoadActionCode.Armed);
        else
            setLoadActionCode(LoadActionCode.Unknown);
    }
    
    @Override
    public boolean messageReceived(Return returnMsg)
    {
        SimpleMeter meter = meterDao.getSimpleMeterForId(returnMsg.getDeviceID());
        setMeterNumber(meter.getMeterNumber());
        setResultMessage(returnMsg.getResultString());

        if( returnMsg.getStatus() == 0) {

            if( returnMsg.getVector().size() > 0){
                for (int i = 0; i < returnMsg.getVector().size(); i++) {    //assuming only 1 in vector
                    Object o = returnMsg.getVector().elementAt(i);
                    if (o instanceof PointData)
                    {
                        PointData pd = (PointData) o;
                        setResultMessage(pd.getStr());
                        if ( pd.getStr().length() > 0 ){
                            setLoadActionCode(pd.getValue());
                        }
                    }
                }
            } else if(returnMsg.getResultString().length() > 0){
                setLoadActionCode(returnMsg.getResultString());
            }else //Communication failure of sorts
                setLoadActionCode(LoadActionCode.Unknown);
        }
        else //Communication failure of sorts
            setLoadActionCode(LoadActionCode.Unknown);
      
        eventNotification();
        return true;
    }

    /**
     * Send an CDEventNotification to the CB_Server webservice containing cdEvents 
     * @param cdEvents
     */
    @Override
    public void eventNotification()
    {
        CTILogger.info("Sending CDStateChangedNotification ("+ getResponseUrl() + "): Meter Number " + getMeterNumber());

        try
        {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(getMspVendor(), getResponseUrl());
            if (port != null) {
                port.CDStateChangedNotification(getMeterNumber(), getLoadActionCode(), getTransactionID(), "errorString?");
            } else {
                CTILogger.error("Port not found for CB_Server (" + getMspVendor().getCompanyName() + ")");
            }  
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + getResponseUrl() + " - initiateConnectDisconnect (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            e.printStackTrace();
        }   
    }
    
    @Override
    public boolean isPopulated() {
        return (getLoadActionCode() != null); 
    }
}
