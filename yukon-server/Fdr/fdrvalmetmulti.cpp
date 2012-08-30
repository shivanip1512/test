#include "precompiled.h"

#include <iostream>

// get the STL into our namespace for use.  Do NOT use iostream.h anymore
#include <stdio.h>
#include <sstream>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

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
#include "fdrsocketinterface.h"
#include "fdrscadahelper.h"
#include "utility.h"
#include "dbaccess.h"

// this class header
#include "fdrvalmetmulti.h"
#include "fdrvalmetutil.h"

using std::string;
using std::endl;
using std::set;
using namespace Fdr::Valmet;

/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDR_ValmetMulti * valmetMultiInterface;

const CHAR * CtiFDR_ValmetMulti::KEY_LISTEN_PORT_NUMBER = "FDR_VALMETMULTI_PORT_NUMBER";
const CHAR * CtiFDR_ValmetMulti::KEY_TIMESTAMP_WINDOW = "FDR_VALMETMULTI_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_ValmetMulti::KEY_DB_RELOAD_RATE = "FDR_VALMETMULTI_DB_RELOAD_RATE";
const CHAR * CtiFDR_ValmetMulti::KEY_QUEUE_FLUSH_RATE = "FDR_VALMETMULTI_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_ValmetMulti::KEY_DEBUG_MODE = "FDR_VALMETMULTI_DEBUG_MODE";
const CHAR * CtiFDR_ValmetMulti::KEY_OUTBOUND_SEND_RATE = "FDR_VALMETMULTI_SEND_RATE";
const CHAR * CtiFDR_ValmetMulti::KEY_OUTBOUND_SEND_INTERVAL = "FDR_VALMETMULTI_SEND_INTERVAL";
const CHAR * CtiFDR_ValmetMulti::KEY_TIMESYNC_VARIATION = "FDR_VALMETMULTI_MAXIMUM_TIMESYNC_VARIATION";
const CHAR * CtiFDR_ValmetMulti::KEY_TIMESYNC_UPDATE = "FDR_VALMETMULTI_RESET_PC_TIME_ON_TIMESYNC";
const CHAR * CtiFDR_ValmetMulti::KEY_LINK_TIMEOUT = "FDR_VALMETMULTI_LINK_TIMEOUT_SECONDS";
const CHAR * CtiFDR_ValmetMulti::KEY_SCAN_DEVICE_POINTNAME = "FDR_VALMETMULTI_SCAN_DEVICE_POINTNAME";
const CHAR * CtiFDR_ValmetMulti::KEY_SEND_ALL_POINTS_POINTNAME = "FDR_VALMETMULTI_SEND_ALL_POINTS_POINTNAME";
const CHAR * CtiFDR_ValmetMulti::KEY_STARTUP_DELAY_SECONDS = "FDR_VALMETMULTI_STARTUP_DELAY";
const CHAR * CtiFDR_ValmetMulti::KEY_PORTS_TO_LOG = "FDR_VALMETMULTI_PORTS_TO_LOG";

// Constructors, Destructor, and Operators
CtiFDR_ValmetMulti::CtiFDR_ValmetMulti()
: CtiFDRScadaServer(string(FDR_VALMETMULTI)),
    _helper(NULL),
    _listenerThreadStartupDelay(0),
    _specificPortLoggingEnabled(false)
{
    // Set to prevent normal connection thread
    setSingleListeningPort(false);
    init();
    _helper = new CtiFDRScadaHelper<CtiValmetPortId>(this);
}

CtiFDR_ValmetMulti::~CtiFDR_ValmetMulti()
{
    delete _helper;
}

BOOL CtiFDR_ValmetMulti::run( void )
{
    // load up the base class
    CtiFDRScadaServer::run();

    _listnerStarterThread = rwMakeThreadFunction(*this, &CtiFDR_ValmetMulti::threadListenerStartupMonitor);
    _listnerStarterThread.start();

    return TRUE;
}

BOOL CtiFDR_ValmetMulti::stop( void )
{
    // stop the base class
    CtiFDRScadaServer::stop();

    _listnerStarterThread.requestCancellation();
    _listnerStarterThread.join();

    //Stop all listener threads happens in CtiFDRSocketServer
    stopMultiListeners();

    return TRUE;
}

void CtiFDR_ValmetMulti::startMultiListeners()
{
    CtiLockGuard<CtiMutex> lockGuard(_listeningThreadManagementMutex);

    for each(int port in _listeningPortNumbers)
    {
        PortNumToListenerThreadMap::iterator itr = _listenerThreads.find(port);
        if (itr == _listenerThreads.end())
        {
            RWThreadFunction thr = rwMakeThreadFunction(*(static_cast<CtiFDRSocketServer*>(this)),
                                                        &CtiFDR_ValmetMulti::threadFunctionConnection,
                                                        port,
                                                        _listenerThreadStartupDelay);
            thr.start();

            _listenerThreads[port] = thr;
        } // There is already a thread running.
    }

    _listeningPortNumbers.clear();
}

void CtiFDR_ValmetMulti::stopMultiListeners()
{
    CtiLockGuard<CtiMutex> lockGuard(_listeningThreadManagementMutex);

    for each(std::pair<int,RWThreadFunction> itr in _listenerThreads)
    {
        itr.second.requestCancellation();
        itr.second.join();
    }
}

/*************************************************
* Function Name: CtiFDR_ValmetMulti::config()
*
* Description: loads cparm config values
*
*************************************************/
int CtiFDR_ValmetMulti::readConfig()
{
    Inherited::readConfig();

    int linkTimeout = gConfigParms.getValueAsInt(KEY_LINK_TIMEOUT, 60);
    setLinkTimeout(linkTimeout);

    int portNumber = gConfigParms.getValueAsInt(KEY_LISTEN_PORT_NUMBER, VALMET_PORTNUMBER);
    setPortNumber(portNumber);

    int timestampWindow = gConfigParms.getValueAsInt(KEY_TIMESTAMP_WINDOW, 120);
    setTimestampReasonabilityWindow(timestampWindow);

    int reloadRate = gConfigParms.getValueAsInt(KEY_DB_RELOAD_RATE, 86400);
    setReloadRate(reloadRate);

    int queueFlushRate = gConfigParms.getValueAsInt(KEY_QUEUE_FLUSH_RATE, 1);
    setQueueFlushRate(queueFlushRate);

    int outboundSendRate = gConfigParms.getValueAsInt(KEY_OUTBOUND_SEND_RATE, 1);
    setOutboundSendRate(outboundSendRate);

    int outboundSendInterval = gConfigParms.getValueAsInt(KEY_OUTBOUND_SEND_INTERVAL, 0);
    setOutboundSendInterval(outboundSendInterval);

    int timesyncVariation = gConfigParms.getValueAsInt(KEY_TIMESYNC_VARIATION, 30);
    setTimeSyncVariation(timesyncVariation);

    if(getTimeSyncVariation() < 5)
    {
        if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Valmet max time sync variation of " << getTimeSyncVariation() << " second(s) is invalid, defaulting to 5 seconds" << endl;
        }
        // default to 5 seconds
        setTimeSyncVariation(5);
    }

    bool updatePcTimeFlag = gConfigParms.isTrue(KEY_TIMESYNC_UPDATE, true);
    setUpdatePCTimeFlag(updatePcTimeFlag);

    string tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    setInterfaceDebugMode(tempStr.length() > 0);

    _scanDevicePointName = gConfigParms.getValueAsString(KEY_SCAN_DEVICE_POINTNAME, "DEVICE_SCAN");
    _sendAllPointsPointName = gConfigParms.getValueAsString(KEY_SEND_ALL_POINTS_POINTNAME, "SEND_ALL_POINTS");

    _listenerThreadStartupDelay = gConfigParms.getValueAsInt(KEY_STARTUP_DELAY_SECONDS, 0);


    string portsToLogStr = gConfigParms.getValueAsString(KEY_PORTS_TO_LOG, "");
    _portsToLog = parseCommaSeparatedInt(portsToLogStr);
    if(_portsToLog.size() > 0)
    {
        _specificPortLoggingEnabled = true;
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Valmet Multi timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << CtiTime() << " Valmet Multi db reload rate " << getReloadRate() << endl;
        dout << CtiTime() << " Valmet Multi queue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << CtiTime() << " Valmet Multi send rate " << getOutboundSendRate() << endl;
        dout << CtiTime() << " Valmet Multi send interval " << getOutboundSendInterval() << " second(s) " << endl;
        dout << CtiTime() << " Valmet Multi max time sync variation " << getTimeSyncVariation() << " second(s) " << endl;
        dout << CtiTime() << " Valmet Multi link timeout " << getLinkTimeout() << " second(s) " << endl;
        dout << CtiTime() << " Valmet Multi force scan pointname " << _sendAllPointsPointName << endl;
        dout << CtiTime() << " Valmet Multi scan device compare string " << _scanDevicePointName << endl;
        dout << CtiTime() << " Valmet time sync will " + string(shouldUpdatePCTime() ? "" : "not ") + "reset PC clock" << endl;
        dout << CtiTime() << " Valmet running in " + string(isInterfaceInDebugMode() ? "debug" : "normal") + " mode" << endl;
        if (_specificPortLoggingEnabled)
        {
            dout << CtiTime() << " Valmet Multi logging only these ports: " << portsToLogStr << endl;
        }

    }

    return true;
}



void CtiFDR_ValmetMulti::begineNewPoints()
{
    signalReloadList();
}

void CtiFDR_ValmetMulti::signalReloadList()
{
    _helper->clearMappings();
    //The list is being reloaded. Clear our tracking map to be re-filled.
    _receiveNameToPointId.clear();
}

bool CtiFDR_ValmetMulti::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool foundPoint = false;

    for each(const CtiFDRDestination &pointDestination in translationPoint->getDestinationList())
    {
        foundPoint = true;

        string pointName = pointDestination.getTranslationValue("Point");
        string portNumber = pointDestination.getTranslationValue("Port");

        if (portNumber.empty() || pointName.empty())
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Unable to add destination " << pointDestination << " due to misconfiguration in the database. Missing the Point Name or Port" << endl;
            return false;
        }

        CtiValmetPortId valmetPortId;
        valmetPortId.PortNumber = atoi(portNumber.c_str());
        valmetPortId.PointName = pointName;

        if (valmetPortId.PortNumber != 0)
        {

            CtiLockGuard<CtiMutex> lockGuard(_listeningThreadManagementMutex);
            _listeningPortNumbers.insert(valmetPortId.PortNumber);
        }

        if (sendList)
        {
            _helper->addSendMapping(valmetPortId, pointDestination);
            insertPortToPointsMap(valmetPortId.PortNumber,translationPoint->getPointID());
        }
        else
        {
            int pointId = translationPoint->getPointID();
            string upperPointName = pointName;
            std::transform(upperPointName.begin(), upperPointName.end(), upperPointName.begin(), toupper);
            NameToPointIdMap::iterator itr = _receiveNameToPointId.find(upperPointName);
            if(itr == _receiveNameToPointId.end())
            {
                if(getDebugLevel() & TRANSLATION_LOADING_DEBUGLEVEL)
                {
                    // We don't have this point in our translation list yet, add it!
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << " Point [" << upperPointName << "," << pointId << "] added to translation map." << endl;
                }
                _receiveNameToPointId.insert(std::make_pair(upperPointName, pointId));
            }
            _helper->addReceiveMapping(valmetPortId, pointDestination);
        }
    }

    return foundPoint;
}

void CtiFDR_ValmetMulti::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    for each(const CtiFDRDestination &pointDestination in translationPoint->getDestinationList())
    {
        // translate and put the point id the list
        string pointName = pointDestination.getTranslationValue("Point");
        string portNumber = pointDestination.getTranslationValue("Port");

        if (portNumber.empty() || pointName.empty())
        {
            return;
        }

        CtiValmetPortId valmetPortId;
        valmetPortId.PortNumber = atoi(portNumber.c_str());
        valmetPortId.PointName = pointName;

        if (!recvList)
        {
            _helper->removeSendMapping(valmetPortId, pointDestination);
        }
        else
        {
            _helper->removeReceiveMapping(valmetPortId, pointDestination);
        }

        string upperPointName = pointName;
        std::transform(upperPointName.begin(), upperPointName.end(), upperPointName.begin(), toupper);
        _receiveNameToPointId.erase(upperPointName);

        removePortToPointsMap(valmetPortId.PortNumber,translationPoint->getPointID());
    }
}

CtiFDRClientServerConnectionSPtr CtiFDR_ValmetMulti::createNewConnection(SOCKET newSocket)
{
    sockaddr_in sockAddr;
    int sockAddrSize = sizeof(sockAddr);
    getsockname(newSocket, (SOCKADDR*) &sockAddr, &sockAddrSize);
    int port = ntohs(sockAddr.sin_port);

    std::stringstream ss;
    ss << "port_" << port;

    std::string connName = ss.str();
    CtiFDRClientServerConnectionSPtr newConnection(new CtiFDRClientServerConnection(connName.c_str(), newSocket, this));

    newConnection->setRegistered(true); //ACS doesn't have a separate registration message

    // I'm not sure this is the best location for this
    sendAllPoints(newConnection);

    return newConnection;
}

CtiFDRClientServerConnectionSPtr CtiFDR_ValmetMulti::findConnectionForDestination(const CtiFDRDestination destination) const
{
    // Port match needs to happen for ValmetMulti.
    int destPort = atoi(destination.getTranslationValue("Port").c_str());

    // Because new connections are put on the end of the list,
    // we want to search the list backwards so that we find
    // the newest connection that matches the destination.
    // Any prior connections are assumed to be a failed connection and the newest the reconnection.
    // IF we have 2 computers connecting at the same port, we WILL starve the older connections.
    ConnectionList::const_reverse_iterator myIter;

    CtiLockGuard<CtiMutex> guard(_connectionListMutex);
    for (myIter = _connectionList.rbegin(); myIter != _connectionList.rend(); ++myIter)
    {
        if ((*myIter)->getPortNumber() == destPort)
        {
            return (*myIter);
        }
    }

    return CtiFDRClientServerConnectionSPtr();
}

bool CtiFDR_ValmetMulti::buildForeignSystemMessage(const CtiFDRDestination& destination,
                                                   char** buffer,
                                                   unsigned int& bufferSize)
{
    CHAR *valmet=NULL;
    CtiFDRPoint point = *(destination.getParentPoint());

   /* we allocate a valmet message here and it will be deleted
    * inside of the write function on the connection
    */

    CtiValmetPortId valmetPortId;
    if (!_helper->getIdForDestination(destination, valmetPortId))
    {
        return false;
    }

    valmet = new CHAR[sizeof(ValmetExtendedInterface_t)];
    ValmetExtendedInterface_t *ptr = (ValmetExtendedInterface_t *)valmet;

    if (point.isCommStatus() && point.getValue() != 0)
    {
        updatePointQualitiesOnDevice(NonUpdatedQuality, point.getPaoID());
    }
   /**************************
    * we allocate a valmet message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */

    // make sure we have all the pieces
    if (valmet == NULL)
    {
        return false;
    }
    // set the timestamp, everything else is based on type of message
    strcpy (ptr->TimeStamp,  YukonToForeignTime (point.getLastTimeStamp()).c_str());

    switch (point.getPointType())
    {
        case AnalogPointType:
        case CalculatedPointType:
        case PulseAccumulatorPointType:
        case DemandAccumulatorPointType:
            {
                ptr->Function = htons (SINGLE_SOCKET_VALUE);
                strcpy(ptr->Value.Name,valmetPortId.PointName.c_str());
                ptr->Value.Quality = YukonToForeignQuality (point);
                ptr->Value.LongValue = CtiFDRSocketInterface::htonieeef (point.getValue());

                if (isPortLoggingNotRestricted(destination) && getDebugLevel () & DATA_SEND_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Analog/Calculated point " << point.getPointID();
                    dout << " queued as " << ptr->Value.Name;
                    dout << " value " << point.getValue() << " with quality of " << ForeignQualityToString(ptr->Value.Quality);
                    dout << " to " << getInterfaceName() << " on Port " << atoi(destination.getTranslationValue("Port").c_str()) << endl;
                }
                break;
            }

        case CalculatedStatusPointType:
        case StatusPointType:
            {
                if (point.isControllable())
                {
                    ptr->Function = htons (SINGLE_SOCKET_CONTROL);
                    strcpy (ptr->Control.Name,valmetPortId.PointName.c_str());

                    // check for validity of the status, we only have open or closed in controls
                    if ((point.getValue() != OPENED) && (point.getValue() != CLOSED))
                    {
                        delete [] valmet;
                        valmet = NULL;

                        if (isPortLoggingNotRestricted(destination) && getDebugLevel() & DATA_SEND_ERR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Point " << point.getPointID() << " State " << point.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                        }
                    }
                    else
                    {
                        ptr->Control.Value = YukonToForeignStatus (point.getValue());

                         if (isPortLoggingNotRestricted(destination) && getDebugLevel () & DATA_SEND_DEBUGLEVEL)
                         {
                             CtiLockGuard<CtiLogger> doubt_guard(dout);
                             dout << CtiTime() << " Control point " << point.getPointID();
                             dout << " queued as " << ptr->Control.Name;
                             if (point.getValue() == OPENED)
                             {
                                 dout << " state of Open ";
                             }
                             else
                             {
                                 dout << " state of Close ";
                             }
                             dout << "to " << getInterfaceName() << " on Port " << atoi(destination.getTranslationValue("Port").c_str()) << endl;;
                         }
                    }
                }
                else
                {
                    ptr->Function = htons (SINGLE_SOCKET_STATUS);
                    strcpy (ptr->Value.Name,valmetPortId.PointName.c_str());
                    ptr->Status.Quality = YukonToForeignQuality (point);

                    // check for validity of the status, we only have open or closed for Valmet
                    if ((point.getValue() != OPENED) && (point.getValue() != CLOSED))
                    {
                        delete [] valmet;
                        valmet = NULL;

                        if (isPortLoggingNotRestricted(destination) && getDebugLevel() & DATA_SEND_ERR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Point " << point.getPointID() << " State " << point.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                        }
                    }
                    else
                    {
                        ptr->Status.Value = YukonToForeignStatus (point.getValue());

                         if (isPortLoggingNotRestricted(destination) && getDebugLevel () & DATA_SEND_DEBUGLEVEL)
                         {
                             CtiLockGuard<CtiLogger> doubt_guard(dout);
                             dout << CtiTime() << " Status point " << point.getPointID();
                             dout << " queued as " << point.getTranslateName(string (FDR_VALMETMULTI));
                             if (point.getValue() == OPENED)
                             {
                                 dout << " state of Open ";
                             }
                             else
                             {
                                 dout << " state of Close ";
                             }
                             dout << "with quality of " << ForeignQualityToString(ptr->Status.Quality)<< " ";
                             dout << "to " << getInterfaceName() << endl;;
                         }
                    }
                }
                break;
            }
        default:
            delete []valmet;
            valmet = NULL;
            break;
    }
    *buffer = valmet;
    bufferSize = sizeof(ValmetExtendedInterface_t);

    return valmet != NULL;
}

bool CtiFDR_ValmetMulti::buildForeignSystemHeartbeatMsg(char** buffer, unsigned int& bufferSize)
{
    CHAR *valmet=NULL;

    /**************************
    * we allocate a valmet message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    valmet = new CHAR[sizeof(ValmetExtendedInterface_t)];
    ValmetExtendedInterface_t *ptr = (ValmetExtendedInterface_t *)valmet;
    bool retVal = true;

    if (valmet != NULL)
    {
        ptr->Function = htons (SINGLE_SOCKET_NULL);
        strcpy (ptr->TimeStamp, YukonToForeignTime (CtiTime()).c_str());
    }
    *buffer = valmet;
    bufferSize = sizeof(ValmetExtendedInterface_t);
    return valmet != NULL;
}

unsigned int CtiFDR_ValmetMulti::getMessageSize(const char* data)
{
    return sizeof (ValmetExtendedInterface_t);
}

string CtiFDR_ValmetMulti::decodeClientName(CHAR * aBuffer)
{
    return getInterfaceName();
}

bool CtiFDR_ValmetMulti::processTimeSyncMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* aData, unsigned int size)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetExtendedInterface_t  *data = (ValmetExtendedInterface_t*)aData;
    CtiTime              timestamp;
    string           desc;
    string               action;

    timestamp = ForeignToYukonTime (data->TimeStamp, getTimestampReasonabilityWindow(),true);
    if (timestamp == PASTDATE)
    {
        if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getInterfaceName() << "time sync request was invalid " <<  string (data->TimeStamp) << endl;
        }
        desc = getInterfaceName() + string (" time sync request was invalid ") + string (data->TimeStamp);
        logEvent (desc,action,true);
        retVal = !NORMAL;
    }
    else
    {
        CtiTime now;
        // check if the stamp is inside the window
        if (timestamp.seconds() > (now.seconds()-getTimeSyncVariation()) &&
            timestamp.seconds() < (now.seconds()+getTimeSyncVariation()))
        {
            retVal = NORMAL;
        }
        else
        {
            // timestamp is not inside the the window, is it realistic (inside 30 minutes)
            if (timestamp.seconds() > (now.seconds()-(30 * 60)) &&
                timestamp.seconds() < (now.seconds()+(30 * 60)))
            {
                // reset the time and log the change
                /**********************
                *   Straight from the help files
                *
                * It is not recommended that you add and subtract values
                * from the SYSTEMTIME structure to obtain relative times. Instead, you should
                *
                * Convert the SYSTEMTIME structure to a FILETIME structure.
                * Copy the resulting FILETIME structure to a ULARGE_INTEGER structure.
                * Use normal 64-bit arithmetic on the ULARGE_INTEGER value.
                ***********************
                */
                SYSTEMTIME  sysTime;
                FILETIME    fileTime;
                GetSystemTime(&sysTime);
                if (SystemTimeToFileTime (&sysTime, &fileTime))
                {
                    ULARGE_INTEGER timeNow; // 64 bit number of 100 nanosecond parts since 1601
                    timeNow.LowPart = fileTime.dwLowDateTime;
                    timeNow.HighPart = fileTime.dwHighDateTime;

                    // which way to move converted to 100 nanosecond parts
                    if (timestamp.seconds() > (now.seconds()))
                        timeNow.QuadPart += ( __int64)((timestamp.seconds() - now.seconds()) * 10000000);
                    else
                        timeNow.QuadPart -= ( __int64)((now.seconds() - timestamp.seconds()) * 10000000);

                    fileTime.dwLowDateTime = timeNow.LowPart;
                    fileTime.dwHighDateTime = timeNow.HighPart;

                    if (FileTimeToSystemTime (&fileTime, &sysTime))
                    {
                        SetSystemTime (&sysTime);
                        desc = getInterfaceName() + "'s request to change PC time to ";
                        desc += timestamp.asString() + " was processed";
                        action += "PC time reset to" + timestamp.asString();
                        logEvent (desc,action,true);

                        if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getInterfaceName() << "'s request to change PC time to " << timestamp.asString() << " was processed" << endl;
                        }
                    }
                    else
                    {
                        if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Unable to process time change from " << getInterfaceName();
                        }
                        desc = getInterfaceName() + "'s request to change PC time to ";
                        desc += timestamp.asString() + " failed";
                        action = string ("System time update API failed");
                        logEvent (desc,action,true);
                        retVal = !NORMAL;
                    }
                }
                else
                {
                    if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Unable to process time change from " << getInterfaceName();
                    }
                    desc = getInterfaceName() + "'s request to change PC time to ";
                    desc += timestamp.asString() + " failed";
                    action = string ("System time update API failed");
                    logEvent (desc,action,true);
                    retVal = !NORMAL;
                }
            }
            else
            {
                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Time change requested from " << getInterfaceName();
                    dout << " of " << timestamp.asString() << " is outside standard +-30 minutes " << endl;
                }

                //log we're way out of whack now
                desc = getInterfaceName() + "'s request to change PC time to ";
                desc += timestamp.asString() + " was denied";
                action = string ("Requested time is greater that +-30 minutes");
                logEvent (desc,action,true);
            }
        }
    }

    return retVal;
}

CtiFDRPointSPtr CtiFDR_ValmetMulti::findFdrPointInPointList(const std::string &translationName)
{
    CtiFDRPointSPtr point;

    CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());

    string pointName = translationName;
    std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);
    NameToPointIdMap::iterator iter = _receiveNameToPointId.find(pointName);
    if( iter != _receiveNameToPointId.end() ) {
        point = getReceiveFromList().getPointList()->findFDRPointID(iter->second);
    }

    return point;
}

bool CtiFDR_ValmetMulti::processValueMessage(Cti::Fdr::ServerConnection&connection,
                                         const char* aData, unsigned int size)
{
    int retVal = NORMAL, quality;
    CtiPointDataMsg *pData;
    ValmetExtendedInterface_t *data = (ValmetExtendedInterface_t*)aData;
    string desc;
    double value;
    CtiTime timestamp;
    char action[60];

    // convert to our name
    const string translationName = string(data->Value.Name);

    //Find
    CtiFDRPointSPtr point = findFdrPointInPointList(translationName);

    if (point)
    {
        if ((point->getPointType() == AnalogPointType) ||
            (point->getPointType() == PulseAccumulatorPointType) ||
            (point->getPointType() == DemandAccumulatorPointType) ||
            (point->getPointType() == CalculatedPointType))
        {
            // assign last stuff
            quality = ForeignToYukonQuality (data->Value.Quality);
            value = CtiFDRSocketInterface::ntohieeef (data->Value.LongValue);
            value *= point->getMultiplier();
            value += point->getOffset();

            timestamp = ForeignToYukonTime (data->TimeStamp, getTimestampReasonabilityWindow());
            if (timestamp == PASTDATE)
            {
                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getInterfaceName() << " analog value received with an invalid timestamp " <<  string (data->TimeStamp) << endl;
                }

                desc = getInterfaceName() + " analog point received with an invalid timestamp ";
                desc += string (data->TimeStamp);
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point->getPointID());
                logEvent (desc,string (action));
                retVal = !NORMAL;
            }
            else
            {
                if (point->isControllable())
                {
                    CtiCommandMsg *aoMsg = createAnalogOutputMessage(point->getPointID(), translationName, value);
                    sendMessageToDispatch(aoMsg);
                    return  NORMAL;
                }
                pData = new CtiPointDataMsg(point->getPointID(),
                                                value,
                                                quality,
                                                point->getPointType());
                pData->setTime(timestamp);

                // consumes a delete memory
                queueMessageToDispatch(pData);


                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Analog point " << translationName;
                    dout << " value " << value << " from " << getInterfaceName() << " assigned to point " << point->getPointID() << endl;;
                }
            }
        }
        else
        {
            if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Analog point " << translationName;
                    dout << " from " << getInterfaceName() << " was mapped incorrectly to non-analog point " << point->getPointID() << endl;
                }
                CHAR pointID[20];
                desc = getInterfaceName() + string (" analog point is incorrectly mapped to point ") + string (ltoa(point->getPointID(),pointID,10));
                _snprintf(action,60,"%s", translationName.c_str());
                logEvent (desc,string (action));
            }
            retVal = !NORMAL;
        }
    }
    else
    {
        if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Translation for analog point " << translationName;
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
            desc = getInterfaceName() + string (" analog point is not listed in the translation table");
            _snprintf(action,60,"%s", translationName.c_str());
            logEvent (desc,string (action));
        }
        retVal = !NORMAL;
    }

    return retVal;
}

bool CtiFDR_ValmetMulti::processStatusMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* aData, unsigned int size)
{
    int retVal = NORMAL, quality;
    CtiPointDataMsg *pData;
    ValmetExtendedInterface_t *data = (ValmetExtendedInterface_t*)aData;
    DOUBLE value;
    CtiTime timestamp;
    string desc;
    CHAR action[60];

    string translationName = string (data->Status.Name);

    CtiFDRPointSPtr point = findFdrPointInPointList(translationName);

    if(point)
    {
        if(point->getPointType() == StatusPointType)
        {
            // assign last stuff
            quality = ForeignToYukonQuality (data->Status.Quality);

            value = ForeignToYukonStatus (data->Status.Value);
            timestamp = ForeignToYukonTime (data->TimeStamp, getTimestampReasonabilityWindow());
            if (timestamp == PASTDATE)
            {
                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getInterfaceName() << " status value received with an invalid timestamp " <<  string (data->TimeStamp) << endl;
                }

                desc = getInterfaceName() + string (" status point received with an invalid timestamp ") + string (data->TimeStamp);
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point->getPointID());
                logEvent (desc,string (action));
                retVal = !NORMAL;
            }
            else
            {
                pData = new CtiPointDataMsg(point->getPointID(),
                                            value,
                                            quality,
                                            StatusPointType);

                pData->setTime(timestamp);

                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Status point " << translationName;
                    if (value == OPENED)
                    {
                        dout << " new state: Open " ;
                    }
                    else if (value == CLOSED)
                    {
                        dout << " new state: Closed " ;
                    }
                    else if (value == INDETERMINATE)
                    {
                        dout << " new state: Indeterminate " ;
                    }
                    else
                    {
                        dout << " new state: " << value;
                    }

                    dout <<" from " << getInterfaceName() << " assigned to point " << point->getPointID() << endl;;
                }
            }
        }
        else
        {
            if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Status point " << translationName;
                    dout << " from " << getInterfaceName() << " was mapped incorrectly to non-status point " << point->getPointID() << endl;
                }
                CHAR pointID[20];
                desc = getInterfaceName() + string (" status point is incorrectly mapped to point ") + string (ltoa(point->getPointID(),pointID,10));
                _snprintf(action,60,"%s", translationName.c_str());
                logEvent (desc,string (action));
            }
            retVal = !NORMAL;
        }
    }
    else
    {
        if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Translation for status point " <<  translationName;
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
            desc = getInterfaceName() + string (" status point is not listed in the translation table");
            _snprintf(action,60,"%s", translationName.c_str());
            logEvent (desc,string (action));
        }
        retVal = !NORMAL;
    }

    return retVal;
}

bool CtiFDR_ValmetMulti::isPortLoggingNotRestricted(Cti::Fdr::ServerConnection& connection)
{
    return isPortLoggingNotRestricted(connection.getPortNumber());
}

bool CtiFDR_ValmetMulti::isPortLoggingNotRestricted(const CtiFDRDestination& destination)
{
    string portStr = destination.getTranslationValue("Port");
    int portNumber = atoi(portStr.c_str());

    //0 is an invalid port. and 0 is returned from an invalid atoi() call. default to true
    if (portNumber != 0)
    {
        return isPortLoggingNotRestricted(portNumber);
    }
    return true;
}

bool CtiFDR_ValmetMulti::isPortLoggingNotRestricted(int portNumber)
{
    if (_specificPortLoggingEnabled)
    {
        set<int>::iterator itr = _portsToLog.find(portNumber);
        if (itr == _portsToLog.end())
        {
            return false;
        }
        return true;
    }
    return true;
}

int CtiFDR_ValmetMulti::processScanMessage(CtiFDRClientServerConnection* connection, const char* aData)
{
    ValmetExtendedInterface_t  *data = (ValmetExtendedInterface_t*)aData;
    string  translationName(data->ForceScan.Name);

    if (ciStringEqual(translationName, _sendAllPointsPointName))
    {
        if (isPortLoggingNotRestricted(*((Cti::Fdr::ServerConnection*)connection)) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Submitting Scan Request to process. Translation Point: " << translationName << " on " << connection->getName() << endl;
        }

        //Put an InitiateScan command on Dispatch inQueue, this will cause the send thread to build up the points to send
        CtiCommandMsg* cmdMessage = new CtiCommandMsg(CtiCommandMsg::InitiateScan);
        CtiCommandMsg::CtiOpArgList_t ops;
        ops.push_back(connection->getPortNumber());
        cmdMessage->setOpArgList(ops);
        iDispatchConn->getInQueueHandle().putQueue(cmdMessage);

        return NORMAL;
    }
    else
    {
        if (isPortLoggingNotRestricted(*((Cti::Fdr::ServerConnection*)connection)) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Ignored bad Scan Request for Translation Point: " << translationName << " on " << connection->getName() << endl;
        }
    }
    return NORMAL;
}

bool CtiFDR_ValmetMulti::processControlMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* aData, unsigned int size)
{
    int retVal = NORMAL, quality = NormalQuality;
    CtiPointDataMsg *pData;
    ValmetExtendedInterface_t *data = (ValmetExtendedInterface_t*)aData;
    USHORT value = ntohs(data->Control.Value);
    CtiTime timestamp;
    string desc;
    CHAR action[60];

    // convert to our name
    string translationName = string (data->Control.Name);

    CtiFDRPointSPtr point = findFdrPointInPointList(translationName);

    if (point && point->isControllable())
    {
        if (point->getPointType() == StatusPointType)
        {
            int controlState=INVALID;
            controlState = ForeignToYukonStatus (data->Control.Value);

            if ((controlState != OPENED) && (controlState != CLOSED))
            {
                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Control point " << translationName;
                    dout << " from " << getInterfaceName() << " has an invalid control state " << ntohs(data->Control.Value) << endl;
                }
                CHAR state[20];
                desc = getInterfaceName() + string (" control point received with an invalid state ") + string (itoa (ntohs(data->Control.Value),state,10));
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point->getPointID());
                logEvent (desc,string (action));
                retVal = !NORMAL;
            }
            else
            {
                // build the command message and send the control
                CtiCommandMsg *cmdMsg = NULL;
                if (stringContainsIgnoreCase(translationName, _scanDevicePointName))
                {
                    if (controlState == CLOSED)
                    {
                        cmdMsg = createScanDeviceMessage(point->getPaoID(), translationName);
                    }
                }
                else
                {
                    cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);
                    cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                    cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
                    cmdMsg->insert(point->getPointID());  // point for control
                    cmdMsg->insert(controlState);
                }
                if (cmdMsg)
                {
                    sendMessageToDispatch(cmdMsg);
                }

                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Control point " << translationName;
                    string controlString = (controlState == OPENED ? " control: Open " : " control: Closed ");
                    dout << controlString <<" from " << getInterfaceName() << " and processed for point " << point->getPointID() << endl;;
                }
            }
        }
        else if (point->getPointType() == AnalogPointType)
        {
            double dValue = CtiFDRSocketInterface::ntohieeef(data->Control.LongValue);
            if (point->getPointOffset() == 10001)
            {
                int controlState=INVALID;
                controlState = ForeignToYukonStatus (data->Control.Value);
                dValue = ForeignToYukonStatus (data->Control.Value);
                if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Analog Output Point " << translationName << " with PointOffset " << point->getPointOffset() << " received value of c:" << controlState << " d: " << dValue;
                }
            }
            CtiCommandMsg *aoMsg = createAnalogOutputMessage(point->getPointID(), translationName, dValue);
            sendMessageToDispatch(aoMsg);
            if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Analog Output point with pointOffset " << point->getPointOffset() << " " << translationName;
                dout << " value " << dValue << " from " << getInterfaceName() << " sending to device " << point->getPaoID() << endl;
            }
        }
    }
    else
    {
        if (!point)
        {
            if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Translation for control point " <<  translationName;
                    dout << " from " << getInterfaceName() << " was not found" << endl;
                }
                desc = getInterfaceName() + string (" control point is not listed in the translation table");
                _snprintf(action,60,"%s", translationName.c_str());
                logEvent (desc,string (action));
            }
        }
        else if (!point->isControllable())
        {
            if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Control point " << translationName;
                    dout << " received from " << getInterfaceName();
                    dout << " was not configured receive for control for point " << point->getPointID() << endl;
                }
                desc = getInterfaceName() + string (" control point is not configured to receive controls");
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point->getPointID());
                logEvent (desc,string (action));
            }
        }
        else
        {
            if (isPortLoggingNotRestricted(connection) && getDebugLevel () & DATA_RECV_ERR_DEBUGLEVEL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Control point " << translationName;
                    dout << " received from " << getInterfaceName();
                    dout << " was mapped to non-control point " <<  point->getPointID() << endl;;
                }
                CHAR pointID[20];
                desc = getInterfaceName() + string (" control point is incorrectly mapped to point ") + string (ltoa(point->getPointID(),pointID,10));
                _snprintf(action,60,"%s", translationName.c_str());
                logEvent (desc,string (action));
            }
        }
        retVal = !NORMAL;
    }

    return retVal;
}


void CtiFDR_ValmetMulti::updatePointQualitiesOnDevice(PointQuality_t quality, long paoId)
{
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Updating All Point Qualities on Device with ID: "<< paoId <<" with Send Direction to Quality of " << quality << endl;
    }

    CtiFDRManager* mgrPtr = getSendToList().getPointList();
    CtiFDRManager::readerLock guard(mgrPtr->getLock());
    CtiFDRPointSPtr point;

    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
    CtiFDRManager::spiterator myIterator = mgrPtr->getMap().begin();

    for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator )
    {
        // find the point id
        point = (*myIterator).second;

        if (point->getPaoID() == paoId && !point->isCommStatus() && point->getQuality() != quality)
        {
            CtiPointDataMsg* localMsg = new CtiPointDataMsg (point->getPointID(), point->getValue(), quality, point->getPointType());
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Updating quality to: " << quality << " for PointId: "<<point->getPointID() << " for " << getInterfaceName() << " interface."<< endl;
            }
            sendMessageToDispatch (localMsg);
        }
    }
}

bool CtiFDR_ValmetMulti::alwaysSendRegistrationPoints()
{
    return true;
}

void CtiFDR_ValmetMulti::threadListenerStartupMonitor( void )
{
    RWRunnableSelf pSelf = rwRunnable();
    int refreshDelaySeconds = 5;
    CtiTime timeNow;
    CtiTime refreshTime = timeNow + refreshDelaySeconds;

    try
    {
        if (getDebugLevel () & CONNECTION_INFORMATION_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << " Initializing CtiFDR_ValmetMulti::threadListenerStartupMonitor " << endl;
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = CtiTime();
            if (timeNow >= refreshTime)
            {
                startMultiListeners();
                refreshTime = timeNow + refreshDelaySeconds;
            }
        }
    }
    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "threadListenerStartupMonitor shutdown" << endl;
    }
    catch ( ... ) // try and catch the thread death
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error: threadListenerStartupMonitor is dead! " << endl;
    }
}


/****************************************************************************************
*
*      Here Starts some C functions that are used to Start the
*      Interface and Stop it from the Main() of FDR.EXE.
*
*/

#ifdef __cplusplus
extern "C" {
#endif

/************************************************************************
* Function Name: Extern C int RunInterface(void)
*
* Description: This is used to Start the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function creates a global FDRCygnet Object and then
*              calls its run method to cank it up.
*
*************************************************************************
*/

    DLLEXPORT int RunInterface(void)
    {

        // make a point to the interface
        valmetMultiInterface = new CtiFDR_ValmetMulti();

        // now start it up
        return valmetMultiInterface->run();
    }

/************************************************************************
* Function Name: Extern C int StopInterface(void)
*
* Description: This is used to Stop the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function stops a global FDRCygnet Object and then
*              deletes it.
*
*************************************************************************
*/
    DLLEXPORT int StopInterface( void )
    {

        valmetMultiInterface->stop();
        delete valmetMultiInterface;
        valmetMultiInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif



