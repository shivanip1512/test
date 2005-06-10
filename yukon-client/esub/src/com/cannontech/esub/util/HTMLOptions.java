/*
 * Created on Jul 15, 2003
  */
package com.cannontech.esub.util;

/**
 * Encapsulates the HTML generation options used by SVGGenerator
 * @author aaron
  */
public class HTMLOptions {
	
	/* If true no scripts will be included that use the network */
	private boolean staticHTML = true;

	public boolean isStaticHTML() {
		return staticHTML;
	}

	public void setStaticHTML(boolean staticHTML) {
		this.staticHTML = staticHTML;
	}	
}
