package com.cannontech.web.common.resources.service.impl;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;


import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.service.PackageFilterService;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;

/** JSCompressionFilterServiceImpl
*The JS Compression lib minifies/compresses the specifeid ResourceBundle exposed. 
*TODO:create the validation for JS in next phase/sprint.
*TODO:find a decent compressor to work with build.
*
* @version 1
*/
public class JSCompressionFilterServiceImpl implements PackageFilterService {
     
    private ResourceBundleFilterServiceType filterType = ResourceBundleFilterServiceType.JS_MINIFY;
    private static final Logger log = YukonLogManager.getLogger(JSCompressionFilterServiceImpl.class);
    @Override
    public void validateResourceBundle(ResourceBundle bundle) throws Exception {
        //TODO:create the validation for JS in next phase/sprint.
    }

    @Override
    public void processResourceBundle(ResourceBundle bundle) throws Exception {
         
        log.debug("processResourceBundle -> " + bundle.getName());
        Instant start = Instant.now();
        //TODO:find a decent compressor to work with build.        
        log.debug("load operation for : " +  bundle.getName() + "duration of : " + 
        new Duration(start, Instant.now()).getMillis() + "ms");
    }

    @Override
    public ResourceBundleFilterServiceType getFilterType() {
        return this.filterType;
    }

}

