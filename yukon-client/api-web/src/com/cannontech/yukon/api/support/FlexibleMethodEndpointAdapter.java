package com.cannontech.yukon.api.support;

import java.lang.reflect.Method;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.jdom.Element;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.adapter.AbstractMethodEndpointAdapter;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.util.YukonXml;
import com.google.common.collect.ImmutableSet;

public class FlexibleMethodEndpointAdapter extends AbstractMethodEndpointAdapter {
	private YukonUserDao yukonUserDao;
	
	private static final Class<?> validParameterTypesArray[] = {Element.class, LiteYukonUser.class, YukonUserContext.class};
	private Set<Class<?>> validParameterTypes = ImmutableSet.of(validParameterTypesArray);

    protected boolean supportsInternal(MethodEndpoint methodEndpoint) {
        Method method = methodEndpoint.getMethod();
        
        boolean goodReturnType = Void.TYPE.isAssignableFrom(method.getReturnType()) ||
                Element.class.isAssignableFrom(method.getReturnType());
        
        Class<?>[] parameterTypes = method.getParameterTypes();
        Set<Class<?>> parameterTypesSet = ImmutableSet.of(parameterTypes);
        
        boolean goodParameters = validParameterTypes.containsAll(parameterTypesSet);
        
		return goodReturnType && goodParameters;
    }

    protected void invokeInternal(MessageContext messageContext, MethodEndpoint methodEndpoint) throws Exception {
    	Class<?>[] parameterTypes = methodEndpoint.getMethod().getParameterTypes();
    	Object[] arguments = new Object[parameterTypes.length];
    	for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameter = parameterTypes[i];
			Object thisArgument = null;
			if (Element.class.equals(parameter)) {
				Source requestSource = messageContext.getRequest().getPayloadSource();
				if (requestSource != null) {
					JDOMResult jdomResult = new JDOMResult();
					transform(requestSource, jdomResult);
					thisArgument = jdomResult.getDocument().getRootElement();
				}
			} else if (LiteYukonUser.class.equals(parameter)) {
				
				QName userQName = new QName(YukonXml.getYukonNamespace().getURI(), "yukonUser");
				String userName = SoapHeaderElementUtil.findElementValue(messageContext.getRequest(), userQName);
				
				if (userName != null) {
					LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userName);
					if (yukonUser == null) {
						throw new NotFoundException("User " + userName + " is not known");
					} else {
						thisArgument = yukonUser;
					}
				} else {
					throw new NotAuthorizedException("Service requires user name header");
				}
			} else if (YukonUserContext.class.equals(parameter)) {
				thisArgument = new SystemUserContext();
			}
			
			if (thisArgument == null) {
				throw new RuntimeException("Unable to resolve method argument " + parameter + " on " + methodEndpoint);
			}
			
			// all is good, assign it to argument array
			arguments[i] = thisArgument;
		}
    	
        Object result;
		try {
			result = methodEndpoint.invoke(arguments);
			if (result != null) {
				
				// response
				Element responseElement = (Element) result;
				WebServiceMessage responseMessage = messageContext.getResponse();
				
				QName extraHeaderName = new QName(YukonXml.getYukonNamespace().getURI(), "extra");
				SoapHeaderElementUtil.copySoapHeaderFromRequestToResponse(messageContext, extraHeaderName);
				
				QName userHeaderName = new QName(YukonXml.getYukonNamespace().getURI(), "yukonUser");
				SoapHeaderElementUtil.copySoapHeaderFromRequestToResponse(messageContext, userHeaderName);
				
				transform(new JDOMSource(responseElement), responseMessage.getPayloadResult());
			}
		} catch (Exception e) {
			CTILogger.error("unable to envoke endpoint: " + methodEndpoint, e);
			throw e;
		}
        
    }
    
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
		this.yukonUserDao = yukonUserDao;
	}
    
}