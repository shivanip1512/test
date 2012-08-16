package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.amr.rfn.service.WaitableRfnMeterDisconnectCallback;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class RfnMeterDisconnectWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private ConfigurationSource configurationSource;
    
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
    
    @RequestMapping
    public String query(ModelMap model, HttpServletRequest request) throws Exception {
        model.addAttribute("command", "query");
        setupRenderModel(model, request);
        setupSendCommandModel(request, model, RfnMeterDisconnectStatusType.QUERY);
        
        return "rfnMeterDisconnectWidget/result.jsp";
    }
    
    private void setupRenderModel(ModelMap model, HttpServletRequest request) throws Exception {
        RfnMeter meter = rfnDeviceDao.getMeterForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
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
        final RfnMeter meter = rfnDeviceDao.getMeterForId(deviceId);
        model.addAttribute("deviceId", deviceId);
        
        WaitableRfnMeterDisconnectCallback waitableCallback = new WaitableRfnMeterDisconnectCallback() {
            
            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                model.addAttribute("responseStatus", message);
                model.addAttribute("responseSuccess", false);
            }

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state) {
                model.addAttribute("responseSuccess", true);
            }

            @Override
            public void receivedError(MessageSourceResolvable message) {
                model.addAttribute("responseStatus", message);
                model.addAttribute("responseSuccess", false);
            }
        };
        
        rfnMeterDisconnectService.send(meter, action, waitableCallback);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
    }

    private boolean isArming() {
        return configurationSource.getBoolean(MasterConfigBooleanKeysEnum.RFN_METER_DISCONNECT_ARMING);
    }
    
}