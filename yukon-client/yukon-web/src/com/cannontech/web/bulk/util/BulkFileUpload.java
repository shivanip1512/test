package com.cannontech.web.bulk.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BulkFileUpload {
    
    private List<String> errors = new ArrayList<String>();
    private File file = null;
    private boolean hasErrors = false;
    
    public List<String> getErrors() {
        return errors;
    }
    public void addError(String error) {
        this.errors.add(error);
        this.hasErrors = true;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public boolean hasErrors() {
        return this.hasErrors;
    }
    
    
}