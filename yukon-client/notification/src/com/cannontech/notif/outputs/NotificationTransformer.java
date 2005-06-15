package com.cannontech.notif.outputs;

import java.io.InputStream;
import java.net.URL;

import org.jdom.Document;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;


/**
 * This class is responsible for transforming the XML Document stored within
 * a Notification object to an XML Document suitable for output. By default,
 * this class uses a XSLT stylesheet to perform the transformation. The stylesheet
 * is located by the concatenation of the following string:
 *    rootDirectory + messageType + "_" + outputType + ".xsl"
 * Where rootDirectory is specified when the class is constructed and should 
 * specify the root of a URL and end with a slash. For instance, to point to
 * a file on the local file system:
 *    file://
 */
public class NotificationTransformer {
    /**
     * This shall contain Notification -> Document mapping.
     */
    private final String _rootDirectory;
    private String _outputType;

    public NotificationTransformer(String rootDirectory, String outputType) {
        _rootDirectory = rootDirectory;
        _outputType = outputType;
    }
    
    public Document transform(Notification notif) throws TransformException {
        try {
            Document result;
            InputStream styleSheet = getStyleSheet(notif.getMessageType(),
                                                   _outputType);
            XSLTransformer trans = new XSLTransformer(styleSheet);
            result = trans.transform(notif.getDocument());
            return result;
        } catch (XSLTransformException e) {
            throw new TransformException("Unable to transform " + notif + " to " + _outputType + ".",e);
        }
    }

    private InputStream getStyleSheet(String messageType, String outputType) throws TransformException {
        String urlString = _rootDirectory + messageType + "_" + outputType + ".xsl";
        try {
            URL url = new URL(urlString);
            return url.openStream();
        } catch (Exception e) {
            throw new TransformException("Unable to find stylesheet: " + urlString);
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
