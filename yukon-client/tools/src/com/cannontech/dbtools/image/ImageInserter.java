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
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.IRunnableDBTool;

/**
 * Utility program to insert an entire directory of images
 * into the database.
 * 
 * Each image will be inserted into the StateImage table with
 * an ascending generated id.
 * @author alauinger
 */
public class ImageInserter extends MessageFrameAdaptor
{

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
		System.setProperty( IRunnableDBTool.PROP_VALUE, args[0] );
		ii.run();

		//ii.insertImages(args[0]);
	}
	
	public void insertImages(String dir) throws Exception 
	{
		File fDir = new File(dir);
		
		if( !fDir.exists() ) {
			getIMessageFrame().addOutput("Directory does not exist");
			return;
		}
		if( !fDir.isDirectory() ) {
			getIMessageFrame().addOutput("Directory specified is not actually a directory");
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
			
			for( int i = 0; i < allFiles.length; i++ ) 
			{
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
	            
					getIMessageFrame().addOutput(" (success) Inserted " + yukName + " with id: " + id );
				}	
			}

			// Not sure why this was here
			//pstmt.executeBatch();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			try {
				pstmt.close();
				conn.close();
		    } catch(Exception e2 ) { e2.printStackTrace(); throw e2; }
			
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
	
	
	public String getName()
	{
		return "Image Inserter";
	}

	public String getParamText()
	{
		return "Image Directory:";
	}

	public String getDefaultValue()
	{
		return CtiUtilities.USER_DIR;
	}

	public void run()
	{
		try
		{
			getIMessageFrame().addOutput("");
			getIMessageFrame().addOutput("-------- Started " + getName() + "..." );

			//Do the Stuff here
			insertImages( System.getProperty(IRunnableDBTool.PROP_VALUE) );
			
			getIMessageFrame().addOutput("-------- " + getName() + " Completed" );
			

			getIMessageFrame().finish( getName() + " Completed" );
		}
		catch( Exception e )
		{
			getIMessageFrame().addOutput("-------- " + getName() + " Completed with an EXCEPTION" );
			getIMessageFrame().finish( "Completed with an EXCEPTION" );
		}

	}
}