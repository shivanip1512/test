package com.cannontech.web.capcontrol;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.CapBankAntennaType;
import com.cannontech.capcontrol.CapBankCommunicationMedium;
import com.cannontech.capcontrol.CapBankConfig;
import com.cannontech.capcontrol.CapBankPointPhase;
import com.cannontech.capcontrol.CapBankPotentialTransformer;
import com.cannontech.capcontrol.CapBankSize;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.CapBankService;
import com.cannontech.web.capcontrol.service.impl.CbcServiceImpl;
import com.cannontech.web.capcontrol.validators.CapBankValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapBankController {

    @Autowired private PointDao pointDao;
    @Autowired private CapBankService capbankService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CapControlCache ccCache;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private CapBankValidator capBankValidator;
    @Autowired private CbcServiceImpl cbcService;
    @Autowired private PaoDao paoDao;
    @Autowired private CapbankDao capbankDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private static final String baseKey = "yukon.web.modules.capcontrol.capbank";

    @RequestMapping(value = "capbanks/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        CapBank capbank = capbankService.getCapBank(id);
        model.addAttribute("mode", PageEditMode.VIEW);
        return setUpModel(model, capbank, userContext);
    }
    
    @RequestMapping(value = "capbanks/{id}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id, YukonUserContext userContext) {
        CapBank capbank = capbankService.getCapBank(id);
        model.addAttribute("mode", PageEditMode.EDIT);
        return setUpModel(model, capbank, userContext);
    }

    @RequestMapping(value = "capbanks/create", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        CapBank capbank = new CapBank();
        model.addAttribute("mode", PageEditMode.CREATE);
        //check for parentId to assign to
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            LiteYukonPAObject parent = dbCache.getAllPaosMap().get(Integer.parseInt(parentId));
            model.addAttribute("parent", parent);
        }
        return setUpModel(model, capbank, userContext);
    }

    @RequestMapping(value="capbanks", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("capbank") CapBank capbank, 
            BindingResult result, 
            RedirectAttributes redirectAttributes,
            FlashScope flash, HttpServletRequest request) {

        capBankValidator.validate(capbank, result);

        if (result.hasErrors()) {
            redirectAttributes.addAttribute("parentId", request.getParameter("parentId"));
            return bindAndForward(capbank, result, redirectAttributes);
        }
        int id = capbankService.save(capbank);
        
        //assign to parent if parentId is there
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            capbankDao.assignCapbank(Integer.parseInt(parentId), id);
        }

        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".saved"));
        return "redirect:/capcontrol/capbanks/" + capbank.getId();
    }

    @RequestMapping(value="capbanks/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(ModelMap model, @PathVariable int id, FlashScope flash, YukonUserContext userContext) {
        
        CapBank capbank = capbankService.getCapBank(id);
        
        try {
            Integer parentId = capbankDao.getParentFeederIdentifier(id).getPaoId();
            capbankService.delete(id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".delete.success", capbank.getName()));

            if (parentId != null && parentId > 0) {
                return "redirect:/capcontrol/feeders/" + parentId;
            }
        } catch (EmptyResultDataAccessException e) {
            capbankService.delete(id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".delete.success", capbank.getName()));
            // do nothing and return to orphan page
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
                parentId = capbankDao.getParentFeederIdentifier(capBankId).getPaoId();

                LiteYukonPAObject parent = dbCache.getAllPaosMap().get(parentId);
                model.addAttribute("orphan", false);
                model.addAttribute("parent", parent);

                SubStation substation = ccCache.getParentSubstation(capBankId);
                model.addAttribute("substationId", substation.getCcId());
                model.addAttribute("substationName", substation.getCcName());

                int areaId = ccCache.getParentAreaId(capBankId);
                LiteYukonPAObject area = dbCache.getAllPaosMap().get(areaId);

                model.addAttribute("areaId", area.getLiteID());
                model.addAttribute("areaName", area.getPaoName());
                
                int busId = ccCache.getParentSubBusId(capBankId);
                SubBus bus = ccCache.getSubBus(busId);

                model.addAttribute("busId", bus.getCcId());
                model.addAttribute("busName", bus.getCcName());
                
                int feederId = ccCache.getParentFeederId(capBankId);
                Feeder feeder = ccCache.getFeeder(feederId);
                
                capbank.setOverrideFeederLimitsSupported(feeder.isOverrideFeederLimitsSupported());

                model.addAttribute("feederId", feeder.getCcId());
                model.addAttribute("feederName", feeder.getCcName());
            } catch (EmptyResultDataAccessException | NotFoundException e) {
                model.addAttribute("orphan", true);
            }
            
            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(capBankId);
            model.addAttribute("points", points);

        }
        
        //CBC Controller
        if(capbank.getCapBank().getControlDeviceID() != 0){
            CapControlCBC cbc = cbcService.getCbc(capbank.getCapBank().getControlDeviceID());
            model.addAttribute("cbc", cbc);
            model.addAttribute("controllerRouteName", paoDao.getYukonPAOName(cbc.getDeviceCBC().getRouteID()));
            model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        }

        //Cap Bank Enums
        model.addAttribute("configList", CapBankConfig.values());
        model.addAttribute("potentialTransformerList", CapBankPotentialTransformer.values());
        model.addAttribute("communicationMediumList", CapBankCommunicationMedium.values());
        model.addAttribute("antennaTypeList", CapBankAntennaType.values());
        model.addAttribute("pointPhaseList", CapBankPointPhase.values());

        Set<TimeIntervals> timeInterval = TimeIntervals.getCapControlIntervals();
        List<TimeIntervals> timeIntervals = new LinkedList<>(timeInterval);
        timeIntervals.add(0, TimeIntervals.NONE);

        model.addAttribute("timeIntervals", timeIntervals);
        model.addAttribute("opMethods", BankOpState.values());
        model.addAttribute("bankSizes", CapBankSize.values());
        
        List<PaoType> cbcTypes =
            PaoType.getCbcTypes().stream().filter(cbcType -> paoDefinitionDao.getPaoDefinition(cbcType).isCreatable() && cbcType != PaoType.CBC_LOGICAL).collect(
                Collectors.toList());
        
        model.addAttribute("cbcTypes", cbcTypes);
        model.addAttribute("availablePorts", dbCache.getAllPorts());
        model.addAttribute("twoWayTypes", CapControlCBC.getTwoWayTypes());
        model.addAttribute("logicalTypes", CapControlCBC.getLogicalTypes());
        
        Set<Integer> tcpPorts = dbCache.getAllPorts().stream()
                .filter(new Predicate<LiteYukonPAObject> () {

                    @Override
                    public boolean test(LiteYukonPAObject port) {
                        return port.getPaoType() == PaoType.TCPPORT;
                    }

                }).map(new Function<LiteYukonPAObject, Integer>(){

                    @Override
                    public Integer apply(LiteYukonPAObject port) {
                        return port.getLiteID();
                    }

                }).collect(Collectors.toSet());

            model.addAttribute("tcpCommPorts", tcpPorts);
        
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        int ecId = energyCompany.getId();
        model.addAttribute("energyCompanyId", ecId);
        
        capbankService.setAssignedPoints(capbank);
        model.addAttribute("unassignedPoints", capbankService.getUnassignedPoints(capbank));
        
        return "capBank.jsp";
    }
    
    @RequestMapping("capbanks/{capbankId}/points/edit")
    public String editPoints(ModelMap model, @PathVariable int capbankId) {

        CapBank capbank = capbankService.getCapBank(capbankId);
        
        List<CCMonitorBankList> unassigned = capbankService.getUnassignedPoints(capbank);
        
        capbankService.setAssignedPoints(capbank);
        List<CCMonitorBankList> assigned = capbank.getCcMonitorBankList();
    
        model.addAttribute("assigned", assigned);
        model.addAttribute("unassigned", unassigned);

        return "assignment-popup.jsp";
    }
    
    @RequestMapping(value="capbanks/{capbankId}/points", method=RequestMethod.POST)
    public void savePoints(HttpServletResponse resp, @PathVariable int capbankId, FlashScope flash,
            @RequestParam(value="children[]", required=false, defaultValue="") Integer[] pointIds) {
        capbankService.savePoints(capbankId, pointIds);
        
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".points.updated"));

        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    private String bindAndForward(CapBank capbank, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("capbank", capbank);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.capbank", result);

        if (capbank.getId() == null) {
            return "redirect:capbanks/create";
        }

        return "redirect:capbanks/" + capbank.getId() + "/edit";
    }


}