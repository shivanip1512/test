package com.cannontech.web.bulk;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.GROUP_DEMAND_RESET)
@Controller
@RequestMapping("/demand-reset/*")
public class DemandResetController {
    
    private static final Logger log = YukonLogManager.getLogger(DemandResetController.class);

    @Autowired private DemandResetService demandResetService;
    @Autowired private AlertService alertService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

   @RequestMapping(value = "action", method = RequestMethod.GET)
    public String action(ModelMap model, DeviceCollection deviceCollection){
        model.addAttribute("deviceCollection", deviceCollection); 
        return "demand/reset/demandReset.jsp";
    }

    @RequestMapping(value = "start", method = RequestMethod.POST)
    public String start(HttpServletRequest request, DeviceCollection deviceCollection, YukonUserContext userContext) {
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.DEMAND_RESET_COMPLETION, alertService,
                messageResolver.getMessageSourceAccessor(userContext), request);
        int key = demandResetService.sendDemandResetAndVerify(deviceCollection, alertCallback, userContext);
        return "redirect:/bulk/progressReport/detail?key=" + key;
    }
}
