package unit.cayenta;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.impl.CayentaApiServiceImpl;

public class CayentaApiServiceTest {
	
	private CayentaApiServiceImpl cayentaApiService;
	
	@Before
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
		Assert.assertEquals("Wrong City", "Minneapolis", locationInfo.getLocationCity());
		Assert.assertEquals("Wrong Zip Code", "55199", locationInfo.getLocationZipCode());
		Assert.assertEquals("Wrong State", "MN", locationInfo.getLocationState());
		
		CayentaMeterInfo meterInfo = cayentaApiService.getMeterInfoForMeterName("1236");
		Assert.assertEquals("Wrong Account Number", "1236", meterInfo.getAccountNumber());
		Assert.assertEquals("Wrong Name", "John Smith", meterInfo.getName());
		Assert.assertEquals("Wrong Location Number", "1013", meterInfo.getLocationNumber());
		Assert.assertEquals("Wrong Serial Number", "1000123", meterInfo.getSerialNumber());
		Assert.assertEquals("Wrong Address", "123 Main Street", meterInfo.getAddress());
		
		CayentaPhoneInfo phoneInfo = cayentaApiService.getPhoneInfoForAccountNumber("6321");
		Assert.assertEquals("Wrong Phone Number", "555-867-5309", phoneInfo.getPhoneNumber());
    }
}
