#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrrdex.cpp
*
*    DATE: 03/07/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrrdex.cpp-arc  $
*    REVISION     :  $Revision: 1.6 $
*    DATE         :  $Date: 2004/09/24 14:36:53 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: generic Interface to a Foreign System
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 from a scada system.  The data is both status and Analog data.
*             Information is exchanged using sockets opened on a predefined socket 
*             number and also pre-defined messages between the systems.  See the 
*             design document for more information
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrrdex.cpp,v $
      Revision 1.6  2004/09/24 14:36:53  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.5  2004/06/08 19:22:07  dsutton
      The timestamp being sent wasn't setting the daylight savings flag correctly

      Revision 1.4  2003/10/31 21:15:41  dsutton
      Updated to allow us to send and receive accumlator points to other systems.
      Oversite from the original implementation

      Revision 1.3  2002/04/16 15:58:36  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:57  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.4   06 Mar 2002 11:26:22   dsutton
   added delay before sending the registration ack to allow the machinery to finish starting up.  Changed the load list function to allow colons in the translation string (still no semi colons allowed)
   
      Rev 2.3   01 Mar 2002 13:25:52   dsutton
   stumbled across an error when calculating the time received from a foreign system.  We weren't using their daylight savings flag
   
      Rev 2.2   15 Feb 2002 11:17:16   dsutton
   added two new cparms to control data flow to RDEX that limit the number of entries sent per so many seconds.  Also changed the log wording to be entries queued, not sent to handle possible discrepancies
   
      Rev 2.1   11 Feb 2002 15:03:34   dsutton
   added event logs when the connection is established or failed, unknown points, invalid states, etc
   
      Rev 2.0   20 Dec 2001 14:52:42   dsutton
   Overrode the isregistrationneeded to return true and added a few calls to make sure the client was registered before trying to send any data on to the system
   
      Rev 1.0   14 Dec 2001 17:23:40   dsutton
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
#include "fdrserverconnection.h"

// this class header
#include "fdrrdex.h"


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

// Constructors, Destructor, and Operators
CtiFDR_Rdex::CtiFDR_Rdex()
: CtiFDRSingleSocket(RWCString("RDEX"))
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
int CtiFDR_Rdex::readConfig()
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
        setPortNumber (RDEX_PORTNUMBER);
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
        setReloadRate (3600);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr));
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

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Rdex port number " << getPortNumber() << endl;
        dout << RWTime() << " Rdex timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << RWTime() << " Rdex db reload rate " << getReloadRate() << endl;
        dout << RWTime() << " Rdex queue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << RWTime() << " Rdex send rate " << getOutboundSendRate() << endl;
        dout << RWTime() << " Rdex send interval " << getOutboundSendInterval() << " second(s) " << endl;

        if (isInterfaceInDebugMode())
            dout << RWTime() << " Rdex running in debug mode " << endl;
        else
            dout << RWTime() << " Rdex running in normal mode "<< endl;
    }
    return successful;
}


bool CtiFDR_Rdex::translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aDestinationIndex)
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
            // this in the translation name on rdex
            RWCTokenizer nextTempToken(tempString1);

            // do not care about the first part
            nextTempToken(":");
            tempString2 = nextTempToken(";");
            /**************************
            * as luck would have it, I need to allow colons in the translation names
            * because of this, I know the first character in tempString2 is the first : 
            * found.  I need what is left in the string after removing the colon
            ***************************
            */
            tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));

            // now we have a translation name
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

CHAR *CtiFDR_Rdex::buildForeignSystemMsg ( CtiFDRPoint &aPoint )
{
    CHAR *buffer=NULL;
    bool                 retVal = true;

   /**************************
    * we allocate a valmet message here and it will be deleted
    * inside of the write function on the connection 
    ***************************
    */
    buffer = new CHAR[sizeof (RdexInterface_t)];
    RdexInterface_t *ptr = (RdexInterface_t *)buffer;

    // make sure we have all the pieces
    if (buffer != NULL)
    {
        // set the timestamp, everything else is based on type of message
        strcpy (ptr->timestamp,  YukonToForeignTime (aPoint.getLastTimeStamp()));

        switch (aPoint.getPointType())
        {
            case AnalogPointType:
            case CalculatedPointType:
            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
                {
                    ptr->function = htonl (SINGLE_SOCKET_VALUE);
                    strcpy (ptr->value.translation,aPoint.getTranslateName(getLayer()->getName()));
                    ptr->value.quality = YukonToForeignQuality (aPoint.getQuality());
                    ptr->value.longValue = htonieeef (aPoint.getValue());

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Analog/Calculated point " << aPoint.getPointID();
                        dout << " queued as " << aPoint.getTranslateName(getLayer()->getName());
                        dout << " value " << aPoint.getValue() << " to " << getLayer()->getName() << endl;;
                    }
                    break;
                }

            case StatusPointType:
                {
                    if (aPoint.isControllable())
                    {
                        ptr->function = htonl (SINGLE_SOCKET_CONTROL);
                        strcpy (ptr->control.translation,aPoint.getTranslateName(getLayer()->getName()));

                        // check for validity of the status, we only have open or closed for rdex
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] buffer;
                            buffer = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for Rdex interface to " << getLayer()->getName() << endl;
                            }
                        }
                        else
                        {
                            ptr->control.value = YukonToForeignStatus (aPoint.getValue());

                             if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime() << " Control point " << aPoint.getPointID();
                                 dout << " queued as " << aPoint.getTranslateName(getLayer()->getName());
                                 if (aPoint.getValue() == OPENED)
                                 {
                                     dout << " state of Open ";
                                 }
                                 else
                                 {
                                     dout << " state of Close ";
                                 }
                                 dout << "to " << getLayer()->getName() << endl;;
                             }
                        }
                    }
                    else
                    {
                        ptr->function = htonl (SINGLE_SOCKET_STATUS);

                        strcpy (ptr->status.translation,aPoint.getTranslateName(getLayer()->getName()));
                        ptr->status.quality = YukonToForeignQuality (aPoint.getQuality());

                        // check for validity of the status, we only have open or closed for rdex
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] buffer;
                            buffer = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for Rdex interface to " << getLayer()->getName() << endl;
                            }
                        }
                        else
                        {
                            ptr->status.value = YukonToForeignStatus (aPoint.getValue());

                             if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << RWTime() << " Status point " << aPoint.getPointID();
                                 dout << " queued as " << aPoint.getTranslateName(getLayer()->getName());
                                 if (aPoint.getValue() == OPENED)
                                 {
                                     dout << " state of Open ";
                                 }
                                 else
                                 {
                                     dout << " state of Close ";
                                 }
                                 dout << "to " << getLayer()->getName() << endl;;
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
    return buffer;
}

CHAR *CtiFDR_Rdex::buildForeignSystemHeartbeatMsg ()
{
    CHAR *buffer=NULL;

    // check registration here, we're not supposed to send if we haven't registered
    if (isRegistered())
    {
        /**************************
        * we allocate a rdex message here and it will be deleted
        * inside of the write function on the connection 
        ***************************
        */
        buffer = new CHAR[sizeof (RdexInterface_t)];
        RdexInterface_t *ptr = (RdexInterface_t *)buffer;
        bool retVal = true;

        if (buffer != NULL)
        {
            ptr->function = htonl (SINGLE_SOCKET_NULL);
            strcpy (ptr->timestamp, YukonToForeignTime (RWTime()));
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Not building heartbeat message, not registered " << endl;
    }
    return buffer;
}

int CtiFDR_Rdex::getMessageSize(CHAR *aBuffer)
{
    return sizeof (RdexInterface_t);
}


RWCString CtiFDR_Rdex::decodeClientName(CHAR * aBuffer)
{
    RdexInterface_t *ptr = (RdexInterface_t *)aBuffer;

    if (ptr == NULL)
    {
        return RWCString("RDEX");
    }
    else
    {
        return RWCString(ptr->registration.clientName);
    }

}

int CtiFDR_Rdex::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = NORMAL;
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Time sync message received from " << getLayer()->getName() << endl;
                }
                break;
            }
        case SINGLE_SOCKET_NULL:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Heartbeat message received from " << getLayer()->getName() << " at " << RWCString (inet_ntoa(getLayer()->getInBoundConnection()->getAddr().sin_addr)) <<  endl;;
                }
                break;
            }
        default:
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unknown message type " << ntohl (rdex->function) <<  " received from " << getInterfaceName() << endl;
            }
    }

    return retVal;

}

int CtiFDR_Rdex::processRegistrationMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    Rdex_Registration *data = (Rdex_Registration*)aData;
    RWCString           desc;
    RWCString           action;


    getLayer()->setName (RWCString (data->clientName));
    CHAR *buffer=NULL;
    /**************************
    * we allocate a rdex message here and it will be deleted
    * inside of the write function on the connection 
    ***************************
    */
    buffer = new CHAR[sizeof (RdexInterface_t)];
    Rdex_Acknowledgement *ptr = (Rdex_Acknowledgement *)buffer;

    if (buffer != NULL)
    {
        ptr->function = htonl (SINGLE_SOCKET_ACKNOWLEDGEMENT);
        strcpy (ptr->timestamp, YukonToForeignTime (RWTime()));
        strcpy (ptr->serverName, "YUKON_RDEX");
        Sleep (500);  // this a dangerous way to do this
        if (getLayer()->write (buffer))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Registration acknowledgement to " << getLayer()->getName() << " failed " << endl;
            }

            action = getLayer()->getName() + ": Acknowledgment";
            desc = getLayer()->getName() + " registration acknowledgment message has failed";
            logEvent (desc,action,true);
            retVal = !NORMAL;
        }
        else
        {
            setRegistered (true);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getLayer()->getName() << " has registered with Yukon" << endl;
            }

            action = getLayer()->getName() + ": Registration";
            desc = getLayer()->getName() + " has registered with Yukon";
            logEvent (desc,action,true);

            retVal = NORMAL;
        }
    }

    return retVal;
}


int CtiFDR_Rdex::processValueMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    CtiPointDataMsg     *pData;
    RdexInterface_t  *data = (RdexInterface_t*)aData;
    RWCString           translationName;
    int                 quality;
    FLOAT               value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool               flag = true;
    CHAR               action[60];
    RWCString          desc;

    // accept nothing until we are registered
    if (isRegistered())
    {
        // convert to our name
        translationName = RWCString (data->value.translation);

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
            if (timestamp == rwEpoch)
            {
                desc = getInterfaceName() + RWCString (" analog point received with an invalid timestamp ") + RWCString (data->timestamp);
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
                    dout << " value " << value << " from " << getLayer()->getName() << " assigned to point " << point.getPointID() << endl;;
                }
                retVal = NORMAL;
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
                    dout << " from " << getLayer()->getName() << " was not found" << endl;
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
                    dout << " from " << getLayer()->getName() << " was mapped incorrectly to non-analog point " << point.getPointID() << endl;
                }
                CHAR pointID[20];
                desc = getInterfaceName() + RWCString (" analog point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
                _snprintf(action,60,"%s", translationName);
                logEvent (desc,RWCString (action));
            }
            retVal = !NORMAL;
        }
    }
    return retVal;
}

int CtiFDR_Rdex::processStatusMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    CtiPointDataMsg     *pData;
    RdexInterface_t  *data = (RdexInterface_t*)aData;
    RWCString           translationName;
    int                 quality;
    FLOAT               value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    CHAR                action[60];
    RWCString           desc;

    // accept nothing until we are registered
    if (isRegistered())
    {
        translationName = RWCString (data->status.translation);
    
        // see if the point exists
        flag = findTranslationNameInList (translationName, getReceiveFromList(), point);
    
        if ((flag == true) && (point.getPointType() == StatusPointType))
        {
            // assign last stuff  
            quality = ForeignToYukonQuality (data->status.quality);
            value = ForeignToYukonStatus (data->status.value);
            timestamp = ForeignToYukonTime (data->timestamp);
    
            if ((timestamp == rwEpoch) || (value == Rdex_Invalid))
            {
                if (timestamp == rwEpoch)
                {
                    desc = getInterfaceName() + RWCString (" status point received with an invalid timestamp ") + RWCString (data->timestamp);
                    _snprintf(action,60,"%s for pointID %d", 
                              translationName,
                              point.getPointID());
                    logEvent (desc,RWCString (action));
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Status point " << translationName;
                        dout << " from " << getLayer()->getName()<< " has an invalid state " <<ntohl(data->status.value) << endl;
                    }
                    CHAR state[20];
                    desc = getInterfaceName() + RWCString (" status point received with an invalid state ") + RWCString (itoa (ntohl(data->status.value),state,10));
                    _snprintf(action,60,"%s for pointID %d", 
                              translationName,
                              point.getPointID());
                    logEvent (desc,RWCString (action));
                }
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
                    else 
                    {
                        dout << " new state: Closed " ;
                    }
                    dout <<" from " << getLayer()->getName() << " assigned to point " << point.getPointID() << endl;;
                }
                retVal = NORMAL;
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
                    dout << " from " << getLayer()->getName() << " was not found" << endl;
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
                    dout << " from " << getLayer()->getName() << " was mapped incorrectly to non-status point " << point.getPointID() << endl;
                }
                CHAR pointID[20];
                desc = getInterfaceName() + RWCString (" status point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
                _snprintf(action,60,"%s", translationName);
                logEvent (desc,RWCString (action));
            }
            retVal = !NORMAL;
        }
    }
    return retVal;
}

int CtiFDR_Rdex::processControlMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    CtiPointDataMsg     *pData;
    RdexInterface_t  *data = (RdexInterface_t*)aData;
    RWCString           translationName;
    int                 quality =NormalQuality;
    FLOAT               value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    CHAR           action[60];
    RWCString      desc;
    
    // accept nothing until we are registered
    if (isRegistered())
    {
        // convert to our name
        translationName = RWCString (data->control.translation);
    
        // see if the point exists
        flag = findTranslationNameInList (translationName, getReceiveFromList(), point);
    
        if ((flag == true) && (point.getPointType() == StatusPointType) && (point.isControllable()))
        {
            ULONG controlState;
            controlState = ForeignToYukonStatus (data->control.value);
    
            if (controlState == Rdex_Invalid)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Control point " << translationName;
                    dout << " from " << getLayer()->getName()<< " has an invalid control state " <<ntohl(data->control.value) << endl;
                }
                CHAR state[20];
                desc = getInterfaceName() + RWCString (" control point received with an invalid state ") + RWCString (itoa (ntohl(data->control.value),state,10));
                _snprintf(action,60,"%s for pointID %d", 
                          translationName,
                          point.getPointID());
                logEvent (desc,RWCString (action));
                retVal = !NORMAL;
            }
            else if ((controlState == OPENED || controlState == CLOSED))
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
    
                    dout <<" from " << getLayer()->getName() << " and processed for point " << point.getPointID() << endl;;
                 }
                retVal = NORMAL;
            }
        }
        else
        {
            if (flag == false)
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Translation for control point " <<  translationName;
                    dout << " from " << getLayer()->getName() << " was not found" << endl;
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
                    dout << " received from " << getLayer()->getName();
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
                    dout << " received from " << getLayer()->getName();
                    dout << " was mapped to non-control point " <<  point.getPointID() << endl;;
                }
                CHAR pointID[20];
                desc = getInterfaceName() + RWCString (" control point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
                _snprintf(action,60,"%s", translationName);
                logEvent (desc,RWCString (action));
            }
            retVal = !NORMAL;
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
            tmpstatus = OPENED;
            break;
        case Rdex_Closed:
            tmpstatus = CLOSED;
            break;
    }
    return(tmpstatus);
}


ULONG CtiFDR_Rdex::YukonToForeignStatus (ULONG aStatus)
{
   ULONG tmpstatus=Rdex_Open;   

   switch (aStatus)
   {
      case OPENED:
         tmpstatus = Rdex_Open;
         break;
      case CLOSED:
         tmpstatus = Rdex_Closed;
         break;
   }
   return(htonl (tmpstatus));
}


RWTime CtiFDR_Rdex::ForeignToYukonTime (PCHAR aTime)
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
        return(RWTime(rwEpoch));
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

    RWTime returnTime(&ts);

    // if RWTime can't make a time ???
    if ((returnTime.seconds() > (RWTime::now().seconds() + getTimestampReasonabilityWindow())) || (!returnTime.isValid()))
    {
        return(RWTime(rwEpoch));
    }


    return returnTime;
}

RWCString CtiFDR_Rdex::YukonToForeignTime (RWTime aTimeStamp)
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



