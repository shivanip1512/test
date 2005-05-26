package com.cannontech.notif.outputs;

import java.io.InputStream;
import java.net.URL;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.jdom.Document;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;


public class NotificationTransformer {
    private static NotificationTransformer _self;
    /**
     * This shall contain Notification -> (String -> Document) mapping.
     */
    private WeakHashMap _cache = new WeakHashMap();
    private static final String ROOT_DIRECTORY = null;

    private NotificationTransformer() {
        
    }
    
    public static NotificationTransformer getInstance() {
        if (_self == null) {
            _self = new NotificationTransformer();
        }
        return _self;
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
        String urlString = ROOT_DIRECTORY + messageType + "_" + outputType + ".xsl";
        try {
            URL url = new URL(urlString);
            return url.openStream();
        } catch (Exception e) {
            throw new TransformException("Unable to find stylesheet: " + urlString);
        }
    }
    
    public class TransformException extends Exception {
        private static final long serialVersionUID = 1L;

        public TransformException(String message, Throwable cause) {
            super(message, cause);
        }

        public TransformException(String message) {
            super(message);
        }
    }

}
