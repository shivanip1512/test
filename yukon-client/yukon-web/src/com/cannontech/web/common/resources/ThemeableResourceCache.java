package com.cannontech.web.common.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.extensions.processor.css.LessCssProcessor;
import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;
import ro.isdc.wro.model.group.processor.Injector;
import ro.isdc.wro.model.group.processor.InjectorBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
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
    
    private static final Logger log = YukonLogManager.getLogger(ThemeableResourceCache.class);
    
    private LessCssProcessor processor = new LessCssProcessor();
    private YUICssCompressorProcessor compressor = new YUICssCompressorProcessor();
    private static Cache<ThemeableResource, CachedResourceValue> cache = CacheBuilder.newBuilder().build();
    
    public CachedResourceValue getResource(final ThemeableResource resource) throws ExecutionException, IOException {
        
        CachedResourceValue value = cache.get(resource, new Callable<CachedResourceValue>() {
            @Override
            public CachedResourceValue call() throws Exception {
                return load(resource);
            }
        });
        
        if (config.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)) {
            long lastModified = loader.getResource(resource.getPath()).lastModified();
            if (value.getTimestamp() < lastModified) {
                cache.put(resource, load(resource));
                value = cache.getIfPresent(resource);
            }
        }
        
        return value;
    }
    
    @PostConstruct
    private void init() throws IOException {
        
        Context.set(Context.standaloneContext(), new WroConfiguration());
        Injector injector = new InjectorBuilder().build();
        injector.inject(processor);
        
        reloadAll();
    }

    public void reloadAll() throws IOException {
        for (ThemeableResource resource : ThemeableResource.values()) {
            cache.put(resource, load(resource));
        }
    }
    
    private CachedResourceValue load(ThemeableResource resource) throws IOException {
        
        Instant start = Instant.now();
        
        Resource file = loader.getResource(resource.getPath());
        
        Reader less;
        if (resource.isDefaultFile()) {
            less = new BufferedReader(new InputStreamReader(file.getInputStream()));
        } else {
            less = applyThemeProperties(file);
        }
        
        log.info("Loading " + resource + ": theme property replacement took " + new Duration(start, Instant.now()).getMillis() + "ms");
        start = Instant.now();
        
        StringWriter css = new StringWriter();
        processor.process(less, css);
        less.close();
        css.close();
        
        log.info("Loading " + resource + ": less compiling took " + new Duration(start, Instant.now()).getMillis() + "ms");
        start = Instant.now();
        
        boolean devMode = config.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE);
        if (!devMode) {
            
            StringWriter minified = new StringWriter();
            compressor.process(new StringReader(css.toString()), minified);
            
            log.info("Loading " + resource + ": css compression took " + new Duration(start, Instant.now()).getMillis() + "ms");
            
            return CachedResourceValue.of(minified.toString(), Instant.now().getMillis());
            
        }
        
        return CachedResourceValue.of(css.toString(), Instant.now().getMillis());
    }

    private Reader applyThemeProperties(Resource file) throws IOException {
        
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
                    replacement = "'/common/images/" +  replacement + "'";
                } else if (type.getInputType() instanceof PixelType) {
                    replacement = replacement + "px";
                }
                line = line.replaceAll(regex, replacement);
            }
            less.append(line).append("\n");
            
            line = br.readLine();
        }
        br.close();
        
        return new StringReader(less.toString());
    }
    
}