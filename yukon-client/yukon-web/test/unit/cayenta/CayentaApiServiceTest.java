package unit.cayenta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.impl.CayentaApiServiceImpl;

public class CayentaApiServiceTest {
	
	private CayentaApiServiceImpl cayentaApiService;
	
	@BeforeEach
    public void setUp() throws Exception {
		
		cayentaApiService = new CayentaApiServiceImpl();
		cayentaApiService.setSimpleHttpPostServiceFactory(new MockSimpleHttpPostServiceFactory());
		cayentaApiService.setConfigurationSource(new CayentaApiMockConfigurationSource());
    }
	
	class CayentaApiMockConfigurationSource extends MockConfigurationSource {

		@Override
		public int getRequiredInteger(String key) throws UnknownKeyException {
			return 0;
		}
		@Override
		public String getRequiredString(String key) throws UnknownKeyException {
			return "";
		}
	}
	
	@Test
    public void testInvoke() throws Exception {
   
		// SUCCESS TEST
		CayentaLocationInfo locationInfo = cayentaApiService.getLocationInfoForMeterName("1236");
		assertEquals("Minneapolis", locationInfo.getLocationCity(), "Wrong City");
		assertEquals("55199", locationInfo.getLocationZipCode(), "Wrong Zip Code");
		assertEquals("MN", locationInfo.getLocationState(), "Wrong State");
		
		CayentaMeterInfo meterInfo = cayentaApiService.getMeterInfoForMeterName("1236");
		assertEquals("1236", meterInfo.getAccountNumber(), "Wrong Account Number");
		assertEquals("John Smith", meterInfo.getName(), "Wrong Name");
		assertEquals("1013", meterInfo.getLocationNumber(), "Wrong Location Number");
		assertEquals("1000123", meterInfo.getSerialNumber(), "Wrong Serial Number");
		assertEquals("123 Main Street", meterInfo.getAddress(), "Wrong Address");
		
		CayentaPhoneInfo phoneInfo = cayentaApiService.getPhoneInfoForAccountNumber("6321");
		assertEquals("555-867-5309", phoneInfo.getPhoneNumber(), "Wrong Phone Number");
    }
}
