package com.cannontech.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.constants.LoginController;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;

public class HomeServlet extends HttpServlet {

    public HomeServlet() {
        super();
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            redirectToLogin(request, response);
            return;
        }
        
        LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
        if (user == null) {
            redirectToLogin(request, response);
            return;
        }
        
        String home_url = AuthFuncs.getRolePropertyValue(user, WebClientRole.HOME_URL);
        if (StringUtils.isBlank(home_url)) {
            home_url = "/";
        }
        response.sendRedirect(request.getContextPath() + home_url);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginUrl = request.getContextPath() + "/login.jsp";
        response.sendRedirect(loginUrl);
    }
}
