package com.cannontech.amr.errors.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class DeviceErrorTranslatorDaoImpl implements DeviceErrorTranslatorDao {
	
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private Logger log = YukonLogManager.getLogger(DeviceErrorTranslatorDaoImpl.class);
    private InputStream errorDefinitions;
    private Map<Locale, Map<Integer, DeviceErrorDescription>> store = new HashMap<Locale, Map<Integer, DeviceErrorDescription>>();
    private DeviceErrorDescription defaultTranslation;
    private String baseKey = "yukon.web.errorCodes_";

    /**
     * Returns the US translation for the error code.
     */
    @Override
    public DeviceErrorDescription translateErrorCode(int error) {
		DeviceErrorDescription dded = translateErrorCode(error,
				new SimpleYukonUserContext(null, Locale.US, TimeZone.getDefault(), ThemeUtils.getDefaultThemeName()));
        if (dded != null) {
            return dded;
        }

        // Clone the defaultTranslation and set the error code
        DeviceErrorDescription ded = defaultTranslation;
        return new DeviceErrorDescription(error, ded.getCategory(), ded.getPorter(), 
                                          ded.getDescription(), ded.getTroubleshooting());
    }

    @Override
    public DeviceErrorDescription translateErrorCode(int error, YukonUserContext userContext) {
    	
    	//check the cache
    	Map<Integer, DeviceErrorDescription> localeStore = store.get(userContext.getLocale());
    	if(localeStore != null){
	    	if (localeStore.get(error) != null) {
	    		return localeStore.get(error);
	    	}
    	}else{
	    	//try resolving the error for the user's locale
	    	try{
	    		//the first time we encounter a new Locale, try to cache the errors
	    		cacheErrorsForLocale(userContext);
	    		localeStore = store.get(userContext.getLocale());
	    		return localeStore.get(error);
	    	}catch(Exception exception){
	    		log.info("Unable to load errors for ("+ userContext.getLocale().getDisplayCountry() +"): " + error);
	    	}
    	}

    	// Clone the defaultTranslation and set the error code
    	return defaultTranslation;
    }

    /**
     * Loads the error-code.xml file with English values.  This fallback is required for EIM Server, Service Manager, etc
     * where there is no access to the i18n files.
     */
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
            List<?> troubleNodes = Collections.emptyList();
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
        store.put(Locale.US, mapBuilder.build());
        log.info("Device error code descriptions loaded: " + store.size());
    }

    public void setErrorDefinitions(InputStream errorDefinitions) {
        this.errorDefinitions = errorDefinitions;
    }

    @Override
    public Iterable<DeviceErrorDescription> getAllErrors() {
        return getAllErrors(Locale.US);
    }

    public Iterable<DeviceErrorDescription> getAllErrors(Locale locale) {
    	return store.get(locale).values();
    }
    
    private void cacheErrorsForLocale(YukonUserContext userContext) {
    	//loop over us entries
    	Builder<Integer, DeviceErrorDescription> mapBuilder = ImmutableMap.builder();
    	for(Integer errorCode : store.get(Locale.US).keySet()){

    		try{
	    		//resolve each message
	    		MessageSourceResolvable categoryResolvable = new YukonMessageSourceResolvable(baseKey + errorCode + ".category");
	    		MessageSourceResolvable porterResolvable = new YukonMessageSourceResolvable(baseKey + errorCode + ".porter");
	    		MessageSourceResolvable descriptionResolvable = new YukonMessageSourceResolvable(baseKey + errorCode + ".description");
	    		MessageSourceResolvable troubleshootingResolvable = new YukonMessageSourceResolvable(baseKey + errorCode + ".troubleshooting");
	    		
	    		DeviceErrorDescription dded = new DeviceErrorDescription(errorCode, 
	    				messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(categoryResolvable), 
	    				messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(porterResolvable), 
	    				messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(descriptionResolvable), 
	    				messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(troubleshootingResolvable));
	    		
	    		mapBuilder.put(errorCode, dded);
    		}catch(NoSuchMessageException e){

    			//if not found, clone the US message as a fallback
    			log.info("Device error code descriptions for error code: " 
	    			+ errorCode + " and Locale: " 
	    			+ userContext.getLocale().getDisplayCountry());
    			
    			DeviceErrorDescription fallback = store.get(Locale.US).get(errorCode);
    			DeviceErrorDescription dded = new DeviceErrorDescription(errorCode, 
    					fallback.getCategory(), 
    					fallback.getPorter(), 
    					fallback.getDescription(), 
    					fallback.getTroubleshooting());
    			
    			mapBuilder.put(errorCode, dded);
    		}
    	}
    	
        store.put((Locale)userContext.getLocale().clone(), mapBuilder.build());
        log.info("Device error code descriptions loaded ("+ userContext.getLocale().getDisplayCountry() +"): " + store.get(userContext.getLocale()).size());
    }
}
