package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.users.dao.UserPreferenceDao;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

@Controller
public class HomeController {

    @Autowired private UserPreferenceDao prefDao;
    
    @RequestMapping({"/home", "/index.jsp"})
    public String home(HttpServletRequest req) {
        
        final LiteYukonUser user = ServletUtil.getYukonUser(req);
        String homeUrl = prefDao.getValueOrDefault(user, UserPreferenceName.HOME_URL);
        
        return "redirect:" + ServletUtil.createSafeUrl(req, homeUrl);
    }
    
    @RequestMapping({"/dashboard", "/operator/Operations.jsp"})
    public String dashboard(HttpServletRequest req) {
        //TODO make an awesome dashboard
        return "dashboard.jsp";
    }
    
}