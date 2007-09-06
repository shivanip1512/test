package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.util.ServletUtil;

public class HomeServletController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        String home_url = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.HOME_URL);
        if (StringUtils.isBlank(home_url)) {
            home_url = "/";
        }
        
        mav.setViewName("redirect:" + home_url);
        return mav;
    }

}
