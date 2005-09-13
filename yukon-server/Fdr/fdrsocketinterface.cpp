/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrsocketinterface.cpp
*
*    DATE: 05/25/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrsocketinterface.cpp-arc  $
*    REVISION     :  $Revision: 1.10 $
*    DATE         :  $Date: 2005/09/13 20:47:23 $
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
*     $Log: fdrsocketinterface.cpp,v $
*     Revision 1.10  2005/09/13 20:47:23  tmack
*     In the process of working on the new ACS(MULTI) implementation, the following changes were made:
*
*     - added the ntohieeef() and htonieeef() methods that used to be in a subclass
*
*
*
*    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"

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
    iConnectPortNumber (aPortNumber),
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

int CtiFDRSocketInterface::getConnectPortNumber () const
{
    return iConnectPortNumber;
}

CtiFDRSocketInterface& CtiFDRSocketInterface::setConnectPortNumber (int aPort)
{
    iConnectPortNumber = aPort;
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
                dout << RWTime() << " Point registration response tag set, point " << localMsg->getId() << " will not be sent to " << getInterfaceName() << endl;
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

/** Network to Host IEEE Float
 *  Convert the order of an IEEE floating point from network to host.
 */
FLOAT CtiFDRSocketInterface::ntohieeef (LONG NetLong)
{
    union
    {
        FLOAT HostFloat;
        LONG HostLong;
    } HostUnion;

    HostUnion.HostLong = ntohl (NetLong);

    return(HostUnion.HostFloat);
}

/* Host to Network IEEE Float
 * Convert the order of an IEEE floating point from host to network.
 */
LONG CtiFDRSocketInterface::htonieeef (FLOAT  HostFloat) 
{
    union
    {
        FLOAT HostFloat;
        LONG HostLong;
    } HostUnion;

    HostUnion.HostFloat = HostFloat;

    return(htonl (HostUnion.HostLong));
}
