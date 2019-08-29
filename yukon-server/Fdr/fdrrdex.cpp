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
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"
#include "fdrsocketlayer.h"
#include "fdrserverconnection.h"
#include "socket_helper.h"

// this class header
#include "fdrrdex.h"

using namespace std;

/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDR_Rdex * rdexInterface;

const CHAR * CtiFDR_Rdex::KEY_LISTEN_PORT_NUMBER = "FDR_RDEX_PORT_NUMBER";
const CHAR * CtiFDR_Rdex::KEY_TIMESTAMP_WINDOW = "FDR_RDEX_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_Rdex::KEY_DB_RELOAD_RATE = "FDR_RDEX_DB_RELOAD_RATE";
const CHAR * CtiFDR_Rdex::KEY_QUEUE_FLUSH_RATE = "FDR_RDEX_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Rdex::KEY_DEBUG_MODE = "FDR_RDEX_DEBUG_MODE";
const CHAR * CtiFDR_Rdex::KEY_OUTBOUND_SEND_RATE = "FDR_RDEX_SEND_RATE";
const CHAR * CtiFDR_Rdex::KEY_OUTBOUND_SEND_INTERVAL = "FDR_RDEX_SEND_INTERVAL";
const CHAR * CtiFDR_Rdex::KEY_LINK_TIMEOUT = "FDR_RDEX_LINK_TIMEOUT_SECONDS";
// Constructors, Destructor, and Operators
CtiFDR_Rdex::CtiFDR_Rdex()
: CtiFDRSingleSocket(string("RDEX"))
{
    setRegistered (false);
    init();
}


CtiFDR_Rdex::~CtiFDR_Rdex()
{
}

bool CtiFDR_Rdex::isRegistrationNeeded()
{
    return true;
}

/*************************************************
* Function Name: CtiFDR_ACS::config()
*
* Description: loads cparm config values
*
**************************************************
*/
bool CtiFDR_Rdex::readConfig()
{
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setPortNumber (RDEX_PORTNUMBER);
    }

    tempStr = getCparmValueAsString(KEY_LINK_TIMEOUT);
    if (tempStr.length() > 0)
    {
        setLinkTimeout (atoi(tempStr.c_str()));
    }
    else
    {
        setLinkTimeout (60);
    }

    tempStr = getCparmValueAsString(KEY_TIMESTAMP_WINDOW);
    if (tempStr.length() > 0)
    {
        setTimestampReasonabilityWindow (atoi (tempStr.c_str()));
    }
    else
    {
        setTimestampReasonabilityWindow (120);
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    }
    else
    {
        // default to 1
        setQueueFlushRate (1);
    }

    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);


    tempStr = getCparmValueAsString(KEY_OUTBOUND_SEND_RATE);
    if (tempStr.length() > 0)
    {
        setOutboundSendRate (atoi(tempStr.c_str()));
    }
    else
    {
        // default to 1
        setOutboundSendRate (1);
    }
    tempStr = getCparmValueAsString(KEY_OUTBOUND_SEND_INTERVAL);
    if (tempStr.length() > 0)
    {
        setOutboundSendInterval (atoi(tempStr.c_str()));
    }
    else
    {
        // default to 1
        setOutboundSendInterval (0);
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("Rdex port number")      << getPortNumber();
        loglist.add("Rdex timestamp window") << getTimestampReasonabilityWindow();
        loglist.add("Rdex db reload rate")   << getReloadRate();
        loglist.add("Rdex queue flush rate") << getQueueFlushRate() << " second(s)";
        loglist.add("Rdex send rate")        << getOutboundSendRate();
        loglist.add("Rdex send interval")    << getOutboundSendInterval() << " second(s)";

        if (isInterfaceInDebugMode())
            loglist <<"Rdex running in debug mode";
        else
            loglist <<"Rdex running in normal mode";

        CTILOG_DEBUG(dout, loglist);
    }
    return true;
}


bool CtiFDR_Rdex::translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aDestinationIndex)
{
    bool                successful(false);
    string           tempString2;
    string           translationName;
    bool                foundPoint = false;

    try
    {
        tempString2 = translationPoint->getDestinationList()[aDestinationIndex].getTranslationValue("Translation");

        // now we have a translation name
        if ( !tempString2.empty() )
        {
            // put translation in final name
            translationName= tempString2;

            // i'm updating my copied list
            translationPoint->getDestinationList()[aDestinationIndex].setTranslation (tempString2);
            successful = true;
        }   // no point name

    }
    // try and catch the thread death
    catch ( ... )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );

        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return successful;
}

CHAR *CtiFDR_Rdex::buildForeignSystemMsg ( CtiFDRPoint &aPoint )
{
   /**************************
    * we allocate a rdex message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    CHAR * buffer = new CHAR[ sizeof RdexInterface_t ];     // allocate

    // make sure we have all the pieces
    if (buffer != NULL)
    {
        RdexInterface_t * ptr = reinterpret_cast<RdexInterface_t *>( buffer );  // get a "more useful" representation

        *ptr = {};      // zero initialize 

        // Moved this out to catch the case where we don't find the destination.
        string translationName = aPoint.getTranslateName(getLayer()->getName());

        if (!translationName.empty())
        {
            // set the timestamp, everything else is based on type of message
            strcpy (ptr->timestamp,  YukonToForeignTime (aPoint.getLastTimeStamp()).c_str());

            // set the translation name for all valid messages
            strcpy (ptr->value.translation,translationName.c_str());

            switch (aPoint.getPointType())
            {
                case AnalogPointType:
                case CalculatedPointType:
                case PulseAccumulatorPointType:
                case DemandAccumulatorPointType:
                    {
                        ptr->function = htonl (SINGLE_SOCKET_VALUE);

                        ptr->value.quality = YukonToForeignQuality (aPoint.getQuality());
                        ptr->value.longValue = htonieeef (aPoint.getValue());

                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "Analog/Calculated point "<< aPoint.getPointID() <<
                                    " queued as "<< aPoint.getTranslateName(getLayer()->getName()) <<
                                    " value "<< aPoint.getValue() <<" to "<< getLayer()->getName());
                        }

                        break;
                    }

                case CalculatedStatusPointType:
                case StatusPointType:
                    {
                        if (aPoint.isControllable())
                        {
                            ptr->function = htonl (SINGLE_SOCKET_CONTROL);

                            // check for validity of the status, we only have open or closed for rdex
                            if ((aPoint.getValue() != STATE_OPENED) && (aPoint.getValue() != STATE_CLOSED))
                            {
                                delete [] buffer;
                                buffer = NULL;

                                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Point "<< aPoint.getPointID() <<" State "<< aPoint.getValue() <<" is invalid for Rdex interface to "<< getLayer()->getName());
                                }
                            }
                            else
                            {
                                ptr->control.value = YukonToForeignStatus (aPoint.getValue());

                                 if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                 {
                                     Cti::StreamBuffer logmsg;

                                     logmsg <<" Control point "<< aPoint.getPointID() <<" queued as "<< aPoint.getTranslateName(getLayer()->getName());
                                     if (aPoint.getValue() == STATE_OPENED)
                                     {
                                         logmsg << " state of Open ";
                                     }
                                     else
                                     {
                                         logmsg << " state of Close ";
                                     }
                                     logmsg <<"to "<< getLayer()->getName();

                                     CTILOG_DEBUG(dout, logmsg);
                                 }
                            }
                        }
                        else
                        {
                            ptr->function = htonl (SINGLE_SOCKET_STATUS);

                            ptr->status.quality = YukonToForeignQuality (aPoint.getQuality());

                            // check for validity of the status, we only have open or closed for rdex
                            if ((aPoint.getValue() != STATE_OPENED) && (aPoint.getValue() != STATE_CLOSED))
                            {
                                delete [] buffer;
                                buffer = NULL;

                                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Point "<< aPoint.getPointID() <<" State "<< aPoint.getValue() <<" is invalid for Rdex interface to "<< getLayer()->getName());
                                }
                            }
                            else
                            {
                                ptr->status.value = YukonToForeignStatus (aPoint.getValue());

                                 if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                 {
                                     Cti::StreamBuffer logmsg;

                                     logmsg <<" Status point "<< aPoint.getPointID() <<" queued as "<< aPoint.getTranslateName(getLayer()->getName());
                                     if (aPoint.getValue() == STATE_OPENED)
                                     {
                                         logmsg << " state of Open ";
                                     }
                                     else
                                     {
                                         logmsg << " state of Close ";
                                     }
                                     logmsg << "to " << getLayer()->getName();

                                     CTILOG_DEBUG(dout, logmsg);
                                 }
                            }
                        }
                        break;
                    }
                default:
                    delete []buffer;
                    buffer = NULL;
                    break;

            }
        }
        else
        {
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Destination not found to send update to. Point is misconfigured or registered incorrectly. ID: " << aPoint.getPointID());
            }
            delete []buffer;
            buffer = NULL;
        }
    }
    return buffer;
}

CHAR *CtiFDR_Rdex::buildForeignSystemHeartbeatMsg ()
{
    // check registration here, we're not supposed to send if we haven't registered
    if ( ! isRegistered() )
    {
        CTILOG_WARN(dout, "Not building heartbeat message, not registered");
        return nullptr;
    }

    /**************************
    * we allocate a rdex message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */

    CHAR * buffer = new CHAR[ sizeof RdexInterface_t ];     // allocate

    if (buffer != NULL)
    {
        RdexInterface_t * ptr = reinterpret_cast<RdexInterface_t *>( buffer );  // get a "more useful" representation

        *ptr = {};      // zero initialize

        ptr->function = htonl (SINGLE_SOCKET_NULL);
        strcpy (ptr->timestamp, YukonToForeignTime (CtiTime()).c_str());
    }

    return buffer;
}

int CtiFDR_Rdex::getMessageSize(CHAR *aBuffer)
{
    return sizeof (RdexInterface_t);
}


string CtiFDR_Rdex::decodeClientName(CHAR * aBuffer)
{
    RdexInterface_t *ptr = (RdexInterface_t *)aBuffer;

    if (ptr == NULL)
    {
        return string("RDEX");
    }
    else
    {
        return string(ptr->registration.clientName);
    }

}

int CtiFDR_Rdex::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = ClientErrors::None;
    RdexInterface_t *rdex = (RdexInterface_t *)aBuffer;

    switch (ntohl (rdex->function))
    {
        case SINGLE_SOCKET_VALUE:
            {
                retVal = processValueMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_REGISTRATION:
            {
                retVal = processRegistrationMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_STATUS:
            {
                retVal = processStatusMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_CONTROL:
        case SINGLE_SOCKET_VALMET_CONTROL:
            {
                retVal = processControlMessage (aBuffer);
                break;
            }
        case SINGLE_SOCKET_TIMESYNC:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Time sync message received from "<< getLayer()->getName());
                }
                break;
            }
        case SINGLE_SOCKET_NULL:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Heartbeat message received from "<< getLayer()->getName() <<" at "<< getLayer()->getInBoundConnection()->getAddr());
                }
                break;
            }
        default:
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Unknown message type "<< ntohl (rdex->function) <<" received from "<< getInterfaceName());
            }
    }

    return retVal;

}

int CtiFDR_Rdex::processRegistrationMessage(CHAR *aData)
{
    int retVal = ClientErrors::Abnormal;
    Rdex_Registration *data = (Rdex_Registration*)aData;
    string           desc;
    string           action;

    getLayer()->setName (string (data->clientName));

    /**************************
    * we allocate a rdex message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    CHAR * buffer = new CHAR[ sizeof RdexInterface_t ];     // allocate

    if (buffer != NULL)
    {
        Rdex_Acknowledgement * ptr = reinterpret_cast<Rdex_Acknowledgement *>( buffer );    // get a "more useful" representation

        *ptr = {};      // zero initialize

        ptr->function = htonl (SINGLE_SOCKET_ACKNOWLEDGEMENT);
        strcpy (ptr->timestamp, YukonToForeignTime (CtiTime()).c_str());
        strcpy (ptr->serverName, "YUKON_RDEX");
        Sleep (500);  // this a dangerous way to do this
        if (getLayer()->write (buffer))
        {
            CTILOG_ERROR(dout, "Registration acknowledgment to "<< getLayer()->getName() <<" failed");

            action = getLayer()->getName() + ": Acknowledgment";
            desc = getLayer()->getName() + " registration acknowledgment message has failed";
            logEvent (desc,action,true);
            retVal = ClientErrors::Abnormal;
        }
        else
        {
            setRegistered (true);
            CTILOG_INFO(dout, getLayer()->getName() <<" has registered with Yukon");

            action = getLayer()->getName() + ": Registration";
            desc = getLayer()->getName() + " has registered with Yukon";
            logEvent (desc,action,true);

            retVal = ClientErrors::None;
        }
    }

    return retVal;
}


int CtiFDR_Rdex::processValueMessage(CHAR *aData)
{
    int retVal = ClientErrors::Abnormal;
    CtiPointDataMsg     *pData;
    RdexInterface_t  *data = (RdexInterface_t*)aData;
    string           translationName;
    int                 quality;
    FLOAT               value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool               flag = true;
    CHAR               action[60];
    string          desc;

    // accept nothing until we are registered
    if (isRegistered())
    {
        // convert to our name
        translationName = string (data->value.translation);

        // see if the point exists
         flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

        if ((flag == true) &&
            ((point.getPointType() == AnalogPointType) ||
             (point.getPointType() == PulseAccumulatorPointType) ||
             (point.getPointType() == DemandAccumulatorPointType) ||
             (point.getPointType() == CalculatedPointType)))

        {
            // assign last stuff
            quality = ForeignToYukonQuality (data->value.quality);
            value = ntohieeef (data->value.longValue);
            value *= point.getMultiplier();
            value += point.getOffset();

            timestamp = ForeignToYukonTime (data->timestamp);
            if (timestamp == PASTDATE)
            {
                desc = getInterfaceName() + string (" analog point received with an invalid timestamp ") + string (data->timestamp);
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point.getPointID());
                logEvent (desc,string (action));
                retVal = ClientErrors::Abnormal;
            }
            else
            {
                pData = new CtiPointDataMsg(point.getPointID(),
                                            value,
                                            quality,
                                            point.getPointType());

                pData->setTime(timestamp);

                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Analog point "<< translationName <<" value "<< value <<" from "<< getLayer()->getName() <<
                            " assigned to point " << point.getPointID());
                }

                retVal = ClientErrors::None;
            }
        }
        else
        {
            if (flag == false)
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Translation for analog point "<< translationName <<" from "<< getLayer()->getName() <<
                            " was not found");

                    desc = getInterfaceName() + string (" analog point is not listed in the translation table");
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
            }
            else
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Analog point "<< translationName <<" from "<< getLayer()->getName() <<
                            " was mapped incorrectly to non-analog point "<< point.getPointID());

                    CHAR pointID[20];
                    desc = getInterfaceName() + string (" analog point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
            }
            retVal = ClientErrors::Abnormal;
        }
    }
    return retVal;
}

int CtiFDR_Rdex::processStatusMessage(CHAR *aData)
{
    int retVal = ClientErrors::Abnormal;
    CtiPointDataMsg     *pData;
    RdexInterface_t  *data = (RdexInterface_t*)aData;
    string           translationName;
    int                 quality;
    FLOAT               value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    CHAR                action[60];
    string           desc;

    // accept nothing until we are registered
    if (isRegistered())
    {
        translationName = string (data->status.translation);

        // see if the point exists
        flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

        if ((flag == true) && (point.getPointType() == StatusPointType))
        {
            // assign last stuff
            quality = ForeignToYukonQuality (data->status.quality);
            value = ForeignToYukonStatus (data->status.value);
            timestamp = ForeignToYukonTime (data->timestamp);

            if ((timestamp == PASTDATE) || (value == Rdex_Invalid))
            {
                if (timestamp == PASTDATE)
                {
                    desc = getInterfaceName() + string (" status point received with an invalid timestamp ") + string (data->timestamp);
                    _snprintf(action,60,"%s for pointID %d",
                              translationName.c_str(),
                              point.getPointID());
                    logEvent (desc,string (action));
                }
                else
                {
                    CTILOG_ERROR(dout, "Status point "<< translationName <<" from "<< getLayer()->getName()<<" has an invalid state " <<ntohl(data->status.value));

                    CHAR state[20];
                    desc = getInterfaceName() + string (" status point received with an invalid state ") + string (itoa (ntohl(data->status.value),state,10));
                    _snprintf(action,60,"%s for pointID %d",
                              translationName.c_str(),
                              point.getPointID());
                    logEvent (desc,string (action));
                }
                retVal = ClientErrors::Abnormal;
            }
            else
            {
                pData = new CtiPointDataMsg(point.getPointID(),
                                            value,
                                            quality,
                                            StatusPointType);

                pData->setTime(timestamp);

                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    Cti::StreamBuffer logmsg;

                    logmsg <<" Status point "<< translationName;
                    if (value == STATE_OPENED)
                    {
                        logmsg <<" new state: Open ";
                    }
                    else
                    {
                        logmsg <<" new state: Closed ";
                    }
                    logmsg <<" from " << getLayer()->getName() << " assigned to point " << point.getPointID();

                    CTILOG_DEBUG(dout, logmsg);
                }

                retVal = ClientErrors::None;
            }
        }
        else
        {
            if (flag == false)
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Translation for status point "<< translationName <<" from "<< getLayer()->getName() <<
                            " was not found");

                    desc = getInterfaceName() + string (" status point is not listed in the translation table");
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
            }
            else
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Status point "<< translationName <<" from "<< getLayer()->getName() <<
                            " was mapped incorrectly to non-status point "<< point.getPointID());

                    CHAR pointID[20];
                    desc = getInterfaceName() + string (" status point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
            }
            retVal = ClientErrors::Abnormal;
        }
    }
    return retVal;
}

int CtiFDR_Rdex::processControlMessage(CHAR *aData)
{
    int retVal = ClientErrors::Abnormal;
    CtiPointDataMsg     *pData;
    RdexInterface_t  *data = (RdexInterface_t*)aData;
    string           translationName;
    int                 quality =NormalQuality;
    FLOAT               value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    CHAR           action[60];
    string      desc;

    // accept nothing until we are registered
    if (isRegistered())
    {
        // convert to our name
        translationName = string (data->control.translation);

        // see if the point exists
        flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

        if ((flag == true) && (point.getPointType() == StatusPointType) && (point.isControllable()))
        {
            ULONG controlState;
            controlState = ForeignToYukonStatus (data->control.value);

            if (controlState == Rdex_Invalid)
            {
                CTILOG_ERROR(dout, "Control point "<< translationName <<
                        " from "<< getLayer()->getName() <<" has an invalid control state "<< ntohl(data->control.value));

                CHAR state[20];
                desc = getInterfaceName() + string (" control point received with an invalid state ") + string (itoa (ntohl(data->control.value),state,10));
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point.getPointID());
                logEvent (desc,string (action));
                retVal = ClientErrors::Abnormal;
            }
            else if ((controlState == STATE_OPENED || controlState == STATE_CLOSED))
            {
                // build the command message and send the control
                CtiCommandMsg *cmdMsg;
                cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
                cmdMsg->insert(point.getPointID());  // point for control
                cmdMsg->insert(controlState);
                sendMessageToDispatch(cmdMsg);

                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    Cti::StreamBuffer logmsg;

                    logmsg <<" Control point "<< translationName;
                    if (controlState == STATE_OPENED)
                    {
                        logmsg <<" control: Open " ;
                    }
                    else
                    {
                        logmsg <<" control: Closed ";
                    }
                    logmsg <<" from " << getLayer()->getName() << " and processed for point " << point.getPointID();

                    CTILOG_DEBUG(dout, logmsg);
                 }
                retVal = ClientErrors::None;
            }
        }
        else
        {
            if (flag == false)
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Translation for control point " <<  translationName <<
                            " from " << getLayer()->getName() << " was not found");

                    desc = getInterfaceName() + string (" control point is not listed in the translation table");
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }

            }
            else if (!point.isControllable())
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Control point "<< translationName <<
                            " received from " << getLayer()->getName() <<
                            " was not configured receive for control for point " << point.getPointID());

                    desc = getInterfaceName() + string (" control point is not configured to receive controls");
                    _snprintf(action,60,"%s for pointID %d",
                              translationName.c_str(),
                              point.getPointID());
                    logEvent (desc,string (action));
                }
            }
            else
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Control point " << translationName <<
                            " received from " << getLayer()->getName() <<
                            " was mapped to non-control point " << point.getPointID());

                    CHAR pointID[20];
                    desc = getInterfaceName() + string (" control point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
            }
            retVal = ClientErrors::Abnormal;
        }
    }

    return retVal;
}

ULONG CtiFDR_Rdex::ForeignToYukonQuality (ULONG aQuality)
{
    ULONG Quality = QuestionableQuality;
    ULONG HostQuality;

    HostQuality = ntohl (aQuality);

    // test for the various Rdex Qualities and translate to CTI
    if (HostQuality & RDEX_QUESTIONABLE)
        Quality = QuestionableQuality;
    else if (HostQuality & RDEX_MANUAL)
        Quality = ManualQuality;
    else if (HostQuality & RDEX_NORMAL)
        Quality = NormalQuality;

    return(Quality);
}

ULONG CtiFDR_Rdex::YukonToForeignQuality (ULONG aQuality)
{
   ULONG Quality = RDEX_QUESTIONABLE;

   // Test for the various CTI Qualities and translate to Valmet
   if (aQuality == ManualQuality)
      Quality = RDEX_MANUAL;
   else if (aQuality == NormalQuality)
      Quality = RDEX_NORMAL;

   return(htonl (Quality));
}

// Convert Valmet status to CTI Status
ULONG CtiFDR_Rdex::ForeignToYukonStatus (ULONG aStatus)
{
    ULONG tmpstatus=Rdex_Invalid;

    switch (ntohl (aStatus))
    {
        case Rdex_Open:
            tmpstatus = STATE_OPENED;
            break;
        case Rdex_Closed:
            tmpstatus = STATE_CLOSED;
            break;
    }
    return(tmpstatus);
}


ULONG CtiFDR_Rdex::YukonToForeignStatus (ULONG aStatus)
{
   ULONG tmpstatus=Rdex_Open;

   switch (aStatus)
   {
      case STATE_OPENED:
         tmpstatus = Rdex_Open;
         break;
      case STATE_CLOSED:
         tmpstatus = Rdex_Closed;
         break;
   }
   return(htonl (tmpstatus));
}


CtiTime CtiFDR_Rdex::ForeignToYukonTime (PCHAR aTime)
{
    struct tm ts;

    if (sscanf (aTime,
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        return(CtiTime(PASTDATE));
    }

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

    // if CtiTime can't make a time ???
    if ((returnTime.seconds() > (CtiTime::now().seconds() + getTimestampReasonabilityWindow())) || (!returnTime.isValid()))
    {
        return(CtiTime(PASTDATE));
    }


    return returnTime;
}

string CtiFDR_Rdex::YukonToForeignTime (CtiTime aTimeStamp)
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

   // Place it into the Valmet structure */
   _snprintf (tmp,
             30,
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
        rdexInterface = new CtiFDR_Rdex();

        // now start it up
        return rdexInterface->run();
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

        rdexInterface->stop();
        delete rdexInterface;
        rdexInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif



