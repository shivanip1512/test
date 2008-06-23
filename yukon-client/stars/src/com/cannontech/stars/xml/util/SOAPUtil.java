package com.cannontech.stars.xml.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.xml.soap.SOAPMessage;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import com.cannontech.stars.xml.StarsMessage;
import com.cannontech.stars.xml.serialize.StarsOperation;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPUtil {

    private SOAPUtil() {
    
    }


    public static SOAPMessage createMessage() throws Exception {
        return new StarsMessage();
    }

    public static SOAPMessage buildSOAPMessage(StarsOperation operation) throws Exception {
        StarsMessage message = new StarsMessage();
        message.setStarsOperation(operation);
        return message;
    }

    public static StarsOperation parseSOAPMsgForOperation(SOAPMessage message) throws Exception {
        if (message instanceof StarsMessage) {
            StarsMessage starsMessage = (StarsMessage) message;
            StarsOperation operation = starsMessage.getStarsOperation();
            return operation;
        }
        throw new UnsupportedOperationException("Only instances of StarsMessage are supported.");
    }
    
    public static void mergeSOAPMsgOfOperation(SOAPMessage msg1, SOAPMessage msg2) throws Exception {
        if ((msg1 instanceof StarsMessage) && (msg2 instanceof StarsMessage)) {
            StarsMessage message1 = (StarsMessage) msg1;
            StarsMessage message2 = (StarsMessage) msg2;
            
            
            StarsOperation starsOperation1 = message1.getStarsOperation();
            StarsOperation starsOperation2 = message2.getStarsOperation();
            
            // nothing to set on old
            if (starsOperation1 == null) {
                message1.setStarsOperation(starsOperation2);
                return;
            }
            
            String className1 = starsOperation1.getClass().getName();
            String className2 = starsOperation2.getClass().getName();
            
            if (!className1.equals(className2)) {
                throw new UnsupportedOperationException("Both StarsOperation's must be of the same class.");
            }
            
            BeanWrapper wrapper1 = PropertyAccessorFactory.forBeanPropertyAccess(starsOperation1);
            BeanWrapper wrapper2 = PropertyAccessorFactory.forBeanPropertyAccess(starsOperation2);
            
            PropertyDescriptor[] descriptors1 = wrapper1.getPropertyDescriptors();
            PropertyDescriptor[] descriptors2 = wrapper2.getPropertyDescriptors();
            
            for (int x = 0; x < descriptors2.length; x++) {
                PropertyDescriptor descriptor1 = descriptors1[x];
                PropertyDescriptor descriptor2 = descriptors2[x];
                
                //get from msg2, set on msg1
                Object value = descriptor2.getReadMethod().invoke(starsOperation2);
                if (value == null) continue;
                
                Method writeMethod = descriptor1.getWriteMethod();
                if (writeMethod != null) { // check if this is a read only property
                    writeMethod.invoke(starsOperation1, value);
                }
            }
            return;
        }
        throw new UnsupportedOperationException("Only instances of StarsMessage are supported.");
    }
    
}