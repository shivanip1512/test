/*
 * Created on May 9, 2003
  */
package com.cannontech.esub.viewer;

import com.cannontech.esub.Drawing;

/**
 * @author alauinger
 */
public class Viewer {
	
	
	private Drawing drawing;
	
	public Viewer() {
		initialize();
	}
	
	public Viewer(String drawingFile) {
		initialize();
		load(drawingFile);
	}
	
	private void initialize() {
		
	}
	
	public void load(String drawingFile) {
		
	}
	
	public static void main(String[] args) {
	}
}
