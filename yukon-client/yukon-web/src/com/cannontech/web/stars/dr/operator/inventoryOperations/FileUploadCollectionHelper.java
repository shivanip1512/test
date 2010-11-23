package com.cannontech.web.stars.dr.operator.inventoryOperations;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class FileUploadCollectionHelper {
    
    private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    
    @RequestMapping(value = "/operator/inventory/inventoryOperations/uploadFile")
    public String fileUploadHelper(HttpServletRequest request, ModelMap modelMap) throws ServletRequestBindingException {
        InventoryCollection inventoryCollection = inventoryCollectionFactory.createCollection(request);
        modelMap.addAllAttributes(inventoryCollection.getCollectionParameters());
        
        /* TODO Error handling */
        
        return "redirect:inventoryActions";
    }

    @Autowired
    public void setInventoryCollectionFactory(InventoryCollectionFactoryImpl inventoryCollectionFactory) {
        this.inventoryCollectionFactory = inventoryCollectionFactory;
    }
    
}