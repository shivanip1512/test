/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.stars.hardware;

import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMThermostatSchedule extends DBPersistent {
	
	private com.cannontech.database.db.stars.hardware.LMThermostatSchedule lmThermostatSchedule = null;
	
	private ArrayList thermostatSeasons = null;	// List of com.cannontech.database.data.stars.hardware.LMThermostatSeason
	
	public void setScheduleID(Integer scheduleID) {
		getLmThermostatSchedule().setScheduleID( scheduleID );
	}
	
	public void setDbConnection( java.sql.Connection conn ) {
		super.setDbConnection( conn );
		getLmThermostatSchedule().setDbConnection( conn );
		for (int i = 0; i < getThermostatSeasons().size(); i++)
			((LMThermostatSeason) getThermostatSeasons().get(i)).setDbConnection( conn );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getLmThermostatSchedule().add();
		
		for (int i = 0; i < getThermostatSeasons().size(); i++) {
			LMThermostatSeason season = (LMThermostatSeason) getThermostatSeasons().get(i);
			season.getLMThermostatSeason().setScheduleID( getLmThermostatSchedule().getScheduleID() );
			season.add();
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		com.cannontech.database.db.stars.hardware.LMThermostatSeason[] seasons =
				com.cannontech.database.db.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons( getLmThermostatSchedule().getScheduleID().intValue() );
		for (int i = 0; i < seasons.length; i++) {
			LMThermostatSeason season = new LMThermostatSeason();
			season.setSeasonID( seasons[i].getSeasonID() );
			season.setDbConnection( getDbConnection() );
			season.delete();
		}
		
		getLmThermostatSchedule().delete();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLmThermostatSchedule().retrieve();
		
		getThermostatSeasons().clear();
		LMThermostatSeason[] seasons = LMThermostatSeason.getAllLMThermostatSeasons( getLmThermostatSchedule().getScheduleID().intValue() );
		for (int i = 0; i < seasons.length; i++)
			getThermostatSeasons().add( seasons[i] );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLmThermostatSchedule().update();
		
		com.cannontech.database.db.stars.hardware.LMThermostatSeason[] seasons =
				com.cannontech.database.db.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons( getLmThermostatSchedule().getScheduleID().intValue() );
		ArrayList newSeasons = new ArrayList( getThermostatSeasons() );
		
		for (int i = 0; i < seasons.length; i++) {
			LMThermostatSeason newSeason = null;
			for (int j = 0; j < newSeasons.size(); j++) {
				LMThermostatSeason season = (LMThermostatSeason) newSeasons.get(j);
				if (season.getLMThermostatSeason().getSeasonID().equals( seasons[i].getSeasonID() )) {
					newSeason = season;
					newSeasons.remove(j);
					break;
				}
			}
			
			if (newSeason != null) {
				// A new season is found for the existing season, so update it
				newSeason.update();
			}
			else {
				// No new season found for the existing season, so delete the existing one
				LMThermostatSeason oldSeason = new LMThermostatSeason();
				oldSeason.setSeasonID( seasons[i].getSeasonID() );
				oldSeason.setDbConnection( getDbConnection() );
				oldSeason.delete();
			}
			
			// Commit the DB changes to prevent deadlock (in LMThermostatSeasonEntry table)
			getDbConnection().commit();
		}
		
		// The remaining new seasons don't have a corresponding existing season, so add them
		for (int i = 0; i < newSeasons.size(); i++) {
			LMThermostatSeason season = (LMThermostatSeason) newSeasons.get(i);
			season.add();
		}
	}
	
	public static void deleteAllThermostatSchedules(int accountID) {
		try {
			com.cannontech.database.db.stars.hardware.LMThermostatSchedule[] schedules =
					com.cannontech.database.db.stars.hardware.LMThermostatSchedule.getAllThermostatSchedules( accountID );
			
			for (int i = 0; i < schedules.length; i++) {
				LMThermostatSchedule schedule = new LMThermostatSchedule();
				schedule.setScheduleID( schedules[i].getScheduleID() );
				Transaction.createTransaction( Transaction.DELETE, schedule ).execute();
			}
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

	/**
	 * @return
	 */
	public com.cannontech.database.db.stars.hardware.LMThermostatSchedule getLmThermostatSchedule() {
		if (lmThermostatSchedule == null)
			lmThermostatSchedule = new com.cannontech.database.db.stars.hardware.LMThermostatSchedule();
		return lmThermostatSchedule;
	}

	/**
	 * @param schedule
	 */
	public void setLmThermostatSchedule(com.cannontech.database.db.stars.hardware.LMThermostatSchedule schedule) {
		lmThermostatSchedule = schedule;
	}

	/**
	 * @return
	 */
	public ArrayList getThermostatSeasons() {
		if (thermostatSeasons == null)
			thermostatSeasons = new ArrayList();
		return thermostatSeasons;
	}

	/**
	 * @param list
	 */
	public void setThermostatSeasons(ArrayList list) {
		thermostatSeasons = list;
	}

}
