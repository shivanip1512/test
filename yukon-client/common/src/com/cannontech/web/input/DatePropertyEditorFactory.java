package com.cannontech.web.input;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.DataBinder;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.user.YukonUserContext;

public class DatePropertyEditorFactory {
    private DateFormattingService dateFormattingService;
    
    public static enum BlankMode {ERROR, NULL, CURRENT};

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
                ? "" : dateFormattingService.format(date, dateFormat, userContext);
        }
    }

    private class DateTimePropertyEditor extends PropertyEditorSupport {
        private DateFormatEnum dateFormat;
        private YukonUserContext userContext;

        private DateTimePropertyEditor(DateFormatEnum dateFormat,
                YukonUserContext userContext) {
            this.dateFormat = dateFormat;
            this.userContext = userContext;
        }

        public void setAsText(String dateStr) throws IllegalArgumentException {
            try {
                setValue(dateFormattingService.flexibleDateParser(dateStr,
                                                                  userContext));
            } catch (ParseException exception) {
                throw new IllegalArgumentException("Could not parse date", exception);
            }
        }

        public String getAsText() {
            Date date = (Date) getValue();
            return date == null
                ? "" : dateFormattingService.format(date, dateFormat, userContext);
        }
    }

    private class LocalTimePropertyEditor extends PropertyEditorSupport {
        private DateFormatEnum timeFormat;
        private YukonUserContext userContext;
        private BlankMode blankMode;

        private LocalTimePropertyEditor(DateFormatEnum timeFormat,
                YukonUserContext userContext,
                BlankMode blankMode) {
            this.timeFormat = timeFormat;
            this.userContext = userContext;
            this.blankMode = blankMode;
        }

        @Override
        public void setAsText(String localTimeStr) throws IllegalArgumentException {
            if (StringUtils.isBlank(localTimeStr)) {
                switch (blankMode) {
                case CURRENT:
                    setValue(new LocalTime(userContext.getJodaTimeZone()));
                    break;
                case ERROR:
                    throw new IllegalArgumentException("Could not parse blank time");
                case NULL:
                    setValue(null);
                    break;
                }
                return;
            }
            
            try {
                setValue(dateFormattingService.parseLocalTime(localTimeStr,
                                                              userContext));
            } catch (ParseException exception) {
                throw new IllegalArgumentException("Could not parse date", exception);
            }
        }

        @Override
        public String getAsText() {
            LocalTime localTime = (LocalTime) getValue();
            return localTime == null
                ? "" : dateFormattingService.format(localTime, timeFormat, userContext);
        }
    }

    private class LocalDatePropertyEditor extends PropertyEditorSupport {
        private DateFormatEnum dateFormat;
        private YukonUserContext userContext;
        private BlankMode blankMode;
        
        private LocalDatePropertyEditor(DateFormatEnum dateFormat,
                                         YukonUserContext userContext, BlankMode blankMode) {
            this.dateFormat = dateFormat;
            this.userContext = userContext;
            this.blankMode = blankMode;
        }

        @Override
        public void setAsText(String localDateStr) throws IllegalArgumentException {
            if (StringUtils.isBlank(localDateStr)) {
                switch (blankMode) {
                case CURRENT:
                    setValue(new LocalDate(userContext.getJodaTimeZone()));
                    break;
                case ERROR:
                    throw new IllegalArgumentException("Could not parse blank date");
                case NULL:
                    setValue(null);
                    break;
                }
                return;
            }
            
            try {
                setValue(dateFormattingService.parseLocalDate(localDateStr,
                                                              userContext));
            } catch (ParseException exception) {
                throw new IllegalArgumentException("Could not parse date", exception);
            }
        }

        @Override
        public String getAsText() {
            LocalDate localDate = (LocalDate) getValue();
            return localDate == null
                ? "" : dateFormattingService.format(localDate, dateFormat, userContext);
        }
    }
    
    public PropertyEditor getPropertyEditor(DateFormattingService.DateOnlyMode dateOnlyMode,
            YukonUserContext userContext) {
        return new DatePropertyEditor(dateOnlyMode, userContext);
    }

    public PropertyEditor getPropertyEditor(DateFormatEnum dateFormat,
            YukonUserContext userContext) {
        return new DateTimePropertyEditor(dateFormat, userContext);
    }

    public PropertyEditor getLocalTimePropertyEditor(DateFormatEnum dateFormat,
            YukonUserContext userContext) {
        return new LocalTimePropertyEditor(dateFormat, userContext, BlankMode.ERROR);
    }

    public PropertyEditor getLocalTimePropertyEditor(DateFormatEnum dateFormat,
            YukonUserContext userContext, BlankMode blankMode) {
        return new LocalTimePropertyEditor(dateFormat, userContext, blankMode);
    }
    
    public PropertyEditor getLocalDatePropertyEditor(DateFormatEnum dateFormat,
                                                      YukonUserContext userContext) {
        return new LocalDatePropertyEditor(dateFormat, userContext, BlankMode.NULL);
    }
    
    public PropertyEditor getLocalDatePropertyEditor(DateFormatEnum dateFormat,
            YukonUserContext userContext, BlankMode blankMode) {
        return new LocalDatePropertyEditor(dateFormat, userContext, blankMode);
    }
    
    public void setupLocalDatePropertyEditor(DataBinder dataBinder, 
            YukonUserContext userContext, BlankMode blankMode) {
        PropertyEditor propertyEditor = getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext, blankMode);
        dataBinder.registerCustomEditor(LocalDate.class, propertyEditor);
    }
    
    public void setupLocalTimePropertyEditor(DataBinder dataBinder,
            YukonUserContext userContext, BlankMode blankMode) {
        PropertyEditor propertyEditor = getLocalTimePropertyEditor(DateFormatEnum.TIME, userContext, blankMode);
        dataBinder.registerCustomEditor(LocalTime.class, propertyEditor);
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

}
