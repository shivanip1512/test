package com.cannontech.system;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ThirdPartyProject implements Comparable<ThirdPartyProject> {

    public String project;
    public String projectUrl;
    public List<LibraryLicenseType> licenses;
    public List<String> licenseUrls;

    public String getProject() {
        return project;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    @JsonSetter("project URL")
    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public List<String> getLicenseUrls() {
        return licenseUrls;
    }

    @JsonSetter("license")
    public void setLicense(String licenseString) {
        licenses = Arrays.stream(licenseString.split(", *"))
                       .map(LibraryLicenseType::getByLicenseName)
                       .collect(Collectors.toList());
    }
    
    public List<LibraryLicenseType> getLicenses() {
        return licenses;
    }

    @JsonSetter("license URLs")
    public void setLicenseUrls(String licenseUrlString) {
        licenseUrls = Arrays.asList(licenseUrlString.split(", *"));
    }
    
    public boolean isAttributionRequired() {
        //  Attribution is only required if all license types require attribution
        return licenses.stream().allMatch(LibraryLicenseType::isAttributionRequired); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseUrls, licenses, project, projectUrl);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ThirdPartyProject other = (ThirdPartyProject) obj;
        return Objects.equals(licenseUrls,
                              other.licenseUrls) && 
               Objects.equals(licenses,
                              other.licenses) && 
               Objects.equals(project,
                              other.project) && 
               Objects.equals(projectUrl,
                              other.projectUrl);
    }
    
    @Override
    public int compareTo(ThirdPartyProject other) {
        return this.project.compareToIgnoreCase(other.project);
    }
}