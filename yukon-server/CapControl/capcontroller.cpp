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
#include "yukon.h"

#include "dbaccess.h"
#include "connection.h"
#include "ccmessage.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_signal.h"
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
#include "netports.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "devicetypes.h"
#include "resolvers.h" 
//#include "ccscheduler.h"
#include "mgr_paosched.h"
#include "utility.h"

#include "ccclientconn.h"
#include "ccclientlistener.h"
#include <rw/thr/prodcons.h>

extern ULONG _CC_DEBUG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;
#include <vector>
using std::vector;

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
CtiCapController::CtiCapController() : control_loop_delay(500), control_loop_inmsg_delay(0), control_loop_outmsg_delay(0)
{
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
    if( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }
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
    try
    {
        if ( _substationBusThread.isValid() && _substationBusThread.requestCancellation() == RW_THR_ABORTED )
        {
            _substationBusThread.terminate();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Forced to terminate." << endl;
            }
        }
        else
        {
            _substationBusThread.requestCancellation();
            _substationBusThread.join();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        if( _dispatchConnection!=NULL && _dispatchConnection->valid() )
        {
            _dispatchConnection->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        }
        _dispatchConnection->ShutdownConnection();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        if( _pilConnection!=NULL && _pilConnection->valid() )
        {
            _pilConnection->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        }
        _pilConnection->ShutdownConnection();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
            registerForPoints(*store->getCCSubstationBuses(CtiTime().seconds()));
        }
        store->setReregisterForPoints(FALSE);
        store->verifySubBusAndFeedersStates();

        CtiPAOScheduleManager* scheduleMgr = CtiPAOScheduleManager::getInstance();
        scheduleMgr->start();

        // 3 cparms, set for control loop wait scenerios - main, rcv msgs, send msgs
        loadControlLoopCParms();

        CtiTime currentDateTime;
        CtiCCSubstationBus_vec substationBusChanges;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        LONG lastThreadPulse = 0;
        LONG lastDailyReset = 0;
        while(TRUE)
        {
            long main_wait = control_loop_delay;
            bool received_message = false;
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

                currentDateTime.now();
                LONG secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
                ULONG secondsFrom1901 = currentDateTime.seconds();

                {
                    if( (secondsFrom1901%900) == 0 && secondsFrom1901 != lastThreadPulse )
                    {//every thirty minutes tell the user if the control thread is still alive
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Controller thread pulse" << endl;
                        }
                        lastThreadPulse = secondsFrom1901;
                        store->verifySubBusAndFeedersStates();
                    }
                    if( secondsFromBeginningOfDay <= 60 && secondsFrom1901 >= lastDailyReset+61 )
                    {
                        store->resetDailyOperations();
                        lastDailyReset = secondsFrom1901;
                    }
                }

                rwRunnable().serviceCancellation();

                CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(secondsFrom1901);

                try
                {
                    if( store->getReregisterForPoints() )
                    {
                        registerForPoints(ccSubstationBuses);
                        store->setReregisterForPoints(FALSE);
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                try
                {
                    checkDispatch(secondsFrom1901);
                    checkPIL(secondsFrom1901);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                RWOrdered& pointChanges = multiDispatchMsg->getData();
                RWOrdered& pilMessages = multiPilMsg->getData();

                for(LONG i=0;i<ccSubstationBuses.size();i++)
                {
                    CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                    try
                    {
                        currentSubstationBus->isPeakTime(currentDateTime);//put here to make sure the peak time flag is set correctly
                        if( currentSubstationBus->getStrategyId() > 0 &&
                            ( currentSubstationBus->isVarCheckNeeded(currentDateTime) ||
                              currentSubstationBus->isConfirmCheckNeeded() ||
                              currentSubstationBus->getVerificationFlag() )) 
                        {
                            if( currentSubstationBus->getRecentlyControlledFlag() )
                            {
                                try
                                {
                                    if( currentSubstationBus->isAlreadyControlled() ||
                                        currentSubstationBus->isPastMaxConfirmTime(currentDateTime) )
                                    {
                                        if( (_SEND_TRIES > 1 ||
                                             currentSubstationBus->getControlSendRetries() > 0 ||
                                             currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                                            !currentSubstationBus->isAlreadyControlled() &&
                                            currentSubstationBus->checkForAndPerformSendRetry(currentDateTime, pointChanges, pilMessages) )
                                        {
                                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        }
                                        else if( currentSubstationBus->capBankControlStatusUpdate(pointChanges) )
                                        {
                                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        }
                                    }
                                    else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
                                    {
                                        if( !currentSubstationBus->getDisableFlag() &&
                                            !currentSubstationBus->getWaiveControlFlag() &&
                                            stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::ManualOnlyControlMethod) )//intentionally left the ! off
                                        {
                                            currentSubstationBus->checkForAndProvideNeededControl(currentDateTime, pointChanges, pilMessages);
                                        }
                                    }
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            else if (currentSubstationBus->getVerificationFlag()) //verification Flag set!!!
                            {
                                if (currentSubstationBus->isBusPerformingVerification())
                                {
                                    if (currentSubstationBus->isVerificationAlreadyControlled() ||
                                        currentSubstationBus->isVerificationPastMaxConfirmTime(currentDateTime))
                                    {

                                        if( (_SEND_TRIES > 1 ||
                                             currentSubstationBus->getControlSendRetries() > 0 ||
                                             currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                                            !currentSubstationBus->isVerificationAlreadyControlled() &&
                                            currentSubstationBus->checkForAndPerformSendRetry(currentDateTime, pointChanges, pilMessages) )
                                        {
                                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        }
                                        else if(!currentSubstationBus->capBankVerificationStatusUpdate(pointChanges)  && 
                                           currentSubstationBus->getCurrentVerificationCapBankId() != -1)
                                        {
                                            currentSubstationBus->sendNextCapBankVerificationControl(currentDateTime, pointChanges, pilMessages);
                                        }
                                        else
                                        {
                                            if(currentSubstationBus->areThereMoreCapBanksToVerify())
                                            {
                                                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPAOId()<<" CB-"<<currentSubstationBus->getCurrentVerificationCapBankId() << endl;
                                                }

                                                currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, pilMessages);
                                            }
                                            else
                                            {
                                                //reset VerificationFlag
                                                currentSubstationBus->setVerificationFlag(FALSE);
                                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                CtiCCExecutorFactory f;
                                                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPAOId(),0, -1));
                                                executor->Execute();
                                                delete executor;
                                                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                {
                                                   CtiLockGuard<CtiLogger> logger_guard(dout);
                                                   dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPAOId() << endl;
                                                } 
                                                //ALSO need to reset verification flags/ busperforming verificationflags/ on feeders and capbanks!!!
                                            }
                                        }

                                    }
                                    else // WAIT!!!
                                    {

                                    }
                                }
                                else
                                {
                                    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                    {
                                       CtiLockGuard<CtiLogger> logger_guard(dout);
                                       dout << CtiTime() << " - Performing VERIFICATION ON: subBusID: "<<currentSubstationBus->getPAOId() << endl;
                                    }
                                    int strategy = (long)currentSubstationBus->getVerificationStrategy();

                                    currentSubstationBus->setCapBanksToVerifyFlags(strategy);
                                    if(currentSubstationBus->areThereMoreCapBanksToVerify())
                                    {
                                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                        {
                                             CtiLockGuard<CtiLogger> logger_guard(dout);
                                             dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPAOId()<<" CB-"<<currentSubstationBus->getCurrentVerificationCapBankId() << endl;
                                        }
                                        currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, pilMessages);
                                        //currentSubstationBus->setPerformingVerificationFlag(TRUE);
                                        // 
                                        //currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    }
                                    else
                                    {
                                        //reset VerificationFlag
                                        currentSubstationBus->setVerificationFlag(FALSE);
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        CtiCCExecutorFactory f;
                                        CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPAOId(),0, -1));
                                        executor->Execute();
                                        delete executor;

                                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                        {
                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                           dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPAOId() << endl;
                                        } 

                                        //ALSO need to reset verification flags/ busperforming verificationflags/ on feeders and capbanks!!!
                                    }
                                    //currentSubstationBus->setPerformingVerificationFlag(TRUE);

                                }
                            }  
                            else if( currentSubstationBus->isVarCheckNeeded(currentDateTime) )
                            {//not recently controlled and var check needed
                                try
                                {
                                    if( !currentSubstationBus->getDisableFlag() &&
                                        !currentSubstationBus->getWaiveControlFlag() &&
                                        stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::ManualOnlyControlMethod) )//intentionally left the ! off
                                    {
                                        currentSubstationBus->checkForAndProvideNeededControl(currentDateTime, pointChanges, pilMessages);
                                    }
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }

                            try
                            {
                                //so we don't do this over and over we need to clear out
                                currentSubstationBus->clearOutNewPointReceivedFlags();
                                if( currentSubstationBus->isVarCheckNeeded(currentDateTime) &&
                                    currentSubstationBus->getControlInterval() > 0 )
                                {
                                    currentSubstationBus->figureNextCheckTime();
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                    try
                    {
                        //accumulate all buses with any changes into msg for all clients
                        if( currentSubstationBus->getBusUpdatedFlag() )
                        {
                            substationBusChanges.push_back(currentSubstationBus);
                            currentSubstationBus->setBusUpdatedFlag(FALSE);
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                
                try
                {
                    //send point changes to dispatch
                    if( pointChanges.entries() > 0 )
                    {
                        multiDispatchMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
                        getDispatchConnection()->WriteConnQue(multiDispatchMsg);
                        multiDispatchMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                try
                {
                    //send pil commands to porter
                    if( multiPilMsg->getCount() > 0 )
                    {
                        multiPilMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
                        getPILConnection()->WriteConnQue(multiPilMsg);
                        multiPilMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                /*******************************************************************/
                try
                {
                    if( substationBusChanges.size() > 0 )
                    {
                        //send the substation bus changes to all cap control clients
                        try
                        {
                            store->dumpAllDynamicData();
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        CtiCCExecutorFactory f1;
                        CtiCCExecutor* executor1 = f1.createExecutor(new CtiCCSubstationBusMsg(substationBusChanges));
                        try
                        {
                            executor1->Execute();
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        try
                        {
                            delete executor1;
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        try
                        {
                            substationBusChanges.clear();//TS//DO NOT DESTROY
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                
                /** JULIE Adding _clientMsgQueue to hopefully solve deadlock issue **/
                try
                {
                    CtiTime tempTime;
                    CtiCCExecutorFactory f;
                    RWCollectable* clientMsg = NULL;

                    try
                    {
                        tempTime.now();
                        while(_inClientMsgQueue.canRead())
                        {

                            try
                            {   
                                clientMsg = _inClientMsgQueue.read();
                                try
                                {
                                    if( clientMsg != NULL )
                                    {
                                        try
                                        {
                                            CtiCCExecutor* executor = f.createExecutor( (CtiMessage*) clientMsg );
                                            try
                                            {
                                                executor->Execute();
                                            }
                                            catch(...)
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                            }
                                            try
                                            {
                                                delete executor;
                                            }
                                            catch(...)
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                            }
                                        }
                                        catch(...)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                        }

                                        //delete clientMsg;
                                    }
                                    if (CtiTime::now().seconds() - tempTime.seconds() <= 1) 
                                    {
                                        break;
                                    }
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        };
                        //delete clientMsg;

                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }

                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            /********************************************************************/
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
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    getDispatchConnection

    Returns a connection to Dispatch, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiCapController::getDispatchConnection()
{
    try
    {
        if( _dispatchConnection == NULL || (_dispatchConnection != NULL && _dispatchConnection->verifyConnection()) )
        {
            string str;
            char var[128];
            string dispatch_host = "127.0.0.1";

            strcpy(var, "DISPATCH_MACHINE");
            if( !(str = gConfigParms.getValueAsString(var)).empty() )
            {
                dispatch_host = str.c_str();
                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << var << ":  " << dispatch_host << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            int dispatch_port = VANGOGHNEXUS;

            strcpy(var, "DISPATCH_PORT");
            if( !(str = gConfigParms.getValueAsString(var)).empty() )
            {
                dispatch_port = atoi(str.c_str());
                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << var << ":  " << dispatch_port << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            //throw away the old connection if there was one that couldn't be verified
            if( _dispatchConnection != NULL && _dispatchConnection->verifyConnection() )
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Dispatch Connection Hickup in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                delete _dispatchConnection;
                _dispatchConnection = NULL;
            }

            if( _dispatchConnection == NULL )
            {
                //Connect to Dispatch
                _dispatchConnection = new CtiConnection( dispatch_port, dispatch_host );

                //Send a registration message to Dispatch
                CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, FALSE );
                _dispatchConnection->WriteConnQue( registrationMsg );
            }
        }

        return _dispatchConnection;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

        return NULL;
    }
}

/*---------------------------------------------------------------------------
    getPILConnection

    Returns a connection to PIL, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiCapController::getPILConnection()
{
    try
    {
        if( _pilConnection == NULL || (_pilConnection != NULL && _pilConnection->verifyConnection()) )
        {
            string str;
            char var[128];
            string pil_host = "127.0.0.1";

            strcpy(var, "PIL_MACHINE");
            if( !(str = gConfigParms.getValueAsString(var)).empty() )
            {
                pil_host = str.c_str();
                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << var << ":  " << pil_host << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            INT pil_port = PORTERINTERFACENEXUS;

            strcpy(var, "PIL_PORT");
            if( !(str = gConfigParms.getValueAsString(var)).empty() )
            {
                pil_port = atoi(str.c_str());
                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << var << ":  " << pil_port << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            //throw away the old connection if there was one that couldn't be
            if( _pilConnection != NULL && _pilConnection->verifyConnection() )
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Porter Connection Hickup in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                delete _pilConnection;
                _pilConnection = NULL;
            }

            if( _pilConnection == NULL )
            {
                //Connect to Pil
                _pilConnection = new CtiConnection( pil_port, pil_host );

                //Send a registration message to Pil
                CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, FALSE );
                _pilConnection->WriteConnQue( registrationMsg );
            }
        }

        return _pilConnection;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

        return NULL;
    }
}

/*---------------------------------------------------------------------------
    checkDispatch

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::checkDispatch(ULONG secondsFrom1901)
{
    BOOL done = FALSE;
    CtiConnection* tempPtrDispatchConn = getDispatchConnection();
    do
    {
        try
        {
            CtiMessage* in = (CtiMessage*) tempPtrDispatchConn->ReadConnQue(0);

            if ( in != NULL )
            {
                parseMessage(in,secondsFrom1901);
                delete in;
            }
            else
                done = TRUE;
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    while(!done);
}

/*---------------------------------------------------------------------------
    checkPIL

    Reads off the PIL connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::checkPIL(ULONG secondsFrom1901)
{
    BOOL done = FALSE;
    CtiConnection* tempPtrPorterConn = getPILConnection();
    do
    {
        try
        {
            CtiMessage* in = (CtiMessage*) tempPtrPorterConn->ReadConnQue(0);

            if ( in != NULL )
            {
                parseMessage(in,secondsFrom1901);
                delete in;
            }
            else
                done = TRUE;
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    while(!done);
}

/*---------------------------------------------------------------------------
    registerForPoints

    Registers for all points of the substations buses.
---------------------------------------------------------------------------*/
void CtiCapController::registerForPoints(const CtiCCSubstationBus_vec& subBuses)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Registering for point changes." << endl;
    }

    CtiPointRegistrationMsg* regMsg;// = new CtiPointRegistrationMsg();
    string simple_registration = gConfigParms.getValueAsString("CAP_CONTROL_SIMPLE_REGISTRATION", "false");
    if(simple_registration == "true" || simple_registration == "TRUE")
    {   
        //register for all points
        regMsg = new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
    }
    else
    { 
        //register for each point specifically
        regMsg = new CtiPointRegistrationMsg();

        for(LONG i=0;i<subBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)subBuses.at(i);

            if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
            {
                regMsg->insert(currentSubstationBus->getCurrentVarLoadPointId());
            }
            if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
            {
                regMsg->insert(currentSubstationBus->getCurrentWattLoadPointId());
            }
            if (currentSubstationBus->getCurrentVoltLoadPointId() > 0)
            {
                regMsg->insert(currentSubstationBus->getCurrentVoltLoadPointId());
            }
            if (currentSubstationBus->getEstimatedVarLoadPointId() > 0)
            {
                regMsg->insert(currentSubstationBus->getEstimatedVarLoadPointId());
            }
            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
            {
                regMsg->insert(currentSubstationBus->getDailyOperationsAnalogPointId());
            }
            if (currentSubstationBus->getPowerFactorPointId() > 0)
            {
                regMsg->insert(currentSubstationBus->getPowerFactorPointId());
            }
            if (currentSubstationBus->getEstimatedPowerFactorPointId() > 0)
            {
                regMsg->insert(currentSubstationBus->getEstimatedPowerFactorPointId());
            }
            
            CtiFeeder_vec &ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0; j < ccFeeders.size(); j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));

                if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                {
                    regMsg->insert(currentFeeder->getCurrentVarLoadPointId());
                }
                if( currentFeeder->getCurrentWattLoadPointId() > 0 )
                {
                    regMsg->insert(currentFeeder->getCurrentWattLoadPointId());
                }
                if ( currentFeeder->getCurrentVoltLoadPointId() > 0)
                {
                    regMsg->insert(currentFeeder->getCurrentVoltLoadPointId());
                }
                if (currentFeeder->getEstimatedVarLoadPointId() > 0)
                {
                    regMsg->insert(currentFeeder->getEstimatedVarLoadPointId());
                }
                if (currentFeeder->getDailyOperationsAnalogPointId() > 0)
                {
                    regMsg->insert(currentFeeder->getDailyOperationsAnalogPointId());
                }
                if (currentFeeder->getPowerFactorPointId() > 0)
                {
                    regMsg->insert(currentFeeder->getPowerFactorPointId());
                }
                if (currentFeeder->getEstimatedPowerFactorPointId() > 0)
                {
                    regMsg->insert(currentFeeder->getEstimatedPowerFactorPointId());
                }                

                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
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

    }
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - End Registering for point changes." << endl;
    }

    /*for(LONG x=0;x<regMsg->getCount();x++)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        LONG pid = regMsg->operator [](x);
        dout << CtiTime() << " - Registered for Point Id: " << pid << endl;
    }*/
    try
    {
        getDispatchConnection()->WriteConnQue(regMsg);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    regMsg = NULL;
}

/*---------------------------------------------------------------------------
    parseMessage

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::parseMessage(RWCollectable *message, ULONG secondsFrom1901)
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
                          (dbChange->getDatabase() == ChangeStateGroupDb && dbChange->getId() == 3) ||
                          dbChange->getDatabase() == ChangeCBCStrategyDb ||
                          dbChange->getDatabase() == ChangePAOScheduleDB ))
                    {
                        if( _CC_DEBUG & CC_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Relevant database change.  Setting reload flag." << endl;
                        }

                        if( dbChange->getTypeOfChange() == ChangeTypeDelete &&
                            resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SUBSTATION_BUS )
                        {
                            CtiCCSubstationBusStore::getInstance()->setWasSubBusDeletedFlag(TRUE);
                        }
                        //CtiCCSubstationBusStore::getInstance()->setValid(false);
                        //CtiPAOScheduleManager::getInstance()->setValid(false);

                        long objType = CtiCCSubstationBusStore::Unknown;
                        long changeId = dbChange->getId();
                        if (dbChange->getDatabase() == ChangeCBCStrategyDb)
                        {
                            objType = CtiCCSubstationBusStore::Strategy;
                        }
                        else if (dbChange->getDatabase() == ChangePAODb && !(stringCompareIgnoreCase(dbChange->getObjectType(),"cap bank")))
                        {
                            objType = CtiCCSubstationBusStore::CapBank;
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SUBSTATION_BUS)
                        {
                            objType = CtiCCSubstationBusStore::SubBus;

                            CtiPAOScheduleManager::getInstance()->setValid(false);  
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_FEEDER)
                        {
                            objType =  CtiCCSubstationBusStore::Feeder;
                        }
                        else if (dbChange->getDatabase() == ChangePAOScheduleDB)
                        {
                            CtiPAOScheduleManager::getInstance()->setValid(false);  
                        }
                        else if (dbChange->getDatabase() == ChangePointDb)
                        {   
                            if (CtiCCSubstationBusStore::getInstance()->findSubBusByPointID(dbChange->getId(), 0) != NULL)
                            {
                                CtiCCSubstationBusPtr sub = CtiCCSubstationBusStore::getInstance()->findSubBusByPointID(dbChange->getId(), 0);
                                objType = CtiCCSubstationBusStore::SubBus;
                                changeId = sub->getPAOId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findFeederByPointID(dbChange->getId(), 0) != NULL)
                            {
                                CtiCCFeederPtr feed = CtiCCSubstationBusStore::getInstance()->findFeederByPointID(dbChange->getId(), 0);
                                objType = CtiCCSubstationBusStore::Feeder;
                                changeId = feed->getPAOId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(dbChange->getId(), 0) != NULL)
                            {
                                CtiCCCapBankPtr cap = CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(dbChange->getId(), 0);
                                objType = CtiCCSubstationBusStore::CapBank;
                                changeId = cap->getPAOId();
                            }
                        }
                        else if (objType == CtiCCSubstationBusStore::Unknown)
                        {
                            CtiCCSubstationBusStore::getInstance()->setValid(false);
                            CtiPAOScheduleManager::getInstance()->setValid(false);  
                        }

                        CC_DBRELOAD_INFO reloadInfo = {changeId, dbChange->getTypeOfChange(), objType};

                        CtiCCSubstationBusStore::getInstance()->insertDBReloadList(reloadInfo);
                    }
                }
                break;
            case MSG_POINTDATA:
                {
                    pData = (CtiPointDataMsg *)message;
                    pointDataMsg( pData->getId(), pData->getValue(), pData->getQuality(), pData->getTags(), pData->getTime(), secondsFrom1901 );
                }
                break;
            case MSG_PCRETURN:
                {
                    pcReturn = (CtiReturnMsg *)message;
                    porterReturnMsg( pcReturn->DeviceId(), pcReturn->CommandString(), pcReturn->Status(), pcReturn->ResultString(), secondsFrom1901 );
                }
                break;
            case MSG_COMMAND:
                {
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Command Message received from Dispatch" << endl;
                    }

                    cmdMsg = (CtiCommandMsg *)message;
                    if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
                    {
                        try
                        {
                            getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage());
                            if( _CC_DEBUG & CC_DEBUG_STANDARD )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Replied to Are You There message." << endl;
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Received not supported Command Message from Dispatch with Operation: "
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
                        parseMessage(temp[i],secondsFrom1901);
                    }
                }
                break;
            case MSG_SIGNAL:
                {
                    signal = (CtiSignalMsg *)message;
                    signalMsg( signal->getId(), signal->getTags(), signal->getText(), signal->getAdditionalInfo(), secondsFrom1901 );
                }
                break;
            case MSG_TAG:
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Received Tag Message from Porter.  Check Comm Routes."<< endl;
                }
                break;
            default:
                {
                    char tempstr[64] = "";
                    _itoa(message->isA(),tempstr,10);
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - message->isA() = " << tempstr << endl;
                    dout << CtiTime() << " - Unknown message type: parseMessage(RWCollectable *message) in controller.cpp" << endl;
                }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return;
}


DOUBLE convertPowerFactorToSend(DOUBLE powerFactor)
{
    DOUBLE returnPowerFactor = powerFactor;
    if( powerFactor > 1 )
    {
        returnPowerFactor = powerFactor - 2;
    }

    //returnPowerFactor = returnPowerFactor * 100.0;

    return returnPowerFactor;
}

/*---------------------------------------------------------------------------
    pointDataMsg

    Handles point data messages and updates substation bus point values.
---------------------------------------------------------------------------*/
void CtiCapController::pointDataMsg( long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901 )
{
    if( _CC_DEBUG & CC_DEBUG_POINT_DATA )
    {
        char tempchar[80];
        string outString = "Point Data, ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Val:";
        int precision = 3;
        _snprintf(tempchar,80,"%.*f",precision,value);
        outString += tempchar;
        outString += " Time: ";
        outString += CtiTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << outString.c_str() << endl;
    }

    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    //CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(secondsFrom1901);


    try
    {   CtiCCSubstationBus* currentSubstationBus = NULL;
        int subCount = store->getNbrOfSubBusesWithPointID(pointID);
        while (subCount > 0)
        {
            try
            {
                currentSubstationBus = store->findSubBusByPointID(pointID, subCount);
                if (currentSubstationBus != NULL)
                {
                    if( currentSubstationBus->getCurrentVarLoadPointId() == pointID )
                    {
                        if( timestamp > currentSubstationBus->getLastCurrentVarPointUpdateTime() )
                        {
                            currentSubstationBus->setLastCurrentVarPointUpdateTime(timestamp);
                            currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                        }
                        currentSubstationBus->setCurrentVarLoadPointValue(value);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentSubstationBus->setCurrentVarPointQuality(quality);
                        if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                        {
                            sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }

                        if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
                        {
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::PF_BY_KQControlUnits) )
                            {
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(value,currentSubstationBus->getCurrentWattLoadPointValue()));
                            }
                            currentSubstationBus->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            if( currentSubstationBus->getPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                        }
                        //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                        else if( !( !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::KVARControlUnits) ||
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::VoltControlUnits)) )
                        {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Watt Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        found = TRUE;
                        //break;
                    }
                    else if( currentSubstationBus->getCurrentWattLoadPointId() == pointID )
                    {                         
                        if (!stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::PF_BY_KQControlUnits))
                        {
                            DOUBLE tempKQ = currentSubstationBus->convertKVARToKQ(value,currentSubstationBus->getCurrentWattLoadPointValue());
                            currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(tempKQ,value));
                        }

                        currentSubstationBus->setCurrentWattLoadPointValue(value);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);

                        if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
                        {
                            currentSubstationBus->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            if( currentSubstationBus->getPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                        }
                        //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                        else if( stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::KVARControlUnits) )
                        {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Var Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        found = TRUE;
                       // break;
                    }
                    else if( currentSubstationBus->getCurrentVoltLoadPointId() == pointID )
                    {

                        currentSubstationBus->setCurrentVoltLoadPointValue(value);
                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        found = TRUE;
                       // break;
                    }
                    else
                    {
                        //print something out about being not found...
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            subCount--;
        }
        int feederCount = store->getNbrOfFeedersWithPointID(pointID);
        while (feederCount > 0)
        {
            try
            {
                currentSubstationBus = NULL;
                CtiCCFeederPtr currentFeeder = store->findFeederByPointID(pointID, feederCount);
                if (currentFeeder != NULL)
                {
                    long subBusId = store->findSubBusIDbyFeederID(currentFeeder->getPAOId());
                    if (subBusId != NULL)
                    {
                        currentSubstationBus = store->findSubBusByPAObjectID(subBusId);
                    }

                    if (currentSubstationBus != NULL)
                    {
                        if( currentFeeder->getCurrentVarLoadPointId() == pointID )
                        {
                            if( timestamp > currentFeeder->getLastCurrentVarPointUpdateTime() )
                            {
                                currentFeeder->setLastCurrentVarPointUpdateTime(timestamp);
                                currentFeeder->setNewPointDataReceivedFlag(TRUE);
                            }
                            currentFeeder->setCurrentVarLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentFeeder->figureEstimatedVarLoadPointValue();
                            currentFeeder->setCurrentVarPointQuality(quality);
                            if( currentFeeder->getEstimatedVarLoadPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedVarLoadPointId(),currentFeeder->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                            }

                            if( currentFeeder->getCurrentWattLoadPointId() > 0 )
                            {
                                if( !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::PF_BY_KQControlUnits) )
                                {
                                    currentFeeder->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(value,currentFeeder->getCurrentWattLoadPointValue()));
                                }
                                currentFeeder->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getCurrentVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentFeeder->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                if( currentFeeder->getPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                            }
                            //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                            else if( !( !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::KVARControlUnits) ||
                                        !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::VoltControlUnits) )) 
                            {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Watt Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                            found = TRUE;
                           // break;
                        }
                        else if( currentFeeder->getCurrentWattLoadPointId() == pointID )
                        {
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::PF_BY_KQControlUnits) )
                            {
                                DOUBLE tempKQ = currentSubstationBus->convertKVARToKQ(value,currentFeeder->getCurrentWattLoadPointValue());
                                currentFeeder->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(tempKQ,value));
                            }

                            currentFeeder->setCurrentWattLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);

                            if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                            {
                                currentFeeder->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getCurrentVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentFeeder->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                if( currentFeeder->getPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                            }
                            //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                            else if( stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::KVARControlUnits) )
                            {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Var Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                            found = TRUE;
                           // break;
                        }
                        else if( currentFeeder->getCurrentVoltLoadPointId() == pointID )
                        {
                            currentFeeder->setCurrentVoltLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);

                            found = TRUE;
                           // break;
                        }
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            feederCount--;
        }

        int capCount = store->getNbrOfCapBanksWithPointID(pointID);
        while (capCount > 0)
        {                  
            try
            {
                CtiCCCapBank* currentCapBank = store->findCapBankByPointID(pointID, capCount);
                currentSubstationBus = NULL;
                CtiCCFeeder* currentFeeder = NULL;
                if (currentCapBank != NULL)
                {
                    long subBusId = store->findSubBusIDbyCapBankID(currentCapBank->getPAOId());
                    if (subBusId != NULL)
                    {
                        currentSubstationBus = store->findSubBusByPAObjectID(subBusId);
                        if (currentSubstationBus != NULL)
                        {
                            long feederId = store->findFeederIDbyCapBankID(currentCapBank->getPAOId());
                            if (feederId != NULL)
                            {
                                currentFeeder = store->findFeederByPAObjectID(feederId);
                            }
                        }
                    }

                    if (currentSubstationBus != NULL && currentFeeder != NULL)
                    {                                                        
                        vector<CtiCCFeeder*>& ccFeeders = currentSubstationBus->getCCFeeders();

                        if( currentCapBank->getStatusPointId() == pointID )
                        {
                            if( timestamp > currentCapBank->getLastStatusChangeTime() ||
                                currentCapBank->getControlStatus() != (LONG)value ||
                                currentCapBank->getTagsControlStatus() != (LONG)tags )
                            {
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }

                            currentCapBank->setControlStatus((LONG)value);
                            currentCapBank->setTagsControlStatus((LONG)tags);
                            currentCapBank->setLastStatusChangeTime(timestamp);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));

                            if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
                            {
                                currentSubstationBus->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                                if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                            }
                            if( currentFeeder->getCurrentWattLoadPointId() > 0 )
                            {
                                currentFeeder->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                            }
                            found = TRUE;
                           // break;
                        }
                        else if( currentCapBank->getOperationAnalogPointId() == pointID )
                        {
                            if( timestamp > currentCapBank->getLastStatusChangeTime() ||
                                currentCapBank->getTotalOperations() != (LONG)value )
                            {
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentCapBank->setTotalOperations((LONG)value);
                            found = TRUE;
                            //break;
                        }
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            capCount--;

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return;
}

/*---------------------------------------------------------------------------
    porterReturn

    Handles porter return messages and updates the status of substation bus
    cap bank controls.
---------------------------------------------------------------------------*/
void CtiCapController::porterReturnMsg( long deviceId, const string& _commandString, int status, const string& _resultString, ULONG secondsFrom1901 )
{
    string commandString = _commandString;

//This Variable is passed in but is not ever called on.   -TS
    string resultString  = _resultString;//Not used as far as I can see


    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Porter return received." << endl;
    }

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(secondsFrom1901);

    BOOL found = FALSE;
    for(int i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);

        vector<CtiCCFeeder*>& ccFeeders = currentSubstationBus->getCCFeeders();

        for(int j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(int k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                if( currentCapBank->getControlDeviceId() == deviceId )
                {
                    if( currentSubstationBus->getRecentlyControlledFlag() &&
                        (currentFeeder->getLastCapBankControlledDeviceId() == currentCapBank->getPAOId()) )
                    {
                        if( status == 0 )
                        {
                            std::transform(commandString.begin(), commandString.end(), commandString.begin(), tolower);
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
                            std::transform(commandString.begin(), commandString.end(), commandString.begin(), tolower);
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
                                dout << CtiTime() << " - Porter Return caused a Cap Bank to go into Failed State!  Bus: " << currentSubstationBus->getPAOName() << ", Feeder: " << currentFeeder->getPAOName()<< ", CapBank: " << currentCapBank->getPAOName() << endl;
                            }

                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                string additional = ("Sub: ");
                                additional += currentSubstationBus->getPAOName();
                                additional += "  Feeder: ";
                                additional += currentFeeder->getPAOName();

                                string text = string("Porter Return caused a Cap Bank to go into Failed State!");
                                sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,"cap control"));
                                sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                currentCapBank->setLastStatusChangeTime(CtiTime());
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                              << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                            }
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                    }
                    found = TRUE;
                    break;
                }
            }
        }
        if( found )
        {
            if (status != 0)
            {              
                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
                {
                    for(j=0;j<ccFeeders.size();j++)
                    {
                        if (((CtiCCFeeder*)ccFeeders[j])->getRecentlyControlledFlag())
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                        }
                    }
                }
            }
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    signalMessage

    Handles signal messages and updates substation bus tags.
---------------------------------------------------------------------------*/
void CtiCapController::signalMsg( long pointID, unsigned tags, const string& text, const string& additional, ULONG secondsFrom1901 )
{
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        char tempchar[64] = "";
        string outString = "Signal Message received. Point ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << outString << "  Text: "
                      << text << " Additional Info: " << additional << endl;
    }

    /*BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(secondsFrom1901);

    for(int i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);

        if( !found )
        {
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(int k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getStatusPointId() == pointID )
                    {
                        if( currentCapBank->getTagsControlStatus() != (LONG)tags )
                        {
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                        currentCapBank->setTagsControlStatus((LONG)tags);
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
    }*/

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

    try
    {
        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Sending following message to Dispatch:" << endl;
            message->dump();
        }*/
        getDispatchConnection()->WriteConnQue(message);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    manualCapBankControl

    Handles a manual cap bank control sent by a client application.
---------------------------------------------------------------------------*/
void CtiCapController::manualCapBankControl( CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    try
    {
        getPILConnection()->WriteConnQue(pilRequest);
        if( multiMsg->getCount() > 0 )
            getDispatchConnection()->WriteConnQue(multiMsg);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
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
    try
    {
        getPILConnection()->WriteConnQue(pilRequest);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

RWPCPtrQueue< RWCollectable > &CtiCapController::getInClientMsgQueueHandle()
{
    return _inClientMsgQueue;
} 
RWPCPtrQueue< RWCollectable > &CtiCapController::getOutClientMsgQueueHandle()
{
    return _outClientMsgQueue;
} 

/*
 * loadControlLoopCParms
 * initialize control loop delay cparms that control behavior of the main loop
 * these cparms are optional.
 */
void CtiCapController::loadControlLoopCParms()
{
    string str;
    char var[128];

    strcpy(var, "CAP_CONTROL_CONTROL_LOOP_NORMAL_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
    control_loop_delay = atoi(str.c_str());
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << var << ":  " << str << endl;
    }
    }

    strcpy(var, "CAP_CONTROL_CONTROL_LOOP_INMSG_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
    control_loop_inmsg_delay = atoi(str.c_str());
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << var << ":  " << str << endl;
    }
    }

    strcpy(var, "CAP_CONTROL_CONTROL_LOOP_OUTMSG_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
    control_loop_outmsg_delay = atoi(str.c_str());
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << var << ":  " << str << endl;
    }
    }
}

