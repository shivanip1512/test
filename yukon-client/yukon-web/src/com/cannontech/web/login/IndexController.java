package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.util.ServletUtil;

public class IndexController implements Controller {
    private AuthDao authDao;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        String homeUrl = ServletUtil.createSafeUrl(request, authDao.getRolePropertyValue(user, WebClientRole.HOME_URL));
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:" + homeUrl);
        return mav;
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
}
