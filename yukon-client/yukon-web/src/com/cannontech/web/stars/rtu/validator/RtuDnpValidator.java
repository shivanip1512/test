package com.cannontech.web.stars.rtu.validator;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Service
public class RtuDnpValidator extends SimpleValidator<RtuDnp> {

    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;

    private static final String basekey = "yukon.web.modules.operator.rtuDetail.error";

    public RtuDnpValidator() {
        super(RtuDnp.class);
    }

    @Override
    protected void doValidation(RtuDnp rtuDnp, Errors errors) {
        validateName(rtuDnp, errors);
        validateCommPort(rtuDnp, errors);
        validateScanIntervals(rtuDnp, errors);
    }

    private void validateName(RtuDnp rtuDnp, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", rtuDnp.getName(), 60);
            if (!errors.hasFieldErrors("name")) {
                if (!PaoUtils.isValidPaoName(rtuDnp.getName())) {
                    errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
                }
                if (!errors.hasFieldErrors("name")) {
                    boolean idSpecified = rtuDnp.getId() != null;

                    boolean nameAvailable = paoDao.isNameAvailable(rtuDnp.getName(), rtuDnp.getPaoType());

                    if (!nameAvailable) {
                        if (!idSpecified || rtuDnp.isCopyFlag()) {
                            // For create, we must have an available name
                        	//For Copy, We have used flag as idSpecified flag will be true
                            errors.rejectValue("name", "yukon.web.error.nameConflict");
                        } else {
                            // For edit, we can use our own existing name
                            LiteYukonPAObject existingPao = dbCache.getAllPaosMap().get(rtuDnp.getId());
                            if (!existingPao.getPaoName().equals(rtuDnp.getName())) {
                                errors.rejectValue("name", "yukon.web.error.nameConflict");
                            }
                        }
                    }
                }
            }
        }
    }

    private void validateCommPort(RtuDnp rtuDnp, Errors errors) {
    	
    	 DeviceAddress deviceAddress = rtuDnp.getDeviceAddress();
    	 
    	 if (!errors.hasFieldErrors("deviceAddress.slaveAddress")) {
             YukonValidationUtils.checkRange(errors, "deviceAddress.slaveAddress", deviceAddress.getSlaveAddress(), 0, 65535,
                 true);
         }

        if (rtuDnp.getDeviceDirectCommSettings() == null) {
            return;
        }

        Integer portId = rtuDnp.getDeviceDirectCommSettings().getPortID();
        YukonValidationUtils.checkRange(errors, "deviceDirectCommSettings.portID", portId, 0, Integer.MAX_VALUE, true);

        YukonValidationUtils.checkRange(errors, "deviceAddress.postCommWait", deviceAddress.getPostCommWait(), 0, 99999,
            true);
        if(!errors.hasFieldErrors("deviceAddress.masterAddress")) {
            YukonValidationUtils.checkRange(errors, "deviceAddress.masterAddress", deviceAddress.getMasterAddress(), 0,
                65535, true);
        }

        if (!errors.hasFieldErrors("deviceAddress.masterAddress") && !errors.hasFieldErrors("deviceAddress.slaveAddress")) {
            List<Integer> devicesWithSameAddress =
                deviceDao.getDevicesByDeviceAddress(deviceAddress.getMasterAddress(), deviceAddress.getSlaveAddress());

            devicesWithSameAddress.removeAll(Collections.singleton(rtuDnp.getId()));

            if (!devicesWithSameAddress.isEmpty()) {

                List<Integer> devicesOnPort = deviceDao.getDevicesByPort(portId);
                SetView<Integer> masterSlavePortConflicts =
                    Sets.intersection(ImmutableSet.copyOf(devicesOnPort), ImmutableSet.copyOf(devicesWithSameAddress));

                if (!masterSlavePortConflicts.isEmpty()) {

                    String deviceName =
                        masterSlavePortConflicts.stream().findFirst().map(new Function<Integer, String>() {
                            @Override
                            public String apply(Integer id) {
                                return dbCache.getAllPaosMap().get(id).getPaoName();
                            }
                        }).get();

                    errors.rejectValue("deviceAddress.masterAddress", basekey + ".masterSlave",
                        new Object[] { deviceName }, "Master/Slave combination in use");
                    errors.rejectValue("deviceAddress.slaveAddress", "yukon.common.blank");
                }

            }
        }
        
    }

    private void validateScanIntervals(RtuDnp rtuDnp, Errors errors) {
        rtuDnp.getDeviceScanRateMap().forEach((key, deviceScanRate) -> {
            if (deviceScanRate.getAlternateRate().equals(deviceScanRate.getIntervalRate())) {
                errors.rejectValue("deviceScanRateMap['" + key + "'].alternateRate",
                    "yukon.web.error.valuesCannotBeSame");
                errors.rejectValue("deviceScanRateMap['" + key + "'].intervalRate",
                    "yukon.web.error.valuesCannotBeSame");
            }
        });
    }
}
