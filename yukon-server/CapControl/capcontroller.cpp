/*-----------------------------------------------------------------------------
    Filename:  capcontroller.cpp

    Programmer:  Josh Wolberg

    Description:    Source file for CtiCapController
                    Once started CtiCapController is reponsible
                    for determining if and when to run the
                    schedules provided by the CtiCCSubstationBusStore.
                    It runs schedules in collaboration with
                    CtiCCScheduleRunner.

    Initial Date:  8/31/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "connection.h"
#include "ccmessage.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_dbchg.h"
#include "pointtypes.h"
#include "configparms.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "ctibase.h"
#include "yukon.h"
#include "netports.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "resolvers.h"

#include <rw/thr/prodcons.h>

extern BOOL _CC_DEBUG;

/* The singleton instance of CtiCapController */
CtiCapController* CtiCapController::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns the single instance of CtiCapController
---------------------------------------------------------------------------*/
CtiCapController* CtiCapController::getInstance()
{
    if ( _instance == NULL )
        _instance = new CtiCapController();

    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor

    Private to ensure that only one CtiCapController is available through the
    instance member function
---------------------------------------------------------------------------*/
CtiCapController::CtiCapController()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _dispatchConnection = NULL;
    _pilConnection = NULL;
}

/*---------------------------------------------------------------------------
    Destructor

    Private to ensure that the single instance of CtiCapController is not
    deleted
---------------------------------------------------------------------------*/
CtiCapController::~CtiCapController()
{
}

/*---------------------------------------------------------------------------
    start

    Starts the controller thread
---------------------------------------------------------------------------*/
void CtiCapController::start()
{
    RWThreadFunction threadfunc = rwMakeThreadFunction( *this, &CtiCapController::controlLoop );
    _substationBusThread = threadfunc;
    threadfunc.start();
}

/*---------------------------------------------------------------------------
    stop

    Stops the controller thread
---------------------------------------------------------------------------*/
void CtiCapController::stop()
{
    //if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Shutting down controller thread..." << endl;
    }

    if ( _substationBusThread.isValid() && _substationBusThread.requestCancellation() == RW_THR_ABORTED )
    {
        _substationBusThread.terminate();

        //if( _CC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Forced to terminate." << endl;
        }
    } else
    {
        _substationBusThread.join();

        //if( _CC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Controller thread shutdown" << endl;
        }
    }

    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        getDispatchConnection()->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        getDispatchConnection()->ShutdownConnection();
        getPILConnection()->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        getPILConnection()->ShutdownConnection();
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout.interrupt(CtiThread::SHUTDOWN);
        dout.join();
    }
}

/*---------------------------------------------------------------------------
    controlLoop

    Desides when to control banks, update statuses, and send messages to
    other related applications (server programs: pil, dispatch;
    client: cap control client in TDC).
--------------------------------------------------------------------------*/
void CtiCapController::controlLoop()
{
    try
    {
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
        registerForPoints(store);
        store->setReregisterForPoints(FALSE);

        RWDBDateTime currentDateTime;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        CtiCCSubstationBusMsg* substationBusMsg = new CtiCCSubstationBusMsg();
        while(TRUE)
        {
            if(_CC_DEBUG)
            {
                if( (RWDBDateTime().seconds()%1800) == 0 )
                {//every five minutes tell the user if the control thread is still alive
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Controller thread pulse" << endl;
                }
            }

            rwRunnable().serviceCancellation();
            try
            {
                if( !store->isValid() )
                {
                    store->dumpAllDynamicData();
                    CtiCCExecutorFactory f;
                    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                    CtiCCExecutor* executor = f.createExecutor(new CtiCCCapBankStatesMsg(store->getCCCapBankStates()));
                    executor->Execute(queue);
                    delete executor;
                    executor = f.createExecutor(new CtiCCGeoAreasMsg(store->getCCGeoAreas()));
                    executor->Execute(queue);
                    delete executor;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                if( store->getReregisterForPoints() )
                {
                    registerForPoints(store);
                    store->setReregisterForPoints(FALSE);
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                checkDispatch();
                checkPIL();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses();

            RWOrdered& pointChanges = multiDispatchMsg->getData();
            RWOrdered& pilMessages = multiPilMsg->getData();
            RWOrdered& substationBusChanges = substationBusMsg->getCCSubstationBuses();
            currentDateTime.now();
            for(UINT i=0;i<ccSubstationBuses.entries();i++)
            {
                CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                try
                {
                    if( currentSubstationBus->areAllCapBankStatusesReceived() &&
                        currentSubstationBus->isVarCheckNeeded(currentDateTime) )
                    {
                        if( currentSubstationBus->getRecentlyControlledFlag() )
                        {
                            try
                            {
                                if( currentSubstationBus->isAlreadyControlled() ||
                                    currentSubstationBus->isPastResponseTime(currentDateTime) )
                                {
                                    if( currentSubstationBus->capBankControlStatusUpdate(pointChanges) )
                                    {
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    }
                                }
                                else if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                                {
                                    if( !currentSubstationBus->getDisableFlag() )
                                    {
                                        currentSubstationBus->checkForAndProvideNeededControl(currentDateTime, pointChanges, pilMessages);
                                    }
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        else//not recently controlled
                        {
                            try
                            {
                                if( !currentSubstationBus->getDisableFlag() )
                                {
                                    currentSubstationBus->checkForAndProvideNeededControl(currentDateTime, pointChanges, pilMessages);
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }

                        try
                        {
                            if( currentSubstationBus->getControlInterval() == 0 )
                            {//so we don't do this over and over we need to clear out
                                currentSubstationBus->clearOutNewPointReceivedFlags();
                            }
                            else
                            {
                                currentSubstationBus->figureNextCheckTime();
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                try
                {
                    //accumulate all buses with any changes into msg for all clients
                    if( currentSubstationBus->getBusUpdatedFlag() )
                    {
                        substationBusChanges.insert(currentSubstationBus->replicate());
                        currentSubstationBus->setBusUpdatedFlag(FALSE);
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }

            try
            {
                //send point changes to dispatch
                if( pointChanges.entries() > 0 )
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                    getDispatchConnection()->WriteConnQue(multiDispatchMsg);
                    multiDispatchMsg = new CtiMultiMsg();
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                //send pil commands to porter
                if( multiPilMsg->getCount() > 0 )
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                    getPILConnection()->WriteConnQue(multiPilMsg);
                    multiPilMsg = new CtiMultiMsg();
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                if( substationBusChanges.entries() > 0 )
                {
                    //send the substation bus changes to all cap control clients
                    store->dumpAllDynamicData();
                    CtiCCExecutorFactory f;
                    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                    CtiCCExecutor* executor = f.createExecutor(substationBusMsg);
                    executor->Execute(queue);
                    delete executor;

                    substationBusMsg = new CtiCCSubstationBusMsg();
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep( 500 );
            }
            catch(RWCancellation& )
            {
                throw;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    getDispatchConnection

    Returns a connection to Dispatch, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiCapController::getDispatchConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _dispatchConnection == NULL )
    {
        RWCString str;
        char var[128];
        RWCString dispatch_host = "127.0.0.1";

        strcpy(var, "DISPATCH_MACHINE");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            dispatch_host = str.data();
            if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << dispatch_host << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        int dispatch_port = VANGOGHNEXUS;

        strcpy(var, "DISPATCH_PORT");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            dispatch_port = atoi(str.data());
            if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << dispatch_port << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        //Connect to Dispatch
        _dispatchConnection = new CtiConnection( dispatch_port, dispatch_host );

        //Send a registration message to Dispatch
        CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, FALSE );
        _dispatchConnection->WriteConnQue( registrationMsg );
    }

    return _dispatchConnection;
}

/*---------------------------------------------------------------------------
    getPILConnection

    Returns a connection to PIL, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiCapController::getPILConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _pilConnection == NULL )
    {
        RWCString str;
        char var[128];
        RWCString pil_host = "127.0.0.1";

        strcpy(var, "PIL_MACHINE");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            pil_host = str.data();
            if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << pil_host << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        INT pil_port = PORTERINTERFACENEXUS;

        strcpy(var, "PIL_PORT");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            pil_port = atoi(str.data());
            if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << pil_port << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        //Connect to Pil
        _pilConnection = new CtiConnection( pil_port, pil_host );

        //Send a registration message to Pil
        CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, FALSE );
        _pilConnection->WriteConnQue( registrationMsg );
    }

    return _pilConnection;
}

/*---------------------------------------------------------------------------
    checkDispatch

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::checkDispatch()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    BOOL done = FALSE;
    do
    {
        CtiMessage* in = (CtiMessage*) getDispatchConnection()->ReadConnQue(0);

        if ( in != NULL )
        {
            parseMessage(in);
            delete in;
        }
        else
            done = TRUE;
    }
    while(!done);
}

/*---------------------------------------------------------------------------
    checkPIL

    Reads off the PIL connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::checkPIL()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    BOOL done = FALSE;
    do
    {
        CtiMessage* in = (CtiMessage*) getPILConnection()->ReadConnQue(0);

        if ( in != NULL )
        {
            parseMessage(in);
            delete in;
        }
        else
            done = TRUE;
    }
    while(!done);
}

/*---------------------------------------------------------------------------
    registerForPoints

    Registers for all points of the substations buses.
---------------------------------------------------------------------------*/
void CtiCapController::registerForPoints(CtiCCSubstationBusStore* store)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    //if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Registering for point changes." << endl;
    }

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses();
    CtiPointRegistrationMsg* regMsg = new CtiPointRegistrationMsg();
    for(UINT i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
        {
            regMsg->insert(currentSubstationBus->getCurrentVarLoadPointId());
        }
        if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
        {
            regMsg->insert(currentSubstationBus->getCurrentWattLoadPointId());
        }
        
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(UINT j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders[j]);

            if( currentFeeder->getCurrentVarLoadPointId() > 0 )
            {
                regMsg->insert(currentFeeder->getCurrentVarLoadPointId());
            }
            if( currentFeeder->getCurrentWattLoadPointId() > 0 )
            {
                regMsg->insert(currentFeeder->getCurrentWattLoadPointId());
            }

            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(UINT k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[k]);

                if( currentCapBank->getStatusPointId() > 0 )
                {
                    regMsg->insert(currentCapBank->getStatusPointId());
                }
                if( currentCapBank->getOperationAnalogPointId() > 0 )
                {
                    regMsg->insert(currentCapBank->getOperationAnalogPointId());
                }
            }
        }
    }

    /*for(UINT x=0;x<regMsg->getCount();x++)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        LONG pid = regMsg->operator [](x);
        dout << RWTime() << " - Registered for Point Id: " << pid << endl;
    }*/
    getDispatchConnection()->WriteConnQue(regMsg);
    regMsg = NULL;
}

/*---------------------------------------------------------------------------
    parseMessage

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::parseMessage(RWCollectable *message)
{
    try
    {
        CtiMultiMsg* msgMulti;
        CtiPointDataMsg* pData;
        CtiReturnMsg* pcReturn;
        CtiCommandMsg* cmdMsg;
        CtiDBChangeMsg* dbChange;
        CtiSignalMsg* signal;
        int i = 0;
        switch( message->isA() )
        {
            case MSG_DBCHANGE:
                {
                    dbChange = (CtiDBChangeMsg *)message;
                    if( dbChange->getSource() != CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE &&
                        ( (dbChange->getDatabase() == ChangePAODb && resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_CAP_CONTROL) ||
                          (dbChange->getDatabase() == ChangePAODb && resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_DEVICE) ||
                          dbChange->getDatabase() == ChangePointDb ||
                          (dbChange->getDatabase() == ChangeStateGroupDb && dbChange->getId() == 3) ) )
                    {
                        //if( _CC_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Relavant database change.  Setting reload flag." << endl;
                        }
    
                        CtiCCSubstationBusStore::getInstance()->setValid(FALSE);
                        CtiCCSubstationBusStore::getInstance()->setReregisterForPoints(TRUE);
                        if( dbChange->getDatabase() == ChangeStateGroupDb )
                        {
    
                            CtiCCExecutorFactory f;
                            RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCapBankStatesMsg(CtiCCSubstationBusStore::getInstance()->getCCCapBankStates()));
                            try
                            {
                                executor->Execute(queue);
                                delete executor;
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        else
                        {
                            CtiCCExecutorFactory f;
                            RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses())));
                            try
                            {
                                executor->Execute(queue);
                                delete executor;
                                executor = f.createExecutor(new CtiCCCapBankStatesMsg(store->getCCCapBankStates()));
                                executor->Execute(queue);
                                delete executor;
                                executor = f.createExecutor(new CtiCCGeoAreasMsg(store->getCCGeoAreas()));
                                executor->Execute(queue);
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                            delete executor;
                        }
                    }
                }
                break;
            case MSG_POINTDATA:
                {
                    pData = (CtiPointDataMsg *)message;
                    pointDataMsg( pData->getId(), pData->getValue(), pData->getTags(), pData->getTime() );
                }
                break;
            case MSG_PCRETURN:
                {
                    pcReturn = (CtiReturnMsg *)message;
                    porterReturnMsg( pcReturn->DeviceId(), pcReturn->CommandString(), pcReturn->Status(), pcReturn->ResultString() );
                }
                break;
            case MSG_COMMAND:
                {
                    /*if( _CC_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Command Message received from Dispatch" << endl;
                    }*/
    
                    cmdMsg = (CtiCommandMsg *)message;
                    if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
                    {
                        if( _CC_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Replying to Are You There message." << endl;
                        }
                        {
                            RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                            getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage());
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Received not supported Command Message from Dispatch with Operation: "
                             << cmdMsg->getOperation() << ", and Op String: " << cmdMsg->getOpString() << endl;
                    }
                }
                break;
            case MSG_MULTI:
                {
                    msgMulti = (CtiMultiMsg *)message;
                    RWOrdered& temp = msgMulti->getData( );
                    for(i=0;i<temp.entries( );i++)
                    {
                        parseMessage(temp[i]);
                    }
                }
                break;
            case MSG_SIGNAL:
                {
                    signal = (CtiSignalMsg *)message;
                    signalMsg( signal->getId(), signal->getTags(), signal->getText(), signal->getAdditionalInfo() );
                }
                break;
            default:
                {
                    char tempstr[64] = "";
                    _itoa(message->isA(),tempstr,10);
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - message->isA() = " << tempstr << endl;
                    dout << RWTime() << " - Unknown message type: parseMessage(RWCollectable *message) in controller.cpp" << endl;
                }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return;
}

/*---------------------------------------------------------------------------
    pointDataMsg

    Handles point data messages and updates substation bus point values.
---------------------------------------------------------------------------*/
void CtiCapController::pointDataMsg( long pointID, double value, unsigned tags, RWTime& timestamp )
{
    if( _CC_DEBUG )
    {
        char tempchar[80];
        RWCString outString = "Point data message received from Dispatch. ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Value:";
        int precision = 3;
        _snprintf(tempchar,80,"%.*f",precision,value);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;
        outString += " Timestamp: ";
        outString += RWDBDateTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << outString.data() << endl;
    }

    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses();

    for(int i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        if( currentSubstationBus->getCurrentVarLoadPointId() == pointID )
        {
            if( timestamp > currentSubstationBus->getLastCurrentVarPointUpdateTime() )
            {
                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Last Point Update: " << currentSubstationBus->LastCurrentVarPointUpdateTime().asString() << endl;
                }*/
                currentSubstationBus->setLastCurrentVarPointUpdateTime(timestamp);
                if( currentSubstationBus->getControlInterval() == 0 )
                    currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
            }
            currentSubstationBus->setCurrentVarLoadPointValue(value);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            currentSubstationBus->figureEstimatedVarLoadPointValue();
            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
            {
                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }
            found = TRUE;
            break;
        }
        else if( currentSubstationBus->getCurrentWattLoadPointId() == pointID )
        {
            currentSubstationBus->setCurrentWattLoadPointValue(value);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            currentSubstationBus->figureEstimatedVarLoadPointValue();
            found = TRUE;
            break;
        }
        else if( !found )
        {
            RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.entries();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                if( currentFeeder->getCurrentVarLoadPointId() == pointID )
                {
                    if( timestamp > currentFeeder->getLastCurrentVarPointUpdateTime() )
                    {
                        /*{
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Last Point Update: " << currentFeeder->LastCurrentVarPointUpdateTime().asString() << endl;
                        }*/
                        currentFeeder->setLastCurrentVarPointUpdateTime(timestamp);
                        if( currentSubstationBus->getControlInterval() == 0 )
                            currentFeeder->setNewPointDataReceivedFlag(TRUE);
                    }
                    currentFeeder->setCurrentVarLoadPointValue(value);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentFeeder->figureEstimatedVarLoadPointValue();
                    if( currentFeeder->getEstimatedVarLoadPointId() > 0 )
                    {
                        sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedVarLoadPointId(),currentFeeder->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                    }
                    found = TRUE;
                    break;
                }
                else if( currentFeeder->getCurrentWattLoadPointId() == pointID )
                {
                    currentFeeder->setCurrentWattLoadPointValue(value);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentFeeder->figureEstimatedVarLoadPointValue();
                    found = TRUE;
                    break;
                }
                else if( !found )
                {
                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.entries();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getStatusPointId() == pointID )
                        {
                            if( timestamp > currentCapBank->getLastStatusChangeTime() ||
                                currentCapBank->getControlStatus() != (ULONG)value )
                            {
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentCapBank->setControlStatus((ULONG)value);
                            currentCapBank->setTagsControlStatus((ULONG)tags);
                            currentCapBank->setLastStatusChangeTime(timestamp);
                            currentCapBank->setStatusReceivedFlag(TRUE);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                            found = TRUE;
                            break;
                        }
                        else if( currentCapBank->getOperationAnalogPointId() == pointID )
                        {
                            if( timestamp > currentCapBank->getLastStatusChangeTime() ||
                                currentCapBank->getCurrentDailyOperations() != (ULONG)value )
                            {
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentCapBank->setCurrentDailyOperations((ULONG)value);
                            found = TRUE;
                            break;
                        }
                    }
                }
            }
        }
        else if( found )
        {
            break;
        }
    }

    return;
}

/*---------------------------------------------------------------------------
    porterReturn

    Handles porter return messages and updates the status of substation bus
    cap bank controls.
---------------------------------------------------------------------------*/
void CtiCapController::porterReturnMsg( long deviceId, RWCString commandString, int status, RWCString resultString )
{
    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Porter return received." << endl;
    }*/

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses();

    BOOL found = FALSE;
    for(int i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(int j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(int k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                if( currentCapBank->getControlDeviceId() == deviceId )
                {
                    if( currentSubstationBus->getRecentlyControlledFlag() &&
                        (currentFeeder->getLastCapBankControlledDeviceId() == currentCapBank->getPAOId()) )
                    {
                        if( status == 0 )
                        {
                            commandString.toLower();
                            if( commandString == "control open" )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                            }
                            else if( commandString == "control close" )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                            }
                        }
                        else
                        {
                            commandString.toLower();
                            if( commandString == "control open" )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            }
                            else if( commandString == "control close" )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                            }

                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Porter Return caused a Cap Bank to go into Failed State!  Bus: " << currentSubstationBus->getPAOName() << ", Feeder: " << currentFeeder->getPAOName()<< ", CapBank: " << currentCapBank->getPAOName() << endl;
                            }

                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                        }
                    }
                    found = TRUE;
                    break;
                }
            }
        }
        if( found )
        {
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    signalMessage

    Handles signal messages and updates substation bus tags.
---------------------------------------------------------------------------*/
void CtiCapController::signalMsg( long pointID, unsigned tags, RWCString text, RWCString additional )
{
    if( _CC_DEBUG )
    {
        char tempchar[64] = "";
        RWCString outString = "Signal Message received. Point ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << outString.data() << "  Text: "
                      << text << " Additional Info: " << additional << endl;
    }

    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses();

    for(int i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        if( !found )
        {
            RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.entries();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(int k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getStatusPointId() == pointID )
                    {
                        if( currentCapBank->getTagsControlStatus() != (ULONG)tags )
                        {
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                        currentCapBank->setTagsControlStatus((ULONG)tags);
                        found = TRUE;
                        break;
                    }
                }
            }
        }
        else if( found )
        {
            break;
        }
    }

    return;
}

/*---------------------------------------------------------------------------
    sendMessageToDispatch

    Sends a cti message to dispatch, this is a way for other threads to use
    the CtiCapController's connection to dispatch.
---------------------------------------------------------------------------*/
void CtiCapController::sendMessageToDispatch( CtiMessage* message )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    getDispatchConnection()->WriteConnQue(message);
}

/*---------------------------------------------------------------------------
    manualCapBankControl

    Handles a manual cap bank control sent by a client application.
---------------------------------------------------------------------------*/
void CtiCapController::manualCapBankControl( CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    getPILConnection()->WriteConnQue(pilRequest);
    if( multiMsg->getCount() > 0 )
        getDispatchConnection()->WriteConnQue(multiMsg);
}

/*---------------------------------------------------------------------------
    confirmCapBankControl

    Sends another porter request message to try to get the cap in the correct
    field state.  Just sends a command, does not look for var changes or
    update cap bank control status point.
---------------------------------------------------------------------------*/
void CtiCapController::confirmCapBankControl( CtiRequestMsg* pilRequest )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    getPILConnection()->WriteConnQue(pilRequest);
}
