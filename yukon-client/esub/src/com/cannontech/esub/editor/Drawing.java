package com.cannontech.esub.editor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.util.HTMLGenerator;
import com.cannontech.esub.util.SVGGenerator;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxSVGGenerator;
import com.loox.jloox.LxView;

/**
 * @author alauinger
 *
 * Wraps a LxGraph and LxView and provides a little behavior
 * A LxView isn't always necessary so it could be null
 * in some contexts.  (you can load a drawing and run it
 * through an svggenerator without creating an LxView for example)
 * 
 */
public class Drawing implements Serializable {

	private String fileName;

	// The jloox graph and view to use
	private LxGraph lxGraph;
	private LxView lxView;

	public synchronized void clear() {
		fileName = null;
		getLxGraph().removeAll();
		setModified(false);
	}
	public synchronized void load(String file) {
		clear();
		getLxGraph().read(file);
		fileName = file;

		// Fix up each element so they know who their drawing is
		LxComponent[] comps = lxGraph.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof DrawingElement) {
				((DrawingElement) comps[i]).setDrawing(this);
			}							
		}
	}

	public synchronized void save(String fileName) {
		String jlxFileName = fileName;

		if (!jlxFileName.endsWith(".jlx")) {
			jlxFileName = jlxFileName.concat(".jlx");
		}

		setFileName(jlxFileName);
		getLxGraph().save(jlxFileName);

		String svgFileName = fileName;
		if (svgFileName.endsWith(".jlx")) {
			svgFileName = svgFileName.substring(0, svgFileName.length() - 4);
		}

		svgFileName = svgFileName.concat(".svg");

		try {
			SVGGenerator gen2 = new SVGGenerator();
			FileWriter fw = new FileWriter(svgFileName);

			gen2.generate(fw, lxGraph);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String htmlFileName = fileName;
		
		if (htmlFileName.endsWith(".jlx")) {
			htmlFileName = htmlFileName.substring(0, htmlFileName.length() - 4);
		}

		htmlFileName = htmlFileName.concat(".html");

		try { 
			HTMLGenerator gen3 = new HTMLGenerator();
			FileWriter fw = new FileWriter(htmlFileName);
			
			gen3.generate(fw, this);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

		
		LxSVGGenerator lxGen = new LxSVGGenerator();
		lxGen.saveAsSVG(lxGraph, svgFileName + ".svg");		
	}

	public synchronized void save() {
		save(getFileName());
	}

	/**
	 * Returns the lxGraph.
	 * @return LxGraph
	 */
	public LxGraph getLxGraph() {
		if (lxGraph == null) {
			lxGraph = new LxGraph();
			lxGraph.setDefaultLineColor(java.awt.Color.white);
		}

		return lxGraph;
	}

	/**
	 * Returns the lxView.
	 * @return LxView
	 */
	public LxView getLxView() {
		if (lxView == null) {
			lxView = new LxView();
			lxView.setGraph(getLxGraph());
			lxView.setEditMode(LxView.EDITOR_MODE);
			lxView.setAntialiasingActivated(false);
			lxView.lassoSetDisplayed(false);
			lxView.setBackground(java.awt.Color.black);
			lxView.setMagneticGridColor(java.awt.Color.gray);
			lxView.setMagneticGridEnabled(true);
			lxView.setMagneticGridVisible(true);
			lxView.setMagneticGridSize(10);
		}

		return lxView;
	}

	/**
	 * Sets the lxGraph.
	 * @param lxGraph The lxGraph to set
	 */
	public void setLxGraph(LxGraph lxGraph) {
		this.lxGraph = lxGraph;
	}

	/**
	 * Sets the lxView.
	 * @param lxView The lxView to set
	 */
	public void setLxView(LxView lxView) {
		this.lxView = lxView;
	}

	/**
	 * Returns the fileName.
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName.
	 * @param fileName The fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the modified.
	 * @return boolean
	 */
	public boolean isModified() {
		return getLxGraph().isModified();
	}

	/**
	 * Sets the modified.
	 * @param modified The modified to set
	 */
	public void setModified(boolean modified) {
		getLxGraph().setModified(modified);
	}

}
