/*
 * Created on May 9, 2003
  */
package com.cannontech.esub.viewer;

import javax.swing.JFrame;

import com.cannontech.esub.editor.Drawing;

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
