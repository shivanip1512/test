package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.users.dao.YukonUserPreferenceDao;
import com.cannontech.core.users.model.YukonUserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

@Controller
public class HomeController {

    @Autowired private YukonUserPreferenceDao prefDao;
    
    @RequestMapping({"/home", "/index.jsp"})
    public String home(HttpServletRequest req) {
        
        final LiteYukonUser user = ServletUtil.getYukonUser(req);
        String homeUrl = prefDao.getValueOrDefault(user, YukonUserPreferenceName.HOME_URL);
        
        return "redirect:" + ServletUtil.createSafeUrl(req, homeUrl);
    }
    
    @RequestMapping("/dashboard")//WTF WON"T THIS WORK?
    public String dashboard(HttpServletRequest req) {
        
        return "dashboard.jsp";
    }
    
}