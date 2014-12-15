package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.OutputColumn;
import com.cannontech.web.picker.Picker;
import com.cannontech.web.picker.service.PickerFactory;
import com.google.common.collect.Lists;

@Configurable(value="pickerPropertiesTagPrototype", autowire=Autowire.BY_NAME)
public class PickerPropertiesTag extends YukonTagSupport {
    
    @Autowired private PickerFactory pickerFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private String var;
    private String type;
    private Property property;
    private enum Property { ID_FIELD_NAME, OUTPUT_COLUMNS }
    
    @Override
    public void doTag() throws IOException {
        
        Picker<?> picker = pickerFactory.getPicker(type);
        
        String result = null;
        
        if (property == Property.ID_FIELD_NAME) {
            result = StringEscapeUtils.escapeEcmaScript(picker.getIdFieldName());
        } else if (property == Property.OUTPUT_COLUMNS) {
            result = JsonUtils.toJson(localize(picker.getOutputColumns(), getUserContext()));
        }
        
        if (StringUtils.isNotBlank(var)) {
            getJspContext().setAttribute(var, result);
        } else {
            getJspContext().getOut().print(result);
        }
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setProperty(String property) {
        this.property = Property.valueOf(property);
    }
    
    /**
     * The ultimate use of these output columns is in Javascript
     * (yukon.ui.util.createTable via yukon.picker.js) to determine the label (title) and value
     * (field is the name of the property on the row object) of each column.
     */
    private List<LocalOutputColumn> localize(List<OutputColumn> columns, YukonUserContext userContext) {
        
        List<LocalOutputColumn> localized = Lists.newArrayList();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        for (OutputColumn column : columns) {
            String title = accessor.getMessage(column.getTitle());
            String field = column.getField();
            int maxCharsDisplayed = column.getMaxCharsDisplayed();
            localized.add(new LocalOutputColumn(field, title, maxCharsDisplayed));
        }
        
        return localized;
    }

    public static final class LocalOutputColumn {
        
        private final String field;
        private final String title;
        private final int maxCharsDisplayed;
        
        private LocalOutputColumn(String field, String title, int maxCharsDisplayed) {
            this.field = field;
            this.title = title;
            this.maxCharsDisplayed = maxCharsDisplayed;
        }
        
        public String getField() {
            return field;
        }
        
        public String getTitle() {
            return title;
        }
        
        public int getMaxCharsDisplayed() {
            return maxCharsDisplayed;
        }
    }
    
}