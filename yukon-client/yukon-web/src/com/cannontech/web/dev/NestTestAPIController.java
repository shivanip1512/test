package com.cannontech.web.dev;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.v3.CustomerEnrollment;
import com.cannontech.dr.nest.model.v3.CustomerEnrollments;
import com.cannontech.dr.nest.model.v3.CustomerInfo;
import com.cannontech.dr.nest.model.v3.Customers;
import com.cannontech.dr.nest.model.v3.EnrollmentSource;
import com.cannontech.dr.nest.model.v3.EnrollmentState;
import com.cannontech.dr.nest.model.v3.EventId;
import com.cannontech.dr.nest.model.v3.PostalAddress;
import com.cannontech.dr.nest.model.v3.ProgramType;
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
    @RequestMapping(value = "/v3/partners/{partnerId}/customers:batchUpdateEnrollments", method = RequestMethod.PUT)
    public void updateEnrollment(@PathVariable("partnerId") String partnerId, @RequestBody CustomerEnrollments enrollments) {
        log.debug("partnerId {}", partnerId);
        CustomerEnrollment enrollment = enrollments.getCustomerEnrollments().get(0);
        if(enrollment.getEnrollmentState() == EnrollmentState.DISSOLVED) {
            nestSimService.dissolveAccountWithNest(enrollment.getCustomerId());
        }else if(enrollment.getEnrollmentState() == EnrollmentState.ACCEPTED) {
            nestSimService.updateGroup(enrollment.getCustomerId(), enrollment.getGroupId());
        }
      //simulate errors
    }
    
    @IgnoreCsrfCheck
    @RequestMapping(value = "/v3/partners/{partnerId}/rushHourEvents/{eventId}:stop ", method = RequestMethod.POST)
    public void stop(@PathVariable("partnerId") String partnerId, @PathVariable("eventId") String eventId,
            HttpServletRequest request, HttpServletResponse response) {
        log.debug("Stropped control partnerId {} eventId {}", eventId);
        //simulate errors
    }
    
    @IgnoreCsrfCheck
    @RequestMapping(value = "/v3/partners/{partnerId}/rushHourEvents/{eventId}:cancel", method = RequestMethod.POST)
    public void cancel(@PathVariable("partnerId") String partnerId, @PathVariable("eventId") String eventId,
            HttpServletRequest request, HttpServletResponse response) {
        log.debug("Canceled control partnerId {} eventId {}", eventId);
      //simulate errors
    }
   
    @IgnoreCsrfCheck
    @RequestMapping(value = "/v3/partners/{partnerId}/rushHourEvents/{eventType}", method = RequestMethod.POST)
    @ResponseBody
    public EventId control(@PathVariable("partnerId") String partnerId, @PathVariable("eventType") String eventType,
            HttpServletRequest request, HttpServletResponse response) {
        log.debug("Control partnerId {} eventType {}", partnerId, eventType);
        byte[] array = new byte[5];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        return new EventId(generatedString);
        // simulate errors
        //if error return SchedulabilityError? We do not know if it one error or a map
    }
    
    @IgnoreCsrfCheck
    @RequestMapping(value = "/v3/partners/{partnerId}/customers", method = RequestMethod.GET)
    @ResponseBody
    public Customers getAllCustomers(@PathVariable("partnerId") String partnerId) {
        List<NestExisting> simulated = nestSimService.downloadExisting();
        List<CustomerInfo> infos = simulated.stream().map(sim -> {
            DateTime date = new DateTime(Integer.parseInt(sim.getYear()), Integer.parseInt(sim.getMonth()),
                Integer.parseInt(sim.getDay()), 0, 0, 0, 0);
            CustomerInfo info = new CustomerInfo();
            info.setAccountNumber(sim.getAccountNumber());
            info.setApproveTime(date.toString());
            info.setCustomerId(sim.getRef());
            info.setDeviceIds(getThemostatsForAccountInNest(sim));
            info.setEmail(sim.getEmail());
            info.setEnrollmentSource(EnrollmentSource.ENROLLMENT_APP);
            info.setEnrollmentState(EnrollmentState.ACCEPTED);
            info.setEnrollTime(date.toString());
            info.setGroupId(sim.getGroup());
            info.setName(sim.getName());
            List<ProgramType> programTypes = new ArrayList<>();
            if(!Strings.isEmpty(sim.getSummerRhr())){
                programTypes.add(ProgramType.SUMMER_RUSH_HOUR_REWARDS);
            }  
            if(!Strings.isEmpty(sim.getWinterRhr())){
                programTypes.add(ProgramType.WINTER_RUSH_HOUR_REWARDS);
            }
            info.setProgramType(programTypes);
            info.setServiceAddress(
                new PostalAddress(sim.getAddress(), sim.getCity(), sim.getState(), sim.getZipCode()));
            return info;
        }).collect(Collectors.toList());
        return new Customers(infos, "");
    }   
    
    private List<String> getThemostatsForAccountInNest(NestExisting row){
        Set<String> newThermostats = new HashSet<>();
        if(!Strings.isEmpty(row.getSummerRhr())){
            newThermostats.addAll(Arrays.asList(row.getSummerRhr().split(",")));
        }
        if(!Strings.isEmpty(row.getWinterRhr())){
            newThermostats.addAll(Arrays.asList(row.getWinterRhr().split(",")));
        }
        return newThermostats.stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
