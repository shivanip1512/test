package com.cannontech.tools.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.YukonRoleDefs;
import com.cannontech.tools.gui.IRunnableDBTool;
import com.cannontech.user.UserUtils;

/**
 * Utility program to migrate to Yukon 3.00
 * 
 * @author ryan
 */
public class Yukon3Migrator extends MessageFrameAdaptor
{
	private static final String CFG_FILE = "config.properties";

	
	public static void main(String[] args) {
		if( args.length != 1 ) {
			System.out.println("Usage:  ImageInserter dir");
			System.out.println("Where dir is a directory");
			return;
		}
		
		Yukon3Migrator mi = new Yukon3Migrator();		
		System.setProperty( IRunnableDBTool.PROP_VALUE, args[0] );
		mi.run();
	}
	
	public void copyConfigPropsToDB(String dir) throws Exception 
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


		try 
		{			
			File[] allFiles = fDir.listFiles();
			Properties props = null;
	
			for( int i = 0; i < allFiles.length; i++ )
			{
				File f = allFiles[i];

				if( f.isFile()
					 && f.getAbsoluteFile().toString().toLowerCase().endsWith(CFG_FILE) )
				{
					InputStream is = new FileInputStream(f);

					try
					{
						props = new Properties();
						props.load(is);
					}
					catch (Exception e)
					{
						getIMessageFrame().addOutput(
							"Can't read the " + CFG_FILE + " file. " +
							"Make sure it is in the selected path" );
						
						props = null;
					}
					
					break;	
				}
			}
					
			
			if( props != null )
			{
				getIMessageFrame().addOutput("  Starting YUKON_USER properties");				
				YukonUser yukUser = updateYukonUserProps( props );				
				getIMessageFrame().addOutput("");
				
				getIMessageFrame().addOutput("  Starting YUKON_GROUP properties");
				YukonGroup yukGrp = updateYukonGroupProps( props );


				getIMessageFrame().addOutput(
					" Writting properties to the Database..." );

				Transaction.createTransaction(
						Transaction.UPDATE, yukUser ).execute();

				Transaction.createTransaction(
						Transaction.UPDATE, yukGrp ).execute();

				getIMessageFrame().addOutput(" (SUCCESS) Saved all found properties to the database");
			}		

		}
		catch(Exception e) 
		{
			getIMessageFrame().addOutput("An error occured when trying to process the properties" );
			e.printStackTrace();
			throw e;
		}

	}

//	d:\yukon\client\config\

	private YukonUser updateYukonUserProps( Properties oldProps ) throws Exception
	{
		if( oldProps == null )
			throw new IllegalArgumentException("Found no (null) old properties to exist");

		LiteYukonUser yukUser =
			YukonUserFuncs.getLiteYukonUser( UserUtils.USER_YUKON_ID );

		YukonUser yukUserPersist = 
				(YukonUser)LiteFactory.createDBPersistent( yukUser );
		
		//fill out the DB Persistent with data
		yukUserPersist = 
			(YukonUser)Transaction.createTransaction( 
						Transaction.RETRIEVE, yukUserPersist ).execute();
		
		List roleProps =
			DefaultDatabaseCache.getInstance().getAllYukonRoleProperties();

		for( int i = 0; i < roleProps.size(); i++ )
		{
			for( int j = 0; j < yukUserPersist.getYukonUserRoles().size(); j++ )
			{
				YukonUserRole usrRole =
					(YukonUserRole)yukUserPersist.getYukonUserRoles().get(j);
			
				LiteYukonRoleProperty lProp = (LiteYukonRoleProperty)roleProps.get(i);
				if( lProp.getRolePropertyID() == usrRole.getRolePropertyID().intValue() )
				{
					String propVal = oldProps.getProperty( lProp.getKeyName() );
					
					//we must have a valid value to assign it here
					if( propVal != null )
					{
						getIMessageFrame().addOutput(
							"   Update Pending:  " +
							lProp.getKeyName() + "=" + propVal );

						usrRole.setValue( propVal );
					}

					break;
				}

			}
			
		}


		//let the caller write this to the db
		return yukUserPersist;
	}


	private YukonGroup updateYukonGroupProps( Properties oldProps ) throws Exception
	{
		if( oldProps == null )
			throw new IllegalArgumentException("Found no (null) old properties to exist");

		LiteYukonGroup yukGrp =
			AuthFuncs.getGroup( YukonGroupRoleDefs.GRP_YUKON );
			
		YukonGroup yukGrpPersist = 
				(YukonGroup)LiteFactory.createDBPersistent( yukGrp );
		
		//fill out the DB Persistent with data
		yukGrpPersist = 
			(YukonGroup)Transaction.createTransaction( 
						Transaction.RETRIEVE, yukGrpPersist ).execute();
		
		
		LiteYukonRoleProperty[] roleProps =
				RoleFuncs.getRoleProperties( YukonRoleDefs.SYSTEM_ROLEID );
				
		for( int i = 0; i < roleProps.length; i++ )
		{
			for( int j = 0; j < yukGrpPersist.getYukonGroupRoles().size(); j++ )
			{
				YukonGroupRole grpRole = 
					(YukonGroupRole)yukGrpPersist.getYukonGroupRoles().get(j);
			
				if( roleProps[i].getRolePropertyID() == grpRole.getRolePropertyID().intValue() )
				{
					String propVal = oldProps.getProperty( roleProps[i].getKeyName() );
					
					//we must have a valid value to assign it here
					if( propVal != null )
					{
						getIMessageFrame().addOutput(
							"   Update Pending:  " +
							roleProps[i].getKeyName() + "=" + propVal );

						grpRole.setValue( propVal );
					}

					break;
				}

			}
			
		}
		
		
		//give back the DB object
		return yukGrpPersist; 
	}


	public String getName()
	{
		return "3.0 Migration";
	}

	public String getParamText()
	{
		return "Old Yukon Config Directory:";
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
			double dbVers = getDBVersion();
			
			getIMessageFrame().addOutput(
				" Current DB version: " + dbVers );
			
			if( dbVers >= 3.0 )
			{
				//Do the Stuff here
				copyConfigPropsToDB( System.getProperty(IRunnableDBTool.PROP_VALUE) );
				
				getIMessageFrame().addOutput("-------- " + getName() + " Completed" );
			}
			else
				getIMessageFrame().addOutput(
					" This tool is only valid for 3.0 or later databases");

			getIMessageFrame().finish( getName() + " Completed" );
		}
		catch( Exception e )
		{
			getIMessageFrame().addOutput("-------- " + getName() + " Completed with an EXCEPTION" );
			getIMessageFrame().finish( "Completed with an EXCEPTION" );
		}

	}
	
	private double getDBVersion()
	{
		CTIDatabase db = VersionTools.getDatabaseVersion();			

		return Double.parseDouble(db.getVersion());				
	}

}