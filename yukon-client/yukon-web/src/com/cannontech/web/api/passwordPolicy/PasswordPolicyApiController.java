package com.cannontech.web.api.passwordPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.service.PasswordResetService;

@RestController
public class PasswordPolicyApiController {

    @Autowired private PasswordResetService passwordResetService;
    @Autowired private PasswordPolicyService passwordPolicyService;

    @GetMapping("/change-password/{uuid}")
    public ResponseEntity<Object> getPasswordPolicies(@PathVariable("uuid") String uuid) throws PasswordExpiredException {

        final PasswordPolicyResponse passwordPolicyResponse = new PasswordPolicyResponse();
        final LiteYukonUser User = passwordResetService.findUserFromPasswordKey(uuid);
        if (User == null) {
            // to do if uuid is invalid
            throw new NotFoundException("Invalid UUID");
        }

        final PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(User);

        passwordPolicyResponse.setPasswordPolicy(passwordPolicy);
        passwordPolicyResponse.setUserId(User.getUserID());
        passwordPolicyResponse.setUuid(uuid);

        return new ResponseEntity<>(passwordPolicyResponse, HttpStatus.OK);
    }

}
