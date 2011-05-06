#include "yukon.h"

#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

#define _WINDLL

#include <winsock2.h>

#include "ctitime.h"
#include "ctidate.h"

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
#include "ctistring.h"

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
CtiFDRPiBase::CtiFDRPiBase() : 
  CtiFDRSimple( "PI" ),
  _connectionFailureCount(0),
  _collectiveConnectionRetries(0)
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

  string tempStr = gConfigParms.getValueAsString( KEY_FLAVOR, "POLL" );
  if (tempStr == "POLL")
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime::now() << " FDRPI: Instantiating POLL flavor of FDRPI" << endl;
    return new CtiFDRPiPoll();
  }
  else
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime::now() << " FDRPI: Instantiating NOTIFY flavor of FDRPI" << endl;
    return new CtiFDRPiNotify();
  }
}

/**
 * Open a connection to the remote system.
 */
bool CtiFDRPiBase::connect()
{
  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Attempting connect to PI server " << endl;
  }
  if(_connectionFailureCount >= FailureThreshold)
  {
    // We've failed to connect to this guy alot. If there's only one node,
    // there's nothing we can do, but if we're connecting to a collective
    // we should try to switch to a secondary for next time!
    switchCurrentNode();
  }
  int err = piut_setservernode(getCurrentNodeName().c_str());
  if (err != 0)
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
      std::string piError = getPiErrorDescription(err, "piut_setservernode");
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to set server node, piut_setservernode returned "
        << piError << endl;
    }
    setConnected( false );
    _connectionFailureCount++;
    return false;
  }

  // Reset the connection failure count if we get to this point!
  _connectionFailureCount = 0;

  int32 valid = 0;
  err = piut_login(_serverUsername.c_str(),
                   _serverPassword.c_str(),
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
    setConnected( false );
  }
  else
  {
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Login succesful, access level: " << valid << endl;
    }
    testConnection();
  }

  return isConnected();
}

/**
 * Test the connection.
 */
bool CtiFDRPiBase::testConnection()
{
  // test connection
  int32 serverTime = 0;
  int err = pitm_servertime(&serverTime);
  if (err == 1) // for some reason, this error code is different than all others
  {
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Connection test passed, setting connected " << endl;
    }
    setConnected( true );
  }
  else
  {
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Connection test failed, forcing disconnect " << endl;
    }
    setConnected( false );
    piut_disconnect();
  }

  return isConnected();
}

/**
 * Add a new point to the appropriate lists.
 */
void CtiFDRPiBase::processNewPoint(CtiFDRPointSPtr ctiPoint)
{
  PiPointInfo info;
  info.ctiPoint = ctiPoint.get();


  // we're interested in the first (and only) destination
  string tagName = ctiPoint->getDestinationList()[0].getTranslationValue("Tag Name");

  PiPointId piId;
  int err = getPiPointIdFromTag(tagName,piId);
  if (err != 0)
  {
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

  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
    CtiLockGuard<CtiLogger> doubt_guard( dout );
    logNow() << "Added point " << info.ctiPoint->getPointID() << " and tag " << tagName << endl;
  }

}

/**
 * 
 * Function to grab the server node name we're connecting to. 
 * Used primarily for support of collectives, but will work for 
 * systems using one database as well. 
 *  
 * @return The name of the server whose PI Database we are 
 *         connecting to.
 */
std::string CtiFDRPiBase::getCurrentNodeName()
{
  if(_serverNodeNames.empty())
  {
    return "";
  }

  return _serverNodeNames[_currentNodeIndex];
}

/**
 * Function to switch the current node if we are having problems 
 * connecting to our primary node. This funtion will work 
 * correctly for both collectives and single node servers. 
 *  
 * If we aren't connecting to a collective, the current node 
 * index will remain untouched, so previous behavior will be 
 * left intact. 
 */
void CtiFDRPiBase::switchCurrentNode()
{
  // We ideally want to be connected to the primary server at all times.
  if(_currentNodeIndex != 0)
  {
    // We were connected to a secondary node. Check if the primary is back up yet!
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to connect to secondary server node " << getCurrentNodeName() << ".\n";
      _currentNodeIndex = 0;
      logNow() << "Attempting to reconnect to primary server node " << getCurrentNodeName() << ".\n";
    }
    if(!isCollectivePrimaryNodeOnline())
    {
      // Our current node is bad and so is the primary! Switch to the next available.
      // What happens if there aren't any more in the list? Just keep bouncing between
      // offline nodes? Is this too expensive? Lots of questions!
      _currentNodeIndex = (_currentNodeIndex + 1) % _serverNodeNames.size();
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Primary node and most recently connected secondary node are down. " << endl;
        logNow() << "Attempting to connect to secondary server node " << getCurrentNodeName() << ".\n";
      }
    }
  }
  else if(isCollectiveConnection())
  {
    // We are connecting to a collective and the primary is down! Choose a backup!
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Unable to connect to primary server node " << getCurrentNodeName() << ".\n";
      _currentNodeIndex = 1;
      logNow() << "Switching to secondary server node " << getCurrentNodeName() << ".\n";
    }
  }
}

/**
 * This function simply returns whether the number of servers in 
 * our server node list is greater than one, indicating that we 
 * are indeed connected to a collective. 
 *  
 * @return <code>true</code> if there is more than one server in
 *         our node name list, <code>false</code> otherwise.
 */
bool CtiFDRPiBase::isCollectiveConnection()
{
  return _serverNodeNames.size() > 1;
}

bool CtiFDRPiBase::isCollectivePrimaryNodeOnline()
{
  if(_currentNodeIndex == 0)
  {
    // No worries, we're already the primary.
    return true;
  }
  else
  {
    // We are a collective since the index wasn't 0. Try to see if the primary is
    // back up so that we can connect to it.

    int lastNode = _currentNodeIndex;
    _currentNodeIndex = 0;
    int32 err = piut_setservernode(getCurrentNodeName().c_str());
    if(err == 0 && testConnection())
    {
      return true;
    }
    else
    {  
      // Test failed, revert back to the node we were just connected to!
      _currentNodeIndex = lastNode;
      piut_setservernode(getCurrentNodeName().c_str());
      return false;
    }
  }
}

/**
 * This function is used in the <code>doUpdates()</code> 
 * function of <code>CtiFDRPiPoll</code> and 
 * <code>CtiFDRPiNotify</code> to retry the connection to the 
 * primary node if we're utilizing a PI collective and we're 
 * currently connected to a secondary node.
 */
void CtiFDRPiBase::attemptPrimaryReconnection()
{  
  if( ++_collectiveConnectionRetries % UpdatesPerRetry == 0 )
  {
    if(isCollectivePrimaryNodeOnline())
    {
      if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Connection test passed, switching to primary node " << getCurrentNodeName() << endl;
      }
    }
    else
    {
      if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
      {
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Connection test failed, remaining connected to secondary node " << getCurrentNodeName() << endl;
      }
    }
    _collectiveConnectionRetries = 0;
  }
}

int CtiFDRPiBase::getPiPointIdFromTag(const string& tagName, PiPointId& piId)
{
  char tagBuf[80]; // max length of a tag, pipt_findpoint writes the tag back to this buffer
  strcpy(tagBuf, tagName.c_str());
  piId = 0;
  int err = 0;
  err = pipt_findpoint(tagBuf, &piId);
  return err;
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
    if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Handling PI_REAL_POINT update for " << info.ctiPoint->getPointID()
        << ", rval=" << rval <<", UTC timestamp="<<timestamp_utc<<", Local TimeStamp="<< CtiTime(timestamp_utc)<< endl;
    }
    handleUpdate(info.ctiPoint, rval, timestamp_utc);
  }
  else if (info.piType == PI_INTEGER_POINT && istat >= 0)
  {
    if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
      CtiLockGuard<CtiLogger> doubt_guard( dout );
      logNow() << "Handling PI_INTEGER_POINT update for " << info.ctiPoint->getPointID()
        << ", rval=" << rval << ", istat=" << istat <<", UTC timestamp="<<timestamp_utc<<", Local TimeStamp="<< CtiTime(timestamp_utc)<< endl;
    }
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
      // value between 0 and info.digitalLastIndex, inclusive). Because the numerical value of
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
      if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
      {
        char buf[80] = "";
        pipt_digstate(istat, buf, 80);
        CtiLockGuard<CtiLogger> doubt_guard( dout );
        logNow() << "Handling PI_DIGITAL_POINT update for " << info.ctiPoint->getPointID()
          << ", rval=" << rval << ", istat=" << istat << ", state=" << state
          << ", offset=" << info.digitalOffset << ", lastIndex=" << info.digitalLastIndex
          << "digstate=" << buf <<", UTC timestamp="<<timestamp_utc<<", Local TimeStamp="<< CtiTime(timestamp_utc)<< endl;
      }
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

  CtiString serverNodeNames = gConfigParms.getValueAsString( KEY_SERVER_NODE_NAME, "" );

  // Determine if multiple server node names are present.
  if(!serverNodeNames.empty() && _serverNodeNames.empty())
  {
    while(!serverNodeNames.empty())
    {
      CtiString str = serverNodeNames.match("[-.A-Za-z0-9]*,?");
      if(!str.empty())
      {
        str = str.strip(CtiString::both, ' ');
        str = str.strip(CtiString::trailing, ',');
        str = str.strip(CtiString::both, ' ');
        _serverNodeNames.push_back(str);
      }

      serverNodeNames.replace("[-.A-Za-z0-9]*,?", "");
    }

    if(!_serverNodeNames.empty())
    {
      // Primary is the first node listed in the master.cfg file. 
      _currentNodeIndex = 0;
    }
  }

  _serverUsername = gConfigParms.getValueAsString( KEY_SERVER_USERNAME, "" );

  _serverPassword = gConfigParms.getValueAsString( KEY_SERVER_PASSWORD, "" );

  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "----------------FDRPiBase Configs------------------------------" << endl;
    dout << "  " << KEY_SERVER_NODE_NAME << (isCollectiveConnection() ? "S: " : ": ");
    for(int i = 0; i < _serverNodeNames.size(); i++)
    {
      dout << _serverNodeNames[i] << ((i + 1 < _serverNodeNames.size()) ? ", " : "\n");
    }
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













