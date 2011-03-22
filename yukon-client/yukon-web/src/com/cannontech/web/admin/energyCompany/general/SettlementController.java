package com.cannontech.web.admin.energyCompany.general;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.settlement.model.AvailableRate;
import com.cannontech.stars.dr.settlement.model.SettlementDto;
import com.cannontech.stars.dr.settlement.service.SettlementService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/energyCompany/settlement/*")
public class SettlementController { 
    
    private SettlementService settlementService;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonListDao yukonListDao;
    
    @RequestMapping
    public String list(YukonUserContext userContext, ModelMap modelMap, int ecId,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        YukonSelectionList settlementTypes = 
            yukonListDao.findSelectionListByEnergyCompanyIdAndListName(energyCompanyInfoFragment.getEnergyCompanyId(), 
                                                                       YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
        
        modelMap.addAttribute("settlementTypes", settlementTypes);
        
        return "energyCompany/settlement/settlementList.jsp";
    }
    
    @RequestMapping(value="edit", method=RequestMethod.GET)
    public String edit(YukonUserContext userContext, ModelMap modelMap, Integer settlementYukonDefId,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        modelMap.addAttribute("settlementYukonDefId", settlementYukonDefId);
        modelMap.addAttribute("mode", PageEditMode.EDIT);

        SettlementDto settlementDto = getSettlementDto(energyCompanyInfoFragment, settlementYukonDefId);
        modelMap.addAttribute("settlementDto", settlementDto);
        
        String settlementName = getSettlementName(energyCompanyInfoFragment.getEnergyCompanyId(), settlementYukonDefId);
        modelMap.addAttribute("settlementName", settlementName);
        
        return "energyCompany/settlement/settlementEdit.jsp";
    }
 
    @RequestMapping(value="edit", params="save", method=RequestMethod.POST)
    public String save(YukonUserContext userContext, ModelMap modelMap, Integer settlementYukonDefId, FlashScope flashScope,
                       SettlementDto settlementDto, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        
        settlementService.saveSettlementDto(settlementDto, energyCompanyInfoFragment.getEnergyCompanyId(), settlementYukonDefId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.settlement.settlementSaved"));
        
        return "redirect:list";
    }
    
    @RequestMapping(value="edit", params="cancel", method=RequestMethod.POST)
    public String cancel(YukonUserContext userContext, ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);

        return "redirect:list";
    }

    /**
     * This method builds up a settlementDto object that is used to represent a settlement.
     */
    private SettlementDto getSettlementDto(EnergyCompanyInfoFragment energyCompanyInfoFragment, int settlementYukonDefId){
        
        SettlementDto settlementDto = new SettlementDto();
        
        // Get configuration settings
        List<LiteSettlementConfig> editableLiteSettlementConfigs = settlementService.getEditableConfigs(settlementYukonDefId);
        settlementDto.setEditableLiteSettlementConfigs(editableLiteSettlementConfigs);
        
        // Get Available Rates
        List<AvailableRate> availableRates = settlementService.getAvailableRates(energyCompanyInfoFragment.getEnergyCompanyId(), settlementYukonDefId);
        settlementDto.setAvailableRates(availableRates);
        
        return settlementDto;
    }

    /** 
     * This method returns the settlement name using the nergy company id and yukon def it.  If it cannot find the
     * settlement name, it will return null.
     */
    private String getSettlementName(int energyCompanyId, int settlementYukonDefId){
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        YukonSelectionList yukonSelectionList = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
        
        List<YukonListEntry> settlementNames = yukonSelectionList.getYukonListEntries();
        for (YukonListEntry yukonListEntry : settlementNames) {
            if (yukonListEntry.getYukonDefID() == settlementYukonDefId) {
                return yukonListEntry.getEntryText();
            }
        }
        
        return null;
    }
    
    /* DI Setters */
    @Autowired
    public void setSettlementService(SettlementService settlementService) {
        this.settlementService = settlementService;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
}


