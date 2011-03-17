package com.cannontech.web.editor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.util.ParamUtil;
import com.cannontech.web.navigation.CtiNavObject;

public class OnelineBookmarkController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req,
            HttpServletResponse resp) throws Exception {

        String itemID = ParamUtil.getString(req, "itemid", null);
        String url = null;
        
        if (itemID != null)
        {
            url = OnelineUtil.createEditLink(Integer.parseInt( itemID));

            HttpSession session = req.getSession(false);
            CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
            navObject.setModuleExitPage(navObject.getPreviousPage());
            navObject.setCurrentPage(url);
            //The above two setter lines are in place because of a navigation problem when trying to return
            //from a faces edit page within OneLine.

            resp.sendRedirect(url);
        }
    
        return null;
    }

}
