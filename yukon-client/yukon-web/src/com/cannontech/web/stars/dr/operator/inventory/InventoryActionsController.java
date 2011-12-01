package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryActionsController {
    
    private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    private static final int MAX_SELECTED_INVENTORY_DISPLAYED = 1000;
    private InventoryDao inventoryDao;

    /* Inventory Actions */
    @RequestMapping(value = "/operator/inventory/inventoryActions", method=RequestMethod.GET)
    public String inventoryActions(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
        
        InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        modelMap.addAttribute("inventoryCollection", yukonCollection);
        modelMap.addAllAttributes(yukonCollection.getCollectionParameters());
        
        return "operator/inventory/inventoryActions.jsp";
    }
    
    /* Inventory Collection Popup Table */
    @RequestMapping(value = "/operator/inventory/selectedInventoryTable", method=RequestMethod.GET)
    public String selectedInventoryTable(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException, CollectionCreationException {
        
        InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        int totalInventoryCount = yukonCollection.getCount();
        List<InventoryIdentifier> inventoryToLoad = yukonCollection.getSubList(0, MAX_SELECTED_INVENTORY_DISPLAYED);

        List<DisplayableLmHardware> displayableLmHardware = inventoryDao.getDisplayableLMHardware(inventoryToLoad);
        
        List<List<String>> inventoryInfoList = Lists.newArrayList();
        for (DisplayableLmHardware inventory : displayableLmHardware) {
            List<String> row = Lists.newArrayList();
            row.add(inventory.getSerialNumber());
            row.add(inventory.getDeviceType());
            row.add(inventory.getLabel());
            inventoryInfoList.add(row);
        }
        
        if (totalInventoryCount > MAX_SELECTED_INVENTORY_DISPLAYED) {
            modelMap.addAttribute("resultsLimitedTo", MAX_SELECTED_INVENTORY_DISPLAYED);
        }
        modelMap.addAttribute("inventoryInfoList", inventoryInfoList);
        
        return "operator/inventory/selectedInventoryPopup.jsp";
    }
    
    @Autowired
    public void setInventoryCollectionFactory(InventoryCollectionFactoryImpl inventoryCollectionFactory) {
        this.inventoryCollectionFactory = inventoryCollectionFactory;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
}