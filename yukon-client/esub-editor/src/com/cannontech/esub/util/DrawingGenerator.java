package com.cannontech.esub.util;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.dom.svg.SVGStylableSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGStyleElement;
import org.xml.sax.SAXException;

import com.cannontech.esub.editor.Drawing;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxLine;

/**
 * Initialize an esubstation drawing from an svg document
 * @author alauinger
 *
 */
public class DrawingGenerator {
	public DrawingGenerator() { }
	
	public void generate(InputStream in, Drawing d) {


	// configure the document builder factory
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //factory.setValidating(true);
   // factory.setNamespaceAware(true);

    try
    {
      // get a builder to create a DOM document
      DocumentBuilder builder = factory.newDocumentBuilder();

      // parse the damned document
      Document doc = builder.parse(in);

      
      Element rootElem = doc.getDocumentElement();
     // visit(rootElem);
     visitNode(d,rootElem);
    }
    catch( ParserConfigurationException pce )
    {
      pce.printStackTrace();
    }
    catch( SAXException se )
    {
      se.printStackTrace();
    }
    catch( IOException ioe )
    {
      ioe.printStackTrace();
    }
  }

	
  public void visitNode(Drawing d, Node n)
  {  		
  	if(n.getNodeType() == Node.ELEMENT_NODE) {
  		LxComponent comp = createElement(n);
		if(comp != null) {
			d.getLxGraph().add(comp);
		}
  	}
  	
    NodeList children = n.getChildNodes();
    if( children != null ) {
    	for( int i = 0; i < children.getLength(); i++ ) {
            visitNode(d,children.item(i));
        }
    }
  }		
  	
 	   /*switch(n.getNodeType())
    	{
      	case Node.DOCUMENT_NODE:
       { NodeList children = n.getChildNodes();
        if( children != null )
        {
          for( int i = 0; i < children.getLength(); i++ )
          {
            visitNode(children.item(i));
          }
        }
       }
        break;

      case Node.ELEMENT_NODE:
      	{
      		
      		createElement(n);
      	NodeList children = n.getChildNodes();
        if( children != null )
        {
          for( int i = 0; i < children.getLength(); i++ )
          {
            visitNode(children.item(i));
          }
        }
      	}
       // getElementChildren(n) ;       
        break;

      case Node.TEXT_NODE:
        String text = getTextNode(n);
        if( text.length() > 0 )
        {
          log( indent + "  " + text );
        }
        break;

      
      
    }
  }
*/

  /*private void getElementChildren(Node e, String indent)
  {
    NodeList children = e.getChildNodes();
    if( children != null )
    {
      for( int i = 0; i < children.getLength(); i++ )
        printNode(children.item(i), indent + "  ");
    }
  }*/

private LxComponent createElement(Node e)
{  		
	NamedNodeMap attributes = e.getAttributes();
 	Node classIDNode = attributes.getNamedItem("classid");
 	if(classIDNode == null) {
 		return null; 		
 	}
 	
 	LxComponent comp = null;
 	try {
	 	Class c = Class.forName(classIDNode.getNodeValue());
	 	comp = (LxComponent) c.newInstance();
	
		Node attrib1;
		Node attrib2;
		
	 	attrib1 = attributes.getNamedItem("x");
	 	attrib2 = attributes.getNamedItem("y");
	 	
	 	if(attrib1 != null && attrib2 != null) {
		 	comp.setLocation(Double.parseDouble(attrib1.getNodeValue()),
		 						Double.parseDouble(attrib2.getNodeValue()));
	 	}
	 	
	 	attrib1 = attributes.getNamedItem("width");
	 	attrib2 = attributes.getNamedItem("height");
	 	if(attrib1 != null && attrib2 != null) {
	 		comp.setSize(Double.parseDouble(attrib1.getNodeValue()),
	 						Double.parseDouble(attrib2.getNodeValue()));
	 	}
	 	
	 	if(comp instanceof LxLine) {
	 		setLineAttributes((LxLine)comp, attributes);
	 	}
 	}
 	catch(Exception ex) {
 		ex.printStackTrace();
 	}
 	 	
 	return comp;
} 	

private void setLineAttributes(LxLine line, NamedNodeMap attribs) {
	Node attrib = attribs.getNamedItem("style");
	if(attrib != null) {
		SVGStyle style = new SVGStyle(attrib.getNodeValue());
		
		Float opacity = style.getOpacity();
		if(opacity != null)
			line.getStyle().setTransparency(opacity.floatValue());
			
		Color lineColor = style.getStrokeColor();
		if(lineColor != null) 	
			line.getStyle().setLineColor(lineColor);
			
		Float strokeWidth = style.getStrokeWidth();
		if(strokeWidth != null) 
			line.getStyle().setLineThickness(strokeWidth.floatValue());
			
		//no fill color	
	}
	
	
}	 
 		 
 /*		 
    StringBuffer buf = new StringBuffer();
    NamedNodeMap attributes = e.getAttributes();
    for( int i = 0; i < attributes.getLength(); i++ )
    {
      Node n = attributes.item(i);
      buf.append( " " );
      buf.append( n.getNodeName() );
      buf.append( "=" );
      buf.append( n.getNodeValue() );
     
      if(n.getNodeName().equals("classid")) {
      	System.out.println("classid=" + n.getNodeValue());
      	if(n.getNodeValue().equals("com.cannontech.esub.editor.element.DynamicGraphElement")) {
      	}
      	
      	}      
    }
    
    Node n = attributes.getNamedItem("classid");
    if(n != null) {
    String classID = n.getNodeValue();
    if(classID.equals("com.cannontech.esub.editor.element.DynamicGraphElement")) {
    	System.out.println("DynamicGraph");
    	
    	Node x = attributes.getNamedItem("x");
    	System.out.println("x=" + x.getNodeValue());
   	
    	Node gdef = attributes.getNamedItem("gdefid");
    	System.out.println("gdefid=" + gdef.getNodeValue());
    }
    }
    return null;
  }*/
  
 /* retElement.setAttributeNS(null, "x", Integer.toString(x));
		retElement.setAttributeNS(null, "y", Integer.toString(y));
		retElement.setAttributeNS(null, "width", Integer.toString(width));
		retElement.setAttributeNS(null, "height", Integer.toString(height));			
		retElement.setAttributeNS(null, "object", "graph");
		retElement.setAttributeNS(null, "gdefid", Integer.toString(graph.getGraphDefinitionID()));
		retElement.setAttributeNS(null, "model",  Integer.toString(graph.getTrendType()));
		retElement.setAttributeNS(null, "format", "svg");
		retElement.setAttributeNS(null, "db", CtiUtilities.getDatabaseAlias());
		retElement.setAttributeNS(null, "loadfactor", "false");
		retElement.setAttributeNS(null, "start", dateFormat.format(graph.getCurrentStartDate()));
		retElement.setAttributeNS(null, "end", dateFormat.format(graph.getCurrentEndDate()));
*/
/*  private String getTextNode(Node t)
  {
    return t.getNodeValue().trim();
  }
*/



}
