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
    @Autowired private ExportFormatValidatorHelper validatorHelper;

    public ExportFormatValidator() {
        super(ExportFormat.class);
    }

    /**
     * Validate formatName only when user clicks save button. All other field validations are in ExportFormatValidatorHelper
     * class so that these can be reused in ExportFormatTemplateValidator. ExportFormatTemplateValidator is used to validate
     * the yaml data i.e when user create a data export from template. Any new field validation in ExportFormat POJO should go to
     * ExportFormatValidatorHelper class.
     */
    @Override
    protected void doValidation(ExportFormat target, Errors errors) {
        // Validate formatName
        YukonValidationUtils.checkIsBlank(errors, "formatName", target.getFormatName(), "Format Name", false);
        YukonValidationUtils.checkExceedsMaxLength(errors, "formatName", target.getFormatName(), 100);
        ExportFormat format = archiveValuesExportFormatDao.findByFormatName(target.getFormatName());
        if (format != null && format.getFormatId() != target.getFormatId()) {
            errors.rejectValue("formatName", DataExporterFormatController.BASE_KEY + "formatError.duplicateName");
        }

        // Validate other fields
        validatorHelper.validateExportFormatFields(target, errors);
    }

}