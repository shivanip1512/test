package com.cannontech.esub.util;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.editor.element.DynamicGraphElement;
import com.cannontech.esub.editor.element.DynamicText;
import com.cannontech.esub.editor.element.StateImage;
import com.cannontech.esub.editor.element.StaticImage;
import com.cannontech.esub.editor.element.StaticText;
import com.cannontech.graph.model.TrendModel;
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
	
	private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;	
		
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
	 	
	 	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	 	SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
	 	
	 	// get the root element (the svg element)
		Element svgRoot = doc.getDocumentElement();
		
		svgRoot.setAttributeNS(null, "width", "1024");
		svgRoot.setAttributeNS(null, "height", "768");
	 	svgRoot.setAttributeNS(null, "onload", "refresh(evt)");
	 	
		Element scriptElem = doc.createElementNS(null, "script");
		scriptElem.setAttributeNS(null, "type", "text/ecmascript");
		scriptElem.setAttributeNS(null, "xlink:href", "refresh.js");
		svgRoot.appendChild(scriptElem);
		 
		
		Element backRect = doc.createElementNS(svgNS, "rect");
		backRect.setAttributeNS(null, "width", "100%");
		backRect.setAttributeNS(null, "height", "100%");
		backRect.setAttributeNS(null, "color", "#000000");
		svgRoot.appendChild(backRect);

		LxComponent[] c	= graph.getComponents();
		for( int i = 0; i < c.length; i++ ) {
			/*if( c[i] instanceof DrawingElement &&
				((DrawingElement) c[i]).getLinkTo() != null &&
				((DrawingElement) c[i]).getLinkTo().length() > 0 ) {
				
				generateStartLink(writer, (DrawingElement) c[i]);
			}*/
			
			Element elem = null;
			
			if( c[i] instanceof LxLine ) {
				elem = createLine(doc, (LxLine) c[i]);
			}
			else			
			if( c[i] instanceof LxRectangle ) {
				elem = createRectangle(doc, (LxRectangle) c[i]);
			}
			else
			if( c[i] instanceof StaticImage ) {
				elem = createStaticImage(doc, (StaticImage) c[i]);
			}
			else
			if( c[i] instanceof StaticText ) {
				elem = createStaticText(doc, (StaticText) c[i]);
			}
			else
			if( c[i] instanceof StateImage ) {
				elem = createStateImage(doc, (StateImage) c[i]);
			}
			else 
			if( c[i] instanceof DynamicText ) {
				elem = createDynamicText(doc, (DynamicText) c[i]);
			}
			else
			if( c[i] instanceof DynamicGraphElement ) {
				elem = createDynamicGraph(doc, (DynamicGraphElement) c[i]);
			}

			if( c[i] instanceof DrawingElement &&
				((DrawingElement) c[i]).getLinkTo() != null &&
				((DrawingElement) c[i]).getLinkTo().length() > 0 ) {
					Element linkElem = createLink(doc, (DrawingElement) c[i]);
					
					if( elem != null ) {
						linkElem.insertBefore(elem, null);
						elem = linkElem;
					}					
			}
			
			if( elem != null ) {
				svgRoot.appendChild(elem);
			}
		}
		com.cannontech.esub.xml.Writer domWriter = new com.cannontech.esub.xml.Writer();
		domWriter.setOutput(writer);
		domWriter.write(doc);
		writer.flush();
		
/*		for( int i = 0; i < c.length; i++ ) {
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
		
		writer.flush();*/
	}
	
	private Element createLink(SVGDocument doc, DrawingElement elem) {
		Element linkElem = doc.createElementNS(svgNS, "a");
		linkElem.setAttributeNS(null, "xlink:href", elem.getLinkTo());
		return linkElem;		
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
	
	private Element createDynamicText(SVGDocument doc, DynamicText text) throws IOException {
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
	
		Element textElem = doc.createElementNS(svgNS, "text");
		textElem.setAttributeNS(null, "id", Integer.toString(text.getPointID()));
		textElem.setAttributeNS(null, "dattrib", Integer.toString(text.getDisplayAttribs()));
		textElem.setAttributeNS(null, "x", Integer.toString(x));
		textElem.setAttributeNS(null, "y", Integer.toString(y));
		textElem.setAttributeNS(null, "style", "fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";");
		
		Text theText = doc.createTextNode(text.getText());
		textElem.insertBefore(theText, null);
		
		return textElem;
					
	}

	private Element createRectangle(SVGDocument doc, LxRectangle rect) {
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
		
		Element rectElem = doc.createElementNS(svgNS, "rect");
		rectElem.setAttributeNS(null, "id", rect.getName());
		rectElem.setAttributeNS(null, "x", Integer.toString(x));
		rectElem.setAttributeNS(null, "y", Integer.toString(y));
		rectElem.setAttributeNS(null, "width", Integer.toString(width));
		rectElem.setAttributeNS(null, "height", Integer.toString(height));
		rectElem.setAttributeNS(null, "style", "fill:" + fillStr + ";stroke:rgb(" + strokeColor.getRed() + "," + strokeColor.getGreen() + "," + strokeColor.getBlue() + "); stroke-width:1.0");
		
		return rectElem;
	}
	
	/**
	 * Creates a svg line element
	 * @param writer
	 * @param line
	 * @throws IOException
	 */
	private  Element createLine(SVGDocument doc, LxLine line) throws IOException {		
		Color c = line.getStyle().getLineColor();
		Shape[] s = line.getShape();
		float opacity = line.getStyle().getTransparency();
		
		String pathStr = getPathString(s, line.getCenterX(), line.getCenterY());
		float width = line.getStyle().getLineThickness();
		
		Element lineElem = doc.createElementNS(svgNS, "path");
		lineElem.setAttributeNS(null, "id", line.getName());
		lineElem.setAttributeNS(null, "style", "fill:none;opacity:" + opacity + ";stroke:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "); stroke-width:" + width);
		lineElem.setAttributeNS(null, "d", pathStr);
		
		return lineElem;		
	}
	
	private Element createDynamicGraph(SVGDocument doc, DynamicGraphElement graph) {
			
		Rectangle2D r = graph.getStrokedBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;
			
		Element retElement = null;
		
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID(new Integer(611));
	
		if (gDef != null)
		{		
			java.sql.Connection conn = null;
			try
			{
				conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
				gDef.setDbConnection(conn);
				gDef.retrieve();

				// Lose the reference to the connection
				gDef.setDbConnection(null);
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();		 
			}
			finally
			{   //make sure to close the connection
				try { if( conn != null ) conn.close(); } catch( java.sql.SQLException e2 ) { e2.printStackTrace(); };
			}
					
			gDef.getGraphDefinition().setStartDate( new Date("9/5/2002") );
			gDef.getGraphDefinition().setStopDate( new Date("9/8/2002") );
						
			com.cannontech.graph.Graph ctiGraph = new com.cannontech.graph.Graph();
			ctiGraph.setDatabaseAlias(CtiUtilities.getDatabaseAlias());
			ctiGraph.setSize(width,height);
			ctiGraph.setCurrentGraphDefinition(gDef);
			ctiGraph.setSeriesType( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);

			ctiGraph.setModelType(0);
			
			ctiGraph.update();			

			org.apache.batik.svggen.SVGGraphics2D svgGenerator = 
				new org.apache.batik.svggen.SVGGraphics2D(doc);
		
			ctiGraph.getFreeChart().draw(svgGenerator, new Rectangle(width,height));
			retElement = svgGenerator.getRoot();
			
			retElement.setAttributeNS(null, "x", Integer.toString(x));
			retElement.setAttributeNS(null, "y", Integer.toString(y));
			retElement.setAttributeNS(null, "width", Integer.toString(width));
			retElement.setAttributeNS(null, "height", Integer.toString(height));
			
	}
		return retElement;
	}
	

	private Element createStaticImage(SVGDocument doc, StaticImage img) throws IOException {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;		
		String relImage = img.getYukonImage().getImageName();	
		
		Element imgElem = doc.createElementNS(svgNS, "image");
		imgElem.setAttributeNS(null, "id", img.getName());
		imgElem.setAttributeNS(null, "xlink:href", relImage);
		imgElem.setAttributeNS(null, "x", Integer.toString(x));
		imgElem.setAttributeNS(null, "y", Integer.toString(y));
		imgElem.setAttributeNS(null, "width", Integer.toString(width));
		imgElem.setAttributeNS(null, "height", Integer.toString(height));
		return imgElem; 	 		
	}


	private Element createStateImage(SVGDocument doc, StateImage img) throws IOException {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;

		String imgName = "X.gif";		
		LiteState ls = img.getCurrentState();

		if( ls != null ) {
			LiteYukonImage lyi = YukonImageFuncs.getLiteYukonImage(ls.getImageID());			
			if( lyi != null ) {
				imgName = lyi.getImageName();
			}
		}

		Element imgElem = doc.createElementNS(svgNS, "image");
		imgElem.setAttributeNS(null, "id", imgName);
		imgElem.setAttributeNS(null, "xlink:href", imgName);
		imgElem.setAttributeNS(null, "x", Integer.toString(x));
		imgElem.setAttributeNS(null, "y", Integer.toString(y));
		imgElem.setAttributeNS(null, "width", Integer.toString(width));
		imgElem.setAttributeNS(null, "height", Integer.toString(height));
		return imgElem;		
	}	
	
	private Element createStaticText(SVGDocument doc, StaticText text) throws IOException {
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
		Element textElem = doc.createElementNS(svgNS, "text");
		textElem.setAttributeNS(null, "id", text.getName());
		textElem.setAttributeNS(null, "x", Integer.toString(x));
		textElem.setAttributeNS(null, "y", Integer.toString(y));
		textElem.setAttributeNS(null, "style", "fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";");
		
		Text theText = doc.createTextNode(text.getText());
		textElem.insertBefore(theText, null);
		
		return textElem;
		
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
						CTILogger.info("unknown path type");
				}	
			
				pi.next();
			}
		}
		return pathStr;		
	}		
}
