package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.amr.rfn.service.WaitableRfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class RfnMeterDisconnectWidget extends AdvancedWidgetControllerBase {
    
    private RfnMeterDao rfnMeterDao;
    private AttributeService attributeService;
    private DynamicDataSource dynamicDataSource;
    private StateDao stateDao;
    private RfnMeterDisconnectService rfnMeterDisconnectService;
    private ConfigurationSource configurationSource;
    private enum DisconnectState {
        UNKNOWN(0), CONNECTED(1), DISCONNECTED(2), ARMED(3);
        
        private int rawState;
        
        private DisconnectState(int rawState) {
            this.rawState = rawState;
        }
        
        public static DisconnectState getForRawState(int rawState) {
            for(DisconnectState state : values()) {
                if(state.rawState == rawState) {
                    return state;
                }
            }
            throw new IllegalArgumentException();
        }
    };
    private static final Logger log = YukonLogManager.getLogger(RfnMeterDisconnectWidget.class);
    
    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        setupRenderModel(model, request);
        return "rfnMeterDisconnectWidget/render.jsp";
    }
    
    @RequestMapping
    public String connect(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RfnMeterDisconnectStatusType action;
        if (isArming()) {
            action = RfnMeterDisconnectStatusType.ARM;
            model.addAttribute("command", "arm");
        } else {
            action = RfnMeterDisconnectStatusType.RESUME; 
            model.addAttribute("command", "connect");
        }
        setupRenderModel(model, request);
        setupSendCommandModel(request, model, action);
        
        return "rfnMeterDisconnectWidget/render.jsp";
    }
    
    @RequestMapping
    public String disconnect(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("command", "disconnect");
        setupRenderModel(model, request);
        setupSendCommandModel(request, model, RfnMeterDisconnectStatusType.TERMINATE);
        
        return "rfnMeterDisconnectWidget/render.jsp";
    }
    
    private void setupRenderModel(ModelMap model, HttpServletRequest request) throws Exception {
        RfnMeter meter = rfnMeterDao.getForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        model.addAttribute("meter", meter);
        
        model.addAttribute("useArming", isArming());
        
        try {
            DisconnectState currentState = getCurrentState(meter);
            model.addAttribute("currentState", currentState);
        } catch (IllegalUseOfAttribute e) {
            model.addAttribute("pointNotConfigured", true);
        }
    }
    
    private void setupSendCommandModel(HttpServletRequest request, final ModelMap model, RfnMeterDisconnectStatusType action) throws ServletRequestBindingException, NotFoundException {
        model.addAttribute("deviceId", WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        RfnMeter meter = rfnMeterDao.getForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        
        WaitableRfnMeterDisconnectCallback waitableCallback = new WaitableRfnMeterDisconnectCallback(new RfnMeterDisconnectCallback() {

            @Override
            public void receivedInitialReply(RfnMeterDisconnectInitialReplyType replyType) {/* Ignore */}
            
            @Override
            public void receivedInitialError(RfnMeterDisconnectInitialReplyType replyType) {
                model.addAttribute("responseStatus", replyType.name());
            }
            
            @Override
            public void receivedConfirmationReply(RfnMeterDisconnectConfirmationReplyType replyType) {
                model.addAttribute("responseStatus", replyType.name());
            }
            
            @Override
            public void receivedConfirmationError(RfnMeterDisconnectConfirmationReplyType replyType) {
                model.addAttribute("responseStatus", replyType.name());
            }
            
            @Override
            public void processingExceptionOccured(String message) {
                log.error("Processing exception occured during meter disconnet command: " + message);
            }
            
            @Override
            public void complete() {/* Ignore */}
            
        });
        
        rfnMeterDisconnectService.send(meter.getMeterIdentifier(), action, waitableCallback);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
    }
    
    private DisconnectState getCurrentState(RfnMeter meter) {
        LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
        PointValueHolder pointValue = dynamicDataSource.getPointValue(litePoint.getPointID());
        LiteState liteState = stateDao.getLiteState(litePoint.getStateGroupID(), (int) pointValue.getValue());
        return DisconnectState.getForRawState(liteState.getStateRawState());
    }
    
    private boolean isArming() {
        return configurationSource.getBoolean("RFN_METER_DISCONNECT_ARMING", false);
    }
    
    @Autowired
    public void setRfnMeterDao(RfnMeterDao rfnMeterDao) {
        this.rfnMeterDao = rfnMeterDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setDynamiceDataSource(DynamicDataSource dynamiceDataSource) {
        this.dynamicDataSource = dynamiceDataSource;
    }
    
    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setRfnMeterDisconnectService(RfnMeterDisconnectService rfnMeterDisconnectService) {
        this.rfnMeterDisconnectService = rfnMeterDisconnectService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
}