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
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.google.common.collect.Lists;

@RequestMapping("/operator/inventory/*")
@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryActionsController {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private static final int MAX_SELECTED_INVENTORY_DISPLAYED = 1000;
    private static final String JSP_DIRECTORY = "operator/inventory/";

    /* Inventory Actions */
    @RequestMapping ("inventoryActions")
    public String inventoryActions(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
        
        InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage("yukon.common.device.bulk.bulkAction.collection.idList");
        
        InventoryCollection memoryCollection = memoryCollectionProducer.createCollection(yukonCollection.iterator(), displayHint);
        
        modelMap.addAttribute("inventoryCollection", memoryCollection);
        modelMap.addAllAttributes(memoryCollection.getCollectionParameters());
        
        boolean digiEnabled = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DIGI_ENABLED);
        modelMap.addAttribute("digiEnabled", digiEnabled);
        
        boolean showSaveToFile = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.ENABLE_INVENTORY_SAVE_TO_FILE);
        modelMap.addAttribute("showSaveToFile", showSaveToFile);
        
        return JSP_DIRECTORY + "inventoryActions.jsp";
    }
    
    /* Inventory Configuration */
    @RequestMapping ("inventoryConfiguration")
    public String inventoryConfiguration(HttpServletRequest request,
    									 ModelMap modelMap,
    									 YukonUserContext userContext) throws ServletRequestBindingException {
    	//secure this action
    	rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_RECONFIG, userContext.getYukonUser());
    	
    	InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage("yukon.common.device.bulk.bulkAction.collection.idList");
        
        InventoryCollection memoryCollection = memoryCollectionProducer.createCollection(yukonCollection.iterator(), displayHint);
        
        modelMap.addAttribute("inventoryCollection", memoryCollection);
        modelMap.addAllAttributes(memoryCollection.getCollectionParameters());
    	return JSP_DIRECTORY + "inventoryConfiguration.jsp";
    }
    
    /* Inventory Collection Popup Table */
    @RequestMapping ("selectedInventoryTable")
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
        
        return JSP_DIRECTORY + "selectedInventoryPopup.jsp";
    }
    
}