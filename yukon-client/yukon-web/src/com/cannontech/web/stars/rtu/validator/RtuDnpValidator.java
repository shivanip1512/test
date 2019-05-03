package com.cannontech.web.stars.rtu.validator;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Service
public class RtuDnpValidator extends SimpleValidator<RtuDnp> {

    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RtuDnpValidationUtil rtuDnpValidationUtil; 

    private static final String basekey = "yukon.web.modules.operator.rtuDetail.error";

    public RtuDnpValidator() {
        super(RtuDnp.class);
    }

    @Override
    protected void doValidation(RtuDnp rtuDnp, Errors errors) {
        rtuDnpValidationUtil.validateName(rtuDnp, errors, false);
        validateCommPort(rtuDnp, errors);
        validateScanIntervals(rtuDnp, errors);
        if (dbCache.getAllPaosMap().get(rtuDnp.getDeviceDirectCommSettings().getPortID()).getPaoType() == PaoType.TCPPORT) {
            YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", rtuDnp.getIpAddress());
            YukonValidationUtils.validatePort(errors, "port", rtuDnp.getPort());
        }
       
    }

    private void validateCommPort(RtuDnp rtuDnp, Errors errors) {

        if (rtuDnp.getDeviceDirectCommSettings() == null) {
            return;
        }

        Integer portId = rtuDnp.getDeviceDirectCommSettings().getPortID();
        YukonValidationUtils.checkRange(errors, "deviceDirectCommSettings.portID", portId, 0, Integer.MAX_VALUE, true);

        DeviceAddress deviceAddress = rtuDnp.getDeviceAddress();

        YukonValidationUtils.checkRange(errors, "deviceAddress.postCommWait", deviceAddress.getPostCommWait(), 0, 99999,
            true);
        if(!errors.hasFieldErrors("deviceAddress.masterAddress")) {
            YukonValidationUtils.checkRange(errors, "deviceAddress.masterAddress", deviceAddress.getMasterAddress(), 0,
                65535, true);
        }

        if (!errors.hasFieldErrors("deviceAddress.slaveAddress")) {
            YukonValidationUtils.checkRange(errors, "deviceAddress.slaveAddress", deviceAddress.getSlaveAddress(), 0, 65535,
                true);
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
