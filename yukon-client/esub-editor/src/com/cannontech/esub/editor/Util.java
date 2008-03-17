package com.cannontech.esub.editor;

import java.util.Iterator;
import java.util.List;

import com.cannontech.common.util.FileFilter;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.esub.editor.element.PointSelectionPanel;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.StateImage;
import com.cannontech.spring.YukonSpringHook;

/**
 * Util has a grab bag of convienience methods relating to the esub editor.
 * @author aaron
 */
public class Util {

	private static PointSelectionPanel pointSelectionPanel = null;
	private static javax.swing.JColorChooser colorChooser = null;
	private static javax.swing.JFileChooser drawingFileChooser = null;
	private static javax.swing.JFileChooser linkFileChooser = null;
	private static PointDao pointDao = YukonSpringHook.getBean("pointDao",PointDao.class);
	
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
     * For each element in the list, this method will change the point id's of the attributes
     * driven by points to be the point id's of the given paobject with the same point offsets.
     * @param elements List<DynamicText>
     * @param pao LiteYukonPAObject
     * @return boolean
     */
    public static void changeDeviceForDynamicTextElements(List<DynamicText> elements, LiteYukonPAObject pao) {
        Iterator<DynamicText> iter = elements.iterator();
        while(iter.hasNext()) {
            DynamicText text = iter.next();
            
            LitePoint point = text.getPoint();
            Integer pointOffset = point.getPointOffset();
            
            LitePoint controlPoint = text.getControlPoint();
            Integer controlPointOffeset = controlPoint.getPointOffset();
            
            LitePoint colorPoint= pointDao.getLitePoint(text.getColorPointID());
            Integer colorPointOffset = colorPoint.getPointOffset();
            
            LitePoint blinkPoint = pointDao.getLitePoint(text.getBlinkPointID());
            Integer blinkPointOffset = blinkPoint.getPointOffset();
            
            LitePoint currentStatePoint = pointDao.getLitePoint(text.getCurrentStateID());
            Integer currentStatePointOffset = blinkPoint.getPointOffset();
            
            Integer newPointId = pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), pointOffset, point.getPointType());
            Integer newControlPointId = pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), controlPointOffeset, controlPoint.getPointType());
            Integer newColorPointId = pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), colorPointOffset, colorPoint.getPointType());
            Integer newBlinkPointId = pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), blinkPointOffset, blinkPoint.getPointType());
            Integer newCurrentStatePointId = pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), currentStatePointOffset, currentStatePoint.getPointType());
            
            text.setPoint(pointDao.getLitePoint(newPointId));
            text.setPointId(newPointId);
            text.setControlPoint(pointDao.getLitePoint(newControlPointId));
            text.setControlPointId(newControlPointId);
            text.setColorPointID(newColorPointId);
            text.setBlinkPointID(newBlinkPointId);
            text.setCurrentStateID(newCurrentStatePointId);
        }
    }

    /**
     * For each element in the list, this method will change the point id's of the attributes
     * driven by points to be the point id's of the given paobject with the same point offsets.
     * @param elements List<StateImage>
     * @param pao LiteYukonPAObject
     * @return boolean
     */
    public static void changeDeviceForStateImageElements(List<StateImage> elements, LiteYukonPAObject pao) {
        Iterator<StateImage> iter = elements.iterator();
        while(iter.hasNext()) {
            StateImage image = iter.next();
            
            LitePoint point = image.getPoint();
            Integer pointOffset = point.getPointOffset();
            
            Integer newPointId = pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), pointOffset, point.getPointType());
            
            image.setPoint(pointDao.getLitePoint(newPointId));
        }
    }
}
