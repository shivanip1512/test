package com.cannontech.datagenerator;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author rneuharth
 * Jul 31, 2002 at 1:41:21 PM
 * 
 * A undefined generated comment
 */
public class DataTools
{
	private Random rand = new Random();
	private static DecimalFormat SERIAL_FORM = new DecimalFormat("000000000");
	private static DecimalFormat DBL_FORM = new DecimalFormat("#.00");


	//local params shared by each sub-app
	private int intParam1 = -1;
	
	/**
	 * Constructor for DataTools.
	 */
	private DataTools()
	{
		super();
	}

   public static String createByteHexStream( java.io.File f ) throws java.io.IOException
   {
      long len = f.length();             
      java.io.InputStream in = new java.io.FileInputStream(f.getPath());
      StringBuffer buf = new StringBuffer( (int)len );

      byte[] b = new byte[2048];
      int n;
      while( (n = in.read(b, 0, b.length)) != -1) 
      {
         for( int j = 0; j < n; j++ )
         {
            String s = Integer.toHexString(b[j]);
            buf.append(
               (s.length() < 2 ? "0"+s :
                  (s.length() > 2 ? s.substring(s.length()-2,s.length()) : s)) );
         }
      }
      
//      System.out.println();
//      System.out.println("       Len = " + b.length );      
      
      return buf.toString();
   }
 
   private static void printUsage()
   {
		System.out.println("DataTools option [params]");
		System.out.println(" Options:");
		System.out.println("  -cbc  : create a CapControl database");
		System.out.println("          A route must exist for this operation to work");
		System.out.println("          For every sub 3 feeders and 12 Banks are created");
		System.out.println("");
		System.out.println("  -hx   : displays the hex stream for the given file(s)");
		System.out.println("");
		
		System.out.println("[params]");
		System.out.println("    -cbc : <RouteID> <Number_of_Subs>");
		System.out.println("    -hx  : <filepath1> <filepath2>...<filepathN>");
   }
 
   public static void main(String args[])
   {
      if( args.length <= 1 )
      {
         printUsage();
         return;
      }      

      if( "-cbc".equalsIgnoreCase(args[0]) && args.length >= 3 )
      {
      	DataTools dt = new DataTools();
      	dt.createCapControlDB(
      			Integer.parseInt(args[1]),    //prexisting routeID
				Integer.parseInt(args[2]) );  //Number of Subs
      }
      else if( "-hx".equalsIgnoreCase(args[0]) && args.length >= 2 )
      {
	      for( int i = 1; i < args.length; i++ )
	      {               
	         try
	         {
	            java.io.File f = new java.io.File(args[i]);
	            System.out.println( "0x" + DataTools.createByteHexStream(f) );
	         }
	         catch( Exception e )
	         {
	            e.printStackTrace( System.out );
	         }
	      }
      }
      else
      	printUsage();

   }
   
   
   public void createCapControlDB( int routeID, int subCount )
   {
   		intParam1 = routeID;
   		int subTotal = 1;
   		int feederTotal = 3; //per sub
   		int bankTotal = 4;  //per feeder
   		
		//int paoId = YukonPAObject.getNextYukonPAObjectID().intValue();
   		for( int i = 0; i < subCount; i++ )
   		{
   			Vector ccFeederVector = new Vector( feederTotal );
   			Vector ccBankVector = new Vector( bankTotal );
   			
   			int[] feederIDs = new int[ feederTotal ];
   			for( int j = 0; j < feederTotal; j++ )
   			{
   				int[] bankIDs = new int[ bankTotal ];
   				for( int k = 0; k < bankTotal; k++ )
   				{
   					SmartMultiDBPersistent sm = (SmartMultiDBPersistent)createCBCBank(
   							YukonPAObject.getNextYukonPAObjectID().intValue() );

   	   	   			writeToSQLDatabase( sm );
   	   	   			bankIDs[k] = ((CapBank)sm.getOwnerDBPersistent()).getPAObjectID().intValue();
   				}


   				feederIDs[j] = YukonPAObject.getNextYukonPAObjectID().intValue();
   				for( int x = 0; x < bankIDs.length; x++ )
   					ccBankVector.add( new CCFeederBankList(
   							new Integer(feederIDs[j]),
   							new Integer(bankIDs[x]),
							new Integer(x+1)) );

   	   			CapControlFeeder f = createCBCFeeder( feederIDs[j] );
   	   			f.setCcBankListVector( ccBankVector );
   	   			writeToSQLDatabase( f );
   	   			
   	   			ccBankVector.clear();
   			}
   			

	   			
   			CapControlSubBus s = createCBCSubBus( YukonPAObject.getNextYukonPAObjectID().intValue() );
   			for( int x = 0; x < feederIDs.length; x++ )
   				ccFeederVector.addElement( new CCFeederSubAssignment(
						  new Integer(feederIDs[x]),
						  s.getCapControlPAOID(),
						  new Integer(i+1)) );

   			s.setCcFeederListVector( ccFeederVector );
   			writeToSQLDatabase( s );
   			ccFeederVector.clear();
   		}
   	
   }
   
   private CapControlSubBus createCBCSubBus( int paoID )
   {
		CapControlSubBus ccSubBus = 
			(CapControlSubBus)CCYukonPAOFactory.createCapControlPAO( 
							CapControlTypes.CAP_CONTROL_SUBBUS );
		
		ccSubBus.setCapControlPAOID( new Integer(paoID) );
		ccSubBus.setName( "SubBus " + paoID );
		ccSubBus.setGeoAreaName( "Gen Area" );
		ccSubBus.getCapControlSubstationBus().setDaysOfWeek( new String("NYYYYYNN") );
		ccSubBus.setDisableFlag( new Character('N') );		
		
		ccSubBus.getCapControlSubstationBus().setCurrentVarLoadPointID( new Integer(0) );
		ccSubBus.getCapControlSubstationBus().setCurrentWattLoadPointID( new Integer(0) );

		ccSubBus.getCapControlSubstationBus().setPeakSetPoint(
				new Double(getRandDbl(80, 100)) );
		ccSubBus.getCapControlSubstationBus().setOffPeakSetPoint(
				new Double(getRandDbl(50, 60)) );

		//10 minutes
		ccSubBus.getCapControlSubstationBus().setMinResponseTime(
				new Integer(600));
				
		// 10am
		ccSubBus.getCapControlSubstationBus().setPeakStartTime(
				new Integer(36000) );
		// 6pm
		ccSubBus.getCapControlSubstationBus().setPeakStopTime(
				new Integer(64800) );

		return ccSubBus;
   }

   private CapControlFeeder createCBCFeeder( int paoID )
   {
		CapControlFeeder ccFeeder =
			(CapControlFeeder)CCYukonPAOFactory.createCapControlPAO( 
							CapControlTypes.CAP_CONTROL_FEEDER );
	
		ccFeeder.setCapControlPAOID( new Integer(paoID) );
		ccFeeder.setName( "Feeder " + paoID );
		ccFeeder.setDisableFlag( new Character('N') );
		ccFeeder.getCapControlFeeder().setCurrentVarLoadPointID( new Integer(0) );
		ccFeeder.getCapControlFeeder().setCurrentWattLoadPointID( new Integer(0) );

		ccFeeder.getCapControlFeeder().setPeakSetPoint(
				new Double(getRandDbl(40, 60)) );
		ccFeeder.getCapControlFeeder().setOffPeakSetPoint(
				new Double(getRandDbl(10, 30)) );
		
		return ccFeeder;
   }
   
   private SmartMultiDBPersistent createCBCController( 
		SmartMultiDBPersistent ogMulti, CapBank capBank, int serialNumber )
	{
   		DeviceBase newCBC = null;
		newCBC = DeviceFactory.createDevice(PAOGroups.CAPBANKCONTROLLER);
		
		if( newCBC instanceof ICapBankController )
		{
	   		ICapBankController cntrler = (ICapBankController)newCBC;
	   		cntrler.assignAddress( new Integer(serialNumber) );
	  		cntrler.setCommID( new Integer(intParam1) );
		}
		else
			throw new IllegalStateException("CBC class of: " + newCBC.getClass().getName() + " not found");

		//just use the serial number in the name
		newCBC.setPAOName( "CBC " + SERIAL_FORM.format(serialNumber) );

		//set the paoID
		newCBC.setDeviceID( YukonPAObject.getNextYukonPAObjectID() );
		

		//a status point is automatically added to all capbank controllers
		PointBase newPoint =
		   	CapBankController.createStatusControlPoint( 
		   			newCBC.getDevice().getDeviceID().intValue() );
		
		ogMulti.insertDBPersistentAt( newCBC, 0 );
		ogMulti.insertDBPersistentAt( newPoint, 1 );

		return ogMulti;
	}
   
   private SmartMultiDBPersistent createCBCBank( int paoID )
   {
		CapBank newCapBank = 
			(CapBank)DeviceFactory.createDevice(PAOGroups.CAPBANK);

		newCapBank.setPAOName( "CapBank " + paoID );
		newCapBank.setLocation( "Location " + paoID );
		
		//every 30 banks are fixed
		newCapBank.getCapBank().setOperationalState( 
				((paoID % 30) == 0 ? CapBank.FIXED_OPSTATE : CapBank.SWITCHED_OPSTATE) );
		
		//size between 600 ~ 1200
	   	newCapBank.getCapBank().setBankSize(
	   			new Integer(getRandInt(6, 12) * 100));
	   
		//add any objects that get created automatically
		SmartMultiDBPersistent smartMulti = new SmartMultiDBPersistent();

		//only create Status point if the capbank is Fixed
		if( newCapBank.getCapBank().getOperationalState().equalsIgnoreCase(CapBank.FIXED_OPSTATE) )
		{
			PointFactory.createBankStatusPt( smartMulti );
		}
		else
		{
			PointFactory.createBankStatusPt( smartMulti );
			PointFactory.createBankOpCntPoint( smartMulti );		
		}

		newCapBank.setDeviceID( new Integer(paoID) );
		smartMulti.addDBPersistent( newCapBank );

		//the capBank is the owner in this case
		smartMulti.setOwnerDBPersistent( newCapBank );

		//auto create a CBC Controllers for this Bank
		createCBCController( smartMulti, newCapBank, paoID );
		
		//Assign the CapBanks control IDs to that of the CBC status points pointID
		for( int i = 0; i < smartMulti.size(); i++ )
		{
			if( smartMulti.getDBPersistent(i) instanceof PointBase )
			{
				newCapBank.getCapBank().setControlPointID(
						((PointBase)smartMulti.getDBPersistent(i)).getPoint().getPointID() );

				newCapBank.getCapBank().setControlDeviceID(
						((PointBase)smartMulti.getDBPersistent(i)).getPoint().getPaoID() );
				break;
			}
		}

		return smartMulti;
   }

   public int getRandInt( int min, int max )
   {
   		int val = rand.nextInt ( (max - min) + 1 );
		return val + min;
   }

   public double getRandDbl( int min, int max )
   {
   		int val = getRandInt( min, max );
		return val + Double.parseDouble(DBL_FORM.format(rand.nextDouble()));
   }

   /**
    * Insert the method's description here.
    * Creation date: (3/31/2001 12:07:17 PM)
    * @param multi com.cannontech.database.data.multi.MultiDBPersistent
    */
   private boolean writeToSQLDatabase(DBPersistent db) 
   {
	   	try
	   	{
	         db = (DBPersistent)Transaction.createTransaction(
	                  Transaction.INSERT, 
	                  db).execute();
	     	
    		DBChangeMsg[] dbChange = 
    				DefaultDatabaseCache.getInstance().createDBChangeMessages(
    					(CTIDbChange)db, DBChangeMsg.CHANGE_TYPE_ADD );

    		for( int i = 0; i < dbChange.length; i++ )
    		{
    			//handle the DBChangeMsg locally
    			LiteBase lBase = 
    					DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
    		}
	     	
	   		return true;
	   	}
	   	catch( com.cannontech.database.TransactionException t )
	   	{
	   		CTILogger.error( t.getMessage(), t );
	   		return false;
	   	}
   }
}
