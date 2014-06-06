package com.cannontech.web.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;

public class SortingParametersResolver implements HandlerMethodArgumentResolver {
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(SortingParameters.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, 
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, 
                                  WebDataBinderFactory binderFactory) throws Exception {
        
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();

        String sort = ServletRequestUtils.getRequiredStringParameter(nativeRequest, "sort");
        Direction dir = Direction.valueOf(ServletRequestUtils.getRequiredStringParameter(nativeRequest, "dir"));

        return new SortingParameters(sort, dir);
    }
}
