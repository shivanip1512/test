package loadControl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ProgramStartRequestEndpoint {

    private LoadControlService loadControlService;
    
    private XPath programNameExpression;
    private XPath startTimeExpression;
    private XPath stopTimeExpression;
    
    @PostConstruct
    public void initialize() throws JDOMException {
        
        programNameExpression = XPath.newInstance("//y:programName");
        programNameExpression.addNamespace(YukonXml.getYukonNamespace());
        startTimeExpression = XPath.newInstance("//y:startTime");
        startTimeExpression.addNamespace(YukonXml.getYukonNamespace());
        stopTimeExpression = XPath.newInstance("//y:stopTime");
        stopTimeExpression.addNamespace(YukonXml.getYukonNamespace());
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStartRequest")
    protected Element invoke(Element programStartRequest) throws Exception {
        
        String programName = programNameExpression.valueOf(programStartRequest);
        String startTimeStr = programNameExpression.valueOf(programStartRequest);
        String stopTimeStr = programNameExpression.valueOf(programStartRequest);
        
        Namespace ns = YukonXml.getYukonNamespace();
        DateFormat df = XmlUtils.getDateFormat();
        Date startTime = df.parse(startTimeStr);
        Date stopTime = df.parse(stopTimeStr);
        
        ProgramStatus programStatus = loadControlService.startControlByProgramName(programName, startTime, stopTime, false, true);
        
        Element resp = new Element("programStartResponse", ns);
        resp.addContent(XmlUtils.createStringElement("programName", ns, programName));
        
        List<String> violations = new ArrayList<String>();
        violations.addAll(programStatus.getConstraintViolations());
        if (violations.size() > 0) {
            
            resp.addContent(XmlUtils.createStringElement("errorCode", ns, "1"));
            resp.addContent(XmlUtils.createStringElement("errorDescription", ns, StringUtils.join(violations, ",")));
        } else {
            resp.addContent(XmlUtils.createStringElement("errorCode", ns, ""));
            resp.addContent(XmlUtils.createStringElement("errorDescription", ns, ""));
        }
        
        return resp;
    }
    
   
    
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}
