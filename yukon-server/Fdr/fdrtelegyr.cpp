
#pragma warning( disable : 4786 )

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
#define FDR_TELEGYR_VERSION   "1.1.10f"
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
const CHAR * CtiFDRTelegyr::KEY_RELOAD_FREQUENCY         = "FDR_TELEGYR_RELOAD_FREQUENCY";
const CHAR * CtiFDRTelegyr::KEY_PANIC_NUMBER             = "FDR_TELEGYR_EXPECTED_DATA_RATE";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_GROUPID      = "GroupID";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_NAME         = "GroupName";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_INTERVAL     = "CollectionInterval";
const CHAR * CtiFDRTelegyr::COLNAME_TELEGYR_TYPE         = "GroupType";



//=================================================================================================================================
// Constructor
//=================================================================================================================================

CtiFDRTelegyr::CtiFDRTelegyr() : CtiFDRInterface( RWCString( "TELEGYR" ) ) , _hiReasonabilityFilter( 0.0 )
{
   _reloadTimer = RWTime::now();   //so we'll load right away
   _reloadTimer -= _dbReloadInterval;

   init();

   _inited = -1;
   _regFlag = false;
   _quit = false;
}

//=================================================================================================================================
// Destructor
//=================================================================================================================================

CtiFDRTelegyr::~CtiFDRTelegyr()
{
}

//=================================================================================================================================
//=================================================================================================================================

BOOL CtiFDRTelegyr::init( void )
{
   BOOL  retVal = FALSE;

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
   int index = 0;

   setConnected( false );

   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " ---- In contact() " << endl;
   }

   connect( index, status );

   if( isConnected() )
   {
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << RWTime::now() << " ---- Connected to api_client " << endl;
      }

      CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );
      deleteGroups();
   }

   return( isConnected() );
}

//=================================================================================================================================
//we want to be able to connect to any number of Telegyr Control Centers (servers) from anywhere, so we just pass the index of the
//server we want and the method parses the info from a list of servers available
//=================================================================================================================================

bool CtiFDRTelegyr::connect( int centerNumber, int &status )
{
   char  *op;
   char  *pw;
   char  *sn;
   int   groupValid;
   int   sysType;

   op = new char[_controlCenter.getOperator().length()+1];
   strcpy( op, _controlCenter.getOperator().data() );

   pw = new char[_controlCenter.getPassword().length()+1];
   strcpy( pw, _controlCenter.getPassword().data() );

   sn = new char[_controlCenter.getSysName().length()+1];
   strcpy( sn, _controlCenter.getSysName().data() );

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

   //connect to the Telegyr server
   status = api_connect( _controlCenter.getChannelID(),
                         op,
                         pw,
                         sn,
                         _controlCenter.getAccess(),
                         &groupValid,
                         &sysType );

   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << RWTime::now() << " api_connect (in connect) returned " << status << endl; 
   }

   delete [] op;
   delete [] pw;
   delete [] sn;

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
//can't tell the ems to delete the groups you've registered for unless we're connected to the client
//FIXME : may have to add a delete counters if someone wants them
//=================================================================================================================================

void CtiFDRTelegyr::deleteGroups( void )
{
   if( isConnected() )
   {
      if( _controlCenter.getTelegyrGroupList().size() != 0 )
      {
         int status = api_disable_all_cyclic( _controlCenter.getChannelID() );

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " || Disable-all-cyclic returns (code #): " << status << " on channel " << _controlCenter.getChannelID() << endl;
         }

         status = api_disable_all_spontaneous( _controlCenter.getChannelID() );

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " || Disable-all-spontaneous returns (code #): " << status << " on channel " << _controlCenter.getChannelID() << endl;
         }

         status = api_delete_all_groups( _controlCenter.getChannelID() );

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " || Delete-all-groups returns (code #): " << status << " on channel " << _controlCenter.getChannelID() <<  endl;
         }
      }
   }

   _reloadPending = false;
}

//=================================================================================================================================
//we get in here during startup and every time we do a database reload
//=================================================================================================================================

bool CtiFDRTelegyr::loadTranslationLists()
{
   bool  retCode = true;

   CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );

   retCode = loadGroupLists();

   if( retCode != true )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " Translation list load for FDRTelegyr failed " << endl;
   }

   return( retCode );
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

      while( !_quit )
      {
         //init the Telegyr API
         while( _inited != API_NORMAL )   
         {
            status = api_init( newPath, applicationName, apiVer, PRIORITY );
            _inited = status;

            pSelf.sleep( 30000 );
         }

         if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() <<" TelegyrAPI init() returned code " << status << endl;
         }

         pSelf.sleep( 1000 );

         CtiLockGuard<CtiMutex> sendGuard( _controlCenter.getMutex() );         

         if( isConnected() )
         {
            //peek at the input-queue and see if anybody wrote to us from home...
            int reason = -1;
            returnCode = -1;
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
                  dout << RWTime::now() << " Queued data returned grp_nbr " << group_num << " reason " << decipherReason( reason ) << endl;
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

                        if( loadGroupLists() )
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
                                    if( result[index] == API_NORMAL )
                                    {
                                       processAnalog( measurands[index], group_num, group_type, index );
                                    }
                                    else
                                    {
                                       if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                       {
                                          CtiLockGuard<CtiLogger> doubt_guard(dout);
                                          dout << RWTime::now() << " result " << result[index] << endl;
                                       }
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
                                       processDigital( indications[index], group_num, group_type, index );
                                    }
                                    else
                                    {
                                       if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                       {
                                          CtiLockGuard<CtiLogger> doubt_guard(dout);
                                          dout << RWTime::now() << " result " << result[index] << endl;
                                       }
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
                                       processCounter( counters[index], group_num, group_type, index );
                                    }
                                    else
                                    {
                                       if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                                       {
                                          CtiLockGuard<CtiLogger> doubt_guard(dout);
                                          dout << RWTime::now() << " result " << result[index] << endl;
                                       }
                                       processBadPoint( group_num, index );
                                    }
                                 }
                              }
                              else
                              {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime::now() << " TelegyrAPI api_unpack_counter_values() failed: " << status << endl;
                              }
                              delete [] counters;
                           }
                        }
                        break;
                     }
                  }
                  break;

               case API_DELALL_RES:    //we've done a global delete group call
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Calling to build groups..." << endl;
                     }

                     // we know the old groups are now gone, build and register the new ones

                     buildAndRegisterGroups();
                     _reloadTimer = RWTime::now();
                     _regFlag = true;

                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " ...finished " << endl;
                     }
                  }
                  break;

               case API_DISCYCALL_RES:    //we've done a call to disable cyclic
                  {
                     if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to disable all cyclic data executed" << endl;
                     }
                  }
                  break;

               case API_DISSPOALL_RES:    //we've done a call to disable all spon data
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to disable all spontaneous data executed" << endl;
                     }
                  }
                  break;

               case API_ENCYC_RES:     //we've done a call to enable cyclic data
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Call to enable cyclic data executed" << endl;
                     }
                  }
                  break;

               case API_ENSPO_RES:     //we've done a call to enable spontaineous data
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
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
                        dout << RWTime::now() << " Group creation attempted" << endl;
                     }
                  }
                  break;

               default:              //we don't know what the heck is going on....
                  {
                     if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " Unsupported message reason (code #): "<< reason << " " << decipherReason( reason ) << endl;
                     }
                  }
                  break;
               }

               badMsgCount = 0;
            }
            else
            {
               //if we stop getting data for some # minutes, we'll try to re-connect
               badMsgCount++;

               if( badMsgCount > _panicNumber )
               {
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " ---- Data Timeout: Starting Over " << endl;
                  }
                  api_disconnect( _controlCenter.getChannelID(), API_VALID );
                  setConnected( false );
                  badMsgCount = 0;
               }
            }

            if( _reloadPending )
            {
               if( isReloadTime() )
               {
                  //call delete groups, we've got a new list already
                  deleteGroups();
               }
            }

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
         }
         else  //not connected
         {
            timer = 0;
            waiter++;                        //we want to try to reconnect every 15 secs or so 
            disconCount++;

            if( disconCount >= 15 )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Telegyr is disconnected from api_client " << endl;
               disconCount = 0;
            }

            if( waiter >= 15 )
            {
               if( _inited == API_NORMAL )
               {
                  contact( status );            //NOTE:used this as it calls build&Register()
               }
               else
               {
                  {
                     CtiLockGuard<CtiLogger> doubt_guard( dout );
                     dout << RWTime::now() << " Trying to re-init the api " << endl;
                  }
                  _inited = api_init( newPath, applicationName, apiVer, PRIORITY );
               }

               printCount++;                    //we want to tell someone that we tried to reconnect every now and again
               waiter = 0;

               if( printCount >= 10 )
               {
                  printCount = 0;

                  if( !isConnected() )
                  {
                     {
                        CtiLockGuard<CtiLogger> doubt_guard( dout );
                        dout << RWTime::now() << " TelegyrAPI not connected" << endl;
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
                        dout << RWTime::now() << " TelegyrAPI is connected" << endl;
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

      //
      //we're going to shut down now
      //

      delete [] newPath;
      delete [] applicationName;
      delete [] apiVer;

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
            int reason = -1;
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

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime::now() << " ---- Shutdown queued data got code " << returnCode << "; reason " << reason << endl;
            }

            if(( reason == API_DISC_NOTIFY ) || ( reason == API_DELALL_RES ))
            {
               break;
            }
            Sleep( 100 );
         }

         if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " ---- Going to disconnect due to shutdown" << endl;
         }

         int disconn = api_disconnect( _controlCenter.getChannelID(), API_VALID );

         if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime::now() << " ---- Disconnect returned " << disconn << endl;
         }

         if( _inited == API_NORMAL )
         {
            if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime::now() << " ---- Stopping the api due to shutdown" << endl;
            }

            int end = api_end();

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime::now() << " ---- API shutdown returned " << end << endl;
            }
         }
      }

      return;

   }
   catch( RWCancellation &cancellationMsg )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " ---- Caught a cancellation message" << endl;
   }
   catch( ... )
   {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " ---- Caught some message" << endl;

      return;
   }
}

//=================================================================================================================================
//here we are yanking data out of our telegyr group list and setting up groups that the foreign system will understand
//we should only be here if we've gotten past the api_init() and api_connect() methods built into telegyr
//=================================================================================================================================

void CtiFDRTelegyr::buildAndRegisterGroups( void )
{
   int                  index;
   int                  cnt;
   int                  returnCode;
   int                  channel_id;
   int                  group_type;
   int                  group_number;
   int                  persistence;
   int                  object_count;
   int                  cycle_time;
   int                  totalGroups = _controlCenter.getTelegyrGroupList().size();
   char                 **name_list;
   bool                 cyclic = false;
   bool                 *created = new bool[totalGroups];

   for( index = 0; index < totalGroups; index++ )
   {
      cyclic = false;
      cycle_time = 0;
      channel_id = _controlCenter.getChannelID();

      RWCString type = _controlCenter.getTelegyrGroupList()[index].getGroupType();
      type.toLower();

      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[index].getInterval() != 0 )
      {
         if(( type == STATUS_TYPE ) || ( type == DIGITAL_TYPE ))
         {
            group_type = API_GET_CYC_IND;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found a status group w/interval" << endl;
            }
         }
         else if( type == ANALOG_TYPE )
         {
            group_type = API_GET_CYC_MEA;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found an analog group w/interval" << endl;
            }
         }
         else if( type == COUNTER_TYPE )
         {
            group_type = API_GET_CYC_CNT;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found a counter group w/interval" << endl;
            }
         }
         else
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found w/interval " << _controlCenter.getTelegyrGroupList()[index].getGroupType() << endl;
            }
         }

         //this will always be in seconds
         cycle_time = _controlCenter.getTelegyrGroupList()[index].getInterval();
         cyclic = true;
      }
      else   //if there's no interval, we get data when a point changes on the server
      {
         if( ( type == STATUS_TYPE ) || ( type == DIGITAL_TYPE ) )
         {
            group_type = API_GET_SPO_IND;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found a status group w/o interval" << endl;
            }
         }
         else if( type == ANALOG_TYPE )
         {
            group_type = API_GET_SPO_MEA;

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
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
         }
         else
         {
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " Found w/o interval " << _controlCenter.getTelegyrGroupList()[index].getGroupType() << endl;
            }
         }
      }

      group_number = _controlCenter.getTelegyrGroupList()[index].getGroupID();
      persistence = API_GRP_NO_PERSISTENCE;                          
      object_count = _controlCenter.getTelegyrGroupList()[index].getPointList().size();

      if( object_count != 0 )       //don't create empty groups
      {
         //make enough pointers for our pointnames
         name_list = new char *[object_count];                    

         for( cnt = 0; cnt < object_count; cnt++ )
         {
            //makes some space to copy our pointnames
            name_list[cnt] = new char[200];
            strcpy( name_list[cnt], _controlCenter.getTelegyrGroupList()[index].getPointList()[cnt].getTranslateName( 0 ) );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " " << cnt << " " << name_list[cnt] << endl;
            }
         }                                                   

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

            returnCode = api_create_group( channel_id, group_type, group_number, persistence, PRIORITY, object_count, name_list );

            if( returnCode != API_NORMAL )
            {
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << RWTime::now() << " Creation of ";
               }

               if(( group_type == API_GET_CYC_IND ) || ( group_type == API_GET_SPO_IND ))
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << "status group " << group_number << " failed on submission.  Error code: " << returnCode << endl;
               }
               else if(( group_type == API_GET_CYC_MEA ) || ( group_type == API_GET_SPO_MEA ))
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << "analog group " << group_number << " failed on submission.  Error code: " << returnCode << endl;
               }
               else
               {
                  CtiLockGuard<CtiLogger> doubt_guard( dout );
                  dout << "counter group " << group_number << " failed on submission.  Error code: " << returnCode << endl;
               }
            }
            else
            {
               created[index] = true;
            }
         }

         for( cnt = 0; cnt < object_count; cnt++ )
         {
            delete [] name_list[cnt];
         }
         delete [] name_list;
      }
   }

   //enable all the groups
   for( int i = 0; i < totalGroups; i++ )
   {
      cyclic = false;
      group_number = _controlCenter.getTelegyrGroupList()[i].getGroupID();
      channel_id = _controlCenter.getChannelID();
      object_count = _controlCenter.getTelegyrGroupList()[i].getPointList().size();

      //if there's an interval, we have cyclic data
      if( _controlCenter.getTelegyrGroupList()[i].getInterval() != 0 )
      {
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

         //this will always be in seconds
         cycle_time = _controlCenter.getTelegyrGroupList()[i].getInterval();
         cyclic = true;
      }
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
         {
            group_type = API_GET_SPO_CNT;
         }
      }

      if( ( object_count > 0 ) && created[i] )    //don't enable empty/uncreated groups
      {
         int retCode;

         if( cyclic )
         {
            //by specifying the NOALIG parameter, we are telling the telegyr system to go ahead and start sending
            //data back to use immediately
            retCode = api_enable_cyclic( channel_id, group_type, group_number, cycle_time, API_SECOND, API_ALIG_NOALIG );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime::now() << " api_enable_cyclic group_number " << group_number << " returned " << retCode << endl;
            }

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
      }
      else
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " no points in " << _controlCenter.getTelegyrGroupList()[index].getGroupName() << endl;
         }
      }
   }

   delete [] created;
}

//=================================================================================================================================
//get point info from a database load/reload
//we are looking at the translation string and breaking it down to figure out what type the point is, what it's
//interval is, and what it's name is. Then we look for a group of the same specs that has room for the point
//=================================================================================================================================

bool CtiFDRTelegyr::loadGroupLists( void )
{
   CtiFDRPoint             *translationPoint = NULL;
   RWCString               myTranslateName;
   RWCString               pointType;           //analog or status
   RWCString               pointStr;            //pointid
   RWCString               groupStr;            //now holds the rate to receive
   RWDBStatus              listStatus;
   bool                    successful( FALSE );
   bool                    foundPoint        = false;
   bool                    foundGroup        = false;
   int                     groupNum          = 1;
   int                     analogNum         = 1;
   int                     statusNum         = 101;

   vector< CtiTelegyrGroup >           groupList;

   try
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard( dout );
         dout << RWTime::now() << " ---- Loading And Building Groups " << endl;
      }

      // make a list with all received points
      CtiFDRManager *pointList = new CtiFDRManager( getInterfaceName(), RWCString( FDR_INTERFACE_RECEIVE ) );

      listStatus = pointList->loadPointList();

      // if status is ok, we were able to read the database at least
      if( listStatus.errorCode() == ( RWDBStatus::ok ) )
      {
         //delete our old list of points
//         _controlCenter.deleteTelegyrGroupList();

         //===================================================================================
         //seeing occasional problems where we get empty data sets back and there should be 
         //info in them,  we're checking this to see if is reasonable if the list may now be 
         //empty the 2 entry thing is completly arbitrary
         //===================================================================================

         if(( pointList->entries() == 0 ) || ( pointList->entries() > 0 ))
         {
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

                     groupStr = nextTempToken( ":" );
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
/*
               //we need to spin through the list
               for( int i = 0; i < _controlCenter.getTelegyrGroupList().size(); i++ )
               {
                  RWCString type = _controlCenter.getTelegyrGroupList()[i].getGroupType();
                  int interval = _controlCenter.getTelegyrGroupList()[i].getInterval();
                  int size = _controlCenter.getTelegyrGroupList()[i].getPointList().size();
                  type.toLower();
                  pointType.toLower();

//                  if(( type == pointType ) && ( interval == atoi( groupStr ) ) && ( size < 127 ))
                  if( ( type == pointType ) && ( size < 127 ) )
                  {
                     _controlCenter.getTelegyrGroupList()[i].getPointList().push_back( *translationPoint );
                     foundGroup = true;
                     break;
                  }
               }
*/
               for( int i = 0; i < groupList.size(); i++ )
               {
                  RWCString type = groupList[i].getGroupType();
                  int interval = groupList[i].getInterval();
                  int size = groupList[i].getPointList().size();
                  type.toLower();
                  pointType.toLower();

//                  if(( type == pointType ) && ( interval == atoi( groupStr ) ) && ( size < 127 ))
                  if( ( type == pointType ) && ( size < 127 ) )
                  {
                     groupList[i].getPointList().push_back( *translationPoint );
                     foundGroup = true;
                     break;
                  }
               }

//               foundGroup=true;
               //we didn't stick the point anywhere, make a new group and put it there
               if( !foundGroup )
               {
                  CtiTelegyrGroup tempGroup;
                  RWCString comp( "status" );
                  char lum[15];

                  pointType.toLower();

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
                  tempGroup.setGroupType( pointType );
                  tempGroup.getPointList().push_back( *translationPoint );
//                  _controlCenter.addToGroupList( tempGroup );
                  groupList.push_back( tempGroup );
               }
               else
               {
                  foundGroup = false;     //reset for the next point
               }   
            }

            _reloadPending = true;

            delete pointList;
            pointList=NULL;

            if( !successful )
            {
               if( !foundPoint )
               {
                  // means there was nothing in the list, wait until next db change or reload
                  successful = true;
               }
            }
            //new else block
            else
            {
               //delete the old and swap the new list in
               _controlCenter.deleteTelegyrGroupList();
               _controlCenter.getTelegyrGroupList() = groupList;
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

bool CtiFDRTelegyr::processAnalog( APICLI_GET_MEA aPoint, int groupid, int group_type, int index )
{
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

      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out

      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
            pointid = _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointID();

            _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getPointType();

            value = value * ( _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getMultiplier() );
            value += ( _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getOffset() );

            if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
            {
               CtiLockGuard<CtiLogger> doubt_guard( dout );
               dout << RWTime() << " Raw Analog Value: " << raw << " from Point " << _controlCenter.getTelegyrGroupList()[x].getPointList()[index].getTranslateName(0) << " groupid " << groupid << endl;
            }

            //if the value is higher than it should be, we want to let the boss man know
            if( ( getHiReasonabilityFilter() > 0 ) && ( getHiReasonabilityFilter() > value ) )
            {
               nonUpdated = true;
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

bool CtiFDRTelegyr::processDigital( APICLI_GET_IND aPoint, int groupid, int group_type, int index )
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

      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out

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
   }

   return returnCode;
}

//=================================================================================================================================
//we want to decipher the point and put it on the queue to Dispatch
//=================================================================================================================================

bool CtiFDRTelegyr::processCounter( APICLI_GET_CNT aPoint, int groupid, int group_type, int index )
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

      //flip through our groups until the groupid matches the one this point came from
      //then snag the pointid out

      for( x = 0; x < _controlCenter.getTelegyrGroupList().size(); x++ )
      {
         if( _controlCenter.getTelegyrGroupList()[x].getGroupID() == groupid )
         {
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
   RWCString            pointName;

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

   case API_CYC_DATA:
      retReason = "API_CYC_DATA";
      break;

   case API_SPO_DATA:
      retReason = "API_SPO_DATA";
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

   case API_DELALL_RES:
      retReason = "API_DELALL_RES";
      break;

   case API_ENCYC_RES:
      retReason = "API_ENCYC_RES";
      break;

   case API_ENSPO_RES:
      retReason = "API_ENSPO_RES";
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
//here we read our Cparms to get our settings
//=================================================================================================================================

int CtiFDRTelegyr::readConfig( void )
{
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

   if( tempStr.length() > 0 )
      _controlCenter.setSysName( tempStr );
   else
      _controlCenter.setSysName( "MEC" );

   tempStr = getCparmValueAsString( KEY_CHANNEL_ID );

   if( tempStr.length() > 0 )
      _controlCenter.setChannelID( atof( tempStr ) );
   else
      _controlCenter.setChannelID( 1 );

   tempStr = getCparmValueAsString( KEY_ACCESS );

   if( tempStr.length() > 0 )
      _controlCenter.setAccess( atof( tempStr ) );
   else
      _controlCenter.setAccess( 1 );

   tempStr = getCparmValueAsString( KEY_RELOAD_FREQUENCY );

   if( tempStr.length() > 0 )
      _dbReloadInterval = atof( tempStr );
   else
      _dbReloadInterval = 60;                

   tempStr = getCparmValueAsString( KEY_PANIC_NUMBER );

   if( tempStr.length() > 0 )
      _panicNumber = atof( tempStr );
   else
      _panicNumber = 600;

   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << endl;
      dout << endl;
      dout << "-----------------------Configs---------------------------------" << endl;
      dout << "Our Name             : " << getCparmValueAsString( KEY_APPLICATION_NAME ) << endl;
      dout << "Reload Rate          : " << getCparmValueAsString( KEY_DB_RELOAD_RATE ) << " secs" << endl;
      dout << "Hi_Res               : " << getCparmValueAsString( KEY_HI_REASONABILITY_FILTER ) << endl;
      dout << "Debug Mode           : " << getCparmValueAsString( KEY_DEBUG_MODE ) << endl;
      dout << "Api path             : " << getCparmValueAsString( KEY_API_PATH ) << endl;
      dout << "Api Version          : " << getCparmValueAsString( KEY_API_VERSION ) << endl;
      dout << "Operator             : " << getCparmValueAsString( KEY_OPERATOR ) << endl;
      dout << "Password             : " << getCparmValueAsString( KEY_PASSWORD ) << endl;
      dout << "Sysname              : " << getCparmValueAsString( KEY_SYSTEM_NAME ) << endl;
      dout << "Channel Id           : " << getCparmValueAsString( KEY_CHANNEL_ID ) << endl;
      dout << "Access               : " << getCparmValueAsString( KEY_ACCESS ) << endl;
      dout << "Minimum Reload Rate  : " << _dbReloadInterval << endl;
      dout << "Expected Data Rate   : " << _panicNumber << endl;
      dout << "-----------------------Configs---------------------------------" << endl;
      dout << endl;
      dout << endl;
   }

   return TRUE;
}

//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::isReloadTime( void )
{
   if( ( RWTime::now().seconds() - _reloadTimer.seconds() ) >= _dbReloadInterval )
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime::now() << " ---- Time for reload" << endl;
      }

      return true;
   }
   else
   {
      return false;
   }
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

double CtiFDRTelegyr::getHiReasonabilityFilter() const
{
   return _hiReasonabilityFilter;
}

//=================================================================================================================================
//=================================================================================================================================

CtiFDRTelegyr & CtiFDRTelegyr::setHiReasonabilityFilter( const double myValue )
{
   _hiReasonabilityFilter = myValue;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

long CtiFDRTelegyr::getLinkStatusID( void ) const
{
   return _linkStatusID;
}

//=================================================================================================================================
//=================================================================================================================================

CtiFDRTelegyr & CtiFDRTelegyr::setLinkStatusID( const long aPointID )
{
   _linkStatusID = aPointID;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiFDRTelegyr::getPath( void )
{
   return _path;
}

//=================================================================================================================================
//=================================================================================================================================

CtiFDRTelegyr & CtiFDRTelegyr::setPath( RWCString inPath )
{
   _path = inPath;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

void CtiFDRTelegyr::sendLinkState( int aState )
{
   if( getLinkStatusID() != 0 )
   {
      CtiPointDataMsg   *pData;

      pData = new CtiPointDataMsg( getLinkStatusID(), aState, NormalQuality, StatusPointType );
      sendMessageToDispatch( pData );
   }
}

//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRTelegyr::isConnected( void )
{
   return _connected;
}

//=================================================================================================================================
//=================================================================================================================================

CtiFDRTelegyr & CtiFDRTelegyr::setConnected( bool conn )
{
   if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " ---- Setting connected to " << conn << endl;
   }
   _connected = conn;
   return *this;
}

//=================================================================================================================================
//runs the interface
//=================================================================================================================================

BOOL CtiFDRTelegyr::run( void )
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " ---- Starting FDR Telegyr Version " << FDR_TELEGYR_VERSION << endl;
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
   _quit = true;

   _threadGetTelegyrData.join( 30000 );

   // stop the base class
   Inherited::stop();

   return( TRUE );
}

//=================================================================================================================================
//      Here Starts some C functions that are used to Start the
//     Interface and Stop it from the Main() of FDR.EXE.
//=================================================================================================================================

#ifdef __cplusplus
extern "C"
{
#endif

   //==============================================================================================================================
   // Function Name: Extern C int RunInterface(void)
   //
   // Description: This is used to Start the Interface from the Main()
   //              of FDR.EXE. Each interface it Dynamically loaded and
   //             this function creates a global FDRTelegyr Object and then
   //              calls its run method to cank it up.
   //==============================================================================================================================

   DLLEXPORT int RunInterface( void )
   {
      // make a point to the interface
      myInterface = new CtiFDRTelegyr();

      // now start it up
      return myInterface->run();
   }

   //==============================================================================================================================
   // Function Name: Extern C int StopInterface(void)
   //
   // Description: This is used to Stop the Interface from the Main()
   //              of FDR.EXE. Each interface is Dynamically loaded and
   //              this function stops a global FDRTelegyr Object and then
   //              deletes it.
   //
   //==============================================================================================================================

   DLLEXPORT int StopInterface( void )
   {
      myInterface->stop();
      delete myInterface;
      myInterface = 0;

      return 0;
   }
#ifdef __cplusplus
}
#endif


