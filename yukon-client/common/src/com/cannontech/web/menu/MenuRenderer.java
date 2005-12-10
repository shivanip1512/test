package com.cannontech.web.menu;

import java.io.IOException;
import java.io.Writer;

/**
 * Some day there may be other MenuRenderers. So, this interface describes what
 * must be provided. If an addional renderer is created, a factory class should
 * be created to control which one is used.
 */
public interface MenuRenderer {

    public String renderMenu();

    public void renderMenu(Writer out) throws IOException;

    public void setBreadCrumb(String breadCrumbs);

}