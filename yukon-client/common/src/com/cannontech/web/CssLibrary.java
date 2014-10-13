package com.cannontech.web;

/**
 * Enumeration for defining css libraries in Yukon. 
 * 
 * @param path Path to the location of the library.
 */
public enum CssLibrary {
    
    ANIMATE("/resources/css/lib/animate.css"), // http://daneden.github.io/animate.css/
    BOOTSTRAP("/resources/css/lib/bootstrap.css"), // built subset from www.getbootstrap.com
    NORMALIZE("/resources/css/lib/normalize.css"), // http://necolas.github.io/normalize.css/
    
    OVERRIDES("/WebConfig/yukon/styles/overrides.css"), // compilied internally
    LAYOUT("/WebConfig/yukon/styles/layout.css"), // compilied internally
    YUKON("/WebConfig/yukon/styles/yukon.css"), // compilied internally
    YUKON_DEFAULT("/WebConfig/yukon/styles/yukon.default.css"), // compilied internally, unaffected by theming
    BUTTONS("/WebConfig/yukon/styles/buttons.css"), // compilied internally
    BUTTONS_DEFAULT("/WebConfig/yukon/styles/buttons.default.css"), // compilied internally, unaffected by theming
    ICONS("/WebConfig/yukon/styles/icons.css"), // internal
    
    TIPSY("/JavaScript/lib/jQuery/plugins/tipsy/stylesheets/tipsy.css"), // see JsLibrary.JQUERY_TIPSY
    SPECTRUM("/JavaScript/lib/jQuery/plugins/spectrum/spectrum.css"), // see JsLibrary.JQUERY_SPECTRUM
    CHOSEN("/resources/js/lib/chosen/chosen.min.css"), // see JsLibrary.JQUERY_CHOSEN
    
    JQUERY_UI("/resources/js/lib/jquery-ui/jquery-ui.css"), // http://jqueryui.com/
    JQUERY_UI_MIN("/resources/js/lib/jquery-ui/jquery-ui.min.css"), // http://jqueryui.com/
    ;
    
    private String path = "";
    
    CssLibrary(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
}