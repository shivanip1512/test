package com.cannontech.web.widget;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/pendingProfilesWidget/*")
public class PendingProfilesWidget extends WidgetControllerBase {
    
    @Autowired private LoadProfileService loadProfileService;
    @Autowired private PaoDao paoDao;
    
    @Autowired
    public PendingProfilesWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        Set<WidgetInput> simpleWidgetInputSet = new HashSet<WidgetInput>();
        simpleWidgetInputSet.add(simpleWidgetInput);
        
        String checkRole = YukonRole.METERING.name();
        setIdentityPath("common/deviceIdentity.jsp");
        setInputs(simpleWidgetInputSet);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("pendingProfilesWidget/render.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // get pending
        List<Map<String, String>> pendingRequests = getPendingRequests(deviceId, userContext);
        mav.addObject("pendingRequests", pendingRequests);
        mav.addObject("deviceId", deviceId);
        
        return mav;
    }
    
    @RequestMapping("refreshPending")
    public ModelAndView refreshPending(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("pendingProfilesWidget/ongoingProfiles.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");

        // stop request
        long stopRequestId = ServletRequestUtils.getLongParameter(request, "stopRequestId", 0);
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        if (stopRequestId != 0) {
            loadProfileService.removePendingLoadProfileRequest(device, stopRequestId, userContext);
        }
        
        // re-get pending
        List<Map<String, String>> pendingRequests = getPendingRequests(deviceId, userContext);
        
        for (Map<String, String> pendingRequest : pendingRequests) {
            
            Long requestId = Long.valueOf(pendingRequest.get("requestId"));
            Double percentDone = loadProfileService.calculatePercentDone(requestId);
            
            pendingRequest.put("requestId", requestId.toString());
            
            if(percentDone != null){
                
                DecimalFormat df = new DecimalFormat("#.#");
                pendingRequest.put("percentDone", df.format(percentDone));
            }
            else{
                mav.addObject("percentDone", percentDone);
                mav.addObject("lastReturnMsg", loadProfileService.getLastReturnMsg(requestId));
            }
        }
        
        mav.addObject("isRfn",device.getPaoType().isRfMeter());
        mav.addObject("pendingRequests", pendingRequests);
        mav.addObject("deviceId", deviceId);
        
        return mav;
    }
    
    private List<Map<String, String>> getPendingRequests(int deviceId, YukonUserContext userContext) {
        
        LiteYukonPAObject devicePaoObj = paoDao.getLiteYukonPAO(deviceId);
        List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(devicePaoObj, userContext);
        
        return pendingRequests;
        
    }
    
}