package com.cannontech.yukon.api.support;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.adapter.AbstractMethodEndpointAdapter;
import org.w3c.dom.Node;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableSet;

public class FlexibleMethodEndpointAdapter extends AbstractMethodEndpointAdapter {
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private AuthDao authDao;
    @Autowired private CustomerAccountDao customerAccountDao;

    private static final Class<?> validParameterTypesArray[] = {Element.class, LiteYukonUser.class, YukonUserContext.class, CustomerAccount.class};
    private Set<Class<?>> validParameterTypes = ImmutableSet.copyOf(validParameterTypesArray);

    @Override
    protected boolean supportsInternal(MethodEndpoint methodEndpoint) {
        Method method = methodEndpoint.getMethod();

        boolean goodReturnType = Void.TYPE.isAssignableFrom(method.getReturnType()) ||
        Element.class.isAssignableFrom(method.getReturnType());

        Class<?>[] parameterTypes = method.getParameterTypes();
        Set<Class<?>> parameterTypesSet = ImmutableSet.copyOf(parameterTypes);

        boolean goodParameters = validParameterTypes.containsAll(parameterTypesSet);

        return goodReturnType && goodParameters;
    }

    @Override
    protected void invokeInternal(MessageContext messageContext, MethodEndpoint methodEndpoint) throws Exception {
        try {
            Class<?>[] parameterTypes = methodEndpoint.getMethod().getParameterTypes();
            Object[] arguments = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameter = parameterTypes[i];
                Object thisArgument = null;
                if (Element.class.equals(parameter)) {
                    Source requestSource = messageContext.getRequest().getPayloadSource();
                    if (requestSource != null) {
                        if ( requestSource instanceof DOMSource) {
                            Node node = ((DOMSource) requestSource).getNode();
                            DOMBuilder domBuilder = new DOMBuilder();
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                thisArgument = domBuilder.build((org.w3c.dom.Element) node);
                            }
                            else if (node.getNodeType() == Node.DOCUMENT_NODE) {
                                Document document = domBuilder.build((org.w3c.dom.Document) node);
                                thisArgument = document.getRootElement();
                            }
                        }
                        if (thisArgument == null) {
                            // direct conversion not possible, must transform
                            JDOMResult jdomResult = new JDOMResult();
                            transform(requestSource, jdomResult);
                            thisArgument = jdomResult.getDocument().getRootElement();
                        }
                    }
                } else if (LiteYukonUser.class.equals(parameter)) {

                    LiteYukonUser yukonUser = getYukonUser(messageContext);
                    thisArgument = yukonUser;
                } else if (YukonUserContext.class.equals(parameter)) {
                    LiteYukonUser yukonUser = getYukonUser(messageContext);

                    TimeZone userTimeZone = authDao.getUserTimeZone(yukonUser);
                    YukonUserContext yukonUserContext = new SimpleYukonUserContext(yukonUser, Locale.ENGLISH, userTimeZone, ThemeUtils.getDefaultThemeName());
                    thisArgument = yukonUserContext;
                } else if(CustomerAccount.class.equals(parameter)){
                    CustomerAccount customerAccount = getCustomerAccount(messageContext);
                    thisArgument = customerAccount;
                }

                if (thisArgument == null) {
                    throw new RuntimeException("Unable to resolve method argument " + parameter + " on " + methodEndpoint);
                }

                // all is good, assign it to argument array
                arguments[i] = thisArgument;
            }

            Object result = methodEndpoint.invoke(arguments);
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
            CTILogger.error("unable to invoke endpoint: " + methodEndpoint, e);
            throw e;
        }

    }

    private LiteYukonUser getYukonUser(MessageContext messageContext) {
        QName userQName = new QName(YukonXml.getYukonNamespace().getURI(), "yukonUser");
        String userName = SoapHeaderElementUtil.findElementValue(messageContext.getRequest(), userQName);

        if (userName == null) {
            throw new NotAuthorizedException("Service requires username header");
        }
        LiteYukonUser yukonUser = yukonUserDao.findUserByUsername(userName);
        if (yukonUser == null) {
            throw new NotFoundException("User " + userName + " is not known");
        }
        return yukonUser;
    }
    
    private CustomerAccount getCustomerAccount(MessageContext messageContext) {
        QName userQName = new QName(YukonXml.getYukonNamespace().getURI(), "yukonUser");
        String userName = SoapHeaderElementUtil.findElementValue(messageContext.getRequest(), userQName);

        if (userName == null) {
            throw new NotAuthorizedException("Service requires username header");
        }
        LiteYukonUser yukonUser = yukonUserDao.findUserByUsername(userName);
        if (yukonUser == null) {
            throw new NotFoundException("User " + userName + " is not known");
        }
        
        CustomerAccount customerAccount = customerAccountDao.getCustomerAccount(yukonUser);
        if(customerAccount == null) {
            throw new NotFoundException("No account associated with " + userName);
        }
        return customerAccount;
    }
}
