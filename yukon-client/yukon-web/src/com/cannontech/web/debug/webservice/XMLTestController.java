package com.cannontech.web.debug.webservice;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.JsonView;

public class XMLTestController extends MultiActionController {
    
	private WebServiceTemplate webServiceTemplate;
	private Resource[] exampleRequestXmls;
	
	// HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	
    	ModelAndView mav = new ModelAndView("webservice/home.jsp");
    	mav.addObject("exampleFileNames", getExampleFileNames());
    	mav.addObject("uriNames", getUriNames());
    	mav.addObject("uri", getUriNames().get(0));
    	mav.addObject("userName", userContext.getYukonUser().getUsername());
    	
    	return mav;
    }
    
    // XML TEMPLATE CHANGE
    public ModelAndView xmlTemplateChange(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
    	int xmlTemplateIdx = ServletRequestUtils.getRequiredIntParameter(request, "xmlTemplateIdx");
    	
    	Resource fileResource = exampleRequestXmls[xmlTemplateIdx - 1];
    	byte[] fileByteArray = FileCopyUtils.copyToByteArray(fileResource.getInputStream());
		String exampleXml = new String(fileByteArray);
    	
        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("exampleXml", exampleXml);
        
        return mav;
    }
    
    public ModelAndView resetUserName(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
    	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	
        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("userName", userContext.getYukonUser().getUsername());
        
        return mav;
    }

    // EXECUTE REQUEST
    public ModelAndView executeRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	ModelAndView mav = new ModelAndView("webservice/home.jsp");
    	
    	String xmlRequest = ServletRequestUtils.getRequiredStringParameter(request, "xmlRequest");
    	String selectedTemplateIndex = ServletRequestUtils.getRequiredStringParameter(request, "selectedTemplateIndex");
    	String selectedUriIndex = ServletRequestUtils.getRequiredStringParameter(request, "selectedUriIndex");
    	String uri = ServletRequestUtils.getRequiredStringParameter(request, "uri");
    	String userName = ServletRequestUtils.getStringParameter(request, "userName", "");
    	
    	String xmlResponse;
    	try {
    	
	        // source
	        Source requestSource = new StreamSource(new StringReader(xmlRequest));
	        
	        // request call back
	        UserHeaderSettingCallback callback = new UserHeaderSettingCallback(userName);
	        
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
    	mav.addObject("userName", userName);
    	
        return mav;
    }
    
    
    private class UserHeaderSettingCallback implements WebServiceMessageCallback {

    	private String userName;
    	
    	public UserHeaderSettingCallback(String userName) {
    	
    		this.userName = userName;
    	}
    	
		@Override
		public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
			
			QName headerElementName = new QName("http://yukon.cannontech.com/api", "yukonUser", "api");
			XmlUtils.addHeaderToMessage(message, headerElementName, this.userName, false);
		}
    }

    
    private List<String> getExampleFileNames() throws IOException {
    	
    	List<String> exampleFileNames = new ArrayList<String>();
    	for (Resource r : exampleRequestXmls) {
    		exampleFileNames.add(r.getFilename());
    	}
    	
    	return exampleFileNames;
    }
    
    private List<String> getUriNames() throws IOException {
    	
    	List<String> uriNames = new ArrayList<String>();
    	uriNames.add("http://127.0.0.1:8081/api/soap");
    	uriNames.add("http://www.weather.gov/forecasts/xml/SOAP_server/ndfdXMLserver.php");
			
    	return uriNames;
    }
    
    @Autowired
    public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}
    
    public void setExampleRequestXmls(Resource[] exampleRequestXmls) {
		this.exampleRequestXmls = exampleRequestXmls;
	}
}
