#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrsocketinterface.cpp
*
*    DATE: 05/25/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrsocketinterface.cpp-arc  $
*    REVISION     :  $Revision: 1.7 $
*    DATE         :  $Date: 2004/02/13 20:37:04 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: methods and members of a standard socket interface
*
*    DESCRIPTION: This class implements a point list with built in mutex protection
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrsocketinterface.cpp,v $
      Revision 1.7  2004/02/13 20:37:04  dsutton
      Added a new cparm for ACS interface that allows the user to filter points
      being routed to ACS by timestamp.  The filter is the number of seconds
      since the last update.   If set to zero, system behaves as it always has,
      routing everything that comes from dispatch.  If the cparm is > 0, FDR will
      first check the value and route the point if it has changed.  If the value has
      not changed, FDR will check the timestamp to see if it is greater than or equal
      to the previous timestamp plus the cparm.  If so, route the data, if not, throw
      the point update away.

      Revision 1.6  2003/04/24 19:41:06  dsutton
      Changed a dispatch connection check from isValid to verify per Corey's request

      Revision 1.5  2003/04/22 20:50:52  dsutton
      Added a null check in the sendall points on the dispatch connection

      Revision 1.4  2002/08/06 22:04:23  dsutton
      Programming around the error that happens if the dataset is empty when it is
      returned from the database and shouldn't be.  If our point list had more than
      two entries in it before, we fail the attempt and try again in 60 seconds

      Revision 1.3  2002/04/16 15:58:38  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:58  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

   
      Rev 2.8   12 Mar 2002 10:31:44   dsutton
   added getters/setters and appropriate shutdown calls for the listener object.  Changed sendall points to return on an error and allow the calling thread to loop on the error condition instead of staying in this function forever
   
      Rev 2.7   06 Mar 2002 11:19:16   dsutton
   updated the send all points function to make sure the connection is ok before we try and send everything after dropping out of our registration loop
   
      Rev 2.6   01 Mar 2002 13:30:02   dsutton
   removed a debug line that prints whenever something is received that we don't need.  Because RCCS has to register for everything, it may sometimes get points it doesn't want, causing confusion
   
      Rev 2.5   20 Feb 2002 08:46:16   dsutton
   changed the moa logging to detail debug level and put a try catch block around the buildAndWrite function to keep the thread from dying should there be a problem constructing the outgoing message
   
      Rev 2.4   20 Dec 2001 14:58:36   dsutton
   Added a registration flag to the base socket interface because it may be used by other interfaces eventually.  Default condition is true, currently only RDEX registers. Updated the sendall and sendto functions to check for registration first if it is needed before sending the data
   
      Rev 2.3   14 Dec 2001 17:21:10   dsutton
   changed fdrpointidmap and fdrprotectedmaplist classes references to fdrpoint and fdrpointlist references
   
      Rev 2.2   15 Nov 2001 16:16:40   dsutton
   code for multipliers and an queue for the messages to dispatch along with fixes to RCCS/INET interface. Lazy checkin
   
      Rev 2.1   26 Oct 2001 15:20:30   dsutton
   moving revision 1 to 2.x
   
      Rev 1.2.1.0   26 Oct 2001 14:27:20   dsutton
   unk
   
      Rev 1.2   20 Jul 2001 10:02:10   dsutton
   removed the destination list
   
      Rev 1.1   04 Jun 2001 09:32:12   dsutton
   removed the reload db thread and put it in the base class
   
      Rev 1.0   30 May 2001 16:55:28   dsutton
   Initial revision.
   
      Rev 1.0   10 May 2001 11:16:06   dsutton
   Initial revision.
   
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

#include <stdio.h>
//#include <rw/cstring.h>
//#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

/** include files **/
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrpointlist.h"
#include "fdrsocketconnection.h"
#include "fdrsocketinterface.h"


CtiFDRSocketInterface::CtiFDRSocketInterface(RWCString & interfaceType, int aPortNumber, int aWindow)
: CtiFDRInterface(interfaceType), 
    iPortNumber (aPortNumber),
    iTimestampReasonabilityWindow(aWindow),
    iPointTimeVariation(0),
    iRegistered(true)

{
    iListener = new CtiFDRSocketConnection();
}


CtiFDRSocketInterface::~CtiFDRSocketInterface()
{
    if (iListener->getConnectionStatus () == CtiFDRSocketConnection::Ok)
    {
        shutdownListener();
    }
}

CtiMutex & CtiFDRSocketInterface::getListenerMux ()
{
    return iListenerMux;
}

CtiFDRSocketConnection * CtiFDRSocketInterface::getListener()
{
    return iListener;
}

CtiFDRSocketInterface& CtiFDRSocketInterface::setListener(CtiFDRSocketConnection * aSocket)
{
    iListener = aSocket;
    return *this;
}

void CtiFDRSocketInterface::shutdownListener()
{
    CtiLockGuard<CtiMutex> destGuard(getListenerMux());  
    iListener->closeAndFailConnection();
    delete iListener;
}


int CtiFDRSocketInterface::getPortNumber () const
{
    return iPortNumber;
}
CtiFDRSocketInterface& CtiFDRSocketInterface::setPortNumber (int aPort)
{
    iPortNumber = aPort;
    return *this;
}

int CtiFDRSocketInterface::getPointTimeVariation () const
{
    return iPointTimeVariation;
}
CtiFDRSocketInterface& CtiFDRSocketInterface::setPointTimeVariation (int aTime)
{
    iPointTimeVariation = aTime;
    return *this;
}

int CtiFDRSocketInterface::getTimestampReasonabilityWindow () const
{
    return iTimestampReasonabilityWindow;
}
CtiFDRSocketInterface& CtiFDRSocketInterface::setTimestampReasonabilityWindow (int aWindow)
{
    iTimestampReasonabilityWindow= aWindow;
    return *this;
}

bool CtiFDRSocketInterface::isRegistered ()
{
    return iRegistered;
}

CtiFDRSocketInterface& CtiFDRSocketInterface::setRegistered (bool aFlag)
{
    iRegistered = aFlag;
    return *this;
}

bool CtiFDRSocketInterface::isClientConnectionValid ()
{
    return true;
}

/************************************************************************
* Function Name: CtiFDRInterface::loadTranslationList()
*
* Description: Creates a seperate collection of Status and Analog Point
*              IDs and ACS IDs for translation.
* 
*************************************************************************
*/
bool CtiFDRSocketInterface::loadTranslationLists()
{
    bool retCode = true;

    retCode = loadList(RWCString(FDR_INTERFACE_SEND),getSendToList());

    if (retCode)
    {
        retCode = loadList(RWCString (FDR_INTERFACE_RECEIVE),getReceiveFromList());
    }
    return retCode;
}

/*************************************************
* Function Name: CtiFDRSocketInterface::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRSocketInterface::init( void )
{
    // init the base class
    Inherited::init();    
    iPointTimeVariation = 0;
    return TRUE;
}

/*************************************************
* Function Name: CtiFDRSocketInterface::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDRSocketInterface::run( void )
{                      

    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDRSocketInterface::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDRSocketInterface::stop( void )
{
    Inherited::stop();
    return TRUE;
}


int CtiFDRSocketInterface::sendAllPoints()
{
    CHAR *ptr=NULL;
    int retVal=NORMAL;
    CtiFDRPoint *point=NULL;

    // bad bad bad
    if (iDispatchConn == NULL)
    {
        Sleep (250);
        retVal = FDR_NOT_CONNECTED_TO_DISPATCH;
    }
    else
    {
        // just stay here until the link to dispatch becomes valid
        if ( (iDispatchConn->verifyConnection()) != NORMAL )
        {
            Sleep (250);
            retVal = FDR_NOT_CONNECTED_TO_DISPATCH;
        }
        else
        {
            // check registration
            if (!isRegistered())
            {
                retVal = FDR_CLIENT_NOT_REGISTERED;
                Sleep (250);
            }
            else
            {
                Sleep (2000);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Uploading all requested points to " << getInterfaceName() << endl;
                }

                CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());  
                CtiFDRManager::CTIFdrPointIterator  myIterator(getSendToList().getPointList()->getMap());
                for ( ; myIterator(); )
                {
                    point = myIterator.value();
                    if (!point->isControllable())
                    {
                        if (point->getLastTimeStamp() < RWTime(RWDate(1,1,2001)))
                        {
                            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " PointId " << point->getPointID();
                                dout << " was not sent to " << getInterfaceName() << " because it hasn't been initialized " << endl;
                            }
                        }
                        else
                        {
                            buildAndWriteToForeignSystem(*point);
                        }
                    }
                    else
                    {
                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Control point Id " << point->getPointID();
                            dout << " was not sent to " << getInterfaceName() << " because a database reload triggered the send " << endl;
                        }
                    }
                }
                retVal = NORMAL;
            }
        }
    }
    return retVal;
}

bool CtiFDRSocketInterface::sendMessageToForeignSys ( CtiMessage *aMessage )
{   
    bool retVal = true;
    bool forwardPointData=true;
    CtiPointDataMsg     *localMsg = (CtiPointDataMsg *)aMessage;
    CtiFDRPoint point;

    // if requested, check the timestamp and value to see if we should forward this message
    if (getPointTimeVariation() > 0)
    {
        findPointIdInList (localMsg->getId(), getSendToList(), point);

        // if the values are equal
        if (point.getValue() == localMsg->getValue())
        {
            // check timestamp
            if (point.getLastTimeStamp() + getPointTimeVariation() >= localMsg->getTime())
            {
                forwardPointData = false;
            }
        }
    }

    if (forwardPointData)
    {
        // need to update this in my list always
        updatePointByIdInList (getSendToList(), localMsg);

        // if this is a response to a registration, do nothing
        if (localMsg->getTags() & TAG_POINT_MOA_REPORT)
        {
            findPointIdInList (localMsg->getId(), getSendToList(), point);

            if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " MOA registration tag set, point " << localMsg->getId() << " will not be sent to " << getInterfaceName() << endl;
            }
            retVal = false;
        }
        else
        {
            // see if the point exists;
            retVal = findPointIdInList (localMsg->getId(), getSendToList(), point);

            if (retVal == false)
            {
                if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Translation for point " << localMsg->getId() << " to " << getInterfaceName() << " not found " << endl;;
                }
            }
            else
            {
                /*******************************
                * if the timestamp is less than 01-01-2000 (completely arbitrary number)
                * then dont' route the point because it is uninitialized
                * note: uninitialized points come across as 11-10-1990 
                ********************************
                */
                if (point.getLastTimeStamp() < RWTime(RWDate(1,1,2001)))
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " PointId " << point.getPointID();
                        dout << " was not sent to " << getInterfaceName() << " because it hasn't been initialized " << endl;
                    }
                    retVal = false;
                }
                else
                {
                    // if we haven't registered, don't bother
                    if (isRegistered())
                    {
                        try 
                        {
                            retVal = buildAndWriteToForeignSystem (point);
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << " **** Checkpoint **** building msg error" << endl;
                        }
                    }
                }
            }
        }
    }
   return retVal;
}



