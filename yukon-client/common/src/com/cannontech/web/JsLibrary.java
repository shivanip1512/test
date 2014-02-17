package com.cannontech.web;

/**
 * Enumeration for defining the 'correct' version of a library to include.  Currently used by
 * IncludeScriptTag.java
 * 
 * Ideally we would not include multiple versions of JS libraries in the application, however if 
 * it is required you can add those versions here in the DEPRECATED VERSIONS section.
 * 
 * Each Enumeration is defined as follows:
 * 
 *  JSLibrary(path)
 *  
 *  @param path         String      Relative path to the location of the library - DO NOT include any
 *                                  file names in this path
 */
public enum JsLibrary {
    //if updating jquery be sure to include the noconflict call at the end of the file if prototype
    JQUERY("/JavaScript/lib/jQuery/jquery-1.11.0.js"), //http://www.jquery.com
    JQUERY_MIN("/JavaScript/lib/jQuery/jquery-1.11.0.min.js"), //http://www.jquery.com
    
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
    JQUERY_SCROLLTO("/JavaScript/lib/jQuery/plugins/scrollTo/jquery.scrollTo.min.js"), //http://demos.flesler.com/jquery/scrollTo/
    JQUERY_SPECTRUM("/JavaScript/lib/jQuery/plugins/spectrum/spectrum.js"), //http://bgrins.github.io/spectrum/
    JQUERY_TRAVERSABLE("/JavaScript/lib/jQuery/plugins/traversable/jquery.traversable.js"), //https://github.com/adelegard/jquery-traverse
    JQUERY_TIPSY("/JavaScript/lib/jQuery/plugins/tipsy/javascripts/jquery.tipsy.js"), //https://github.com/jaz303/tipsy
    JQUERY_TREE("/JavaScript/lib/jQuery/plugins/dynatree/jquery.dynatree.min.js"), //http://wwwendt.de/tech/dynatree/doc/dynatree-doc.html
    JQUERY_TREE_HELPERS("/JavaScript/lib/jQuery/plugins/dynatree/treeHelper.js"), //in-house
    
    JQUERY_UI("/JavaScript/lib/jQueryUI/jquery-ui-1.10.4.custom.js"), //http://www.jqueryui.com
    JQUERY_UI_MIN("/JavaScript/lib/jQueryUI/jquery-ui-1.10.4.custom.min.js"), //http://www.jqueryui.com
    
    JQUERY_UI_DIALOG_HELPER("/JavaScript/lib/jQueryUI/plugins/dialog-helper.js"), //in-house
    JQUERY_UI_TIME_PICKER_ADDON("/JavaScript/lib/jQueryUI/plugins/datetimepicker/jquery-ui-timepicker-addon.js"), //https://github.com/trentrichardson/jQuery-Timepicker-Addon
    JQUERY_UI_WIDGET("/JavaScript/lib/jQuery/plugins/fileupload/jquery.ui.widget.js"), //https://github.com/blueimp/jQuery-File-Upload/wiki/Basic-plugin

    YUKON_FLOTCHARTS("/JavaScript/yukon.flot.js"), //in-house
    JQUERY_FLOTCHARTS("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_PIE("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.pie.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_SELECTION("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.selection.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_AXIS_LABEL("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.axislabels.js"), //https://github.com/markrcote/flot-axislabels
    JQUERY_FLOTCHARTS_RESIZE("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.resize.min.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_TIME("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.time.min.js"), //http://www.flotcharts.org/
    JQUERY_EXCANVAS("/JavaScript/lib/jQuery/plugins/flotcharts/excanvas.min.js"), //http://www.flotcharts.org/

    JSON("/JavaScript/lib/JSON/2.0/json2.js"),
    PROTOTYPE("/JavaScript/lib/prototype/1.7.0.0/prototype.js"),
    YUKON("/JavaScript/yukon/ui/general.js"),
    YUKON_ALERTS("/JavaScript/yukon/yukon.alerts.js"),
    YUKON_FAVORITES("/JavaScript/yukon/yukon.favorites.js"),
    YUKON_CONFIRM("/JavaScript/yukon/ui/confirm_dialog_manager.js"),
    YUKON_IVVC_ZONE("/JavaScript/yukon.ivvc.zone.js"),
    YUKON_SURVEYS_LIST("/JavaScript/yukon.surveys.list.js"),
    YUKON_SURVEYS_EDIT("/JavaScript/yukon.surveys.edit.js"),
    YUKON_SURVEYS_OPT_OUT("/JavaScript/yukon.surveys.optOut.js"),
    YUKON_TIME_FORMATTER("/JavaScript/timeFormatter.js"),
    
    MODERNIZR("/JavaScript/lib/modernizr/custom.js"),
    
    // --DEPRECATED VERSIONS --//
    PROTOTYPE_150("/JavaScript/prototype150.js");
    //-- END DEPRECATED VERSIONS --//
    
    private String path = "";  //path to library
    
    JsLibrary(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}
