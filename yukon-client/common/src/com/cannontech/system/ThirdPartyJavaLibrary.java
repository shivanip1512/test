package com.cannontech.system;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ThirdPartyJavaLibrary extends ThirdPartyLibrary {

    public String filename;
    @JsonAlias("maven URL")
    public String mavenUrl;
}


