package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporterBackingBean;

public class ExportFormatValidator extends SimpleValidator<ArchivedValuesExporterBackingBean>{
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    
    public ExportFormatValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "format.formatName", ArchivedValuesExporterController.baseKey+"formatError.formatNameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.formatName", target.getFormat().getFormatName(), 100);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.delimiter", target.getFormat().getDelimiter(), 1);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.header", target.getFormat().getHeader(), 255);
        YukonValidationUtils.checkExceedsMaxLength(errors, "format.footer", target.getFormat().getFooter(), 255);

        if (!errors.hasErrors() && target.getFormat().getFormatId() == 0) {
            ExportFormat format = archiveValuesExportFormatDao.findByFormatName(target.getFormat().getFormatName());
            if (format != null) {
                errors.rejectValue("format.formatName", ArchivedValuesExporterController.baseKey+"formatError.duplicateName");
            }
        }

//        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "format.delimiter", ArchivedValuesExporterController.baseKey+"formatError.delimiterRequired");
        if (target.getFormat().getFields().isEmpty()) {
            errors.reject(ArchivedValuesExporterController.baseKey+"formatError.fieldsRequired");
        }
    }
}