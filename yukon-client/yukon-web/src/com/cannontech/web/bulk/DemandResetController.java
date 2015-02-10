package com.cannontech.web.bulk;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.GROUP_DEMAND_RESET)
@Controller
@RequestMapping("/demand-reset/*")
public class DemandResetController {
    
    private static final Logger log = YukonLogManager.getLogger(DemandResetController.class);

    @Autowired private DemandResetService demandResetService;
    @Autowired private AlertService alertService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @RequestMapping("action")
    public String action(ModelMap model, DeviceCollection deviceCollection, String key){
        model.addAttribute("deviceCollection", deviceCollection); 
        if (StringUtils.isNotBlank(key)) {
            model.addAttribute("result", demandResetService.getResult(key));
        }
        
        return "demand/reset/resultDetail.jsp";
    }

    @RequestMapping("start")
    public String start(HttpServletRequest request, 
                      ModelMap model, 
                      DeviceCollection deviceCollection,
                      YukonUserContext userContext){
        
        DemandResetResult result = demandResetService.sendDemandResetAndVerify(deviceCollection,
                                                            new AlertCallback(request),
                                                            userContext);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        model.addAttribute("key", result.getKey());

        
        return "redirect:action";
    }

    @RequestMapping("recent-results")
    public String recentResults(ModelMap model){
        model.addAttribute("results", demandResetService.getResults()); 
        
        return "demand/reset/results.jsp";
    }

    @RequestMapping("result-detail")
    public String resultDetail(ModelMap model, String resultKey){
        model.addAttribute("result", demandResetService.getResult(resultKey));    
        
        return "demand/reset/resultDetail.jsp";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public void cancel(HttpServletResponse resp, String key, YukonUserContext userContext) {
        
        demandResetService.cancel(key, userContext.getYukonUser());
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }

    private class AlertCallback implements SimpleCallback<DemandResetResult> {

        String url;

        AlertCallback(HttpServletRequest request) {
            this.url= ServletUtil.createSafeUrl(request, "/bulk/demand-reset/result-detail?resultKey=");
        }

        @Override
        public void handle(DemandResetResult result) {
            log.debug("Alert Callback");
            ResolvableTemplate template;
            int successCount = result.getSuccessCount();

            if (log.isDebugEnabled()) {
                log.debug("successCount=" + result.getSuccessCount());
                log.debug("failureCount=" +  result.getFailedCount());
                log.debug("completedCount=" + result.getCompletedCount());
                log.debug("notAttemptedCount=" + result.getNotAttemptedCount());
            }
            int percentSuccess =
                result.getCompletedCount() > 0 ? successCount * 100 / result.getCompletedCount() : 0;
            if (result.isExceptionOccured()) {
                template = new ResolvableTemplate("yukon.common.alerts.demandResetCompletion.failed");
                int shouldHaveCompleted = result.getTotalCount() - result.getNotAttemptedCount();  
                int notCompletedCount = shouldHaveCompleted - result.getCompletedCount();
                if (log.isDebugEnabled()) {
                    log.debug("shouldHaveCompleted=" + shouldHaveCompleted + " notCompletedCount" + notCompletedCount);
                }
                String exceptionReason = result.getExceptionReason();
                template.addData("notCompletedCount", notCompletedCount);
                template.addData("exceptionReason", exceptionReason);
            } else {
                template = new ResolvableTemplate("yukon.common.alerts.demandResetCompletion");
            }
            template.addData("url", url + result.getKey());
            template.addData("percentSuccess", percentSuccess);
            template.addData("completedCount", result.getCompletedCount());

            Alert alert = new BaseAlert(new Date(), template) {
                @Override
                public com.cannontech.common.alert.model.AlertType getType() {
                    return AlertType.DEMAND_RESET_COMPLETION;
                };
            };
            alertService.add(alert);
        }
    };
}
