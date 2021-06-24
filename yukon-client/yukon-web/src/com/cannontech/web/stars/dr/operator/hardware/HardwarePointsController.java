package com.cannontech.web.stars.dr.operator.hardware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;

@Controller
@RequestMapping("/operator/hardware/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class HardwarePointsController {

    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private InventoryDao inventoryDao;
    
    @GetMapping("points")
    public String points(ModelMap model, int deviceId, AccountInfoFragment account) {
        
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        
        model.addAttribute("device", device);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        
        InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        int inventoryId = inventory.getInventoryId();
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("accountId", hardware.getAccountId());
        model.addAttribute("inventoryId", inventoryId);
        if (account != null) {
            AccountInfoFragmentHelper.setupModelMapBasics(account, model);
            model.addAttribute("accountNumber", account.getAccountNumber());
        }
        if (account == null) {
            model.addAttribute("page", "inventory." + PageEditMode.VIEW);
        } else {
            model.addAttribute("page", "hardware.points");
        }
        return "operator/hardware/hardware.points.jsp";
    }
    
}
