package com.cannontech.web.stars.dr.operator.energyCompany;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.web.stars.dr.operator.service.EnergyCompanyInfoService;


public class EnergyCompanyInfoFragmentArgumentResolver implements WebArgumentResolver {

    private EnergyCompanyInfoService energyCompanyInfoService;
    
    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {

        Class<?> parameterType = methodParameter.getParameterType();
        if (parameterType.isAssignableFrom(EnergyCompanyInfoFragment.class)) {
            
            HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
            
            int ecId = ServletRequestUtils.getRequiredIntParameter(nativeRequest, "ecId");
            
            EnergyCompanyInfoFragment energyCompanyInfoFragment = energyCompanyInfoService.getEnergyCompanyInfoFragment(ecId);
            
            return energyCompanyInfoFragment;
        }
        
        return UNRESOLVED;
    }
    
    @Autowired
    public void setOperatorAccountService(EnergyCompanyInfoService energyCompanyInfoService) {
        this.energyCompanyInfoService = energyCompanyInfoService;
    }
}