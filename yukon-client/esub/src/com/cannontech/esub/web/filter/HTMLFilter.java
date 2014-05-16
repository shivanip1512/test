package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.HTMLGenerator;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

/**
 * Forwards all request for any file that matches this filter to the /images directory
 */
public class HTMLFilter implements Filter {

    private FilterConfig config;
    private HTMLGenerator htmlGenerator = new HTMLGenerator();

    @Override
    public void init(FilterConfig fc) throws ServletException {
        config = fc;
        htmlGenerator.getGenOptions().setStaticHTML(false);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        response.setContentType("text/html");

        String uri = request.getRequestURI();

        // Do nothing if this isn't an HTML request.
        if (!uri.endsWith(".html")) {
            chain.doFilter(request, response);
            return;
        }

        String conPath = request.getContextPath();

        String jlxPath = uri.replaceFirst(conPath, "");
        jlxPath = config.getServletContext().getRealPath(jlxPath);

        // Assume this ends with .html
        jlxPath = jlxPath.substring(0, jlxPath.length() - 5) + ".jlx";
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        try {
            Drawing drawing = new Drawing();
            drawing.setUserContext(userContext);

            LiteYukonUser user = (LiteYukonUser) request.getSession(false).getAttribute(LoginController.YUKON_USER);
            YukonRole yukonRole = drawing.getMetaElement().getRole();

            drawing.load(jlxPath);

            // Check if this user has access to this drawing!
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);

            if (rolePropertyDao.checkRole(yukonRole, user)) {
                htmlGenerator.generate(response.getWriter(), drawing);
            }
        } catch (Exception e) {
            CTILogger.error(e);
        }
    }

    @Override
    public void destroy() {
        config = null;
    }
}
