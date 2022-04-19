package com.cannontech.web.filter;

public enum ContentSecurityPolicyFilterType {
    DEFAULT_SRC("default-src 'self' 'unsafe-inline' " 
        + "http://java.sun.com " 
        + "http://cannontech.com "
        + "http://docs.spring.io " 
        + "https://getbootstrap.com " 
        + "http://www.springframework.org "
        + "http://api.tiles.mapbox.com/ " 
        + "http://*.tiles.mapbox.com/ " 
        + "http://www.w3.org "
        + "https://api-secure.recaptcha.net "   // Recaptcha
        + "https://www.google.com "             // Recaptcha
        + "http://www.google.com ; "),          // Recaptcha
    SCRIPT_SRC("script-src 'self' 'unsafe-inline' 'unsafe-eval' "
        + "https://api.tiles.mapbox.com " 
        + "https://www.googletagmanager.com "   //Google Analytics
        + "http://www.googletagmanager.com "    //Google Analytics
        + "http://www.google-analytics.com "    //Google Analytics
        + "https://api-secure.recaptcha.net "   // Recaptcha
        + "https://www.gstatic.com/recaptcha/ " // Recaptcha
        + "https://www.google.com "             // Recaptcha
        + "http://www.google.com "              // Recaptcha
        + "https://ssl.google-analytics.com/ga.js ; "),
    CONNECT_SRC("connect-src 'self' 'unsafe-inline' " 
        + "https://api.mapbox.com "
        + "https://*.tiles.mapbox.com " 
        + "https://www.google-analytics.com "    //Google Analytics
        + "https://www.google.com/recaptcha/ ;"),
    IMG_SRC("img-src 'self' data: Access-Control-Allow-Origin: * " // Access-Control-Allow-Origin: * used for cross origin resource sharing for map images
        + "https://api-secure.recaptcha.net "   // Recaptcha
        + "https://www.google.com "             // Recaptcha
        + "http://www.google.com ; "),          // Recaptcha
    STYLE_SRC("style-src 'self' 'unsafe-inline' fonts.googleapis.com " 
        + "https://api.tiles.mapbox.com "
        + "https://www.google.com/recaptcha ;"),
    MEDIA_SRC("media-src 'self' "
        + "https://www.google.com "             // Recaptcha
        + "http://www.google.com ; "),          // Recaptcha"),
    CHILD_SRC("child-src 'self' blob: ; "),
    OBJECT_SRC("object-src 'self' "
        + "https://www.google.com "             // Recaptcha
        + "http://www.google.com ; "),          // Recaptcha
    FONT_SRC("font-src 'self' fonts.gstatic.com ; "),
    FRAME_SRC("frame-src 'self' " 
        + "https://www.google.com "             // Recaptcha
        + "http://www.google.com ; "),          // Recaptcha
    FRAME_ANCESTORS("frame-ancestors 'self' ;"),
    FORM_ACTION("form-action 'self' "
        + "https://export.highcharts.com ; "),
    ;

    private final String value;

    private ContentSecurityPolicyFilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}