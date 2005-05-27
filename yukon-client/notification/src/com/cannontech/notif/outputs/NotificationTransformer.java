package com.cannontech.notif.outputs;

import java.io.InputStream;
import java.net.URL;
import java.util.TreeMap;
import java.util.WeakHashMap;

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
     * This shall contain Notification -> (String -> Document) mapping.
     */
    private WeakHashMap _cache = new WeakHashMap();
    private final String _rootDirectory;

    public NotificationTransformer(String rootDirectory) {
        _rootDirectory = rootDirectory;
        
    }
    
    public Document transform(Notification notif, String outputType) throws TransformException {
        try {
            TreeMap outputMap;
            if (!_cache.containsKey(notif)) {
                outputMap = new TreeMap();
                _cache.put(notif, outputMap);
            } else {
                outputMap = (TreeMap) _cache.get(notif);
            }
            Document result;
            if (outputMap.containsKey(outputType)) {
                result = (Document) outputMap.get(outputType);
            } else {
                InputStream styleSheet = getStyleSheet(notif.getMessageType(),
                                                       outputType);
                XSLTransformer trans = new XSLTransformer(styleSheet);
                result = trans.transform(notif.getDocument());
                outputMap.put(outputType, result);
            }
            return result;
        } catch (XSLTransformException e) {
            throw new TransformException("Unable to transform " + notif + " to " + outputType + ".",e);
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
