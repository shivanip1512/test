/*
 * Created on Jul 15, 2003
  */
package com.cannontech.esub.util;

/**
 * Encapsulates the SVG generation options used by SVGGenerator
 * @author aaron
  */
public class SVGOptions {
	
	/* If true no scripts will be included that use the network */
	private boolean staticSVG = true;
	
	private boolean scriptingEnabled = false;
	private boolean editEnabled = false;
	private boolean controlEnabled = false;
		
	/**
	 * @return
	 */
	public boolean isControlEnabled() {
		return controlEnabled;
	}

	/**
	 * @return
	 */
	public boolean isEditEnabled() {
		return editEnabled;
	}

	/**
	 * @return
	 */
	public boolean isStaticSVG() {
		return staticSVG;
	}

	/**
	 * @param b
	 */
	public void setControlEnabled(boolean b) {
		controlEnabled = b;
	}

	/**
	 * @param b
	 */
	public void setEditEnabled(boolean b) {
		editEnabled = b;
	}

	/**
	 * @param b
	 */
	public void setStaticSVG(boolean b) {
		staticSVG = b;
	}

	/**
	 * @return
	 */
	public boolean isScriptingEnabled() {
		return scriptingEnabled;
	}

	/**
	 * @param b
	 */
	public void setScriptingEnabled(boolean b) {
		scriptingEnabled = b;
	}

}
