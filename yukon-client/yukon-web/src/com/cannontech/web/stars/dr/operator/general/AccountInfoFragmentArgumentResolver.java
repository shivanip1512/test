package com.cannontech.web.stars.dr.operator.general;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;

public class AccountInfoFragmentArgumentResolver implements WebArgumentResolver {

	private OperatorAccountService operatorAccountService;
	
	@Override
	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {

		Class<?> parameterType = methodParameter.getParameterType();
        if (parameterType.isAssignableFrom(AccountInfoFragment.class)) {
            
        	HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
            
        	int accountId = ServletRequestUtils.getRequiredIntParameter(nativeRequest, "accountId");
        	
        	AccountInfoFragment accountInfoFragment = operatorAccountService.getAccountInfoFragment(accountId);
        	
            return accountInfoFragment;
        }
        
        return UNRESOLVED;
	}
	
	@Autowired
	public void setOperatorAccountService(OperatorAccountService operatorAccountService) {
		this.operatorAccountService = operatorAccountService;
	}
}
