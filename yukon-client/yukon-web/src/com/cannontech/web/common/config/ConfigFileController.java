package com.cannontech.web.common.config;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.dao.DeviceDefinitionDao;
import com.cannontech.common.config.dao.RfnPointMappingDao;

@Controller
@RequestMapping("/config/*")
public class ConfigFileController {
    @Autowired private RfnPointMappingDao rfnPointMappingDao;
    @Autowired private DeviceDefinitionDao deviceDefinitionDao;

    @RequestMapping("rfn")
    public void rfn(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/xml");
        // FileCopyUtils.copy closes both streams when finished.
        FileCopyUtils.copy(rfnPointMappingDao.getPointMappingFile(), resp.getOutputStream());
    }

    @RequestMapping("deviceDefinition")
    public void deviceDefinition(HttpServletResponse resp) throws IOException {
        InputStream customConfigFile = deviceDefinitionDao.findCustomDeviceDefinitions();
        resp.setContentType("text/xml");
        if (customConfigFile != null) {
            resp.setContentLength((int) deviceDefinitionDao.getCustomFileSize());
            // FileCopyUtils.copy closes both streams when finished.
            FileCopyUtils.copy(customConfigFile, resp.getOutputStream());
        }
    }
}
