/*-----------------------------------------------------------------------------
    Filename:  controller.cpp

    Programmer:  Josh Wolberg

    Description:    Source file for CtiCController
                    Once started CtiCController is reponsible
                    for determining if and when to run the
                    schedules provided by the CtiCCStrategyStore.
                    It runs schedules in collaboration with
                    CtiCCScheduleRunner.

    Initial Date:  8/23/2000

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "connection.h"
#include "message.h"
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
#include "controller.h"
#include "strategystore.h"
#include "executor.h"
#include "ctibase.h"
#include "yukon.h"
#include "netports.h"
#include "pointdefs.h"
#include "pointtypes.h"

#include <rw/thr/prodcons.h>

extern BOOL _CAP_DEBUG;

/* The singleton instance of CtiCController */
CtiCController* CtiCController::_instance = NULL;

/*---------------------------------------------------------------------------
    Instance

    Returns the single instance of CtiCController
---------------------------------------------------------------------------*/
CtiCController* CtiCController::Instance()
{
    if ( _instance == NULL )
        _instance = new CtiCController();

    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor

    Private to ensure that only one CtiCController is available through the
    instance member function
---------------------------------------------------------------------------*/
CtiCController::CtiCController()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _dispatchConnection = NULL;
    _pilConnection = NULL;
}

/*---------------------------------------------------------------------------
    Destructor

    Private to ensure that the single instance of CtiCController is not
    deleted
---------------------------------------------------------------------------*/
CtiCController::~CtiCController()
{
}

/*---------------------------------------------------------------------------
    start

    Starts the controller thread
---------------------------------------------------------------------------*/
void CtiCController::start()
{
    RWThreadFunction threadfunc = rwMakeThreadFunction( *this, &CtiCController::controlLoop );
    _stratthr = threadfunc;
    threadfunc.start();
}

/*---------------------------------------------------------------------------
    stop

    Stops the controller thread
---------------------------------------------------------------------------*/
void CtiCController::stop()
{
    if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Shutting down controller thread..." << endl;
    }

    if ( _stratthr.isValid() && _stratthr.requestCancellation() == RW_THR_ABORTED )
    {
        _stratthr.terminate();

        if( _CAP_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Forced to terminate." << endl;
        }
    } else
    {
        _stratthr.join();

        if( _CAP_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Controller thread shutdown" << endl;
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
void CtiCController::controlLoop()
{
    try
    {
        CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
        registerForPoints(store);
        store->setReregisterForPoints(FALSE);

        DOUBLE setpoint = 0.0;
        RWDBDateTime currentDateTime;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        CtiCCStrategyListMsg* strategyMsg = new CtiCCStrategyListMsg();
        while(TRUE)
        {
            if(_CAP_DEBUG)
            {
                if( (RWDBDateTime().seconds()%300) == 0 )
                {//every five minutes tell the user if the control thread is still alive
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Controller thread pulse" << endl;
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
                    CtiCCExecutor* executor = f.createExecutor(new CtiCCStateListMsg(store->StateList()));
                    executor->Execute(queue);
                    delete executor;
                    executor = f.createExecutor(new CtiCCAreaListMsg(store->AreaList()));
                    executor->Execute(queue);
                    delete executor;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                checkDispatch();
                checkPIL();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            RWOrdered &strategies = store->Strategies();

            RWOrdered &pointChanges = multiDispatchMsg->getData();
            RWOrdered &strategyChanges = strategyMsg->getStrategyList();
            currentDateTime.now();
            unsigned nowInSeconds = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
            for(LONG i=0;i<strategies.entries();i++)
            {
                CtiCCStrategy* current = (CtiCCStrategy*)strategies[i];

                try
                {
                    if( current->areAllCapBankStatusesReceived() )
                    {
                        if( ((current->ControlInterval() > 0) && (current->NextCheckTime() <= currentDateTime)) ||
                            ((current->ControlInterval() == 0) && (current->NewPointDataReceived())) )
                        {
                            if( current->RecentlyControlled() )
                            {
                                try
                                {
                                    if( current->isAlreadyControlled() ||
                                        ((current->LastOperation().seconds() + current->MinResponseTime()) <= currentDateTime.seconds()) )
                                    {
                                        current->setStrategyUpdated(current->capBankControlStatusUpdate(pointChanges));
                                        current->figureActualVarPointValue();
                                        current->setNewPointDataReceived(FALSE);
                                    }
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            else
                            {
                                try
                                {
                                    if( current->Status() == CtiCCStrategy::Enabled )
                                    {
                                        setpoint = current->figureCurrentSetPoint(nowInSeconds);

                                        if( (setpoint - current->Bandwidth() > current->CalculatedVarPointValue()) ||
                                            (setpoint + current->Bandwidth() < current->CalculatedVarPointValue()) )
                                        {
                                            CtiRequestMsg* request = NULL;
                                            try
                                            {
                                                if( setpoint < current->CalculatedVarPointValue() )
                                                {
                                                    if( _CAP_DEBUG )
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << RWTime().asString() << " - " << "Attempting to Reduce Var level in strategy: " << current->Name().data() << endl;
                                                    }

                                                    request = current->createDecreaseVarRequest(pointChanges);

                                                    if( request == NULL )
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << RWTime().asString() << " - " << "Can Not Reduce Var level for strategy: " << current->Name()
                                                            << " any further.  All cap banks are already in the Close state" << endl;
                                                    }
                                                }
                                                else
                                                {
                                                    if( _CAP_DEBUG )
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << RWTime().asString() << " - " << "Attempting to Increase Var level in strategy: " << current->Name().data() << endl;
                                                    }

                                                    request = current->createIncreaseVarRequest(pointChanges);

                                                    if( request == NULL )
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << RWTime().asString() << " - " << "Can Not Increase Var level for strategy: " << current->Name()
                                                            << " any further.  All cap banks are already in the Open state" << endl;
                                                    }
                                                }
                                            }
                                            catch(...)
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                            }

                                            try
                                            {
                                                if( request != NULL )
                                                {
                                                    multiPilMsg->insert(request);
                                                    current->setLastOperation(currentDateTime);
                                                }

                                                if( current->ActualVarPointId() > 0 )
                                                {
                                                    pointChanges.insert(new CtiPointDataMsg(current->ActualVarPointId(),current->ActualVarPointValue(),NormalQuality,AnalogPointType));
                                                }

                                                //regardless what happened the strategy should be should be sent to the client
                                                current->setStrategyUpdated(TRUE);
                                            }
                                            catch(...)
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                            }

                                        }
                                    }
                                    current->setNewPointDataReceived(FALSE);
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }

                            }

                            try
                            {
                                if( current->ControlInterval() > 0 )
                                    current->figureNextCheckTime();
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        else if( current->RecentlyControlled() )
                        {
                            try
                            {
                                if( ((current->LastOperation().seconds() + current->MinResponseTime()) <= currentDateTime.seconds()) )
                                {
                                    current->setStrategyUpdated(current->capBankControlStatusUpdate(pointChanges));
                                    current->figureActualVarPointValue();
                                    current->setNewPointDataReceived(FALSE);
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                try
                {
                    if( current->StrategyUpdated() )
                    {
                        strategyChanges.insert(current->replicate());
                        current->setStrategyUpdated(FALSE);
                        //current->dumpDynamicData();
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }

            try
            {
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
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
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
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                if( strategyChanges.entries() > 0 )
                {
                    store->dumpAllDynamicData();
                    CtiCCExecutorFactory f;
                    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                    CtiCCExecutor* executor = f.createExecutor(strategyMsg);
                    executor->Execute(queue);
                    delete executor;

                    strategyMsg = new CtiCCStrategyListMsg();
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep( 500 );
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        dout << RWTime().asString() << " - " << "Controller thread dead, Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    getDispatchConnection

    Returns a connection to Dispatch, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiCController::getDispatchConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _dispatchConnection == NULL )
    {
        //Set up the defaults
        INT dispatch_port = VANGOGHNEXUS;
        RWCString dispatch_host = "127.0.0.1";

        HINSTANCE hLib = LoadLibrary("cparms.dll");

        if (hLib)
        {
            char temp[80];

            CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

            bool trouble = FALSE;

            if ( (*fpGetAsString)("DISPATCH_MACHINE", temp, 80) )
            {
                if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Dispatch Machine:   " << temp << endl;
                }
                dispatch_host = temp;
            } else
                trouble = TRUE;

            if ( (*fpGetAsString)("DISPATCH_PORT", temp, 80) )
            {
                if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Dispatch Port:   " << temp << endl;
                }
                dispatch_port = atoi(temp);
            } else
                trouble = TRUE;


            if ( trouble )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Unable to find one or more config values for dispatch machine connection." << endl;
            }

            FreeLibrary(hLib);
        } else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Unable to load cparms dll." << endl;
        }

        //Connect to Dispatch
        _dispatchConnection = new CtiConnection( dispatch_port, dispatch_host );

        //Send a registration message to Dispatch
        CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, false );
        _dispatchConnection->WriteConnQue( registrationMsg );
    }

    return _dispatchConnection;
}

/*---------------------------------------------------------------------------
    getPILConnection

    Returns a connection to PIL, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiCController::getPILConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _pilConnection == NULL )
    {
        //Set up the defaults
        INT pil_port = PORTERINTERFACENEXUS;
        RWCString pil_host = "127.0.0.1";

        HINSTANCE hLib = LoadLibrary("cparms.dll");

        if (hLib)
        {
            char temp[80];

            CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

            bool trouble = FALSE;

            if ( (*fpGetAsString)("PIL_MACHINE", temp, 80) )
            {
                if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Pil Machine:   " << temp << endl;
                }
                pil_host = temp;
            } else
                trouble = TRUE;

            if ( (*fpGetAsString)("PIL_PORT", temp, 80) )
            {
                if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Pil Port:   " << temp << endl;
                }
                pil_port = atoi(temp);
            } else
                trouble = TRUE;


            if ( trouble )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Unable to find one or more config values for pil machine connection." << endl;
            }

            FreeLibrary(hLib);
        } else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Unable to load cparms dll." << endl;
        }

        //Connect to Pil
        _pilConnection = new CtiConnection( pil_port, pil_host );

        //Send a registration message to Pil
        CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, false );
        _pilConnection->WriteConnQue( registrationMsg );
    }

    return _pilConnection;
}

/*---------------------------------------------------------------------------
    checkDispatch

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCController::checkDispatch()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    bool done = FALSE;
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
void CtiCController::checkPIL()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    bool done = FALSE;
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

    Registers for all points of the strategies.
---------------------------------------------------------------------------*/
void CtiCController::registerForPoints(CtiCCStrategyStore* store)
{
    if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Registering for point changes." << endl;
    }

    RWOrdered &strategies = store->Strategies();
    CtiPointRegistrationMsg* regMsg = new CtiPointRegistrationMsg();
    for(LONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy *current = (CtiCCStrategy*)strategies[i];

        if( current->CalculatedVarPointId() > 0 )
        {
            regMsg->insert(current->CalculatedVarPointId());
        }
        
        RWOrdered &capbanks = current->CapBankList();

        for(LONG j=0;j<capbanks.entries();j++)
        {
            CtiCapBank *capBank = (CtiCapBank*)(current->CapBankList()[j]);

            if( capBank->StatusPointId() > 0 )
            {
                regMsg->insert(capBank->StatusPointId());
            }
            if( capBank->OperationAnalogPointId() > 0 )
            {
                regMsg->insert(capBank->OperationAnalogPointId());
            }
        }
    }

    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        getDispatchConnection()->WriteConnQue(regMsg);
    }
    regMsg = NULL;
}

/*---------------------------------------------------------------------------
    parseMessage

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCController::parseMessage(RWCollectable *message)
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
                if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Database change.  Setting reload flag." << endl;
                }

                dbChange = (CtiDBChangeMsg *)message;
                if( dbChange->getDatabase() == ChangeAllDb ||
                    dbChange->getDatabase() == ChangeDeviceDb ||
                    dbChange->getDatabase() == ChangePointDb ||
                    dbChange->getDatabase() == ChangeCapControlDb ||
                    dbChange->getDatabase() == ChangeStateGroupDb ||
                    dbChange->getDatabase() == ChangeInvalidDb )
                {
                    CtiCCStrategyStore::Instance()->notValid();
                    CtiCCStrategyStore::Instance()->setReregisterForPoints(TRUE);
                    if( dbChange->getDatabase() == ChangeStateGroupDb )
                    {

                        CtiCCExecutorFactory f;
                        RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
                        CtiCCExecutor* executor = f.createExecutor(new CtiCCStateListMsg(CtiCCStrategyStore::Instance()->StateList()));
                        executor->Execute(queue);
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
                if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Command Message received from Dispatch" << endl;
                }

                cmdMsg = (CtiCommandMsg *)message;
                if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
                {
                    if( _CAP_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime().asString() << " - " << "Replying to Are You There message." << endl;
                    }
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                        getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage());
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Command Message with type = "
                                  << cmdMsg->getOperation() << ") not supported from Dispatch" << endl;
                }
            }
            break;
        case MSG_MULTI:
            {
                msgMulti = (CtiMultiMsg *)message;
                RWOrdered &temp = msgMulti->getData( );
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
                dout << RWTime().asString() << " - " << "message->isA() = " << tempstr << endl;
                dout << RWTime().asString() << " - " << "Unknown message type: parseMessage(RWCollectable *message) in controller.cpp" << endl;
            }
    }
    return;
}

/*---------------------------------------------------------------------------
    pointDataMsg

    Handles point data messages and updates strategy point values.
---------------------------------------------------------------------------*/
void CtiCController::pointDataMsg( long pointID, double value, unsigned tags, RWTime& timestamp )
{
    if( _CAP_DEBUG )
   {
        char tempchar[64];
        RWCString outString = "Point data message received from Dispatch. ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Value:";
        int precision = 3;
        sprintf(tempchar,"%.*f",precision,value);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;
        outString += " Timestamp: ";
        outString += RWDBDateTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << outString.data() << endl;
   }

    bool found = FALSE;
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();

    RWOrdered &strategies = store->Strategies();

    for(int i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy *currentStrategy = (CtiCCStrategy*)strategies[i];

        if( currentStrategy->CalculatedVarPointId() == pointID )
        {
            if( timestamp > currentStrategy->LastPointUpdate() )
            {
                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - Last Point Update: " << currentStrategy->LastPointUpdate().asString() << endl;
                }*/
                currentStrategy->setLastPointUpdate(timestamp);
                if( currentStrategy->ControlInterval() == 0 )
                    currentStrategy->setNewPointDataReceived(TRUE);
            }
            currentStrategy->setCalculatedVarPointValue(value);
            currentStrategy->setStrategyUpdated(TRUE);
            currentStrategy->figureActualVarPointValue();
            if( currentStrategy->ActualVarPointId() > 0 )
                sendMessageToDispatch(new CtiPointDataMsg(currentStrategy->ActualVarPointId(),currentStrategy->ActualVarPointValue(),NormalQuality,AnalogPointType));
            break;
        }
        else if( !found )
        {
            RWOrdered &capBanks = currentStrategy->CapBankList();

            for(int j=0;j<capBanks.entries();j++)
            {
                CtiCapBank *currentCapBank = (CtiCapBank*)capBanks[j];

                if( currentCapBank->StatusPointId() == pointID )
                {
                    if( currentCapBank->ControlStatus() != (LONG)value )
                    {
                        currentStrategy->setStrategyUpdated(TRUE);
                    }
                    currentCapBank->setControlStatus((LONG)value);
                    currentCapBank->setTagsControlStatus((LONG)tags);
                    currentCapBank->setLastStatusChangeTime(timestamp);
                    currentCapBank->setStatusReceivedFlag(TRUE);
                    currentStrategy->figureActualVarPointValue();
                    if( currentStrategy->ActualVarPointId() > 0 )
                        sendMessageToDispatch(new CtiPointDataMsg(currentStrategy->ActualVarPointId(),currentStrategy->ActualVarPointValue(),NormalQuality,AnalogPointType));
                    found = TRUE;
                    break;
                }
                else if( currentCapBank->OperationAnalogPointId() == pointID )
                {
                    if( currentCapBank->Operations() != (LONG)value )
                    {
                        currentStrategy->setStrategyUpdated(TRUE);
                    }
                    currentCapBank->setOperations((LONG)value);
                    found = TRUE;
                    break;
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

    Handles porter return messages and updates the status of strategy cap
    bank controls.
---------------------------------------------------------------------------*/
void CtiCController::porterReturnMsg( long deviceId, RWCString commandString, int status, RWCString resultString )
{
    if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Porter return received." << endl;
    }

    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();

    RWOrdered &strategies = store->Strategies();

    bool found = FALSE;
    for(int i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy *currentStrategy = (CtiCCStrategy*)strategies[i];

        RWOrdered &capBanks = currentStrategy->CapBankList();

        for(int j=0;j<capBanks.entries();j++)
        {
            CtiCapBank *currentCapBank = (CtiCapBank*)capBanks[j];

            if( currentCapBank->ControlDeviceId() == deviceId )
            {
                if( currentStrategy->RecentlyControlled() &&
                    (currentStrategy->LastCapBankControlled() == currentCapBank->Id()) )
                {
                    if( status == 0 )
                    {
                        commandString.toLower();
                        if( commandString == "control open" )
                        {
                            currentCapBank->setControlStatus(CtiCapBank::OpenPending);
                        }
                        else if( commandString == "control close" )
                        {
                            currentCapBank->setControlStatus(CtiCapBank::ClosePending);
                        }
                    }
                    else
                    {
                        commandString.toLower();
                        if( commandString == "control open" )
                        {
                            currentCapBank->setControlStatus(CtiCapBank::OpenFail);
                        }
                        else if( commandString == "control close" )
                        {
                            currentCapBank->setControlStatus(CtiCapBank::CloseFail);
                        }
                        currentStrategy->setRecentlyControlled(FALSE);
                    }
                }
                found = TRUE;
                break;
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

    Handles signal messages and updates strategy tags.
---------------------------------------------------------------------------*/
void CtiCController::signalMsg( long pointID, unsigned tags, RWCString text, RWCString additional )
{
    if( _CAP_DEBUG )
   {
        char tempchar[64] = "";
        RWCString outString = "Signal Message received. Point ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << outString.data() << "  Text: "
                      << text << " Additional Info: " << additional << endl;
   }

    bool found = FALSE;
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();

    RWOrdered &strategies = store->Strategies();

    for(int i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy *currentStrategy = (CtiCCStrategy*)strategies[i];

        if( !found )
        {
            RWOrdered &capBanks = currentStrategy->CapBankList();

            for(int j=0;j<capBanks.entries();j++)
            {
                CtiCapBank *currentCapBank = (CtiCapBank*)capBanks[j];

                if( currentCapBank->StatusPointId() == pointID )
                {
                    if( currentCapBank->TagsControlStatus() != (LONG)tags )
                    {
                        currentStrategy->setStrategyUpdated(TRUE);
                    }
                    currentCapBank->setTagsControlStatus((LONG)tags);
                    found = TRUE;
                    break;
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
    the CtiCController's connection to dispatch.
---------------------------------------------------------------------------*/
void CtiCController::sendMessageToDispatch( CtiMessage* message )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    getDispatchConnection()->WriteConnQue(message);
}

/*---------------------------------------------------------------------------
    manualCapBankControl

    Handles a manual cap bank control sent by a client application.
---------------------------------------------------------------------------*/
void CtiCController::manualCapBankControl( CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg )
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
void CtiCController::confirmCapBankControl( CtiRequestMsg* pilRequest )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    getPILConnection()->WriteConnQue(pilRequest);
}
