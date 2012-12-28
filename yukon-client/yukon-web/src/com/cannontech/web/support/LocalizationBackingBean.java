package com.cannontech.web.support;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class LocalizationBackingBean {
    private String task;
    private String query;
    private boolean caseSensitive;
    private String searchBy;
    private String searchInChoice;
    private String searchInTheme;
    private CommonsMultipartFile searchInCustom;
    private String modifiedChoice;
    private String modifiedTheme;
    private CommonsMultipartFile modifiedCustom;
    private String baseChoice;
    private String baseTheme;
    private CommonsMultipartFile baseCustom;
    private String compareAction;

    public LocalizationBackingBean() {
    }
    
    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    public String getSearchBy() {
        return searchBy;
    }
    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }
    public String getSearchInChoice() {
        return searchInChoice;
    }
    public void setSearchInChoice(String searchInChoice) {
        this.searchInChoice = searchInChoice;
    }
    public String getSearchInTheme() {
        return searchInTheme;
    }
    public void setSearchInTheme(String searchInTheme) {
        this.searchInTheme = searchInTheme;
    }
    public CommonsMultipartFile getSearchInCustom() {
        return searchInCustom;
    }
    public void setSearchInCustom(CommonsMultipartFile searchInCustom) {
        this.searchInCustom = searchInCustom;
    }
    public String getModifiedChoice() {
        return modifiedChoice;
    }
    public void setModifiedChoice(String modifiedChoice) {
        this.modifiedChoice = modifiedChoice;
    }
    public String getModifiedTheme() {
        return modifiedTheme;
    }
    public void setModifiedTheme(String modifiedTheme) {
        this.modifiedTheme = modifiedTheme;
    }
    public CommonsMultipartFile getModifiedCustom() {
        return modifiedCustom;
    }
    public void setModifiedCustom(CommonsMultipartFile modifiedCustom) {
        this.modifiedCustom = modifiedCustom;
    }
    public String getBaseChoice() {
        return baseChoice;
    }
    public void setBaseChoice(String baseChoice) {
        this.baseChoice = baseChoice;
    }
    public String getBaseTheme() {
        return baseTheme;
    }
    public void setBaseTheme(String baseTheme) {
        this.baseTheme = baseTheme;
    }
    public CommonsMultipartFile getBaseCustom() {
        return baseCustom;
    }
    public void setBaseCustom(CommonsMultipartFile baseCustom) {
        this.baseCustom = baseCustom;
    }
    
    public String getCompareAction() {
        return compareAction;
    }

    public void setCompareAction(String compareAction) {
        this.compareAction = compareAction;
    }

    public void setDefaultsIfNotSet(){
        if(getSearchInChoice() == null){
            setSearchInChoice("default");
        }
        if(getModifiedChoice() == null){
            setModifiedChoice("default");
        }
        if(getBaseChoice() == null){
            setBaseChoice("default");
        }
    }
    
}
