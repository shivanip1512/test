package com.cannontech.dbtools.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ImageFilter;
import com.cannontech.database.PoolManager;

/**
 * Utility program to insert an entire directory of images
 * into the database.
 * 
 * Each image will be inserted into the StateImage table with
 * an ascending generated id.
 * @author alauinger
 */
public class ImageInserter {

	private static final String DB_TABLE = "YukonImage";
	
	private static final String MAX_SQL = "SELECT MAX(ImageID) FROM " + DB_TABLE;
	private static final String INSERT_SQL = "INSERT INTO " + DB_TABLE + " VALUES( ?,?,?,? )";
		
	public static void main(String[] args) {
		if( args.length != 1 ) {
			System.out.println("Usage:  ImageInserter dir");
			System.out.println("Where dir is a directory");
			System.out.println("Inserts all the images in dir into the " + DB_TABLE + " table");	
			return;
		}
		
		ImageInserter ii = new ImageInserter();
		ii.insertImages(args[0]);
	}
	
	public void insertImages(String dir) {
		File fDir = new File(dir);
		
		if( !fDir.exists() ) {
			System.out.println("Directory does not exist");
			return;
		}
		if( !fDir.isDirectory() ) {
			System.out.println("Directory specified is not actually a directory");
			return;
		}		
	
		Connection conn = null;
		PreparedStatement pstmt= null;
		
		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			pstmt = conn.prepareStatement(INSERT_SQL);
			
			//int id = getMaxID(conn) + 1;
								
		File[] allFiles = fDir.listFiles();
		ImageFilter filter = new ImageFilter();
		
		for( int i = 0; i < allFiles.length; i++ ) {
			File f = allFiles[i];
			if( f.isFile() && filter.accept(f) ) {
				String yukName = f.getName().substring(f.getName().indexOf('-')+1);
				int id = Integer.parseInt(f.getName().substring(0, f.getName().indexOf('-')));
				
				long len = f.length();
				
				InputStream in = new FileInputStream(f);
				
				pstmt.setInt(1, id);
            	pstmt.setString(2, fDir.getName());
            	pstmt.setString(3, yukName);
				pstmt.setBinaryStream(4, in, (int) len);
				pstmt.execute();
            
            System.out.println(" (success) Inserted " + yukName + " with id: " + id );
			}	
		}
			pstmt.executeBatch();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
				conn.close();
		    } catch(Exception e2 ) { e2.printStackTrace(); }
			
		}
		
		
	}
		
	/**
	 * Returns the max imageid.
	 * @param conn
	 * @return int
	 */
	private int getMaxID(Connection conn) {
		int maxID = 0;
				
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(MAX_SQL);
			
			if( rset.next() ) {
				maxID = rset.getInt(1);			
			}			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
			if( rset != null ) rset.close();
			} catch( SQLException e) { e.printStackTrace(); }
			
			try {
			if( stmt != null ) stmt.close();
			} catch( SQLException e ) { e.printStackTrace(); }
		}
		
		return maxID;
	}
}
