package com.cannontech.web.bulk;

import java.util.Collections;
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
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.i18n.MessageSourceAccessor;
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
    @Autowired private AlertService alertService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CollectionActionService collectionActionService;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, DeviceCollection deviceCollection) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        if (disconnectService.supportsArm(deviceCollection.getDeviceList())) {
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
            model.addAttribute("result", collectionActionService.getResult(Integer.parseInt(key)));
        }
        
        return "disconnect/resultDetail.jsp";
    }

    @RequestMapping("start")
    public String start(HttpServletRequest request, 
                      ModelMap model, 
                      DeviceCollection deviceCollection,
                      YukonUserContext userContext, 
                      DisconnectCommand command) throws ServletException {
        CollectionActionResult result = disconnectService.execute(command,
                                      deviceCollection,
                                      new AlertCallback(userContext, request),
                                      userContext);
        return "redirect:/bulk/progressReport/detail?key=" + result.getCacheKey();
    }

    @RequestMapping("recentResults")
    public String recentResults(ModelMap model) throws ServletException {
     //   model.addAttribute("results", disconnectService.getResults()); 
        
        return "disconnect/results.jsp";
    }

    @RequestMapping("resultDetail")
    public String resultDetail(ModelMap model, String resultKey, DisconnectCommand command) throws ServletException {
        CollectionActionResult result = collectionActionService.getResult(Integer.parseInt(resultKey));
     /*   model.addAttribute("result", result);
        model.addAttribute("command", command);*/
        
        return "disconnect/resultDetail.jsp";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> cancel(String key, YukonUserContext userContext, DisconnectCommand command) {
      //  disconnectService.cancel(new Integer(key), userContext.getYukonUser(), command);
        
        return Collections.singletonMap("success", "true");
    }

    private class AlertCallback implements SimpleCallback<CollectionActionResult> {
        private MessageSourceAccessor accessor;
        private String partialUrl;

        AlertCallback(YukonUserContext userContext, HttpServletRequest request) {
            this.accessor = messageResolver.getMessageSourceAccessor(userContext);
            this.partialUrl = ServletUtil.createSafeUrl(request, "/bulk/disconnect/resultDetail");
        }

        @Override
        public void handle(CollectionActionResult result) {
           /* log.debug("Alert Callback");
            ResolvableTemplate template;
            double percentSuccess = result.getCounts().getPercentSuccess();
            if (result.isFailed()) {
                template = new ResolvableTemplate("yukon.common.alerts.disconnectCompletion.failed");
                String exceptionReason = result.getExecutionExceptionText();
                template.addData("notCompletedCount", result.getCounts().getNotCompleted());
                template.addData("exceptionReason", exceptionReason);
            } else {
                template = new ResolvableTemplate("yukon.common.alerts.disconnectCompletion");
            }
            String url = partialUrl + "?resultKey=" + result.getCacheKey();
            template.addData("url", url);
            template.addData("command", accessor.getMessage(result.getAction().toString()));
            template.addData("percentSuccess", percentSuccess);
            template.addData("completedCount", result.getCounts().getCompleted());

            Alert alert = new BaseAlert(new Date(), template) {
                @Override
                public com.cannontech.common.alert.model.AlertType getType() {
                    return AlertType.DISCONNECT_COMPLETION;
                };
            };*/
            //alertService.add(alert);
        }
    };
}
