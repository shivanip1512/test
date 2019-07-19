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
    
    public List<ThirdPartyCppLibrary> getCppLibraries() {
        return cppLibraries;
    }
    public void setCppLibraries(List<ThirdPartyCppLibrary> cppLibraries) {
        this.cppLibraries = cppLibraries;
    }
    public List<ThirdPartyJavaLibrary> getJavaLibraries() {
        return javaLibraries;
    }
    public void setJavaLibraries(List<ThirdPartyJavaLibrary> javaLibraries) {
        this.javaLibraries = javaLibraries;
    }
    public List<ThirdPartyJavaScriptLibrary> getJsLibraries() {
        return jsLibraries;
    }
    public void setJsLibraries(List<ThirdPartyJavaScriptLibrary> jsLibraries) {
        this.jsLibraries = jsLibraries;
    }
    public List<ThirdPartyIconLibrary> getIconLibraries() {
        return iconLibraries;
    }
    public void setIconLibraries(List<ThirdPartyIconLibrary> iconLibraries) {
        this.iconLibraries = iconLibraries;
    }

}


