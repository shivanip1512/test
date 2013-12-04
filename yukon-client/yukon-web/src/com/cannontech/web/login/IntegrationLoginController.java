package com.cannontech.web.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.service.impl.SimpleMessageDigestHasher;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.access.UrlAccessChecker;

@Controller
public class IntegrationLoginController {
    private static final String HEADER_PROPERTY_KEY = "SITE_MINDER_HEADER";
    private static final String SECRET_PROPERTY_KEY = "SITE_MINDER_SECRET";

    @Autowired private SimpleMessageDigestHasher simpleHasher;
    @Autowired private LoginService loginService;
    @Autowired private UrlAccessChecker urlAccessChecker;
    @Autowired private ConfigurationSource config;
    @Autowired private YukonUserDao yukonUserDao;

    private final Logger logger = YukonLogManager.getLogger(getClass());

    @RequestMapping("/integrationLogin")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        ServletUtil.deleteAllCookies(request, response);

        LiteYukonUser user = validateLogin(request, response);
        String redirect = null;
        if (user != null) {
            redirect = "/home";

            boolean hasUrlAccess = urlAccessChecker.hasUrlAccess(redirect, user);
            if (!hasUrlAccess) {
                redirect = LoginController.LOGIN_URL + "?" + LoginController.INVALID_URL_ACCESS_PARAM;
            }
        } else {
            ServletUtil.deleteAllCookies(request, response);

            String referer = request.getHeader("Referer");
            referer = (referer != null) ? referer : LoginController.LOGIN_URL;

            redirect = ServletUtil.createSafeRedirectUrl(request, referer);
        }

        return new ModelAndView("redirect:" + redirect);
    }
    
    @RequestMapping(value="/remoteLogin", method=RequestMethod.POST)
    public @ResponseBody JSONObject remoteLogin(HttpServletRequest request, HttpServletResponse response, String username, String password) throws Exception {
        JSONObject result = new JSONObject();
        try {
            loginService.login(request, username, password);
            result.put("result", "success");
            result.put("jsessionId",  request.getSession().getId());
        } catch (BadAuthenticationException|NotAuthorizedException e) {
            result.put("result", "failure");
            result.put("errorMsg", "Unable to log user in. Reason: " + e.getMessage());
        } catch (PasswordExpiredException e) {
            result.put("result", "failure");
            result.put("errorMsg", "The password is expired");
        }
        return result;
    }
    
    @RequestMapping(value="/checkConnection", method=RequestMethod.POST)
    public @ResponseBody JSONObject checkConnection(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        if(request.getSession(false) == null){
            result.put("result", "failure");
        }else{
            result.put("result", "success");  
        }
        return result;
    }
    
    private LiteYukonUser validateLogin(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        String headerName = config.getString(HEADER_PROPERTY_KEY, "");
        if (headerName.equals("")) return null;

        String loginToken = request.getHeader(headerName);
        if (loginToken == null) return null;
        logger.debug("Header found");

        String premiseNumber = ServletRequestUtils.getStringParameter(request, "PremiseNumber");
        String accountIdentifier = ServletRequestUtils.getStringParameter(request, "Account_Identifier");

        if (premiseNumber == null || accountIdentifier == null) return null;
        logger.debug("Premise number and Account Identifier found");

        String secret = config.getString(SECRET_PROPERTY_KEY, "");
        if (secret.equals("")) return null;
        logger.debug("Secret found");

        String input = premiseNumber + loginToken + secret;
        String hash = simpleHasher.hash(input);

        if (!hash.equalsIgnoreCase(accountIdentifier)) return null;
        logger.debug("Hash validation successful");

        LiteYukonUser user = yukonUserDao.findUserByUsername(premiseNumber);
        if (user == null) return null;
        logger.debug("User found for premise");

        loginService.createSession(request, user);
        ActivityLogger.logEvent(user.getUserID(),
                                LoginService.LOGIN_WEB_ACTIVITY_ACTION,
                                "User " + user.getUsername() + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());        
        return user;
    }
}
   
