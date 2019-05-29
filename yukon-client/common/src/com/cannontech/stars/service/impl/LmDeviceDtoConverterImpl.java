package com.cannontech.stars.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.pao.model.GPS;
import com.cannontech.common.pao.service.impl.LocationServiceImpl;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.service.LmDeviceDtoConverter;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.util.ImportFields;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.util.ServletUtil;

public class LmDeviceDtoConverterImpl implements LmDeviceDtoConverter {

    @Autowired private EnergyCompanyService ecService;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private YukonListDao yukonListDao;
    
    @Override
    public LmDeviceDto createNewDto(String accountNo, String[] hwFields, YukonEnergyCompany lsec) throws ParseException {
        
        LmDeviceDto dto = new LmDeviceDto();
        dto.setAccountNumber(accountNo);
        
        String label = hwFields[ImportFields.IDX_DEVICE_LABEL];
        if(StringUtils.isNotEmpty(label)){
            dto.setDeviceLabel(label);
        }
        
        String deviceTypeText = hwFields[ImportFields.IDX_DEVICE_TYPE];
        dto.setDeviceType(deviceTypeText);
        
        dto.setFieldInstallDate(new Date());
        TimeZone ecTimeZone = ecService.getDefaultTimeZone(lsec.getEnergyCompanyId());
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_INSTALL_DATE])) {
            Date installDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_INSTALL_DATE], ecTimeZone);
            if (installDate == null) {
                installDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_INSTALL_DATE]);
            }
            dto.setFieldInstallDate(installDate);
        }
        
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_REMOVE_DATE])) {
            Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_REMOVE_DATE], ecTimeZone);
            if (removeDate == null) {
                removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_REMOVE_DATE]);
            }
            dto.setFieldRemoveDate(removeDate);
        }
        
        dto.setSerialNumber(hwFields[ImportFields.IDX_SERIAL_NO]);
        dto.setServiceCompanyName(hwFields[ImportFields.IDX_SERVICE_COMPANY]);
        dto.setMacAddress(hwFields[ImportFields.IDX_MAC_ADDRESS]);
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID])
            && StringUtils.isNumeric(hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID])) {
            dto.setDeviceVendorUserId(Integer.valueOf((hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID])));
        }

        GPS gps = getLocationGps(hwFields);
        if (gps != null) {
            dto.setGps(gps);
        }

        return dto;
    }

    
    @Override
    public LmDeviceDto getDtoForHardware(String accountNo, LiteInventoryBase lib, YukonEnergyCompany lsec) {
        
        // init dto with existing hardware
        LmDeviceDto dto = new LmDeviceDto();
        dto.setAccountNumber(accountNo);
        dto.setDeviceLabel(lib.getDeviceLabel());
        int devTypeId = ((LiteLmHardwareBase)lib).getLmHardwareTypeID();
        YukonListEntry yukonListEntry = yukonListDao.getYukonListEntry(devTypeId);
        String typeStr = yukonListEntry.getEntryText();
        dto.setDeviceType(typeStr);
        dto.setFieldInstallDate(new Date(lib.getInstallDate()));
        dto.setFieldRemoveDate(new Date(lib.getRemoveDate()));
        if (lib instanceof LiteLmHardwareBase) {
            dto.setSerialNumber(((LiteLmHardwareBase)lib).getManufacturerSerialNumber());
        }
        int installationCompanyId = lib.getInstallationCompanyID();
        
        try {
            ServiceCompanyDto serviceCompany = serviceCompanyDao.getCompanyById(installationCompanyId);
            dto.setServiceCompanyName(serviceCompany.getCompanyName());
        } catch (IncorrectResultSizeDataAccessException e) {
            // we didn't find one...should have but guess we didn't...don't fail because of it though.
        }
        
        return dto;
    }
    
    @Override
    public void updateDtoWithHwFields (LmDeviceDto dto, String[] hwFields, YukonEnergyCompany energyCompany) 
            throws ParseException {
        
        if (!StringUtils.isEmpty(hwFields[ImportFields.IDX_DEVICE_LABEL])) {
            dto.setDeviceLabel(hwFields[ImportFields.IDX_DEVICE_LABEL]);
        }
        
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_DEVICE_TYPE])) {
            dto.setDeviceType(hwFields[ImportFields.IDX_DEVICE_TYPE]);
        }

        TimeZone ecTimeZone = ecService.getDefaultTimeZone(energyCompany.getEnergyCompanyId());
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_INSTALL_DATE])) {
            Date installDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_INSTALL_DATE], ecTimeZone);
            if (installDate == null) {
                installDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_INSTALL_DATE]);
            }
            dto.setFieldInstallDate(installDate);
        }
        
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_REMOVE_DATE])) {
            Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_REMOVE_DATE], ecTimeZone);
            if (removeDate == null) {
                removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_REMOVE_DATE]);
            }
            dto.setFieldRemoveDate(removeDate);
        }
        
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_SERIAL_NO])) {
            dto.setSerialNumber(hwFields[ImportFields.IDX_SERIAL_NO]);
        }
        
        if (!StringUtils.isEmpty(hwFields[ImportFields.IDX_SERVICE_COMPANY])) {
            dto.setServiceCompanyName(hwFields[ImportFields.IDX_SERVICE_COMPANY]);
        }
        
        if (!StringUtils.isEmpty(hwFields[ImportFields.IDX_MAC_ADDRESS])) {
            dto.setMacAddress(hwFields[ImportFields.IDX_MAC_ADDRESS]);
        }
        
        if (!StringUtils.isBlank(hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID])
            && StringUtils.isNumeric(hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID])) {
            dto.setDeviceVendorUserId(Integer.valueOf((hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID])));
        }

        GPS gps = getLocationGps(hwFields);
        if (gps != null) {
            dto.setGps(gps);
        }
    }

    /**
     * Returns GPS object when both latitude and longitude are not blank, else return <code>null<code>.
     */
    private static GPS getLocationGps(String[] hwFields) {
        // If both are blank, return null else return GPS object(Example: lat: Blank, long : Invalid)
        if (StringUtils.isNotBlank(hwFields[ImportFields.IDX_LATITUDE])
            || StringUtils.isNotBlank(hwFields[ImportFields.IDX_LONGITUDE])) {
            GPS gps = new GPS(null, null);

            if (("DELETE".equalsIgnoreCase(hwFields[ImportFields.IDX_LATITUDE])
                || "NULL".equalsIgnoreCase(hwFields[ImportFields.IDX_LATITUDE]))
                && ("DELETE".equalsIgnoreCase(hwFields[ImportFields.IDX_LONGITUDE])
                    || "NULL".equalsIgnoreCase(hwFields[ImportFields.IDX_LONGITUDE]))) {
                // both sets must be DELETE or NULL, or will be invalid data
                // do nothing, use null default
            } else {
                // this could have parse exception I think, which will catch any case but DELETE or NULL (as
                // above if check)
                gps = LocationServiceImpl.getValidLocationFormat(hwFields[ImportFields.IDX_LATITUDE],
                    hwFields[ImportFields.IDX_LONGITUDE]);
            }
            return gps;
        }
        return null;
    }
}