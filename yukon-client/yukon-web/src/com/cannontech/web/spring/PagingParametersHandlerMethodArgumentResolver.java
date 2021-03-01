package com.cannontech.web.spring;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.web.spring.parameters.exceptions.InvalidPagingParametersException;
import com.cannontech.yukon.api.amr.endpoint.ArchivedValuesRequestEndpoint;

public class PagingParametersHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final static Logger log = YukonLogManager.getLogger(ArchivedValuesRequestEndpoint.class);
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
     * Return default value of Items per page i.e 25 if null value passed.
     * @throws InvalidPagingParametersException for Invalid Items per page
     */
    private static Integer getValidItemsPerPage(String itemsPerPageString) {
        Integer itemsPerPage = null;
        try {
            itemsPerPage = Integer.valueOf(itemsPerPageString);
        } catch (NumberFormatException e) {
            log.warn("'{}' is not a valid Integer for ItemsPerPage. Setting it to default value: {}.", itemsPerPageString,
                    CtiUtilities.DEFAULT_ITEMS_PER_PAGE);
            itemsPerPage = CtiUtilities.DEFAULT_ITEMS_PER_PAGE;
        }

        // Items per page should be less than 0 and more than 1000
        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE || itemsPerPage < CtiUtilities.MIN_ITEMS_PER_PAGE) {
            throw new InvalidPagingParametersException("Items per page should be greater than 0 and less than 1000");
        }

        return itemsPerPage;
    }

    /**
     * Get valid page number
     * @throws InvalidPagingParametersException for Invalid page number
     */
    private static Integer getValidPageNumber(String pageString) {
        Integer pageNumber = null;
        try {
            pageNumber = Integer.valueOf(pageString);
        } catch (NumberFormatException e) {
            log.warn("'{}' is not a valid Integer for page. Setting it to default value: {}.", pageNumber, CtiUtilities.DEFAULT_PAGE_NUMBER);
            pageNumber = CtiUtilities.DEFAULT_PAGE_NUMBER;
        }

        // Page no should be always greater than 0
        if (pageNumber <= 0) {
            throw new InvalidPagingParametersException("Page should be greater than 0");
        }

        return pageNumber;
    }
}
