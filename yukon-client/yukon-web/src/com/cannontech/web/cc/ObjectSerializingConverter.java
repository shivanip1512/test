package com.cannontech.web.cc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.codec.binary.Base64;

import com.cannontech.clientutils.CTILogger;

public class ObjectSerializingConverter implements Converter {

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) throws ConverterException {
        try {
            byte[] escapedBytes = value.getBytes();
            byte[] rawBytes = Base64.decodeBase64(escapedBytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(rawBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object result = ois.readObject();
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            CTILogger.error("Unable to unserialize object", e);
        }
        
        return null;
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.flush();
            byte[] rawByteArray = baos.toByteArray();
            byte[] cleanBytes = Base64.encodeBase64(rawByteArray);
            String cleanString = new String(cleanBytes);
            return cleanString;
        } catch (Exception e) {
            CTILogger.error("Unable to serialize object", e);
        }
        return null;
    }

}
