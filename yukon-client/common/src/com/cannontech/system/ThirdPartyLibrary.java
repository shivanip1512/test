package com.cannontech.system;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ThirdPartyLibrary {

    public String project;
    public String version;
    @JsonAlias("project URL")
    public String projectUrl;
    public List<LibraryLicenseType> licenses;
    public List<String> licenseUrls;
    public String md5;
    public String sha1;
    public Date updated;
    public String jira;
    public String notes;
    
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public List<String> getLicenseUrls() {
        return licenseUrls;
    }

    public void setLicenseUrls(List<String> licenseUrls) {
        this.licenseUrls = licenseUrls;
    }

    public void setLicense(String licenseString) {
        licenses = Arrays.stream(licenseString.split(", *"))
                       .map(LibraryLicenseType::getByLicenseName)
                       .collect(Collectors.toList());
    }

    @JsonSetter("license URLs")
    public void setLicenseUrls(String licenseUrlString) {
        licenseUrls = Arrays.asList(licenseUrlString.split(", *"));
    }

}