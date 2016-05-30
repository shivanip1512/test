package com.cannontech.common.pao.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.service.PaoSelectionService.PaoSelectorType;
import com.cannontech.common.util.xml.YukonXml;
import com.google.common.collect.Lists;

public final class PaoSelectionUtil {
    private final static Namespace ns = YukonXml.getYukonNamespace();

    private PaoSelectionUtil() {
        throw new RuntimeException("PaoSelectionUtil is not instantiable.");
    }

    public static Element makePaoElement(YukonPao pao) {
        Element paoElement = new Element(PaoSelectorType.PAO_ID.getElementName(), ns);
        paoElement.setAttribute("value", Integer.toString(pao.getPaoIdentifier().getPaoId()));
        return paoElement;
    }

    public static List<Element> makePaoErrorElements(Map<? extends YukonPao, SpecificDeviceErrorDescription> errors) {
        List<Element> errorElems = Lists.newArrayListWithCapacity(errors.size());
        for (YukonPao device : errors.keySet()) {
            SpecificDeviceErrorDescription error = errors.get(device);
            Element errorElem = new Element("error", ns);

            errorElem.setAttribute("category", error.getCategory());
            errorElem.setAttribute("code", Integer.toString(error.getErrorCode()));
            errorElems.add(errorElem);

            errorElem.addContent(PaoSelectionUtil.makePaoElement(device));

            Element descriptionElem = new Element("description", ns);
            descriptionElem.setText(error.getDescription());
            errorElem.addContent(descriptionElem);

            Element troubleshootingElem = new Element("troubleshooting", ns);
            troubleshootingElem.setText(error.getTroubleshooting());
            errorElem.addContent(troubleshootingElem);

            String resultString = error.getPorter();
            if (!StringUtils.isBlank(resultString)) {
                Element resultStringElem = new Element("resultString", ns);
                resultStringElem.setText(resultString);
                errorElem.addContent(resultStringElem);
            }
        }

        return errorElems;
    }
}
