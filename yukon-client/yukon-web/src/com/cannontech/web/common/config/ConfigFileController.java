package com.cannontech.web.common.config;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.retrieve.DefinitionsConfigRetriever;
import com.cannontech.common.config.retrieve.RfnConfigRetriever;

@Controller
@RequestMapping("/config/*")
public class ConfigFileController {
    
    @Autowired private ResourceLoader loader;
    
    @RequestMapping
    public void rfn(HttpServletResponse resp) throws IOException {
        
        resp.setContentType("text/xml");
        
        Resource customFile = loader.getResource(RfnConfigRetriever.customFilePath);
        
        /* If a custom file exists in .../Server/Config, use that, otherwise use the builtin default file. */
        if (customFile.exists()) {
            FileCopyUtils.copy(customFile.getInputStream(), resp.getOutputStream());
        } else {
            Resource classPathFile = loader.getResource(RfnConfigRetriever.defaultPath);
            FileCopyUtils.copy(classPathFile.getInputStream(), resp.getOutputStream());
        }
        
    }
    
    @RequestMapping
    public void deviceDefinition(HttpServletResponse resp) throws IOException {
        
        Resource customFile = loader.getResource(DefinitionsConfigRetriever.customFilePath);
        
        if (customFile.exists()) {
            resp.setContentType("text/xml");
            FileCopyUtils.copy(customFile.getInputStream(), resp.getOutputStream());
        }
    }

}