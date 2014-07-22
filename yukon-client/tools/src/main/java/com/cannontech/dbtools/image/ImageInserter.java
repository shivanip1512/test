package com.cannontech.dbtools.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ImageFilter;
import com.cannontech.database.PoolManager;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.IRunnableDBTool;

/**
 * Utility program to insert an entire directory of images
 * into the database. This app will recurse through all directories
 * and insert any images found. The parent directory of an image becomes
 * the images Category.
 * 
 * Each image will be inserted into the StateImage table with
 * an ascending generated id.
 * @author alauinger
 */
public class ImageInserter extends MessageFrameAdaptor
{
	private Connection dbConn = null;
		

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
	
	private void insertImages(String dir) 
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


		PreparedStatement pstmt = null;
		File currFile = null;
		File[] allFiles = fDir.listFiles();
		ImageFilter filter = new ImageFilter();
		
		for( int i = 0; i < allFiles.length; i++ ) 
		{
			currFile = allFiles[i];
			if( currFile.isDirectory() ) 
			{
				insertImages( currFile.getAbsolutePath() );
			}
			else if( currFile.isFile() && filter.accept(currFile) ) 
			{
				try
				{
					String yukName = currFile.getName().substring(currFile.getName().indexOf('-')+1);
					int id = Integer.parseInt(currFile.getName().substring(0, currFile.getName().indexOf('-')));
					
				    insertImage(id, currFile.getParentFile().getName(), yukName, currFile);
	            
					getIMessageFrame().addOutput(" (success) Inserted " + yukName + " with id: " + id );
				}
				catch(StringIndexOutOfBoundsException se) {
					getIMessageFrame().addOutput(" Error on " + currFile.getName() + ": Ensure the image file name is in the following format:");
					getIMessageFrame().addOutput("   <id>-<name>.<extension> ");
					getIMessageFrame().addOutput("   example: 12-Breaker.gif");
					se.printStackTrace();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					try {
						if( pstmt != null ) pstmt.close();
					} catch(Exception e2 ) { e2.printStackTrace(); }
			
				}
				
			}	
		}
	}
	
	public void insertImage(int id, String category, String name, File file) throws SQLException, FileNotFoundException {
	    if (dbConn == null) {
            dbConn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	    }

	    PreparedStatement pstmt = null;
        try {
            long len = file.length();
            InputStream in = new FileInputStream(file);
            
            pstmt = dbConn.prepareStatement(INSERT_SQL);                    
            pstmt.setInt(1, id);
            pstmt.setString(2, file.getParentFile().getName());
            
            pstmt.setString(3, name);
            pstmt.setBinaryStream(4, in, (int) len);
            pstmt.execute();
        } finally {
            if( pstmt != null ) pstmt.close();
            if (dbConn != null) dbConn.close();
        }
        
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
		return CtiUtilities.CURRENT_DIR;
	}

	public void run()
	{
		try
		{
			getIMessageFrame().addOutput("");
			getIMessageFrame().addOutput("-------- Started " + getName() + "..." );


			if ( dbConn == null )
				dbConn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			//Do the Stuff here
			insertImages( System.getProperty(IRunnableDBTool.PROP_VALUE) );
			
			try {
				if( dbConn != null ) dbConn.close();
			} catch(Exception e2 ) { e2.printStackTrace(); }
			
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