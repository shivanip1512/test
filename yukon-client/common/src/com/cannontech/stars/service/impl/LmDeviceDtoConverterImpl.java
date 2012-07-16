package com.cannontech.stars.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.service.LmDeviceDtoConverter;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.util.ImportFields;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.util.ServletUtil;

public class LmDeviceDtoConverterImpl implements LmDeviceDtoConverter {

	@Autowired private YukonListDao yukonListDao;
	
	@Override
	public LmDeviceDto createNewDto(String accountNo, String[] hwFields, LiteStarsEnergyCompany lsec) throws ParseException {
		
		LmDeviceDto dto = new LmDeviceDto();
		dto.setAccountNumber(accountNo);
		
		dto.setDeviceLabel(hwFields[ImportFields.IDX_DEVICE_LABEL]);
		String deviceTypeText = hwFields[ImportFields.IDX_DEVICE_TYPE];
        dto.setDeviceType(deviceTypeText);
        
		dto.setFieldInstallDate(new Date());
		if (!StringUtils.isBlank(hwFields[ImportFields.IDX_INSTALL_DATE])) {
			Date installDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_INSTALL_DATE], lsec.getDefaultTimeZone());
			if (installDate == null) {
				installDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_INSTALL_DATE]);
			}
			dto.setFieldInstallDate(installDate);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportFields.IDX_REMOVE_DATE])) {
			Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_REMOVE_DATE], lsec.getDefaultTimeZone());
			if (removeDate == null) {
				removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_REMOVE_DATE]);
			}
			dto.setFieldRemoveDate(removeDate);
		}
		
		dto.setSerialNumber(hwFields[ImportFields.IDX_SERIAL_NO]);
		dto.setServiceCompanyName(hwFields[ImportFields.IDX_SERVICE_COMPANY]);
		
		return dto;
	}

	
	@Override
	public LmDeviceDto getDtoForHardware(String accountNo, LiteInventoryBase lib, LiteStarsEnergyCompany lsec) {
		
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
		List<LiteServiceCompany> companies = lsec.getAllServiceCompanies();
		for (LiteServiceCompany company : companies) {
			if (company.getCompanyID() == installationCompanyId) {
				dto.setServiceCompanyName(company.getCompanyName());
				break;
			}
		}
		
		return dto;
	}
	
	@Override
	public void updateDtoWithHwFields (LmDeviceDto dto, String[] hwFields, LiteStarsEnergyCompany energyCompany) throws ParseException {
		
		if (!StringUtils.isEmpty(hwFields[ImportFields.IDX_DEVICE_LABEL])) {
			dto.setDeviceLabel(hwFields[ImportFields.IDX_DEVICE_LABEL]);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportFields.IDX_DEVICE_TYPE])) {
			dto.setDeviceType(hwFields[ImportFields.IDX_DEVICE_TYPE]);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportFields.IDX_INSTALL_DATE])) {
			Date installDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone());
			if (installDate == null) {
				installDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_INSTALL_DATE]);
			}
			dto.setFieldInstallDate(installDate);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportFields.IDX_REMOVE_DATE])) {
			Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone());
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
	}
	
}