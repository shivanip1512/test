package com.cannontech.system;

import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ThirdPartyLibrary {

    public String filename;
    @JsonAlias("yukon group")
    public LibraryGroup group;
    public String project;
    public String version;
    @JsonAlias("project URL")
    public String projectUrl;
    public List<LibraryLicenseType> licenses;
    public List<String> licenseUrls;
    @JsonAlias("maven URL")
    public String mavenUrl;
    public String md5;
    public String sha1;
    public Date updated;
    public String jira;
    public String notes;
    
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


