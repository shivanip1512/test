package unit.cayenta;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.web.cayenta.model.CayentaLocationInfo;
import com.cannontech.web.cayenta.model.CayentaMeterInfo;
import com.cannontech.web.cayenta.model.CayentaPhoneInfo;
import com.cannontech.web.cayenta.service.impl.CayentaApiServiceImpl;
import com.cannontech.web.cayenta.util.CayentaRequestException;
import com.cannontech.web.simplePost.SimpleHttpPostServiceFactory;

public class CayentaApiServiceTest {
	
	private CayentaApiServiceImpl cayentaApiService;
	private SimpleHttpPostServiceFactory simpleHttpPosterServiceFactory;
	
	@Before
    public void setUp() throws Exception {
		
		simpleHttpPosterServiceFactory = new MockSimpleHttpPostServiceFactory();
		
		cayentaApiService = new CayentaApiServiceImpl();
		cayentaApiService.setSimpleHttpPostServiceFactory(simpleHttpPosterServiceFactory);
    }
	
	@Test
    public void testInvoke() throws Exception {
   
		// SUCCESS TEST
		CayentaLocationInfo locationInfo = cayentaApiService.getLocationInfoForMeterNumber("1236");
		Assert.assertEquals("Wrong City", "Minneapolis", locationInfo.getLocationCity());
		Assert.assertEquals("Wrong Zip Code", "55199", locationInfo.getLocationZipCode());
		Assert.assertEquals("Wrong State", "MN", locationInfo.getLocationState());
		
		CayentaMeterInfo meterInfo = cayentaApiService.getMeterInfoForMeterNumber("1236");
		Assert.assertEquals("Wrong Account Number", "1236", meterInfo.getAccountNumber());
		Assert.assertEquals("Wrong Name", "John Smith", meterInfo.getName());
		Assert.assertEquals("Wrong Location Number", "1013", meterInfo.getLocationNumber());
		Assert.assertEquals("Wrong Serial Number", "1000123", meterInfo.getSerialNumber());
		Assert.assertEquals("Wrong Address", "123 Main Street", meterInfo.getAddress());
		
		CayentaPhoneInfo phoneInfo = cayentaApiService.getPhoneInfoForAccountNumber("6321");
		Assert.assertEquals("Wrong Phone Number", "555-867-5309", phoneInfo.getPhoneNumber());
		
		// SYSTEM FAILURE TEST
		String systemFailureMessage = null;
		try {
			cayentaApiService.getMeterInfoForMeterNumber(MockSimpleHttpPostService.SYSTEM_FAILURE);
		} catch (CayentaRequestException e) {
			systemFailureMessage = e.getMessage();
		}
		Assert.assertEquals("Expected System Failure", "Reply contains system error status.", systemFailureMessage);
		
		// FUNCTION FAILURE TEST
		String functionFailureMessage = null;
		try {
			cayentaApiService.getMeterInfoForMeterNumber(MockSimpleHttpPostService.FUNCTION_FAILURE);
		} catch (CayentaRequestException e) {
			functionFailureMessage = e.getMessage();
		}
		Assert.assertEquals("Expected Function Failure", "Reply contains function error status.", functionFailureMessage);
		
		// HTTP FAILURE TEST
		String httpFailureMessage = null;
		try {
			cayentaApiService.getMeterInfoForMeterNumber(MockSimpleHttpPostService.HTTP_FAILURE);
		} catch (CayentaRequestException e) {
			httpFailureMessage = e.getMessage();
		}
		Assert.assertEquals("Unable to communicate with Cayenta API server.", httpFailureMessage);
		
		// HTTP IO FAILURE TEST
		String httpIoFailureMessage = null;
		try {
			cayentaApiService.getMeterInfoForMeterNumber(MockSimpleHttpPostService.HTTP_IO_FAILURE);
		} catch (CayentaRequestException e) {
			httpIoFailureMessage = e.getMessage();
		}
		Assert.assertEquals("Unable to communicate with Cayenta API server.", httpIoFailureMessage);
    }
}
