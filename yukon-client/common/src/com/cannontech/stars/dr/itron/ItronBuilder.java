package com.cannontech.stars.dr.itron;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.exception.DeviceMacAddressAlreadyExistsException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.Validator;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ItronBuilder implements HardwareTypeExtensionProvider {
        
    private static final Logger log = YukonLogManager.getLogger(ItronBuilder.class);
    private static final ImmutableMap<HardwareType, PaoType> hardwareTypeToPaoType = ImmutableMap.<HardwareType, PaoType> builder()
            .put(HardwareType.LCR_6600S, PaoType.LCR6600S)
            .put(HardwareType.LCR_6601S, PaoType.LCR6601S)
            .build();
    
    @Autowired private DeviceCreationService creationService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private ItronCommunicationService itronCommunicationService;
    @Autowired private AccountService accountService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private IDatabaseCache cache;
    
    @Override
    public void createDevice(Hardware hardware) {
        try {
            if (deviceDao.isMacAddressExists(hardware.getMacAddress())) {
                throw new DeviceMacAddressAlreadyExistsException();
            }
            AccountDto account = null;
            if (hardware.getAccountId() > 0) {
                account = accountService.getAccountDto(hardware.getAccountId(), hardware.getEnergyCompanyId());
            }
            SimpleDevice pao = creationService.createDeviceByDeviceType(
                hardwareTypeToPaoType.get(hardware.getHardwareType()), hardware.getSerialNumber());
            inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), pao.getDeviceId());
            deviceDao.updateDeviceMacAddress(pao.getDeviceType(), pao.getDeviceId(), hardware.getMacAddress());
            itronCommunicationService.addDevice(hardware, account);
            hardware.setDeviceId(pao.getDeviceId());
            itronCommunicationService.saveSecondaryMacAddress(pao.getDeviceType(), pao.getDeviceId(), hardware.getMacAddress());
        } catch (ItronCommunicationException e) {
            log.error("Unable to create device.", e);
            MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(YukonUserContext.system);
            throw new DeviceCreationException(accessor.getMessage(e.getItronMessage()), "invalidDeviceCreation", e);
        }
    }

    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
       // Nothing to do.
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        String macAddress = deviceDao.getDeviceMacAddress(pao.getPaoIdentifier().getPaoId());
        itronCommunicationService.removeDeviceFromServicePoint(macAddress);
        deviceDao.removeDevice(pao.getPaoIdentifier().getPaoId());
    }

    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return hardwareTypeToPaoType.keySet();
    }

    @Override
    public void updateDevice(Hardware hardware) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(hardware.getDeviceId());
        deviceDao.updateSecondaryMacAddress(pao.getPaoType(), hardware.getDeviceId(), hardware.getSecondaryMacAddress());
    }

    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
        String macAddress = deviceDao.getDeviceMacAddress(pao.getPaoIdentifier().getPaoId());
        itronCommunicationService.removeDeviceFromServicePoint(macAddress);
    }

    @Override
    public void retrieveDevice(Hardware hardware) {
        try {
            hardware.setSecondaryMacAddress(deviceDao.getSecondaryMacAddressForDevice(hardware.getDeviceId()));
        } catch (NotFoundException nfe) {
            log.debug("No secondary mac found for device id " + hardware.getDeviceId(), nfe);
            hardware.setSecondaryMacAddress("");
        }
    }

    @Override
    public void validateDevice(Hardware hardware, Errors errors) {
        String macAddress = hardware.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isMacAddress(macAddress, true)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui64");
        }
        
        String secondaryMacAddress = hardware.getSecondaryMacAddress();
        if (!StringUtils.isBlank(secondaryMacAddress) && !Validator.isMacAddress(secondaryMacAddress, true)) {
            errors.rejectValue("secondaryMacAddress", "yukon.web.modules.operator.hardware.error.format.eui64");
        }
    }
}
