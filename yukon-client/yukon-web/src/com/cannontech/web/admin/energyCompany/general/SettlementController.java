package com.cannontech.web.admin.energyCompany.general;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.dr.settlement.model.AvailableRate;
import com.cannontech.stars.dr.settlement.model.SettlementDto;
import com.cannontech.stars.dr.settlement.service.SettlementService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@CheckCparm(MasterConfigBooleanKeysEnum.ENABLE_SETTLEMENTS)
@RequestMapping("/energyCompany/settlement/*")
public class SettlementController { 
    @Autowired private SettlementService settlementService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private SelectionListService selectionListService;
    
    @RequestMapping("list")
    public String list(ModelMap modelMap, int ecId,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        
        YukonSelectionList settlementTypes = 
            yukonListDao.findSelectionListByEnergyCompanyIdAndListName(energyCompanyInfoFragment.getEnergyCompanyId(), 
                                                                       YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
        
        modelMap.addAttribute("settlementTypes", settlementTypes);
        
        return "energyCompany/settlement/settlementList.jsp";
    }
    
    @RequestMapping(value="edit", method=RequestMethod.GET)
    public String edit(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("mode", PageEditMode.EDIT);

        SettlementDto settlementDto = getSettlementDto(energyCompanyInfoFragment);
        modelMap.addAttribute("settlementDto", settlementDto);
        
        String settlementName = getSettlementName(energyCompanyInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("settlementName", settlementName);
        
        return "energyCompany/settlement/settlementEdit.jsp";
    }
 
    @RequestMapping(value="edit", params="save", method=RequestMethod.POST)
    public String save(ModelMap modelMap, FlashScope flashScope,
                       SettlementDto settlementDto, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        
        settlementService.saveSettlementDto(settlementDto, energyCompanyInfoFragment.getEnergyCompanyId());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.settlements.settlementSaved"));
        
        return "redirect:list";
    }
    
    @RequestMapping(value="edit", params="cancel", method=RequestMethod.POST)
    public String cancel(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        return "redirect:list";
    }

    /**
     * This method builds up a settlementDto object that is used to represent a settlement.
     */
    private SettlementDto getSettlementDto(EnergyCompanyInfoFragment energyCompanyInfoFragment){
        
        SettlementDto settlementDto = new SettlementDto();
        
        // Get configuration settings
        List<LiteSettlementConfig> editableLiteSettlementConfigs = settlementService.getEditableConfigs();
        settlementDto.setEditableLiteSettlementConfigs(editableLiteSettlementConfigs);
        
        // Get Available Rates
        List<AvailableRate> availableRates = settlementService.getAvailableRates(energyCompanyInfoFragment.getEnergyCompanyId());
        settlementDto.setAvailableRates(availableRates);
        
        return settlementDto;
    }

    /** 
     * This method returns the settlement name using the nergy company id and yukon def it.  If it cannot find the
     * settlement name, it will return null.
     */
    private String getSettlementName(int energyCompanyId){
        YukonEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        YukonSelectionList yukonSelectionList = selectionListService.getSelectionList(energyCompany, 
                                                      YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
        
        List<YukonListEntry> settlementNames = yukonSelectionList.getYukonListEntries();
        for (YukonListEntry yukonListEntry : settlementNames) {
            return yukonListEntry.getEntryText();
        }
        
        return null;
    }
}


