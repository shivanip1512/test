/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrrdex.cpp
*
*    DATE: 03/07/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrrdex.cpp-arc  $
*    REVISION     :  $Revision: 1.15.2.1 $
*    DATE         :  $Date: 2008/11/13 17:23:47 $
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
      Revision 1.15.2.1  2008/11/13 17:23:47  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.15  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.14  2008/09/23 15:14:58  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.13  2008/09/15 21:08:48  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.12  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.11  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.10  2005/12/20 17:17:14  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.9  2005/10/19 16:53:22  dsutton
      Added the ability to set the connection timeout using a cparm.  Interfaces will
      kill the connection if they haven't heard anything from the other system after
      this amount of time.  Defaults to 60 seconds.  Also changed the logging to
      the system log so we don't log every unknown point as it comes in from the
      foreign system.  It will no log these points only if a debug level is set.

      Revision 1.8  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.7  2004/09/29 17:47:47  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

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
#include "yukon.h"

#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
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
int CtiFDR_Rdex::readConfig()
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Rdex port number " << getPortNumber() << endl;
        dout << CtiTime() << " Rdex timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << CtiTime() << " Rdex db reload rate " << getReloadRate() << endl;
        dout << CtiTime() << " Rdex queue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << CtiTime() << " Rdex send rate " << getOutboundSendRate() << endl;
        dout << CtiTime() << " Rdex send interval " << getOutboundSendInterval() << " second(s) " << endl;

        if (isInterfaceInDebugMode())
            dout << CtiTime() << " Rdex running in debug mode " << endl;
        else
            dout << CtiTime() << " Rdex running in normal mode "<< endl;
    }
    return successful;
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
        strcpy (ptr->timestamp,  YukonToForeignTime (aPoint.getLastTimeStamp()).c_str());

        // Moved this out to catch the case where we don't find the destination.
        string translationName = aPoint.getTranslateName(getLayer()->getName());

        if (!ciStringEqual("",translationName))
        {
            switch (aPoint.getPointType())
            {
                case AnalogPointType:
                case CalculatedPointType:
                case PulseAccumulatorPointType:
                case DemandAccumulatorPointType:
                    {
                        ptr->function = htonl (SINGLE_SOCKET_VALUE);

                        strcpy (ptr->value.translation,translationName.c_str());
                        ptr->value.quality = YukonToForeignQuality (aPoint.getQuality());
                        ptr->value.longValue = htonieeef (aPoint.getValue());

                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Analog/Calculated point " << aPoint.getPointID();
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
                            strcpy (ptr->value.translation,translationName.c_str());

                            // check for validity of the status, we only have open or closed for rdex
                            if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                            {
                                delete [] buffer;
                                buffer = NULL;

                                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for Rdex interface to " << getLayer()->getName() << endl;
                                }
                            }
                            else
                            {
                                ptr->control.value = YukonToForeignStatus (aPoint.getValue());

                                 if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                 {
                                     CtiLockGuard<CtiLogger> doubt_guard(dout);
                                     dout << CtiTime() << " Control point " << aPoint.getPointID();
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

                            strcpy (ptr->value.translation,translationName.c_str());
                            ptr->status.quality = YukonToForeignQuality (aPoint.getQuality());

                            // check for validity of the status, we only have open or closed for rdex
                            if ((aPoint.getValue() != OPENED) && (aPoint.getValue() != CLOSED))
                            {
                                delete [] buffer;
                                buffer = NULL;

                                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Point " << aPoint.getPointID() << " State " << aPoint.getValue() << " is invalid for Rdex interface to " << getLayer()->getName() << endl;
                                }
                            }
                            else
                            {
                                ptr->status.value = YukonToForeignStatus (aPoint.getValue());

                                 if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                 {
                                     CtiLockGuard<CtiLogger> doubt_guard(dout);
                                     dout << CtiTime() << " Status point " << aPoint.getPointID();
                                     dout << " queued as " << aPoint.getTranslateName(getLayer()->getName());
                                     if (aPoint.getValue() == OPENED)
                                     {
                                         dout << " state of Open ";
                                     }
                                     else
                                     {
                                         dout << " state of Close ";
                                     }
                                     dout << "to " << getLayer()->getName() << endl;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Destination not found to send update to. Point is misconfigured or registered incorrectly. ID: " << aPoint.getPointID() << endl;
            }
            delete []buffer;
            buffer = NULL;
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
            strcpy (ptr->timestamp, YukonToForeignTime (CtiTime()).c_str());
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Not building heartbeat message, not registered " << endl;
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
                    dout << CtiTime() << " Time sync message received from " << getLayer()->getName() << endl;
                }
                break;
            }
        case SINGLE_SOCKET_NULL:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Heartbeat message received from " << getLayer()->getName() << " at " << string (inet_ntoa(getLayer()->getInBoundConnection()->getAddr().sin_addr)) <<  endl;;
                }
                break;
            }
        default:
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unknown message type " << ntohl (rdex->function) <<  " received from " << getInterfaceName() << endl;
            }
    }

    return retVal;

}

int CtiFDR_Rdex::processRegistrationMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    Rdex_Registration *data = (Rdex_Registration*)aData;
    string           desc;
    string           action;


    getLayer()->setName (string (data->clientName));
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
        strcpy (ptr->timestamp, YukonToForeignTime (CtiTime()).c_str());
        strcpy (ptr->serverName, "YUKON_RDEX");
        Sleep (500);  // this a dangerous way to do this
        if (getLayer()->write (buffer))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Registration acknowledgement to " << getLayer()->getName() << " failed " << endl;
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
                dout << CtiTime() << " " << getLayer()->getName() << " has registered with Yukon" << endl;
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
                    dout << CtiTime() << " Analog point " << translationName;
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Translation for analog point " << translationName;
                        dout << " from " << getLayer()->getName() << " was not found" << endl;
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
                        dout << " from " << getLayer()->getName() << " was mapped incorrectly to non-analog point " << point.getPointID() << endl;
                    }
                    CHAR pointID[20];
                    desc = getInterfaceName() + string (" analog point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Status point " << translationName;
                        dout << " from " << getLayer()->getName()<< " has an invalid state " <<ntohl(data->status.value) << endl;
                    }
                    CHAR state[20];
                    desc = getInterfaceName() + string (" status point received with an invalid state ") + string (itoa (ntohl(data->status.value),state,10));
                    _snprintf(action,60,"%s for pointID %d",
                              translationName.c_str(),
                              point.getPointID());
                    logEvent (desc,string (action));
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
                    dout << CtiTime() << " Status point " << translationName;
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Translation for status point " <<  translationName;
                        dout << " from " << getLayer()->getName() << " was not found" << endl;
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
                        dout << " from " << getLayer()->getName() << " was mapped incorrectly to non-status point " << point.getPointID() << endl;
                    }
                    CHAR pointID[20];
                    desc = getInterfaceName() + string (" status point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Control point " << translationName;
                    dout << " from " << getLayer()->getName()<< " has an invalid control state " <<ntohl(data->control.value) << endl;
                }
                CHAR state[20];
                desc = getInterfaceName() + string (" control point received with an invalid state ") + string (itoa (ntohl(data->control.value),state,10));
                _snprintf(action,60,"%s for pointID %d",
                          translationName.c_str(),
                          point.getPointID());
                logEvent (desc,string (action));
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
                    dout << CtiTime() << " Control point " << translationName;
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Translation for control point " <<  translationName;
                        dout << " from " << getLayer()->getName() << " was not found" << endl;
                    }
                    desc = getInterfaceName() + string (" control point is not listed in the translation table");
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }

            }
            else if (!point.isControllable())
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Control point " << translationName;
                        dout << " received from " << getLayer()->getName();
                        dout << " was not configured receive for control for point " << point.getPointID() << endl;
                    }
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Control point " << translationName;
                        dout << " received from " << getLayer()->getName();
                        dout << " was mapped to non-control point " <<  point.getPointID() << endl;;
                    }
                    CHAR pointID[20];
                    desc = getInterfaceName() + string (" control point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                    _snprintf(action,60,"%s", translationName.c_str());
                    logEvent (desc,string (action));
                }
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



