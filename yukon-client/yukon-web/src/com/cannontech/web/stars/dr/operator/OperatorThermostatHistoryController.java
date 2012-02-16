package com.cannontech.web.stars.dr.operator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
@RequestMapping(value = "/operator/thermostat/history/*")
public class OperatorThermostatHistoryController {

    private InventoryDao inventoryDao;
    private CustomerEventDao customerEventDao;
    private CustomerDao customerDao;
    private CustomerAccountDao customerAccountDao;
    private OperatorThermostatHelper operatorThermostatHelper;
    private ThermostatEventHistoryDao thermostatEventHistoryDao;
    
    // VIEW history for 1 or more thermostats
    @RequestMapping
    public String view(String thermostatIds, ModelMap modelMap, FlashScope flashScope, AccountInfoFragment accountInfoFragment, HttpServletRequest request) {

        List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
        
        ThermostatManualEvent event;
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIdsList.get(0));
        
        // single thermostat
        if (thermostatIdsList.size() == 1) {
            Integer inventoryId = thermostatIdsList.get(0);
            event = customerEventDao.getLastManualEvent(inventoryId);
            modelMap.addAttribute("thermostat", thermostat);
            modelMap.addAttribute("inventoryId", inventoryId);
            modelMap.addAttribute("pageNameSuffix", "single");
        // multiple thermostats
        } else {
            event = new ThermostatManualEvent();
            modelMap.addAttribute("pageNameSuffix", "multiple");
        }
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        modelMap.addAttribute("temperatureUnit", temperatureUnit);
        modelMap.addAttribute("event", event);
        
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getEventsByThermostatIds(thermostatIdsList);

        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);

        SearchResult<ThermostatEvent> result = new SearchResult<ThermostatEvent>().pageBasedForWholeList(currentPage, itemsPerPage, eventHistoryList);
        modelMap.addAttribute("searchResult", result);
        modelMap.addAttribute("eventHistoryList", result.getResultList());
        
        if(thermostatIdsList.size() > 1){
            modelMap.addAttribute("displayName", new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatManual.multipleLabel"));
        }else{
            //the serial number is the same as the hardwareDto.getDisplayName() that appears on the parent page.
            modelMap.addAttribute("displayName", thermostat.getSerialNumber());
        }
        
        return "operator/operatorThermostat/history/view.jsp";
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setOperatorThermostatHelper(OperatorThermostatHelper operatorThermostatHelper) {
        this.operatorThermostatHelper = operatorThermostatHelper;
    }
    
    @Autowired
    public void setCustomerEventDao(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }
    
    @Autowired
    public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
        this.thermostatEventHistoryDao = thermostatEventHistoryDao;
    }
}
