package com.cannontech.web.common.resources.service.impl;

import java.io.StringReader;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.service.PackageFilterService;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;
import com.cannontech.web.common.resources.service.error.PackageResourceFilterException;
import com.cannontech.web.common.resources.service.error.PackageResourceFilterServiceErrorState;
import com.cannontech.web.common.resources.service.error.YuiCompressorErrorState;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.yahoo.platform.yui.compressor.org.mozilla.javascript.EvaluatorException;


/** JSCompressionFilterServiceImpl
*The JS Compression lib minifies/compresses the specifeid ResourceBundle exposed. 
*TODO:create the validation for JS in next phase/sprint.
*TODO:find a decent compressor to work with build.
*
* @version 1
*/
public class JSCompressionFilterServiceImpl implements PackageFilterService {
    private final static int lineBreakPos = -1;
    private final static boolean munge = true;
    private final static boolean verbose = false;
    private final static boolean preserveAllSemiColons = false;
    private final static boolean disableOptimizations = false; 
    
    
    private ResourceBundleFilterServiceType filterType = ResourceBundleFilterServiceType.JS_MINIFY;
     
    private static final Logger log = YukonLogManager.getLogger(JSCompressionFilterServiceImpl.class);
    @Override
    public void validateResourceBundle(ResourceBundle bundle) throws PackageResourceFilterException {
        //TODO:create the validation for JS in next phase/sprint.
    }

    @Override
    public void processResourceBundle(ResourceBundle bundle) throws PackageResourceFilterException {
        log.debug("processResourceBundle -> " + bundle.getName());
        Instant start = Instant.now();
        StringReader in = null;
        StringWriter mungeMap = null;
        StringWriter out = new StringWriter();
        try {
            in = new StringReader(bundle.getResourceResult());
            
            JavaScriptCompressor compressor = new JavaScriptCompressor(in,  new YuiCompressorErrorReporter());   
            
            compressor.compress(out,mungeMap, lineBreakPos, munge, verbose, preserveAllSemiColons, disableOptimizations);
            bundle.setResourceResult(out.toString());
            log.debug("load operation for : " +  bundle.getName() + "duration of : " + 
                    new Duration(start, Instant.now()).getMillis() + "ms");
        }
        catch(Exception e)
        {
            String message = e.getMessage();
            log.debug(message);
            throw new PackageResourceFilterException(message, e, PackageResourceFilterServiceErrorState.JS_MINIFY_FAIL);
        }
        finally{
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(mungeMap);
        }
    }

    @Override
    public ResourceBundleFilterServiceType getFilterType() {
        return this.filterType;
    }
   
    private static class YuiCompressorErrorReporter implements com.yahoo.platform.yui.compressor.org.mozilla.javascript.ErrorReporter
    {
        @Override
        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                log.debug("[" + YuiCompressorErrorState.SEVERE + "]" +  message);
            } else {
                log.debug("[" + YuiCompressorErrorState.SEVERE + "]" +  line + ':' + lineOffset + ':' + message);
            }
        }

        @Override
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                log.debug("[" + YuiCompressorErrorState.WARNING + "]" +  message);
            } else {
                log.debug("[" + YuiCompressorErrorState.WARNING + "]" +  line + ':' + lineOffset + ':' + message);
            }
        }

        @Override
        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            error(message,sourceName,line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
}


