/*
 * Created on Nov 14, 2003
 */
package com.cannontech.cttp;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.cttp.data.CttpCmd;
import com.cannontech.cttp.db.CttpCmdGroup;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.spring.YukonSpringHook;

/**
 * Keeps track of cttp commands and load groups.
 * @author aaron
 */
public class CttpCmdCache implements Serializable {
	
	//Map< Integer(cttpcmdid), CttpCmd >
	Map cmdMap = new HashMap();
	
	//Map< Integer(groupid), List< Integer(cttpcmdid) > >
	Map groupIDCmdIDMap = new HashMap();
	
	//Map< Integer(cttpcmdid), List< Integer(groupid) > >
	Map cmdIDGroupIDMap = new HashMap();
	
	/**
	 * Return any cttp cmd
	 * @param trackingID
	 * @return
	 */
	public synchronized CttpCmd getCmd(int trackingID) {
		return (CttpCmd) cmdMap.get(new Integer(trackingID));	
	}
	
	/**
	 * Return any current cttp cmds for the given group
	 * @param groupID
	 * @return
	 */
	public synchronized CttpCmd getCurrentCmdForGroup(int groupID) {
		CttpCmd retCmd = null;
		List cmdIDList = (List) groupIDCmdIDMap.get(new Integer(groupID));
		if(cmdIDList == null)
			return null;
			
		//find the most recent command
		Iterator cmdIDIter = cmdIDList.iterator();
		while(cmdIDIter.hasNext()) {
			CttpCmd cmd = (CttpCmd) cmdMap.get((Integer)cmdIDIter.next());
			if(retCmd == null) {
				retCmd = cmd;
				continue;
			}
			
			if(cmd.getTimeSent().getTime() > retCmd.getTimeSent().getTime()) {
				retCmd = cmd;
				continue; 
			}
		}
		
		if(retCmd.getClearCmd().charValue() != 'Y' &&
			System.currentTimeMillis() < retCmd.getTimeSent().getTime() + (1000*60*60*retCmd.getDuration().longValue())) {
				return retCmd;
		}
		
		return null;	
	}
	
	/**
	 * Returns the list of groups for a given command
	 * @param trackingID
	 * @return
	 */
	public synchronized List getGroupIDsForCmd(int trackingID) {
		return (List) cmdIDGroupIDMap.get(new Integer(trackingID));
	}
	
	/**
	 * retreive the full heavy lmgroup from the database
	 * @param lmgroupid
	 * @return
	 */
	public LMGroup retrieveGroup(int lmgroupid) {
		LiteYukonPAObject lmg = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(lmgroupid);
		if(lmg == null)
			return null;
		
		try {	
			LMGroup lmGroupPAO= (LMGroup) PAOFactory.createPAObject(lmg);
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, lmGroupPAO);
			t.execute();
			
			return lmGroupPAO;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
		
	/** 
	 * add a cttp offset command to the cache and database
	 * @param trackingID
	 * @param offset
	 * @param duration
	 * @param groupID
	 */
	public synchronized void addOffsetCmd(int tID, int userID, int offset, int duration, List groupIDList) {
		addOffsetCmd(tID, userID, false, offset,duration,groupIDList);
	}		
	
	/**
	 * add a cttp clear command the cache and database
	 * @param trackingID
	 * @param userID
	 * @param groupIDList
	 */
	public synchronized void addClearCmd(int trackingID, int userID, List groupIDList) {
		addOffsetCmd(trackingID, userID, true, -1, -1, groupIDList); 
	}
		
	private synchronized void addOffsetCmd(int tID, int userID, boolean clearcmd, int offset, int duration, List groupIDList) {
		CttpCmd cmd = new CttpCmd();
		com.cannontech.cttp.db.CttpCmd cmdDB = new com.cannontech.cttp.db.CttpCmd();
		Integer trackingID = new Integer(tID);
		cmdDB.setTrackingID(trackingID);
		cmdDB.setUserID(new Integer(userID));
		if(clearcmd) {
			cmdDB.setClearCmd(new Character('Y'));
		}
		else {
			cmdDB.setClearCmd(new Character('N'));
		}
		cmdDB.setStatus("SENT");
		cmdDB.setDegOffset(new Integer(offset));
		cmdDB.setDuration(new Integer(duration));
		cmdDB.setTimeSent(new Date());
		cmdDB.setLastUpdated(new Date());
		cmd.setCttpCmd(cmdDB);
		
		Iterator gIter = groupIDList.iterator();
		while(gIter.hasNext()) {
			Integer groupID = (Integer) gIter.next();
			CttpCmdGroup cmdGrp = new CttpCmdGroup();
			cmdGrp.setTrackingID(trackingID);
			cmdGrp.setLmGroupID(groupID);
			cmd.getCttpCmdGroups().add(cmdGrp);
			
			List cmdIDList = (List) groupIDCmdIDMap.get(groupID);
			if(cmdIDList == null) {
				cmdIDList = new ArrayList();
				groupIDCmdIDMap.put(groupID, cmdIDList);
			}
			cmdIDList.add(trackingID);
		}
		
		cmdMap.put(trackingID, cmd);
		cmdIDGroupIDMap.put(trackingID, groupIDList);
		
		addCmdToDB(cmd); 
	}	
		
	public int getNextTrackingID() {
		Connection conn = null; 
		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			return com.cannontech.cttp.db.CttpCmd.getNextID(conn);
		}
		finally {
			try {
				if(conn != null) conn.close();
			} catch(SQLException e2) { }
		}
	}
	
	private void addCmdToDB(CttpCmd cmd) {
		Connection conn = null;
		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			Transaction t = Transaction.createTransaction(Transaction.INSERT, cmd);
			t.execute();
		}

		catch(TransactionException te) {
			te.printStackTrace();
		}
		finally {
			if(conn != null) { try {conn.close(); } catch(SQLException e2)	{}		
			}
		}
	}
	
	/**
	 * Fill up our maps from the database
	 * @return
	 */
	private static CttpCmdCache restore() {
		CttpCmdCache cmdCache = new CttpCmdCache();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		Statement stmt2 = null;
		ResultSet rset2 = null;
 		 	
		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			stmt = conn.createStatement();
			
			rset = stmt.executeQuery("SELECT TrackingID, UserID, TimeSent, LastUpdated, Status, ClearCmd, DegOffset, Duration FROM CTTPCMD");
			while(rset.next()) {
				Integer trackingID = new Integer(rset.getInt(1));
				CttpCmd cmd = new CttpCmd();

				com.cannontech.cttp.db.CttpCmd cmdDB = new com.cannontech.cttp.db.CttpCmd();
				cmdDB.setTrackingID(trackingID);
				cmdDB.setUserID(new Integer(rset.getInt(2)));
				cmdDB.setTimeSent(new Date(rset.getTimestamp(3).getTime()));
				cmdDB.setLastUpdated(new Date(rset.getTimestamp(4).getTime()));
				cmdDB.setStatus(rset.getString(5));
				cmdDB.setClearCmd(new Character(rset.getString(6).charAt(0)));
				cmdDB.setDegOffset(new Integer(rset.getInt(7)));
				cmdDB.setDuration(new Integer(rset.getInt(8)));
				cmd.setCttpCmd(cmdDB);
				
				cmdCache.cmdMap.put(trackingID, cmd);
			}
			
			rset.close();
			
			rset = stmt.executeQuery("SELECT CmdGroupID, TrackingID, LMGroupID FROM CTTPCMDGROUP");
			while(rset.next()) {
				Integer trackingID = new Integer(rset.getInt(2));
				Integer lmGroupID = new Integer(rset.getInt(3));
				
				CttpCmdGroup cmdGroup = new CttpCmdGroup();
				cmdGroup.setCmdGroupID(new Integer(rset.getInt(1)));
				cmdGroup.setTrackingID(trackingID);
				cmdGroup.setLmGroupID(lmGroupID);
				
				CttpCmd cmd = (CttpCmd) cmdCache.cmdMap.get(trackingID);
				cmd.getCttpCmdGroups().add(cmdGroup);
				
				List grpIDList = (List) cmdCache.cmdIDGroupIDMap.get(trackingID);
				if(grpIDList == null) {
					grpIDList = new ArrayList();
					cmdCache.cmdIDGroupIDMap.put(trackingID, grpIDList);
				}
				grpIDList.add(lmGroupID);
							
				List cmdIDList = (List) cmdCache.groupIDCmdIDMap.get(lmGroupID);
				if(cmdIDList == null) {
					cmdIDList = new ArrayList();
					cmdCache.groupIDCmdIDMap.put(lmGroupID, cmdIDList);
				}
				cmdIDList.add(trackingID);
			}
			
			rset.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			SqlUtils.close(rset, rset2, stmt, stmt2, conn);
		}
		
		return cmdCache;
	}

	private static CttpCmdCache instance = null;
	
	public static synchronized final CttpCmdCache getInstance() {
		if(instance == null) {
			instance = restore();			
		}

		return instance;
	}
}
