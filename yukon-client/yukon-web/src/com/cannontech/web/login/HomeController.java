package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

@Controller
public class HomeController {

    @Autowired private RolePropertyDao rolePropertyDao;

    @RequestMapping({"/home", "/index.jsp"})
    public String home(HttpServletRequest req) {
        
        final LiteYukonUser user = ServletUtil.getYukonUser(req);
        String homeUrl = ServletUtil.createSafeUrl(req, rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user));
        
        return "redirect:" + homeUrl;
    }
    
    @RequestMapping({"/dashboard", "/operator/Operations.jsp"})
    public String dashboard(HttpServletRequest req) {
        //TODO make an awesome dashboard
        return "dashboard.jsp";
    }
    
}