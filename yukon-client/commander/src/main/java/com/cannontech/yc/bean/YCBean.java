package com.cannontech.yc.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.commander.YC;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public class YCBean extends YC implements MessageListener, HttpSessionBindingListener {
    
    private List<LiteYukonPAObject> deviceHistory = new ArrayList<>();
    private HashMap<String, Vector<String>> serialTypeToNumberMap;
    private int userID;
    private YukonUserContext userContext;
    
    /** Valid route types for serial commands to be sent out on */
    private PaoType [] validRouteTypes = new PaoType[] {
        PaoType.ROUTE_CCU,
        PaoType.ROUTE_MACRO
    };
    
    private YukonUserDao userDao = YukonSpringHook.getBean(YukonUserDao.class);
    
    public void setSerialNumber(String serialType, String serialNumber) {
        
        if (serialType != null && serialNumber != null) {
            super.setSerialNumber(serialNumber);
            
            if (serialNumber.length() > 0) {
                
                Vector<String> serialNumbers = getSerialNumbers(serialType);
                if (serialNumbers == null) {
                    serialNumbers = new Vector<String>();
                }
                
                if (!serialNumbers.contains(serialNumber)) {
                    serialNumbers.add(serialNumber);
                }
                    
                getSerialTypeToNumberMap().put(serialType, serialNumbers);
            }
            
            clearErrorMsg();
        }
    }
    
    @Override
    public void setSerialNumber(String serialNumber) {
        setSerialNumber(getDeviceType(), serialNumber);
    }
    
    @Override
    public void messageReceived(MessageEvent e) {
        
        Message in = e.getMessage();
        if (in instanceof Return) {
            
            Return returnMsg = (Return)in;
            
            if (returnMsg.getStatus() != 0) {
                // Error Message!
                if (getErrorMsg().indexOf(returnMsg.getCommandString()) < 0) {
                    // Command string not displayed yet.
                    setErrorMsg(getErrorMsg() + "<br>* Command Failed - " + returnMsg.getCommandString());
                }
                
                DeviceErrorDescription deviceErrorDesc = null;
                if (userContext != null) {
                    deviceErrorDesc = deviceErrorTranslatorDao.translateErrorCode(returnMsg.getStatus(), userContext);
                }else{
                    deviceErrorDesc = deviceErrorTranslatorDao.translateErrorCode(returnMsg.getStatus());
                }
                setErrorMsg(getErrorMsg() + "<BR><B>"+deviceErrorDesc.getCategory()+"</B> -- " 
                        + deviceErrorDesc.getDescription() + "<BR>" + returnMsg.getResultString());
            }
            
            if (returnMsg.getMessages().size() > 0) {
                
                for (int i = 0; i < returnMsg.getMessages().size(); i++) {
                    
                    Object o = returnMsg.getMessages().get(i);
                    
                    if (o instanceof PointData) {
                        // Clear the Error Message Log, we did eventually read the meter.
                        // This is a request from Jeff W. to only display the error messages when no data is returned.
                        clearErrorMsg();
                    }
                }
            }
        }
        
        super.messageReceived(e);
    }
    
    @Override
    public void valueBound(HttpSessionBindingEvent arg0) {
        log.info("YCBean value bound to session.");
    }
    
    @Override
    public void valueUnbound(HttpSessionBindingEvent arg0) {
        log.info("YCBean value UnBound from session.");
        clearRequestMessage();
        connection.removeMessageListener(this);
    }
    
    /**
     * Returns a vector of serialNumbers from the serialTypeToNumbersMap with key value of serialType
     * @return
     */
    public Vector<String> getSerialNumbers(String serialType) {
        return getSerialTypeToNumberMap().get(serialType);
    }
    
    private HashMap<String, Vector<String>> getSerialTypeToNumberMap() {
        
        if (serialTypeToNumberMap == null) {
            serialTypeToNumberMap = new HashMap<String, Vector<String>>();
        }
        
        return serialTypeToNumberMap;
    }
    
    public void setDeviceType(int deviceId) {
        LiteYukonPAObject lPao = cache.getAllPaosMap().get(deviceId);
        deviceType = lPao.getPaoType().getDbString();
        
        log.debug(" DEVICE TYPE for command lookup: " + deviceType);
        
        setLiteDeviceTypeCommands(commandDao.getAllDevTypeCommands(deviceType));
    }

    public final LiteYukonPAObject[] getValidRoutes() {
        return YukonSpringHook.getBean(PaoDao.class).getRoutesByType(validRouteTypes);
    }

    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userId) {
        this.userID = userId;
        setLiteUser(userDao.getLiteYukonUser(userId));
    }

    /** Adds an element to the previously viewed devices. */
    private void addToDeviceHistory(LiteYukonPAObject liteYukonPao) {
        
        if (!this.deviceHistory.contains(liteYukonPao)) { 
            this.deviceHistory.add(liteYukonPao);
        }
        
        // Remove data from other devices
        try {
            setCommandString("");
        } catch (PaoAuthorizationException e) {}
        
        clearErrorMsg();
    }

    /** Gets the list of previously added devices. */
    public List<LiteYukonPAObject> getDeviceHistory() {
        return this.deviceHistory;
    }
    
    @Override
    public void setLiteYukonPao(LiteYukonPAObject liteYukonPao) {
        // Only update the liteYukonPaobject if it has changed to prevent
        // history from this paobject from being removed.
        if (liteYukonPao != null && !(liteYukonPao.equals(super.getLiteYukonPao()))) {
            addToDeviceHistory(liteYukonPao);
        }
        
        super.setLiteYukonPao(liteYukonPao);
    }
    
    public void setLiteYukonPao(int paoId) {
        
        LiteYukonPAObject litePAO = null;
        if (paoId != PAOGroups.INVALID) {
            litePAO = cache.getAllPaosMap().get(paoId);
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