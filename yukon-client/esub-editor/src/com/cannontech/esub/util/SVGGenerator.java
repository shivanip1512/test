package com.cannontech.esub.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;

import javax.swing.text.Style;

import com.cannontech.esub.editor.element.LinkedElement;
import com.cannontech.esub.editor.element.StaticImage;
import com.cannontech.esub.editor.element.StaticText;
import com.loox.jloox.LxAbstractStyle;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;
import com.loox.jloox.LxRectangle;
import com.loox.jloox.LxStyle;

/**
 * @author alauinger
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SVGGenerator {
	private static final String svgHeader = 
		"<?xml version = \"1.0\" standalone = \"no\"?>\n" + 
		"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20000802//EN\" \"http://www.w3.org/TR/2000/CR-SVG-20000802/DTD/svg-20000802.dtd\" >\n";
	
	private static final String svgFooter = 
		"</svg>";
		
	public void generate(Writer writer, LxGraph graph) throws IOException {
		LxComponent[] c	= graph.getComponents();
		
		writer.write(svgHeader);
		writer.write("<svg width=\"" + graph.getView(0).getWidth() + ".0px\" height=\"" + graph.getView(0).getHeight() + ".0px\" >\n");
		writer.write("<rect width=\"100%\" height=\"100%\" color=\"#000000\" />\n");
		
		for( int i = 0; i < c.length; i++ ) {
			if( c[i] instanceof LinkedElement &&
				((LinkedElement) c[i]).getLinkTo() != null &&
				((LinkedElement) c[i]).getLinkTo().length() > 0 ) {
				
				generateStartLink(writer, (LinkedElement) c[i]);
			}
			
			if( c[i] instanceof LxLine ) {
				generateLine(writer, (LxLine) c[i]);
			}
			
			if( c[i] instanceof LxRectangle ) {
				generateRect(writer, (LxRectangle) c[i]);
			}
			
			if( c[i] instanceof StaticImage ) {
				generateImage(writer, (StaticImage) c[i]);
			}
			
			if( c[i] instanceof StaticText ) {
				generateStaticText(writer, (StaticText) c[i]);
			}
			
			if( c[i] instanceof LinkedElement &&
				((LinkedElement) c[i]).getLinkTo() != null &&
				((LinkedElement) c[i]).getLinkTo().length() > 0 ) {
				generateEndLink(writer);
			}
		}
		
		writer.write(svgFooter);
		writer.flush();
	}
	
	private void generateStartLink(Writer writer, LinkedElement elem) throws IOException {		
			writer.write("<a xlink:href=\"" + elem.getLinkTo() + "\">\n");
	}
	
	private void generateEndLink(Writer writer) throws IOException {
		writer.write("</a>\n");
	}
	
	private void generateLine(Writer writer, LxLine line) throws IOException {
		Color c = line.getStyle().getLineColor();
		Shape[] s = line.getShape();
		float opacity = line.getStyle().getTransparency();
		
		String pathStr = getPathString(s, line.getCenterX(), line.getCenterY());

		float width = line.getStyle().getLineThickness();
		writer.write("<path id=\"" + line.getName() + "\" style=\"fill:none;opacity:" + opacity + ";stroke:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "); stroke-width:" + width + ";\" d=\"" + pathStr + "\" />\n");
	}
	
	private void generateRect(Writer writer, LxRectangle rect) throws IOException {
		LxAbstractStyle style = rect.getStyle();
		Color strokeColor = style.getLineColor();
		Color fillColor = (Color) style.getPaint();
		
		Rectangle2D r = rect.getStrokedBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;
			
		String fillStr = "none";
		if( fillColor != null ) {
			fillStr = "rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ")";
		}
		
		writer.write("<rect id=\"" + rect.getName() + "\" x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:" + fillStr + ";stroke:rgb(" + strokeColor.getRed() + "," + strokeColor.getGreen() + "," + strokeColor.getBlue() + "); stroke-width:1.0\" />\n");
	}
	
	private void generateImage(Writer writer, StaticImage img) throws IOException {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;
		
		writer.write("<image id=\"" + img.getName() + "\" xlink:href=\"" + img.getImageName() + "\" x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\" />\n");
	}
	
	private void generateStaticText(Writer writer, StaticText text) throws IOException {
		//Ignore stroke color for now, always use fill color
		//could become a problem, pay attention
		Rectangle2D r = text.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();

		LxAbstractStyle style = text.getStyle();
		
		Color fillColor = (Color) style.getPaint();
		
		String fontStyleStr = "normal";
		if( text.getFont().isItalic() ) {
			fontStyleStr = "italic";
		}
			
		String fontWeightStr = "normal";
		if( text.getFont().isBold() ) {
			fontWeightStr = "bold";
		}
		
		float opacity = text.getStyle().getTransparency();
		
		writer.write("<text id=\"" + text.getName() + "\" style=\"fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";\" transform=\"translate(" + x + "," + y + ")\" >" + text.getText() + "</text>\n");	
	}
	
	private String getPathString(Shape[] s, double cx, double cy) {
		String pathStr = "";
				
		//array to store segment info
		double[] seg = new double[6];
		for( int i = 0; i < s.length; i++ ) {
	 		PathIterator pi = s[i].getPathIterator(AffineTransform.getTranslateInstance(cx, cy));
			while( !pi.isDone() ) {
				int type = pi.currentSegment(seg);
				switch(type) {
					case PathIterator.SEG_MOVETO:
						pathStr += "M " + seg[0] + " " + seg[1] + " ";
						break;						
					case PathIterator.SEG_LINETO:
						pathStr += "L " + seg[0] + " " + seg[1] + " ";
						break;
					case PathIterator.SEG_CLOSE:
						pathStr += "Z ";
						break;
					default: 
						System.out.println("unknown path type");
				}	
			
				pi.next();
			}
		}
		return pathStr;	
	
	}
		
}
