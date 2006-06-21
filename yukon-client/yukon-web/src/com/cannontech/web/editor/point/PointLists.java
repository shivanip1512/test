package com.cannontech.web.editor.point;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.yukon.IDatabaseCache;

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
    private boolean isPointUofM(LitePoint lPoint, int[] uofmIDs) {

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
    public LiteYukonPAObject[] getPAOsByUofMPoints(int[] uofmIDs) {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        //ensures uniqueness and ordering by name
        TreeSet paoSet = new TreeSet(LiteComparators.liteStringComparator);

        synchronized (cache) {
            java.util.List allPoints = cache.getAllPoints();
            LitePoint litePoint = null;

            for (int i = 0; i < allPoints.size(); i++) {

                litePoint = (LitePoint) allPoints.get(i);

                //use the validPt boolean to see if this point is worthy
                if (isPointUofM(litePoint, uofmIDs)
                        && litePoint.getPointType() == PointTypes.ANALOG_POINT
                        || litePoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    LiteYukonPAObject liteDevice = DaoFactory.getPaoDao()
                            .getLiteYukonPAO(litePoint.getPaobjectID());

                    if (DeviceClasses.isCoreDeviceClass(liteDevice
                            .getPaoClass()))
                        paoSet.add(liteDevice);
                }
            }
        }

        //return the uniquly ordered elements
        return (LiteYukonPAObject[]) paoSet
                .toArray(new LiteYukonPAObject[paoSet.size()]);
    }

    /**
     * Comment
     */
    public LitePoint[] getPointsByUofMPAOs(int paoID, int[] uofmIDs) {

        //if the (none) object is selected, just return
        //     getJComboBoxVarPoint().setEnabled(
        //           getJComboBoxVarDevice().getSelectedItem() != LiteYukonPAObject.LITEPAOBJECT_NONE );
        //      if( getJComboBoxVarDevice().getSelectedItem() == LiteYukonPAObject.LITEPAOBJECT_NONE )
        //         return new LitePoint[0];

        //ensures uniqueness and ordering by name
        TreeSet pointSet = new TreeSet(LiteComparators.liteStringComparator);

        LitePoint[] litePts = DaoFactory.getPaoDao().getLitePointsForPAObject(paoID);
        Arrays.sort(litePts, LiteComparators.liteStringComparator); //sort the small list by PointName

        for (int i = 0; i < litePts.length; i++) {
            if (isPointUofM(litePts[i], uofmIDs)
                    && (litePts[i].getPointType() == PointTypes.ANALOG_POINT || litePts[i]
                            .getPointType() == PointTypes.CALCULATED_POINT)) {
                pointSet.add(litePts[i]);
            }
        }

        //return the uniquly ordered elements
        return (LitePoint[]) pointSet.toArray(new LitePoint[pointSet.size()]);
    }


    public static Set getAllTwoStateStatusPoints() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        TreeSet pointSet = new TreeSet();
        synchronized (cache) {
            java.util.List allPoints = cache.getAllPoints();
            LitePoint litePoint = null;

            for (int i = 0; i < allPoints.size(); i++) {

                litePoint = (LitePoint) allPoints.get(i);
                
                int pointType = litePoint.getPointType();
                if ((pointType == PointTypes.STATUS_POINT)
                    || pointType == PointTypes.CALCULATED_STATUS_POINT ) {
                    if (!litePoint.getPointName().equals("BANK STATUS"))
	                	{
	                    int stateGrpId = litePoint.getStateGroupID();
	                    LiteStateGroup liteStateGroup = DaoFactory.getStateDao().getLiteStateGroup(stateGrpId);
	                    if (liteStateGroup.getStatesList().size() == 2) {
	                        pointSet.add(litePoint);
	                    }
	                }
	               }
            }
        }

        return pointSet;
    }


   
}