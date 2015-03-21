package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
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
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.google.common.collect.Lists;

@RequestMapping("/operator/inventory/*")
@Controller
@CheckRole(YukonRole.INVENTORY)
public class InventoryActionsController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private static final int maxInventory = 1000;
    private static final String idListKey = "yukon.common.device.bulk.bulkAction.collection.idList";
    private static final String view = "operator/inventory/";
    
    /* Inventory Actions */
    @RequestMapping("inventoryActions")
    public String inventoryActions(HttpServletRequest req, ModelMap model, YukonUserContext userContext) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage(idListKey);
        
        InventoryCollection memoryCollection = collectionProducer.createCollection(collection.iterator(), displayHint);
        
        model.addAttribute("inventoryCollection", memoryCollection);
        model.addAllAttributes(memoryCollection.getCollectionParameters());
        
        boolean digiEnabled = configSource.getBoolean(MasterConfigBoolean.DIGI_ENABLED);
        model.addAttribute("digiEnabled", digiEnabled);
        
        boolean showSaveToFile = configSource.getBoolean(MasterConfigBoolean.ENABLE_INVENTORY_SAVE_TO_FILE);
        model.addAttribute("showSaveToFile", showSaveToFile);
        
        return view + "actions.jsp";
    }
    
    /* Inventory Configuration */
    @RequestMapping("inventoryConfiguration")
    public String inventoryConfiguration(HttpServletRequest req, ModelMap model, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_RECONFIG, user);
        
        boolean showNewConfig =  configSource.getBoolean(MasterConfigBoolean.SEND_INDIVIDUAL_SWITCH_CONFIG, false);
        model.addAttribute("showNewConfig", showNewConfig);
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String displayHint = accessor.getMessage(idListKey);
        
        InventoryCollection memoryCollection = collectionProducer.createCollection(collection.iterator(), displayHint);
        
        model.addAttribute("inventoryCollection", memoryCollection);
        model.addAllAttributes(memoryCollection.getCollectionParameters());
        
        return view + "inventoryConfiguration.jsp";
    }
    
    /* Inventory Collection Popup Table */
    @RequestMapping("selectedInventoryTable")
    public String selectedInventoryTable(HttpServletRequest req, ModelMap model) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        int totalInventoryCount = collection.getCount();
        List<InventoryIdentifier> inventoryToLoad = collection.getSubList(0, maxInventory);
        
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
            model.addAttribute("resultsLimitedTo", maxInventory);
        }
        model.addAttribute("inventoryInfoList", inventoryInfoList);
        
        return view + "selectedInventoryPopup.jsp";
    }
    
}