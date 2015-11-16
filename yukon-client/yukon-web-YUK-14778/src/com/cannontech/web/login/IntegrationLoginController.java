package com.cannontech.web.login;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.service.impl.SimpleMessageDigestHasher;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.access.UrlAccessChecker;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.google.common.collect.ImmutableMap;

@Controller
public class IntegrationLoginController {
    private final static Logger logger = YukonLogManager.getLogger(IntegrationLoginController.class);

    private final static String HEADER_PROPERTY_KEY = "SITE_MINDER_HEADER";
    private final static String SECRET_PROPERTY_KEY = "SITE_MINDER_SECRET";
    private final static Map<String, String> successfulConnectionResults = ImmutableMap.of("result", "success");

    @Autowired private SimpleMessageDigestHasher simpleHasher;
    @Autowired private LoginService loginService;
    @Autowired private UrlAccessChecker urlAccessChecker;
    @Autowired private ConfigurationSource config;
    @Autowired private YukonUserDao yukonUserDao;

    @RequestMapping("/integrationLogin")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        ServletUtil.deleteAllCookies(request, response);

        LiteYukonUser user = validateLogin(request);
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
    @IgnoreCsrfCheck
    public @ResponseBody Map<String, String> remoteLogin(HttpServletRequest request,
            String username, String password) throws Exception {
        Map<String, String> result = new HashMap<>();
        try {
            loginService.login(request, username, password);
            result.put("result", "success");
            result.put("jsessionId", request.getSession().getId());
        } catch (PasswordExpiredException e) {
            logger.info("The password is expired", e);
            result.put("result", "failure");
            result.put("errorMsg", "The password for " + username
                + " is expired.  Please log into the web to reset it.");
        } catch (AuthenticationThrottleException e) {
            logger.info("Login disabled", e);
            result.put("result", "failure");
            result.put("errorMsg", "Login disabled, please retry after " + e.getThrottleSeconds()
                + " seconds. If the problem persists, contact your system administrator.");
        } catch (BadAuthenticationException | NotAuthorizedException e) {
            logger.info("Invalid username/password", e);
            result.put("result", "failure");
            result.put("errorMsg", "Authentication failed for " + username
                + ". Check that CAPS LOCK is off, and try again. If the problem persists, contact your system "
                + "administrator.");
        }

        return result;
    }

    @RequestMapping(value="/checkConnection", method=RequestMethod.POST)
    @IgnoreCsrfCheck
    public @ResponseBody Map<String, String> checkConnection() {
        return successfulConnectionResults;
    }

    private LiteYukonUser validateLogin(HttpServletRequest request) throws ServletException {
        String headerName = config.getString(HEADER_PROPERTY_KEY, "");
        if (headerName.equals(""))
            return null;

        String loginToken = request.getHeader(headerName);
        if (loginToken == null)
            return null;
        logger.debug("Header found");

        String premiseNumber = ServletRequestUtils.getStringParameter(request, "PremiseNumber");
        String accountIdentifier = ServletRequestUtils.getStringParameter(request, "Account_Identifier");

        if (premiseNumber == null || accountIdentifier == null) {
            return null;
        }
        logger.debug("Premise number and Account Identifier found");

        String secret = config.getString(SECRET_PROPERTY_KEY, "");
        if (secret.equals(""))
            return null;
        logger.debug("Secret found");

        String input = premiseNumber + loginToken + secret;
        String hash = simpleHasher.hash(input);

        if (!hash.equalsIgnoreCase(accountIdentifier)) {
            return null;
        }
        logger.debug("Hash validation successful");

        LiteYukonUser user = yukonUserDao.findUserByUsername(premiseNumber);
        if (user == null) {
            return null;
        }
        logger.debug("User found for premise");

        loginService.createSession(request, user);
        ActivityLogger.logEvent(user.getUserID(), LoginService.LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername()
            + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());
        return user;
    }
}
