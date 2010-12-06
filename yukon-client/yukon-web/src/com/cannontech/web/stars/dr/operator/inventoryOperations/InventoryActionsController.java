package com.cannontech.web.stars.dr.operator.inventoryOperations;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.YukonCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryActionsController {
    
    private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    private static final int MAX_SELECTED_INVENTORY_DISPLAYED = 1000;
    private InventoryDao inventoryDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    /* Inventory Actions */
    @RequestMapping(value = "/operator/inventory/inventoryOperations/inventoryActions", method=RequestMethod.GET)
    public String inventoryActions(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
        
        YukonCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        modelMap.addAttribute("inventoryCollection", yukonCollection);
        modelMap.addAllAttributes(yukonCollection.getCollectionParameters());
        
        return "operator/inventory/inventoryOperations/inventoryActions.jsp";
    }
    
    /* Inventory Collection Popup Table */
    @RequestMapping(value = "/operator/inventory/inventoryOperations/selectedInventoryTable", method=RequestMethod.GET)
    public String selectedInventoryTable(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException, CollectionCreationException {
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String serialNumber = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventoryActions.serialNumber");
        String hardwareType = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventoryActions.hardwareType");
        String label = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventoryActions.label");
        
        YukonCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        int totalInventoryCount = (int)yukonCollection.getCount();
        List<InventoryIdentifier> inventoryToLoad = yukonCollection.getSubList(0, MAX_SELECTED_INVENTORY_DISPLAYED);

        List<DisplayableLmHardware> displayableLmHardware = inventoryDao.getDisplayableLMHardware(inventoryToLoad);
        
        List<Map<String, Object>> inventoryInfoList = new ArrayList<Map<String, Object>>();
        for (DisplayableLmHardware inventory : displayableLmHardware) {
            
            Map<String, Object> inventoryInfo = new LinkedHashMap<String, Object>();
            
            inventoryInfo.put(serialNumber, inventory.getSerialNumber());
            inventoryInfo.put(hardwareType, inventory.getDeviceType());
            inventoryInfo.put(label, inventory.getLabel());
            
            inventoryInfoList.add(inventoryInfo);
        }
        
        if (totalInventoryCount > MAX_SELECTED_INVENTORY_DISPLAYED) {
            modelMap.addAttribute("resultsLimitedTo", MAX_SELECTED_INVENTORY_DISPLAYED);
        }
        modelMap.addAttribute("inventoryInfoList", inventoryInfoList);
        
        return "operator/inventory/inventoryOperations/selectedInventoryPopup.jsp";
    }
    
    @Autowired
    public void setInventoryCollectionFactory(InventoryCollectionFactoryImpl inventoryCollectionFactory) {
        this.inventoryCollectionFactory = inventoryCollectionFactory;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
}