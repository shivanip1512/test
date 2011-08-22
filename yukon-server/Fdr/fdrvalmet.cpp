#include "precompiled.h"


#include <iostream>

  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

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
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"
#include "fdrsocketlayer.h"

// this class header
#include "fdrvalmet.h"

using std::string;
using std::endl;

/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDR_Valmet * valmetInterface;

const CHAR * CtiFDR_Valmet::KEY_LISTEN_PORT_NUMBER = "FDR_VALMET_PORT_NUMBER";
const CHAR * CtiFDR_Valmet::KEY_TIMESTAMP_WINDOW = "FDR_VALMET_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_Valmet::KEY_DB_RELOAD_RATE = "FDR_VALMET_DB_RELOAD_RATE";
const CHAR * CtiFDR_Valmet::KEY_QUEUE_FLUSH_RATE = "FDR_VALMET_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Valmet::KEY_DEBUG_MODE = "FDR_VALMET_DEBUG_MODE";
const CHAR * CtiFDR_Valmet::KEY_OUTBOUND_SEND_RATE = "FDR_VALMET_SEND_RATE";
const CHAR * CtiFDR_Valmet::KEY_OUTBOUND_SEND_INTERVAL = "FDR_VALMET_SEND_INTERVAL";
const CHAR * CtiFDR_Valmet::KEY_TIMESYNC_VARIATION = "FDR_VALMET_MAXIMUM_TIMESYNC_VARIATION";
const CHAR * CtiFDR_Valmet::KEY_TIMESYNC_UPDATE = "FDR_VALMET_RESET_PC_TIME_ON_TIMESYNC";
const CHAR * CtiFDR_Valmet::KEY_LINK_TIMEOUT = "FDR_VALMET_LINK_TIMEOUT_SECONDS";
const CHAR * CtiFDR_Valmet::KEY_SCAN_DEVICE_POINTNAME = "FDR_VALMET_SCAN_DEVICE_POINTNAME";
const CHAR * CtiFDR_Valmet::KEY_SEND_ALL_POINTS_POINTNAME = "FDR_VALMET_SEND_ALL_POINTS_POINTNAME";

// Constructors, Destructor, and Operators
CtiFDR_Valmet::CtiFDR_Valmet()
: CtiFDRSingleSocket(string(FDR_VALMET))
{
    init();
}


CtiFDR_Valmet::~CtiFDR_Valmet()
{
}

/*************************************************
* Function Name: CtiFDR_Valmet::config()
*
* Description: loads cparm config values
*
**************************************************
*/
int CtiFDR_Valmet::readConfig()
{
    int         successful = TRUE;
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setPortNumber (VALMET_PORTNUMBER);
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
        // default to 5 seconds, this could be a lot of points
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Valmet max time sync variation of " << getTimeSyncVariation() << " second(s) is invalid, defaulting to 5 seconds" << endl;
            }
            // default to 5 seconds
            setTimeSyncVariation(5);
        }
    }
    else
    {
        // default to 5 seconds
        setTimeSyncVariation(30);
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


    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);

    scanDevicePointName = gConfigParms.getValueAsString(KEY_SCAN_DEVICE_POINTNAME, "DEVICE_SCAN");
    sendAllPointsPointName = gConfigParms.getValueAsString(KEY_SEND_ALL_POINTS_POINTNAME, "SEND_ALL_POINTS");

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Valmet port number " << getPortNumber() << endl;
        dout << CtiTime() << " Valmet timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << CtiTime() << " Valmet db reload rate " << getReloadRate() << endl;
        dout << CtiTime() << " Valmet queue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << CtiTime() << " Valmet send rate " << getOutboundSendRate() << endl;
        dout << CtiTime() << " Valmet send interval " << getOutboundSendInterval() << " second(s) " << endl;
        dout << CtiTime() << " Valmet max time sync variation " << getTimeSyncVariation() << " second(s) " << endl;
        dout << CtiTime() << " Valmet link timeout " << getLinkTimeout() << " second(s) " << endl;        
        dout << CtiTime() << " Valmet force scan pointname " << sendAllPointsPointName << endl;
        dout << CtiTime() << " Valmet scan device compare string " << scanDevicePointName << endl;


        if (shouldUpdatePCTime())
            dout << CtiTime() << " Valmet time sync will reset PC clock" << endl;
        else
            dout << CtiTime() << " Valmet time sync will not reset PC clock" << endl;

        if (isInterfaceInDebugMode())
            dout << CtiTime() << " Valmet running in debug mode " << endl;
        else
            dout << CtiTime() << " Valmet running in normal mode "<< endl;
    }

    return successful;
}

void CtiFDR_Valmet::signalReloadList()
{
    //The list is being reloaded. Clear our tracking map to be re-filled.
    nameToPointId.clear();
}

void CtiFDR_Valmet::signalPointRemoved(string &pointName)
{
    //This point is being removed, so lets remove it from our lists.
    nameToPointId.erase(pointName);
}

bool CtiFDR_Valmet::translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aDestinationIndex)
{
    bool             successful(false);
    string           tempString1;
    string           tempString2;
    string           translationName;
    bool             foundPoint = false;

    try
    {
        tempString2 = translationPoint->getDestinationList()[aDestinationIndex].getTranslationValue("Point");
        if ( !tempString2.empty() )
        {
            // put category in final name
            translationName= tempString2;

            //Insert the translation into our tracking list.
            string pointName = translationName;
            std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);
            nameToPointId.insert(std::pair<string,int>(pointName,translationPoint->getPointID()));

            // i'm updating my copied list
            translationPoint->getDestinationList()[aDestinationIndex].setTranslation (tempString2);
            successful = true;

        }   // invalid
    } // end try

    catch (RWExternalErr e )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  (...) "<< endl;
    }
    return successful;
}

CHAR *CtiFDR_Valmet::buildForeignSystemMsg ( CtiFDRPoint &aPoint )
{
    CHAR *valmet=NULL;
    bool                 retVal = true;
    if (aPoint.isCommStatus() && aPoint.getValue() != 0)
    {
        updatePointQualitiesOnDevice(NonUpdatedQuality, aPoint.getPaoID());
    }
   /**************************
    * we allocate a valmet message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    valmet = new CHAR[sizeof (ValmetInterface_t)];
    ValmetInterface_t *ptr = (ValmetInterface_t *)valmet;

    // make sure we have all the pieces
    if (valmet != NULL)
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
                            strcpy (ptr->Value.Name,aPoint.getTranslateName(string (FDR_VALMET)).c_str());
                    ptr->Value.Quality = YukonToForeignQuality (aPoint.getQuality());
                    ptr->Value.LongValue = htonieeef (aPoint.getValue());

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Analog/Calculated point " << aPoint.getPointID();
                        dout << " queued as " << aPoint.getTranslateName(string (FDR_VALMET));
                        dout << " value " << aPoint.getValue() << " with quality of " << ForeignQualityToString(ptr->Value.Quality);
                        dout << " to " << getInterfaceName() << endl;
                    }
                    break;
                }

            case CalculatedStatusPointType:
            case StatusPointType:
                {
                    if (aPoint.isControllable())
                    {
                        ptr->Function = htons (SINGLE_SOCKET_CONTROL);
                        strcpy (ptr->Control.Name,aPoint.getTranslateName(string (FDR_VALMET)).c_str());

                        // check for validity of the status, we only have open or closed in controls
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] valmet;
                            valmet = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                            }
                        }
                        else
                        {
                            ptr->Control.Value = YukonToForeignStatus (aPoint.getValue());

                             if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << CtiTime() << " Control point " << aPoint.getPointID();
                                 dout << " queued as " << aPoint.getTranslateName(string (FDR_VALMET));
                                 if (aPoint.getValue() == OPENED)
                                 {
                                     dout << " state of Open ";
                                 }
                                 else
                                 {
                                     dout << " state of Close ";
                                 }
                                 dout << "to " << getInterfaceName() << endl;;
                             }
                        }
                    }
                    else
                    {
                        ptr->Function = htons (SINGLE_SOCKET_STATUS);
                        strcpy (ptr->Value.Name,aPoint.getTranslateName(string (FDR_VALMET)).c_str());
                        ptr->Status.Quality = YukonToForeignQuality (aPoint.getQuality());

                        // check for validity of the status, we only have open or closed for Valmet
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] valmet;
                            valmet = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                            }
                        }
                        else
                        {
                            ptr->Status.Value = YukonToForeignStatus (aPoint.getValue());

                             if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << CtiTime() << " Status point " << aPoint.getPointID();
                                 dout << " queued as " << aPoint.getTranslateName(string (FDR_VALMET));
                                 if (aPoint.getValue() == OPENED)
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
    }
    return valmet;
}

CHAR *CtiFDR_Valmet::buildForeignSystemHeartbeatMsg ()
{
    CHAR *valmet=NULL;

    /**************************
    * we allocate a valmet message here and it will be deleted
    * inside of the write function on the connection
    ***************************
    */
    valmet = new CHAR[sizeof (ValmetInterface_t)];
    ValmetInterface_t *ptr = (ValmetInterface_t *)valmet;
    bool retVal = true;

    if (valmet != NULL)
    {
        ptr->Function = htons (SINGLE_SOCKET_NULL);
        strcpy (ptr->TimeStamp, YukonToForeignTime (CtiTime()).c_str());
    }
    return valmet;
}

int CtiFDR_Valmet::getMessageSize(CHAR *aBuffer)
{
    return sizeof (ValmetInterface_t);
}

string CtiFDR_Valmet::decodeClientName(CHAR * aBuffer)
{
    return getInterfaceName();
}


int CtiFDR_Valmet::processTimeSyncMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    CtiTime              timestamp;
    string           desc;
    string               action;

    timestamp = ForeignToYukonTime (data->TimeStamp,true);
    if (timestamp == PASTDATE)
    {
        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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

//                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getInterfaceName() << "'s request to change PC time to " << timestamp.asString() << " was processed" << endl;
                        }
                    }
                    else
                    {
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

int CtiFDR_Valmet::processValueMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    string           translationName;
    int                 quality;
    DOUBLE              value;
    CtiTime             timestamp;
    CtiFDRPointSPtr     point;
    bool   flag = true;
    string           desc;
    CHAR               action[60];

    // convert to our name
    translationName = string (data->Value.Name);

    //Find
    //flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    {
        CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
        string pointName = translationName;
        std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);

        std::map<string,int>::iterator iter = nameToPointId.find(pointName);
        if( iter != nameToPointId.end() ) {
            point = getReceiveFromList().getPointList()->findFDRPointID(iter->second);
            flag = true;
        }
        else
        {
            flag = false;
        }
    }



    if ((flag == true) &&
        ((point->getPointType() == AnalogPointType) ||
         (point->getPointType() == PulseAccumulatorPointType) ||
         (point->getPointType() == DemandAccumulatorPointType) ||
         (point->getPointType() == CalculatedPointType)))

    {
        // assign last stuff
        quality = ForeignToYukonQuality (data->Value.Quality);
        value = ntohieeef (data->Value.LongValue);
        value *= point->getMultiplier();
        value += point->getOffset();

        timestamp = ForeignToYukonTime (data->TimeStamp);
        if (timestamp == PASTDATE)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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
            

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Analog point " << translationName;
                dout << " value " << value << " from " << getInterfaceName() << " assigned to point " << point->getPointID() << endl;;
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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
        }
        else
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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

        }

        retVal = !NORMAL;
    }

    return retVal;
}


int CtiFDR_Valmet::processStatusMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    string           translationName;
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;
    CtiFDRPointSPtr         point;
    bool                 flag = true;

    string           desc;
    CHAR           action[60];

    translationName = string (data->Status.Name);

    // see if the point exists
    {
        CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
        string pointName = translationName;
        std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);

        std::map<string,int>::iterator iter = nameToPointId.find(pointName);
        if( iter != nameToPointId.end() ) {
            point = getReceiveFromList().getPointList()->findFDRPointID(iter->second);
            flag = true;
        }
        else
        {
            flag = false;
        }
    }

    if ((flag == true) && (point->getPointType() == StatusPointType))
    {
        // assign last stuff
        quality = ForeignToYukonQuality (data->Status.Quality);

        value = ForeignToYukonStatus (data->Status.Value);
        timestamp = ForeignToYukonTime (data->TimeStamp);
        if (timestamp == PASTDATE)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
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
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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
        }
        else
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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
        }
        retVal = !NORMAL;
    }

    return retVal;
}

int CtiFDR_Valmet::processScanMessage(CHAR *aData)
{
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    string  translationName(data->ForceScan.Name); 

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Received Scan Request for Translation Point: " << translationName << endl;
    }
    if (ciStringEqual(translationName, sendAllPointsPointName))
    {
        CtiFDRSingleSocket::getLayer()->sendAllPoints();
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Processed Scan Request for All Valmet Translation Points." << endl;
        }
        return NORMAL;
    }
    return NORMAL;
}


int CtiFDR_Valmet::processControlMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    string           translationName;
    int                 quality =NormalQuality;
    USHORT              value = ntohs(data->Control.Value);        
    CtiTime              timestamp;
    CtiFDRPointSPtr         point;

    string           desc;
    CHAR          action[60];

    // convert to our name
    translationName = string (data->Control.Name);

    // see if the point exists
    {
        CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
        string pointName = translationName;
        std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);

        std::map<string,int>::iterator iter = nameToPointId.find(pointName);
        if( iter != nameToPointId.end() ) {
            point = getReceiveFromList().getPointList()->findFDRPointID(iter->second);
        }
    }

    if (point && point->isControllable())
    {
        if (point->getPointType() == StatusPointType)
        {
            int controlState=INVALID;
            controlState = ForeignToYukonStatus (data->Control.Value);
    
            if ((controlState != OPENED) && (controlState != CLOSED))
            {
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
                if (stringContainsIgnoreCase(translationName, scanDevicePointName)) 
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
    
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
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
            double dValue = ntohieeef(data->Control.LongValue);    
            if (point->getPointOffset() == 10001) 
            {    
                int controlState=INVALID;
                controlState = ForeignToYukonStatus (data->Control.Value);
                dValue = ForeignToYukonStatus (data->Control.Value);
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Analog Output Point " << translationName << " with PointOffset " << point->getPointOffset() << " received value of c:" << controlState << " d: " << dValue;
                }
            }
            CtiCommandMsg *aoMsg = createAnalogOutputMessage(point->getPointID(), translationName, dValue);
            sendMessageToDispatch(aoMsg);
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
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
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
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

USHORT CtiFDR_Valmet::ForeignToYukonQuality (USHORT aQuality)
{
    USHORT Quality = NormalQuality;
    USHORT HostQuality;

    HostQuality = ntohs (aQuality);

    /* Test for the various Valmet Qualities and translate to CTI */
    if (HostQuality & VALMET_PLUGGED)
        Quality = NonUpdatedQuality;
    else if (HostQuality & VALMET_MANUALENTRY)
        Quality = ManualQuality;
    else if (HostQuality & VALMET_DATAINVALID)
        Quality = InvalidQuality;
    else if (HostQuality & VALMET_UNREASONABLE)
        Quality = AbnormalQuality;
    else if (HostQuality & VALMET_OUTOFSCAN)
        Quality = AbnormalQuality;

    return(Quality);
}

USHORT CtiFDR_Valmet::YukonToForeignQuality (USHORT aQuality)
{
    USHORT Quality = VALMET_NORMAL;

    /* Test for the various CTI Qualities and translate to Valmet */
    if (aQuality == NonUpdatedQuality)
        Quality = VALMET_PLUGGED;
    else if (aQuality == InvalidQuality)
        Quality = VALMET_PLUGGED;
    else if (aQuality == ManualQuality)
        Quality = VALMET_MANUALENTRY;
    else if (aQuality == AbnormalQuality)
        Quality = VALMET_PLUGGED;
    if (aQuality == UnintializedQuality)
        Quality = VALMET_PLUGGED;

    return htons (Quality);
}

 string CtiFDR_Valmet::ForeignQualityToString(USHORT quality)
 {
     string retString;
     quality = ntohs(quality);
     if (quality == VALMET_NORMAL)
        retString = "VALMET_NORMAL";
     else if (quality == VALMET_PLUGGED)
        retString = "VALMET_PLUGGED";
     else if (quality == VALMET_MANUALENTRY)
         retString = "VALMET_MANUALENTRY";
     else if (quality == VALMET_DATAINVALID)
         retString = "VALMET_DATAINVALID";
     else if (quality == VALMET_UNREASONABLE)
         retString = "VALMET_UNREASONABLE";
     else if (quality == VALMET_OUTOFSCAN)
         retString = "VALMET_OUTOFSCAN";
     else
     {    
         retString = "UNDEFINED - 0x";
         CHAR buf[20];
         retString += string (itoa (quality,buf,16));
     }
     return retString;
 }


// Convert Valmet status to CTI Status
int CtiFDR_Valmet::ForeignToYukonStatus (USHORT aStatus)
{
    int tmpstatus=INVALID;

    switch (ntohs (aStatus))
    {
        case Valmet_Open:
            tmpstatus = OPENED;
            break;
        case Valmet_Closed:
            tmpstatus = CLOSED;
            break;
        case Valmet_Indeterminate:
            tmpstatus = INDETERMINATE;
            break;

    }
    return(tmpstatus);
}

USHORT CtiFDR_Valmet::YukonToForeignStatus (int aStatus)
{
    USHORT tmpstatus=Valmet_Invalid;

    switch (aStatus)
    {
        case OPENED:
            tmpstatus = Valmet_Open;
            break;
        case CLOSED:
            tmpstatus = Valmet_Closed;
            break;
        case INDETERMINATE:
            tmpstatus = Valmet_Indeterminate;
            break;
    }
    return(htons (tmpstatus));
}


CtiTime CtiFDR_Valmet::ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag)
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

    ts.tm_year -= 1900;
    ts.tm_mon--;

    /*********************
    * valmet doesn't fill this in apparently so
    * use whatever we think daylight savings is
    *********************
    */
    ts.tm_isdst = CtiTime().isDST();

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

    return retVal;
}

string CtiFDR_Valmet::YukonToForeignTime (CtiTime aTimeStamp)
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
             "%4ld%02ld%02ld%02ld%02ld%02ldCST",
             tmpDate.year(),
             tmpDate.month(),
             tmpDate.dayOfMonth(),
             aTimeStamp.hour(),
             aTimeStamp.minute(),
             aTimeStamp.second());

    if (aTimeStamp.isDST())
    {
        tmp[15] = 'D';
    }

    return(string (tmp));
}

void CtiFDR_Valmet::updatePointQualitiesOnDevice(PointQuality_t quality, long paoId)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Updating All Point Qualities on Device with ID: "<< paoId <<" with Send Direction to Quality of " << quality << endl;
    }
    {
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
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Updating quality to: " << quality << " for PointId: "<<point->getPointID() << " for " << getInterfaceName() << " interface."<< endl;
                }
                sendMessageToDispatch (localMsg);
            }
        }

    }

}

bool CtiFDR_Valmet::alwaysSendRegistrationPoints()
{
    return true;
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
        valmetInterface = new CtiFDR_Valmet();

        // now start it up
        return valmetInterface->run();
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

        valmetInterface->stop();
        delete valmetInterface;
        valmetInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif



