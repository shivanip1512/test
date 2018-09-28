package com.cannontech.web.dev;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.dr.nest.service.impl.NestCommunicationServiceImpl;
import com.cannontech.dr.nest.service.impl.NestSimulatorServiceImpl;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;

@Controller
@RequestMapping("/nestApi/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class NestTestAPIController {

    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class);
    @Autowired NestSimulatorService nestService;

    @RequestMapping(value = "/v1/users/current/latest.csv")
    public void existing(HttpServletResponse response) {
        log.info("Reading existing file");
        String filePath = NestSimulatorServiceImpl.SIMULATED_FILE_PATH;
        String defaultFileName = nestService.getFileName(YukonSimulatorSettingsKey.NEST_FILE_NAME);
        String readFile = filePath + "\\" + defaultFileName;
        log.info("Reading file " + readFile);
        try (OutputStream output = response.getOutputStream();
             InputStream input = new FileInputStream(readFile);) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            log.error("Exception is reading existing file from yukon " + e);
        }
    }
    
    @IgnoreCsrfCheck
    @RequestMapping(value = "/v1/users/current", method = RequestMethod.POST)
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                char[] charBuffer = new char[1024];
                int bytesRead;  
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
            log.info("Data " + stringBuilder.toString());
        } catch (IOException e) {
            log.error("Error in parsing " + e);
        }
    } 
}
