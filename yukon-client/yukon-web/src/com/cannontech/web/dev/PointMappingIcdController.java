package com.cannontech.web.dev;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PointMappingIcdController {

    @Value("classpath:yukonPointMappingIcd.yaml") private Resource inputFile;

    @RequestMapping("/rfn/icd/view")
    public String view(ModelMap model) {

        try {
            Yaml y = new Yaml();
            Iterable<Object> yamlObjects = y.loadAll(inputFile.getInputStream());
            
            ObjectMapper jsonFormatter = new ObjectMapper();
            
            jsonFormatter.enable(SerializationFeature.INDENT_OUTPUT);
            
            String formatted = 
                    StreamSupport.stream(yamlObjects.spliterator(), false)
                        .map(obj -> { try { return jsonFormatter.writeValueAsString(obj); } catch (IOException e) { return e.toString(); } })
                        .collect(Collectors.joining("\r\n"));
            
            model.addAttribute("icd", formatted);
        } catch (IOException e) {
            model.addAttribute("icd", e);
        }

        return "rfn/pointMappingIcd.jsp";
    }
}
