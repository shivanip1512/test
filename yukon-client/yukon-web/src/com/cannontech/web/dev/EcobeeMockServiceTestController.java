package com.cannontech.web.dev;

import java.io.IOException;
import java.net.URI;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.api.dr.ecobee.EcobeeZeusJwtTokenAuthService;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData.ecp_thermostat_info;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Timestamp;

import io.jsonwebtoken.Jwts;

@Controller
@RequestMapping("/ecobee/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class EcobeeMockServiceTestController {
    private static final List<String> status = ImmutableList.of("SUCCESS(0)",
            "AUTHENTICATION_FAILED(1)", "NOT_AUTHORIZED(2)",
            "PROCESSING_ERROR(3)", "SERIALIZATION_ERROR(4)",
            "INVALID_REQUEST_FORMAT(5)", "TOO_MANY_THERMOSTATS(6)",
            "VALIDATION_ERROR(7)", "INVALID_FUNCTION(8)",
            "INVALID_SELECTION(9)", "INVALID_PAGE(10)", "FUNCTION_ERROR(11)",
            "POST_NOT_SUPPORTED(12)", "GET_NOT_SUPPORTED(13)",
            "AUTHENTICATION_EXPIRED(14)", "DUPLICATE_DATA_VIOLATION(15)");

    private static final List<String> zeusStatus = ImmutableList.of("SUCCESS(0)",
            "UNAUTHORIZED(1)", "BAD_REQUEST(2)", "NOT_FOUND(3)");

    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    @Autowired private ZeusEcobeeDataConfiguration zeusEcobeeDataConfiguration;
    @Autowired private EcobeeZeusJwtTokenAuthService ecobeeZeusJwtTokenAuthService;

    @RequestMapping("/viewBase")
    public String viewBase(ModelMap model) {
        model.addAttribute("status", status);
        return "ecobee/viewBase.jsp";
    }

    @IgnoreCsrfCheck
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(Integer regDevice, Integer movDevice, Integer creatSet, Integer movSet, Integer remSet, Integer drSend,
            Integer restoreSend, Integer listHierarchy, Integer authenticateOp,
            Integer runtimeReportOp, Integer assignThermostatOp, FlashScope flashScope, ModelMap modelMap) throws IOException {
        ecobeeDataConfiguration.setEcobeeDataConfiguration(regDevice, movDevice, creatSet, movSet, remSet, drSend, restoreSend,
                listHierarchy, authenticateOp, runtimeReportOp, assignThermostatOp);
        modelMap.addAttribute("register", ecobeeDataConfiguration.getRegisterDevice());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.ecobee.mockTest.saved"));
        return "redirect:viewBase";
    }

    @IgnoreCsrfCheck
    @RequestMapping(value = "zeus/update", method = RequestMethod.POST)
    public String updateZeus(Integer authenticateOp, Integer createDeviceOp, Integer deleteDeviceOp, String serialNumber, FlashScope flashScope, ModelMap modelMap) throws Exception {
        zeusEcobeeDataConfiguration.setZeusEcobeeDataConfiguration(authenticateOp, createDeviceOp, deleteDeviceOp, serialNumber);
        sendHttpProtoMessage(serialNumber);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.ecobee.mockTest.saved"));
        return "redirect:viewBase";
    }

    @RequestMapping("zeus/viewBase")
    public String viewZeusBase(ModelMap model) {
        model.addAttribute("status", zeusStatus);
        return "ecobee/zeusViewBase.jsp";
    }

    public void sendHttpProtoMessage(String serialNumber) throws Exception {

        //TODO URL Global settings
        final String url = "http://127.0.0.1:8080/yukon/api/ecobee/runtimeData";
        EcobeeZeusRuntimeData.ecp_thermostat_message message = EcobeeZeusRuntimeData.ecp_thermostat_message.newBuilder()
                                                                                                           .setMessageTimeUtc(Timestamp.newBuilder()
                                                                                                                                       .setSeconds(Instant.now().getEpochSecond())
                                                                                                                                       .build())
                                                                                                           .setThermostatInfo(ecp_thermostat_info.newBuilder()
                                                                                                                                                 .setThermostatId(serialNumber)
                                                                                                                                                 .build())
                                                                                                           .build();
        
        System.out.println(message.toString());
        RestTemplate restTemplate = getRestTemplate();
        RequestEntity<EcobeeZeusRuntimeData.ecp_thermostat_message> entity = RequestEntity.post(new URI(url)).headers(getHttpHeaders()).body(message);

        restTemplate.exchange(entity, Object.class);

    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        converters.add(new ProtobufHttpMessageConverter());
        return restTemplate;
    }

    private HttpHeaders getHttpHeaders() throws Exception {
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.set("Authorization", "Bearer " + createJwtSigned());
        newheaders.setContentType(MediaType.parseMediaType("application/x-protobuf"));
        return newheaders;
    }

    public String createJwtSigned() throws Exception {
        String jwtToken = null;
        PrivateKey privateKey = ecobeeZeusJwtTokenAuthService.getPrivateKey();
        if (privateKey != null) {
            Instant now = Instant.now();
            jwtToken = Jwts.builder()
                           .claim("name", "Eaton")
                           .claim("email", "test@eaton.com")
                           .setSubject("Ecobee")
                           .setId(UUID.randomUUID().toString())
                           .setIssuedAt(Date.from(now))
                           .setExpiration(Date.from(now.plus(5l, ChronoUnit.MINUTES)))
                           .signWith(privateKey)
                           .compact();
        }

        return jwtToken;
    }
}
