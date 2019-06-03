package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public final class SpringWebUtil {

    public static final LiteYukonUser getYukonUser() throws ServletRequestBindingException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        
        LiteYukonUser user = (LiteYukonUser) requestAttributes.getAttribute(ServletUtil.ATT_YUKON_USER, 
                                                            RequestAttributes.SCOPE_SESSION);

        if (user == null) {
            user = ApiRequestContext.getContext().getLiteYukonUser();
        }

        if (user == null) {
            throw new ServletRequestBindingException("Required user not found in request.");
        }
        
        return user;
        
    }
    
    public static BindingResult bind(ModelMap model, HttpServletRequest req,
            Object target, String objectName, String messageBase) {
        
        ServletRequestDataBinder binder = new ServletRequestDataBinder(target, objectName);
        
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(messageBase);
            binder.setMessageCodesResolver(msgCodesResolver);
        }
        
        binder.bind(req);
        BindingResult result = binder.getBindingResult();
        model.addAttribute(objectName, target);
        model.putAll(binder.getBindingResult().getModel());
        
        return result;
    }
    
}