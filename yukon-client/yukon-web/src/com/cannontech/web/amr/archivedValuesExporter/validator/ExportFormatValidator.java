package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;

public class ExportFormatValidator extends SimpleValidator<ArchivedValuesExporterBackingBean>{
    private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    
    private static final String formatNameRequired = "yukon.web.modules.amr.archivedValueExporter.formatError.formatNameRequired";
    private static final String delimiterRequired = "yukon.web.modules.amr.archivedValueExporter.formatError.delimiterRequired";
    private static final String duplicateName = "yukon.web.modules.amr.archivedValueExporter.formatError.duplicateName";
    private static final String fieldsRequired = "yukon.web.modules.amr.archivedValueExporter.formatError.fieldsRequired";

    public ExportFormatValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "format.formatName", formatNameRequired);
        if (!errors.hasErrors() && target.getFormat().getFormatId() == 0) {
            ExportFormat format = archiveValuesExportFormatDao.getByFormatName(target.getFormat().getFormatName());
            if (format != null) {
                errors.rejectValue("format.formatName", duplicateName);
            }
        }
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "format.delimiter", delimiterRequired);
        if(target.getFormat().getFields().isEmpty()){
            errors.reject(fieldsRequired);
        }
    }

    @Autowired
    public void setArchiveValuesExportFormatDao(ArchiveValuesExportFormatDao archiveValuesExportFormatDao) {
        this.archiveValuesExportFormatDao = archiveValuesExportFormatDao;
    }
    
}