package com.cannontech.web.widget;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
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
        
        return "rfnMeterDisconnectWidget/result.jsp";
    }
    
    @RequestMapping
    public String disconnect(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("command", "disconnect");
        setupRenderModel(model, request);
        setupSendCommandModel(request, model, RfnMeterDisconnectStatusType.TERMINATE);
        
        return "rfnMeterDisconnectWidget/result.jsp";
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
        final int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        final RfnMeter meter = rfnMeterDao.getForId(deviceId);
        model.addAttribute("deviceId", deviceId);
        
        WaitableRfnMeterDisconnectCallback waitableCallback = new WaitableRfnMeterDisconnectCallback() {
            
            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                model.addAttribute("responseStatus", message);
                model.addAttribute("responseSuccess", false);
            }

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state) {
                // state will only be used as the state of the meter when doing
                // a 'QUERY' command for now.  This may change based on what
                // NM will decide to set this as for connect/disconnect/arm commands.
                model.addAttribute("responseSuccess", true);
                if (action == RfnMeterDisconnectStatusType.QUERY) {
                    publishPointData(state.getRawState(), meter);
                } else {
                    publishPointData(RfnMeterDisconnectState.getForType(action).getRawState(), meter);
                }
            }

            @Override
            public void receivedError(MessageSourceResolvable message) {
                model.addAttribute("responseStatus", message);
                model.addAttribute("responseSuccess", false);
            }
        };
        
        rfnMeterDisconnectService.send(meter.getMeterIdentifier(), action, waitableCallback);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
    }
    
    private void publishPointData(int rawState, RfnMeter meter) {
        LitePoint point = getDisconnectStatusPoint(meter);
        PointData pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(rawState);
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