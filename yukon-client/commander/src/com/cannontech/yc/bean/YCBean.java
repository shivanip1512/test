/*
 * Created on Oct 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yc.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yc.gui.YC;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class YCBean extends YC implements MessageListener, HttpSessionBindingListener
{
    List<LiteYukonPAObject> deviceHistory = new ArrayList<LiteYukonPAObject>();
    
    //Contains <String>serialType to <Vector<String>> serialNumbers
	private HashMap<String, Vector<String>> serialTypeToNumberMap = null;

	private int userID = 0;
	private YukonUserContext userContext = null;
	
	/** Valid route types for serial commands to be sent out on */
	private int [] validRouteTypes = new int[]{
		RouteTypes.ROUTE_CCU,
		RouteTypes.ROUTE_MACRO
		};	

	public YCBean()
	{
		super();
	}

	/**
	 * Set the serialNumber
	 * The serialNumber for the LCR commands
	 * @param serialNumber_ java.lang.String
	 */
	public void setSerialNumber(String serialType_, String serialNumber_) {
	    
		if( serialType_ != null && serialNumber_ != null) {
			super.setSerialNumber(serialNumber_);
			
			if( serialNumber_.length() > 0)// && !getSerialNumbers().contains(serialNumber_))
			{
				Vector<String> serialNumbers = getSerialNumbers(serialType_);
				if( serialNumbers == null)
					serialNumbers = new Vector<String>();
				
				if( !serialNumbers.contains(serialNumber_))
					serialNumbers.add(serialNumber_);
					
				getSerialTypeToNumberMap().put(serialType_, serialNumbers);
			}
			clearErrorMsg();
		}
	}

	/**
	 * Set the serialNumber
	 * The serialNumber for the LCR commands
	 * @param serialNumber_ java.lang.String
	 */
	public void setSerialNumber(String serialNumber_) {
		setSerialNumber(getDeviceType(), serialNumber_);
	}	
	
	/* (non-Javadoc)
	 * Load the data maps with the returned pointData 
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();
		if( in instanceof Return) {
		    
			Return returnMsg = (Return)in;
			if ( returnMsg.getStatus() != 0) {	//Error Message!

				if( getErrorMsg().indexOf(returnMsg.getCommandString()) < 0) {	//command string not displayed yet
					setErrorMsg(getErrorMsg() + "<br>* Command Failed - " + returnMsg.getCommandString());
				}
				DeviceErrorTranslatorDao deviceErrorTrans = YukonSpringHook.getBean("deviceErrorTranslator", DeviceErrorTranslatorDao.class);
				DeviceErrorDescription deviceErrorDesc = null;
				if(userContext != null){
					deviceErrorDesc = deviceErrorTrans.translateErrorCode(returnMsg.getStatus(), userContext);
				}else{
					deviceErrorDesc = deviceErrorTrans.translateErrorCode(returnMsg.getStatus());
				}
				setErrorMsg( getErrorMsg() + "<BR><B>"+deviceErrorDesc.getCategory()+"</B> -- " 
						+ deviceErrorDesc.getDescription() + "<BR>" + returnMsg.getResultString());
			}
			
			if(returnMsg.getVector().size() > 0 ) {
			    
				for (int i = 0; i < returnMsg.getVector().size(); i++) {
				    
					Object o = returnMsg.getVector().elementAt(i);
					
					if (o instanceof PointData) {
						//Clear the Error Message Log, we did eventually read the meter
						//This is a request from Jeff W. to only display the error messages when no data is returned.
						clearErrorMsg();
					}
				}
			}
		}
		super.messageReceived(e);		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {
		CTILogger.info("YCBean value bound to session.");
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		CTILogger.info("YCBean value UnBound from session.");
        clearRequestMessage();
        getPilConn().removeMessageListener(this);
	}

	/**
	 * Returns a vector of serialNumbers from the serialTypeToNumbersMap with key value of serialType_
	 * @return
	 */
	public Vector<String> getSerialNumbers(String serialType_) {
		return getSerialTypeToNumberMap().get(serialType_);
	}

	private HashMap<String, Vector<String>> getSerialTypeToNumberMap() {
		if( serialTypeToNumberMap == null)
			serialTypeToNumberMap = new HashMap<String, Vector<String>>();
	
		return serialTypeToNumberMap;
	}	
	
	public void setDeviceType(int devID) {
		LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(devID);
		deviceType = lPao.getPaoType().getDbString();
		CTILogger.debug(" DEVICE TYPE for command lookup: " + deviceType);
		setLiteDeviceTypeCommandsVector(DaoFactory.getCommandDao().getAllDevTypeCommands(deviceType));
	}

	public final LiteYukonPAObject[] getValidRoutes() {
		return DaoFactory.getPaoDao().getRoutesByType(validRouteTypes);
	}

    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
        setLiteUser(DaoFactory.getYukonUserDao().getLiteYukonUser(userID));
    }

    // Adds an element to the previously viewed devices
    private void addToDeviceHistory(LiteYukonPAObject liteYukonPao){
        if(!this.deviceHistory.contains(liteYukonPao)){ 
            this.deviceHistory.add(liteYukonPao);
        }
        
        // Remove data from other devices
        try{
            setCommandString("");
        } catch (PaoAuthorizationException e){}
        
        clearErrorMsg();
    }

    // Gets the list of previously added devices
    public List<LiteYukonPAObject> getDeviceHistory(){
        return this.deviceHistory;
    }
    

    /**
     * @param liteYukonPao
     */
    public void setLiteYukonPao(LiteYukonPAObject liteYukonPao){
        //Only update the liteYukonPaobject if it has changed to prevent
        // history from this paobject from being removed.
        if( liteYukonPao != null && 
                !(liteYukonPao.equals(super.getLiteYukonPao()))) {
            addToDeviceHistory(liteYukonPao);
        }
        super.setLiteYukonPao(liteYukonPao);
    }
    
    /**
     * 
     * @param paoId
     */
    public void setLiteYukonPao(int paoId){
        LiteYukonPAObject litePAO = null;
        if (paoId != PAOGroups.INVALID){
            litePAO = paoDao.getLiteYukonPAO(paoId);
        }
        this.setLiteYukonPao(litePAO);
    }

	public YukonUserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(YukonUserContext userContext) {
		this.userContext = userContext;
	}
}