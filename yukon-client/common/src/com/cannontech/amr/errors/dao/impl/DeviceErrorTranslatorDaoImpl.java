package com.cannontech.amr.errors.dao.impl;

import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

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
    private Map<Locale, Map<Integer, DeviceErrorDescription>> store;
    private DeviceErrorDescription defaultTranslation;
    private String baseKey = "yukon.web.errorCodes_";

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
    	Map<Integer, DeviceErrorDescription> ddedStore = store.get(userContext.getLocale());
    	if(ddedStore != null){		
	    	if (ddedStore.get(error) != null) {
	    		return ddedStore.get(error);
	    	}
    	}else{
	    	//try resolving the error for the user's locale
	    	try{
	    		cacheErrorsForLocale(userContext);
	    		ddedStore = store.get(userContext.getLocale());
	    		return ddedStore.get(error);
	    	}catch(Exception exception){
	    		log.info("Unable to load errors for ("+ userContext.getLocale().getDisplayCountry() +"): " + error);
	    	}
    	}

    	// Clone the defaultTranslation and set the error code
    	DeviceErrorDescription ded = defaultTranslation;
    	MessageSourceResolvable categoryResolvable = new YukonMessageSourceResolvable(ded.getCategory());
    	MessageSourceResolvable porterResolvable = new YukonMessageSourceResolvable(ded.getPorter());
    	MessageSourceResolvable descriptionResolvable = new YukonMessageSourceResolvable(ded.getDescription());
    	MessageSourceResolvable troubleshootingResolvable = new YukonMessageSourceResolvable(ded.getTroubleshooting());
    	
    	return new DeviceErrorDescription(error, 
    			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(categoryResolvable), 
    			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(porterResolvable), 
    			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(descriptionResolvable), 
    			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(troubleshootingResolvable));
    }

    public void initialize() throws JDOMException, IOException {
    	YukonUserContext userContext = new SimpleYukonUserContext(null, Locale.US , TimeZone.getDefault(), ThemeUtils.getDefaultThemeName());
    	cacheErrorsForLocale(userContext);
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
    
    @SuppressWarnings("unchecked")
    private void cacheErrorsForLocale(YukonUserContext userContext) throws JDOMException, IOException{
    	Builder<Integer, DeviceErrorDescription> mapBuilder = ImmutableMap.builder();
        Format compactFormat = Format.getCompactFormat();
        compactFormat.setOmitDeclaration(true);
        compactFormat.setOmitEncoding(true);
        compactFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
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
            
            MessageSourceResolvable categoryResolvable = new YukonMessageSourceResolvable(baseKey + errorCodeStr + ".category");
        	MessageSourceResolvable porterResolvable = new YukonMessageSourceResolvable(baseKey + errorCodeStr + ".porter");
        	MessageSourceResolvable descriptionResolvable = new YukonMessageSourceResolvable(baseKey + errorCodeStr + ".description");
        	MessageSourceResolvable troubleshootingResolvable = new YukonMessageSourceResolvable(baseKey + errorCodeStr + ".troubleshooting");
            
        	DeviceErrorDescription dded = new DeviceErrorDescription(errorCode, 
        			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(categoryResolvable), 
        			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(porterResolvable), 
        			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(descriptionResolvable), 
        			messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(troubleshootingResolvable));
        	
            if (errorCode == null) {
                defaultTranslation = dded;
            } else {
                mapBuilder.put(errorCode, dded);
            }
        }
        Validate.notNull(defaultTranslation, "No default translation found");
        
        Builder <Locale, Map<Integer, DeviceErrorDescription>> storeBuilder = ImmutableMap.builder();
        storeBuilder.put(userContext.getLocale(), mapBuilder.build());
        
        store = storeBuilder.build();
        
        log.info("Device error code descriptions loaded ("+ userContext.getLocale().getDisplayCountry() +"): " + store.get(userContext.getLocale()).size());
    }
}
