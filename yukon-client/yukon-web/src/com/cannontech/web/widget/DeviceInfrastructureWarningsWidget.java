package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display Infrastructure Warnings
 */
@Controller
@RequestMapping("/deviceInfrastructureWarningsWidget/*")
public class DeviceInfrastructureWarningsWidget extends AdvancedWidgetControllerBase {
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;

    @Autowired
    public DeviceInfrastructureWarningsWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {

        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setLazyLoad(true);
    }

    @RequestMapping("render")
    public String render(ModelMap model, int deviceId, YukonUserContext userContext) {
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings(deviceId);
        Comparator<InfrastructureWarning> comparator =
            (o1, o2) -> o1.getSeverity().name().compareTo(o2.getSeverity().name());
        if (!CollectionUtils.isEmpty(warnings)) {
            Collections.sort(warnings, comparator);
        }
        model.addAttribute("warnings", warnings);

        return "deviceInfrastructureWarningsWidget/render.jsp";
    }

}