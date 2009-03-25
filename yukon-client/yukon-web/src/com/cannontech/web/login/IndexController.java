package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class IndexController implements Controller {
    private RolePropertyDao rolePropertyDao;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        String homeUrl = ServletUtil.createSafeUrl(request, rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user));
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:" + homeUrl);
        return mav;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
}
