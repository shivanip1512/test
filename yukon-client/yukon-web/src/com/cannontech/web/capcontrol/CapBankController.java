package com.cannontech.web.capcontrol;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.CapBankAntennaType;
import com.cannontech.capcontrol.CapBankCommunicationMedium;
import com.cannontech.capcontrol.CapBankConfig;
import com.cannontech.capcontrol.CapBankPotentialTransformer;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.CapBankService;
import com.cannontech.web.capcontrol.validators.CapBankValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapBankController {

    @Autowired private PointDao pointDao;
    @Autowired private CapBankService capbankService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CapControlCache ccCache;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private CapBankValidator capBankValidator;

    private static final String baseKey = "yukon.web.modules.capcontrol.capbank";


    @RequestMapping(value = "capbanks/{id}", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {

        CapBank capbank = capbankService.getCapBank(id);

        boolean canEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        PageEditMode mode = canEdit ? PageEditMode.EDIT : PageEditMode.VIEW;
        model.addAttribute("mode", mode);

        return setUpModel(model, capbank, userContext);
    }

    @RequestMapping(value = "capbanks/create", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, YukonUserContext userContext) {
        CapBank capbank = new CapBank();
        model.addAttribute("mode", PageEditMode.CREATE);
        return setUpModel(model, capbank, userContext);
    }

    @RequestMapping(value="capbanks", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("capbank") CapBank capbank, 
            BindingResult result, 
            RedirectAttributes redirectAttributes,
            FlashScope flash) {

        capBankValidator.validate(capbank, result);

        if (result.hasErrors()) {
            return bindAndForward(capbank, result, redirectAttributes);
        }
        capbankService.save(capbank);

        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".updated"));
        return "redirect:/capcontrol/capbanks/" + capbank.getId();
    }

    @RequestMapping(value="capbanks/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(ModelMap model, @PathVariable int id, FlashScope flash, YukonUserContext userContext) {
        
        CapBank capbank = capbankService.getCapBank(id);
        
        try {
            Integer parentId = ccCache.getParentFeederId(id);
            capbankService.delete(id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".delete.success", capbank.getName()));

            if (parentId != null && parentId > 0) {
                return "redirect:/capcontrol/feeders/" + parentId;
            }
        } catch (NotFoundException e ){
            capbankService.delete(id);
            //do nothing and return to orphan page
        }
        
        return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oBanks__";

    }

    private String setUpModel(ModelMap model, CapBank capbank, YukonUserContext userContext) {
        
        Object modelCapBank = model.get("capbank");
        if (modelCapBank instanceof CapBank) {
            capbank = (CapBank) modelCapBank;
        }
        model.addAttribute("capbank", capbank);
        model.addAttribute("orphan", true);

        
        if(capbank.getId() != null) {

            int capBankId = capbank.getId();
            
            model.addAttribute("capbankId", capBankId);
            model.addAttribute("capbankName", capbank.getName());
            
            int parentId = 0;
            try {
                parentId = ccCache.getParentFeederId(capBankId);
            } catch (NotFoundException e){
                model.addAttribute("orphan", true);
            }
            
            if(parentId > 0) {
                LiteYukonPAObject parent = dbCache.getAllPaosMap().get(parentId);
                model.addAttribute("orphan", false);
                model.addAttribute("parent", parent);

                
                SubStation substation = ccCache.getParentSubstation(capBankId);
                model.addAttribute("substationId", substation.getCcId());
                model.addAttribute("substationName", substation.getCcName());

                int areaId = ccCache.getParentAreaId(capBankId);
                Area area = ccCache.getArea(areaId);

                model.addAttribute("areaId", area.getCcId());
                model.addAttribute("areaName", area.getCcName());
                
                int busId = ccCache.getParentSubBusId(capBankId);
                SubBus bus = ccCache.getSubBus(busId);

                model.addAttribute("busId", bus.getCcId());
                model.addAttribute("busName", bus.getCcName());
                
                int feederId = ccCache.getParentFeederId(capBankId);
                Feeder feeder = ccCache.getFeeder(feederId);

                model.addAttribute("feederId", feeder.getCcId());
                model.addAttribute("feederName", feeder.getCcName());
            }

            
            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(capBankId);
            model.addAttribute("points", points);

        }
        
        //Cap Bank Enums
        model.addAttribute("configList", CapBankConfig.values());
        model.addAttribute("potentialTransformerList", CapBankPotentialTransformer.values());
        model.addAttribute("communicationMediumList", CapBankCommunicationMedium.values());
        model.addAttribute("antennaTypeList", CapBankAntennaType.values());
        
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        int ecId = energyCompany.getId();
        model.addAttribute("energyCompanyId", ecId);

        return "capBank.jsp";
    }
    
    private String bindAndForward(CapBank capbank, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("capBank", capbank);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.capbank", result);

        if (capbank.getId() == null) {
            return "redirect:capbanks/create";
        }

        return "redirect:capbanks/" + capbank.getId();
    }

}