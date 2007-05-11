/*
 *
 *    FILE NAME: fdracsmulti.cpp
 *
 *    DATE: 03/07/2001
 *
 *    PVCS KEYWORDS:
 *    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdracs.cpp-arc  $
 *    REVISION     :  $Revision$
 *    DATE         :  $Date$
 *
 *
 *    AUTHOR: David Sutton
 *
 *    PURPOSE: Interface to the Acs Foreign System
 *
 *    DESCRIPTION: This class implements an interface that exchanges point data
 *                 from an ACS scada system.  The data is both status and Analog data.
 *                Information is exchanged using sockets opened on a predefined socket
 *                number and also pre-defined messages between the systems.  See the
 *                design document for more information
 *
 *    ---------------------------------------------------
 *    History:
 *      $Log$
 *      Revision 1.9  2007/05/11 19:05:06  tspar
 *      YUK-3880
 *
 *      Refactored a few interfaces.
 *
 *      Revision 1.8  2006/01/16 21:09:52  mfisher
 *      removed RogueWave stuff out of comments
 *
 *      Revision 1.7  2006/01/03 20:23:37  tspar
 *      Moved non RW string utilities from rwutil.h to utility.h
 *
 *      Revision 1.6  2005/12/20 17:17:12  tspar
 *      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
 *
 *      Revision 1.5  2005/10/28 19:27:01  tmack
 *      Added a configuration parameter to set the link timeout value.
 *
 *      Revision 1.4  2005/10/05 19:10:37  tmack
 *      Fixed bug with handleStatusUpdate.
 *      Fixed small bug with time sync variation when the set time was less than the allowed minimum.
 *
 *      Revision 1.3  2005/09/14 16:22:27  tmack
 *      Made some small changes for ACS(MULTI), mostly related to logging and error handling.
 *
 *      Revision 1.2  2005/09/13 21:53:50  tmack
 *      Changed config file keys to contain "MULTI".
 *
 *      Revision 1.1  2005/09/13 20:37:27  tmack
 *      New file for the ACS(MULTI) implementation.
 *
 *
 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */
#include "yukon.h"


#include <windows.h>
#include <iostream>

#include <stdio.h>

/** include files **/

#include <rw/ctoken.h>
#include "ctidate.h"
#include "ctitime.h"
#include <boost/tokenizer.hpp>

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
#include "fdrpointlist.h"
#include "fdrsocketinterface.h"
#include "fdrscadahelper.h"
#include "utility.h"
// this class header
#include "fdracsmulti.h"


/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDRAcsMulti * acsInterface;

const CHAR * CtiFDRAcsMulti::KEY_LISTEN_PORT_NUMBER = "FDR_ACSMULTI_PORT_NUMBER";
const CHAR * CtiFDRAcsMulti::KEY_TIMESTAMP_WINDOW = "FDR_ACSMULTI_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDRAcsMulti::KEY_DB_RELOAD_RATE = "FDR_ACSMULTI_DB_RELOAD_RATE";
const CHAR * CtiFDRAcsMulti::KEY_QUEUE_FLUSH_RATE = "FDR_ACSMULTI_QUEUE_FLUSH_RATE";
const CHAR * CtiFDRAcsMulti::KEY_DEBUG_MODE = "FDR_ACSMULTI_DEBUG_MODE";
const CHAR * CtiFDRAcsMulti::KEY_OUTBOUND_SEND_RATE = "FDR_ACSMULTI_SEND_RATE";
const CHAR * CtiFDRAcsMulti::KEY_OUTBOUND_SEND_INTERVAL = "FDR_ACSMULTI_SEND_INTERVAL";
const CHAR * CtiFDRAcsMulti::KEY_TIMESYNC_VARIATION = "FDR_ACSMULTI_MAXIMUM_TIMESYNC_VARIATION";
const CHAR * CtiFDRAcsMulti::KEY_TIMESYNC_UPDATE = "FDR_ACSMULTI_RESET_PC_TIME_ON_TIMESYNC";
const CHAR * CtiFDRAcsMulti::KEY_POINT_TIME_VARIATION = "FDR_ACSMULTI_POINT_TIME_VARIATION";
const CHAR * CtiFDRAcsMulti::KEY_FDR_ACS_SERVER_NAMES = "FDR_ACSMULTI_SERVER_NAMES";
const CHAR * CtiFDRAcsMulti::KEY_LINK_TIMEOUT = "FDR_ACSMULTI_LINK_TIMEOUT_SECONDS";

// Constructors, Destructor, and Operators
CtiFDRAcsMulti::CtiFDRAcsMulti()
: CtiFDRScadaServer(string("ACSMULTI"))
{}

void CtiFDRAcsMulti::startup()
{
    init();
    _helper = new CtiFDRScadaHelper<CtiAcsId>(this);
}

CtiFDRAcsMulti::~CtiFDRAcsMulti()
{
    delete _helper;
}

/*************************************************
* Function Name: CtiFDRAcsMulti::config()
*
* Description: loads cparm config values
*
**************************************************
*/
int CtiFDRAcsMulti::readConfig()
{
    int         successful = TRUE;
    string   tempStr;

    setPortNumber(iConfigParameters.getValueAsInt( KEY_LISTEN_PORT_NUMBER, ACS_PORTNUMBER));

    setTimestampReasonabilityWindow(iConfigParameters.getValueAsInt(KEY_TIMESTAMP_WINDOW, 120));

    setReloadRate(iConfigParameters.getValueAsInt(KEY_DB_RELOAD_RATE, 86400));

    setQueueFlushRate(iConfigParameters.getValueAsInt(KEY_QUEUE_FLUSH_RATE, 1));

    setOutboundSendRate(iConfigParameters.getValueAsInt(KEY_OUTBOUND_SEND_RATE, 1));

    setOutboundSendInterval(iConfigParameters.getValueAsInt(KEY_OUTBOUND_SEND_INTERVAL, 0));

    setLinkTimeout(iConfigParameters.getValueAsInt(KEY_LINK_TIMEOUT, 60));

    setTimeSyncVariation(iConfigParameters.getValueAsInt(KEY_TIMESYNC_VARIATION, 30));
    if (getTimeSyncVariation() < 5)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Max time sync variation of "
                << getTimeSyncVariation()
                << " second(s) is invalid, defaulting to 5 seconds"
                << endl;
        }
        // default to 5 seconds
        setTimeSyncVariation(5);
    }

    setPointTimeVariation (iConfigParameters.getValueAsInt(KEY_POINT_TIME_VARIATION, 0));

    // default to true
    setUpdatePCTimeFlag (true);
    tempStr = iConfigParameters.getValueAsString(KEY_TIMESYNC_UPDATE, "true");
    if (!stringCompareIgnoreCase (tempStr,"false"))
    {
        setUpdatePCTimeFlag (false);
    }

    tempStr = iConfigParameters.getValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
    {
        setInterfaceDebugMode (true);
    }
    else
    {
        setInterfaceDebugMode (false);
    }

    tempStr = iConfigParameters.getValueAsString(KEY_FDR_ACS_SERVER_NAMES);
    std::string serverNames = tempStr;
    typedef boost::tokenizer<boost::char_separator<char> > tokenizer;
    boost::char_separator<char> sep(",");
    tokenizer pairTok(serverNames, sep);
    for (tokenizer::iterator pairIter = pairTok.begin();
         pairIter != pairTok.end();
         ++pairIter)
    {
        boost::char_separator<char> innerSep("= ");
        tokenizer innerTok(*pairIter, innerSep);
        tokenizer::iterator first = innerTok.begin();
        tokenizer::iterator end = innerTok.end();
        if (first != end)
        {
            tokenizer::iterator second = first;
            ++second;
            if (second != end)
            {
                std::string serverName = *first;
                std::string serverAddress = *second;
                _serverNameLookup[serverAddress] = serverName;
                if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "Added server mapping: " << serverAddress
                        << " -> " << serverName << endl;
                }
            }
        }
    }


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "----------------FDR-ACS Configs------------------------------" << endl;
        dout << "  " << KEY_LISTEN_PORT_NUMBER << ": "
            << getPortNumber() << endl;

        dout << "  " << KEY_TIMESTAMP_WINDOW << ": "
            << getTimestampReasonabilityWindow() << endl;

        dout << "  " << KEY_DB_RELOAD_RATE << ": "
            << getReloadRate() << endl;

        dout << "  " << KEY_QUEUE_FLUSH_RATE << ": "
            << getQueueFlushRate() << "       second(s)" << endl;

        dout << "  " << KEY_OUTBOUND_SEND_RATE << ": "
            << getOutboundSendRate() << endl;

        dout << "  " << KEY_OUTBOUND_SEND_INTERVAL << ": "
            << getOutboundSendInterval() << "       second(s)" << endl;

        dout << "  " << KEY_TIMESYNC_VARIATION << ": "
            << getTimeSyncVariation() << "       second(s)" << endl;

        dout << "  " << KEY_POINT_TIME_VARIATION << ": "
            << getPointTimeVariation() << "       second(s)" << endl;

        dout << "  " << KEY_LINK_TIMEOUT << ": "
            << getLinkTimeout() << "       second(s)" << endl;

        dout << "  " << KEY_TIMESYNC_UPDATE << ": "
            << (shouldUpdatePCTime() ? "TRUE" : "FALSE") << endl;

        dout << "  " << KEY_DEBUG_MODE << ": "
            << (isInterfaceInDebugMode() ? "TRUE" : "FALSE") << endl;

    }
    return successful;
}

CtiFDRClientServerConnection* CtiFDRAcsMulti::createNewConnection(SOCKET newSocket)
{
    sockaddr_in peerAddr;
    int peerAddrSize = sizeof(peerAddr);
    getpeername(newSocket, (SOCKADDR*) &peerAddr, &peerAddrSize);
    std::string ipString(inet_ntoa(peerAddr.sin_addr));
    std::string connName;
    ServerNameMap::const_iterator iter = _serverNameLookup.find(ipString);
    if (iter == _serverNameLookup.end())
    {
        connName = ipString;
    }
    else
    {
        connName = _serverNameLookup[ipString];
    }
    CtiFDRClientServerConnection* newConnection;
    newConnection = new CtiFDRClientServerConnection(connName.c_str(),
                                                 newSocket,
                                                 this);
    newConnection->setRegistered(true); //ACS doesn't have a separate registration message

    // I'm not sure this is the best location for this
    sendAllPoints(newConnection);

    return newConnection;
}

void CtiFDRAcsMulti::begineNewPoints()
{
    _helper->clearMappings();
}

bool CtiFDRAcsMulti::processNewDestination(CtiFDRDestination& pointDestination, bool isSend)
{
    bool successful = true;

    try
    {
        string remoteNumber = pointDestination.getTranslationValue("Remote");
        string pointNumber = pointDestination.getTranslationValue("Point");
        string categoryCode = pointDestination.getTranslationValue("Category");

        if (remoteNumber.empty() || pointNumber.empty() || categoryCode.empty())
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Unable to add destination " << pointDestination
                << " because one of the fields was blank" << endl;
            return false;
        }

        CtiAcsId acsId;
        acsId.RemoteNumber = atoi(remoteNumber.c_str());
        acsId.PointNumber = atoi(pointNumber.c_str());
        const char* temp = categoryCode.c_str(); // should be: acsId.CategoryCode = categoryCode[0]
        acsId.CategoryCode = temp[0];
        acsId.ServerName = pointDestination.getDestination();

        if (isSend)
        {
            _helper->addSendMapping(acsId, pointDestination);
        }
        else
        {
            _helper->addReceiveMapping(acsId, pointDestination);
        }
    } // end try

    catch (RWExternalErr e)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Exception in CtiFDRAcsMulti::processNewDestination(): " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Unknown exception in CtiFDRAcsMulti::processNewDestination()" << endl;
        successful = false;
    }
    return successful;
}

bool CtiFDRAcsMulti::buildForeignSystemMessage(const CtiFDRDestination& destination,
                                           char** buffer,
                                           unsigned int& bufferSize)
{
    CHAR* acs = NULL;
    CtiFDRPoint point = *(destination.getParentPoint());

   /* we allocate a acs message here and it will be deleted
    * inside of the write function on the connection
    */


    CtiAcsId acsId;
    if (!_helper->getIdForDestination(destination, acsId))
    {
        return false;
    }

    acs = new CHAR[sizeof (ACSInterface_t)];
    ACSInterface_t *ptr = (ACSInterface_t *)acs;

    // make sure we have all the pieces
    if (acs == NULL)
    {
        return false;
    }
    // set the timestamp, everything else is based on type of message
    strcpy (ptr->TimeStamp,  YukonToForeignTime (point.getLastTimeStamp()).c_str());

    ptr->Value.RemoteNumber = htons(acsId.RemoteNumber);
    ptr->Value.PointNumber = htons(acsId.PointNumber);
    ptr->Value.CategoryCode = acsId.CategoryCode;

    switch (point.getPointType())
    {
        case AnalogPointType:
        case CalculatedPointType:
        case PulseAccumulatorPointType:
        case DemandAccumulatorPointType:
            {
                ptr->Function = htons (SINGLE_SOCKET_VALUE);

                ptr->Value.Quality = YukonToForeignQuality (point.getQuality());
                ptr->Value.LongValue = CtiFDRSocketInterface::htonieeef (point.getValue());

                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "New Analog/Calculated value " << point.getValue() << " from "
                        << point << " queued to " << acsId << endl;
                }

                break;
            }

        case StatusPointType:
            {
                /* status point is both status and or control so we must check
                 * here if we are supposed to be sending a control
                 */
                if (point.isControllable())
                {
                    // we are doing control
                    ptr->Function = htons (SINGLE_SOCKET_CONTROL);

                    // check for validity of the status, we only have open or closed for ACS
                    if ((point.getValue() != OPENED) && (point.getValue() != CLOSED))
                    {
                        delete [] acs;
                        acs = NULL;

                        if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "Invalid control state of " << point.getValue()
                                << " for " << point << endl;
                        }
                    }
                    else
                    {
                        ptr->Control.Value = YukonToForeignStatus (point.getValue());

                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "New Control State "
                                << (point.getValue() == OPENED ? "OPEN" : "CLOSE")
                                << " (" << ptr->Control.Value << ") from "
                                << point << " queued to " << acsId << endl;
                        }
                    }
                }
                else
                {
                    ptr->Function = htons (SINGLE_SOCKET_STATUS);
                    // everything for control and status is the same except function
                    ptr->Status.Quality = YukonToForeignQuality (point.getQuality());

                    // check for validity of the status, we only have open or closed for ACS
                    if ((point.getValue() != OPENED) && (point.getValue() != CLOSED))
                    {
                        delete [] acs;
                        acs = NULL;

                        if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "State " << point.getValue()
                                << " is invalid for " << point << endl;
                        }
                    }
                    else
                    {
                        ptr->Status.Value = YukonToForeignStatus (point.getValue());

                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            logNow() << "New Status Value "
                                << (point.getValue() == OPENED ? "OPEN" : "CLOSE")
                                << " (" << ptr->Control.Value << ") from "
                                << point << " queued to " << acsId << endl;
                        }
                    }
                }

                break;
            }
        default:
            delete []acs;
            acs = NULL;
            break;
    }
    *buffer = acs;
    bufferSize = sizeof (ACSInterface_t);
    return acs != NULL;
}


bool CtiFDRAcsMulti::buildForeignSystemHeartbeatMsg(char** buffer,
                                                 unsigned int& bufferSize)
{
    CHAR* acs = NULL;

    /* we allocate a acs message here and it will be deleted
     * inside of the write function on the connection
     */
    acs = new CHAR[sizeof (ACSInterface_t)];
    ACSInterface_t* ptr = (ACSInterface_t* )acs;

    if (acs != NULL)
    {
        ptr->Function = htons (SINGLE_SOCKET_NULL);
        strcpy (ptr->TimeStamp, YukonToForeignTime (CtiTime()).c_str());
    }
    *buffer = acs;
    bufferSize = sizeof (ACSInterface_t);
    return acs != NULL;
}

unsigned int CtiFDRAcsMulti::getMessageSize(unsigned long header)
{
    return sizeof (ACSInterface_t);
}

bool CtiFDRAcsMulti::processValueMessage(CtiFDRClientServerConnection& connection,
                                     char* data, unsigned int size)
{
    ACSInterface_t     *acsData = (ACSInterface_t*)data;
    int                 quality;
    double              value;
    CtiTime              timestamp;

    CtiAcsId acsId = ForeignToYukonId(acsData->Value.RemoteNumber,
                                    acsData->Value.CategoryCode,
                                    acsData->Value.PointNumber,
                                    connection.getName());
    // assign last stuff
    quality = ForeignToYukonQuality(acsData->Value.Quality);
    timestamp = ForeignToYukonTime(acsData->TimeStamp);
    value = CtiFDRSocketInterface::ntohieeef(acsData->Value.LongValue);

    return _helper->handleValueUpdate(acsId, value, quality, timestamp);
}

bool CtiFDRAcsMulti::processStatusMessage(CtiFDRClientServerConnection& connection,
                                      char* data, unsigned int size)
{
    ACSInterface_t  *acsData = (ACSInterface_t*)data;
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;

    CtiAcsId acsId = ForeignToYukonId(acsData->Status.RemoteNumber,
                                    acsData->Status.CategoryCode,
                                    acsData->Status.PointNumber,
                                    connection.getName());

    // assign last stuff
    quality = ForeignToYukonQuality(acsData->Status.Quality);
    timestamp = ForeignToYukonTime(acsData->TimeStamp);
    value = ForeignToYukonStatus(acsData->Status.Value);

    return _helper->handleStatusUpdate(acsId, value, quality, timestamp);
}

bool CtiFDRAcsMulti::processControlMessage(CtiFDRClientServerConnection& connection, char* data, unsigned int size)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *acsData = (ACSInterface_t*)data;
    int                 quality =NormalQuality;
    CtiTime             timestamp;
    string              desc;
    CHAR                action[60];

    CtiAcsId acsId = ForeignToYukonId(acsData->Control.RemoteNumber,
                                    acsData->Control.CategoryCode,
                                    acsData->Control.PointNumber,
                                    connection.getName());


    int controlState = ForeignToYukonStatus (acsData->Control.Value);

    return _helper->handleControl(acsId, controlState);
}

bool CtiFDRAcsMulti::processTimeSyncMessage(CtiFDRClientServerConnection& connection, char* data, unsigned int size)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *acsData = (ACSInterface_t*)data;
    CtiTime              timestamp;

    timestamp = ForeignToYukonTime (acsData->TimeStamp,true);
    if (timestamp == PASTDATE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Time sync request was invalid: "
                <<  acsData->TimeStamp << endl;
        }
        return false;
    }

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
                {
                    timeNow.QuadPart +=
                        ( __int64)((timestamp.seconds() - now.seconds()) * 10000000);
                }
                else
                {
                    timeNow.QuadPart -=
                        ( __int64)((now.seconds() - timestamp.seconds()) * 10000000);
                }

                fileTime.dwLowDateTime = timeNow.LowPart;
                fileTime.dwHighDateTime = timeNow.HighPart;

                if (FileTimeToSystemTime (&fileTime, &sysTime))
                {
                    SetSystemTime (&sysTime);
                    std::ostringstream msg;
                    msg << getInterfaceName() << ": Request from " << connection
                        << " to change PC time has been processed.";
                    string desc(msg.str().c_str());
                    string action("New time = ");
                    action += timestamp.asString();
                    logEvent (desc,action,true);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Request from " << connection
                            << " to change PC time to " << timestamp.asString()
                            << " has been processed" << endl;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Unable to process time change (A)";
                    }
                    retVal = false;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "Unable to process time change (B)";
                }
                retVal = false;
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                logNow() << " Time change requested from " << connection
                    << " of " << timestamp.asString() << " is outside standard +/-30 minutes"
                    << endl;
            }
        }
    }

    return retVal;
}

CtiAcsId CtiFDRAcsMulti::ForeignToYukonId(USHORT remote, CHAR category, USHORT point,
                                      CtiFDRClientServerConnection::Destination serverName)
{
    CtiAcsId acsId;

    acsId.CategoryCode = category;
    acsId.PointNumber = ntohs(point);
    acsId.RemoteNumber = ntohs(remote);
    acsId.ServerName = serverName;

    return acsId;
}

USHORT CtiFDRAcsMulti::ForeignToYukonQuality (USHORT aQuality)
{
    USHORT Quality = NormalQuality;
    USHORT HostQuality;

    HostQuality = ntohs (aQuality);

    /* Test for the various ACS Qualities and translate to CTI */
    if (HostQuality & ACS_PLUGGED)
        Quality = NonUpdatedQuality;

    if (HostQuality & ACS_MANUALENTRY)
        Quality = ManualQuality;

    return(Quality);
}

USHORT CtiFDRAcsMulti::YukonToForeignQuality (USHORT aQuality)
{
    USHORT Quality = ACS_PLUGGED;

    /* Test for the various CTI Qualities and translate to ACS */
    if (aQuality == NonUpdatedQuality)
        Quality = ACS_PLUGGED;

    if (aQuality == ManualQuality)
        Quality = ACS_MANUALENTRY;

    if (aQuality == NormalQuality)
        Quality = ACS_NORMAL;

    if (aQuality == UnintializedQuality)
        Quality = ACS_PLUGGED;

    return(htons (Quality));
}


// Convert ACS status to CTI Status
int CtiFDRAcsMulti::ForeignToYukonStatus (USHORT aStatus)
{
    int tmpstatus=INVALID;

    switch (ntohs (aStatus))
    {
        case ACS_Open:
            tmpstatus = OPENED;
            break;
        case ACS_Closed:
            tmpstatus = CLOSED;
            break;
    }
    return(tmpstatus);
}


USHORT CtiFDRAcsMulti::YukonToForeignStatus (int aStatus)
{
    USHORT tmpstatus=ACS_Invalid;

    switch (aStatus)
    {
        case OPENED:
            tmpstatus = ACS_Open;
            break;
        case CLOSED:
            tmpstatus = ACS_Closed;
            break;
    }
    return(htons (tmpstatus));
}


CtiTime CtiFDRAcsMulti::ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag)
{
    struct tm ts;
    CtiTime retVal;

    if (sscanf (aTime,
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        retVal = PASTDATE;
    }
    else
    {
        ts.tm_year -= 1900;
        ts.tm_mon--;

        if (aTime[14] == 'D' ||
            aTime[14] == 'd')
        {
            ts.tm_isdst = TRUE;
        }
        else
        {
            ts.tm_isdst = FALSE;
        }

        CtiTime returnTime(&ts);

        if (aTimeSyncFlag)
        {
            // just check for validy
            if (!returnTime.isValid())
            {
                retVal = PASTDATE;
            }
            else
            {
                retVal = returnTime;
            }
        }
        else
        {
            // if we can't make a time or are outside the window
            if ((returnTime.seconds() > (CtiTime::now().seconds() + getTimestampReasonabilityWindow())) ||
                (returnTime.seconds() < (CtiTime::now().seconds() - getTimestampReasonabilityWindow())) ||
                (!returnTime.isValid()))
        //    if ((returnTime.seconds() > (CtiTime().seconds() + getTimestampReasonabilityWindow())) || (!returnTime.isValid()))
            {
                retVal = PASTDATE;
            }
            else
            {
                retVal = returnTime;
            }
        }
    }

    return retVal;
}

string CtiFDRAcsMulti::YukonToForeignTime (CtiTime aTimeStamp)
{
    CHAR      tmp[30];

    /*******************************
    * if the timestamp is less than 01-01-2000 (completely arbitrary number)
    * then set it to now because its probably an error or its uninitialized
    * note: uninitialized points come across as 11-10-1990
    ********************************
    */
    if (aTimeStamp < CtiTime(CtiDate(1,1,2001)))
    {
        aTimeStamp = CtiTime();
    }

    CtiDate tmpDate (aTimeStamp);

    // Place it into the ACS structure */
    _snprintf (tmp,26,
             "%4ld%02ld%02ld%02ld%02ld%02ldS",
             tmpDate.year(),
             tmpDate.month(),
             tmpDate.dayOfMonth(),
             aTimeStamp.hour(),
             aTimeStamp.minute(),
             aTimeStamp.second());

    if (aTimeStamp.isDST())
    {
        tmp[14] = 'D';
    }

    return(string (tmp));
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
        acsInterface = new CtiFDRAcsMulti();
        acsInterface->startup();
        // now start it up
        return acsInterface->run();
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

        acsInterface->stop();
        delete acsInterface;
        acsInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


