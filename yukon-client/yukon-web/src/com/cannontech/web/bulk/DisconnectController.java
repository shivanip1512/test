package com.cannontech.web.bulk;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.GROUP_DISCONNECT_CONTROL)
@Controller
@RequestMapping("/disconnect/*")
public class DisconnectController {

    @Autowired private DisconnectService disconnectService;
    @Autowired private AlertService alertService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, DeviceCollection deviceCollection) throws ServletException {
        setupModel(model, deviceCollection);
        model.addAttribute("action", CollectionAction.DISCONNECT);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/disconnect/home.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "disconnectInputs", method = RequestMethod.GET)
    public String disconnectInputs(ModelMap model, DeviceCollection deviceCollection) throws ServletException {
        setupModel(model, deviceCollection);
        return "disconnect/home.jsp";
    }
    
    private void setupModel(ModelMap model, DeviceCollection deviceCollection) {
        model.addAttribute("deviceCollection", deviceCollection);
        if (disconnectService.supportsArm(deviceCollection.getDeviceList())) {
            model.addAttribute("displayArmLink", "true");
        }
    }

    @RequestMapping(value = "start", method = RequestMethod.POST)
    public String start(HttpServletRequest request, ModelMap model, DeviceCollection deviceCollection,
            YukonUserContext userContext, DisconnectCommand command) throws ServletException {
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.DISCONNECT_COMPLETION, alertService,
                messageResolver.getMessageSourceAccessor(userContext), request);
        CollectionActionResult result =
            disconnectService.execute(command, deviceCollection, alertCallback, null, userContext);
        return "redirect:/collectionActions/progressReport/detail?key=" + result.getCacheKey();
    }
}
