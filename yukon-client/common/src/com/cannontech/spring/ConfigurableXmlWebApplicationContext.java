package com.cannontech.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * An updated version of CannonXmlWebApplictionContext that eschews RequiredAnnotationBeanPostProcessor
 * and allows configuration of any number of XML context files.
 * 
 * This application context does not automatically include the /WEB-INF/contexts/sharedWebContext.xml
 * or [namespace]-servlet.xml files like CannonXmlWebApplictionContext, so those must be manually 
 * included in the constructor contextFilePaths list for it to be used.
 */
public class ConfigurableXmlWebApplicationContext extends XmlWebApplicationContext {
    private static final String SHARED_WEB_CONFIG_LOCATION = "/WEB-INF/contexts/sharedWebContext.xml";
    public static final String DEFAULT_ROOT = "/WEB-INF/contexts/";
    
    private final String[] contextFilePaths;
    private final boolean includeDefaultSharedWebContext;
    private final boolean includeDefaultNamespaceContext;
    
    /**
     * Constructs an application context that incorporates the specified XML context files.
     * @param contextFilePaths The paths to the XML context files.
     */
    public ConfigurableXmlWebApplicationContext(String... contextFilePaths) {
        this.contextFilePaths = contextFilePaths;
        includeDefaultSharedWebContext = false;
        includeDefaultNamespaceContext = false;
    }
    
    /**
     * Constructs an application context that incorporates the specified XML context files.
     * 
     * @param pathRoot The root path to prepend to each context file path.
     * @param contextFilePaths The remainder of the context file path(s), which will be appended to the root.
     */
    public ConfigurableXmlWebApplicationContext(String pathRoot, String... contextFilePaths) {
        this.contextFilePaths = Arrays.stream(contextFilePaths)
                .map(path -> pathRoot + path)
                .toArray(String[]::new);
        includeDefaultSharedWebContext = false;
        includeDefaultNamespaceContext = false;
    }
    
    /**
     * Constructs an application context that incorporates the specified XML context files. When 
     * includeDefaultSharedWebContext and includeDefaultServletContext are set to <code>true</code>,
     * this behaves similarly to CannonXmlWebApplicationContext.
     * 
     * @param includeDefaultSharedWebContext Include the /WEB-INF/contexts/sharedWebContext.xml.
     * @param includeDefaultServletContext Include the /WEB-INF/contexts/[namespace]-servlet.xml.
     * @param contextFilePaths The paths to additional XML context files.
     */
    public ConfigurableXmlWebApplicationContext(boolean includeDefaultSharedWebContext, 
            boolean includeDefaultNamespaceContext, String... contextFilePaths) {
        
        this.contextFilePaths = contextFilePaths;
        this.includeDefaultSharedWebContext = includeDefaultSharedWebContext;
        this.includeDefaultNamespaceContext = includeDefaultNamespaceContext;
    }
    
    @Override
    public String[] getConfigLocations() {
        List<String> configLocationsList = new ArrayList<>();
        
        // Add the shared web config location only if specified
        if (includeDefaultSharedWebContext) {
            configLocationsList.add(SHARED_WEB_CONFIG_LOCATION);
        }
        
        // Add config locations from the parent
        Collections.addAll(configLocationsList, super.getConfigLocations());
        
        // Add config locations from our constructor
        Collections.addAll(configLocationsList, contextFilePaths);
        
        // Convert to array and return
        return configLocationsList.toArray(new String[configLocationsList.size()]);
    }
    
    @Override
    protected String[] getDefaultConfigLocations() {
        if (getNamespace() != null && includeDefaultNamespaceContext) {
            return new String[] {DEFAULT_ROOT + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
        }
        return new String[] {"/WEB-INF/contexts/applicationContext.xml"};
    }
}
