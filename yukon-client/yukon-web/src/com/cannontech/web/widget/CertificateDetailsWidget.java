package com.cannontech.web.widget;

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
@RequestMapping("/certificateDetailsWidget")
public class CertificateDetailsWidget extends AdvancedWidgetControllerBase {

    @Autowired private GatewayControllerHelper helper;

    @GetMapping("render")
    public String render(ModelMap model, YukonUserContext userContext,
            @DefaultSort(dir = Direction.desc, sort = "TIMESTAMP") SortingParameters sorting) {
        helper.buildCertificateListModel(model, userContext, sorting);
        return "certificateDetailsWidget/render.jsp";
    }

}
