/*
 * Created on Nov 19, 2003
 */
package com.cannontech.cttp.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cannontech.cttp.db.CttpCmdGroup;
import com.cannontech.database.db.DBPersistent;

/**
 * @author aaron
 */
public class CttpCmd extends DBPersistent {
	
	private com.cannontech.cttp.db.CttpCmd cttpCmd;
	private List cttpCmdGroups = new ArrayList();
	
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getCttpCmd().add();
		Iterator iter = cttpCmdGroups.iterator();
		while(iter.hasNext()) {
			CttpCmdGroup cmdGrp = (CttpCmdGroup) iter.next();
			cmdGrp.add();
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Iterator iter = cttpCmdGroups.iterator();
		while(iter.hasNext()) {
			CttpCmdGroup cmdGrp = (CttpCmdGroup) iter.next();
			cmdGrp.delete();
		}
		getCttpCmd().delete();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getCttpCmd().retrieve();
		Iterator iter = cttpCmdGroups.iterator();
		while(iter.hasNext()) {
			CttpCmdGroup cmdGrp = (CttpCmdGroup) iter.next();
			cmdGrp.retrieve();
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		//The groups should never change, if at some point this changes
		// then update will have to take into account changes to the list
		//of cttpcmdgroups, for now don't bother, w00t
		getCttpCmd().update();
	}

	/**
	 * @return
	 */
	public com.cannontech.cttp.db.CttpCmd getCttpCmd() {
		return cttpCmd;
	}

	/**
	 * @param cmd
	 */
	public void setCttpCmd(com.cannontech.cttp.db.CttpCmd cmd) {
		cttpCmd = cmd;
	}

	/**
	 * @return
	 */
	public Character getClearCmd() {
		return cttpCmd.getClearCmd();
	}

	
	/**
	 * @return
	 */
	public Integer getDegOffset() {
		return cttpCmd.getDegOffset();
	}

	/**
	 * @return
	 */
	public Integer getDuration() {
		return cttpCmd.getDuration();
	}

	
	/**
	 * @return
	 */
	public Integer getTrackingID() {
		return cttpCmd.getTrackingID();
	}

	/**
	 * @return
	 */
	public Integer getUserID() {
		return cttpCmd.getUserID();
	}

	/**
	 * @param character
	 */
	public void setClearCmd(Character character) {
		cttpCmd.setClearCmd(character);
	}

	

	/**
	 * @param integer
	 */
	public void setDegOffset(Integer integer) {
		cttpCmd.setDegOffset(integer);
	}

	/**
	 * @param integer
	 */
	public void setDuration(Integer integer) {
		cttpCmd.setDuration(integer);
	}

	/**
	 * @param integer
	 */
	public void setTrackingID(Integer integer) {
		cttpCmd.setTrackingID(integer);
	}

	/**
	 * @param integer
	 */
	public void setUserID(Integer integer) {
		cttpCmd.setUserID(integer);
	}
	/**
	 * @return
	 */
	public Date getLastUpdated() {
		return cttpCmd.getLastUpdated();
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return cttpCmd.getStatus();
	}

	/**
	 * @return
	 */
	public Date getTimeSent() {
		return cttpCmd.getTimeSent();
	}

	/**
	 * @param date
	 */
	public void setLastUpdated(Date date) {
		cttpCmd.setLastUpdated(date);
	}

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		cttpCmd.setStatus(string);
	}

	/**
	 * @param date
	 */
	public void setTimeSent(Date date) {
		cttpCmd.setTimeSent(date);
	}

	/**
	 * @return
	 */
	public List getCttpCmdGroups() {
		return cttpCmdGroups;
	}

	/**
	 * @param list
	 */
	public void setCttpCmdGroups(List list) {
		cttpCmdGroups = list;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#setDbConnection(java.sql.Connection)
	 */
	public void setDbConnection(Connection c) {
		super.setDbConnection(c);
		getCttpCmd().setDbConnection(c);
		Iterator iter = cttpCmdGroups.iterator();
		while(iter.hasNext()) {
			CttpCmdGroup cmdGrp = (CttpCmdGroup) iter.next();
			cmdGrp.setDbConnection(c);
		}
	}

}
