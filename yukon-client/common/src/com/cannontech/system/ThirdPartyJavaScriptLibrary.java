package com.cannontech.system;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ThirdPartyJavaScriptLibrary extends ThirdPartyLibrary {

    @JsonAlias("npm URL")
    public String npmUrl;
}


