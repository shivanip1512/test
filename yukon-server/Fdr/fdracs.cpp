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

// this class header
#include "fdracs.h"
#include "utility.h"

using namespace std;

/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDR_ACS * acsInterface;

const CHAR * CtiFDR_ACS::KEY_LISTEN_PORT_NUMBER = "FDR_ACS_PORT_NUMBER";
const CHAR * CtiFDR_ACS::KEY_TIMESTAMP_WINDOW = "FDR_ACS_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_ACS::KEY_DB_RELOAD_RATE = "FDR_ACS_DB_RELOAD_RATE";
const CHAR * CtiFDR_ACS::KEY_QUEUE_FLUSH_RATE = "FDR_ACS_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_ACS::KEY_OUTBOUND_SEND_RATE = "FDR_ACS_SEND_RATE";
const CHAR * CtiFDR_ACS::KEY_OUTBOUND_SEND_INTERVAL = "FDR_ACS_SEND_INTERVAL";
const CHAR * CtiFDR_ACS::KEY_TIMESYNC_VARIATION = "FDR_ACS_MAXIMUM_TIMESYNC_VARIATION";
const CHAR * CtiFDR_ACS::KEY_TIMESYNC_UPDATE = "FDR_ACS_RESET_PC_TIME_ON_TIMESYNC";
const CHAR * CtiFDR_ACS::KEY_POINT_TIME_VARIATION = "FDR_ACS_POINT_TIME_VARIATION";
const CHAR * CtiFDR_ACS::KEY_LINK_TIMEOUT = "FDR_ACS_LINK_TIMEOUT_SECONDS";

// Constructors, Destructor, and Operators
CtiFDR_ACS::CtiFDR_ACS()
: CtiFDRSingleSocket(string("ACS"))
{}


CtiFDR_ACS::~CtiFDR_ACS()
{}

/*************************************************
* Function Name: CtiFDR_ACS::config()
*
* Description: loads cparm config values
*
**************************************************
*/
bool CtiFDR_ACS::readConfig()
{
    bool successful = Inherited::readConfig();
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setPortNumber (ACS_PORTNUMBER);
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

    tempStr = getCparmValueAsString(KEY_TIMESYNC_VARIATION);
    if (tempStr.length() > 0)
    {
        setTimeSyncVariation (atoi(tempStr.c_str()));
        if (getTimeSyncVariation() < 5)
        {
            CTILOG_ERROR(dout, "ACS max time sync variation of "<< getTimeSyncVariation() <<" second(s) is invalid, defaulting to 5 seconds");

            // default to 5 seconds
            setTimeSyncVariation(5);
        }
    }
    else
    {
        // default to 30 seconds
        setTimeSyncVariation(30);
    }

    tempStr = getCparmValueAsString(KEY_POINT_TIME_VARIATION);
    if (tempStr.length() > 0)
    {
        setPointTimeVariation (atoi(tempStr.c_str()));
    }
    else
    {
        // default to 0
        setPointTimeVariation (0);
    }

    // default to true
    setUpdatePCTimeFlag (true);
    tempStr = getCparmValueAsString(KEY_TIMESYNC_UPDATE);
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"false"))
        {
            setUpdatePCTimeFlag (false);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;
        loglist.add("ACS port number")              << getPortNumber();
        loglist.add("ACS timestamp window")         << getTimestampReasonabilityWindow();
        loglist.add("ACS DB reload rate")           << getReloadRate();
        loglist.add("ACS queue flush rate")         << getQueueFlushRate();
        loglist.add("ACS send rate")                << getOutboundSendRate();
        loglist.add("ACS send interval")            << getOutboundSendInterval();
        loglist.add("ACS max time sync variation")  << getTimeSyncVariation();
        loglist.add("ACS point time variation")     << getPointTimeVariation();

        if( shouldUpdatePCTime() )
        {
            loglist <<"ACS time sync will reset PC clock";
        }
        else
        {
            loglist <<"ACS time sync will not reset PC clock";
        }

        CTILOG_DEBUG(dout, loglist);
    }
    return successful;
}


bool CtiFDR_ACS::translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aDestinationIndex)
{
    bool                successful(false);
    string           tempString1;
    string           tempString2;
    string           translationName;
    bool                foundPoint = false;
    char                wb[20];
    const string translation = translationPoint->getDestinationList()[aDestinationIndex].getTranslation();

    try
    {
        boost::char_separator<char> sep1(";");
        Boost_char_tokenizer nextTranslate(translation, sep1);
        Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin();

        if ( tok_iter != nextTranslate.end() )
        {
            tempString1 = *tok_iter; tok_iter++;
            boost::char_separator<char> sep2(":");
            Boost_char_tokenizer nextTempToken(tempString1, sep2);
            Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();


            if( tok_iter1 != nextTempToken.end() )
            {
                tok_iter1++;
                if( tok_iter1 != nextTempToken.end() )
                {
                    tempString2 = *tok_iter1;
                }else
                {
                    tempString2 = "";
                }

                // now we have a category with a :
                if ( !tempString2.empty() )
                {
                    // put category in final name
                    translationName= "C";
                    translationName += tempString2[0];

                    // next token is the remote number
                    if ( tok_iter != nextTranslate.end())
                    {
                        tempString1 = *tok_iter; tok_iter++;
                        Boost_char_tokenizer nextTempToken(tempString1, sep2);
                        Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

                        if( tok_iter1 != nextTempToken.end() )
                        {
                            tok_iter1++;
                            if( tok_iter1 != nextTempToken.end() )
                            {
                                tempString2 = *tok_iter1;
                            }else
                            {
                                tempString2 = "";
                            }

                            // now we have a category with a :
                            if ( !tempString2.empty() )
                            {
                                // put category in final name
                                translationName= "R"+tempString2 + translationName;

                                // next token is the point number
                                if ( tok_iter != nextTranslate.end())
                                {
                                    tempString1 = *tok_iter; tok_iter++;
                                    Boost_char_tokenizer nextTempToken(tempString1, sep2);
                                    Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

                                    if( tok_iter1 != nextTempToken.end() )
                                    {
                                        tok_iter1++;
                                        if( tok_iter1 != nextTempToken.end() )
                                        {
                                            tempString2 = *tok_iter1;
                                        }else
                                        {
                                            tempString2 = "";
                                        }

                                        // now we have a category with a :
                                        if ( !tempString2.empty() )
                                        {
                                            // put category in final name
                                            translationName= "T" + string (itoa(translationPoint->getPointType(),wb,10)) + translationName + "P"+ tempString2;

                                            // add this point ID and the translated ID
                                            translationPoint->getDestinationList()[aDestinationIndex].setTranslation (translationName);
                                            successful = true;

                                        }   // point id invalid
                                    }
                                }   // third token invalid

                            }   //remote number invalid
                        }
                    }   // second token invalid

                }   // category invalid
            }
        }   // first token invalid
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

CHAR *CtiFDR_ACS::buildForeignSystemMsg (CtiFDRPoint &aPoint )
{
    CHAR *acs=NULL;
    bool                 retVal = true;

    /**************************
    * we allocate a acs message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    acs = new CHAR[sizeof (ACSInterface_t)];
    ACSInterface_t *ptr = (ACSInterface_t *)acs;

    // make sure we have all the pieces
    if (acs != NULL)
    {
        // set the timestamp, everything else is based on type of message
        strcpy (ptr->TimeStamp,  YukonToForeignTime (aPoint.getLastTimeStamp()).c_str());

        switch (aPoint.getPointType())
        {
            case AnalogPointType:
            case CalculatedPointType:
            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
                {
                    ptr->Function = htons (SINGLE_SOCKET_VALUE);
                    YukonToForeignId (aPoint.getTranslateName(string(FDR_ACS)),
                                      ptr->Value.RemoteNumber,
                                      ptr->Value.CategoryCode,
                                      ptr->Value.PointNumber);
                    ptr->Value.Quality = YukonToForeignQuality (aPoint.getQuality());
                    ptr->Value.LongValue = htonieeef (aPoint.getValue());

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Analog/Calculated point "<< aPoint.getPointID() <<
                                " queued as Remote: "<< ntohs(ptr->Value.RemoteNumber) <<
                                " Category: "<< ptr->Value.CategoryCode <<
                                " Point: "<< ntohs(ptr->Value.PointNumber) <<
                                " value "<< aPoint.getValue() << " to "<< getInterfaceName());
                    }

                    break;
                }

            case CalculatedStatusPointType:
            case StatusPointType:
                {
                    /****************************
                    * status point is both status and or control so we must check
                    * here if we are supposed to be sending a control
                    *****************************
                    */
                    if (aPoint.isControllable())
                    {
                        // we are doing control
                        ptr->Function = htons (SINGLE_SOCKET_CONTROL);
                        YukonToForeignId (aPoint.getTranslateName(string(FDR_ACS)),
                                          ptr->Control.RemoteNumber,
                                          ptr->Control.CategoryCode,
                                          ptr->Control.PointNumber);

                        // check for validity of the status, we only have open or closed for ACS
                        if ((aPoint.getValue() != STATE_OPENED) && (aPoint.getValue() != STATE_CLOSED))
                        {
                            delete [] acs;
                            acs = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CTILOG_DEBUG(dout, "Control Point "<< aPoint.getPointID() <<" State "<< aPoint.getValue() <<" is invalid for interface "<< getInterfaceName());
                            }
                        }
                        else
                        {
                            ptr->Control.Value = YukonToForeignStatus (aPoint.getValue());

                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                Cti::StreamBuffer logmsg;
                                logmsg <<"Control point "<< aPoint.getPointID()
                                       <<" queued as Remote: "<< ntohs (ptr->Status.RemoteNumber)
                                       <<" Category: "<< ptr->Status.CategoryCode
                                       <<" Point: "<< ntohs(ptr->Status.PointNumber);

                                if(aPoint.getValue() == STATE_OPENED)
                                {
                                    logmsg <<" state of Open "<< ptr->Control.Value <<" "<< aPoint.getValue();
                                }
                                else
                                {
                                    logmsg <<" state of Close "<< ptr->Control.Value <<" "<< aPoint.getValue();
                                }

                                logmsg <<" to "<< getInterfaceName();

                                CTILOG_DEBUG(dout, logmsg);
                            }
                        }
                    }
                    else
                    {
                        ptr->Function = htons (SINGLE_SOCKET_STATUS);
                        // everything for control and status is the same except function
                        YukonToForeignId (aPoint.getTranslateName(string(FDR_ACS)),
                                          ptr->Status.RemoteNumber,
                                          ptr->Status.CategoryCode,
                                          ptr->Status.PointNumber);
                        ptr->Status.Quality = YukonToForeignQuality (aPoint.getQuality());

                        // check for validity of the status, we only have open or closed for ACS
                        if ((aPoint.getValue() != STATE_OPENED) && (aPoint.getValue() != STATE_CLOSED))
                        {
                            delete [] acs;
                            acs = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CTILOG_DEBUG(dout, "Point "<< aPoint.getPointID() <<" State "<< aPoint.getValue() <<" is invalid for interface "<< getInterfaceName());
                            }
                        }
                        else
                        {
                            ptr->Status.Value = YukonToForeignStatus (aPoint.getValue());

                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                Cti::StreamBuffer logmsg;

                                logmsg <<" Status point "<< aPoint.getPointID()
                                       <<" queued as Remote: "<< ntohs (ptr->Status.RemoteNumber)
                                       <<" Category: "<< ptr->Status.CategoryCode
                                       <<" Point: "<< ntohs(ptr->Status.PointNumber);

                                if (aPoint.getValue() == STATE_OPENED)
                                {
                                    logmsg <<" state of Open ";
                                }
                                else
                                {
                                    logmsg <<" state of Close ";
                                }

                                logmsg <<" to "<< getInterfaceName();

                                CTILOG_DEBUG(dout, logmsg);
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
    }
    return acs;
}


CHAR *CtiFDR_ACS::buildForeignSystemHeartbeatMsg ()
{
    CHAR *acs=NULL;

    /**************************
    * we allocate a acs message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    acs = new CHAR[sizeof (ACSInterface_t)];
    ACSInterface_t *ptr = (ACSInterface_t *)acs;
    bool retVal = true;

    if (acs != NULL)
    {
        ptr->Function = htons (SINGLE_SOCKET_NULL);
        strcpy (ptr->TimeStamp, YukonToForeignTime (CtiTime()).c_str());
    }
    return acs;
}

int CtiFDR_ACS::getMessageSize(CHAR *aBuffer)
{
    return sizeof (ACSInterface_t);
}

string CtiFDR_ACS::decodeClientName(CHAR * aBuffer)
{
    return getInterfaceName();
}


int CtiFDR_ACS::processValueMessage(CHAR *aData)
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    string           translationName;
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    string           desc;
    CHAR               action[60];
    CHAR                wb[20];

    // convert to our name
    translationName = ForeignToYukonId (data->Value.RemoteNumber,data->Value.CategoryCode,data->Value.PointNumber);
    translationName = "T" + string (itoa(AnalogPointType,wb,10)) + translationName;

    // see if the point exists
     flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))

    {
        // assign last stuff
        quality = ForeignToYukonQuality (data->Value.Quality);
        value = ntohieeef (data->Value.LongValue);

        value *= point.getMultiplier();
        value += point.getOffset();

        timestamp = ForeignToYukonTime (data->TimeStamp);
        if (timestamp == PASTDATE)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, getInterfaceName() <<" analog value received with an invalid timestamp "<< data->TimeStamp);
            }

            desc = getInterfaceName() + string (" analog point received with an invalid timestamp ") + string (data->TimeStamp);
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d",
                      ntohs(data->Value.RemoteNumber),
                      data->Value.CategoryCode,
                      ntohs(data->Value.PointNumber),
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
                CTILOG_DEBUG(dout, "Analog point Remote: "<< ntohs(data->Value.RemoteNumber) <<
                        " Category: "<< data->Value.CategoryCode <<
                        " Point: "<< ntohs(data->Value.PointNumber) <<
                        " value "<< value <<" from "<< getInterfaceName() <<" assigned to point "<< point.getPointID());
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for analog point "<<
                        " Remote: "<< ntohs(data->Value.RemoteNumber) <<
                        " Category: "<< data->Value.CategoryCode <<
                        " Point: "<< ntohs(data->Value.PointNumber) <<
                        " from "<< getInterfaceName() <<" was not found");

                desc = getInterfaceName() + string (" analog point is not listed in the translation table");
                _snprintf(action,60,"Remote:%d Category:%c Point:%d",
                          ntohs(data->Value.RemoteNumber),
                          data->Value.CategoryCode,
                          ntohs(data->Value.PointNumber));
                logEvent (desc,string (action));
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Analog point "<<
                        " Remote: "<< ntohs(data->Value.RemoteNumber) <<
                        " Category: "<< data->Value.CategoryCode <<
                        " Point: "<< ntohs(data->Value.PointNumber) <<
                        " from "<< getInterfaceName() <<" was mapped incorrectly to non-analog point "<< point.getPointID());

            CHAR pointID[20];
            desc = getInterfaceName() + string (" analog point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d",
                      ntohs(data->Value.RemoteNumber),
                      data->Value.CategoryCode,
                      ntohs(data->Value.PointNumber));
            logEvent (desc,string (action));
        }
        retVal = ClientErrors::Abnormal;
    }

    return retVal;
}

int CtiFDR_ACS::processStatusMessage(CHAR *aData)
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    string           translationName;
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;

    string           desc;
    CHAR                action[60];
    CHAR                wb[20];


    // convert to our name
    translationName = ForeignToYukonId (data->Status.RemoteNumber,data->Status.CategoryCode,data->Status.PointNumber);
    translationName = "T" + string (itoa(StatusPointType,wb,10)) + translationName;

    // see if the point exists
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) && (point.getPointType() == StatusPointType) && (!point.isControllable()))
    {
        // assign last stuff
        quality = ForeignToYukonQuality (data->Status.Quality);

        value = ForeignToYukonStatus (data->Status.Value);

        if (value == STATE_INVALID)
        {
            CTILOG_ERROR(dout, "Status point "<<
                    " Remote: "<< ntohs(data->Status.RemoteNumber) <<
                    " Category: "<< data->Status.CategoryCode <<
                    " Point: "<< ntohs(data->Status.PointNumber) <<
                    " from "<< getInterfaceName() <<" has an invalid status code "<< ntohs(data->Status.Value));

            CHAR state[20];
            desc = getInterfaceName() + string (" status point received with an invalid state ") + string (itoa (ntohs(data->Status.Value),state,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d",
                      ntohs(data->Status.RemoteNumber),
                      data->Status.CategoryCode,
                      ntohs(data->Status.PointNumber),
                      point.getPointID());
            logEvent (desc,string (action));
            retVal = ClientErrors::Abnormal;
        }
        else
        {
            timestamp = ForeignToYukonTime (data->TimeStamp);
            if (timestamp == PASTDATE)
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, getInterfaceName() <<" status value received with an invalid timestamp "<< data->TimeStamp);
                }

                desc = getInterfaceName() + string (" status point received with an invalid timestamp ") + string (data->TimeStamp);
                _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d",
                          ntohs(data->Status.RemoteNumber),
                          data->Status.CategoryCode,
                          ntohs(data->Status.PointNumber),
                          point.getPointID());
                logEvent (desc,string (action));
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
                    logmsg <<"Status point Remote: "<< ntohs(data->Status.RemoteNumber)
                           <<" Category: "<< data->Status.CategoryCode
                           <<" Point: "<< ntohs(data->Status.PointNumber);

                    if (value == STATE_OPENED)
                    {
                        logmsg <<" New state: Open ";
                    }
                    else
                    {
                        logmsg <<" New state: Closed ";
                    }

                    logmsg <<" from "<< getInterfaceName() <<" assigned to point "<< point.getPointID();

                    CTILOG_DEBUG(dout, logmsg);
                }
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for status point "<<
                        " Remote: "<< ntohs(data->Status.RemoteNumber) <<
                        " Category: "<< data->Status.CategoryCode <<
                        " Point: "<< ntohs(data->Status.PointNumber) <<
                        " from "<< getInterfaceName() <<" was not found");

                desc = getInterfaceName() + string (" status point is not listed in the translation table");
                _snprintf(action,60,"Remote:%d Category:%c Point:%d",
                          ntohs(data->Status.RemoteNumber),
                          data->Status.CategoryCode,
                          ntohs(data->Status.PointNumber));
                logEvent (desc,string (action));
            }
        }
        else if ( point.isControllable() )
        {
            CTILOG_ERROR(dout, "Status point "<<
                    " Remote: " << ntohs(data->Status.RemoteNumber) <<
                    " Category: " << data->Status.CategoryCode <<
                    " Point: " << ntohs(data->Status.PointNumber) <<
                    " from " << getInterfaceName() <<
                    " was configured receive for control for point " << point.getPointID());

            desc = getInterfaceName() + string (" control point is configured to receive controls");
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d",
                      ntohs(data->Status.RemoteNumber),
                      data->Status.CategoryCode,
                      ntohs(data->Status.PointNumber),
                      point.getPointID());
            logEvent (desc,string (action));
        }
        else
        {
            CTILOG_ERROR(dout, "Status point "<<
                    " Remote: "<< ntohs(data->Status.RemoteNumber) <<
                    " Category: "<< data->Status.CategoryCode <<
                    " Point: "<< ntohs(data->Status.PointNumber) <<
                    " from "<< getInterfaceName() <<" was mapped incorrectly to non-status point "<< point.getPointID());

            CHAR pointID[20];
            desc = getInterfaceName() + string (" status point is incorrectly mapped to point ") + string(ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d",
                      ntohs(data->Status.RemoteNumber),
                      data->Status.CategoryCode,
                      ntohs(data->Status.PointNumber));
            logEvent (desc,string (action));
        }
        retVal = ClientErrors::Abnormal;
    }

    return retVal;
}

int CtiFDR_ACS::processControlMessage(CHAR *aData)
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    string           translationName;
    int                 quality =NormalQuality;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    string           desc;
    CHAR                action[60];
    CHAR                wb[20];

    // convert to our name
    translationName = ForeignToYukonId (data->Control.RemoteNumber,data->Control.CategoryCode,data->Control.PointNumber);
    translationName = "T" + string (itoa(StatusPointType,wb,10)) + translationName;

    // see if the point exists
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) && (point.getPointType() == StatusPointType) && (point.isControllable()))
    {
        int controlState=STATE_INVALID;

        controlState = ForeignToYukonStatus (data->Control.Value);

        // make sure the value is valid
        if (controlState == STATE_INVALID)
        {
            CTILOG_ERROR(dout, "Control point "<<
                    " Remote: "<< ntohs(data->Status.RemoteNumber) <<
                    " Category: "<< data->Status.CategoryCode <<
                    " Point: "<< ntohs(data->Status.PointNumber) <<
                    " from " << getInterfaceName() <<" has an invalid control state "<< ntohs(data->Control.Value));

            CHAR state[20];
            desc = getInterfaceName() + string (" control point received with an invalid state ") + string (itoa (ntohs(data->Control.Value),state,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d",
                      ntohs(data->Control.RemoteNumber),
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber),
                      point.getPointID());
            logEvent (desc,string (action));
            retVal = ClientErrors::Abnormal;
        }
        else if (controlState == STATE_OPENED || controlState == STATE_CLOSED)
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

                logmsg <<"Control point Remote: " << ntohs(data->Control.RemoteNumber);
                logmsg <<" Category: " << data->Control.CategoryCode;
                logmsg <<" Point: " << ntohs(data->Control.PointNumber);

                if (controlState == STATE_OPENED)
                {
                    logmsg <<" Control: Open ";
                }
                else
                {
                    logmsg <<" Control: Closed ";
                }

                logmsg <<" from "<< getInterfaceName() <<" and processed for point "<< point.getPointID();

                CTILOG_DEBUG(dout, logmsg);
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                // FIXME: DEBUG or ERROR?
                CTILOG_DEBUG(dout, "Translation for control point "<<
                        " Remote: " << ntohs(data->Control.RemoteNumber) <<
                        " Category: " << data->Control.CategoryCode <<
                        " Point: " << ntohs(data->Control.PointNumber) <<
                        " from " << getInterfaceName() <<" was not found");

                desc = getInterfaceName() + string (" control point is not listed in the translation table");
                _snprintf(action,60,"Remote:%d Category:%c Point:%d",
                          ntohs(data->Control.RemoteNumber),
                          data->Control.CategoryCode,
                          ntohs(data->Control.PointNumber));
                logEvent (desc,string (action));
            }

        }
        else if (!point.isControllable())
        {
            CTILOG_ERROR(dout, "Control point "<<
                    " Remote: " << ntohs(data->Control.RemoteNumber) <<
                    " Category: " << data->Control.CategoryCode <<
                    " Point: " << ntohs(data->Control.PointNumber) <<
                    " received from " << getInterfaceName() <<
                    " was not configured receive for control for point " << point.getPointID());

            desc = getInterfaceName() + string (" control point is not configured to receive controls");
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d",
                      ntohs(data->Control.RemoteNumber),
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber),
                      point.getPointID());
            logEvent (desc,string (action));
        }
        else
        {
            CTILOG_ERROR(dout, "Control point "<<
                    " Remote: "<< ntohs(data->Control.RemoteNumber) <<
                    " Category: "<< data->Control.CategoryCode <<
                    " Point: "<< ntohs(data->Control.PointNumber) <<
                    " received from "<< getInterfaceName() <<
                    " was mapped to non-control point "<< point.getPointID());

            CHAR pointID[20];
            desc = getInterfaceName() + string (" control point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d",
                      ntohs(data->Control.RemoteNumber),
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber));
            logEvent (desc,string (action));
        }
        retVal = ClientErrors::Abnormal;
    }

    return retVal;
}


int CtiFDR_ACS::processTimeSyncMessage(CHAR *aData)
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    CtiTime              timestamp;
    string           desc;
    string               action;

    timestamp = ForeignToYukonTime (data->TimeStamp,true);
    if (timestamp == PASTDATE)
    {
        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, getInterfaceName() <<" time sync request was invalid "<< data->TimeStamp);
        }

        desc = getInterfaceName() + string (" time sync request was invalid ") + string (data->TimeStamp);
        logEvent (desc,action,true);
        retVal = ClientErrors::Abnormal;
    }
    else
    {
        CtiTime now;
        // check if the stamp is inside the window
        if (timestamp.seconds() > (now.seconds()-getTimeSyncVariation()) &&
            timestamp.seconds() < (now.seconds()+getTimeSyncVariation()))
        {
            retVal = ClientErrors::None;
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
                        timeNow.QuadPart += ( long long)((timestamp.seconds() - now.seconds()) * 10000000);
                    else
                        timeNow.QuadPart -= ( long long)((now.seconds() - timestamp.seconds()) * 10000000);

                    fileTime.dwLowDateTime = timeNow.LowPart;
                    fileTime.dwHighDateTime = timeNow.HighPart;

                    if (FileTimeToSystemTime (&fileTime, &sysTime))
                    {
                        SetSystemTime (&sysTime);
                        desc = getInterfaceName() + "'s request to change PC time to ";
                        desc += timestamp.asString() + " was processed";
                        action = "PC time reset to" + timestamp.asString();
                        logEvent (desc,action,true);

                        CTILOG_INFO(dout, getInterfaceName() <<"'s request to change PC time to "<< timestamp <<" was processed");
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Unable to process time change from "<< getInterfaceName());

                        desc = getInterfaceName() + "'s request to change PC time to ";
                        desc += timestamp.asString() + " failed";
                        action = string ("System time update API failed");
                        logEvent (desc,action,true);
                        retVal = ClientErrors::Abnormal;
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Unable to process time change from "<< getInterfaceName());

                    desc = getInterfaceName() + "'s request to change PC time to ";
                    desc += timestamp.asString() + " failed";
                    action = string ("System time update API failed");
                    logEvent (desc,action,true);
                    retVal = ClientErrors::Abnormal;
                }
            }
            else
            {
                CTILOG_WARN(dout, "Time change requested from "<< getInterfaceName() <<" of "<< timestamp <<" is outside standard +-30 minutes");

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



string CtiFDR_ACS::ForeignToYukonId (USHORT remote, CHAR category, USHORT point)
{
    CHAR tmpName[STANDNAMLEN+1];

    // put the remote and point number into our point
    _snprintf (tmpName, 21, "R%dC%cP%d",ntohs(remote),category,ntohs(point));

    // set point name to all blanks
    return string (tmpName);
}

int CtiFDR_ACS::YukonToForeignId (string aPointName, USHORT &aRemoteNumber, CHAR &aCategory, USHORT &aPointNumber)
{
    USHORT tmp_remote, tmp_point, tmp_type;
    CHAR tmp_category;
    CHAR pointName[100];

    // put this in a characater buffer
    strcpy (pointName, aPointName.c_str());

    // Read the values out of point name
    if (sscanf (pointName, "T%hdR%hdC%cP%hd", &tmp_type, &tmp_remote, &tmp_category, &tmp_point) != 4)
    {
        return ClientErrors::Abnormal;
    }

    // put these into our values
    aRemoteNumber = htons(tmp_remote);
    aPointNumber = htons(tmp_point);
    aCategory = tmp_category;
    return ClientErrors::None;
}


USHORT CtiFDR_ACS::ForeignToYukonQuality (USHORT aQuality)
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

USHORT CtiFDR_ACS::YukonToForeignQuality (USHORT aQuality)
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
int CtiFDR_ACS::ForeignToYukonStatus (USHORT aStatus)
{
    int tmpstatus=STATE_INVALID;

    switch (ntohs (aStatus))
    {
        case ACS_Open:
            tmpstatus = STATE_OPENED;
            break;
        case ACS_Closed:
            tmpstatus = STATE_CLOSED;
            break;
    }
    return(tmpstatus);
}


USHORT CtiFDR_ACS::YukonToForeignStatus (int aStatus)
{
    USHORT tmpstatus=ACS_Invalid;

    switch (aStatus)
    {
        case STATE_OPENED:
            tmpstatus = ACS_Open;
            break;
        case STATE_CLOSED:
            tmpstatus = ACS_Closed;
            break;
    }
    return(htons (tmpstatus));
}


CtiTime CtiFDR_ACS::ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag)
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
            // if CtiTime can't make a time or we are outside the window
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

string CtiFDR_ACS::YukonToForeignTime (CtiTime aTimeStamp)
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
        acsInterface = new CtiFDR_ACS();
        acsInterface->init();
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


