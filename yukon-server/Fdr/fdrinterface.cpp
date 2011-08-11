/*
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
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 *    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrinterface.cpp-arc  $
 *    REVISION     :  $Revision: 1.35 $
 *    DATE         :  $Date: 2008/11/18 21:50:22 $
 *    History:
 *     $Log: fdrinterface.cpp,v $
 *     Revision 1.35  2008/11/18 21:50:22  tspar
 *     YUK-5013 Full FDR reload changes
 *
 *     Fixed a bad condition check in UpdateBypointid. Smart Pointer retruns true if not null
 *
 *     Revision 1.34  2008/10/15 14:38:23  jotteson
 *     Fixing problems found by static analysis tools.
 *
 *     Revision 1.33  2008/10/13 21:23:30  mfisher
 *     YUK-6574 Server-side logging is slow
 *     Limited localtime()/fstat() checks to twice per day instead of on each write
 *
 *     Revision 1.32  2008/10/02 23:57:15  tspar
 *     YUK-5013 Full FDR reload should not happen with every point
 *
 *     YUKRV-325  review changes
 *
 *     Revision 1.31  2008/09/23 15:14:58  tspar
 *     YUK-5013 Full FDR reload should not happen with every point db change
 *
 *     Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.
 *
 *     Revision 1.30  2008/09/15 21:08:48  tspar
 *     YUK-5013 Full FDR reload should not happen with every point db change
 *
 *     Changed interfaces to handle points on an individual basis so they can be added
 *     and removed by point id.
 *
 *     Changed the fdr point manager to use smart pointers to help make this transition possible.
 *
 *     Revision 1.29  2008/03/20 21:27:14  tspar
 *     YUK-5541 FDR Textimport and other interfaces incorrectly use the boost tokenizer.
 *
 *     Changed all uses of the tokenizer to have a local copy of the string being tokenized.
 *
 *     Revision 1.28  2007/04/10 23:42:09  tspar
 *     Added even more protection against bad input when tokenizing.
 *
 *     Doing a ++ operation on an token iterator that is already at the end will also assert.
 *
 *     Revision 1.27  2007/04/10 23:04:35  tspar
 *     Added some more protection against bad input when tokenizing.
 *
 *     Revision 1.26  2006/09/26 14:02:48  mfisher
 *     fixes for std namespace
 *
 *     Revision 1.25  2006/08/09 05:03:17  tspar
 *     changed maps in macs to not use a pointer as a key, to fix the find() calls.
 *
 *     Revision 1.24  2006/05/23 17:17:43  tspar
 *     bug fix: boost iterator used incorrectly in loop.
 *
 *     Revision 1.23  2006/04/24 14:47:32  tspar
 *     RWreplace: replacing a few missed or new Rogue Wave elements
 *
 *     Revision 1.22  2006/02/17 17:04:31  tspar
 *     CtiMultiMsg:  replaced RWOrdered with vector<RWCollectable*> throughout the tree
 *
 *     Revision 1.21  2006/01/03 20:23:37  tspar
 *     Moved non RW string utilities from rwutil.h to utility.h
 *
 *     Revision 1.20  2005/12/20 17:17:13  tspar
 *     Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
 *
 *     Revision 1.19  2005/09/13 20:43:07  tmack
 *     In the process of working on the new ACS(MULTI) implementation, the following changes were made:
 *
 *     - clean up logging messages (especially those at shutdown)
 *     - change logEvent() parameters to be const (allows passing char*)
 *     - add logNow() method that provides a common string at the front of all dout messages.
 *
 *
 */
#include "precompiled.h"

#include "row_reader.h"

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

#include "utility.h"

using std::string;
using std::endl;

/** local definitions **/
const CHAR * CtiFDRInterface::KEY_DISPATCH_NAME = "DISPATCH_MACHINE";
const CHAR * CtiFDRInterface::KEY_DEBUG_LEVEL = "_DEBUGLEVEL";

bool isPointIdEqual (CtiFDRManager::ptr_type &a, void* arg);
bool isTranslationNameEqual (CtiFDRManager::ptr_type &a, void* arg);


bool isPointIdEqual(CtiFDRManager::ptr_type &aPoint, void *arg)
{
    LONG  id = *((LONG*)arg);  // Wha tis the ID of the pointI care for!
    BOOL  retVal = false;

    if (aPoint->getPointID() == id)
        retVal = true;

    return retVal;
}

bool isTranslationNameEqual(CtiFDRManager::ptr_type &aPoint, void *arg)
{
    string name = *((string*)arg);  // Wha tis the ID of the pointI care for!

    for (int x=0; x < aPoint->getDestinationList().size(); x++)
    {
        if (ciStringEqual(aPoint->getDestinationList()[x].getTranslation(),name))
            return true;
    }
    return false;
}


// constructors, destructor, operators

CtiFDRInterface::CtiFDRInterface(string & interfaceType) :
    iInterfaceName(interfaceType),
    iDebugLevel(0),
    iDbReloadReason(NotReloaded),
    iQueueFlushRate(1),
    iOutboundSendRate(1),
    iOutboundSendInterval(0),
    iTimeSyncVariation (30),
    iUpdatePCTimeFlag(true),
    iDebugMode(false),
    iReloadRate(0),
    iDispatchOK(0)
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

long CtiFDRInterface::getClientLinkStatusID(string &aClientName)
{
    bool                successful(false);
    CtiFDRPointSPtr     translationPoint;
    CtiFDRPointSPtr     point;
    string              translationName;
    bool                foundPoint = false, translatedPoint(false);
    string              controlDirection;
    long                retID=0;

    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(string (FDR_SYSTEM),
                                                       string (FDR_INTERFACE_LINK_STATUS));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            // get iterator on list
            CtiFDRManager::spiterator myIterator = (pointList->getMap()).begin();

            for ( ; myIterator !=  (pointList->getMap()).end(); ++myIterator )
            {
                translationPoint = (*myIterator).second;
                for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
                {
                    string tempString1,tempString2;
                    const string translation = translationPoint->getDestinationList()[x].getTranslation();
                    boost::char_separator<char> sep(";");
                    Boost_char_tokenizer nextTranslate(translation, sep);
                    Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin();

                    if ( tok_iter != nextTranslate.end() )
                    {
                        tempString1 = *tok_iter++;tok_iter++;
                        // this contains Interface:name of the interface
                        boost::char_separator<char> sep1(":");
                        Boost_char_tokenizer nextTempToken(tempString1, sep1);
                        Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin();

                        // do not care about the first part

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
                            // now we have a name
                            if ( !tempString2.empty() )
                            {
                                if (ciStringEqual(tempString2,aClientName))
                                {
                                    retID = translationPoint->getPointID();

                                    if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " Point ID " << translationPoint->getPointID();
                                        dout << " defined as " << aClientName << "'s link status point" << endl;
                                    }
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
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime () << " " << __FILE__ << " (" << __LINE__ << ") getClientStatusID " << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime () << " " << __FILE__ << " (" << __LINE__ << ") getClientStatusID UNKNOWN (...)" << endl;
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
    iOutBoundPoints = new CtiFDRManager(iInterfaceName, string(FDR_INTERFACE_SEND));
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
* Function Name: CtiFDRInterface::setInterfaceName(string &)
*
* Description: sets the name of the Interface.  This set by the
*              constructor and this setter should not be needed.
*
*************************************************************************
*/
CtiFDRInterface & CtiFDRInterface::setInterfaceName(string & aInterfaceName)
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
string & CtiFDRInterface::getInterfaceName(void)
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
    try
    {
        // tell dispatch we are shutting down
        disconnect();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Attemping to cancel threadFunctionReloadDb" << endl;
        }
        iThreadDbChange.requestCancellation();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Attempting to cancel threadFunctionReceiveFromDispatch" << endl;
        }
        iThreadFromDispatch.requestCancellation();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Attempting to cancel threadFunctionSendToDispatch;" << endl;
        }
        iThreadToDispatch.requestCancellation();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Attempting to shutdown connections" << endl;
        }

        // this is throwing an exception sometimes, I'm cheating since its only shutdown
       iDispatchConn->ShutdownConnection();

       iThreadDbChange.join();
       iThreadFromDispatch.join();
       iThreadToDispatch.join();
       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           logNow() << "All threads have joined up" << endl;
       }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Unknown exception on shutdown (CtiFDRInterface::stop)" << endl;
    }

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
                dout << CtiTime() << " Attempting to connect to dispatch at " << iDispatchMachine << " for " << getInterfaceName() << endl;
            }
            iDispatchConn = new CtiConnection(VANGOGHNEXUS, iDispatchMachine);
            iDispatchConn->setName("FDR to Dispatch");

            if (iDispatchConn->verifyConnection() != NORMAL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Attempt to reconnect to dispatch at " << iDispatchMachine;
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
            dout << CtiTime() << " Attempt to connect to dispatch at " << iDispatchMachine << " for " << getInterfaceName() << " failed by exception " << endl;
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
    string       regStr("FDR" + iInterfaceName);

    // register who we are
    pMsg = new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE);
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Registering:  " << regStr << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") Default setCurrentClientLinkStates called " << endl;
    }
}
/************************************************************************
* Function Name: CtiFDRInterface::getCparmValueAsString(string key)
*
* Description: Uses the Static CParm  object to get a parameter base on
*              the key value.  Returns a null string if it does not exist.
*
*************************************************************************
*/
string CtiFDRInterface::getCparmValueAsString(string key)
{
    string   myTempStr("");

    // invokes cparm method
    if (gConfigParms.isOpt(key))
    {
        // string existed so load it
        myTempStr = gConfigParms.getValueAsString(key);
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
    string   tempStr;
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
            dout << CtiTime() << " DISPATCH_MACHINE not defined, defaulting to local 127.0.0.1 " << endl;
        }
        iDispatchMachine = string ("127.0.0.1");
    }

    // make name based on interface for debug level: FDR_xxxx_DEBUG_LEVEL
    string myKeyName("FDR_" + iInterfaceName + KEY_DEBUG_LEVEL);

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
        iDebugLevel = strtoul(tempStr.c_str(), &eptr, 16);
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
                //            regStr += CtiTime( ).asString( );
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
                dout << CtiTime() << " Error building point registration list for dispatch for " << getInterfaceName() << endl;
            }
        }
    }

    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << getInterfaceName() << "'s sendRegistration failed by exception." << endl;
    }

    return TRUE;
}


void CtiFDRInterface::buildRegistrationPointList (CtiPointRegistrationMsg **aMsg)
{
    CtiFDRPointSPtr pFdrPoint;
    CtiPointRegistrationMsg *testMsg;

    // we have some points to send
    *aMsg = new CtiPointRegistrationMsg( REG_TAG_MARKMOA );
    testMsg = *aMsg;

    // get iterator on outbound list
    CtiFDRManager::readerLock guard(iOutBoundPoints->getLock());
    CtiFDRManager::spiterator  myIterator = iOutBoundPoints->getMap().begin();

    for ( ; myIterator != iOutBoundPoints->getMap().end(); ++myIterator)
    {
        pFdrPoint = (*myIterator).second;

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
            dout << CtiTime() << " Initializing CtiFDRInterface::threadFunctionReceiveFromDispatch"  << endl;
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
                                dout << CtiTime() << " " << getInterfaceName() << "'s connection to dispatch failed verification.  Attempting to reconnect" << endl;
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
                            logEvent (string (getInterfaceName() + string ("'s connection to dispatch has been restarted")),string());
                            registerWithDispatch();
                        }
                    }
                    catch (...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getInterfaceName() << "'s connection to dispatch failed by exception in threadFunctionReceiveFromDispatch." << endl;
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
                        dout << CtiTime() << " " << getInterfaceName() << "'s connection to dispatch is not initialized.  Attempting to reconnect" << endl;
                    }
                    // we're assuming that the send to dispatch will catch the problem
                    {
                        CtiLockGuard<CtiMutex> guard(iDispatchMux);
                        // possible two minute gap here
                        connectWithDispatch();
                    }
                    pSelf.serviceCancellation( );
                    logEvent (string (getInterfaceName() + string ("'s connection to dispatch has been restarted")),string());
                    registerWithDispatch();
                }

            } while ( incomingMsg == NULL );


            switch (incomingMsg->isA())
            {
                case MSG_DBCHANGE:
                    {
                        // db change message reload if type if point
                        int changeId = ((CtiDBChangeMsg*)incomingMsg)->getId();
                        int changeType = ((CtiDBChangeMsg*)incomingMsg)->getTypeOfChange();

                        if ( ((CtiDBChangeMsg*)incomingMsg)->getDatabase() == ChangePointDb)
                        {
                            if (changeType == ChangeTypeDelete)
                                processFDRPointChange(changeId, true);
                            else
                            {
                                processFDRPointChange(changeId, false);
                                reRegisterWithDispatch();
                            }

                        }
                        else if (((CtiDBChangeMsg*)incomingMsg)->getDatabase() == ChangePAODb)
                        {
                            // Ignoring delete device changes. If its a delete, we will error on sql anyways, they are gone
                            // Updates get sent on point level. We only have to deal with change type add for pao's
                            if (changeType == ChangeTypeAdd)
                            {
                                // get all points on device and make a message for each
                                std::vector<int> ids = getPointIdsOnPao(changeId);

                                for (std::vector<int>::iterator itr = ids.begin(); itr != ids.end(); itr++)
                                {
                                    // Only have to setId to id in list and setTypeOfChange to match original message.
                                    int pid = *itr;
                                    CtiDBChangeMsg* ptr = new CtiDBChangeMsg(pid, 0, "", "", changeType);
                                    processFDRPointChange(pid,false);//always false.  Tested up top.
                                    reRegisterWithDispatch();
                                    delete ptr;
                                }
                            }
                        }
                        break;

                    }
                case MSG_COMMAND:
                    {
                        // we will handle this message for other classes
                        CtiCommandMsg* cmd = (CtiCommandMsg*)incomingMsg;

                        switch (cmd->getOperation())
                        {
                            case (CtiCommandMsg::Shutdown):
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " FDR received a shutdown message from somewhere- Ignoring!!" << endl;
                                }
                                break;

                            case (CtiCommandMsg::AreYouThere):
                                // echo back the same message - we are here
                                sendMessageToDispatch(cmd->replicateMessage());

    //                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " FDR" << getInterfaceName() << " has been pinged by dispatch" << endl;
                                }
                                break;

                            default:
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " FDR received a unknown Command message- " << endl;
                                }
                                break;

                        }
                    }

                    break;

                case MSG_MULTI:
                    //  seperate it out into distinct CtiMessages, so the translation
                    //  function can be a simple switch block
                    for ( x = 0; x < ((CtiMultiMsg *)incomingMsg)->getData( ).size( ); x++ )
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
                            dout << CtiTime() << " received a single point data message type- " << endl;
                        }
                        sendMessageToForeignSys(incomingMsg);
                        break;
                    }
                default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " FDR received a unknown message type- " << incomingMsg->isA() << endl;
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
        logNow() << "threadFunctionReceiveFromDispatch shutdown" << endl;
        return;
    }

    // catch whatever is left
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error:  receiveFromDispatchThread is dead! " << endl;
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
    CtiTime checkTime;
    bool hasEntries = true;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDRInterface::threadFunctionSendToDispatch " << endl;
        }

        // create the muli
        checkTime = CtiTime() + getQueueFlushRate();

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
            if (( entries >= 500) || (CtiTime() > checkTime))
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
                        pMultiData->getData( ).push_back( incomingMsg );

                        // if divisible by 500, send it and start again
                        if (cnt % 500 == 0)
                        {
//                            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Sending batch of " << cnt << " entries to dispatch from " << getInterfaceName() << endl;
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
                        dout << CtiTime() << " Sending batch of " << cnt << " entries to dispatch from " << getInterfaceName() << endl;
                    }
                    cnt=0;

                    sendMessageToDispatch( pMultiData );
                }

                checkTime = CtiTime() + getQueueFlushRate();
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
        logNow() << "threadFunctionSendToDispatch shutdown" << endl;
        return;
    }

    // catch whatever is left
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error:  threadFunctionSendToDispatch is dead! " << endl;
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
    CtiTime timeNow;
    CtiTime hourstart;
    CtiTime refreshTime;

    if (getReloadRate() > 3600)
    {
        hourstart = CtiTime(timeNow.seconds() - (timeNow.seconds() % 3600)); // align to the hour.
        refreshTime=CtiTime(hourstart.seconds() - ((hourstart.hour()* 3600) % getReloadRate()) + getReloadRate());
    }
    else
    {
        refreshTime = CtiTime (timeNow.seconds() - (timeNow.seconds() % getReloadRate()) + getReloadRate());
    }

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << " Initializing CtiFDRInterface::threadFunctionReloadDb " << endl;
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = CtiTime();

            if ((getDbReloadReason() == ForceReload) || (timeNow >= refreshTime))
            {
                if ((getDbReloadReason() == ForceReload))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Reload of DB Forced. Reloading Translations" << endl;
                    }
                }
                else
                {
                    // reload point list
                    setDbReloadReason(Periodic);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Periodic Timer expired -  Reloading Translations" << endl;
                    }
                }

                // if we successfully reload
                if (reloadTranslationLists())
                {
                    // do this whenever we reload the database
                    reRegisterWithDispatch();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " - Re-registered with dispatch - " << getInterfaceName() << endl;
                    }

                    // wait until the state goes back to normal before resetting and continuing
                    setDbReloadReason(NotReloaded);

                    // reset refresh time
                    if (getReloadRate() > 3600)
                    {
                        hourstart = CtiTime(timeNow.seconds() - (timeNow.seconds() % 3600)); // align to the hour.
                        refreshTime=CtiTime(hourstart.seconds() - ((hourstart.hour()* 3600) % getReloadRate()) + getReloadRate());
                    }
                    else
                    {
                        refreshTime = CtiTime (timeNow.seconds() - (timeNow.seconds() % getReloadRate()) + getReloadRate());
                    }
                }
                else
                {
                    {
                        // sleep a second and try again
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << "Error reloading points, "
                            << "will reset periodic timer to 60 seconds" << endl;
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
        logNow() << "threadFunctionReloadDb shutdown" << endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error: threadFunctionReloadDb is dead! " << endl;
    }
}

bool CtiFDRInterface::logEvent( const string &aDesc, const string &aAction, bool aSendImmediatelyFlag )
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

CtiCommandMsg* CtiFDRInterface::createAnalogOutputMessage(long pointId, string translationName, double value)
{
    // build the command message and send the control
    CtiCommandMsg *cmdMsg = new CtiCommandMsg(CtiCommandMsg::AnalogOutput);

    cmdMsg->insert(pointId);  // point for control
    cmdMsg->insert(value);

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Analog Output Point " << translationName << " was sent new value: " << value;
        dout <<" from " << getInterfaceName() << " and processed for point " << pointId << endl;
    }
    return cmdMsg;
}

CtiCommandMsg* CtiFDRInterface::createScanDeviceMessage(long paoId, string translationName)
{
    // build the command message and send the control
    CtiCommandMsg *cmdMsg = new CtiCommandMsg(CtiCommandMsg::InitiateScan);
    cmdMsg->insert( paoId );  // This is the device id to scan
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Scan Integrity Request sent to DeviceID: " << paoId << endl;
    }
    return cmdMsg;
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
            dout << CtiTime() << " The attempt to write point data from " << getInterfaceName() << " to dispatch has failed. Trying again " << endl;
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
                dout << CtiTime() << " Connection to dispatch for " << getInterfaceName() << " has failed.  Data will be lost " << endl;
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
            dout << CtiTime() << " " << getInterfaceName() << "'s connection to dispatch failed by exception in attemptSend()." << endl;
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
            dout << CtiTime() << " Dispatch queue is over the 10,000 point threshold for " << getInterfaceName() << ". Purging oldest 1000 entries " << endl;
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

    CtiFDRManager   *tmpList = new CtiFDRManager(iInterfaceName, string(FDR_INTERFACE_SEND));

    // try and reload the outbound list
    if (tmpList->loadPointList())
    {
        // destroy the old one and set it to the new one
        delete iOutBoundPoints;
        iOutBoundPoints = NULL;
        iOutBoundPoints = tmpList;
    }
    else
    {
        retVal = !NORMAL;
        delete tmpList;
        tmpList = NULL;
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
    CtiFDRPointSPtr point;

    // lock the list
    CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
    CtiFDRManager::readerLock guard(aList.getPointList()->getLock());

    // check if the point id exists
    CtiFDRManager::spiterator itr;

    itr = aList.getPointList()->getMap().find(aMessage->getId());
    if( itr != aList.getPointList()->getMap().end() )
    {
            point = (*itr).second;
    }

    if ( aList.getPointList()->getMap().size() == 0 || point.get() == NULL)
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
    CtiFDRPointSPtr point;

    // lock the list
    CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());

    // check if the point id exists
    CtiFDRManager::spiterator  itr;
    itr = aList.getPointList()->getMap().find(aPointId);
    if( itr != aList.getPointList()->getMap().end() )
        point = (*itr).second;

    if ( aList.getPointList()->getMap().size() == 0 ||  point.get() == NULL)
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
bool CtiFDRInterface::findTranslationNameInList(string aTranslationName,
                                                CtiFDRPointList &aList,
                                                CtiFDRPoint &aPoint)
{
    bool               foundFlag= false;
    int                index =0;
    CtiFDRPointSPtr point;

    // lock the list
    CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
    point = aList.getPointList()->find(isTranslationNameEqual, (void *) &aTranslationName);

    if (point.get() == NULL)
    {
        foundFlag = false;
    }
    else
    {
        // reset reason for reload
        aPoint = *(point.get());
        foundFlag = true;
    }
    return foundFlag;
}

/***
* Process a change to the FDR point list
*/
void CtiFDRInterface::processFDRPointChange(int pointId, bool deleteType)
{
    // Remove it from our list if we have it.
    // Then re-insert it if the db query finds something. (It will not find anything if is a delete)

    removeTranslationPoint(pointId);

    //already know its a point db change. So just check for not delete
    if (!deleteType)
    {
        //if type is delete. we dont have to re-add.
        loadTranslationPoint(pointId);
    }
}

//Load single point, maintaining current lists
bool CtiFDRInterface::loadTranslationPoint(long pointId)
{
    bool ret = false;
    bool inRecv = false;
    bool inSend = false;

    CtiLockGuard<CtiMutex> receiveGuard(iReceiveFromList.getMutex());
    CtiLockGuard<CtiMutex> sendGuard(iSendToList.getMutex());

    CtiFDRManager* recvMgr = iReceiveFromList.getPointList();
    CtiFDRManager* sendMgr = iSendToList.getPointList();

    printLists(" Before point add call for ", pointId);

    if (recvMgr != NULL)
    {
        bool foundPoint = false;
        CtiFDRPointSPtr fdrPoint;
        recvMgr->addFDRPointId(pointId,fdrPoint);

        if (fdrPoint)
        {
            ret = translateSinglePoint(fdrPoint,false);
        }
    }

    if (sendMgr != NULL)
    {
        bool foundPoint = false;
        CtiFDRPointSPtr fdrPoint;
        sendMgr->addFDRPointId(pointId,fdrPoint);

        if (fdrPoint)
        {
            ret = translateSinglePoint(fdrPoint,true);
        }
    }

    printLists(" After point add call for ", pointId);

    return ret;
}

//remove single point maintaining current lists
void CtiFDRInterface::removeTranslationPoint(long pointId)
{
    CtiFDRPointSPtr inRecv;
    CtiFDRPointSPtr inSend;

    printLists(" Before remove of point ", pointId);
    {
        CtiLockGuard<CtiMutex> receiveGuard(iReceiveFromList.getMutex());
        CtiLockGuard<CtiMutex> sendGuard(iSendToList.getMutex());

        CtiFDRManager* recvMgr = iReceiveFromList.getPointList();
        CtiFDRManager* sendMgr = iSendToList.getPointList();

        if (recvMgr != NULL)
        {
            inRecv = recvMgr->removeFDRPointID(pointId);
            if (inRecv.get() != NULL)
            {
                cleanupTranslationPoint(inRecv,true);
            }

        }
        if (sendMgr != NULL)
        {
            inSend = sendMgr->removeFDRPointID(pointId);
            if (inSend.get() != NULL)
            {
                cleanupTranslationPoint(inSend,false);
            }

        }
    }
    printLists(" After remove of point ", pointId);

    return;
}

void CtiFDRInterface::printLists(string title, int pid)
{
    if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiMutex> receiveGuard(iReceiveFromList.getMutex());
        CtiLockGuard<CtiMutex> sendGuard(iSendToList.getMutex());

        CtiFDRManager* recvMgr = iReceiveFromList.getPointList();
        CtiFDRManager* sendMgr = iSendToList.getPointList();

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << title << " " << pid << endl << "Recv: ";
        if (recvMgr != NULL)
        {
            recvMgr->printIds(dout);
        }
        dout << "\nSend: ";
        if (sendMgr != NULL)
        {
            sendMgr->printIds(dout);
        }
        dout << endl;
    }
}

/* This is here to be replaced with child processes to notify them of the removal and give them the chance to act.*/
void CtiFDRInterface::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    return;
}

/**
 * Return the 'dout' logger and prepend the current time and the interface name.
 */
std::ostream& CtiFDRInterface::logNow() {
  return dout <<  CtiTime::now()  << string(" FDR-") << getInterfaceName() << string(": ");
}

