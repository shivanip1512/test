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
public enum JSLibrary {
    PROTOTYPE("/JavaScript/lib/prototype/1.7.0.0/", "prototype.js"),
    SCRIPTACULOUS("/JavaScript/lib/scriptaculous/1.8.3/", "scriptaculous.js"),
    OVERLIB("/JavaScript/ol/", "overlib_mini.js"),
    
    // --DEPRECATED VERSIONS --//
    PROTOTYPE_150("/JavaScript/", "prototype150.js");
    //-- END DEPRECATED VERSIONS --//
    
    private String path = "";  //path to library
    private String defaultComponent = ""; //default library component file
    
    JSLibrary(String path, String defaultComponent) {
        this.path = path;
        this.defaultComponent = defaultComponent;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getDefaultComponent() {
        return defaultComponent;
    }
    
    public String getDefaultInclude() {
        return path + defaultComponent;
    }
}
