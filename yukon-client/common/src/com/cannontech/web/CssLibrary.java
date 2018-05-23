package com.cannontech.web;

/**
 * Enum for defining css libraries in Yukon. 
 * 
 * @param path Path to the location of the library.
 */
public enum CssLibrary {
    
    ANIMATE("/resources/css/lib/animate.css"), // http://daneden.github.io/animate.css/
    BOOTSTRAP("/resources/css/lib/bootstrap/bootstrap.css"), // built subset from www.getbootstrap.com
    NORMALIZE("/resources/css/lib/normalize.css"), // http://necolas.github.io/normalize.css/
    
    OVERRIDES("/WebConfig/yukon/styles/overrides.css"), // compiled internally
    LAYOUT("/WebConfig/yukon/styles/layout.css"), // compiled internally
    YUKON("/WebConfig/yukon/styles/yukon.css"), // compiled internally
    YUKON_DEFAULT("/WebConfig/yukon/styles/yukon.default.css"), // compiled internally, unaffected by theming
    BUTTONS("/WebConfig/yukon/styles/buttons.css"), // compiled internally
    BUTTONS_DEFAULT("/WebConfig/yukon/styles/buttons.default.css"), // compiled internally, unaffected by theming
    ICONS("/WebConfig/yukon/styles/icons.css"), // internal
    
    TIPSY("/resources/js/lib/tipsy/tipsy.css"), // see JsLibrary.JQUERY_TIPSY
    SPECTRUM("/resources/js/lib/spectrum/spectrum.css"), // see JsLibrary.JQUERY_SPECTRUM
    CHOSEN("/resources/js/lib/chosen/chosen.min.css"), // see JsLibrary.JQUERY_CHOSEN
    TIME_PICKER("/resources/js/lib/time-picker/jquery-ui-timepicker-addon.min.css"), // see JsLibrary.JQUERY_UI_TIME_PICKER
    FLOTCHARTS("/WebConfig/yukon/styles/flotChart.css"), // http://www.flotcharts.org/
    
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