package com.cannontech.web.bulk;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.GROUP_DISCONNECT_CONTROL)
@Controller
@RequestMapping("/disconnect/*")
public class DisconnectController {
    
    private static final Logger log = YukonLogManager.getLogger(DisconnectController.class);

    @Autowired private DisconnectService disconnectService;
    @Autowired private MeterDao meterDao;
    @Autowired private AlertService alertService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @RequestMapping("home")
    public String home(ModelMap model, DeviceCollection deviceCollection) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        if (disconnectService.supportsArm(deviceCollection)) {
            model.addAttribute("displayArmLink", "true");
        }
       
        return "disconnect/home.jsp";
    }

    @RequestMapping("action")
    public String action(ModelMap model, DeviceCollection deviceCollection, String key, DisconnectCommand command)
            throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("command", command);   
        if (StringUtils.isNotBlank(key)) {
            model.addAttribute("result", disconnectService.getResult(key));
        }
        
        return "disconnect/resultDetail.jsp";
    }

    @RequestMapping("start")
    public String start(HttpServletRequest request, 
                      ModelMap model, 
                      DeviceCollection deviceCollection,
                      YukonUserContext userContext, 
                      DisconnectCommand command) throws ServletException {
        DisconnectResult result = disconnectService.execute(command,
                                      deviceCollection,
                                      new AlertCallback(userContext, request),
                                      userContext);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        model.addAttribute("key", result.getKey());
        model.addAttribute("command", command);   
        
        return "redirect:action";
    }

    @RequestMapping("recentResults")
    public String recentResults(ModelMap model) throws ServletException {
        model.addAttribute("results", disconnectService.getResults()); 
        
        return "disconnect/results.jsp";
    }

    @RequestMapping("resultDetail")
    public String resultDetail(ModelMap model, String resultKey, DisconnectCommand command) throws ServletException {
        DisconnectResult result = disconnectService.getResult(resultKey);
        model.addAttribute("result", result);
        model.addAttribute("command", command);
        
        return "disconnect/resultDetail.jsp";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> cancel(String key, YukonUserContext userContext, DisconnectCommand command) {
        disconnectService.cancel(key, userContext, command);
        
        return Collections.singletonMap("success", "true");
    }

    private class AlertCallback implements SimpleCallback<DisconnectResult> {
        MessageSourceAccessor accessor;
        HttpServletRequest request;

        AlertCallback(YukonUserContext userContext, HttpServletRequest request) {
            accessor = messageResolver.getMessageSourceAccessor(userContext);
            this.request = request;
        }

        @Override
        public void handle(DisconnectResult result) {
            log.debug("Alert Callback");
            ResolvableTemplate template;
            int successCount = result.getSuccessCount();

            if (log.isDebugEnabled()) {
                log.debug("successCount=" + result.getSuccessCount());
                log.debug("failureCount=" +  result.getFailedCount());
                log.debug("completedCount=" + result.getCompletedCount());
            }
            int percentSuccess =
                result.getCompletedCount() > 0 ? successCount * 100 / result.getCompletedCount() : 0;
            if (result.isExceptionOccured()) {
                template = new ResolvableTemplate("yukon.common.alerts.disconnectCompletion.failed");
                int shouldHaveCompleted = result.getTotalCount() - result.getNotAttemptedCount();  
                int notCompletedCount = shouldHaveCompleted - result.getCompletedCount();
                if (log.isDebugEnabled()) {
                    log.debug("shouldHaveCompleted=" + shouldHaveCompleted + " notCompletedCount" + notCompletedCount);
                }
                String exceptionReason = result.getExceptionReason();
                template.addData("notCompletedCount", notCompletedCount);
                template.addData("exceptionReason", exceptionReason);
            } else {
                template = new ResolvableTemplate("yukon.common.alerts.disconnectCompletion");
            }
            String url =
                ServletUtil.createSafeUrl(request, "/bulk/disconnect/resultDetail?resultKey=" + result.getKey() + "&command="
                                                  + result.getCommand());
            template.addData("url", url);
            template.addData("command", accessor.getMessage(result.getCommand()));
            template.addData("percentSuccess", percentSuccess);
            template.addData("completedCount", result.getCompletedCount());

            Alert alert = new BaseAlert(new Date(), template) {
                @Override
                public com.cannontech.common.alert.model.AlertType getType() {
                    return AlertType.DISCONNECT_COMPLETION;
                };
            };
            alertService.add(alert);
        }
    };
}
