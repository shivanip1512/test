package com.cannontech.web.capcontrol.validators;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Service
public class CbcValidator extends SimpleValidator<CapControlCBC> {

    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;

    private static final String basekey = "yukon.web.modules.capcontrol.cbc.error";

    public CbcValidator() {
        super(CapControlCBC.class);
    }

    @Override
    public void doValidation(CapControlCBC cbc, Errors errors) {
        validateName(cbc, errors);
        validateSerialNumber(cbc, errors);
        if (cbc.isTwoWay()) {
            validateCommPort(cbc, errors);
            if (!cbc.getDeviceScanRateMap().isEmpty()) {
                validateScanIntervals(cbc, errors);
            }
        }
        if (cbc.isLogical()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "parentRtuId", basekey + ".parentRTURequired");
        }
        if (cbc.getPaoType().isTcpPortEligible() && 
            dbCache.getAllPaosMap().get(cbc.getDeviceDirectCommSettings().getPortID()).getPaoType() == PaoType.TCPPORT) {
            YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", cbc.getIpAddress());
            YukonValidationUtils.validatePort(errors, "port", cbc.getPort());
        }
      
    }

    private void validateName(CapControlCBC cbc, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        
        if(!errors.hasFieldErrors("name")){
            if(!PaoUtils.isValidPaoName(cbc.getName())){
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        YukonValidationUtils.checkExceedsMaxLength(errors, "name", cbc.getName(), 60);
        boolean idSpecified = cbc.getId() != null;

        boolean nameAvailable = paoDao.isNameAvailable(cbc.getName(), cbc.getPaoType());
        
        if (!nameAvailable) {
            if (!idSpecified) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use our own existing name
                LiteYukonPAObject existingPao = dbCache.getAllPaosMap().get(cbc.getId());
                if (!existingPao.getPaoName().equals(cbc.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }

    private void validateScanIntervals(CapControlCBC cbc, Errors errors) {
        cbc.getDeviceScanRateMap().forEach((key, deviceScanRate) -> {
            if (deviceScanRate.getAlternateRate().equals(deviceScanRate.getIntervalRate())) {
                errors.rejectValue("deviceScanRateMap['" + key + "'].alternateRate",
                    "yukon.web.error.valuesCannotBeSame");
                errors.rejectValue("deviceScanRateMap['" + key + "'].intervalRate",
                    "yukon.web.error.valuesCannotBeSame");
            }
        });
    }

    private void validateSerialNumber(CapControlCBC cbc, Errors errors) {
        Integer serialNumber = cbc.getDeviceCBC().getSerialNumber();
        YukonValidationUtils.checkRange(errors, "deviceCBC.serialNumber", serialNumber,
            0, Integer.MAX_VALUE, true);
        if (serialNumber != null) {
            String[] paos = DeviceCBC.isSerialNumberUnique(serialNumber, cbc.getId());
            if (paos != null && paos.length > 0) {
                errors.rejectValue("deviceCBC.serialNumber", basekey + ".duplicateSerial", paos, "Serial Number in use");
            }
        }
    }

    private void validateCommPort(CapControlCBC cbc, Errors errors) {

        if(cbc.getDeviceDirectCommSettings()== null) {
            return;
        }

        Integer portId = cbc.getDeviceDirectCommSettings().getPortID();
        YukonValidationUtils.checkRange(errors, "deviceDirectCommSettings.portID", portId,
            0, Integer.MAX_VALUE, true);

        DeviceAddress deviceAddress = cbc.getDeviceAddress();

        YukonValidationUtils.checkRange(errors, "deviceAddress.postCommWait", deviceAddress.getPostCommWait(),
            0, 99999, true);
        YukonValidationUtils.checkRange(errors, "deviceAddress.masterAddress", deviceAddress.getMasterAddress(),
            0, 65535, true);
        YukonValidationUtils.checkRange(errors, "deviceAddress.slaveAddress", deviceAddress.getSlaveAddress(),
            0, 65535, true);

        List<Integer> devicesWithSameAddress = deviceDao.getDevicesByDeviceAddress(
            deviceAddress.getMasterAddress(), deviceAddress.getSlaveAddress());

        devicesWithSameAddress.removeAll(Collections.singleton(cbc.getId()));

        if (!devicesWithSameAddress.isEmpty()) {

            List<Integer> devicesOnPort = deviceDao.getDevicesByPort(portId);
             SetView<Integer> masterSlavePortConflicts = Sets.intersection(
                 ImmutableSet.copyOf(devicesOnPort), ImmutableSet.copyOf(devicesWithSameAddress));

             if (!masterSlavePortConflicts.isEmpty()) {

                 String deviceName = masterSlavePortConflicts.stream()
                     .findFirst()
                     .map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer id) {
                            return dbCache.getAllPaosMap().get(id).getPaoName();
                        }
                     }).get();

                 errors.rejectValue("deviceAddress.masterAddress", 
                     basekey + ".masterSlave", new Object[] {deviceName}, "Master/Slave combination in use");
                 errors.rejectValue("deviceAddress.slaveAddress", "yukon.common.blank");
             }

        }
    }
}
