package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.temperature.Temperature;
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
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
@RequestMapping(value = "/operator/thermostatManual/*")
public class OperatorThermostatManualController {
    
	private InventoryDao inventoryDao;
	private CustomerEventDao customerEventDao;
	private CustomerDao customerDao;
	private CustomerAccountDao customerAccountDao;
	private ThermostatService thermostatService;
	private OperatorThermostatHelper operatorThermostatHelper;
	private ThermostatEventHistoryDao thermostatEventHistoryDao;
	
	// VIEW
	@RequestMapping
    public String view(String thermostatIds,
				    		 ModelMap modelMap,
					         FlashScope flashScope,
					         AccountInfoFragment accountInfoFragment,
					         HttpServletRequest request) {

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
        
        return "operator/operatorThermostat/manual/view.jsp";
    }
	
	// SAVE
	@RequestMapping
    public String save(String thermostatIds,
    		    		String mode, 
    		    		String fan, 
    		    		String temperatureUnit,
    		    		Double temperature,
    		    		YukonUserContext userContext,
    		            HttpServletRequest request, 
    		            ModelMap modelMap,
    		            FlashScope flashScope,
    			        AccountInfoFragment accountInfoFragment) {
	    
	    Temperature temp = thermostatService.getTempOrDefault(temperature, temperatureUnit);
		executeManualEvent(thermostatIds, mode, fan, temperatureUnit, temp, userContext, request, modelMap, flashScope, accountInfoFragment);
		
        return "redirect:view";
    }
	
	// RUN PROGRAM
	@RequestMapping
    public String runProgram(String thermostatIds,
				    		String mode, 
				    		String fan, 
				    		String temperatureUnit,
				    		Double temperature,
				    		YukonUserContext userContext,
				            HttpServletRequest request, 
				            ModelMap modelMap,
				            FlashScope flashScope,
					        AccountInfoFragment accountInfoFragment) {

	    Temperature temp = thermostatService.getTempOrDefault(temperature, temperatureUnit);
		executeManualEvent(thermostatIds, mode, fan, temperatureUnit, temp, userContext, request, modelMap, flashScope, accountInfoFragment);
		
        return "redirect:/spring/stars/operator/thermostatSchedule/savedSchedules";
    }
	
    private void executeManualEvent(String thermostatIds,
				    		String mode, 
				    		String fan, 
				    		String temperatureUnit,
				    		Temperature temperature,
				    		YukonUserContext userContext,
				            HttpServletRequest request, 
				            ModelMap modelMap,
				            FlashScope flashScope,
					        AccountInfoFragment accountInfoFragment) {

		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        
		thermostatService.logOperatorThermostatManualSaveAttempt(thermostatIdsList, userContext, customerAccount);
        ThermostatMode thermostatMode = thermostatService.getThermostatModeFromString(mode);
        
        thermostatService.updateTempUnitForCustomer(temperatureUnit, customerAccount.getCustomerId());
        
        // See if the run program button was clicked
        String runProgramButtonClicked = ServletRequestUtils.getStringParameter(request, "runProgram", null);
        boolean runProgram = runProgramButtonClicked != null;
        
        //temperature must be validated if mode is heat or cool and the run program
        //button was not pressed
        boolean needsTempValidation = thermostatMode.isHeatOrCool() && !runProgram;
        boolean isValid = true;
        ThermostatManualEventResult message = null;
        
        //Validate temperature for mode and thermostat type
        if(needsTempValidation) {
            ThermostatManualEventResult limitMessage = thermostatService.validateTempAgainstLimits(thermostatIdsList, temperature, thermostatMode);
            
            if(limitMessage != null) {
                String key = "yukon.dr.consumer.manualevent.result.OPERATOR_" + limitMessage.name();
                MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(key);
                flashScope.setError(messageResolvable);
                isValid = false;
            }
        }
        
        if(isValid) {
            boolean hold = ServletRequestUtils.getBooleanParameter(request, "hold", false);
            
            message = thermostatService.setupAndExecuteManualEvent(thermostatIdsList, hold, runProgram, temperature, mode, fan, customerAccount, userContext);
            
            // Add thermostat labels to message
            List<String> thermostatLabels = new ArrayList<String>();
            for(Integer thermostatId : thermostatIdsList) {
            	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            	thermostatLabels.add(thermostat.getLabel());
            }
            String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
            String key = "yukon.dr.consumer.manualevent.result.OPERATOR_" + message.name();
            MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(key, thermostatLabelString);
            
        	flashScope.setMessage(messageResolvable, message.isFailed() ? FlashScopeMessageType.ERROR : FlashScopeMessageType.CONFIRM);
        }
    }
    
	@Autowired
	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	
	@Autowired
	public void setCustomerEventDao(CustomerEventDao customerEventDao) {
		this.customerEventDao = customerEventDao;
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
	public void setThermostatService(ThermostatService thermostatService) {
		this.thermostatService = thermostatService;
	}
	
	@Autowired
	public void setOperatorThermostatHelper(OperatorThermostatHelper operatorThermostatHelper) {
		this.operatorThermostatHelper = operatorThermostatHelper;
	}
	
	@Autowired
	public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
	    this.thermostatEventHistoryDao = thermostatEventHistoryDao;
	}
}
