package com.cannontech.web.widget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.stars.gateway.GatewayControllerHelper;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Controller
@RequestMapping("/gatewayListWidget")
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class GatewayListWidget extends AdvancedWidgetControllerBase {

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private GatewayControllerHelper helper;

    @GetMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, FlashScope flash,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting) {
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        // Check for gateways with duplicate colors
        // If any are found, output a flash scope warning to notify the user
        Multimap<Short, RfnGateway> duplicateColorGateways = rfnGatewayService.getDuplicateColorGateways(gateways);
        if (!duplicateColorGateways.isEmpty()) {
            StringBuilder gatewaysString = new StringBuilder();
            for (Short color : duplicateColorGateways.keySet()) {
                Set<String> gatewayNames = duplicateColorGateways.get(color)
                                                                 .stream()
                                                                 .map(RfnGateway::getName)
                                                                 .collect(Collectors.toSet());
                gatewaysString.append(color)
                              .append(" (")
                              .append(StringUtils.join(gatewayNames, ", "))
                              .append(") ");
            }
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable(
                    "yukon.web.modules.operator.gateways.list.duplicateColors",
                    gatewaysString);
            flash.setWarning(message);
        }
        helper.addGatewayMessages(model, userContext);
        helper.buildGatewayListModel(model, userContext, sorting, gateways);
        return "gatewayListWidget/render.jsp";
    }

}
