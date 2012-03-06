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

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.pao.service.PaoSelectionService.PaoSelectionData;
import com.cannontech.common.pao.service.PaoSelectionUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Sets;

@Endpoint
public class DemandResetRequestEndpoint {
    private static class MyDemandResetCallback implements DemandResetCallback {
        List<Element> errorElems;

        @Override
        public void completed(Results results) {
            Map<SimpleDevice, SpecificDeviceErrorDescription> errors = results.getErrors();
            errorElems = PaoSelectionUtil.makePaoErrorElements(errors);
        }
    }

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

            Element resetsRequestedElem = new Element("resetsRequested", ns);

            PaoSelectionData paoData =
                    paoSelectionService.selectPaoIdentifiersByType(paoCollectionNode);
            int numLookupFailures = paoData.getNumLookupFailures();

            Set<PaoIdentifier> devices = paoData.getPaoDataById().keySet();
            final Set<PaoIdentifier> validDevices =
                    Sets.newHashSet(demandResetService.validDevices(devices));
            Set<PaoIdentifier> invalidDevices = Sets.difference(devices, validDevices);

            MyDemandResetCallback callback = new MyDemandResetCallback();
            demandResetService.sendDemandReset(validDevices, callback, user);

            int numErrors = callback.errorElems.size();
            resetsRequestedElem.setAttribute("initiated",
                                             Integer.toString(validDevices.size() - numErrors));
            resetsRequestedElem.setAttribute("numLookupErrors", Integer.toString(numLookupFailures));
            resetsRequestedElem.setAttribute("numUnsupportedDevices", Integer.toString(invalidDevices.size()));
            resetsRequestedElem.setAttribute("numErrors", Integer.toString(numErrors));
            response.addContent(resetsRequestedElem);
            paoSelectionService.addLookupErrorsNode(paoData, response);
            if (invalidDevices.size() > 0) {
                Element unsupportedDevicesElem = new Element("unsupportedDevices", ns);
                for (PaoIdentifier invalidDevice : invalidDevices) {
                    Element paoElem = PaoSelectionUtil.makePaoElement(invalidDevice);
                    unsupportedDevicesElem.addContent(paoElem);
                }
                response.addContent(unsupportedDevicesElem);
            }
            response.addContent(callback.errorElems);

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
}
