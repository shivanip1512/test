package com.cannontech.esub.editor;

import com.cannontech.common.util.FileFilter;
import com.cannontech.esub.editor.element.PointSelectionPanel;

/**
 * Util has a grab bag of convienience methods relating to the esub editor.
 * @author aaron
 */
public class Util {

	private static PointSelectionPanel pointSelectionPanel = null;
	private static javax.swing.JColorChooser colorChooser = null;
	private static javax.swing.JFileChooser drawingFileChooser = null;
	private static javax.swing.JFileChooser linkFileChooser = null;
	
	public static synchronized javax.swing.JFileChooser getDrawingJFileChooser() {
		if (drawingFileChooser == null) {
			drawingFileChooser = new javax.swing.JFileChooser();
			FileFilter filter = new FileFilter(new String("jlx"), "JLX files");
			drawingFileChooser.addChoosableFileFilter(filter);
		}
	
		return drawingFileChooser;
	}
		public static synchronized javax.swing.JFileChooser getLinkJFileChooser() {
			if (linkFileChooser == null) {
				linkFileChooser = new javax.swing.JFileChooser();
	//			FileFilter filter = new FileFilter(new String("jlx"), "JLX files");
	//			drawingFileChooser.addChoosableFileFilter(filter);
			}
	
			return linkFileChooser;
		}
	public static synchronized javax.swing.JColorChooser getJColorChooser() {
		if (colorChooser == null) {
			colorChooser = new javax.swing.JColorChooser();
		}
	
		return colorChooser;
	}
	public static synchronized PointSelectionPanel getPointSelectionPanel() {
		if(pointSelectionPanel == null) {
			pointSelectionPanel = new PointSelectionPanel();
		}
		
		return pointSelectionPanel;
	}

}
