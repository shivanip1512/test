#include "precompiled.h"

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
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "dbaccess.h"
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

using namespace std;

/** global used to start the interface by c functions **/
CtiFDRPiBase * myInterface;

const CHAR * CtiFDRPiBase::KEY_FLAVOR                   = "FDR_PI_FLAVOR";
const CHAR * CtiFDRPiBase::KEY_DB_RELOAD_RATE           = "FDR_PI_DB_RELOAD_RATE";
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
  _connected(false),
  _currentNodeIndex(0)
{
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
      CTILOG_INFO(dout, "Instantiating POLL flavor of FDRPI");
      return new CtiFDRPiPoll();
  }
  else
  {
      CTILOG_INFO(dout, "Instantiating NOTIFY flavor of FDRPI");
      return new CtiFDRPiNotify();
  }
}

/**
 * Attempt a connection to the primary PI server. If the attempt
 * to set the node and login to the server are successful, the
 * current node index is set to 0 to alert callers of
 * needConnetion() that we're happily connected to the primary.
 *
 * @return <code>true</code> if setting the active connection to
 *         the primary PI server and subsequent login attempt
 *         were successful,
 *         <code>false</code> otherwise.
 */
bool CtiFDRPiBase::tryPrimaryConnection()
{
  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
      CTILOG_DEBUG(dout, "Attempting connect to primary PI server: " << getPrimaryNodeName());
  }

  int32 err = piut_setservernode(getPrimaryNodeName().c_str());
  if (err != 0)
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
        const std::string piError = getPiErrorDescription(err, "piut_setservernode");
        CTILOG_DEBUG(dout, logNow() <<"Unable to set server node, piut_setservernode returned " << piError);
    }
    setConnected( false );
    return false;
  }
  else
  {
    if( serverNodeLogin() )
    {
      _currentNodeIndex = 0;
      return true;
    }
    else
    {
      return false;
    }
  }
}

/**
 * Attempt a connection to a secondary PI server. All handling
 * of secondary node switching occurs in this function. If a
 * secondary has been attempted too many times, future efforts
 * will attempt an alternative secondary node if any exist. This
 * function will never attempt a connection to the primary node.
 * If the attempt to set the node and login to the server are
 * successful, the connection failure count is reset.
 *
 * @return <code>true</code> if setting the active connection to
 *         a secondary PI server and subsequent login attempt
 *         were successful,
 *         <code>false</code> otherwise.
 */
bool CtiFDRPiBase::trySecondaryConnection()
{
  if( !isCollectiveConnection() )
  {
    // This just won't do! We don't have any secondary nodes!
    return false;
  }

  if(_currentNodeIndex == 0)
  {
    // We got in here because the connection to the primary wasn't any good.
    // Start with the first secondary and try to find a good backup to use!
    _currentNodeIndex = 1;
  }

  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
      CTILOG_DEBUG(dout, logNow() <<"Attempting connect to secondary PI server: "<< getCurrentNodeName());
  }

  int err = piut_setservernode(getCurrentNodeName().c_str());
  if (err != 0)
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
        const std::string piError = getPiErrorDescription(err, "piut_setservernode");

        CTILOG_DEBUG(dout, logNow() <<"Unable to set server node, piut_setservernode returned "<< piError);
    }

    setConnected( false );

    // We failed to connect to this secondary. Increment the failure count.
    _connectionFailureCount++;

    // Have we failed to connect enough times that it merits trying a new node?
    if( _connectionFailureCount >= FailureThreshold )
    {
        CTILOG_ERROR(dout, logNow() <<"Attempts to connect to PI server "<< getCurrentNodeName()
                <<" have failed. Attempting a node switch.");

      _currentNodeIndex = (_currentNodeIndex + 1) % _serverNodeNames.size();
      _connectionFailureCount = 0;
    }

    return false;
  }
  else
  {
    if( serverNodeLogin() )
    {
      _connectionFailureCount = 0;
      return true;
    }
    else
    {
      return false;
    }
  }
}

bool CtiFDRPiBase::needsConnection()
{
  return !isConnected() || (_currentNodeIndex != 0);
}

/**
 * Open a connection to the remote system.
 *
 * We are in this function for one of the following reasons:
 *    1) We are _NOT_ connected to any PI server
 *    2) We _ARE_ connected to a secondary PI server
 *    3) We lost connection to a secondary server (both of the
 *      above)
 */
bool CtiFDRPiBase::connect()
{
  if( !tryPrimaryConnection() )
  {
    trySecondaryConnection();
  }

  return isConnected();
}

/**
 * Attempt a login to the PI server we have currently set for
 * our active connection.
 */
bool CtiFDRPiBase::serverNodeLogin()
{
  int32 valid = 0;
  int err = piut_login(_serverUsername.c_str(),
                       _serverPassword.c_str(),
                       &valid);
  if (err != 0)
  {
    if( getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL )
    {
        const std::string piError = getPiErrorDescription(err, "piut_login");
        CTILOG_DEBUG(dout, logNow() <<"Unable to login to PI server, piut_login returned"<< piError <<", valid = "<< valid);
    }
    setConnected( false );
    return false;
  }
  else
  {
    if( getDebugLevel() & DETAIL_FDR_DEBUGLEVEL )
    {
        CTILOG_DEBUG(dout, logNow() <<"Login successful, access level: "<< valid);
    }
    return testConnection();
  }
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
        CTILOG_DEBUG(dout, logNow() <<"Connection test passed, setting connected");
    }
    setConnected( true );
  }
  else
  {
    if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
    {
        CTILOG_DEBUG(dout, logNow() <<"Connection test failed, forcing disconnect");
    }
    setConnected( false );
    piut_disconnect();
  }

  return isConnected();
}

void CtiFDRPiBase::setConnected( bool conn )
{
  if(_connected != conn)
  {
    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
    {
        CTILOG_DEBUG(dout, logNow() <<"Setting PI "<< (conn?"":"dis") << "connected");
    }
    _connected = conn;
  }
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
      const std::string piError = getPiErrorDescription(err, "pipt_findpoint");
      CTILOG_ERROR(dout, logNow() <<"Unable to find PI tag '"<< tagName <<
              "' for point "<< info.ctiPoint->getPointID() <<
              ", pipt_findpoint returned "<< piError);

    return;
  }
  info.piPointId = piId;

  char type = '\0';
  err = pipt_pointtype(piId, &type);
  if (err != 0) {
    if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
    {
        const std::string piError = getPiErrorDescription(err, "pipt_pointtype");
        CTILOG_DEBUG(dout, logNow() <<"Unable to get PI point type for tag "<< tagName <<
                ", pipt_pointtype returned "<< piError);
    }
    return;
  }
  info.piType = type;
  if (info.piType == PI_REAL_POINT || info.piType == PI_INTEGER_POINT)
  {
    if (info.ctiPoint->getPointType() != AnalogPointType)
    {
        CTILOG_ERROR(dout, logNow() <<"Incompatible type for point "<< info.ctiPoint->getPointID() <<
                "; expected AnalogPointType, got "<< info.ctiPoint->getPointType());

        return;
    }
  }
  else if (info.piType == PI_DIGITAL_POINT)
  {
    if (info.ctiPoint->getPointType() != StatusPointType)
    {
        CTILOG_ERROR(dout, logNow() <<"Incompatible type for point " << info.ctiPoint->getPointID() <<
                "; expected StatusPointType, got "<< info.ctiPoint->getPointType());

        return;
    }

    // Get offset needed to decode value
    int32 digcode, dignumb;
    err = pipt_digpointers(piId, &digcode, &dignumb);
    if (err != 0) {
      if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
      {
          const std::string piError = getPiErrorDescription(err, "pipt_digpointers");
          CTILOG_ERROR(dout, logNow() <<"Unable to get digital offset for tag "<< tagName <<
                  ", pipt_digpointers returned "<< piError);
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
        CTILOG_DEBUG(dout, logNow() <<"Unknown type "<< info.piType <<" for tag "<< tagName);
    }
    return;
  }

  processNewPiPoint(info);

  if( isDebugLevel( DETAIL_FDR_DEBUGLEVEL ) )
  {
      CTILOG_DEBUG(dout, logNow() <<"Added point "<< info.ctiPoint->getPointID() <<" and tag "<< tagName);
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

std::string CtiFDRPiBase::getPrimaryNodeName()
{
  if(_serverNodeNames.empty())
  {
    return "";
  }

  return _serverNodeNames[0];
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
                                const float64 rval,
                                const int32 ival,
                                const int32 istat,
                                const time_t timestamp_utc,
                                const int32 errorcode)
{
  if (errorcode != 0) {
    if( isDebugLevel( MIN_DETAIL_FDR_DEBUGLEVEL ) )
    {
        const std::string piError = getPiErrorDescription(errorcode);
        CTILOG_DEBUG(dout, logNow() <<"Unable to use value from Pi for point "<< info.ctiPoint->getPointID() <<
                ", error code "<< piError);
    }
    handleNonUpdate(info.ctiPoint, timestamp_utc);
  }
  else if (info.piType == PI_REAL_POINT && istat == 0)
  {
    if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
        CTILOG_DEBUG(dout, logNow() <<"Handling PI_REAL_POINT update for "<< info.ctiPoint->getPointID() <<
                ", rval="<< rval <<", UTC timestamp="<< timestamp_utc <<", Local TimeStamp="<< CtiTime(timestamp_utc));
    }
    handleUpdate(info.ctiPoint, rval, timestamp_utc);
  }
  else if (info.piType == PI_INTEGER_POINT)
  {
    if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
    {
        CTILOG_DEBUG(dout, logNow() <<"Handling PI_INTEGER_POINT update for "<< info.ctiPoint->getPointID() <<
                ", ival="<< ival <<", UTC timestamp="<<timestamp_utc<<", Local TimeStamp="<< CtiTime(timestamp_utc));
    }
    handleUpdate(info.ctiPoint, ival, timestamp_utc);
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
        CTILOG_DEBUG(dout, logNow() <<"Got not-good status for point "<< info.ctiPoint->getPointID() <<
                ", state="<< buf);
      }
      handleNonUpdate(info.ctiPoint, timestamp_utc);
    }
    else
    {
      if( isDebugLevel( MAJOR_DETAIL_FDR_DEBUGLEVEL ) )
      {
        char buf[80] = "";
        pipt_digstate(istat, buf, 80);
        CTILOG_DEBUG(dout, logNow() <<"Handling PI_DIGITAL_POINT update for "<< info.ctiPoint->getPointID() <<
                ", rval="<< rval <<", istat="<< istat <<", state="<< state <<
                ", offset="<< info.digitalOffset <<", lastIndex="<< info.digitalLastIndex <<
                "digstate="<< buf <<", UTC timestamp="<<timestamp_utc<<", Local TimeStamp="<< CtiTime(timestamp_utc));
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
      CTILOG_DEBUG(dout, logNow() <<"Got not-good status for point "<< info.ctiPoint->getPointID() <<
              "; rval="<< rval <<", state="<< buf);
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

  std::string serverNodeNames = gConfigParms.getValueAsString( KEY_SERVER_NODE_NAME, "" );

  // Determine if multiple server node names are present.
  boost::char_separator<char> sep(", ");
  Boost_char_tokenizer tokens(serverNodeNames, sep);

  for each( std::string str in tokens )
  {
    _serverNodeNames.push_back(str);
  }

  _serverUsername = gConfigParms.getValueAsString( KEY_SERVER_USERNAME, "" );

  _serverPassword = gConfigParms.getValueAsString( KEY_SERVER_PASSWORD, "" );

  if( getDebugLevel() & STARTUP_FDR_DEBUGLEVEL )
  {
      Cti::FormattedList loglist;

      {
          Cti::StreamBufferSink& logNames = loglist.add(std::string(KEY_SERVER_NODE_NAME) + (isCollectiveConnection() ? "S" : ""));
          for(int i=0; i < _serverNodeNames.size(); i++)
          {
              logNames << (i ? ", ":"") << _serverNodeNames[i];
          }
      }

      loglist.add(KEY_SERVER_USERNAME) << _serverUsername;
      loglist.add(KEY_SERVER_PASSWORD) << _serverPassword;

      CTILOG_DEBUG(dout, "FDRPiBase Configs"<<
              loglist);
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

time_t CtiFDRPiBase::piToYukonTime(PITIMESTAMP piTimeStamp)
{
     time_t time = CtiTime().seconds();
     struct tm* temp = CtiTime().localtime_r(&time);
     temp->tm_year =  piTimeStamp.year - 1900;
     temp->tm_mon  =  piTimeStamp.month - 1;
     temp->tm_mday =  piTimeStamp.day;
     temp->tm_hour =  piTimeStamp.hour;
     temp->tm_min  =  piTimeStamp.minute;
     temp->tm_sec  =  piTimeStamp.second;

     return mktime(temp);
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

