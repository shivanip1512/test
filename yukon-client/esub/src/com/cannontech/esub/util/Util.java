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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.activity.ActivityLog;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.RectangleElement;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.SystemUserContext;
import com.google.common.collect.Lists;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;
import com.loox.jloox.LxRectangle;
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
		{ 71, 73, 70, 56, 57, 97, 20, 0, 20, 0, -62, 0, 0, 0, 0, 0, -52, -52, -52, 51, 51, 51, -46, -46, -46, -1,
	       -1, -1, 66, 66, 66, -38, -38, -38, -1, -1, -1, 44, 0, 0, 0, 0, 20, 0, 20, 0, 0, 3, 83, 8, -70, -36, 78,
           38, -56, 73, -87, 33, -22, -42, 61, -95, -110, 16, 55, 105, -63, 23, -112, 98, 88, 2, -108, 106, 17, 
           -108, 41, -95, 93, 52, -55, -11, 12, 87, -72, 14, -38, 49, 22, -121, -80, -37, -12, 58, 69, -98, -16,
           -11, 51, 46, 115, -89, -92, 4, 71, 107, -34, -98, -82, 81, -46, 84, -83, -88, 76, -39, 13, 41, 35, 21,
           5, 60, -128, -80, -7, 2, 16, 56, -34, 13, 65, 2, 0, 59
        };

	/**
	 * Util constructor comment.
	 */
	public Util() {
		super();
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
	    Drawing d = new Drawing();
	    d.setUserContext(new SystemUserContext());
	    doExport(d, inDir, outDir);
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
					//Drawing drawing = new Drawing();
					CTILogger.info(f.getCanonicalPath() + " -> " + outDir.getCanonicalPath());
					drawing.load(f.getCanonicalPath());
					ESubDrawingUpdater updater = new ESubDrawingUpdater(drawing);
					updater.setUpdateGraphs(true);
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
		LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(pointID);		
		return (lp != null && lp.getPointType() == PointTypes.STATUS_POINT);	
	}
	
	public static void logActivity(int userID, String action, String description) {
		try {		
			ActivityLog al = new ActivityLog();
			al.setUserID(userID);
			al.setAction(action);
			al.setDescription(description);
			Transaction<ActivityLog> t = Transaction.createTransaction(Transaction.INSERT, al);
			t.execute(); 
		} catch(TransactionException te) {
			te.printStackTrace();
		}		
	}
	
	/**
	 * Creates a new LineElement Object and sets it's attributes with
	 * those of the LxLine passed in. Used for loading drawings with 
     * old versions of lines and saving them as the new version.
	 * @param lxLine
	 * @return line
	 */
	public static LineElement convertToLineElement(LxLine lxLine) {
	    LineElement line = new LineElement();
	    line.setX(lxLine.getX());
	    line.setY(lxLine.getY());
	    line.setPoint1(lxLine.getPoint1());
	    line.setPoint2(lxLine.getPoint2());
	    line.setLineColor(lxLine.getLineColor());
	    line.setTransparency(lxLine.getTransparency());
	    line.setLineThickness(lxLine.getLineThickness());
	    line.setLineArrow(lxLine.getLineArrow());
	    return line;
	}
	
	/**
	 * Creates a new RectangleElement Object and sets it's attributes with
     * those of the LxRectangle passed in. Used for loading drawings with 
     * old versions of recangles and saving them as the new version.
	 * @param lxRectangle
	 * @return rectangle
	 */
	public static RectangleElement convertToRectangleElement(LxRectangle lxRectangle) {
	    RectangleElement rectangle = new RectangleElement();
	    rectangle.setX(lxRectangle.getX());
	    rectangle.setY(lxRectangle.getY());
	    rectangle.setSize(lxRectangle.getSize());
	    rectangle.setPaint(lxRectangle.getPaint());
        rectangle.setLineColor(lxRectangle.getLineColor());
        rectangle.setTransparency(lxRectangle.getTransparency());
        rectangle.setLineThickness(lxRectangle.getLineThickness());
        return rectangle;
    }
	
	public static int[] fixDeviceIds(int[] oldArray){
        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
        List<Integer> newDeviceIds = Lists.newArrayList();
        for(int deviceId : oldArray){
            try {
                paoDao.getYukonPao(deviceId);
                newDeviceIds.add(deviceId);
            } catch (NotFoundException e){
                CTILogger.error("DeviceId: " + deviceId + " not found.");
            }
        }
        int newDeviceIdArray[] = new int[newDeviceIds.size()];
        int i=0;
        for(Integer deviceId : newDeviceIds){
            newDeviceIdArray[i] = deviceId;
            i++;
        }
        return newDeviceIdArray;
    }
    
    public static int[] fixPointIds(int[] oldArray){
        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
        List<Integer> newPointIds = Lists.newArrayList();
        for(int pointId : oldArray){
            try {
                pointDao.getLitePoint(pointId);
                newPointIds.add(pointId);
            } catch (NotFoundException e){
                CTILogger.error("PointId: " + pointId + " not found.");
            }
        }
        int newPointIdArray[] = new int[newPointIds.size()];
        int i=0;
        for(Integer pointId : newPointIds){
            newPointIdArray[i] = pointId;
            i++;
        }
        return newPointIdArray;
    }
}
