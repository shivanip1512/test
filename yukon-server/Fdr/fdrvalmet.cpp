#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrvalmet.cpp
*
*    DATE: 03/07/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrvalmet.cpp-arc  $
*    REVISION     :  $Revision: 1.6 $
*    DATE         :  $Date: 2004/09/29 17:47:48 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Interface to the Valmet Foreign System
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 from a Valmet scada system.  The data is both status and Analog data.
*				  Information is exchanged using sockets opened on a predefined socket 
*				  number and also pre-defined messages between the systems.  See the 
*				  design document for more information
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrvalmet.cpp,v $
      Revision 1.6  2004/09/29 17:47:48  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

      Revision 1.5  2004/09/24 14:36:53  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.4  2003/10/31 21:15:41  dsutton
      Updated to allow us to send and receive accumlator points to other systems.
      Oversite from the original implementation

      Revision 1.3  2002/04/16 15:58:39  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:59  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.8   08 Apr 2002 14:41:36   dsutton
   updated foreigntoyukontime function to contain a flag that says whether we're processing a time sync.  If we are, we don't want to do the validity window since the timesync has a configurable window of its own
   
      Rev 2.7   01 Mar 2002 13:13:30   dsutton
   added timesync processing cparms and functions
   
      Rev 2.6   15 Feb 2002 11:16:42   dsutton
   added two new cparms to control data flow to VALMET that limit the number of entries sent per so many seconds.  Also changed the log wording to be entries queued, not sent to handle possible discrepancies
   
      Rev 2.5   11 Feb 2002 15:03:14   dsutton
   added event logs when the connection is established or failed, unknown points, invalid states, etc
   
      Rev 2.4   14 Dec 2001 17:17:40   dsutton
   the functions that load the lists of points noware updating point managers instead of creating separate lists of their own.  Hopefully this is easier to follow
   
      Rev 2.3   15 Nov 2001 16:16:40   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin
   
      Rev 2.2   26 Oct 2001 15:20:34   dsutton
   moving revision 1 to 2.x
   
      Rev 1.2.1.0   26 Oct 2001 14:27:50   dsutton
   processing massive updates
   
      Rev 1.2   23 Aug 2001 14:00:02   dsutton
   updated to intercept control points from the yukon side
   
      Rev 1.1   20 Jul 2001 10:04:10   dsutton
   unk
   
      Rev 1.0   19 Jun 2001 10:53:06   dsutton
   Initial revision.
   
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/


#include <windows.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

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

// Constructors, Destructor, and Operators
CtiFDR_Valmet::CtiFDR_Valmet()
: CtiFDRSingleSocket(RWCString(FDR_VALMET))
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
    RWCString   tempStr;

    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr));
    }
    else
    {
        setPortNumber (VALMET_PORTNUMBER);
    }

    tempStr = getCparmValueAsString(KEY_TIMESTAMP_WINDOW);
    if (tempStr.length() > 0)
    {
        setTimestampReasonabilityWindow (atoi (tempStr));
    }
    else
    {
        setTimestampReasonabilityWindow (120);
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr));
    }
    else
    {
        // default to 5 seconds, this could be a lot of points
        setQueueFlushRate (1);
    }

    tempStr = getCparmValueAsString(KEY_OUTBOUND_SEND_RATE);
    if (tempStr.length() > 0)
    {
        setOutboundSendRate (atoi(tempStr));
    }
    else
    {
        // default to 1
        setOutboundSendRate (1);
    }

    tempStr = getCparmValueAsString(KEY_OUTBOUND_SEND_INTERVAL);
    if (tempStr.length() > 0)
    {
        setOutboundSendInterval (atoi(tempStr));
    }
    else
    {
        // default to 1
        setOutboundSendInterval (0);
    }

    tempStr = getCparmValueAsString(KEY_TIMESYNC_VARIATION);
    if (tempStr.length() > 0)
    {
        setTimeSyncVariation (atoi(tempStr));
        if (getTimeSyncVariation() < 5)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Valmet max time sync variation of " << getTimeSyncVariation() << " second(s) is invalid, defaulting to 5 seconds" << endl;
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
        if (!tempStr.compareTo ("false",RWCString::ignoreCase))
        {
            setUpdatePCTimeFlag (false);
        }
    }


    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Valmet port number " << getPortNumber() << endl;
        dout << RWTime() << " Valmet timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << RWTime() << " Valmet db reload rate " << getReloadRate() << endl;
        dout << RWTime() << " Valmet queue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << RWTime() << " Valmet send rate " << getOutboundSendRate() << endl;
        dout << RWTime() << " Valmet send interval " << getOutboundSendInterval() << " second(s) " << endl;
        dout << RWTime() << " Valmet max time sync variation " << getTimeSyncVariation() << " second(s) " << endl;

        if (shouldUpdatePCTime())
            dout << RWTime() << " Valmet time sync will reset PC clock" << endl;
        else
            dout << RWTime() << " Valmet time sync will not reset PC clock" << endl;

        if (isInterfaceInDebugMode())
            dout << RWTime() << " Valmet running in debug mode " << endl;
        else
            dout << RWTime() << " Valmet running in normal mode "<< endl;
    }


    return successful;
}


bool CtiFDR_Valmet::translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aDestinationIndex)
{
    bool                successful(false);
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false;

    try
    {
        RWCTokenizer nextTranslate(translationPoint->getDestinationList()[aDestinationIndex].getTranslation());

            // skip the first part entirely for now
        if (!(tempString1 = nextTranslate(";")).isNull())
        {
            // this in the point name on valmet
            RWCTokenizer nextTempToken(tempString1);

            // do not care about the first part
            nextTempToken(":");

            tempString2 = nextTempToken(":");

            // now we have a category with a :
            if ( !tempString2.isNull() )
            {
                // put category in final name
                translationName= tempString2;

                // i'm updating my copied list
                translationPoint->getDestinationList()[aDestinationIndex].setTranslation (tempString2);
                successful = true;
            }   // no point name
        }   // first token invalid
    } // end try

    catch (RWExternalErr e )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  (...) "<< endl;
    }
    return successful;
}

CHAR *CtiFDR_Valmet::buildForeignSystemMsg ( CtiFDRPoint &aPoint )
{
    CHAR *valmet=NULL;
    bool                 retVal = true;

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
        strcpy (ptr->TimeStamp,  YukonToForeignTime (aPoint.getLastTimeStamp()));

        switch (aPoint.getPointType())
        {
            case AnalogPointType:
            case CalculatedPointType:
            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
                {
                    ptr->Function = htons (SINGLE_SOCKET_VALUE);
                            strcpy (ptr->Value.Name,aPoint.getTranslateName(RWCString (FDR_VALMET)));
                    ptr->Value.Quality = YukonToForeignQuality (aPoint.getQuality());
                    ptr->Value.LongValue = htonieeef (aPoint.getValue());

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Analog/Calculated point " << aPoint.getPointID();
                        dout << " queued as " << aPoint.getTranslateName(RWCString (FDR_VALMET));
                        dout << " value " << aPoint.getValue() << " to " << getInterfaceName() << endl;;
                    }
                    break;
                }

            case StatusPointType:
                {
                    if (aPoint.isControllable())
                    {
                        ptr->Function = htons (SINGLE_SOCKET_CONTROL);
                        strcpy (ptr->Control.Name,aPoint.getTranslateName(RWCString (FDR_VALMET)));

                        // check for validity of the status, we only have open or closed in controls
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] valmet;
                            valmet = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                            }
                        }
                        else
                        {
                            ptr->Control.Value = YukonToForeignStatus (aPoint.getValue());

                             if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime() << " Control point " << aPoint.getPointID();
                                 dout << " queued as " << aPoint.getTranslateName(RWCString (FDR_VALMET));
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
                        strcpy (ptr->Value.Name,aPoint.getTranslateName(RWCString (FDR_VALMET)));
                        ptr->Status.Quality = YukonToForeignQuality (aPoint.getQuality());

                        // check for validity of the status, we only have open or closed for Valmet
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] valmet;
                            valmet = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                            }
                        }
                        else
                        {
                            ptr->Status.Value = YukonToForeignStatus (aPoint.getValue());

                             if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime() << " Status point " << aPoint.getPointID();
                                 dout << " queued as " << aPoint.getTranslateName(RWCString (FDR_VALMET));
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
        strcpy (ptr->TimeStamp, YukonToForeignTime (RWTime()));
    }
    return valmet;
}

int CtiFDR_Valmet::getMessageSize(CHAR *aBuffer)
{
    return sizeof (ValmetInterface_t);
}

RWCString CtiFDR_Valmet::decodeClientName(CHAR * aBuffer)
{
    return getInterfaceName();
}


int CtiFDR_Valmet::processTimeSyncMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    RWTime              timestamp;
    RWCString           desc;
    RWCString               action;

    timestamp = ForeignToYukonTime (data->TimeStamp,true);
    if (timestamp == rwEpoch)
    {
        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getInterfaceName() << "time sync request was invalid " <<  RWCString (data->TimeStamp) << endl;
        }
        desc = getInterfaceName() + RWCString (" time sync request was invalid ") + RWCString (data->TimeStamp);
        logEvent (desc,action,true);
        retVal = !NORMAL;
    }
    else
    {
        RWTime now;
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
                        desc = getInterfaceName() + RWCString ("'s request to change PC time to ") + timestamp.asString() + RWCString (" was processed");
                        action = RWCString ("PC time reset to") + timestamp.asString();
                        logEvent (desc,action,true);

//                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << getInterfaceName() << "'s request to change PC time to " << timestamp.asString() << " was processed" << endl;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Unable to process time change from " << getInterfaceName();
                        }
                        desc = getInterfaceName() + RWCString ("'s request to change PC time to ") + timestamp.asString() + RWCString (" failed");
                        action = RWCString ("System time update API failed");
                        logEvent (desc,action,true);
                        retVal = !NORMAL;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Unable to process time change from " << getInterfaceName();
                    }
                    desc = getInterfaceName() + RWCString ("'s request to change PC time to ") + timestamp.asString() + RWCString (" failed");
                    action = RWCString ("System time update API failed");
                    logEvent (desc,action,true);
                    retVal = !NORMAL;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Time change requested from " << getInterfaceName();
                    dout << " of " << timestamp.asString() << " is outside standard +-30 minutes " << endl;
                }

                //log we're way out of whack now
                desc = getInterfaceName() + RWCString ("'s request to change PC time to ") + timestamp.asString() + RWCString (" was denied");
                action = RWCString ("Requested time is greater that +-30 minutes");
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
    RWCString           translationName;
    int                 quality;
    DOUBLE              value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool   flag = true;
    RWCString           desc;
    CHAR               action[60];

    // convert to our name
    translationName = RWCString (data->Value.Name);

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
        if (timestamp == rwEpoch)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getInterfaceName() << " analog value received with an invalid timestamp " <<  RWCString (data->TimeStamp) << endl;
            }

            desc = getInterfaceName() + RWCString (" analog point received with an invalid timestamp ") + RWCString (data->TimeStamp);
            _snprintf(action,60,"%s for pointID %d", 
                      translationName,
                      point.getPointID());
            logEvent (desc,RWCString (action));
            retVal = !NORMAL;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Analog point " << translationName;
                dout << " value " << value << " from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
            }

        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for analog point " << translationName;
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
            desc = getInterfaceName() + RWCString (" analog point is not listed in the translation table");
            _snprintf(action,60,"%s", translationName);
            logEvent (desc,RWCString (action));
        }
        else
        {    
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Analog point " << translationName;
                dout << " from " << getInterfaceName() << " was mapped incorrectly to non-analog point " << point.getPointID() << endl;
            }

            CHAR pointID[20];
            desc = getInterfaceName() + RWCString (" analog point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"%s", translationName);
            logEvent (desc,RWCString (action));
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
    RWCString           translationName;
    int                 quality;
    DOUBLE              value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;

    RWCString           desc;
    CHAR           action[60];

    translationName = RWCString (data->Status.Name);

    // see if the point exists
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) && (point.getPointType() == StatusPointType))
    {
        // assign last stuff	
        quality = ForeignToYukonQuality (data->Status.Quality);

        value = ForeignToYukonStatus (data->Status.Value);
        timestamp = ForeignToYukonTime (data->TimeStamp);
        if (timestamp == rwEpoch)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getInterfaceName() << " status value received with an invalid timestamp " <<  RWCString (data->TimeStamp) << endl;
            }

            desc = getInterfaceName() + RWCString (" status point received with an invalid timestamp ") + RWCString (data->TimeStamp);
            _snprintf(action,60,"%s for pointID %d", 
                      translationName,
                      point.getPointID());
            logEvent (desc,RWCString (action));
            retVal = !NORMAL;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Status point " << translationName;
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

                dout <<" from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for status point " <<  translationName;
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
            desc = getInterfaceName() + RWCString (" status point is not listed in the translation table");
            _snprintf(action,60,"%s", translationName);
            logEvent (desc,RWCString (action));
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Status point " << translationName;
                dout << " from " << getInterfaceName() << " was mapped incorrectly to non-status point " << point.getPointID() << endl;
            }
            CHAR pointID[20];
            desc = getInterfaceName() + RWCString (" status point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"%s", translationName);
            logEvent (desc,RWCString (action));
        }
        retVal = !NORMAL;
    }

    return retVal;
}

int CtiFDR_Valmet::processControlMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ValmetInterface_t  *data = (ValmetInterface_t*)aData;
    RWCString           translationName;
    int                 quality =NormalQuality;
    DOUBLE              value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;

    RWCString           desc;
    CHAR          action[60];

    // convert to our name
    translationName = RWCString (data->Control.Name);

    // see if the point exists
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) && (point.getPointType() == StatusPointType) && (point.isControllable()))
    {
        int controlState=INVALID; 
        controlState = ForeignToYukonStatus (data->Control.Value);

        if ((controlState != OPENED) && (controlState != CLOSED))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " << translationName;
                dout << " from " << getInterfaceName() << " has an invalid control state " << ntohs(data->Control.Value) << endl;
            }
            CHAR state[20];
            desc = getInterfaceName() + RWCString (" control point received with an invalid state ") + RWCString (itoa (ntohs(data->Control.Value),state,10));
            _snprintf(action,60,"%s for pointID %d", 
                      translationName,
                      point.getPointID());
            logEvent (desc,RWCString (action));
            retVal = !NORMAL;
        }
        else 
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " << translationName;
                if (controlState == OPENED)
                {
                    dout << " control: Open " ;
                }
                else
                {
                    dout << " control: Closed " ;
                }

                dout <<" from " << getInterfaceName() << " and processed for point " << point.getPointID() << endl;;
            }

        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for control point " <<  translationName;
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }
            desc = getInterfaceName() + RWCString (" control point is not listed in the translation table");
            _snprintf(action,60,"%s", translationName);
            logEvent (desc,RWCString (action));
        }
        else if (!point.isControllable())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " << translationName;
                dout << " received from " << getInterfaceName();
                dout << " was not configured receive for control for point " << point.getPointID() << endl;
            }
            desc = getInterfaceName() + RWCString (" control point is not configured to receive controls");
            _snprintf(action,60,"%s for pointID %d", 
                      translationName,
                      point.getPointID());
            logEvent (desc,RWCString (action));
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " << translationName;
                dout << " received from " << getInterfaceName();
                dout << " was mapped to non-control point " <<  point.getPointID() << endl;;
            }
            CHAR pointID[20];
            desc = getInterfaceName() + RWCString (" control point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"%s", translationName);
            logEvent (desc,RWCString (action));
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
	USHORT Quality = VALMET_PLUGGED;

	/* Test for the various CTI Qualities and translate to Valmet */
	if (aQuality == NonUpdatedQuality)
		Quality = VALMET_PLUGGED;
	else if (aQuality == InvalidQuality)
		Quality = VALMET_DATAINVALID;
	else if (aQuality == ManualQuality)
		Quality = VALMET_MANUALENTRY;
	else if (aQuality == AbnormalQuality)
		Quality = VALMET_UNREASONABLE;
	if (aQuality == UnintializedQuality)
		Quality = VALMET_DATAINVALID;

	return(htons (Quality));
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


RWTime CtiFDR_Valmet::ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag)
{
    struct tm ts;
    RWTime retVal;

    if (sscanf (aTime,
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        retVal = rwEpoch;
    }

    ts.tm_year -= 1900;
    ts.tm_mon--;

    /*********************
    * valmet doesn't fill this in apparently so 
    * use whatever we think daylight savings is 
    *********************
    */
    ts.tm_isdst = RWTime().isDST();

    RWTime returnTime(&ts);

    if (aTimeSyncFlag)
    {
        // just check for validy
        if (!returnTime.isValid())
        {
            retVal = rwEpoch;
        }
        else
        {
            retVal = returnTime;
        }
    }
    else
    {
        // if RWTime can't make a time or we are outside the window
        if ((returnTime.seconds() > (RWTime::now().seconds() + getTimestampReasonabilityWindow())) ||
            (returnTime.seconds() < (RWTime::now().seconds() - getTimestampReasonabilityWindow())) ||
            (!returnTime.isValid()))
    //    if ((returnTime.seconds() > (RWTime().seconds() + getTimestampReasonabilityWindow())) || (!returnTime.isValid()))
        {
            retVal = rwEpoch;
        }
        else
        {
            retVal = returnTime;
        }
    }

    return retVal;
}

RWCString CtiFDR_Valmet::YukonToForeignTime (RWTime aTimeStamp)
{
    CHAR      tmp[30];

    /*******************************
    * if the timestamp is less than 01-01-2000 (completely arbitrary number)
    * then set it to now because its probably an error or its uninitialized
    * note: uninitialized points come across as 11-10-1990 
    ********************************
    */
    if (aTimeStamp < RWTime(RWDate(1,1,2001)))
    {
        aTimeStamp = RWTime();
    }

    RWDate tmpDate (aTimeStamp);

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

	return(RWCString (tmp));
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



