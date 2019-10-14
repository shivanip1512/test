package com.cannontech.web.stars.dr.operator.inventory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.InventorySearchResult;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.model.OperatorInventorySearchBy;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole({YukonRole.CONSUMER_INFO, YukonRole.INVENTORY})
@RequestMapping("/operator/inventory/*")
public class InventorySearchController {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PhoneNumberFormattingService phoneFormattingService;
    @Autowired private PaoNotesService paoNotesService;

    @RequestMapping("search")
    public String search(HttpServletRequest request,
                         ModelMap model, 
                         YukonUserContext context, 
                         @ModelAttribute InventorySearch inventorySearch, 
                         @DefaultItemsPerPage(25) PagingParameters paging,
                         FlashScope flashScope) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.INVENTORY_SEARCH, context.getYukonUser());
        YukonEnergyCompany ec = energyCompanyDao.getEnergyCompanyByOperator(context.getYukonUser()); //This may be wrong
        LiteStarsEnergyCompany liteEc = starsDatabaseCache.getEnergyCompany(ec);
        
        String searchByStr = ServletRequestUtils.getStringParameter(request, "searchBy", null);
        
        if (StringUtils.isNotBlank(searchByStr)) {
            // Search initiated from Operations.jsp (Search for existing hardware)
            OperatorInventorySearchBy searchBy = OperatorInventorySearchBy.valueOf(searchByStr);
            String searchValue = ServletRequestUtils.getStringParameter(request, "searchValue", null);
            updateInventorySearch(inventorySearch, searchBy, searchValue);
        }
        
        boolean hasWarnings = false;
        
        if (StringUtils.isNotBlank(inventorySearch.getPhoneNumber())) {
            if (phoneFormattingService.isHasInvalidCharacters(inventorySearch.getPhoneNumber())) {
                MessageSourceResolvable invalidPhoneNumberWarning =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.inventory.invalidPhoneNumber");
                flashScope.setWarning(Collections.singletonList(invalidPhoneNumberWarning));
                hasWarnings = true;
            }
        }
        
        if(!hasWarnings){
            List<EnergyCompany> descendantEcs = 
                    energyCompanyDao.getEnergyCompany(liteEc.getEnergyCompanyId()).getDescendants(true);
            List<Integer> ecDescendantIds = Lists.transform(descendantEcs, EnergyCompanyDao.TO_ID_FUNCTION);
            SearchResults<InventorySearchResult> results = inventoryDao.search(inventorySearch, ecDescendantIds, 
                    paging.getStartIndex(), paging.getItemsPerPage());

            // Redirect to inventory page if only one result is found
           if (results.getHitCount() == 1) {
                InventorySearchResult inventory = results.getResultList().get(0);
                
                if (inventory.getAccountId() > 0) {
                    model.addAttribute("inventoryId", inventory.getInventoryIdentifier().getInventoryId());
                    model.addAttribute("accountId", inventory.getAccountId());
                    return "redirect:/stars/operator/hardware/view";
                }
                else {
                    model.addAttribute("inventoryId", inventory.getInventoryIdentifier().getInventoryId());
                    return "redirect:/stars/operator/inventory/view";
                }
            }
            model.addAttribute("results", results);
            
            List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(results.getResultList()
                                                                                .stream()
                                                                                .map(result -> result.getDeviceId())
                                                                                .filter(deviceId -> deviceId != 0)
                                                                                .collect(Collectors.toList()));
            model.addAttribute("notesList", notesList);
        }
        
        model.addAttribute("showAccountNumber", StringUtils.isNotBlank(inventorySearch.getAccountNumber()));
        model.addAttribute("showPhoneNumber", StringUtils.isNotBlank(inventorySearch.getPhoneNumber()));
        model.addAttribute("showLastName", StringUtils.isNotBlank(inventorySearch.getLastName()));
        model.addAttribute("showWordOrder", StringUtils.isNotBlank(inventorySearch.getWorkOrderNumber()));
        model.addAttribute("showAltTrackingNumber", StringUtils.isNotBlank(inventorySearch.getAltTrackingNumber()));
        model.addAttribute("showEc", liteEc.hasChildEnergyCompanies());
        model.addAttribute("hasWarnings", hasWarnings);
        
        return "operator/inventory/inventoryList.jsp";
    }
    
    private void updateInventorySearch(InventorySearch inventorySearch, OperatorInventorySearchBy searchBy, 
            String searchValue){
        
        switch(searchBy){
        case serialNumber:
            inventorySearch.setSerialNumber(searchValue);
            break;
        case meterNumber:
            inventorySearch.setMeterNumber(searchValue);
            break;
        case accountNumber:
            inventorySearch.setAccountNumber(searchValue);
            break;
        case phoneNumber:
            inventorySearch.setPhoneNumber(searchValue);
            break;
        case lastName:
            inventorySearch.setLastName(searchValue);
            break;
        case workOrderNumber:
            inventorySearch.setWorkOrderNumber(searchValue);
            break;
        case altTrackingNumber:
            inventorySearch.setAltTrackingNumber(searchValue);
            break;
        }
    }
    
}
