package com.cannontech.datagenerator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.roles.yukon.SystemRole;

/**
 * Replays the point changes of a given day in the past.
 * Connects to the database to get the data to generate point changes
 * Connects to dispatch to send the point changes.
 * @author alauinger
 */
public class PointChangePlayer {

	public static void main(String[] args) throws IOException {
				
		if( args.length != 1 ) {
			System.out.println("Specify a date to replay, ie 9/12/2002");
			System.exit(0);
		}
		
		Date begin = new Date(args[0]);
		PointChangePlayer pcl = new PointChangePlayer();
		pcl.doIt(begin);
	}
	
	private void doIt(Date begin) {
		System.out.println("Using " + begin.toString() + " to source point changes");
		
		PointData[] pChanges = loadChanges(begin);
		System.out.println("loaded " + pChanges.length + " point changes");
		
		ClientConnection conn = new ClientConnection();
		conn.setHost(
			ClientSession.getInstance().getRolePropertyValue(
				SystemRole.DISPATCH_MACHINE).toString());
				
		conn.setPort(Integer.parseInt
				(ClientSession.getInstance().getRolePropertyValue(
					SystemRole.DISPATCH_PORT).toString()));
		try {
			conn.connect();
		} catch (IOException e) {
		}
		
		while(true) {
			for( int i = 0; i < pChanges.length; i++ ) {
				Multi multi = new Multi();
				
				for( int j = 0; j < 10000 && i < pChanges.length; j++, i++ ) {
					PointData pch = pChanges[i];
					Date now = new Date();
					pch.setTime(now);
					pch.setTimeStamp(now);				
					multi.getVector().add(pch);										
				}
				System.out.println("wrote: " + multi.getVector().size());
				conn.write(multi);				
				
			    try {
					Thread.currentThread().sleep(200);
				} catch (InterruptedException e) {
				}
				} 	
		}
	}
	
	private PointData[] loadChanges(Date d) {
		
		ArrayList changeList = new ArrayList();
		
		Timestamp start = new Timestamp(d.getTime());
		Timestamp end = new Timestamp(d.getTime() * 86400 * 1000 );
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			pstmt = conn.prepareStatement("select pointid,quality,value from rawpointhistory where timestamp > ? and timestamp < ? order by changeid");
			pstmt.setTimestamp(1, start);
			pstmt.setTimestamp(2, end);
			
			rset = pstmt.executeQuery();
		
			while( rset.next() ) {
				PointData pch = new PointData();
				pch.setType(PointTypes.ANALOG_POINT);
				pch.setId(rset.getInt(1));				
				pch.setQuality(rset.getInt(2));
				pch.setValue(rset.getDouble(3));
				changeList.add(pch);
			}	
			
		} catch(Exception e ) {
		}
		finally {
			try {
			pstmt.close();
			conn.close();
			}catch(Exception e2) {}				
		}		
		
		PointData[] retVal = new PointData[changeList.size()];
		changeList.toArray(retVal);
		return retVal;
	}
}
