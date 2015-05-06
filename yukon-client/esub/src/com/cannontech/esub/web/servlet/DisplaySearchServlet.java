package com.cannontech.esub.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.esub.util.DisplaySearch;
import com.cannontech.util.ServletUtil;

/**
 * Takes a search key and redirects the browser to the matching display, if any.
 * In the event a display is found, the browser is redirect to the display url,
 * otherwise the browser is redirected back to the searchurl.  A parameters
 * is attached to indicate that the search failed.  search=false
 * 
 * Parameters:
 * key       -   The search key
 * searchurl -   The url of the search page in case nothing is found.
 *    
 * @author alauinger
 */
public class DisplaySearchServlet extends HttpServlet {

   private static final String PARAM_SEARCH_KEY = "key";
   private static final String PARAM_SEARCH_URL = "searchurl";
   
    /**
     * @see javax.servlet.http.HttpServlet#service(HttpServletRequest,
     *      HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String key = req.getParameter(PARAM_SEARCH_KEY);
        String searchUrl = req.getParameter(PARAM_SEARCH_URL);
        
        if(key != null) {
            DisplaySearch ds = new DisplaySearch();
            String displayUrl = ds.findDisplay(key);
            if(displayUrl != null) {
                displayUrl = ServletUtil.createSafeRedirectUrl(req, displayUrl);
                resp.sendRedirect(displayUrl);
                return;
            }
        }
        searchUrl += "?search=false";
        searchUrl = ServletUtil.createSafeRedirectUrl(req, searchUrl);
        resp.sendRedirect(searchUrl);
    }

}
