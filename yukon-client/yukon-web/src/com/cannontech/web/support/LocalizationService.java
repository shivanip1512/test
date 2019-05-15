package com.cannontech.web.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.web.support.LocalizationBackingBean.Choice;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class LocalizationService implements ApplicationContextAware {
    private static final String themesFolderName = CtiUtilities.getYukonBase() + "/Server/Config/Themes/";
    private static final String workspaceDir = System.getProperty("com.cooperindustries.dev.wsdir");
    @Resource(name="i18nBasenames") private List<String> defaultI18nFiles;
    private ApplicationContext applicationContext;
    private Logger log = YukonLogManager.getLogger(LocalizationService.class);
    private Properties builtInProperties = null;
    private enum SelectorType { SEARCH_IN, MODIFIED, BASE }
    
    /**
     * Generates a list of LocalizationPairs where either <br>
     * -The modified localization has a key not present in the base <br>
     * -The base localization value for a key does not match the modified localizations value<br>
     * 
     * @param backingBean relevant properties: <br>
     *       baseChoice - What to use for base localization("theme", "custom", "default")<br>
     *       baseTheme - (if selected) name of base theme<br>
     *       baseCustom - (if selected) custom base file<br>
     *       modifiedChoice - What to use for modified localization("theme", "custom", "default")<br>
     *       modifiedTheme - (if selected) name of modified theme<br>
     *       modifiedCustom - (if selected) custom modified file<br>  
     * @return Localization pairs where: <br>
     *  value= modified localization's value <br>
     *  secondaryValue = base localization's value <br>
     * @throws FileNotFoundException when a theme/custom file is not found or cannot be opened
     * @throws InvalidPropertiesFormatException when a theme/custom file cannot be parsed as an xml properties file
     */
    public List<LocalizationPair> compare(LocalizationBackingBean backingBean) throws FileNotFoundException, InvalidPropertiesFormatException {
        Properties baseProperties = getProperties(backingBean, SelectorType.BASE);
        Properties modifiedProperties = getProperties(backingBean, SelectorType.MODIFIED);
        
        List<LocalizationPair> results = Lists.newArrayList();
        
        for (String key : modifiedProperties.stringPropertyNames()) {
            String modifiedValue = modifiedProperties.getProperty(key);
            String baseValue = (String) baseProperties.get(key);
            
            if (! modifiedValue.equals(baseValue)) {
                results.add(new LocalizationPair(key, modifiedValue, baseValue));
            }
        }
        Collections.sort(results);
        
        return results;
    }

    /**
     * Searches the localization specified by the backing bean for the query found in the backing bean
     * @param backingBean relevant properties: <br>
     *       query interpreted using '*' as a wildcard, which can be escaped with '\*' <br>
     *       searchBy - whether to search by key or value (anything other than "key" is considered value)<br>
     *       caseSensitive - whether the search should be case sensitive<br>
     *       searchInChoice - What to search in ("theme", "custom", "default")<br>
     *       searchInTheme - (if selected) name of theme to search<br>
     *       searchInCustom - (if selected) custom file to search<br>
     *       
     *             
     * @return
     * @throws FileNotFoundException when a theme/custom file is not found or cannot be opened
     * @throws InvalidPropertiesFormatException when a theme/custom file cannot be parsed as an xml properties file
     */
    public List<String> search(LocalizationBackingBean backingBean) throws FileNotFoundException, InvalidPropertiesFormatException {
        boolean searchByKey = "KEY".equals(backingBean.getSearchBy());

        Properties properties = getProperties(backingBean, SelectorType.SEARCH_IN);
        List<String> results = search(backingBean.getQuery(), properties, searchByKey, backingBean.isCaseSensitive());
        return results;
    }
    
    /**
     * @param query interpreted using '*' as a wildcard, which can be escaped with '\*' 
     * @param properties Properties to search in for query
     * @param searchByKey true= search by key, false=search by value
     * @param isCaseSensitive true=case sensitve search, false=case insensitive search
     * @return xml entries of key-value pairs found matching the query, sorted by key
     */
    private List<String> search(String query, Properties properties, boolean searchByKey, boolean isCaseSensitive) {
        if(! isCaseSensitive){
            query = query.toLowerCase();
        }
        
        final Pattern regex = simplifiedRegex(query);
        
        List<String> results = Lists.newArrayList();

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            
            String comparison = value;
            if(searchByKey){
                comparison = key;
            }
            if(! isCaseSensitive){
                comparison = comparison.toLowerCase();
            }
            
            if(regex.matcher(comparison).matches()){
                results.add( new LocalizationPair(key, value).getXml() );
            }
        }
        Collections.sort(results);
        return results;
    }


    /**
     * @param query interpreted using '*' as a wildcard, which can be escaped with '\*' 
     * @return Compiled regex for matching the query
     */
    private Pattern simplifiedRegex(String query) {
        query = Pattern.quote(query);
        query = query.replace("*", "\\E.*\\Q"); //Change '*' to unescaped wildcard
        query = query.replace("\\\\E.*\\Q", "*"); //Change '\*' in original to '*'
        query = query.replace("\\Q\\E", "");
        query = ".*" + query + ".*";
        Pattern regex = Pattern.compile(query, Pattern.DOTALL);
        return regex;
    }

    /**
     * Generates a Properties out of a backing bean's choices and the selectorType
     * @param backingBean contains selected choices, as well as custom uploaded file
     * @param selector (MODIFIED, BASE, or SEARCH_IN)
     * @throws FileNotFoundException when a theme/custom file is not found or cannot be opened
     * @throws InvalidPropertiesFormatException when a theme/custom file cannot be parsed as an xml properties file
     */
    private Properties getProperties(LocalizationBackingBean backingBean, SelectorType selector) throws FileNotFoundException, InvalidPropertiesFormatException {
        Choice choice = null;

        switch (selector) {
        case MODIFIED:
            choice = backingBean.getModifiedChoiceEnum();
            break;
        case BASE:
            choice = backingBean.getBaseChoiceEnum();
            break;
        case SEARCH_IN:
            choice = backingBean.getSearchInChoiceEnum();
            break;
        }
        
        Properties properties = new Properties();
        
        switch (choice) {
        case THEME:
            String themeName = null;
            switch (selector) {
            case MODIFIED:
                themeName = backingBean.getModifiedTheme();
                break;
            case BASE:
                themeName = backingBean.getBaseTheme();
                break;
            case SEARCH_IN:
                themeName = backingBean.getSearchInTheme();
            }
            if(! getThemeFilesList().contains(themeName)){
                log.error("Selected theme not found in Themes folder: " + themeName);
               throw new FileNotFoundException(selector.toString());
            }
            File theme = new File(themesFolderName + themeName);
            FileInputStream stream;
            try {
                stream = new FileInputStream(theme);
            } catch (FileNotFoundException e) {
                log.error("Cannot open theme " + themeName);
                throw new FileNotFoundException(selector.toString());
            }
            
            try {
                properties.loadFromXML(stream);
            } catch (InvalidPropertiesFormatException e1) {
                log.error("Cannot process theme " + themeName + ": " + e1.getMessage());
                throw new InvalidPropertiesFormatException(selector.toString());
            } catch (IOException e) {
                log.error("Cannot open theme " + themeName + ": " + e.getMessage());
                throw new FileNotFoundException(selector.toString() );
            }
            break;

        case CUSTOM:
            CommonsMultipartFile custom = null;
            switch (selector) {
            case MODIFIED:
                custom = backingBean.getModifiedCustom();
                break;
            case BASE:
                custom = backingBean.getBaseCustom();
                break;
            case SEARCH_IN:
                custom = backingBean.getSearchInCustom();
            }
            try {
                properties.loadFromXML(custom.getInputStream());
            } catch (InvalidPropertiesFormatException e) {
                log.error("Cannot process uploaded file");
                throw new InvalidPropertiesFormatException(selector.toString());
            } catch (IOException e) {
                log.error("Cannot open uploaded file");
                throw new FileNotFoundException(selector.toString());
            }
            break;
        case DEFAULT:
        default:
            boolean devMode = (workspaceDir != null);
            
            if(!devMode && builtInProperties != null){
                return builtInProperties;
            }
            
            for (String i18nFile : defaultI18nFiles) {
                org.springframework.core.io.Resource res = applicationContext.getResource(i18nFile + ".xml");

                Properties newProperties = new Properties();
                try {
                    newProperties.loadFromXML(res.getInputStream());
                } catch (InvalidPropertiesFormatException e) {
                    log.warn("Cannot process file " + i18nFile + ", skipping");
                } catch (IOException e) {
                    log.warn("Cannot open file " + i18nFile + ", skipping");
                }
                
                Set<String> oldKeys = properties.stringPropertyNames();
                Set<String> dupKeys = Sets.newHashSet(newProperties.stringPropertyNames());
                
                dupKeys.retainAll(oldKeys);
                
                for(String dupKey : dupKeys){
                    log.warn("Duplicate elements with key '" + dupKey + "'");
                    String newValue = newProperties.getProperty(dupKey);
                    while(oldKeys.contains(dupKey)){
                        dupKey += " DUPLICATE";
                    }
                    properties.setProperty(dupKey, newValue);
                    newProperties.remove(dupKey);
                }

                properties.putAll(newProperties);
            }
            builtInProperties = properties;
            
            break;
        }
        return properties;
    }

    /**
     * @return List of theme files names stored in 'Server/Config/Themes/'
     */
    public List<String> getThemeFilesList() {
        File folder = new File(themesFolderName);

        List<String> themes = Lists.newArrayList();
        if(folder.listFiles() != null){
            for (File file : folder.listFiles()) {
                themes.add(file.getName());
            }
        }
        return themes;
    }
    
    /**
     * Generates an xml properties file stream from key-value's stored in a list of LocalizationPair's
     * @param localizationPairs List of key-value pairs to be put in the file
     * @return Stream of a xml properties file
     */
    public InputStream getXML(List<LocalizationPair> localizationPairs){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
                + "<properties>\n");

        for (LocalizationPair localizationPair : localizationPairs) {
            sb.append(localizationPair.getXml() + "\n");
        }
        sb.append("</properties>");

        String xml = sb.toString();
        
        InputStream xmlStream = null;
        try {
            xmlStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            /*
             * This is unpossible. Every implementation of Java is required to support UTF-8 encoding with the name "UTF-8"
             * Reference http://docs.oracle.com/javase/1.4.2/docs/api/java/nio/charset/Charset.html
             */
        }
        
        return xmlStream;      
    }

    /**
     * @return All default i18n files as 1 theme file
     */
    public InputStream getDumpXml() {
        //Build up a dummy bean to get the properties
        LocalizationBackingBean backingBean = new LocalizationBackingBean();
        backingBean.setBaseChoice("default");
        
        Properties properties = null;
        try {
            properties = getProperties(backingBean, SelectorType.BASE);
        } catch (FileNotFoundException | InvalidPropertiesFormatException e) {
            //Base selection does not throw exceptions.
        }
        List<LocalizationPair>  localizationPairs = Lists.newArrayList();
        
        for(String key : properties.stringPropertyNames()){
            localizationPairs.add(new LocalizationPair(key, properties.getProperty(key)));
        }
        
        Collections.sort(localizationPairs);
        
        return getXML(localizationPairs);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
