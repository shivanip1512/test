package com.cannontech.web.api.token;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.exception.BadRequestException;
import com.cannontech.common.exception.PasswordChangeException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.util.YukonUserContextResolver;

@RestController
public class PasswordApiController {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private CaptchaService captchaService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private YukonUserDao userDao;
    @Autowired private AuthenticationService authService;
    @Autowired private YukonUserContextResolver contextResolver;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private static final String baseKey = "yukon.web.modules.login.";

    @PostMapping("/forgottenPassword")
    public ResponseEntity<Object> forgottenPassword(HttpServletRequest request,
            @RequestBody ForgotPasswordRequest forgottenPassword) {

        if (forgottenPassword.getForgottenPasswordField() != null) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);

            ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();

            // Process Captcha
            if (forgottenPassword.getgRecaptchaResponse() != null) {
                Captcha captcha = new Captcha(forgottenPassword.getgRecaptchaResponse());
                CaptchaResponse captchaResponse = captchaService.checkCaptcha(captcha);

                // The Captcha failed. return the user the forgotten password page
                if (captchaResponse.isError()) {
                    String captchaResponseMessage = captchaResponse.getError().getFormatKey();
                    throw new PasswordChangeException(messageSourceAccessor.getMessage(captchaResponseMessage));
                }
            }

            // Getting the need password reset information.
            PasswordResetInfo passwordResetInfo = passwordResetService
                    .getPasswordResetInfo(forgottenPassword.getForgottenPasswordField());
            // Validate the request.
            if (!passwordResetInfo.isPasswordResetInfoValid()) {
                String invalidPasswordInfoMessage = baseKey + "forgottenPassword.invalidProvidedInformation";
                throw new PasswordChangeException(messageSourceAccessor.getMessage(invalidPasswordInfoMessage));
            }

            // Are we allowed to set this password?
            UserAuthenticationInfo userAuthenticationInfo = userDao
                    .getUserAuthenticationInfo(passwordResetInfo.getUser().getUserID());
            if (!authService.supportsPasswordSet(userAuthenticationInfo.getAuthenticationCategory())) {
                String passwordChangeNotSupported = baseKey + "passwordChange.passwordChangeNotSupported";
                throw new NotFoundException(messageSourceAccessor.getMessage(passwordChangeNotSupported));
            }

            String passwordResetUrl = passwordResetService.getPasswordResetUrl(passwordResetInfo.getUser().getUsername(), request,
                    true);
            YukonUserContext passwordResetUserContext = contextResolver.resolveContext(passwordResetInfo.getUser(), request);

            // Send out the forgotten password email
            try {
                passwordResetService.sendPasswordResetEmail(passwordResetUrl, passwordResetInfo.getContact(),
                        passwordResetUserContext);
            } catch (NotFoundException e) {
                String emailNotFound = baseKey + "forgottenPassword.emailNotFound";
                throw new NotFoundException(messageSourceAccessor.getMessage(emailNotFound));
            } catch (EmailException e) {
                String emailConnectionIssues = baseKey + "forgottenPassword.emailConnectionIssues";
                throw new EmailException(messageSourceAccessor.getMessage(emailConnectionIssues));
            }
            String emailSentMessage = baseKey + "forgottenPassword.forgottenPasswordEmailSent";
            forgotPasswordResponse.setStatus("Success");
            forgotPasswordResponse.setMessage(messageSourceAccessor.getMessage(emailSentMessage));
            return new ResponseEntity<>(forgotPasswordResponse, HttpStatus.OK);
        } else {
            throw new PasswordChangeException("Email, Username, or Account Number are not provided.");
        }
    }
}
