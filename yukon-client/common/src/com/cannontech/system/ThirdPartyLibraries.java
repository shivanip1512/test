package com.cannontech.system;

import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.stream.StreamUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

public class ThirdPartyLibraries {

    @JsonProperty("C++")
    public List<ThirdPartyCppLibrary> cppLibraries;
    @JsonProperty("Java")
    public List<ThirdPartyJavaLibrary> javaLibraries;
    @JsonProperty("JavaScript")
    public List<ThirdPartyJavaScriptLibrary> jsLibraries;
    @JsonProperty("Icons")
    public List<ThirdPartyIconLibrary> iconLibraries;
    
    public List<ThirdPartyProject> getAllProjects() {
        Iterable<? extends ThirdPartyLibrary> allLibraries = Iterables.concat(cppLibraries, javaLibraries, jsLibraries, iconLibraries);
        return getUniqueProjects(allLibraries);
    }
    private static <T extends ThirdPartyProject> List<ThirdPartyProject> getUniqueProjects(Iterable<? extends ThirdPartyLibrary> allLibraries) {
        return StreamUtils.stream(allLibraries)
                        .map(library -> (ThirdPartyProject)library)
                        .distinct()
                        .sorted()
                        .filter(ThirdPartyProject::isAttributionRequired)
                        .collect(Collectors.toList());
    }
}


