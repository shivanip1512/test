package com.cannontech.esub.util;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.FunctionElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.util.ServletUtil;
import com.loox.jloox.LxAbstractImage;
import com.loox.jloox.LxAbstractStyle;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;
import com.loox.jloox.LxRectangle;

/**
 * Generates an svg document given an Esub drawing
 * @see SVGOptions
 * @author alauinger
 */
public class SVGGenerator {
	
	private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	private static final String adobeNS = "http://www.adobe.com/svg10-extensions";
	private static final String xlinkNS = "http://www.w3.org/1999/xlink";
	
	private SVGOptions genOptions;
	
	// Used to generate as well as accumulate javascript during genreation.
	private JS jsGenerator = new JS();
		
	public SVGGenerator() {
		this(new SVGOptions());
	} 
	
	public SVGGenerator(SVGOptions options) {
		genOptions = options;
	}
		
	/**  
	 * Writes an svg document to the given write based on the graph passed.
	 * @param writer
	 * @param graph
	 * @throws IOException
	 */
	public void generate(Writer writer, Drawing d) throws IOException {	
		if(!genOptions.isStaticSVG()) {
			DrawingUpdater updater = new DrawingUpdater(d);
			updater.setUpdateGraphs(false);
		 	updater.updateDrawing();
		}    
					
	 	LxGraph graph = d.getLxGraph();
	 	
	 	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	 	SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);

	 	ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "href=\"esub.css\" type=\"text/css\""); 
	 	doc.insertBefore(pi, doc.getDocumentElement());
	 	
	 	// get the root element (the svg element)
		Element svgRoot = doc.getDocumentElement();
		svgRoot.setAttributeNS(null, "xmlns", svgNS);
		svgRoot.setAttributeNS(null, "xmlns:xlink", xlinkNS);
		svgRoot.setAttributeNS(null, "xmlns:a", adobeNS);
		svgRoot.setAttributeNS(null, "a:timeline", "independent");
		svgRoot.setAttributeNS(null, "viewBox", "0 0 " + d.getMetaElement().getDrawingWidth() + " " + d.getMetaElement().getDrawingHeight());
		svgRoot.setAttributeNS(null, "displayName", d.getFileName());
		
		Element backRect = doc.createElementNS(svgNS, "rect");
		backRect.setAttributeNS(null, "id", "backgroundRect");
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

		// If there is any accumulated scripting insert it now
		if(genOptions.isScriptingEnabled()) {
			Element e = doc.createElementNS(null, "script");
			e.setAttributeNS(null, "type", "text/ecmascript");
			CDATASection cdata = doc.createCDATASection(jsGenerator.getScript());
			e.appendChild(cdata);
			svgRoot.insertBefore(e, svgRoot.getFirstChild());
		}
		
		if(!genOptions.isStaticSVG() && genOptions.isScriptingEnabled()) {			
	 		svgRoot.setAttributeNS(null, "onload", "_onload(evt)");

	 		svgRoot.insertBefore(createScriptElement(doc, "refresh.js"), svgRoot.getFirstChild());
	 		svgRoot.insertBefore(createScriptElement(doc, "updateGraph.js"), svgRoot.getFirstChild());
	 		svgRoot.insertBefore(createScriptElement(doc, "action.js"), svgRoot.getFirstChild());
	 		svgRoot.insertBefore(createScriptElement(doc, "point.js"), svgRoot.getFirstChild());
	 		svgRoot.insertBefore(createScriptElement(doc, "xmlhttp.js"), svgRoot.getFirstChild());
	 		svgRoot.insertBefore(createScriptElement(doc, "cgui_lib.js"), svgRoot.getFirstChild());
		}	

		if(genOptions.isAudioEnabled()) {
			// Generate the audio elemnt if necessary, but do not start it
			// The client script will be responsible for enabling the audio
			// TODO: Figure out a way to not play a sound without specifying a
			// wav file that sounds like silence.
			// The current method of toggling between sound and no sound
			// is to change the href from a wav file that makes sound to
			// one that doesn't.  The rub is that the silent file has to be 
			// good, it can't not exist or just be empty otherwise the 
			// plugin seems to not want to play any sounds again.  humph.
			Element audioElem = doc.createElementNS(adobeNS, "a:audio");
			audioElem.setAttributeNS(null, "id", "audioElement");
			audioElem.setAttributeNS(null, "xlink:href", "silence.wav");
			audioElem.setAttributeNS(null, "repeatCount", "indefinite");
			svgRoot.insertBefore(audioElem, svgRoot.getFirstChild());
		}

		OutputFormat format  = new OutputFormat( doc, "ISO-8859-1", true );   //Serialize DOM
		format.setIndenting(true);
		format.setMethod("xml");
		
        XMLSerializer    serial = new XMLSerializer(writer, format);
        serial.asDOMSerializer();                            
        serial.serialize( doc );    
        
        // clean up in case this object is reused.
        jsGenerator.reset();
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
			else
			if( comp instanceof AlarmTextElement ) {
				elem = createAlarmText(doc, (AlarmTextElement) comp);
			}
			else 
		    if( comp instanceof FunctionElement ) {
		    	elem = createFunction(doc, (FunctionElement) comp);
		    }
			
			if( elem != null ) {	
				if( comp instanceof DrawingElement ) {
					DrawingElement de = (DrawingElement) comp;
					String link = de.getLinkTo();
										
					elem.setAttributeNS(null,"elementID", de.getElementID());
					elem.setAttributeNS(null,"classid",comp.getClass().getName());
					
					if(link != null && link.length() > 0) {
						//remove any onclicks
						elem.removeAttributeNS(null, "onclick");
						
						if(comp instanceof LxAbstractText && genOptions.isScriptingEnabled()) {
							elem.setAttributeNS(null,"onmouseover", "underLine(evt.getTarget())");
							elem.setAttributeNS(null,"onmouseout", "noUnderLine(evt.getTarget())");
						}
						else 
						if(comp instanceof LxAbstractImage && genOptions.isScriptingEnabled()){						
							elem.setAttributeNS(null,"onmouseover", "addBorder(evt.getTarget())");
							elem.setAttributeNS(null,"onmouseout", "noBorder(evt.getTarget())");
						}
						
						Element linkElem = doc.createElementNS(xlinkNS, "a");
						linkElem.setAttributeNS(xlinkNS, "xlink:href", link);

						linkElem.appendChild(elem);
						elem = linkElem;
					}
				}
			}
			
		return elem;
	}
	
	private Element createDynamicText(SVGDocument doc, DynamicText text)  {
		
		int x = (int) Math.round(text.getBaseLinePoint1().getX());
		int y = (int) Math.round(text.getBaseLinePoint1().getY());
		
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
		
		if(!genOptions.isStaticSVG() && genOptions.isScriptingEnabled()) {
			if(genOptions.isEditEnabled() && text.isEditable()) {
				textElem.setAttributeNS(null, "onclick", "editValue(evt)");	
			} 
			else {
				textElem.setAttributeNS(null, "onclick", "showPointDetails(evt, " + String.valueOf(text.getControlEnabled()) + " )");
			}
		}
		
		Text theText = doc.createTextNode(text.getText());
		textElem.insertBefore(theText, null);
		
		return textElem;					
	}

	private Element createRectangle(SVGDocument doc, LxRectangle rect) {
		
		Color c = rect.getStyle().getLineColor();
		Shape[] s = rect.getShape();
		float opacity = rect.getStyle().getTransparency();
	
		String pathStr = getPathString(s, rect.getCenterX(), rect.getCenterY());
		float width = rect.getStyle().getLineThickness();
		
		String fillStr = "none";
		
		Paint p = rect.getPaint();
		if(p != null) {
			fillStr = "#" + ServletUtil.getHTMLColor((Color) p);		
		}

		Element rectElem = doc.createElementNS(svgNS, "path");
		rectElem.setAttributeNS(null, "id", rect.getName());
		rectElem.setAttributeNS(null, "style", "fill: " + fillStr + ";opacity:" + opacity + ";stroke:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "); stroke-width:" + width);
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
		
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yy"); 
		
		Element imgElem = doc.createElementNS(svgNS, "image");
 
		imgElem.setAttributeNS(null, "id", Integer.toString(graph.getGraphDefinitionID()));
		if(genOptions.isStaticSVG()) {
			imgElem.setAttributeNS(xlinkNS, "xlink:href", Util.genExportedGraphName(graph));
		}
		else {
			imgElem.setAttributeNS(xlinkNS, "xlink:href", "/servlet/GraphGenerator?action=EncodeGraph&gdefid=" + graph.getGraphDefinitionID() + "&view=" + graph.getTrendType() + "&width=" + width + "&height=" + height + "&format=png&start=" + dateFormat.format(graph.getCurrentStartDate()) + "&period=" + graph.getDisplayPeriod() + "&option=" + Integer.toString(GraphRenderers.LEGEND_MIN_MAX_MASK));
		}
		imgElem.setAttributeNS(null, "x", Integer.toString(x));
		imgElem.setAttributeNS(null, "y", Integer.toString(y));
		imgElem.setAttributeNS(null, "width", Integer.toString(width));
		imgElem.setAttributeNS(null, "height", Integer.toString(height));
		imgElem.setAttributeNS(null, "object", "graph");
		imgElem.setAttributeNS(null, "gdefid", Integer.toString(graph.getGraphDefinitionID()));
		imgElem.setAttributeNS(null, "view", Integer.toString(graph.getTrendType()));
		imgElem.setAttributeNS(null, "format", "png");
		imgElem.setAttributeNS(null, "db", CtiUtilities.getDatabaseAlias());
		imgElem.setAttributeNS(null, "option", Integer.toString(GraphRenderers.LEGEND_MIN_MAX_MASK));
		imgElem.setAttributeNS(null, "loadfactor", "false");
		imgElem.setAttributeNS(null, "start", dateFormat.format(graph.getCurrentStartDate()));
		imgElem.setAttributeNS(null, "period", graph.getDisplayPeriod());

		if(genOptions.isScriptingEnabled()) {
			imgElem.setAttributeNS(null, "onclick", "updateGraphChange(evt)");
		}
		
		return imgElem; 	 		
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
		imgElem.setAttributeNS(xlinkNS, "xlink:href", relImage);
		imgElem.setAttributeNS(null, "x", Integer.toString(x));
		imgElem.setAttributeNS(null, "y", Integer.toString(y));
		imgElem.setAttributeNS(null, "width", Integer.toString(width));
		imgElem.setAttributeNS(null, "height", Integer.toString(height));
		return imgElem; 	 		
	}

	private Element createStateImage(SVGDocument doc, StateImage img) {
		Rectangle2D r = img.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();
		int width = (int) r.getMaxX() - x;
		int height = (int) r.getMaxY() - y;

		String imgName = img.getImageName();

		Element imgElem = doc.createElementNS(svgNS, "image");
		imgElem.setAttributeNS(null, "id", Integer.toString(img.getPointID()));
		imgElem.setAttributeNS(xlinkNS, "xlink:href", imgName);
		imgElem.setAttributeNS(null, "x", Integer.toString(x));
		imgElem.setAttributeNS(null, "y", Integer.toString(y));
		imgElem.setAttributeNS(null, "width", Integer.toString(width));
		imgElem.setAttributeNS(null, "height", Integer.toString(height));
		
		List imageNames = img.getImageNames();
		for(int i = 0; i < imageNames.size(); i++) {
			String imageName = (String) imageNames.get(i);
			imgElem.setAttributeNS(null, "image" + i, imageName);
		}
		
		if(!genOptions.isStaticSVG() && genOptions.isScriptingEnabled()) {
			imgElem.setAttributeNS(null, "onclick", "showPointDetails(evt, " + String.valueOf(img.getControlEnabled()) + " )");
		}
		return imgElem;
	}	
	
	private Element createStaticText(SVGDocument doc, StaticText text) {

		int x = (int) Math.round(text.getBaseLinePoint1().getX());
		int y = (int) Math.round(text.getBaseLinePoint1().getY());

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
		textElem.setAttributeNS(null, "style", "fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + text.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + text.getFont().getSize() + ";opacity:" + opacity + ";cursor:move;");

		Text theText = doc.createTextNode(text.getText());
		textElem.insertBefore(theText, null);
		
		return textElem;		
	}
	
	/**
	 * Creates an alarm table element.  
	 * @param doc
	 * @param table
	 * @param alarmAudio	Add an attribute to the table to indicate alarm audio should play
	 * @return
	 */
	public Element createAlarmsTable(SVGDocument doc, CurrentAlarmsTable table) {
		Rectangle2D r = table.getBounds2D();
		int x = (int) r.getMinX();
		int y = (int) r.getMinY();

		int width = (int) r.getMaxX() - x; 
		int height = (int) r.getMaxY() - y;

					
		Element retElement = null;

		SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);
		
		table.getTable().draw(svgGenerator, new Rectangle(width, height));
		retElement = svgGenerator.getRoot();
		
		int[] deviceIds = table.getDeviceIds();
		int[] pointIds = table.getPointIds();
		int[] alarmCategoryIds = table.getAlarmCategoryIds();
		
		String deviceIdList = buildIdList(deviceIds);
		String pointIdList = buildIdList(pointIds);
		String alarmCategoryIdList = buildIdList(alarmCategoryIds);
		
		retElement.setAttributeNS(null, "x", Integer.toString(x));
		retElement.setAttributeNS(null, "y", Integer.toString(y));
		retElement.setAttributeNS(null, "width", Integer.toString(width));
		retElement.setAttributeNS(null, "height", Integer.toString(height));			
		retElement.setAttributeNS(null, "object", "table");
		retElement.setAttributeNS(null, "deviceid", deviceIdList);
		retElement.setAttributeNS(null, "pointid", pointIdList);
		retElement.setAttributeNS(null, "alarmcategoryid", alarmCategoryIdList);
		
		if(genOptions.isEditEnabled()) {
			int ackX = x + 5;
			int ackY = y + 6;

			String ackAlarmArgs = "'" + deviceIdList + "','" + pointIdList + "','" + alarmCategoryIdList + "'";
			jsGenerator.addButton(ackX, ackY, 150, 16, false, false, false, "CGUI.root", "Acknowledge Alarms", "acknowledgeAlarm(" + ackAlarmArgs + ");");
			
			int muteX = x + 170;
			int muteY = y + 6;
			jsGenerator.addButton(muteX, muteY, 150, 16, false, false, false, "CGUI.root", "Mute", "toggleMute();");		
		}
		
		return retElement;		
	}
	
	
	private Element createAlarmText(SVGDocument doc, AlarmTextElement alarmText) {

		int x = (int) Math.round(alarmText.getBaseLinePoint1().getX());
		int y = (int) Math.round(alarmText.getBaseLinePoint1().getY());
		
		LxAbstractStyle style = alarmText.getStyle();
		
		Color fillColor = (Color) style.getPaint();
		
		String fontStyleStr = "normal";
		if( alarmText.getFont().isItalic() ) {
			fontStyleStr = "italic";
		}
			
		String fontWeightStr = "normal";
		if( alarmText.getFont().isBold() ) {
			fontWeightStr = "bold";
		}
		
		float opacity = alarmText.getStyle().getTransparency();
	
		Color defaultColor = alarmText.getDefaultTextColor();
		Color alarmColor = alarmText.getAlarmTextColor();
		
		int[] deviceIds = alarmText.getDeviceIds();
		int[] pointIds = alarmText.getPointIds();
		int[] alarmCategoryIds = alarmText.getAlarmCategoryIds();

		Element textElem = doc.createElementNS(svgNS, "text");
		textElem.setAttributeNS(null, "deviceid", buildIdList(deviceIds));
		textElem.setAttributeNS(null, "pointid", buildIdList(pointIds));
		textElem.setAttributeNS(null, "alarmcategoryid", buildIdList(alarmCategoryIds));
		textElem.setAttributeNS(null, "x", Integer.toString(x));
		textElem.setAttributeNS(null, "y", Integer.toString(y));
		textElem.setAttributeNS(null, "style", "fill:rgb(" + fillColor.getRed() + "," + fillColor.getGreen() + "," + fillColor.getBlue() + ");font-family:'" + alarmText.getFont().getFontName() + "';font-style:" + fontStyleStr + ";font-weight:" + fontWeightStr + ";font-size:" + alarmText.getFont().getSize() + ";opacity:" + opacity + ";");
		textElem.setAttributeNS(null, "fill1", "rgb(" + defaultColor.getRed() + "," + defaultColor.getGreen() + "," + defaultColor.getBlue() + ")");
		textElem.setAttributeNS(null, "fill2", "rgb(" + alarmColor.getRed() + "," + alarmColor.getGreen() + "," + alarmColor.getBlue() + ")");
		
		Text theText = doc.createTextNode(alarmText.getText());
		textElem.insertBefore(theText, null);
		
		return textElem;					
	}
		
	public Element createFunction(SVGDocument doc, FunctionElement function) {
		String js = function.getScript();
		jsGenerator.addOnload(js);
		return null;
	}
	
	/**
	 * Create a String that is a comma seperated list of integer ids
	 * @param ids
	 * @return
	 */
	private String buildIdList(int[] ids) {
		StringBuffer idBuf = new StringBuffer(ids.length*4);
		if(ids.length > 0) {
			idBuf.append(Integer.toString(ids[0]));
		}
		for (int i = 1; i < ids.length; i++) {
			idBuf.append(',');
			idBuf.append(Integer.toString(ids[i]));
		}
		return idBuf.toString();
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
	 * Convience method to create an script element for use in
	 * building the document
	 * @param doc	
	 * @param jsFileName
	 * @return
	 */
	private Element createScriptElement(Document doc, String jsFileName) {
		Element elem = doc.createElementNS(null, "script");
		elem.setAttributeNS(null, "type", "text/ecmascript");
		elem.setAttributeNS(xlinkNS, "xlink:href", jsFileName);
		return elem;
	}
}
