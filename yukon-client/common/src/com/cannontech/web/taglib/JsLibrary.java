package com.cannontech.web.taglib;

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
    EXT_ALL("/JavaScript/extjs/ext-all.js"),
    EXT_BASE("/JavaScript/extjs/ext-base.js"),

    //if updating jquery be sure to include the noconflict call at the end of the file if prototype
    //if ext is still being included
    JQUERY("/JavaScript/lib/jQuery/1.6.4/jquery.min.js"),       //http://www.jquery.com
    JQUERY_COOKIE("/JavaScript/lib/jQuery/plugins/cookie/jquery.cookie.js"),
    JQUERY_FORM("/JavaScript/lib/jQuery/plugins/form/jquery.form.js"),  //http://jquery.malsup.com/form/
    JQUERY_GRID("/JavaScript/lib/jQuery/plugins/jqGrid/4.2.0/js/jquery.jqGrid.min.js"),         //http://www.trirand.com/jqgridwiki/doku.php?id=wiki:jqgriddocs
    JQUERY_GRID_HELPER("/JavaScript/lib/jQuery/plugins/jqGrid/jqGridHelper.js"),         //in-house
    JQUERY_NOCONFLICT("/JavaScript/lib/jQuery/1.6.4/jquery.noconflict.min.js"), //http://api.jquery.com/jQuery.noConflict/
    JQUERY_SCROLLTO("/JavaScript/lib/jQuery/plugins/scrollTo/jquery.scrollTo-1.4.2-min.js"),    //http://demos.flesler.com/jquery/scrollTo/
    JQUERY_TREE("/JavaScript/lib/jQuery/plugins/dynatree/jquery.dynatree.min.js"),  //http://wwwendt.de/tech/dynatree/doc/dynatree-doc.html
    JQUERY_TREE_HELPERS("/JavaScript/lib/jQuery/plugins/dynatree/treeHelper.js"),   //in-house
    JQUERY_UI("/JavaScript/lib/jQuery/plugins/jQueryUI/jquery-ui-1.8.16.custom.min.js"),    //http://www.jqueryui.com

    JSON("/JavaScript/lib/JSON/2.0/json2.js"),
    OVERLIB("/JavaScript/ol/overlib_mini.js"),
    PROTOTYPE("/JavaScript/lib/prototype/1.7.0.0/prototype.js"),
    RSV("/JavaScript/lib/prototype/plugins/rsv/2.5.1/prototype.rsv.js"),
    SCRIPTACULOUS("/JavaScript/lib/scriptaculous/1.8.3/scriptaculous.js"),
    SCRIPTACULOUS_EFFECTS("/JavaScript/lib/scriptaculous/1.8.3/effects.js"),
    YUKON_UI("/JavaScript/yukon/ui/general.js"),
    
    // --DEPRECATED VERSIONS --//
    PROTOTYPE_150("/JavaScript/prototype150.js"),
    SCRIPTACULOUS_161("/JavaScript/lib/scriptaculous/1.6.1/scriptaculous.js"),
    SCRIPTACULOUS_EFFECTS_161("/JavaScript/lib/scriptaculous/1.6.1/effects.js");
    //-- END DEPRECATED VERSIONS --//
    
    private String path = "";  //path to library
    
    JsLibrary(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}
