package com.cannontech.web.widget.lcrInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display basic LCR information
 */
@Controller
@RequestMapping("/lcrInformationWidget/*")
public class LcrInformationWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private AccountService accountService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private AttributeService attributeService;
    @Autowired private InventoryDao inventoryDao;
    
    @Autowired
    public LcrInformationWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, int deviceId, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.VIEW);
        InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        Hardware hardware = hardwareUiService.getHardware(inventory.getInventoryId());
        model.addAttribute("hardware", hardware);
        HardwareType type = hardware.getHardwareType();
        
        // Setup elements to hide/show based on device type/class
        HardwareClass clazz = type.getHardwareClass();
        model.addAttribute("displayTypeKey", ".displayType." + clazz);
        
        /* AccountDto */
        if (hardware.getAccountId() > 0) {
            AccountDto accountDto = accountService.getAccountDto(hardware.getAccountId(), hardware.getEnergyCompanyId(), userContext);
            model.addAttribute("accountDto", accountDto);
        }
        
        if (hardware.getRouteId() > 0) {
            LiteYukonPAObject route = cache.getAllRoutesMap().get(hardware.getRouteId());
            model.addAttribute("route", route);
        }
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(hardware.getDeviceId());
        boolean isRf = pao.getPaoType().isTwoWayRfnLcr();
        if (isRf) {
            LitePoint serviceStatusPoint = attributeService.getPointForAttribute(pao, BuiltInAttribute.SERVICE_STATUS);
            model.addAttribute("serviceStatusPointId", serviceStatusPoint.getPointID());
        }
        model.addAttribute("isRf", isRf);
        
        return "lcrInformationWidget/render.jsp";
    }

}