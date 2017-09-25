package com.cannontech.common.validator;

import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadValidator extends SimpleValidator<MultipartFile> {
    public FileUploadValidator() {
        super(MultipartFile.class);
    }

    @Override
    protected void doValidation(MultipartFile file, Errors errors) {
        if (!file.getContentType().startsWith("text") && !file.getContentType().endsWith("excel")) {
            throw new IllegalArgumentException("File must be text or csv");
        }

    }
}
