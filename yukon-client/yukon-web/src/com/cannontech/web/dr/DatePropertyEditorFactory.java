package com.cannontech.web.dr;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.user.YukonUserContext;

public class DatePropertyEditorFactory {
    private DateFormattingService dateFormattingService;

    private class DatePropertyEditor extends PropertyEditorSupport {
        private DateFormattingService.DateOnlyMode dateOnlyMode;
        private YukonUserContext userContext;

        DatePropertyEditor(DateFormattingService.DateOnlyMode dateOnlyMode,
                YukonUserContext userContext) {
            this.dateOnlyMode = dateOnlyMode;
            this.userContext = userContext;
        }

        public void setAsText(String dateStr) throws IllegalArgumentException {
            try {
                setValue(dateFormattingService.flexibleDateParser(dateStr,
                                                                  dateOnlyMode,
                                                                  userContext));
            } catch (ParseException exception) {
                throw new IllegalArgumentException("Could not parse date", exception);
            }
        }

        public String getAsText() {
            Date date = (Date) getValue();
            DateFormatEnum dateFormat = dateOnlyMode == DateOnlyMode.START_OF_DAY
                ? DateFormatEnum.DATE : DateFormatEnum.DATE_MIDNIGHT_PREV;
            return date == null
                ? "" : dateFormattingService.formatDate(date, dateFormat, userContext);
        }
    }

    public PropertyEditor getPropertyEditor(DateFormattingService.DateOnlyMode dateOnlyMode,
            YukonUserContext userContext) {
        return new DatePropertyEditor(dateOnlyMode, userContext);
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}
