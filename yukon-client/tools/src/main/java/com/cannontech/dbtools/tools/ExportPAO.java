/*
 * Created on Aug 11, 2003
 */
package com.cannontech.dbtools.tools;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author aaron
 * @see com.cannontech.dbtools.tools.ImportPAO
 */
public class ExportPAO {

	private static final String USAGE = "ExportPAO paoid dumpfile";
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.out.println(USAGE);
			System.exit(-1);
		}
		Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		if(conn == null) {
			System.out.println("Couldn't get a database connection!");
			System.exit(-1);
			
		}
				
		int id = Integer.parseInt(args[0]);
		String fileName = args[1];

		LiteYukonPAObject litePao = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(id);
        List<LitePoint> litePoints = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(id);
		
		DBPersistent dbPao = LiteFactory.createDBPersistent(litePao);
		CTILogger.info("paoid: " + id);
		dbPao.setDbConnection(conn);
		dbPao.retrieve();
		
		DBPersistent[] dbPoints = new DBPersistent[litePoints.size()];
		HashMap stateGroupMap = new HashMap();
		
		for(int i = 0; i < litePoints.size(); i++) {
			dbPoints[i] = LiteFactory.createDBPersistent(litePoints.get(i));
			
			CTILogger.info("point id: " + litePoints.get(i).getLiteID());
			dbPoints[i].setDbConnection(conn);
			dbPoints[i].retrieve();
			
			LiteStateGroup lsg = YukonSpringHook.getBean(StateGroupDao.class).getStateGroup(((PointBase)dbPoints[i]).getPoint().getStateGroupID().intValue());
			if(lsg != null) {				
				stateGroupMap.put(new Integer(lsg.getStateGroupID()), LiteFactory.createDBPersistent(lsg));
			}
		}
			
		
			
		DBPersistent[] stateGroups = new DBPersistent[stateGroupMap.size()];
		Iterator sgIter = stateGroupMap.entrySet().iterator();
		int i = 0;
		while(sgIter.hasNext()) {
			Map.Entry e = (Map.Entry) sgIter.next();
			stateGroups[i] = (DBPersistent) e.getValue();
			CTILogger.info("stategroup id: " + stateGroups[i]);
			stateGroups[i].setDbConnection(conn);
			stateGroups[i].retrieve();
			i++;
		}
		
		conn.close();
		
		CTILogger.info("\nWriting " + fileName + "...");
		
		FileOutputStream of = new FileOutputStream(fileName);
		ObjectOutputStream os = new ObjectOutputStream(of);		
		
		os.writeObject(dbPao);		
		os.writeObject(stateGroups);
		os.writeObject(dbPoints);
		
		of.close();
		
		CTILogger.info("...done");
		System.exit(0);
	}
}
