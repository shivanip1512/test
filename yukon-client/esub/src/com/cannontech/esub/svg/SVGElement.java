package com.cannontech.esub.svg;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import com.loox.jloox.LxElement;

/**
 * @author alauinger
 */
public interface SVGElement {
	public Element createSVGElement(SVGDocument doc);
	public LxElement createLxElement(Node n);
}
