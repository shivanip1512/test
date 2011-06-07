#include "yukon.h"

#include "dbaccess.h"
#include "connection.h"
#include "ccmessage.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_signal.h"
#include "msg_tag.h"
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
#include "ccoriginalparent.h"
#include "mgr_paosched.h"
#include "thread_monitor.h"
#include "utility.h"
#include "ctitime.h"
#include "ControlStrategy.h"
#include "ThreadStatusKeeper.h"

#include "ccclientconn.h"
#include "ccclientlistener.h"
#include <rw/thr/prodcons.h>

extern void refreshGlobalCParms();

extern ULONG _CC_DEBUG;
extern ULONG _LINK_STATUS_TIMEOUT;
extern BOOL  _ALLOW_PARALLEL_TRUING;
extern BOOL  _ENABLE_IVVC;
extern BOOL  _AUTO_VOLT_REDUCTION;
extern LONG  _VOLT_REDUCTION_SYSTEM_POINTID;

using Cti::ThreadStatusKeeper;

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
    deleteInstance

    Deletes the singleton instance of CtiCCSubstationBusStore
---------------------------------------------------------------------------*/
void CtiCapController::deleteInstance()
{
    if( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }
}

/*---------------------------------------------------------------------------
    setInstance

    Sets the instance to the passed instance. This should only be used in Unit Tests.
---------------------------------------------------------------------------*/
void CtiCapController::setInstance(CtiCapController* controller)
{
    if (_instance != NULL)
    {
        deleteInstance();
    }
    _instance = controller;

}

/*---------------------------------------------------------------------------
    Constructor

    Private to ensure that only one CtiCapController is available through the
    instance member function
---------------------------------------------------------------------------*/
CtiCapController::CtiCapController() : control_loop_delay(500), control_loop_inmsg_delay(0), control_loop_outmsg_delay(0)
{
    _dispatchConnection.reset();
    _pilConnection = NULL;
}

/*---------------------------------------------------------------------------
    Destructor

    Private to ensure that the single instance of CtiCapController is not
    deleted
---------------------------------------------------------------------------*/
CtiCapController::~CtiCapController()
{

    _dispatchConnection.reset();
    _pilConnection = NULL;
    if( _instance != NULL )
    {
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

    RWThreadFunction threadfunc3 = rwMakeThreadFunction( *this, &CtiCapController::outClientMsgs );
    _outClientMsgThread = threadfunc3;
    threadfunc3.start();

    RWThreadFunction threadfunc4 = rwMakeThreadFunction( *this, &CtiCapController::messageSender );
    _messageSenderThread = threadfunc4;
    threadfunc4.start();

    RWThreadFunction incomingMessageProcessorThread = rwMakeThreadFunction( *this, &CtiCapController::incomingMessageProcessor );
    _incomingMessageProcessorThread = incomingMessageProcessorThread;
    incomingMessageProcessorThread.start();
}

/*---------------------------------------------------------------------------
    stop

    Stops the controller thread
---------------------------------------------------------------------------*/
void CtiCapController::stop()
{
    try
    {
        //Shutdown control loop
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

        //Shutdown outClientMsgThread
        if ( _outClientMsgThread.isValid() && _outClientMsgThread.requestCancellation() == RW_THR_ABORTED )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - OutClientMsg Thread forced to terminate." << endl;
            }

            _outClientMsgThread.terminate();
        }
        else
        {
            _outClientMsgThread.requestCancellation();
            _outClientMsgThread.join();
        }

        //Shutdown messageSenderThread
        if ( _messageSenderThread.isValid() && _messageSenderThread.requestCancellation() == RW_THR_ABORTED )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - messageSender Thread forced to terminate." << endl;
            }

            _messageSenderThread.terminate();
        }
        else
        {
            _messageSenderThread.requestCancellation();
            _messageSenderThread.join();
        }

        //Shutdown incomingMessageProcessorThread
        if ( _incomingMessageProcessorThread.isValid() && _incomingMessageProcessorThread.requestCancellation() == RW_THR_ABORTED )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - incomingMessageProcessorThread Thread forced to terminate." << endl;
            }

            _incomingMessageProcessorThread.terminate();
        }
        else
        {
            _incomingMessageProcessorThread.requestCancellation();
            _incomingMessageProcessorThread.join();
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if( _dispatchConnection != NULL && _dispatchConnection->valid() )
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

void CtiCapController::messageSender()
{
    ThreadStatusKeeper threadStatus("CapControl messageSender");

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
        registerForPoints(*store->getCCSubstationBuses(CtiTime().seconds()));
    }
    while(TRUE)
    {
        CtiTime currentDateTime;
        BOOL waitToBroadCastEverything = FALSE;

        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
            if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - ~~~~~~~~~ Message Sender Start ~~~~~~~~~~~~ " << endl;
            }

            try
            {

                if( store->getReregisterForPoints() )
                {
                    registerForPoints(*store->getCCSubstationBuses(CtiTime().seconds()));
                    store->setReregisterForPoints(FALSE);
                    waitToBroadCastEverything = TRUE;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                checkPIL();

                store->checkUnsolicitedList();
                store->checkRejectedList();
                store->checkUnexpectedUnsolicitedList();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            if (store->getLinkStatusPointId() > 0 &&
                 (store->getLinkStatusFlag() == CLOSED) &&
                 store->getLinkDropOutTime().seconds() + (60* _LINK_STATUS_TIMEOUT) < currentDateTime.seconds())
            {
                 updateAllPointQualities(NonUpdatedQuality);
                 store->setLinkDropOutTime(currentDateTime);
                 {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - store->getLinkDropOutTime() " << store->getLinkDropOutTime().asString()<< endl;
                 }
            }

            readClientMsgQueue();
            CtiCCSubstationBus_vec subStationBusChanges;
            CtiCCSubstation_set stationChanges;
            CtiCCArea_set areaChanges;

            PaoIdToSubBusMap::iterator busIter = store->getPAOSubMap()->begin();
            for ( ; busIter != store->getPAOSubMap()->end() ; busIter++)
            {
                CtiCCSubstationBusPtr currentSubstationBus = busIter->second;CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                CtiCCAreaPtr currentArea = NULL;
                if (currentStation != NULL )//&& !currentStation->getDisableFlag())
                {
                    currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                }

                if (currentArea != NULL )//&& !currentArea->getDisableFlag())
                {
                    if( currentSubstationBus->getBusUpdatedFlag())
                    {
                        currentStation->checkAndUpdateRecentlyControlledFlag();

                        if (currentStation->getStationUpdatedFlag())
                        {
                            store->updateSubstationObjectSet(currentStation->getPaoId(), (CtiMultiMsg_set&)stationChanges);
                            currentStation->setStationUpdatedFlag(FALSE);
                        }
                        if (currentArea->getAreaUpdatedFlag())
                        {
                            store->updateAreaObjectSet(currentArea->getPaoId(),(CtiMultiMsg_set&)areaChanges);
                            currentArea->setAreaUpdatedFlag(FALSE);
                        }
                        subStationBusChanges.push_back(currentSubstationBus);
                        currentSubstationBus->setBusUpdatedFlag(FALSE);
                    }
                }
            }

            if (subStationBusChanges.size() > 0)
            {
                getOutClientMsgQueueHandle().write(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)subStationBusChanges, CtiCCSubstationBusMsg::SubBusModified));
            }
            if (areaChanges.size() > 0)
            {
                getOutClientMsgQueueHandle().write(new CtiCCGeoAreasMsg((CtiCCArea_set&)areaChanges, CtiCCGeoAreasMsg::AreaModified));
            }
            if (stationChanges.size() > 0)
            {
                getOutClientMsgQueueHandle().write(new CtiCCSubstationsMsg((CtiCCSubstation_set&)stationChanges,CtiCCSubstationsMsg::SubModified));
            }
            if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - ~~~~~~~~~ Message Sender End ~~~~~~~~~~~~ " << endl;
            }
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
        threadStatus.monitorCheck();
    };
}

void CtiCapController::incomingMessageProcessor()
{
    ThreadStatusKeeper threadKeeper("CapControl Incoming Message Thread");

    while(TRUE)
    {
        CtiMessage* msg = NULL;
        bool retVal = _incomingMessageQueue.read(msg,5000);

        if (retVal)
        {
            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Processing New Message" << endl;
            }

            parseMessage(msg);
            delete msg;
        }

        try
        {
            rwRunnable().serviceCancellation();
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

        threadKeeper.monitorCheck();
    };
}

void CtiCapController::processNewMessage(CtiMessage* message)
{
    if (message != NULL)
    {
        _incomingMessageQueue.write(message);
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
        CtiCCSubstation_set stationChanges;
        CtiCCArea_set areaChanges;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
        CtiMultiMsg* multiCCEventMsg = new CtiMultiMsg();
        LONG lastThreadPulse = 0;
        LONG lastDailyReset = 0;
        BOOL waitToBroadCastEverything = FALSE;
        BOOL startUpSendStats = TRUE;

        ThreadStatusKeeper threadStatus("CapControl controlLoop");

        CtiTime fifteenMinCheck = nextScheduledTimeAlignedOnRate( currentDateTime,  900);

        long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::CapControl);
        CtiTime NextThreadMonitorReportTime;
        CtiThreadMonitor::State previous;

        while(TRUE)
        {
            long main_wait = control_loop_delay;
            bool received_message = false;
            {

                currentDateTime = CtiTime();
                LONG secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
                ULONG secondsFrom1901 = currentDateTime.seconds();


                CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
                CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
                CtiMultiMsg_vec& capMessages = multiCapMsg->getData();
                CtiMultiMsg_vec& ccEvents = multiCCEventMsg->getData();
                try
                {

                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

                    if( currentDateTime.seconds() > fifteenMinCheck.seconds() && secondsFrom1901 != lastThreadPulse)
                    {//every  fifteen minutes tell the user if the control thread is still alive
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Controller refreshing CPARMS" << endl;
                        }
                        refreshGlobalCParms();
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
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }



                rwRunnable().serviceCancellation();
                try
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

                    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(secondsFrom1901, true);
                    CtiCCSubstation_vec& ccSubstations = *store->getCCSubstations(secondsFrom1901);
                    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(secondsFrom1901);


                    if( (secondsFrom1901%60) == 0 && secondsFrom1901 != lastThreadPulse )
                    {
                        for(LONG i=0;i<ccSubstationBuses.size();i++)
                        {
                            CtiCCSubstationBusPtr currentSubstationBus = ccSubstationBuses[i];
                            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                            BOOL peakFlag = currentSubstationBus->isPeakTime(currentDateTime);
                            for(LONG j=0;j<ccFeeders.size();j++)
                            {
                                CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder &&
                                    !(ciStringEqual(currentFeeder->getStrategy()->getStrategyName(), ControlStrategy::NoControlUnit)) &&
                                    (currentFeeder->getStrategy()->getPeakStartTime() > 0 && currentFeeder->getStrategy()->getPeakStopTime() > 0 ))
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
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                try
                {
                    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - ********** Control Loop Start ********* " << endl;
                    }

                    store->setStoreRecentlyReset(FALSE);

                    areaChanges.clear();
                    stationChanges.clear();
                    PaoIdToSubBusMap::iterator busIter = store->getPAOSubMap()->begin();
                    for ( ;busIter != store->getPAOSubMap()->end(); busIter++)
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
                        if (store->getStoreRecentlyReset())
                            break;

                        CtiCCSubstationBusPtr currentSubstationBus = busIter->second;

                        //Bypass IVVC subbuses.  IVVC subbuses are analyzed in the IVVC Algorithm.
                        if (currentSubstationBus->getStrategy()->getControlUnits() == ControlStrategy::IntegratedVoltVarControlUnit)
                            continue;
                        
                        CtiCCAreaPtr currentArea = NULL;
                        CtiCCSubstation* currentStation = NULL;

                        currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                        if (currentStation != NULL )
                        {
                            currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                        }

                        try
                        {
                            if (currentArea != NULL )
                            {
                                currentSubstationBus->performDataOldAndFallBackNecessaryCheck();

                                if (currentSubstationBus->isMultiVoltBusAnalysisNeeded(currentDateTime))
                                {
                                    if (currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder)
                                    {
                                        currentSubstationBus->analyzeMultiVoltBus1(currentDateTime, pointChanges, ccEvents, pilMessages);
                                    }
                                    else
                                    {
                                        currentSubstationBus->analyzeMultiVoltBus(currentDateTime, pointChanges, ccEvents, pilMessages);
                                    }
                                }
                                else if (currentSubstationBus->isBusAnalysisNeeded(currentDateTime))
                                {
                                    if (currentSubstationBus->getRecentlyControlledFlag() || currentSubstationBus->getWaitToFinishRegularControlFlag())
                                    {
                                        try
                                        {
                                            if (currentSubstationBus->isAlreadyControlled() ||
                                                currentSubstationBus->isPastMaxConfirmTime(currentDateTime))
                                            {
                                                if ((currentSubstationBus->getStrategy()->getControlSendRetries() > 0 ||
                                                     currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                                                    !currentSubstationBus->isAlreadyControlled() &&
                                                     currentSubstationBus->checkForAndPerformSendRetry(currentDateTime, pointChanges, ccEvents, pilMessages))
                                                {
                                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                }
                                                else
                                                {
                                                    currentSubstationBus->capBankControlStatusUpdate(pointChanges, ccEvents);
                                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                                }
                                            }
                                            else if (currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder)
                                            {
                                                checkBusForNeededControl(currentArea,currentStation, currentSubstationBus, currentDateTime,pointChanges, ccEvents, pilMessages);
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
                                        analyzeVerificationBus(currentSubstationBus, currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);
                                    }
                                    else if (currentSubstationBus->isVarCheckNeeded(currentDateTime))
                                    {//not recently controlled and var check needed
                                        checkBusForNeededControl(currentArea,currentStation, currentSubstationBus, currentDateTime,pointChanges, ccEvents, pilMessages);
                                    }

                                    try
                                    {
                                        //so we don't do this over and over we need to clear out
                                        currentSubstationBus->clearOutNewPointReceivedFlags();
                                        if( currentSubstationBus->isVarCheckNeeded(currentDateTime) &&
                                            currentSubstationBus->getStrategy()->getControlInterval() > 0 )
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
                                try
                                {
                                    //accumulate all buses with any changes into msg for all clients
                                    if (currentSubstationBus->getBusUpdatedFlag())
                                    {
                                        currentStation->checkAndUpdateRecentlyControlledFlag();
                                        if (currentStation->getStationUpdatedFlag())
                                        {
                                            store->updateSubstationObjectSet(currentStation->getPaoId(), (CtiMultiMsg_set&)stationChanges);
                                            currentStation->setStationUpdatedFlag(FALSE);
                                        }
                                        if (currentArea->getAreaUpdatedFlag())
                                        {
                                            store->updateAreaObjectSet(currentArea->getPaoId(),(CtiMultiMsg_set&)areaChanges);
                                            currentArea->setAreaUpdatedFlag(FALSE);
                                        }

                                        getOutClientMsgQueueHandle().write(new CtiCCSubstationBusMsg(currentSubstationBus));
                                        currentSubstationBus->setBusUpdatedFlag(FALSE);
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
                            rwRunnable().serviceCancellation();
                            rwRunnable().sleep( 1 );
                        }
                        catch (RWCancellation& )
                        {
                            throw;
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        threadStatus.monitorCheck();
                        if(pointID!=0)
                        {
                            CtiThreadMonitor::State next;
                            if((next = ThreadMonitor.getState()) != previous ||
                               CtiTime::now() > NextThreadMonitorReportTime)
                            {
                                // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                                previous = next;
                                NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                                getDispatchConnection()->WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
                            }
                        }
                    }
                    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - ********** Control Loop End ********* " << endl;
                    }
                }
                catch (RWCancellation& )
                {
                    throw;
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

                try
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

                    if (areaChanges.size() > 0 &&  !store->getStoreRecentlyReset())
                    {
                        getOutClientMsgQueueHandle().write(new CtiCCGeoAreasMsg((CtiCCArea_set&)areaChanges, CtiCCGeoAreasMsg::AreaModified));
                        areaChanges.clear();
                    }
                    if (stationChanges.size() > 0 &&  !store->getStoreRecentlyReset())
                    {
                        getOutClientMsgQueueHandle().write(new CtiCCSubstationsMsg((CtiCCSubstation_set&)stationChanges,CtiCCSubstationsMsg::SubModified));
                        stationChanges.clear();
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                            CtiCCExecutorFactory::createExecutor((CtiCCMessage *) temp[i])->execute();
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
                        _ccEventMsgQueue.write(multiCCEventMsg->replicateMessage());
                        delete multiCCEventMsg;
                        multiCCEventMsg = new CtiMultiMsg();
                    }
                    if (_ccEventMsgQueue.canRead())
                    {
                        processCCEventMsgs();
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

            try
            {
                if (_ENABLE_IVVC == TRUE)
                {
                    store->executeAllStrategies();
                }
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Exception while execute strategies " << __FILE__ << " at:" << __LINE__ << endl;
            }

            // Send updated Voltage Regulators.
            try
            {
                VoltageRegulatorMessage * message = store->getVoltageRegulatorManager()->getVoltageRegulatorMessage(false);
                if ( message != 0 )
                {
                    getOutClientMsgQueueHandle().write( message );
                }
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Exception while updating Voltage Regulators.  " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
    }
    catch(RWCancellation& )
    {
        try
        {
            CtiPAOScheduleManager* scheduleMgr = CtiPAOScheduleManager::getInstance();
            scheduleMgr->stop();
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        throw;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        dout << CtiTime() << " - Control Loop thread terminated unexpectedly." << endl;
    }
}

void CtiCapController::checkBusForNeededControl(CtiCCAreaPtr currentArea,  CtiCCSubstation* currentStation, CtiCCSubstationBusPtr currentSubstationBus, const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages)
{

    try
    {
        if( !currentSubstationBus->getDisableFlag() &&
            !currentArea->getDisableFlag() &&
            !currentStation->getDisableFlag() &&
            currentSubstationBus->getLikeDayControlFlag() &&
            currentSubstationBus->getStrategy()->getMethodType() != ControlStrategy::ManualOnly )
        {
            currentSubstationBus->checkForAndProvideNeededFallBackControl(currentDateTime, pointChanges, ccEvents, pilMessages);
        }
        else if ( !currentSubstationBus->getDisableFlag() &&
                !currentArea->getDisableFlag() &&
                !currentStation->getDisableFlag() &&
            currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
        {
            currentSubstationBus->checkForAndProvideNeededTimeOfDayControl(currentDateTime, pointChanges, ccEvents, pilMessages);
        }
        else if( !currentSubstationBus->getDisableFlag() &&
            !currentArea->getDisableFlag() &&
            !currentStation->getDisableFlag() &&
            !currentSubstationBus->getWaiveControlFlag() &&
             currentSubstationBus->getStrategy()->getMethodType() != ControlStrategy::ManualOnly )
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

void CtiCapController::readClientMsgQueue()
{
    CtiTime tempTime;
    RWCollectable* clientMsg = NULL;

    tempTime.now();
    while(_inClientMsgQueue.canRead())
    {
        clientMsg = _inClientMsgQueue.read();
        if( clientMsg != NULL )
        {
            try
            {
                CtiCCExecutorFactory::createExecutor( (CtiMessage*) clientMsg )->execute();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }

        if (CtiTime::now().seconds() - tempTime.seconds() <= 1)
        {
            break;
        }
    }
}

void CtiCapController::broadcastMessagesToClient(CtiCCSubstationBus_vec& substationBusChanges,
                                                 CtiCCSubstation_vec& stationChanges, CtiCCArea_vec& areaChanges, long broadCastMask)
{
    try
    {
        if (substationBusChanges.size() > 0)
        {
            int size = substationBusChanges.size();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - substationBusChanges: "<<size << endl;
            }

            try
            {
                CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(substationBusChanges, broadCastMask))->execute();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        if( stationChanges.size() > 0 )
        {
            try
            {
                CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(stationChanges, broadCastMask))->execute();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
         }
         if( areaChanges.size() > 0 )
         {
             try
             {
                 CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(areaChanges, broadCastMask))->execute();
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

void CtiCapController::analyzeVerificationBus(CtiCCSubstationBusPtr currentSubstationBus, const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages,
                            CtiMultiMsg_vec& capMessages)
{
    try
    {
        if (currentSubstationBus->isBusPerformingVerification())
        {
            currentSubstationBus->setLastVerificationCheck(currentDateTime);



            if( (currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                 currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder) &&
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
            else if (currentSubstationBus->isVerificationAlreadyControlled() ||
                currentSubstationBus->isVerificationPastMaxConfirmTime(currentDateTime))
            {
                if( (currentSubstationBus->getStrategy()->getControlSendRetries() > 0 ||
                         currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                         !currentSubstationBus->isVerificationAlreadyControlled() &&
                         (currentDateTime.seconds() < currentSubstationBus->getLastOperationTime().seconds() + currentSubstationBus->getStrategy()->getMaxConfirmTime()))

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
                                    dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPaoName()<< "( "<<currentSubstationBus->getPaoId()<<" ) CB-"<<currentSubstationBus->getCurrentVerificationCapBankId() << endl;
                            }

                            currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                        }
                        else
                        {

                            //reset VerificationFlag
                            currentSubstationBus->setVerificationFlag(FALSE);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPaoId(),0, -1, currentSubstationBus->getVerificationDisableOvUvFlag()));
                            capMessages.push_back(new CtiCCCommand(CtiCCCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()));
                            if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                            {
                               CtiLockGuard<CtiLogger> logger_guard(dout);
                               dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName()<< "( "<<currentSubstationBus->getPaoId()<<" ) "<<endl;
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
        }
        else
        {
            currentSubstationBus->setLastVerificationCheck(currentDateTime);

            try
            {
                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Performing VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName() << endl;
                }
                int strategy = (long)currentSubstationBus->getVerificationStrategy();

                currentSubstationBus->setCapBanksToVerifyFlags(strategy, ccEvents);

                if( (currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                     currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder) &&
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
                             dout << CtiTime() << " ------ CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPaoName()<<" CB-"<<currentSubstationBus->getCurrentVerificationCapBankId() << endl;
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
                        //reset VerificationFlag
                        currentSubstationBus->setVerificationFlag(FALSE);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        capMessages.push_back(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION, currentSubstationBus->getPaoId(),0, -1, currentSubstationBus->getVerificationDisableOvUvFlag()));
                        capMessages.push_back(new CtiCCCommand(CtiCCCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()));

                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                        {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName() << endl;
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


void CtiCapController::outClientMsgs()
{
    try
    {
        ThreadStatusKeeper threadStatus("CapControl outClientMsgs");

        while (TRUE)
        {
            RWCollectable* msg = NULL;
            bool retVal = getOutClientMsgQueueHandle().read(msg,5000);
            int totalEntries = getOutClientMsgQueueHandle().entries();

            if (retVal)
            {
                CtiCCClientListener::getInstance()->BroadcastMessage((CtiMessage*)msg);
                delete msg;
            }

            try
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep( 50 );
            }
            catch(RWCancellation& e)
            {
                throw;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            threadStatus.monitorCheck();
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
        dout << CtiTime() << " - Out Client Messages thread terminated unexpectedly." << endl;
    }
}

void CtiCapController::processCCEventMsgs()
{
    try
    {
        RWCollectable* msg = NULL;
        int msgCount = 0;
        CtiTime processTimeStart = CtiTime();

        while(_ccEventMsgQueue.canRead() && msgCount < 100)
        {
            try
            {
                msg = _ccEventMsgQueue.read();

                if (msg->isA() == MSG_MULTI && ((CtiMultiMsg*) msg)->getCount() > 0)
                {
                    CtiMultiMsg_vec& temp = ((CtiMultiMsg*) msg)->getData();
                    for(int i=0;i<temp.size( );i++)
                    {

                        CtiCCSubstationBusStore::getInstance()->InsertCCEventLogInDB((CtiCCEventLogMsg *) temp[i]);
                        msgCount++;
                    }
                }
                else if (msg->isA() == CTICCEVENTLOG_ID)
                {
                    CtiCCSubstationBusStore::getInstance()->InsertCCEventLogInDB((CtiCCEventLogMsg *) msg);
                    msgCount++;
                }
                if (msg != NULL)
                {
                    delete msg;
                    msg = NULL;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        };
        LONG processTimeSecs = CtiTime().seconds() - processTimeStart.seconds();
        if (_CC_DEBUG & CC_DEBUG_CCEVENTINSERT)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Processed "<<msgCount<<" CC Event Log Messages in "<<processTimeSecs<<" seconds."<< endl;
        }

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
DispatchConnectionPtr CtiCapController::getDispatchConnection()
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
                _dispatchConnection.reset();
            }

            if( _dispatchConnection == NULL )
            {

                CapControlDispatchConnection* conn = new CapControlDispatchConnection("CC to Dispatch", dispatch_port, dispatch_host);
                //Connect to Dispatch
                _dispatchConnection = DispatchConnectionPtr((DispatchConnection*)conn);

                //Send a registration message to Dispatch
                CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("CapController", 0, FALSE );
                _dispatchConnection->WriteConnQue( registrationMsg );
            }
            _dispatchConnection->addMessageListener(this);
        }


        return _dispatchConnection;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

        return DispatchConnectionPtr();
    }
}

/*---------------------------------------------------------------------------
    getPILConnection

    Returns a connection to PIL, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnectionPtr CtiCapController::getPILConnection()
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
                _pilConnection->setName("CC to Pil");

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
void CtiCapController::checkDispatch()
{
    BOOL done = FALSE;
    CtiTime tempTime;
    DispatchConnectionPtr tempPtrDispatchConn = getDispatchConnection();
    tempTime.now();

    int sizeOfQueue = tempPtrDispatchConn->getInQueueHandle().size();

    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - ENTER: Dispatch Queue: " <<sizeOfQueue << endl;
    }
    int counter = 0;
    do
    {
        try
        {
            CtiMessage* inMsg = (CtiMessage*) tempPtrDispatchConn->ReadConnQue(100);
            if ( inMsg != NULL )
            {
                parseMessage(inMsg);
                delete inMsg;
            }
            else
                done = TRUE;
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        counter++;
    }
    while(!done);
    sizeOfQueue = tempPtrDispatchConn->getInQueueHandle().size();

    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Messages Processed from Queue: " << counter<<"  EXIT: Dispatch Queue: " <<sizeOfQueue << endl;
    }
}

/*---------------------------------------------------------------------------
    checkPIL

    Reads off the PIL connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::checkPIL()
{
    BOOL done = FALSE;
    CtiConnection* tempPtrPorterConn = getPILConnection();
    do
    {
        try
        {
            CtiMessage* inMsg = (CtiMessage*) tempPtrPorterConn->ReadConnQue(0);

            if ( inMsg != NULL )
            {
                parseMessage(inMsg);
                delete inMsg;
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
void CtiCapController::updateAllPointQualities(long quality)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Updating CapControl Point Qualities to " << quality<< endl;
    }
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBusPtr currentSubstationBus = ccSubstationBuses[i];

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
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)(ccFeeders[j]);

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
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    {
        CtiPointRegistrationMsg* regMsg;// = new CtiPointRegistrationMsg();
        std::set<long> registrationIds;
        //This is left here as there is no other documentation of this cparm ever existing!
        /*string simple_registration = gConfigParms.getValueAsString("CAP_CONTROL_SIMPLE_REGISTRATION", "false");
        if(simple_registration == "true" || simple_registration == "TRUE")
        {
            //register for all points
            regMsg = new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
        }
        else*/
        {
            //register for each point specifically
            regMsg = new CtiPointRegistrationMsg();


            LONG i=0;

            CtiCCSubstationBus_vec subStationBusChanges;
            CtiCCSubstation_vec stationChanges;
            CtiCCArea_vec areaChanges;

            PaoIdToAreaMap::iterator areaIter = store->getPAOAreaMap()->begin();
            for ( ; areaIter != store->getPAOAreaMap()->end() ; areaIter++)
            {
                CtiCCAreaPtr currentArea = areaIter->second;

                if( currentArea->getVoltReductionControlPointId() > 0 )
                {
                    registrationIds.insert(currentArea->getVoltReductionControlPointId());
                    regMsg->insert(currentArea->getVoltReductionControlPointId());
                }
                if (currentArea->getOperationStats().getUserDefOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentArea->getOperationStats().getUserDefOpSuccessPercentId());
                    regMsg->insert(currentArea->getOperationStats().getUserDefOpSuccessPercentId());
                }
                if (currentArea->getOperationStats().getDailyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentArea->getOperationStats().getDailyOpSuccessPercentId());
                    regMsg->insert(currentArea->getOperationStats().getDailyOpSuccessPercentId());
                }
                if (currentArea->getOperationStats().getWeeklyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentArea->getOperationStats().getWeeklyOpSuccessPercentId());
                    regMsg->insert(currentArea->getOperationStats().getWeeklyOpSuccessPercentId());
                }
                if (currentArea->getOperationStats().getMonthlyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentArea->getOperationStats().getMonthlyOpSuccessPercentId());
                    regMsg->insert(currentArea->getOperationStats().getMonthlyOpSuccessPercentId());
                }
            }
            PaoIdToSpecialAreaMap::iterator spAreaIter = store->getPAOSpecialAreaMap()->begin();
            for ( ; spAreaIter != store->getPAOSpecialAreaMap()->end() ; spAreaIter++)
            {
                CtiCCSpecialPtr currentSpArea = spAreaIter->second;
                if( currentSpArea->getVoltReductionControlPointId() > 0 )
                {
                    registrationIds.insert(currentSpArea->getVoltReductionControlPointId());
                    regMsg->insert(currentSpArea->getVoltReductionControlPointId());
                }
                if (currentSpArea->getOperationStats().getUserDefOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSpArea->getOperationStats().getUserDefOpSuccessPercentId());
                    regMsg->insert(currentSpArea->getOperationStats().getUserDefOpSuccessPercentId());
                }
                if (currentSpArea->getOperationStats().getDailyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSpArea->getOperationStats().getDailyOpSuccessPercentId());
                    regMsg->insert(currentSpArea->getOperationStats().getDailyOpSuccessPercentId());
                }
                if (currentSpArea->getOperationStats().getWeeklyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSpArea->getOperationStats().getWeeklyOpSuccessPercentId());
                    regMsg->insert(currentSpArea->getOperationStats().getWeeklyOpSuccessPercentId());
                }
                if (currentSpArea->getOperationStats().getMonthlyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSpArea->getOperationStats().getMonthlyOpSuccessPercentId());
                    regMsg->insert(currentSpArea->getOperationStats().getMonthlyOpSuccessPercentId());
                }

            }
            PaoIdToSubstationMap::iterator stationIter = store->getPAOStationMap()->begin();
            for ( ; stationIter != store->getPAOStationMap()->end() ; stationIter++)
            {
                CtiCCSubstation* currentStation = stationIter->second;

                if( currentStation->getVoltReductionControlId() > 0 )
                {
                    registrationIds.insert(currentStation->getVoltReductionControlId());
                    regMsg->insert(currentStation->getVoltReductionControlId());
                }
                if ( currentStation->getOperationStats().getUserDefOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentStation->getOperationStats().getUserDefOpSuccessPercentId());
                    regMsg->insert( currentStation->getOperationStats().getUserDefOpSuccessPercentId());
                }
                if ( currentStation->getOperationStats().getDailyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentStation->getOperationStats().getDailyOpSuccessPercentId());
                    regMsg->insert( currentStation->getOperationStats().getDailyOpSuccessPercentId());
                }
                if ( currentStation->getOperationStats().getWeeklyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentStation->getOperationStats().getWeeklyOpSuccessPercentId());
                    regMsg->insert( currentStation->getOperationStats().getWeeklyOpSuccessPercentId());
                }
                if ( currentStation->getOperationStats().getMonthlyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentStation->getOperationStats().getMonthlyOpSuccessPercentId());
                    regMsg->insert( currentStation->getOperationStats().getMonthlyOpSuccessPercentId());
                }

            }
            PaoIdToSubBusMap::iterator busIter = store->getPAOSubMap()->begin();
            for ( ; busIter != store->getPAOSubMap()->end() ; busIter++)
            {
                CtiCCSubstationBusPtr currentSubstationBus = busIter->second;

                if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
                {
                    registrationIds.insert(currentSubstationBus->getCurrentVarLoadPointId());
                    regMsg->insert(currentSubstationBus->getCurrentVarLoadPointId());
                }
                if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
                {
                    registrationIds.insert(currentSubstationBus->getCurrentWattLoadPointId());
                    regMsg->insert(currentSubstationBus->getCurrentWattLoadPointId());
                }
                if (currentSubstationBus->getCurrentVoltLoadPointId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getCurrentVoltLoadPointId());
                    regMsg->insert(currentSubstationBus->getCurrentVoltLoadPointId());
                }
                if (currentSubstationBus->getEstimatedVarLoadPointId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getEstimatedVarLoadPointId());
                    regMsg->insert(currentSubstationBus->getEstimatedVarLoadPointId());
                }
                if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getDailyOperationsAnalogPointId());
                    regMsg->insert(currentSubstationBus->getDailyOperationsAnalogPointId());
                }
                if (currentSubstationBus->getPowerFactorPointId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getPowerFactorPointId());
                    regMsg->insert(currentSubstationBus->getPowerFactorPointId());
                }
                if (currentSubstationBus->getEstimatedPowerFactorPointId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getEstimatedPowerFactorPointId());
                    regMsg->insert(currentSubstationBus->getEstimatedPowerFactorPointId());
                }
                if (currentSubstationBus->getSwitchOverPointId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getSwitchOverPointId());
                    regMsg->insert(currentSubstationBus->getSwitchOverPointId());
                }
                if (currentSubstationBus->getPhaseBId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getPhaseBId());
                    regMsg->insert(currentSubstationBus->getPhaseBId());
                }
                if (currentSubstationBus->getPhaseCId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getPhaseCId());
                    regMsg->insert(currentSubstationBus->getPhaseCId());
                }
                if (currentSubstationBus->getVoltReductionControlId() > 0 )
                {
                    registrationIds.insert(currentSubstationBus->getVoltReductionControlId());
                    regMsg->insert(currentSubstationBus->getVoltReductionControlId());
                }
                if (currentSubstationBus->getDisableBusPointId() > 0 )
                {
                    registrationIds.insert(currentSubstationBus->getDisableBusPointId());
                    regMsg->insert(currentSubstationBus->getDisableBusPointId());
                }
                if ( currentSubstationBus->getOperationStats().getUserDefOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getOperationStats().getUserDefOpSuccessPercentId());
                    regMsg->insert( currentSubstationBus->getOperationStats().getUserDefOpSuccessPercentId());
                }
                if ( currentSubstationBus->getOperationStats().getDailyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getOperationStats().getDailyOpSuccessPercentId());
                    regMsg->insert( currentSubstationBus->getOperationStats().getDailyOpSuccessPercentId());
                }
                if ( currentSubstationBus->getOperationStats().getWeeklyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getOperationStats().getWeeklyOpSuccessPercentId());
                    regMsg->insert( currentSubstationBus->getOperationStats().getWeeklyOpSuccessPercentId());
                }
                if ( currentSubstationBus->getOperationStats().getMonthlyOpSuccessPercentId() > 0)
                {
                    registrationIds.insert(currentSubstationBus->getOperationStats().getMonthlyOpSuccessPercentId());
                    regMsg->insert( currentSubstationBus->getOperationStats().getMonthlyOpSuccessPercentId());
                }


                CtiFeeder_vec &ccFeeders = currentSubstationBus->getCCFeeders();

                for(LONG j=0; j < ccFeeders.size(); j++)
                {
                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)(ccFeeders.at(j));

                    if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                    {
                        registrationIds.insert(currentFeeder->getCurrentVarLoadPointId());
                        regMsg->insert(currentFeeder->getCurrentVarLoadPointId());
                    }
                    if( currentFeeder->getCurrentWattLoadPointId() > 0 )
                    {
                        registrationIds.insert(currentFeeder->getCurrentWattLoadPointId());
                        regMsg->insert(currentFeeder->getCurrentWattLoadPointId());
                    }
                    if ( currentFeeder->getCurrentVoltLoadPointId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getCurrentVoltLoadPointId());
                        regMsg->insert(currentFeeder->getCurrentVoltLoadPointId());
                    }
                    if (currentFeeder->getEstimatedVarLoadPointId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getEstimatedVarLoadPointId());
                        regMsg->insert(currentFeeder->getEstimatedVarLoadPointId());
                    }
                    if (currentFeeder->getDailyOperationsAnalogPointId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getDailyOperationsAnalogPointId());
                        regMsg->insert(currentFeeder->getDailyOperationsAnalogPointId());
                    }
                    if (currentFeeder->getPowerFactorPointId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getPowerFactorPointId());
                        regMsg->insert(currentFeeder->getPowerFactorPointId());
                    }
                    if (currentFeeder->getEstimatedPowerFactorPointId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getEstimatedPowerFactorPointId());
                        regMsg->insert(currentFeeder->getEstimatedPowerFactorPointId());
                    }

                    if (currentFeeder->getPhaseBId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getPhaseBId());
                        regMsg->insert(currentFeeder->getPhaseBId());
                    }
                    if (currentFeeder->getPhaseCId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getPhaseCId());
                        regMsg->insert(currentFeeder->getPhaseCId());
                    }
                    if ( currentFeeder->getOperationStats().getUserDefOpSuccessPercentId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getOperationStats().getUserDefOpSuccessPercentId());
                        regMsg->insert( currentFeeder->getOperationStats().getUserDefOpSuccessPercentId());
                    }
                    if ( currentFeeder->getOperationStats().getDailyOpSuccessPercentId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getOperationStats().getDailyOpSuccessPercentId());
                        regMsg->insert( currentFeeder->getOperationStats().getDailyOpSuccessPercentId());
                    }
                    if ( currentFeeder->getOperationStats().getWeeklyOpSuccessPercentId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getOperationStats().getWeeklyOpSuccessPercentId());
                        regMsg->insert( currentFeeder->getOperationStats().getWeeklyOpSuccessPercentId());
                    }
                    if ( currentFeeder->getOperationStats().getMonthlyOpSuccessPercentId() > 0)
                    {
                        registrationIds.insert(currentFeeder->getOperationStats().getMonthlyOpSuccessPercentId());
                        regMsg->insert( currentFeeder->getOperationStats().getMonthlyOpSuccessPercentId());
                    }

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)(ccCapBanks[k]);

                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            registrationIds.insert(currentCapBank->getStatusPointId());
                            regMsg->insert(currentCapBank->getStatusPointId());
                        }
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            registrationIds.insert(currentCapBank->getOperationAnalogPointId());
                            regMsg->insert(currentCapBank->getOperationAnalogPointId());
                        }
                        if ( currentCapBank->getOperationStats().getUserDefOpSuccessPercentId() > 0)
                        {
                            registrationIds.insert(currentCapBank->getOperationStats().getUserDefOpSuccessPercentId());
                            regMsg->insert( currentCapBank->getOperationStats().getUserDefOpSuccessPercentId());
                        }
                        if ( currentCapBank->getOperationStats().getDailyOpSuccessPercentId() > 0)
                        {
                            registrationIds.insert(currentCapBank->getOperationStats().getDailyOpSuccessPercentId());
                            regMsg->insert( currentCapBank->getOperationStats().getDailyOpSuccessPercentId());
                        }
                        if ( currentCapBank->getOperationStats().getWeeklyOpSuccessPercentId() > 0)
                        {
                            registrationIds.insert(currentCapBank->getOperationStats().getWeeklyOpSuccessPercentId());
                            regMsg->insert(currentCapBank->getOperationStats().getWeeklyOpSuccessPercentId());
                        }
                        if ( currentCapBank->getOperationStats().getMonthlyOpSuccessPercentId() > 0)
                        {
                            registrationIds.insert(currentCapBank->getOperationStats().getMonthlyOpSuccessPercentId());
                            regMsg->insert( currentCapBank->getOperationStats().getMonthlyOpSuccessPercentId());
                        }
                        if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") )
                        {
                            CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)currentCapBank->getTwoWayPoints();
                            //registrationIds.push_back();//pass this down the chain.
                            twoWayPts->addAllCBCPointsToRegMsg(registrationIds);
                            //twoWayPts->addAllCBCPointsToRegMsg(regMsg);
                        }

                    }
                }
            }

            if (CtiCCSubstationBusStore::getInstance()->getLinkStatusPointId() > 0)
            {
                registrationIds.insert(CtiCCSubstationBusStore::getInstance()->getLinkStatusPointId());
                regMsg->insert(CtiCCSubstationBusStore::getInstance()->getLinkStatusPointId());
            }

            if (_VOLT_REDUCTION_SYSTEM_POINTID > 0)
            {
                registrationIds.insert(_VOLT_REDUCTION_SYSTEM_POINTID);
                regMsg->insert(_VOLT_REDUCTION_SYSTEM_POINTID);
            }

        }

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - End Registering for point changes." << endl;
        }

        try
        {
            getDispatchConnection()->registerForPoints(this,registrationIds);
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        regMsg = NULL;
    }
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
        CtiTagMsg* tagMsg;
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
                              resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SUBSTATION ||
                              resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_AREA||
                              resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SPECIALAREA ) )
                        {
                            CtiCCSubstationBusStore::getInstance()->setWasSubBusDeletedFlag(TRUE);
                        }

                        Cti::CapControl::CapControlType objType = Cti::CapControl::Undefined;
                        long changeId = dbChange->getId();
                        if (dbChange->getDatabase() == ChangeCBCStrategyDb)
                        {
                            objType = Cti::CapControl::Strategy;
                        }
                        else if (dbChange->getDatabase() == ChangePAODb && ciStringEqual(dbChange->getObjectType(),"cap bank"))
                        {
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " capBank DB change message received for Cap: "<<dbChange->getId() << endl;
                            }
                            objType = Cti::CapControl::CapBank;
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_VIRTUAL_SYSTEM)
                        {
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " VIRTUAL SYSTEM Device Change "<<dbChange->getId() << endl;
                            }
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SPECIALAREA)
                        {
                            objType = Cti::CapControl::SpecialArea;

                            CtiPAOScheduleManager::getInstance()->setValid(false);
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_AREA)
                        {
                            objType = Cti::CapControl::Area;

                            CtiPAOScheduleManager::getInstance()->setValid(false);
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SUBSTATION)
                        {
                            objType = Cti::CapControl::Substation;

                            CtiPAOScheduleManager::getInstance()->setValid(false);
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_SUBSTATION_BUS)
                        {
                            objType = Cti::CapControl::SubBus;

                            CtiPAOScheduleManager::getInstance()->setValid(false);
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_FEEDER)
                        {
                            objType =  Cti::CapControl::Feeder;
                        }
                        else if (resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_CC_VOLTAGEREGULATOR)
                        {
                            objType =  Cti::CapControl::VoltageRegulatorType;
                        }
                        else if (dbChange->getDatabase() == ChangePAOScheduleDB)
                        {
                            CtiPAOScheduleManager::getInstance()->setValid(false);
                        }
                        else if (dbChange->getDatabase() == ChangePointDb)
                        {
                            PointIdToSubBusMultiMap::iterator      subBusBegin,  subBusEnd;
                            PointIdToFeederMultiMap::iterator      feederBegin,  feederEnd;
                            PointIdToCapBankMultiMap::iterator     capBankBegin, capBankEnd;
                            PointIdToAreaMultiMap::iterator        areaBegin,    areaEnd;
                            PointIdToSpecialAreaMultiMap::iterator sAreaBegin,   sAreaEnd;
                            if (CtiCCSubstationBusStore::getInstance()->findSubBusByPointID(dbChange->getId(), subBusBegin, subBusEnd))
                            {
                                CtiCCSubstationBusPtr sub = subBusBegin->second;
                                objType = Cti::CapControl::SubBus;
                                changeId = sub->getPaoId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findFeederByPointID(dbChange->getId(), feederBegin, feederEnd))
                            {
                                CtiCCFeederPtr feed = feederBegin->second;
                                objType = Cti::CapControl::Feeder;
                                changeId = feed->getPaoId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(dbChange->getId(), capBankBegin, capBankEnd))
                            {
                                CtiCCCapBankPtr cap = capBankBegin->second;
                                objType = Cti::CapControl::CapBank;
                                changeId = cap->getPaoId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findAreaByPointID(dbChange->getId(), areaBegin, areaEnd))
                            {
                                CtiCCAreaPtr area = areaBegin->second;
                                objType = Cti::CapControl::Area;
                                changeId = area->getPaoId();
                            }
                            else if (CtiCCSubstationBusStore::getInstance()->findSpecialAreaByPointID(dbChange->getId(), sAreaBegin, sAreaEnd))
                            {
                                CtiCCSpecialPtr specialArea = sAreaBegin->second;
                                objType = Cti::CapControl::SpecialArea;
                                changeId = specialArea->getPaoId();
                            }
                        }
                        else if (resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_DEVICE &&
                                 (resolveDeviceType(dbChange->getObjectType()) == TYPEVERSACOMCBC ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPEEXPRESSCOMCBC ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPECBC7010 ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPECBC7020 ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPECBC8020 ||
                                  resolveDeviceType(dbChange->getObjectType()) == TYPECBCDNP ||
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
                                objType = Cti::CapControl::CapBank;
                                changeId = cap->getPaoId();
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                     CtiLockGuard<CtiLogger> logger_guard(dout);
                                     dout << CtiTime() << " Cap "<<cap->getPaoName() <<" was found on sub " << endl;
                                }
                            }
                        }
                        else if (objType == Cti::CapControl::Undefined)
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
                    else if (dbChange->getDatabase() == ChangeIvvcZone)
                    {
                        if( _CC_DEBUG & CC_DEBUG_IVVC )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC - Zone Change received. Reloading All Zones." << endl;
                        }

                        CC_DBRELOAD_INFO reloadInfo = {dbChange->getId(), dbChange->getTypeOfChange(), Cti::CapControl::ZoneType};
                        CtiCCSubstationBusStore::getInstance()->insertDBReloadList(reloadInfo);
                    }
                }
                break;
            case MSG_POINTDATA:
                {
                    pData = (CtiPointDataMsg*) message;
                    pointDataMsg(pData);
                }
                break;
            case MSG_PCRETURN:
                {
                    pcReturn = (CtiReturnMsg *)message;
                    porterReturnMsg(pcReturn->DeviceId(), pcReturn->CommandString(), pcReturn->Status(), pcReturn->ResultString());
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
                    else if ( cmdMsg->getOperation() == CtiCommandMsg::UpdateFailed )
                    {
                        /*if( _CC_DEBUG & CC_DEBUG_STANDARD )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Scan completed with an error. This causes Quality to go nonUpdated on all affected points!" << endl;
                        } */
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
                    if(temp.size() > 1 &&  _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - ParseMessage MsgMulti has "<< temp.size() <<" entries."<< endl;
                    }
                    for(i=0;i<temp.size( );i++)
                    {
                        parseMessage(temp[i]);
                    }
                }
                break;
            case MSG_SIGNAL:
                {
                    signal = (CtiSignalMsg *)message;
                    signalMsg(signal->getId(), signal->getTags(), signal->getText(), signal->getAdditionalInfo());
                }
                break;
            case MSG_TAG:
                {
                    tagMsg = (CtiTagMsg*)message;
                    {
                        if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Tag Message - PointID: "<< tagMsg->getPointID()<< " TagDesc:"<<tagMsg->getDescriptionStr() <<"."<< endl;
                        }
                    }
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

void CtiCapController::adjustAlternateBusModeValues(long pointID, double value, CtiCCSubstationBusPtr currentBus)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    if (currentBus->getAltDualSubId() == currentBus->getPaoId() )
    {
        if (ciStringEqual(currentBus->getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit) )
        {
            CtiCCSubstationBusPtr altSub = store->findSubBusByPAObjectID(currentBus->getAltDualSubId());
            if (altSub != NULL)
            {
                currentBus->setAltSubControlValue(altSub->getTotalizedVarLoadPointValue());
                currentBus->setAllAltSubValues(altSub->getCurrentVoltLoadPointValue(),
                                       altSub->getTotalizedVarLoadPointValue(),
                                       altSub->getRawCurrentWattLoadPointValue());
            }
        }
        else
        {
            currentBus->setAltSubControlValue(currentBus->getRawCurrentVarLoadPointValue());
        }
        currentBus->setBusUpdatedFlag(TRUE);
        return;
    }
    CtiCCSubstationBusPtr primarySub = store->findSubBusByPAObjectID(currentBus->getAltDualSubId());
    if (primarySub != NULL)
    {
        if( ciStringEqual(currentBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
            ciStringEqual(currentBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit) ||
            ciStringEqual(currentBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
        {
            if (primarySub->getPrimaryBusFlag())
            {
                primarySub->setAllAltSubValues((primarySub->getCurrentVoltLoadPointValue() + currentBus->getCurrentVoltLoadPointValue()) / 2,
                                       primarySub->getTotalizedVarLoadPointValue() + currentBus->getTotalizedVarLoadPointValue(),
                                       primarySub->getRawCurrentWattLoadPointValue() + currentBus->getRawCurrentWattLoadPointValue());
                primarySub->setNewPointDataReceivedFlag(TRUE);

                if (currentBus->isControlPoint(pointID) && primarySub->getStrategy()->getIntegrateFlag())
                {
                    if (currentBus->getCurrentVarLoadPointId() == pointID)
                    {
                        primarySub->updateIntegrationVPoint(CtiTime());
                    }
                    if (currentBus->getCurrentWattLoadPointId() == pointID)
                    {
                        primarySub->updateIntegrationWPoint(CtiTime());
                    }
                }
                primarySub->figureEstimatedVarLoadPointValue();
            }

            currentBus->setAllAltSubValues(currentBus->getCurrentVoltLoadPointValue(),
                                             currentBus->getTotalizedVarLoadPointValue(),
                                             currentBus->getRawCurrentWattLoadPointValue());

            primarySub->setBusUpdatedFlag(TRUE);
            currentBus->setBusUpdatedFlag(TRUE);
        }
        else if (ciStringEqual(currentBus->getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit) )
        {
            if (currentBus->getSwitchOverStatus())
            {

                currentBus->setAllAltSubValues(primarySub->getCurrentVoltLoadPointValue(),
                                                 primarySub->getCurrentVarLoadPointValue(),
                                                 primarySub->getCurrentWattLoadPointValue());

            }

        }

    }
}


void CtiCapController::handleAlternateBusModeValues(long pointID, double value, CtiCCSubstationBusPtr currentSubstationBus)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    BOOL updateSwitchOverStatus = TRUE;

    if (value != currentSubstationBus->getSwitchOverStatus())
    {
        LONG stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
        getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSwitchOverUpdate, currentSubstationBus->getEventSequence(), value, "Switch Over Point Updated", "cap control"));
        if (!currentSubstationBus->getDualBusEnable())
        {
            if (value > 0)
            {
                if (!currentSubstationBus->getDisableFlag())
                {
                    currentSubstationBus->setDisableFlag(TRUE);
                    currentSubstationBus->setReEnableBusFlag(TRUE);
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, "Substation Bus Disabled By Inhibit", "cap control"));
                    string text = currentSubstationBus->getPaoName();
                    text += " Disabled";
                    string additional = string("Inhibit PointId Updated");

                    sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));
                }

            }
            else
            {
                currentSubstationBus->setDisableFlag(FALSE);
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, "Substation Bus Enabled By Inhibit", "cap control"));
                string text = currentSubstationBus->getPaoName();
                text += " Enabled";
                string additional = string("Inhibit PointId Updated");

                sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));

            }


        }
        else //dual Bus Enabled.
        {
            string text = currentSubstationBus->getPaoName();
            if (currentSubstationBus->getAltDualSubId() != currentSubstationBus->getPaoId())
            {
                CtiCCSubstationBusPtr altSub = NULL;
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
                            if (currentSubstationBus->getPrimaryBusFlag())
                            {
                                updateSwitchOverStatus = FALSE;
                                text += " is already operating in Dual Bus Mode operating as Primary for ";
                                text += altSub->getPaoName();
                                text += ".";
                            }
                            else
                            {
                                currentSubstationBus->setDisableFlag(TRUE);
                                currentSubstationBus->setReEnableBusFlag(TRUE);
                                altSub->setDisableFlag(TRUE);
                                altSub->setReEnableBusFlag(TRUE);
                                altSub->setBusUpdatedFlag(TRUE);
                                text += " Disabled by Alt Sub";
                            }
                        }
                        else
                        {
                            text += " Alt Sub Enabled";
                            if (ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
                                ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit) ||
                                ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                            {

                                altSub->setPrimaryBusFlag(TRUE);
                                altSub->setBusUpdatedFlag(TRUE);
                                altSub->setAllAltSubValues((altSub->getCurrentVoltLoadPointValue() + currentSubstationBus->getCurrentVoltLoadPointValue()) / 2,
                                                       altSub->getTotalizedVarLoadPointValue() + currentSubstationBus->getTotalizedVarLoadPointValue(),
                                                       altSub->getRawCurrentWattLoadPointValue() + currentSubstationBus->getRawCurrentWattLoadPointValue());

                                if ((altSub->isControlPoint(pointID) || currentSubstationBus->isControlPoint(pointID) ) && altSub->getStrategy()->getIntegrateFlag())
                                {
                                    altSub->setIVCount( 0 );
                                    altSub->updateIntegrationVPoint(CtiTime());
                                    altSub->setIWCount( 0 );
                                    altSub->updateIntegrationWPoint(CtiTime());
                                }

                                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                                int j = ccFeeders.size();
                                while (j > 0)
                                {
                                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j-1];

                                    CtiCCExecutorFactory::createExecutor(new CtiCCObjectMoveMsg(false, currentSubstationBus->getPaoId(),
                                                                                                currentFeeder->getPaoId(), altSub->getPaoId(),
                                                                                                currentFeeder->getDisplayOrder() + 0.5))->execute();
                                    j--;
                                }
                                altSub->reOrderFeederDisplayOrders();
                                store->UpdateFeederSubAssignmentInDB(altSub);


                            }
                        }

                    }
                    else
                    {
                        if (currentSubstationBus->getDisableFlag() && currentSubstationBus->getReEnableBusFlag())
                        {
                            currentSubstationBus->setDisableFlag(FALSE);
                            currentSubstationBus->setReEnableBusFlag(FALSE);
                            text += " ReEnabled";
                            if (altSub->getAltDualSubId() == currentSubstationBus->getPaoId())
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
                        {
                            altSub->setPrimaryBusFlag(FALSE);
                            altSub->setBusUpdatedFlag(TRUE);
                            text += " Alt Sub Not Enabled";
                            if (ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
                                ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit) ||
                                ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                            {

                                CtiFeeder_vec& ccFeeders = altSub->getCCFeeders();
                                int j = ccFeeders.size();
                                while (j > 0)
                                {
                                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j-1];
                                    if (currentFeeder->getOriginalParent().getOriginalParentId() == currentSubstationBus->getPaoId())
                                    {
                                        CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::RETURN_FEEDER_TO_ORIGINAL_SUBBUS, currentFeeder->getPaoId()))->execute();
                                    }
                                    j--;
                                }
                                currentSubstationBus->reOrderFeederDisplayOrders();
                                altSub->reOrderFeederDisplayOrders();
                                store->UpdateFeederSubAssignmentInDB(currentSubstationBus);
                                store->UpdateFeederSubAssignmentInDB(altSub);
                                if ((altSub->isControlPoint(pointID) || currentSubstationBus->isControlPoint(pointID) ) && altSub->getStrategy()->getIntegrateFlag())
                                {
                                    altSub->setIVCount( 0 );
                                    altSub->updateIntegrationVPoint(CtiTime());
                                    altSub->setIWCount( 0 );
                                    altSub->updateIntegrationWPoint(CtiTime());
                                    currentSubstationBus->setIVCount( 0 );
                                    currentSubstationBus->updateIntegrationVPoint(CtiTime());
                                    currentSubstationBus->setIWCount( 0 );
                                    currentSubstationBus->updateIntegrationWPoint(CtiTime());
                                }
                            }
                        }

                    }
                }
            }
            string additional = string("Dual Bus Switch PointId Updated");
            sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));
        }
    }
    else if (value > 0 && !currentSubstationBus->getDisableFlag() && !currentSubstationBus->getDualBusEnable() )
    {
        currentSubstationBus->setDisableFlag(TRUE);
        currentSubstationBus->setReEnableBusFlag(TRUE);
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
        LONG stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
        getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, "Substation Bus Disabled By Inhibit", "cap control"));
        string text = currentSubstationBus->getPaoName();
        text += " Disabled";
        string additional = string("Inhibit PointId Updated");

        sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"));

    }
    if (updateSwitchOverStatus)
    {
        currentSubstationBus->setSwitchOverStatus(value);
        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
        currentSubstationBus->setBusUpdatedFlag(TRUE);
    }

}

/*---------------------------------------------------------------------------
    pointDataMsg

    Handles point data messages and updates substation bus point values.
---------------------------------------------------------------------------*/
void CtiCapController::pointDataMsg (CtiPointDataMsg* message)
{
    int pointID = message->getId();
    double value = message->getValue();
    unsigned quality = message->getQuality();
    unsigned tags = message->getTags();
    CtiTime timestamp = message->getTime();

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

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    try
    {
        CapControlPointDataHandler pointHandler = store->getPointDataHandler();
        pointHandler.processIncomingPointData(message);

        if (!Cti::CapControl::isQualityOk(quality))
        {
            return;
        }
        pointDataMsgBySubBus(pointID, value, quality, timestamp);

        pointDataMsgByFeeder(pointID, value, quality, timestamp);

        pointDataMsgByCapBank(pointID, value, quality, tags, timestamp);

        pointDataMsgBySubstation(pointID, value, quality, timestamp);

        pointDataMsgByArea(pointID, value, quality, timestamp);

        pointDataMsgBySpecialArea(pointID, value, quality, timestamp);

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
        if (pointID == _VOLT_REDUCTION_SYSTEM_POINTID)
        {
            if (value != store->getVoltReductionSystemDisabled())
            {
                if (value > 0)
                {
                    store->setVoltReductionSystemDisabled(TRUE);
                    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
                    for(LONG i=0;i<ccAreas.size();i++)
                    {
                        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
                        if (currentArea != NULL)
                        {
                            CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_DISABLE_OVUV, currentArea->getPaoId()))->execute();
                        }
                    }
                }
                else
                {
                    store->setVoltReductionSystemDisabled(FALSE);
                    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
                    for(LONG i=0;i<ccAreas.size();i++)
                    {
                        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
                        if (currentArea != NULL)
                        {
                            CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_ENABLE_OVUV, currentArea->getPaoId()))->execute();
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
    return;
}

void CtiCapController::pointDataMsgByArea( long pointID, double value, unsigned quality, CtiTime& timestamp)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();



    CtiCCAreaPtr currentArea = NULL;
    PointIdToAreaMultiMap::iterator areaIter, end;
    store->findAreaByPointID(pointID, areaIter, end);

    while (areaIter != end)
    {
        try
        {
            currentArea = areaIter->second;
            if (currentArea != NULL)
            {
                if( currentArea->getVoltReductionControlPointId() == pointID )
                {
                    //if( timestamp > currentArea->getLastControlPointUpdateTime() )
                    {
                        if (currentArea->getVoltReductionControlValue() != value)
                        {
                            currentArea->setVoltReductionControlValue(value);
                            if (_AUTO_VOLT_REDUCTION)
                            {
                                if (currentArea->getVoltReductionControlValue())
                                {
                                    CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_DISABLE_OVUV, currentArea->getPaoId()))->execute();
                                }
                                else
                                {
                                    CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_ENABLE_OVUV, currentArea->getPaoId()))->execute();
                                }
                            }
                            currentArea->checkAndUpdateChildVoltReductionFlags();
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
        areaIter++;
    }
}
void CtiCapController::pointDataMsgBySpecialArea( long pointID, double value, unsigned quality, CtiTime& timestamp)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    CtiCCSpecialPtr currentSpArea = NULL;
    PointIdToSpecialAreaMultiMap::iterator saIter, end;
    store->findSpecialAreaByPointID(pointID, saIter, end);

    while (saIter != end)
    {
        try
        {
            currentSpArea = saIter->second;
            if (currentSpArea != NULL)
            {
                if( currentSpArea->getVoltReductionControlPointId() == pointID )
                {
                   // if( timestamp > currentSpArea->getLastControlPointUpdateTime() )
                    {
                        if (currentSpArea->getVoltReductionControlValue() != value &&
                            !currentSpArea->getDisableFlag())
                        {
                            currentSpArea->setVoltReductionControlValue(value);
                            if (_AUTO_VOLT_REDUCTION)
                            {
                                if (currentSpArea->getVoltReductionControlValue())
                                {
                                    CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_DISABLE_OVUV, currentSpArea->getPaoId()))->execute();
                                }
                                else
                                {
                                    CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_ENABLE_OVUV, currentSpArea->getPaoId()))->execute();
                                }
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
        saIter++;
    }
}
void CtiCapController::pointDataMsgBySubstation( long pointID, double value, unsigned quality, CtiTime& timestamp)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    CtiCCSubstation* currentStation = NULL;
    CtiCCAreaPtr currentArea = NULL;
    PointIdToSubstationMultiMap::iterator stationIter, end;
    store->findSubstationByPointID(pointID, stationIter, end);

    while (stationIter != end)
    {
        try
        {
            currentStation = stationIter->second;
            if (currentStation != NULL)
            {
                if (currentStation->getVoltReductionControlId() == pointID)
                {
                    if (!currentStation->getVoltReductionFlag())
                    {
                        if (value > 0)
                        {
                            currentStation->setVoltReductionFlag(TRUE);
                            currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                            if (currentArea != NULL)
                            {
                                currentArea->setChildVoltReductionFlag(TRUE);
                            }
                            if (_AUTO_VOLT_REDUCTION)
                            {
                                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_DISABLE_OVUV, currentStation->getPaoId()))->execute();
                            }
                        }
                    }
                    else
                    {
                        if (value == 0)
                        {
                            currentStation->setVoltReductionFlag(FALSE);
                            currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                            if (currentArea != NULL)
                            {
                                currentArea->checkAndUpdateChildVoltReductionFlags();
                            }
                            if (_AUTO_VOLT_REDUCTION)
                            {
                                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_ENABLE_OVUV, currentStation->getPaoId()))->execute();
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
        stationIter++;
    }
}

void CtiCapController::pointDataMsgBySubBus( long pointID, double value, unsigned quality, CtiTime& timestamp)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCSubstation* currentStation = NULL;
    CtiCCAreaPtr currentArea = NULL;

    PointIdToSubBusMultiMap::iterator subIter, end;
    store->findSubBusByPointID(pointID, subIter, end);

    while (subIter != end)
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

                            if (currentSubstationBus->getRawCurrentVarLoadPointValue() != value)
                            {
                                currentSubstationBus->setCurrentVarLoadPointValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getStrategy()->getIntegrateFlag())
                            {
                                currentSubstationBus->updateIntegrationVPoint(CtiTime());
                            }
                            adjustAlternateBusModeValues(pointID, value, currentSubstationBus);

                        }
                        else //phase A point id
                        {
                            if (currentSubstationBus->getPhaseAValue() != value)
                            {
                                currentSubstationBus->setPhaseAValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue(), timestamp);
                            }
                            if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getStrategy()->getIntegrateFlag())
                            {
                                currentSubstationBus->updateIntegrationVPoint(CtiTime());
                            }
                            adjustAlternateBusModeValues(pointID, value, currentSubstationBus);
                        }
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentSubstationBus->setCurrentVarPointQuality(quality);
                        if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                        {
                            sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }

                        if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
                        {
                            if( ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                            {
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(value,currentSubstationBus->getCurrentWattLoadPointValue()),timestamp);
                            }
                            currentSubstationBus->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            if( currentSubstationBus->getPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                        }
                        else if( !( ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
                                    ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::VoltsControlUnit)) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Watt Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        if (currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::IntegratedVoltVar)
                        {
                            currentSubstationBus->figureAndSetTargetVarValue();
                        }
                    }

                }
                else if( currentSubstationBus->getCurrentWattLoadPointId() == pointID )
                {
                    if( timestamp > currentSubstationBus->getLastWattPointTime() )
                    {
                        currentSubstationBus->setLastWattPointTime(timestamp);
                        if (ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit))
                        {
                            DOUBLE tempKQ = currentSubstationBus->convertKVARToKQ(value,currentSubstationBus->getCurrentWattLoadPointValue());
                            currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(tempKQ,value),timestamp);
                        }

                        if (currentSubstationBus->getCurrentWattLoadPointValue() != value)
                        {
                            currentSubstationBus->setCurrentWattLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                        if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getStrategy()->getIntegrateFlag())
                        {
                            currentSubstationBus->updateIntegrationWPoint(CtiTime());
                        }

                        currentSubstationBus->setCurrentWattPointQuality(quality);

                        if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
                        {
                            currentSubstationBus->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            if( currentSubstationBus->getPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                            }
                        }
                        else if( !ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Var Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        adjustAlternateBusModeValues(pointID, value, currentSubstationBus);
                    }
                }
                else if( currentSubstationBus->getCurrentVoltLoadPointId() == pointID )
                {

                    if( timestamp > currentSubstationBus->getLastVoltPointTime() )
                    {
                        currentSubstationBus->setLastVoltPointTime(timestamp);
                        if (currentSubstationBus->getCurrentVoltLoadPointValue() != value)
                        {
                            currentSubstationBus->setCurrentVoltLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                        if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getStrategy()->getIntegrateFlag())
                        {
                            currentSubstationBus->updateIntegrationVPoint(CtiTime());
                        }
                        currentSubstationBus->setCurrentVoltPointQuality(quality);
                        adjustAlternateBusModeValues(pointID, value, currentSubstationBus);
                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);
                        currentSubstationBus->figureAndSetTargetVarValue();
                    }
                }
                else if (currentSubstationBus->getSwitchOverPointId() == pointID)
                {
                    handleAlternateBusModeValues(pointID, value, currentSubstationBus);
                }

                if ((currentSubstationBus->getPhaseBId() == pointID ||
                         currentSubstationBus->getPhaseCId() == pointID ) &&
                         currentSubstationBus->getUsePhaseData())
                {
                    if( timestamp >= currentSubstationBus->getLastCurrentVarPointUpdateTime() )
                    {
                        currentSubstationBus->setNewPointDataReceivedFlag(TRUE);

                        if (currentSubstationBus->getPhaseBId() == pointID)
                        {
                            if (currentSubstationBus->getPhaseBValue() != value)
                            {
                                currentSubstationBus->setPhaseBValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue(),timestamp);
                            }
                        }
                        if (currentSubstationBus->getPhaseCId() == pointID)
                        {
                            if (currentSubstationBus->getPhaseCValue() != value)
                            {
                                currentSubstationBus->setPhaseCValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue(),timestamp);
                            }
                        }

                        adjustAlternateBusModeValues(pointID, value, currentSubstationBus);
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentSubstationBus->figureAndSetTargetVarValue();
                    }
                }
                else if (currentSubstationBus->getDailyOperationsAnalogPointId()  == pointID)
                {
                    if( currentSubstationBus->getDailyOperationsAnalogPointId()  == pointID)
                    {
                        if (currentSubstationBus->getCurrentDailyOperations() != value)
                        {
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                        currentSubstationBus->setCurrentDailyOperations(value);
                    }
                }
                else if (currentSubstationBus->getEstimatedPowerFactorPointId()  == pointID||
                         currentSubstationBus->getEstimatedVarLoadPointId()  == pointID||
                         currentSubstationBus->getPowerFactorPointId() == pointID )
                {
                    if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Optional POINT data message received for: " << pointID << " on SUB: " << currentSubstationBus->getPaoName() << endl;
                    }
                }


                // check for alt sub bus id, and update all sub's alt values
                PaoIdToPointIdMultiMap::iterator it;
                std::pair<PaoIdToPointIdMultiMap::iterator,PaoIdToPointIdMultiMap::iterator> ret;

                ret = store->getSubsWithAltSubID(currentSubstationBus->getPaoId());
                for (it = ret.first; it != ret.second; it++)
                {
                    CtiCCSubstationBusPtr altSub = store->findSubBusByPAObjectID((*it).second);
                    if (altSub != NULL)
                    {
                        if( ciStringEqual(altSub->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
                            ciStringEqual(altSub->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit) ||
                            ciStringEqual(altSub->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                        {
                            if (currentSubstationBus->getPrimaryBusFlag())
                            {
                                currentSubstationBus->setAllAltSubValues((altSub->getCurrentVoltLoadPointValue() + currentSubstationBus->getCurrentVoltLoadPointValue()) / 2,
                                                           altSub->getRawCurrentVarLoadPointValue() + currentSubstationBus->getRawCurrentVarLoadPointValue(),
                                                           altSub->getRawCurrentWattLoadPointValue() + currentSubstationBus->getRawCurrentWattLoadPointValue());
                                currentSubstationBus->setBusUpdatedFlag(TRUE);

                            }
                        }
                        else
                        {
                             altSub->setAllAltSubValues(currentSubstationBus->getCurrentVoltLoadPointValue(), currentSubstationBus->getCurrentVarLoadPointValue(),
                             currentSubstationBus->getCurrentWattLoadPointValue());
                             altSub->setAltSubControlValue(currentSubstationBus->getTotalizedVarLoadPointValue());
                             altSub->setBusUpdatedFlag(TRUE);
                             altSub->setNewPointDataReceivedFlag(currentSubstationBus->getNewPointDataReceivedFlag());
                        }
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentSubstationBus->figureAndSetTargetVarValue();
                    }
                }

                if (currentSubstationBus->getVoltReductionControlId() == pointID)
                {
                    if (!currentSubstationBus->getVoltReductionFlag())
                    {
                        if (value > 0)
                        {
                            currentSubstationBus->setVoltReductionFlag(TRUE);
                            currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (currentStation != NULL)
                            {
                                currentStation->setChildVoltReductionFlag(TRUE);
                                currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                                if (currentArea != NULL)
                                {
                                    currentArea->setChildVoltReductionFlag(TRUE);
                                }
                            }


                            if (_AUTO_VOLT_REDUCTION)
                            {
                                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_DISABLE_OVUV, currentSubstationBus->getPaoId()))->execute();
                            }
                        }
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                    }
                    else
                    {
                        if (value == 0)
                        {
                            currentSubstationBus->setVoltReductionFlag(FALSE);
                            store->checkAndUpdateVoltReductionFlagsByBus(currentSubstationBus);

                            if (_AUTO_VOLT_REDUCTION)
                            {
                                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::AUTO_ENABLE_OVUV, currentSubstationBus->getPaoId()))->execute();
                            }

                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                        }
                    }

                }
                if (currentSubstationBus->getDisableBusPointId() == pointID)
                {
                    if (value > 0 && !currentSubstationBus->getDisableFlag())
                    {
                       CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::DISABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()))->execute();
                       currentSubstationBus->setReEnableBusFlag(TRUE);
                    }
                    if (value == 0 && currentSubstationBus->getDisableFlag() && currentSubstationBus->getReEnableBusFlag())
                    {
                       CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()))->execute();
                    }
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        subIter++;
    }

}

void CtiCapController::pointDataMsgByFeeder( long pointID, double value, unsigned quality, CtiTime& timestamp )
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = NULL;
    PointIdToFeederMultiMap::iterator feedIter, end;
    store->findFeederByPointID(pointID, feedIter, end);

    while (feedIter != end)
    {
        currentFeeder = feedIter->second;
        try
        {
            currentSubstationBus = NULL;
            //CtiCCFeederPtr currentFeeder = store->findFeederByPointID(pointID, feederCount);
            if (currentFeeder != NULL)
            {
                long subBusId = store->findSubBusIDbyFeederID(currentFeeder->getPaoId());
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
                                    currentFeeder->setCurrentVarLoadPointValue(value, timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    if (currentFeeder->isControlPoint(pointID) && currentFeeder->getStrategy()->getIntegrateFlag())
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
                                        currentFeeder->setPhaseAValue(value,timestamp);
                                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                                        currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue(), timestamp);
                                        if (currentFeeder->isControlPoint(pointID) && currentFeeder->getStrategy()->getIntegrateFlag())
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
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                                {
                                    currentFeeder->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(value,currentFeeder->getCurrentWattLoadPointValue()), timestamp);
                                }
                                currentFeeder->setPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getCurrentVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentFeeder->setEstimatedPowerFactorValue(currentSubstationBus->calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentSubstationBus->figureAndSetPowerFactorByFeederValues();
                                store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                                if( currentFeeder->getPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                            }                          
                            else if( !( ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
                                        ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::VoltsControlUnit) ))
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Watt Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                    }
                    else if( currentFeeder->getCurrentWattLoadPointId() == pointID )
                    {
                        if( timestamp > currentFeeder->getLastWattPointTime() )
                        {
                            currentFeeder->setLastWattPointTime(timestamp);
                            if( ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                            {
                                DOUBLE tempKQ = currentSubstationBus->convertKVARToKQ(value,currentFeeder->getCurrentWattLoadPointValue());
                                currentFeeder->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(tempKQ,value),timestamp);
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
                                currentSubstationBus->figureAndSetPowerFactorByFeederValues();
                                store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                                if( currentFeeder->getPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType));
                                }
                            }
                            else if( !ciStringEqual(currentSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Var Point, cannot calculate power factor, in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }

                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                    }
                    else if( currentFeeder->getCurrentVoltLoadPointId() == pointID )
                    {
                        if( timestamp > currentFeeder->getLastVoltPointTime() )
                        {
                            currentFeeder->setLastVoltPointTime(timestamp);
                            if (currentFeeder->getCurrentVoltLoadPointValue() != value)
                            {
                                currentFeeder->setCurrentVoltLoadPointValue(value);
                                currentFeeder->setNewPointDataReceivedFlag(TRUE);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentFeeder->setCurrentVoltPointQuality(quality);

                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                    }
                    else if (currentFeeder->getDailyOperationsAnalogPointId()  == pointID )
                    {
                        if(  currentFeeder->getDailyOperationsAnalogPointId()  == pointID)
                        {
                            if (currentFeeder->getCurrentDailyOperations() != value)
                            {
                                //sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getDailyOperationsAnalogPointId(),value,NormalQuality,AnalogPointType));
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            currentFeeder->setCurrentDailyOperations(value);

                        }
                    }
                    else if ( currentFeeder->getEstimatedPowerFactorPointId()  == pointID||
                          currentFeeder->getEstimatedVarLoadPointId()  == pointID||
                          currentFeeder->getPowerFactorPointId() == pointID )
                    {
                        if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Optional POINT data message received for: " << pointID << " on SUB: " << currentSubstationBus->getPaoName() << endl;
                        }

                        //do nothing
                    }

                    if ((currentFeeder->getPhaseBId() == pointID ||
                         currentFeeder->getPhaseCId() == pointID ) &&
                         currentFeeder->getUsePhaseData())
                    {
                        if( timestamp >= currentFeeder->getLastCurrentVarPointUpdateTime() )
                        {
                            if (currentFeeder->getPhaseBId() == pointID)
                            {
                                if (currentFeeder->getPhaseBValue() != value)
                                {
                                    currentFeeder->setNewPointDataReceivedFlag(TRUE);

                                    currentFeeder->setPhaseBValue(value, timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue(),timestamp);
                                }
                            }
                            if (currentFeeder->getPhaseCId() == pointID)
                            {
                                if (currentFeeder->getPhaseCValue() != value)
                                {
                                    currentFeeder->setNewPointDataReceivedFlag(TRUE);

                                    currentFeeder->setPhaseCValue(value, timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue(),timestamp);
                                }
                            }
                            currentFeeder->figureEstimatedVarLoadPointValue();

                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
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
    }

    /*if( pointChanges.size() > 0 )
    {
        getDispatchConnection()->WriteConnQue(multiPointMsg);
    }*/

}


void CtiCapController::pointDataMsgByCapBank( long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = NULL;
    CtiCCCapBankPtr currentCapBank = NULL;
    PointIdToCapBankMultiMap::iterator capIter, end;
    store->findCapBankByPointID(pointID, capIter, end);

    while (capIter != end)
    {
        try
        {
            currentCapBank = capIter->second;
            currentSubstationBus = NULL;
            currentFeeder = NULL;
            if (currentCapBank != NULL)
            {
                long subBusId = store->findSubBusIDbyCapBankID(currentCapBank->getPaoId());
                if (subBusId != NULL)
                {
                    currentSubstationBus = store->findSubBusByPAObjectID(subBusId);
                    if (currentSubstationBus != NULL)
                    {
                        long feederId = store->findFeederIDbyCapBankID(currentCapBank->getPaoId());
                        if (feederId != NULL)
                        {
                            currentFeeder = store->findFeederByPAObjectID(feederId);
                        }
                    }
                }

                if (currentSubstationBus != NULL && currentFeeder != NULL)
                {
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
                            if (currentCapBank->getControlStatus() != (LONG)value &&
                                !currentCapBank->getInsertDynamicDataFlag() &&
                                timestamp > currentCapBank->getLastStatusChangeTime())
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - CapBank: "<<currentCapBank->getPaoName()<<" State adjusted from "<<currentCapBank->getControlStatus() <<" to "<<value<< endl;
                                }
                                if ((currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending) &&
                                    value < 6 )
                                {
                                    currentCapBank->setBeforeVarsString(" Point Data ");
                                    currentCapBank->setAfterVarsString(" Point Data ");
                                    currentCapBank->setPercentChangeString(" --- ");
                                    string text = "";
                                    text = string("Var: Cancelled by Pending Override, ");
                                    currentCapBank->setControlStatus((LONG)value);
                                    text += currentCapBank->getControlStatusText();
                                    currentFeeder->setRetryIndex(0);
                                    LONG stationId, areaId, spAreaId;
                                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                    CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, "point data msg" );
                                    eventMsg->setActionId(currentCapBank->getActionId());
                                    eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());

                                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
                                    currentCapBank->setIgnoreFlag(FALSE);
                                    currentCapBank->setPorterRetFailFlag(FALSE);
                                }
                                currentCapBank->setControlStatus((LONG)value);
                                currentCapBank->setTagsControlStatus((LONG)tags);
                                currentCapBank->setLastStatusChangeTime(timestamp);
                                currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
                            }
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
                    }
                    else if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") )
                    {
                        CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)currentCapBank->getTwoWayPoints();
                        //NEED to check this value for a toggle, before setting status points.
                        if (currentCapBank->getPointIdByAttribute(PointAttribute::IgnoredIndicator) == pointID)
                        {

                            if (twoWayPts->getPointValueByAttribute(PointAttribute::IgnoredIndicator)!= value &&
                                timestamp >= currentCapBank->getIgnoreIndicatorTimeUpdated() )
                            {
                                currentCapBank->setIgnoreIndicatorTimeUpdated(timestamp);
                                store->insertRejectedCapBankList(currentCapBank);
                            }
                        }
                        if (twoWayPts->setTwoWayStatusPointValue(pointID, value, timestamp))
                        {
                            if (currentCapBank->getPointIdByAttribute(PointAttribute::CapacitorBankState) == pointID )
                            {
                                if (currentCapBank->getReportedCBCState() != value &&
                                    currentCapBank->getReportedCBCState() >= 0 )
                                {
                                    currentCapBank->setReportedCBCStateTime(timestamp);

                                    if ( (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending &&
                                          value == 1 ) ||
                                         (currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending &&
                                          value == 0 ) )
                                    {
                                        currentCapBank->setUnsolicitedChangeTimeUpdated(timestamp);
                                        store->insertUnexpectedUnsolicitedList(currentCapBank);
                                        currentCapBank->setUnsolicitedPendingFlag(TRUE);
                                    }

                                    if ( currentCapBank->getControlStatus() != value &&
                                         !currentCapBank->getControlRecentlySentFlag() &&
                                          !currentCapBank->getVerificationFlag() &&
                                          !currentCapBank->getInsertDynamicDataFlag()  &&
                                          !currentCapBank->isFailedOrQuestionableStatus() )
                                    {
                                        currentCapBank->setControlStatus(value);
                                        currentCapBank->setLastStatusChangeTime(CtiTime());
                                        currentCapBank->setUnsolicitedChangeTimeUpdated(timestamp);
                                        store->insertUnsolicitedCapBankList(currentCapBank);
                                        currentCapBank->setUnsolicitedPendingFlag(TRUE);
                                    }

                                }
                                else if (stringContainsIgnoreCase(currentCapBank->getAfterVarsString(), "Manual") &&
                                    ( (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::Close ) &&
                                       value !=  currentCapBank->getControlStatus()) )
                                {
                                    currentCapBank->setControlStatus(value);
                                    currentCapBank->setAfterVarsString("Adjusted to CBC Reported State");
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - Adjusting CapBank status to match CBC..."<< endl;
                                    }
                                    sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                                }

                                if ( !currentFeeder->getRecentlyControlledFlag() &&
                                     currentCapBank->getControlRecentlySentFlag() &&
                                     ( (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::Close ) &&
                                       value ==  currentCapBank->getControlStatus()) )
                                {
                                    currentCapBank->setControlRecentlySentFlag(FALSE);
                                }
                                currentCapBank->setReportedCBCState(twoWayPts->getPointValueByAttribute(PointAttribute::CapacitorBankState));
                                store->set2wayFlagUpdate(TRUE);

                            }
                            if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Set a cbc 2 way status point... pointID ("<<pointID<<") = "<<value<< endl;
                            }
                            if (twoWayPts->getPointValueByAttribute(PointAttribute::AutoVoltControl))
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
                            if (twoWayPts->getPointValueByAttribute(PointAttribute::LastControlOvUv) > 0)
                            {
                                if (!currentCapBank->getOvUvSituationFlag())
                                {
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    currentCapBank->setOvUvSituationFlag(TRUE);
                                }
                            }
                            else
                            {
                                if (currentCapBank->getOvUvSituationFlag())
                                {
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                    currentCapBank->setOvUvSituationFlag(FALSE);
                                }

                            }

                            if (twoWayPts->getPointValueByAttribute(PointAttribute::ControlMode) == 0 )
                            {
                                if (currentCapBank->getLocalControlFlag())
                                {
                                    currentCapBank->setLocalControlFlag(FALSE);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                                }
                            }
                            else
                            {
                                if (!currentCapBank->getLocalControlFlag())
                                {
                                    currentCapBank->setLocalControlFlag(TRUE);
                                    currentSubstationBus->setBusUpdatedFlag(TRUE);

                                }
                            }

                        }
                        else if (twoWayPts->setTwoWayAnalogPointValue(pointID, value, timestamp))
                        {
                            if (currentCapBank->getPointIdByAttribute(PointAttribute::UDPIpAddress) == pointID)
                            {
                                currentCapBank->setIpAddress(twoWayPts->getPointValueByAttribute(PointAttribute::UDPIpAddress));
                            }
                            else if (currentCapBank->getPointIdByAttribute(PointAttribute::UDPPortNumber) == pointID)
                            {
                                currentCapBank->setUDPPort(twoWayPts->getPointValueByAttribute(PointAttribute::UDPPortNumber));
                            }
                            else if (currentCapBank->getPointIdByAttribute(PointAttribute::IgnoredReason) == pointID)
                            {
                                currentCapBank->setIgnoredReason(value);
                                currentSubstationBus->setBusUpdatedFlag(TRUE);
                                currentCapBank->setIgnoreReasonTimeUpdated(timestamp);
                             }

                            if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Set a cbc 2 way Analog point... pointID ("<<pointID<<") = "<<value<< endl;
                            }
                        }
                        else if (twoWayPts->setTwoWayPulseAccumulatorPointValue(pointID, value, timestamp))
                        {
                            if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Set a cbc 2 way Pulse Accumulator point... pointID ("<<pointID<<") = "<<value<< endl;
                            }
                        }


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
        capIter++;
    }
}

/*---------------------------------------------------------------------------
    porterReturn

    Handles porter return messages and updates the status of substation bus
    cap bank controls.
---------------------------------------------------------------------------*/
void CtiCapController::porterReturnMsg( long deviceId, const string& _commandString, int status, const string& _resultString )
{
    string commandString = _commandString;
    if( ciStringEqual(commandString, "scan general") ||
        ciStringEqual(commandString, "scan integrity") )
    {
        return;
    }

    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Porter return received." << endl;
    }

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());


    int bankid = store->findCapBankIDbyCbcID(deviceId);
    if ( bankid == NULL )
        return;
    CtiCCCapBankPtr currentCapBank = store->findCapBankByPAObjectID(bankid);
    if( currentCapBank == NULL )
        return;
    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(currentCapBank->getParentId());
    if( currentFeeder == NULL )
        return;
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
    if (currentSubstationBus == NULL)
        return;

    BOOL found = FALSE;

    if( currentCapBank->getControlDeviceId() == deviceId && deviceId > 0)
    {
        if( currentSubstationBus->getRecentlyControlledFlag() ||
             currentSubstationBus->getVerificationFlag() )
        {

            if( status == 0 )
            {
                std::transform(commandString.begin(), commandString.end(), commandString.begin(), tolower);
                if ( !stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") &&
                     !currentSubstationBus->getVerificationFlag() )
                {
                    if( commandString == "control open" )
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                    }
                    else if( commandString == "control close" )
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                    }
                }
                if (currentCapBank->getControlStatusQuality() == CC_CommFail)
                {
                    currentCapBank->setControlStatusQuality(CC_Normal);
                }
            }
            else
            {
                std::transform(commandString.begin(), commandString.end(), commandString.begin(), tolower);
                if( commandString == "control open" )
                {
                    if (!stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") )
                        currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                }
                else if( commandString == "control close" )
                {
                    if (!stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") )
                        currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
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
                    dout << CtiTime() << " - Porter Return May Indicate a Comm Failure!!  Bus: " << currentSubstationBus->getPaoName() << ", Feeder: " << currentFeeder->getPaoName()<< ", CapBank: " << currentCapBank->getPaoName() << endl;
                }
                if( currentCapBank->getStatusPointId() > 0 )
                {
                    string additional = ("Sub: ");
                    additional += currentSubstationBus->getPaoName();
                    additional += "  Feeder: ";
                    additional += currentFeeder->getPaoName();

                    string text = string("Porter Return May Indicate a Comm Failure!");
                    string text1 = string("Var: Porter Fail Msg, ");
                    currentCapBank->setAfterVarsString(" Cmd rejected ");
                    currentCapBank->setPercentChangeString(" --- ");

                    currentCapBank->setControlStatusQuality(CC_CommFail);
                    string userName = "cap control";
                    if (currentCapBank->getVerificationFlag())
                    {
                        userName += " verification";

                        if (!stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702"))
                        {
                            currentCapBank->setPorterRetFailFlag(TRUE);
                            currentCapBank->setControlRecentlySentFlag(FALSE);

                            if (currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                            }
                            else if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                                     currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending)
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            }
                            currentFeeder->setPorterRetFailFlag(TRUE);
                            currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
                        }

                    }

                    else if (!stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") )
                    {
                        text1 += currentCapBank->getControlStatusText();
                        currentCapBank->setControlRecentlySentFlag(FALSE);

                        sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                        CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, userName, 0, 0, 0, currentCapBank->getIpAddress());
                        eventMsg->setActionId(currentCapBank->getActionId());
                        eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                        getCCEventMsgQueueHandle().write(eventMsg);
                        currentCapBank->setLastStatusChangeTime(CtiTime());
                        currentFeeder->setRetryIndex(0);

                    }
                    sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,userName));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                  << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                }

                if (!stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702") )
                {
                    currentFeeder->setPorterRetFailFlag(TRUE);
                    currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
                }

                currentSubstationBus->setBusUpdatedFlag(TRUE);
            }
        }
    }

}



void CtiCapController::handleRejectionMessaging(CtiCCCapBankPtr currentCapBank, CtiCCFeederPtr currentFeeder,
                                                CtiCCSubstationBusPtr currentSubstationBus, CtiCCTwoWayPoints* twoWayPts)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiMultiMsg* multiCCEventMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = multiCCEventMsg->getData();

    currentCapBank->setIgnoreFlag(TRUE);
    currentSubstationBus->setBusUpdatedFlag(TRUE);

    string text = string("CBC rejected command!");
    string text1 = string("Var:");
    string afterVarsString = string(" CBC rejected ");
    if (!currentFeeder->getRecentlyControlledFlag() &&
        !currentSubstationBus->getRecentlyControlledFlag() &&
        currentCapBank->getControlStatus() == CtiCCCapBank::Open)
    {
        afterVarsString += "OpenPending";
    }
    else if (!currentFeeder->getRecentlyControlledFlag() &&
             !currentSubstationBus->getRecentlyControlledFlag() &&
             currentCapBank->getControlStatus() == CtiCCCapBank::Close)
    {
        afterVarsString += "ClosePending";
    }
    else
    afterVarsString += currentCapBank->getControlStatusText();
    text1 += afterVarsString;
    currentCapBank->setAfterVarsString(afterVarsString);
    currentCapBank->setPercentChangeString(" Rejection by " +currentCapBank->getIgnoreReasonText());

    text1 += " command-";
    text1 += currentCapBank->getIgnoreReasonText();
    if (twoWayPts->getPointValueByAttribute(PointAttribute::IgnoredReason) == 4) //voltage
    {
        if ( ((currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
              currentCapBank->getControlStatus() == CtiCCCapBank::Close) && //if bank was set directly to close, through sendAll command
             (twoWayPts->getPointValueByAttribute(PointAttribute::CbcVoltage) + twoWayPts->getPointValueByAttribute(PointAttribute::DeltaVoltage)) > twoWayPts->getPointValueByAttribute(PointAttribute::OvThreshold)) ||
             ((currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
              currentCapBank->getControlStatus() == CtiCCCapBank::Open) && //if bank was set directly to open, through sendAll command
             (twoWayPts->getPointValueByAttribute(PointAttribute::CbcVoltage) - twoWayPts->getPointValueByAttribute(PointAttribute::DeltaVoltage)) < twoWayPts->getPointValueByAttribute(PointAttribute::UvThreshold)) )
        {
            currentCapBank->setPercentChangeString(" Rejection by Delta Voltage ");
            text1 += " delta";
        }

        else if ( ( (currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
              currentCapBank->getControlStatus() == CtiCCCapBank::Close) && //if bank was set directly to open, through sendAll command
              twoWayPts->getPointValueByAttribute(PointAttribute::CbcVoltage) >= twoWayPts->getPointValueByAttribute(PointAttribute::OvThreshold))  ||
             ( (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
              currentCapBank->getControlStatus() == CtiCCCapBank::Open) && //if bank was set directly to open, through sendAll command
              twoWayPts->getPointValueByAttribute(PointAttribute::CbcVoltage) <= twoWayPts->getPointValueByAttribute(PointAttribute::UvThreshold) ) ||
             twoWayPts->getPointValueByAttribute(PointAttribute::OvCondition) || twoWayPts->getPointValueByAttribute(PointAttribute::UvCondition) )
        {
            currentCapBank->setPercentChangeString(" Rejection by OVUV ");
            text1 += " ovuv";
        }
    }

    text1 += "! Adjusting state, ";
    currentCapBank->setControlStatus(twoWayPts->getPointValueByAttribute(PointAttribute::CapacitorBankState));
    currentCapBank->setReportedCBCState(twoWayPts->getPointValueByAttribute(PointAttribute::CapacitorBankState));
    text1 += currentCapBank->getControlStatusText();
    currentCapBank->setControlStatusQuality(CC_NoControl);

    currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
    string userName = "cap control";
    if (currentCapBank->getVerificationFlag())
    {
        currentCapBank->setPorterRetFailFlag(TRUE);
        userName += " verification";
        currentCapBank->setPercentChangeString(" verify set to CBC state ");
    }

    sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
    LONG stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

    ccEvents.push_back( new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, userName, 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));


    if( currentCapBank->getOperationAnalogPointId() > 0 )
    {
        if (currentCapBank->getTotalOperations() > 0)
            currentCapBank->setTotalOperations( currentCapBank->getTotalOperations() - 1);
        if (currentCapBank->getCurrentDailyOperations() > 0)
            currentCapBank->setCurrentDailyOperations( currentCapBank->getCurrentDailyOperations() - 1);
        getDispatchConnection()->WriteConnQue(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Command Refused, Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
        ccEvents.push_back( new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "Command Refused, opCount adjustment", userName, 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));
    }
    getCCEventMsgQueueHandle().write(multiCCEventMsg);

    currentCapBank->setLastStatusChangeTime(CtiTime());
    currentCapBank->setControlRecentlySentFlag(FALSE);
}
void CtiCapController::handleUnsolicitedMessaging(CtiCCCapBankPtr currentCapBank, CtiCCFeederPtr currentFeeder,
                                                  CtiCCSubstationBusPtr currentSubstationBus, CtiCCTwoWayPoints* twoWayPts)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiMultiMsg* multiCCEventMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = multiCCEventMsg->getData();

    currentSubstationBus->setBusUpdatedFlag(TRUE);
    string text = string("CBC Unsolicited Event!");
    string text1 = string("Var:");
    string beforeVarsString = string(" CBC Unsolicited ");
    beforeVarsString += currentCapBank->getControlStatusText();

    text1 += beforeVarsString;
    text1 += "-"+twoWayPts->getLastControlText();

    currentCapBank->setBeforeVarsString(beforeVarsString);
    currentCapBank->setAfterVarsString(twoWayPts->getLastControlText());
    currentCapBank->setPercentChangeString(" --- ");

    text1 += "! Adjusting CapBank state,  ";
    text1 += currentCapBank->getControlStatusText();

    string opText = currentCapBank->getControlStatusText();
    opText += " sent, CBC Local Change";
    currentCapBank->setControlStatusQuality(CC_UnSolicited);

    sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

    //send the cceventmsg.
    LONG stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    currentCapBank->setActionId( CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
    ccEvents.push_back( new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), opText, "cap control", 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));


    ccEvents.push_back( new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, "cap control", 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));
    if (ccEvents.size() > 0)
    {
        getCCEventMsgQueueHandle().write(multiCCEventMsg);
        multiCCEventMsg = NULL;
    }
    else
    {
        //This should never happen.
        delete multiCCEventMsg;
        multiCCEventMsg = NULL;
    }

    currentCapBank->setUnsolicitedPendingFlag(FALSE);
    currentCapBank->setIgnoreFlag(FALSE);
    //store->removeCapbankFromUnsolicitedCapBankList(currentCapBank);

}



void CtiCapController::handleUnexpectedUnsolicitedMessaging(CtiCCCapBankPtr currentCapBank, CtiCCFeederPtr currentFeeder,
                                                  CtiCCSubstationBusPtr currentSubstationBus, CtiCCTwoWayPoints* twoWayPts)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiMultiMsg* multiCCEventMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = multiCCEventMsg->getData();

    currentSubstationBus->setBusUpdatedFlag(TRUE);
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CBC reporting unexpected state change of: "<<twoWayPts->getPointValueByAttribute(PointAttribute::CapacitorBankState) <<" CAPBANK: "<<
        currentCapBank->getPaoName() << " is "<< currentCapBank->getControlStatusText() <<
        " waiting for VAR change." << endl;
    }

    string text = string("CBC Unsolicited Event!");
    string text1 = string("CBC State Change. ");
    string beforeVarsString = string("CBC Unsolicited-Unexpected ");
    beforeVarsString += (twoWayPts->getPointValueByAttribute(PointAttribute::CapacitorBankState) == 0 ? "Open" : "Close");

    text1 += beforeVarsString;
    text1 += ", "+ twoWayPts->getLastControlText() + "! Mismatch Likely.";

    currentCapBank->setBeforeVarsString(beforeVarsString);
    currentCapBank->setAfterVarsString(twoWayPts->getLastControlText());
    currentCapBank->setPercentChangeString(" --- ");


    //send the cceventmsg.
    LONG stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    ccEvents.push_back( new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlUnexpectedCBCStateReported, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, "cap control", 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));
    if (ccEvents.size() > 0)
    {
        getCCEventMsgQueueHandle().write(multiCCEventMsg);
        multiCCEventMsg = NULL;
    }
    else
    {
        //This should never happen.
        delete multiCCEventMsg;
        multiCCEventMsg = NULL;
    }

    currentCapBank->setUnsolicitedPendingFlag(FALSE);
    currentCapBank->setIgnoreFlag(FALSE);

}



/*---------------------------------------------------------------------------
    signalMessage

    Handles signal messages and updates substation bus tags.
---------------------------------------------------------------------------*/
void CtiCapController::signalMsg(long pointID, unsigned tags, const string& text, const string& additional)
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
    try
    {
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
    try
    {
        if (pilRequest != NULL)
        {
            getPILConnection()->WriteConnQue(pilRequest);
        }

        if (multiMsg != NULL)
        {
            if (multiMsg->getCount() > 0)
            {
                getDispatchConnection()->WriteConnQue(multiMsg);
            }
            else
            {
                delete multiMsg;
            }
        }
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
    try
    {
        if (pilMultiMsg != NULL)
        {
            if (pilMultiMsg->getCount() > 0)
            {
                getPILConnection()->WriteConnQue(pilMultiMsg);
            }
            else
            {
                delete pilMultiMsg;
            }
        }

        if (multiMsg != NULL)
        {
            if( multiMsg->getCount() > 0 )
            {
                getDispatchConnection()->WriteConnQue(multiMsg);
            }
            else
            {
                delete multiMsg;
            }
        }
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

void CtiCapController::sendEventLogMessage(CtiMessage* message)
{
    getCCEventMsgQueueHandle().write(message);
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

