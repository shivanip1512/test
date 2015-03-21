package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
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
    private RecentResultsCache<AbstractInventoryTask> resultsCache;

    @RequestMapping("view")
    public String view(HttpServletRequest req, ModelMap model, String taskId, LiteYukonUser user) {
        
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany lec = starsDbCache.getEnergyCompany(ec);
        
        List<LiteServiceCompany> sc = lec.getServiceCompanies();
        model.addAttribute("serviceCompanies", sc);
        collectionFactory.addCollectionToModelMap(req, model);
        
        if (taskId != null) {
            ChangeServiceCompanyTask task = (ChangeServiceCompanyTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
        }
        
        return "operator/inventory/changeServiceCompany.jsp";
    }
    
    @RequestMapping(value="do", params="start")
    public String changeType(HttpServletRequest request, YukonUserContext context, ModelMap model, int serviceCompanyId) {
        
        InventoryCollection collection = collectionFactory.createCollection(request);
        ChangeServiceCompanyTask task = helper.new ChangeServiceCompanyTask(collection, context, serviceCompanyId);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(request, model);
        
        return "redirect:view";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(HttpServletRequest request, ModelMap model) {
        collectionFactory.addCollectionToModelMap(request, model);
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}