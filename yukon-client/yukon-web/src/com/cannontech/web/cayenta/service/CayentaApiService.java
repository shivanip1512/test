package com.cannontech.web.cayenta.service;

import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.util.CayentaRequestException;

public interface CayentaApiService {

	public CayentaLocationInfo getLocationInfoForMeterName(String meterName) throws CayentaRequestException;
	
	public CayentaMeterInfo getMeterInfoForMeterName(String meterName) throws CayentaRequestException;
	
	public CayentaPhoneInfo getPhoneInfoForAccountNumber(String accountNumber) throws CayentaRequestException;
}
