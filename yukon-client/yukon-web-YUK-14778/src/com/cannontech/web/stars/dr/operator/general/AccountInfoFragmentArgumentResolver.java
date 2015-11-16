package com.cannontech.web.stars.dr.operator.general;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;

public class AccountInfoFragmentArgumentResolver implements HandlerMethodArgumentResolver{
    @Autowired private OperatorAccountService operatorAccountService;

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(AccountInfoFragment.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        int accountId = ServletRequestUtils.getRequiredIntParameter(nativeRequest, "accountId");
        AccountInfoFragment accountInfoFragment = operatorAccountService.getAccountInfoFragment(accountId);
        return accountInfoFragment;
    }
}
