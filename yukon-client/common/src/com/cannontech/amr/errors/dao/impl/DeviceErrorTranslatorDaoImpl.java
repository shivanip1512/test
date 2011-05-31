package com.cannontech.amr.errors.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class DeviceErrorTranslatorDaoImpl implements DeviceErrorTranslatorDao {
    private Logger log = YukonLogManager.getLogger(DeviceErrorTranslatorDaoImpl.class);
    private InputStream errorDefinitions;
    private Map<Integer, DeviceErrorDescription> store;
    private DeviceErrorDescription defaultTranslation;

    public DeviceErrorDescription translateErrorCode(int error) {
        DeviceErrorDescription dded = store.get(error);
        if (dded != null) {
            return dded;
        }

        // Clone the defaultTranslation and set the error code
        DeviceErrorDescription ded = defaultTranslation;
        return new DeviceErrorDescription(error, ded.getCategory(), ded.getPorter(), 
                                          ded.getDescription(), ded.getTroubleshooting());
    }

    @SuppressWarnings("unchecked")
    public void initialize() throws JDOMException, IOException {
        Builder<Integer, DeviceErrorDescription> mapBuilder = ImmutableMap.builder();
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
            String category = errorEl.getChildTextTrim("category");
            Validate.notEmpty(category, "Category for error " + errorCodeStr + " must not be blank");

            String porter = errorEl.getChildTextTrim("porter");
           
            String description = errorEl.getChildTextTrim("description");
            Validate.notEmpty(description, "Description for error " + errorCodeStr + " must not be blank");
            Element troubleEl = errorEl.getChild("troubleshooting");
            List troubleNodes = Collections.emptyList();
            if (troubleEl != null) {
                troubleNodes = troubleEl.getContent();
            }
            String troubleHtml = xmlOut.outputString(troubleNodes).trim();
            DeviceErrorDescription dded = new DeviceErrorDescription(errorCode, category, porter, 
                                                                     description, troubleHtml);
            if (errorCode == null) {
                defaultTranslation = dded;
            } else {
                mapBuilder.put(errorCode, dded);
            }
        }
        Validate.notNull(defaultTranslation, "No default translation found");
        
        store = mapBuilder.build();
        
        log.info("Device error code descriptions loaded: " + store.size());
    }

    public void setErrorDefinitions(InputStream errorDefinitions) {
        this.errorDefinitions = errorDefinitions;
    }

    @Override
    public Iterable<DeviceErrorDescription> getAllErrors() {
        return store.values();
    }
}
