package com.cannontech.yukon.api.common.endpoint;

import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoSelectionUtil;
import com.cannontech.common.token.DeviceTokenStatus;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenStatus;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.token.service.TokenService.TokenHandler;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Endpoint
public class TokenStatusRequestEndpoint {
    private final static Namespace ns = YukonXml.getYukonNamespace();

    private ImmutableMap<TokenType, TokenHandler> tokenHandlers;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="tokenStatusRequest")
    public Element invoke(Element requestElem, YukonUserContext userContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(requestElem, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element responseElem = new Element("tokenStatusResponse", ns);
        XmlVersionUtils.addVersionAttribute(responseElem, XmlVersionUtils.YUKON_MSG_VERSION_1_0);

        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(requestElem);
        String tokenStr = requestTemplate.evaluateAsString("/y:tokenStatusRequest/y:token/@value");

        Token token = new Token(tokenStr);
        TokenHandler tokenHandler = tokenHandlers.get(token.getType());
        TokenStatus status = tokenHandler.getStatus(token);
        if (status == null) {
            // Unknown token.
            Element errorElem = new Element("failure", ns);
            errorElem.addContent("Token " + tokenStr + " is invalid or has expired.");
            responseElem.addContent(errorElem);
        } else if (status.isFinished()) {
            Element mainElem = new Element("complete", ns);
            if (token.getType() == TokenType.PROFILE_COLLECTION) {
                DeviceTokenStatus deviceTokenStatus = (DeviceTokenStatus) status;
                mainElem.setAttribute("numSuccesses", Integer.toString(deviceTokenStatus.getSuccesses().size()));
        
                Map<PaoIdentifier, SpecificDeviceErrorDescription> errors = deviceTokenStatus.getErrors();
                mainElem.setAttribute("numErrors", Integer.toString(errors.size()));
                List<Element> errorElems = PaoSelectionUtil.makePaoErrorElements(errors);
                mainElem.addContent(errorElems);
        
                List<PaoIdentifier> canceledItems = deviceTokenStatus.getCanceledItems();
                mainElem.setAttribute("numCanceled", Integer.toString(canceledItems.size()));
                if (!canceledItems.isEmpty()) {
                    Element canceledElem = new Element("canceled", ns);
                    for (PaoIdentifier paoIdentifier : canceledItems) {
                        canceledElem.addContent(PaoSelectionUtil.makePaoElement(paoIdentifier));
                    }
                    mainElem.addContent(canceledElem);
                }
            }
            responseElem.addContent(mainElem);
        } else {
            responseElem.addContent(new Element("started", ns));
        }

        return responseElem;
    }

    @Autowired
    public void setTokenHandlers(List<TokenHandler> tokenHandlers) {
        this.tokenHandlers = Maps.uniqueIndex(tokenHandlers, new Function<TokenHandler, TokenType>() {
            @Override
            public TokenType apply(TokenHandler tokenHandler) {
                return tokenHandler.getHandledType();
            }
        });
    }
}
