package com.cannontech.notif.outputs;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.*;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.notifications.NotificationConfigurationRole;
import com.cannontech.spring.YukonSpringHook;


/**
 * This class is responsible for transforming the XML Document stored within
 * a Notification object to an XML Document suitable for output. By default,
 * this class uses a XSLT stylesheet to perform the transformation. The stylesheet
 * is located by the concatenation of the following string:
 *    rootDirectory + messageType + "_" + outputType + ".xsl"
 * Where rootDirectory is specified when the class is constructed and should 
 * specify the root of a URL and end with a slash. For instance, to point to
 * a file on the local file system:
 *    file:/C:/eclipse-workspace/notification/sample_templates/
 */
public class NotificationTransformer {
    private Logger log = YukonLogManager.getLogger(NotificationTransformer.class);
    private final String _rootDirectory;
    private String _outputType;

    public NotificationTransformer(String rootDirectory, String outputType) {
        _rootDirectory = rootDirectory;
        _outputType = outputType;
    }
    
    public NotificationTransformer(LiteEnergyCompany energyCompany, String outputType) throws TransformException {
        try {
            LiteYukonUser user = YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(energyCompany.getUserID());
            _rootDirectory = YukonSpringHook.getBean(AuthDao.class).getRolePropertyValueEx(user, NotificationConfigurationRole.TEMPLATE_ROOT) + "/";
            _outputType = outputType;
        } catch (UnknownRolePropertyException e) {
            throw new TransformException("Could not get Template Root Role Property for " + energyCompany, e);
        }
    }
    
    public Document transform(Notification notif) throws TransformException {
        try {
            log.debug("Transforming notification");
            log.debug("javax.xml: " + System.getProperty("javax.xml.transform.TransformerFactory"));
            //System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
            Document result;
            InputStream styleSheet = getStyleSheet(notif.getMessageType(),
                                                   _outputType);
            XSLTransformer trans = new XSLTransformer(styleSheet);
            result = trans.transform(notif.getDocument());
            
            if (log.isDebugEnabled()) {
                log.debug("  Input document:\n" + notif.getXmlString());

                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                String xmlDebug = outputter.outputString(result);
                log.debug("  Output document:\n" + xmlDebug);
            }
            return result;
        } catch (XSLTransformException e) {
            throw new TransformException("Unable to transform " + notif + " to " + _outputType + ".",e);
        }
    }

    private InputStream getStyleSheet(String messageType, String outputType) throws TransformException {
        URL url;
        String fileName = messageType + "_" + outputType + ".xsl";
        if (_rootDirectory.matches("^(http|file):.*")) {
            String urlString = _rootDirectory + fileName;
            log.debug("  Using stylesheet from (absolute): " + urlString);
            try {
                url = new URL(urlString);
            } catch (Exception e) {
                throw new TransformException("Unable to find stylesheet url: " + urlString, e);
            }
        } else {
            // treat as relative to yukon_base (this needs to have been passed in on the cmd line)
            String yukonBase = CtiUtilities.getYukonBase();
            if (yukonBase == null) {
                throw new IllegalStateException("'yukon_base' property has not been defined");
            }
            String fileString = yukonBase + "/" + _rootDirectory + fileName;
            log.debug("  Using stylesheet from (yukon_home): " + fileString);
            File file = new File(fileString);
            try {
                url = file.toURL();
            } catch (Exception e) {
                throw new TransformException("Unable to find stylesheet file: " + fileString, e);
            }
        }
        try {
            return url.openStream();
        } catch (Exception e) {
            throw new TransformException("Unable to read stylesheet: " + url, e);
        }
    }
    
    public class TransformException extends Exception {

        public TransformException(String message, Throwable cause) {
            super(message, cause);
        }

        public TransformException(String message) {
            super(message);
        }
    }

}
