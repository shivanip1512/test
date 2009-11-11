/*
 * Created on Dec 2, 2003
 */
package com.cannontech.esub.tool;

import java.io.File;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.Drawing;
import com.cannontech.user.SystemUserContext;

/**
 * Recursively load and save drawings.
 * Can be used to update an entire set of drawings.
 * @author aaron
 */
public class UpdateDrawings implements Runnable {
	
	private File _drawingDir;
	private boolean fixDrawings = false;
	
	public static void main(String[] args) {
		if(args.length < 1){
			CTILogger.error("Recursively loads and saves drawings.  Make a backup first!!");
			CTILogger.error("Usage:\nUpdateDrawings <dir> \n...Loads and Saves Drawings");
			CTILogger.error("\nUsage:\nUpdateDrawings <dir> fix\n...Loads, fixes bad point/device references, Saves Drawings");
			System.exit(-1);
		}
		UpdateDrawings ud = new UpdateDrawings(args);
		ud.run();
		CTILogger.info("Done!");
		System.exit(0);
	}
	
	public void run() {
		CTILogger.info("Updating drawings located at " + _drawingDir.getAbsolutePath());
		CTILogger.info("Fix drawing = " + fixDrawings);
		processFile(getDrawingDir());
	}
	
	private void processFile(File f) {
		if(f.isDirectory()) {
			File files[] = f.listFiles();			
			for(int i = 0; i < files.length; i++) {
				if(files[i].isDirectory() || files[i].getName().toLowerCase().endsWith(".jlx")) {
					processFile(files[i]);
				}
			}
		} else {
			Drawing d = new Drawing();
			d.setUserContext(new SystemUserContext());
			
			CTILogger.info(f.getAbsolutePath());
			try {			
				d.load(f.getAbsolutePath());
				if(fixDrawings){
				    if(d.fixDrawing()){
				        CTILogger.info("File " + f.getName() + ": " + f.getAbsolutePath() + " has broken elements and needs attention.");
				    }
				}
				d.save();
			} catch(Exception e) {
				CTILogger.error("An error occurred processing: " + f.getAbsolutePath(), e);
			}
		}
	}
	
	public UpdateDrawings(String[] args) {
		setDrawingDir(args[0]);
		if(args.length > 1){
		    fixDrawings = args[1].equalsIgnoreCase("fix");
		}
	}
	
	/**
	 * @return
	 */
	public File getDrawingDir() {
		return _drawingDir;
	}

	/**
	 * @param string
	 */
	public void setDrawingDir(String string) {
		_drawingDir = new File(string);
		if(!_drawingDir.exists()) {
			throw new IllegalArgumentException("Directory doesn't exist");
		}
	}

}
