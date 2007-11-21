package com.cannontech.esub.util;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.esub.Drawing;

/**
 * Generates html for an esub drawing. There is a dependendency on the
 * EmbedSVGJSServlet as well as web.xml to avoid a particular bug in IE. Check
 * out EmbedSVGJSServlet for details regarding the problem.
 * @author alauinger
 * @see com.cannontech.esub.servlet.EmbedSVGJSServlet
 */
public class HTMLGenerator {
	private static final String footer = "</body></html>\n";

    private String script = "<script type=\"text/JavaScript\" src=\"PopupWindow.js\"></script>" + "<script type=\"text/JavaScript\" src=\"AnchorPosition.js\"></script>" + "<script type=\"text/JavaScript\" src=\"Calendar1-82.js\"></script>" + "<script type=\"text/JavaScript\" src=\"updateGraph.js\"></script>" + "<script type=\"text/JavaScript\" src=\"point.js\"></script>";
    private HTMLOptions genOptions;

    public HTMLGenerator() {
        this(new HTMLOptions());
    }

    public HTMLGenerator(HTMLOptions options) {
        genOptions = options;
    }

    /*
     * Generate html for a given Drawing, the drawing must be initialized
     */
    public void generate(Writer w, Drawing d) throws IOException {
        String svgFile = new File(d.getFileName().replaceAll("jlx", "svg")).getName();

        int width = d.getMetaElement().getDrawingWidth();
        int height = d.getMetaElement().getDrawingHeight();
        String htmlColor = d.getMetaElement().getDrawingHTMLColor();
        
        generate(w, svgFile, width, height, htmlColor);

    }

    /*
     * Generate an html file for a given svg file, use this if you don't want to
     * load the drawing before generating the html.
     */
    public void generate(Writer w, String svgFile, int width, int height, String htmlColor)
            throws IOException {
        w.write(createHeaderHtml(htmlColor));
        w.write("<DIV ID=\"graphsettings\" STYLE=\"position:absolute;visibility:hidden;background-color:" + htmlColor +";width:300px;height:175px;\"></DIV>");
        w.write("<DIV ID=\"controlrequest\" STYLE=\"position:absolute;visibility:hidden;background-color:" + htmlColor +";\"></DIV>");
        writeAdditionalFields(w);
        if (!genOptions.isStaticHTML()) {
            w.write(getScript());
        }
        w.write("<A NAME=\"popupanchor\" ID=\"popupanchor\" > </A>");
        w.write("<script src='embedSVGControl.js?svgfile=" + svgFile + "&width=" + width + "&height=" + height + "'></script>");
        w.write(footer);
    }

    public void writeAdditionalFields(Writer w) {
        //do nothing here
    }

    public String createHeaderHtml(String htmlColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n  <BODY BGCOLOR=");
        sb.append(quoteString(htmlColor));
        sb.append(" LINK=");
        sb.append(quoteString(htmlColor));
        sb.append(" ALINK=");
        sb.append(quoteString(htmlColor));
        sb.append(" VLINK=");
        sb.append(quoteString(htmlColor));
        sb.append(">\n");
        return sb.toString();
    }
    
    private String quoteString(String s) {
        return new StringBuilder().append("\"").append(s).append("\"").toString();
    }
    
    public HTMLOptions getGenOptions() {
        return genOptions;
    }

    public void setGenOptions(HTMLOptions genOptions) {
        this.genOptions = genOptions;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

}