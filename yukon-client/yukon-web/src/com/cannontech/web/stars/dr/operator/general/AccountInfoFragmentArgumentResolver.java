package com.cannontech.web.stars.dr.operator.general;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;

public class AccountInfoFragmentArgumentResolver {

    @Autowired private OperatorAccountService operatorAccountService;

    protected boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(AccountInfoFragment.class);
    }

    protected Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        int accountId = ServletRequestUtils.getRequiredIntParameter(nativeRequest, "accountId");
        AccountInfoFragment accountInfoFragment = operatorAccountService.getAccountInfoFragment(accountId);
        return accountInfoFragment;
    }
}
