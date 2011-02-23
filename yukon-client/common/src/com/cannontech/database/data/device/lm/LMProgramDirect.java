package com.cannontech.database.data.device.lm;

import java.util.Vector;

import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.device.lm.DeviceListItem;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;

public class LMProgramDirect extends LMProgramBase {

	private com.cannontech.database.db.device.lm.LMProgramDirect directProgram = null;
	private Vector<LMProgramDirectGear> lmProgramDirectGearVector = null;
	private Vector<LMDirectNotificationGroupList> lmProgramDirectNotifyGroupVector = null;

	/**
	 * LMProgramBase constructor comment.
	 */
	public LMProgramDirect() {
		super();

		getYukonPAObject().setType(PAOGroups.STRING_LM_DIRECT_PROGRAM[0]);
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void add() throws java.sql.SQLException {
		super.add();
		getDirectProgram().add();

		// delete all the current gears from the DB
		// LMProgramDirectGear.deleteAllDirectGears( getDevice().getDeviceID()
		// );

		// add the gears
		for (LMProgramDirectGear lmProgramDirectGear : getLmProgramDirectGearVector()) {
			lmProgramDirectGear.add();
		}

		// add all the Groups for this DirectProgram
		for (DeviceListItem deviceListItem : getLmProgramStorageVector()) {
			if (deviceListItem instanceof LMProgramDirectGroup) {
				LMProgramDirectGroup lmProgramDirectGroup = (LMProgramDirectGroup) deviceListItem;
				lmProgramDirectGroup.setDeviceID(getPAObjectID());
				lmProgramDirectGroup.setDbConnection(getDbConnection());
				lmProgramDirectGroup.add();
			}
		}

		// add the customers
		for (LMDirectNotificationGroupList lmDirectNotificationGroupList : getLmProgramDirectNotifyGroupVector()) {
			lmDirectNotificationGroupList.add();
		}
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException {

		// delete all of our direct customers first
		delete(LMDirectNotificationGroupList.TABLE_NAME,
				LMDirectNotificationGroupList.CONSTRAINT_COLUMNS[0],
				getPAObjectID());

		LMProgramDirectGear.deleteAllDirectGears(getPAObjectID(),
				getDbConnection());

		com.cannontech.database.db.device.lm.LMProgramDirectGroup
				.deleteAllDirectGroups(getPAObjectID(), getDbConnection());

		deleteFromDynamicTables();

		getDirectProgram().delete();
		super.delete();
	}

	private void deleteFromDynamicTables() throws java.sql.SQLException {
		delete("DynamicLMProgramDirect", "deviceID", getPAObjectID());
		// delete("DynamicLMGroup", "LMProgramID", getPAObjectID() );
	}

	public com.cannontech.database.db.device.lm.LMProgramDirect getDirectProgram() {
		if (directProgram == null) {
			directProgram = new com.cannontech.database.db.device.lm.LMProgramDirect();
		}

		return directProgram;
	}

	public Vector<LMProgramDirectGear> getLmProgramDirectGearVector() {
		if (lmProgramDirectGearVector == null) {
			lmProgramDirectGearVector = new Vector<LMProgramDirectGear>(10);
		}

		return lmProgramDirectGearVector;
	}

	public Vector<LMDirectNotificationGroupList> getLmProgramDirectNotifyGroupVector() {
		if (lmProgramDirectNotifyGroupVector == null) {
			lmProgramDirectNotifyGroupVector = new Vector<LMDirectNotificationGroupList>();
		}

		return lmProgramDirectNotifyGroupVector;
	}

	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getDirectProgram().retrieve();

		// retrieve all the gears for this Program
		Vector<LMProgramDirectGear> gears = LMProgramDirectGear.getAllDirectGears(getPAObjectID(), getDbConnection());
		getLmProgramDirectGearVector().clear();
		for (LMProgramDirectGear gear : gears) {
			getLmProgramDirectGearVector().add(gear);
		}

		// retrieve all the Groups for this Program
		com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups = 
			com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups(getPAObjectID());
		getLmProgramStorageVector().clear();
		for (int i = 0; i < groups.length; i++) {
			getLmProgramStorageVector().add(groups[i]);
		}

		LMDirectNotificationGroupList[] customers = 
			com.cannontech.database.db.device.lm.LMProgramDirect.getAllNotificationGroupsList(
					getPAObjectID(),getDbConnection());
		getLmProgramDirectNotifyGroupVector().clear();
		for (LMDirectNotificationGroupList customer: customers) {
			getLmProgramDirectNotifyGroupVector().add(customer);
		}
	}

	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getDirectProgram().setDbConnection(conn);

		for (LMProgramDirectGear lmProgramDirectGear : getLmProgramDirectGearVector()) {
			lmProgramDirectGear.setDbConnection(conn);
		}
		
		for (LMDirectNotificationGroupList lmDirectNotificationGroupList: getLmProgramDirectNotifyGroupVector()) {
			lmDirectNotificationGroupList.setDbConnection(conn);
		}
	}

	public void setDirectProgram(com.cannontech.database.db.device.lm.LMProgramDirect newDirectProgram) {
		directProgram = newDirectProgram;
	}

	public void setLmProgramDirectGearVector(Vector<LMProgramDirectGear> newLmProgramDirectGearVector) {
		lmProgramDirectGearVector = newLmProgramDirectGearVector;
	}

	public void setPAObjectID(Integer paoID) {
		super.setPAObjectID(paoID);
		getDirectProgram().setDeviceID(paoID);

		for (LMProgramDirectGear lmProgramDirectGear : getLmProgramDirectGearVector()) {
			lmProgramDirectGear.setDeviceID(paoID);
		}
		
		for (LMDirectNotificationGroupList lmDirectNotificationGroupList: getLmProgramDirectNotifyGroupVector()) {
			lmDirectNotificationGroupList.setDeviceID(paoID);
		}
	}

	public void update() throws java.sql.SQLException {
		super.update();
		getDirectProgram().update();
		Vector<LMProgramDirectGear> gearVector = new Vector<LMProgramDirectGear>();

		// grab all the previous gear entries for this program
		Vector<LMProgramDirectGear> oldGears = LMProgramDirectGear.getAllDirectGears(
				getPAObjectID(), getDbConnection());

		// unleash the power of the NestedDBPersistent
		gearVector = NestedDBPersistentComparators.NestedDBPersistentCompare(
				oldGears, getLmProgramDirectGearVector(),
				NestedDBPersistentComparators.lmDirectGearComparator);

		// throw the gears into the Db
		for (LMProgramDirectGear lmProgramDirectGear : gearVector) {
			lmProgramDirectGear.setDeviceID(getPAObjectID());
			lmProgramDirectGear.executeNestedOp();

		}

		// delete all the current associated groups from the DB
		com.cannontech.database.db.device.lm.LMProgramDirectGroup
				.deleteAllDirectGroups(getPAObjectID(), getDbConnection());

		// add the groups
		for (DeviceListItem deviceListItem : getLmProgramStorageVector()) {
			if (deviceListItem instanceof LMProgramDirectGroup) {
				LMProgramDirectGroup lmProgramDirectGroup = (LMProgramDirectGroup)deviceListItem;
				lmProgramDirectGroup.setDeviceID(getPAObjectID());
				lmProgramDirectGroup.setDbConnection(getDbConnection());
				lmProgramDirectGroup.add();
			}
		}

		// delete all of our energy exchange customers first
		delete(LMDirectNotificationGroupList.TABLE_NAME,
				LMDirectNotificationGroupList.CONSTRAINT_COLUMNS[0],
				getPAObjectID());

		for (LMDirectNotificationGroupList lmDirectNotificationGroupList : getLmProgramDirectNotifyGroupVector()) {
			lmDirectNotificationGroupList.setDeviceID(getDirectProgram().getDeviceID());
			lmDirectNotificationGroupList.add();
		}
	}
}