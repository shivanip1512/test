package com.cannontech.web.widget;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
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
import com.cannontech.core.dynamic.PointValueQualityHolder;
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
    @Autowired private MeterDao meterDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private ConfigurationSource configurationSource;
    
    private RfnMeterDisconnectArming mode;
    
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        final int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        RfnMeter meter = meterDao.getRfnMeterForId(deviceId);
        
        setupRenderModel(model, request, meter);
        return "rfnMeterDisconnectWidget/render.jsp";
    }
    
    @RequestMapping("connect")
    public String connect(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.RESUME, "connect");
    }
    
    @RequestMapping("arm")
    public String arm(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.ARM, "arm");
    }
    
    @RequestMapping("disconnect")
    public String disconnect(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.TERMINATE, "disconnect");
    }
    
    @RequestMapping("query")
    public String query(ModelMap model, HttpServletRequest request) throws Exception {
        return doCommand(model, request, RfnMeterDisconnectStatusType.QUERY, "query");
    }
    
    private String doCommand(ModelMap model, HttpServletRequest request, RfnMeterDisconnectStatusType type, String command) throws Exception {
        model.addAttribute("command", command);
        
        final int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        RfnMeter meter = meterDao.getRfnMeterForId(deviceId);

        setupRenderModel(model, request, meter);
        setupSendCommandModel(request, model, type, meter);
        
        return "rfnMeterDisconnectWidget/result.jsp";
    }
    
    private void setupRenderModel(ModelMap model, HttpServletRequest request, RfnMeter meter) throws Exception {
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
    
    private void setupSendCommandModel(HttpServletRequest request, final ModelMap model, 
            final RfnMeterDisconnectStatusType action, RfnMeter meter) throws ServletRequestBindingException, NotFoundException {

        model.addAttribute("deviceId", meter.getPaoIdentifier().getPaoId());
        
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
                public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                    model.addAttribute("responseSuccess", true);
                }
    
                @Override
                public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state) {
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