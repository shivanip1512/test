package com.cannontech.web.editor.point;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author ryan
 *
 */
public class PointLists {

    /**
     * 
     */
    public PointLists() {
        super();
    }

    /**
     * Determines if the given point is in our set of valid UofM
     *
     */
    private boolean isPointUofM(LitePoint lPoint, Integer[] uofmIDs) {

        if (lPoint == null)
            return false;
        else
            return CtiUtilities.isInSet(uofmIDs, lPoint.getUofmID());
    }

    /**
     * Returns all the PAOs that have a point within the given
     * UofM id set
     *
     */
    public LiteYukonPAObject[] getPAOsByUofMPoints(Integer[] uofmIDs) {
        Integer[] pointTypes = new Integer[] { PointTypes.ANALOG_POINT, PointTypes.CALCULATED_POINT };
        List<LiteYukonPAObject> paos = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAObjectBy(null, null,DeviceClasses.CORE_DEVICE_CLASSES,pointTypes,uofmIDs);
        
        Collections.sort(paos, LiteComparators.liteStringComparator);
        return paos.toArray(new LiteYukonPAObject[paos.size()]);
    }

    /**
     * Comment
     */
    public LitePoint[] getPointsByUofMPAOs(int paoID, Integer[] uofmIDs) {

        //if the (none) object is selected, just return
        //     getJComboBoxVarPoint().setEnabled(
        //           getJComboBoxVarDevice().getSelectedItem() != LiteYukonPAObject.LITEPAOBJECT_NONE );
        //      if( getJComboBoxVarDevice().getSelectedItem() == LiteYukonPAObject.LITEPAOBJECT_NONE )
        //         return new LitePoint[0];

        //ensures uniqueness and ordering by name
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(paoID);
        ListIterator<LitePoint> iter = points.listIterator();
        while(iter.hasPrevious()) {
            LitePoint p = iter.previous();
            if((p.getPointType() != PointTypes.ANALOG_POINT && 
                p.getPointType() != PointTypes.CALCULATED_POINT) ||
                !isPointUofM(p, uofmIDs)) {
                iter.remove();
            }
        }
        Collections.sort(points, LiteComparators.liteStringComparator);
        return points.toArray(new LitePoint[points.size()]);
    }


    public static TreeSet<LitePoint> getAllTwoStateStatusPoints() {
        List<LitePoint> twoStatePoints = YukonSpringHook.getBean(PointDao.class).getLitePointsByNumStates(2);
        ListIterator<LitePoint> iter = twoStatePoints.listIterator();
        while(iter.hasPrevious()) {
            LitePoint p = iter.previous();
            if((p.getPointType() != PointTypes.STATUS_POINT &&
                p.getPointType() != PointTypes.CALCULATED_STATUS_POINT) ||
                p.getPointName().equals("BANK STATUS")) {
                iter.remove();
            }
        }
        
        TreeSet<LitePoint> pointSet = new TreeSet<LitePoint>(twoStatePoints);
        return pointSet;
    }


   
}