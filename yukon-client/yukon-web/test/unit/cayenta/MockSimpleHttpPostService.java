package unit.cayenta;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.web.cayenta.util.CayentaRequestException;
import com.cannontech.web.cayenta.util.CayentaXmlUtils;
import com.cannontech.web.simplePost.SimpleHttpPostService;

class MockSimpleHttpPostService implements SimpleHttpPostService {
    
	public static String SYSTEM_FAILURE = "SYSTEM_FAILURE";
	public static String FUNCTION_FAILURE = "FUNCTION_FAILURE";
	public static String HTTP_FAILURE = "HTTP_FAILURE";
	public static String HTTP_IO_FAILURE = "HTTP_IO_FAILURE";
	
	// Mocks the Cayenta API server.
	// Receive a request, parse out the request type and send an example response back.
	// In some cases, the meter number in the request is used to indicate that some special response should be returned.
	@Override
	public String postValue(String name, String value) throws IOException, HttpException {
		
		String response = "";
		String meterNumber = "";
		try {
			
			// get name of request type, template
			Resource requestResource = new ByteArrayResource(value.getBytes());
			Element requestElement = XmlUtils.createElementFromResource(requestResource);
			String requestTypeName = CayentaXmlUtils.getMethodName(requestElement);
			SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(requestElement);
			
			// failure responses
			meterNumber = requestTemplate.evaluateAsString("/Request/" + requestTypeName + "/Params/METER_NO");
			if (SYSTEM_FAILURE.equals(meterNumber) && "GetInstalledMeters".equals(requestTypeName)) {
				return getStringFromExampleFile("/unit/cayenta/testXml/ExampleGetInstalledMetersReply_SystemFailure.xml");
			} else if (FUNCTION_FAILURE.equals(meterNumber) && "GetInstalledMeters".equals(requestTypeName)) {
				return getStringFromExampleFile("/unit/cayenta/testXml/ExampleGetInstalledMetersReply_FunctionFailure.xml");
			} else if (HTTP_FAILURE.equals(meterNumber)) {
				// throw a HttpException outside this try/catch
			} else if (HTTP_IO_FAILURE.equals(meterNumber)) {
				// throw an IOException outside this try/catch
			} else {
			
				// GetLocation
				if ("GetLocation".equals(requestTypeName)) {
					return getStringFromExampleFile("/unit/cayenta/testXml/ExampleGetLocationReply_Success.xml");
				}
				
				// GetMeter
				if ("GetInstalledMeters".equals(requestTypeName)) {
					return getStringFromExampleFile("/unit/cayenta/testXml/ExampleGetInstalledMetersReply_Success.xml");
				}
				
				// GetAccountPhone
				if ("GetAccountPhone".equals(requestTypeName)) {
					return getStringFromExampleFile("/unit/cayenta/testXml/ExampleGetAccountPhoneReply_Success.xml");
				}
			}
			
		// these exceptions are not part of test, they are just to cover the operations performed to parse 
		// the request and send a mock response back code within this try should be kept error-free ;)
		} catch (IOException e) {
		} catch (JDOMException e) {
		} catch (CayentaRequestException e) {
		}
		
		// mock http errors
		if (HTTP_FAILURE.equals(meterNumber)) {
			throw new HttpException("");
		} else if(HTTP_IO_FAILURE.equals(meterNumber)) {
			throw new IOException("");
		}
		
		return response;
	}
	
	private String getStringFromExampleFile(String path) throws IOException {
		
		Resource requestSchemaResource = new ClassPathResource(path, this.getClass());
		StringWriter writer = new StringWriter();
		IOUtils.copy(requestSchemaResource.getInputStream(), writer);
		String text = writer.toString();
		return text;
	}
}