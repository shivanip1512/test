#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdracs.cpp
*
*    DATE: 03/07/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdracs.cpp-arc  $
*    REVISION     :  $Revision: 1.7 $
*    DATE         :  $Date: 2004/09/24 14:36:52 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Interface to the Acs Foreign System
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 from an ACS scada system.  The data is both status and Analog data.
*				  Information is exchanged using sockets opened on a predefined socket 
*				  number and also pre-defined messages between the systems.  See the 
*				  design document for more information
*
*    ---------------------------------------------------
*    History: 
      $Log: fdracs.cpp,v $
      Revision 1.7  2004/09/24 14:36:52  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.6  2004/05/10 20:52:22  dsutton
      Interface was not distinguishing between a status point  with remote 11 point 2
      and an analog point remote 11 point 2.  Added code so the point type is part
      of the translation string so all should be fine

      Revision 1.5  2004/02/13 20:37:04  dsutton
      Added a new cparm for ACS interface that allows the user to filter points
      being routed to ACS by timestamp.  The filter is the number of seconds
      since the last update.   If set to zero, system behaves as it always has,
      routing everything that comes from dispatch.  If the cparm is > 0, FDR will
      first check the value and route the point if it has changed.  If the value has
      not changed, FDR will check the timestamp to see if it is greater than or equal
      to the previous timestamp plus the cparm.  If so, route the data, if not, throw
      the point update away.

      Revision 1.3.38.1  2003/10/31 18:31:53  dsutton
      Updated to allow us to send and receive accumlator points to other systems.
      Oversite from the original implementation

      Revision 1.3  2002/04/16 15:58:30  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:53  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.8   08 Apr 2002 14:41:44   dsutton
   updated foreigntoyukontime function to contain a flag that says whether we're processing a time sync.  If we are, we don't want to do the validity window since the timesync has a configurable window of its own
   
      Rev 2.7   01 Mar 2002 13:13:24   dsutton
   added timesync processing cparms and functions
   
      Rev 2.6   15 Feb 2002 11:16:58   dsutton
   added two new cparms to control data flow to ACS that limit the number of entries sent per so many seconds.  Also changed the log wording to be entries queued, not sent to handle possible discrepancies
   
      Rev 2.5   11 Feb 2002 15:03:08   dsutton
   added event logs when the connection is established or failed, unknown points, invalid states, etc
   
      Rev 2.4   14 Dec 2001 17:17:34   dsutton
   the functions that load the lists of points noware updating point managers instead of creating separate lists of their own.  Hopefully this is easier to follow
   
      Rev 2.3   15 Nov 2001 16:16:36   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin
   
      Rev 2.2   26 Oct 2001 15:20:16   dsutton
   moving revision 1 to 2.x
   
      Rev 1.7.1.0   26 Oct 2001 14:19:22   dsutton
   pointype addition, is point sendable, handling massive downloads, etc
   
      Rev 1.7   23 Aug 2001 13:58:48   dsutton
   updated to intercept control points from yukon side.  Won't be sent on startu
   p or any other database reload
   
      Rev 1.6   20 Jul 2001 09:58:48   dsutton
   No change.
   
      Rev 1.5   19 Jun 2001 10:44:08   dsutton
   updated to inherit from a new single socket class
   
      Rev 1.4   04 Jun 2001 14:21:56   dsutton
   updated logging and removed debug messages
   
      Rev 1.3   04 Jun 2001 09:31:32   dsutton
   changed a few calls since things had moved
   
      Rev 1.2   30 May 2001 16:45:22   dsutton
   listener now resides in the connection thread and the server socket is passed
   into the socketlayer
   
      Rev 1.1   10 May 2001 11:12:12   dsutton
   updated with new socket classes
   
      Rev 1.0   23 Apr 2001 11:17:58   dsutton
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
#include "fdracs.h"


/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDR_ACS * acsInterface;

const CHAR * CtiFDR_ACS::KEY_LISTEN_PORT_NUMBER = "FDR_ACS_PORT_NUMBER";
const CHAR * CtiFDR_ACS::KEY_TIMESTAMP_WINDOW = "FDR_ACS_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_ACS::KEY_DB_RELOAD_RATE = "FDR_ACS_DB_RELOAD_RATE";
const CHAR * CtiFDR_ACS::KEY_QUEUE_FLUSH_RATE = "FDR_ACS_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_ACS::KEY_DEBUG_MODE = "FDR_ACS_DEBUG_MODE";
const CHAR * CtiFDR_ACS::KEY_OUTBOUND_SEND_RATE = "FDR_ACS_SEND_RATE";
const CHAR * CtiFDR_ACS::KEY_OUTBOUND_SEND_INTERVAL = "FDR_ACS_SEND_INTERVAL";
const CHAR * CtiFDR_ACS::KEY_TIMESYNC_VARIATION = "FDR_ACS_MAXIMUM_TIMESYNC_VARIATION";
const CHAR * CtiFDR_ACS::KEY_TIMESYNC_UPDATE = "FDR_ACS_RESET_PC_TIME_ON_TIMESYNC";
const CHAR * CtiFDR_ACS::KEY_POINT_TIME_VARIATION = "FDR_ACS_POINT_TIME_VARIATION";
                                    
// Constructors, Destructor, and Operators
CtiFDR_ACS::CtiFDR_ACS()
: CtiFDRSingleSocket(RWCString("ACS"))
{   
    init();
}


CtiFDR_ACS::~CtiFDR_ACS()
{
}

/*************************************************
* Function Name: CtiFDR_ACS::config()
*
* Description: loads cparm config values
* 
**************************************************
*/
int CtiFDR_ACS::readConfig()
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
        setPortNumber (ACS_PORTNUMBER);
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
                dout << RWTime() << " ACS max time sync variation of " << getTimeSyncVariation() << " second(s) is invalid, defaulting to 5 seconds" << endl;
            }
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
        setPointTimeVariation (atoi(tempStr));
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
        dout << RWTime() << " ACS port number " << getPortNumber() << endl;
        dout << RWTime() << " ACS timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << RWTime() << " ACS db reload rate " << getReloadRate() << endl;
        dout << RWTime() << " ACS queue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << RWTime() << " ACS send rate " << getOutboundSendRate() << endl;
        dout << RWTime() << " ACS send interval " << getOutboundSendInterval() << " second(s) " << endl;
        dout << RWTime() << " ACS max time sync variation " << getTimeSyncVariation() << " second(s) " << endl;
        dout << RWTime() << " ACS point time variation " << getPointTimeVariation() << " second(s) " << endl;

        if (shouldUpdatePCTime())
            dout << RWTime() << " ACS time sync will reset PC clock" << endl;
        else
            dout << RWTime() << " ACS time sync will not reset PC clock" << endl;


        if (isInterfaceInDebugMode())
            dout << RWTime() << " ACS running in debug mode " << endl;
        else
            dout << RWTime() << " ACS running in normal mode "<< endl;
    }
    return successful;
}


bool CtiFDR_ACS::translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aDestinationIndex)
{
    bool                successful(false);
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false;
    char                wb[20];

    try
    {
        RWCTokenizer nextTranslate(translationPoint->getDestinationList()[aDestinationIndex].getTranslation());
        
        if (!(tempString1 = nextTranslate(";")).isNull())
        {
            // this in the from of CATEGORY:c
            RWCTokenizer nextTempToken(tempString1);
        
            // do not care about the first part
            nextTempToken(":");
        
            tempString2 = nextTempToken(":");
        
            // now we have a category with a :
            if ( !tempString2.isNull() )
            {
                // put category in final name
                translationName= "C";
                translationName += tempString2.data()[0];
        
                // next token is the remote number
                if (!(tempString1 = nextTranslate(";")).isNull())
                {
                    // this in the from of REMOTE:xxxx
                    RWCTokenizer nextTempToken(tempString1);
        
                    // do not care about the first part
                    nextTempToken(":");
        
                    tempString2 = nextTempToken(":");
        
                    // now we have a category with a :
                    if ( !tempString2.isNull() )
                    {
                        // put category in final name
                        translationName= "R"+tempString2 + translationName;
        
                        // next token is the point number
                        if (!(tempString1 = nextTranslate(";")).isNull())
                        {
                            // this in the from of POINT:xxxx
                            RWCTokenizer nextTempToken(tempString1);
        
                            // do not care about the first part
                            nextTempToken(":");
        
                            tempString2 = nextTempToken(":");
        
                            // now we have a category with a :
                            if ( !tempString2.isNull() )
                            {
                                // put category in final name
                                translationName= "T" + RWCString (itoa(translationPoint->getPointType(),wb,10)) + translationName + "P"+ tempString2;
        
                                // add this point ID and the translated ID
                                translationPoint->getDestinationList()[aDestinationIndex].setTranslation (translationName);
                                successful = true;
        
                            }   // point id invalid
        
                        }   // third token invalid
        
                    }   //remote number invalid
        
                }   // second token invalid
        
            }   // category invalid
        
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
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  (...) " << endl;
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
        strcpy (ptr->TimeStamp,  YukonToForeignTime (aPoint.getLastTimeStamp()));

        switch (aPoint.getPointType())
        {
            case AnalogPointType:
            case CalculatedPointType:
            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
                {
                    ptr->Function = htons (SINGLE_SOCKET_VALUE);
                    YukonToForeignId (aPoint.getTranslateName(RWCString(FDR_ACS)),
                                      ptr->Value.RemoteNumber,
                                      ptr->Value.CategoryCode, 
                                      ptr->Value.PointNumber);
                    ptr->Value.Quality = YukonToForeignQuality (aPoint.getQuality());
                    ptr->Value.LongValue = htonieeef (aPoint.getValue());

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Analog/Calculated point " << aPoint.getPointID();
                        dout << " queued as Remote: " << ntohs(ptr->Value.RemoteNumber);
                        dout << " Category: " << ptr->Value.CategoryCode;
                        dout << " Point: " << ntohs(ptr->Value.PointNumber);
                        dout << " value " << aPoint.getValue() << " to " << getInterfaceName() << endl;;
                    }

                    break;
                }

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
                        YukonToForeignId (aPoint.getTranslateName(RWCString(FDR_ACS)),
                                          ptr->Control.RemoteNumber,
                                          ptr->Control.CategoryCode, 
                                          ptr->Control.PointNumber);

                        // check for validity of the status, we only have open or closed for ACS
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] acs;
                            acs = NULL;

                            if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Control Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for interface " << getInterfaceName() << endl;
                            }
                        }
                        else
                        {
                            ptr->Control.Value = YukonToForeignStatus (aPoint.getValue());

                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Control point " << aPoint.getPointID();
                                dout << " queued as Remote: " << ntohs (ptr->Status.RemoteNumber);
                                dout << " Category: " << ptr->Status.CategoryCode;
                                dout << " Point: " << ntohs(ptr->Status.PointNumber);

                                if (aPoint.getValue() == OPENED)
                                {
                                    dout << " state of Open " << ptr->Control.Value << " " << aPoint.getValue();
                                }
                                else
                                {
                                    dout << " state of Close " << ptr->Control.Value << " " << aPoint.getValue();
                                }
                                dout << " to " << getInterfaceName() << endl;;
                            }
                        }
                    }
                    else
                    {
                        ptr->Function = htons (SINGLE_SOCKET_STATUS);
                        // everything for control and status is the same except function
                        YukonToForeignId (aPoint.getTranslateName(RWCString(FDR_ACS)),
                                          ptr->Status.RemoteNumber,
                                          ptr->Status.CategoryCode, 
                                          ptr->Status.PointNumber);
                        ptr->Status.Quality = YukonToForeignQuality (aPoint.getQuality());

                        // check for validity of the status, we only have open or closed for ACS
                        if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                        {
                            delete [] acs;
                            acs = NULL;

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
                                dout << " queued as Remote: " << ntohs (ptr->Status.RemoteNumber);
                                dout << " Category: " << ptr->Status.CategoryCode;
                                dout << " Point: " << ntohs(ptr->Status.PointNumber);

                                if (aPoint.getValue() == OPENED)
                                {
                                    dout << " state of Open ";
                                }
                                else
                                {
                                    dout << " state of Close ";
                                }
                                dout << " to " << getInterfaceName() << endl;;
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
        strcpy (ptr->TimeStamp, YukonToForeignTime (RWTime()));
    }
    return acs;
}

int CtiFDR_ACS::getMessageSize(CHAR *aBuffer)
{
    return sizeof (ACSInterface_t);
}

RWCString CtiFDR_ACS::decodeClientName(CHAR * aBuffer)
{
    return getInterfaceName();
}


int CtiFDR_ACS::processValueMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    RWCString           translationName;
    int                 quality;
    DOUBLE              value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    RWCString           desc;
    CHAR               action[60];
    CHAR                wb[20];

    // convert to our name
    translationName = ForeignToYukonId (data->Value.RemoteNumber,data->Value.CategoryCode,data->Value.PointNumber);
    translationName = "T" + RWCString (itoa(AnalogPointType,wb,10)) + translationName;

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
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d", 
                      ntohs(data->Value.RemoteNumber), 
                      data->Value.CategoryCode,
                      ntohs(data->Value.PointNumber),
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
                dout << RWTime() << " Analog point Remote: " << ntohs(data->Value.RemoteNumber);
                dout << " Category: " << data->Value.CategoryCode;
                dout << " Point: " << ntohs(data->Value.PointNumber);
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
                dout << RWTime() << " Translation for analog point ";
                dout << " Remote: " << ntohs(data->Value.RemoteNumber);
                dout << " Category: " << data->Value.CategoryCode;
                dout << " Point: " << ntohs(data->Value.PointNumber);
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }

            desc = getInterfaceName() + RWCString (" analog point is not listed in the translation table");
            _snprintf(action,60,"Remote:%d Category:%c Point:%d", 
                      ntohs(data->Value.RemoteNumber), 
                      data->Value.CategoryCode,
                      ntohs(data->Value.PointNumber));
            logEvent (desc,RWCString (action));
        }
        else
        {      
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Analog point ";
                dout << " Remote: " << ntohs(data->Value.RemoteNumber);
                dout << " Category: " << data->Value.CategoryCode;
                dout << " Point: " << ntohs(data->Value.PointNumber);
                dout << " from " << getInterfaceName() << " was mapped incorrectly to non-analog point " << point.getPointID() << endl;
            }

            CHAR pointID[20];
            desc = getInterfaceName() + RWCString (" analog point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d", 
                      ntohs(data->Value.RemoteNumber), 
                      data->Value.CategoryCode,
                      ntohs(data->Value.PointNumber));
            logEvent (desc,RWCString (action));
        }
        retVal = !NORMAL;
    }

    return retVal;
}

int CtiFDR_ACS::processStatusMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    RWCString           translationName;
    int                 quality;
    DOUBLE              value;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;

    RWCString           desc;
    CHAR                action[60];
    CHAR                wb[20];


    // convert to our name
    translationName = ForeignToYukonId (data->Status.RemoteNumber,data->Status.CategoryCode,data->Status.PointNumber);
    translationName = "T" + RWCString (itoa(StatusPointType,wb,10)) + translationName;

    // see if the point exists
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) && (point.getPointType() == StatusPointType))
    {
        // assign last stuff	
        quality = ForeignToYukonQuality (data->Status.Quality);

        value = ForeignToYukonStatus (data->Status.Value);

        if (value == INVALID)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Status point " ;
                dout << " Remote: " << ntohs(data->Status.RemoteNumber);
                dout << " Category: " << data->Status.CategoryCode;
                dout << " Point: " << ntohs(data->Status.PointNumber);
                dout << " from " << getInterfaceName() << " has an invalid status code " <<ntohs(data->Status.Value) << endl;
            }


            CHAR state[20];
            desc = getInterfaceName() + RWCString (" status point received with an invalid state ") + RWCString (itoa (ntohs(data->Status.Value),state,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d", 
                      ntohs(data->Status.RemoteNumber), 
                      data->Status.CategoryCode,
                      ntohs(data->Status.PointNumber),
                      point.getPointID());
            logEvent (desc,RWCString (action));
            retVal = !NORMAL;
        }
        else
        {
            timestamp = ForeignToYukonTime (data->TimeStamp);
            if (timestamp == rwEpoch)
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getInterfaceName() << " status value received with an invalid timestamp " <<  RWCString (data->TimeStamp) << endl;
                }

                desc = getInterfaceName() + RWCString (" status point received with an invalid timestamp ") + RWCString (data->TimeStamp);
                _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d", 
                          ntohs(data->Status.RemoteNumber), 
                          data->Status.CategoryCode,
                          ntohs(data->Status.PointNumber),
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
                    dout << RWTime() << " Status point Remote: " << ntohs(data->Status.RemoteNumber);
                    dout << " Category: " << data->Status.CategoryCode;
                    dout << " Point: " << ntohs(data->Status.PointNumber);
                    if (value == OPENED)
                    {
                        dout << " New state: Open " ;
                    }
                    else
                    {
                        dout << " New state: Closed " ;
                    }

                    dout <<" from " << getInterfaceName() << " assigned to point " << point.getPointID() << endl;;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for status point " ;
                dout << " Remote: " << ntohs(data->Status.RemoteNumber);
                dout << " Category: " << data->Status.CategoryCode;
                dout << " Point: " << ntohs(data->Status.PointNumber);
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }

            desc = getInterfaceName() + RWCString (" status point is not listed in the translation table");
            _snprintf(action,60,"Remote:%d Category:%c Point:%d", 
                      ntohs(data->Status.RemoteNumber), 
                      data->Status.CategoryCode,
                      ntohs(data->Status.PointNumber));
            logEvent (desc,RWCString (action));

        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Status point " ;
                dout << " Remote: " << ntohs(data->Status.RemoteNumber);
                dout << " Category: " << data->Status.CategoryCode;
                dout << " Point: " << ntohs(data->Status.PointNumber);
                dout << " from " << getInterfaceName() << " was mapped incorrectly to non-status point " << point.getPointID() << endl;
            }

            CHAR pointID[20];
            desc = getInterfaceName() + RWCString (" status point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d", 
                      ntohs(data->Status.RemoteNumber), 
                      data->Status.CategoryCode,
                      ntohs(data->Status.PointNumber));
            logEvent (desc,RWCString (action));
        }
        retVal = !NORMAL;
    }

    return retVal;
}

int CtiFDR_ACS::processControlMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    RWCString           translationName;
    int                 quality =NormalQuality;
    RWTime              timestamp;
    CtiFDRPoint         point;
    bool                 flag = true;
    RWCString           desc;
    CHAR                action[60];
    CHAR                wb[20];

    // convert to our name
    translationName = ForeignToYukonId (data->Control.RemoteNumber,data->Control.CategoryCode,data->Control.PointNumber);
    translationName = "T" + RWCString (itoa(StatusPointType,wb,10)) + translationName;

    // see if the point exists
    flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

    if ((flag == true) && (point.getPointType() == StatusPointType) && (point.isControllable()))
    {
        int controlState=INVALID;

        controlState = ForeignToYukonStatus (data->Control.Value);

        // make sure the value is valid
        if (controlState == INVALID)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " ;
                dout << " Remote: " << ntohs(data->Status.RemoteNumber);
                dout << " Category: " << data->Status.CategoryCode;
                dout << " Point: " << ntohs(data->Status.PointNumber);
                dout << " from " << getInterfaceName() << " has an invalid control state " <<ntohs(data->Control.Value) << endl;
            }

            CHAR state[20];
            desc = getInterfaceName() + RWCString (" control point received with an invalid state ") + RWCString (itoa (ntohs(data->Control.Value),state,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d", 
                      ntohs(data->Control.RemoteNumber), 
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber),
                      point.getPointID());
            logEvent (desc,RWCString (action));
            retVal = !NORMAL;
        }
        else if (controlState == OPENED || controlState == CLOSED)
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
                dout << RWTime() << " Control point Remote: " << ntohs(data->Control.RemoteNumber);
                dout << " Category: " << data->Control.CategoryCode;
                dout << " Point: " << ntohs(data->Control.PointNumber);
                if (controlState == OPENED)
                {
                    dout << " Control: Open " ;
                }
                else
                {
                    dout << " Control: Closed " ;
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
                dout << RWTime() << " Translation for control point " ;
                dout << " Remote: " << ntohs(data->Control.RemoteNumber);
                dout << " Category: " << data->Control.CategoryCode;
                dout << " Point: " << ntohs(data->Control.PointNumber);
                dout << " from " << getInterfaceName() << " was not found" << endl;
            }

            desc = getInterfaceName() + RWCString (" control point is not listed in the translation table");
            _snprintf(action,60,"Remote:%d Category:%c Point:%d", 
                      ntohs(data->Control.RemoteNumber), 
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber));
            logEvent (desc,RWCString (action));
        }
        else if (!point.isControllable())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " ;
                dout << " Remote: " << ntohs(data->Control.RemoteNumber);
                dout << " Category: " << data->Control.CategoryCode;
                dout << " Point: " << ntohs(data->Control.PointNumber);
                dout << " received from " << getInterfaceName();
                dout << " was not configured receive for control for point " << point.getPointID() << endl;
            }

            desc = getInterfaceName() + RWCString (" control point is not configured to receive controls");
            _snprintf(action,60,"Remote:%d Category:%c Point:%d for pointID %d", 
                      ntohs(data->Control.RemoteNumber), 
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber),
                      point.getPointID());
            logEvent (desc,RWCString (action));
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Control point " ;
                dout << " Remote: " << ntohs(data->Control.RemoteNumber);
                dout << " Category: " << data->Control.CategoryCode;
                dout << " Point: " << ntohs(data->Control.PointNumber);
                dout << " received from " << getInterfaceName();
                dout << " was mapped to non-control point " <<  point.getPointID() << endl;;
            }

            CHAR pointID[20];
            desc = getInterfaceName() + RWCString (" control point is incorrectly mapped to point ") + RWCString (ltoa(point.getPointID(),pointID,10));
            _snprintf(action,60,"Remote:%d Category:%c Point:%d", 
                      ntohs(data->Control.RemoteNumber), 
                      data->Control.CategoryCode,
                      ntohs(data->Control.PointNumber));
            logEvent (desc,RWCString (action));
        }
        retVal = !NORMAL;
    }

    return retVal;
}


int CtiFDR_ACS::processTimeSyncMessage(CHAR *aData)
{
    int retVal = NORMAL;
    CtiPointDataMsg     *pData;
    ACSInterface_t  *data = (ACSInterface_t*)aData;
    RWTime              timestamp;
    RWCString           desc;
    RWCString               action;

    timestamp = ForeignToYukonTime (data->TimeStamp,true);
    if (timestamp == rwEpoch)
    {
        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getInterfaceName() << " time sync request was invalid " <<  RWCString (data->TimeStamp) << endl;
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



RWCString CtiFDR_ACS::ForeignToYukonId (USHORT remote, CHAR category, USHORT point)
{
    CHAR tmpName[STANDNAMLEN+1];

    // put the remote and point number into our point
    _snprintf (tmpName, 21, "R%dC%cP%d",ntohs(remote),category,ntohs(point));

    // set point name to all blanks
    return RWCString (tmpName);
}

int CtiFDR_ACS::YukonToForeignId (RWCString aPointName, USHORT &aRemoteNumber, CHAR &aCategory, USHORT &aPointNumber)
{
	USHORT tmp_remote, tmp_point, tmp_type;
	CHAR tmp_category;
    CHAR pointName[100];

    // put this in a characater buffer
    strcpy (pointName, aPointName);

    // Read the values out of point name
    if (sscanf (pointName, "T%hdR%hdC%cP%hd", &tmp_type, &tmp_remote, &tmp_category, &tmp_point) != 4)
        return (!NORMAL);

	// put these into our values
	aRemoteNumber = htons(tmp_remote);
	aPointNumber = htons(tmp_point);
	aCategory = tmp_category;
   	return (NORMAL);
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


USHORT CtiFDR_ACS::YukonToForeignStatus (int aStatus)
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


RWTime CtiFDR_ACS::ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag)
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
    }

    return retVal;
}

RWCString CtiFDR_ACS::YukonToForeignTime (RWTime aTimeStamp)
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
        acsInterface = new CtiFDR_ACS();

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


