package com.cannontech.web.admin.energyCompany.general;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;


public class EnergyCompanyInfoFragmentWebArgumentResolver extends EnergyCompanyInfoFragmentArgumentResolver
        implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        if (supportsParameter(parameter)) {
            super.resolveArgument(parameter, webRequest);
        }
        return UNRESOLVED;
    }
}