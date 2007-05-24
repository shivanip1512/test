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
        HttpSession session = req.getSession(false);
        String url = null;
        CapControlForm capForm = null;
        capForm = (CapControlForm) session.getAttribute("capControlForm");
        
        if (itemID != null)
        {
            if (capForm != null)
                capForm.initItem(Integer.parseInt( itemID), OnelineUtil.EDITOR_CAPCONTROL);
            url = OnelineUtil.createEditLink(Integer.parseInt( itemID));
            CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
            navObject.setNavigation(url);
            navObject.getHistory().push(navObject.getPreviousPage());
            resp.sendRedirect(url);
        }
    
        return null;
    }

}
