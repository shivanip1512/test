package com.cannontech.web.spring;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.common.model.DefaultSort;
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

        String sort = ServletRequestUtils.getStringParameter(nativeRequest, "sort");
        if (StringUtils.isNotBlank(sort)) {
            Direction dir = Direction.valueOf(ServletRequestUtils.getStringParameter(nativeRequest, "dir", "desc"));
            return SortingParameters.of(sort, dir);
        } else if (methodParameter.hasParameterAnnotation(DefaultSort.class)) {
            sort = methodParameter.getParameterAnnotation(DefaultSort.class).sort();
            Direction dir = methodParameter.getParameterAnnotation(DefaultSort.class).dir();
            return SortingParameters.of(sort, dir);
        } else {
            return null;
        }
    }
}
