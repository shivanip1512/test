/*
 * Created on Jul 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.esub.util;

import java.io.File;
import java.io.IOException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.Drawing;

/**
 * @author aaron 
 */
public class DrawingExporter {
	public void doExport(File inDir, File outDir) throws IOException {
	}
	
	private void doExport(Drawing drawing, File inDir, File outDir) throws IOException {
	File[] files = inDir.listFiles();
	for(int i = 0; i < files.length; i++) {
		File f = files[i];
		if(f.isDirectory()) {				
			File d = new File(outDir.getCanonicalPath() + File.separatorChar + f.getName());
			d.mkdir();
			doExport(f, d);
		}
		else {
			if(f.getName().endsWith(".jlx")) {
				long start = System.currentTimeMillis();
				CTILogger.info(f.getCanonicalPath() + " .loading.");
				drawing.load(f.getCanonicalPath());
				CTILogger.info(".updating.");
				DrawingUpdater updater = new DrawingUpdater(drawing);
				updater.setUpdateGraphs(true);
				updater.updateDrawing();
				CTILogger.info(".writing.");
				drawing.exportAs(outDir.getCanonicalPath() + File.separatorChar + f.getName());
				CTILogger.info(".done (" + (System.currentTimeMillis() - start) + "ms)");
			}
		}
		}
	}	
}
