package com.cannontech.web.menu.renderer;

import java.io.IOException;
import java.io.Writer;

import com.cannontech.web.menu.MenuFeatureSet;

/**
 * Some day there may be other MenuRenderers. So, this interface describes what
 * must be provided. If an additional renderer is created, a factory class should
 * be created to control which one is used.
 */
public interface MenuRenderer {

    public void renderMenu(Writer out) throws IOException;

    public void setBreadCrumb(String breadCrumbs);

    public void setFeatures(MenuFeatureSet features);
    
    public MenuFeatureSet getFeatures();
    
    public void setMenuSelection(String menuSelection);
    
    public void setHomeUrl(String homeUrl);

}