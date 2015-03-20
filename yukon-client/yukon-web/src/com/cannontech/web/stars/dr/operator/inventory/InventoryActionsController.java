package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.google.common.collect.Lists;

@RequestMapping("/operator/inventory/*")
@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryActionsController {
    
    @Autowired private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private static final int maxInventory = 1000;
    private static final String view = "operator/inventory/";
    
    /* Inventory Actions */
    @RequestMapping("inventoryActions")
    public String inventoryActions(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
        
        InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage("yukon.common.device.bulk.bulkAction.collection.idList");
        
        InventoryCollection memoryCollection = memoryCollectionProducer.createCollection(yukonCollection.iterator(), displayHint);
        
        modelMap.addAttribute("inventoryCollection", memoryCollection);
        modelMap.addAllAttributes(memoryCollection.getCollectionParameters());
        
        boolean digiEnabled = configSource.getBoolean(MasterConfigBooleanKeysEnum.DIGI_ENABLED);
        modelMap.addAttribute("digiEnabled", digiEnabled);
        
        boolean showSaveToFile = configSource.getBoolean(MasterConfigBooleanKeysEnum.ENABLE_INVENTORY_SAVE_TO_FILE);
        modelMap.addAttribute("showSaveToFile", showSaveToFile);
        
        return view + "actions.jsp";
    }
    
    /* Inventory Configuration */
    @RequestMapping("inventoryConfiguration")
    public String inventoryConfiguration(HttpServletRequest request, ModelMap model,
                                         YukonUserContext userContext) throws ServletRequestBindingException {
        LiteYukonUser user = userContext.getYukonUser();
        rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_RECONFIG, user);
        
        boolean showNewConfig =  configSource.getBoolean(MasterConfigBooleanKeysEnum.SEND_INDIVIDUAL_SWITCH_CONFIG, false);
        model.addAttribute("showNewConfig", showNewConfig);
        
        InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage("yukon.common.device.bulk.bulkAction.collection.idList");
        
        InventoryCollection memoryCollection = memoryCollectionProducer.createCollection(yukonCollection.iterator(), displayHint);
        
        model.addAttribute("inventoryCollection", memoryCollection);
        model.addAllAttributes(memoryCollection.getCollectionParameters());
        
        return view + "inventoryConfiguration.jsp";
    }
    
    /* Inventory Collection Popup Table */
    @RequestMapping("selectedInventoryTable")
    public String selectedInventoryTable(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException, CollectionCreationException {
        
        InventoryCollection yukonCollection = inventoryCollectionFactory.createCollection(request);
        int totalInventoryCount = yukonCollection.getCount();
        List<InventoryIdentifier> inventoryToLoad = yukonCollection.getSubList(0, maxInventory);

        List<DisplayableLmHardware> displayableLmHardware = inventoryDao.getDisplayableLMHardware(inventoryToLoad);
        
        List<List<String>> inventoryInfoList = Lists.newArrayList();
        for (DisplayableLmHardware inventory : displayableLmHardware) {
            List<String> row = Lists.newArrayList();
            row.add(inventory.getSerialNumber());
            row.add(inventory.getDeviceType());
            row.add(inventory.getLabel());
            inventoryInfoList.add(row);
        }
        
        if (totalInventoryCount > maxInventory) {
            modelMap.addAttribute("resultsLimitedTo", maxInventory);
        }
        modelMap.addAttribute("inventoryInfoList", inventoryInfoList);
        
        return view + "selectedInventoryPopup.jsp";
    }
    
}