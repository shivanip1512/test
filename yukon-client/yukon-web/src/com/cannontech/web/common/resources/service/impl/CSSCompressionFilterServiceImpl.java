package com.cannontech.web.common.resources.service.impl;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.service.PackageFilterService;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;


/** CSSCompressionFilterServiceImpl
* The Css Compression Fitler service applies YUICssCompressorProcessor as provided by
* WRO4j library. @see http://wro4j.readthedocs.org/en/latest/ 
*
*This utility has known issues as described by https://github.com/yui/yuicompressor/blob/master/doc/CHANGELOG #keyword regex
*TODO upgrade successfully to a stable build of YUICompression lib,
*TODO create a validation for the css.
* @version 1.0
*/
public class CSSCompressionFilterServiceImpl implements PackageFilterService {

    private YUICssCompressorProcessor compressor = new YUICssCompressorProcessor();
    private ResourceBundleFilterServiceType filterType = ResourceBundleFilterServiceType.CSS_MINIFY;
    private static final Logger log = YukonLogManager.getLogger(CSSCompressionFilterServiceImpl.class);
    @Override
    public void validateResourceBundle(ResourceBundle bundle) throws Exception {
        //TODO:create validation for CSS validation
    }

    @Override
    public void processResourceBundle(ResourceBundle bundle) throws Exception {
        log.debug("processResourceBundle -> " + bundle.getName());
        try{
        Instant start = Instant.now();
        StringWriter minified = new StringWriter();
        compressor.process(new StringReader(bundle.getResourceResult()), minified);
        bundle.setResourceResult(minified.toString());
        minified.close();
        log.debug("processResourceBundle operation for : " +  bundle.getName() + "duration of : " + 
                new Duration(start, Instant.now()).getMillis() + "ms");
        }
        catch(Exception e)
        {
            log.debug(e.getMessage());
        }
    }

    @Override
    public ResourceBundleFilterServiceType getFilterType() {
        return this.filterType;
    }

}

