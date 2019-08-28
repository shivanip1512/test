package com.cannontech.web.bulk;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.service.MeterProgrammingService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableSet;

@Controller
@RequestMapping("/config/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class MeterProgrammingController {
    private final static Logger log = YukonLogManager.getLogger(MeterProgrammingController.class);
    private final static String baseKey = "yukon.web.modules.tools.bulk.meterProgramming.";

    @Autowired protected CollectionActionService collectionActionService;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
    @Autowired private MeterProgrammingService meterProgrammingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private MeterProgrammingConfigurationValidator validator;

    @RequestMapping(value = "meterProgrammingInputs", method = RequestMethod.GET)
    public String meterProgrammingInputs(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        setupModel(deviceCollection, model);
        model.addAttribute("programmingConfiguration", new MeterProgrammingConfiguration());
        return "meterProgramming/programmingInputs.jsp";
    }
    
    private void setupModel(DeviceCollection deviceCollection, ModelMap model) {
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("existingConfigurations", meterProgrammingDao.getAllMeterPrograms());
        //TODO Get these from Pao Tag??
        model.addAttribute("availableTypes", ImmutableSet.of(PaoType.RFN430A3D, PaoType.RFN430A3T, PaoType.RFN430A3K, PaoType.RFN430A3R, PaoType.RFN530S4X,
                                                             PaoType.RFN430SL0, PaoType.RFN430SL1, PaoType.RFN430SL2, PaoType.RFN430SL3, PaoType.RFN430SL4));
    }
    
    @RequestMapping(value = "meterProgramming", method = RequestMethod.POST)
    public String postMeterProgramming(@ModelAttribute("programmingConfiguration") MeterProgrammingConfiguration programmingConfiguration, BindingResult result, 
                                       DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model,
                                       HttpServletRequest request, HttpServletResponse response) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        validator.validate(programmingConfiguration, result);

        if (result.hasErrors()) {
            return errorView(response, model, deviceCollection);
        }
        
        //Get name or upload new file
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        MeterProgram program;
        if (programmingConfiguration.isNewConfiguration()) {
            program = new MeterProgram();
            MultipartFile dataFile = null;
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {
                MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
                dataFile = mRequest.getFile("dataFile");
                try {
                    String programData = new String(dataFile.getBytes());
                    program.setProgram(programData);
                } catch (IOException e) {
                    log.warn("Exception trying to read C&I Meter Programming", e);
                    model.addAttribute("errorMsg", accessor.getMessage(baseKey + "invalidFileSelected"));
                    return errorView(response, model, deviceCollection);
                }
            } else {
                model.addAttribute("errorMsg", accessor.getMessage(baseKey + "noFileSelected"));
                return errorView(response, model, deviceCollection);
            }
            //save
            program.setName(programmingConfiguration.getName());
            program.setPaoType(programmingConfiguration.getPaoType());
            //TODO Get Guid from Save?
            meterProgrammingDao.saveMeterProgram(program);
            String guid = meterProgrammingDao.getAllMeterPrograms().get(0).getGuid();
            program.setGuid(guid);
        } else {
            program = meterProgrammingDao.getMeterProgram(programmingConfiguration.getExistingConfigurationGuid());
        }
        userInputs.put(CollectionActionInput.CONFIGURATION.name(), program.getName());
        
        int key = meterProgrammingService.initiateMeterProgramUpload(deviceCollection, program.getGuid(), userContext);
        
        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }
    
    private String errorView(HttpServletResponse response, ModelMap model, DeviceCollection deviceCollection) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        setupModel(deviceCollection, model);
        return "meterProgramming/programmingInputs.jsp";
    }

}