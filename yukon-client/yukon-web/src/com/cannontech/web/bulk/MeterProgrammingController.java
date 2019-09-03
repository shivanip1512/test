package com.cannontech.web.bulk;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.service.MeterProgrammingService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/meterProgramming/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class MeterProgrammingController {
    private final static Logger log = YukonLogManager.getLogger(MeterProgrammingController.class);
    private final static String baseKey = "yukon.web.modules.tools.bulk.meterProgramming.";

    @Autowired protected CollectionActionService collectionActionService;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
    @Autowired private MeterProgrammingService meterProgrammingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private MeterProgrammingModelValidator validator;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @GetMapping("inputs")
    public String meterProgrammingInputs(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        setupModel(deviceCollection, model);
        model.addAttribute("programModel", new MeterProgrammingModel());
        return "meterProgramming/programmingInputs.jsp";
    }
    
    private void setupModel(DeviceCollection deviceCollection, ModelMap model) {
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("existingConfigurations", meterProgrammingDao.getAllMeterPrograms());
        List<PaoType> programmableTypes = paoDefinitionDao.getPaosThatSupportTag(PaoTag.METER_PROGRAMMING).stream().map(t -> t.getType())
                                                                                    .sorted(Comparator.naturalOrder())
                                                                                    .collect(Collectors.toList());
        model.addAttribute("availableTypes", programmableTypes);
    }
    
    @PostMapping("send")
    public String send(@ModelAttribute("programModel") MeterProgrammingModel programModel, BindingResult result, 
                                       DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model,
                                       HttpServletRequest request, HttpServletResponse response) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        validator.validate(programModel, result);

        if (result.hasErrors()) {
            return errorView(response, model, deviceCollection);
        }
        
        //Get name or upload new file
        MeterProgram program;
        if (programModel.isNewProgram()) {
            program = new MeterProgram();
            MultipartFile dataFile = null;
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {
                MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
                dataFile = mRequest.getFile("dataFile");
                try {
                    program.setProgram(dataFile.getBytes());
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
            program.setName(programModel.getName());
            program.setPaoType(programModel.getPaoType());
            try {
                String guid = meterProgrammingDao.saveMeterProgram(program);
                program.setGuid(guid);
            } catch (DuplicateException e) {
                model.addAttribute("errorMsg", accessor.getMessage(baseKey + "duplicateName"));
                return errorView(response, model, deviceCollection);
            }
        } else {
            program = meterProgrammingDao.getMeterProgram(programModel.getExistingProgramGuid());
        }
        
        int key = meterProgrammingService.initiateMeterProgramUpload(deviceCollection, program.getGuid(), userContext);
        
        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }
    
    private String errorView(HttpServletResponse response, ModelMap model, DeviceCollection deviceCollection) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        setupModel(deviceCollection, model);
        return "meterProgramming/programmingInputs.jsp";
    }

}