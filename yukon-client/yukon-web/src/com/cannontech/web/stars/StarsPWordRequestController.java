package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.model.RequestPword;
import com.cannontech.stars.core.model.StarsRequestPword;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.impl.GlobalSettingsDaoImpl;
import com.cannontech.util.ServletUtil;

public class StarsPWordRequestController implements Controller {
    private static final String INVALID_URI = "/spring/login/forgotPassword?failedMsg=";
    private static final String SUCCESS_URI = "/spring/login/forgotPassword?success=true";
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    
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
        boolean authorized = YukonSpringHook.getBean("globalSettingsDao",GlobalSettingsDaoImpl.class).checkSetting(GlobalSetting.ENABLE_PASSWORD_RECOVERY);
        if(!authorized) {
            throw new NotAuthorizedException("Missing a required role or property to use this page.");
        }
        else if( !reqPword.isValidParams() )
        {
            //not enough info, return error
            returnURI = INVALID_URI + "MORE_INFO";
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
            String userName, String email, String fName, String lName, String accNum )
    {
        StarsRequestPword reqPword = new StarsRequestPword( 
                userName,
                email,
                fName,
                lName,
                accNum,
                starsCustAccountInformationDao);

        return reqPword;
    }

    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
}
