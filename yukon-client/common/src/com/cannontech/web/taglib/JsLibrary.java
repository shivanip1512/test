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
    PROTOTYPE("/JavaScript/lib/prototype/1.7.0.0/prototype.js"),
    SCRIPTACULOUS("/JavaScript/lib/scriptaculous/1.8.3/scriptaculous.js"),
    SCRIPTACULOUS_EFFECTS("/JavaScript/lib/scriptaculous/1.8.3/effects.js"),
    OVERLIB("/JavaScript/ol/overlib_mini.js"),
    RSV("/JavaScript/lib/prototype/plugins/rsv/2.5.1/prototype.rsv.js"),
    EXT_BASE("/JavaScript/extjs/ext-base.js"),
    EXT_ALL("/JavaScript/extjs/ext-all.js"),
    COOPER_UI("/JavaScript/cooper/ui/general.js"),
    
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
