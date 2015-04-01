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
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeServiceCompanyHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.ChangeServiceCompanyHelper.ChangeServiceCompanyTask;

@Controller
@RequestMapping("/operator/inventory/changeServiceCompany/*")
@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
public class ChangeServiceCompanyController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private ChangeServiceCompanyHelper helper;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    @RequestMapping("setup")
    public String setup(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lec = starsDbCache.getEnergyCompany(ec);
        
        List<LiteServiceCompany> sc = lec.getServiceCompanies();
        model.addAttribute("serviceCompanies", sc);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "operator/inventory/changeServiceCompany.jsp";
    }
    
    @RequestMapping("{taskId}/status")
    public String view(HttpServletRequest req, ModelMap model, @PathVariable String taskId, LiteYukonUser user) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lec = starsDbCache.getEnergyCompany(ec);
        
        List<LiteServiceCompany> sc = lec.getServiceCompanies();
        model.addAttribute("serviceCompanies", sc);
        
        ChangeServiceCompanyTask task = (ChangeServiceCompanyTask) resultsCache.getResult(taskId);
        model.addAttribute("task", task);
        model.addAttribute("inventoryCollection", task.getCollection());
        
        return "operator/inventory/changeServiceCompany.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String changeType(HttpServletRequest req, YukonUserContext userContext, ModelMap model, int serviceCompanyId) {
        
        InventoryCollection collection = collectionFactory.createCollection(req);
        ChangeServiceCompanyTask task = helper.new ChangeServiceCompanyTask(collection, userContext, serviceCompanyId);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest request, ModelMap model) {
        collectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
}