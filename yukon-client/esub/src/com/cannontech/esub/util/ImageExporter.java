/*
 * Created on Jul 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.esub.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.YukonImageElement;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

/**
 * @author aaron
 
 */
public class ImageExporter {
	private Drawing drawing;
	
	public ImageExporter(Drawing d) {
		drawing = d;
	}
	
	public void exportImages(String dir) {
		exportImages(dir, drawing);
	}
	
 	public void exportImages(String dir, Drawing d) {
		LxGraph graph = d.getLxGraph();
		LxComponent[] c	= graph.getComponents();
		for( int i = 0; i < c.length; i++ ) {
			
			if(c[i] instanceof YukonImageElement) {
				YukonImageElement yie = (YukonImageElement) c[i];
				LiteYukonImage img = yie.getYukonImage();
				if(img != null) {
					writeImage(dir + File.separatorChar + img.getImageName(), img.getImageValue());																	
				}
				else {
					writeImage(dir + File.separatorChar + Util.DEFAULT_IMAGE_NAME, Util.DEFAULT_IMAGE_BYTES);
				}
			}
			else 
			if(c[i] instanceof DynamicGraphElement) {
				DynamicGraphElement dge = (DynamicGraphElement) c[i];
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				try {				
					dge.getCTIGraph().encodePng(bout);
					writeImage(dir + File.separatorChar + Util.genExportedGraphName(dge), bout.toByteArray());
				}catch(IOException ioe) {
					CTILogger.error(ioe.getMessage(), ioe);
				}
			}
 		
 		}	
	}
	
	private void writeImage(String fileName, byte[] buf) {FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(fileName);
			fout.write(buf);
			fout.close();
		}
		catch(FileNotFoundException fne) {
														
		}
		catch(IOException ioe) {
						
		}
		finally {
			try { if(fout != null) fout.close(); } catch(IOException ioe2) { }												
		}
		
	}
}
