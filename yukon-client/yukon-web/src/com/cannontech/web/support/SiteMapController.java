package com.cannontech.web.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
public class SiteMapController {
    
    @Autowired private SiteMapHelper siteMapHelper;
    @Autowired private RolePropertyDao rolePropertyDao;

    @RequestMapping("/sitemap")
    public String view(ModelMap model, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        boolean hasCIRole = rolePropertyDao.checkRole(YukonRole.CI_CURTAILMENT, user);
        boolean isCIOperator = rolePropertyDao.checkProperty(YukonRoleProperty.CURTAILMENT_IS_OPERATOR, user);
        if (hasCIRole && !isCIOperator) {
            throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
        }

        model.addAttribute("pageMap", siteMapHelper.getSiteMap(context));
        return "siteMap.jsp";
    }
}