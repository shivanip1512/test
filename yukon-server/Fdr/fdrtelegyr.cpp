
#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
*
* File:   fdrtelegyr
*
* Date:   5/14/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
<<<<<<< fdrtelegyr.cpp
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2004/09/24 14:36:53 $
=======
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2004/09/24 14:36:53 $
>>>>>>> 1.7.12.2
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>
#include <rw/db/db.h>
#include <rw/db/connect.h>
#include <rw/db/status.h>

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "dbaccess.h"
#include "hashkey.h"
#include "resolvers.h"

#include "fdr.h"
#include "fdrdebuglevel.h"
#include "fdrinterface.h"

// this class header
#include "fdrtelegyr.h"

/** local definitions **/
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
#define FDR_TELEGYR_VERSION   "1.1.4 (compacted)"
=======
#define FDR_TELEGYR_VERSION   "1.1.8"
>>>>>>> 1.7.6.7
=======
#define FDR_TELEGYR_VERSION   "1.0"
>>>>>>> 1.9
#define PRIORITY              1         //only 1 is supported at this point

/** global used to start the interface by c functions **/
CtiFDRTelegyr * myInterface;

const CHAR * CtiFDRTelegyr::TBLNAME_TELEGYR_GROUPS       = "FDRTelegyrGroup";

const CHAR * CtiFDRTelegyr::KEY_APPLICATION_NAME         = "FDR_TELEGYR_APPLICATION_NAME";
const CHAR * CtiFDRTelegyr::KEY_API_VERSION              = "FDR_TELEGYR_API_VERSION";
const CHAR * CtiFDRTelegyr::KEY_HI_REASONABILITY_FILTER  = "FDR_TELEGYR_HI_REASONABILITY_FILTER";
const CHAR * CtiFDRTelegyr::KEY_DB_RELOAD_RATE           = "FDR_TELEGYR_DB_RELOAD_RATE";
const CHAR * CtiFDRTelegyr::KEY_API_PATH                 = "FDR_TELEGYR_API_PATH";
const CHAR * CtiFDRTelegyr::KEY_QUEUE_FLUSH_RATE         = "FDR_TELEGYR_QUEUE_FLUSH_RATE";
const CHAR * CtiFDRTelegyr::KEY_DEBUG_MODE               = "FDR_TELEGYR_DEBUG_MODE";
const CHAR * CtiFDRTelegyr::KEY_OPERATOR                 = "FDR_TELEGYR_OPERATOR";
const CHAR * CtiFDRTelegyr::KEY_PASSWORD                 = "FDR_TELEGYR_PASSWORD";
const CHAR * CtiFDRTelegyr::KEY_SYSTEM_NAME              = "FDR_TELEGYR_SYSTEM_NAME";
const CHAR * CtiFDRTelegyr::KEY_CHANNEL_ID               = "FDR_TELEGYR_CHANNEL_ID";
const CHAR * CtiFDRTelegyr::KEY_ACCESS                   = "FDR_TELEGYR_ACCESS";

const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_GROUPID      = "GroupID";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_NAME         = "GroupName";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_INTERVAL     = "CollectionInterval";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_TYPE         = "GroupType";



//=================================================================================================================================
// Constructor
//=================================================================================================================================
CtiFDRTelegyr::CtiFDRTelegyr() : CtiFDRInterface( RWCString( "TELEGYR" ) ) , _hiReasonabilityFilter( 0.0 )
{
   init();

   CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
   getReceiveFromList().setPointList( recList );

   recList = NULL;
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   _regFlag = false;
=======
>>>>>>> 1.7.12.2
=======
   _regFlag = false;
>>>>>>> 1.7.6.7
=======
	_regFlag = false;
>>>>>>> 1.9
}

//=================================================================================================================================
// Destructor
//=================================================================================================================================
CtiFDRTelegyr::~CtiFDRTelegyr()
{
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   delete getReceiveFromList().getPointList();
=======
>>>>>>> 1.7.12.2
=======
   delete getReceiveFromList().getPointList();
>>>>>>> 1.7.6.7
=======
	delete getReceiveFromList().getPointList();
>>>>>>> 1.9
}

//=================================================================================================================================
//=================================================================================================================================

BOOL CtiFDRTelegyr::init( void )
{
   RWCString   Description;
   RWCString   Action;
   BOOL        retVal = FALSE;
   int         status;

   setConnected( false );
   _numberOfConnections = 0;

   // init the base class
   if( !Inherited::init() )
   {
      retVal = FALSE;
   }
   else
   {
      if( !readConfig() )
      {
         retVal = FALSE;
      }

      //this loads the data from the FDR table and sorts it all out into chunks we can use
      loadTranslationLists();

      // create a TelegyrGetData thread object
      _threadGetTelegyrData = rwMakeThreadFunction( *this, &CtiFDRTelegyr::threadFunctionGetDataFromTelegyr );
   }
   return retVal;
}

//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::contact( int &status )
{
   setConnected( false );
   int index = 0;
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp

=======
//   int status = 0; //08/02

   //in the future, we may have more than one control center, so we'd loop through them all
   //for( index; index < _controlCenterList.size(); index++ )
   {
      setConnected( connect( index, status ) );
   }
=======
>>>>>>> 1.7.6.7

>>>>>>> 1.7.12.2
=======
      
>>>>>>> 1.9
   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << "  In contact() " << endl;
   }

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   connect( index, status );

=======
>>>>>>> 1.7.12.2
=======
   connect( index, status );
=======
	if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
	{
		CtiLockGuard<CtiLogger> doubt_guard( dout );
		dout << RWTime::now() << " DEBUG: --------------------------------------In contact, connecting " << endl;
	}
	connect( index, status );
>>>>>>> 1.9

>>>>>>> 1.7.6.7
   if( isConnected() )
   {
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " ... connected to api_client " << endl;
      }

      if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " DEBUG: --------------------------------------Am connected, deleting residue groups " << endl;
      }

      CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );
      deleteGroups();

      if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " DEBUG: --------------------------------------Am connected, re-registering with EMS " << endl;
      }
=======
      buildAndRegisterGroups();
>>>>>>> 1.7.12.2
=======
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " ... connected to api_client " << endl;
      }
=======
		if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
		{
			CtiLockGuard< CtiLogger > doubt_guard( dout );
			dout << RWTime::now() << " ... connected to api_client " << endl;
		}
		
		if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
		{
			CtiLockGuard<CtiLogger> doubt_guard( dout );
			dout << RWTime::now() << " DEBUG: --------------------------------------Am connected, deleting residue groups " << endl;
		}
>>>>>>> 1.9

		CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );
		deleteGroups();
      
<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
=======
		if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
		{
			CtiLockGuard<CtiLogger> doubt_guard( dout );
			dout << RWTime::now() << " DEBUG: --------------------------------------Am connected, re-registering with EMS " << endl;
		}
>>>>>>> 1.9
   }

   return( isConnected() );
}

//=================================================================================================================================
//we want to be able to connect to any number of Telegyr Control Centers (servers) from anywhere, so we just pass the index of the
//server we want and the method parses the info from a list of servers available
//=================================================================================================================================

bool CtiFDRTelegyr::connect( int centerNumber, int &status )
{
   char        *op;
   char        *pw;
   char        *sn;
   int         groupValid;
   int         sysType;

   op = new char[_controlCenter.getOperator().length()+1];
   strcpy( op, _controlCenter.getOperator().data() );

   pw = new char[_controlCenter.getPassword().length()+1];
   strcpy( pw, _controlCenter.getPassword().data() );

   sn = new char[_controlCenter.getSysName().length()+1];
   strcpy( sn, _controlCenter.getSysName().data() );

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " id " << _controlCenter.getChannelID() 
                              << " operator " << op 
                              << " password " << pw
                              << " system name " << sn
                              << " access " << _controlCenter.getAccess() 
                              << endl;
   }

   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " DEBUG: --------------------------------------calling API_CONNECT " << endl;
   }

=======
>>>>>>> 1.7.12.2
=======
   if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " id " << _controlCenter.getChannelID() 
                            << " operator " << op 
                            << " password " << pw
                            << " system name " << sn
                            << " access " << _controlCenter.getAccess() 
                            << endl;
   }

>>>>>>> 1.7.6.7
=======
	if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
	{
		CtiLockGuard< CtiLogger > doubt_guard( dout );
		dout << RWTime::now() << " id " << _controlCenter.getChannelID() <<
										" operator " << op << 
										" password " << pw <<
										" system name " << sn << 
										" access " << _controlCenter.getAccess() << endl;
	} 

	if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
	{
		CtiLockGuard<CtiLogger> doubt_guard( dout );
		dout << RWTime::now() << " DEBUG: --------------------------------------calling API_CONNECT " << endl;
	}
>>>>>>> 1.9
   //connect to the Telegyr server
   status = api_connect( _controlCenter.getChannelID(),
                         op,
                         pw,
                         sn,
                         _controlCenter.getAccess(),
                         &groupValid,
                         &sysType );

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " DEBUG: --------------------------------------returning from API_CONNECT " << endl;
   }

   delete [] op;
   delete [] pw;
   delete [] sn;

=======
>>>>>>> 1.7.12.2
=======
   delete [] op;
   delete [] pw;
   delete [] sn;
=======
	if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
	{
		CtiLockGuard<CtiLogger> doubt_guard( dout );
		dout << RWTime::now() << " DEBUG: --------------------------------------returning from API_CONNECT " << endl;
	}

	delete [] op;
	delete [] pw;
	delete [] sn;
>>>>>>> 1.9

>>>>>>> 1.7.6.7
   if( status != API_NORMAL )
   {
      setConnected( false );
   }
   else
   {
      setConnected( true );
      _numberOfConnections++;
   }

   return isConnected();
}

//=================================================================================================================================
//this is our loop for getting data
//we just look at the queue every second or so and look for messages
//the telegyr server should be putting data in that queue for us to read as we requested in the buildAndRegisterGroups() method
//=================================================================================================================================
void CtiFDRTelegyr::threadFunctionGetDataFromTelegyr( void )
{
	RWRunnableSelf    pSelf = rwRunnable();
	int               returnCode;
	int               status;
	bool              foundList;
	char              *newPath;
	char              *applicationName;
	char              *apiVer;
	int               timer;
	bool              startUp = true;

	//queue stuff
	int               reason;
	int               func_status;
	int               channel_id;
	int               group_type;
	int               group_num;
	APICLI_TIME       group_time;
	int               first_index;
	int               last_index;
	int               more;
	int               result[256];
	int               waiter = 15;	//03/28 - so we try to connect the first time through without waiting
	int               printCount;
	int 					disconCount=15;

	try
	{
		newPath = new char[getPath().length()+1];
		strcpy( newPath, getPath().data() );

		applicationName = new char[_appName.length()+1];
		strcpy( applicationName, _appName.data() );

		apiVer = new char[_apiVersion.length()+1];
		strcpy( apiVer, _apiVersion.data() );

		setLinkStatusID( getClientLinkStatusID( getInterfaceName() ) );

		if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
		{
			CtiLockGuard<CtiLogger> doubt_guard( dout );
			dout << RWTime::now() << " - Path To API:" << newPath << ":" << " Version: " << apiVer << " - "<< endl;
		}

		//init the Telegyr API
		status = api_init( newPath, applicationName, apiVer, PRIORITY );
		_inited = status;

		if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
		{
			CtiLockGuard<CtiLogger> doubt_guard( dout );
			dout << RWTime::now() <<" TelegyrAPI init() returned (code #) " << status << endl;
		}

		if( status == API_NORMAL )
		{
			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " DEBUG: --------------------------------------Init success, going to contact " << endl;
			}
			
			contact( status );		

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " DEBUG: -------------------------------------- Back from contact " << endl;
			}
		}

		for( ;; )
		{
			pSelf.sleep( 1000 );
			pSelf.serviceCancellation();
         
			CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );         

			pSelf.serviceCancellation();

			reason = 0;	  //just a test

			if( isConnected() != false )
			{
				if( timer >= 120 )
				{
					if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
					{
						CtiLockGuard<CtiLogger> doubt_guard( dout );
						dout << RWTime::now() << " - Valid Connection Exists To Telegyr - " << endl;
					}
					timer = 0;
				}

				timer++;

				//peek at the input-queue and see if anybody wrote to us from home...
				returnCode = api_get_queued_data( 0,
															 API_NO_TIMEOUT,
															 &reason,
															 &func_status,
															 &channel_id,
															 &group_type,
															 &group_num,
															 &group_time,
															 &first_index,
															 &last_index,
															 &more,
															 &result[0] );
				
				if( returnCode == API_NORMAL )
				{
					if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
					{
						CtiLockGuard<CtiLogger> doubt_guard( dout );
						dout << RWTime::now() << " Queued data returned grp_nbr " << group_num << " reason " << reason << endl;
					}
					
					foundList = false;

					switch( reason )
					{
					case API_DISC_NOTIFY:	//we've lost connection to the control center
						{  
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Disconnect detected: reason " << decipherReason( reason ) << " func_status " << func_status << endl;
							}

							if( func_status != APIERR_HSB_DISCONNECT )
							{
								int ret = api_disconnect( _controlCenter.getChannelID(), API_VALID );

								if( ret == API_NORMAL )
								{
									_numberOfConnections--;
									setConnected( false );

									CtiLockGuard<CtiLogger> doubt_guard( dout );
									dout << RWTime::now() << " - FDRTelegyr lost connection with Control Center - " << endl;
								}
								else
								{
									CtiLockGuard<CtiLogger> doubt_guard( dout );
									dout << RWTime::now() << " - api_disconnect returned (code #) " << ret << endl;
								}	 
								pSelf.sleep( 10000 );
							}
						}
						break;

					case API_CYC_DATA:		//we've got cyclic data on the queue
					case API_SPO_DATA:		//we've got data cause someone altered the server's database
						{
							int arraySize = 0;

							for( int x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
							{

								if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == group_num )
								{
									arraySize = _controlCenter.getTelegyrGroupList()[x].getPointList().size();
									break;
								}
							}

							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard(dout);

								if( reason == API_CYC_DATA )
									dout << RWTime::now() <<" - We're getting cyclic data for grouptype " << group_type << endl;
								else
									dout << RWTime::now() <<" - We're getting spontaneous data for grouptype "	<<	group_type << endl;
							}

							switch( group_type )
							{
							case API_GET_CYC_MEA:
							case API_GET_SPO_MEA:
								{
									APICLI_GET_MEA *measurands=NULL;
									measurands = new APICLI_GET_MEA[arraySize];

									if( measurands != NULL )
									{
										status = api_unpack_measurands( measurands );

										if( status == API_NORMAL )
										{
											//we might only get a few points back, so we'll have to adjust
											//for where they are on the FDR side of things during processing
											for( int index = 0; index < last_index - first_index + 1 ; index++ )
											{
												if( result[index] == API_NORMAL )
												{
													processAnalog( measurands[index], group_num, index );
												}
												else
												{
													processBadPoint( group_num, index );
												}
											}
										}
										else
										{
											CtiLockGuard<CtiLogger> doubt_guard(dout);
											dout << RWTime::now() << " TelegyrAPI api_unpack_measurands() failed: " << status << endl;
										}
										delete [] measurands;
									}
								}
								break;

							case API_GET_CYC_IND:
							case API_GET_SPO_IND:
								{
									APICLI_GET_IND *indications=NULL;
									indications = new APICLI_GET_IND[arraySize];

									if( indications != NULL )
									{
										status = api_unpack_indications( indications );

										if( status == API_NORMAL )
										{
											for( int index = 0; index < last_index-first_index+1 ; index++ )
											{
												if( result[index] == API_NORMAL )
												{
													processDigital( indications[index], group_num, index );
												}
												else
												{
													processBadPoint( group_num, index );
												}
											}
										}
										else
										{
											CtiLockGuard<CtiLogger> doubt_guard(dout);
											dout << RWTime::now() << " TelegyrAPI api_unpack_indications() failed: " << status << endl;
										}
										delete [] indications;
									}
								}
								break;

							case API_GET_CYC_CNT:
							case API_GET_SPO_CNT:
								{
									APICLI_GET_CNT *counters=NULL;
									counters = new APICLI_GET_CNT[arraySize];

									if( counters != NULL )
									{
										status = api_unpack_counter_values( counters );

										if( status == API_NORMAL )
										{
											for( int index = 0; index < last_index-first_index+1 ; index++ )
											{
												if( result[index] == API_NORMAL )
												{
													processCounter( counters[index], group_num, index );
												}
												else
												{
													processBadPoint( group_num, index );
												}
											}
										}
										else
										{
											{
												CtiLockGuard<CtiLogger> doubt_guard(dout);
												dout << RWTime::now() << " TelegyrAPI api_unpack_counter_values() failed: " << status << endl;
											}
										}
										delete [] counters;
									}
								}
								break;
							}
						}
						break;

					case API_DELALL_RES:		//we've done a global delete group call
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Call to delete all groups executed" << endl;
							}

							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " DEBUG: --------------------------------------Received a global delete all groups response" << endl;
							}


							// we know the old groups are now gone, build and register the new ones

							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " DEBUG: --------------------------------------Entering BuildReg " << endl;
							}

							buildAndRegisterGroups();
							_regFlag = true;

							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " DEBUG: --------------------------------------Leaving BuildReg " << endl;
							}
                  }
						break;


					case API_DISCYCALL_RES:		//we've done a call to disable cyclic
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Call to disable all cyclic data executed" << endl;
							}

							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " DEBUG: --------------------------------------Received a global disable cyclic" << endl;
							}
						}
						break;

					case API_DISSPOALL_RES:		//we've done a call to disable all spon data
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Call to disable all spontaneous data executed" << endl;
							}

							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " DEBUG: --------------------------------------Received a global disable spontaneous" << endl;
							}
						}
						break;
					
					case API_ENCYC_RES:		//we've done a call to enable cyclic data
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Call to enable cyclic data executed" << endl;
							}
						}
						break;

					case API_ENSPO_RES:		//we've done a call to enable spontaineous data
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Call to enable spontaneous data executed" << endl;
							}
						}
						break;

					case API_CRE_RES:		 //we've created a group
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Group creation attempted" << endl;
							}
						}
						break;

					default:					 //we don't know what the heck is going on....
						{
							if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " Unsupported message reason (code #): "<< reason << " " << decipherReason( reason ) << endl;
							}
						}
						break;
					}
				}
			}
			else
			{
				timer = 0;
				waiter++;								//we want to try to reconnect every 15 secs or so 
				disconCount++;

				if( disconCount >= 15 )
				{
					CtiLockGuard<CtiLogger> doubt_guard( dout );
					dout << RWTime::now() << " Telegyr disconnected from api_client " << endl;
					disconCount = 0;
				}
				
				if( waiter >= 15 )
				{
					if( _inited == API_NORMAL )
					{
						{
							CtiLockGuard<CtiLogger> doubt_guard( dout );
							dout << RWTime::now() << " Calling contact from main " << endl;
						}
						contact( status );				//NOTE:used this as it calls build&Register()
					}
					else
					{
						_inited = api_init( newPath, applicationName, apiVer, PRIORITY );
					}

					printCount++;							//we want to tell someone that we tried to reconnect every now and again
					waiter = 0;

					if( printCount >= 10 )
					{
						printCount = 0;

						if( !isConnected() )                             
						{
							if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " TelegyrAPI not connected (main)" << endl;
							}

							if( startUp == true )
							{
								startUp = false;
							}

							sendLinkState( FDR_NOT_CONNECTED );
						}
						else //successful reconnection
						{
							if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
							{
								CtiLockGuard<CtiLogger> doubt_guard( dout );
								dout << RWTime::now() << " TelegyrAPI connected is connected (main)" << endl;
							}

							if( startUp == true )
							{
								startUp = false;
							}

							sendLinkState( FDR_CONNECTED );
						}
					}
				}
			}
		}
		 
		delete [] newPath;
		delete [] applicationName;
		delete [] apiVer;
	}
	catch( RWCancellation &cancellationMsg )
	{
		return;
	}
}

//=================================================================================================================================
//here we are yanking data out of our telegyr group list and setting up groups that the foreign system will understand
//we should only be here if we've gotten past the api_init() and api_connect() methods built into telegyr
//=================================================================================================================================

bool CtiFDRTelegyr::buildAndRegisterGroups( void )
{
	int                  index;
	int                  count;
	int                  returnCode;
	int                  channel_id;
	int                  group_type;
	int                  group_number;
	int                  persistence;
	int                  object_count;
	char                 **name_list;
	int                  cycle_time;
	bool                 cyclic = false;
	int						totalGroups = _controlCenter.getTelegyrGroupList().size();

	for( index = 0; index < totalGroups; index++ )
	{
		cyclic = false;
		channel_id = _controlCenter.getChannelID();

		//if there's an interval, we have cyclic data
		if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
		{
			if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
			{
				group_type = API_GET_CYC_IND;
			}
			else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
			{
				group_type = API_GET_CYC_MEA;
			}
			else// COUNTER_TYPE
			{
				group_type = API_GET_CYC_CNT;
			}

			//this will always be in seconds
			cycle_time = _controlCenter.getTelegyrGroupList()[index].getInterval();
			cyclic = true;
		}
		else	 //if there's no interval, we get data when a point changes on the server
		{
			if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
			{
				group_type = API_GET_SPO_IND;
			}
			else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
			{
				group_type = API_GET_SPO_MEA;
			}
			else// COUNTER_TYPE
			{
				group_type = API_GET_SPO_CNT;
			}
		}

		group_number   = _controlCenter.getTelegyrGroupList()[index].getGroupID();
		persistence    = API_GRP_NO_PERSISTENCE;									//I think we'll just re-register anytime we need to
		object_count   = _controlCenter.getTelegyrGroupList()[index].getPointList().size();

		if( object_count != 0 )
		{
			//make enough pointers for our pointnames
			name_list = new char *[object_count];                    

			for( count = 0; count < object_count; count++ )
			{
				//makes some space to copy our pointnames
				name_list[count] = new char[200];
				strcpy( name_list[count], _controlCenter.getTelegyrGroupList()[index].getPointList()[count].getTranslateName( 0 ) );
			}

			//do the api-registration of the group...
			if( isConnected() == true )
			{	
				returnCode = api_create_group( channel_id, group_type, group_number, persistence, PRIORITY, object_count, name_list );
				
				if( returnCode != API_NORMAL )	  
				{
					CtiLockGuard<CtiLogger> doubt_guard( dout );
					dout << RWTime::now() << " Failed to create group number: " << group_number << endl;
				}
			}
			
			for( count = 0; count < object_count; count++ )
			{
				delete [] name_list[count];
			}
			delete [] name_list;
		}
		
		if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
		{
			CtiLockGuard<CtiLogger> doubt_guard( dout );
			dout << RWTime::now() << " Created Groups In BUILD&REGISTER: " << endl;
		}
	}

	if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
	{
		CtiLockGuard<CtiLogger> doubt_guard( dout );
		dout << RWTime::now() << " Deleted Our Namelist In BUILD&REGISTER: " << endl;
	}

	//enable all the groups
	for( index = 0; index < totalGroups; index++ )
	{
		cyclic = false;
		group_number = _controlCenter.getTelegyrGroupList()[index].getGroupID();
		channel_id = _controlCenter.getChannelID();

		//if there's an interval, we have cyclic data
		if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
		{
			if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
			{
				group_type = API_GET_CYC_IND;
			}
			else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
			{
				group_type = API_GET_CYC_MEA;
			}
			else//COUNTER_TYPE
			{
				group_type = API_GET_CYC_CNT;
			}

			//this will always be in seconds
			cycle_time = _controlCenter.getTelegyrGroupList()[index].getInterval();
			cyclic = true;
		}
		else	 //if there's no interval, we get data when a point changes on the server
		{
			if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
			{
				group_type = API_GET_SPO_IND;
			}
			else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
			{
				group_type = API_GET_SPO_MEA;
			}
			else//COUNTER_TYPE
			{
				group_type = API_GET_SPO_CNT;
			}
		}

		if( cyclic != false )
		{
			//by specifying the NOALIG parameter, we are telling the telegyr system to go ahead and start sending
			//data back to use immediately
			int code = api_enable_cyclic( channel_id, group_type, group_number, cycle_time, API_SECOND, API_ALIG_NOALIG );
				
			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " api_enable_cyclic returned " << code << endl;
			}
				
			cyclic = false;
		}
		else
		{
			int retCode = api_enable_spontaneous( channel_id, group_type, group_number );
			
			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " api_enable_spontaneous returned " << retCode<< endl;
			} 
		}
	}

	return( true );
}
 
//=================================================================================================================================
//we just have this in case anyone ever really wants to send a message, right now, it's not used....
//=================================================================================================================================

bool CtiFDRTelegyr::sendMessageToForeignSys( CtiMessage *aMessage )
{
   // message is deleted in fdrinterface thread
   return TRUE;
}

//=================================================================================================================================
//this is the receive message function that is our only concern for now
//here is where we'll wait for data to come back from the telegyr system after we've told it what we want
//=================================================================================================================================

INT CtiFDRTelegyr::processMessageFromForeignSystem( CHAR *data )
{
   return NORMAL;
}

//=================================================================================================================================
//can't tell the ems to delete the groups you've registered for unless we're connected to the client
//=================================================================================================================================

bool CtiFDRTelegyr::deleteGroups( void )
{
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   RWRunnableSelf    pSelf = rwRunnable();
   int               returnCode;
   int               status;
   bool              foundList;
   char              *newPath;
   char              *applicationName;
   char              *apiVer;
   int               timer;
   bool              startUp = true;

   //queue stuff
   int               func_status;
   int               channel_id;
   int               group_type;
   int               group_num;
   APICLI_TIME       group_time;
   int               first_index;
   int               last_index;
   int               more;
   int               result[256];
   int               waiter      = 10;   //03/28 - so we try to connect the first time through without waiting
   int               printCount;
   int               disconCount = 15;
   int               badMsgCount = 0;
=======
	if( isConnected() )
	{
		if( _controlCenter.getTelegyrGroupList().size() != 0 )
		{
			int status = api_disable_all_cyclic( _controlCenter.getChannelID() );

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " DEBUG --------------------------------------Disabled Cyclic " << status << endl;
			}

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard< CtiLogger > doubt_guard( dout );
				dout << " Disable-all-cyclic returns (code #): " << status << " on channel " << _controlCenter.getChannelID() << endl;
			}

			status = api_disable_all_spontaneous( _controlCenter.getChannelID() );

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " DEBUG --------------------------------------Disabled spontaneous " << status << endl;
			}

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard< CtiLogger > doubt_guard( dout );
				dout << " Disable-all-spontaneous returns (code #): " << status << " on channel " << _controlCenter.getChannelID() << endl;
			}

			status = api_delete_all_groups( _controlCenter.getChannelID() );

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " DEBUG --------------------------------------deleted all " << status << endl;
			}

			if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
			{
				CtiLockGuard< CtiLogger > doubt_guard( dout );
				dout << " Delete-all-groups returns (code #): " << status << " on channel " << _controlCenter.getChannelID() <<  endl;
			}
		}
	}
>>>>>>> 1.9

	return( false );
}

//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::loadTranslationLists()
{
	bool	retCode = true;

	CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

	if( getGroupLists() != false )
	{
		//note: these are our internal lists, Telegyr doesn't know about them yet
		retCode = loadLists( getReceiveFromList() );

		if( retCode != true )
		{
			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime::now() << " Translation list load for FDRTelegyr failed " << endl;
			}
		}
	}
		
	return( retCode );
}

//=================================================================================================================================
// we're loading up the FDRTelegyrGroup table in the event of a first load or a reload
//=================================================================================================================================

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
         CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );         

         pSelf.serviceCancellation();
         
         int reason = 0;   //just a test

=======
         CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );         
=======
bool CtiFDRTelegyr::getGroupLists( void )
{
   RWCString      Description;
   RWCString      Action;
   bool           gotList = false;
   RWCString      tempGroupName;
   int            tempGroupID;
   RWCString      tempGroupType;
   int            tempInterval;
   RWDBStatus     retStatus;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
         if( isConnected() != false )
         {
            if( timer >= 120 )
            {
<<<<<<< fdrtelegyr.cpp
               if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << RWTime::now() << " - Valid Connection Exists To Telegyr - " << endl;
               }
=======
               if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << RWTime::now() << " - Valid Connection Exists To Telegyr - " << endl;
               }
=======
   if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << " ***** We're getting the GROUPLISTS ***** " << endl;
   }
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
               timer = 0;
            }
=======
   try
   {
      // Make sure all objects that that store results
      CtiLockGuard<CtiSemaphore> cg( gDBAccessSema );
      RWDBConnection conn = getConnection();
>>>>>>> 1.9

      // are out of scope when the release is called

<<<<<<< fdrtelegyr.cpp
            //peek at the input-queue and see if anybody wrote to us from home...
<<<<<<< fdrtelegyr.cpp
            returnCode = -1;
=======
            int reason = -1;
            returnCode = -1;
>>>>>>> 1.7.6.7
            returnCode = api_get_queued_data( 0,
                                              API_NO_TIMEOUT,
                                              &reason,
                                              &func_status,
                                              &channel_id,
                                              &group_type,
                                              &group_num,
                                              &group_time,
                                              &first_index,
                                              &last_index,
                                              &more,
                                              &result[0] );
=======
      RWDBDatabase db = conn.database();
      RWDBTable fdrTelegyrGroups = db.table( TBLNAME_TELEGYR_GROUPS );
      RWDBSelector selector = db.selector();
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
            if( returnCode == API_NORMAL )
            {
               if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
<<<<<<< fdrtelegyr.cpp
                  dout << RWTime::now() << " Queued data returned grp_nbr " << group_num << " reason " << reason << endl;
=======
                  dout << RWTime::now() << " Queued data returned grp_nbr " << group_num << " reason " << decipherReason( reason ) << endl;
>>>>>>> 1.7.6.7
               }

               foundList = false;

               switch( reason )
               {
               case API_DISC_NOTIFY:   //we've lost connection to the control center
                  {  
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Disconnect detected: reason " << decipherReason( reason ) << " func_status " << func_status << endl;
                     }

                     if( func_status == APIERR_HSB_DISCONNECT )
                     {
                        //just delete and re-register the groups
                        _regFlag = false;
                        deleteGroups();
                        {
                           CtiLockGuard<CtiLogger> doubt_guard( dout );
                           dout << RWTime::now() << " - FDRTelegyr lost connection with Hot Standby Host - " << endl;
                        }
                     }
                     else //we lost all/primary connections - start over
                     {
                        int ret = api_disconnect( _controlCenter.getChannelID(), API_VALID );

                        if( ret == API_NORMAL )
                        {
                           _numberOfConnections--;
                           setConnected( false );
                           {
                              CtiLockGuard<CtiLogger> doubt_guard( dout );
                              dout << RWTime::now() << " - FDRTelegyr lost connection with Control Center - " << endl;
                           }
                        }
                        else
                        {
                           CtiLockGuard<CtiLogger> doubt_guard( dout );
                           dout << RWTime::now() << " - api_disconnect returned (code #) " << ret << endl;
                        }   
                        pSelf.sleep( 10000 );
                     }
                  }
                  break;
=======
      selector
			<< fdrTelegyrGroups[COLNAME_TELEGYR_GROUPID]
			<< fdrTelegyrGroups[COLNAME_TELEGYR_NAME]
			<< fdrTelegyrGroups[COLNAME_TELEGYR_INTERVAL]
			<< fdrTelegyrGroups[COLNAME_TELEGYR_TYPE];
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
               case API_CYC_DATA:      //we've got cyclic data on the queue
               case API_SPO_DATA:      //we've got data cause someone altered the server's database
                  {
                     int arraySize = 0;

                     for( int x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
                     {
                        if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == group_num )
                        {
                           arraySize = _controlCenter.getTelegyrGroupList()[x].getPointList().size();
                           break;
                        }
                     }

                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);

                        if( reason == API_CYC_DATA )
                           dout << RWTime::now() <<" - We're getting cyclic data for grouptype " << group_type << endl;
                        else
                           dout << RWTime::now() <<" - We're getting spontaneous data for grouptype " << group_type << endl;
                     }

                     switch( group_type )
                     {
                     case API_GET_CYC_MEA:
                     case API_GET_SPO_MEA:
                        {
                           APICLI_GET_MEA *measurands=NULL;
                           measurands = new APICLI_GET_MEA[arraySize];

                           if( measurands != NULL )
                           {
                              status = api_unpack_measurands( measurands );

                              if( status == API_NORMAL )
                              {
                                 //we might only get a few points back, so we'll have to adjust
                                 //for where they are on the FDR side of things during processing
                                 for( int index = 0; index < last_index - first_index + 1 ; index++ )
                                 {
<<<<<<< fdrtelegyr.cpp
                                    if( result[index] == API_NORMAL )
                                    {
                                       processAnalog( measurands[index], group_num, index );
                                    }
                                    else
                                    {
                                       processBadPoint( group_num, index );
                                    }
=======
                                    if( result[index] == API_NORMAL )
                                    {
                                       processAnalog( measurands[index], group_num, index );
                                    }
                                    else
                                    {
                                       if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                                       {
                                          CtiLockGuard<CtiLogger> doubt_guard(dout);
                                          dout << RWTime::now() << " result " << result[index] << endl;
                                       }
                                       processBadPoint( group_num, index );
                                    }
>>>>>>> 1.7.6.7
                                 }
                              }
                              else
                              {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime::now() << " TelegyrAPI api_unpack_measurands() failed: " << status << endl;
                              }
                              delete [] measurands;
                           }
                        }
                        break;

                     case API_GET_CYC_IND:
                     case API_GET_SPO_IND:
                        {
                           APICLI_GET_IND *indications=NULL;
                           indications = new APICLI_GET_IND[arraySize];

                           if( indications != NULL )
                           {
                              status = api_unpack_indications( indications );

                              if( status == API_NORMAL )
                              {
                                 for( int index = 0; index < last_index-first_index+1 ; index++ )
                                 {
<<<<<<< fdrtelegyr.cpp
                                    if( result[index] == API_NORMAL )
                                    {
                                       processDigital( indications[index], group_num, index );
                                    }
                                    else
                                    {
                                       processBadPoint( group_num, index );
                                    }
=======
                                    if( result[index] == API_NORMAL )
                                    {
                                       processDigital( indications[index], group_num, index );
                                    }
                                    else
                                    {
                                       if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                                       {
                                          CtiLockGuard<CtiLogger> doubt_guard(dout);
                                          dout << RWTime::now() << " result " << result[index] << endl;
                                       }
                                       processBadPoint( group_num, index );
                                    }
>>>>>>> 1.7.6.7
                                 }
                              }
                              else
                              {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime::now() << " TelegyrAPI api_unpack_indications() failed: " << status << endl;
                              }
                              delete [] indications;
                           }
                        }
                        break;

                     case API_GET_CYC_CNT:
                     case API_GET_SPO_CNT:
                        {
                           APICLI_GET_CNT *counters=NULL;
                           counters = new APICLI_GET_CNT[arraySize];

                           if( counters != NULL )
                           {
                              status = api_unpack_counter_values( counters );

                              if( status == API_NORMAL )
                              {
                                 for( int index = 0; index < last_index-first_index+1 ; index++ )
                                 {
<<<<<<< fdrtelegyr.cpp
                                    if( result[index] == API_NORMAL )
                                    {
                                       processCounter( counters[index], group_num, index );
                                    }
                                    else
                                    {
                                       processBadPoint( group_num, index );
                                    }
=======
                                    if( result[index] == API_NORMAL )
                                    {
                                       processCounter( counters[index], group_num, index );
                                    }
                                    else
                                    {
                                       if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                                       {
                                          CtiLockGuard<CtiLogger> doubt_guard(dout);
                                          dout << RWTime::now() << " result " << result[index] << endl;
                                       }
                                       processBadPoint( group_num, index );
                                    }
>>>>>>> 1.7.6.7
                                 }
                              }
                              else
                              {
<<<<<<< fdrtelegyr.cpp
                                 {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime::now() << " TelegyrAPI api_unpack_counter_values() failed: " << status << endl;
                                 }
=======
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime::now() << " TelegyrAPI api_unpack_counter_values() failed: " << status << endl;
>>>>>>> 1.7.6.7
                              }
                              delete [] counters;
                           }
                        }
                        break;
                     }
<<<<<<< fdrtelegyr.cpp
                  }
                  break;

               case API_DELALL_RES:    //we've done a global delete group call
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to delete all groups executed" << endl;
                     }
=======
                  }
                  break;
=======
      selector.from( fdrTelegyrGroups );
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
               case API_DELALL_RES:    //we've done a global delete group call
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Calling to delete groups..." << endl;
                     }

                     // we know the old groups are now gone, build and register the new ones
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
                     // we know the old groups are now gone, build and register the new ones
=======
                     buildAndRegisterGroups();
                     _regFlag = true;
>>>>>>> 1.7.6.7

                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
<<<<<<< fdrtelegyr.cpp
                        dout << RWTime::now() << " DEBUG: --------------------------------------Entering BuildReg " << endl;
=======
                        dout << RWTime::now() << " ...finished " << endl;
>>>>>>> 1.7.6.7
                     }
                  }
                  break;

<<<<<<< fdrtelegyr.cpp
                     buildAndRegisterGroups();
                     _regFlag = true;

                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " DEBUG: --------------------------------------Leaving BuildReg " << endl;
                     }
=======
               case API_DISCYCALL_RES:    //we've done a call to disable cyclic
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to disable all cyclic data executed" << endl;
                     }
>>>>>>> 1.7.6.7
                  }
<<<<<<< fdrtelegyr.cpp
                  break;
=======
      // check execute status to stop the exception when database is gone
      retStatus = selector.execute( conn ).status();
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
               case API_DISCYCALL_RES:    //we've done a call to disable cyclic
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to disable all cyclic data executed" << endl;
                     }
                  }
=======
>>>>>>> 1.7.6.7
                  break;
=======
      if( retStatus.errorCode() == RWDBStatus::ok )
      {
         if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " retStatus.errorCode() == RWDBStatus::ok " << endl;
         }
>>>>>>> 1.9


			//if we've gotten an ack from the ems, we can go ahead
			if( _regFlag )
			{
				// delete FDRpoint vectors, then delete the group list vector
				if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
				{
					CtiLockGuard<CtiLogger> doubt_guard( dout );
					dout << RWTime::now() << " DEBUG --------------------------------------deleting groups on EMS " << endl;
				}
				
				deleteGroups();
				
				if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
				{
					CtiLockGuard<CtiLogger> doubt_guard( dout );
					dout << RWTime::now() << " DEBUG --------------------------------------done deleting groups on EMS" << endl;
				}
			}
			
			//delete our local list 
			_controlCenter.deleteTelegyrGroupList();
         
			RWDBReader  rdr = selector.reader( conn );
         CtiTelegyrGroup tempGroup;

         while( rdr() )
         {
            //put all the new values into variables
            rdr[COLNAME_TELEGYR_GROUPID]    >> tempGroupID;
            rdr[COLNAME_TELEGYR_NAME]       >> tempGroupName;
            rdr[COLNAME_TELEGYR_INTERVAL]   >> tempInterval;
            rdr[COLNAME_TELEGYR_TYPE]       >> tempGroupType;

            //add the values to a new grouplist
            tempGroup.setGroupID( tempGroupID );
            tempGroup.setGroupName( tempGroupName );
            tempGroup.setInterval( tempInterval );
            tempGroup.setGroupType( tempGroupType );
            
				//plunk the grouplist into the vector
            _controlCenter.addToGroupList( tempGroup );
             
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << endl;
               dout << endl;
               dout << RWTime::now() << " - Group added with Groupname: " << tempGroupName << " Grouptype: " << tempGroupType << " - " << endl;
               dout << "TempGroupID " << tempGroupID << endl;
               dout << "TempGroupName " << tempGroupName << endl;
               dout << "TempInterval " << tempInterval << endl;
               dout << "TempGroupType " << tempGroupType << endl;
               dout << endl;
               dout << endl;
            }
         }
         gotList = true;
      }
      else
      {
         if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " retStatus.errorCode() != RWDBStatus::ok " << endl;
         }
      }
   }
   catch( RWExternalErr e )
   {
      RWTHROW(e);
   }
   catch( ... )
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
      }
   }

   return gotList;
}

//=================================================================================================================================
//get point info from a database load/reload
//=================================================================================================================================

bool CtiFDRTelegyr::loadLists( CtiFDRPointList &aList )
{
   bool                successful( FALSE );
   int                 analogCount = 0;
   int                 statusCount = 0;
   CtiFDRPoint         *translationPoint = NULL;
   RWCString           myTranslateName;
   RWCString           tempString1;
   RWCString           pointStr;
   RWCString           groupStr;
   RWCString           newTranslationString;
   bool                foundPoint = false;
   RWDBStatus          listStatus;


<<<<<<< fdrtelegyr.cpp
            if( waiter >= 15 )
            {
               if( _inited == API_NORMAL )
               {
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " Calling contact from main " << endl;
                  }
                  contact( status );            //NOTE:used this as it calls build&Register()
               }
               else
               {
                  _inited = api_init( newPath, applicationName, apiVer, PRIORITY );
               }

               printCount++;                    //we want to tell someone that we tried to reconnect every now and again
               waiter = 0;

               if( printCount >= 10 )
               {
                  printCount = 0;

                  if( !isConnected() )
                  {
<<<<<<< fdrtelegyr.cpp
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
=======
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " TelegyrAPI not connected (main)" << endl;
                     }

                     if( startUp == true )
>>>>>>> 1.7.6.7
                     {
<<<<<<< fdrtelegyr.cpp
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " TelegyrAPI not connected (main)" << endl;
=======
                        startUp = false;
>>>>>>> 1.7.6.7
                     }
<<<<<<< fdrtelegyr.cpp
=======
=======
   try
   {
      // make a list with all received points
      CtiFDRManager *pointList = new CtiFDRManager( getInterfaceName(), RWCString( FDR_INTERFACE_RECEIVE ) );

      listStatus = pointList->loadPointList();
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
                     sendLinkState( FDR_NOT_CONNECTED );
                  }
                  else //successful reconnection
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " TelegyrAPI is connected (main)" << endl;
                     }
>>>>>>> 1.7.6.7

                     if( startUp == true )
                     {
                        startUp = false;
                     }
=======
      // if status is ok, we were able to read the database at least
      if ( listStatus.errorCode() == (RWDBStatus::ok))
      {
          /**************************************
          * seeing occasional problems where we get empty data sets back
          * and there should be info in them,  we're checking this to see if
          * is reasonable if the list may now be empty
          * the 2 entry thing is completly arbitrary
          ***************************************
          */
          if (((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) || (pointList->entries() > 0))
          {
              // get iterator on list
              CtiFDRManager::CTIFdrPointIterator myIterator( pointList->getMap() );
              int x;

              //iterate through all our points in the list
              for( ; myIterator(); )
              {
                 foundPoint = true;
                 translationPoint = myIterator.value();

                 //iterate through all our destinations per point (should have 1 for telegyr)
                 for( x = 0; x < translationPoint->getDestinationList().size(); x++ )
                 {
                    // there should only be one destination for telegyr points
                    RWCTokenizer nextTranslate( translationPoint->getDestinationList()[x].getTranslation() );

                    //note: we're making a brand new token (string) out of the data before the ';'
                    if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                    {
                       // this in the form of POINTID:xxxx
                       RWCTokenizer nextTempToken( tempString1 );

                       // do not care about the first part so toss it
                       nextTempToken( ":" );

                       //now we find the end of the string
                       RWCString pointStr1 = nextTempToken(":");
                       RWCString pointStr2=nextTempToken(":");

                       pointStr = pointStr1 + RWCString(":") + pointStr2;

                       // now we have a pointid with a :
                       if( !pointStr.isNull() )
                       {
                          successful = true;

                          if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                          {
                             CtiLockGuard<CtiLogger> doubt_guard( dout );
                             dout << RWTime::now() << " - Point with Pointid: " << pointStr << " found - " << endl;
                          }
                       }
                    }

                    //note: we're making a brand new token (string) out of the data before the ';'
                    if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                    {
                       // this in the form of GROUP:xxxx
                       RWCTokenizer nextTempToken( tempString1 );

                       // do not care about the first part so toss it
                       nextTempToken( ":" );

                       //now we find the end of the string
                       groupStr = nextTempToken(":");

                       // now we have a groupid with a ':'
                       if( !groupStr.isNull() )
                       {
                          successful = true;

                          if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                          {
                             CtiLockGuard<CtiLogger> doubt_guard( dout );
                             dout << RWTime::now() << " - Point with Group: " << groupStr << " found - " << endl;
                          }
                       }
                    }

                    translationPoint->getDestinationList()[x].setTranslation( pointStr );
                 }

                 bool found = false;

                 //we need to spin through the list
                 for( int i = 0; i <  _controlCenter.getTelegyrGroupList().size(); i++ )
                 {
                    //and add our FDRPoints to the groups
                    if( _controlCenter.getTelegyrGroupList()[i].getGroupName() == groupStr )
                    {
                       _controlCenter.getTelegyrGroupList()[i].getPointList().push_back( *translationPoint );
                    }
                 }
              }

				  delete pointList;	//????
              pointList=NULL;

              if( !successful )
              {
                 if( !foundPoint )
                 {
                    // means there was nothing in the list, wait until next db change or reload
                    successful = true;
                 }
              }
          }
          else
          {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
              successful = false;
          }
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
                     sendLinkState( FDR_NOT_CONNECTED );
=======
   RWRunnableSelf    pSelf = rwRunnable();
   RWCString         Description;
   RWCString         Action;
   RWTime            eventTime;
   ULONG             nextScanTime;
   time_t            timeNow;
   int               returnCode;
   int               status;
   bool              foundList;
   char              *newPath;
   char              *applicationName;
   char              *apiVer;
   int               timer;
   bool              startUp = true;

   //queue stuff
   int               reason;
   int               func_status;
   int               channel_id;
   int               group_type;
   int               group_num;
   APICLI_TIME       group_time;
   int               first_index;
   int               last_index;
   int               more;
   int               result;
   int               waiter;
   int               printCount;

   try
=======
                     sendLinkState( FDR_CONNECTED );
                  }
               }
            }
         }
=======
>>>>>>> 1.9
      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
         successful = false;
      }
   }
<<<<<<< fdrtelegyr.cpp
   catch( RWCancellation &cancellationMsg )
>>>>>>> 1.7.6.7
=======
   catch( RWExternalErr e )
>>>>>>> 1.9
   {
<<<<<<< fdrtelegyr.cpp
=======
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << "loadTranslationList():  " << e.why() << endl;
      RWTHROW( e );
   }
   return successful;
}
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
      newPath = new char[getPath().length()+1];
      strcpy( newPath, getPath().data() );

<<<<<<< fdrtelegyr.cpp
      applicationName = new char[_appName.length()+1];
      strcpy( applicationName, _appName.data() );
=======
//=================================================================================================================================
//we want to decipher the point and put it on the queue to Dispatch
//=================================================================================================================================
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
      apiVer = new char[_apiVersion.length()+1];
      strcpy( apiVer, _apiVersion.data() );
=======
bool CtiFDRTelegyr::buildAndRegisterGroups( void )
=======
bool CtiFDRTelegyr::processAnalog( APICLI_GET_MEA aPoint, int groupid, int index )
>>>>>>> 1.9
{
<<<<<<< fdrtelegyr.cpp
   int                  index;
   int                  count;
   int                  returnCode;
   int                  channel_id;
   int                  group_type;
   int                  group_number;
   int                  persistence;
   int                  object_count;
   char                 **name_list;
   int                  cycle_time;
   bool                 cyclic = false;
   int                  totalGroups = _controlCenter.getTelegyrGroupList().size();
   bool                 *created = new bool[totalGroups];
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
=======
   for( index = 0; index < totalGroups; index++ )
   {
      cyclic = false;
      cycle_time = 0;
      channel_id = _controlCenter.getChannelID();
>>>>>>> 1.7.6.7
=======
   CtiPointDataMsg      *pData      = NULL;
   CtiCommandMsg        *pCmdMsg    = NULL;
   CtiFDRPoint          *point      = NULL;
   long                 pointid;
   bool                 returnCode;
   int                  x;
   double               value;
	double               raw;
   USHORT               quality;
   bool                 nonUpdated = false;

   if( aPoint.mea_valid == API_VALID )
   {
      value = aPoint.mea_value.mea4_value;
		raw = value;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
      if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
=======
      RWCString type = _controlCenter.getTelegyrGroupList()[index].getGroupType();
      type.toLower();
=======
      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
>>>>>>> 1.7.6.7
=======
      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
>>>>>>> 1.9
      {
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " - Path To API:" << newPath << ":" << " Version: " << apiVer << " - "<< endl;
      }

      //init the Telegyr API
      status = api_init( newPath, applicationName, apiVer, PRIORITY );
=======
         if(( type == STATUS_TYPE ) || ( type == DIGITAL_TYPE ))
=======
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
>>>>>>> 1.9
         {
<<<<<<< fdrtelegyr.cpp
            group_type = API_GET_CYC_IND;
>>>>>>> 1.7.6.7
=======
            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();

            _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointType();
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
      if( status != API_NORMAL )
      {
         delete newPath;
         delete applicationName;
         delete apiVer;
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found a status group w/interval" << endl;
            }
         }
         else if( type == ANALOG_TYPE )
         {
            group_type = API_GET_CYC_MEA;
>>>>>>> 1.7.6.7
=======
            value = value * ( _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getMultiplier() );
            value += ( _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getOffset() );
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
         //log the event and put in the status (code returned)
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() <<" TelegyrAPI INIT failed. Status returned " << decipherError( status ) << endl;
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found an analog group w/interval" << endl;
            }
         }
         else if( type == COUNTER_TYPE )
         {
            group_type = API_GET_CYC_CNT;
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
         Description = " TelegyrAPI INIT failed ";
         logEvent( Description, Action, false );
      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() <<" TelegyrAPI init successful " << endl;
=======
				if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
				{
					CtiLockGuard<CtiLogger> doubt_guard( dout );
					dout << RWTime() << " Raw Analog Value: " << raw << " from Point " << _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0) << " groupid " << groupid << endl;
				}
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
         contact( status );
      }
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
=======
            //if the value is higher than it should be, we want to let the boss man know
            if( ( getHiReasonabilityFilter() > 0 ) && ( getHiReasonabilityFilter() > value ) )
>>>>>>> 1.9
            {
               nonUpdated = true;
            }
            break;
         }
<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
=======
      }
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
      for( ;; )
=======
      //get the quality of the point data
      if( !api_check_any_quality_bit_set( aPoint.sys_dependent_info ) )
      {
         quality = NormalQuality;
      }
      else
>>>>>>> 1.9
      {
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
         //  while i'm not getting anything
         pSelf.serviceCancellation();
         pSelf.sleep( 1000 );

         if( isConnected() != false )
=======
         if(( type == STATUS_TYPE ) || ( type == DIGITAL_TYPE ))
>>>>>>> 1.7.6.7
=======
         //there are a bunch more that telegyr supports, we just don't care
         if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_OUT_SCAN_BIT ) )
>>>>>>> 1.9
         {
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
            if( timer >= 120 )
=======
            group_type = API_GET_SPO_IND;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
>>>>>>> 1.7.6.7
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
<<<<<<< fdrtelegyr.cpp
               dout << RWTime::now() << " - Valid Connection Exists To Telegyr - " << endl;
               timer = 0;
=======
               dout << RWTime::now() << " Found a status group w/o interval" << endl;
>>>>>>> 1.7.6.7
            }
<<<<<<< fdrtelegyr.cpp
=======
=======
            quality = NonUpdatedQuality;
>>>>>>> 1.9
         }
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TELEM_F_BIT ) )
         {
<<<<<<< fdrtelegyr.cpp
            group_type = API_GET_SPO_MEA;
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
            timer++;

            //peek at the input-queue and see if anybody wrote to us from home...
            returnCode = api_get_queued_data( 0,
                                              API_NO_TIMEOUT,
                                              &reason,
                                              &func_status,
                                              &channel_id,
                                              &group_type,
                                              &group_num,
                                              &group_time,
                                              &first_index,
                                              &last_index,
                                              &more,
                                              &result );

            if( returnCode == API_NORMAL )
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
>>>>>>> 1.7.6.7
            {
<<<<<<< fdrtelegyr.cpp
               foundList = false;
=======
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found an analog group w/o interval" << endl;
            }
         }
         else if( type == COUNTER_TYPE )
         {
            group_type = API_GET_SPO_CNT;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found a counter group w/o interval" << endl;
            }
=======
            quality = NonUpdatedQuality;
            nonUpdated = true;
>>>>>>> 1.9
         }
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_Q_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TEST_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_BACKUP_BIT ) )
            quality = QuestionableQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_UN_INIT_BIT ) )
            quality = UnintializedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MISSING_BIT ) )
            quality = PartialIntervalQuality;
         else
            quality = NormalQuality;
      }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << reason << " " << decipherReason(reason) << endl;
               }
=======
      group_number = _controlCenter.getTelegyrGroupList()[index].getGroupID();
      persistence = API_GRP_NO_PERSISTENCE;                          
      object_count = _controlCenter.getTelegyrGroupList()[index].getPointList().size();
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
               switch( reason )
               {
               case API_DISC_NOTIFY:   //we've lost connection to the control center
                  {
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << returnCode << " API_DISC_NOTIFY " << endl;
                     }
=======
      if( object_count != 0 )       //don't create empty groups
=======
      if( nonUpdated == true )
>>>>>>> 1.9
      {
<<<<<<< fdrtelegyr.cpp
         //make enough pointers for our pointnames
         name_list = new char *[object_count];                    
>>>>>>> 1.7.6.7
=======
         pCmdMsg = new CtiCommandMsg( CtiCommandMsg::UpdateFailed );
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
                     //in the future, we may have more than one control center, so we'd loop through them all
                     //for( index; index < _controlCenterList.size(); index++ )
                     {
                        CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );
=======
         for( count = 0; count < object_count; count++ )
         {
            //makes some space to copy our pointnames
            name_list[count] = new char[200];
            strcpy( name_list[count], _controlCenter.getTelegyrGroupList()[index].getPointList()[count].getTranslateName( 0 ) );
>>>>>>> 1.7.6.7
=======
         pCmdMsg->insert( -1 );                             // This is the dispatch token and is unimplemented at this time
         pCmdMsg->insert( OP_POINTID );                     // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
         pCmdMsg->insert( point->getPointID() );            // The id (device or point which failed)
         pCmdMsg->insert( ScanRateGeneral );                // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
         pCmdMsg->insert( UnknownError );                   // The error number from dsm2.h or yukon.h which was reported.
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
                        if( _controlCenter.getTelegyrGroupList().size() != 0 )
                        {
                           api_delete_all_groups( _controlCenter.getChannelID() );
                        }
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " " << name_list[count] << endl;
            }
         }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
                        api_disconnect( _controlCenter.getChannelID(), API_VALID );
=======
         //do the api-registration of the group...
         if( isConnected() )
         {
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Create ch_id " << channel_id 
                                     << " type " << group_type 
                                     << " num " << group_number 
                                     << " pers " << persistence 
                                     << " pri " << PRIORITY
                                     << " objs " << object_count << endl;
            }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
                        if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                        {
                           CtiLockGuard<CtiLogger> doubt_guard( dout );
                           dout << RWTime::now() << " - FDRTelegyr lost connection with Control Center - " << endl;
                        }
=======
            returnCode = api_create_group( channel_id, group_type, group_number, persistence, PRIORITY, object_count, name_list );
>>>>>>> 1.7.6.7

                        Description = " FDRTelegyr lost connection with Control Center ";
                        logEvent( Description, Action, false );

                        _numberOfConnections--;
                        setConnected( false );
                     }
>>>>>>> 1.7.12.2
                  }
<<<<<<< fdrtelegyr.cpp
                  else //successful reconnection
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " TelegyrAPI connected is connected (main)" << endl;
                     }

                     if( startUp == true )
                     {
                        startUp = false;
                     }
=======
                  break;
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
                     sendLinkState( FDR_CONNECTED );
                  }
               }
            }
         }
      }
=======
               case API_CYC_DATA:      //we've got cyclic data on the queue
               case API_SPO_DATA:      //we've got data cause someone altered the server's database
                  {
                     int x;
                     int arraySize = 0;

                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << returnCode << " API_CYC_DATA & API_SPO_DATA " << endl;
                     }

                     //loop through the list comparing groupnumber (which we decided were groupIDs)
                     //when we find one, we get the size of the point list inside it and get an array
                     //big enough to hold the point data we've gotten back
                     CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

                     for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
                     {
                        if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == group_num )
                        {
                           arraySize = _controlCenter.getTelegyrGroupList()[x].getPointList().size();
                           break;
                        }
                     }


                     switch( group_type )
                     {
                     case API_GET_CYC_MEA:
                     case API_GET_SPO_MEA:
                        {
                           int x;

                           {
                              CtiLockGuard<CtiLogger> doubt_guard( dout );
                              dout << returnCode << " API_GET_CYC_MEA & API_GET_SPO_MEA " << endl;
                           }

                           APICLI_GET_MEA *measurands;
                           measurands = new APICLI_GET_MEA[arraySize];

                           if( measurands != NULL )
                           {
                              status = api_unpack_measurands( measurands );

                              if( status == API_NORMAL )
                              {
                                 if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                 {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime::now() <<" - Measurands unpacked successfully - " << endl;
                                 }

                                 int index;

                                 //we might only get a few points back, so we'll have to adjust
                                 //for where they are on the FDR side of things during processing

                                 for( index = 0; index < last_index-first_index+1 ; index++ )
                                 {
                                    processAnalog( measurands[index], group_num, index );
                                 }
                              }
                              else
                              {
                                 if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                 {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime::now() << " TelegyrAPI api_unpack_measurands() failed: " <<  " " << decipherError( status ) << eventTime << endl;
                                 }
                              }
                              delete measurands;
                           }
                        }
                        break;

                     case API_GET_CYC_IND:
                     case API_GET_SPO_IND:
                        {
                           int x;

                           {
                              CtiLockGuard<CtiLogger> doubt_guard( dout );
                              dout << returnCode << " API_GET_CYC_IND & API_GET_SPO_IND " << endl;
                           }

                           APICLI_GET_IND *indications;
                           indications = new APICLI_GET_IND[arraySize];

                           if( indications != NULL )
                           {
                              status = api_unpack_indications( indications );

                              if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                              {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime::now() << " - Indications unpacked successfully - " << endl;
                              }

                              if( status == API_NORMAL )
                              {
                                 int index;

                                 for( index = 0; index < last_index-first_index+1 ; index++ )
                                 {
                                    processDigital( indications[index], group_num, index );
                                 }
                              }
                              else
                              {
                                 if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                 {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime::now() << " TelegyrAPI api_unpack_indications() failed " <<  " " << decipherError( status ) << eventTime << endl;
                                 }
                              }
                              delete indications;
                           }
                        }
                        break;

                     case API_GET_CYC_CNT:
                     case API_GET_SPO_CNT:
                        {
                           int x;

                           {
                              CtiLockGuard<CtiLogger> doubt_guard( dout );
                              dout << returnCode << " API_GET_CYC_CNT & API_GET_SPO_CNT " << endl;
                           }

                           APICLI_GET_CNT *counters;
                           counters = new APICLI_GET_CNT[arraySize];

                           if( counters != NULL )
                           {
                              status = api_unpack_counter_values( counters );

                              if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                              {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime::now() << " - Counters unpacked successfully - " << endl;
                              }

                              if( status == API_NORMAL )
                              {
                                 int index;

                                 for( index = 0; index < last_index-first_index+1 ; index++ )
                                 {
                                    processCounter( counters[index], group_num, index );
                                 }
                              }
                              else
                              {
                                 if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                 {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime::now() << " TelegyrAPI api_unpack_counter_values() failed " <<  " " << decipherError( status ) << eventTime << endl;
                                 }
                              }
                              delete counters;
                           }
                        }
                        break;
                     }


                     if( status != API_NORMAL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Unpack error occured: " << decipherError( status ) << endl;
                     }

                  }
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
      delete [] newPath;
      delete [] applicationName;
      delete [] apiVer;
   }
   catch( RWCancellation &cancellationMsg )
   {
      return;
   }
=======
                  break;

               case API_DELALL_RES:    //we've done a global delete group call
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to delete all groups executed" << endl;
                     }
                  }
                  break;

               case API_ENCYC_RES:     //we've done a call to enable cyclic data
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to enable cyclic data executed" << endl;
                     }
                  }
                  break;

               case API_ENSPO_RES:     //we've done a call to enable spontainious data
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to enable spontaneous data executed" << endl;
                     }
                  }
                  break;

               case API_CRE_RES:     //we've created a group
               {
                  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " Group Created!! " << endl;
                  }
               }
               break;

               default:              //we don't know what the heck is going on....
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Unsupported message reason : " << decipherReason( reason ) << endl;
                     }
                  }
                  break;
               }
            }
            else  //if there aren't any messages, that's fine
            {
               if( waiter >= 15 )
               {
                  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " No messages on the queue" << endl;
                  }

                  waiter = 0;
               }
            }
         }
         else
         {
            timer = 0;
            waiter++;               //we want to try to reconnect every 15 secs or so (if we've lost connection)

            if( waiter >= 15 )
            {
//               setConnected( connect( 0, status ) );         //NOTE:we used this Aug '02 at MEC

               contact( status );                                      //NOTE:used this as it calls build&Register()

               printCount++;           //we want to tell someone that we tried to reconnect every 60 secs (or so?)
               waiter = 0;

               if( printCount >= 10 )
               {
                  printCount = 0;

                  if( isConnected() != true )
                  {

                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " TelegyrAPI connect failed. Status returned: " << decipherError( status ) << endl;

                     if( startUp == true )
                     {
                        Description = " TelegyrAPI connect failed. Status returned: " + decipherError( status );
                        logEvent( Description, Action, false );
                        startUp = false;
                     }
                  }
                  else //successful reconnection
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " TelegyrAPI connected. Status returned: " << decipherError( status ) << endl;

                     if( startUp == true )
                     {
                        Description = " FDRTelegyr connected to Control Center. Status returned: " + decipherError( status );
                        logEvent( Description, Action, false );
                        startUp = false;
                     }

                     // update the status point watching the connection to failed
                     setLinkStatusID( getClientLinkStatusID( getInterfaceName() ) );
                  }
               }
            }
         }
      }
   }
   catch( RWCancellation &cancellationMsg )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      return;
   }
>>>>>>> 1.7.12.2
}

//=================================================================================================================================
//here we are yanking data out of our telegyr group list and setting up groups that the foreign system will understand
//we should only be here if we've gotten past the api_init() and api_connect() methods built into telegyr
//=================================================================================================================================

bool CtiFDRTelegyr::buildAndRegisterGroups( void )
{
<<<<<<< fdrtelegyr.cpp
   int                  index;
   int                  count;
   int                  returnCode;
   int                  channel_id;
   int                  group_type;
   int                  group_number;
   int                  persistence;
   int                  object_count;
   char                 **name_list;
   int                  cycle_time;
   bool                 cyclic = false;
//   int                  totalGroups = _controlCenter.getTelegyrGroupList().size();
   int                  totalGroups = 96;

//   for( index = 0; index < _controlCenter.getTelegyrGroupList().size(); index++ )
   for( index = 0; index < totalGroups; index++ )
   {
      cyclic = false;
      channel_id = _controlCenter.getChannelID();

      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
      {
         if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_CYC_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_CYC_MEA;
         }
         else// COUNTER_TYPE
         {
            group_type = API_GET_CYC_CNT;
         }

         //this will always be in seconds
         cycle_time = _controlCenter.getTelegyrGroupList()[index].getInterval();
         cyclic = true;
      }
      else   //if there's no interval, we get data when a point changes on the server
      {
         if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_SPO_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_SPO_MEA;
         }
         else// COUNTER_TYPE
         {
            group_type = API_GET_SPO_CNT;
         }
      }

      group_number = _controlCenter.getTelegyrGroupList()[index].getGroupID();
      persistence = API_GRP_NO_PERSISTENCE;                          //I think we'll just re-register anytime we need to
      object_count = _controlCenter.getTelegyrGroupList()[index].getPointList().size();

      if( object_count != 0 )       //don't create empty groups
      {
         //make enough pointers for our pointnames
         name_list = new char *[object_count];                    

         for( count = 0; count < object_count; count++ )
         {
            //makes some space to copy our pointnames
            name_list[count] = new char[200];
            strcpy( name_list[count], _controlCenter.getTelegyrGroupList()[index].getPointList()[count].getTranslateName( 0 ) );
         }

         //do the api-registration of the group...
         if( isConnected() == true )
         {
            returnCode = api_create_group( channel_id, group_type, group_number, persistence, PRIORITY, object_count, name_list );

            if( returnCode != API_NORMAL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Failed to create group number: " << group_number << "; code: " << returnCode << endl;
            }
         }

         for( count = 0; count < object_count; count++ )
         {
            delete [] name_list[count];
         }
         delete [] name_list;
      }
   }

   //enable all the groups
//   for( index = 0; index < _controlCenter.getTelegyrGroupList().size(); index++ )
   for( index = 0; index < totalGroups; index++ )
   {
      cyclic = false;
      group_number = _controlCenter.getTelegyrGroupList()[index].getGroupID();
      channel_id = _controlCenter.getChannelID();
      object_count = _controlCenter.getTelegyrGroupList()[index].getPointList().size();

      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
      {
         if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_CYC_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_CYC_MEA;
         }
         else//COUNTER_TYPE
         {
            group_type = API_GET_CYC_CNT;
         }

         //this will always be in seconds
         cycle_time = _controlCenter.getTelegyrGroupList()[index].getInterval();
         cyclic = true;
      }
      else   //if there's no interval, we get data when a point changes on the server
      {
         if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_SPO_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_SPO_MEA;
         }
         else//COUNTER_TYPE
         {
            group_type = API_GET_SPO_CNT;
         }
      }

      if( object_count != 0 )    //don't enable empty groups!
      {
         if( cyclic != false )
         {
            //by specifying the NOALIG parameter, we are telling the telegyr system to go ahead and start sending
            //data back to use immediately
            int code = api_enable_cyclic( channel_id, group_type, group_number, cycle_time, API_SECOND, API_ALIG_NOALIG );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " api_enable_cyclic returned " << code << endl;
            }

            cyclic = false;
         }
         else
         {
            int retCode = api_enable_spontaneous( channel_id, group_type, group_number );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " api_enable_spontaneous returned " << retCode<< endl;
            }
         }
      }
      else
      {
         if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " no points in " << _controlCenter.getTelegyrGroupList()[index].getGroupID() << endl;
         }
      }
   }
=======
   RWCString   Description;
   RWCString   Action;
   int         index;
   int         count;
   int         returnCode;
   int         channel_id;
   int         group_type;
   int         group_number;
   int         persistence;
   int         priority;
   int         object_count;
   char        **name_list;
   int         cycle_time;
   bool        cyclic = false;
   bool        madeGroup = false;

   CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

   for( index = 0; index < _controlCenter.getTelegyrGroupList().size(); index++ )
   {
      cyclic = false;

      channel_id = _controlCenter.getChannelID();

      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
      {
         if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " - Cyclic data group found: " << _controlCenter.getTelegyrGroupList()[index].getGroupName() << " - " << endl;
         }

         if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_CYC_IND;

            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << returnCode << " group_type = API_GET_CYC_IND " << endl;
            }
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_CYC_MEA;

            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << returnCode << " group_type = API_GET_CYC_MEA " << endl;
            }
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == COUNTER_TYPE )
         {
            group_type = API_GET_CYC_CNT;

            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << returnCode << " group_type = API_GET_CYC_CNT " << endl;
            }
         }
         else
         {

            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << returnCode << "Unknown: " << _controlCenter.getTelegyrGroupList()[index].getGroupType() << endl;
         }

         //this will always be in seconds
         cycle_time = _controlCenter.getTelegyrGroupList()[index].getInterval();
         cyclic = true;
      }
      else   //if there's no interval, we get data when a point changes on the server
      {
         if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " - Spontaneous data group found: " << _controlCenter.getTelegyrGroupList()[index].getGroupName() << " - " << endl;
         }

         if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_SPO_IND;

            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << returnCode << " group_type = API_GET_SPO_IND " << endl;
            }
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_SPO_MEA;
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
   return( true );
=======
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << returnCode << " group_type = API_GET_SPO_MEA " << endl;
            }
         }
         else if( _controlCenter.getTelegyrGroupList()[index].getGroupType() == COUNTER_TYPE )
         {
            group_type = API_GET_SPO_CNT;
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << returnCode << " group_type = API_GET_SPO_CNT " << endl;
            }
         }
      }

      group_number   = _controlCenter.getTelegyrGroupList()[index].getGroupID();
      persistence    = API_GRP_NO_PERSISTENCE;                          //I think we'll just re-register anytime we need to
      priority       = 1;                                               //telegyr only supports 1 at this point
      object_count   = _controlCenter.getTelegyrGroupList()[index].getPointList().size();

      if( object_count != 0 )
      {
         //make enough pointers for our pointnames
         name_list = new char *[object_count];                    //Matt doesn't like this...... Eric is afraid

         //I be doin' some poppin' an' ploppin' on deez pointnames
         for( count = 0; count < object_count; count++ )
         {
            //makes some space to copy our pointnames
            name_list[count] = new char[100];

            strcpy( name_list[count], _controlCenter.getTelegyrGroupList()[index].getPointList()[count].getTranslateName( 0 ) );
         }

         //do the api-registration of the group...
         if( isConnected() == true )
         {
            returnCode = api_create_group( channel_id, group_type, group_number, persistence, priority, object_count, name_list );

            delete []name_list;

            if( returnCode != API_NORMAL )
            {
               //log the event and put in the status (code returned)
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " TelegyrAPI create_group " << group_number << " failed: " << decipherError( returnCode ) << returnCode << endl;
            }
            else
            {
               madeGroup = true;

               if( cyclic != false )
               {
                  //by specifying the NOALIG parameter, we are telling the telegyr system to go ahead and start sending
                  //data back to use immediately
                  api_enable_cyclic( channel_id, group_type, group_number, cycle_time, API_SECOND, API_ALIG_NOALIG );
                  cyclic = false;
               }
               else
               {
                  api_enable_spontaneous( channel_id, group_type, group_number );
               }
            }
         }
      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " Empty Pointlist: Won't register...." << _controlCenter.getTelegyrGroupList()[index].getGroupName() << endl;
      }
   }
   return madeGroup;
>>>>>>> 1.7.12.2
}

//=================================================================================================================================
//we just have this in case anyone ever really wants to send a message, right now, it's not used....
//=================================================================================================================================

bool CtiFDRTelegyr::sendMessageToForeignSys( CtiMessage *aMessage )
{
   // message is deleted in fdrinterface thread
   return TRUE;
}

//=================================================================================================================================
//this is the receive message function that is our only concern for now
//here is where we'll wait for data to come back from the telegyr system after we've told it what we want
//=================================================================================================================================

INT CtiFDRTelegyr::processMessageFromForeignSystem( CHAR *data )
{
   return NORMAL;
}

//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::loadTranslationLists()
{
<<<<<<< fdrtelegyr.cpp
   if( isConnected() )
   {
      if( _controlCenter.getTelegyrGroupList().size() != 0 )
      {
         int status = api_disable_all_cyclic( _controlCenter.getChannelID() );
         
         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " Disable-all-cyclic returns (code #): " << status << " on channel " << _controlCenter.getChannelID() << endl;
         }

         status = api_disable_all_spontaneous( _controlCenter.getChannelID() );

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " Disable-all-spontaneous returns (code #): " << status << " on channel " << _controlCenter.getChannelID() << endl;
         }

         status = api_delete_all_groups( _controlCenter.getChannelID() );

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " Delete-all-groups returns (code #): " << status << " on channel " << _controlCenter.getChannelID() <<  endl;
         }
      }
   }
=======
   RWCString   Description;
   RWCString   Action;
   bool        retCode = true;
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
   return( false );
}
=======
   if( getGroupLists() != false )
   {
      if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " Group list reload for FDRTelegyr" << endl;
      }

      //note: these are our internal lists, Telegyr doesn't know about them yet
      retCode = loadLists( getReceiveFromList() );
>>>>>>> 1.7.12.2

      if( retCode != true )
      {
         if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " Translation list load for FDRTelegyr failed " << endl;
         }
      }

<<<<<<< fdrtelegyr.cpp
bool CtiFDRTelegyr::loadTranslationLists()
{
   bool  retCode = true;

   CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );
=======
      //if we're already connected, dispatch caused us to do the stuff we just did and we need
      //to re-register our groups with Telegyr
      if( isConnected() )
      {
         buildAndRegisterGroups();
      }
   }
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
   if( getGroupLists() != false )
   {
      //note: these are our internal lists, Telegyr doesn't know about them yet
      retCode = loadLists( getReceiveFromList() );

      if( retCode != true )
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " Translation list load for FDRTelegyr failed " << endl;
      }
   }
=======
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
   return( retCode );
=======
   return retCode;
>>>>>>> 1.7.12.2
}

//=================================================================================================================================
// we're loading up the FDRTelegyrGroup table in the event of a first load or a reload
//=================================================================================================================================

bool CtiFDRTelegyr::getGroupLists( void )
{
   bool           gotList = false;
   RWCString      tempGroupName;
   int            tempGroupID;
   RWCString      tempGroupType;
   int            tempInterval;
   RWDBStatus     retStatus;

   if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << " ***** We're getting the GROUPLISTS ***** " << endl;
   }

   try
   {
      // Make sure all objects that that store results
      CtiLockGuard<CtiSemaphore> cg( gDBAccessSema );
      RWDBConnection conn = getConnection();

      // are out of scope when the release is called

      RWDBDatabase db = conn.database();
      RWDBTable fdrTelegyrGroups = db.table( TBLNAME_TELEGYR_GROUPS );
      RWDBSelector selector = db.selector();

      selector
      << fdrTelegyrGroups[COLNAME_TELEGYR_GROUPID]
      << fdrTelegyrGroups[COLNAME_TELEGYR_NAME]
      << fdrTelegyrGroups[COLNAME_TELEGYR_INTERVAL]
      << fdrTelegyrGroups[COLNAME_TELEGYR_TYPE];

      selector.from( fdrTelegyrGroups );

      // check execute status to stop the exception when database is gone
      retStatus = selector.execute( conn ).status();

      if( retStatus.errorCode() == RWDBStatus::ok )
      {
         if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " retStatus.errorCode() == RWDBStatus::ok " << endl;
         }

         // delete FDRpoint vectors, then delete the group list vector
         CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

         if( _controlCenter.getTelegyrGroupList().size() != 0 )
         {
            int status;

            status = api_disable_all_cyclic( _controlCenter.getChannelID() );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard< CtiLogger > doubt_guard( dout );
               dout << " Disable all cyclic say: " << decipherError( status ) << endl;
            }

            status = api_disable_all_spontaneous( _controlCenter.getChannelID() );

<<<<<<< fdrtelegyr.cpp
         //if we've gotten an ack from the ems, we can go ahead
         if( _regFlag )
         {
            // delete FDRpoint vectors, then delete the group list vector
            if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " DEBUG --------------------------------------deleting groups on EMS " << endl;
            }

            deleteGroups();

            if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " DEBUG --------------------------------------done deleting groups on EMS" << endl;
            }
         }

         //delete our local list 
         _controlCenter.deleteTelegyrGroupList();

         RWDBReader  rdr = selector.reader( conn );
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard< CtiLogger > doubt_guard( dout );
               dout << " Disable all spontaneous say: " << decipherError( status ) << endl;
            }

            status = api_delete_all_groups( _controlCenter.getChannelID() );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard< CtiLogger > doubt_guard( dout );
               dout << " Delete all groups say: " << decipherError( status ) << endl;
            }

            _controlCenter.deleteTelegyrGroupList();

         }

         RWDBReader  rdr = selector.reader( conn );
>>>>>>> 1.7.12.2
         CtiTelegyrGroup tempGroup;

         while( rdr() )
         {
            //put all the new values into variables
            rdr[COLNAME_TELEGYR_GROUPID]    >> tempGroupID;
            rdr[COLNAME_TELEGYR_NAME]       >> tempGroupName;
            rdr[COLNAME_TELEGYR_INTERVAL]   >> tempInterval;
            rdr[COLNAME_TELEGYR_TYPE]       >> tempGroupType;

            //add the values to a new grouplist
            tempGroup.setGroupID( tempGroupID );
            tempGroup.setGroupName( tempGroupName );
            tempGroup.setInterval( tempInterval );
            tempGroup.setGroupType( tempGroupType );
<<<<<<< fdrtelegyr.cpp

            //plunk the grouplist into the vector
=======

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard< CtiLogger > doubt_guard( dout );
               dout << " Going to addToGroupList() " << endl;
            }

            //plunk the grouplist into the vector
            CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );
>>>>>>> 1.7.12.2
            _controlCenter.addToGroupList( tempGroup );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << endl;
               dout << endl;
               dout << RWTime::now() << " - Group added with Groupname: " << tempGroupName << " Grouptype: " << tempGroupType << " - " << endl;
               dout << "TempGroupID " << tempGroupID << endl;
               dout << "TempGroupName " << tempGroupName << endl;
               dout << "TempInterval " << tempInterval << endl;
               dout << "TempGroupType " << tempGroupType << endl;
               dout << endl;
               dout << endl;
            }
         }
         gotList = true;
      }
      ///////////temp
      else
      {
         if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " retStatus.errorCode() != RWDBStatus::ok " << endl;
         }
      }
   }

   catch( RWExternalErr e )
   {
      RWTHROW(e);
   }
   catch( ... )
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
      }
   }

   return gotList;
}
/*
//=================================================================================================================================
//
//=================================================================================================================================

bool CtiFDRTelegyr::loadLists( CtiFDRPointList &aList )
{
   bool                successful( FALSE );
   int                 analogCount = 0;
   int                 statusCount = 0;

   CtiFDRPoint         *translationPoint = NULL;
   RWCString           myTranslateName;
   RWCString           tempString1;
   RWCString            pointStr1;
   RWCString            pointStr2;
   RWCString            pointType;
   RWCString           pointStr;
   RWCString           groupStr;
   RWCString           newTranslationString;
   bool                foundPoint = false;
   RWDBStatus          listStatus;

   try
   {
      // make a list with all received points
      CtiFDRManager *pointList = new CtiFDRManager( getInterfaceName(), RWCString( FDR_INTERFACE_RECEIVE ) );

      listStatus = pointList->loadPointList();

      // if status is ok, we were able to read the database at least
      if( listStatus.errorCode() == (RWDBStatus::ok))
      {
<<<<<<< fdrtelegyr.cpp
         //===================================================================================
         //seeing occasional problems where we get empty data sets back and there should be 
         //info in them,  we're checking this to see if is reasonable if the list may now be 
         //empty the 2 entry thing is completly arbitrary
         //===================================================================================

         if(((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) || (pointList->entries() > 0))
         {
            // get iterator on list
            CtiFDRManager::CTIFdrPointIterator myIterator( pointList->getMap() );
            int x;

            //iterate through all our points in the list
            for( ; myIterator(); )
            {
               foundPoint = true;
               translationPoint = myIterator.value();

               //iterate through all our destinations per point (should have 1 for telegyr)
               for( x = 0; x < translationPoint->getDestinationList().size(); x++ )
               {
                  // there should only be one destination for telegyr points
                  RWCTokenizer nextTranslate( translationPoint->getDestinationList()[x].getTranslation() );

                  //note: we're making a brand new token (string) out of the data before the ';'
                  if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of POINTID:xxxx
                     RWCTokenizer nextTempToken( tempString1 );

                     // do not care about the first part so toss it
                     nextTempToken( ":" );

                     //now we find the end of the string
                     pointStr1 = nextTempToken(":");    //this is analog or status

                     if( pointStr1.compareTo( "STATUS" ) == 0 )
                     {
                        RWCString a( "digital" );
                        pointType = a;
                     }
                     else
                     {
                        RWCString a( "analog" );
                        pointType = a;
                     }
                     

                     pointStr2 = nextTempToken(":");    //this is the points name

                     pointStr = pointStr1 + RWCString(":") + pointStr2;

                     // now we have a pointid with a :
                     if( !pointStr.isNull() )
                     {
                        successful = true;

                        if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                        {
                           CtiLockGuard<CtiLogger> doubt_guard( dout );
                           dout << RWTime::now() << " - Point with Pointid: " << pointStr << " found - " << endl;
                        }
                     }
                  }

                  //note: we're making a brand new token (string) out of the data before the ';'
                  if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of GROUP:xxxx
                     RWCTokenizer nextTempToken( tempString1 );

                     // do not care about the first part so toss it
                     nextTempToken( ":" );

                     //now we find the end of the string
                     groupStr = nextTempToken(":");

                     // now we have a groupid with a ':'
                     if( !groupStr.isNull() )
                     {
                        successful = true;
                     }
                  }

                  translationPoint->getDestinationList()[x].setTranslation( pointStr );
               }

               //we need to spin through the list
               for( int i = 0; i < _controlCenter.getTelegyrGroupList().size(); i++ )
               {
                  RWCString type = _controlCenter.getTelegyrGroupList()[i].getGroupType();
                  type.toLower();
                  pointType.toLower();

                  int size = _controlCenter.getTelegyrGroupList()[i].getPointList().size();
                                    
                  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " type " << type << " size " << size << " " << _controlCenter.getTelegyrGroupList()[i].getGroupID() << endl;
                  }

                  if(( type == pointType ) && ( size < 127 ))
                  {
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " adding " << pointType << " to " << _controlCenter.getTelegyrGroupList()[i].getGroupID() << endl;
                     }

                     _controlCenter.getTelegyrGroupList()[i].getPointList().push_back( *translationPoint );
                     break;
                  }
               }
            }

            delete pointList;  //????
            pointList=NULL;

            if( !successful )
            {
               if( !foundPoint )
               {
                  // means there was nothing in the list, wait until next db change or reload
                  successful = true;
               }
            }
         }
         else
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
            }
            successful = false;
         }
=======

          /**************************************
          * seeing occasional problems where we get empty data sets back
          * and there should be info in them,  we're checking this to see if
          * is reasonable if the list may now be empty
          * the 2 entry thing is completly arbitrary
          ***************************************
          */
          if (((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) ||
              (pointList->entries() > 0))
          {

              // get iterator on list
              CtiFDRManager::CTIFdrPointIterator myIterator( pointList->getMap() );
              int x;

              //iterate through all our points in the list
              for( ; myIterator(); )
              {
                 foundPoint = true;
                 translationPoint = myIterator.value();

                 //iterate through all our destinations per point (should have 1 for telegyr)
                 for( x = 0; x < translationPoint->getDestinationList().size(); x++ )
                 {
                    //put in debug output from cygnet

                    // there should only be one destination for telegyr points
                    RWCTokenizer nextTranslate( translationPoint->getDestinationList()[x].getTranslation() );

                    //note: we're making a brand new token (string) out of the data before the ';'
                    if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                    {
                       // this in the form of POINTID:xxxx
                       RWCTokenizer nextTempToken( tempString1 );

                       // do not care about the first part so toss it
                       nextTempToken( ":" );

                       //now we find the end of the string
                       RWCString pointStr1 = nextTempToken(":");
                       RWCString pointStr2=nextTempToken(":");

                       pointStr = pointStr1 + RWCString(":") + pointStr2;

                       // now we have a pointid with a :
                       if( !pointStr.isNull() )
                       {
                          successful = true;

                          if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                          {
                             CtiLockGuard<CtiLogger> doubt_guard( dout );
                             dout << RWTime::now() << " - Point with Pointid: " << pointStr << " found - " << endl;
                          }
                       }
                    }

                    //note: we're making a brand new token (string) out of the data before the ';'
                    if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                    {
                       // this in the form of GROUP:xxxx
                       RWCTokenizer nextTempToken( tempString1 );

                       // do not care about the first part so toss it
                       nextTempToken( ":" );

                       //now we find the end of the string
                       groupStr = nextTempToken(":");

                       // now we have a groupid with a ':'
                       if( !groupStr.isNull() )
                       {
                          successful = true;

                          if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                          {
                             CtiLockGuard<CtiLogger> doubt_guard( dout );
                             dout << RWTime::now() << " - Point with Group: " << groupStr << " found - " << endl;
                          }
                       }
                    }

                    translationPoint->getDestinationList()[x].setTranslation( pointStr );
                 }

                 int i;

                 bool found = false;

                 //we need to spin through the list
                 for( i = 0; i <  _controlCenter.getTelegyrGroupList().size(); i++ )
                 {
                    //and add our FDRPoints to the groups
                    if( _controlCenter.getTelegyrGroupList()[i].getGroupName() == groupStr )
                    {
                       _controlCenter.getTelegyrGroupList()[i].getPointList().push_back( *translationPoint );
                    }
                 }

                 //put in debug output from cygnet
              }

              CtiLockGuard<CtiMutex> sendGuard( aList.getMutex() );
              if( aList.getPointList() != NULL )
              {
                 aList.deletePointList();
              }
              aList.setPointList( pointList );
              pointList=NULL;

              if( !successful )
              {
                 if( !foundPoint )
                 {
                    // means there was nothing in the list, wait until next db change or reload
                    successful = true;

                    //put in debug output from cygnet
                 }
              }
          }
          else
          {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
              successful = false;
          }
>>>>>>> 1.7.12.2

      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
         successful = false;
      }
   }
   catch( RWExternalErr e )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << "loadTranslationList():  " << e.why() << endl;
      RWTHROW( e );
   }
   return successful;
}
*/
//=================================================================================================================================
//get point info from a database load/reload
//=================================================================================================================================

bool CtiFDRTelegyr::loadLists( CtiFDRPointList &aList )
{
   bool                successful( FALSE );
   int                 analogCount = 0;
   int                 statusCount = 0;
   CtiFDRPoint         *translationPoint = NULL;
   RWCString           myTranslateName;
   RWCString           tempString1;
   RWCString           pointStr;
   RWCString           groupStr;
   RWCString           newTranslationString;
   bool                foundPoint = false;
   RWDBStatus          listStatus;


   try
   {
      // make a list with all received points
      CtiFDRManager *pointList = new CtiFDRManager( getInterfaceName(), RWCString( FDR_INTERFACE_RECEIVE ) );

      listStatus = pointList->loadPointList();

      // if status is ok, we were able to read the database at least
      if( listStatus.errorCode() == (RWDBStatus::ok))
      {
         //===================================================================================
         //seeing occasional problems where we get empty data sets back and there should be 
         //info in them,  we're checking this to see if is reasonable if the list may now be 
         //empty the 2 entry thing is completly arbitrary
         //===================================================================================

         if(((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2)) || (pointList->entries() > 0))
         {
            // get iterator on list
            CtiFDRManager::CTIFdrPointIterator myIterator( pointList->getMap() );
            int x;

            //iterate through all our points in the list
            for( ; myIterator(); )
            {
               foundPoint = true;
               translationPoint = myIterator.value();

               //iterate through all our destinations per point (should have 1 for telegyr)
               for( x = 0; x < translationPoint->getDestinationList().size(); x++ )
               {
                  // there should only be one destination for telegyr points
                  RWCTokenizer nextTranslate( translationPoint->getDestinationList()[x].getTranslation() );

                  //note: we're making a brand new token (string) out of the data before the ';'
                  if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of POINTID:xxxx
                     RWCTokenizer nextTempToken( tempString1 );

                     // do not care about the first part so toss it
                     nextTempToken( ":" );

                     //now we find the end of the string
                     RWCString pointStr1 = nextTempToken(":");
                     RWCString pointStr2 = nextTempToken(":");

                     pointStr = pointStr1 + RWCString(":") + pointStr2;

                     // now we have a pointid with a :
                     if( !pointStr.isNull() )
                     {
                        successful = true;

                        if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                        {
                           CtiLockGuard<CtiLogger> doubt_guard( dout );
                           dout << RWTime::now() << " - Point with Pointid: " << pointStr << " found - " << endl;
                        }
                     }
                  }

                  //note: we're making a brand new token (string) out of the data before the ';'
                  if( !(tempString1 = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of GROUP:xxxx
                     RWCTokenizer nextTempToken( tempString1 );

                     // do not care about the first part so toss it
                     nextTempToken( ":" );

                     //now we find the end of the string
                     groupStr = nextTempToken(":");

                     // now we have a groupid with a ':'
                     if( !groupStr.isNull() )
                     {
                        successful = true;

                        if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                        {
                           CtiLockGuard<CtiLogger> doubt_guard( dout );
                           dout << RWTime::now() << " - Point with Group: " << groupStr << " found - " << endl;
                        }
                     }
                  }

                  translationPoint->getDestinationList()[x].setTranslation( pointStr );
               }

               bool found = false;

               //we need to spin through the list
               for( int i = 0; i <  _controlCenter.getTelegyrGroupList().size(); i++ )
               {
                  //and add our FDRPoints to the groups
                  if( _controlCenter.getTelegyrGroupList()[i].getGroupName() == groupStr )
                  {
                     _controlCenter.getTelegyrGroupList()[i].getPointList().push_back( *translationPoint );
                  }
               }
            }

            delete pointList;  //????
            pointList=NULL;

            if( !successful )
            {
               if( !foundPoint )
               {
                  // means there was nothing in the list, wait until next db change or reload
                  successful = true;
               }
            }
         }
         else
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
            successful = false;
         }

      }
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
         successful = false;
      }
   }
   catch( RWExternalErr e )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << "loadTranslationList():  " << e.why() << endl;
      RWTHROW( e );
   }
   return successful;
}

//=================================================================================================================================
//we want to decipher the point and put it on the queue to Dispatch
//=================================================================================================================================

bool CtiFDRTelegyr::processAnalog( APICLI_GET_MEA aPoint, int groupid, int index )
{
   CtiPointDataMsg      *pData      = NULL;
   CtiCommandMsg        *pCmdMsg    = NULL;
   CtiFDRPoint          *point      = NULL;
   RWCString            Description;
   RWCString            Action;
   long                 pointid;
   bool                 returnCode;
   int                  x;
   double               value;
<<<<<<< fdrtelegyr.cpp
   double               raw;
=======
>>>>>>> 1.7.12.2
   USHORT               quality;
   bool                 nonUpdated = false;

   if( aPoint.mea_valid == API_VALID )
   {
      value = aPoint.mea_value.mea4_value;
<<<<<<< fdrtelegyr.cpp
      raw = value;
=======

      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << "Raw Analog Value: " << value << endl;
      }
>>>>>>> 1.7.12.2

      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
      CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();

            _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointType();

            value = value * ( _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getMultiplier() );
            value += ( _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getOffset() );

<<<<<<< fdrtelegyr.cpp
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
<<<<<<< fdrtelegyr.cpp
               dout << RWTime() << " Raw Analog Value: " << raw << " from Point " << _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0) << " groupid " << groupid << endl;
=======
               dout << RWTime::now() << " Failed to create group number: " << group_number << " type: " << group_type << " code: " << returnCode << endl;
>>>>>>> 1.7.6.7
            }
=======
            {
<<<<<<< fdrtelegyr.cpp
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << "Point with PointID " << pointid << "from Control Center "<< _controlCenter.getSysName() << endl;
               dout << /*add some more crap here*/endl;
            }
>>>>>>> 1.7.12.2

            //if the value is higher than it should be, we want to let the boss man know
            if( ( getHiReasonabilityFilter() > 0 ) && ( getHiReasonabilityFilter() > value ) )
            {
               nonUpdated = true;
=======
               created[index] = true;
>>>>>>> 1.7.6.7
            }
            break;
         }
      }

      //get the quality of the point data
      if( !api_check_any_quality_bit_set( aPoint.sys_dependent_info ) )
      {
         quality = NormalQuality;
      }
      else
      {
         //there are a bunch more that telegyr supports, we just don't care
         if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_OUT_SCAN_BIT ) )
         {
            quality = NonUpdatedQuality;
         }
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TELEM_F_BIT ) )
         {
            quality = NonUpdatedQuality;
            nonUpdated = true;
         }
<<<<<<< fdrtelegyr.cpp
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_Q_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TEST_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_BACKUP_BIT ) )
            quality = QuestionableQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_UN_INIT_BIT ) )
            quality = UnintializedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MISSING_BIT ) )
            quality = PartialIntervalQuality;
         else
            quality = NormalQuality;

      }

      if( nonUpdated == true )
      {
         pCmdMsg = new CtiCommandMsg( CtiCommandMsg::UpdateFailed );

         pCmdMsg->insert( -1 );                             // This is the dispatch token and is unimplemented at this time
         pCmdMsg->insert( OP_POINTID );                     // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
         pCmdMsg->insert( point->getPointID() );            // The id (device or point which failed)
         pCmdMsg->insert( ScanRateGeneral );                // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
         pCmdMsg->insert( UnknownError );                   // The error number from dsm2.h or yukon.h which was reported.

         // consumes and deletes pData memory
         sendMessageToDispatch(pCmdMsg);
      }
      else
      {
         pData = new CtiPointDataMsg( pointid, value, quality, AnalogPointType );

         //plop whichever message is valid onto the Dispatch pile
         if( pData )
         {
            // consumes a delete memory
            queueMessageToDispatch( pData );
         }
      }
      returnCode = true;
   }
   else
   {
      returnCode = false;
   }

   return returnCode;
}

//=================================================================================================================================
//we want to decipher the point and put it on the queue to Dispatch
//=================================================================================================================================

bool CtiFDRTelegyr::processDigital( APICLI_GET_IND aPoint, int groupid, int index )
{
   CtiPointDataMsg      *pData      = NULL;
   long                 pointid;
   bool                 returnCode;
   int                  x;
   double               value;
   USHORT               quality;

   if( aPoint.ind_valid == API_VALID )
   {
      value = aPoint.ind_value;

      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << "My Raw Digital: " << value << endl;
      }

      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
      CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
<<<<<<< fdrtelegyr.cpp
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime() << " Raw Digital Value: " << value << " from Point " << _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0) << " groupid " << groupid << endl;
            }

=======
>>>>>>> 1.7.12.2
            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();
            break;
         }
      }

      //get the quality of the point data
      if( api_check_any_quality_bit_set( aPoint.sys_dependent_info ) )
      {
         quality = NormalQuality;
      }
      else
      {  //there are a bunch more that telegyr supports, we just don't care
         if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_OUT_SCAN_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TELEM_F_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_Q_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TEST_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_BACKUP_BIT ) )
            quality = QuestionableQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_UN_INIT_BIT ) )
            quality = UnintializedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MISSING_BIT ) )
            quality = PartialIntervalQuality;
         else
            quality = NormalQuality;
      }
      pData = new CtiPointDataMsg( pointid, value, quality, StatusPointType );

      if( pData )
      {
         // consumes a delete memory
         queueMessageToDispatch( pData );
      }
      returnCode = true;
   }
   else
   {
      returnCode = false;
=======

         for( count = 0; count < object_count; count++ )
         {
            delete [] name_list[count];
         }
         delete [] name_list;
      }
>>>>>>> 1.7.6.7
   }
<<<<<<< fdrtelegyr.cpp

   return returnCode;
}

//=================================================================================================================================
//we want to decipher the point and put it on the queue to Dispatch
//=================================================================================================================================

bool CtiFDRTelegyr::processCounter( APICLI_GET_CNT aPoint, int groupid, int index )
{
   CtiPointDataMsg      *pData      = NULL;
   long                 pointid;
   bool                 returnCode;
   int                  x;
   double               value;
   USHORT               quality;

   if( aPoint.cnt_valid == API_VALID )
   {
      value = aPoint.cnt_value.cnt8_value;
=======
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
      CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();
            break;
         }
      }
=======
   //enable all the groups
   for( int i = 0; i < totalGroups; i++ )
   {
      cyclic = false;
      group_number = _controlCenter.getTelegyrGroupList()[i].getGroupID();
      channel_id = _controlCenter.getChannelID();
      object_count = _controlCenter.getTelegyrGroupList()[i].getPointList().size();
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
      //get the quality of the point data
      if( api_check_any_quality_bit_set( aPoint.sys_dependent_info ) )
=======
      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[i].getInterval() != 0 )
>>>>>>> 1.7.6.7
      {
<<<<<<< fdrtelegyr.cpp
         quality = NormalQuality;
      }
      else
      {  //there are a bunch more that telegyr supports, we just don't care
         if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_OUT_SCAN_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TELEM_F_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_Q_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TEST_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_BACKUP_BIT ) )
            quality = QuestionableQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_UN_INIT_BIT ) )
            quality = UnintializedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MISSING_BIT ) )
            quality = PartialIntervalQuality;
         else
            quality = NormalQuality;
      }
      pData = new CtiPointDataMsg( pointid, value, quality, PulseAccumulatorPointType );
=======
         if( _controlCenter.getTelegyrGroupList()[i].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_CYC_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[i].getGroupType() == DIGITAL_TYPE )
         {
            group_type = API_GET_CYC_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[i].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_CYC_MEA;
         }
         else//COUNTER_TYPE
         {
            group_type = API_GET_CYC_CNT;
         }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
      if( pData )
      {
         // consumes a delete memory
         queueMessageToDispatch( pData );
=======
         //this will always be in seconds
         cycle_time = _controlCenter.getTelegyrGroupList()[i].getInterval();
         cyclic = true;
>>>>>>> 1.7.6.7
      }
<<<<<<< fdrtelegyr.cpp
      returnCode = true;
   }
   else
   {
      returnCode = false;
   }

   return returnCode;
}

<<<<<<< fdrtelegyr.cpp

//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::processBadPoint( int groupid, int index )
{
   long                 pointid;
   int                  x;
   RWCString            pointName;

   for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
   {
      if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
      {
         pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();
         pointName = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0);

=======
      else   //if there's no interval, we get data when a point changes on the server
      {
         if( _controlCenter.getTelegyrGroupList()[i].getGroupType() == STATUS_TYPE )
         {
            group_type = API_GET_SPO_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[i].getGroupType() == DIGITAL_TYPE )
         {
            group_type = API_GET_SPO_IND;
         }
         else if( _controlCenter.getTelegyrGroupList()[i].getGroupType() == ANALOG_TYPE )
         {
            group_type = API_GET_SPO_MEA;
         }
         else//COUNTER_TYPE
>>>>>>> 1.7.6.7
         {
<<<<<<< fdrtelegyr.cpp
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime() << " Bad point " << pointName << " pointid " << pointid << " groupid " << groupid << endl;
=======
            group_type = API_GET_SPO_CNT;
>>>>>>> 1.7.6.7
         }
<<<<<<< fdrtelegyr.cpp
         break;
      }
   }
=======
      }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   return( true );
}
=======
      if(( object_count > 0 ) && created[i] )    //don't enable empty/uncreated groups
      {
         int retCode;
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
=======
>>>>>>> 1.7.12.2
//=================================================================================================================================
//=================================================================================================================================

RWCString CtiFDRTelegyr::decipherReason( int transmissionReason )
{
   RWCString retReason;
=======
         if( cyclic )
         {
            //by specifying the NOALIG parameter, we are telling the telegyr system to go ahead and start sending
            //data back to use immediately
            retCode = api_enable_cyclic( channel_id, group_type, group_number, cycle_time, API_SECOND, API_ALIG_NOALIG );
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   switch( transmissionReason )
   {
   case API_REQ_DATA:
      retReason = "API_REQ_DATA";
      break;

   case API_DQS_DATA:
      retReason = "API_DQS_DATA";
      break;

   case  API_LIST_ALM_EVT:
      retReason = "API_LIST_ALM_EVT";
      break;
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " api_enable_cyclic group_number " << group_number << " returned " << retCode << endl;
            }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case API_ALARMS_LIST:
      retReason = "API_ALARMS_LIST";
      break;

   case API_INOUT_SCAN:
      retReason = "API_INOUT_SCAN";
      break;

   case API_CRE_RES:
      retReason = "API_CRE_RES";
      break;

   case API_DEL_RES:
      retReason = "API_DEL_RES";
      break;

   case API_ENDQS_RES:
      retReason = "API_ENDQS_RES";
      break;
=======
            cyclic = false;
         }
         else
         {
            retCode = api_enable_spontaneous( channel_id, group_type, group_number );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " api_enable_spontaneous group_number " << group_number << " returned " << retCode << endl;
            }
         }
=======
         // consumes and deletes pData memory
         sendMessageToDispatch(pCmdMsg);
>>>>>>> 1.9
      }
      else
      {
<<<<<<< fdrtelegyr.cpp
         {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " no points in " << _controlCenter.getTelegyrGroupList()[index].getGroupName() << endl;
         }
      }
   }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case API_ENLIST_RES:
      retReason = "API_ENLIST_RES";
      break;
=======
         pData = new CtiPointDataMsg( pointid, value, quality, AnalogPointType );
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
   case API_DISCYC_RES:
      retReason = "API_DISCYC_RES";
      break;
=======
   return( true );
}
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case API_DISSPO_RES:
      retReason = "API_DISSPO_RES";
      break;
=======
//=================================================================================================================================
//get point info from a database load/reload
//we are looking at the translation string and breaking it down to figure out what type the point is, what it's
//interval is, and what it's name is. Then we look for a group of the same specs that has room for the point
//=================================================================================================================================
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case API_DISDQS_RES:
      retReason = "API_DISDQS_RES";
      break;

   case API_DISLIST_RES:
      retReason = "API_DISLIST_RES";
      break;

   case API_DISCYCALL_RES:
      retReason = "API_DISCYCALL_RES";
      break;
=======
bool CtiFDRTelegyr::loadLists( CtiFDRPointList &aList )
{
   CtiFDRPoint          *translationPoint = NULL;
   RWCString            myTranslateName;
   RWCString            pointType;           //analog or status
   RWCString            pointStr;            //pointid
   RWCString            groupStr;            //now holds the rate to receive
   RWDBStatus           listStatus;
   bool                 successful( FALSE );
   bool                 foundPoint        = false;
   bool                 foundGroup        = false;
   int                  groupNum          = 1;
   int                  analogNum         = 1;
   int                  statusNum         = 51;
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case API_DISSPOALL_RES:
      retReason = "API_DISSPOALL_RES";
      break;
=======
   try
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " ----Loading And Building Groups---- " << endl;
      }

      // make a list with all received points
      CtiFDRManager *pointList = new CtiFDRManager( getInterfaceName(), RWCString( FDR_INTERFACE_RECEIVE ) );
>>>>>>> 1.7.6.7

   case API_COM_RES:
      retReason = "API_COM_RES";
      break;

<<<<<<< fdrtelegyr.cpp
   case API_WRI_RES:
      retReason = "API_WRI_RES";
      break;
=======
      // if status is ok, we were able to read the database at least
      if( listStatus.errorCode() == (RWDBStatus::ok))
      {
         //===================================================================================
         //seeing occasional problems where we get empty data sets back and there should be 
         //info in them,  we're checking this to see if is reasonable if the list may now be 
         //empty the 2 entry thing is completly arbitrary
         //===================================================================================

         if( ((pointList->entries() == 0) && (aList.getPointList()->entries() <= 2) ) || (pointList->entries() > 0))
=======
         //plop whichever message is valid onto the Dispatch pile
         if( pData )
>>>>>>> 1.9
         {
<<<<<<< fdrtelegyr.cpp
            // get iterator on list
            CtiFDRManager::CTIFdrPointIterator myIterator( pointList->getMap() );

            //iterate through all our points in the list
            for( ; myIterator(); )
            {
               foundPoint = true;
               translationPoint = myIterator.value();

               //iterate through all our destinations per point (should have 1 for telegyr)
               for( int x = 0; x < translationPoint->getDestinationList().size(); x++ )
               {
                  RWCTokenizer nextTranslate( translationPoint->getDestinationList()[x].getTranslation() );

                  //note: we're making a brand new token (string) out of the data before the ';'
                  if( !(myTranslateName = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of POINTID:xxxx 
                     RWCTokenizer nextTempToken( myTranslateName );

                     // do not care about the first part so toss it
                     nextTempToken( ":" );

                     //now we find the end of the string
                     pointStr = nextTempToken(";");
                     pointStr( 0, pointStr.length() ) = pointStr( 1, pointStr.length()-1 );  //shifts over 1
                  }
                   
                  if( !(myTranslateName = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of GROUP:xxxx (really, this is interval now)
                     RWCTokenizer nextTempToken( myTranslateName );

                     nextTempToken( ":" );

                     groupStr = nextTempToken(":");
                  }
                  
                  if( !(myTranslateName = nextTranslate( ";" ) ).isNull() )
                  {
                     // this in the form of POINTTYPE:xxxx
                     RWCTokenizer nextTempToken( myTranslateName );

                     nextTempToken( ":" );

                     pointType = nextTempToken(":");

                     if( !pointType.isNull() )
                     {
                        successful = true;
                     }
                  }
                  
                  translationPoint->getDestinationList()[x].setTranslation( pointStr );
               }

               //we need to spin through the list
               for( int i = 0; i < _controlCenter.getTelegyrGroupList().size(); i++ )
               {
                  RWCString type = _controlCenter.getTelegyrGroupList()[i].getGroupType();
                  int interval = _controlCenter.getTelegyrGroupList()[i].getInterval();
                  int size = _controlCenter.getTelegyrGroupList()[i].getPointList().size();
                                    
                  type.toLower();
                  pointType.toLower();
                  
//                  if(( type == pointType ) && ( interval == atoi( groupStr ) ) && ( size < 127 ))
                  if(( type == pointType ) && ( size < 127 ))
                  {
                     _controlCenter.getTelegyrGroupList()[i].getPointList().push_back( *translationPoint );
                     foundGroup = true;
                     break;
                  }
               }
               
               //we didn't stick the point anywhere, make a new group and put it there
               if( !foundGroup )
               {
                  CtiTelegyrGroup tempGroup;
                  RWCString comp( "status" );
                  char lum[15];

                  if( pointType == comp )
                  {
                     char *num = itoa( statusNum, lum, 10 );
                     groupNum = statusNum;
                     statusNum++;
                  }
                  else
                  {
                     char *num = itoa( analogNum, lum, 10 );
                     groupNum = analogNum;
                     analogNum++;
                  }
                  
                  RWCString name( pointType + lum );
                  tempGroup.setGroupID( groupNum );         
                  tempGroup.setGroupName( name );    

//                  tempGroup.setInterval( atoi( groupStr ) );
                  tempGroup.setInterval( 120 );             //just temp until we fix MEC's database

                  pointType.toLower();

                  tempGroup.setGroupType( pointType );
                  tempGroup.getPointList().push_back( *translationPoint );
                  _controlCenter.addToGroupList( tempGroup );
               }
               else
               {
                  foundGroup = false;     //reset for the next point
               }
            }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case API_WRT_RES:
      retReason = "API_WRT_RES";
      break;

   case API_TAG_RES:
      retReason = "API_TAG_RES";
      break;

   case API_DQS_RES:
      retReason = "API_DQS_RES";
      break;

   case API_ALARM_ACK_DELETE_RES:
      retReason = "API_ALARM_ACK_DELETE_RES";
      break;

   case API_SETPADQMEA_RES:
      retReason = "API_SETPADQMEA_RES";
      break;

   case API_SETPADQIND_RES:
      retReason = "API_SETPADQIND_RES";
      break;

   case API_SETPADQCNT_RES:
      retReason = "API_SETPADQCNT_RES";
      break;

   case API_DISPLAY_REQUEST_RES:
      retReason = "API_DISPLAY_REQUEST_RES";
      break;

   case API_GROUPS:
      retReason = "API_GROUPS";
      break;

   case API_READ_GRP:
      retReason = "API_READ_GRP";
      break;

<<<<<<< fdrtelegyr.cpp
   case API_DISC_NOTIFY:
      retReason = "API_DISC_NOTIFY";
      break;

=======
>>>>>>> 1.7.12.2
   case API_FULL_NOTIFY:
      retReason = "API_FULL_NOTIFY";
      break;

   default:
      retReason = "UNKNOWN CODE";
      break;
   }
   //break;
   return( retReason );
}

//=================================================================================================================================
//here we read our Cparms to get our settings
//=================================================================================================================================

int CtiFDRTelegyr::readConfig( void )
{
   int         successful = TRUE;                      //need to read the cparms for telegyr
   RWCString   tempStr;

   tempStr = getCparmValueAsString( KEY_DB_RELOAD_RATE );

   if( tempStr.length() > 0 )
      setReloadRate( atoi( tempStr ) );
   else
      setReloadRate( 3600 );

   tempStr = getCparmValueAsString( KEY_HI_REASONABILITY_FILTER );

   if( tempStr.length() > 0 )
      setHiReasonabilityFilter( atof( tempStr ) );

   tempStr = getCparmValueAsString( KEY_DEBUG_MODE );

   if( tempStr.length() > 0 )
      setInterfaceDebugMode( true );
   else
      setInterfaceDebugMode( false );

   tempStr = getCparmValueAsString( KEY_API_PATH );

   if( tempStr.length() > 0 )
      setPath( tempStr );
   else
      setPath( "c:/yukon/telegyr/api/" );             //not sure what else would be appropriate

   tempStr = getCparmValueAsString( KEY_APPLICATION_NAME );
=======
            delete pointList;  //????
            pointList=NULL;
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   if( tempStr.length() > 0 )
      _appName = tempStr;
   else
      _appName = "dev_yukon";
=======
            if( !successful )
            {
               if( !foundPoint )
               {
                  // means there was nothing in the list, wait until next db change or reload
                  successful = true;
               }
            }
         }
         else
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
            }
            successful = false;
=======
            // consumes a delete memory
            queueMessageToDispatch( pData );
>>>>>>> 1.9
         }
      }
      returnCode = true;
   }
   else
   {
      returnCode = false;
   }

   return returnCode;
}

//=================================================================================================================================
//we want to decipher the point and put it on the queue to Dispatch
//=================================================================================================================================
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   tempStr = getCparmValueAsString( KEY_API_VERSION );
=======
bool CtiFDRTelegyr::processAnalog( APICLI_GET_MEA aPoint, int groupid, int index )
=======
bool CtiFDRTelegyr::processDigital( APICLI_GET_IND aPoint, int groupid, int index )
>>>>>>> 1.9
{
   CtiPointDataMsg      *pData      = NULL;
   long                 pointid;
   bool                 returnCode;
   int                  x;
   double               value;
   USHORT               quality;
<<<<<<< fdrtelegyr.cpp
   bool                 nonUpdated = false;
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   if( tempStr.length() > 0 )
      _apiVersion = tempStr;
   else
      _apiVersion = "v4_1";
=======
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
   //defaults for the control center
=======
   if( aPoint.mea_valid == API_VALID )
=======
   if( aPoint.ind_valid == API_VALID )
>>>>>>> 1.9
   {
<<<<<<< fdrtelegyr.cpp
      value = aPoint.mea_value.mea4_value;
      raw = value;
>>>>>>> 1.7.6.7
=======
      value = aPoint.ind_value;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
   tempStr = getCparmValueAsString( KEY_OPERATOR );
=======
      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   if( tempStr.length() > 0 )
      _controlCenter.setOperator( tempStr );
   else
      _controlCenter.setOperator( "LGS" );


   tempStr = getCparmValueAsString( KEY_PASSWORD );

<<<<<<< fdrtelegyr.cpp
   if( tempStr.length() > 0 )
      _controlCenter.setPassword( tempStr );
   else
      _controlCenter.setPassword( "1234" );
=======
            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime() << " Raw Analog Value: " << raw << " from Point " << _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0) << " groupid " << groupid << endl;
            }
>>>>>>> 1.7.6.7

   tempStr = getCparmValueAsString( KEY_SYSTEM_NAME );

<<<<<<< fdrtelegyr.cpp
   if( tempStr.length() > 0 )
      _controlCenter.setSysName( tempStr );
   else
      _controlCenter.setSysName( "MEC" );
=======
      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
				if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
				{
					CtiLockGuard<CtiLogger> doubt_guard( dout );
					dout << RWTime() << " Raw Digital Value: " << value << " from Point " << _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0) << " groupid " << groupid << endl;
				}

            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();
            break;
         }
      }
>>>>>>> 1.9

   tempStr = getCparmValueAsString( KEY_CHANNEL_ID );
=======
      //get the quality of the point data
      if( api_check_any_quality_bit_set( aPoint.sys_dependent_info ) )
      {
         quality = NormalQuality;
      }
      else
      {  //there are a bunch more that telegyr supports, we just don't care
         if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_OUT_SCAN_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TELEM_F_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_Q_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TEST_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_BACKUP_BIT ) )
            quality = QuestionableQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_UN_INIT_BIT ) )
            quality = UnintializedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MISSING_BIT ) )
            quality = PartialIntervalQuality;
         else
            quality = NormalQuality;
      }
<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
=======
      pData = new CtiPointDataMsg( pointid, value, quality, StatusPointType );
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
   if( tempStr.length() > 0 )
      _controlCenter.setChannelID( atof( tempStr ) );
   else
      _controlCenter.setChannelID( 1 );

   tempStr = getCparmValueAsString( KEY_ACCESS );

   if( tempStr.length() > 0 )
      _controlCenter.setAccess( atof( tempStr ) );
   else
      _controlCenter.setAccess( 1 );

   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
=======
      if( pData )
      {
         // consumes a delete memory
         queueMessageToDispatch( pData );
      }
      returnCode = true;
   }
   else
>>>>>>> 1.9
   {
      dout << endl;
      dout << endl;
      dout << "-----------------------Configs---------------------------------" << endl;
      dout << "Our Name    : " << getCparmValueAsString( KEY_APPLICATION_NAME ) << endl;
      dout << "Reload Rate : " << getCparmValueAsString( KEY_DB_RELOAD_RATE ) << " secs" << endl;
      dout << "Hi_Res      : " << getCparmValueAsString( KEY_HI_REASONABILITY_FILTER ) << endl;
      dout << "Debug Mode  : " << getCparmValueAsString( KEY_DEBUG_MODE ) << endl;
      dout << "Api path    : " << getCparmValueAsString( KEY_API_PATH ) << endl;
      dout << "Api Version : " << getCparmValueAsString( KEY_API_VERSION ) << endl;
      dout << "Operator    : " << getCparmValueAsString( KEY_OPERATOR ) << endl;
      dout << "Password    : " << getCparmValueAsString( KEY_PASSWORD ) << endl;
      dout << "Sysname     : " << getCparmValueAsString( KEY_SYSTEM_NAME ) << endl;
      dout << "Channel Id  : " << getCparmValueAsString( KEY_CHANNEL_ID ) << endl;
      dout << "Access      : " << getCparmValueAsString( KEY_ACCESS ) << endl;
      dout << "-----------------------Configs---------------------------------" << endl;
      dout << endl;
      dout << endl;
   }

   return successful;
}

//=================================================================================================================================
//=================================================================================================================================

double CtiFDRTelegyr::getHiReasonabilityFilter() const
{
   return _hiReasonabilityFilter;
}

//=================================================================================================================================
//=================================================================================================================================

CtiFDRTelegyr & CtiFDRTelegyr::setHiReasonabilityFilter(const double myValue)
{
   _hiReasonabilityFilter = myValue;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

<<<<<<< fdrtelegyr.cpp
long CtiFDRTelegyr::getLinkStatusID( void ) const
=======
bool CtiFDRTelegyr::processCounter( APICLI_GET_CNT aPoint, int groupid, int index )
>>>>>>> 1.9
{
   return _linkStatusID;
}

//=================================================================================================================================
//=================================================================================================================================

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
CtiFDRTelegyr & CtiFDRTelegyr::setLinkStatusID( const long aPointID )
{
   _linkStatusID = aPointID;
   return *this;
}
=======
   if( aPoint.cnt_valid == API_VALID )
   {
      value = aPoint.cnt_value.cnt8_value;
>>>>>>> 1.9

//=================================================================================================================================
//=================================================================================================================================
=======
      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
RWCString CtiFDRTelegyr::getPath( void )
{
   return _path;
}
=======
      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();
            break;
         }
      }
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
//=================================================================================================================================
//=================================================================================================================================
=======
      //get the quality of the point data
      if( api_check_any_quality_bit_set( aPoint.sys_dependent_info ) )
      {
         quality = NormalQuality;
      }
      else
      {  //there are a bunch more that telegyr supports, we just don't care
         if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_OUT_SCAN_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TELEM_F_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_Q_MAN_BIT ) )
            quality = ManualQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_TEST_BIT ) )
            quality = NonUpdatedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_BACKUP_BIT ) )
            quality = QuestionableQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_UN_INIT_BIT ) )
            quality = UnintializedQuality;
         else if( api_check_quality_bit_set( aPoint.sys_dependent_info, API_QUAL_MISSING_BIT ) )
            quality = PartialIntervalQuality;
         else
            quality = NormalQuality;
      }
      pData = new CtiPointDataMsg( pointid, value, quality, PulseAccumulatorPointType );

      if( pData )
      {
         // consumes a delete memory
         queueMessageToDispatch( pData );
      }
      returnCode = true;
   }
   else
   {
      returnCode = false;
   }

   return returnCode;
}


//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::processBadPoint( int groupid, int index )
{
   long                 pointid;
   int                  x;
   RWCString				pointName;

	for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
	{
		if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
		{
			pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();
			pointName = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0);

			{
				CtiLockGuard<CtiLogger> doubt_guard( dout );
				dout << RWTime() << " Bad point " << pointName << " pointid " << pointid << " groupid " << groupid << endl;
			}
			break;
		}
	}

   return( true );
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiFDRTelegyr::decipherReason( int transmissionReason )
{
   RWCString retReason;

   switch( transmissionReason )
   {
   case API_REQ_DATA:
      retReason = "API_REQ_DATA";
      break;

   case API_DQS_DATA:
      retReason = "API_DQS_DATA";
      break;

   case  API_LIST_ALM_EVT:
      retReason = "API_LIST_ALM_EVT";
      break;

   case API_ALARMS_LIST:
      retReason = "API_ALARMS_LIST";
      break;

   case API_INOUT_SCAN:
      retReason = "API_INOUT_SCAN";
      break;

   case API_CRE_RES:
      retReason = "API_CRE_RES";
      break;

   case API_DEL_RES:
      retReason = "API_DEL_RES";
      break;

   case API_ENDQS_RES:
      retReason = "API_ENDQS_RES";
      break;

   case API_ENLIST_RES:
      retReason = "API_ENLIST_RES";
      break;

   case API_DISCYC_RES:
      retReason = "API_DISCYC_RES";
      break;

   case API_DISSPO_RES:
      retReason = "API_DISSPO_RES";
      break;

   case API_DISDQS_RES:
      retReason = "API_DISDQS_RES";
      break;

   case API_DISLIST_RES:
      retReason = "API_DISLIST_RES";
      break;

   case API_DISCYCALL_RES:
      retReason = "API_DISCYCALL_RES";
      break;

   case API_DISSPOALL_RES:
      retReason = "API_DISSPOALL_RES";
      break;

   case API_COM_RES:
      retReason = "API_COM_RES";
      break;

   case API_WRI_RES:
      retReason = "API_WRI_RES";
      break;

   case API_WRT_RES:
      retReason = "API_WRT_RES";
      break;

   case API_TAG_RES:
      retReason = "API_TAG_RES";
      break;

   case API_DQS_RES:
      retReason = "API_DQS_RES";
      break;

   case API_ALARM_ACK_DELETE_RES:
      retReason = "API_ALARM_ACK_DELETE_RES";
      break;

   case API_SETPADQMEA_RES:
      retReason = "API_SETPADQMEA_RES";
      break;

   case API_SETPADQIND_RES:
      retReason = "API_SETPADQIND_RES";
      break;

   case API_SETPADQCNT_RES:
      retReason = "API_SETPADQCNT_RES";
      break;

   case API_DISPLAY_REQUEST_RES:
      retReason = "API_DISPLAY_REQUEST_RES";
      break;

   case API_GROUPS:
      retReason = "API_GROUPS";
      break;

   case API_READ_GRP:
      retReason = "API_READ_GRP";
      break;

	case API_DISC_NOTIFY:
		retReason = "API_DISC_NOTIFY";
		break;

   case API_FULL_NOTIFY:
      retReason = "API_FULL_NOTIFY";
      break;

   default:
      retReason = "UNKNOWN CODE";
      break;
   }
   //break;
   return( retReason );
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiFDRTelegyr::decipherError( int status )
{
   RWCString retReason;

   switch( status )
   {
   case API_NORMAL:
      retReason = "API_NORMAL";
      break;

   case APIERR_GENERIC:
      retReason = "APIERR_GENERIC";
      break;

   case APIERR_FILE_OPEN:
      retReason = "APIERR_FILE_OPEN";
      break;

   case APIERR_FILE_CLOSE:
      retReason = "APIERR_FILE_CLOSE";
      break;


   case APIERR_FILE_READ:
      retReason = "APIERR_FILE_READ";
      break;

   case APIERR_NO_MEMORY:
      retReason = "APIERR_NO_MEMORY";
      break;

   case APIERR_NUL_PTR_PAR:
      retReason = "APIERR_NUL_PTR_PAR";
      break;

   case APIERR_PROC_CRE_FAIL:
      retReason = "APIERR_PROC_CRE_FAIL";
      break;

   case APIERR_FATAL:
      retReason = "APIERR_FATAL";
      break;

   case APIERR_CLIENT_TOO_L:
      retReason = "APIERR_CLIENT_TOO_L";
      break;

   case APIERR_OPERATOR_TOO_L:
      retReason = "APIERR_OPERATOR_TOO_L";
      break;

   case APIERR_PASSWORD_TOO_L:
      retReason = "APIERR_PASSWORD_TOO_L";
      break;

   case APIERR_SYSTEM_TOO_L:
      retReason = "APIERR_SYSTEM_TOO_L";
      break;

   case APIERR_OUT_OF_OPER:
      retReason = "APIERR_OUT_OF_OPER";
      break;

   case APIERR_UNK_SYS_NAM:
      retReason = "APIERR_UNK_SYS_NAM";
      break;

   case APIERR_SYS_ALR_CON:
      retReason = "APIERR_SYS_ALR_CON";
      break;

   case APIERR_NO_CNF_FILE:
      retReason = "APIERR_NO_CNF_FILE";
      break;

   case APIERR_NO_HOST_NAM:
      retReason = "APIERR_NO_HOST_NAM";
      break;

   case APIERR_SRV_NOT_READY:
      retReason = "APIERR_SRV_NOT_READY";
      break;

   case APIERR_TOO_MNY_CON:
      retReason = "APIERR_TOO_MNY_CON";
      break;

   case APIERR_INV_CHAN_ID:
      retReason = "APIERR_INV_CHAN_ID";
      break;

   case APIERR_INV_CLI_ID:
      retReason = "APIERR_INV_CLI_ID";
      break;

   case APIERR_INV_CLI_REQ:
      retReason = "APIERR_INV_CLI_REQ";
      break;

   case APIERR_INV_GRP_TYP:
      retReason = "APIERR_INV_GRP_TYP";
      break;

   case APIERR_INV_GRP_NBR:
      retReason = "APIERR_INV_GRP_NBR";
      break;

   case APIERR_INV_OBJ_COU:
      retReason = "APIERR_INV_OBJ_COU";
      break;

   case APIERR_INV_EVENT:
      retReason = "APIERR_INV_EVENT";
      break;

   case APIERR_CLI_NO_COMP:
      retReason = "APIERR_CLI_NO_COMP";
      break;

   case APIERR_INV_PAT_NAM:
      retReason = "APIERR_INV_PAT_NAM";
      break;

   case APIERR_INV_ACC_TYP:
      retReason = "APIERR_INV_ACC_TYP";
      break;

   case APIERR_INV_CLI_NAM:
      retReason = "APIERR_INV_CLI_NAM";
      break;

   case APIERR_INV_OPE_NAM:
      retReason = "APIERR_INV_OPE_NAM";
      break;

   case APIERR_INV_PASSW:
      retReason = "APIERR_INV_PASSW";
      break;

   case APIERR_INV_CEN_NAM:
      retReason = "APIERR_INV_CEN_NAM";
      break;

   case APIERR_INV_COMMAND:
      retReason = "APIERR_INV_COMMAND";
      break;

   case APIERR_INV_PRG_NAM:
      retReason = "APIERR_INV_PRG_NAM";
      break;

   case APIERR_INV_TIM_UNI:
      retReason = "APIERR_INV_TIM_UNI";
      break;

   case APIERR_INV_TIM_SIZ:
      retReason = "APIERR_INV_TIM_SIZ";
      break;

   case APIERR_INV_IDX_RAN:
      retReason = "APIERR_INV_IDX_RAN";
      break;

   case APIERR_INV_VALUE:
      retReason = "APIERR_INV_VALUE";
      break;

   case APIERR_INV_DATE:
      retReason = "APIERR_INV_DATE";
      break;

   case APIERR_DATE_NOT_ARCH:
      retReason = "APIERR_DATE_NOT_ARCH";
      break;

   case APIERR_INV_OBJ:
      retReason = "APIERR_INV_OBJ";
      break;

   case APIERR_INV_CHECKSUM:
      retReason = "APIERR_INV_CHECKSUM";
      break;

   case APIERR_INV_AOR:
      retReason = "APIERR_INV_AOR";
      break;

   case APIERR_INV_CODE:
      retReason = "APIERR_INV_CODE";
      break;

   case APIERR_INV_PARAM:
      retReason = "APIERR_INV_PARAM";
      break;

   case APIERR_INV_TAG_TYPE:
      retReason = "APIERR_INV_TAG_TYPE";
      break;

   case APIERR_BAD_TAG:
      retReason = "APIERR_BAD_TAG";
      break;

   case APIERR_GRP_DEF_SUB:
      retReason = "APIERR_GRP_DEF_SUB";
      break;

   case APIERR_GRP_DEF_MSG:
      retReason = "APIERR_GRP_DEF_MSG";
      break;

   case APIERR_GRP_CEL_MSG:
      retReason = "APIERR_GRP_CEL_MSG";
      break;

   case APIERR_GRP_TOO_LAR:
      retReason = "APIERR_GRP_TOO_LAR";
      break;

   case APIERR_GRP_TOO_MNY:
      retReason = "APIERR_GRP_TOO_MNY";
      break;

   case APIERR_GRP_NOT_FOU:
      retReason = "APIERR_GRP_NOT_FOU";
      break;

   case APIERR_GRP_INV_IDX:
      retReason = "APIERR_GRP_INV_IDX";
      break;

   case APIERR_GRP_NOT_ACC:
      retReason = "APIERR_GRP_NOT_ACC";
      break;

   case APIERR_GRP_NOT_SAV:
      retReason = "APIERR_GRP_NOT_SAV";
      break;

   case APIERR_GRP_IS_ENA:
      retReason = "APIERR_GRP_IS_ENA";
      break;

   case APIERR_GRP_NOT_ENA:
      retReason = "APIERR_GRP_NOT_ENA";
      break;

   case APIERR_BUF_TOO_SMA:
      retReason = "APIERR_BUF_TOO_SMA";
      break;

   case APIERR_INV_BUF_SIZ:
      retReason = "APIERR_INV_BUF_SIZ";
      break;

   case APIERR_NOT_REQ_DAT:
      retReason = "APIERR_NOT_REQ_DAT";
      break;

   case APIERR_NOT_UNP_DAT:
      retReason = "APIERR_NOT_UNP_DAT";
      break;

   case APIERR_NO_SUCH_INC:
      retReason = "APIERR_NO_SUCH_INC";
      break;

   case APIERR_NOT_UNP_MEA:
      retReason = "APIERR_NOT_UNP_MEA";
      break;

   case APIERR_NOT_UNP_CNT:
      retReason = "APIERR_NOT_UNP_CNT";
      break;

   case APIERR_NOT_UNP_IND:
      retReason = "APIERR_NOT_UNP_IND";
      break;

   case APIERR_NOT_UNP_GEN:
      retReason = "APIERR_NOT_UNP_GEN";
      break;

   case APIERR_NOT_UNP_DQS:
      retReason = "APIERR_NOT_UNP_DQS";
      break;

   case APIERR_NOT_UNP_LIST:
      retReason = "APIERR_NOT_UNP_LIST";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
CtiFDRTelegyr & CtiFDRTelegyr::setPath( RWCString inPath )
{
   _path = inPath;
   return *this;
}
=======
   case APIERR_NOT_UNP_EQUIP:
      retReason = "APIERR_NOT_UNP_EQUIP";
      break;

   case APIERR_NOT_UNP_TAG:
      retReason = "APIERR_NOT_UNP_TAG";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
//=================================================================================================================================
//=================================================================================================================================
=======
   case APIERR_DQS_IS_ENA:
      retReason = "APIERR_DQS_IS_ENA";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
void CtiFDRTelegyr::sendLinkState( int aState )
{
   if( getLinkStatusID() != 0 )
   {
      CtiPointDataMsg     *pData;
=======
   case APIERR_DQS_NOT_ENA:
      retReason = "APIERR_DQS_NOT_ENA";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
      pData = new CtiPointDataMsg( getLinkStatusID(), aState, NormalQuality, StatusPointType );
      sendMessageToDispatch( pData );
   }
}
=======
   case APIERR_NO_SUCH_PRC:
      retReason = "APIERR_NO_SUCH_PRC";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
//=================================================================================================================================
//=================================================================================================================================
=======
      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out
>>>>>>> 1.7.6.7
=======
   case APIERR_DQS_PRC_OFF:
      retReason = "APIERR_DQS_PRC_OFF";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
bool CtiFDRTelegyr::isConnected( void )
{
   return _connected;
}
=======
   case APIERR_DQS_GEN_FAIL:
      retReason = "APIERR_DQS_GEN_FAIL";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
//=================================================================================================================================
//=================================================================================================================================
=======
   case APIERR_APPL_NOT_CONN:
      retReason = "APIERR_APPL_NOT_CONN";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
CtiFDRTelegyr & CtiFDRTelegyr::setConnected( bool conn )
{
   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " setting connected to " << conn << endl;
   }
   _connected = conn;
   return *this;
}
=======
   case APIERR_DQSPKT_TOO_LAR:
      retReason = "APIERR_DQSPKT_TOO_LAR";
      break;

   case APIERR_FAIL_PRI_WRT:
      retReason = "APIERR_FAIL_PRI_WRT";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
//=================================================================================================================================
//runs the interface
//=================================================================================================================================
=======
   case APIERR_WRT_NOT_ALW:
      retReason = "APIERR_WRT_NOT_ALW";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
BOOL CtiFDRTelegyr::run( void )
=======
bool CtiFDRTelegyr::processBadPoint( int groupid, int index )
{
   long                 pointid;
   int                  x;
   RWCString            pointName;
=======
   case APIERR_IS_OUT_SCAN:
      retReason = "APIERR_IS_OUT_SCAN";
      break;
>>>>>>> 1.9

   case APIERR_CTL_NOT_ALW:
      retReason = "APIERR_CTL_NOT_ALW";
      break;

   case APIERR_CTL_IN_PRG:
      retReason = "APIERR_CTL_IN_PRG";
      break;

   case APIERR_CTL_IS_TAG:
      retReason = "APIERR_CTL_IS_TAG";
      break;

   case APIERR_CTL_PRE_OP:
      retReason = "APIERR_CTL_PRE_OP";
      break;

<<<<<<< fdrtelegyr.cpp
RWCString CtiFDRTelegyr::decipherReason( int transmissionReason )
>>>>>>> 1.7.6.7
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " ----- Starting FDR Telegyr Version " << FDR_TELEGYR_VERSION << endl;
   }

   // crank up the base class
   Inherited::run();

   // startup our interfaces
   _threadGetTelegyrData.start();

   return TRUE;
}

//=================================================================================================================================
//=================================================================================================================================

BOOL CtiFDRTelegyr::stop( void )
{
   int               reason;
   int               func_status;
   int               channel_id;
   int               group_type;
   int               group_num;
   APICLI_TIME       group_time;
   int               first_index;
   int               last_index;
   int               more;
   int               result[256];

   _threadGetTelegyrData.requestCancellation();

   if( isConnected() )
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ----- Going to delete due to shutdown" << endl;
      }
      deleteGroups();

      for( int i = 0; i < 100; i++ )
      {
         int returnCode = api_get_queued_data( 0,
                                           API_NO_TIMEOUT,
                                           &reason,
                                           &func_status,
                                           &channel_id,
                                           &group_type,
                                           &group_num,
                                           &group_time,
                                           &first_index,
                                           &last_index,
                                           &more,
                                           &result[0] );
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " ----- shutdown queued data got " << decipherReason( reason ) << endl;
         }

         if(( reason == API_DISC_NOTIFY ) || ( reason == API_DELALL_RES ))
         {
            break;
         }
         Sleep( 250 );
      }

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ----- Going to disconnect due to shutdown" << endl;
      }
      
      int disconn = api_disconnect( _controlCenter.getChannelID(), API_VALID );
      
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ----- Disconnect returned " << disconn << endl;
      }
      /*
      if( _inited )
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " ----- Stopping the api due to shutdown" << endl;
         }
         int end = api_end();
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " -----api shutdown returned " << end << endl;
         }
      } */
   }

   // stop the base class
   Inherited::stop();

   return(TRUE);
}

//=================================================================================================================================
//      Here Starts some C functions that are used to Start the
//     Interface and Stop it from the Main() of FDR.EXE.
//=================================================================================================================================

#ifdef __cplusplus
extern "C"{
#endif

//=================================================================================================================================
// Function Name: Extern C int RunInterface(void)
//
// Description: This is used to Start the Interface from the Main()
//              of FDR.EXE. Each interface it Dynamically loaded and
//             this function creates a global FDRTelegyr Object and then
//              calls its run method to crank it up.
//=================================================================================================================================

DLLEXPORT int RunInterface( void )
{
   // make a point to the interface
   myInterface = new CtiFDRTelegyr();

   // now start it up
   return myInterface->run();
}

//=================================================================================================================================
// Function Name: Extern C int StopInterface(void)
//
// Description: This is used to Stop the Interface from the Main()
//              of FDR.EXE. Each interface i2 Dynamically loaded and
//              this function stops a global FDRTelegyr Object and then
//              deletes it.
//
//=================================================================================================================================

<<<<<<< fdrtelegyr.cpp
DLLEXPORT int StopInterface( void )
{
   myInterface->stop();
   delete myInterface;
   myInterface = 0;
=======
   case API_DQS_RES:
      retReason = "API_DQS_RES";
      break;

   case API_ALARM_ACK_DELETE_RES:
      retReason = "API_ALARM_ACK_DELETE_RES";
      break;

   case API_SETPADQMEA_RES:
      retReason = "API_SETPADQMEA_RES";
      break;

   case API_SETPADQIND_RES:
      retReason = "API_SETPADQIND_RES";
      break;

   case API_SETPADQCNT_RES:
      retReason = "API_SETPADQCNT_RES";
      break;

   case API_DISPLAY_REQUEST_RES:
      retReason = "API_DISPLAY_REQUEST_RES";
      break;

   case API_GROUPS:
      retReason = "API_GROUPS";
      break;

   case API_READ_GRP:
      retReason = "API_READ_GRP";
      break;

   case API_DISC_NOTIFY:
      retReason = "API_DISC_NOTIFY";
      break;

   case API_FULL_NOTIFY:
      retReason = "API_FULL_NOTIFY";
      break;
>>>>>>> 1.7.6.7

   return 0;
}

/*
//=================================================================================================================================
//here we read our Cparms to get our settings
//=================================================================================================================================

int CtiFDRTelegyr::readConfig( void )
{
   int         successful = TRUE;                      //need to read the cparms for telegyr
   RWCString   tempStr;

   tempStr = getCparmValueAsString( KEY_DB_RELOAD_RATE );

   if( tempStr.length() > 0 )
      setReloadRate( atoi( tempStr ) );
   else
      setReloadRate( 3600 );

   tempStr = getCparmValueAsString( KEY_HI_REASONABILITY_FILTER );

   if( tempStr.length() > 0 )
      setHiReasonabilityFilter( atof( tempStr ) );

   tempStr = getCparmValueAsString( KEY_DEBUG_MODE );

   if( tempStr.length() > 0 )
      setInterfaceDebugMode( true );
   else
      setInterfaceDebugMode( false );

   tempStr = getCparmValueAsString( KEY_API_PATH );

   if( tempStr.length() > 0 )
      setPath( tempStr );
   else
      setPath( "c:/yukon/telegyr/api/" );             //not sure what else would be appropriate

<<<<<<< fdrtelegyr.cpp
   case APIERR_PROC_CRE_FAIL:
      retReason = "APIERR_PROC_CRE_FAIL";
      break;

   case APIERR_FATAL:
      retReason = "APIERR_FATAL";
      break;

   case APIERR_CLIENT_TOO_L:
      retReason = "APIERR_CLIENT_TOO_L";
      break;

   case APIERR_OPERATOR_TOO_L:
      retReason = "APIERR_OPERATOR_TOO_L";
      break;

   case APIERR_PASSWORD_TOO_L:
      retReason = "APIERR_PASSWORD_TOO_L";
      break;

   case APIERR_SYSTEM_TOO_L:
      retReason = "APIERR_SYSTEM_TOO_L";
      break;

   case APIERR_OUT_OF_OPER:
      retReason = "APIERR_OUT_OF_OPER";
      break;

   case APIERR_UNK_SYS_NAM:
      retReason = "APIERR_UNK_SYS_NAM";
      break;

   case APIERR_SYS_ALR_CON:
      retReason = "APIERR_SYS_ALR_CON";
      break;

   case APIERR_NO_CNF_FILE:
      retReason = "APIERR_NO_CNF_FILE";
      break;

   case APIERR_NO_HOST_NAM:
      retReason = "APIERR_NO_HOST_NAM";
      break;

   case APIERR_SRV_NOT_READY:
      retReason = "APIERR_SRV_NOT_READY";
      break;

   case APIERR_TOO_MNY_CON:
      retReason = "APIERR_TOO_MNY_CON";
      break;

   case APIERR_INV_CHAN_ID:
      retReason = "APIERR_INV_CHAN_ID";
      break;

   case APIERR_INV_CLI_ID:
      retReason = "APIERR_INV_CLI_ID";
      break;

   case APIERR_INV_CLI_REQ:
      retReason = "APIERR_INV_CLI_REQ";
      break;

   case APIERR_INV_GRP_TYP:
      retReason = "APIERR_INV_GRP_TYP";
      break;

   case APIERR_INV_GRP_NBR:
      retReason = "APIERR_INV_GRP_NBR";
      break;

   case APIERR_INV_OBJ_COU:
      retReason = "APIERR_INV_OBJ_COU";
      break;

   case APIERR_INV_EVENT:
      retReason = "APIERR_INV_EVENT";
      break;

   case APIERR_CLI_NO_COMP:
      retReason = "APIERR_CLI_NO_COMP";
      break;

   case APIERR_INV_PAT_NAM:
      retReason = "APIERR_INV_PAT_NAM";
      break;

   case APIERR_INV_ACC_TYP:
      retReason = "APIERR_INV_ACC_TYP";
      break;

   case APIERR_INV_CLI_NAM:
      retReason = "APIERR_INV_CLI_NAM";
      break;

   case APIERR_INV_OPE_NAM:
      retReason = "APIERR_INV_OPE_NAM";
      break;

   case APIERR_INV_PASSW:
      retReason = "APIERR_INV_PASSW";
      break;

   case APIERR_INV_CEN_NAM:
      retReason = "APIERR_INV_CEN_NAM";
      break;

   case APIERR_INV_COMMAND:
      retReason = "APIERR_INV_COMMAND";
      break;
=======
   case APIERR_NO_DATA:
      retReason = "APIERR_NO_DATA";
      break;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_PRG_NAM:
      retReason = "APIERR_INV_PRG_NAM";
=======
   case APIERR_READ_DEF_MSG:
      retReason = "APIERR_READ_DEF_MSG";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_TIM_UNI:
      retReason = "APIERR_INV_TIM_UNI";
=======
   case APIERR_WRI_DEF_MSG:
      retReason = "APIERR_WRI_DEF_MSG";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_TIM_SIZ:
      retReason = "APIERR_INV_TIM_SIZ";
=======
   case APIERR_FUNC_NOT_SUPPORTED:
      retReason = "APIERR_FUNC_NOT_SUPPORTED";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_IDX_RAN:
      retReason = "APIERR_INV_IDX_RAN";
=======
   case APIERR_LIST_NOT_CMPLT:
      retReason = "APIERR_LIST_NOT_CMPLT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_VALUE:
      retReason = "APIERR_INV_VALUE";
=======
   case APIERR_INV_ALMLIST_INDEX:
      retReason = "APIERR_INV_ALMLIST_INDEX";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_DATE:
      retReason = "APIERR_INV_DATE";
=======
   case APIERR_INVALID_CRT_NUM:
      retReason = "APIERR_INVALID_CRT_NUM";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_DATE_NOT_ARCH:
      retReason = "APIERR_DATE_NOT_ARCH";
=======
   case APIERR_CRT_NOT_CONNECTED:
      retReason = "APIERR_CRT_NOT_CONNECTED";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_OBJ:
      retReason = "APIERR_INV_OBJ";
=======
   case APIERR_INVALID_WINDOW_NUM:
      retReason = "APIERR_INVALID_WINDOW_NUM";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_CHECKSUM:
      retReason = "APIERR_INV_CHECKSUM";
=======
   case APIERR_WINDOW_NOT_ACTIVE:
      retReason = "APIERR_WINDOW_NOT_ACTIVE";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_AOR:
      retReason = "APIERR_INV_AOR";
=======
   case APIERR_NO_SUCH_DISPLAY_FILE:
      retReason = "APIERR_NO_SUCH_DISPLAY_FILE";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_CODE:
      retReason = "APIERR_INV_CODE";
=======
   case APIERR_VALUE_OUT_OF_RANGE:
      retReason = "APIERR_VALUE_OUT_OF_RANGE";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_PARAM:
      retReason = "APIERR_INV_PARAM";
=======
   case APIERR_NOTIME:
      retReason = "APIERR_NOTIME";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_TAG_TYPE:
      retReason = "APIERR_INV_TAG_TYPE";
=======
   case APIERR_CONNECT:
      retReason = "APIERR_CONNECT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_BAD_TAG:
      retReason = "APIERR_BAD_TAG";
=======
   case APIERR_DISCONNECT:
      retReason = "APIERR_DISCONNECT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_DEF_SUB:
      retReason = "APIERR_GRP_DEF_SUB";
=======
   case APIERR_LAN_FAIL:
      retReason = "APIERR_LAN_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_DEF_MSG:
      retReason = "APIERR_GRP_DEF_MSG";
=======
   case APIERR_QUEUE_CREATE_FAIL:
      retReason = "APIERR_QUEUE_CREATE_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_CEL_MSG:
      retReason = "APIERR_GRP_CEL_MSG";
=======
   case APIERR_TIM_CRE_FAIL:
      retReason = "APIERR_TIM_CRE_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_TOO_LAR:
      retReason = "APIERR_GRP_TOO_LAR";
=======
   case APIERR_QUEUE_FAIL:
      retReason = "APIERR_QUEUE_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_TOO_MNY:
      retReason = "APIERR_GRP_TOO_MNY";
=======
   case APIERR_QUEUE_CON_FAIL:
      retReason = "APIERR_QUEUE_CON_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_NOT_FOU:
      retReason = "APIERR_GRP_NOT_FOU";
=======
   case APIERR_LOCAL_READ_FAIL:
      retReason = "APIERR_LOCAL_READ_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_INV_IDX:
      retReason = "APIERR_GRP_INV_IDX";
=======
   case APIERR_LOCAL_WRT_FAIL:
      retReason = "APIERR_LOCAL_WRT_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_NOT_ACC:
      retReason = "APIERR_GRP_NOT_ACC";
=======
   case APIERR_NET_WRT_FAIL:
      retReason = "APIERR_NET_WRT_FAIL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_NOT_SAV:
      retReason = "APIERR_GRP_NOT_SAV";
=======
   case APIERR_UNK_EVENT:
      retReason = "APIERR_UNK_EVENT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_IS_ENA:
      retReason = "APIERR_GRP_IS_ENA";
=======
   case APIERR_MISS_DATA:
      retReason = "APIERR_MISS_DATA";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_GRP_NOT_ENA:
      retReason = "APIERR_GRP_NOT_ENA";
=======
   case APIERR_PRIMARY_DISCONNECT:
      retReason = "APIERR_PRIMARY_DISCONNECT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_BUF_TOO_SMA:
      retReason = "APIERR_BUF_TOO_SMA";
=======
   case APIERR_HSB_DISCONNECT:
      retReason = "APIERR_HSB_DISCONNECT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_BUF_SIZ:
      retReason = "APIERR_INV_BUF_SIZ";
=======
   case APIERR_MISS_DATA_LOCAL:
      retReason = "APIERR_MISS_DATA_LOCAL";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_NOT_REQ_DAT:
      retReason = "APIERR_NOT_REQ_DAT";
=======
   case APIERR_INV_HOST_DISCONNECT:
      retReason = "APIERR_INV_HOST_DISCONNECT";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_NOT_UNP_DAT:
      retReason = "APIERR_NOT_UNP_DAT";
=======
   case APIERR_SYS_DBA_REA:
      retReason = "APIERR_SYS_DBA_REA";
>>>>>>> 1.9
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_NO_SUCH_INC:
      retReason = "APIERR_NO_SUCH_INC";
=======
   case APIERR_SYS_DQS_SND:
      retReason = "APIERR_SYS_DQS_SND";
>>>>>>> 1.9
      break;
<<<<<<< fdrtelegyr.cpp
=======
   }
   return( retReason );
}
>>>>>>> 1.9

   case APIERR_NOT_UNP_MEA:
      retReason = "APIERR_NOT_UNP_MEA";
      break;

   case APIERR_NOT_UNP_CNT:
      retReason = "APIERR_NOT_UNP_CNT";
      break;

   case APIERR_NOT_UNP_IND:
      retReason = "APIERR_NOT_UNP_IND";
      break;

   case APIERR_NOT_UNP_GEN:
      retReason = "APIERR_NOT_UNP_GEN";
      break;

   case APIERR_NOT_UNP_DQS:
      retReason = "APIERR_NOT_UNP_DQS";
      break;

   case APIERR_NOT_UNP_LIST:
      retReason = "APIERR_NOT_UNP_LIST";
      break;

   case APIERR_NOT_UNP_EQUIP:
      retReason = "APIERR_NOT_UNP_EQUIP";
      break;

   case APIERR_NOT_UNP_TAG:
      retReason = "APIERR_NOT_UNP_TAG";
      break;

   case APIERR_DQS_IS_ENA:
      retReason = "APIERR_DQS_IS_ENA";
      break;

   case APIERR_DQS_NOT_ENA:
      retReason = "APIERR_DQS_NOT_ENA";
      break;

   case APIERR_NO_SUCH_PRC:
      retReason = "APIERR_NO_SUCH_PRC";
      break;
=======
   tempStr = getCparmValueAsString( KEY_APPLICATION_NAME );

   if( tempStr.length() > 0 )
      _appName = tempStr;
   else
      _appName = "dev_yukon";

   tempStr = getCparmValueAsString( KEY_API_VERSION );

   if( tempStr.length() > 0 )
      _apiVersion = tempStr;
   else
      _apiVersion = "v4_1";



   //defaults for the control center

   tempStr = getCparmValueAsString( KEY_OPERATOR );

   if( tempStr.length() > 0 )
      _controlCenter.setOperator( tempStr );
   else
      _controlCenter.setOperator( "LGS" );


   tempStr = getCparmValueAsString( KEY_PASSWORD );

   if( tempStr.length() > 0 )
      _controlCenter.setPassword( tempStr );
   else
      _controlCenter.setPassword( "1234" );


   tempStr = getCparmValueAsString( KEY_SYSTEM_NAME );
>>>>>>> 1.7.6.7

   case APIERR_DQS_PRC_OFF:
      retReason = "APIERR_DQS_PRC_OFF";
      break;

   case APIERR_DQS_GEN_FAIL:
      retReason = "APIERR_DQS_GEN_FAIL";
      break;

   case APIERR_APPL_NOT_CONN:
      retReason = "APIERR_APPL_NOT_CONN";
      break;

   case APIERR_DQSPKT_TOO_LAR:
      retReason = "APIERR_DQSPKT_TOO_LAR";
      break;

   case APIERR_FAIL_PRI_WRT:
      retReason = "APIERR_FAIL_PRI_WRT";
      break;

   case APIERR_WRT_NOT_ALW:
      retReason = "APIERR_WRT_NOT_ALW";
      break;

   case APIERR_IS_OUT_SCAN:
      retReason = "APIERR_IS_OUT_SCAN";
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_CTL_NOT_ALW:
      retReason = "APIERR_CTL_NOT_ALW";
      break;
=======
//=================================================================================================================================
//=================================================================================================================================
>>>>>>> 1.7.6.7

   case APIERR_CTL_IN_PRG:
      retReason = "APIERR_CTL_IN_PRG";
      break;

   case APIERR_CTL_IS_TAG:
      retReason = "APIERR_CTL_IS_TAG";
      break;

   case APIERR_CTL_PRE_OP:
      retReason = "APIERR_CTL_PRE_OP";
      break;

   case APIERR_NO_DATA:
      retReason = "APIERR_NO_DATA";
      break;

   case APIERR_READ_DEF_MSG:
      retReason = "APIERR_READ_DEF_MSG";
      break;

   case APIERR_WRI_DEF_MSG:
      retReason = "APIERR_WRI_DEF_MSG";
      break;

   case APIERR_FUNC_NOT_SUPPORTED:
      retReason = "APIERR_FUNC_NOT_SUPPORTED";
      break;

   case APIERR_LIST_NOT_CMPLT:
      retReason = "APIERR_LIST_NOT_CMPLT";
      break;

   case APIERR_INV_ALMLIST_INDEX:
      retReason = "APIERR_INV_ALMLIST_INDEX";
      break;

   case APIERR_INVALID_CRT_NUM:
      retReason = "APIERR_INVALID_CRT_NUM";
      break;

   case APIERR_CRT_NOT_CONNECTED:
      retReason = "APIERR_CRT_NOT_CONNECTED";
      break;

   case APIERR_INVALID_WINDOW_NUM:
      retReason = "APIERR_INVALID_WINDOW_NUM";
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_WINDOW_NOT_ACTIVE:
      retReason = "APIERR_WINDOW_NOT_ACTIVE";
      break;
=======
void CtiFDRTelegyr::sendLinkState( int aState )
{
   if( getLinkStatusID() != 0 )
   {
      CtiPointDataMsg     *pData;
      pData = new CtiPointDataMsg( getLinkStatusID(), aState, NormalQuality, StatusPointType );
      sendMessageToDispatch( pData );
   }
}
>>>>>>> 1.7.6.7

   case APIERR_NO_SUCH_DISPLAY_FILE:
      retReason = "APIERR_NO_SUCH_DISPLAY_FILE";
      break;

   case APIERR_VALUE_OUT_OF_RANGE:
      retReason = "APIERR_VALUE_OUT_OF_RANGE";
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_NOTIME:
      retReason = "APIERR_NOTIME";
      break;
=======
CtiFDRTelegyr & CtiFDRTelegyr::setConnected( bool conn )
{
	if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " setting disconnected " << endl;
   }
   _connected = conn;
   return *this;
}
>>>>>>> 1.7.12.2

   case APIERR_CONNECT:
      retReason = "APIERR_CONNECT";
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_DISCONNECT:
      retReason = "APIERR_DISCONNECT";
      break;
=======
BOOL CtiFDRTelegyr::run( void )
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " ----- Starting FDR Telegyr Version " << FDR_TELEGYR_VERSION << endl;
   }
>>>>>>> 1.7.6.7

   case APIERR_LAN_FAIL:
      retReason = "APIERR_LAN_FAIL";
      break;

   case APIERR_QUEUE_CREATE_FAIL:
      retReason = "APIERR_QUEUE_CREATE_FAIL";
      break;

   case APIERR_TIM_CRE_FAIL:
      retReason = "APIERR_TIM_CRE_FAIL";
      break;

   case APIERR_QUEUE_FAIL:
      retReason = "APIERR_QUEUE_FAIL";
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_QUEUE_CON_FAIL:
      retReason = "APIERR_QUEUE_CON_FAIL";
      break;
=======
BOOL CtiFDRTelegyr::stop( void )
{
<<<<<<< fdrtelegyr.cpp
   int               reason;
   int               func_status;
   int               channel_id;
   int               group_type;
   int               group_num;
   APICLI_TIME       group_time;
   int               first_index;
   int               last_index;
   int               more;
   int               result[256];

   _threadGetTelegyrData.requestCancellation();
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   case APIERR_LOCAL_READ_FAIL:
      retReason = "APIERR_LOCAL_READ_FAIL";
      break;
=======
=======
   if( isConnected() )
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ---- Going to delete due to shutdown" << endl;
      }



      CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );         
      deleteGroups();
      for( int i = 0; i < 1000; i++ )
      {
         int returnCode = api_get_queued_data( 0,
                                              API_NO_TIMEOUT,
                                              &reason,
                                              &func_status,
                                              &channel_id,
                                              &group_type,
                                              &group_num,
                                              &group_time,
                                              &first_index,
                                              &last_index,
                                              &more,
                                              &result[0] );

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " ---- shutdown queued data got " << returnCode << " " << decipherReason( reason ) << endl;
         }

         if(( reason == API_DISC_NOTIFY ) || ( reason == API_DELALL_RES ))
         {
            break;
         }
         Sleep( 100 );
      }

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ---- Going to disconnect due to shutdown" << endl;
      }
      
      int disconn = api_disconnect( _controlCenter.getChannelID(), API_VALID );
      
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ---- Disconnect returned " << disconn << endl;
      }
      
      if( _inited )
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " ---- Stopping the api due to shutdown" << endl;
         }

         int end = api_end();
=======
	_threadGetTelegyrData.requestCancellation();
>>>>>>> 1.9

	deleteGroups();
	api_disconnect( _controlCenter.getChannelID(), API_VALID );
	api_end();

<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
   // stop the base class
   Inherited::stop();
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
   case APIERR_LOCAL_WRT_FAIL:
      retReason = "APIERR_LOCAL_WRT_FAIL";
      break;
=======
	// stop the base class
	Inherited::stop();
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   case APIERR_NET_WRT_FAIL:
      retReason = "APIERR_NET_WRT_FAIL";
      break;
=======
   return TRUE;
=======
   return(TRUE);
>>>>>>> 1.7.6.7
=======
	return(TRUE);
>>>>>>> 1.9
}
>>>>>>> 1.7.12.2

   case APIERR_UNK_EVENT:
      retReason = "APIERR_UNK_EVENT";
      break;

   case APIERR_MISS_DATA:
      retReason = "APIERR_MISS_DATA";
      break;

<<<<<<< fdrtelegyr.cpp
   case APIERR_PRIMARY_DISCONNECT:
      retReason = "APIERR_PRIMARY_DISCONNECT";
      break;
=======
//=================================================================================================================================
// Function Name: Extern C int RunInterface(void)
//
// Description: This is used to Start the Interface from the Main()
//              of FDR.EXE. Each interface it Dynamically loaded and
//             this function creates a global FDRTelegyr Object and then
//              calls its run method to cank it up.
//=================================================================================================================================
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   case APIERR_HSB_DISCONNECT:
      retReason = "APIERR_HSB_DISCONNECT";
      break;
=======
   DLLEXPORT int RunInterface( void )
   {
      // make a point to the interface
      myInterface = new CtiFDRTelegyr();
>>>>>>> 1.7.12.2
=======
DLLEXPORT int RunInterface( void )
{
<<<<<<< fdrtelegyr.cpp
   // make a point to the interface
   myInterface = new CtiFDRTelegyr();
>>>>>>> 1.7.6.7
=======
	// make a point to the interface
	myInterface = new CtiFDRTelegyr();
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   case APIERR_MISS_DATA_LOCAL:
      retReason = "APIERR_MISS_DATA_LOCAL";
      break;
=======
      // now start it up
      return myInterface->run();
   }
>>>>>>> 1.7.12.2
=======
   // now start it up
   return myInterface->run();
=======
	// now start it up
	return myInterface->run();
>>>>>>> 1.9
}
>>>>>>> 1.7.6.7

<<<<<<< fdrtelegyr.cpp
   case APIERR_INV_HOST_DISCONNECT:
      retReason = "APIERR_INV_HOST_DISCONNECT";
      break;
=======
//=================================================================================================================================
// Function Name: Extern C int StopInterface(void)
//
// Description: This is used to Stop the Interface from the Main()
//              of FDR.EXE. Each interface i2 Dynamically loaded and
//              this function stops a global FDRTelegyr Object and then
//              deletes it.
//
//=================================================================================================================================
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
   case APIERR_SYS_DBA_REA:
      retReason = "APIERR_SYS_DBA_REA";
      break;
=======
   DLLEXPORT int StopInterface( void )
   {
      myInterface->stop();
      delete myInterface;
      myInterface = 0;
>>>>>>> 1.7.12.2

<<<<<<< fdrtelegyr.cpp
   case APIERR_SYS_DQS_SND:
      retReason = "APIERR_SYS_DQS_SND";
      break;
   }
   return( retReason );
}
*/
=======
      return 0;
   }
=======
DLLEXPORT int StopInterface( void )
{
<<<<<<< fdrtelegyr.cpp
   myInterface->stop();
   delete myInterface;
   myInterface = 0;
>>>>>>> 1.7.6.7
=======
	myInterface->stop();
	delete myInterface;
	myInterface = 0;
>>>>>>> 1.9

<<<<<<< fdrtelegyr.cpp
<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.12.2
=======
   return 0;
=======
	return 0;
>>>>>>> 1.9
}
<<<<<<< fdrtelegyr.cpp
>>>>>>> 1.7.6.7
=======

>>>>>>> 1.9
#ifdef __cplusplus
}
#endif













