package com.cannontech.esub.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.common.util.FileFilter;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.PointSelectionPanel;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.message.dispatch.ClientConnection;

import com.loox.jloox.LxGraph;
import com.loox.jloox.LxRotatable;

/**
 * Creation date: (1/14/2002 4:28:01 PM)
 * @author: alauinger 
 */
public class Util {

	// The external name of the default image
	public static String DEFAULT_IMAGE_NAME = "X.gif";
	
	// The grey X that is displayed in place of an image that couldn't be found
	public static byte[] DEFAULT_IMAGE_BYTES =
		{
				71,
				73,
				70,
				56,
				57,
				97,
				20,
				0,
				20,
				0,
				-62,
				0,
				0,
				0,
				0,
				0,
				-52,
				-52,
				-52,
				51,
				51,
				51,
				-46,
				-46,
				-46,
				-1,
				-1,
				-1,
				66,
				66,
				66,
				-38,
				-38,
				-38,
				-1,
				-1,
				-1,
				44,
				0,
				0,
				0,
				0,
				20,
				0,
				20,
				0,
				0,
				3,
				83,
				8,
				-70,
				-36,
				78,
				38,
				-56,
				73,
				-87,
				33,
				-22,
				-42,
				61,
				-95,
				-110,
				16,
				55,
				105,
				-63,
				23,
				-112,
				98,
				88,
				2,
				-108,
				106,
				17,
				-108,
				41,
				-95,
				93,
				52,
				-55,
				-11,
				12,
				87,
				-72,
				14,
				-38,
				49,
				22,
				-121,
				-80,
				-37,
				-12,
				58,
				69,
				-98,
				-16,
				-11,
				51,
				46,
				115,
				-89,
				-92,
				4,
				71,
				107,
				-34,
				-98,
				-82,
				81,
				-46,
				84,
				-83,
				-88,
				76,
				-39,
				13,
				41,
				35,
				21,
				5,
				60,
				-128,
				-80,
				-7,
				2,
				16,
				56,
				-34,
				13,
				65,
				2,
				0,
				59
				 	};

	private static PointSelectionPanel pointSelectionPanel = null;
	private static javax.swing.JColorChooser colorChooser = null;
	private static javax.swing.JFileChooser drawingFileChooser = null;
	private static javax.swing.JFileChooser linkFileChooser = null;
	private static ClientConnection dispatchConnection = null;

	/**
	 * Util constructor comment.
	 */
	public Util() {
		super();
	}

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
	
	/**
	 * Generates an absolute path from the drawings location
	 * and the relative path given.
	 * @param d
	 * @param relPath
	 * @return String
	 */
	public static String getAbsolutePath(Drawing d, String relPath) {
		return new File(d.getFileName()).getParent() + "/" + relPath;
	}
	/**
	 * Determines the path to file f from directory d
	 */
	public static String getRelativePathD(File d, File f) throws IOException {
		File dummy = new File(d.getCanonicalPath() + File.separatorChar + "dummy.txt");
		return getRelativePath(dummy, f);		
	}
	
	/**
	 * Determines the path to f2 from f1.
	 * f1 = c:\temp\joe.txt
	 * f2 = c:\temp\images\joe.gif
	 * getRelativePath will return
	 * note this only works if f2 is below f1
	 * images\joe.gif
	*/
	public static String getRelativePath(File f1, File f2) throws IOException {
		String p1 = f1.getCanonicalPath();
		String p2 = f2.getCanonicalPath();
		
		int p1Len = p1.length();
		int p2Len = p2.length();
		
		int i = 0; 
		while(i < p1Len && i < p2Len && p1.charAt(i) == p2.charAt(i)) 
			i++;
		
		if(i == 0) //nothing matched (different drives?)
			return null;
				
		if(i == p1Len && i == p2Len) //they are the same file
			return p2.substring(p1.lastIndexOf(File.separatorChar) + 1);
						
		p1 = p1.substring(i);
		p2 = p2.substring(i);
		
		if(p1.length() < p2.length()) {
			return p2;
		}
		
		StringBuffer p = new StringBuffer();
		for(i = 0; i < p1.length(); i++) {
			if(p1.charAt(i) == File.separatorChar) {
				p.append("../");
			}
		}
		p.append(p2);
		return p.toString();
	}
	
	
	public static int compare(String s1, String s2) {
		int i = 0;
		while(s1.charAt(i) == s2.charAt(i)) i++;
		return i;
	}
	
	public static Image findImage(String imageName) {
		if (!imageName.startsWith("/")) {
			imageName = "/" + imageName;
		}

		URL imageURL = Util.class.getResource(imageName);

		if (imageURL == null) {
			Logger.global.warning("Could not locate: " + imageName);
			return null;
		}

		return (Toolkit.getDefaultToolkit().getImage(imageURL));
	}

	public static Image prepareImage(byte[] imageBuf) {
		Image img = Toolkit.getDefaultToolkit().createImage(imageBuf);
		Component comp = new JComponent() {
		};

		// Wait for the image to be prepared.
		MediaTracker mt = new MediaTracker(comp);
		mt.addImage(img, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return img;
	}

	/**
	 * Rotate all the selected elements in g by the number
	 * of radians specified by r
	 * @param g
	 * @param r
	 */
	public static void rotateSelected(LxGraph g, double r) {
		Object[] sel = g.getSelectedObjects();
		for (int i = 0; i < sel.length; i++) {
			if (sel[i] instanceof LxRotatable) {
				LxRotatable rot = (LxRotatable) sel[i];
				rot.rotate(r);				
			}
		}
	}
	
	public static synchronized com
		.cannontech
		.message
		.dispatch
		.ClientConnection getConnToDispatch() {
		if (dispatchConnection == null) {

			String host;
			int port;

			try {
				host =
					com.cannontech.common.util.CtiProperties.getInstance().getProperty(
						com.cannontech.common.util.CtiProperties.KEY_DISPATCH_MACHINE,
						"127.0.0.1");
				port =
					Integer.parseInt(
						CtiProperties.getInstance().getProperty(
							CtiProperties.KEY_DISPATCH_PORT,
							"1510"));

			} catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
				return null;
			}

			dispatchConnection = new com.cannontech.message.dispatch.ClientConnection();
			com.cannontech.message.dispatch.message.Registration reg =
				new com.cannontech.message.dispatch.message.Registration();
			reg.setAppName(
				"Esubstation Editor @" + com.cannontech.common.util.CtiUtilities.getUserName());
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay(300); // 5 minutes should be OK

			//conn.addObserver(this);
			dispatchConnection.setHost(host);
			dispatchConnection.setPort(port);
			dispatchConnection.setAutoReconnect(true);
			dispatchConnection.setRegistrationMsg(reg);
			
			try {
				dispatchConnection.connectWithoutWait();
			} catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
			}
		}
		
		return dispatchConnection;
	}

	public static String stripArgument(String fn) {
		return ( fn == null || fn.length() < 3 ? null :
					fn.substring( fn.indexOf('(')+1, fn.indexOf(')')));
	}
	
		/**
	 * Builds up a svg path string given a shape and the center of the element.
	 * @param s
	 * @param cx
	 * @param cy
	 * @return String
	 */
	public static String getPathString(Shape[] s, double cx, double cy) {
		String pathStr = "";
				
		//array to store segment info
		double[] seg = new double[6];
		for( int i = 0; i < s.length; i++ ) {
	 		PathIterator pi = s[i].getPathIterator(AffineTransform.getTranslateInstance(cx, cy));
			while( !pi.isDone() ) {
				int type = pi.currentSegment(seg);
				switch(type) {
					case PathIterator.SEG_MOVETO:
						pathStr += "M " + seg[0] + " " + seg[1] + " ";
						break;						
					case PathIterator.SEG_LINETO:
						pathStr += "L " + seg[0] + " " + seg[1] + " ";
						break;
					case PathIterator.SEG_CLOSE:
						pathStr += "Z ";
						break;
					default: 
						CTILogger.info("unknown path type");
				}	
			
				pi.next();
			}
		}
		return pathStr;		
	}
	
	/**
	 * Utility program to export jlx files to their html, svg, and image components for static viewing.
	 * @param inDir
	 * @param outDir
	 * @throws IOException
	 */
	public static void doExport(File inDir, File outDir) throws IOException {
		doExport(new Drawing(), inDir, outDir);
	}	
		
	private static void doExport(Drawing drawing, File inDir, File outDir) throws IOException {
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
					//Drawing drawing = new Drawing();
					CTILogger.info(f.getCanonicalPath() + " -> " + outDir.getCanonicalPath());
					drawing.load(f.getCanonicalPath());
					DrawingUpdater updater = new DrawingUpdater(drawing);
					updater.updateDrawing();
					drawing.exportAs(outDir.getCanonicalPath() + File.separatorChar + f.getName());
				}
			}
	}
		
	}
	
	/**
	 * Generates the name of a yukon graph so that it can be exported to the filesystem as well as referred to by an svg document
	 * The element MUST be part of a drawing.
	 * @param dge
	 * @return
	 */
	public static String genExportedGraphName(DynamicGraphElement dge) {
		Drawing d = dge.getDrawing();			
		String name = new File(d.getFileName()).getName();
		name = name.substring(0, name.lastIndexOf('.')) + "-" + dge.getGraphDefinitionID() + ".png";
		return name;
	}
	
	public static boolean isStatusPoint(int pointID) {
		LitePoint lp = PointFuncs.getLitePoint(pointID);		
		return (lp != null && lp.getPointType() == PointTypes.STATUS_POINT);	
	}
}
