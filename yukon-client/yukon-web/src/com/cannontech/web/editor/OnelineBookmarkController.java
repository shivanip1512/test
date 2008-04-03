package com.cannontech.web.editor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.util.ParamUtil;

public class OnelineBookmarkController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req,
            HttpServletResponse resp) throws Exception {

        String itemID = ParamUtil.getString(req, "itemid", null);
        String url = null;
        
        if (itemID != null)
        {
            url = OnelineUtil.createEditLink(Integer.parseInt( itemID));
            //removed since CapControlForm is controlling it's own bookmarking now.
            resp.sendRedirect(url);
        }
    
        return null;
    }

}
