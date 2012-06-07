package com.cannontech.stars.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.service.StarsControllableDeviceDTOConverter;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.stars.ws.StarsControllableDeviceDTO;
import com.cannontech.util.ServletUtil;

public class StarsControllableDeviceDTOConverterImpl implements
		StarsControllableDeviceDTOConverter {

	private YukonListDao yukonListDao;
	
	@Override
	public StarsControllableDeviceDTO createNewDto(String accountNumber, String[] hwFields, LiteStarsEnergyCompany energyCompany) throws ParseException {
		
		StarsControllableDeviceDTO dto = new StarsControllableDeviceDTO();
		dto.setAccountNumber(accountNumber);
		
		dto.setDeviceLabel(hwFields[ImportManagerUtil.IDX_DEVICE_LABEL]);
		dto.setDeviceType(hwFields[ImportManagerUtil.IDX_DEVICE_TYPE]);
		
		dto.setFieldInstallDate(new Date());
		if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_INSTALL_DATE])) {
			Date installDate = ServletUtil.parseDateStringLiberally(hwFields[ImportManagerUtil.IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone());
			if (installDate == null) {
				installDate = StarsUtils.starsDateFormat.parse(hwFields[ImportManagerUtil.IDX_INSTALL_DATE]);
			}
			dto.setFieldInstallDate(installDate);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_REMOVE_DATE])) {
			Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportManagerUtil.IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone());
			if (removeDate == null) {
				removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportManagerUtil.IDX_REMOVE_DATE]);
			}
			dto.setFieldRemoveDate(removeDate);
		}
		
		dto.setSerialNumber(hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
		dto.setServiceCompanyName(hwFields[ImportManagerUtil.IDX_SERVICE_COMPANY]);
		
		return dto;
	}

	
	@Override
	public StarsControllableDeviceDTO getDtoForHardware(
			String accountNumber, LiteInventoryBase liteInv,
			LiteStarsEnergyCompany energyCompany) {
		
		// init dto with existing hardware
		StarsControllableDeviceDTO dto = new StarsControllableDeviceDTO();
		dto.setAccountNumber(accountNumber);
		dto.setDeviceLabel(liteInv.getDeviceLabel());
		int devTypeId = ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID();
		YukonListEntry yukonListEntry = yukonListDao.getYukonListEntry(devTypeId);
		String typeStr = yukonListEntry.getEntryText();
		dto.setDeviceType(typeStr);
		dto.setFieldInstallDate(new Date(liteInv.getInstallDate()));
		dto.setFieldRemoveDate(new Date(liteInv.getRemoveDate()));
		if (liteInv instanceof LiteStarsLMHardware) {
			dto.setSerialNumber(((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber());
		}
		int installationCompanyId = liteInv.getInstallationCompanyID();
		List<LiteServiceCompany> companies = energyCompany.getAllServiceCompanies();
		for (LiteServiceCompany company : companies) {
			if (company.getCompanyID() == installationCompanyId) {
				dto.setServiceCompanyName(company.getCompanyName());
				break;
			}
		}
		
		return dto;
	}
	
	@Override
	public void updateDtoWithHwFields (StarsControllableDeviceDTO dto, String[] hwFields, LiteStarsEnergyCompany energyCompany) throws ParseException {
		
		if (!StringUtils.isEmpty(hwFields[ImportManagerUtil.IDX_DEVICE_LABEL])) {
			dto.setDeviceLabel(hwFields[ImportManagerUtil.IDX_DEVICE_LABEL]);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_DEVICE_TYPE])) {
			dto.setDeviceType(hwFields[ImportManagerUtil.IDX_DEVICE_TYPE]);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_INSTALL_DATE])) {
			Date installDate = ServletUtil.parseDateStringLiberally(hwFields[ImportManagerUtil.IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone());
			if (installDate == null) {
				installDate = StarsUtils.starsDateFormat.parse(hwFields[ImportManagerUtil.IDX_INSTALL_DATE]);
			}
			dto.setFieldInstallDate(installDate);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_REMOVE_DATE])) {
			Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportManagerUtil.IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone());
			if (removeDate == null) {
				removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportManagerUtil.IDX_REMOVE_DATE]);
			}
			dto.setFieldRemoveDate(removeDate);
		}
		
		if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_SERIAL_NO])) {
			dto.setSerialNumber(hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
		}
		
		if (!StringUtils.isEmpty(hwFields[ImportManagerUtil.IDX_SERVICE_COMPANY])) {
			dto.setServiceCompanyName(hwFields[ImportManagerUtil.IDX_SERVICE_COMPANY]);
		}
	}
	

	@Autowired
	public void setYukonListDao(YukonListDao yukonListDao) {
		this.yukonListDao = yukonListDao;
	}
	
}
