package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Controller
@CheckRole({YukonRole.CONSUMER_INFO, YukonRole.INVENTORY})
@RequestMapping("/operator/inventory/*")
public class AssetDashboardController {
    
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EnergyCompanySettingDao ecSettingsDao;
    @Autowired private SelectionListService selectionListService;

    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        boolean ecOperator = energyCompanyDao.isEnergyCompanyOperator(context.getYukonUser());
        
        if (ecOperator) {
            EnergyCompany energyCompany = energyCompanyDao.getEnergyCompanyByOperator(user);
            
            int ecId = energyCompany.getId();
            model.addAttribute("energyCompanyId", ecId);
            
            MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(context);
            String title = messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.home.fileUploadTitle");
            model.addAttribute("fileUploadTitle", title);
            
            MeteringType type = ecSettingsDao.getEnum(EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION, MeteringType.class, ecId);
            model.addAttribute("showAddMeter", type == MeteringType.yukon);
            
            boolean showLinks = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DIGI_ENABLED);
            model.addAttribute("showLinks", showLinks);
            
            boolean showSearch = rolePropertyDao.checkProperty(YukonRoleProperty.INVENTORY_SEARCH, user);
            model.addAttribute("showSearch", showSearch);
            
            boolean showAccountSearch = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ACCOUNT_SEARCH, user);
            model.addAttribute("showAccountSearch", showAccountSearch);
            
            /** Hardware Creation */
            boolean hasAddHardwareByRange = rolePropertyDao.checkProperty(YukonRoleProperty.SN_ADD_RANGE, user);
            boolean hasCreateHardware = rolePropertyDao.checkProperty(YukonRoleProperty.INVENTORY_CREATE_HARDWARE, user);

            boolean showHardwareCreate = hasAddHardwareByRange || hasCreateHardware;
            model.addAttribute("showHardwareCreate", showHardwareCreate);
            
            /** Account Creation */
            boolean hasCreateAccount = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_NEW_ACCOUNT_WIZARD, user);
            boolean showAccountCreate = hasCreateAccount && ecOperator;
            model.addAttribute("showAccountCreate", showAccountCreate);
            
            /** Actions Dropdown Button */
            if(showHardwareCreate || showAccountCreate) {
                model.addAttribute("showActions", true);
            }
            
            SerialNumberValidation snv = ecSettingsDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION,
                                                                         SerialNumberValidation.class,
                                                                         ecId);
            model.addAttribute("showAddByRange", snv == SerialNumberValidation.NUMERIC && hasAddHardwareByRange);
            
            model.addAttribute("inventorySearch", new InventorySearch());
            
            List<YukonListEntry> yukonListEntries = 
                selectionListService.getSelectionList(energyCompany, 
                                              YukonSelectionListEnum.DEVICE_TYPE.getListName()).getYukonListEntries();
            Iterable<YukonListEntry> addHardwareTypes = Iterables.filter(yukonListEntries, new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry input) {
                    HardwareType type = HardwareType.valueOf(input.getYukonDefID());
                    return type != HardwareType.YUKON_METER && type != HardwareType.NON_YUKON_METER;
                }
            });
            model.addAttribute("addHardwareTypes", addHardwareTypes.iterator());
            
            Iterable<YukonListEntry> addHardwareByRangeTypes = Iterables.filter(yukonListEntries, new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry input) {
                    HardwareType type = HardwareType.valueOf(input.getYukonDefID()); 
                    return type.isSupportsAddByRange();
                }
            });
            model.addAttribute("addHardwareByRangeTypes", addHardwareByRangeTypes.iterator());
        }

        return "operator/inventory/home.jsp";
    }
    
}
