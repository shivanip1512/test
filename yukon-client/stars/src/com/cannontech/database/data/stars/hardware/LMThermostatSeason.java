package com.cannontech.database.data.stars.hardware;

import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMThermostatSeason extends DBPersistent {
	
	private com.cannontech.database.db.stars.hardware.LMThermostatSeason lmThermostatSeason = null;
	private ArrayList lmThermostatSeasonEntries = null;
	
	public LMThermostatSeason() {
		super();
	}
	
	public void setSeasonID(Integer newID) {
		getLMThermostatSeason().setSeasonID( newID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getLMThermostatSeason().setDbConnection( conn );
		for (int i = 0; i < getLMThermostatSeasonEntries().size(); i++) {
			LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) getLMThermostatSeasonEntries().get(i);
			entry.setDbConnection( conn );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getLMThermostatSeason().add();
		
		for (int i = 0; i < getLMThermostatSeasonEntries().size(); i++) {
			LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) getLMThermostatSeasonEntries().get(i);
			entry.setSeasonID( getLMThermostatSeason().getSeasonID() );
			entry.add();
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		LMThermostatSeasonEntry.deleteAllLMThermostatSeasonEntries( getLMThermostatSeason().getSeasonID() );
		getLMThermostatSeason().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLMThermostatSeason().retrieve();
		
		getLMThermostatSeasonEntries().clear();
		LMThermostatSeasonEntry[] entries = LMThermostatSeasonEntry.getAllLMThermostatSeasonEntries(
				getLMThermostatSeason().getSeasonID(), getDbConnection() );
		for (int i = 0; i < entries.length; i++)
			getLMThermostatSeasonEntries().add( entries[i] );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMThermostatSeason().update();
		
		LMThermostatSeasonEntry.deleteAllLMThermostatSeasonEntries( getLMThermostatSeason().getSeasonID() );
		for (int i = 0; i < getLMThermostatSeasonEntries().size(); i++) {
			LMThermostatSeasonEntry entry = (LMThermostatSeasonEntry) getLMThermostatSeasonEntries().get(i);
			entry.add();
		}
	}
	
	public static LMThermostatSeason[] getAllLMThermostatSeasons(int inventoryID, java.sql.Connection conn) {
		com.cannontech.database.db.stars.hardware.LMThermostatSeason[] seasonDBs =
				com.cannontech.database.db.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons( inventoryID, conn );
		if (seasonDBs == null) return null;
		
		try {
			LMThermostatSeason[] seasons = new LMThermostatSeason[ seasonDBs.length ];
			for (int i = 0; i < seasonDBs.length; i++) {
				seasons[i] = new LMThermostatSeason();
				seasons[i].setSeasonID( seasonDBs[i].getSeasonID() );
				seasons[i].setDbConnection( conn );
				seasons[i].retrieve();
			}
			return seasons;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Returns the lmThermostatSeason.
	 * @return com.cannontech.database.db.stars.hardware.LMThermostatSeason
	 */
	public com.cannontech.database.db.stars.hardware.LMThermostatSeason getLMThermostatSeason() {
		if (lmThermostatSeason == null)
			lmThermostatSeason = new com.cannontech.database.db.stars.hardware.LMThermostatSeason();
		return lmThermostatSeason;
	}

	/**
	 * Sets the lmThermostatSeason.
	 * @param lmThermostatSeason The lmThermostatSeason to set
	 */
	public void setLMThermostatSeason(com.cannontech.database.db.stars.hardware.LMThermostatSeason lmThermostatSeason) {
		this.lmThermostatSeason = lmThermostatSeason;
	}

	/**
	 * Returns the lmThermostatSeasonEntries.
	 * @return ArrayList
	 */
	public ArrayList getLMThermostatSeasonEntries() {
		if (lmThermostatSeasonEntries == null)
			lmThermostatSeasonEntries = new ArrayList();
		return lmThermostatSeasonEntries;
	}

	/**
	 * Sets the lmThermostatSeasonEntries.
	 * @param lmThermostatSeasonEntries The lmThermostatSeasonEntries to set
	 */
	public void setLMThermostatSeasonEntries(ArrayList lmThermostatSeasonEntries) {
		this.lmThermostatSeasonEntries = lmThermostatSeasonEntries;
	}

}
