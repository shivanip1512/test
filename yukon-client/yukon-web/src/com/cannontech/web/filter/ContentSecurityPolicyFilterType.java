package com.cannontech.web.filter;

public enum ContentSecurityPolicyFilterType {
    DEFAULT_SRC("default-src "
        + "'self' "
        + "'unsafe-inline' " 
        + "http://java.sun.com " 
        + "http://cannontech.com "
        + "http://docs.spring.io " 
        + "https://getbootstrap.com " 
        + "http://www.springframework.org "
        + "http://api.tiles.mapbox.com/ " 
        + "http://*.tiles.mapbox.com/ " 
        + "http://www.w3.org "
        + "http://api.recaptcha.net " 
        + "http://www.google-analytics.com " 
        + "https://www.google.com/recaptcha/; "),
    SCRIPT_SRC("script-src "
        + "'self' "
        + "http://www.google.com/js " 
        + "https://api.tiles.mapbox.com " 
        + "'unsafe-inline' "
        + "'unsafe-eval' " 
        + "http://www.google-analytics.com " 
        + "https://www.google.com/recaptcha/; "),
    CONNECT_SRC("connect-src " 
        + "'self' " 
        + "'unsafe-inline' " 
        + "https://api.mapbox.com "
        + "https://*.tiles.mapbox.com " 
        + "https://www.google.com/recaptcha/; "),
    IMG_SRC("img-src "
        + "'self' " 
        + "https://www.google.com/recaptcha/ " 
        + "data: " + "http://www.google-analytics.com "
        + "http://api.recaptcha.net " 
        + "Access-Control-Allow-Origin: *;"), // Access-Control-Allow-Origin: * used for cross origin resource sharing for map images
    STYLE_SRC("style-src "
        + "'self' " 
        + "'unsafe-inline' " 
        + "https://api.tiles.mapbox.com "
        + "https://www.google.com/recaptcha; "),
    MEDIA_SRC("media-src 'self';"),
    CHILD_SRC("child-src 'self';"),
    OBJECT_SRC("object-src 'self';"),
    FONT_SRC("font-src 'self';"),
    FRAME_SRC("frame-src "
        + "'self' " 
        + "https://www.google.com/recaptcha"),
    FRAME_ANCESTORS("'self'"),
    FORM_ACTION("'self'"),
    ;

    private final String value;

    private ContentSecurityPolicyFilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}