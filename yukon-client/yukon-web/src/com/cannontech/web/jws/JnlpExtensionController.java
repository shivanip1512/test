package com.cannontech.web.jws;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.web.servlet.ModelAndView;

public class JnlpExtensionController extends JnlpControllerBase {
    public JnlpExtensionController() {
        setCacheSeconds(0);
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("application/x-java-jnlp-file");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");

        Document doc = new Document();
        Element jnlpElem = new Element("jnlp");
        doc.addContent(jnlpElem);
        jnlpElem.setAttribute("spec", "1.0+");
        // determine code base
        String url = request.getRequestURL().toString();
        String codebase = url.substring(0, url.lastIndexOf("/")) + "/";
        jnlpElem.setAttribute("codebase", codebase);

        Element securityElem = new Element("security");
        jnlpElem.addContent(securityElem);
        securityElem.addContent(new Element("all-permissions"));

        Element infoElem = new Element("information");
        jnlpElem.addContent(infoElem);
        infoElem.addContent(new Element("title").setText("Yukon(TODO) " + title));
        infoElem.addContent(new Element("vendor").setText("Cooper Industries plc."));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.cannontech.com/"));
        infoElem.addContent(new Element("description").setText(description));

        Element resourcesElem = new Element("resources");
        jnlpElem.addContent(resourcesElem);

        for (String jarFile : jars) {
            Element jarElem = new Element("jar");
            jarElem.setAttribute("href", jarFile);
            resourcesElem.addContent(jarElem);
        }

        Element compElem = new Element("component-desc");
        jnlpElem.addContent(compElem);

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        ServletOutputStream responseOutStream = response.getOutputStream();
        out.output(doc, responseOutStream);
        return null;
    }
}
