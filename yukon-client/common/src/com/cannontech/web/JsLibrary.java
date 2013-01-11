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
 *  JSLibrary(path, component)
 *  
 *  @param path         String      Relative path to the location of the library - DO NOT include any
 *                                  file names in this path
 *  @param component    String      The default component of the library.  Refer to your library's
 *                                  documentation to determine the single file include.
 *  
 */
public enum JsLibrary {
    //if updating jquery be sure to include the noconflict call at the end of the file if prototype
    JQUERY("/JavaScript/lib/jQuery/jquery-1.8.3.min.js"),       //http://www.jquery.com
    JQUERY_COOKIE("/JavaScript/lib/jQuery/plugins/cookie/jquery.cookie.js"),
    JQUERY_FORM("/JavaScript/lib/jQuery/plugins/form/jquery.form.js"),  //http://jquery.malsup.com/form/
    JQUERY_GRID("/JavaScript/lib/jQuery/plugins/jqGrid/4.2.0/js/jquery.jqGrid.min.js"),         //http://www.trirand.com/jqgridwiki/doku.php?id=wiki:jqgriddocs
    JQUERY_GRID_HELPER("/JavaScript/lib/jQuery/plugins/jqGrid/jqGridHelper.js"),         //in-house
    JQUERY_PLACEHOLDER("/JavaScript/lib/jQuery/plugins/placeholder/jquery.placeholder.js"), // https://github.com/thehappybit/jQuery-HTML5-placeholder-plugin
    JQUERY_SCROLLTO("/JavaScript/lib/jQuery/plugins/scrollTo/jquery.scrollTo-1.4.2-min.js"),    //http://demos.flesler.com/jquery/scrollTo/
    JQUERY_STICKY_PANEL("/JavaScript/lib/jQuery/plugins/stickyPanel/jquery.stickyPanel.1.4.1.min.js"),    //http://code.google.com/p/sticky-panel/
    JQUERY_TREE("/JavaScript/lib/jQuery/plugins/dynatree/jquery.dynatree.min.js"),  //http://wwwendt.de/tech/dynatree/doc/dynatree-doc.html
    JQUERY_TREE_HELPERS("/JavaScript/lib/jQuery/plugins/dynatree/treeHelper.js"),   //in-house
    JQUERY_UI("/JavaScript/lib/jQuery/plugins/jQueryUI/jquery-ui-1.9.2.custom.min.js"),    //http://www.jqueryui.com
    JQUERY_UI_DIALOG_HELPER("/JavaScript/lib/jQuery/plugins/jQueryUI/dialog-helper.js"), //in-house
    JQUERY_UI_CHECK_ALL("/JavaScript/lib/jQuery/plugins/checkAll/jquery.checkall.js"), //https://github.com/mjball/jQuery-CheckAll
    JQUERY_UI_ACTION_WHEN("/JavaScript/lib/jQuery/plugins/actionWhen/jquery.actionwhen.js"), //in-house
    JQUERY_TRAVERSABLE("/JavaScript/lib/jQuery/plugins/traversable/jquery.traversable.js"), //https://github.com/adelegard/jquery-traverse

    YUKON_FLOTCHARTS("/JavaScript/yukon.flot.js"), //in-house
    JQUERY_FLOTCHARTS("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_PIE("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.pie.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_SELECTION("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.selection.js"), //http://www.flotcharts.org/
    JQUERY_FLOTCHARTS_AXIS_LABEL("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.axislabels.js"), //https://github.com/markrcote/flot-axislabels
    JQUERY_FLOTCHARTS_RESIZE("/JavaScript/lib/jQuery/plugins/flotcharts/jquery.flot.resize.js"), //http://www.flotcharts.org/
    JQUERY_EXCANVAS("/JavaScript/lib/jQuery/plugins/flotcharts/excanvas.min.js"), //http://www.flotcharts.org/

    JSON("/JavaScript/lib/JSON/2.0/json2.js"),
    OVERLIB("/JavaScript/ol/overlib_mini.js"),
    PROTOTYPE("/JavaScript/lib/prototype/1.7.0.0/prototype.js"),
    RSV("/JavaScript/lib/prototype/plugins/rsv/2.5.1/prototype.rsv.js"),
    YUKON_UI("/JavaScript/yukon/ui/general.js"),
    
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
