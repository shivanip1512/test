#include "yukon.h"

/**
 * File:   fdrpibase
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * PVCS KEYWORDS:
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.2 $
 * DATE         :  $Date: 2005/04/15 15:34:41 $
 */

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
const CHAR * CtiFDRPiBase::KEY_SERVER_USERNAME          = "FDR_PI_SERVER_USERNAME";
const CHAR * CtiFDRPiBase::KEY_SERVER_PASSWORD          = "FDR_PI_SERVER_PASSWORD";


const char CtiFDRPiBase::PI_REAL_POINT = 'R';
const char CtiFDRPiBase::PI_INTEGER_POINT = 'I';
const char CtiFDRPiBase::PI_DIGITAL_POINT = 'D';



/**
 * Default Constructor.
 */ 
CtiFDRPiBase::CtiFDRPiBase() : CtiFDRSimple( "PI" )
{
  readThisConfig();
}

/**
 * Destructor.
 */ 
CtiFDRPiBase::~CtiFDRPiBase()
{

}

/**
 * Factory method to return one of the two "flavors" based on the
 * KEY_FLAVOR value in the configuration file.
 */ 
CtiFDRPiBase* CtiFDRPiBase::createInstance()
{
  // can't check debuglevel in this function because it is static

  RWCString tempStr = iConfigParameters.getValueAsString( KEY_FLAVOR, "POLL" );
  if (tempStr == "POLL")
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << RWTime::now() << " FDRPI: Instantiating POLL flavor of FDRPI" << endl;
    return new CtiFDRPiPoll();
  }
  else
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << RWTime::now() << " FDRPI: Instantiating NOTIFY flavor of FDRPI" << endl;
    return new CtiFDRPiNotify();
  }
}

/**
 * Open a connection to the remote system.
 */ 
bool CtiFDRPiBase::connect()
{
  int err = 0;

  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Attempting connect to PI server " << endl;
  }
  //connect to the Pi server
  piut_setprocname("YUKON");
  err = piut_setservernode(_serverNodeName);
  if (err != 0)
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      std::string piError = getPiErrorDescription(err, "piut_setservernode");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to set server node, piut_setservernode returned " 
        << piError << endl;
    }
  }

  int32 valid = 0;
  err = piut_login(_serverUsername.data(),
                   _serverPassword.data(),
                   &valid);
  if (err != 0)
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      std::string piError = getPiErrorDescription(err, "piut_login");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to login to PI server, piut_login returned " 
        << piError
        << ", valid = " << valid << endl;
    }
  }
  else
  {
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Login succesfull, access level: " << valid << endl;
    }
  }

  // if the above fails, there's no harm trying this also...
  err = piut_connect("");
  if( err != 0 ) {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      std::string piError = getPiErrorDescription(err, "piut_connect");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to connect to PI server, piut_connect returned " 
        << piError << endl;
    }
    setConnected( false );
  }
  else
  {
    testConnection();
  }

  return isConnected();
}

/**
 * Test the connection.
 */ 
void CtiFDRPiBase::testConnection()
{
  // test connection
  int32 serverTime = 0;
  int err = pitm_servertime(&serverTime);
  if (err == 1) // for some reason, this error code is different than all others
  { 
    setConnected( true );
  }
  else
  {
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Connection test failed, forcing disconnect " << endl;
    }
    piut_disconnect();
  }
}

/**
 * Add a new point to the appropriate lists.
 */ 
void CtiFDRPiBase::processNewPoint(CtiFDRPoint *ctiPoint)
{
  PiPointInfo info;
  info.ctiPoint = ctiPoint;


  // we're interested in the first (and only) destination
  RWCString tagName = ctiPoint->getDestinationList()[0].getTranslationValue("Tag Name");

  char tagBuf[80]; // max length of a tag, pipt_findpoint writes the tag back to this buffer
  strcpy(tagBuf, tagName);
  PiPointId piId = 0;
  int err = 0;
  err = pipt_findpoint(tagBuf, &piId);
  if (err != 0) {
    //if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      std::string piError = getPiErrorDescription(err, "pipt_findpoint");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to find PI tag '" << tagName << 
        "' for point " << info.ctiPoint->getPointID() <<
        ", pipt_findpoint returned " << piError << endl;
    }
    return;
  }
  info.piPointId = piId;
  char type = '\0';
  err = pipt_pointtype(piId, &type);
  if (err != 0) {
    if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
    {
      std::string piError = getPiErrorDescription(err, "pipt_pointtype");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to get PI point type for tag " << tagName 
        << ", pipt_pointtype returned " << piError << endl;
    }
    return;
  } 
  info.piType = type;
  if (info.piType == PI_REAL_POINT || info.piType == PI_INTEGER_POINT)
  {
    if (info.ctiPoint->getPointType() != AnalogPointType)
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Incompatible type for point " << info.ctiPoint->getPointID()  
        << "; expected AnalogPointType, got " << info.ctiPoint->getPointType() << endl;
      return;
    }
  }
  else if (info.piType == PI_DIGITAL_POINT)
  {
    if (info.ctiPoint->getPointType() != StatusPointType)
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Incompatible type for point " << info.ctiPoint->getPointID() 
        << "; expected StatusPointType, got " << info.ctiPoint->getPointType() << endl;
      return;
    }

    // Get offset needed to decode value
    int32 digcode, dignumb;
    err = pipt_digpointers(piId, &digcode, &dignumb);
    if (err != 0) {
      if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
      {
        std::string piError = getPiErrorDescription(err, "pipt_digpointers");
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Unable to get digital offset for tag " << tagName 
          << ", pipt_digpointers returned " << piError << endl;
      }
      return;
    }
    info.digitalOffset = digcode;
    info.digitalLastIndex = dignumb;
  }
  else
  {
    if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unknown type " << info.piType << " for tag " << tagName << endl;
    }
    return;
  }

  processNewPiPoint(info);
}

/**
 * Handle updated values from Pi.
 */ 
void CtiFDRPiBase::handlePiUpdate(const PiPointInfo info, 
                                const float rval, 
                                const int32 istat, 
                                const time_t timestamp_utc, 
                                const int32 errorcode)
{
  if (errorcode != 0) {
    if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
    {
      std::string piError = getPiErrorDescription(errorcode);
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to use value from Pi for point " << info.ctiPoint->getPointID()
        << ", error code " << piError << endl;
    }
    handleNonUpdate(info.ctiPoint, timestamp_utc);
  }
  else if (info.piType == PI_REAL_POINT && istat == 0)
  {
    handleUpdate(info.ctiPoint, rval, timestamp_utc);
  }
  else if (info.piType == PI_INTEGER_POINT && istat >= 0)
  {
    handleUpdate(info.ctiPoint, istat, timestamp_utc);
  }
  else if (info.piType == PI_DIGITAL_POINT)
  {
    // Perform magic Pi voodoo. Check out:
    // Z:\Software Development Documents\Protocols\OSI PI\Documentation\PISDKAPIDOC\apiman13x\Api6.doc
    // section "Setting the Value of the istat Argument"

    int state = -istat - info.digitalOffset;

    if (state < 0 || state > info.digitalLastIndex) {
      // This would happen if the point had one of the system wide statuses set for it.
      // For instance, it could be set to "Shutdown" which is a valid status, but isn't one
      // of the expected statuses for this point (valid statuses for this point have a numerical
      // value between 0 and info.digitalLength - 1, inclusive). Because the numerical value of 
      // "Shutdown" is meaningless to Yukon, we'll send the NonUpdatedQuality and the last state.
      if ( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
      {
        char buf[80] = "";
        pipt_digstate(istat, buf, 80);
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Got not-good status for point " << info.ctiPoint->getPointID()
          << ", state=" << buf << endl;
      }
      handleNonUpdate(info.ctiPoint, timestamp_utc);
    } 
    else
    {
      handleUpdate(info.ctiPoint, state, timestamp_utc);
    }
  }
  else
  {
    if ( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      char buf[80] = "";
      pipt_digstate(istat, buf, 80);
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Got not-good status for point " << info.ctiPoint->getPointID()
        << "; rval=" << rval << ", state=" << buf << endl;
    }
    handleNonUpdate(info.ctiPoint, timestamp_utc);
  }

}

/**
 * Read our configuration file.
 */ 
void CtiFDRPiBase::readThisConfig()
{
  CtiFDRSimple::readThisConfig();

  _serverNodeName = iConfigParameters.getValueAsString( KEY_SERVER_NODE_NAME, "" );

  _serverUsername = iConfigParameters.getValueAsString( KEY_SERVER_USERNAME, "" );

  _serverPassword = iConfigParameters.getValueAsString( KEY_SERVER_PASSWORD, "" );




  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "----------------FDRPiBase Configs------------------------------" << endl;
    dout << "  " << KEY_SERVER_NODE_NAME << ": " << _serverNodeName << endl;
    dout << "  " << KEY_SERVER_USERNAME << ": " << _serverUsername << endl;
    dout << "  " << KEY_SERVER_PASSWORD << ": " << _serverPassword << endl;
    dout << endl;
  }

}


/**
 * Convert Pi error code to string.
 */ 
std::string CtiFDRPiBase::getPiErrorDescription(int errCode, char* functionName)
{
  const int BUFFER_SIZE = 1024;  //this should be plenty big for any error message
  char buf[BUFFER_SIZE] = "";
  size_t bufSize = BUFFER_SIZE;

  piut_strerror(errCode, buf, &bufSize, functionName);

  buf[bufSize] = '\0';
  std::string errMessage(buf);

  return errMessage;
}


/*==============================================================================
 *      Here Starts some C functions that are used to Start the
 *     Interface and Stop it from the Main() of FDR.EXE.
 *==============================================================================
 */

#ifdef __cplusplus
extern "C"{
#endif

/**
 * This is used to Start the Interface from the Main()
 * of FDR.EXE. Each interface it Dynamically loaded and
 * this function creates a global FDRTelegyr Object and then
 * calls its run method to cank it up.
 */
DLLEXPORT int RunInterface( void )
{
    // make a point to the interface
    myInterface = CtiFDRPiBase::createInstance();

    // now start it up
    return myInterface->run();
}

/**
 * This is used to Stop the Interface from the Main()
 * of FDR.EXE. Each interface i2 Dynamically loaded and
 * this function stops a global FDRTelegyr Object and then
 * deletes it.
 */
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













