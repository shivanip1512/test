package com.cannontech.esub.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.util.SVGGenerator;

import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxView;

/**
 * @author alauinger
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Drawing {

	private String fileName;

	// The jloox graph and view to use
	private LxGraph lxGraph;
	private LxView lxView;

	public void clear() {
		fileName = null;
		lxGraph.removeAll();
		setModified(false);
	}
	public void load(String file) {
		clear();
		lxGraph.read(file);
		fileName = file;

		// Fix up each element so they know who their drawing is
		LxComponent[] comps = lxGraph.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof DrawingElement) {
				((DrawingElement) comps[i]).setDrawing(this);
			}
		}
	}

	public void save(String fileName) {
		String jlxFileName = fileName;

		if (!jlxFileName.endsWith(".jlx")) {
			jlxFileName = jlxFileName.concat(".jlx");
		}

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

		setFileName(jlxFileName);
	}

	public void save() {
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
			lxView.setAntialiasingActivated(true);
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
