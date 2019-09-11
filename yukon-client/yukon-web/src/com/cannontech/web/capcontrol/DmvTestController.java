package com.cannontech.web.capcontrol;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.dao.DmvTestDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.db.capcontrol.DmvTest;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.validators.DmvTestValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparmLicense;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
@CheckCparmLicense(license = MasterConfigLicenseKey.DEMAND_MEASUREMENT_VERIFICATION_ENABLED)
public class DmvTestController {
    private static Logger log = YukonLogManager.getLogger(DmvTestController.class);
    private static final String baseKey = "yukon.web.modules.capcontrol.dmvTest";

    @Autowired DmvTestDao dmvTestDao;
    @Autowired DmvTestValidator validator;
    
    @RequestMapping(value="dmvTestList", method=RequestMethod.GET)
    public String dmvTestList(ModelMap model) {
        
        List<DmvTest> dmvTestList = dmvTestDao.getAllDmvTest();
        
        model.addAttribute("dmvTestList", dmvTestList);
        
        return "dmvtest/dmvTestList.jsp";
    }
    
    @RequestMapping(value="dmvTest/create", method=RequestMethod.GET)
    public String create(ModelMap model) {
        
        DmvTest dmvTest = new DmvTest();
        
        model.addAttribute("mode",  PageEditMode.CREATE);
        
        return setUpModel(model, dmvTest);
    }

    @RequestMapping(value="dmvTest/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        
        DmvTest dmvTest = dmvTestDao.getDmvTestById(id); 
        
        model.addAttribute("mode", PageEditMode.VIEW);
        
        return setUpModel(model, dmvTest);
    }
    
    @RequestMapping(value="dmvTest/{id}/edit", method=RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id) {
        
        DmvTest dmvTest = dmvTestDao.getDmvTestById(id); 
        
        model.addAttribute("mode", PageEditMode.EDIT);
        
        return setUpModel(model, dmvTest);
    }
    
    private String setUpModel(ModelMap model, DmvTest dmvTest) {
        if (!model.containsAttribute("dmvTest")) {
            model.addAttribute("dmvTest", dmvTest);   
        }
        return "dmvtest/dmvTest.jsp";    
        
    }
    
    @RequestMapping(value={"dmvTest"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(@ModelAttribute("dmvTest") DmvTest dmvTest,
            BindingResult result, FlashScope flash,
            RedirectAttributes redirectAttributes) {
          
        validator.validate(dmvTest, result);
        
        if (result.hasErrors()) {
            return bindAndForward(dmvTest, result, redirectAttributes);
        }
        int id = 0;
        try {
            id = dmvTestDao.updateDmvTest(dmvTest);
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".updateFailed"));
            log.error("Error saving dmvTest: " + e.getMessage());
            return bindAndForward(dmvTest, result, redirectAttributes);
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".saved"));
        
        return "redirect:dmvTest/" + id;
    }
    
    private String bindAndForward(DmvTest dmvTest, BindingResult result, RedirectAttributes attrs) {
        
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.dmvTest", result);
        attrs.addFlashAttribute("dmvTest", dmvTest);
        
        if (dmvTest.getDmvTestId() == 0) {
            return "redirect:dmvTest/create";
        }
        return "redirect:dmvTest/" + dmvTest.getDmvTestId() + "/edit";
    }
    
    @RequestMapping(value="dmvTest/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(@PathVariable int id, FlashScope flash) {
        boolean success = dmvTestDao.delete(id);
        if (!success) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".deleteFailed"));
            return "redirect:dmvTest/" + id;
        }
        return "redirect:/capcontrol/dmvTestList";
    }
}
