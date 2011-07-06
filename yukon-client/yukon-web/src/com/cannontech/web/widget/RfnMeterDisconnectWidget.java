package com.cannontech.web.widget;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.amr.rfn.service.WaitableRfnMeterDisconnectCallback;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class RfnMeterDisconnectWidget extends AdvancedWidgetControllerBase {
    
    private RfnMeterDao rfnMeterDao;
    private AttributeService attributeService;
    private DynamicDataSource dynamicDataSource;
    private RfnMeterDisconnectService rfnMeterDisconnectService;
    private ConfigurationSource configurationSource;
    private enum DisconnectState {
        
        UNKNOWN(0, null), 
        CONNECTED(1, RfnMeterDisconnectStatusType.RESUME), 
        DISCONNECTED(2, RfnMeterDisconnectStatusType.TERMINATE), 
        ARMED(3, RfnMeterDisconnectStatusType.ARM);
        
        private int rawState;
        private RfnMeterDisconnectStatusType type; 
        
        private DisconnectState(int rawState, RfnMeterDisconnectStatusType type) {
            this.rawState = rawState;
        }
        
        public static DisconnectState getForType(RfnMeterDisconnectStatusType type) {
            for(DisconnectState state : values()) {
                if(state.type == type) {
                    return state;
                }
            }
            throw new IllegalArgumentException();
        }
        
        public int getRawState() {
            return rawState;
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
            LitePoint litePoint = getDisconnectStatusPoint(meter);
            model.addAttribute("pointId", litePoint.getPointID());
        } catch (IllegalUseOfAttribute e) {
            model.addAttribute("pointNotConfigured", true);
        }
    }

    private LitePoint getDisconnectStatusPoint(RfnMeter meter) {
        LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
        return litePoint;
    }
    
    private void setupSendCommandModel(HttpServletRequest request, final ModelMap model, final RfnMeterDisconnectStatusType action) throws ServletRequestBindingException, NotFoundException {
        model.addAttribute("deviceId", WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        final RfnMeter meter = rfnMeterDao.getForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        
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
                if (replyType.equals(RfnMeterDisconnectConfirmationReplyType.SUCCESS)) {
                    publishPointData(action, meter);
                }
            }

            @Override
            public void receivedConfirmationError(RfnMeterDisconnectConfirmationReplyType replyType) {
                model.addAttribute("responseStatus", replyType.name());
            }
            
            @Override
            public void processingExceptionOccured(String message) {
                log.error("Processing exception occurred during meter disconnect command: " + message);
            }
            
            @Override
            public void complete() {/* Ignore */}
            
        });
        
        rfnMeterDisconnectService.send(meter.getMeterIdentifier(), action, waitableCallback);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
    }
    
    private void publishPointData(RfnMeterDisconnectStatusType action, RfnMeter meter) {
        LitePoint point = getDisconnectStatusPoint(meter);
        PointData pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(DisconnectState.getForType(action).getRawState());
        pointData.setTime(new Date());
        pointData.setType(point.getPointType());
        
        dynamicDataSource.putValue(pointData);
        
        log.debug("PointData generated for RfnMeterDisconnectRequest");
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
    public void setRfnMeterDisconnectService(RfnMeterDisconnectService rfnMeterDisconnectService) {
        this.rfnMeterDisconnectService = rfnMeterDisconnectService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
}