package com.cannontech.system;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThirdPartyLibraries {

    @JsonProperty("C++")
    public List<ThirdPartyCppLibrary> cppLibraries;
    @JsonProperty("Java")
    public List<ThirdPartyJavaLibrary> javaLibraries;
    @JsonProperty("Network Manager")
    public List<ThirdPartyJavaLibrary> networkManagerLibraries;
    @JsonProperty("JavaScript")
    public List<ThirdPartyJavaScriptLibrary> jsLibraries;
    @JsonProperty("Icons")
    public List<ThirdPartyIconLibrary> iconLibraries;
    @JsonProperty("Css")
    public List<ThirdPartyLibrary> cssLibraries;
    
    public List<ThirdPartyProject> getAllProjects() {
        return Stream.of(cppLibraries, javaLibraries, networkManagerLibraries, jsLibraries, cssLibraries)
                     .map(List::stream)
                     .flatMap(Function.identity())
                     .distinct()
                     .sorted()
                     .filter(ThirdPartyProject::isAttributionRequired)
                     .filter(ThirdPartyProject::isNotJunitProject)
                     .collect(Collectors.toList());
    }
}