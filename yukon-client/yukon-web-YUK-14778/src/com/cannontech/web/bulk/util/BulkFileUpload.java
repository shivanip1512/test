package com.cannontech.web.bulk.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BulkFileUpload {
    
    private String name;
    private List<String> errors = new ArrayList<String>();
    private File file = null;
    private boolean hasErrors = false;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
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
    
    @Override
    public String toString() {
        return String.format("BulkFileUpload [name=%s, errors=%s, file=%s, hasErrors=%s]", name, errors, file, hasErrors);
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public boolean hasErrors() {
        return this.hasErrors;
    }
    
}