package com.cannontech.web.debug.webservice;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.JsonView;

public class XMLTestController extends MultiActionController {
    
	private WebServiceTemplate webServiceTemplate;
	
	// HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	ModelAndView mav = new ModelAndView("webservice/home.jsp");
    	mav.addObject("exampleFileNames", getExampleFileNames());
    	mav.addObject("uriNames", getUriNames());
    	mav.addObject("uri", getUriNames().get(0));
    	
    	return mav;
    }
    
    // XML TEMPLATE CHANGE
    public ModelAndView xmlTemplateChange(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
    	String xmlTemplate = ServletRequestUtils.getRequiredStringParameter(request, "xmlTemplate");
    	
    	Resource templateResource = new ClassPathResource("/com/cannontech/web/debug/webservice/exampleXml/" + xmlTemplate, this.getClass());
    	String exampleXml = FileUtils.readFileToString(templateResource.getFile());
    	
        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("exampleXml", exampleXml);
        
        return mav;
    }

    // EXECUTE REQUEST
    public ModelAndView executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	ModelAndView mav = new ModelAndView("webservice/home.jsp");
    	
    	String xmlRequest = ServletRequestUtils.getRequiredStringParameter(request, "xmlRequest");
    	String selectedTemplateIndex = ServletRequestUtils.getRequiredStringParameter(request, "selectedTemplateIndex");
    	String selectedUriIndex = ServletRequestUtils.getRequiredStringParameter(request, "selectedUriIndex");
    	String uri = ServletRequestUtils.getRequiredStringParameter(request, "uri");
    	
    	String xmlResponse;
    	try {
    	
	        // source
	        Source requestSource = new StreamSource(new StringReader(xmlRequest));
	        
	        // request call back
	        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
	        UserHeaderSettingCallback callback = new UserHeaderSettingCallback(userContext);
	        
	        // result
	        Writer responseWriter = new StringWriter();
	        Result responseResult = new StreamResult(responseWriter);
	
	        // send
	    	webServiceTemplate.sendSourceAndReceiveToResult(uri, requestSource, callback, responseResult);
	    	
	    	xmlResponse = responseWriter.toString();
	    	
    	} catch (Exception e) {
    		
    		xmlResponse = e.getMessage();
    	}
    	
    	mav.addObject("exampleFileNames", getExampleFileNames());
    	mav.addObject("uriNames", getUriNames());
    	mav.addObject("xmlRequest", xmlRequest);
    	mav.addObject("uri", uri);
    	mav.addObject("xmlResponse", xmlResponse);
    	mav.addObject("selectedTemplateIndex", selectedTemplateIndex);
    	mav.addObject("selectedUriIndex", selectedUriIndex);
    	
        return mav;
    }
    
    
    private class UserHeaderSettingCallback implements WebServiceMessageCallback {

    	private YukonUserContext userContext;
    	
    	public UserHeaderSettingCallback(YukonUserContext userContext) {
    	
    		this.userContext = userContext;
    	}
    	
		@Override
		public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
			
			SoapMessage soapMessage = ((SoapMessage)message);
			SoapHeader soapHeader = soapMessage.getSoapHeader();

			QName userElementName = new QName("http://yukon.cannontech.com/api", "user", "api");
			SoapHeaderElement userHeaderElement = soapHeader.addHeaderElement(userElementName);
			userHeaderElement.setText(this.userContext.getYukonUser().getUsername());
			userHeaderElement.setMustUnderstand(false);
		}
    }

    
    
    
    
    
    @SuppressWarnings("unchecked")
    private List<String> getExampleFileNames() throws IOException {
    	
    	Resource exampleXmlFolderResource = new ClassPathResource("/com/cannontech/web/debug/webservice/exampleXml", this.getClass());
    	File exampleXmlDirectoryFile = exampleXmlFolderResource.getFile();
    	Collection<File> listFiles = (Collection<File>)FileUtils.listFiles(exampleXmlDirectoryFile, FileFileFilter.FILE, DirectoryFileFilter.INSTANCE);
    	List<String> exampleFileNames = new ArrayList<String>();
    	for (File f : listFiles) {
    		exampleFileNames.add(f.getName());
    	}
    	return exampleFileNames;
    }
    
    private List<String> getUriNames() throws IOException {
    	
    	List<String> uriNames = new ArrayList<String>();
    	uriNames.add("http://127.0.0.1:8081/api/soap/loadManagement");
    	uriNames.add("http://127.0.0.1:8081/api/soap/stars");
    	uriNames.add("http://127.0.0.1:8081/api/soap/account");
    	uriNames.add("http://www.weather.gov/forecasts/xml/SOAP_server/ndfdXMLserver.php");
			
    	return uriNames;
    }
    
    @Autowired
    public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}
}
