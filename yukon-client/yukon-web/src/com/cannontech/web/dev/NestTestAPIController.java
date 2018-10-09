package com.cannontech.web.dev;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestUploadError;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.dr.nest.service.impl.NestCommunicationServiceImpl;
import com.cannontech.dr.nest.service.impl.NestSimulatorServiceImpl;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/nestApi/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NestTestAPIController {

    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class);
    @Autowired NestSimulatorService nestSimService;

    @RequestMapping(value = "/v1/users/current/latest.csv")
    public void existing(HttpServletResponse response) {
        log.info("Reading existing file");
        String filePath = NestSimulatorServiceImpl.SIMULATED_FILE_PATH;
        String defaultFileName = nestSimService.getFileName(YukonSimulatorSettingsKey.NEST_FILE_NAME);
        String readFile = filePath + "\\" + defaultFileName;
        log.info("Reading file " + readFile);
        try (OutputStream output = response.getOutputStream();
             InputStream input = new FileInputStream(readFile);) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            throw new NestException("Failed to read simulated file", e);
        }
    }
    
    @IgnoreCsrfCheck
    @RequestMapping(value = "/v1/users/current", method = RequestMethod.POST)
    public void upload(HttpServletRequest request, HttpServletResponse response) {

        boolean returnError = false;
        NestUploadInfo result = null;
        
        if(returnError) {
            NestUploadError error = new NestUploadError(0, null, null, Lists.newArrayList("Nest error"));
            result = new NestUploadInfo(0, 0, Lists.newArrayList(error));
        } else {
            try {
                result = nestSimService.upload(request.getInputStream());
            } catch (IOException e) {
                throw new NestException("Error reading uploaded file", e);
            }
        }
        try {
            String resultString = JsonUtils.toJson(result);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(resultString);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (JsonProcessingException e) {
            throw new NestException("Error converting response to json "+ result, e);
        } catch (IOException e) {
            throw new NestException("Error sending response", e);
        }
    }
}
