package com.cannontech.system;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;

public class ThirdPartyLibraries {

    @JsonAlias("C++")
    public List<ThirdPartyCppLibrary> cppLibraries;
    @JsonAlias("Java")
    public List<ThirdPartyJavaLibrary> javaLibraries;
    @JsonAlias("JavaScript")
    public List<ThirdPartyJavaScriptLibrary> jsLibraries;
    @JsonAlias("Icons")
    public List<ThirdPartyIconLibrary> iconLibraries;
}


