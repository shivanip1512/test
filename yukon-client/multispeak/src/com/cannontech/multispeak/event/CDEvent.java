/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.CB_CDSoap_BindingStub;
import com.cannontech.multispeak.service.LoadActionCode;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDEvent extends MultispeakEvent{

    private String meterNumber = null;
    private LoadActionCode loadActionCode = null;
    private String resultMessage = null;

    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public CDEvent(MultispeakVendor mspVendor_, long pilMessageID_, int returnMessages_) {
        super(mspVendor_, pilMessageID_, returnMessages_);
    }
    
	/**
	 * @param mspVendor_
	 * @param pilMessageID_
	 */
	public CDEvent(MultispeakVendor mspVendor_, long pilMessageID_) {
		this(mspVendor_, pilMessageID_, 1);
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
    
    public void setLoadActionCode(String string) {
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
    
    public void setLoadActionCode(double pointValue) {
        
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
    
    public boolean messageReceived(Return returnMsg)
    {
        String key = getMspVendor().getUniqueKey();
        String objectID = ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).getObjectID(key, returnMsg.getDeviceID());                        
        
        setMeterNumber(objectID);
        setResultMessage(returnMsg.getResultString());

        if( returnMsg.getStatus() == 0) {
            
            if(returnMsg.getResultString().length() > 0){
                setLoadActionCode(returnMsg.getResultString());
            } else if( returnMsg.getVector().size() > 0){
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
                
            }else //Communication failure of sorts
                setLoadActionCode(LoadActionCode.Unknown);
        }
        else //Communication failure of sorts
            setLoadActionCode(LoadActionCode.Unknown);
      
        eventNotification();
        return true;
    }

    /**
     * Send an CDEventNotification to the CB_CD webservice containing cdEvents 
     * @param cdEvents
     */
    public void eventNotification()
    {
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.CB_CD_STR);

        CTILogger.info("Sending CDStateChangedNotification ("+ endpointURL+ "): Meter Number " + getMeterNumber());

        try
        {
            CB_CDSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(getMspVendor());
            port.CDStateChangedNotification(getMeterNumber(), getLoadActionCode());
            
        } catch (ServiceException e) {   
            CTILogger.info("CB_CD service is not defined for company(" + getMspVendor().getCompanyName()+ ") - CDStateChangedNotification failed.");
            CTILogger.error("ServiceExceptionDetail: "+e.getMessage());
            
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - initiateConnectDisconnect (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }   
    }
    
    public boolean isPopulated() {
        return (getLoadActionCode() != null); 
    }
}
