package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class DisconnectMeterWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private PlcDeviceAttributeReadService plcDeviceAttributeReadService;
    private AttributeService attributeService;
    private StateDao stateDao;
    private PaoDefinitionDao paoDefinitionDao;
    private DynamicDataSource dynamicDataSource;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    private PaoCommandAuthorizationService commandAuthorizationService;
    private DeviceDao deviceDao = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    
    private final String CONTROL_CONNECT_COMMAND = "control connect";
    private final String CONTROL_DISCONNECT_COMMAND = "control disconnect";
    
    
    private Set<? extends Attribute> disconnectAttribute = Collections.singleton(BuiltInAttribute.DISCONNECT_STATUS);
    private enum DisconnectState {
        CONNECTED, DISCONNECTED, UNKNOWN}; 

    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("disconnectMeterWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attribute", BuiltInAttribute.DISCONNECT_STATUS);
        mav.addObject("isRead", false);

        boolean isConfigured = true;
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            PointValueHolder pointValue = dynamicDataSource.getPointValue(litePoint.getPointID());
            int stateGroupId = litePoint.getStateGroupID();
            LiteState liteState = stateDao.getLiteState(stateGroupId, (int) pointValue.getValue());
            mav.addObject("state", getDisconnectedState(liteState.getStateRawState()));
            
            LiteState[] liteStates = stateDao.getLiteStates(stateGroupId);
            mav.addObject("stateGroups",liteStates);
            
        } catch(IllegalUseOfAttribute e) {
            isConfigured = false;
        }
        
        mav.addObject("isConfigured", isConfigured);
        mav.addObject("configString", "");
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        boolean controllable = commandAuthorizationService.isAuthorized(user, CONTROL_CONNECT_COMMAND, meter);
        mav.addObject("controllable", controllable);

        boolean readable = plcDeviceAttributeReadService.isReadable(meter, disconnectAttribute, user);
        mav.addObject("readable", readable);
        
        int pointId = getPointId(request);
        mav.addObject("pointId", pointId);
        
        return mav;
    }

    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Meter meter = getMeter(request);
        ModelAndView mav = getReadModelAndView(meter, true);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = plcDeviceAttributeReadService.readMeter(meter, disconnectAttribute, DeviceRequestType.DISCONNECT_STATUS_ATTRIBUTE_READ,user);
        
        mav.addObject("state", getDisconnectedState(meter, result));
        String configStr = "";
        if ( result.getErrors().isEmpty() )
            configStr = result.getLastResultString().replaceAll("\n", "<BR>");
        mav.addObject("configString", configStr);
        
        mav.addObject("result", result);
        
        boolean controllable = commandAuthorizationService.isAuthorized(user, CONTROL_CONNECT_COMMAND, meter);
        mav.addObject("controllable", controllable);

        boolean readable = plcDeviceAttributeReadService.isReadable(meter, disconnectAttribute, user);
        mav.addObject("readable", readable);
        
        int pointId = getPointId(request);
        mav.addObject("pointId", pointId);

        return mav;
    }

    public ModelAndView helpInfo(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        ModelAndView mav = new ModelAndView("disconnectMeterWidget/helpInfo.jsp");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("device", device);
        
        boolean isConfigured = true;
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS);
            int stateGroupId = litePoint.getStateGroupID();
            LiteState[] liteStates = stateDao.getLiteStates(stateGroupId);
            mav.addObject("stateGroups",liteStates);
        } catch(IllegalUseOfAttribute e) {
            isConfigured = false;
        }
        
        boolean is410Supported =
            paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.DISCONNECT_410);
        mav.addObject("is410Supported", is410Supported);

        boolean is310Supported =
            paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.DISCONNECT_310);
        mav.addObject("is310Supported", is310Supported);
        
        boolean is213Supported =
            paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.DISCONNECT_213);
        mav.addObject("is213Supported",is213Supported);
        
        mav.addObject("isConfigured", isConfigured);
        return mav;
    }
    
    public ModelAndView connect(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext); 
    	Meter meter = getMeter(request);
    	
    	LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = commandRequestExecutor.execute(meter, CONTROL_CONNECT_COMMAND, DeviceRequestType.CONTROL_CONNECT_DISCONNECT_COMAMND, user);
        
        ModelAndView mav = getControlModelAndView(request, result);
        if(result.isComplete() && !(result.isAnyErrorOrException()))
        {
            mav.addObject("successMsg", messageSourceAccessor.getMessage("yukon.web.widgets.disconnectMeterWidget.connectSuccess"));
        }
        return mav;
    }
    
    public ModelAndView disconnect(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
    	Meter meter = getMeter(request);
    	
    	LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = commandRequestExecutor.execute(meter, CONTROL_DISCONNECT_COMMAND, DeviceRequestType.CONTROL_CONNECT_DISCONNECT_COMAMND, user);
        
        ModelAndView mav = getControlModelAndView(request, result);
        if(result.isComplete() && !(result.isAnyErrorOrException()))
        {
            mav.addObject("successMsg", messageSourceAccessor.getMessage("yukon.web.widgets.disconnectMeterWidget.disconnectSuccess"));
        }
        return mav;
    }
    
    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    private int getPointId(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        LitePoint litePoint = attributeService.getPointForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS);
        int pointId = litePoint.getLiteID();
        return pointId;
    }
    
    private ModelAndView getReadModelAndView(Meter meter, boolean isRead){
        
        ModelAndView mav = new ModelAndView("disconnectMeterWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attribute", BuiltInAttribute.DISCONNECT_STATUS);
        mav.addObject("isRead", isRead);
        mav.addObject("isSupported", true);
        mav.addObject("isConfigured", true);
        
        return mav;
    }
    
    private ModelAndView getControlModelAndView(HttpServletRequest request, CommandResultHolder result) throws Exception {
        Meter meter = getMeter(request);
        
        ModelAndView mav = getReadModelAndView(meter, true);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        mav.addObject("state", getDisconnectedState(meter, result));
        mav.addObject("configString", "");
        
        mav.addObject("result", result);
        
        boolean controllable = commandAuthorizationService.isAuthorized(user, "control connect", meter);
        mav.addObject("controllable", controllable);

        boolean readable = plcDeviceAttributeReadService.isReadable(meter, disconnectAttribute, user);
        mav.addObject("readable", readable);
        
        int pointId = getPointId(request);
        mav.addObject("pointId", pointId);
        
        return mav;
    }
    
    private DisconnectState getDisconnectedState(Meter meter, CommandResultHolder result) {
        
        Double stateValue =  null;
        LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
        
        // result is empty, do lookup on dynamicDataSource
        if( result.getValues().isEmpty()) {
            
            PointValueHolder pointValue = dynamicDataSource.getPointValue(litePoint.getPointID());
            int stateGroupId = litePoint.getStateGroupID();
            LiteState liteState = stateDao.getLiteState(stateGroupId, (int) pointValue.getValue());
            
            stateValue = (double)liteState.getStateRawState();
        }
        
        //else grab from result if correct point is found, otherwise unknown
        else {
            
            for (PointValueHolder pvh : result.getValues()) {
                
                int pointId = pvh.getId();
                if (pointId == litePoint.getLiteID()) {
                    stateValue = pvh.getValue();
                    break;
                }
            }
            
            if (stateValue == null) {
                return DisconnectState.UNKNOWN;
            }
        }
        
        return getDisconnectedState(stateValue);
    }
    
    /**
     * Default Disconnect states:
     * 0 Confirmed Disconnected
     * 1 Connected
     * 2 Unconfirmed Disconnected
     * 3 Connect Armed
     * @param value
     * @return true if value is a disconnected state
     */
    private DisconnectState getDisconnectedState(double value) {

        if (value == 0 || value == 2) 
            return DisconnectState.DISCONNECTED;
        else 
            return DisconnectState.CONNECTED;
    }
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setPlcDeviceAttributeReadService(PlcDeviceAttributeReadService plcDeviceAttributeReadService) {
        this.plcDeviceAttributeReadService = plcDeviceAttributeReadService;
    }

    @Required
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Required
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Required
    public void setCommandRequestExecutor(
            CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    @Required
    public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}

