package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.gateway.GatewayControllerHelper;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/firmwareDetailsWidget")
public class FirmwareDetailsWidget extends AdvancedWidgetControllerBase {

    @Autowired private GatewayControllerHelper helper;

    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
            @DefaultSort(dir = Direction.desc, sort = "TIMESTAMP") SortingParameters sorting) {
        helper.addGatewayMessages(model, userContext);
        helper.buildFirmwareListModel(model, userContext, sorting);
        return "firmwareDetailsWidget/render.jsp";
    }

}
