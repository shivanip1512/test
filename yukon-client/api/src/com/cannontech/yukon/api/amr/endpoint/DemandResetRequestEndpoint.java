package com.cannontech.yukon.api.amr.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.xml.validation.XmlValidationException;
import org.springframework.xml.xpath.XPathException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.pao.service.PaoSelectionService.PaoSelectionData;
import com.cannontech.common.pao.service.PaoSelectionService.PaoSelectorType;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Endpoint
public class DemandResetRequestEndpoint {
    private final static Logger log = YukonLogManager.getLogger(DemandResetRequestEndpoint.class);
    private final static Namespace ns = YukonXml.getYukonNamespace();

    @Autowired private PaoSelectionService paoSelectionService;
    @Autowired private DemandResetService demandResetService;

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "demandResetRequest")
    public Element invoke(Element demandResetRequest, LiteYukonUser user) {
        XmlVersionUtils.verifyYukonMessageVersion(demandResetRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("demandResetResponse", ns);

        try {
            SimpleXPathTemplate requestTemplate =
                    YukonXml.getXPathTemplateForElement(demandResetRequest);

            Node paoCollectionNode =
                    requestTemplate.evaluateAsNode("/y:demandResetRequest/y:paos");

            // TODO:  do some overall checks--e.g. is porter up?

            Element resetsRequestedElem = new Element("resetsRequested", ns);
            Element lookupFailureElem = new Element("lookupError", ns);
            boolean hasLookupFailures = false;
            final List<Element> errorElems = Lists.newArrayList();

            PaoSelectionData paoData =
                    paoSelectionService.selectPaoIdentifiersByType(paoCollectionNode);
            for (Map.Entry<PaoSelectorType, List<String>> entry : paoData.getLookupFailures().entrySet()) {
                PaoSelectorType selectorType = entry.getKey();
                List<String> lookupFailures = entry.getValue();
                if (!lookupFailures.isEmpty()) {
                    for (String lookupFailure : lookupFailures) {
                        Element paoElement = new Element(selectorType.getElementName(), ns);
                        paoElement.setAttribute("value", lookupFailure);
                        lookupFailureElem.addContent(paoElement);
                    }
                    hasLookupFailures = true;
                }
            }

            Set<PaoIdentifier> devices = paoData.getPaoDataById().keySet();
            final Set<PaoIdentifier> validDevices =
                    Sets.newHashSet(demandResetService.validDevices(devices));
            Set<PaoIdentifier> invalidDevices = Sets.difference(devices, validDevices);
            for (PaoIdentifier invalidDevice : invalidDevices) {
                errorElems.add(makeErrorElement(invalidDevice, ErrorCode.UNSUPPORTED_DEVICE));
            }

            demandResetService.sendDemandReset(validDevices, user);

            resetsRequestedElem.setAttribute("initiated", Integer.toString(validDevices.size()));
            response.addContent(resetsRequestedElem);
            if (hasLookupFailures) {
                response.addContent(lookupFailureElem);
            }
            response.addContent(errorElems);

        } catch (XmlValidationException xmle) {
            log.error("XML validation error", xmle);
            Element error = XMLFailureGenerator.generateFailure(response, xmle, "InvalidResponseType",
                                                                xmle.getMessage());
            response.addContent(error);
        } catch (XPathException xpe) {
            log.error("XML validation error", xpe);
            Element error = XMLFailureGenerator.generateFailure(response, xpe, "XmlParseError",
                                                                xpe.getMessage());
            response.addContent(error);
        } catch (DOMException dome) {
            log.error("XML validation error", dome);
            Element error = XMLFailureGenerator.generateFailure(response, dome, "XmlParseError",
                                                                dome.getMessage());
            response.addContent(error);
        } catch (Exception exception) {
            log.error("other error", exception);
            Element error = XMLFailureGenerator.generateFailure(response, exception, "OtherError",
                                                                exception.getMessage());
            response.addContent(error);
        }

        return response;
    }

    private Element makeErrorElement(YukonPao pao, ErrorCode errorCode) {
        Element errorElem = new Element("error", ns);
        errorElem.setAttribute("errorCode", errorCode.getXmlValue());
        errorElem.addContent(makePaoElement(pao));
        return errorElem;
    }

    private Element makePaoElement(YukonPao pao) {
        Element paoElement = new Element(PaoSelectorType.PAO_ID.getElementName(), ns);
        paoElement.setAttribute("value", Integer.toString(pao.getPaoIdentifier().getPaoId()));
        return paoElement;
    }
}
