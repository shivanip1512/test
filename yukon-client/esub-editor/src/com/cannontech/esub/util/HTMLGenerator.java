package com.cannontech.esub.util;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.esub.editor.Drawing;

/**
 * Description Here
 * @author alauinger
 */
public class HTMLGenerator {
	private static final String header = 
	"<html>\n<head><script language=\"JavaScript\" src=\"svgcheck.js\"></script><script language=\"VBScript\" src=\"svgcheck.vbs\"></script></head><script language=\"JavaScript\"><!-- checkAndGetSVGViewer(); // --></script>\n<BODY BGCOLOR=\"#000000\" LINK=\"#000000\" ALINK=\"#000000\" VLINK=\"#000000\">\n";
	
	private static final String footer = 
	"</html>\n";
	
	
	public HTMLGenerator() {
	}
	
	public void generate(Writer w, Drawing d) throws IOException {
		String svgFile = new File(d.getFileName().replaceAll("jlx", "svg")).getName();
		
		w.write(header);
//		generateEmbedString(w, d);
		//w.write("<script language=\"JavaScript\"><!--\nemitSVG('src=\"" + svgFile + "\" name=\"SVGEmbed\" height=\"800\" width=\"600\" type=\"image/svg-xml\"');\n// -->\n</script>\n<noscript>\n<embed src=\"" + svgFile + "\" name=\"SVGEmbed\" width=\"1024\" height=\"768\" type=\"image/svg-xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install\" wmode=\"transparent\" />\n</noscript>");
		w.write("<embed src=\"" + svgFile + "\" name=\"SVGEmbed\" width=\"1024\" height=\"768\" type=\"image/svg-xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install\" wmode=\"transparent\" />");
		w.write(footer);
	}

} 