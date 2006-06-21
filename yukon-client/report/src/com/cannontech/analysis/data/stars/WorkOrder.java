/*
 * Created on Dec 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.stars;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrder {
	
	private LiteWorkOrderBase liteWorkOrderBase = null;
//	private ArrayList<LiteInventoryBase> liteInventories = null;
	private LiteInventoryBase liteInventoryBase = null;
	
	public WorkOrder() {
	}

	public WorkOrder(LiteWorkOrderBase liteWorkOrderBase_) {
		this.liteWorkOrderBase = liteWorkOrderBase_;
	}

	public WorkOrder(LiteWorkOrderBase liteWorkOrderBase_, LiteInventoryBase liteInventoryBase_) {
		this.liteWorkOrderBase = liteWorkOrderBase_;
		this.liteInventoryBase = liteInventoryBase_;
	}

	public LiteWorkOrderBase getLiteWorkOrderBase() {
		return liteWorkOrderBase;
	}

	public void setLiteWorkOrderBase(LiteWorkOrderBase liteWorkOrderBase) {
		this.liteWorkOrderBase = liteWorkOrderBase;
	}

	public LiteInventoryBase getLiteInventoryBase() {
		return liteInventoryBase;
	}

	public void setLiteInventoryBase(LiteInventoryBase liteInventoryBase) {
		this.liteInventoryBase = liteInventoryBase;
	}

	/*public ArrayList<LiteInventoryBase> getLiteInventories() {
		if( liteInventories == null)
			liteInventories = new ArrayList<LiteInventoryBase>();
		return liteInventories;
	}

	public void setLiteInventories(ArrayList<LiteInventoryBase> liteInventories) {
		this.liteInventories = liteInventories;
	}*/
}
