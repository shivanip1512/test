package com.cannontech.amr.errors.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;

public class DeviceErrorTranslatorDaoImpl implements DeviceErrorTranslatorDao {
    private Logger log = YukonLogManager.getLogger(DeviceErrorTranslatorDaoImpl.class);
    private InputStream errorDefinitions;
    private Map<Integer, DeviceErrorDescription> store = new HashMap<Integer, DeviceErrorDescription>();
    private DeviceErrorDescription defaultTranslation;

    public DeviceErrorDescription traslateErrorCode(int error) {
        DeviceErrorDescription dded = store.get(error);
        if (dded != null) {
            return dded;
        }
        return defaultTranslation;
    }

    @SuppressWarnings("unchecked")
    public void initialize() throws JDOMException, IOException {
        Format compactFormat = Format.getCompactFormat();
        compactFormat.setOmitDeclaration(true);
        compactFormat.setOmitEncoding(true);
        compactFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        XMLOutputter xmlOut = new XMLOutputter(compactFormat);
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(errorDefinitions);
        Element rootElement = document.getRootElement();
        List<Element> children = rootElement.getChildren("error");
        for (Element errorEl : children) {
            String errorCodeStr = errorEl.getAttributeValue("code");
            Integer errorCode = null;
            if (!"*".equals(errorCodeStr)) {
                errorCode = Integer.parseInt(errorCodeStr);
            }
            String description = errorEl.getChildTextTrim("description");
            Validate.notEmpty(description, "Description for error " + errorCodeStr + " must not be blank");
            Element troubleEl = errorEl.getChild("troubleshooting");
            List troubleNodes = Collections.emptyList();
            if (troubleEl != null) {
                troubleNodes = troubleEl.getContent();
            }
            String troubleHtml = xmlOut.outputString(troubleNodes).trim();
            DeviceErrorDescription dded = new DeviceErrorDescription();
            dded.setErrorCode(errorCode);
            dded.setDescription(description);
            dded.setTroubleshooting(troubleHtml);
            if (errorCode == null) {
                defaultTranslation = dded;
            } else {
                store.put(errorCode, dded);
            }
        }
        Validate.notNull(defaultTranslation, "No default translation found");
        log.info("Friend device error codes loaded: " + children.size());
    }

    public void setErrorDefinitions(InputStream errorDefinitions) {
        this.errorDefinitions = errorDefinitions;
    }

}
