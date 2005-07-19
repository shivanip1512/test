/*****************************************************************************
*
*    FILE NAME: fdrinterface.cpp
*
*    DATE: 07/15/2000
*
*    AUTHOR: Matt Fisher
*
*    PURPOSE: Base Class Functions and Interface for Foreign Data
*
*    DESCRIPTION: Profides an interface for all Foreign Data Interfaces
*                 data exchanges.  The Interfaces implement methods to
*                 exchange data with other systems.
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.

*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrinterface.cpp-arc  $
*    REVISION     :  $Revision: 1.18 $
*    DATE         :  $Date: 2005/07/19 22:48:53 $
*    History:
      $Log: fdrinterface.cpp,v $
      Revision 1.18  2005/07/19 22:48:53  alauinger
      Dispatch no longer handles email notifications, removed CtiEmailMsg.  Instead CtiNotifEmailMsg's should be sent to the notification server as a replacement.

      Revision 1.17  2005/02/17 19:02:58  mfisher
      Removed space before CVS comment header, moved #include "yukon.h" after CVS header

      Revision 1.16  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.15  2004/08/30 20:27:54  dsutton
      Updated the RCCS interface to accept different connection and listen sockets
      when the interface is initialized.  A new CPARM was created to define the
      new connection socket number.  This will allow Progress energy to run both
      their RCCS system and their Yukon system on the same cluster

      Revision 1.14  2003/04/24 19:42:50  dsutton
      Added more try catches around the dispatch connection.  Added dispatch
      mutex in spots it was missing.  After an attempted connection I verify it before
      returning.  Capped the queue to dispatch at 10000 and now delete the oldest
      1000 as we go over that cap

      Revision 1.13  2003/04/22 20:50:18  dsutton
      Updated dispatch connections to null after deleting them hoping to catch
      the problem CPL has

      Revision 1.12  2003/03/24 23:00:10  dsutton
      Added some try catch blocks around writing to the dispatch connection.
      Sometimes the connection goes away and there is no cleanup done behind
      it so FDR still thinks its valid.  When he does work on the connection, it throws
      and exception and FDR doesn't recover.

      Revision 1.11  2002/10/14 21:10:55  dsutton
      In the database translation routines, if we failed to hit the database
      we called the load routine again just to get the error code.  Whoops
      The error code is now saved from the original call and printed as needed

      Revision 1.10  2002/10/11 14:24:50  dsutton
      Whoops, new calculation should only be done when the rate is 3600
      or higher

      Revision 1.9  2002/10/11 14:12:49  dsutton
      Updated the db reload methods to handle reload rates
      that are over 3600 seconds.  Anything bigger than 3600 was
      returning the time in GMT instead of the time zone appropriate

      Revision 1.8  2002/09/12 21:33:33  dsutton
      Updated how FDR reconnects to dispatch in the event dispatch has swept
      the leg on the connection.  We now reconnect in one place, receivefromdispatch
      Send to dispatch tries a second time before throwing away the data and continuing.

      Revision 1.7  2002/09/06 18:58:55  dsutton
      Added new functions for connecting and registering with dispatch.  Changed
      the logic in the places I try to read and write to dispatch to check the
      connection validity before and reconnect if there are problems

      Revision 1.6  2002/08/28 16:10:38  cplender
      setBlockingWrites is no more!.

      Revision 1.5  2002/08/06 21:59:56  dsutton
      Programming around the error that happens if the dataset is empty when it is
      returned from the database and shouldn't be.  The interfaces fail the call and
      we now try to reload the db every 60 seconds until successful

      Revision 1.4  2002/05/08 15:34:14  dsutton
      removed the debug levels around the db reload code so it was easier to keep
      track of what caused the reload

      Revision 1.3  2002/04/16 15:58:33  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:56  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


****************************************************************************/
#include "yukon.h"

#include <rw/db/connect.h>
#include <rw/db/reader.h>
#include <rw/db/table.h>
#include <rw/db/result.h>
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/collstr.h>

#include "cparms.h"
#include "dbaccess.h"
#include "ctinexus.h"
#include "resolvers.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_signal.h"

#include "fdrinterface.h"
#include "fdrpoint.h"
#include "mgr_fdrpoint.h"

#include "logger.h"
#include "guard.h"
#include "fdrpointlist.h"


/** local definitions **/
CtiConfigParameters CtiFDRInterface::iConfigParameters;

const CHAR * CtiFDRInterface::KEY_DISPATCH_NAME = "DISPATCH_MACHINE";
const CHAR * CtiFDRInterface::KEY_DEBUG_LEVEL = "_DEBUGLEVEL";

bool isPointIdEqual (CtiFDRPoint* a, void* arg);
bool isTranslationNameEqual (CtiFDRPoint* a, void* arg);


bool isPointIdEqual(CtiFDRPoint* aPoint, void *arg)
{
    LONG  id = *((LONG*)arg);  // Wha tis the ID of the pointI care for!
    BOOL  retVal = false;

    if (aPoint->getPointID() == id)
        retVal = true;

    return retVal;
}

bool isTranslationNameEqual(CtiFDRPoint* aPoint, void *arg)
{
    RWCString name = *((RWCString*)arg);  // Wha tis the ID of the pointI care for!
    BOOL  retVal = false;

    for (int x=0; x < aPoint->getDestinationList().size(); x++)
    {
        if (!(aPoint->getDestinationList()[x].getTranslation().compareTo (name,RWCString::ignoreCase)))
            retVal = true;
    }


    return retVal;
}


// constructors, destructor, operators

CtiFDRInterface::CtiFDRInterface(RWCString & interfaceType)
:   iInterfaceName(interfaceType),
iDebugLevel(0),
iDbReloadReason(NotReloaded),
iQueueFlushRate(1),
iOutboundSendRate(1),
iOutboundSendInterval(0),
iTimeSyncVariation (30),
iUpdatePCTimeFlag(true),
iDebugMode(false)
{
    iDispatchConn = NULL;
    iOutBoundPoints = 0;
}


CtiFDRInterface::~CtiFDRInterface()
{
    iDispatchQueue.clearAndDestroy();
    delete iOutBoundPoints;
    delete iDispatchConn;
}

CtiFDRPointList CtiFDRInterface::getSendToList () const
{
    return iSendToList;
}

CtiFDRPointList &CtiFDRInterface::getSendToList ()
{
    return iSendToList;
}

CtiFDRInterface& CtiFDRInterface::setSendToList (CtiFDRPointList &aList)
{
    iSendToList = aList;
    return *this;
}

CtiFDRPointList CtiFDRInterface::getReceiveFromList () const
{
    return iReceiveFromList;
}

CtiFDRPointList &CtiFDRInterface::getReceiveFromList ()
{
    return iReceiveFromList;
}

CtiFDRInterface& CtiFDRInterface::setReceiveFromList (CtiFDRPointList &aList)
{
    iReceiveFromList =  aList;
    return *this;
}

long CtiFDRInterface::getClientLinkStatusID(RWCString &aClientName)
{
    bool                successful(false);
    CtiFDRPoint *       translationPoint = NULL;
    CtiFDRPoint *       point = NULL;
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false, translatedPoint(false);
    RWCString           controlDirection;
    long                retID=0;
    RWDBStatus          listStatus;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(RWCString (FDR_SYSTEM),
                                                       RWCString (FDR_INTERFACE_LINK_STATUS));

        listStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( listStatus.errorCode() == (RWDBStatus::ok))
        {
            // get iterator on list
            CtiFDRManager::CTIFdrPointIterator  myIterator(pointList->getMap());
            int x;

            for ( ; myIterator(); )
            {
                translationPoint = myIterator.value();
                for (x=0; x < translationPoint->getDestinationList().size(); x++)
                {
                    RWCString tempString1,tempString2;
                    RWCTokenizer nextTranslate(translationPoint->getDestinationList()[x].getTranslation());
                    if (!(tempString1 = nextTranslate(";")).isNull())
                    {
                        // this contains Interface:name of the interface
                        RWCTokenizer nextTempToken(tempString1);

                        // do not care about the first part
                        nextTempToken(":");

                        tempString2 = nextTempToken(":");

                        // now we have a name
                        if ( !tempString2.isNull() )
                        {
                            if (!tempString2.compareTo (aClientName,RWCString::ignoreCase))
                            {
                                retID = translationPoint->getPointID();

                                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Point ID " << translationPoint->getPointID();
                                    dout << " defined as " << aClientName << "'s link status point" << endl;
                                }
                            }
                        }
                    }
                }
            }   // end for interator
            delete pointList;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") getClientStatusID " << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") getClientStatusID UNKNOWN (...)" << endl;
    }

    return retID;
}

/*************************************************
* Function Name: CtiFDRInterface::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRInterface::init( void )
{
    // only need to register outbound points
    iOutBoundPoints = new CtiFDRManager(iInterfaceName, RWCString(FDR_INTERFACE_SEND));
    iOutBoundPoints->loadPointList();

    if ( !readConfig() )
    {
        return FALSE;
    }

    // create a dispatch thread object
    // this thread knows how to handle messages from dispatch
    iThreadFromDispatch = rwMakeThreadFunction(*this, &CtiFDRInterface::threadFunctionReceiveFromDispatch);
    iThreadToDispatch = rwMakeThreadFunction(*this, &CtiFDRInterface::threadFunctionSendToDispatch);

    iThreadDbChange = rwMakeThreadFunction(*this,
                                           &CtiFDRInterface::threadFunctionReloadDb);
    return TRUE;
}

/************************************************************************
* Function Name: CtiFDRInterface::setInterfaceName(RWCString &)
*
* Description: sets the name of the Interface.  This set by the
*              constructor and this setter should not be needed.
*
*************************************************************************
*/
CtiFDRInterface & CtiFDRInterface::setInterfaceName(RWCString & aInterfaceName)
{
    iInterfaceName = aInterfaceName;

    return *this;
}

/************************************************************************
* Function Name: CtiFDRInterface::getInterfaceName()
*
* Description: returns the name of the Interface.  This set by the
*              implementing class.
*
*************************************************************************
*/
RWCString & CtiFDRInterface::getInterfaceName(void)
{
    return iInterfaceName;
}

/************************************************************************
* Function Name: CtiFDRInterface::getDbReloadReason()
*
* Description: retrieves the current level of the db change that has occurred
*
*************************************************************************
*/
FDRDbReloadReason CtiFDRInterface::getDbReloadReason() const
{
    return iDbReloadReason;
}

/************************************************************************
* Function Name: CtiFDRInterface::setDbChangeLevel(DbChangeLevel)
*
* Description: set the current level of change
*
*************************************************************************
*/
CtiFDRInterface &CtiFDRInterface::setDbReloadReason(FDRDbReloadReason aReason)
{
    iDbReloadReason = aReason;
    return *this;
}

/************************************************************************
* Function Name: CtiFDRInterface::isInterfaceInDebugMode()
*
* Description: this is true when a debugging is needed
*
*************************************************************************
*/
BOOL CtiFDRInterface::isInterfaceInDebugMode() const
{
    return iDebugMode;
}

/************************************************************************
* Function Name: CtiFDRInterface::setInterfaceDebugMode(BOOL)
*
* Description:
*
*************************************************************************
*/
void CtiFDRInterface::setInterfaceDebugMode(const BOOL aChangeFlag)
{
    iDebugMode = aChangeFlag;
}


/************************************************************************
* Function Name: CtiFDRInterface::getReloadRate()
*
* Description: rate in seconds the database should be reloaded
*
*************************************************************************
*/
int CtiFDRInterface::getReloadRate () const
{
    return iReloadRate;
}
/************************************************************************
* Function Name: CtiFDRInterface::setReloadRate(aRate)
*
* Description: set rate in seconds the database should be reloaded
*
*************************************************************************
*/
CtiFDRInterface& CtiFDRInterface::setReloadRate (INT aRate)
{
    iReloadRate = aRate;
    return *this;
}

/************************************************************************
* Function Name: CtiFDRInterface::getOutboundSendRate()
*
* Description:
*
*************************************************************************
*/
int CtiFDRInterface::getOutboundSendRate () const
{
    return iOutboundSendRate;
}
/************************************************************************
* Function Name: CtiFDRInterface::setOutboundSendRate(aRate)
*
* Description:
*
*************************************************************************
*/
CtiFDRInterface& CtiFDRInterface::setOutboundSendRate (INT aRate)
{
    iOutboundSendRate = aRate;
    return *this;
}
/************************************************************************
* Function Name: CtiFDRInterface::getOutboundSendInterval()
*
* Description:
*
*************************************************************************
*/
int CtiFDRInterface::getOutboundSendInterval () const
{
    return iOutboundSendInterval;
}
/************************************************************************
* Function Name: CtiFDRInterface::setOutboundSendRate(aRate)
*
* Description:
*
*************************************************************************
*/
CtiFDRInterface& CtiFDRInterface::setOutboundSendInterval (INT aInterval)
{
    iOutboundSendInterval = aInterval;
    return *this;
}
/************************************************************************
* Function Name: CtiFDRInterface::getQueueFlushRate()
*
* Description: rate in seconds the database should be reloaded
*
*************************************************************************
*/
int CtiFDRInterface::getQueueFlushRate () const
{
    return iQueueFlushRate;
}
/************************************************************************
* Function Name: CtiFDRInterface::setQueueFlushRate(aRate)
*
* Description: set rate in seconds the dispatch queue to be flushed
*
*************************************************************************
*/
CtiFDRInterface& CtiFDRInterface::setQueueFlushRate (INT aRate)
{
    iQueueFlushRate = aRate;
    return *this;
}



int CtiFDRInterface::getTimeSyncVariation() const
{
    return iTimeSyncVariation;
}

CtiFDRInterface& CtiFDRInterface::setTimeSyncVariation (INT aVariation)
{
    iTimeSyncVariation = aVariation;
    return *this;
}

BOOL CtiFDRInterface::shouldUpdatePCTime() const
{
    return iUpdatePCTimeFlag;
}

void CtiFDRInterface::setUpdatePCTimeFlag(const BOOL aChangeFlag)
{
    iUpdatePCTimeFlag = aChangeFlag;
}


/************************************************************************
* Function Name: CtiFDRInterface::run()
*
* Description: This Starts the interface
*
*
*************************************************************************
*/
BOOL CtiFDRInterface::run( void )
{
    {
        CtiLockGuard<CtiMutex> guard(iDispatchMux);
        connectWithDispatch();
    }

    // start our dispatch distibution thread
    iThreadFromDispatch.start();
    iThreadToDispatch.start();
    iThreadDbChange.start();

    registerWithDispatch();
    return TRUE;
}


/************************************************************************
* Function Name: CtiFDRInterface::stop()
*
* Description: This Stops the interface
*
*
*************************************************************************
*/
BOOL CtiFDRInterface::stop( void )
{
    // tell dispatch we are shutting down
    disconnect( );
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "disconnect( );" << endl;
        dout << "attemping to cancel FromDispatchThread;" << endl;
    }

    iThreadDbChange.requestCancellation();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Completed cancel threadFunctionReloadDb;" << endl;
    }

    iThreadFromDispatch.requestCancellation();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Completed cancel threadFunctionFromDispatch;" << endl;
    }

    iThreadToDispatch.requestCancellation();
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Completed cancel threadFunctionToDispatch;" << endl;
        dout << "calling ShutdownConnection();" << endl;
    }

    // this is throwing an exception sometimes, I'm cheating since its only shutdown
    try
    {
       iDispatchConn->ShutdownConnection();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "completed ShutdownConnection();" << endl;
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Exception on shutdown " << endl;
    }

    //
    // FIXFIXFIX  - may need to add exception handling here
    //

    return TRUE;
}

BOOL CtiFDRInterface::connectWithDispatch()
{
    BOOL retVal = TRUE;
    try
    {
        if (iDispatchConn == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Attempting to connect to dispatch at " << iDispatchMachine << " for " << getInterfaceName() << endl;
            }
            iDispatchConn = new CtiConnection(VANGOGHNEXUS, iDispatchMachine);

            if (iDispatchConn->verifyConnection() != NORMAL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Attempt to reconnect to dispatch at " << iDispatchMachine;
                    dout << " for " << getInterfaceName() << " failed.  Attempting again" << endl;
                }
                delete iDispatchConn;
                iDispatchConn=NULL;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Attempt to connect to dispatch at " << iDispatchMachine << " for " << getInterfaceName() << " failed by exception " << endl;
        }
        retVal = FALSE;
        if (iDispatchConn != NULL)
        {
            delete iDispatchConn;
            iDispatchConn = NULL;
        }
    }

    return retVal;
}

BOOL CtiFDRInterface::registerWithDispatch()
{
    CtiMessage *    pMsg;
    RWCString       regStr("FDR" + iInterfaceName);

    // register who we are
    pMsg = new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE);
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Registering:  " << regStr << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    sendMessageToDispatch( pMsg );
    sendPointRegistration();
    return TRUE;
}

/************************************************************************
* Function Name: CtiFDRInterface::reloadTranslationList()
*
* Description: Deletes Send and Receive Point collection and calls
*              loadTranslationList().
*
*************************************************************************
*/
bool CtiFDRInterface::reloadTranslationLists()
{
    bool retCode = true;

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Reloading translation list: reloadTranslationList()" << endl;
    }

    // reset status associated with clients on each reload in case the
    // point has been added  that watches the link
    setCurrentClientLinkStates();
    return loadTranslationLists();
}

void CtiFDRInterface::setCurrentClientLinkStates()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") Default setCurrentClientLinkStates called " << endl;
    }
}
/************************************************************************
* Function Name: CtiFDRInterface::getCparmValueAsString(RWCString key)
*
* Description: Uses the Static CParm  object to get a parameter base on
*              the key value.  Returns a null string if it does not exist.
*
*************************************************************************
*/
RWCString CtiFDRInterface::getCparmValueAsString(RWCString key)
{
    RWCString   myTempStr("");

    // invokes cparm method
    if (iConfigParameters.isOpt(key))
    {
        // string existed so load it
        myTempStr = iConfigParameters.getValueAsString(key);
    }

    return myTempStr;
}


/************************************************************************
* Function Name: CtiFDRInterface::readConfig(  )
*
* Description: loads base class config information.
*              (Dispatch Name and FDR Debug level)
*
*************************************************************************
*/
int CtiFDRInterface::readConfig( void )
{
    int         successful = TRUE;
    RWCString   tempStr;
    char        *eptr;

    tempStr = getCparmValueAsString(KEY_DISPATCH_NAME);
    if (tempStr.length() > 0)
    {
        iDispatchMachine = tempStr;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " DISPATCH_MACHINE not defined, defaulting to local 127.0.0.1 " << endl;
        }
        iDispatchMachine = RWCString ("127.0.0.1");
    }

    // make name based on interface for debug level: FDR_xxxx_DEBUG_LEVEL
    RWCString myKeyName("FDR_" + iInterfaceName + KEY_DEBUG_LEVEL);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "** Loading Debug Level: " << myKeyName << endl;
    }


    tempStr = getCparmValueAsString(myKeyName);
    if (tempStr.length() > 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " Loaded Debug Level Text " << tempStr << endl;
        }
        iDebugLevel = strtoul(tempStr, &eptr, 16);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " Loaded Debug Level " << myKeyName << " Value is: " << iDebugLevel << endl;
        }

    }

    return successful;
}

/************************************************************************
* Function Name: CtiFDRInterface::sendPointRegistration(  )
*
* Description: registers all points that are sent TO the other system.
*
*
*************************************************************************
*/

bool CtiFDRInterface::sendPointRegistration( void )
{
    CtiMultiMsg             *multiMsg;
    CtiPointRegistrationMsg *ptRegMsg=NULL;

    if (iOutBoundPoints == 0 || iDispatchConn == NULL)
    {
        // not started correctly
        return FALSE;
    }

    try
    {

        if (iOutBoundPoints->entries() > 0)
        {
            buildRegistrationPointList (&ptRegMsg);

            if (ptRegMsg != NULL)
            {
                multiMsg = new CtiMultiMsg( );

                // debug printing
                //            regStr  = "FDR - \"" + iinterfaceType + "\" module - " + "destination \"" + idestination + "\" - ";
                //            regStr += RWTime( ).asString( );
                //         CtiLockGuard<CtiLogger> doubt_guard(dout);
                //            dout << regStr << endl;

                //multiMsg->insert( new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE) );
                multiMsg->insert( ptRegMsg );

                // FIXFIXFIX : may need to had failure and delete memory
                sendMessageToDispatch( multiMsg );
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error building point registration list for dispatch for " << getInterfaceName() << endl;
            }
        }
    }

    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getInterfaceName() << "'s sendRegistration failed by exception." << endl;
    }

    return TRUE;
}


void CtiFDRInterface::buildRegistrationPointList (CtiPointRegistrationMsg **aMsg)
{
    CtiFDRPoint             *pFdrPoint = NULL;
    CtiPointRegistrationMsg *testMsg;

    // we have some points to send
    *aMsg = new CtiPointRegistrationMsg( REG_TAG_MARKMOA );
    testMsg = *aMsg;

    // get iterator on outbound list
    CtiFDRManager::CTIFdrPointIterator  myIterator(iOutBoundPoints->getMap());

    for ( ; myIterator(); )
    {
        pFdrPoint = myIterator.value();

        // add this point ID to register
        testMsg->insert( pFdrPoint->getPointID());
    }
}
/************************************************************************
* Function Name: CtiFDRInterface::disconnect(  )
*
* Description: sends a disconnect message to Dispatch. Used for shutdown.
*
*
*************************************************************************
*/
void CtiFDRInterface::disconnect( void )
{
    sendMessageToDispatch( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15 ) );
}


/************************************************************************
* Function Name: CtiFDRInterface::receiveFromDispatchThread(  )
*
* Description: thread that receives messages from Dispatch
*              and sends to foreignSystem method.
*
*************************************************************************
*/
void CtiFDRInterface::threadFunctionReceiveFromDispatch( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    CtiMessage      *incomingMsg=NULL;
    int x,cnt=0;
    static int cnt2=0;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Initializing CtiFDRInterface::threadFunctionReceiveFromDispatch"  << endl;
        }


        for ( ; ; )
        {

            //  while i'm not getting anything
            do
            {
                pSelf.serviceCancellation( );

                if (iDispatchConn != NULL)
                {
                    // unfortunately, the connection is not always valid even if its not zero
                    try
                    {
                        if ( (iDispatchConn->verifyConnection()) == NORMAL )
                        {
                            incomingMsg = iDispatchConn->ReadConnQue( 1000 );
                        }
                        else
                        {
                            pSelf.serviceCancellation( );

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << getInterfaceName() << "'s connection to dispatch failed verification.  Attempting to reconnect" << endl;
                            }
                            // we're assuming that the send to dispatch will catch the problem
                            {
                                CtiLockGuard<CtiMutex> guard(iDispatchMux);
                                delete iDispatchConn;
                                iDispatchConn=NULL;

                                pSelf.serviceCancellation( );
                                // possible two minute gap here
                                connectWithDispatch();
                            }
                            pSelf.serviceCancellation( );
                            logEvent (RWCString (getInterfaceName() + RWCString ("'s connection to dispatch has been restarted")),RWCString());
                            registerWithDispatch();
                        }
                    }
                    catch (...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << getInterfaceName() << "'s connection to dispatch failed by exception in threadFunctionReceiveFromDispatch." << endl;
                        }
                        {
                            CtiLockGuard<CtiMutex> guard(iDispatchMux);
                            delete iDispatchConn;
                            iDispatchConn=NULL;

                            if (incomingMsg != NULL)
                            {
                                delete incomingMsg;
                                incomingMsg = NULL;
                            }
                        }
                    }
                }
                else
                {
                    pSelf.serviceCancellation( );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << getInterfaceName() << "'s connection to dispatch is not initialized.  Attempting to reconnect" << endl;
                    }
                    // we're assuming that the send to dispatch will catch the problem
                    {
                        CtiLockGuard<CtiMutex> guard(iDispatchMux);
                        // possible two minute gap here
                        connectWithDispatch();
                    }
                    pSelf.serviceCancellation( );
                    logEvent (RWCString (getInterfaceName() + RWCString ("'s connection to dispatch has been restarted")),RWCString());
                    registerWithDispatch();
                }

            } while ( incomingMsg == NULL );


            switch (incomingMsg->isA())
            {

                case MSG_DBCHANGE:
                    // db change message reload if type if point
                    if ( ((CtiDBChangeMsg*)incomingMsg)->getDatabase() == ChangePointDb)
                    {
                        //FIXFIXFIX code here to reload and re-register point list

                        // let Inherited class know we had a change
                        setDbReloadReason(Signaled);
                    }
                    break;

                case MSG_COMMAND:
                    {
                        // we will handle this message for other classes
                        CtiCommandMsg* cmd = (CtiCommandMsg*)incomingMsg;

                        switch ( cmd->getOperation())
                        {
                            case (CtiCommandMsg::Shutdown):
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " FDR received a shutdown message from somewhere- Ignoring!!" << endl;
                                }
                                break;

                            case (CtiCommandMsg::AreYouThere):
                                // echo back the same message - we are here
                                sendMessageToDispatch(cmd->replicateMessage());

    //                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " FDR" << getInterfaceName() << " has been pinged by dispatch" << endl;
                                }
                                break;

                            default:
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " FDR received a unknown Command message- " << endl;
                                }
                                break;

                        }
                    }

                    break;

                case MSG_MULTI:
                    //  seperate it out into distinct CtiMessages, so the translation
                    //  function can be a simple switch block
                    for ( x = 0; x < ((CtiMultiMsg *)incomingMsg)->getData( ).entries( ); x++ )
                    {
                        // for now, send on only the pointdata messages
                        if (((CtiMessage *)((CtiMultiMsg *)incomingMsg)->getData()[x])->isA() == MSG_POINTDATA)
                        {
                            //  send the current node change to foreign system
                            sendMessageToForeignSys((CtiMessage *)((CtiMultiMsg *)incomingMsg)->getData()[x]);
                        }

                    }
                    break;
                case MSG_POINTDATA:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " received a single point data message type- " << endl;
                        }
                        sendMessageToForeignSys(incomingMsg);
                        break;
                    }
                default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " FDR received a unknown message type- " << incomingMsg->isA() << endl;
                    }
                    break;


            }
            // get rid of the message we received
            delete incomingMsg;
            incomingMsg = NULL;
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "cancellation" << endl;
        return;
    }

    // catch whatever is left
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Fatal Error:  receiveFromDispatchThread for " << iInterfaceName << " is dead! " << endl;
        return;
    }
}

/************************************************************************
* Function Name: CtiFDRInterface::threadFunctionSendToDispatch(  )
*
* Description: thread that sends messages to Dispatch
*
*************************************************************************
*/
void CtiFDRInterface::threadFunctionSendToDispatch( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    CtiMessage      *incomingMsg=NULL;
    int cnt,entries;
    RWTime checkTime;
    bool hasEntries = true;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Initializing CtiFDRInterface::threadFunctionSendToDispatch " << endl;
        }

        // create the muli
        iDispatchQueue.resize(1000);

        checkTime = RWTime() + getQueueFlushRate();

        for ( ; ; )
        {
            pSelf.serviceCancellation( );

            // check the entry list
            entries = iDispatchQueue.entries();

            /***************************
            * no more than 500 at a time or 5 seconds, whichever comes first
            ****************************
            */
            cnt=0;
            if (( entries >= 500) || (RWTime() > checkTime))
            {

                // make sure we have something before trying to send
                if (entries > 0)
                {
                    CtiMultiMsg       *pMultiData = NULL;
                    pMultiData = new CtiMultiMsg;

                    for (int x=0; x < entries ;x++)
                    {
                        cnt++;
                        incomingMsg = iDispatchQueue.getQueue();
                        pMultiData->getData( ).insert( incomingMsg );

                        // if divisible by 500, send it and start again
                        if (cnt % 500 == 0)
                        {
//                            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Sending batch of " << cnt << " entries to dispatch from " << getInterfaceName() << endl;
                            }

                            sendMessageToDispatch( pMultiData );
                            cnt=0;
                            pMultiData = new CtiMultiMsg;
                        }
                    }

                    // reset the counter and send the last of them
//                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Sending batch of " << cnt << " entries to dispatch from " << getInterfaceName() << endl;
                    }
                    cnt=0;

                    sendMessageToDispatch( pMultiData );
                }

                checkTime = RWTime() + getQueueFlushRate();
            }
            else
            {
                pSelf.sleep (100);
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION of threadFunctionSendToDispatch for " << iInterfaceName << endl;
        return;
    }

    // catch whatever is left
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Fatal Error:  threadFunctionSendToDispatch for " << iInterfaceName << " is dead! " << endl;
        return;
    }
}


/**************************************************************************
* Function Name: CtiFDRInterface::reloadDbThreadFunction( void )
*
* Description: thread that loads the database if needed
*
***************************************************************************
*/
void CtiFDRInterface::threadFunctionReloadDb( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0;
    RWTime timeNow;
    RWTime hourstart;
    RWTime refreshTime;

    if (getReloadRate() > 3600)
    {
        hourstart = RWTime(timeNow.seconds() - (timeNow.seconds() % 3600)); // align to the hour.
        refreshTime=RWTime(hourstart.seconds() - ((hourstart.hour()* 3600) % getReloadRate()) + getReloadRate());
    }
    else
    {
        refreshTime = RWTime (timeNow.seconds() - (timeNow.seconds() % getReloadRate()) + getReloadRate());
    }

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Initializing CtiFDRInterface::threadFunctionReloadDb " << endl;
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (100);

            timeNow = RWTime();

            if ((getDbReloadReason() != NotReloaded) || (timeNow >= refreshTime))
            {
                if ((getDbReloadReason() != NotReloaded))
                {
                    // reload point list
//                    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " - Db change reloading points for Interface - " << getInterfaceName() << endl;
                    }
                }
                else
                {
                    // reload point list
                    setDbReloadReason(Periodic);
//                    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " - Periodic timer expired -  reloading " << getInterfaceName() << endl;
                    }
                }

                // if we successfully reload
                if (reloadTranslationLists())
                {
                    // do this whenever we reload the database
                    reRegisterWithDispatch();

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " - Re-registered with dispatch - " << getInterfaceName() << endl;
                    }

                    // wait until the state goes back to normal before resetting and continuing
                    setDbReloadReason(NotReloaded);

                    // reset refresh time
                    if (getReloadRate() > 3600)
                    {
                        hourstart = RWTime(timeNow.seconds() - (timeNow.seconds() % 3600)); // align to the hour.
                        refreshTime=RWTime(hourstart.seconds() - ((hourstart.hour()* 3600) % getReloadRate()) + getReloadRate());
                    }
                    else
                    {
                        refreshTime = RWTime (timeNow.seconds() - (timeNow.seconds() % getReloadRate()) + getReloadRate());
                    }
                }
                else
                {
                    {
                        // sleep a second and try again
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " - Error reloading points for " << getInterfaceName() << " will reset periodic timer to 60 seconds" << endl;
                    }
                    setDbReloadReason(NotReloaded);
                    refreshTime = timeNow - (timeNow.seconds() % 60) + 60;
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CANCELLATION of CtiFDRInterface::threadFunctionReloadDb " << endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Fatal Error:  threadFunctionReloadDb in CtiFDRInterfacet is dead! " << endl;
    }
}

bool CtiFDRInterface::logEvent( RWCString &aDesc, RWCString &aAction, bool aSendImmediatelyFlag )
{
    CtiSignalMsg  *     eventLog    = NULL;
    eventLog = new CtiSignalMsg(0,
                                0,
                                aDesc,
                                aAction,
                                GeneralLogType);

    if (aSendImmediatelyFlag)
    {
        return (sendMessageToDispatch(eventLog));
    }
    else
    {
        return (queueMessageToDispatch(eventLog));
    }
}


bool CtiFDRInterface::sendMessageToDispatch( CtiMessage *aMessage )
{
    bool retVal=false;
    int attemptReturned;

    {
        // lock the semaphore out here
        CtiLockGuard<CtiMutex> guard(iDispatchMux);
        attemptReturned = attemptSend (aMessage);
    }

    // log and try again
    if (attemptReturned == ConnectionFailed)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " The attempt to write point data from " << getInterfaceName() << " to dispatch has failed. Trying again " << endl;
        }
        Sleep (1000);

        {
            CtiLockGuard<CtiMutex> guard(iDispatchMux);
            attemptReturned = attemptSend (aMessage);
        }
        if (attemptReturned == ConnectionFailed)
        {
            {
                // lock the semaphore out here
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Connection to dispatch for " << getInterfaceName() << " has failed.  Data will be lost " << endl;
            }
            retVal = false;
            delete aMessage;
        }
        else
        {
            if (attemptReturned == ConnectionOkWriteOk)
            {
                retVal = true;
            }
            else
            {
                retVal = false;
            }
        }
    }
    else
    {
        if (attemptReturned == ConnectionOkWriteOk)
        {
            retVal = true;
        }
        else
        {
            retVal = false;
        }
    }
    return retVal;
}
/************************************************************************
* Function Name: CtiFDRInterface::sendMessageToDispatch( CtiMessage *)
*
* Description: used to send data to Dispatch
*
*
*************************************************************************
*/
int CtiFDRInterface::attemptSend( CtiMessage *aMessage )
{
    int returnValue = ConnectionFailed;
    bool retVal=false;

    // unfortunately, the connection is not always valid even if its not zero
    try
    {
        // make sure the connection hasn't been deleted first
        if (iDispatchConn != NULL)
        {
            if ( (iDispatchConn->verifyConnection()) == NORMAL )
            {
                // data is eaten no matter what
                retVal = iDispatchConn->WriteConnQue( aMessage );

                if (retVal == false)
                {
                    returnValue = ConnectionOkWriteFailed;
                }
                else
                {
                    returnValue = ConnectionOkWriteOk;
                }
            }
        }
    }
    catch (...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getInterfaceName() << "'s connection to dispatch failed by exception in attemptSend()." << endl;
        }
    }

    return returnValue;
}
/************************************************************************
* Function Name: CtiFDRInterface::queueMessageToDispatch( CtiMessage *)
*
* Description: used to send data to Dispatch
*
*
*************************************************************************
*/
bool CtiFDRInterface::queueMessageToDispatch( CtiMessage *aMessage )
{
    bool retVal = true;

    /*************************
    * completely arbitrary for now but
    * if we go over 10000 things on the queue, we remove
    * the oldest 1000 before adding anymore
    **************************
    */
    if (iDispatchQueue.entries() > 10000)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Dispatch queue is over the 10,000 point threshold for " << getInterfaceName() << ". Purging oldest 1000 entries " << endl;
        }
        CtiMessage      *oldMsg=NULL;

        for (int x=0; x < 1000 ;x++)
        {
            oldMsg = iDispatchQueue.getQueue();
            delete oldMsg;
        }
    }
    iDispatchQueue.putQueue (aMessage);
    return retVal;
}

/************************************************************************
* Function Name: CtiFDRInterface::reRegisterWithDispatch( )
*
* Description: used to reload the outbound list and re-register it with dispatch
*
*************************************************************************
*/
INT CtiFDRInterface::reRegisterWithDispatch(  )
{
    INT retVal=NORMAL;

    CtiFDRManager   *tmpList = new CtiFDRManager(iInterfaceName, RWCString(FDR_INTERFACE_SEND));

    // try and reload the outbound list
    if (tmpList->loadPointList().errorCode() == (RWDBStatus::ok))
    {
        // destroy the old one and set it to the new one
        delete iOutBoundPoints;
        iOutBoundPoints = NULL;
        iOutBoundPoints = tmpList;
    }
    else
    {
        retVal = !NORMAL;
    }

    sendPointRegistration();
    return retVal;
}


/************************************************************************
* Function Name: CtiFDRInterface::updatePointByIdInList()
*
* Description: Finds a specific pointid in the list and returns a pointer to  it
*
*************************************************************************
*/
bool CtiFDRInterface::updatePointByIdInList(CtiFDRPointList &aList,
                                            CtiPointDataMsg *aMessage)
{
    bool               foundFlag= false;
    CtiFDRPoint        *point=NULL;

    // lock the list
    CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());

    // check if the point id exists
    CtiHashKey key(aMessage->getId());
    point = aList.getPointList()->getMap().findValue(&key);

    if ( aList.getPointList()->getMap().entries() == 0 ||  point == NULL)
    {
        foundFlag = false;
    }
    else
    {
        // reset reason for reload in the list itself (order here is important !!!!!!)
        point->setValue (aMessage->getValue());
        point->setQuality (aMessage->getQuality());
        point->setLastTimeStamp(aMessage->getTime());
        foundFlag=true;
    }
    return foundFlag;
}

/************************************************************************
* Function Name: CtiFDRInterface::findPointIdInList()
*
* Description: Finds a specific pointid in the list and returns a pointer to  it
*
*************************************************************************
*/
bool CtiFDRInterface::findPointIdInList(long aPointId,
                                        CtiFDRPointList &aList,
                                        CtiFDRPoint &aPoint)
{
    bool               foundFlag= false;
    CtiFDRPoint        *point=NULL;

    // lock the list
    CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());

    // check if the point id exists
    CtiHashKey key(aPointId);
    point = aList.getPointList()->getMap().findValue(&key);

    if ( aList.getPointList()->getMap().entries() == 0 ||  point == NULL)
    {
        foundFlag = false;
    }
    else
    {
        // copy into return pointer
        aPoint = *point;
        foundFlag=true;
    }
    return foundFlag;
}

/************************************************************************
* Function Name: CtiFDRInterface::findTranslationNameInList()
*
* Description: Finds a specific translation name in the list and returns a pointer to  it
*
*************************************************************************
*/
bool CtiFDRInterface::findTranslationNameInList(RWCString aTranslationName,
                                                CtiFDRPointList &aList,
                                                CtiFDRPoint &aPoint)
{
    bool               foundFlag= false;
    int                index =0;
    CtiFDRPoint        *point=rwnil;

    // lock the list
    CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
    point = aList.getPointList()->find(isTranslationNameEqual, (void *) &aTranslationName);

    if (point == rwnil)
    {
        foundFlag = false;
    }
    else
    {
        // reset reason for reload
        aPoint = *point;
        foundFlag=true;
    }
    return foundFlag;
}

