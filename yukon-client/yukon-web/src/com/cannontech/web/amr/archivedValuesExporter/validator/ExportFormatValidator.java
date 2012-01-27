package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;

public class ExportFormatValidator extends SimpleValidator<ArchivedValuesExporterBackingBean>{
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    
    private static final String formatNameRequiredMsgKey = ArchivedValuesExporterController.baseKey +"formatError.formatNameRequired";
    private static final String delimiterRequiredMsgKey = ArchivedValuesExporterController.baseKey +"formatError.delimiterRequired";
    private static final String duplicateNameMsgKey = ArchivedValuesExporterController.baseKey +"formatError.duplicateName";
    private static final String fieldsRequiredMsgKey = ArchivedValuesExporterController.baseKey +"formatError.fieldsRequired";

    public ExportFormatValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                       "format.formatName",
                                                       formatNameRequiredMsgKey);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.formatName", target.getFormat()
            .getFormatName(), 100);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.delimiter", target.getFormat()
            .getDelimiter(), 20);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.header", target.getFormat()
            .getHeader(), 255);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.footer", target.getFormat()
            .getFooter(), 255);

        if (!errors.hasErrors() && target.getFormat().getFormatId() == 0) {
            ExportFormat format =
                archiveValuesExportFormatDao.findByFormatName(target.getFormat().getFormatName());
            if (format != null) {
                errors.rejectValue("format.formatName", duplicateNameMsgKey);
            }
        }
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                       "format.delimiter",
                                                       delimiterRequiredMsgKey);
        if (target.getFormat().getFields().isEmpty()) {
            errors.reject(fieldsRequiredMsgKey);
  }
    }
}