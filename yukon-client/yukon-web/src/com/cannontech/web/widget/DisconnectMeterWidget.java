package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class DisconnectMeterWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private AttributeService attributeService;
    private StateDao stateDao;
    private DynamicDataSource dynamicDataSource;
    private CommandRequestExecutor commandRequestExecutor;
    private PaoCommandAuthorizationService commandAuthorizationService;
    
    private boolean controlable = false;
    private boolean readable = false;
    
    private Set<Attribute> disconnectAttribute = Collections.singleton((Attribute)BuiltInAttribute.DISCONNECT_STATUS);
    private enum DISCONNECT_STATE {
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
            
        } catch(NotFoundException e) {
            isConfigured = false;
        }
        
        mav.addObject("isConfigured", isConfigured);
        mav.addObject("configString", "");
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        controlable = commandAuthorizationService.isAuthorized(user, "control connect", meter);
        mav.addObject("controlable", controlable);

        readable = meterReadService.isReadable(meter, disconnectAttribute, user);
        mav.addObject("readable", readable);
        
        return mav;
    }

    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Meter meter = getMeter(request);
        ModelAndView mav = getReadModelAndView(meter);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = meterReadService.readMeter(meter, disconnectAttribute, user);
        
        mav.addObject("state", getDisconnectedState(result));
        mav.addObject("errorsExist", result.isErrorsExist());
        String configStr = "";
        if ( result.getErrors().isEmpty() )
            configStr = result.getLastResultString().replaceAll("\n", "<BR>");
        mav.addObject("configString", configStr);
        
        mav.addObject("result", result);
        return mav;
    }

    public ModelAndView connect(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        ModelAndView mav = getControlModelAndView(request, "control connect");
        
        return mav;
    }
    
    public ModelAndView disconnect(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        ModelAndView mav = getControlModelAndView(request, "control disconnect");
        return mav;
    }
    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    private ModelAndView getReadModelAndView(Meter meter){
        
        ModelAndView mav = new ModelAndView("disconnectMeterWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attribute", BuiltInAttribute.DISCONNECT_STATUS);
        mav.addObject("isRead", true);
        mav.addObject("isSupported", true);
        mav.addObject("isConfigured", true);
        mav.addObject("controlable", controlable);
        mav.addObject("readable", readable);

        return mav;
    }
    
    private ModelAndView getControlModelAndView(HttpServletRequest request, String command) throws Exception {
        Meter meter = getMeter(request);
        
        ModelAndView mav = getReadModelAndView(meter);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = commandRequestExecutor.execute(meter, command, user);
        
        mav.addObject("state", getDisconnectedState(result));
        mav.addObject("configString", "");
        
        mav.addObject("errorsExist", result.isErrorsExist());
        mav.addObject("result", result);
        
        return mav;
    }
    
    private DISCONNECT_STATE getDisconnectedState(CommandResultHolder result) {
        if( result.getValues().isEmpty())
            return DISCONNECT_STATE.UNKNOWN;

        double value = result.getValues().get(0).getValue();
        return getDisconnectedState(value);
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
    private DISCONNECT_STATE getDisconnectedState(double value) {

        if (value == 0 || value == 2) 
            return DISCONNECT_STATE.DISCONNECTED;
        else 
            return DISCONNECT_STATE.CONNECTED;
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
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
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
            CommandRequestExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    @Required
    public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}
}
