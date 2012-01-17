package com.cannontech.web.amr.archivedValuesExporter.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;

public class ExportReportValidator extends SimpleValidator<ArchivedValuesExporterBackingBean> {
    private static final String endDate =
        "yukon.web.modules.amr.archivedValueExporter.formatError.endDateRequired";

    public ExportReportValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
       try{
           SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
           sdf.parse(target.getEndDate());
        }catch(ParseException e){
            errors.reject(endDate);
        }
    }
}

