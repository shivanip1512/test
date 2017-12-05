package com.cannontech.web.widget.hardwareInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display basic hardware information
 */
@Controller
@RequestMapping("/hardwareInformationWidget/*")
public class HardwareInformationWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private HardwareUiService hardwareUiService;
    
    @Autowired
    public HardwareInformationWidget(@Qualifier("widgetInput.inventoryId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        //TODO: What roles should we look for?
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, int inventoryId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        model.addAttribute("hardware", hardware);
        model.addAttribute("energyCompanyId", hardware.getEnergyCompanyId());
        HardwareType type = hardware.getHardwareType();
        
        // Setup elements to hide/show based on device type/class
        HardwareClass clazz = type.getHardwareClass();
        model.addAttribute("displayTypeKey", ".displayType." + clazz);
        
        // For switches and tstats, show serial number instead of device name
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
        }

        if (type.isZigbee()) {
            model.addAttribute("showMacAddress", true);
            if (!type.isGateway()) {
                model.addAttribute("showInstallCode", true);
            } else {
                model.addAttribute("showFirmwareVersion", true);
            }
        } else if (type.isHoneywell()) {
            model.addAttribute("showMacAddress", true);
            model.addAttribute("showDeviceVendorUserId", true);
        }

        boolean showVoltage = !type.isZigbee() && !clazz.isGateway() && !clazz.isThermostat();
        model.addAttribute("showVoltage", showVoltage);

        if (type.showRoute()) {
            model.addAttribute("showRoute", true);
        }

        // Show two way device row for non-ZigBee two way LCRs
        if (type == HardwareType.LCR_3102) {
            model.addAttribute("showTwoWay", true);
        }

        model.addAttribute("showInstallNotes", true);
        
        return "hardwareInformationWidget/render.jsp";
    }

}