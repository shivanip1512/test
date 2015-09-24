
package com.cannontech.web.common.resources.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.data.ResourcePackageList;
import com.cannontech.web.common.resources.data.ResourceType;
import com.cannontech.web.common.resources.service.PackageFilterService;
import com.cannontech.web.common.resources.service.ResourceBundleService;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;
import com.cannontech.web.tools.trends.data.TrendType.GraphType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

/** ResourceBundleServiceImpl
*   This version of the ResourceBundleService implementation utilizes at compile time:
*   
* <ul>
* <li>Factory of the filters
* <li>Resource Loader
*</ul>
* **Entries for the filters are written in applicationContext.xml for dependency injection.
*
* At runtime and per request, the main entry getResourceBundle will try to determine if
* there is an entry in CACHE, and either deliver a completed bundle or fetch a manifest
* to make one. 
* 
* The ResourceBundleService comes in 4 steps: search, combine, filter and delivery if no exception
* is triggered. Exception leads to a 404 error on a failed package. A low TTL should allow for testing 
* and updating of packages at RUNTIME. This will allow FE developers to continue to develop during
* application life-cycle.
* 
*/
@Service
public class ResourceBundleServiceImpl implements ResourceBundleService {

    @Autowired private ResourceLoader loader;
    @Autowired
    @Qualifier("cssCompressionFilterService")
    private PackageFilterService cssCompressService;
    @Autowired
    @Qualifier("jsCompressionFilterService")
    private PackageFilterService jsCompressionService;
    @Autowired
    @Qualifier("lessToCssFilterService")
    private PackageFilterService lessToCSSService;
    
    private static Cache<String, ResourceBundle> CACHE = CacheBuilder.newBuilder().build();
    private static final Logger log = YukonLogManager.getLogger(ResourceBundleServiceImpl.class);
    private static final String resourceBundlePath = "/WEB-INF/contexts/resource-bundle-manifest.xml";
    private static final String resourceBundleListInstance ="yukonResourceList";
    private Map<ResourceBundleFilterServiceType,PackageFilterService> filters;
 
    @Override
    public ResourceBundle getResourceBundle(final String packageName, ResourceType type) throws Exception {
        log.debug("getResourceBundle -> " + packageName + " resourceType: " + type);
        ResourceBundle resourceBundleResponse = CACHE.getIfPresent(packageName);
        if(resourceBundleResponse != null) {
            DateTime resourceBundleDT =  resourceBundleResponse.getTimestamp().plus(resourceBundleResponse.getTTL());
            if(resourceBundleDT.isBeforeNow()){
                resourceBundleResponse = loadResourceBundle(resourceBundleResponse.getName());
            }
        }else{
            
            resourceBundleResponse =  loadResourceBundle(packageName);
        }
        if(resourceBundleResponse.getResourceType() != type) { 
            throw new Exception("ResourceType mismatch! " + resourceBundleResponse.getResourceType() + " : " + type);
        }
        
        return resourceBundleResponse;
    }

    @Override
    public void combineResourceContent(ResourceBundle resource) throws Exception {
        log.debug("getEncodedResourceContent -> " + resource.getName());
        Instant start = Instant.now();
        StringBuilder mergedPackageWriter = new StringBuilder();
        List<String> packageList = resource.getResourcesPathList();
        packageList.stream().forEach(new Consumer<String>() {
            @Override
            public void accept(String packagePath) {
                org.springframework.core.io.Resource file = loader.getResource(packagePath);
                try {
                    String fileContent = IOUtils.toString(file.getInputStream(), resource.getResourceType().getEncoding());
                    mergedPackageWriter.append(fileContent);
                } catch (IOException e) {
                    log.warn("caught exception in accept", e);
                }
            }
        });
        log.debug("Encoding for package " + resource.getName() + "is complete with " +
        mergedPackageWriter.length() + " length and duration of : " + 
        new Duration(start, Instant.now()).getMillis() + "ms");
        resource.setTimestamp(new DateTime(start));
        resource.setResourceResult(mergedPackageWriter.toString());
        CACHE.put(resource.getName(), resource);
    }
    private ResourceBundle loadResourceBundle(String packageName) throws Exception {
        log.debug("load -> " + packageName);
        Instant start = Instant.now();
        Resource resource = loader.getResource(resourceBundlePath);
        GenericApplicationContext ctx = new GenericApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
        xmlReader.loadBeanDefinitions(resource);
        ctx.refresh();
        ResourcePackageList resourcePackageList = (ResourcePackageList) ctx.getBean(resourceBundleListInstance);
        log.debug("package name:"+ packageName);
        if(!resourcePackageList.getPackages().containsKey(packageName))
        {
            throw new Exception("package does not exist " + packageName);
        }
        ResourceBundle resourceBundle = resourcePackageList.getPackages().get(packageName);
        combineResourceContent(resourceBundle);
        processFilterList(resourceBundle);
        log.debug("load operation for : " +  packageName + "duration of : " + 
                new Duration(start, Instant.now()).getMillis() + "ms");
        return  resourceBundle;
    }

    @Override
    public void processFilterList(ResourceBundle resource) throws Exception {
        log.debug("processFilterList -> " + resource.getName());
        Instant start = Instant.now();
        List <ResourceBundleFilterServiceType> processFilterServiceList = resource.getFilterServiceList();
        for(ResourceBundleFilterServiceType processFilterType : processFilterServiceList){
            if(filters.containsKey(processFilterType)){
                PackageFilterService filterService = filters.get(processFilterType);
                filterService.processResourceBundle(resource);
            }
        }
        log.debug("processFilterList operation for : " +  resource.getName() + "duration of : " + 
                new Duration(start, Instant.now()).getMillis() + "ms");
    }
    @PostConstruct
    private void init() throws Exception {
        filters = ImmutableMap.of(
                ResourceBundleFilterServiceType.CSS_MINIFY, cssCompressService,
                ResourceBundleFilterServiceType.JS_MINIFY, jsCompressionService,
                ResourceBundleFilterServiceType.LESS_TO_CSS, lessToCSSService
            );

    }

}
