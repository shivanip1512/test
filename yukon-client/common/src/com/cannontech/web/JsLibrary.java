package com.cannontech.web;

/**
 * Enumeration for defining javascript libraries in Yukon. 
 * Currently used by IncludeScriptTag.java
 * 
 * @param path Path to the location of the library.
 */
public enum JsLibrary {
    
    DEBUGGER("/resources/js/lib/debugger.js"), //http://benalman.com/projects/javascript-debug-console-log/
    
    JQUERY("/JavaScript/lib/jQuery/jquery-1.11.1.js"), //http://www.jquery.com
    JQUERY_MIN("/JavaScript/lib/jQuery/jquery-1.11.1.min.js"), //http://www.jquery.com
    
    JQUERY_CHECK_ALL("/JavaScript/lib/jQuery/plugins/checkAll/jquery.checkall.js"), //https://github.com/mjball/jQuery-CheckAll
    JQUERY_COOKIE("/JavaScript/lib/jQuery/plugins/cookie/jquery.cookie.js"), //https://github.com/carhartl/jquery-cookie
    JQUERY_DATE_TIME_ENTRY("/JavaScript/lib/jQuery/plugins/datetimeentry/jquery.datetimeentry.min.js"), //http://keith-wood.name/datetimeEntry.html
    JQUERY_FILE_UPLOAD("/JavaScript/lib/jQuery/plugins/fileupload/jquery.fileupload.js"), //https://github.com/blueimp/jQuery-File-Upload/tree/master/js
    JQUERY_FORM("/JavaScript/lib/jQuery/plugins/form/jquery.form.js"), //https://github.com/malsup/form/
    JQUERY_GRID("/JavaScript/lib/jQuery/plugins/jqGrid/js/jquery.jqGrid.min.js"), //http://www.trirand.com/jqgridwiki/doku.php?id=wiki:jqgriddocs
    JQUERY_GRID_HELPER("/JavaScript/lib/jQuery/plugins/jqGrid/jqGridHelper.js"), //in-house
    JQUERY_IFRAME_TRANSPORT("/JavaScript/lib/jQuery/plugins/fileupload/jquery.iframe-transport.js"), //https://github.com/blueimp/jQuery-File-Upload/tree/master/js
    JQUERY_MOUSEWHEEL("/JavaScript/lib/jQuery/plugins/mousewheel/jquery.mousewheel.min.js"), //https://github.com/brandonaaron/jquery-mousewheel
    JQUERY_PLACEHOLDER("/JavaScript/lib/jQuery/plugins/placeholder/jquery.placeholder.js"), //https://github.com/mathiasbynens/jquery-placeholder
    JQUERY_SCROLLTO("/resources/js/lib/scroll.to/jquery.scrollTo.min.js"), //https://github.com/flesler/jquery.scrollTo/releases
    JQUERY_SPECTRUM("/JavaScript/lib/jQuery/plugins/spectrum/spectrum.js"), //http://bgrins.github.io/spectrum/
    JQUERY_TIPSY("/JavaScript/lib/jQuery/plugins/tipsy/javascripts/jquery.tipsy.js"), //https://github.com/jaz303/tipsy
    JQUERY_TREE("/resources/js/lib/dynatree/jquery.dynatree.js"), //http://wwwendt.de/tech/dynatree/doc/dynatree-doc.html
    JQUERY_TREE_MIN("/resources/js/lib/dynatree/jquery.dynatree.min.js"),
    JQUERY_TREE_HELPERS("/JavaScript/yukon.dynatree.js"), //in-house
    JQUERY_CHOSEN("/resources/js/lib/chosen/chosen.jquery.min.js"), //https://github.com/harvesthq/chosen/releases
    JQUERY_HIDDEN_DIMENSIONS("/resources/js/lib/hidden-dimensions/hidden-dimensions.js"), //https://github.com/brianfreud/greasemonkey-batchCAA/tree/master/jQuery.getHiddenDimensions
    
    JQUERY_UI("/resources/js/lib/jquery-ui/jquery-ui.js"), //http://www.jqueryui.com
    JQUERY_UI_MIN("/resources/js/lib/jquery-ui/jquery-ui.min.js"), //http://www.jqueryui.com
    
    JQUERY_UI_TIME_PICKER("/resources/js/lib/time-picker/jquery-ui-timepicker-addon.min.js"), //https://github.com/trentrichardson/jQuery-Timepicker-Addon
    JQUERY_UI_WIDGET("/JavaScript/lib/jQuery/plugins/fileupload/jquery.ui.widget.js"), //https://github.com/blueimp/jQuery-File-Upload/wiki/Basic-plugin

    YUKON_FLOTCHARTS("/JavaScript/yukon.flot.js"), //in-house
    JQUERY_FLOTCHARTS("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_PIE("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.pie.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_SELECTION("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.selection.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_AXIS_LABEL("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.axislabels.js"), //https://github.com/markrcote/flot-axislabels
    JQUERY_FLOTCHARTS_RESIZE("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.resize.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_TIME("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.time.min.js"), //http://www.flotcharts.org/
    JQUERY_EXCANVAS("/JavaScript/lib/jQuery/plugins/flotcharts/excanvas.min.js"), //http://www.flotcharts.org/

    HIGH_STOCK("/resources/js/lib/highstock/highstock.js"),
    HIGH_STOCK_EXPORTING("/resources/js/lib/highstock/modules/exporting.js"),
    HIGH_STOCK_NO_DATA("/resources/js/lib/highstock/modules/no-data-to-display.js"),
    
    OPEN_LAYERS_MIN("/resources/js/lib/open-layers/ol.js"),
    OPEN_LAYERS("/resources/js/lib/open-layers/ol-debug.js"),
    
    JSON("/JavaScript/lib/JSON/2.0/json2.js"),
    
    YUKON("/JavaScript/yukon.js"),
    YUKON_COOKIE("/resources/js/common/yukon.cookie.js"),
    YUKON_ALERTS("/JavaScript/yukon.alerts.js"),
    YUKON_FAVORITES("/JavaScript/yukon.favorites.js"),
    YUKON_PICKER("/resources/js/tags/yukon.picker.js"),
    YUKON_SIMPLE_POPUPS("/resources/js/common/yukon.simple.popups.js"),
    YUKON_DEVICE_GROUP_PICKER("/resources/js/tags/yukon.device.group.picker.js"),
    YUKON_CONFIRM("/JavaScript/yukon.dialog.confirm.js"),
    YUKON_SURVEYS_LIST("/JavaScript/yukon.surveys.list.js"),
    YUKON_SURVEYS_EDIT("/JavaScript/yukon.surveys.edit.js"),
    YUKON_SURVEYS_OPT_OUT("/JavaScript/yukon.surveys.optOut.js"),
    YUKON_TIME_FORMATTER("/JavaScript/yukon.format.time.js"),
    
    MOMENT("/resources/js/lib/moment/moment.min.js"),
    MOMENT_TZ("/resources/js/lib/moment/moment-timezone-with-data.min.js"),
    JS_TIMEZONE_DETECT("/resources/js/lib/js.timezone.detect/jstz-1.0.4.min.js"),
    MODERNIZR("/JavaScript/lib/modernizr/custom.js");
    
    private String path = "";
    
    JsLibrary(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
}