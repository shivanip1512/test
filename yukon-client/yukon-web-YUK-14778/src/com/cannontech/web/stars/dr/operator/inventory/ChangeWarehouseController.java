package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeWarehouseHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeWarehouseHelper.ChangeWarehouseTask;

@Controller
@RequestMapping("/operator/inventory/changeWarehouse/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ChangeWarehouseController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private ChangeWarehouseHelper helper;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lsec = starsDbCache.getEnergyCompany(ec);
        
        List<Warehouse> warehouses = lsec.getWarehouses();
        model.addAttribute("warehouses", warehouses);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "operator/inventory/changeWarehouse.jsp";
    }
    
    @RequestMapping("{taskId}/status")
    public String status(ModelMap model, @PathVariable String taskId, LiteYukonUser user) {
        
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lec = starsDbCache.getEnergyCompany(ec);
        
        List<Warehouse> warehouses = lec.getWarehouses();
        model.addAttribute("warehouses", warehouses);
        
        ChangeWarehouseTask task = (ChangeWarehouseTask) resultsCache.getResult(taskId);
        model.addAttribute("task", task);
        model.addAttribute("inventoryCollection", task.getCollection());
        
        return "operator/inventory/changeWarehouse.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String changeType(HttpServletRequest req, YukonUserContext userContext, int warehouseId) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        ChangeWarehouseTask task = helper.new ChangeWarehouseTask(collection, userContext, warehouseId);
        String taskId = helper.startTask(task);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest req, ModelMap model) {
        collectionFactory.addCollectionToModelMap(req, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
}