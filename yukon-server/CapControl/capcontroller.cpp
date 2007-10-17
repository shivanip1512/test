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
#include "thread_monitor.h"
#include "utility.h"
#include "ctitime.h"


#include "ccclientconn.h"
#include "ccclientlistener.h"
#include <rw/thr/prodcons.h>


#include <vector>
using std::vector;
using std::multimap;

extern ULONG _CC_DEBUG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;
extern BOOL _ALLOW_PARALLEL_TRUING;
extern ULONG _DB_RELOAD_WAIT;
extern ULONG _LINK_STATUS_TIMEOUT;

extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _POINT_AGE;
extern ULONG _SCAN_WAIT_EXPIRE;
extern BOOL _RETRY_FAILED_BANKS;
extern BOOL _END_DAY_ON_TRIP;
extern BOOL _LOG_MAPID_INFO;


//DLLEXPORT BOOL  bGCtrlC = FALSE;

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
                dout << CtiTime() << " - Substation Bus Thread forced to terminate." << endl;
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
        CtiTime registerTimeElapsed;
        CtiCCSubstationBus_vec substationBusChanges;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
        CtiMultiMsg* multiCCEventMsg = new CtiMultiMsg();
        LONG lastThreadPulse = 0;
        LONG lastDailyReset = 0;
        BOOL waitToBroadCastEverything = FALSE;

        CtiTime rwnow;
        CtiTime announceTime((unsigned long) 0);
        CtiTime tickleTime((unsigned long) 0);
        CtiTime fifteenMinCheck = nextScheduledTimeAlignedOnRate( currentDateTime,  900);



        while(TRUE)
        {
            long main_wait = control_loop_delay;
            bool received_message = false;
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

                currentDateTime = CtiTime();
                LONG secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
                ULONG secondsFrom1901 = currentDateTime.seconds();

                {
                    if( currentDateTime.seconds() > fifteenMinCheck.seconds() && secondsFrom1901 != lastThreadPulse)
                    {//every  fifteen minutes tell the user if the control thread is still alive
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Controller refreshing CPARMS" << endl;
                        }
                        refreshCParmGlobals(true);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Controller thread pulse" << endl;
                        }
                        lastThreadPulse = secondsFrom1901;
                        fifteenMinCheck = nextScheduledTimeAlignedOnRate( currentDateTime,  900);
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

                {
                    if( (secondsFrom1901%60) == 0 && secondsFrom1901 != lastThreadPulse )
                    {
                        for(LONG i=0;i<ccSubstationBuses.size();i++)
                        {
                            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
                            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                            BOOL peakFlag = currentSubstationBus->isPeakTime(currentDateTime);
                            for(LONG j=0;j<ccFeeders.size();j++)
                            {
                                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                                if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod)  &&
                                    stringCompareIgnoreCase(currentFeeder->getStrategyName(), "(none)") &&
                                    (currentFeeder->getPeakStartTime() > 0 && currentFeeder->getPeakStopTime() > 0 ))
                                {
                                    currentFeeder->isPeakTime(currentDateTime);
                                }
                                else
                                {
                                    currentFeeder->setPeakTimeFlag(peakFlag);
                                }
                            }
                        }

                    }
                }
                try
                {
                    if( store->getReregisterForPoints() )
                    {
                        registerForPoints(ccSubstationBuses);
                        store->setReregisterForPoints(FALSE);
                        waitToBroadCastEverything = TRUE;
                        registerTimeElapsed.now();

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

                CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
                CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
                CtiMultiMsg_vec& capMessages = multiCapMsg->getData();
                CtiMultiMsg_vec& ccEvents = multiCCEventMsg->getData();

                 if (store->getLinkStatusPointId() > 0 && 
                     (store->getLinkStatusFlag() == CLOSED) &&
                     store->getLinkDropOutTime().seconds() + (60* _LINK_STATUS_TIMEOUT) < currentDateTime.seconds()) 
                {
                     updateAllPointQualities(NonUpdatedQuality, secondsFrom1901);
                     store->setLinkDropOutTime(currentDateTime);
                     {
                         CtiLockGuard<CtiLogger> logger_guard(dout);
                         dout << CtiTime() << " - store->getLinkDropOutTime() " << store->getLinkDropOutTime().asString()<< endl;
                     }
                } 

                for(LONG i=0; i < ccSubstationBuses.size();i++)
                {
                    CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                    CtiCCArea* currentArea = store->findAreaByPAObjectID(currentSubstationBus->getParentId());

                    try
                    {
                        if (currentArea != NULL && !currentArea->getDisableFlag()) 
                        {
                            //currentSubstationBus->isPeakTime(currentDateTime);//put here to make sure the peak time flag is set correctly

                            if (currentSubstationBus->isMultiVoltBusAnalysisNeeded(currentDateTime))
                            {                      
                                if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
                                    currentSubstationBus->analyzeMultiVoltBus1(currentDateTime, pointChanges, ccEvents, pilMessages);
                                else
                                    currentSubstationBus->analyzeMultiVoltBus(currentDateTime, pointChanges, ccEvents, pilMessages);

                            }
                            else if (currentSubstationBus->isBusAnalysisNeeded(currentDateTime))
                            {
                                if( currentSubstationBus->getRecentlyControlledFlag() || currentSubstationBus->getWaitToFinishRegularControlFlag())
                                {
                                    try
                                    {
                                        if( currentSubstationBus->isAlreadyControlled() ||
                                            currentSubstationBus->isPastMaxConfirmTime(currentDateTime) )
                                        {
                                            if( (currentSubstationBus->getControlSendRetries() > 0 ||
                                                 currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                                                !currentSubstationBus->isAlreadyControlled() &&
                                                currentSubstationBus->checkForAndPerformSendRetry(currentDateTime, pointChanges, ccEvents, pilMessages) )
                                            {
                                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                            }
                                            else if( currentSubstationBus->capBankControlStatusUpdate(pointChanges, ccEvents) )
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
                                                currentSubstationBus->checkForAndProvideNeededControl(currentDateTime, pointChanges, ccEvents, pilMessages);
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
                                    try
                                    {
                                        if (currentSubstationBus->isBusPerformingVerification())
                                        {
                                            currentSubstationBus->setLastVerificationCheck(currentDateTime);
                                            if (currentSubstationBus->isVerificationAlreadyControlled() ||
                                                currentSubstationBus->isVerificationPastMaxConfirmTime(currentDateTime))
                                            {
                                                if( (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                                     !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod) ) &&
                                                     _ALLOW_PARALLEL_TRUING)
                                                {
                                                    try
                                                    {
                                                        currentSubstationBus->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }
                                                }
                                                else if( (currentSubstationBus->getControlSendRetries() > 0 ||
                                                         currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                                                         !currentSubstationBus->isVerificationAlreadyControlled() &&
                                                         (currentDateTime.seconds() < currentSubstationBus->getLastOperationTime().seconds() + currentSubstationBus->getMaxConfirmTime()))
                                                         
                                                {
                                                    try
                                                    {
                                                        if (currentSubstationBus->checkForAndPerformVerificationSendRetry(currentDateTime, pointChanges, ccEvents, pilMessages))
                                                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }
                                                }
                                                else if(  currentSubstationBus->getWaitForReCloseDelayFlag() ||
                                                        ( !currentSubstationBus->capBankVerificationStatusUpdate(pointChanges, ccEvents)  && 
                                                           currentSubstationBus->getCurrentVerificationCapBankId() != -1)  )
                                                {
                                                    try
                                                    {

                                                        if (currentSubstationBus->sendNextCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages))
                                                        {
                                                            currentSubstationBus->setWaitForReCloseDelayFlag(FALSE);
                                                        }
                                                        else
                                                            currentSubstationBus->setWaitForReCloseDelayFlag(TRUE);
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }
                                                }
                                                else
                                                {
                                                    try
                                                    {
                                                        if(currentSubstationBus->areThereMoreCapBanksToVerify(ccEvents))
                                                        {
                                                            if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                            {
                                                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                                                    dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPAOName()<< "( "<<currentSubstationBus->getPAOId()<<" ) CB-"<<currentSubstationBus->getCurrentVerificationCapBankId() << endl;
                                                            }

                                                            currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                                                        }
                                                        else
                                                        {
                                                            if( ccEvents.size() > 0)
                                                            {
                                                                _ccEventMsgQueue.write(multiCCEventMsg);
                                                                processCCEventMsgs();
                                                                multiCCEventMsg = new CtiMultiMsg();
                                                            }

                                                            //reset VerificationFlag
                                                            currentSubstationBus->setVerificationFlag(FALSE);
                                                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                            capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPAOId(),0, -1));
                                                            capMessages.push_back(new CtiCCCommand(CtiCCCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPAOId()));
                                                            if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                            {
                                                               CtiLockGuard<CtiLogger> logger_guard(dout);
                                                               dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPAOName()<< "( "<<currentSubstationBus->getPAOId()<<" ) "<<endl;
                                                            } 
                                                        }
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }
                                                }

                                            } 
                                            else // WAIT!!!
                                            {

                                            }
                                        }
                                        else
                                        {
                                            currentSubstationBus->setLastVerificationCheck(currentDateTime);

                                            try
                                            {
                                                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                {
                                                   CtiLockGuard<CtiLogger> logger_guard(dout);
                                                   dout << CtiTime() << " - Performing VERIFICATION ON: subBusID: "<<currentSubstationBus->getPAOName() << endl;
                                                }
                                                int strategy = (long)currentSubstationBus->getVerificationStrategy();

                                                currentSubstationBus->setCapBanksToVerifyFlags(strategy, ccEvents);

                                                if( (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                                     !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::BusOptimizedFeederControlMethod) ) &&
                                                     _ALLOW_PARALLEL_TRUING)
                                                {
                                                    try
                                                    {
                                                        currentSubstationBus->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }
                                                }
                                                else if(currentSubstationBus->areThereMoreCapBanksToVerify(ccEvents))
                                                {
                                                    try
                                                    {
                                                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                        {
                                                             CtiLockGuard<CtiLogger> logger_guard(dout);
                                                             dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPAOName()<<" CB-"<<currentSubstationBus->getCurrentVerificationCapBankId() << endl;
                                                        }
                                                        currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }

                                                }
                                                else
                                                {
                                                    try
                                                    {
                                                        if( ccEvents.size() > 0)
                                                        {
                                                            _ccEventMsgQueue.write(multiCCEventMsg);
                                                            processCCEventMsgs();
                                                            multiCCEventMsg = new CtiMultiMsg();
                                                        }
                                                    }
                                                    catch(...)
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                                    }
                                                    try
                                                    {
                                                        //reset VerificationFlag
                                                        currentSubstationBus->setVerificationFlag(FALSE);
                                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                        capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPAOId(),0, -1));
                                                        capMessages.push_back(new CtiCCCommand(CtiCCCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPAOId()));

                                                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                                        {                 
                                                           CtiLockGuard<CtiLogger> logger_guard(dout);
                                                           dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPAOName() << endl;
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
                                        }
                                    }
                                    catch(...)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                                            currentSubstationBus->checkForAndProvideNeededControl(currentDateTime, pointChanges, ccEvents, pilMessages);
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
                    if( pointChanges.size() > 0 )
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
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " PIL MESSAGES " << endl;
                            multiPilMsg->dump();
                        }
                        getPILConnection()->WriteConnQue(multiPilMsg);
                        multiPilMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                try
                {
                    //execute cap commands
                    if( multiCapMsg->getCount() > 0 )
                    {
                        multiCapMsg->resetTime(); 
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " CAP CREATED MESSAGES " << endl;
                            multiCapMsg->dump();
                        }
                        CtiMultiMsg_vec& temp = multiCapMsg->getData( );
                        for(int i=0;i<temp.size( );i++)
                        {
                            CtiCCMessage* msg = new CtiCCMessage();

                            msg = (CtiCCMessage *) temp[i];
                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(msg);
                            executor->Execute();
                            delete executor;
                           // delete msg;
                        } 
                        multiCapMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                try
                {
                    //send ccEvents to EventLOG!
                    if( ccEvents.size() > 0)
                    {
                        _ccEventMsgQueue.write(multiCCEventMsg);
                        processCCEventMsgs();
                        multiCCEventMsg = new CtiMultiMsg();
                    }
                    else if (_ccEventMsgQueue.canRead())
                    {
                        processCCEventMsgs();
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

                                /*******************************************************************/
                try
                {
                    if( substationBusChanges.size() > 0 && !waitToBroadCastEverything)
                    {
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
                            substationBusChanges.clear();
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    else if (waitToBroadCastEverything && (registerTimeElapsed.seconds() + 15) < currentDateTime.seconds() )
                    {

                        //send the substation bus changes to all cap control clients
                        CtiCCExecutorFactory f1;
                        CtiCCExecutor* executor1 = f1.createExecutor(new CtiCCSubstationBusMsg(ccSubstationBuses));
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
                        waitToBroadCastEverything = FALSE;

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
            rwnow = rwnow.now();
            if(rwnow.seconds() > tickleTime.seconds())
            {
                tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                if( rwnow.seconds() > announceTime.seconds() )
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CapControl controlLoop. TID: " << rwThreadId() << endl;
                }
          
               /* if(!_shutdownOnThreadTimeout)
                { */
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl controlLoop", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
                /*}
                else
                {   
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl controlLoop", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::sendUserQuit, CTIDBG_new string("CapControl controlLoop")) );
                //}  */
            }  


        }

        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl controlLoop", CtiThreadRegData::LogOut ) );
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


void CtiCapController::processCCEventMsgs()
{
    try
    {
        CtiTime tempTime;

        CtiCCEventLogMsg* msg = NULL;

        RWCollectable* msg1 = NULL;
        while(_ccEventMsgQueue.canRead())
        {
            try
            {   
                msg1 = _ccEventMsgQueue.read();

                if (msg1->isA() == MSG_MULTI)
                {
                    CtiMultiMsg_vec& temp = ((CtiMultiMsg*) msg1)->getData();
                    for(int i=0;i<temp.size( );i++)
                    {

                        msg = (CtiCCEventLogMsg *) temp[i];
                        CtiCCSubstationBusStore::getInstance()->InsertCCEventLogInDB(msg);
                        delete msg;
                    }

                }
                else if (msg1->isA() == CTICCEVENTLOG_ID)
                {
                    msg = (CtiCCEventLogMsg *) msg1;
                    CtiCCSubstationBusStore::getInstance()->InsertCCEventLogInDB(msg);
                    delete msg;
                }


            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        };

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
void CtiCapController::updateAllPointQualities(long quality, ULONG secondsFrom1901)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Updating CapControl Point Qualities to " << quality<< endl;
    }
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(secondsFrom1901);

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
        {
            currentSubstationBus->setCurrentVarPointQuality(quality);
        }
        if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
        {
            currentSubstationBus->setCurrentWattPointQuality(quality);
        }
        if (currentSubstationBus->getCurrentVoltLoadPointId() > 0)
        {
            currentSubstationBus->setCurrentVoltPointQuality(quality);
        }
        
        
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders[j]);

            if( currentFeeder->getCurrentVarLoadPointId() > 0 )
            {
                currentFeeder->setCurrentVarPointQuality(quality);
            }
            if( currentFeeder->getCurrentWattLoadPointId() > 0 )
            {
                currentFeeder->setCurrentWattPointQuality(quality);
            }
            if ( currentFeeder->getCurrentVoltLoadPointId() > 0)
            {
                currentFeeder->setCurrentVoltPointQuality(quality);
            }
        }
        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
    }

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
            if (currentSubstationBus->getSwitchOverPointId() > 0)
            {
                regMsg->insert(currentSubstationBus->getSwitchOverPointId());
            }
            if (currentSubstationBus->getPhaseBId() > 0) 
            {
                regMsg->insert(currentSubstationBus->getPhaseBId());
            }
            if (currentSubstationBus->getPhaseCId() > 0) 
            {
                regMsg->insert(currentSubstationBus->getPhaseCId());
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

                if (currentFeeder->getPhaseBId() > 0) 
                 {
                     regMsg->insert(currentFeeder->getPhaseBId());
                 }
                 if (currentFeeder->getPhaseCId() > 0) 
                 {
                     regMsg->insert(currentFeeder->getPhaseCId());
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
        if (CtiCCSubstationBusStore::getInstance()->getLinkStatusPointId() > 0) 
        {
            regMsg->insert(CtiCCSubstationBusStore::getInstance()->getLinkStatusPointId());
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
                            ( resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SUBSTATION_BUS ||
                              resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_AREA ) )
                        {
                            CtiCCSubstationBusStore::getInstance()->setWasSubBusDeletedFlag(TRUE);
                        }

                        long objType = CtiCCSubstationBusStore::Unknown;
                        long changeId = dbChange->getId();
                        if (dbChange->getDatabase() == ChangeCBCStrategyDb)
                        {
                            objType = CtiCCSubstationBusStore::Strategy;
                        }
                        else if (dbChange->getDatabase() == ChangePAODb && !(stringCompareIgnoreCase(dbChange->getObjectType(),"cap bank")))
                        {
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " capBank DB change message received for Cap: "<<dbChange->getId() << endl;
                            }
                            objType = CtiCCSubstationBusStore::CapBank;
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_VIRTUAL_SYSTEM)
                        {
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " VIRTUAL SYSTEM Device Change "<<dbChange->getId() << endl;
                            }
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_AREA)
                        {
                            objType = CtiCCSubstationBusStore::Area;

                            CtiCCSubstationBusStore::getInstance()->setValid(false);
                            CtiPAOScheduleManager::getInstance()->setValid(false);  
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
                            int subCount = 0;
                            int feedCount = 0;
                            int capCount = 0;
                            if (CtiCCSubstationBusStore::getInstance()->findSubBusByPointID(dbChange->getId(), subCount) != NULL)
                            {
                                CtiCCSubstationBusPtr sub = CtiCCSubstationBusStore::getInstance()->findSubBusByPointID(dbChange->getId(), subCount)->second;
                                objType = CtiCCSubstationBusStore::SubBus;
                                changeId = sub->getPAOId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findFeederByPointID(dbChange->getId(), feedCount) != NULL)
                            {
                                CtiCCFeederPtr feed = CtiCCSubstationBusStore::getInstance()->findFeederByPointID(dbChange->getId(), feedCount)->second;
                                objType = CtiCCSubstationBusStore::Feeder;
                                changeId = feed->getPAOId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(dbChange->getId(), capCount) != NULL)
                            {
                                CtiCCCapBankPtr cap = CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(dbChange->getId(), capCount)->second;
                                objType = CtiCCSubstationBusStore::CapBank;
                                changeId = cap->getPAOId();
                            }
                        }
                        else if (resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_DEVICE &&
                                 (resolveDeviceType(dbChange->getObjectType()) == TYPEVERSACOMCBC ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPEEXPRESSCOMCBC ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPECBC7010 ||
                                   resolveDeviceType(dbChange->getObjectType()) == TYPECBC7020 ||
                                   resolveDeviceType(dbChange->getObjectType()) == TYPEFISHERPCBC ||
                                   resolveDeviceType(dbChange->getObjectType()) == TYPECBC6510 ) ) 
                        {
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " cbc DB change message received for cbc: "<<dbChange->getId() << endl;
                            }
                            long capBankId = CtiCCSubstationBusStore::getInstance()->findCapBankIDbyCbcID(dbChange->getId());
                            if (capBankId != NULL) 
                            {
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                     CtiLockGuard<CtiLogger> logger_guard(dout);
                                     dout << CtiTime() << " cbc attached to cap: "<<capBankId << endl;
                                }

                                CtiCCCapBankPtr cap = CtiCCSubstationBusStore::getInstance()->findCapBankByPAObjectID(capBankId);
                                objType = CtiCCSubstationBusStore::CapBank;
                                changeId = cap->getPAOId();
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                     CtiLockGuard<CtiLogger> logger_guard(dout);
                                     dout << CtiTime() << " Cap "<<cap->getPAOName() <<" was found on sub " << endl;
                                }
                            }
                        }
                        else if (objType == CtiCCSubstationBusStore::Unknown)
                        {
                            CtiCCSubstationBusStore::getInstance()->setValid(false);
                            CtiPAOScheduleManager::getInstance()->setValid(false);  
                        }
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                             CtiLockGuard<CtiLogger> logger_guard(dout);
                             dout << CtiTime() << " RELOAD INFO: changeID: "<<changeId << endl;
                             dout << CtiTime() << "            changeType: "<<dbChange->getTypeOfChange() << endl;
                             dout << CtiTime() << "               objType: "<<objType << endl;
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
                    CtiMultiMsg_vec& temp = msgMulti->getData( );
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - ParseMessage MsgMulti has "<< temp.size() <<" entries."<< endl;
                    }
                    for(i=0;i<temp.size( );i++)
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

    try
    {   CtiCCSubstationBus* currentSubstationBus = NULL;
        int subCount = 0;
        std::multimap< long, CtiCCSubstationBusPtr >::iterator subIter = store->findSubBusByPointID(pointID, subCount);
        while (subCount > 0)
        {
            try
            {
                currentSubstationBus = subIter->second;
                if (currentSubstationBus != NULL)
                {
                    if( currentSubstationBus->getCurrentVarLoadPointId() == pointID )
                    {
                        if( timestamp > currentSubstationBus->getLastCurrentVarPointUpdateTime() )
                        {
                              
                            currentSubstationBus->setLastCurrentVarPointUpdateTime(timestamp);
                            currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                            if (!currentSubstationBus->getUsePhaseData())
                            {
                            
                                if (currentSubstationBus->getCurrentVarLoadPointValue() != value) 
                                {
                                    currentSubstationBus->setCurrentVarLoadPointValue(value);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getIntegrateFlag()) 
                                    {
                                        currentSubstationBus->updateIntegrationVPoint(CtiTime());
                                    }
                                }
                                
                                if (currentSubstationBus->getAltDualSubId() == currentSubstationBus->getPAOId() &&
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(), CtiCCSubstationBus::KVARControlUnits) )
                                {
                                    currentSubstationBus->setAltSubControlValue(value);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                }
                            }
                            else //phase A point id
                            {
                                if (currentSubstationBus->getPhaseAValue() != value) 
                                {
                                    currentSubstationBus->setPhaseAValue(value);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                     currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue());
                                    if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getIntegrateFlag()) 
                                    {
                                        currentSubstationBus->updateIntegrationVPoint(CtiTime());
                                    }
                                }
                                
                                
                                if (currentSubstationBus->getAltDualSubId() == currentSubstationBus->getPAOId() &&
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(), CtiCCSubstationBus::KVARControlUnits) )
                                {
                                    currentSubstationBus->setAltSubControlValue(currentSubstationBus->getCurrentVarLoadPointValue());
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                }
                            }
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
                                store->calculateParentPowerFactor(currentSubstationBus->getPAOId());
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

                        if (currentSubstationBus->getCurrentWattLoadPointValue() != value) 
                        {
                            currentSubstationBus->setCurrentWattLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getIntegrateFlag()) 
                            {
                                currentSubstationBus->updateIntegrationWPoint(CtiTime());
                            }
                        }
                        
                        currentSubstationBus->setCurrentWattPointQuality(quality);

                        if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
                        {
                            currentSubstationBus->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            store->calculateParentPowerFactor(currentSubstationBus->getPAOId());

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

                        if (currentSubstationBus->getCurrentVoltLoadPointValue() != value) 
                        {
                            currentSubstationBus->setCurrentVoltLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getIntegrateFlag()) 
                            {
                                currentSubstationBus->updateIntegrationVPoint(CtiTime());
                            }
                        }
                        currentSubstationBus->setCurrentVoltPointQuality(quality);
                        if (currentSubstationBus->getAltDualSubId() == currentSubstationBus->getPAOId() &&
                            !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(),CtiCCSubstationBus::VoltControlUnits))
                        {
                            currentSubstationBus->setAltSubControlValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                        found = TRUE;
                       // break;
                    }
                    else if (currentSubstationBus->getSwitchOverPointId() == pointID)
                    {
                        if (value != currentSubstationBus->getSwitchOverStatus())
                        {
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, currentSubstationBus->getPAOId(), 0, capControlSwitchOverUpdate, currentSubstationBus->getEventSequence(), value, "Switch Over Point Updated", "cap control"));
                            if (!currentSubstationBus->getDualBusEnable())
                            {
                                if (value > 0)
                                {
                                    if (!currentSubstationBus->getDisableFlag())
                                    { 
                                        currentSubstationBus->setDisableFlag(TRUE);
                                        currentSubstationBus->setReEnableBusFlag(TRUE);
                                        getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, currentSubstationBus->getPAOId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, "Substation Bus Disabled By Inhibit", "cap control"));
                                        string text = currentSubstationBus->getPAOName();
                                        text += " Disabled";
                                        string additional = string("Inhibit PointId Updated");

                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));
                                    }

                                }
                                else
                                {
                                    currentSubstationBus->setDisableFlag(FALSE);
                                    getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, currentSubstationBus->getPAOId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, "Substation Bus Enabled By Inhibit", "cap control"));
                                    string text = currentSubstationBus->getPAOName();
                                    text += " Enabled";
                                    string additional = string("Inhibit PointId Updated");

                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));

                                }

                                
                            }
                            else //dual Bus Enabled.
                            {
                                string text = currentSubstationBus->getPAOName();
                                if (currentSubstationBus->getAltDualSubId() != currentSubstationBus->getPAOId())
                                {
                                    CtiCCSubstationBus* altSub = NULL;
                                    altSub = store->findSubBusByPAObjectID(currentSubstationBus->getAltDualSubId());
                                    if (altSub != NULL)
                                    {
                                        if (value > 0)
                                        {
                                            if (altSub->getDisableFlag())
                                            {
                                                currentSubstationBus->setDisableFlag(TRUE);
                                                currentSubstationBus->setReEnableBusFlag(TRUE);
                                                text += " Disabled by Alt Sub";
                                            }
                                            else if (altSub->getSwitchOverStatus())
                                            {
                                                currentSubstationBus->setDisableFlag(TRUE);
                                                currentSubstationBus->setReEnableBusFlag(TRUE);
                                                altSub->setDisableFlag(TRUE);
                                                altSub->setReEnableBusFlag(TRUE);
                                                altSub->setBusUpdatedFlag(TRUE);
                                                text += " Disabled by Alt Sub";
                                            }
                                            else
                                                text += " Alt Sub Enabled";
                                            
                                        }
                                        else
                                        {
                                            if (currentSubstationBus->getDisableFlag() && currentSubstationBus->getReEnableBusFlag())
                                            {
                                                currentSubstationBus->setDisableFlag(FALSE);
                                                currentSubstationBus->setReEnableBusFlag(FALSE);
                                                text += " ReEnabled";
                                                if (altSub->getAltDualSubId() == currentSubstationBus->getPAOId())
                                                {
                                                    if (altSub->getSwitchOverStatus() && altSub->getDisableFlag() && altSub->getReEnableBusFlag())
                                                    {
                                                        altSub->setDisableFlag(FALSE);
                                                        altSub->setReEnableBusFlag(FALSE);
                                                        altSub->setBusUpdatedFlag(TRUE);
                                                    }
                                                }
                                            }
                                            else
                                                text += " Alt Sub Not Enabled";
                                        }
                                    }
                                }
                                string additional = string("Dual Bus Switch PointId Updated");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));
                            }
                        }
                        else if (value > 0 && !currentSubstationBus->getDisableFlag() && !currentSubstationBus->getDualBusEnable() )
                        {
                            currentSubstationBus->setDisableFlag(TRUE);
                            currentSubstationBus->setReEnableBusFlag(TRUE);
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, currentSubstationBus->getPAOId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, "Substation Bus Disabled By Inhibit", "cap control"));
                            string text = currentSubstationBus->getPAOName();
                            text += " Disabled";
                            string additional = string("Inhibit PointId Updated");

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));

                        }
                        
                        currentSubstationBus->setSwitchOverStatus(value);
                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        found = TRUE;
                    }
                    else if ((currentSubstationBus->getPhaseBId() == pointID ||
                             currentSubstationBus->getPhaseCId() == pointID ) &&
                             currentSubstationBus->getUsePhaseData())
                    {
                        if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - 3-Phase DEVELOPMENT NEEDED! " << pointID << " on SUB: " << currentSubstationBus->getPAOName() << endl;
                        }
                        if (currentSubstationBus->getPhaseBId() == pointID) 
                        {
                            if (currentSubstationBus->getPhaseBValue() != value) 
                            {
                                currentSubstationBus->setPhaseBValue(value);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue());
                            }
                        }
                        if (currentSubstationBus->getPhaseCId() == pointID) 
                        {
                            if (currentSubstationBus->getPhaseCValue() != value) 
                            {
                                currentSubstationBus->setPhaseCValue(value);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue());
                            }
                        }
                        
                        if (currentSubstationBus->getAltDualSubId() == currentSubstationBus->getPAOId() &&
                            !stringCompareIgnoreCase(currentSubstationBus->getControlUnits(), CtiCCSubstationBus::KVARControlUnits) )
                        {
                            currentSubstationBus->setAltSubControlValue(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                    }
                    else if (currentSubstationBus->getEstimatedPowerFactorPointId()  == pointID|| 
                             currentSubstationBus->getEstimatedVarLoadPointId()  == pointID||
                             currentSubstationBus->getDailyOperationsAnalogPointId()  == pointID||
                             currentSubstationBus->getPowerFactorPointId() == pointID ) 
                    {
                        if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Optional POINT data message received for: " << pointID << " on SUB: " << currentSubstationBus->getPAOName() << endl;
                        }
                        //do nothing
                    }
                    else
                    {
                        // PROBABLY AN ALTERNATE SUB BUS ID
                        currentSubstationBus->setAltSubControlValue(value);
                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        found = TRUE;
                    }

                    // check for alt sub bus id, and update all sub's alt values
                    int altSubIdCount = store->getNbrOfSubsWithAltSubID(currentSubstationBus->getPAOId());
                    while (altSubIdCount > 0)
                    {
                        long subId = store->findSubIDbyAltSubID(currentSubstationBus->getPAOId(), altSubIdCount);
                        if (subId != NULL)
                        {
                            CtiCCSubstationBusPtr altSub = store->findSubBusByPAObjectID(subId);
                            if (altSub != NULL)
                            {
                                altSub->setAllAltSubValues(currentSubstationBus->getCurrentVoltLoadPointValue(), currentSubstationBus->getCurrentVarLoadPointValue(), 
                                                           currentSubstationBus->getCurrentWattLoadPointValue());
                            }
                        }
                        altSubIdCount--;
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            subCount--;
            subIter++;
        }
        int feederCount = 0;
        std::multimap< long, CtiCCFeederPtr >::iterator feedIter = store->findFeederByPointID(pointID, feederCount);
        CtiCCFeederPtr currentFeeder = NULL;
        while (feederCount > 0)
        {   
            currentFeeder = feedIter->second;
            try
            {
                currentSubstationBus = NULL;
                //CtiCCFeederPtr currentFeeder = store->findFeederByPointID(pointID, feederCount);
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
                                if (!currentFeeder->getUsePhaseData())
                                {
                                    if (currentFeeder->getCurrentVarLoadPointValue() != value) 
                                    {
                                        currentFeeder->setCurrentVarLoadPointValue(value);
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        if (currentFeeder->isControlPoint(pointID) && currentFeeder->getIntegrateFlag()) 
                                        {
                                            currentFeeder->updateIntegrationVPoint(CtiTime(), currentSubstationBus->getNextCheckTime());
                                        }
                                    }
                                } 
                                else //phase A point id
                                {
                                    if (currentFeeder->getPhaseAValue() != value) 
                                    {
                                        if (currentFeeder->getPhaseAValue() != value) 
                                        {
                                            currentFeeder->setPhaseAValue(value);
                                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                                            currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue());
                                            if (currentFeeder->isControlPoint(pointID) && currentFeeder->getIntegrateFlag()) 
                                            {
                                                currentFeeder->updateIntegrationVPoint(CtiTime(), currentSubstationBus->getNextCheckTime());
                                            }
                                        }
                                    }

                                }
                                
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

                            if (currentFeeder->getCurrentWattLoadPointValue() != value) 
                            {
                                currentFeeder->setCurrentWattLoadPointValue(value);
                                currentFeeder->setNewPointDataReceivedFlag(TRUE);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentFeeder->setCurrentWattPointQuality(quality);
                            
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
                            if (currentFeeder->getCurrentVoltLoadPointValue() != value) 
                            {
                                currentFeeder->setCurrentVoltLoadPointValue(value);
                                currentFeeder->setNewPointDataReceivedFlag(TRUE);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentFeeder->setCurrentVoltPointQuality(quality);


                            found = TRUE;
                           // break;
                        }
                        
                        else if ((currentFeeder->getPhaseBId() == pointID ||
                             currentFeeder->getPhaseCId() == pointID ) &&
                             currentFeeder->getUsePhaseData())
                        {
                            if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - 3-Phase DEVELOPMENT NEEDED! " << pointID << " on FEEDER: " << currentFeeder->getPAOName() << endl;

                            }
                            if (currentFeeder->getPhaseBId() == pointID) 
                            {
                                if (currentFeeder->getPhaseBValue() != value) 
                                {
                                    currentFeeder->setPhaseBValue(value);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue());
                                }
                            }
                            if (currentFeeder->getPhaseCId() == pointID) 
                            {
                                if (currentFeeder->getPhaseCValue() != value) 
                                {
                                    currentFeeder->setPhaseCValue(value);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue());
                                }
                            }
                        }
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            feedIter++;
            feederCount--;
        }

        int capCount = 0;
        std::multimap< long, CtiCCCapBankPtr >::iterator capIter = store->findCapBankByPointID(pointID, capCount);
        CtiCCCapBankPtr currentCapBank = NULL;

        while (capCount > 0)
        {                  
            try
            {
                currentCapBank = capIter->second;
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
                                store->set2wayFlagUpdate(TRUE);
                            }
                            //JULIE: TXU fix for status change time setting to old date on disabled banks.
                            if (!currentCapBank->getDisableFlag()) 
                            {    
                                currentCapBank->setControlStatus((LONG)value);
                                currentCapBank->setTagsControlStatus((LONG)tags);
                                currentCapBank->setLastStatusChangeTime(timestamp);
                            }
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
                            //JULIE: BGE fix for status change time setting to old date on disabled banks.
                            if (!currentCapBank->getDisableFlag()) 
                            {
                                currentCapBank->setTotalOperations((LONG)value);
                            }
                            found = TRUE;
                            //break;
                        }
                        else if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") ) 
                        {   
                            CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)currentCapBank->getTwoWayPoints();
                            if (twoWayPts->getIgnoredIndicatorId() == pointID) 
                            {
                                if (twoWayPts->getIgnoredIndicator() != value) 
                                {
                                   currentCapBank->setIgnoreFlag(TRUE);
                                   currentSubstationBus->setBusUpdatedFlag(TRUE);
                                   string text = string("CBC rejected command!");
                                   string text1 = string("Var: CBC rejected command!, ");
                                   if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                       currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                                       currentCapBank->getControlStatus() == CtiCCCapBank::Open ) 
                                   {
                                       currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                                       text1 += "OpenFail";
                                   }
                                   else if (currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                                       currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                       currentCapBank->getControlStatus() == CtiCCCapBank::Close ) 
                                   {
                                       currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                                       text1 += "CloseFail";
                                   }
                                   if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
                                   {
                                       currentFeeder->setRecentlyControlledFlag(FALSE);
                                   }
                                   else
                                   {
                                       currentSubstationBus->setRecentlyControlledFlag(FALSE);
                                       currentFeeder->setRecentlyControlledFlag(FALSE);
                                   }

                                   currentSubstationBus->setBusUpdatedFlag(TRUE);
                                   sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                   CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, "cap control");
                                   eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                                   getCCEventMsgQueueHandle().write(eventMsg);
                                   currentCapBank->setLastStatusChangeTime(CtiTime());
                                }
                            }
                            if (twoWayPts->setTwoWayStatusPointValue(pointID, value))
                            {   
                                if (twoWayPts->getCapacitorBankStateId() == pointID) 
                                {
                                    if (currentCapBank->getReportedCBCState() != value) 
                                    {
                                        currentCapBank->setReportedCBCStateTime(timestamp);
                                    }
                                    currentCapBank->setReportedCBCState(twoWayPts->getCapacitorBankState());

                                    store->set2wayFlagUpdate(TRUE);
                                }
                                if( _CC_DEBUG & CC_DEBUG_POINT_DATA )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Set a cbc 2 way status point..."<< endl;
                                }
                                if (twoWayPts->getAutoVoltControl()) 
                                {
                                    if (currentCapBank->getOvUvDisabledFlag()) 
                                    {
                                        currentCapBank->setOvUvDisabledFlag(FALSE);
                                    }
                                }
                                else 
                                { 
                                    if (!currentCapBank->getOvUvDisabledFlag()) 
                                    {
                                        currentCapBank->setOvUvDisabledFlag(TRUE);
                                    }
                                }

                            }
                            else if (twoWayPts->setTwoWayAnalogPointValue(pointID, value))
                            {
                                if (twoWayPts->getUDPIpAddressId() == pointID) 
                                {
                                    currentCapBank->setIpAddress(twoWayPts->getUDPIpAddress());
                                }
                                else if (twoWayPts->getUDPPortNumberId() == pointID) 
                                {
                                    currentCapBank->setUDPPort(twoWayPts->getUDPPortNumber());
                                }
                                else if (twoWayPts->getIgnoredReasonId() == pointID) 
                                {
                                    currentCapBank->setIgnoredReason(value);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                }
                                if( _CC_DEBUG & CC_DEBUG_POINT_DATA )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Set a cbc 2 way Analog point..."<< endl;
                                }
                            }
                            else if (twoWayPts->setTwoWayPulseAccumulatorPointValue(pointID, value))
                            {
                                if( _CC_DEBUG & CC_DEBUG_POINT_DATA )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Set a cbc 2 way Pulse Accumulator point..."<< endl;
                                }
                            }
                            found = TRUE;


                            for (int i = 0; i < currentCapBank->getMonitorPoint().size(); i++)
                            {
                                CtiCCMonitorPointPtr currentMonPoint = (CtiCCMonitorPointPtr)currentCapBank->getMonitorPoint()[i];
                                if( currentMonPoint->getPointId() == pointID )
                                {
                                    if (currentMonPoint->getValue() != value)
                                    {
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                                        
                                        
                                        currentFeeder->setNewPointDataReceivedFlag(TRUE);
                                    }
                                    currentMonPoint->setValue(value);
                                    currentMonPoint->setTimeStamp(timestamp);
                                    break;
                                }
                            }


                        }
                        else
                        {   
                            for (int i = 0; i < currentCapBank->getMonitorPoint().size(); i++)
                            {
                                CtiCCMonitorPointPtr currentMonPoint = (CtiCCMonitorPointPtr)currentCapBank->getMonitorPoint()[i];
                                if( currentMonPoint->getPointId() == pointID )
                                {
                                    if (currentMonPoint->getValue() != value)
                                    {
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                                        
                                        currentFeeder->setNewPointDataReceivedFlag(TRUE);
                                    }
                                    currentMonPoint->setValue(value);
                                    currentMonPoint->setTimeStamp(timestamp);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            currentCapBank++;
            capCount--;
        }
        if (store->getLinkStatusPointId() > 0) 
        {
            if (store->getLinkStatusPointId() == pointID)
            {
                if (store->getLinkStatusFlag() != value)
                {
                    store->setLinkStatusFlag(value);
                    if (value == CLOSED) 
                    {
                        store->setLinkDropOutTime(timestamp);
                    }
                    if (value == OPENED) 
                    {
                        store->setReregisterForPoints(TRUE);
                    }
                }
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
                    if( (currentSubstationBus->getRecentlyControlledFlag() || 
                         currentSubstationBus->getVerificationFlag() )&&
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
                            else if( commandString == "control flip" )
                            {
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending) 
                                {
                                    currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                                }
                                else
                                {
                                    currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                                }
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
                                string text1 = string("Var: Porter Fail Msg, ");
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail) 
                                {
                                    text1 += "CloseFail";
                                }
                                else
                                    text1 += "OpenFail";
                                sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,"cap control"));
                                sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, "cap control");
                                eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                                getCCEventMsgQueueHandle().write(eventMsg);
                                currentCapBank->setLastStatusChangeTime(CtiTime());
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                              << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                            }
                            currentFeeder->setPorterRetFailFlag(TRUE);
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
                            {
                                currentFeeder->setRecentlyControlledFlag(FALSE);
                            }
                            else
                            {
                                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                                currentFeeder->setRecentlyControlledFlag(FALSE);
                            }
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
void CtiCapController::confirmCapBankControl( CtiMultiMsg* pilMultiMsg, CtiMultiMsg* multiMsg )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    try
    {
        if (pilMultiMsg->getCount() > 0) 
            getPILConnection()->WriteConnQue(pilMultiMsg);
        if( multiMsg->getCount() > 0 )
            getDispatchConnection()->WriteConnQue(multiMsg);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

CtiPCPtrQueue< RWCollectable > &CtiCapController::getInClientMsgQueueHandle()
{
    return _inClientMsgQueue;
} 
CtiPCPtrQueue< RWCollectable > &CtiCapController::getOutClientMsgQueueHandle()
{
    return _outClientMsgQueue;
} 


CtiPCPtrQueue< RWCollectable > &CtiCapController::getCCEventMsgQueueHandle()
{
    return _ccEventMsgQueue;
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

void CtiCapController::refreshCParmGlobals(bool force)
{
    string str;
    char var[128];

    try
    {
        _CC_DEBUG = CC_DEBUG_NONE;

        strcpy(var, "CAP_CONTROL_DEBUG");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), tolower);
            _CC_DEBUG = (str=="true"?(CC_DEBUG_STANDARD|CC_DEBUG_POINT_DATA):CC_DEBUG_NONE);

            if( !_CC_DEBUG )
            {
                if( str=="false" )
                {
                    _CC_DEBUG = CC_DEBUG_NONE;
                }
                else
                {
                    char *eptr;
                    _CC_DEBUG = strtoul(str.c_str(), &eptr, 16);
                }
            }

            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        dout.setOutputFile("capcontrol");

        strcpy(var, "CAP_CONTROL_LOG_FILE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            dout.setOutputFile(str.c_str());
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _DB_RELOAD_WAIT = 5;  //5 seconds

        strcpy(var, "CAP_CONTROL_DB_RELOAD_WAIT");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            _DB_RELOAD_WAIT = atoi(str.c_str());

            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }


        _IGNORE_NOT_NORMAL_FLAG = FALSE;

        strcpy(var, "CAP_CONTROL_IGNORE_NOT_NORMAL");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), tolower);
            _IGNORE_NOT_NORMAL_FLAG = (str=="true"?TRUE:FALSE);
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _SEND_TRIES = 1;

        strcpy(var, "CAP_CONTROL_SEND_RETRIES");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            _SEND_TRIES = atoi(str.c_str())+1;

            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _USE_FLIP_FLAG = FALSE;

        strcpy(var, "CAP_CONTROL_USE_FLIP");
        if ( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), ::tolower);
            _USE_FLIP_FLAG = (str=="true"?TRUE:FALSE);
            if ( _CC_DEBUG & CC_DEBUG_STANDARD)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }


        _POINT_AGE = 3;  //3 minute

        strcpy(var, "CAP_CONTROL_POINT_AGE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            _POINT_AGE = atoi(str.data())+1;
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _SCAN_WAIT_EXPIRE = 1;  //1 minute

        strcpy(var, "CAP_CONTROL_SCAN_WAIT_EXPIRE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            _SCAN_WAIT_EXPIRE = atoi(str.data())+1;
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _ALLOW_PARALLEL_TRUING = FALSE;

        strcpy(var, "CAP_CONTROL_ALLOW_PARALLEL_TRUING");
        if ( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), ::tolower);
            _ALLOW_PARALLEL_TRUING = (str=="true"?TRUE:FALSE);
            if ( _CC_DEBUG & CC_DEBUG_STANDARD)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _RETRY_FAILED_BANKS = FALSE;

        strcpy(var, "CAP_CONTROL_RETRY_FAILED_BANKS");
        if ( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), ::tolower);
            _RETRY_FAILED_BANKS = (str=="true"?TRUE:FALSE);
            if ( _CC_DEBUG & CC_DEBUG_STANDARD)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }
        _END_DAY_ON_TRIP = FALSE;

        strcpy(var, "CAP_CONTROL_END_DAY_ON_TRIP");
        if ( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), ::tolower);
            _END_DAY_ON_TRIP = (str=="true"?TRUE:FALSE);
            if ( _CC_DEBUG & CC_DEBUG_STANDARD)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _LOG_MAPID_INFO = FALSE;

        strcpy(var, "CAP_CONTROL_LOG_MAPID_INFO");
        if ( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), ::tolower);
            _LOG_MAPID_INFO = (str=="true"?TRUE:FALSE);
            if ( _CC_DEBUG & CC_DEBUG_STANDARD)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        _LINK_STATUS_TIMEOUT = 5;  //1 minute

        strcpy(var, "CAP_CONTROL__LINK_STATUS_TIMEOUT");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            _LINK_STATUS_TIMEOUT = atoi(str.data())+1;
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}




