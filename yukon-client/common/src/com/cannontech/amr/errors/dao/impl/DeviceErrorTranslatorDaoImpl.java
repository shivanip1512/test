package com.cannontech.amr.errors.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class DeviceErrorTranslatorDaoImpl implements DeviceErrorTranslatorDao {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private Logger log = YukonLogManager.getLogger(DeviceErrorTranslatorDaoImpl.class);
    private InputStream errorDefinitions;
    private Map<String, Map<DeviceError, DeviceErrorDescription>> store = new HashMap<String, Map<DeviceError, DeviceErrorDescription>>();
    private DeviceErrorDescription defaultTranslation;
    private String defaultThemeKey = getThemeKey(Locale.US, ThemeUtils.getDefaultThemeName());

    /**
     * Returns the US translation for the error code.
     */
    @Override
    public DeviceErrorDescription translateErrorCode(int errorCode) {
        return translateErrorCode(DeviceError.getErrorByCode(errorCode));
    }
    
    /**
     * Returns the US translation for the error code.
     */
    @Override
    public DeviceErrorDescription translateErrorCode(DeviceError error) {
        return translateErrorCode(error, YukonUserContext.system);

    }

    @Override
    public DeviceErrorDescription translateErrorCode(int errorCode, YukonUserContext userContext) {
        return translateErrorCode(DeviceError.getErrorByCode(errorCode), userContext);
    }
    
    @Override
    public DeviceErrorDescription translateErrorCode(DeviceError error, YukonUserContext userContext) {
        
        //check the cache
        Map<DeviceError, DeviceErrorDescription> localeStore = store.get(getThemeKey(userContext.getLocale(), userContext.getThemeName()));
        if(localeStore == null){
            //try resolving the error for the user's locale
            try{
                //the first time we encounter a new Locale, try to cache the errors
                cacheErrorsForLocale(userContext);
                localeStore = store.get(getThemeKey(userContext.getLocale(), userContext.getThemeName()));
            }catch(Exception exception){
                log.info("Unable to load errors for ("+ userContext.getLocale().getDisplayCountry() +"): " + error);
            }
        }
        DeviceErrorDescription localError = localeStore.get(error);
        if(localError != null){
            return localError;
        }else{
         // Clone the defaultTranslation and set the error code
            return new DeviceErrorDescription(error, defaultTranslation.getPorter(),
                defaultTranslation.getDescription(), defaultTranslation.getTroubleshooting());
        }
    }

    /**
     * Loads the error-code.xml file with English values. This fallback is required for EIM Server, Service Manager, etc
     * where there is no access to the i18n files.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initialize() throws JDOMException, IOException {
        Builder<DeviceError, DeviceErrorDescription> mapBuilder = ImmutableMap.builder();
        Format compactFormat = Format.getCompactFormat();
        compactFormat.setOmitDeclaration(true);
        compactFormat.setOmitEncoding(true);
        compactFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        XMLOutputter xmlOut = new XMLOutputter(compactFormat);
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(errorDefinitions);
        Element rootElement = document.getRootElement();
        rootElement.getChildren("error").forEach(errorEl -> {
            String errorCodeStr = errorEl.getAttributeValue("code");
            DeviceError error = DeviceError.getErrorByCode(Integer.parseInt(errorCodeStr));
            String porter = errorEl.getChildTextTrim("porter");
            String description = errorEl.getChildTextTrim("description");
            Validate.notEmpty(description, "Description for error " + errorCodeStr + " must not be blank");
            Element troubleEl = errorEl.getChild("troubleshooting");
            List<? extends Content> troubleNodes = Collections.emptyList();
            if (troubleEl != null) {
                troubleNodes = troubleEl.getContent();
            }
            String troubleHtml = xmlOut.outputString(troubleNodes).trim();
            DeviceErrorDescription dded = new DeviceErrorDescription(error, porter, description, troubleHtml);
            if (error == DeviceError.UNKNOWN) {
                defaultTranslation = dded;
            } else {
                mapBuilder.put(error, dded);
            }
        });
        Validate.notNull(defaultTranslation, "No default translation found");
        store.put(defaultThemeKey, mapBuilder.build());
        log.info("Device error code descriptions loaded: " + store.get(defaultThemeKey).size());
    }

    public void setErrorDefinitions(InputStream errorDefinitions) {
        this.errorDefinitions = errorDefinitions;
    }

    @Override
    public Iterable<DeviceErrorDescription> getAllErrors() {
        return getAllErrors(new SimpleYukonUserContext(null, Locale.US, TimeZone.getDefault(), ThemeUtils.getDefaultThemeName()));
    }

    @Override
    public Iterable<DeviceErrorDescription> getAllErrors(YukonUserContext userContext) {
        Map<DeviceError, DeviceErrorDescription> localeStore = store.get(getThemeKey(userContext.getLocale(), userContext.getThemeName()));
        if(localeStore == null) {
            localeStore = cacheErrorsForLocale(userContext);
        }
        
        return localeStore.values();
    }
    
    private Map<DeviceError, DeviceErrorDescription> cacheErrorsForLocale(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        //loop over us entries
        Builder<DeviceError, DeviceErrorDescription> mapBuilder = ImmutableMap.builder();
        for(DeviceError error : store.get(defaultThemeKey).keySet()){
            try{                
                DeviceErrorDescription dded = new DeviceErrorDescription(error, accessor);
                mapBuilder.put(error, dded);
            }catch(NoSuchMessageException e){

                //if not found, clone the US message as a fallback
                log.info("Device error code descriptions for error code: " 
                    + error.getCode() + " and Locale: " 
                    + userContext.getLocale().getDisplayCountry());
                
                DeviceErrorDescription fallback = store.get(defaultThemeKey).get(error);
                DeviceErrorDescription dded = new DeviceErrorDescription(error, fallback.getPorter(), fallback.getDescription(), fallback.getTroubleshooting());
                
                mapBuilder.put(error, dded);
            }
        }
        
        Map<DeviceError, DeviceErrorDescription> localeStore = mapBuilder.build();
        final String themeKey = getThemeKey(userContext.getLocale(), userContext.getThemeName());
        store.put(themeKey, localeStore);
        log.info("Device error code descriptions loaded ("+ userContext.getLocale().getDisplayCountry() +"): " + store.get(themeKey).size());
        
        return localeStore;
    }
    
    private String getThemeKey(Locale locale, String themeName) {
        return String.format("%s|%s", locale.toString(), themeName);
    }
}
