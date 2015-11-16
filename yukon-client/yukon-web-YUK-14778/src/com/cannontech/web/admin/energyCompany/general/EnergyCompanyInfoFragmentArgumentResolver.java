package com.cannontech.web.admin.energyCompany.general;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoService;


public class EnergyCompanyInfoFragmentArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired private EnergyCompanyInfoService energyCompanyInfoService;

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(EnergyCompanyInfoFragment.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        int ecId = ServletRequestUtils.getRequiredIntParameter(nativeRequest, "ecId");
        EnergyCompanyInfoFragment energyCompanyInfoFragment = energyCompanyInfoService.getEnergyCompanyInfoFragment(ecId);
        return energyCompanyInfoFragment;
    }
}
