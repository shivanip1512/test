package com.cannontech.esub.util;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.DynamicText;
import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.editor.element.StateImage;
import com.cannontech.esub.editor.element.StaticImage;
import com.cannontech.esub.editor.element.StaticText;

import com.loox.jloox.LxAbstractStyle;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;
import com.loox.jloox.LxRectangle;

/**
 * @author alauinger
 *
 * Generates an svg document given an LxGraph
 */
public class SVGGenerator {
		
	private static final String svgHeader = 
		"<?xml version = \"1.0\" standalone = \"no\"?>\n" + 
		"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20000802//EN\" \"http://www.w3.org/TR/2000/CR-SVG-20000802/DTD/svg-20000802.dtd\" >\n";
	
	private static final String svgFooter = 
		"</svg>";
		
	public SVGGenerator() {
	} 
	
	public void generate(Writer writer, Drawing d) throws IOException {
		generate(writer, d.getLxGraph());
	}
	/**  
	 * Writes an svg document to the given write based on the graph passed.
	 * @param writer
	 * @param graph
	 * @throws IOException
	 */
	public void generate(Writer writer, LxGraph graph) throws IOException {
	 	
		int width = 800; //graph.getView(0).getWidth();
		int height = 600;
		LxComponent[] c	= graph.getComponents();
		
		writer.write(svgHeader);
				 
		writer.write("<svg width=\"" + width + ".0px\" height=\"" + height + ".0px\" >\n");		
	 	
		// Force the background black, perhaps pick up the views background color in the future
		writer.write("<rect width=\"100%\" height=\"100%\" color=\"#000000\" />\n");
		
		for( int i = 0; i < c.length; i++ ) {
			if( c[i] instanceof DrawingElement &&
				((DrawingElement) c[i]).getLinkTo() != null &&
				((DrawingElement) c[i]).getLinkTo().length() > 0 ) {
				
				generateStartLink(writer, (DrawingElement) c[i]);
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
			
			if( c[i] instanceof DynamicText ) {
				generateDynamicText(writer, (DynamicText) c[i]);
			}
			
			if( c[i] instanceof StateImage ) {
				generateStateImage(writer, (StateImage) c[i]);
			}
			
			if( c[i] instanceof DrawingElement &&
				((DrawingElement) c[i]).getLinkTo() != null &&
				((DrawingElement) c[i]).getLinkTo().length() > 0 ) {
				generateEndLink(writer);
			}
		}
		
		writer.write(svgFooter);
		
		writer.flush();
	}
	
	/**
	 * Writes the opening tag for a hyperlink
	 * @param writer
	 * @param elem
	 * @throws IOException
	 */
	private void generateStartLink(Writer writer, DrawingElement elem) throws IOException {		
			writer.write("<a xlink:href=\"" + elem.getLinkTo() + "\">\n");
	}
	
	/**
	 * Writes an end link tag.
	 * @param writer
	 * @throws IOException
	 */
	private void generateEndLink(Writer writer) throws IOException {
		writer.write("</a>\n");
	}
	
	private void generateDynamicText(Writer writer, DynamicText text) throws IOException {
		//Ignore stroke color for now, always use fill color
		//could become a problem, pay attention
		Rectangle2D r = text.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMaxY();// + (int) ((r.getMaxY() - r.getMinY()) / 2);

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
		
		writer.write("<text id=\"" + text.getName() + "\" x=\"" + x + "\" y=\"" + y + "\" style=\"fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";\" >" + text.getText() + "</text>\n");		
	}
	/**
	 * Writes out an svg path given an LxLine
	 * @param writer
	 * @param line
	 * @throws IOException
	 */
	private void generateLine(Writer writer, LxLine line) throws IOException {
		Color c = line.getStyle().getLineColor();
		Shape[] s = line.getShape();
		float opacity = line.getStyle().getTransparency();
		
		String pathStr = getPathString(s, line.getCenterX(), line.getCenterY());

		float width = line.getStyle().getLineThickness();
		writer.write("<path id=\"" + line.getName() + "\" style=\"fill:none;opacity:" + opacity + ";stroke:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "); stroke-width:" + width + ";\" d=\"" + pathStr + "\" />\n");
	}
	
	/**
	 * Writes a svg rect tag for the given LxRectangle
	 * @param writer
	 * @param rect
	 * @throws IOException
	 */
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
	
	/**
	 * Writes a svg image tag for the given image.
	 * The image is external and a relative url to the image name is written.
	 * @param writer
	 * @param img
	 * @throws IOException
	 */
	private void generateImage(Writer writer, StaticImage img) throws IOException {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;
		
//	 	String relImage = Util.getRelativePath( new File(img.getDrawing().getFileName()), new File(img.getAbsoluteImagePath()));
		String relImage = img.calcRelativeImagePath();
	 	//relImage = relImage.replace('\\','/');
	 	
		writer.write("<image id=\"" + img.getName() + "\" xlink:href=\"" + relImage + "\" x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\" />\n");
	}
	
	private void generateStateImage(Writer writer, StateImage img) throws IOException {
			Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;
		
		String relImage = Util.getRelativePath( new File(img.getDrawing().getFileName()), new File(img.getAbsoluteImagePath(img.getState())));
	 	relImage = relImage.replace('\\','/');
	 	
		writer.write("<image id=\"" + img.getName() + "\" xlink:href=\"" + relImage + "\" x=\"" + x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\"" + height + "\" />\n");
	}
	
	/**
	 * Writes a svg chunk for text
	 * @param writer
	 * @param text
	 * @throws IOException
	 */
	private void generateStaticText(Writer writer, StaticText text) throws IOException {
		//Ignore stroke color for now, always use fill color
		//could become a problem, pay attention
		Rectangle2D r = text.getBounds2D();
		int x = (int) r.getMinX();		
		int y = (int) r.getMaxY();// + (int) ((r.getMaxY() - r.getMinY()) / 2);

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
		
		writer.write("<text id=\"" + text.getName() + "\" x=\"" + x + "\" y=\"" + y + "\" style=\"fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";\" >" + text.getText() + "</text>\n");			
//		writer.write("<text id=\"" + text.getName() + "\" style=\"fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";\" transform=\"translate(" + x + "," + y + ")\" >" + text.getText() + "</text>\n");	
	}
	
	/**
	 * Builds up a svg path string given a shape and the center of the element.
	 * @param s
	 * @param cx
	 * @param cy
	 * @return String
	 */
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
