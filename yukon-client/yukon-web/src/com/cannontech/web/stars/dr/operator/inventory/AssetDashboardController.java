package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.model.InventorySearch;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@CheckRole({YukonRole.CONSUMER_INFO, YukonRole.INVENTORY})
@Controller
@RequestMapping("/operator/inventory/*")
public class AssetDashboardController {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private ConfigurationSource configSource;
    @Autowired private EnergyCompanySettingDao ecSettingsDao;
    @Autowired private SelectionListService listService;
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private EnergyCompanyDao ecDao;
    
    private static final String key = "yukon.web.modules.operator.inventory.home.";
    
    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext userContext) {
        
        setupModel(model, userContext);
        
        return "operator/inventory/home.jsp";
    }
    
    @RequestMapping("upload-file")
    public String fileUpload(HttpServletRequest req, ModelMap model, FlashScope flash, YukonUserContext userContext) 
    throws ServletRequestBindingException {
        
        try {
            InventoryCollection collection = collectionFactory.createCollection(req);
            model.addAllAttributes(collection.getCollectionParameters());
        } catch (CollectionCreationException e) {
            
            flash.setError(new YukonMessageSourceResolvable(key + e.getMessage()));
            
            setupModel(model, userContext);
            
            return "operator/inventory/home.jsp";
        }
        
        return "redirect:inventoryActions";
    }
    
    /** Add everything the view needs to the model. */
    private void setupModel(ModelMap model, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        boolean ecOperator = ecDao.isEnergyCompanyOperator(userContext.getYukonUser());
        
        if (ecOperator) {
            EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
            
            int ecId = ec.getId();
            model.addAttribute("energyCompanyId", ecId);
            
            MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(userContext);
            String title = messageSourceAccessor.getMessage(key + "fileUploadTitle");
            model.addAttribute("fileUploadTitle", title);
            
            MeteringType type = ecSettingsDao.getEnum(EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION, 
                    MeteringType.class, ecId);
            model.addAttribute("showAddMeter", type == MeteringType.yukon);
            
            boolean showLinks = configSource.getBoolean(MasterConfigBooleanKeysEnum.DIGI_ENABLED);
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
            
            String deviceList = YukonSelectionListEnum.DEVICE_TYPE.getListName();
            List<YukonListEntry> devicesTypes = listService.getSelectionList(ec, deviceList).getYukonListEntries();
            
            Iterable<YukonListEntry> addHardwareTypes = Iterables.filter(devicesTypes, new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry input) {
                    HardwareType type = HardwareType.valueOf(input.getYukonDefID());
                    return type != HardwareType.YUKON_METER && type != HardwareType.NON_YUKON_METER;
                }
            });
            model.addAttribute("addHardwareTypes", addHardwareTypes.iterator());
            
            Iterable<YukonListEntry> addHardwareByRangeTypes = Iterables.filter(devicesTypes, 
            new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry input) {
                    HardwareType type = HardwareType.valueOf(input.getYukonDefID()); 
                    return type.isSupportsAddByRange();
                }
            });
            model.addAttribute("addHardwareByRangeTypes", addHardwareByRangeTypes.iterator());
        }
    }
    
}