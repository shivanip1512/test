/*
 * Created on Aug 11, 2003
*/
package com.cannontech.dbtools.tools;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapControlYukonPAOBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author aaron
 * @see com.cannontech.dbtools.tools.ExportPAO
 */
public class ImportPAO {
	private static String USAGE = "ImportPAO dumpfile idoffset\n Imports a pao and all of its points from a dump file created with ExportPAO";
	
	public static void main(String[] args) throws Exception {
		if(args.length < 1){
			System.out.print(USAGE);
			System.exit(-1);
		}

		Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		if(conn == null) {
			System.out.println("Couldn't get a database connection!");
			System.exit(-1);
			
		}
		
		String fileName = args[0];
		int idOffset = 0;
		
		if(args.length >= 2)
			idOffset = Integer.parseInt(args[1]);
		
		CTILogger.info("Loading " + fileName);
		
		FileInputStream fi = new FileInputStream(fileName);
		ObjectInputStream oi = new ObjectInputStream(fi);
		
		DBPersistent dbPao = (DBPersistent) oi.readObject();
		DBPersistent[] stateGroups = (DBPersistent[]) oi.readObject();
		DBPersistent[] dbPoints = (DBPersistent[]) oi.readObject();

		int id = ((YukonPAObject) dbPao).getPAObjectID().intValue();
		CTILogger.info("paoid: " + id + " -> " + (id + idOffset));
		setPaoID(dbPao, id+idOffset);
		
		if(dbPao instanceof RemoteBase) {
			int portID = ((RemoteBase) dbPao).getDeviceDirectCommSettings().getPortID().intValue();
			((RemoteBase)dbPao).getDeviceDirectCommSettings().setPortID(new Integer(portID+idOffset));
		} 
		//((YukonPAObject) dbPao).setPAObjectID(new Integer(id + idOffset));
		
		dbPao.setDbConnection(conn);
		dbPao.add();
		
		for(int i = 0; i < stateGroups.length; i++) {
			id = ((GroupState) stateGroups[i]).getStateGroup().getStateGroupID().intValue();
			if(YukonSpringHook.getBean(StateGroupDao.class).getStateGroup(id + idOffset) == null) {
				CTILogger.info("stategroupid: " + id + " -> " + (id+idOffset));
				((GroupState) stateGroups[i]).setStateGroupID(new Integer(id+idOffset));
				String newName = ((GroupState) stateGroups[i]).getStateGroup().getName() + idOffset;
				if(newName.length() > 20) {
					newName = newName.substring(0, 20);
				}
				((GroupState) stateGroups[i]).getStateGroup().setName(newName);
				stateGroups[i].setDbConnection(conn);	
				stateGroups[i].add();								
			}
			else {
				CTILogger.info("skipping stategroupid: " + id + " -> " + (id+idOffset));
			}
		}
		
		for(int i = 0; i < dbPoints.length; i++) {
			id = ((PointBase) dbPoints[i]).getPoint().getPointID().intValue();
			CTILogger.info("pointid: " + id + " -> " + (id+idOffset));
			((PointBase) dbPoints[i]).setPointID(new Integer(id+idOffset));
			((PointBase) dbPoints[i]).getPoint().setPaoID( new Integer(((PointBase) dbPoints[i]).getPoint().getPaoID().intValue()+idOffset));
			int oldStateGroupID = ((PointBase) dbPoints[i]).getPoint().getStateGroupID().intValue();
			if(oldStateGroupID > 0) //could be -1 
				((PointBase) dbPoints[i]).getPoint().setStateGroupID(new Integer(oldStateGroupID+idOffset));
				
			((PointBase)dbPoints[i]).getPointAlarming().setNotificationGroupID(new Integer(1));
			dbPoints[i].setDbConnection(conn);
			dbPoints[i].add();
		}
		
		conn.close();
		CTILogger.info("done");
		System.exit(0);
	}

	private static void setPaoID(DBPersistent db, int id) {
		Integer nID = new Integer(id);
		if(db instanceof CapControlYukonPAOBase) {
			((CapControlYukonPAOBase)db).setCapControlPAOID(nID);
		}
		else
		if(db instanceof DeviceBase) {
			((DeviceBase)db).setDeviceID(nID);			
		}
		else 
		if(db instanceof DirectPort) {
			((DirectPort)db).setPortID(nID);
		}
		else
		if(db instanceof LMControlArea) {
			((LMControlArea)db).setPAObjectID(nID);
		}
		else 
		if(db instanceof LMProgramBase) {
			((LMProgramBase)db).setPAObjectID(nID);
		}
		else 
		if(db instanceof RouteBase) {
			((RouteBase)db).setRouteID(nID);
		}
	}	
}