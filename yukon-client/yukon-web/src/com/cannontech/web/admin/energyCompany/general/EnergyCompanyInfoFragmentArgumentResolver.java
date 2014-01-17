package com.cannontech.web.admin.energyCompany.general;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoService;


public class EnergyCompanyInfoFragmentArgumentResolver {

    @Autowired private EnergyCompanyInfoService energyCompanyInfoService;

    protected boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(EnergyCompanyInfoFragment.class);
    }

    protected Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        int ecId = ServletRequestUtils.getRequiredIntParameter(nativeRequest, "ecId");
        EnergyCompanyInfoFragment energyCompanyInfoFragment = energyCompanyInfoService.getEnergyCompanyInfoFragment(ecId);
        return energyCompanyInfoFragment;
    }
}