package com.cannontech.web.api.passwordPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.exception.PasswordException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.service.PasswordResetService;

@RestController
public class PasswordPolicyApiController {

    @Autowired private PasswordResetService passwordResetService;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AuthenticationService authService;

    @GetMapping("/change-password/{uuid}")
    public ResponseEntity<Object> getPasswordPolicies(@PathVariable("uuid") String uuid) {

        final PasswordPolicyResponse passwordPolicyResponse = new PasswordPolicyResponse();
        final LiteYukonUser yukonUser = passwordResetService.findUserFromPasswordKey(uuid);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);

        // if uuid is invalid
        if (yukonUser == null) {
            final String invalidUUIDMsg = "yukon.web.modules.passwordPolicyError.INVALID_UUID";
            throw new PasswordException(messageSourceAccessor.getMessage(invalidUUIDMsg));
        }

        // if password is expired
        if (authService.isPasswordExpired(yukonUser)) {
            final String passwordExpiredMsg = "yukon.web.login.passwordExpired";
            throw new PasswordException(messageSourceAccessor.getMessage(passwordExpiredMsg));
        }

        final PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(yukonUser);

        passwordPolicyResponse.setPasswordPolicy(passwordPolicy);
        passwordPolicyResponse.setUserId(yukonUser.getUserID());
        passwordPolicyResponse.setUuid(uuid);

        return new ResponseEntity<>(passwordPolicyResponse, HttpStatus.OK);
    }

}
