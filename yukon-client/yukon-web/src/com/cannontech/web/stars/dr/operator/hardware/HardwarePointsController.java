package com.cannontech.web.stars.dr.operator.hardware;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

@Controller
@RequestMapping("/operator/hardware/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class HardwarePointsController {

    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private YukonPointHelper yukonPointHelper;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private InventoryDao inventoryDao;
    
    @RequestMapping("points")
    public String points(ModelMap model, 
            int deviceId, 
            YukonUserContext userContext,
            AccountInfoFragment account,
            @DefaultSort(dir=Direction.asc, sort="POINTNAME") SortingParameters sorting) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        List<LiteYukonPoint> liteYukonPoints = yukonPointHelper.getYukonPoints(device, sorting, accessor );
        
        model.addAttribute("device", device);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("points", liteYukonPoints);
        
        InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        int inventoryId = inventory.getInventoryId();
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("accountId", hardware.getAccountId());
        model.addAttribute("inventoryId", inventoryId);
        if (account != null) {
            model.addAttribute("accountNumber", account.getAccountNumber());
        }
        buildColumn(model, accessor, PointSortField.ATTRIBUTE, sorting);
        buildColumn(model, accessor, PointSortField.POINTNAME, sorting);
        buildColumn(model, accessor, PointSortField.POINTTYPE, sorting);
        buildColumn(model, accessor, PointSortField.POINTOFFSET, sorting);
        if (account == null) {
            model.addAttribute("page", "inventory." + PageEditMode.VIEW);
        } else {
            model.addAttribute("page", "hardware.points");
        }
        return "operator/hardware/hardware.points.jsp";
    }
    
    private void buildColumn(ModelMap model, MessageSourceAccessor accessor, PointSortField field, 
            SortingParameters sorting) {
        
        Direction dir = sorting.getDirection();
        PointSortField sort = PointSortField.valueOf(sorting.getSort());
        
        String text = accessor.getMessage(field);
        boolean active = sort == field;
        SortableColumn col = SortableColumn.of(dir, active, text, field.name());
        model.addAttribute(field.name(), col);
    }
    
}
