package com.cannontech.esub.util;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.CurrentAlarmsTable;
import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.editor.element.DynamicGraphElement;
import com.cannontech.esub.editor.element.DynamicText;
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
	
	private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;	
	private static final Random randomGen = new Random(System.currentTimeMillis());
	
	// flags to decide whenter to generate code for editing and controlling
	private boolean editEnabled = false;
	private boolean controlEnabled = false;
		
	public SVGGenerator() {
	} 
	
	public void generate(Writer writer, Drawing d, boolean edit, boolean control) throws IOException {
		setEditEnabled(edit);
	 	setControlEnabled(control);
		generate(writer, d);
	}	
	
	/**  
	 * Writes an svg document to the given write based on the graph passed.
	 * @param writer
	 * @param graph
	 * @throws IOException
	 */
	public void generate(Writer writer, Drawing d) throws IOException {		
	 	LxGraph graph = d.getLxGraph();
	 	
	 	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	 	SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
		
	 	// get the root element (the svg element)
		Element svgRoot = doc.getDocumentElement();

		svgRoot.setAttributeNS(null, "width", Integer.toString(d.getMetaElement().getDrawingWidth()));
		svgRoot.setAttributeNS(null, "height", Integer.toString(d.getMetaElement().getDrawingHeight()));
	 	svgRoot.setAttributeNS(null, "onload", "refresh(evt)");
	 		 	
		Element scriptElem = doc.createElementNS(null, "script");
		scriptElem.setAttributeNS(null, "type", "text/ecmascript");
		scriptElem.setAttributeNS(null, "xlink:href", "refresh.js");
		svgRoot.appendChild(scriptElem);
		
		Element scriptElem2 = doc.createElementNS(null, "script");
		scriptElem2.setAttributeNS(null, "type", "text/ecmascript");
		scriptElem2.setAttributeNS(null, "xlink:href", "updateGraph.js");
		svgRoot.appendChild(scriptElem2);
		
		Element scriptElem3 = doc.createElementNS(null, "script");
		scriptElem3.setAttributeNS(null, "type", "text/ecmascript");
		scriptElem3.setAttributeNS(null, "xlink:href", "action.js");
		svgRoot.appendChild(scriptElem3);
		
		Element backRect = doc.createElementNS(svgNS, "rect");
		backRect.setAttributeNS(null, "width", "100%");
		backRect.setAttributeNS(null, "height", "100%");
		backRect.setAttributeNS(null, "color", "#000000");
		svgRoot.appendChild(backRect);	 		

		LxComponent[] c	= graph.getComponents();
		for( int i = 0; i < c.length; i++ ) {
			Element elem = createElement(doc,c[i]);
			if(elem != null)
				svgRoot.appendChild(elem);
		}
	
		OutputFormat format  = new OutputFormat( doc );   //Serialize DOM
        XMLSerializer    serial = new XMLSerializer(writer, format);
        serial.asDOMSerializer();                            // As a DOM Serializer
        serial.serialize( doc.getDocumentElement() );       		 		
	}
	
	private Element createElement(SVGDocument doc, LxComponent comp) {
			
			Element elem = null;
			
			if( comp instanceof LxLine ) {
				elem = createLine(doc, (LxLine) comp);
			}
			else			
			if( comp instanceof LxRectangle ) {
				elem = createRectangle(doc, (LxRectangle) comp);
			}
			else
			if( comp instanceof StaticImage ) {
				elem = createStaticImage(doc, (StaticImage) comp);
			}
			else
			if( comp instanceof StaticText ) {
				elem = createStaticText(doc, (StaticText) comp);
			}
			else
			if( comp instanceof StateImage ) {
				elem = createStateImage(doc, (StateImage) comp);
			}
			else 
			if( comp instanceof DynamicText ) {
				elem = createDynamicText(doc, (DynamicText) comp);
			}
			else
			if( comp instanceof DynamicGraphElement ) {
				elem = createDynamicGraph(doc, (DynamicGraphElement) comp);
			}
			else
			if( comp instanceof CurrentAlarmsTable ) {
				elem = createAlarmsTable(doc, (CurrentAlarmsTable) comp);
			}	
				
			if( comp instanceof DrawingElement ) {
				DrawingElement de = (DrawingElement) comp;
				String link = de.getLinkTo();
				if(link != null && link.length() > 0) {
					elem.setAttributeNS(null,"onclick", "followLink(\"" + link + "\")");
				}
			}
			
			if( elem != null ) {
				elem.setAttributeNS(null,"classid",comp.getClass().getName());					
			}
		
		return elem;
	}
	
	private Element createDynamicText(SVGDocument doc, DynamicText text)  {
		//Ignore stroke color for now, always use fill color
		//could become a problem, pay attention
		Rectangle2D r = text.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMaxY();

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
		
		if(isEditEnabled() && text.isEditable()) {
			textElem.setAttributeNS(null, "onclick", "editValue(evt)");	
		}
		
		Text theText = doc.createTextNode("-");
		textElem.insertBefore(theText, null);
		
		return textElem;					
	}

	private Element createRectangle(SVGDocument doc, LxRectangle rect) {
		
		Color c = rect.getStyle().getLineColor();
		Shape[] s = rect.getShape();
		float opacity = rect.getStyle().getTransparency();
	
		String pathStr = getPathString(s, rect.getCenterX(), rect.getCenterY());
		float width = rect.getStyle().getLineThickness();
		
		Element rectElem = doc.createElementNS(svgNS, "path");
		rectElem.setAttributeNS(null, "id", rect.getName());
		rectElem.setAttributeNS(null, "style", "fill:none;opacity:" + opacity + ";stroke:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "); stroke-width:" + width);
		rectElem.setAttributeNS(null, "d", pathStr);
		
		return rectElem;
	}
	
	/**
	 * Creates a svg line element
	 * @param writer
	 * @param line
	 * @throws IOException
	 */
	private  Element createLine(SVGDocument doc, LxLine line)  {		
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
			
		Rectangle2D r = graph.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;
			
		Element retElement = null;
		
		//graph.updateGraph();
		SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);
		//graph.getCTIGraph().getFreeChart().draw(svgGenerator, new Rectangle(width,height));
		retElement = svgGenerator.getRoot();

		//java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:dd:ss");
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yy");
			
		retElement.setAttributeNS(null, "x", Integer.toString(x));
		retElement.setAttributeNS(null, "y", Integer.toString(y));
		retElement.setAttributeNS(null, "width", Integer.toString(width));
		retElement.setAttributeNS(null, "height", Integer.toString(height));			
		retElement.setAttributeNS(null, "object", "graph");
		retElement.setAttributeNS(null, "gdefid", Integer.toString(graph.getGraphDefinitionID()));
//		retElement.setAttributeNS(null, "model",  Integer.toString(graph.getTrendType()));
		retElement.setAttributeNS(null, "view", Integer.toString(graph.getTrendType()));
		retElement.setAttributeNS(null, "format", "svg");
		retElement.setAttributeNS(null, "db", CtiUtilities.getDatabaseAlias());
		retElement.setAttributeNS(null, "loadfactor", "false");
		retElement.setAttributeNS(null, "start", dateFormat.format(graph.getCurrentStartDate()));
//		retElement.setAttributeNS(null, "end", dateFormat.format(graph.getCurrentEndDate()));
		retElement.setAttributeNS(null, "period", graph.getDisplayPeriod());
		retElement.setAttributeNS(null, "onclick", "updateGraphChange(evt)");
		
		return retElement;
	}
	

	private Element createStaticImage(SVGDocument doc, StaticImage img) {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;		
		String relImage = img.getYukonImage().getImageName();	
		
		Element imgElem = doc.createElementNS(svgNS, "image");
		imgElem.setAttributeNS(null, "id", img.getName());
		imgElem.setAttributeNS(null, "xlink:href", relImage);
		//imgElem.setAttributeNS(null, "x", Integer.toString(x));
		//imgElem.setAttributeNS(null, "y", Integer.toString(y));
		//imgElem.setAttributeNS(null, "width", Integer.toString(width));
		//imgElem.setAttributeNS(null, "height", Integer.toString(height));
		return imgElem; 	 		
	}


	private Element createStateImage(SVGDocument doc, StateImage img) {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;

		String imgName = "X.gif";		

		Element imgElem = doc.createElementNS(svgNS, "image");
		imgElem.setAttributeNS(null, "id", Integer.toString(img.getPoint().getPointID()));
		imgElem.setAttributeNS(null, "xlink:href", imgName);
/*		imgElem.setAttributeNS(null, "x", Integer.toString(x));
		imgElem.setAttributeNS(null, "y", Integer.toString(y));
		imgElem.setAttributeNS(null, "width", Integer.toString(width));
		imgElem.setAttributeNS(null, "height", Integer.toString(height));*/
		return imgElem;		
	}	
	
	private Element createStaticText(SVGDocument doc, StaticText text) {
		//Ignore stroke color for now, always use fill color
		//could become a problem, pay attention
		Rectangle2D r = text.getBounds2D();
		int x = (int) r.getMinX();		
		int y = (int) r.getMaxY();

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
	
	private Element createAlarmsTable(SVGDocument doc, CurrentAlarmsTable table) {
		Rectangle2D r = table.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();

		int width = (int) r.getMaxX() - x; 
		int height = (int) r.getMaxY() - y;

		int ackX = width - 80;
		int ackY = 18;
					
		Element retElement = null;
				
		SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);
		retElement = svgGenerator.getRoot();
				
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:dd:ss");
						
		retElement.setAttributeNS(null, "x", Integer.toString(x));
		retElement.setAttributeNS(null, "y", Integer.toString(y));
		retElement.setAttributeNS(null, "width", Integer.toString(width));
		retElement.setAttributeNS(null, "height", Integer.toString(height));			
		retElement.setAttributeNS(null, "object", "table");
		retElement.setAttributeNS(null, "devicename", PAOFuncs.getYukonPAOName(table.getDeviceID()));
 		retElement.setAttributeNS(null, "deviceid", Integer.toString(table.getDeviceID())); 
		
		if(editEnabled) {
			Element text = doc.createElementNS(svgNS,"text");
			text.setAttributeNS(null, "fill","rgb(0,125,122)");
			text.setAttributeNS(null, "x", Integer.toString(ackX));
			text.setAttributeNS(null, "y", Integer.toString(ackY));
			text.setAttributeNS(null, "devicename", PAOFuncs.getYukonPAOName(table.getDeviceID()));
	 		text.setAttributeNS(null, "deviceid", Integer.toString(table.getDeviceID())); 
			text.setAttributeNS(null, "onclick", "acknowledgeAlarm(evt)");
			Text theText = doc.createTextNode("Clear Alarms");
			text.insertBefore(theText,null);
			retElement.appendChild(text);
		}
		
		return retElement;		
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
	/**
	 * Returns the controlEnabled.
	 * @return boolean
	 */
	public boolean isControlEnabled() {
		return controlEnabled;
	}

	/**
	 * Returns the editEnabled.
	 * @return boolean
	 */
	public boolean isEditEnabled() {
		return editEnabled;
	}

	/**
	 * Sets the controlEnabled.
	 * @param controlEnabled The controlEnabled to set
	 */
	public void setControlEnabled(boolean controlEnabled) {
		this.controlEnabled = controlEnabled;
	}

	/**
	 * Sets the editEnabled.
	 * @param editEnabled The editEnabled to set
	 */
	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}

}
