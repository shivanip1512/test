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
import com.cannontech.roles.yukon.BillingRole;
import com.cannontech.roles.application.CalcHistoricalRole;
import com.cannontech.roles.application.WebGraphRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.tools.gui.IRunnableDBTool;
import com.cannontech.user.UserUtils;

/**
 * Utility program to migrate to Yukon 3.0.
 * The following is done:
 *   - Copies all config.properties values into the correct Role property
 *   -
 * 
 * @author ryan
 */
public class Yukon3Migrator extends MessageFrameAdaptor
{
	private static final String CFG_FILE = "config.properties";
	
	//A list of property names AND their IDs that have been renamed since 3.0
	private static final Object[][] RENAMED_PROPS =
	{ 
		//<String>oldName, <Integer>propID
		{ "mail.smtp.host", new Integer(SystemRole.SMTP_HOST) },
		{ "mail.from.address", new Integer(SystemRole.MAIL_FROM_ADDRESS) },
		
		{ "webgraph_home_directory", new Integer(WebGraphRole.HOME_DIRECTORY) },		
		{ "webgraph_run_interval", new Integer(WebGraphRole.RUN_INTERVAL) },
		
		{ "billing_wiz_activate", new Integer(BillingRole.WIZ_ACTIVATE) },
		{ "billing_input_file", new Integer(BillingRole.INPUT_FILE) },
		
		{ "calc_historical_interval", new Integer(CalcHistoricalRole.INTERVAL) },
		{ "calc_historical_baseline_calctime", new Integer(CalcHistoricalRole.BASELINE_CALCTIME) },
		{ "calc_historical_daysprevioustocollect", new Integer(CalcHistoricalRole.DAYS_PREVIOUS_TO_COLLECT) }
	};

	
	public static void main(String[] args) 
	{
		if( args.length != 1 ) {
			System.out.println("Usage:  Yukon3Migrator dir");
			System.out.println("Where dir is a directory that contains the Old Yukon Configuration files");
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
			Properties fileProps = null;
	
			for( int i = 0; i < allFiles.length; i++ )
			{
				File f = allFiles[i];

				if( f.isFile()
					 && f.getAbsoluteFile().toString().toLowerCase().endsWith(CFG_FILE) )
				{
					InputStream is = new FileInputStream(f);

					try
					{
						fileProps = new Properties();
						fileProps.load(is);
					}
					catch (Exception e)
					{
						getIMessageFrame().addOutput(
							"Can't read the " + CFG_FILE + " file. " +
							"Make sure it is in the selected path" );
						
						fileProps = null;
					}
					
					break;	
				}
			}
					
			
			if( fileProps != null )
			{
				getIMessageFrame().addOutput("  Starting YUKON_USER properties");				
				YukonUser yukUser = updateYukonUserProps( fileProps );				
				getIMessageFrame().addOutput("");
				
				getIMessageFrame().addOutput("  Starting YUKON_GROUP properties");
				YukonGroup yukGrp = updateYukonGroupProps( fileProps );


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

	private YukonUser updateYukonUserProps( Properties fileProps ) throws Exception
	{
		if( fileProps == null )
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
					String propVal = fileProps.getProperty( lProp.getKeyName() );

					if( propVal == null )
						propVal = handleRenamedProps( lProp.getRolePropertyID(), fileProps );


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


	private YukonGroup updateYukonGroupProps( Properties fileProps ) throws Exception
	{
		if( fileProps == null )
			throw new IllegalArgumentException("Found no (null) old properties to exist");

		LiteYukonGroup yukGrp =
			AuthFuncs.getGroup( YukonGroupRoleDefs.GRP_YUKON );
			
		YukonGroup yukGrpPersist = 
				(YukonGroup)LiteFactory.createDBPersistent( yukGrp );
		
		//fill out the DB Persistent with data
		yukGrpPersist = 
			(YukonGroup)Transaction.createTransaction( 
						Transaction.RETRIEVE, yukGrpPersist ).execute();
		
//		
//		LiteYukonRoleProperty[] roleProps =
//				RoleFuncs.getRoleProperties( YukonRoleDefs.SYSTEM_ROLEID );

		List roleProps =
			DefaultDatabaseCache.getInstance().getAllYukonRoleProperties();

		for( int i = 0; i < roleProps.size(); i++ )
		{
			for( int j = 0; j < yukGrpPersist.getYukonGroupRoles().size(); j++ )
			{
				YukonGroupRole grpRole = 
					(YukonGroupRole)yukGrpPersist.getYukonGroupRoles().get(j);

				LiteYukonRoleProperty lProp = 
						(LiteYukonRoleProperty)roleProps.get(i);
			
				if( lProp.getRolePropertyID() == grpRole.getRolePropertyID().intValue() )
				{
					String propVal = fileProps.getProperty( lProp.getKeyName() );
					
					if( propVal == null )
						propVal = handleRenamedProps( lProp.getRolePropertyID(), fileProps );
					
					//we must have a valid value to assign it here
					if( propVal != null )
					{
						getIMessageFrame().addOutput(
							"   Update Pending:  " +
							lProp.getKeyName() + "=" + propVal );

						grpRole.setValue( propVal );
					}

					break;
				}

			}
			
		}
		
		
		//give back the DB object
		return yukGrpPersist; 
	}

	/**
	 * Checks to see if the given propID was renamed, if so, a property look up
	 * is done with the OldPropertyName as the key
	 * 
	 * @param propID
	 * @param fileProps
	 * @return old property value
	 */
	private String handleRenamedProps( int propID, final Properties fileProps )
	{

		for( int i = 0; i < RENAMED_PROPS.length; i++ )
		{
			int renamedPropID = ((Integer)RENAMED_PROPS[i][1]).intValue();
			if( renamedPropID == propID )
			{
				return fileProps.getProperty( RENAMED_PROPS[i][0].toString() );
			}
			
		}

		return null;
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
		return Double.parseDouble(
                    VersionTools.getDBVersionRefresh().getVersion() );				
	}

}