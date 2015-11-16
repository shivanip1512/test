package com.cannontech.web.capcontrol.validators;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class DateTimeValidator implements Validator{

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Date date = (Date)value;
        Date now = new Date();
        
        if(date.before(now)) {
            FacesMessage message = new FacesMessage();
            message.setDetail("Next run time cannot occur in the past.");
            message.setSummary("Date/Time is not valid: ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

}
