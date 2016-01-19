package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.access.UrlAccessChecker;
import com.cannontech.web.stars.service.PasswordResetService;

public class YukonLoginController extends MultiActionController {
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private LoginCookieHelper loginCookieHelper;
    @Autowired private LoginService loginService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UrlAccessChecker urlAccessChecker;

    /**
     * @param request unused
     * @param response unused
     */
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        if (request.getSession().getAttribute(LoginController.YUKON_USER) != null) {
            String redirect = "/dashboard";
            return new ModelAndView("redirect:" + redirect);
        }

        mav.setViewName("login.jsp");

        boolean useOldForgottenPasswordPage =
            configurationSource.getBoolean(MasterConfigBoolean.USE_OLD_FORGOTTEN_PASSWORD_PAGE);
        mav.addObject("useOldForgottenPasswordPage", useOldForgottenPasswordPage);
        return mav;
    }

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = ServletRequestUtils.getRequiredStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getRequiredStringParameter(request, LoginController.PASSWORD);
        Boolean createRememberMeCookie = ServletRequestUtils.getBooleanParameter(request, "rememberme", false);
        String redirectedFrom = ServletRequestUtils.getStringParameter(request, LoginController.REDIRECTED_FROM);

        String redirect;

        long retrySeconds = 0;
        try {
            loginService.login(request, username, password);
        } catch (AuthenticationThrottleException e) {
            retrySeconds = e.getThrottleSeconds();

            redirect = getBadAuthenticationRedirectUrl(request, redirectedFrom, retrySeconds);
            return new ModelAndView("redirect:" + redirect);
        } catch (BadAuthenticationException e) {
            ServletUtil.deleteAllCookies(request, response);

            redirect = getBadAuthenticationRedirectUrl(request, redirectedFrom, retrySeconds);
            return new ModelAndView("redirect:" + redirect);
        } catch (NotAuthorizedException e) {
            CTILogger.info("Unable to log user in. Reason: " + e.getMessage());

            redirect = LoginController.LOGIN_URL + "?" + LoginController.INVALID_URL_ACCESS_PARAM;
            return new ModelAndView("redirect:" + redirect);
        } catch (PasswordExpiredException e) {
            CTILogger.debug("The password for " + username + " is expired.", e);

            String passwordResetUrl = passwordResetService.getPasswordResetUrl(username, request);
            return new ModelAndView("redirect:" + passwordResetUrl);
        }

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        ActivityLogger.logEvent(user.getUserID(), LoginService.LOGIN_WEB_ACTIVITY_ACTION, "User " + user.getUsername()
            + " (userid=" + user.getUserID() + ") has logged in from " + request.getRemoteAddr());

        if (createRememberMeCookie) {
            loginCookieHelper.setRememberMeCookie(request, response, username, password);
        }

        if (redirectedFrom != null && !redirectedFrom.equals("")) {
            redirect = redirectedFrom;
        } else {
            redirect = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user);
            if ("/operator/Operations.jsp".equals(redirect)) {
                redirect = "/dashboard";
            }
        }

        boolean hasUrlAccess = urlAccessChecker.hasUrlAccess(redirect, user);
        if (!hasUrlAccess) {
            loginService.invalidateSession(request, "No access to URL (" + redirect + ")");
            redirect = LoginController.LOGIN_URL + "?" + LoginController.INVALID_URL_ACCESS_PARAM;
        }

        return new ModelAndView("redirect:" + redirect);
    }

    /**
     * Get the redirect URL when the authentication fails
     */
    private String getBadAuthenticationRedirectUrl(HttpServletRequest request, String redirectedFrom, long retrySeconds) {
        String referer = request.getHeader("Referer");
        String redirect = (referer != null) ? referer : LoginController.LOGIN_URL;
        redirect = appendParams(redirect, retrySeconds, redirectedFrom);

        return redirect;
    }

    /**
     * Append parameters to the redirect URL provided.
     */
    private String appendParams(String redirect, long retrySeconds, String redirectedFrom) {
        String result = redirect;
        // user sees either invalid login OR retry after xx seconds message
        if (retrySeconds > 0) {
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_RETRY_SECONDS_PARAM,
                    Long.toString(retrySeconds));
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_FAILED_PARAM, null);
        } else {
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_FAILED_PARAM, "true");
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_RETRY_SECONDS_PARAM, null);
        }
        if (!StringUtils.isBlank(redirectedFrom)) {
            result = ServletUtil.tweakRequestURL(result, LoginController.REDIRECTED_FROM, redirectedFrom);
        } else {
            result = ServletUtil.tweakRequestURL(result, LoginController.REDIRECTED_FROM, null);
        }
        return result;
    }

    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.logout(request, response);
        return null;
    }

    public ModelAndView clientLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.clientLogin(request, response);
        return null;
    }

    public ModelAndView outboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.outboundVoiceLogin(request, response);
        return null;
    }
}
