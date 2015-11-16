package com.cannontech.web.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.CtiUtilities;

public class PagingParametersHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(PagingParameters.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, 
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, 
                                  WebDataBinderFactory binderFactory) throws Exception {
        
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        
        int itemsPerPage;
        if (methodParameter.hasParameterAnnotation(DefaultItemsPerPage.class)) {
            int defaultItemsPerPage = methodParameter.getParameterAnnotation(DefaultItemsPerPage.class).value();
            itemsPerPage = ServletRequestUtils.getIntParameter(nativeRequest, "itemsPerPage", defaultItemsPerPage);
        } else {
            itemsPerPage = CtiUtilities.itemsPerPage(ServletRequestUtils.getIntParameter(nativeRequest, "itemsPerPage"));
        }
        int page = ServletRequestUtils.getIntParameter(nativeRequest, "page", 1);

        return PagingParameters.of(itemsPerPage, page);
    }
}
