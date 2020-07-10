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
import com.cannontech.web.spring.parameters.exceptions.InvalidPagingParametersException;

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

        String itemsPerPageString;
        if (methodParameter.hasParameterAnnotation(DefaultItemsPerPage.class)) {
            int defaultItemsPerPage = methodParameter.getParameterAnnotation(DefaultItemsPerPage.class).value();
            itemsPerPageString = ServletRequestUtils.getStringParameter(nativeRequest, "itemsPerPage",
                    String.valueOf(defaultItemsPerPage));
        } else {
            itemsPerPageString = ServletRequestUtils.getStringParameter(nativeRequest, "itemsPerPage");
        }
        String pageString = ServletRequestUtils.getStringParameter(nativeRequest, "page", String.valueOf(1));

        return PagingParameters.of(getValidItemsPerPage(itemsPerPageString), getValidPageNumber(pageString));
    }

    /**
     * Get valid Items per page
     * @throws InvalidPagingParametersException for Invalid Items per page
     */
    private Integer getValidItemsPerPage(String itemsPerPageString) {

        Integer itemsPerPage = null;
        try {
            itemsPerPage = Integer.valueOf(itemsPerPageString);
        } catch (NumberFormatException e) {
            throw new InvalidPagingParametersException(itemsPerPageString + " is not a valid Integer for Items per page");
        }

        // Items per page should be less than 0 and less than 1000
        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE || itemsPerPage < CtiUtilities.MIN_ITEMS_PER_PAGE) {
            throw new InvalidPagingParametersException("Items per page should be greater than 0 and less than 1000");
        }

        return itemsPerPage;
    }

    /**
     * Get valid page number
     * @throws InvalidPagingParametersException for Invalid page number
     */
    private Integer getValidPageNumber(String pageString) {

        Integer pageNumber = null;
        try {
            pageNumber = Integer.valueOf(pageString);
        } catch (NumberFormatException e) {
            throw new InvalidPagingParametersException(pageString + " is not a valid Integer for Page Number");
        }

        // Page no should be always greater than 0
        if (pageNumber <= 0) {
            throw new InvalidPagingParametersException("Page should be greater than 0");
        }

        return pageNumber;
    }
}
