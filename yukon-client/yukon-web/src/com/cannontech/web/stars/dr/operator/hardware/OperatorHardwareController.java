package com.cannontech.web.stars.dr.operator.hardware;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.assetavailability.SimpleAssetAvailability;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.cannontech.dr.assetavailability.service.impl.NoInventoryException;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.event.EventInventory;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.digi.model.ZigbeeDeviceDto;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.HardwareModelHelper;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.InventoryCheckingAddDto;
import com.cannontech.web.stars.dr.operator.hardware.model.SerialNumber;
import com.cannontech.web.stars.dr.operator.hardware.service.ZigbeeDeviceService;
import com.cannontech.web.stars.dr.operator.hardware.validator.SerialNumberValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.cannontech.web.util.SessionUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RequestMapping("/operator/hardware/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class OperatorHardwareController {
    private final Logger log = YukonLogManager.getLogger(OperatorHardwareController.class);

    @Autowired private AddressDao addressDao;
    @Autowired private AssetAvailabilityService assetAvailabilityService;
    @Autowired private ContactDao contactDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private HardwareModelHelper hardwareModelHelper;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private HardwareService hardwareService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private OperatorThermostatHelper operatorThermostatHelper;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SerialNumberValidator serialNumberValidator;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private SelectionListService selectionListService;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private YukonListDao listDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ZigbeeDeviceService zigbeeDeviceService;
    @Autowired private HardwareConfigService hardwareConfigService;
    @Autowired private PaoNotesService paoNotesService;

    private static final int THERMOSTAT_DETAIL_NUM_ITEMS = 5;

    @RequestMapping("list")
    public String list(YukonUserContext userContext, ModelMap model, AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        model.addAttribute("serialNumberSwitch", new SerialNumber());
        model.addAttribute("serialNumberThermostat", new SerialNumber());
        model.addAttribute("serialNumberGateway", new SerialNumber());
        model.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());

        setupListModel(accountInfoFragment, model, energyCompany, userContext);
        return "operator/hardware/hardwareList.jsp";
    }

    @RequestMapping("checkSerialNumberSwitch")
    public String checkSerialNumberSwitch(@ModelAttribute("serialNumberSwitch") SerialNumber serialNumber,
            BindingResult bindingResult, ModelMap model, YukonUserContext userContext, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment, int hardwareTypeId) {
        model.addAttribute("serialNumberThermostat", new SerialNumber());
        model.addAttribute("serialNumberGateway", new SerialNumber());
        return checkSerialNumber(serialNumber, bindingResult, model, userContext, flashScope, accountInfoFragment,
                hardwareTypeId);
    }

    @RequestMapping("checkSerialNumberThermostat")
    public String checkSerialNumberThermostat(@ModelAttribute("serialNumberThermostat") SerialNumber serialNumber,
            BindingResult bindingResult, ModelMap model, YukonUserContext userContext, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment, int hardwareTypeId) {
        model.addAttribute("serialNumberSwitch", new SerialNumber());
        model.addAttribute("serialNumberGateway", new SerialNumber());
        return checkSerialNumber(serialNumber, bindingResult, model, userContext, flashScope, accountInfoFragment,
                hardwareTypeId);
    }

    @RequestMapping("checkSerialNumberGateway")
    public String checkSerialNumberGateway(@ModelAttribute("serialNumberGateway") SerialNumber serialNumber,
            BindingResult bindingResult, ModelMap model, YukonUserContext userContext, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment, int hardwareTypeId) {
        model.addAttribute("serialNumberSwitch", new SerialNumber());
        model.addAttribute("serialNumberThermostat", new SerialNumber());
        return checkSerialNumber(serialNumber, bindingResult, model, userContext, flashScope, accountInfoFragment,
                hardwareTypeId);
    }

    private String checkSerialNumber(SerialNumber serialNumber, BindingResult bindingResult, ModelMap model,
            YukonUserContext userContext, FlashScope flashScope, AccountInfoFragment accountInfoFragment,
            int hardwareTypeId) {
        serialNumberValidator.validate(serialNumber, bindingResult);
        YukonListEntry deviceTypeEntry = listDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            if (type.getHardwareClass().equals(HardwareClass.SWITCH)) {
                model.addAttribute("showSwitchCheckingPopup", true);
            } else if (type.getHardwareClass().equals(HardwareClass.THERMOSTAT)) {
                model.addAttribute("showThermostatCheckingPopup", true);
            } else {
                model.addAttribute("showGatewayCheckingPopup", true);
            }
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
            EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
            setupListModel(accountInfoFragment, model, energyCompany, userContext);
            return "operator/hardware/hardwareList.jsp"; 
        }

        try {
            LiteLmHardwareBase possibleDuplicate =
                    starsSearchDao.searchLmHardwareBySerialNumber(serialNumber.getSerialNumber(),
                            accountInfoFragment.getEnergyCompanyId());

            InventoryCheckingAddDto inventoryCheckingAddDto = new InventoryCheckingAddDto(serialNumber.getSerialNumber());
            inventoryCheckingAddDto.setHardwareTypeId(hardwareTypeId);
            inventoryCheckingAddDto.setHardwareType(type);

            if (possibleDuplicate != null) {
                inventoryCheckingAddDto.setInventoryId(possibleDuplicate.getInventoryID());
                inventoryCheckingAddDto.setSerialNumber(serialNumber.getSerialNumber());

                inventoryCheckingAddDto.setDeviceType(listDao.getYukonListEntry(
                        possibleDuplicate.getLmHardwareTypeID()).getEntryText());

                if (possibleDuplicate.getAccountID() > CtiUtilities.NONE_ZERO_ID) {
                    if (possibleDuplicate.getAccountID() == accountInfoFragment.getAccountId()) {
                        // Return to the list page with the 'Already on this account' popup.
                        model.addAttribute("sameAccountSerial", serialNumber.getSerialNumber());
                    } else {
                        // Return to the list page with the confirm steal from account popup.
                        model.addAttribute("confirmAccountSerial", serialNumber.getSerialNumber());
                        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                        String none = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.noneSelectOption");
                        CustomerAccount account = customerAccountDao.getById(possibleDuplicate.getAccountID());
                        inventoryCheckingAddDto.setAccountNumber(account.getAccountNumber());

                        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(possibleDuplicate.getAccountID());

                        inventoryCheckingAddDto.setName(primaryContact.getContFirstName() == null
                                ? none : primaryContact.getContFirstName() + " " + primaryContact.getContLastName());

                        LiteAddress address = addressDao.getByAddressId(primaryContact.getAddressID());
                        inventoryCheckingAddDto.setAddress(Address.getDisplayableAddress(address));
                    }
                } else {
                    // Return to the list page with the confirm add from warehouse popup.
                    model.addAttribute("confirmWarehouseSerial", serialNumber.getSerialNumber());
                    inventoryCheckingAddDto.setAltTrackingNumber(possibleDuplicate.getAlternateTrackingNumber());

                    Warehouse warehouse = warehouseDao.findWarehouseForInventoryId(possibleDuplicate.getInventoryID());
                    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                    String defaultWarehouse = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardware.serialNumber.defaultWarehouse");
                    inventoryCheckingAddDto.setWarehouse(warehouse == null ? defaultWarehouse : warehouse.getWarehouseName());
                }

            } else {
                boolean invCheckingCreate = ecSettingDao.getBoolean(EnergyCompanySettingType.INVENTORY_CHECKING_CREATE, accountInfoFragment.getEnergyCompanyId());
                if (invCheckingCreate) {
                    // Return to the list page with the confirm creation popup.
                    model.addAttribute("confirmCreateSerial", serialNumber.getSerialNumber());
                } else {
                    // Return to the list page with the SN unavailable popup.
                    model.addAttribute("notFoundSerial", serialNumber.getSerialNumber());
                }
            }

            model.addAttribute("checkingAdd", inventoryCheckingAddDto);
        } catch (ObjectInOtherEnergyCompanyException e) {
            // Return to the list page with the hardware found in another ec popup.
            model.addAttribute("anotherECSerial", serialNumber.getSerialNumber());
            model.addAttribute("anotherEC", StringEscapeUtils.escapeHtml4(e.getYukonEnergyCompany().getName()));
        }

        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        setupListModel(accountInfoFragment, model, energyCompany, userContext);
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        return "operator/hardware/hardwareList.jsp";
    }

    @RequestMapping("createPage")
    public String createPage(YukonUserContext userContext, ModelMap model, AccountInfoFragment accountInfoFragment,
            int hardwareTypeId, String serialNumber) {
        model.addAttribute("mode", PageEditMode.CREATE);
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, userContext.getYukonUser());

        Hardware hardware = new Hardware();
        hardware.setAccountId(accountInfoFragment.getAccountId());
        hardware.setHardwareTypeEntryId(hardwareTypeId);
        hardware.setFieldInstallDate(new Date());
        YukonListEntry entry = listDao.getYukonListEntry(hardwareTypeId);
        HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
        hardware.setHardwareType(type);
        hardware.setDisplayType(entry.getEntryText());

        setupCreateModel(accountInfoFragment, model, type, userContext);


        if (StringUtils.isNotBlank(serialNumber)) {
            hardware.setSerialNumber(serialNumber);
        }

        model.addAttribute("hardware", hardware);

        return "operator/hardware/hardware.jsp";
    }

    @RequestMapping("view")
    public String view(ModelMap model, YukonUserContext userContext, AccountInfoFragment accountInfoFragment,
            HttpServletRequest request, int inventoryId) throws NoInventoryException {
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());

        Hardware hardware = hardwareUiService.getHardware(inventoryId);

        model.addAttribute("hardware", hardware);

        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("mode", PageEditMode.VIEW);

        setupHardwareViewEditModel(accountInfoFragment, hardware, model, userContext);
        boolean showViewMore = false;
        if(HardwareType.getForClass(HardwareClass.THERMOSTAT).contains(hardware.getHardwareType())
                && hardware.getHardwareType().isSupportsSchedules()) {
            showViewMore = true;
            operatorThermostatHelper.setupModelMapForCommandHistory(model, request, 
                    Collections.singletonList(inventoryId), accountInfoFragment.getAccountId(), 
                    THERMOSTAT_DETAIL_NUM_ITEMS);
        }
        model.addAttribute("showViewMore", showViewMore);

        if (hardware.getHardwareType().isTwoWay()) {
            SimpleAssetAvailability assetAvail = assetAvailabilityService.getAssetAvailability(hardware.getInventoryId());
            model.addAttribute("assetAvailability", assetAvail);
            model.addAttribute("isTwoWayDevice", true);
        }
        InventoryIdentifier inventory = inventoryDao.getYukonInventory(inventoryId);
        model.addAttribute("canEnableDisable", inventory.getHardwareType().isSupportServiceInServiceOut());
        model.addAttribute("canSendShed", inventory.getHardwareType().isSupportsIndividualDeviceShed());
        boolean isAllowDRControl =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_DR_CONTROL, userContext.getYukonUser());
        model.addAttribute("isAllowDRControl", isAllowDRControl);
        if(hardware.getHardwareType() == HardwareType.NON_YUKON_METER){
            return "redirect:/stars/operator/hardware/mp/view";
        }

        if (hardware.getHardwareType().isRf()) {
            int deviceId = inventoryDao.getDeviceId(inventoryId);
            model.addAttribute("deviceId", deviceId);
            model.addAttribute("showNetworkInfo", true);
            model.addAttribute("showMapNetwork", true);
        } else if (hardware.getHardwareType().isTwoWayPlcLcr()) {
            int deviceId = inventoryDao.getDeviceId(inventoryId);
            model.addAttribute("deviceId", deviceId);
            model.addAttribute("showMapNetwork", true);
        }
        return "operator/hardware/hardware.jsp";
    }

    @RequestMapping("edit")
    public String edit(ModelMap model, YukonUserContext userContext, AccountInfoFragment accountInfoFragment,
            int inventoryId) {
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());

        Hardware hardware = hardwareUiService.getHardware(inventoryId);

        model.addAttribute("hardware", hardware);
        model.addAttribute("displayName", hardware.getDisplayName());
        model.addAttribute("mode", PageEditMode.EDIT);

        setupHardwareViewEditModel(accountInfoFragment, hardware, model, userContext);

        return "operator/hardware/hardware.jsp";
    }

    @RequestMapping(value="update", params="cancel")
    public String cancel(ModelMap model, AccountInfoFragment accountInfoFragment, int inventoryId) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }

    @RequestMapping(value="update", params="save")
    public String update(@ModelAttribute Hardware hardware, BindingResult bindingResult, ModelMap model,
            YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope, int inventoryId)
                    throws ServletRequestBindingException {
        AccountInfoFragment accountInfoFragment = operatorAccountService.getAccountInfoFragment(hardware.getAccountId());
        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        LiteYukonUser user = userContext.getYukonUser();

        hardwareModelHelper.updateAttempted(hardware, user, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, bindingResult);

        if (bindingResult.hasErrors()) {
            return returnToEditWithErrors(userContext, model, accountInfoFragment, flashScope, hardware, bindingResult);
        }

        boolean statusChange = false;

        try {
            statusChange = hardwareUiService.updateHardware(user, hardware);
        } catch (ObjectInOtherEnergyCompanyException | StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.unavailable");
            log.error("Unable to update hardware. ", e);
        } catch (Lcr3102YukonDeviceCreationException e) {
            switch (e.getType()) {
            case UNKNOWN:
                bindingResult.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unknown");
                break;
            case REQUIRED:
                bindingResult.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.required");
                break;
            case UNAVAILABLE:
                bindingResult.rejectValue("twoWayDeviceName", "yukon.web.modules.operator.hardware.error.unavailable");
                break;
            case NON_NUMERIC:
                bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.nonNumericSerialNumber");
                break;
            }
            log.error("Unable to update hardware. ", e);
        }
        if (bindingResult.hasErrors()) {
            return returnToEditWithErrors(userContext, model, accountInfoFragment, flashScope, hardware, bindingResult);
        }

        // If the device status was changed, spawn an event for it
        if (statusChange) {
            int userId = SessionUtil.getParentLoginUserId(request.getSession(), user.getUserID());
            EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_INVENTORY, hardware.getDeviceStatusEntryId(), inventoryId);
        }

        // Flash hardware updated
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareUpdated"));
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        model.addAttribute("inventoryId", inventoryId);

        return "redirect:view";
    }

    private String returnToEditWithErrors(YukonUserContext userContext, ModelMap model,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope, Hardware hardware,
            BindingResult bindingResult) {
        model.addAttribute("mode", PageEditMode.EDIT);
        // Return back to the JSP with the errors
        setupHardwareViewEditModel(accountInfoFragment, hardware, model, userContext);
        model.addAttribute("displayName", hardware.getDisplayName());

        // Add errors to flash scope
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

        return "operator/hardware/hardware.jsp";
    }

    @RequestMapping("create")
    public String create(@ModelAttribute("hardware") Hardware hardware, BindingResult bindingResult, ModelMap model,
            YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope)
                    throws ServletRequestBindingException {
        AccountInfoFragment accountInfoFragment = operatorAccountService.getAccountInfoFragment(hardware.getAccountId());
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        int accountId = accountInfoFragment.getAccountId();

        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        // Cancel Creation
        if (cancelButton != null) {
            model.clear();
            model.addAttribute("accountId", accountId);
            return "redirect:list";
        }

        LiteYukonUser user = userContext.getYukonUser();
        Set<YukonRoleProperty> verifyProperties = Sets.newHashSet(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE);
        hardwareModelHelper.creationAttempted(user, accountInfoFragment.getAccountNumber(), hardware, verifyProperties, bindingResult);

        if (!bindingResult.hasErrors()) {
            int inventoryId = hardwareModelHelper.create(user, hardware, bindingResult, request.getSession());

            if (bindingResult.hasErrors()) {
                return returnToCreateWithErrors(model, hardware, userContext, flashScope, accountInfoFragment, bindingResult);
            }

            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareCreated"));
            model.addAttribute("inventoryId", inventoryId);
            return "redirect:view";

        }

        return returnToCreateWithErrors(model, hardware, userContext, flashScope, accountInfoFragment, bindingResult);
    }

    private String returnToCreateWithErrors(ModelMap model, Hardware hardware, YukonUserContext userContext,
            FlashScope flashScope, AccountInfoFragment accountInfoFragment, BindingResult bindingResult) {
        model.addAttribute("mode", PageEditMode.CREATE);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        setupCreateModel(accountInfoFragment, model, hardware.getHardwareType(), userContext);
        return "operator/hardware/hardware.jsp";
    }

    @RequestMapping("delete")
    public String delete(ModelMap model, LiteYukonUser user, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment, int inventoryId, String deleteOption)
                    throws NotAuthorizedException, NotFoundException, CommandCompletionException, SQLException,
                    PersistenceException, WebClientException {
        Hardware hardwareToDelete = hardwareUiService.getHardware(inventoryId);
        hardwareEventLogService.hardwareDeletionAttempted(user, hardwareToDelete.getDisplayName(), EventSource.OPERATOR);

        hardwareUiService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);

        // Delete this hardware or just take it off the account and put in back in the warehouse
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        hardwareService.deleteHardware(user, delete, inventoryId);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        if (delete) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareDeleted"));
        } else {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareRemoved"));
        }
        return "redirect:list";
    }

    @RequestMapping("serviceCompanyInfo")
    public String serviceCompanyInfo(ModelMap model, int serviceCompanyId) throws ServletRequestBindingException {
        ServiceCompanyDto serviceCompanyDto = serviceCompanyDao.getCompanyById(serviceCompanyId);
        LiteAddress serviceCompanyAddress = addressDao.getByAddressId(serviceCompanyDto.getAddress().getAddressID());
        Address address = Address.getDisplayableAddress(serviceCompanyAddress);

        model.addAttribute("companyName", serviceCompanyDto.getCompanyName());
        model.addAttribute("address", address);
        model.addAttribute("mainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());

        return "operator/hardware/serviceCompanyInfo.jsp";
    }

    @RequestMapping("addDeviceToAccount")
    public String addDeviceToAccount(ModelMap model, LiteYukonUser user, AccountInfoFragment accountInfoFragment,
            FlashScope flashScope, boolean fromAccount, int inventoryId) {
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.assigningHardwareAttempted(user,
                accountInfoFragment.getAccountNumber(),
                lmHardwareBase.getManufacturerSerialNumber(),
                EventSource.OPERATOR);

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);

        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        LiteInventoryBase liteInventoryBase = inventoryBaseDao.getByInventoryId(inventoryId);

        hardwareUiService.addDeviceToAccount(liteInventoryBase, accountInfoFragment.getAccountId(), fromAccount,
                energyCompany, user);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));

        return "redirect:list";
    }

    @RequestMapping("addMeter")
    public String addMeter(ModelMap model, LiteYukonUser user, AccountInfoFragment accountInfoFragment,
            FlashScope flashScope, int meterId) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meterId);
        hardwareEventLogService.hardwareMeterCreationAttempted(user, liteYukonPAO.getPaoName(), EventSource.OPERATOR);

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        try {
            hardwareUiService.addYukonMeter(meterId, accountInfoFragment.getAccountId(), user);
        } catch (ObjectInOtherEnergyCompanyException e) {
            // Ignore
        }

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.hardwareAdded"));

        return "redirect:list";
    }

    @RequestMapping("changeOut")
    public String changeOut(ModelMap model, LiteYukonUser user, AccountInfoFragment accountInfoFragment,
            FlashScope flashScope, int newInventoryId, int oldInventoryId, boolean isMeter, String redirect) {
        // Log change out attempt
        Hardware oldInventory = hardwareUiService.getHardware(oldInventoryId);
        String changeOutMessage = "yukon.web.modules.operator.hardware.hardwareChangeOut";
        FlashScopeMessageType messageType =  FlashScopeMessageType.SUCCESS;

        if (isMeter) {
            LiteYukonPAObject oldLiteYukonPAO = paoDao.getLiteYukonPAO(oldInventory.getDeviceId());
            LiteYukonPAObject newLiteYukonPAO = paoDao.getLiteYukonPAO(newInventoryId);
            hardwareEventLogService.hardwareChangeOutForMeterAttempted(user,
                    oldLiteYukonPAO.getPaoName(),
                    newLiteYukonPAO.getPaoName(),
                    EventSource.OPERATOR);
        } else {
            String newInventorySN = lmHardwareBaseDao.getSerialNumberForInventoryId(newInventoryId);
            String oldInventorySN = oldInventory.getSerialNumber();
            hardwareEventLogService.hardwareChangeOutAttempted(user, oldInventorySN, newInventorySN, EventSource.OPERATOR);
            InventoryIdentifier newInventoryIdentifier = inventoryDao.getYukonInventory(newInventoryId);
            HardwareType type = newInventoryIdentifier.getHardwareType();
            if (type.isGateway()) {
                changeOutMessage = "yukon.web.modules.operator.hardware.hardwareChangeOut.gateway";
                messageType = FlashScopeMessageType.WARNING;
            } else if (type.isThermostat() && type.isZigbee()) {
                changeOutMessage = "yukon.web.modules.operator.hardware.hardwareChangeOut.zigbeeDevice";
                messageType = FlashScopeMessageType.WARNING;
            }
        }

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        int inventoryId = hardwareUiService.changeOutInventory(oldInventoryId, newInventoryId, user, isMeter);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        MessageSourceResolvable message = new YukonMessageSourceResolvable(changeOutMessage);
        flashScope.setMessage(message, messageType);

        if (redirect.equalsIgnoreCase("list")) {
            return "redirect:list";
        }

        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
    }

    // HELPERS

    // Add common Attributes to model for view/edit and create modes both need for the hardware page
    // Should only be called by setupHardwareCreateModelMap and setupHardwareViewEditModelMap methods
    private void setupCommonModelAttributes(HardwareType type, AccountInfoFragment accountInfoFragment, ModelMap model,
            YukonUserContext userContext) {
        model.addAttribute("editingRoleProperty", YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING.name());

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        model.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());

        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(accountInfoFragment.getEnergyCompanyId());
        EnergyCompany energyCompany = ecDao.getEnergyCompany(lsec.getEnergyCompanyId());

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("none", accessor.getMessage("yukon.common.none.choice"));

        String defaultRoute;
        int defaultRouteId = 0;
        try {
            defaultRouteId = defaultRouteService.getDefaultRouteId(energyCompany);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = accessor.getMessage("yukon.common.route.default", defaultRoute);
        } catch(NotFoundException e) {
            defaultRouteId = 0;
            defaultRoute = accessor.getMessage("yukon.common.route.default.none");
        }
        model.addAttribute("defaultRoute", defaultRoute);
        model.addAttribute("defaultRouteId", defaultRouteId);

        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(energyCompany);
        model.addAttribute("routes", routes);

        List<Integer> energyCompanyIds = 
                Lists.transform(energyCompany.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(energyCompanyIds));

        // Setup elements to hide/show based on device type/class
        HardwareClass clazz = type.getHardwareClass();
        model.addAttribute("displayTypeKey", ".displayType." + clazz);

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

    }

    private void setupCreateModel(AccountInfoFragment accountInfoFragment, ModelMap model, HardwareType type,
            YukonUserContext userContext) {
        HardwareClass clazz = type.getHardwareClass();
        setupCommonModelAttributes(type, accountInfoFragment, model, userContext);

        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
            model.addAttribute("serialNumberEditable", true);
        }
        
        if (type.isHoneywell()) {
            model.addAttribute("macAddressEditable", true);
            model.addAttribute("deviceVendorUserIdEditable", true);
        }
        if (type.isZigbee()) {
            model.addAttribute("macAddressEditable", true);
        }
    }

    private void setupHardwareViewEditModel(AccountInfoFragment accountInfoFragment, Hardware hardware, ModelMap model,
            YukonUserContext userContext) {
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        boolean tstatAccess = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT, userContext.getYukonUser());

        boolean inventoryChecking = ecSettingDao.getBoolean(EnergyCompanySettingType.INVENTORY_CHECKING, accountInfoFragment.getEnergyCompanyId());
        model.addAttribute("allowAccountEditing", allowAccountEditing);
        model.addAttribute("inventoryChecking", inventoryChecking);

        HardwareType type = hardware.getHardwareType();
        HardwareClass clazz = type.getHardwareClass();

        setupCommonModelAttributes(type, accountInfoFragment, model, userContext);

        int accountId = accountInfoFragment.getAccountId();
        int inventoryId = hardware.getInventoryId();
        model.addAttribute("inventoryId", inventoryId);

        //  Add Pao Specifics
        Integer deviceId = hardware.getDeviceId();
        if (deviceId != null && deviceId > 0 && !type.isZigbee()) {  // points for ZigBee device have their own special box
            List<LitePoint> points = pointDao.getLitePointsByPaObjectId(deviceId);
            if (!points.isEmpty()) {
                ListMultimap<PointType, LitePoint> pointsMap = ArrayListMultimap.create();
                for (LitePoint point : points) {
                    pointsMap.put(point.getPointTypeEnum(), point);
                }
                for (PointType pointTypeKey : pointsMap.keySet()) {
                    Collections.sort(pointsMap.get(pointTypeKey), new Comparator<LitePoint>() {
                        @Override
                        public int compare(LitePoint o1, LitePoint o2) {
                            return o1.getPointName().compareTo(o2.getPointName());
                        }
                    });
                }
                model.addAttribute("showPoints", true);
                model.addAttribute("points", pointsMap.asMap());
            }
        }

        // Device Status History
        List<EventInventory> deviceStatusHistory = EventInventory.retrieveEventInventories(inventoryId);
        model.addAttribute("deviceStatusHistory", deviceStatusHistory);

        // Hardware History
        model.addAttribute("hardwareHistory", hardwareUiService.getHardwareHistory(inventoryId));

        // Warehouses
        LiteStarsEnergyCompany ec = starsDatabaseCache.getEnergyCompany(accountInfoFragment.getEnergyCompanyId());
        List<Warehouse> warehouses = ec.getWarehouses();
        model.addAttribute("warehouses", warehouses);

        // For switches and tstats, if they have inventory checking turned off they can edit the serial number.
        if (!inventoryChecking && !clazz.isMeter()) {
            // Rf devices can not have their serial number edited here...yet.
            if (!type.isRf()) {
                model.addAttribute("serialNumberEditable", true);
            }
        }

        // For switches and tstats, show serial number instead of device name
        if (!clazz.isMeter()) {
            model.addAttribute("showSerialNumber", true);
        }

        // If ZigBee device, show Commissioned/Decommissioned State
        if (type.isZigbee()) {
            model.addAttribute("showZigbeeState", true);
        }

        // Actions to show
        switch (clazz) {

        case SWITCH:
            model.addAttribute("showSwitchAndTstatConfigAction", true);
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showSwitchChangeoutAction", true);
                }
            }
            if (deviceId != null && deviceId > 0 && !type.isZigbee() && type.isTwoWay()) {
                model.addAttribute("deviceId", deviceId);
                model.addAttribute("showPointsLinkAction", true);
            }
            break;

        case THERMOSTAT:
            model.addAttribute("showSwitchAndTstatConfigAction", true);
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showTstatChangeoutAction", true);
                }
            }

            if (tstatAccess) {
                if (type.isSupportsSchedules()) {
                    model.addAttribute("showScheduleActions", true);
                }
                if (type.isSupportsManualAdjustment()) {
                    model.addAttribute("showManualAction", true);
                }
                model.addAttribute("showThermostatHistory", true);
            }
            break;

        case METER:
            model.addAttribute("showDeviceName", true);
            if (type == HardwareType.YUKON_METER) {
                model.addAttribute("showMeterConfigAction", true);
                if (rolePropertyDao.checkRole(YukonRole.METERING, userContext.getYukonUser())) {
                    model.addAttribute("showMeterDetailAction", true);
                }
            }
            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showMeterChangeoutAction", true);
                }
            }
            break;

        case GATEWAY:
            model.addAttribute("showCommissionActions", true);
            model.addAttribute("showTextMessageAction", true);
            model.addAttribute("showAssignedDevices", true);

            if (allowAccountEditing) {
                if (inventoryChecking) {
                    model.addAttribute("showGatewayChangeOutAction", true);
                }
            }

            List<Pair<InventoryIdentifier, ZigbeeDeviceDto>> zigbeeDevices = zigbeeDeviceService.buildZigbeeDeviceDtoList(accountId);
            List<ZigbeeDeviceDto> assignedDevices = Lists.newArrayList();
            List<ZigbeeDeviceDto> availableDevices = Lists.newArrayList();
            for (Pair<InventoryIdentifier, ZigbeeDeviceDto> pair : zigbeeDevices) {
                if (pair.first == null) {
                    availableDevices.add(pair.second);
                } else if (pair.first != null && pair.first.getInventoryId() == inventoryId) {
                    assignedDevices.add(pair.second);
                }
            }
            model.addAttribute("availableDevices", availableDevices);
            model.addAttribute("assignedDevices", assignedDevices);

            break;
        }

        if (type.isZigbee() && !type.isGateway()) {
            InventoryIdentifier gateway = gatewayDeviceDao.findGatewayByDeviceMapping(deviceId);
            if (gateway == null) {
                model.addAttribute("showDisabledCommissionActions", true);
                model.addAttribute("showDisabledRefresh", true);
            } else {
                model.addAttribute("showCommissionActions", true);
                model.addAttribute("gatewayInventoryId", gateway.getInventoryId());
            }

            model.addAttribute("showGateway", true);
            List<DigiGateway> gateways = gatewayDeviceDao.getGatewaysForAccount(accountId);
            model.addAttribute("gateways", gateways);

        }
        
    }

    private void setupListModel(AccountInfoFragment accountInfoFragment, ModelMap model, EnergyCompany ec,
            YukonUserContext userContext) {
        int energyCompanyId = accountInfoFragment.getEnergyCompanyId();
        // Add device types for dropdown menus
        ListMultimap<String, DeviceTypeOption> deviceTypeMap = ArrayListMultimap.create();
        List<YukonListEntry> deviceTypeList = selectionListService.getSelectionList(ec,
                YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE).getYukonListEntries();

        for (YukonListEntry deviceTypeEntry : deviceTypeList) {
            HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
            DeviceTypeOption option = new DeviceTypeOption();
            option.setDisplayName(deviceTypeEntry.getEntryText());
            option.setHardwareTypeEntryId(deviceTypeEntry.getEntryID());
            if (!type.isNest()) {
                deviceTypeMap.put(type.getHardwareClass().name(), option);
            }
        }
        model.addAttribute("deviceTypeMap", deviceTypeMap.asMap());

        model.addAttribute("energyCompanyId", energyCompanyId);
        ListMultimap<HardwareClass, Hardware> hardwareMap = hardwareUiService.getHardwareMapForAccount(accountInfoFragment.getAccountId());

        model.addAttribute("switches", hardwareMap.get(HardwareClass.SWITCH));
        model.addAttribute("meters", hardwareMap.get(HardwareClass.METER));
        model.addAttribute("thermostats", hardwareMap.get(HardwareClass.THERMOSTAT));
        model.addAttribute("gateways", hardwareMap.get(HardwareClass.GATEWAY));
        Iterable<Hardware> schedualableThermostats = Iterables.filter(hardwareMap.get(HardwareClass.THERMOSTAT), new Predicate<Hardware>() {
            @Override
            public boolean apply(Hardware dto) {
                return dto.getHardwareType().isSupportsSchedules();
            }
        });
        if (Lists.newArrayList(schedualableThermostats).size() > 1) {
            model.addAttribute("showSelectAll", Boolean.TRUE);
        }
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(hardwareMap.values()
                                                                                .stream()
                                                                                .map(hardware -> hardware.getDeviceId())
                                                                                .filter(deviceId -> deviceId != 0)
                                                                                .collect(Collectors.toList()));
        
        model.addAttribute("notesList", notesList);

        model.addAttribute("switchClass", HardwareClass.SWITCH);
        model.addAttribute("thermostatClass", HardwareClass.THERMOSTAT);
        model.addAttribute("meterClass", HardwareClass.METER);
        model.addAttribute("gatewayClass", HardwareClass.GATEWAY);

        MeteringType meterDesignation = ecSettingDao.getEnum(
                EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION, MeteringType.class, energyCompanyId);
        boolean starsMeters = meterDesignation == MeteringType.stars; 
        model.addAttribute("starsMeters", starsMeters);

        boolean inventoryChecking = ecSettingDao.getBoolean(EnergyCompanySettingType.INVENTORY_CHECKING,
                energyCompanyId);
        model.addAttribute("inventoryChecking", inventoryChecking);
    }

    @RequestMapping("disable")
    public String disable(ModelMap model, int inventoryId, YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {

        // Log hardware disable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareDisableAttempted(userContext.getYukonUser(),
                lmHardwareBase.getManufacturerSerialNumber(), accountInfo.getAccountNumber(), EventSource.OPERATOR);

        // Validate request
        verifyHardwareIsForAccount(inventoryId, accountInfo);
        model.addAttribute("accountId", accountInfo.getAccountId());
        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.disable(inventoryId, accountInfo.getAccountId(), accountInfo.getEnergyCompanyId(),
                    userContext);

            MessageSourceResolvable confirmationMessage =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.disableCommandSent");
            flashScope.setConfirm(confirmationMessage);
        } catch (CommandCompletionException e) {
            MessageSourceResolvable errorMessage =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.disableCommandFailed",
                            e.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:view";
    }

    @RequestMapping("enable")
    public String enable(ModelMap model, int inventoryId, YukonUserContext userContext,
            AccountInfoFragment accountInfo, FlashScope flashScope) {

        // Log hardware enable attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        hardwareEventLogService.hardwareEnableAttempted(userContext.getYukonUser(),
                lmHardwareBase.getManufacturerSerialNumber(), accountInfo.getAccountNumber(), EventSource.OPERATOR);

        // Validate request
        verifyHardwareIsForAccount(inventoryId, accountInfo);
        model.addAttribute("accountId", accountInfo.getAccountId());
        model.addAttribute("inventoryId", inventoryId);

        try {
            hardwareConfigService.enable(inventoryId, accountInfo.getAccountId(), accountInfo.getEnergyCompanyId(),
                    userContext);

            MessageSourceResolvable confirmationMessage =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.enableCommandSent");
            flashScope.setConfirm(confirmationMessage);
        } catch (CommandCompletionException e) {
            MessageSourceResolvable errorMessage =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.enableCommandFailed",
                            e.getMessage());
            flashScope.setError(errorMessage);
        }

        return "redirect:view";
    }

    private void verifyHardwareIsForAccount(int inventoryId,
            AccountInfoFragment accountInfo) {
        LiteInventoryBase inventory = inventoryBaseDao.getByInventoryId(inventoryId);
        if (inventory.getAccountID() != accountInfo.getAccountId()) {
            throw new NotAuthorizedException("The device " + inventoryId +
                    " does not belong to account " +
                    accountInfo.getAccountId());
        }
    }
    // DEVICE TYPE SELECT OPTIONS WRAPPER
    public static class DeviceTypeOption {
        private int hardwareTypeEntryId;
        private String displayName;

        public int getHardwareTypeEntryId() {
            return hardwareTypeEntryId;
        }
        public void setHardwareTypeEntryId(int hardwareTypeEntryId) {
            this.hardwareTypeEntryId = hardwareTypeEntryId;
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
