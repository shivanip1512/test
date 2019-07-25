package com.cannontech.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThirdPartyLibraries {

    @JsonProperty("C++")
    public List<ThirdPartyCppLibrary> cppLibraries;
    @JsonProperty("Java")
    public List<ThirdPartyJavaLibrary> javaLibraries;
    @JsonProperty("JavaScript")
    public List<ThirdPartyJavaScriptLibrary> jsLibraries;
    @JsonProperty("Icons")
    public List<ThirdPartyIconLibrary> iconLibraries;
    
    public List<ThirdPartyProject> getCppProjects() {
        return getUniqueProjects(cppLibraries);
    }
    public List<ThirdPartyProject> getJavaProjects() {
        return getUniqueProjects(javaLibraries);
    }
    public List<ThirdPartyProject> getJsProjects() {
        return getUniqueProjects(jsLibraries);
    }
    public List<ThirdPartyProject> getIconProjects() {
        return getUniqueProjects(iconLibraries);
    }
    public List<ThirdPartyProject> getAllProjects() {
        List<ThirdPartyLibrary> allLibraries = new ArrayList<>();
        allLibraries.addAll(cppLibraries);
        allLibraries.addAll(javaLibraries);
        allLibraries.addAll(jsLibraries);
        allLibraries.addAll(iconLibraries);
        return getUniqueProjects(allLibraries);
    }
    private static <T extends ThirdPartyProject> List<ThirdPartyProject> getUniqueProjects(List<T> libraries) {
        return libraries.stream()
                        .map(library -> (ThirdPartyProject)library)
                        .distinct()
                        .sorted()
                        .filter(ThirdPartyProject::isAttributionRequired)
                        .collect(Collectors.toList());
    }
}


