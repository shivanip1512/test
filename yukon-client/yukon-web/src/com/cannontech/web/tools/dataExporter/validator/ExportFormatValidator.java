package com.cannontech.web.tools.dataExporter.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.dataExporter.DataExporterFormatController;

public class ExportFormatValidator extends SimpleValidator<ExportFormat> {
    
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    
    public ExportFormatValidator() {
        super(ExportFormat.class);
    }

    @Override
    protected void doValidation(ExportFormat target, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "formatName", DataExporterFormatController.BASE_KEY + "formatError.formatNameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "formatName", target.getFormatName(), 100);
        YukonValidationUtils.checkExceedsMaxLength(errors, "delimiter", target.getDelimiter(), 20);
        YukonValidationUtils.checkExceedsMaxLength(errors, "header", target.getHeader(), 255);
        YukonValidationUtils.checkExceedsMaxLength(errors, "footer", target.getFooter(), 255);

        ExportFormat format = archiveValuesExportFormatDao.findByFormatName(target.getFormatName());
        if (format != null && format.getFormatId() != target.getFormatId()) {
            errors.rejectValue("formatName", DataExporterFormatController.BASE_KEY + "formatError.duplicateName");
        }

        if (target.getFields().isEmpty()) {
            errors.reject(DataExporterFormatController.BASE_KEY + "formatError.fieldsRequired");
        }
    }
}