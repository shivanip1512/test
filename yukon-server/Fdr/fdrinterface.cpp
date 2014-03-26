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
#include "amq_constants.h"

using std::string;
using std::endl;

/** local definitions **/
const CHAR * CtiFDRInterface::KEY_DEBUG_LEVEL = "_DEBUGLEVEL";
const CHAR * CtiFDRInterface::KEY_CPARM_RELOAD_RATE_SECONDS = "FDR_CPARM_RELOAD_RATE_SECONDS";

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
    iDispatchConnected(false)
{
}

CtiFDRInterface::~CtiFDRInterface()
{
    iDispatchQueue.clearAndDestroy();
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
        boost::scoped_ptr<CtiFDRManager> pointList(
                new CtiFDRManager(
                        string (FDR_SYSTEM),
                        string (FDR_INTERFACE_LINK_STATUS)));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
        {
            // get iterator on list
            CtiFDRManager::spiterator myIterator = pointList->getMap().begin();

            for ( ; myIterator !=  pointList->getMap().end(); ++myIterator )
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
    iOutBoundPoints.reset( new CtiFDRManager(iInterfaceName, string(FDR_INTERFACE_SEND)) );
    iOutBoundPoints->loadPointList();

    if ( !reloadConfigs() )
    {
        return FALSE;
    }

    // create a dispatch thread object
    // this thread knows how to handle messages from dispatch
    iThreadFromDispatch = rwMakeThreadFunction(*this, &CtiFDRInterface::threadFunctionReceiveFromDispatch);
    iThreadToDispatch = rwMakeThreadFunction(*this, &CtiFDRInterface::threadFunctionSendToDispatch);
    iThreadDbChange = rwMakeThreadFunction(*this, &CtiFDRInterface::threadFunctionReloadDb);
    iThreadReloadCparm = rwMakeThreadFunction(*this, &CtiFDRInterface::threadFunctionReloadCparm);


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
    // start our dispatch distibution thread

    {
        WriterGuard guard(iDispatchLock);

        iThreadFromDispatch.start();

        // get the dispatch registration id from the native thread id and proceed with dispatch connection
        iDispatchRegisterId = iThreadFromDispatch.threadId();
        connectWithDispatch();
    }

    iThreadToDispatch.start();
    iThreadDbChange.start();
    iThreadReloadCparm.start();

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
            logNow() << "Attempting to cancel threadFunctionReloadCparm;" << endl;
        }
        iThreadReloadCparm.requestCancellation();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "Attempting to shutdown connections" << endl;
        }

        iThreadDbChange.join();
        iThreadFromDispatch.join();
        iThreadToDispatch.join();
        iThreadReloadCparm.join();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << "All threads have joined up" << endl;
        }

        {
            ReaderGuard guard(iDispatchLock);
            if( iDispatchConn )
            {
                iDispatchConn->close();
            }
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Unknown exception on shutdown (CtiFDRInterface::stop)" << endl;
    }

    return TRUE;
}

/**
 * Connects and register the dispatch connection.
 *
 * @return true if dispatch running and valid, false otherwise
 */
bool CtiFDRInterface::connectWithDispatch()
{
    WriterGuard guard(iDispatchLock);

    try
    {
        if( iDispatchConn && iDispatchConn->verifyConnection() == NORMAL )
        {
            return true;
        }

        if( iDispatchConn )
        {
            iDispatchConn.reset();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << getInterfaceName() << "'s connection to dispatch failed verification.  Attempting to reconnect" << endl;
            }
        }

        if( ! iDispatchRegisterId  )
        {
            iDispatchConn.reset();
            return false;
        }

        //
        // create a new dispatch connection
        //

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Attempting to connect to dispatch for " << getInterfaceName() << endl;
        }

        iDispatchConn.reset( new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::dispatch, &iDispatchInQueue ));
        iDispatchConn->setName( "FDR to Dispatch: " + getInterfaceName() );
        iDispatchConn->start();

        if( iDispatchConn->verifyConnection() != NORMAL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Attempt to reconnect to dispatch for " << getInterfaceName() << " failed.  Attempting again" << endl;
            }
            iDispatchConn.reset();
            return false;
        }

        //
        // register who we are
        //

        const std::string regStr("FDR" + iInterfaceName);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Registering:  " << regStr << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( iDispatchConn->WriteConnQue( new CtiRegistrationMsg( regStr, *iDispatchRegisterId, true)) != NORMAL )
        {
            iDispatchConn.reset();
            return false;
        }

        if( iOutBoundPoints && iOutBoundPoints->entries() > 0 )
        {
            std::auto_ptr<CtiMultiMsg> multiMsg( new CtiMultiMsg() );

            multiMsg->insert( buildRegistrationPointList() );

            if( iDispatchConn->WriteConnQue(multiMsg.release()) != NORMAL )
            {
                iDispatchConn.reset();
                return false;
            }
        }

        if( iDispatchConnected )
        {
            logEvent( getInterfaceName() + "'s connection to dispatch has been restarted",string());
        }
        else
        {
            iDispatchConnected = true;
        }

        return true;
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Attempt to connect to dispatch for " << getInterfaceName() << " failed by exception " << endl;
        }
        iDispatchConn.reset();
        return false;
    }
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

/**
 * Returns the current debuglevel for the interface.
 *
 */
ULONG CtiFDRInterface::getDebugLevel()
{
    CtiLockGuard<CtiMutex> cparm_guard(iCparmMutex);
    return iDebugLevel;
}

/**
 * Wrapping readConfig to add the mutex call for extended
 * classes.
 *
 */
int CtiFDRInterface::reloadConfigs() {
    CtiLockGuard<CtiMutex> cparm_guard(iCparmMutex);
    gConfigParms.RefreshConfigParameters();
    CtiFDRInterface::readConfig();
    return this->readConfig();
}

/************************************************************************
* Function Name: CtiFDRInterface::readConfig(  )
*
* Description: loads base class config information.
*              (Dispatch Name and FDR Debug level)
*
* NOTE: Do not call this directly. This should be called from reloadConfigs only.
*
*************************************************************************
*/
int CtiFDRInterface::readConfig( void )
{
    int         successful = TRUE;
    string   tempStr;
    char        *eptr;

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

    iCparmReloadSeconds = gConfigParms.getValueAsInt(KEY_CPARM_RELOAD_RATE_SECONDS,300);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " CPARM Reload Rate is " << iCparmReloadSeconds << " seconds." << endl;
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
    ReaderGuard guard(iDispatchLock);

    if( ! iOutBoundPoints || ! iDispatchConn )
    {
        // not started correctly
        return false;
    }

    try
    {
        if (iOutBoundPoints->entries() > 0)
        {
            std::auto_ptr<CtiMultiMsg> multiMsg( new CtiMultiMsg() );

            // debug printing
            //            regStr  = "FDR - \"" + iinterfaceType + "\" module - " + "destination \"" + idestination + "\" - ";
            //            regStr += CtiTime( ).asString( );
            //         CtiLockGuard<CtiLogger> doubt_guard(dout);
            //            dout << regStr << endl;

            //multiMsg->insert( new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE) );
            multiMsg->insert( buildRegistrationPointList() );

            sendMessageToDispatch( multiMsg.release() );
        }
        return true;
    }
    catch (...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getInterfaceName() << "'s sendRegistration failed by exception." << endl;
        }
        return false;
    }
}


CtiPointRegistrationMsg* CtiFDRInterface::buildRegistrationPointList()
{
    CtiFDRPointSPtr pFdrPoint;
    std::auto_ptr<CtiPointRegistrationMsg> ptRegMsg( new CtiPointRegistrationMsg( REG_TAG_MARKMOA ));

    // get iterator on outbound list
    CtiFDRManager::readerLock guard(iOutBoundPoints->getLock());
    CtiFDRManager::spiterator  myIterator = iOutBoundPoints->getMap().begin();

    for ( ; myIterator != iOutBoundPoints->getMap().end(); ++myIterator)
    {
        pFdrPoint = (*myIterator).second;

        // add this point ID to register
        ptRegMsg->insert( pFdrPoint->getPointID());
    }

    return ptRegMsg.release();
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
    RWRunnableSelf pSelf = rwRunnable( );

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDRInterface::threadFunctionReceiveFromDispatch"  << endl;
        }

        for( ; ; )
        {
            std::auto_ptr<CtiMessage> incomingMsg;

            //  while i'm not getting anything
            while( ! incomingMsg.get() )
            {
                pSelf.serviceCancellation( );

                {
                    ReaderGuard guard(iDispatchLock);

                    if( iDispatchConn && iDispatchConn->verifyConnection() == NORMAL )
                    {
                        incomingMsg.reset( iDispatchConn->ReadConnQue( 1000 ));
                        continue;
                    }
                }

                connectWithDispatch();
                Sleep(1000);
            }

            switch (incomingMsg->isA())
            {
                case MSG_DBCHANGE:
                {
                    CtiDBChangeMsg* dBChangeMsg = static_cast<CtiDBChangeMsg*>(incomingMsg.get());

                    // db change message reload if type is point
                    int pidChanged = dBChangeMsg->getId();
                    int changeType = dBChangeMsg->getTypeOfChange();

                    if ( dBChangeMsg->getDatabase() == ChangePointDb)
                    {
                        if (changeType == ChangeTypeDelete)
                            processFDRPointChange(pidChanged, true);
                        else
                        {
                            processFDRPointChange(pidChanged, false);
                            reRegisterWithDispatch();
                        }

                    }
                    else if ( dBChangeMsg->getDatabase() == ChangePAODb)
                    {
                        // Ignoring delete device changes. If its a delete, we will error on sql anyways, they are gone
                        // Updates get sent on point level. We only have to deal with change type add for pao's
                        if (changeType == ChangeTypeAdd)
                        {
                            // get all points on device and process point change for each
                            std::vector<int> pointIds = getPointIdsOnPao(pidChanged);
                            for each(int pointId in pointIds)
                            {
                                processFDRPointChange(pointId,false);//changeType tested to be true, so its not a delete
                            }
                        }
                    }
                    break;

                }
                case MSG_COMMAND:
                {
                    CtiCommandMsg* cmd = static_cast<CtiCommandMsg*>(incomingMsg.get());

                    switch (cmd->getOperation())
                    {
                        case (CtiCommandMsg::Shutdown):
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " FDR received a shutdown message from somewhere- Ignoring!!" << endl;
                            }
                            break;
                        }
                        case (CtiCommandMsg::AreYouThere):
                        {
                            // echo back the same message - we are here
                            sendMessageToDispatch(cmd->replicateMessage());
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " FDR" << getInterfaceName() << " has been pinged by dispatch" << endl;
                            }
                            break;
                        }
                        case (CtiCommandMsg::InitiateScan):
                        {
                            processCommandFromDispatch(cmd);
                            break;
                        }
                        default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " FDR received a unknown Command message- " << endl;
                            break;
                        }

                    }
                    break;
                }
                case MSG_MULTI:
                {
                    CtiMultiMsg* multiMsg = static_cast<CtiMultiMsg*>(incomingMsg.get());

                    //  seperate it out into distinct CtiMessages, so the translation
                    //  function can be a simple switch block
                    for( int x = 0; x < multiMsg->getData().size(); x++ )
                    {
                        CtiMessage* dataMsg = multiMsg->getData()[x];

                        // for now, send on only the pointdata messages
                        if( dataMsg->isA() == MSG_POINTDATA )
                        {
                            //  send the current node change to foreign system
                            sendMessageToForeignSys( dataMsg );
                        }
                    }
                    break;
                }
                case MSG_POINTDATA:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " received a single point data message type- " << endl;
                    }
                    sendMessageToForeignSys( incomingMsg.get() );
                    break;
                }
                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " FDR received a unknown message type- " << incomingMsg->isA() << endl;
                    }
                    break;
                }
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "threadFunctionReceiveFromDispatch shutdown" << endl;
    }

    // catch whatever is left
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error:  receiveFromDispatchThread is dead! " << endl;
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

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDRInterface::threadFunctionSendToDispatch " << endl;
        }

        CtiTime checkTime = CtiTime() + getQueueFlushRate();

        for ( ; ; )
        {
            pSelf.serviceCancellation( );

            // check the entry list
            const int entries = iDispatchQueue.entries();

            // no more than 500 at a time or 5 seconds, whichever comes first
            if( entries >= 500 || (entries && CtiTime() > checkTime) )
            {
                std::auto_ptr<CtiMultiMsg> pMultiData( new CtiMultiMsg() );

                for( int msgNbr=0; ; )
                {
                    pMultiData->getData().push_back( iDispatchQueue.getQueue() );

                    // send if no more entries or there is 500
                    if( ++msgNbr == entries || pMultiData->getData().size() == 500 )
                    {
//                            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Sending batch of " << pMultiData->getData().size() << " entries to dispatch from " << getInterfaceName() << endl;
                        }

                        sendMessageToDispatch( pMultiData.release() );

                        if( msgNbr == entries )
                        {
                            break; // exit the loop when we are done
                        }

                        pMultiData.reset( new CtiMultiMsg() );
                    }
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
    }

    // catch whatever is left
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Fatal Error:  threadFunctionSendToDispatch is dead! " << endl;
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

/**
 * This is a thread that will periodically reload the CPARMS for
 * the interface.
 *
 * */
void CtiFDRInterface::threadFunctionReloadCparm( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );

    CtiTime nextReload = CtiTime() + iCparmReloadSeconds;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            logNow() << " Initializing CtiFDRInterface::threadFunctionReloadCparm " << endl;
        }

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            CtiTime timeNow;
            if (timeNow > nextReload) {
                if ( !reloadConfigs() )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        logNow() << " Error while Reloading CPARMS. " << endl;
                    }
                }
                nextReload = timeNow + iCparmReloadSeconds;
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
    std::auto_ptr<CtiSignalMsg> eventLog(
            new CtiSignalMsg(
                    0,
                    0,
                    aDesc,
                    aAction,
                    GeneralLogType));

    if (aSendImmediatelyFlag)
    {
        return (sendMessageToDispatch(eventLog.release()));
    }
    else
    {
        return (queueMessageToDispatch(eventLog.release()));
    }
}

CtiCommandMsg* CtiFDRInterface::createAnalogOutputMessage(long pointId, string translationName, double value)
{
    // build the command message and send the control
    std::auto_ptr<CtiCommandMsg> cmdMsg( new CtiCommandMsg(CtiCommandMsg::AnalogOutput));

    cmdMsg->insert(pointId);  // point for control
    cmdMsg->insert(value);

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Analog Output Point " << translationName << " was sent new value: " << value;
        dout <<" from " << getInterfaceName() << " and processed for point " << pointId << endl;
    }
    return cmdMsg.release();
}

CtiCommandMsg* CtiFDRInterface::createScanDeviceMessage(long paoId, string translationName)
{
    // build the command message and send the control
    std::auto_ptr<CtiCommandMsg> cmdMsg( new CtiCommandMsg(CtiCommandMsg::InitiateScan));
    cmdMsg->insert( paoId );  // This is the device id to scan
    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Scan Integrity Request sent to DeviceID: " << paoId << endl;
    }
    return cmdMsg.release();
}


bool CtiFDRInterface::sendMessageToDispatch( CtiMessage *aMessage )
{
    // take ownership of the message
    std::auto_ptr<CtiMessage> msg(aMessage);

    {
        ReaderGuard guard(iDispatchLock);

        if( iDispatchConn && iDispatchConn->WriteConnQue(msg->replicateMessage()) == NORMAL ) // use a copy in case the send fails
        {
            return true;
        }
    }

    // log and try again
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " The attempt to write point data from " << getInterfaceName() << " to dispatch has failed. Trying again " << endl;
    }

    connectWithDispatch();

    {
        ReaderGuard guard(iDispatchLock);

        if( iDispatchConn && iDispatchConn->WriteConnQue(msg.release()) == NORMAL )
        {
            return true;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Connection to dispatch for " << getInterfaceName() << " has failed.  Data will be lost " << endl;
    }

    return false;
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
bool CtiFDRInterface::reRegisterWithDispatch()
{
    bool retVal=true;

    std::auto_ptr<CtiFDRManager> tmpList( new CtiFDRManager(iInterfaceName, string(FDR_INTERFACE_SEND)));

    // try and reload the outbound list
    if( tmpList->loadPointList() )
    {
        WriterGuard guard(iDispatchLock);

        // destroy the old one and set it to the new one
        iOutBoundPoints.reset( tmpList.release() );
    }
    else
    {
        retVal = false;
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
        point->setUnsolicited(aMessage->getTags() & TAG_POINT_DATA_UNSOLICITED);
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

/**
 * Verify if the dispatch connection is running
 *
 * @return true if dispatch running and valid, false otherwise
 */
bool CtiFDRInterface::verifyDispatchConnection()
{
    {
        ReaderGuard guard(iDispatchLock);

        if( iDispatchConn && iDispatchConn->verifyConnection() == NORMAL )
        {
            return true;
        }
    }

    return false;
}
