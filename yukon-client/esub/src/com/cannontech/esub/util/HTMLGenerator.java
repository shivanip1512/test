package com.cannontech.esub.util;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.esub.Drawing;

/**
 * Description Here
 * @author alauinger
 */
public class HTMLGenerator {
	private static final String header = 
	"<html>\n  <BODY BGCOLOR=\"#000000\" LINK=\"#000000\" ALINK=\"#000000\" VLINK=\"#000000\">\n";
	
	private static final String footer = 
	"</html>\n";
	
	
	public HTMLGenerator() {
	}
	
	public void generate(Writer w, Drawing d) throws IOException {
		String svgFile = new File(d.getFileName().replaceAll("jlx", "svg")).getName();
		
		String width = Integer.toString(d.getMetaElement().getDrawingWidth());
		String height = Integer.toString(d.getMetaElement().getDrawingHeight());
		
		w.write(header);
		w.write("<embed src=\"" + svgFile + "\" name=\"SVGEmbed\" width=\"" + width + "\" height=\"" + height + "\" type=\"image/svg-xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install\" wmode=\"transparent\" />");
		w.write(footer);
	}

} 