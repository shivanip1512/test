package com.cannontech.web.editor.point;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.cannontech.database.data.point.PointArchiveType;


/**
 * This should be removed with PointForm.java
 *
 */
@Deprecated
public class ArchiveTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        PointArchiveType archiveType = PointArchiveType.getByDbString(value);
        return archiveType;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        
        PointArchiveType archiveType;
        if (value instanceof String) {
            archiveType = PointArchiveType.getByDbString((String) value);
        } else if (value instanceof PointArchiveType) {
            archiveType = (PointArchiveType) value;
        } else {
            throw new RuntimeException("Joe Did it");
        }
        return archiveType.getPointArchiveTypeName();
    }

}
