package com.cannontech.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.util.ParamUtil;
import com.cannontech.web.navigation.CtiNavObject;


@SuppressWarnings("serial")
public class OnelineBookmarkServlet extends HttpServlet {

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String itemID = ParamUtil.getString(req, "itemid", null);
        HttpSession session = req.getSession(false);
        String url = null;
        if (itemID != null)
        {
                        
            url = OnelineUtil.createEditLink(Integer.parseInt( itemID));
            //CBCNavigationUtil.goBack(session);
            CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
            //navObject.getHistory().push("/capcontrol/oneline/Sub 1000.html");
            navObject.setNavigation(url);
            navObject.getHistory().push(navObject.getPreviousPage());
            resp.sendRedirect(url);
        }
    }

}
