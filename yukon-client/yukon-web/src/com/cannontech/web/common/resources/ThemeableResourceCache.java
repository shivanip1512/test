package com.cannontech.web.common.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import com.yahoo.platform.yui.compressor.*;
import com.asual.lesscss.LessEngine;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.web.admin.theme.dao.ThemeDao;
import com.cannontech.web.admin.theme.model.Theme;
import com.cannontech.web.admin.theme.model.ThemePropertyType;
import com.cannontech.web.input.type.ImageType;
import com.cannontech.web.input.type.PixelType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ThemeableResourceCache {
    
    @Autowired private ThemeDao themeDao;
    @Autowired private ResourceLoader loader;
    @Autowired private ConfigurationSource config;
    
    private boolean debug;
    private static final Logger log = YukonLogManager.getLogger(ThemeableResourceCache.class);
    
    private static Cache<ThemeableResource, CachedResourceValue> cache = CacheBuilder.newBuilder().build();
    
    public CachedResourceValue getResource(final ThemeableResource resource) throws Exception {
        
        CachedResourceValue value = cache.get(resource, new Callable<CachedResourceValue>() {
            @Override
            public CachedResourceValue call() throws Exception {
                return load(resource);
            }
        });
        
        if (debug) {
            long lastModified = loader.getResource(resource.getPath()).lastModified();
            if (value.getTimestamp() < lastModified) {
                cache.put(resource, load(resource));
                value = cache.getIfPresent(resource);
            }
        }
        
        return value;
    }
    
    @PostConstruct
    private void init() throws Exception {
        debug = config.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
        reloadAll();
    }

    public void reloadAll() throws Exception {
        for (ThemeableResource resource : ThemeableResource.values()) {
            cache.put(resource, load(resource));
        }
    }
    
    private CachedResourceValue load(ThemeableResource resource) throws Exception {
        
        Instant start = Instant.now();
        
        Resource file = loader.getResource(resource.getPath());
        
        String less;
        if (resource.isDefaultFile()) {
            less = IOUtils.toString(file.getInputStream(), "UTF-8");
        } else {
            less = applyThemeProperties(file);
        }
        
        log.info("Loading " + resource + ": theme property replacement took " + new Duration(start, Instant.now()).getMillis() + "ms");
        start = Instant.now();
        
        LessEngine engine = new LessEngine();
        String css = engine.compile(less);
        
        log.info("Loading " + resource + ": less compiling took " + new Duration(start, Instant.now()).getMillis() + "ms");
        start = Instant.now();
        
        if (!debug) {
            StringWriter minified = new StringWriter();
            CssCompressor compressor = new CssCompressor(new StringReader(css.toString()));
            compressor.compress(minified, -1);
            log.info("Loading " + resource + ": css compression took " + new Duration(start, Instant.now()).getMillis() + "ms");
            css = minified.toString();
        }
        
        return CachedResourceValue.of(css, Instant.now().getMillis());
    }

    private String applyThemeProperties(Resource file) throws IOException {
        
        Theme theme = themeDao.getCurrentTheme();
        StringBuilder less = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        
        String regexPrefix = "(?<=@";
        String regexSuffix = ":\\s{0,5}+)[^;]+?(?=;)";
        
        String line = br.readLine();
        while (line != null) {
            for (ThemePropertyType type : theme.getProperties().keySet()) {
                
                String regex = regexPrefix + type.getVarName() + regexSuffix;
                String replacement = (String)theme.getProperties().get(type);
                
                if (type.getInputType() instanceof ImageType) {
                    replacement = "'../../../common/images/" +  replacement + "'";
                } else if (type.getInputType() instanceof PixelType) {
                    replacement = replacement + "px";
                }
                line = line.replaceAll(regex, replacement);
            }
            less.append(line).append("\n");
            
            line = br.readLine();
        }
        br.close();
        
        return less.toString();
    }
    
}