package com.cannontech.esub.svg;

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.esub.Drawing;
import com.loox.jloox.LxComponent;

public interface ISVGGenerator {

    /**  
     * Writes an svg document to the given write based on the graph passed.
     * @param writer
     * @param graph
     * @throws IOException
     */
    public void generate(Writer writer, Drawing d) throws IOException;

    public void addBehavior(LxComponent comp, Element elem);
    public void addJSFiles(SVGDocument doc, Element svgRoot);
    
    public void setGenOptions (SVGOptions o);
}