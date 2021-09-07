package com.cannontech.web.stars.rtu.validator;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.core.dao.DeviceDao;

@Service
public class RtuDnpValidationUtil extends ValidationUtils {
    private static final String PORT_ID = "deviceDirectCommSettings.portID";
    private static final String POST_COMM_WAIT = "deviceAddress.postCommWait";
    private static final String ADDRESS_MASTER = "deviceAddress.masterAddress";
    private static final String ADDRESS_SLAVE = "deviceAddress.slaveAddress";
    private static final String TCP_IP_ADDRESS = "ipAddress";
    private static final String TCP_PORT = "port";
    
    @Autowired private DeviceDao deviceDao;
	@Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoPropertyDao paoPropertyDao;
    
    public void validateName(RtuDnp rtuDnp, Errors errors, boolean isCopyOperation) {
        YukonValidationUtils.checkIsBlank(errors, "name", rtuDnp.getName(), "Name", false);
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
                        if (!idSpecified || isCopyOperation) {
                            // For create, we must have an available name
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

    public void validateAddressing(Integer deviceId, DeviceDirectCommSettings directCommSettings, DeviceAddress address, 
            String tcpIpAddress, String tcpPort, Errors errors, String masterSlaveErrorKey) {
        
        YukonValidationUtils.checkRange(errors, POST_COMM_WAIT, address.getPostCommWait(), 
                0, 99999, true);
        YukonValidationUtils.checkRange(errors, ADDRESS_MASTER, address.getMasterAddress(), 
                0, 65535, true);
        YukonValidationUtils.checkRange(errors, ADDRESS_SLAVE, address.getSlaveAddress(), 
                0, 65535, true);

        if (directCommSettings == null) {
            errors.rejectValue(PORT_ID, "yukon.web.error.required");
            return;
        }

        YukonValidationUtils.checkRange(errors, PORT_ID, directCommSettings.getPortID(), 
                0, Integer.MAX_VALUE, true);
        
        //  If the port or the addresses are out of range, we cannot validate against the port devices
        if (errors.hasFieldErrors(PORT_ID) 
            || errors.hasFieldErrors(ADDRESS_MASTER) 
            || errors.hasFieldErrors(ADDRESS_SLAVE)) {
            return;
        }
        
        var conflictingDevices =
                deviceDao.getDevicesByPortAndDeviceAddress(
                        directCommSettings.getPortID(),
                        address.getMasterAddress(),
                        address.getSlaveAddress()).stream();

        if (deviceId != null) {
            conflictingDevices = conflictingDevices.filter(conflict -> // NOSONAR - disable S3958, the stream is used below
                    conflict.getId() != deviceId);  //  Don't conflict with our existing DB record
        }

        var port = dbCache.getAllPaosMap().get(directCommSettings.getPortID());
        if (port.getPaoType() == PaoType.TCPPORT) {
            if (errors.hasFieldErrors(TCP_IP_ADDRESS) || errors.hasFieldErrors(TCP_PORT)) {
                //  Cannot validate address conflicts on erroneous IP/port entries (such as "(none)") 
                return;
            }
            conflictingDevices = conflictingDevices.filter(conflict ->
                    tcpIpAddress.equals(paoPropertyDao.getByIdAndName(conflict.getId(), PaoPropertyName.TcpIpAddress).getPropertyValue())
                    && tcpPort.equals(paoPropertyDao.getByIdAndName(conflict.getId(), PaoPropertyName.TcpPort).getPropertyValue()));
        }
        
        conflictingDevices
            .findFirst()
            .ifPresent(conflict -> {
                errors.rejectValue(ADDRESS_MASTER, masterSlaveErrorKey, ArrayUtils.toArray(conflict.getName()), "Master/Slave combination in use");
                errors.rejectValue(ADDRESS_SLAVE, "yukon.common.blank");
            });
    }
}
