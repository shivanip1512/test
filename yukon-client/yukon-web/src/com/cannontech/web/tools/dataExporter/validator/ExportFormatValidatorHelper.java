package com.cannontech.web.tools.dataExporter.validator;

import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.dataExporter.DataExporterFormatController;

public class ExportFormatValidatorHelper {

    /**
     * Validate all ExportFormat fields except formatName. Any new field validation in ExportFormat should go to this class so
     * both ExportFormatValidator and ExportFormatTemplateValidator can reuse these.
     */
    public void validateExportFormatFields(ExportFormat target, Errors errors) {
        YukonValidationUtils.checkExceedsMaxLength(errors, "delimiter", target.getDelimiter(), 20);
        YukonValidationUtils.checkExceedsMaxLength(errors, "header", target.getHeader(), 255);
        YukonValidationUtils.checkExceedsMaxLength(errors, "footer", target.getFooter(), 255);

        if (target.getFields().isEmpty()) {
            errors.reject(DataExporterFormatController.BASE_KEY + "formatError.fieldsRequired");
        }
    }
}
