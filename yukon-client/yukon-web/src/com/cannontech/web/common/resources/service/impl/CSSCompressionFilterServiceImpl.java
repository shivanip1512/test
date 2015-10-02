package com.cannontech.web.common.resources.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.yahoo.platform.yui.compressor.*;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.service.PackageFilterService;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;
import com.cannontech.web.common.resources.service.error.PackageResourceFilterException;
import com.cannontech.web.common.resources.service.error.PackageResourceFilterServiceErrorState;


/** CSSCompressionFilterServiceImpl
* The Css Compression Fitler service applies YUICssCompressorProcessor as provided by
* https://github.com/yui/yuicompressor  
*
*This utility has known issues as described by https://github.com/yui/yuicompressor/blob/master/doc/CHANGELOG #keyword regex
*COMPLETED: upgrade successfully to a stable build of YUICompression lib
*TODO create a validation for the css.
* @version 1.0
*/
public class CSSCompressionFilterServiceImpl implements PackageFilterService {

     
    private ResourceBundleFilterServiceType filterType = ResourceBundleFilterServiceType.CSS_MINIFY;
    private static final Logger log = YukonLogManager.getLogger(CSSCompressionFilterServiceImpl.class);
    @Override
    public void validateResourceBundle(ResourceBundle bundle) throws PackageResourceFilterException {
        //TODO:create validation for CSS validation
    }

    @Override
    public void processResourceBundle(ResourceBundle bundle) throws PackageResourceFilterException {
        log.debug("processResourceBundle -> " + bundle.getName());
        
            Instant start = Instant.now();
            StringWriter minified = new StringWriter();
            try {
                CssCompressor compressor = new CssCompressor(new StringReader(bundle.getResourceResult()));
                compressor.compress(minified, -1);
                bundle.setResourceResult(minified.toString());
                minified.close();
            } catch (IOException e) {
                
                log.warn("caught exception in processResourceBundle", e);
                String message =e.getMessage(); 
                log.debug(message);
                throw new PackageResourceFilterException(message, e, PackageResourceFilterServiceErrorState.CSS_COMPRESSION_FAIL);
            }
            log.debug("processResourceBundle operation for : " +  bundle.getName() + "duration of : " + 
            new Duration(start, Instant.now()).getMillis() + "ms");
                
    }

    @Override
    public ResourceBundleFilterServiceType getFilterType() {
        return this.filterType;
    }

}

