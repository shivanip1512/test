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
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.amr.service.ProfileCollectionService;
import com.cannontech.yukon.api.common.service.Token;
import com.cannontech.yukon.api.common.service.TokenStatus;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class TokenStatusRequestEndpoint {
    private final static Namespace ns = YukonXml.getYukonNamespace();

    @Autowired private ProfileCollectionService profileCollectionService;

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="tokenStatusRequest")
    public Element invoke(Element requestElem, YukonUserContext userContext) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(requestElem, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element responseElem = new Element("tokenStatusResponse", ns);

        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(requestElem);
        String tokenStr = requestTemplate.evaluateAsString("/y:tokenStatusRequest/y:token/@value");

        Token token = new Token(tokenStr);
        TokenStatus status = profileCollectionService.getStatus(token);
        if (status == null) {
            // Unknown token.
            Element errorElem = new Element("failure", ns);
            errorElem.addContent("Token " + tokenStr + " is invalid or has expired.");
            responseElem.addContent(errorElem);
        } else if (status.isFinished()) {
            Element mainElem = new Element("complete", ns);
            mainElem.setAttribute("numSuccesses", Integer.toString(status.getSuccesses().size()));

            Map<PaoIdentifier, SpecificDeviceErrorDescription> errors = status.getErrors();
            mainElem.setAttribute("numErrors", Integer.toString(errors.size()));
            List<Element> errorElems = PaoSelectionUtil.makePaoErrorElements(errors);
            mainElem.addContent(errorElems);

            List<PaoIdentifier> canceledItems = status.getCanceledItems();
            mainElem.setAttribute("numCanceled", Integer.toString(canceledItems.size()));
            if (!canceledItems.isEmpty()) {
                Element canceledElem = new Element("canceled", ns);
                for (PaoIdentifier paoIdentifier : canceledItems) {
                    canceledElem.addContent(PaoSelectionUtil.makePaoElement(paoIdentifier));
                }
                mainElem.addContent(canceledElem);
            }

            responseElem.addContent(mainElem);
        } else {
            responseElem.addContent(new Element("started", ns));
        }

        return responseElem;
    }
}
