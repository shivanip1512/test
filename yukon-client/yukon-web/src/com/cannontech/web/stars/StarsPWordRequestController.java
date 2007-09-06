package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.servlet.logic.RequestPword;
import com.cannontech.servlet.logic.StarsRequestPword;
import com.cannontech.util.ServletUtil;

public class StarsPWordRequestController implements Controller {
    private static final String INVALID_URI = "/pwordreq.jsp?failedMsg=";
    private static final String SUCCESS_URI = "/pwordreq.jsp?success=true";
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userName = ServletUtil.getParameter( request, "USERNAME");
        String email = ServletUtil.getParameter( request, "EMAIL");
        String fName = ServletUtil.getParameter( request, "FIRST_NAME");
        String lName = ServletUtil.getParameter( request, "LAST_NAME");
        String accNum = ServletUtil.getParameter( request, "ACCOUNT_NUM");      
        String notes = ServletUtil.getParameter( request, "NOTES");
        String energyComp = ServletUtil.getParameter( request, "ENERGY_COMPANY");


        RequestPword reqPword = createRequest( 
            request, userName,
            email, fName, lName, accNum );

        reqPword.setNotes( notes );
        reqPword.setEnergyCompany( energyComp );

        String returnURI = "";

        if( !reqPword.isValidParams() )
        {
            //not enough info, return error
            returnURI = INVALID_URI + "More information needs to be entered";
        }
        else
        {
            //do the work
            reqPword.doRequest();
            
            //decide where we need to go next
            if( reqPword.getState() == RequestPword.RET_SUCCESS )
                returnURI = SUCCESS_URI;
            else
                returnURI = INVALID_URI + reqPword.getResultString();
        }
        
        
        response.sendRedirect( request.getContextPath() + returnURI );      
        return null;
    }
    
    private RequestPword createRequest( HttpServletRequest req_,
            String userName_, String email_, String fName_, String lName_, String accNum_ )
    {
        StarsRequestPword reqPword = new StarsRequestPword( 
                userName_,
                email_,
                fName_,
                lName_,
                accNum_ );


        return reqPword;
    }

}
