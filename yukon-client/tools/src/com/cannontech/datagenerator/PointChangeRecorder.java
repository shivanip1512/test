package com.cannontech.datagenerator;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * Saves a days worth of point changes by looking at the rawpointhistory table
 * @author alauinger
 */
public class PointChangeRecorder {

	public static void main(String[] args) {		
		
		if( args.length != 2 ) {
			System.out.println("Example usage:\ncom.cannontech.datagenerator.PointChangeRecorder output.dat 9/15/2002");			
			System.exit(-1);
		}
		
		String outputFile = args[0];
		Date startDate = new Date(args[1]);
		Timestamp start = new Timestamp(startDate.getTime());
		Timestamp end = new Timestamp(startDate.getTime() * 86400 * 1000 );
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			pstmt = conn.prepareStatement("select pointid,timestamp,quality,value from rawpointhistory where timestamp > ? and timestamp < ? order by timestamp,changeid");
			pstmt.setTimestamp(1, start);
			pstmt.setTimestamp(2, end);
			
			rset = pstmt.executeQuery();
		
			DataOutputStream das = new DataOutputStream( new BufferedOutputStream(new FileOutputStream(outputFile)) );	
			while( rset.next() ) {
				das.writeInt( rset.getInt(1));
				das.writeLong( rset.getTimestamp(2).getTime() );
				das.writeInt( rset.getInt(3));
				das.writeDouble( rset.getDouble(4));
			}	
			
			das.flush();
			das.close();
			
		} catch(Exception e ) {
		}
		finally {
			try {
			pstmt.close();
			conn.close();
			}catch(Exception e2) {}				
		}		
	}
}
