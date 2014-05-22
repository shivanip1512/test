package com.cannontech.web.dev;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/eimTest/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.ENABLE_WEB_DEBUG_PAGES)
public class EimTestController implements ApplicationContextAware {
    
    @Autowired private WebServiceTemplate webServiceTemplate;

    private ApplicationContext applicationContext;

    private Map<String, List<SampleEimFile>> samplesByCategory;
    private List<String> uris;
    private List<String> categories;
    private Map<Integer, SampleEimFile> samplesById;

    @PostConstruct
    public void init() throws IOException {
        samplesByCategory = Maps.newHashMap();
        samplesById = Maps.newHashMap();
        Resource[] resources =
            applicationContext.getResources("classpath*:com/cannontech/yukon/api/*/schemas/xml-templates/*Request*.xml");
        int id = 0;
        for (Resource resource : resources) {
            SampleEimFile sampleEimFile = new SampleEimFile(id, resource);
            List<SampleEimFile> list = samplesByCategory.get(sampleEimFile.getCategory());
            if (list == null) {
                list = Lists.newArrayList();
                samplesByCategory.put(sampleEimFile.getCategory(), list);
            }
            list.add(sampleEimFile);
            samplesById.put(id++, sampleEimFile);
        }

        for (List<SampleEimFile> list : samplesByCategory.values()) {
            Collections.sort(list);
        }

        // If this weren't a debug page or these values weren't constant, we would have used
        // CollationUtils.getCaseInsensitiveOrdering(userContext) and sorted in the request.
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        };
        categories = Lists.newArrayList(samplesByCategory.keySet());
        Collections.sort(categories, comparator);

        uris = Lists.newArrayList();
        uris.add("http://127.0.0.1:8081/api/soap");
        uris.add("http://www.weather.gov/forecasts/xml/SOAP_server/ndfdXMLserver.php");
    }

    @RequestMapping("main")
    public void main(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("samplesByCategory", samplesByCategory);
        model.addAttribute("categories", categories);
        model.addAttribute("uris", uris);
        model.addAttribute("username", userContext.getYukonUser().getUsername());
    }

    @RequestMapping("sampleXml")
    @ResponseBody
    public String sampleXml(int sampleId) throws IOException {
        SampleEimFile sample = samplesById.get(sampleId);
        if (sample == null) {
            throw new RuntimeException("could not find sample " + sampleId);
        }
        Resource fileResource = sample.getResource();
        byte[] fileByteArray = FileCopyUtils.copyToByteArray(fileResource.getInputStream());
        String exampleXml = new String(fileByteArray);
        return exampleXml;
    }

    @RequestMapping("executeRequest")
    @ResponseBody
    public String executeRequest(String uri, String username, String xmlRequest) throws IOException {
        String response;
        try {
            if (xmlRequest.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                response = sendRawSoapRequest(uri, xmlRequest);
            } else {
                response = sendSoapRequest(uri, username, xmlRequest);
            }
        } catch (Exception exception) {
            response = exception.getMessage();
        }
        return response;
    }

    private class UserHeaderSettingCallback implements WebServiceMessageCallback {
        private String username;

        public UserHeaderSettingCallback(String username) {
            this.username = username;
        }

        @Override
        public void doWithMessage(WebServiceMessage message) throws IOException,
                TransformerException {
            QName usernameElementName =
                    new QName("http://yukon.cannontech.com/api", "yukonUser", "api");
            addHeaderToMessage(message, usernameElementName, username, false);
        }
    }

    /**
     * Wrap the xmlRequest in a SOAP envelop and send it.
     */
    private String sendSoapRequest(String uri, String username, String xmlRequest) {
        Source requestSource = new StreamSource(new StringReader(xmlRequest));

        UserHeaderSettingCallback callback = new UserHeaderSettingCallback(username);

        Writer responseWriter = new StringWriter();
        Result responseResult = new StreamResult(responseWriter);

        webServiceTemplate.sendSourceAndReceiveToResult(uri, requestSource, callback,
                                                        responseResult);
        return responseWriter.toString();
    }

    /**
     * This method assumes the xmlRequest is already wrapped in a SOAP envelop and will just open
     * an HTTP connection and send it.  This is useful for testing a customer's pre-wrapped message.
     */
    private String sendRawSoapRequest(String uri, String xmlRequest) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(xmlRequest.length()));
        connection.connect();

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(xmlRequest);
        out.flush();

        InputStreamReader in = new InputStreamReader(connection.getInputStream());
        char[] cbuf = new char[1024 * 64];
        int charsRead;
        StringBuilder response = new StringBuilder();
        while ((charsRead = in.read(cbuf)) != -1) {
            response.append(cbuf, 0, charsRead);
        }

        out.close();
        in.close();
        connection.disconnect();
        return response.toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static void addHeaderToMessage(WebServiceMessage message, QName headerElementName, String headerValue, boolean mustUnderstand) {
        
        SoapMessage soapMessage = ((SoapMessage)message);
        SoapHeader soapHeader = soapMessage.getSoapHeader();

        SoapHeaderElement headerElement = soapHeader.addHeaderElement(headerElementName);
        headerElement.setText(headerValue);
        headerElement.setMustUnderstand(mustUnderstand);
    }
}
