package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

@Controller
public class EsubHomeController {
    private RolePropertyDao rolePropertyDao;
    
    @RequestMapping("/esub/home")
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        String esubHomeUrl = ServletUtil.createSafeUrl(request, rolePropertyDao.getPropertyStringValue(YukonRoleProperty.ESUB_HOME_URL, user));
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:" + esubHomeUrl);
        return mav;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}
