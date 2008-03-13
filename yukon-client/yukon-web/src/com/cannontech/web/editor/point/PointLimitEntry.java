package com.cannontech.web.editor.point;

import javax.faces.event.ValueChangeEvent;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.PointUnit;

/**
 * A point editor for PointLimits. The Point must be of a Scalar point type.
 * @author ryan
 */
public class PointLimitEntry {
	private ScalarPoint scalarPoint = null;

	public PointLimitEntry( ScalarPoint pBase ) {
		super();
		if( pBase == null ) { 
		    throw new IllegalArgumentException("PointLimitEntry can not be created with a NULL PointBase reference");
		}
		scalarPoint = pBase;
	}

	/**
	 * The instance of the underlying base object
	 */
	private ScalarPoint getScalarPoint() {
		return scalarPoint;
	}

	/**
	 * Puts our form into Point Limit editing mode 
	 */
	public void showLimit( ValueChangeEvent ev ) {
		
		if(ev == null || ev.getNewValue() == null) return;

		Boolean isChecked = (Boolean)ev.getNewValue();
		Integer limitNum = ev.getComponent().getId().equalsIgnoreCase("Limit_One") 
			? new Integer(1) : (ev.getComponent().getId().equalsIgnoreCase("Limit_Two") 
            ? new Integer(2) : null );

		//not a recognized PointNumber key, lets scat
		if( limitNum == null ) {
			return;
		}

		if( isChecked.booleanValue() ) {
			PointLimit pointLimit = new PointLimit();
			pointLimit.setLimitNumber(limitNum);
			//use the default constructor since no values are set
			getScalarPoint().getPointLimitsMap().put( limitNum, pointLimit ); 
		} else {
		    getScalarPoint().getPointLimitsMap().remove( limitNum );
		}
	}

	public boolean isEditingLimitOne() {		
		return getScalarPoint().getPointLimitsMap().containsKey( new Integer(1) );
	}

	public boolean isEditingLimitTwo() {
		return getScalarPoint().getPointLimitsMap().containsKey( new Integer(2) );
	}

	public void showReasonabilityLimit( ValueChangeEvent ev ) {

		if(ev == null || ev.getNewValue() == null) return;

		Boolean isChecked = (Boolean)ev.getNewValue();
		PointUnit pUnit = getScalarPoint().getPointUnit();

		if( ev.getComponent().getId().equalsIgnoreCase("Reasonability_High") ) {
			if( isChecked.booleanValue() ) {
				pUnit.setHighReasonabilityLimit( new Double(0.0) );
			}else {
				pUnit.setHighReasonabilityLimit( new Double(CtiUtilities.INVALID_MAX_DOUBLE) );
			}
		} else if( ev.getComponent().getId().equalsIgnoreCase("Reasonability_Low") ) {
			if( isChecked.booleanValue() ) {
				pUnit.setLowReasonabilityLimit( new Double(0.0) );
			}else {
				pUnit.setLowReasonabilityLimit( new Double(CtiUtilities.INVALID_MIN_DOUBLE) );
			}
		} 
	}

	public boolean isHighReasonabilityValid() {
		return getScalarPoint().getPointUnit().getHighReasonabilityLimit().doubleValue() < CtiUtilities.INVALID_MAX_DOUBLE;
	}

	public boolean isLowReasonabilityValid() {
		return getScalarPoint().getPointUnit().getLowReasonabilityLimit().doubleValue() > CtiUtilities.INVALID_MIN_DOUBLE;
	}

	public void setEditingLimitOne( boolean val ) {}
	public void setEditingLimitTwo( boolean val ) {}
	public void setHighReasonabilityValid( boolean val ) {}
	public void setLowReasonabilityValid( boolean val ) {}
}