package com.cannontech.messaging.serialization.thrift.test.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private String name;
    private Class<?> validatedClass;
    private List<String> comments = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private List<ValidationResult> nestedResults = new ArrayList<ValidationResult>();

    public ValidationResult() {
        this("", null);
    }

    public ValidationResult(Class<?> clazz) {
        this(clazz.getSimpleName(), clazz);
    }

    public ValidationResult(String name, Class<?> clazz) {
        this.name = name;
        validatedClass = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComments(String comment) {
        comments.add(comment);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        if (error != null) {
            this.errors.add(error);
        }
    }

    public void addErrors(String... errors) {
        if (errors != null) {
            for (String error : errors) {
                addError(error);
            }
        }
    }

    public List<ValidationResult> getNestedResults() {
        return nestedResults;
    }

    public void addNestedResults(ValidationResult nestedResult) {
        if (nestedResult != null) {
            this.nestedResults.add(nestedResult);
        }
    }

    public boolean hasError() {
        if (errors.size() != 0) {
            return true;
        }

        for (ValidationResult nestedResult : nestedResults) {
            if (nestedResult.hasError()) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getValidatedClass() {
        return validatedClass;
    }

    public void setValidatedClass(Class<?> validatedClass) {
        this.validatedClass = validatedClass;
    }

    @Override
    public String toString() {        
        return getName() + "(" + getClassName() + ")";
    }

    public String getClassName() {
        String s = "";
        if (validatedClass != null) {
            String canonicalName = validatedClass.getCanonicalName();
            s = canonicalName != null ? canonicalName : validatedClass.getName();
        }
        return s;
    }

}
