#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   fdrpibase
*
* Date:   1/11/2005
*
* Author: Tom Mack
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/02/14 16:38:41 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
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

//osi header
#include "piapi.h"
#include "piapix.h"

//child classes
#include "fdrpipoll.h"
#include "fdrpinotify.h"


// this class header
#include "fdrpibase.h"

/** global used to start the interface by c functions **/
CtiFDRPiBase * myInterface;

const CHAR * CtiFDRPiBase::KEY_FLAVOR                   = "FDR_PI_FLAVOR";
const CHAR * CtiFDRPiBase::KEY_DB_RELOAD_RATE           = "FDR_PI_DB_RELOAD_RATE";
const CHAR * CtiFDRPiBase::KEY_DEBUG_MODE               = "FDR_PI_DEBUG_MODE";
const CHAR * CtiFDRPiBase::KEY_APPLICATION_NAME         = "FDR_PI_APPLICATION_NAME";
const CHAR * CtiFDRPiBase::KEY_SERVER_NODE_NAME         = "FDR_PI_SERVER_NODE_NAME";


const char CtiFDRPiBase::PI_REAL_POINT = 'R';
const char CtiFDRPiBase::PI_INTEGER_POINT = 'I';
const char CtiFDRPiBase::PI_DIGITAL_POINT = 'D';



//=================================================================================================================================
// Constructor
//=================================================================================================================================
CtiFDRPiBase::CtiFDRPiBase() : CtiFDRInterface( RWCString( "PI" ) )
{

   CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
   getReceiveFromList().setPointList( recList );

   recList = NULL;
   _regFlag = false;
   pMultiData = NULL;
}

//=================================================================================================================================
// Destructor
//=================================================================================================================================
CtiFDRPiBase::~CtiFDRPiBase()
{
  delete pMultiData;
}

//=================================================================================================================================
// factory method
//=================================================================================================================================
CtiFDRPiBase* CtiFDRPiBase::createInstance()
{
  // can't check debuglevel in this function because it is static

  RWCString tempStr = iConfigParameters.getValueAsString( KEY_FLAVOR, "POLL" );
  if (tempStr == "POLL") {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << RWTime::now() << " FDRPI: Instantiating POLL flavor of FDRPI" << endl;
    return new CtiFDRPiPoll();
  } else {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << RWTime::now() << " FDRPI: Instantiating NOTIFY flavor of FDRPI" << endl;
    return new CtiFDRPiNotify();
  }
}

//=================================================================================================================================
//=================================================================================================================================

BOOL CtiFDRPiBase::init( void )
{
   RWCString   Description;
   RWCString   Action;
   BOOL        retVal = FALSE;
   int         status;


   // init the base class
   if( !Inherited::init() )
   {
      retVal = FALSE;
   }
   else
   {
      if( !readThisConfig() )
      {
         retVal = FALSE;
      }
      _linkStatusId = getClientLinkStatusID( getInterfaceName() );

      setConnected( false );

      connect();

      _threadGetPiData = rwMakeThreadFunction( *this, &CtiFDRPiBase::threadFunctionGetDataFromPi );
   }
   return retVal;
}

//=================================================================================================================================
// connect to the default PI server as specified by the pilogin.ini or piclient.ini file in the PI distribution
//=================================================================================================================================

bool CtiFDRPiBase::connect()
{

  if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    dout << RWTime::now() << " FDRPI: Attempting connect to PI server " << endl;
  }
  //connect to the Pi server
  //piut_setprocname("YUKON");
  piut_setservernode(_serverNodeName);
  int err = piut_connect("YUKON");
  if( err != 0 ) {
    if( getDebugLevel() & ERROR_FDR_DEBUGLEVEL ) {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Unable to connect to PI server, piut_connect returned " << getPiErrorDescription(err, "piut_connect") << endl;
    }
    setConnected( false );
  } else {
    // test connection
    int32 serverTime = 0;
    err = pitm_servertime(&serverTime);
    if (err == 1) { // for some reason, this error code is different than all others
      setConnected( true );
      if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL ) {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        dout << RWTime::now() << " FDRPI: Connected to PI server " << endl;
      }
    } else {
      if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL ) {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        dout << RWTime::now() << " FDRPI: Connection test failed, forcing disconnect " << endl;
      }
      piut_disconnect();
    }
  }

  return isConnected();
}

//=================================================================================================================================
//this is our loop for getting data
//=================================================================================================================================
void CtiFDRPiBase::threadFunctionGetDataFromPi( void )
{
  RWRunnableSelf    pSelf = rwRunnable();

  pMultiData = new CtiMultiMsg;
  messageCount = 0;
  const int loopsBeforeReconnect = 15;
  int reconnectLoopCount = 0;
  bool loadLists = true;
  const int loopPeriod = 1856;

  try
  {

    for( ;; )
    {
      pSelf.sleep( 1250 ); // just over 1 second, keep things off the intervals

      pSelf.serviceCancellation();

      CtiFDRPointList &aList = getReceiveFromList();
      CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());

      if (isConnected())
      {
        try {
          if (loadLists) {
            loadTranslationLists();
            loadLists = false;
          }
          updatePiValues();
        } catch (PiException& e) {
          setConnected(false);
        }


        if (messageCount > 0) {
          sendMessageToDispatch(pMultiData); //consumes memory
          if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Sent " << messageCount << " messages to dispatch" << endl;
          }
          messageCount = 0;
          pMultiData = new CtiMultiMsg;
        }
        
      }
      else
      {
        if (reconnectLoopCount == 0) {
          if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Connection down, waiting " << 
              ((loopPeriod * loopsBeforeReconnect) / 1000) << " seconds for reconnect" << endl;
          }
        }
        // is there a status message that needs to be send to dispatch???
        // can it be sent within connect()???
        if (reconnectLoopCount > loopsBeforeReconnect) {
          connect();
          reconnectLoopCount = 0;
        } else {
          reconnectLoopCount++;
        }
        loadLists = true;
      }

    }
     
  }
  catch( RWCancellation &cancellationMsg )
  {
      return;
  }
}
 
//=================================================================================================================================
//we just have this in case anyone ever really wants to send a message, right now, it's not used....
//=================================================================================================================================

bool CtiFDRPiBase::sendMessageToForeignSys( CtiMessage *aMessage )
{
   //tmack: I think this should throw an error
   return TRUE;
}

//=================================================================================================================================
//this is the receive message function that is our only concern for now
//here is where we'll wait for data to come back from the telegyr system after we've told it what we want
//=================================================================================================================================

INT CtiFDRPiBase::processMessageFromForeignSystem( CHAR *data )
{
  //tmack: I think this should throw an error
  return NORMAL;  
}


//=================================================================================================================================
//=================================================================================================================================

bool CtiFDRPiBase::loadTranslationLists()
{
  bool    retCode = true;
  bool                successful = true;
  bool                foundPoint = false;
  CtiFDRPoint *       point = NULL;

  if (!isConnected()) {
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Skipping loadTranslationLists(), not connected " << endl;
    }
    return false;
  }

  CtiFDRPointList &aList = getReceiveFromList();

  try
  {
    // make a list with all received points
    CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(), 
                                                   RWCString (FDR_INTERFACE_RECEIVE));

    if (pointList->loadPointList().errorCode() == (RWDBStatus::ok))
    {
      CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());  
      if (aList.getPointList() != NULL)
      {
        aList.deletePointList();
      }
      aList.setPointList (pointList);

      // get iterator on list
      CtiFDRManager::CTIFdrPointIterator  myIterator(aList.getPointList()->getMap());


      beginNewPoints();

      while ( myIterator() )
      {
        PiPointInfo info;
        foundPoint = true;
        point = myIterator.value();

        // we're interested in the first (and only) destination
        RWCString tagName = point->getDestinationList()[0].getTranslationValue("Tag Name");

        char tagBuf[80]; // max length of a tag, pipt_findpoint writes the tag back to this buffer
        strcpy(tagBuf, tagName);
        PiPointId piId = 0;
        int err = 0;
        err = pipt_findpoint(tagBuf, &piId);
        if (err != 0) {
          //if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Unable to find PI tag '" << tagName << 
              "' for point " << info.ctiPoint->getPointID() <<
              ", pipt_findpoint returned " << getPiErrorDescription(err, "pipt_findpoint") << endl;
          }
          continue;
        }
        info.piPointId = piId;
        info.ctiPoint = point;
        char type = '\0';
        err = pipt_pointtype(piId, &type);
        if (err != 0) {
          if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Unable to get PI point type for tag " << tagName << 
              ", pipt_pointtype returned " << getPiErrorDescription(err, "pipt_pointtype") << endl;
          }
          continue;
        } 
        info.piType = type;
        if (info.piType == PI_REAL_POINT || info.piType == PI_INTEGER_POINT) {
          if (info.ctiPoint->getPointType() != AnalogPointType) {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Incompatible type for point " << info.ctiPoint->getPointID() << 
              "; expected AnalogPointType, got " << info.ctiPoint->getPointType() << endl;
            continue;
          }
        } else if (info.piType == PI_DIGITAL_POINT) {
          if (info.ctiPoint->getPointType() != StatusPointType) {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Incompatible type for point " << info.ctiPoint->getPointID() << 
              "; expected StatusPointType, got " << info.ctiPoint->getPointType() << endl;
            continue;
          }

          // Get offset needed to decode value
          int32 digcode, dignumb;
          err = pipt_digpointers(piId, &digcode, &dignumb);
          if (err != 0) {
            if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
            {
              CtiLockGuard<CtiLogger> doubt_guard( dout );
              dout << RWTime::now() << " FDRPI: Unable to get digital offset for tag " << tagName << 
                ", pipt_digpointers returned " << getPiErrorDescription(err, "pipt_digpointers") << endl;
            }
            continue;
          }
          info.digitalOffset = digcode;
          info.digitalLength = dignumb;
        } else {
          if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
          {
            CtiLockGuard<CtiLogger> doubt_guard( dout );
            dout << RWTime::now() << " FDRPI: Unknown type " << info.piType << " for tag " << tagName << endl;
          }
          continue;
        }


        processNewPoint(info);

        if( getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL )
        {
          CtiLockGuard<CtiLogger> doubt_guard( dout );
          dout << RWTime::now() << " FDRPI: Added point " << point->getPointID() << 
            " and tag " << tagName << endl;
        }
      }
      endNewPoints();


    }
    else
    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << " db read code " << pointList->loadPointList().errorCode() << endl;
      successful = false;
    }
  }   // end try block

  catch (RWExternalErr e )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "loadTranslationList():  " << e.why() << endl;
    RWTHROW(e);
  }
  return( successful );
}

//=================================================================================================================================

void CtiFDRPiBase::handleUpdate(const PiPointInfo info, 
                                const float rval, 
                                const int32 istat, 
                                const time_t timestamp_utc, 
                                const int32 errorcode)
{
  RWTime rwTime(rwEpoch + timestamp_utc);

  if (info.ctiPoint->getLastTimeStamp() < rwTime) {

    CtiPointDataMsg   *pData = NULL;

    if (info.piType == PI_REAL_POINT || info.piType == PI_INTEGER_POINT) {
      pData = handleAnalogPoint(info, rval, istat, errorcode);
      
    } else if (info.piType == PI_DIGITAL_POINT) {
      pData = handleStatusPoint(info, rval, istat, errorcode);
    }

    if (!pData) {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Unable to allocate memory for CtiPointDataMsg (CtiFDRPiBase::handleUpdate()) " << endl;
      return;
    }

    pData->setTime(rwTime);

    pMultiData->getData().insert( pData );
    ++messageCount;
    info.ctiPoint->setLastTimeStamp(rwTime);

  }
}


CtiPointDataMsg* CtiFDRPiBase::handleAnalogPoint(const PiPointInfo info, 
                                const float rval, 
                                const int32 istat, 
                                const int32 errorcode) {
  int quality = NormalQuality;
  double value = 0.0;
  CtiPointDataMsg* pData;


  if (errorcode != 0) {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ) {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Unable to use value from Pi for point " << info.ctiPoint->getPointID() << 
        ", error code " << getPiErrorDescription(errorcode) << endl;
    }
    quality = NonUpdatedQuality;
    value = info.ctiPoint->getValue();
  } else {
    if (info.piType == PI_REAL_POINT && istat == 0) {
      value = rval;
    } else if (info.piType == PI_INTEGER_POINT && istat >= 0) {
      value = istat;
    } else {
      if ( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ){
        char buf[80] = "";
        pipt_digstate(istat, buf, 80);
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        dout << RWTime::now() << " FDRPI: Got not-good status for point " << info.ctiPoint->getPointID() << 
          "; rval=" << rval << ", state=" << buf << endl;
      }
      quality = NonUpdatedQuality;
      value = rval;
    }
  }
  value *= info.ctiPoint->getMultiplier();
  value += info.ctiPoint->getOffset();

  info.ctiPoint->setValue(value);

  pData = new CtiPointDataMsg(info.ctiPoint->getPointID(),
                              value,
                              quality,
                              AnalogPointType);

  if( getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL ) {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    dout << RWTime::now() << " FDRPI: new value " << value << " for point " << 
      info.ctiPoint->getPointID() << endl;
  }

  return pData;
}

CtiPointDataMsg* CtiFDRPiBase::handleStatusPoint(const PiPointInfo info, 
                                const float rval, 
                                const int32 istat, 
                                const int32 errorcode) {
  int quality = NormalQuality;
  CtiPointDataMsg* pData;
  int state = 0;

  if (errorcode != 0) {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ) {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      dout << RWTime::now() << " FDRPI: Unable to use value from Pi for point " << info.ctiPoint->getPointID() << 
        ", error code " << getPiErrorDescription(errorcode) << endl;
    }
    quality = NonUpdatedQuality;
    state = info.ctiPoint->getValue();
  } else {
    // Perform magic Pi voodoo. Check out:
    // Z:\Software Development Documents\Protocols\OSI PI\Documentation\PISDKAPIDOC\apiman13x\Api6.doc
    // section "Setting the Value of the istat Argument"

    state = -istat - info.digitalOffset;

    if (state < 0 || state > info.digitalLength) {
      // This would happen if the point had one of the system wide statuses set for it.
      // For instance, it could be set to "Shutdown" which is a valid status, but isn't one
      // of the expected statuses for this point (valid statuses for this point have a numerical
      // value between 0 and info.digitalLength, inclusive). Because the numerical value of 
      // "Shutdown" is meaningless to Yukon, we'll send the NonUpdatedQuality and the last state.
      if ( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL ){
        char buf[80] = "";
        pipt_digstate(istat, buf, 80);
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        dout << RWTime::now() << " FDRPI: Got not-good status for point " << info.ctiPoint->getPointID() << 
          ", state=" << buf << endl;
      }
      quality = NonUpdatedQuality;
      state = info.ctiPoint->getValue();
    }
  }

  info.ctiPoint->setValue(state);

  pData = new CtiPointDataMsg(info.ctiPoint->getPointID(),
                              state,
                              quality,
                              StatusPointType);

  if( getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL ) {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    dout << RWTime::now() << " FDRPI: new state " << state << " for point " << 
      info.ctiPoint->getPointID() << endl;
  }

  return pData;
}


//=================================================================================================================================
//here we read our Cparms to get our settings
//=================================================================================================================================

int CtiFDRPiBase::readThisConfig()
{
  RWCString   tempStr;
  int         tempInt;
/*
   static const CHAR * KEY_FLAVOR;
   static const CHAR * KEY_ALWAYS_SEND;
   static const CHAR * KEY_DB_RELOAD_RATE;
   static const CHAR * KEY_DEBUG_MODE;
   static const CHAR * KEY_APPLICATION_NAME;
   static const CHAR * KEY_SERVER_NODE_NAME;
*/
  setReloadRate( iConfigParameters.getValueAsInt( KEY_DB_RELOAD_RATE, 3600 ) );

  tempStr = iConfigParameters.getValueAsString( KEY_DEBUG_MODE );

  setInterfaceDebugMode( tempStr.length() > 0 );

  _appName = iConfigParameters.getValueAsString( KEY_APPLICATION_NAME, "dev_yukon" );

  _serverNodeName = iConfigParameters.getValueAsString( KEY_SERVER_NODE_NAME, "" );




  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << endl;
    dout << "----------------FDRPiBase Configs------------------------------" << endl;
    dout << "Our Name    : " << _appName << endl;
    dout << "Reload Rate : " << getReloadRate() << " secs" << endl;
    dout << "Debug Mode  : " << isInterfaceInDebugMode() << endl;
    dout << "Node Name   : " << _serverNodeName << endl;
    dout << "----------------FDRPiBase Configs------------------------------" << endl;
    dout << endl;
  }

  return TRUE;
}


//=================================================================================================================================
//=================================================================================================================================

void CtiFDRPiBase::sendLinkState( bool state )
{
    CtiPointDataMsg     *pData;
    pData = new CtiPointDataMsg( _linkStatusId , 
                                 state ? FDR_CONNECTED : FDR_NOT_CONNECTED, 
                                 NormalQuality, StatusPointType );
    sendMessageToDispatch( pData );
}

//=================================================================================================================================
//=================================================================================================================================

void CtiFDRPiBase::setConnected( bool conn )
{
   if( conn != _connected) {
     sendLinkState(conn);
     if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL ) {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime::now() << " FDRPI: setting " << (conn?"":"dis") << "connected" << endl;
     }
   _connected = conn;
   }
}

//=================================================================================================================================
//runs the interface
//=================================================================================================================================

BOOL CtiFDRPiBase::run( void )
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime::now() << " ----- Starting FDR Pi Interface" << endl;
   }

   init();

   // crank up the base class
   Inherited::run();

   // startup our interfaces
   _threadGetPiData.start();

   return TRUE;
}

//=================================================================================================================================
//=================================================================================================================================

BOOL CtiFDRPiBase::stop( void )
{
    _threadGetPiData.requestCancellation();

    // disconnect();

    // stop the base class
    Inherited::stop();

    return(TRUE);
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiFDRPiBase::getPiErrorDescription(int errCode, char* functionName)
{
  const int BUFFER_SIZE = 250;
  char buf[BUFFER_SIZE] = "";
  size_t bufSize = BUFFER_SIZE;

  piut_strerror(errCode, buf, &bufSize, functionName);

  buf[bufSize] = '\0';
  RWCString errMessage(buf);

  return errMessage;
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
//              calls its run method to cank it up.
//=================================================================================================================================

DLLEXPORT int RunInterface( void )
{
    // make a point to the interface
    myInterface = CtiFDRPiBase::createInstance();

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













