/*
 * Created on Dec 2, 2003
 */
package com.cannontech.esub.tool;

import java.io.File;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.Drawing;

/**
 * Recursively load and save drawings.
 * @author aaron
 */
public class UpdateDrawings implements Runnable {
	
	private File _drawingDir;
	
	public static void main(String[] args) {
		if(args.length != 1){
			CTILogger.error("Recursively loads and saves drawings.  Make a backup first!!");
			CTILogger.error("Usage:\nUpdateDrawings <dir>");
			System.exit(-1);
		}
		UpdateDrawings ud = new UpdateDrawings(args[0]);
		ud.run();
		CTILogger.info("Done!");
		System.exit(0);
	}
	
	public void run() {
		CTILogger.info("Updating drawings located at " + _drawingDir.getAbsolutePath());
		processFile(getDrawingDir());
	}
	
	private void processFile(File f) {
		if(f.isDirectory()) {
			File files[] = f.listFiles();			
			for(int i = 0; i < files.length; i++) {
				if(files[i].isDirectory() ||
					files[i].getName().toLowerCase().endsWith(".jlx")) {
					processFile(files[i]);
				}
			}
		}
		else {
			Drawing d = new Drawing();
			CTILogger.info(f.getAbsolutePath());
			try {			
				d.load(f.getAbsolutePath());
				d.save();
			} catch(Exception e) {
				CTILogger.error("An error occured processing: " + f.getAbsolutePath(), e);
			}
		}
	}
	public UpdateDrawings(String drawingDir) {
		setDrawingDir(drawingDir);
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
