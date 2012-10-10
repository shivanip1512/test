package com.cannontech.web.widget;

import javax.annotation.PostConstruct;
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
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.config.RfnMeterDisconnectArming;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class RfnMeterDisconnectWidget extends AdvancedWidgetControllerBase {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private ConfigurationSource configurationSource;
    
    private RfnMeterDisconnectArming mode;
    
    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        setupRenderModel(model, request);
        return "rfnMeterDisconnectWidget/render.jsp";
    }
    
    @RequestMapping
    public String connect(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.RESUME, "connect");
    }
    
    @RequestMapping
    public String arm(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.ARM, "arm");
    }
    
    @RequestMapping
    public String disconnect(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.TERMINATE, "disconnect");
    }
    
    @RequestMapping
    public String query(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.QUERY, "query");
    }
    
    private String doCommand(ModelMap model, HttpServletRequest request, RfnMeterDisconnectStatusType type, String command) throws Exception {
        model.addAttribute("command", command);
        setupRenderModel(model, request);
        setupSendCommandModel(request, model, type);
        
        return "rfnMeterDisconnectWidget/result.jsp";
    }
    
    private void setupRenderModel(ModelMap model, HttpServletRequest request) throws Exception {
        RfnMeter meter = rfnDeviceDao.getMeterForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        model.addAttribute("meter", meter);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        boolean controllable = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, user);
        model.addAttribute("controllable", controllable);
        
        model.addAttribute("arming", mode == RfnMeterDisconnectArming.ARM);
        model.addAttribute("both", mode == RfnMeterDisconnectArming.BOTH);
        
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            model.addAttribute("pointId", litePoint.getPointID());
        } catch (IllegalUseOfAttribute e) {
            model.addAttribute("pointNotConfigured", true);
        }
    }
    
    private void setupSendCommandModel(HttpServletRequest request, final ModelMap model, final RfnMeterDisconnectStatusType action) throws ServletRequestBindingException, NotFoundException {
        final int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        final RfnMeter meter = rfnDeviceDao.getMeterForId(deviceId);
        model.addAttribute("deviceId", deviceId);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        boolean userCanDisconnect = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, user);
        //Always allow querying the status. Only allow other commands if the user has the appropriate role property
        if(action == RfnMeterDisconnectStatusType.QUERY || userCanDisconnect) {
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
            
        } else {
            model.addAttribute("responseStatus", new YukonMessageSourceResolvable("yukon.web.widgets.rfnMeterDisconnectWidget.userNotAuthorized"));
            model.addAttribute("responseSuccess", false);
        }
        
    }

    @PostConstruct
    public void setMode() {
        String arming = configurationSource.getString(MasterConfigStringKeysEnum.RFN_METER_DISCONNECT_ARMING, "FALSE");
        mode =  RfnMeterDisconnectArming.getForCparm(arming);
    }
    
}