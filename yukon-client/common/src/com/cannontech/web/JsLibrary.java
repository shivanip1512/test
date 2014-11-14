package com.cannontech.web;

/**
 * Enum for defining javascript libraries in Yukon. 
 * Currently used by IncludeScriptTag.java
 * 
 * @param path Path to the location of the library.
 */
public enum JsLibrary {
    
    DEBUGGER("/resources/js/lib/debugger.js"), // http://benalman.com/projects/javascript-debug-console-log/
    
    JQUERY("/resources/js/lib/jquery/jquery-1.11.1.js"), // http://www.jquery.com
    JQUERY_MIN("/resources/js/lib/jquery/jquery-1.11.1.min.js"), // http://www.jquery.com
    
    JQUERY_CHECK_ALL("/resources/js/lib/checkall/jquery.checkall.js"), // https://github.com/mjball/jQuery-CheckAll
    JQUERY_COOKIE("/resources/js/lib/cookie/jquery.cookie.js"), // https://github.com/carhartl/jquery-cookie
    JQUERY_DATE_TIME_ENTRY("/resources/js/lib/datetimeentry/jquery.datetimeentry.min.js"), // http://keith-wood.name/datetimeEntry.html
    JQUERY_FILE_UPLOAD("/resources/js/lib/fileupload/jquery.fileupload.js"), // https://github.com/blueimp/jQuery-File-Upload/tree/master/js
    JQUERY_FORM("/resources/js/lib/form/jquery.form.js"), // https://github.com/malsup/form/
    JQUERY_GRID("/resources/js/lib/jqgrid/js/jquery.jqGrid.min.js"), // http://www.trirand.com/jqgridwiki/doku.php?id=wiki:jqgriddocs
    JQUERY_GRID_HELPER("/resources/js/lib/jqgrid/jqGridHelper.js"), //in-house
    JQUERY_IFRAME_TRANSPORT("/resources/js/lib/fileupload/jquery.iframe-transport.js"), // https://github.com/blueimp/jQuery-File-Upload/tree/master/js
    JQUERY_MOUSEWHEEL("/resources/js/lib/mousewheel/jquery.mousewheel.min.js"), // https://github.com/brandonaaron/jquery-mousewheel
    JQUERY_PLACEHOLDER("/resources/js/lib/placeholder/jquery.placeholder.js"), // https://github.com/mathiasbynens/jquery-placeholder
    JQUERY_SCROLLTO("/resources/js/lib/scroll.to/jquery.scrollTo.min.js"), // https://github.com/flesler/jquery.scrollTo/releases
    JQUERY_SPECTRUM("/resources/js/lib/spectrum/spectrum.js"), // http://bgrins.github.io/spectrum/
    JQUERY_TIPSY("/resources/js/lib/tipsy/jquery.tipsy.js"), // https://github.com/jaz303/tipsy
    JQUERY_TREE("/resources/js/lib/dynatree/jquery.dynatree.js"), // http://wwwendt.de/tech/dynatree/doc/dynatree-doc.html
    JQUERY_TREE_MIN("/resources/js/lib/dynatree/jquery.dynatree.min.js"),
    JQUERY_TREE_HELPERS("/resources/js/tags/yukon.dynatree.js"), //in-house
    JQUERY_CHOSEN("/resources/js/lib/chosen/chosen.jquery.min.js"), // https://github.com/harvesthq/chosen/releases
    JQUERY_HIDDEN_DIMENSIONS("/resources/js/lib/hidden-dimensions/hidden-dimensions.js"), // https://github.com/brianfreud/greasemonkey-batchCAA/tree/master/jQuery.getHiddenDimensions
    
    JQUERY_UI("/resources/js/lib/jquery-ui/jquery-ui.js"), // http://www.jqueryui.com
    JQUERY_UI_MIN("/resources/js/lib/jquery-ui/jquery-ui.min.js"), // http://www.jqueryui.com
    
    JQUERY_UI_TIME_PICKER("/resources/js/lib/time-picker/jquery-ui-timepicker-addon.min.js"), // https://github.com/trentrichardson/jQuery-Timepicker-Addon
    JQUERY_UI_WIDGET("/resources/js/lib/fileupload/jquery.ui.widget.js"), // https://github.com/blueimp/jQuery-File-Upload/wiki/Basic-plugin

    YUKON_FLOTCHARTS("/resources/js/common/yukon.flot.js"), //in-house
    JQUERY_FLOTCHARTS("/resources/js/lib/flotcharts/jquery.flot.min.js"), // http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_PIE("/resources/js/lib/flotcharts/jquery.flot.pie.min.js"), // http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_SELECTION("/resources/js/lib/flotcharts/jquery.flot.selection.min.js"), // http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_AXIS_LABEL("/resources/js/lib/flotcharts/jquery.flot.axislabels.js"), // https://github.com/markrcote/flot-axislabels
    JQUERY_FLOTCHARTS_RESIZE("/resources/js/lib/flotcharts/jquery.flot.resize.min.js"), // http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_TIME("/resources/js/lib/flotcharts/jquery.flot.time.min.js"), // http://www.flotcharts.org/
    JQUERY_EXCANVAS("/resources/js/lib/flotcharts/excanvas.min.js"), // http://www.flotcharts.org/

    HIGH_STOCK("/resources/js/lib/highstock/highstock.js"),
    HIGH_STOCK_EXPORTING("/resources/js/lib/highstock/modules/exporting.js"),
    HIGH_STOCK_NO_DATA("/resources/js/lib/highstock/modules/no-data-to-display.js"),
    
    OPEN_LAYERS_MIN("/resources/js/lib/open-layers/ol.js"),
    OPEN_LAYERS("/resources/js/lib/open-layers/ol-debug.js"),
    
    YUKON("/resources/js/common/yukon.js"),
    YUKON_ANALYTICS("/resources/js/common/yukon.analytics.js"),
    YUKON_ALERTS("/resources/js/common/yukon.alerts.js"),
    YUKON_CONFIRM("/resources/js/common/yukon.dialog.confirm.js"),
    YUKON_COOKIE("/resources/js/common/yukon.cookie.js"),
    YUKON_DEVICE_GROUP_PICKER("/resources/js/tags/yukon.device.group.picker.js"),
    YUKON_DROPDOWN("/resources/js/tags/yukon.dropdown.js"),
    YUKON_FAVORITES("/resources/js/common/yukon.favorites.js"),
    YUKON_PICKER("/resources/js/tags/yukon.picker.js"),
    YUKON_SIMPLE_POPUPS("/resources/js/common/yukon.simple.popups.js"),
    YUKON_TIME_FORMATTER("/resources/js/common/yukon.format.time.js"),
    YUKON_UI_UTIL("/resources/js/common/yukon.ui.util.js"),
    YUKON_UPDATER("/resources/js/common/yukon.data.updater.js"),
    
    MOMENT("/resources/js/lib/moment/moment.min.js"),
    MOMENT_TZ("/resources/js/lib/moment/moment-timezone-with-data.min.js"),
    JS_TIMEZONE_DETECT("/resources/js/lib/js.timezone.detect/jstz-1.0.4.min.js"),
    MODERNIZR("/resources/js/lib/modernizr/custom.js");
    
    private String path = "";
    
    JsLibrary(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
}