package com.cannontech.web.taglib;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.common.mapping.service.MappingService;

@Configurable(value="getMappingUrlTagPrototype", autowire=Autowire.BY_NAME)
public class GetMappingUrlTag extends YukonTagSupport {
        
    @Autowired private MappingService mappingService;

    private String viewType;

    @Override
    public void doTag() throws IOException {
        String mappingUrl = mappingService.getMappingUrl(viewType); 
        getJspContext().getOut().print(mappingUrl);
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}