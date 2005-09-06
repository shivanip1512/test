package com.cannontech.web.editor;

import java.util.Arrays;
import java.util.TreeSet;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.PointTypes;

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
	private boolean isPointUofM( LitePoint lPoint, int[] uofmIDs ) {

		if( lPoint == null )
			return false;
		else
			return CtiUtilities.isInSet(uofmIDs, lPoint.getUofmID());
	}

	/**
	 * Returns all the PAOs that have a point within the given
	 * UofM id set
	 *
	 */
	public LiteYukonPAObject[] getPAOsByUofMPoints( int[] uofmIDs )
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();

		//ensures uniqueness and ordering by name
		TreeSet paoSet = new TreeSet( LiteComparators.liteStringComparator );
		
		synchronized(cache) {
			java.util.List allPoints = cache.getAllPoints();
			LitePoint litePoint = null;

			for( int i = 0; i < allPoints.size(); i++ ) {

				litePoint = (LitePoint)allPoints.get(i);

				//use the validPt boolean to see if this point is worthy
				if( isPointUofM(litePoint, uofmIDs)
					&& litePoint.getPointType() == PointTypes.ANALOG_POINT
					|| litePoint.getPointType() == PointTypes.CALCULATED_POINT )				
				{
					LiteYukonPAObject liteDevice = PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );

					if( DeviceClasses.isCoreDeviceClass(liteDevice.getPaoClass()) )
						paoSet.add( liteDevice );
			   }
			}
		 }
		 
		//return the uniquly ordered elements
		return (LiteYukonPAObject[])paoSet.toArray( new LiteYukonPAObject[paoSet.size()] );		 
	}
	

	/**
	 * Comment
	 */
	public LitePoint[] getPointsByUofMPAOs( int paoID, int[] uofmIDs ) {

	   //if the (none) object is selected, just return
//	   getJComboBoxVarPoint().setEnabled(
//			 getJComboBoxVarDevice().getSelectedItem() != LiteYukonPAObject.LITEPAOBJECT_NONE );
//		if( getJComboBoxVarDevice().getSelectedItem() == LiteYukonPAObject.LITEPAOBJECT_NONE )
//		   return new LitePoint[0];

		//ensures uniqueness and ordering by name
		TreeSet pointSet = new TreeSet( LiteComparators.liteStringComparator );

		LitePoint[] litePts = PAOFuncs.getLitePointsForPAObject( paoID );
		Arrays.sort( litePts, LiteComparators.liteStringComparator ); //sort the small list by PointName

		for( int i = 0; i < litePts.length; i++ ) {
			if( isPointUofM(litePts[i], uofmIDs) 
				 && (litePts[i].getPointType() == PointTypes.ANALOG_POINT
					  || litePts[i].getPointType() == PointTypes.CALCULATED_POINT) )
			{      
				pointSet.add( litePts[i] );
			}
		}
	
		//return the uniquly ordered elements
		return (LitePoint[])pointSet.toArray( new LitePoint[pointSet.size()] );
	}
}
