package com.cannontech.esub;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.RectangleElement;
import com.cannontech.esub.svg.ESubSVGGenerator;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.esub.util.HTMLGenerator;
import com.cannontech.esub.util.ImageExporter;
import com.cannontech.esub.util.Util;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;
import com.loox.jloox.LxRectangle;
import com.loox.jloox.LxView;

/**
 * @author alauinger
 *
 * Wraps a LxGraph and LxView and provides a little behavior
 * A LxView isn't always necessary so it could be null
 * in some contexts.  (you can load a drawing and run it
 * through an svggenerator without creating an LxView for example)
 */
public class Drawing implements Serializable {

	private String fileName;

	// The jloox graph and view to use
	private LxGraph lxGraph;
	private LxView lxView;
	private YukonUserContext userContext = null;
	
	public Drawing() {
		//init the view
	    userContext = new SystemUserContext();
		getLxView();
	}
	
	public synchronized void clear() {
		fileName = null;
		getLxGraph().removeAll();
		setModified(false);
	}
	
	public synchronized void load(String file) {
		clear();
		
		//JLoox isn't thread safe on loads
		synchronized(getLxGraph().getClass()) {
			getLxGraph().read(file);
		}
		fileName = file;

		/*
		 *  Fix up each element so they know who their drawing is.
		 *  Create new versions of lines and rectangles and replace
		 *  the old ones with the new ones in the graph component list.
		 */
		List<LxComponent> componentsToAdd = new ArrayList<LxComponent>();
		List<LxComponent> componentsToRemove = new ArrayList<LxComponent>();
		LxComponent[] comps = lxGraph.getComponents();
		for (LxComponent component : comps) {
		    if(component instanceof LxLine) {
                LineElement line = Util.convertToLineElement((LxLine)component);
                ((DrawingElement) line).setDrawing(this);
                componentsToAdd.add(line);
                componentsToRemove.add(component);
            } else 
                if (component instanceof LxRectangle) {
                RectangleElement rectangle = Util.convertToRectangleElement((LxRectangle)component);
                ((DrawingElement) rectangle).setDrawing(this);
                componentsToAdd.add(rectangle);
                componentsToRemove.add(component);
            }
			if (component instanceof DrawingElement) {
			    ((DrawingElement) component).setDrawing(this);
			}							
		}
		// Remove the old versions of these elements
		for(LxComponent oldComp : componentsToRemove) {
		    lxGraph.remove(oldComp);
		}
		// Add the old versions of these elements
		for(LxComponent newComp : componentsToAdd) {
            lxGraph.add(newComp);
        }
		
		getLxView().setSize( getMetaElement().getDrawingWidth(), getMetaElement().getDrawingHeight());
	}

	public synchronized void save(String fileName) {						
        exportAs(fileName);
	}

	
	public synchronized void exportAs(String fileName) {
		// Workaround for a bug that doesn't appear often
		// Outof bounds excpetion is popped from an internal sun font cache occaisionally
		// a retry seems to alleviate things
		int retries = 3;
		
		setFileName(fileName);
		do {		
			try {
				writeJLX(fileName);
				writeSVG(fileName);
				writeHTML(fileName);
				String parent = new File(fileName).getParent();
                writeImages(parent);
				break;
			}
			catch(Exception e) {
				CTILogger.debug(e.getMessage());
			}
		} while(retries-- > 0);
	}
	
	private String writeHTML(String fileName) {
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
		
		return htmlFileName;    
	}

	private String writeSVG(String fileName) {
		String svgFileName = fileName;
		if (svgFileName.endsWith(".jlx")) {
			svgFileName = svgFileName.substring(0, svgFileName.length() - 4);
		}
		
		svgFileName = svgFileName.concat(".svg");
		
		try {
				SVGOptions svgOptions = new SVGOptions();
				svgOptions.setControlEnabled(false);
				svgOptions.setEditEnabled(false);
				svgOptions.setScriptingEnabled(false);
				svgOptions.setStaticSVG(true);
                ESubSVGGenerator gen2 = new ESubSVGGenerator();
		        FileWriter fw = new FileWriter(svgFileName);
		
		        gen2.generate(fw, this);
		        fw.close();

		} catch (IOException e) {
		        e.printStackTrace();
		}
		return svgFileName;
	}

	private String writeJLX(String fileName) {
		String jlxFileName = fileName;
		
		if (!jlxFileName.endsWith(".jlx")) {
			jlxFileName = jlxFileName.concat(".jlx");
		}
		

		        
		getLxGraph().save(jlxFileName);
		return jlxFileName;
	}

	private void writeImages(String dir) {
		ImageExporter ie = new ImageExporter(this);
		ie.exportImages(dir);
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
			lxView = (LxView) getLxGraph().addView();
			lxView.setEditMode(LxView.EDITOR_MODE);
			lxView.setAntialiasingActivated(false);
			lxView.lassoSetDisplayed(true);
			lxView.setBackground(new Color(getMetaElement().getDrawingRGBColor()));
			lxView.setMagneticGridColor(java.awt.Color.gray);
			lxView.setMagneticGridEnabled(true);
			lxView.setMagneticGridVisible(true);
			lxView.setMagneticGridSize(10);
			lxView.setLocation(0,0); 
			lxView.setPreferredSize(new Dimension(getMetaElement().getDrawingWidth(),
									getMetaElement().getDrawingHeight()));
														
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
		if(!fileName.endsWith(".jlx")) {
			fileName += ".jlx";
		}
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
	
	public DrawingMetaElement getMetaElement() {	
		// Fix up each element so they know who their drawing is
		LxComponent[] comps = getLxGraph().getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof DrawingMetaElement)
				return (DrawingMetaElement) comps[i];
		}
		
		DrawingMetaElement metaInfo = new DrawingMetaElement();
		metaInfo.setDrawing(this);			
		getLxGraph().add(metaInfo);
		return metaInfo;							
	}

    public YukonUserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
	
	
}
