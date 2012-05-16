package com.cannontech.web.capcontrol;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.web.navigation.CtiNavObject;

@Controller
@RequestMapping("/onelineBookmark/*")
public class OnelineBookmarkController {

    @RequestMapping
    public void mark(HttpServletRequest request, HttpServletResponse response, Integer itemid) throws IOException {
        if (itemid != null) {
            String url = OnelineUtil.createEditLink(itemid);

            HttpSession session = request.getSession(false);
            CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
            navObject.setModuleExitPage(navObject.getPreviousPage());
            navObject.setCurrentPage(url);
            //The above two setter lines are in place because of a navigation problem when trying to return
            //from a faces edit page within OneLine.

            response.sendRedirect(url);
        }
    }
    
}