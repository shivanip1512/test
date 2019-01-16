#include "precompiled.h"

#include "dbaccess.h"
#include "connection_client.h"
#include "amq_constants.h"
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
#include "MsgVerifyBanks.h"
#include "pointtypes.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "dllbase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "devicetypes.h"
#include "resolvers.h"
#include "ccoriginalparent.h"
#include "thread_monitor.h"
#include "utility.h"
#include "ctitime.h"
#include "ControlStrategy.h"
#include "ThreadStatusKeeper.h"
#include "ExecutorFactory.h"
#include "dllyukon.h"
#include "ccclientconn.h"
#include "ccclientlistener.h"
#include "millisecond_timer.h"
#include "module_util.h"
#include "GlobalSettings.h"
#include "logManager.h"
#include "mgr_config.h"
#include "mgr_holiday.h"
#include "mgr_paosched.h"
#include "mgr_season.h"
#include "ServiceMetricReporter.h"
#include "MessageCounter.h"
#include "database_reader.h"
#include "CapControlPredicates.h"
#include "desolvers.h"

#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/algorithm/partition.hpp>

extern void refreshGlobalCParms();

namespace Cti
{
namespace CapControl
{
extern void writeRegulatorEventsToDatabase();
}
}


extern unsigned long _CC_DEBUG;
extern unsigned long _LINK_STATUS_TIMEOUT;
extern bool  _ALLOW_PARALLEL_TRUING;
extern bool  _ENABLE_IVVC;
extern bool  _AUTO_VOLT_REDUCTION;
extern long  _VOLT_REDUCTION_SYSTEM_POINTID;
extern unsigned long _POST_CONTROL_WAIT;
extern unsigned long _SCAN_WAIT_EXPIRE;

using Cti::ThreadStatusKeeper;
using std::endl;
using std::string;
using Cti::CapControl::EventLogEntry;
using Cti::CapControl::EventLogEntries;

//DLLEXPORT bool  bGCtrlC = false;

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
CtiCapController::CtiCapController()
{
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
        _instance = NULL;
    }
}

/*---------------------------------------------------------------------------
    start

    Starts the controller thread
---------------------------------------------------------------------------*/
void CtiCapController::start()
{
    CTILOG_DEBUG(dout, "Starting CtiCapController");

    _substationBusThread            = boost::thread( &CtiCapController::controlLoop, this );
    _outClientMsgThread             = boost::thread( &CtiCapController::outClientMsgs, this );
    _messageSenderThread            = boost::thread( &CtiCapController::messageSender, this );
    _incomingMessageProcessorThread = boost::thread( &CtiCapController::incomingMessageProcessor, this );

    CTILOG_DEBUG(dout, "CtiCapController is running");
}

/*---------------------------------------------------------------------------
    stop

    Stops the controller thread
---------------------------------------------------------------------------*/
void CtiCapController::stop()
{
    try
    {
        CTILOG_DEBUG(dout, "Stopping CtiCapController");

        _substationBusThread.interrupt();
        _outClientMsgThread.interrupt();
        _messageSenderThread.interrupt();
        _incomingMessageProcessorThread.interrupt();

        if ( ! _substationBusThread.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "CtiCapController substation bus thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _substationBusThread.native_handle(), EXIT_SUCCESS );
        }

        if ( ! _outClientMsgThread.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "CtiCapController OutClientMsg thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _outClientMsgThread.native_handle(), EXIT_SUCCESS );
        }

        if ( ! _messageSenderThread.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "CtiCapController message sender thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _messageSenderThread.native_handle(), EXIT_SUCCESS );
        }

        if ( ! _incomingMessageProcessorThread.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "CtiCapController incoming message thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _incomingMessageProcessorThread.native_handle(), EXIT_SUCCESS );
        }

        CTILOG_DEBUG(dout, "CtiCapController is stopped");
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        if ( _dispatchConnection )
        {
            if( _dispatchConnection->valid() )
            {
                _dispatchConnection->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15 ), CALLSITE );
            }
            _dispatchConnection->close();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        if( _porterConnection )
        {
            if( _porterConnection->valid() )
            {
                _porterConnection->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15 ), CALLSITE );
            }
            _porterConnection->close();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCapController::messageSender()
{
    CTILOG_DEBUG(dout, "CtiCapController message sender thread is starting");

    ThreadStatusKeeper threadStatus("CapControl messageSender");

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    try
    {
        while(true)
        {
            CtiTime currentDateTime;
            {
                CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                {
                    CTILOG_DEBUG(dout, "Message Sender start");
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
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                if (store->getLinkStatusPointId() > 0 &&
                     (store->getLinkStatusFlag() == STATE_CLOSED) &&
                     store->getLinkDropOutTime().seconds() + (60* _LINK_STATUS_TIMEOUT) < currentDateTime.seconds())
                {
                     updateAllPointQualities(NonUpdatedQuality);
                     store->setLinkDropOutTime(currentDateTime);

                     CTILOG_WARN(dout, "store->getLinkDropOutTime() " << store->getLinkDropOutTime());
                }

                readClientMsgQueue();
                CtiCCSubstationBus_vec subStationBusChanges;
                CtiCCSubstation_set stationChanges;
                CtiCCArea_set areaChanges;

                PaoIdToSubBusMap::iterator busIter = store->getPAOSubMap()->begin();
                for ( ; busIter != store->getPAOSubMap()->end() ; busIter++)
                {
                    CtiCCSubstationBusPtr currentSubstationBus = busIter->second;
                    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
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
                                store->updateSubstationObjectSet(currentStation->getPaoId(), stationChanges);
                                currentStation->setStationUpdatedFlag(false);
                            }
                            subStationBusChanges.push_back(currentSubstationBus);
                            currentSubstationBus->setBusUpdatedFlag(false);
                        }
                    }
                }

                if (subStationBusChanges.size() > 0)
                {
                    getOutClientMsgQueueHandle().write(new CtiCCSubstationBusMsg( subStationBusChanges, CtiCCSubstationBusMsg::SubBusModified ));
                }

                for ( CtiCCAreaPtr currentArea : *store->getCCGeoAreas() )
                {
                    if (currentArea->getAreaUpdatedFlag())
                    {
                        store->updateAreaObjectSet(currentArea->getPaoId(), areaChanges);
                        currentArea->setAreaUpdatedFlag(false);
                    }
                }
                if (areaChanges.size() > 0)
                {
                    getOutClientMsgQueueHandle().write(new CtiCCGeoAreasMsg( areaChanges, CtiCCGeoAreasMsg::AreaModified ));
                }
                if (stationChanges.size() > 0)
                {
                    getOutClientMsgQueueHandle().write(new CtiCCSubstationsMsg( stationChanges, CtiCCSubstationsMsg::SubModified ));
                }
                if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                {
                    CTILOG_DEBUG(dout, "Message Sender End");
                }
            }

            boost::this_thread::sleep( boost::posix_time::milliseconds( 500 ) );

            threadStatus.monitorCheck();
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "CtiCapController message sender thread is stopping");
}

void CtiCapController::incomingMessageProcessor()
{
    Cti::MessageCounter mc("CapControl Incoming Message");

    CTILOG_DEBUG(dout, "CtiCapController incoming message thread is starting");

    ThreadStatusKeeper threadKeeper("CapControl Incoming Message Thread");

    try
    {
        while (true) 
        {
            CtiMessage* msg = NULL;
            bool retVal = _incomingMessageQueue.read(msg,5000);

            if (retVal)
            {
                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                {
                    int msgCount = 1;
                    if (msg->isA() == MSG_MULTI)
                    {
                        msgCount = ((CtiMultiMsg*)msg)->getCount();
                    }
                    CTILOG_TRACE(dout, "Processing "<< msgCount <<" New Message(s).");
                }

                mc.increment();

                parseMessage(msg);
                delete msg;
            }

            threadKeeper.monitorCheck();
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "CtiCapController incoming message thread is stopping");
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
    CTILOG_DEBUG(dout, "CtiCapController control loop thread is starting");

    try
    {
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

        store->verifySubBusAndFeedersStates();

        CtiPAOScheduleManager* scheduleMgr = CtiPAOScheduleManager::getInstance();
        scheduleMgr->start();

        CtiCCSubstation_set stationChanges;
        CtiCCArea_set areaChanges;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
        long lastThreadPulse = 0;
        CtiDate lastDailyResetDate;     // == CtiDate::now()

        ThreadStatusKeeper threadStatus("CapControl controlLoop");

        CtiTime fifteenMinCheck = nextScheduledTimeAlignedOnRate( CtiTime(),  900);

        long pointID = ThreadMonitor.getProcessPointID();
        Cti::ServiceMetrics::MetricReporter metricReporter {
            Cti::ServiceMetrics::CpuPointOffsets::CapControl,
            Cti::ServiceMetrics::MemoryPointOffsets::CapControl,
            "CapControl",
            CAPCONTROL_APPLICATION_NAME };

        CtiTime NextThreadMonitorReportTime;
        CtiTime nextCPULoadReportTime;

        CtiThreadMonitor::State previous;

        CtiTime peakTimeCheck = nextScheduledTimeAlignedOnRate( CtiTime(), 60 ) - 60;   // slightly in the past
                

        while(true)
        {
            {
                const CtiTime Now;
                unsigned long secondsFrom1970 = Now.seconds();

                dout->poke();  //  called around 2x/second (see boost::this_thread::sleep at bottom of loop)

                CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
                CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
                CtiMultiMsg_vec& capMessages = multiCapMsg->getData();
                EventLogEntries ccEvents;

                try
                {
                    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                    if( Now > fifteenMinCheck && secondsFrom1970 != lastThreadPulse)
                    {//every  fifteen minutes tell the user if the control thread is still alive
                        CTILOG_INFO(dout, "Controller refreshing CPARMS");
                        refreshGlobalCParms();
                        CTILOG_INFO(dout, "Controller thread pulse");
                        lastThreadPulse = secondsFrom1970;
                        fifteenMinCheck = nextScheduledTimeAlignedOnRate( Now,  900);
                        store->verifySubBusAndFeedersStates();
                    }

                    if ( Now.date() != lastDailyResetDate )
                    {
                        store->resetDailyOperations();

                        lastDailyResetDate = Now;
                    }

                    //  Any DBChanges or resets to process?
                    store->processAnyDBChangesOrResets( Now );

                    //  Process the dispatch point registration
                    _registry.updateRegistry( getDispatchConnection() );
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                boost::this_thread::interruption_point();

                try
                {
                    if ( Now >= peakTimeCheck )
                    {
                        CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                        peakTimeCheck += 60;    // check every minute

                        if ( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            CTILOG_INFO( dout, "Controller refreshing PEAK times" );
                        }

                        for ( CtiCCSubstationBusPtr bus : *store->getCCSubstationBuses() )
                        {
                            const bool busPeakTime        = bus->isPeakTime( Now ),
                                       isIndividualFeeder = bus->getStrategy()->getMethodType()
                                                                == ControlStrategy::IndividualFeeder;

                            for ( CtiCCFeederPtr feeder : bus->getCCFeeders() )
                            {
                                if ( isIndividualFeeder )
                                {
                                    feeder->isPeakTime( Now );
                                }
                                else
                                {
                                    feeder->setPeakTimeFlag( busPeakTime );
                                }
                            }
                        }
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
                try
                {
                    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                    {
                        CTILOG_DEBUG(dout, "Control Loop Start");
                    }
                    areaChanges.clear();
                    stationChanges.clear();
                    PaoIdToSubBusMap::iterator busIter = store->getPAOSubMap()->begin();
                    for ( ;busIter != store->getPAOSubMap()->end(); busIter++)
                    {
                        CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                        if (store->getStoreRecentlyReset())
                            break;

                        CtiCCSubstationBusPtr currentSubstationBus = busIter->second;

                        //Bypass IVVC subbuses.  IVVC subbuses are analyzed in the IVVC Algorithm.
                        if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar )
                        {
                            continue;
                        }

                        CtiCCAreaPtr currentArea = NULL;

                        CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                        if (currentStation != NULL )
                        {
                            currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                        }

                        try
                        {
                            if (currentArea != NULL )
                            {
                                currentSubstationBus->performDataOldAndFallBackNecessaryCheck();

                                if (currentSubstationBus->isMultiVoltBusAnalysisNeeded(Now))
                                {
                                    if ( currentArea->getDisableFlag() )
                                    {
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_INFO( dout, "MULTIVOLT: Skipping analysis for DISABLED Area: "
                                                                        << currentArea->getPaoName() );
                                        }
                                    }
                                    else if ( currentStation->getDisableFlag() )
                                    {
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_INFO( dout, "MULTIVOLT: Skipping analysis for DISABLED Substation: "
                                                                        << currentStation->getPaoName() );
                                        }
                                    }
                                    else if ( currentSubstationBus->getDisableFlag() )
                                    {
                                        if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                        {
                                            CTILOG_INFO( dout, "MULTIVOLT: Skipping analysis for DISABLED SubBus: "
                                                                        << currentSubstationBus->getPaoName() );
                                        }
                                    }
                                    else if (currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder)
                                    {
                                        currentSubstationBus->analyzeMultiVoltBus1(Now, pointChanges, ccEvents, pilMessages);
                                    }
                                    else
                                    {
                                        currentSubstationBus->analyzeMultiVoltBus(Now, pointChanges, ccEvents, pilMessages);
                                    }
                                }
                                else if (currentSubstationBus->isBusAnalysisNeeded(Now))
                                {
                                    if (currentSubstationBus->getRecentlyControlledFlag() || currentSubstationBus->getWaitToFinishRegularControlFlag())
                                    {
                                        try
                                        {
                                            if (currentSubstationBus->isAlreadyControlled() ||
                                                currentSubstationBus->isPastMaxConfirmTime(Now))
                                            {
                                                if ((currentSubstationBus->getStrategy()->getControlSendRetries() > 0 ||
                                                     currentSubstationBus->getLastFeederControlledSendRetries() > 0) &&
                                                    !currentSubstationBus->isAlreadyControlled() &&
                                                     currentSubstationBus->checkForAndPerformSendRetry(Now, pointChanges, ccEvents, pilMessages))
                                                {
                                                    currentSubstationBus->setBusUpdatedFlag(true);
                                                }
                                                else
                                                {
                                                    currentSubstationBus->capBankControlStatusUpdate(pointChanges, ccEvents);
                                                    currentSubstationBus->setBusUpdatedFlag(true);
                                                }
                                            }
                                            else if (currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder)
                                            {
                                                checkBusForNeededControl(currentArea,currentStation, currentSubstationBus, Now,pointChanges, ccEvents, pilMessages);
                                            }
                                        }
                                        catch(...)
                                        {
                                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                                        }
                                    }
                                    else if (currentSubstationBus->getVerificationFlag()) //verification Flag set!!!
                                    {
                                        analyzeVerificationBus(currentSubstationBus, Now, pointChanges, ccEvents, pilMessages, capMessages);
                                    }
                                    else if (currentSubstationBus->isVarCheckNeeded(Now))
                                    {//not recently controlled and var check needed
                                        checkBusForNeededControl(currentArea,currentStation, currentSubstationBus, Now,pointChanges, ccEvents, pilMessages);
                                    }

                                    try
                                    {
                                        //so we don't do this over and over we need to clear out
                                        currentSubstationBus->clearOutNewPointReceivedFlags();
                                        if( currentSubstationBus->isVarCheckNeeded(Now) &&
                                            currentSubstationBus->getStrategy()->getControlInterval() > 0 )
                                        {
                                            currentSubstationBus->figureNextCheckTime();
                                        }
                                    }
                                    catch(...)
                                    {
                                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                                            store->updateSubstationObjectSet(currentStation->getPaoId(), stationChanges);
                                            currentStation->setStationUpdatedFlag(false);
                                        }
                                        if (currentArea->getAreaUpdatedFlag())
                                        {
                                            store->updateAreaObjectSet(currentArea->getPaoId(), areaChanges);
                                            currentArea->setAreaUpdatedFlag(false);
                                        }

                                        getOutClientMsgQueueHandle().write(new CtiCCSubstationBusMsg(currentSubstationBus));
                                        currentSubstationBus->setBusUpdatedFlag(false);
                                    }
                                }
                                catch(...)
                                {
                                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                                }
                            }
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }

                        boost::this_thread::interruption_point();

                    }
                    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                    {
                        CTILOG_DEBUG(dout, "Control Loop End");
                    }
                }
                catch ( boost::thread_interrupted & )
                {
                    throw;
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                    if (areaChanges.size() > 0 &&  !store->getStoreRecentlyReset())
                    {
                        getOutClientMsgQueueHandle().write(new CtiCCGeoAreasMsg( areaChanges, CtiCCGeoAreasMsg::AreaModified ));
                        areaChanges.clear();
                    }
                    if (stationChanges.size() > 0 &&  !store->getStoreRecentlyReset())
                    {
                        getOutClientMsgQueueHandle().write(new CtiCCSubstationsMsg( stationChanges, CtiCCSubstationsMsg::SubModified ));
                        stationChanges.clear();
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
                try
                {
                    //send point changes to dispatch
                    if( pointChanges.size() > 0 )
                    {
                        multiDispatchMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
                        getDispatchConnection()->WriteConnQue( multiDispatchMsg, CALLSITE );
                        multiDispatchMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    //send pil commands to porter
                    if( multiPilMsg->getCount() > 0 )
                    {
                        multiPilMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            CTILOG_DEBUG(dout, "PIL MESSAGES " << multiPilMsg);
                        }
                        getPorterConnection()->WriteConnQue( multiPilMsg, CALLSITE );
                        multiPilMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
                try
                {
                    //execute cap commands
                    if( multiCapMsg->getCount() > 0 )
                    {
                        multiCapMsg->resetTime();
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            CTILOG_DEBUG(dout, "CAP CREATED MESSAGES " << multiCapMsg);
                        }
                        CtiMultiMsg_vec& temp = multiCapMsg->getData( );
                        for(int i=0;i<temp.size( );i++)
                        {
                            CtiMessage* msgCopy = ((CtiMessage *)temp[i])->replicateMessage(); //copy deleted in executor.
                            CtiCCExecutorFactory::createExecutor(msgCopy)->execute();
                        }
                        delete multiCapMsg; //deletes all containing messages.
                        multiCapMsg = new CtiMultiMsg();
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
                try
                {
                    //send ccEvents to EventLOG!
                    enqueueEventLogEntries(ccEvents);

                    writeEventLogsToDatabase();

                    Cti::CapControl::writeRegulatorEventsToDatabase();
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }

            boost::this_thread::sleep( boost::posix_time::milliseconds( 500 ) );

            try
            {
                if (_ENABLE_IVVC == true)
                {
                    store->executeAllStrategies();
                }
            }
            catch (...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Exception while executing strategies");
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
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Exception while updating Voltage Regulators.");
            }

            // This is the main spot for sending the CBC heartbeats out for banks that are not
            //  under IVVC control.  If all the hierarchy is enabled then we send the heartbeat message 
            //  for the banks.  If any part of the hierarchy is disabled or orphaned then we stop the heartbeat.
            {
                CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                using boost::adaptors::filtered;
                using namespace Cti::CapControl;

                const auto stopHeartbeat = []( auto bank ) { bank->executeStopHeartbeat( SystemUser ); };
                const auto sendHeartbeat = []( auto bank ) { bank->executeSendHeartbeat( SystemUser ); };

                const auto nonIvvcBuses = *store->getCCSubstationBuses() | filtered( notIvvcStrategy );

                for ( auto bus : nonIvvcBuses )
                {
                    long stationID, areaID, spAreaID;

                    store->getSubBusParentInfo( bus, spAreaID, areaID, stationID );

                    bool hierarchyEnabled = false;

                    if ( isEnabled( bus ) && existsAndEnabled( store->findSubstationByPAObjectID( stationID ) ) )
                    {
                        if ( areaID > 0 )
                        {
                            hierarchyEnabled |= existsAndEnabled( store->findAreaByPAObjectID( areaID ) );
                        }
                        if ( spAreaID > 0 )
                        {
                            hierarchyEnabled |= existsAndEnabled( store->findSpecialAreaByPAObjectID( spAreaID ) );
                        }
                    }

                    const auto nonIvvcFeeders = bus->getCCFeeders() | filtered( notIvvcStrategy );

                    for ( auto feeder : nonIvvcFeeders )
                    {
                        if ( hierarchyEnabled && isEnabled( feeder ) && ( hasStrategy( bus ) || hasStrategy( feeder ) ) )
                        {
                            auto banks = feeder->getAllCapBanks();
                            
                            auto enabled_end = std::partition( banks.begin(), banks.end(), isEnabled );

                            std::for_each( banks.begin(), enabled_end, sendHeartbeat );
                            std::for_each( enabled_end,   banks.end(), stopHeartbeat );
                        }
                        else  
                        {
                            auto enabledBanks = feeder->getCCCapBanks() | filtered( isEnabled );

                            boost::for_each( enabledBanks, stopHeartbeat );
                        }
                    }
                }
            }

            threadStatus.monitorCheck();

            if(pointID != 0)
            {
                CtiThreadMonitor::State next;
                if((next = ThreadMonitor.getState()) != previous ||
                    CtiTime::now() > NextThreadMonitorReportTime)
                {
                    // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                    previous = next;
                    NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate(CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2);

                    getDispatchConnection()->WriteConnQue(
                        CTIDBG_new CtiPointDataMsg( pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType,
                        ThreadMonitor.getString().c_str() ), CALLSITE );
                }
            }

            metricReporter.reportCheck(CompileInfo, *getDispatchConnection());
        }
    }
    catch ( boost::thread_interrupted & )
    {
        try
        {
            CtiPAOScheduleManager* scheduleMgr = CtiPAOScheduleManager::getInstance();
            scheduleMgr->stop();
        }
        catch (...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Control Loop thread terminated unexpectedly.");
    }

    CTILOG_DEBUG(dout, "CtiCapController control loop thread is stopping");
}

void CtiCapController::checkBusForNeededControl(CtiCCAreaPtr currentArea,  CtiCCSubstationPtr currentStation, CtiCCSubstationBusPtr currentSubstationBus, const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCapController::readClientMsgQueue()
{
    CtiTime tempTime;
    std::unique_ptr<CtiMessage> clientMsg;

    tempTime.now();

    while( ! _inClientMsgQueue.isEmpty() )
    {
        clientMsg.reset( _inClientMsgQueue.getQueue() );

        if( clientMsg.get() )
        {
            try
            {
                CtiCCExecutorFactory::createExecutor( clientMsg.release() )->execute(); // executor takes ownership of the message
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_INFO(dout, "substationBusChanges: "<<size);

            try
            {
                CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(substationBusChanges, broadCastMask))->execute();
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                 CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
             }
         }

      }
      catch(...)
      {
          CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
      }
}

void CtiCapController::analyzeVerificationBus(CtiCCSubstationBusPtr currentSubstationBus, const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages,
                            CtiMultiMsg_vec& capMessages)
{
    // shim out IVVC bus verification to its own function
    if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar )
    {
        analyzeVerificationBusIvvc(currentSubstationBus, currentDateTime,pointChanges, ccEvents, pilMessages, capMessages );

        return;
    }

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
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                            currentSubstationBus->setBusUpdatedFlag(true);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                            currentSubstationBus->setWaitForReCloseDelayFlag(false);
                        }
                        else
                            currentSubstationBus->setWaitForReCloseDelayFlag(true);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                                    CTILOG_DEBUG(dout, "CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPaoName()<< "( "<<currentSubstationBus->getPaoId()<<" ) CB-"<<currentSubstationBus->getCurrentVerificationCapBankId());
                            }

                            currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                        }
                        else
                        {

                            //reset VerificationFlag
                            currentSubstationBus->setVerificationFlag(false);
                            currentSubstationBus->setBusUpdatedFlag(true);
                            capMessages.push_back(new VerifyBanks(currentSubstationBus->getPaoId(),currentSubstationBus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
                            capMessages.push_back(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()));
                            if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                            {
                               CTILOG_DEBUG(dout, "DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName()<< "( "<<currentSubstationBus->getPaoId()<<" ) ");
                            }
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                   CTILOG_DEBUG(dout, "Performing VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName());
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
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                else if(currentSubstationBus->areThereMoreCapBanksToVerify(ccEvents))
                {
                    try
                    {
                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                        {
                             CTILOG_DEBUG(dout, "CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPaoName()<<" CB-"<<currentSubstationBus->getCurrentVerificationCapBankId());
                        }
                        currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                        currentSubstationBus->setBusUpdatedFlag(true);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }

                }
                else
                {
                    try
                    {
                        //reset VerificationFlag
                        currentSubstationBus->setVerificationFlag(false);
                        currentSubstationBus->setBusUpdatedFlag(true);
                        capMessages.push_back(new VerifyBanks(currentSubstationBus->getPaoId(),currentSubstationBus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
                        capMessages.push_back(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()));

                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                        {
                           CTILOG_DEBUG(dout, "DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName());
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiCapController::outClientMsgs()
{
    CTILOG_DEBUG(dout, "CtiCapController outClient message thread is starting");

    try
    {
        ThreadStatusKeeper threadStatus("CapControl outClientMsgs");

        while (true)
        {
            CtiMessage* msg = NULL;
            bool retVal = getOutClientMsgQueueHandle().read(msg,5000);

            if (retVal)
            {
                CtiCCClientListener::getInstance().BroadcastMessage( msg );
                delete msg;
            }

            threadStatus.monitorCheck();
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Out Client Messages thread terminated unexpectedly.");
    }

    CTILOG_DEBUG(dout, "CtiCapController outClient message thread is stopping");
}

void CtiCapController::writeEventLogsToDatabase()
{
    Cti::Timing::MillisecondTimer timer;

    int entries = 0;

    while( entries < 100 && ! _eventLogs.empty() )
    {
        ++entries;
        CtiCCSubstationBusStore::InsertCCEventLogInDB(_eventLogs.getQueue());
    }

    if (_CC_DEBUG & CC_DEBUG_CCEVENTINSERT)
    {
        CTILOG_DEBUG(dout, "Processed " << entries << " CC Event Log entries in " << timer.elapsed() / 1000 << " seconds.");
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
        {
            ReaderGuard guard( _dispatchConnectionLock );

            if( _dispatchConnection && _dispatchConnection->isConnectionUsable() )
            {
                return _dispatchConnection; // use the current connection if its valid
            }
        }

        {
            WriterGuard guard( _dispatchConnectionLock );

            // the connection state might have change, lets re-check it
            if( _dispatchConnection && _dispatchConnection->isConnectionUsable() )
            {
                return _dispatchConnection;
            }

            if( _dispatchConnection )
            {
                CTILOG_WARN(dout, "Dispatch Connection Hiccup");
            }

            _dispatchConnection.reset( new CapControlDispatchConnection( "CC to Dispatch" ));
            _dispatchConnection->addMessageListener(this);
            _dispatchConnection->start();

            // send a registration message to Dispatch
            auto msg = std::make_unique<CtiRegistrationMsg>( "CapController", 0, false );
            msg->setAppExpirationDelay( 900 );
            _dispatchConnection->WriteConnQue( msg.release(), CALLSITE );

            return _dispatchConnection;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        return DispatchConnectionPtr();
    }
}

/*---------------------------------------------------------------------------
    getPorterConnection

    Returns a connection to Porter, initializes if isn't created yet.
---------------------------------------------------------------------------*/
boost::shared_ptr<CtiClientConnection> CtiCapController::getPorterConnection()
{
    try
    {
        {
            ReaderGuard guard( _porterConnectionLock );

            if( _porterConnection && _porterConnection->isConnectionUsable() )
            {
                return _porterConnection; // use the current connection if its valid
            }
        }

        {
            WriterGuard guard( _porterConnectionLock );

            // the connection state might have change, lets re-check it
            if( _porterConnection && _porterConnection->isConnectionUsable() )
            {
                return _porterConnection;
            }

            if( _porterConnection )
            {
                CTILOG_WARN(dout, "Porter Connection Hiccup");
            }

            _porterConnection.reset( new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::porter ));
            _porterConnection->setName("CC to Porter");
            _porterConnection->start();

            // send a registration message to Porter
            auto msg = std::make_unique<CtiRegistrationMsg>( "CapController", 0, false );
            msg->setAppExpirationDelay( 900 );
            _porterConnection->WriteConnQue(msg.release(), CALLSITE);

            return _porterConnection;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        return boost::shared_ptr<CtiClientConnection>();
    }
}

/*---------------------------------------------------------------------------
    checkPIL

    Reads off the PIL connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::checkPIL()
{
    Cti::MessageCounter mc("PIL->CapControl");

    bool done = false;

    do
    {
        try
        {
            CtiMessage* inMsg = getPorterConnection()->ReadConnQue(0);

            if ( inMsg != NULL )
            {
                mc.increment();

                parseMessage(inMsg);
                delete inMsg;
            }
            else
                done = true;
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            done = true;
        }
    }
    while(!done);
}


/*---------------------------------------------------------------------------
    updateAllPointQualities

---------------------------------------------------------------------------*/
void CtiCapController::updateAllPointQualities(long quality)
{
    CTILOG_INFO(dout, "Updating CapControl Point Qualities to " << quality);

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    for(long i=0;i<ccSubstationBuses.size();i++)
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

        for(long j=0;j<ccFeeders.size();j++)
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
        currentSubstationBus->setNewPointDataReceivedFlag(true);
    }
}

void CtiCapController::handleConfigDbChange( CtiDBChangeMsg * dbChange )
{
    Cti::ConfigManager::handleDbChange( dbChange->getId(),
                                        dbChange->getCategory(),
                                        dbChange->getObjectType(),
                                        dbChange->getTypeOfChange() );
/*
    We have 3 different object types that are possible when reloading a device config:
        1. category
        2. config
        3. device
 
    In all instances we need to get a collection of pao IDs and types that we need to reload.  The only things
        in CapControl that currently uses a device config are regulators, DNP CBCs and Logical CBCs.  We filter out
        all the other device types.
*/

    static const std::string configurableDevTypes =
        "'LTC', "
        "'GO_REGULATOR', "
        "'PO_REGULATOR', "
        "'CBC 7020', "
        "'CBC 7022', "
        "'CBC 7023', "
        "'CBC 7024', "
        "'CBC 8020', "
        "'CBC 8024', "
        "'CBC DNP', "
        "'CBC Logical'";

    static const std::set<DeviceTypes>  resolvedRegulatorTypes
    {
        TYPE_LOAD_TAP_CHANGER,
        TYPE_GANG_OPERATED_REGULATOR,
        TYPE_PHASE_OPERATED_REGULATOR
    };

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr( connection );

    if ( dbChange->getObjectType() == "config" )
    {
        // Find all paos assigned to this particular device config.

        rdr.setCommandText
        (
            "SELECT "
                "Y.PAObjectID, "
                "Y.Type "
            "FROM "
                "DeviceConfigurationDeviceMap M "
                    "JOIN YukonPAObject Y "
                        "ON M.DeviceID = Y.PAObjectID "
            "WHERE "
                "Y.Type IN (" + configurableDevTypes + ") "
                    "AND "
                        "M.DeviceConfigurationId = ?"
        );
    }
    else if ( dbChange->getObjectType() == "category" )
    {
        // Find all paos that are assigned a config that contains this particular category.

        rdr.setCommandText
        (
            "SELECT "
                "Y.PAObjectID, "
                "Y.Type "
            "FROM "
                "DeviceConfigCategoryMap C "
                    "JOIN DeviceConfigurationDeviceMap M "
                        "ON C.DeviceConfigurationId = M.DeviceConfigurationId "
                    "JOIN YukonPAObject Y "
                        "ON M.DeviceID = Y.PAObjectID "
            "WHERE "
                "Y.Type IN (" + configurableDevTypes + ") "
                    "AND "
                        "C.DeviceConfigCategoryId = ?"
        );
    }
    else    // dbChange->getObjectType() == "device"
    {
        // Reload this particular device because it has been assigned a different config.

        rdr.setCommandText
        (
            "SELECT "
                "Y.PAObjectID, "
                "Y.Type "
            "FROM "
                "YukonPAObject Y "
            "WHERE "
                "Y.Type IN (" + configurableDevTypes + ") "
                    "AND "
                        "Y.PAObjectID = ?"
        );
    }
    
    rdr << dbChange->getId();

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO( dout, rdr.asString() );
    }

    while ( rdr() )
    {
        long        paoID;
        std::string paoType;

        rdr[ "PAObjectID" ] >> paoID;
        rdr[ "Type" ]       >> paoType;

        long reloadID = paoID;
        Cti::CapControl::CapControlType reloadType = Cti::CapControl::VoltageRegulatorType;

        if ( ! resolvedRegulatorTypes.count( resolveDeviceType( paoType ) ) )    // its not a regulator type, so CBC
        {
            reloadID = CtiCCSubstationBusStore::getInstance()->findCapBankIDbyCbcID( paoID ); 
            reloadType = Cti::CapControl::CapBank;
        }

        if ( reloadID > 0 )
        {
            CcDbReloadInfo reloadInfo( reloadID, ChangeTypeUpdate, reloadType );
            CtiCCSubstationBusStore::getInstance()->insertDBReloadList( reloadInfo );
        }
    }
}

/*---------------------------------------------------------------------------
    isCbcDbChange

    Determines if a DB change is for a CBC device type.
---------------------------------------------------------------------------*/
bool CtiCapController::isCbcDbChange(const CtiDBChangeMsg *dbChange)
{
    if( resolvePAOCategory(dbChange->getCategory()) != PAO_CATEGORY_DEVICE )
    {
        return false;
    }
    else
    {
        INT DeviceType = resolveDeviceType(dbChange->getObjectType());
        return( DeviceType == TYPE_VERSACOMCBC || DeviceType == TYPE_EXPRESSCOMCBC ||
                DeviceType == TYPE_CBC7010 || DeviceType == TYPE_CBC7020 ||
                DeviceType == TYPE_CBC8020 || DeviceType == TYPE_CBCDNP || DeviceType == TYPE_CBCLOGICAL ||
                DeviceType == TYPE_FISHERPCBC );
    }
}

CcDbReloadInfo CtiCapController::resolveCapControlType(CtiDBChangeMsg *dbChange)
{

    CcDbReloadInfo reloadInfo(dbChange->getId(), dbChange->getTypeOfChange(), Cti::CapControl::Undefined);
    int paoType = resolvePAOType(dbChange->getCategory(),dbChange->getObjectType());

    switch (paoType)
    {
        case TYPE_CC_SPECIALAREA:
            {
                reloadInfo.objecttype = Cti::CapControl::SpecialArea;

                CtiPAOScheduleManager::getInstance()->setValid(false);
                break;
            }
        case TYPE_CC_AREA:
            {
                reloadInfo.objecttype = Cti::CapControl::Area;

                CtiPAOScheduleManager::getInstance()->setValid(false);
                break;
            }
        case TYPE_CC_SUBSTATION:
            {
                reloadInfo.objecttype = Cti::CapControl::Substation;

                CtiPAOScheduleManager::getInstance()->setValid(false);
                break;
            }
        case TYPE_CC_SUBSTATION_BUS:
            {
                reloadInfo.objecttype = Cti::CapControl::SubBus;

                CtiPAOScheduleManager::getInstance()->setValid(false);
                break;
            }
        case TYPE_CC_FEEDER:
            {
                reloadInfo.objecttype = Cti::CapControl::Feeder;
                break;
            }
        case TYPE_LOAD_TAP_CHANGER:
        case TYPE_GANG_OPERATED_REGULATOR:
        case TYPE_PHASE_OPERATED_REGULATOR:
            {
                reloadInfo.objecttype = Cti::CapControl::VoltageRegulatorType;

                CtiPAOScheduleManager::getInstance()->setValid(false);
                break;
            }
        case TYPE_VIRTUAL_SYSTEM:
            break;
        default:
            {
                reloadInfo = resolveCapControlTypeByDataBase(dbChange);
                break;
            }
    }


    if (reloadInfo.objecttype == Cti::CapControl::Undefined)
    {
        CtiCCSubstationBusStore::getInstance()->setValid(false);
        CtiPAOScheduleManager::getInstance()->setValid(false);
    }
    return reloadInfo;
}

CcDbReloadInfo CtiCapController::resolveCapControlTypeByDataBase(CtiDBChangeMsg *dbChange)
{
    CcDbReloadInfo reloadInfo(dbChange->getId(), dbChange->getTypeOfChange(), Cti::CapControl::Undefined);
    if (dbChange->getDatabase() == ChangeCBCStrategyDb)
    {
        reloadInfo.objecttype = Cti::CapControl::Strategy;
    }
    else if (dbChange->getDatabase() == ChangePAODb && ciStringEqual(dbChange->getObjectType(),"cap bank"))
    {
        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
             CTILOG_DEBUG(dout, "capBank DB change message received for Cap: "<<dbChange->getId());
        }
        reloadInfo.objecttype = Cti::CapControl::CapBank;
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
            reloadInfo = CcDbReloadInfo(sub->getPaoId(), ChangeTypeUpdate, Cti::CapControl::SubBus);
        }
        else if (CtiCCSubstationBusStore::getInstance()->findFeederByPointID(dbChange->getId(), feederBegin, feederEnd))
        {
            CtiCCFeederPtr feed = feederBegin->second;
            reloadInfo = CcDbReloadInfo(feed->getPaoId(), ChangeTypeUpdate, Cti::CapControl::Feeder);
        }
        else if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(dbChange->getId(), capBankBegin, capBankEnd))
        {
            CtiCCCapBankPtr cap = capBankBegin->second;
            reloadInfo = CcDbReloadInfo(cap->getPaoId(), ChangeTypeUpdate, Cti::CapControl::CapBank);
        }
        else if (CtiCCSubstationBusStore::getInstance()->findAreaByPointID(dbChange->getId(), areaBegin, areaEnd))
        {
            CtiCCAreaPtr area = areaBegin->second;
            reloadInfo = CcDbReloadInfo(area->getPaoId(), ChangeTypeUpdate, Cti::CapControl::Area);
        }
        else if (CtiCCSubstationBusStore::getInstance()->findSpecialAreaByPointID(dbChange->getId(), sAreaBegin, sAreaEnd))
        {
            CtiCCSpecialPtr specialArea = sAreaBegin->second;
            reloadInfo = CcDbReloadInfo(specialArea->getPaoId(), ChangeTypeUpdate, Cti::CapControl::SpecialArea);

        }
    }
    else if ( isCbcDbChange(dbChange) )
    {
        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
             CTILOG_DEBUG(dout, "CBC DB change message received: "<<dbChange->getId());
        }
        long capBankId = CtiCCSubstationBusStore::getInstance()->findCapBankIDbyCbcID(dbChange->getId());
        if (capBankId != NULL)
        {
            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                 CTILOG_DEBUG(dout, "CBC attached to CapBank: "<<capBankId);
            }

            CtiCCCapBankPtr cap = CtiCCSubstationBusStore::getInstance()->findCapBankByPAObjectID(capBankId);
            reloadInfo = CcDbReloadInfo(cap->getPaoId(), ChangeTypeUpdate, Cti::CapControl::CapBank);
        }
    }
    return reloadInfo;
}

/*---------------------------------------------------------------------------
    parseMessage

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiCapController::parseMessage(CtiMessage *message)
{
    try
    {
        switch( message->isA() )
        {
            case MSG_DBCHANGE:
                {
                    CtiDBChangeMsg * dbChange = (CtiDBChangeMsg *)message;
                    if( dbChange->getSource() != CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE &&
                        ( (dbChange->getDatabase() == ChangePAODb && resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_CAP_CONTROL) ||
                          (dbChange->getDatabase() == ChangePAODb && resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_DEVICE) ||
                          dbChange->getDatabase() == ChangePointDb ||
                          dbChange->getDatabase() == ChangeCBCStrategyDb ||
                          dbChange->getDatabase() == ChangePAOScheduleDB ))
                    {
                        if( _CC_DEBUG & CC_DEBUG_STANDARD )
                        {
                            CTILOG_DEBUG(dout, "Relevant database change.  Setting reload flag.");
                        }

                        CcDbReloadInfo reloadInfo = resolveCapControlType(dbChange);

                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            Cti::FormattedList list;

                            list << "RELOAD INFO";
                            list.add("changeID")   << reloadInfo.objectId;
                            list.add("changeType") << reloadInfo.typeOfAction();
                            list.add("objType")    << reloadInfo.objecttype;

                            CTILOG_DEBUG(dout, list);
                        }


                        CtiCCSubstationBusStore::getInstance()->insertDBReloadList(reloadInfo);
                    }
                    else if (dbChange->getDatabase() == ChangeIvvcZone)
                    {
                        if( _CC_DEBUG & CC_DEBUG_IVVC )
                        {
                            CTILOG_DEBUG(dout, "IVVC - Zone Change received. Reloading All Zones.");
                        }

                        CcDbReloadInfo reloadInfo(dbChange->getId(), dbChange->getTypeOfChange(), Cti::CapControl::ZoneType);
                        CtiCCSubstationBusStore::getInstance()->insertDBReloadList(reloadInfo);
                    }
                    else if ( dbChange->getCategory() == "CC_MONITOR_BANK_LIST" )
                    {
                        CcDbReloadInfo reloadInfo(dbChange->getId(), dbChange->getTypeOfChange(), Cti::CapControl::MonitorPoint);

                        CtiCCSubstationBusStore::getInstance()->insertDBReloadList(reloadInfo);
                    }
                    else if ( dbChange && dbChange->getDatabase() == ChangeConfigDb )
                    {
                        handleConfigDbChange( dbChange );
                    }
                    else if ( dbChange && dbChange->getDatabase() == ChangeStateGroupDb )
                    {
                        CTILOG_DEBUG(dout, "State Group database change received, reloading state groups.");

                        ReloadStateNames();
                    }
                    else if ( dbChange && dbChange->getDatabase() == ChangeSeasonScheduleDb )
                    {
                        CTILOG_DEBUG(dout, "Season Schedule DB change received, reloading schedules.");

                        CtiSeasonManager::getInstance().refresh();

                        /* 
                            dbChange->getId() is the schedule ID that needs to be reloaded, the manager currently
                                doesn't have the capability to reload a single schedule.

                            The CapControl objects currently don't get reloaded when a schedule changes - that
                                is the way it has always worked (as of April 2016). Schedule changes were only
                                picked up when an individual object was reloaded. A CapControl restart was
                                necessary to see the DB change sooner.

                            In the future it may be nice to schedule a reload of the objects that are tied to
                                the particular schedule being reloaded.
                        */
                    }
                    else if ( dbChange && dbChange->getDatabase() == ChangeHolidayScheduleDb )
                    {
                        CTILOG_DEBUG(dout, "Holiday Schedule DB change received, reloading schedules.");

                        CtiHolidayManager::getInstance().refresh();

                        /* 
                            dbChange->getId() is the schedule ID that needs to be reloaded, the manager currently
                                doesn't have the capability to reload a single schedule.

                            The CapControl objects currently don't get reloaded when a schedule changes - that
                                is the way it has always worked (as of April 2016). Schedule changes were only
                                picked up when an individual object was reloaded. A CapControl restart was
                                necessary to see the DB change sooner.

                            In the future it may be nice to schedule a reload of the objects that are tied to
                                the particular schedule being reloaded.
                        */
                    }
                    if (resolveDBCategory(dbChange->getCategory()) == CtiDBChangeCategory::GlobalSetting)
                    {
                        GlobalSettings::reload();

                        doutManager.reloadMaxFileSize();
                    }
                }
                break;
            case MSG_POINTDATA:
                {
                    const CtiPointDataMsg * pData = static_cast<const CtiPointDataMsg *>( message );
                    pointDataMsg( *pData );
                }
                break;
            case MSG_PCRETURN:
                {
                    const CtiReturnMsg *pcReturn = static_cast<const CtiReturnMsg *>(message);
                    porterReturnMsg(*pcReturn);
                }
                break;
            case MSG_COMMAND:
                {
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CTILOG_DEBUG(dout, "Command Message received from Dispatch");
                    }

                    CtiCommandMsg * cmdMsg = (CtiCommandMsg *)message;
                    if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
                    {
                        try
                        {
                            getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage(), CALLSITE);
                            if( _CC_DEBUG & CC_DEBUG_STANDARD )
                            {
                                CTILOG_DEBUG(dout, "Replied to Are You There message.");
                            }
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }
                    }
                    else if ( cmdMsg->getOperation() == CtiCommandMsg::UpdateFailed )
                    {
                        /*if( _CC_DEBUG & CC_DEBUG_STANDARD )
                        {
                            CTILOG_DEBUG(dout, "Scan completed with an error. This causes Quality to go nonUpdated on all affected points!");
                        } */
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Received not supported Command Message from Dispatch with Operation: "
                             << cmdMsg->getOperation() << ", and Op String: " << cmdMsg->getOpString());
                    }
                }
                break;
            case MSG_MULTI:
                {
                    CtiMultiMsg * msgMulti = (CtiMultiMsg *)message;
                    CtiMultiMsg_vec& temp = msgMulti->getData( );
                    if(temp.size() > 1 &&  _CC_DEBUG & CC_DEBUG_PERFORMANCE )
                    {
                        CTILOG_DEBUG(dout, "ParseMessage MsgMulti has "<< temp.size() <<" entries.");
                    }
                    for(int i=0;i<temp.size( );i++)
                    {
                        parseMessage(temp[i]);
                    }
                }
                break;
            case MSG_SIGNAL:
                {
                    const CtiSignalMsg * signal = static_cast<const CtiSignalMsg *>( message );
                    signalMsg( *signal );
                }
                break;
            case MSG_TAG:
                {
                    const CtiTagMsg * tagMsg = static_cast<const CtiTagMsg *>( message );
                    {
                        if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
                        {
                            CTILOG_DEBUG(dout, "Tag Message - PointID: "<< tagMsg->getPointID()<< " TagDesc:"<<tagMsg->getDescriptionStr() <<".");
                        }
                    }
                }
                break;
            default:
                {
                    CTILOG_ERROR(dout, "Unknown message type " << message->isA());
                }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


double convertPowerFactorToSend(double powerFactor)
{
    double returnPowerFactor = powerFactor;
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
        if ( currentBus->getStrategy()->getUnitType() == ControlStrategy::Volts )
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
        currentBus->setBusUpdatedFlag(true);
        return;
    }
    CtiCCSubstationBusPtr primarySub = store->findSubBusByPAObjectID(currentBus->getAltDualSubId());
    if (primarySub != NULL)
    {
        if ( currentBus->getStrategy()->getUnitType() == ControlStrategy::KVar ||
             currentBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
             currentBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
        {
            if (primarySub->getPrimaryBusFlag())
            {
                primarySub->setAllAltSubValues((primarySub->getCurrentVoltLoadPointValue() + currentBus->getCurrentVoltLoadPointValue()) / 2,
                                       primarySub->getTotalizedVarLoadPointValue() + currentBus->getTotalizedVarLoadPointValue(),
                                       primarySub->getRawCurrentWattLoadPointValue() + currentBus->getRawCurrentWattLoadPointValue());
                primarySub->setNewPointDataReceivedFlag(true);

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

            primarySub->setBusUpdatedFlag(true);
            currentBus->setBusUpdatedFlag(true);
        }
        else if ( currentBus->getStrategy()->getUnitType() == ControlStrategy::Volts )
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
    bool updateSwitchOverStatus = true;
    const bool newSwitchOverStatus = value;

    if ( newSwitchOverStatus != currentSubstationBus->getSwitchOverStatus())
    {
        long stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
        enqueueEventLogEntry(EventLogEntry(false, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSwitchOverUpdate, currentSubstationBus->getEventSequence(), value, "Switch Over Point Updated", "cap control"));
        if (!currentSubstationBus->getDualBusEnable())
        {
            if (value > 0)
            {
                if (!currentSubstationBus->getDisableFlag())
                {
                    currentSubstationBus->setDisableFlag(true);
                    currentSubstationBus->setReEnableBusFlag(true);
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    enqueueEventLogEntry(EventLogEntry(false, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, "Substation Bus Disabled By Inhibit", "cap control"));
                    string text = currentSubstationBus->getPaoName();
                    text += " Disabled";
                    string additional = string("Inhibit PointId Updated");

                    sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"), CALLSITE);
                }

            }
            else
            {
                currentSubstationBus->setDisableFlag(false);
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                enqueueEventLogEntry(EventLogEntry(false, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, "Substation Bus Enabled By Inhibit", "cap control"));
                string text = currentSubstationBus->getPaoName();
                text += " Enabled";
                string additional = string("Inhibit PointId Updated");

                sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"), CALLSITE);

            }


        }
        else //dual Bus Enabled.
        {
            string text = currentSubstationBus->getPaoName();
            if (currentSubstationBus->getAltDualSubId() != currentSubstationBus->getPaoId())
            {
                CtiCCSubstationBusPtr altSub = store->findSubBusByPAObjectID(currentSubstationBus->getAltDualSubId());
                if (altSub != NULL)
                {
                    if (value > 0)
                    {
                        if (altSub->getDisableFlag())
                        {
                            currentSubstationBus->setReEnableBusFlag(!currentSubstationBus->getDisableFlag());
                            currentSubstationBus->setDisableFlag(true);
                            text += " Disabled by Alt Sub";
                        }
                        else if (altSub->getSwitchOverStatus())
                        {
                            if (currentSubstationBus->getPrimaryBusFlag())
                            {
                                updateSwitchOverStatus = false;
                                text += " is already operating in Dual Bus Mode operating as Primary for ";
                                text += altSub->getPaoName();
                                text += ".";
                            }
                            else
                            {
                                currentSubstationBus->setDisableFlag(true);
                                currentSubstationBus->setReEnableBusFlag(true);
                                altSub->setDisableFlag(true);
                                altSub->setReEnableBusFlag(true);
                                altSub->setBusUpdatedFlag(true);
                                text += " Disabled by Alt Sub";
                            }
                        }
                        else
                        {
                            text += " Alt Sub Enabled";
                            if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::KVar ||
                                 currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                                 currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                            {

                                altSub->setPrimaryBusFlag(true);
                                altSub->setBusUpdatedFlag(true);
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
                            currentSubstationBus->setDisableFlag(false);
                            currentSubstationBus->setReEnableBusFlag(false);
                            text += " ReEnabled";
                            if (altSub->getAltDualSubId() == currentSubstationBus->getPaoId())
                            {
                                if (altSub->getSwitchOverStatus() && altSub->getDisableFlag() && altSub->getReEnableBusFlag())
                                {
                                    altSub->setDisableFlag(false);
                                    altSub->setReEnableBusFlag(false);
                                    altSub->setBusUpdatedFlag(true);
                                }
                            }
                        }
                        else
                        {
                            altSub->setPrimaryBusFlag(false);
                            altSub->setBusUpdatedFlag(true);
                            text += " Alt Sub Not Enabled";
                            if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::KVar ||
                                 currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                                 currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                            {

                                CtiFeeder_vec& ccFeeders = altSub->getCCFeeders();
                                int j = ccFeeders.size();
                                while (j > 0)
                                {
                                    CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j-1];
                                    if (currentFeeder->getOriginalParent().getOriginalParentId() == currentSubstationBus->getPaoId())
                                    {
                                        CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::RETURN_FEEDER_TO_ORIGINAL_SUBBUS, currentFeeder->getPaoId()))->execute();
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
            sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"), CALLSITE);
        }
    }
    else if (value > 0 && !currentSubstationBus->getDisableFlag() && !currentSubstationBus->getDualBusEnable() )
    {
        currentSubstationBus->setDisableFlag(true);
        currentSubstationBus->setReEnableBusFlag(true);
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
        long stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
        enqueueEventLogEntry(EventLogEntry(0, pointID, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, "Substation Bus Disabled By Inhibit", "cap control"));
        string text = currentSubstationBus->getPaoName();
        text += " Disabled";
        string additional = string("Inhibit PointId Updated");

        sendMessageToDispatch(new CtiSignalMsg(pointID,0,text,additional,CapControlLogType,SignalEvent,"cap control"), CALLSITE);

    }
    if (updateSwitchOverStatus)
    {
        currentSubstationBus->setSwitchOverStatus(value);
        currentSubstationBus->setNewPointDataReceivedFlag(true);
        currentSubstationBus->setBusUpdatedFlag(true);
    }

}

/*---------------------------------------------------------------------------
    pointDataMsg

    Handles point data messages and updates substation bus point values.
---------------------------------------------------------------------------*/
void CtiCapController::pointDataMsg ( const CtiPointDataMsg & message )
{
    const int      pointID       = message.getId();
    const double   value         = message.getValue();
    const PointQuality_t quality = static_cast<PointQuality_t>( message.getQuality() );
    const unsigned tags          = message.getTags();
    const CtiTime  timestamp     = message.getTime();

    if( _CC_DEBUG & CC_DEBUG_POINT_DATA )
    {
        Cti::FormattedList list;

        list << "Point Data";
        list.add("ID")      << pointID;
        list.add("Val")     << value;
        list.add("Time")    << timestamp;
        list.add("Quality") << desolvePointQuality( quality );
        list.add("Tags")    << tags;

        CTILOG_INFO(dout, list);
    }

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    try
    {
        CapControlPointDataHandler & pointHandler = store->getPointDataHandler();
        pointHandler.processIncomingPointData( message );

        pointDataMsgBySubBus(pointID, value, quality, timestamp);

        pointDataMsgByFeeder(pointID, value, quality, timestamp);

        if (Cti::CapControl::isQualityOk(quality))
        {
            pointDataMsgByCapBank(pointID, value, quality, tags, timestamp);

            // gross... process point update messages for cbc heartbeat points...
            PointIdToCapBankMultiMap::iterator capIter, end;
            store->findCapBankByPointID(pointID, capIter, end);

            while ( capIter != end )
            {
                capIter->second->handlePointData( message );
                ++capIter;
            }
        }

        // Areas, Special Areas and Substations handled here
        for ( auto range = store->getPointIDToPaoMultiMap().equal_range( pointID );
              range.first != range.second;
              ++range.first )
        {
            range.first->second->handlePointData( message );
        }

        if (store->getLinkStatusPointId() > 0)
        {
            if (store->getLinkStatusPointId() == pointID)
            {
                const bool newLinkStatusFlag = value;
                if (store->getLinkStatusFlag() != newLinkStatusFlag)
                {
                    store->setLinkStatusFlag(value);
                    if (value == STATE_CLOSED)
                    {
                        store->setLinkDropOutTime(timestamp);
                    }
                    if (value == STATE_OPENED)
                    {
                        // dispatch connection will handle the point re-registration itself
                    }
                }
            }
        }
        if (pointID == _VOLT_REDUCTION_SYSTEM_POINTID)
        {
            const bool disabled = value;
            if (disabled != store->getVoltReductionSystemDisabled())
            {
                if (disabled)
                {
                    store->setVoltReductionSystemDisabled(true);
                    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();
                    for(long i=0;i<ccAreas.size();i++)
                    {
                        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
                        if (currentArea != NULL)
                        {
                            CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::AUTO_DISABLE_OVUV, currentArea->getPaoId()))->execute();
                        }
                    }
                }
                else
                {
                    store->setVoltReductionSystemDisabled(false);
                    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();
                    for(long i=0;i<ccAreas.size();i++)
                    {
                        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
                        if (currentArea != NULL)
                        {
                            CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::AUTO_ENABLE_OVUV, currentArea->getPaoId()))->execute();
                        }
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCapController::checkDisablePaoPoint(CapControlPao* pao, long pointID, bool disable, long enableCommand, long disableCommand)
{
    if (pao->getDisabledStatePointId() == pointID)
    {
        if (pao->getDisableFlag() != disable)
        {
            long command = disable ? disableCommand : enableCommand;
            CtiCCExecutorFactory::createExecutor(new ItemCommand(command, pao->getPaoId()))->execute();
        }
    }
}

void CtiCapController::pointDataMsgBySubBus( long pointID, double value, PointQuality_t quality, const CtiTime& timestamp)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCSubstationPtr currentStation = NULL;
    CtiCCAreaPtr currentArea = NULL;

    PointIdToSubBusMultiMap::iterator subIter, end;

    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );
    store->findSubBusByPointID(pointID, subIter, end);

    while (subIter != end )
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
                        currentSubstationBus->setNewPointDataReceivedFlag(true);
                        if (!currentSubstationBus->getUsePhaseData())
                        {

                            if (currentSubstationBus->getRawCurrentVarLoadPointValue() != value)
                            {
                                currentSubstationBus->setCurrentVarLoadPointValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(true);
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
                                currentSubstationBus->setBusUpdatedFlag(true);
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
                            sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType), CALLSITE);
                        }

                        if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
                        {
                            if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                            {
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(value,currentSubstationBus->getCurrentWattLoadPointValue()),timestamp);
                            }
                            currentSubstationBus->setPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                            currentSubstationBus->setBusUpdatedFlag(true);
                            if( currentSubstationBus->getPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                            }
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                            }
                        }
                        else if ( currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::KVar &&
                                  currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::Volts )
                        {
                            CTILOG_ERROR(dout, "No Watt Point attached to bus: " << currentSubstationBus->getPaoName() <<", cannot calculate power factor");
                        }

                        if ( Cti::CapControl::notIvvcStrategy( currentSubstationBus ) )
                        {
                            currentSubstationBus->figureAndSetTargetVarValue();
                        }
                    }
                    else if ( timestamp == currentSubstationBus->getLastCurrentVarPointUpdateTime() 
                                && quality != currentSubstationBus->getCurrentVarPointQuality() )
                    {
                        // so we got a point quality update from dispatch... see YUK-18257

                        currentSubstationBus->setCurrentVarPointQuality( quality );
                        currentSubstationBus->setBusUpdatedFlag( true );
                    }
                }
                else if( currentSubstationBus->getCurrentWattLoadPointId() == pointID )
                {
                    if( timestamp > currentSubstationBus->getLastWattPointTime() )
                    {
                        currentSubstationBus->setLastWattPointTime(timestamp);
                        if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                        {
                            double tempKQ = currentSubstationBus->convertKVARToKQ(value,currentSubstationBus->getCurrentWattLoadPointValue());
                            currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(tempKQ,value),timestamp);
                        }

                        if (currentSubstationBus->getCurrentWattLoadPointValue() != value)
                        {
                            currentSubstationBus->setCurrentWattLoadPointValue(value);
                            currentSubstationBus->setBusUpdatedFlag(true);
                        }
                        if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getStrategy()->getIntegrateFlag())
                        {
                            currentSubstationBus->updateIntegrationWPoint(CtiTime());
                        }

                        currentSubstationBus->setCurrentWattPointQuality(quality);

                        if( currentSubstationBus->getCurrentVarLoadPointId() > 0 )
                        {
                            currentSubstationBus->setPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentSubstationBus->getCurrentVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            currentSubstationBus->setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                            currentSubstationBus->setBusUpdatedFlag(true);
                            if( currentSubstationBus->getPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                            }
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                            }
                        }
                        else if ( currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::KVar )
                        {
                            CTILOG_ERROR(dout, "No Var Point attached to bus: " << currentSubstationBus->getPaoName() <<", cannot calculate power factor");
                        }
                        adjustAlternateBusModeValues(pointID, value, currentSubstationBus);
                    }
                    else if ( timestamp == currentSubstationBus->getLastWattPointTime() 
                                && quality != currentSubstationBus->getCurrentWattPointQuality() )
                    {
                        // so we got a point quality update from dispatch... see YUK-18257

                        currentSubstationBus->setCurrentWattPointQuality( quality );
                        currentSubstationBus->setBusUpdatedFlag( true );
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
                            currentSubstationBus->setBusUpdatedFlag(true);
                        }
                        if (currentSubstationBus->isControlPoint(pointID) && currentSubstationBus->getStrategy()->getIntegrateFlag())
                        {
                            currentSubstationBus->updateIntegrationVPoint(CtiTime());
                        }
                        currentSubstationBus->setCurrentVoltPointQuality(quality);
                        adjustAlternateBusModeValues(pointID, value, currentSubstationBus);
                        currentSubstationBus->setNewPointDataReceivedFlag(true);
                        currentSubstationBus->figureAndSetTargetVarValue();
                    }
                    else if ( timestamp == currentSubstationBus->getLastVoltPointTime() 
                                && quality != currentSubstationBus->getCurrentVoltPointQuality() )
                    {
                        // so we got a point quality update from dispatch... see YUK-18257

                        currentSubstationBus->setCurrentVoltPointQuality( quality );
                        currentSubstationBus->setBusUpdatedFlag( true );
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
                        currentSubstationBus->setNewPointDataReceivedFlag(true);

                        if (currentSubstationBus->getPhaseBId() == pointID)
                        {
                            if (currentSubstationBus->getPhaseBValue() != value)
                            {
                                currentSubstationBus->setPhaseBValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(true);
                                currentSubstationBus->setCurrentVarLoadPointValue(currentSubstationBus->getPhaseAValue() + currentSubstationBus->getPhaseBValue() + currentSubstationBus->getPhaseCValue(),timestamp);
                            }
                        }
                        if (currentSubstationBus->getPhaseCId() == pointID)
                        {
                            if (currentSubstationBus->getPhaseCValue() != value)
                            {
                                currentSubstationBus->setPhaseCValue(value,timestamp);
                                currentSubstationBus->setBusUpdatedFlag(true);
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
                            currentSubstationBus->setBusUpdatedFlag(true);
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
                        CTILOG_DEBUG(dout, "Optional POINT data message received for: " << pointID << " on SUB: " << currentSubstationBus->getPaoName());
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
                        if ( altSub->getStrategy()->getUnitType() == ControlStrategy::KVar ||
                             altSub->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                             altSub->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                        {
                            if (currentSubstationBus->getPrimaryBusFlag())
                            {
                                currentSubstationBus->setAllAltSubValues((altSub->getCurrentVoltLoadPointValue() + currentSubstationBus->getCurrentVoltLoadPointValue()) / 2,
                                                           altSub->getRawCurrentVarLoadPointValue() + currentSubstationBus->getRawCurrentVarLoadPointValue(),
                                                           altSub->getRawCurrentWattLoadPointValue() + currentSubstationBus->getRawCurrentWattLoadPointValue());
                                currentSubstationBus->setBusUpdatedFlag(true);

                            }
                        }
                        else
                        {
                             altSub->setAllAltSubValues(currentSubstationBus->getCurrentVoltLoadPointValue(), currentSubstationBus->getCurrentVarLoadPointValue(),
                             currentSubstationBus->getCurrentWattLoadPointValue());
                             altSub->setAltSubControlValue(currentSubstationBus->getTotalizedVarLoadPointValue());
                             altSub->setBusUpdatedFlag(true);
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
                            currentSubstationBus->setVoltReductionFlag(true);
                            currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (currentStation != NULL)
                            {
                                currentStation->setChildVoltReductionFlag(true);
                                currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                                if (currentArea != NULL)
                                {
                                    currentArea->setChildVoltReductionFlag(true);
                                }
                            }


                            if (_AUTO_VOLT_REDUCTION)
                            {
                                CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::AUTO_DISABLE_OVUV, currentSubstationBus->getPaoId()))->execute();
                            }
                        }
                        currentSubstationBus->setBusUpdatedFlag(true);
                    }
                    else
                    {
                        if (value == 0)
                        {
                            currentSubstationBus->setVoltReductionFlag(false);
                            store->checkAndUpdateVoltReductionFlagsByBus(currentSubstationBus);

                            if (_AUTO_VOLT_REDUCTION)
                            {
                                CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::AUTO_ENABLE_OVUV, currentSubstationBus->getPaoId()))->execute();
                            }

                            currentSubstationBus->setBusUpdatedFlag(true);
                        }
                    }

                }
                if (currentSubstationBus->getDisableBusPointId() == pointID)
                {
                    if (value > 0 && !currentSubstationBus->getDisableFlag())
                    {
                       CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::DISABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()))->execute();
                       currentSubstationBus->setReEnableBusFlag(true);
                    }
                    if (value == 0 && currentSubstationBus->getDisableFlag() && currentSubstationBus->getReEnableBusFlag())
                    {
                       CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()))->execute();
                    }
                }

                if ( currentSubstationBus->getDisabledStatePointId() == pointID )
                {
                    if ( ! currentSubstationBus->getVerificationFlag() )
                    {
                        checkDisablePaoPoint(currentSubstationBus, pointID, value, CapControlCommand::ENABLE_SUBSTATION_BUS, CapControlCommand::DISABLE_SUBSTATION_BUS);
                    }
                    else if ( value == 0 )   // we are trying to enable a bus that is verifying
                    {
                        CTILOG_WARN( dout, "Cannot ENABLE the bus '"
                                        <<  currentSubstationBus->getPaoName() << "' during Bank Verification" );

                        // change the point back to disabled so it is consistent with the actual state of the bus
                        sendMessageToDispatch( new CtiPointDataMsg( pointID, 1.0, NormalQuality, StatusPointType ), CALLSITE );
                    }
                }

                CtiCCMonitorPointPtr currentMonPoint = currentSubstationBus->getMonitorPoint(pointID);
                if( currentMonPoint != NULL  && currentMonPoint->getValue() != value)
                {
                    currentMonPoint->setValue(value);
                    currentMonPoint->setTimeStamp(timestamp);
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
        subIter++;
    }

}

void CtiCapController::pointDataMsgByFeeder( long pointID, double value, PointQuality_t quality, const CtiTime& timestamp )
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = NULL;
    PointIdToFeederMultiMap::iterator feedIter, end;

    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );
    store->findFeederByPointID(pointID, feedIter, end);

    while (feedIter != end)
    {
        currentFeeder = feedIter->second;
        try
        {
            currentSubstationBus = NULL;
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
                            currentFeeder->setNewPointDataReceivedFlag(true);
                            if (!currentFeeder->getUsePhaseData())
                            {
                                if (currentFeeder->getCurrentVarLoadPointValue() != value)
                                {
                                    currentFeeder->setCurrentVarLoadPointValue(value, timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                }
                                if (currentFeeder->isControlPoint(pointID) && currentFeeder->getStrategy()->getIntegrateFlag())
                                {
                                    currentFeeder->updateIntegrationVPoint(CtiTime(), currentSubstationBus->getNextCheckTime());
                                }
                            }
                            else //phase A point id
                            {
                                if (currentFeeder->getPhaseAValue() != value)
                                {
                                    currentFeeder->setPhaseAValue(value,timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue(), timestamp);
                                }
                                if (currentFeeder->isControlPoint(pointID) && currentFeeder->getStrategy()->getIntegrateFlag())
                                {
                                    currentFeeder->updateIntegrationVPoint(CtiTime(), currentSubstationBus->getNextCheckTime());
                                }
                            }

                            currentFeeder->figureEstimatedVarLoadPointValue();
                            currentFeeder->setCurrentVarPointQuality(quality);
                            if( currentFeeder->getEstimatedVarLoadPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedVarLoadPointId(),currentFeeder->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType), CALLSITE);
                            }

                            if( currentFeeder->getCurrentWattLoadPointId() > 0 )
                            {
                                if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                                {
                                    currentFeeder->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(value,currentFeeder->getCurrentWattLoadPointValue()), timestamp);
                                }
                                currentFeeder->setPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentFeeder->getCurrentVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentFeeder->setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentSubstationBus->figureAndSetPowerFactorByFeederValues();
                                store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                                if( currentFeeder->getPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                                }
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                                }
                            }
                            else if ( currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::KVar &&
                                      currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::Volts )
                            {
                                CTILOG_ERROR(dout, "No Watt Point attached to feeder: " << currentFeeder->getPaoName() <<", cannot calculate power factor");
                            }
                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                        else if ( timestamp == currentFeeder->getLastCurrentVarPointUpdateTime() 
                                    && quality != currentFeeder->getCurrentVarPointQuality() )
                        {
                            // so we got a point quality update from dispatch... see YUK-18257

                            currentFeeder->setCurrentVarPointQuality( quality );
                            currentSubstationBus->setBusUpdatedFlag( true );
                        }
                    }
                    else if( currentFeeder->getCurrentWattLoadPointId() == pointID )
                    {
                        if( timestamp > currentFeeder->getLastWattPointTime() )
                        {
                            currentFeeder->setLastWattPointTime(timestamp);
                            if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                            {
                                double tempKQ = currentSubstationBus->convertKVARToKQ(value,currentFeeder->getCurrentWattLoadPointValue());
                                currentFeeder->setCurrentVarLoadPointValue(currentSubstationBus->convertKQToKVAR(tempKQ,value),timestamp);
                            }

                            if (currentFeeder->getCurrentWattLoadPointValue() != value)
                            {
                                currentFeeder->setCurrentWattLoadPointValue(value);
                                currentFeeder->setNewPointDataReceivedFlag(true);
                                currentSubstationBus->setBusUpdatedFlag(true);
                            }
                            currentFeeder->setCurrentWattPointQuality(quality);

                            if( currentFeeder->getCurrentVarLoadPointId() > 0 )
                            {
                                currentFeeder->setPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentFeeder->getCurrentVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentFeeder->setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                                currentSubstationBus->figureAndSetPowerFactorByFeederValues();
                                store->calculateParentPowerFactor(currentSubstationBus->getPaoId());
                                if( currentFeeder->getPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                                }
                                if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                                {
                                    sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                                }
                            }
                            else if ( currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::KVar )
                            {
                                CTILOG_ERROR(dout, "No Var Point attached to feeder: " << currentFeeder->getPaoName() <<", cannot calculate power factor");
                            }

                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                        else if ( timestamp == currentFeeder->getLastWattPointTime() 
                                    && quality != currentFeeder->getCurrentWattPointQuality() )
                        {
                            // so we got a point quality update from dispatch... see YUK-18257

                            currentFeeder->setCurrentWattPointQuality( quality );
                            currentSubstationBus->setBusUpdatedFlag( true );
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
                                currentFeeder->setNewPointDataReceivedFlag(true);
                                currentSubstationBus->setBusUpdatedFlag(true);
                            }
                            currentFeeder->setCurrentVoltPointQuality(quality);

                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                        else if ( timestamp == currentFeeder->getLastVoltPointTime() 
                                    && quality != currentFeeder->getCurrentVoltPointQuality() )
                        {
                            // so we got a point quality update from dispatch... see YUK-18257

                            currentFeeder->setCurrentVoltPointQuality( quality );
                            currentSubstationBus->setBusUpdatedFlag( true );
                        }
                    }
                    else if (currentFeeder->getDailyOperationsAnalogPointId()  == pointID )
                    {
                        if(  currentFeeder->getDailyOperationsAnalogPointId()  == pointID)
                        {
                            if (currentFeeder->getCurrentDailyOperations() != value)
                            {
                                //sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getDailyOperationsAnalogPointId(),value,NormalQuality,AnalogPointType));
                                currentSubstationBus->setBusUpdatedFlag(true);
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
                            CTILOG_DEBUG(dout, "Optional POINT data message received for: " << pointID << " on SUB: " << currentSubstationBus->getPaoName());
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
                                    currentFeeder->setNewPointDataReceivedFlag(true);

                                    currentFeeder->setPhaseBValue(value, timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue(),timestamp);
                                }
                            }
                            if (currentFeeder->getPhaseCId() == pointID)
                            {
                                if (currentFeeder->getPhaseCValue() != value)
                                {
                                    currentFeeder->setNewPointDataReceivedFlag(true);

                                    currentFeeder->setPhaseCValue(value, timestamp);
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentFeeder->setCurrentVarLoadPointValue(currentFeeder->getPhaseAValue() + currentFeeder->getPhaseBValue() + currentFeeder->getPhaseCValue(),timestamp);
                                }
                            }
                            currentFeeder->figureEstimatedVarLoadPointValue();

                            currentFeeder->figureAndSetTargetVarValue(currentSubstationBus->getStrategy()->getControlMethod(), currentSubstationBus->getStrategy()->getControlUnits(), currentSubstationBus->getPeakTimeFlag());
                        }
                    }
                    checkDisablePaoPoint(currentFeeder, pointID, value, CapControlCommand::ENABLE_FEEDER, CapControlCommand::DISABLE_FEEDER);
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
        feedIter++;
    }

    /*if( pointChanges.size() > 0 )
    {
        getDispatchConnection()->WriteConnQue(multiPointMsg);
    }*/

}


void CtiCapController::pointDataMsgByCapBank( long pointID, double value, PointQuality_t quality, unsigned tags, const CtiTime& timestamp)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = NULL;
    CtiCCCapBankPtr currentCapBank = NULL;
    PointIdToCapBankMultiMap::iterator capIter, end;

    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );
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
                            currentSubstationBus->setBusUpdatedFlag(true);
                            store->set2wayFlagUpdate(true);
                        }
                        //JULIE: TXU fix for status change time setting to old date on disabled banks.
                        if (!currentCapBank->getDisableFlag())
                        {
                            if (currentCapBank->getControlStatus() != (LONG)value &&
                                !currentCapBank->getInsertDynamicDataFlag() &&
                                timestamp > currentCapBank->getLastStatusChangeTime())
                            {
                                if (value < 0)
                                {
                                    CTILOG_WARN(dout, "CapBank: "<<currentCapBank->getPaoName()<<" received an invalid control state value ("
                                         << (long)value << "). Not adjusting the cap bank state.");
                                }
                                else
                                {
                                    CTILOG_INFO(dout, "CapBank: "<<currentCapBank->getPaoName()<<" State adjusted from "<<currentCapBank->getControlStatus() <<" to "<<value);
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
                                        long stationId, areaId, spAreaId;
                                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                        EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, "point data msg" );
                                        eventMsg.setActionId(currentCapBank->getActionId());
                                        eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                                        enqueueEventLogEntry(eventMsg);

                                        currentCapBank->setIgnoreFlag(false);
                                        currentCapBank->setPorterRetFailFlag(false);
                                    }
                                    currentCapBank->setControlStatus((LONG)value);
                                    currentCapBank->setTagsControlStatus((LONG)tags);
                                    currentCapBank->setLastStatusChangeTime(timestamp);
                                    currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
                                }
                            }
                        }
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                            sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType), CALLSITE);

                        if( currentSubstationBus->getCurrentWattLoadPointId() > 0 )
                        {
                            currentSubstationBus->setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentSubstationBus->getEstimatedVarLoadPointValue(),currentSubstationBus->getCurrentWattLoadPointValue()));
                            if( currentSubstationBus->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentSubstationBus->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                            }
                        }
                        if( currentFeeder->getCurrentWattLoadPointId() > 0 )
                        {
                            currentFeeder->setEstimatedPowerFactorValue(Cti::CapControl::calculatePowerFactor(currentFeeder->getEstimatedVarLoadPointValue(),currentFeeder->getCurrentWattLoadPointValue()));
                            if( currentFeeder->getEstimatedPowerFactorPointId() > 0 )
                            {
                                sendMessageToDispatch(new CtiPointDataMsg(currentFeeder->getEstimatedPowerFactorPointId(),convertPowerFactorToSend(currentFeeder->getEstimatedPowerFactorValue()),NormalQuality,AnalogPointType), CALLSITE);
                            }
                        }
                    }
                    else if( currentCapBank->getOperationAnalogPointId() == pointID )
                    {
                        if( timestamp > currentCapBank->getLastStatusChangeTime() ||
                            currentCapBank->getTotalOperations() != (LONG)value )
                        {
                            currentSubstationBus->setBusUpdatedFlag(true);
                        }
                        //JULIE: BGE fix for status change time setting to old date on disabled banks.
                        if (!currentCapBank->getDisableFlag())
                        {
                            currentCapBank->setTotalOperations((LONG)value);
                        }
                    }
                    else if (currentCapBank->isControlDeviceTwoWay() )
                    {
                        CtiCCTwoWayPoints & twoWayPts = currentCapBank->getTwoWayPoints();

                        if (twoWayPts.setTwoWayStatusPointValue(pointID, value, timestamp, quality))
                        {
                            if (currentCapBank->getPointIdByAttribute( Attribute::ControlPoint ) == pointID )
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
                                        currentCapBank->setUnsolicitedPendingFlag(true);
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
                                        currentCapBank->setUnsolicitedPendingFlag(true);
                                    }

                                }
                                else if (stringContainsIgnoreCase(currentCapBank->getAfterVarsString(), "Manual") &&
                                    ( (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::Close ) &&
                                       value !=  currentCapBank->getControlStatus()) )
                                {
                                    currentCapBank->setControlStatus(value);
                                    currentCapBank->setAfterVarsString("Adjusted to CBC Reported State");
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    CTILOG_INFO(dout, "Adjusting CapBank status to match CBC...");
                                    sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE), CALLSITE);

                                }

                                if ( !currentFeeder->getRecentlyControlledFlag() &&
                                     currentCapBank->getControlRecentlySentFlag() &&
                                     ( (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                                        currentCapBank->getControlStatus() == CtiCCCapBank::Close ) &&
                                       value ==  currentCapBank->getControlStatus()) )
                                {
                                    currentCapBank->setControlRecentlySentFlag(false);
                                }
                                currentCapBank->setReportedCBCState(twoWayPts.getPointValueByAttribute( Attribute::ControlPoint ));
                                store->set2wayFlagUpdate(true);

                            }
                            if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                            {
                                CTILOG_DEBUG(dout, "Set a cbc 2 way status point... pointID ("<<pointID<<") = "<<value);
                            }
                            if (const auto avc = twoWayPts.findPointValueByAttribute( Attribute::AutoVoltageControl ))
                            {
                                if (*avc)
                                {
                                    if (currentCapBank->getOvUvDisabledFlag())
                                    {
                                        currentCapBank->setOvUvDisabledFlag(false);
                                        currentSubstationBus->setBusUpdatedFlag(true);
                                    }
                                }
                                else
                                {
                                    if (!currentCapBank->getOvUvDisabledFlag())
                                    {
                                        currentCapBank->setOvUvDisabledFlag(true);
                                        currentSubstationBus->setBusUpdatedFlag(true);
                                    }
                                }
                            }
                            if (twoWayPts.getPointValueByAttribute( Attribute::LastControlReasonOvUv ) > 0)
                            {
                                if (!currentCapBank->getOvUvSituationFlag())
                                {
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentCapBank->setOvUvSituationFlag(true);
                                }
                            }
                            else
                            {
                                if (currentCapBank->getOvUvSituationFlag())
                                {
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentCapBank->setOvUvSituationFlag(false);
                                }

                            }

                            if (twoWayPts.getPointValueByAttribute( Attribute::ControlMode ) == 0 )
                            {
                                if (currentCapBank->getLocalControlFlag())
                                {
                                    currentCapBank->setLocalControlFlag(false);
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                }
                            }
                            else
                            {
                                if (!currentCapBank->getLocalControlFlag())
                                {
                                    currentCapBank->setLocalControlFlag(true);
                                    currentSubstationBus->setBusUpdatedFlag(true);

                                }
                            }

                        }
                        else if (twoWayPts.setTwoWayAnalogPointValue(pointID, value, timestamp, quality))
                        {
                            if (currentCapBank->getPointIdByAttribute( Attribute::IpAddress ) == pointID)
                            {
                                currentCapBank->setIpAddress(twoWayPts.getPointValueByAttribute( Attribute::IpAddress ));
                            }
                            else if (currentCapBank->getPointIdByAttribute( Attribute::UdpPort ) == pointID)
                            {
                                currentCapBank->setUDPPort(twoWayPts.getPointValueByAttribute( Attribute::UdpPort ));
                            }
                            else if ( currentCapBank->getPointIdByAttribute( Attribute::IgnoredControlReason ) == pointID )
                            {
                                currentCapBank->setIgnoredReason(value);
                                currentSubstationBus->setBusUpdatedFlag(true);

                                if ( ( tags & TAG_POINT_DATA_TIMESTAMP_VALID ) &&   // it's ba DNP event (class 123), not DNP static data (class 0)  AND
                                     currentCapBank->isPendingStatus() &&           // our bank is pending  AND
                                     ! twoWayPts.isControlAccepted() )              // it is a CBC-8000 'Control Accepted' (19) response
                                {
                                    store->insertRejectedCapBankList(currentCapBank);   // stuff it on the rejected list
                                }
                            }
                            else if ( currentCapBank->getPointIdByAttribute( Attribute::LastControlReason ) == pointID )
                            {
                                if ( value == LastControlReasonCbc802x::EmergencyVoltage ||
                                     value == LastControlReasonCbc802x::OvUvControl )
                                {
                                    if ( ! currentCapBank->getOvUvSituationFlag() )
                                    {
                                        currentCapBank->setOvUvSituationFlag( true );
                                        currentSubstationBus->setBusUpdatedFlag( true );
                                    }
                                }
                                else
                                {
                                    if ( currentCapBank->getOvUvSituationFlag() )
                                    {
                                        currentCapBank->setOvUvSituationFlag( false );
                                        currentSubstationBus->setBusUpdatedFlag( true );
                                    }
                                }
                            }

                            if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                            {
                                CTILOG_DEBUG(dout, "Set a cbc 2 way Analog point... pointID ("<<pointID<<") = "<<value);
                            }
                        }
                        else if (twoWayPts.setTwoWayPulseAccumulatorPointValue(pointID, value, timestamp, quality))
                        {
                            if( _CC_DEBUG & CC_DEBUG_OPTIONALPOINT )
                            {
                                CTILOG_DEBUG(dout, "Set a cbc 2 way Pulse Accumulator point... pointID ("<<pointID<<") = "<<value);
                            }
                        }


                        for (int i = 0; i < currentCapBank->getMonitorPoint().size(); i++)
                        {
                            CtiCCMonitorPointPtr currentMonPoint = (CtiCCMonitorPointPtr)currentCapBank->getMonitorPoint()[i];
                            if( currentMonPoint->getPointId() == pointID )
                            {
                                if (currentMonPoint->getValue() != value)
                                {
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentSubstationBus->setNewPointDataReceivedFlag(true);


                                    currentFeeder->setNewPointDataReceivedFlag(true);
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
                                    currentSubstationBus->setBusUpdatedFlag(true);
                                    currentSubstationBus->setNewPointDataReceivedFlag(true);

                                    currentFeeder->setNewPointDataReceivedFlag(true);
                                }
                                currentMonPoint->setValue(value);
                                currentMonPoint->setTimeStamp(timestamp);
                                break;
                            }
                        }
                    }
                    checkDisablePaoPoint(currentCapBank, pointID, value, CapControlCommand::ENABLE_CAPBANK, CapControlCommand::DISABLE_CAPBANK);
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
        capIter++;
    }
}

/*---------------------------------------------------------------------------
    porterReturn

    Handles porter return messages and updates the status of substation bus
    cap bank controls.
---------------------------------------------------------------------------*/
void CtiCapController::porterReturnMsg( const CtiReturnMsg &retMsg )
{
    const long   deviceId      = retMsg.DeviceId();
    const string commandString = retMsg.CommandString();
    const int    status        = retMsg.Status();
    const bool   expectMore    = retMsg.ExpectMore();

    if( ciStringEqual(commandString, "scan general") ||
        ciStringEqual(commandString, "scan integrity") )
    {
        return;
    }
    if( status == ClientErrors::PortNotInitialized && expectMore )
    {
        return;
    }

    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
    {
        CTILOG_DEBUG(dout, "Porter return received");
    }

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

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

    bool found = false;

    if( currentCapBank->getControlDeviceId() == deviceId && deviceId > 0)
    {
        if( currentSubstationBus->getRecentlyControlledFlag() ||
             currentSubstationBus->getVerificationFlag() )
        {
            using namespace Cti::CapControl;

            const BankOperationType operationType = resolveOperationTypeForPointId( commandString, 
                                                                                    *currentCapBank );

            if( status == ClientErrors::None )
            {
                if ( !currentCapBank->isControlDeviceTwoWay() &&
                     !currentSubstationBus->getVerificationFlag() )
                {
                    switch( operationType )
                    {
                        case BankOperation_Open:
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                            break;

                        case BankOperation_Close:
                            currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                            break;
                    }
                }
                if (currentCapBank->getControlStatusQuality() == CC_CommFail)
                {
                    currentCapBank->setControlStatusQuality(CC_Normal);
                }
            }
            else
            {
                switch( operationType )
                {
                    case BankOperation_Open:
                    {
                        if( ! currentCapBank->isControlDeviceTwoWay() )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                        }
                        break;
                    }

                    case BankOperation_Close:
                    {
                        if( ! currentCapBank->isControlDeviceTwoWay() )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                        }
                        break;
                    }

                    case BankOperation_Flip:
                    {
                        if (currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending)
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                        }
                        break;
                    }
                }
                CTILOG_WARN(dout, "Porter Return May Indicate a Comm Failure!!  Bus: " << currentSubstationBus->getPaoName() << ", Feeder: " << currentFeeder->getPaoName()<< ", CapBank: " << currentCapBank->getPaoName());
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

                        if (!currentCapBank->isControlDeviceTwoWay())
                        {
                            currentCapBank->setPorterRetFailFlag(true);
                            currentCapBank->setControlRecentlySentFlag(false);

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
                            currentFeeder->setPorterRetFailFlag(true);
                            currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
                        }

                    }

                    else if (!currentCapBank->isControlDeviceTwoWay() )
                    {
                        text1 += currentCapBank->getControlStatusText();
                        currentCapBank->setControlRecentlySentFlag(false);

                        sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE), CALLSITE);
                        long stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                        EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, userName, 0, 0, 0, currentCapBank->getIpAddress());
                        eventMsg.setActionId(currentCapBank->getActionId());
                        eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                        enqueueEventLogEntry(eventMsg);

                        currentCapBank->setLastStatusChangeTime(CtiTime());
                        currentFeeder->setRetryIndex(0);

                    }
                    sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,userName), CALLSITE);
                }
                else
                {
                    CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                  << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!");
                }

                if (!currentCapBank->isControlDeviceTwoWay() )
                {
                    currentFeeder->setPorterRetFailFlag(true);
                    currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
                }

                currentSubstationBus->setBusUpdatedFlag(true);
            }
        }
    }

}



void CtiCapController::handleRejectionMessaging(CtiCCCapBankPtr currentCapBank, CtiCCFeederPtr currentFeeder,
                                                CtiCCSubstationBusPtr currentSubstationBus, CtiCCTwoWayPoints & twoWayPts)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    EventLogEntries ccEvents;

    currentCapBank->setIgnoreFlag(true);
    currentSubstationBus->setBusUpdatedFlag(true);

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
    {
        afterVarsString += currentCapBank->getControlStatusText();
    }
    text1 += afterVarsString;
    currentCapBank->setAfterVarsString(afterVarsString);
    currentCapBank->setPercentChangeString(" Rejection by " +twoWayPts.getIgnoredControlText());

    text1 += " command-";
    text1 += twoWayPts.getIgnoredControlText();

    if ( twoWayPts.controlRejectedByVoltageLimits() )
    {
        const long controlStatus = currentCapBank->getControlStatus();

        if ( controlStatus == CtiCCCapBank::Open ||
             controlStatus == CtiCCCapBank::OpenPending ||
             controlStatus == CtiCCCapBank::Close ||
             controlStatus == CtiCCCapBank::ClosePending )
        {
            if ( twoWayPts.checkDeltaVoltageRejection() )
            {
                currentCapBank->setPercentChangeString(" Rejection by Delta Voltage ");
                text1 += " Delta Voltage";
            }
        }
    }

    text1 += "! Adjusting state, ";
    currentCapBank->setControlStatus(twoWayPts.getPointValueByAttribute( Attribute::ControlPoint ));
    currentCapBank->setReportedCBCState(twoWayPts.getPointValueByAttribute( Attribute::ControlPoint ));
    text1 += currentCapBank->getControlStatusText();
    currentCapBank->setControlStatusQuality(CC_NoControl);

    currentSubstationBus->checkAndUpdateRecentlyControlledFlag();
    string userName = "cap control";
    if (currentCapBank->getVerificationFlag())
    {
        currentCapBank->setPorterRetFailFlag(true);
        userName += " verification";
        currentCapBank->setPercentChangeString(" verify set to CBC state ");
    }

    sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE), CALLSITE);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

    ccEvents.push_back( EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, userName, 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));


    if( currentCapBank->getOperationAnalogPointId() > 0 )
    {
        if (currentCapBank->getTotalOperations() > 0)
            currentCapBank->setTotalOperations( currentCapBank->getTotalOperations() - 1);
        if (currentCapBank->getCurrentDailyOperations() > 0)
            currentCapBank->setCurrentDailyOperations( currentCapBank->getCurrentDailyOperations() - 1);
        getDispatchConnection()->WriteConnQue(
            new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(), currentCapBank->getTotalOperations(), NormalQuality, 
            AnalogPointType, "Command Refused, Forced ccServer Update", TAG_POINT_FORCE_UPDATE), CALLSITE);
        ccEvents.push_back( EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "Command Refused, CapBank opCount adjustment", userName, 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));
    }

    if ( currentFeeder->getDailyOperationsAnalogPointId() > 0 )
    {
        if ( currentFeeder->getCurrentDailyOperations() > 0 )
        {
            currentFeeder->setCurrentDailyOperations( currentFeeder->getCurrentDailyOperations() - 1 );
            getDispatchConnection()->WriteConnQue( new CtiPointDataMsg( currentFeeder->getDailyOperationsAnalogPointId(),
                                                                        currentFeeder->getCurrentDailyOperations(),
                                                                        NormalQuality,
                                                                        AnalogPointType,
                                                                        "Command Refused, Forced ccServer Update",
                                                                        TAG_POINT_FORCE_UPDATE), CALLSITE);
            ccEvents.push_back( EventLogEntry( 0,
                                               currentFeeder->getDailyOperationsAnalogPointId(),
                                               spAreaId,
                                               areaId,
                                               stationId,
                                               currentSubstationBus->getPaoId(),
                                               currentFeeder->getPaoId(),
                                               capControlSetOperationCount,
                                               currentSubstationBus->getEventSequence(),
                                               currentFeeder->getCurrentDailyOperations(),
                                               "Command Refused, Feeder opCount adjustment",
                                               userName ) );
        }
    }

    if ( currentSubstationBus->getDailyOperationsAnalogPointId() > 0 &&
         currentSubstationBus->getCurrentDailyOperations() > 0 )
    {
        currentSubstationBus->setCurrentDailyOperations( currentSubstationBus->getCurrentDailyOperations() - 1 );

        getDispatchConnection()->WriteConnQue(
            new CtiPointDataMsg( currentSubstationBus->getDailyOperationsAnalogPointId(),
                                 currentSubstationBus->getCurrentDailyOperations(),
                                 NormalQuality,
                                 AnalogPointType,
                                 "Command Refused, Forced ccServer Update",
                                 TAG_POINT_FORCE_UPDATE), CALLSITE);

        ccEvents.push_back(
            EventLogEntry( 0,
                           currentSubstationBus->getDailyOperationsAnalogPointId(),
                           spAreaId,
                           areaId,
                           stationId,
                           currentSubstationBus->getPaoId(),
                           currentFeeder->getPaoId(),
                           capControlSetOperationCount,
                           currentSubstationBus->getEventSequence(),
                           currentSubstationBus->getCurrentDailyOperations(),
                           "Command Refused, SubBus opCount adjustment",
                           userName ) );
    }

    enqueueEventLogEntries(ccEvents);

    currentCapBank->setLastStatusChangeTime(CtiTime());
    currentCapBank->setControlRecentlySentFlag(false);
}
void CtiCapController::handleUnsolicitedMessaging(CtiCCCapBankPtr currentCapBank, CtiCCFeederPtr currentFeeder,
                                                  CtiCCSubstationBusPtr currentSubstationBus, CtiCCTwoWayPoints & twoWayPts)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    EventLogEntries ccEvents;

    currentSubstationBus->setBusUpdatedFlag(true);
    string text = string("CBC Unsolicited Event!");
    string text1 = string("Var:");
    string beforeVarsString = string(" CBC Unsolicited ");
    beforeVarsString += currentCapBank->getControlStatusText();

    text1 += beforeVarsString;
    text1 += "-"+twoWayPts.getLastControlText();

    currentCapBank->setBeforeVarsString(beforeVarsString);
    currentCapBank->setAfterVarsString(twoWayPts.getLastControlText());
    currentCapBank->setPercentChangeString(" --- ");

    text1 += "! Adjusting CapBank state,  ";
    text1 += currentCapBank->getControlStatusText();

    string opText = currentCapBank->getControlStatusText();

    // Yuck!
    const bool isStandardOp = ( opText == "Open" || opText == "Close" );

    opText += ", CBC Local Change";
    currentCapBank->setControlStatusQuality(CC_UnSolicited);

    sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE), CALLSITE);

    //send the cceventmsg.
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    currentCapBank->setActionId( CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);

    EventLogEntry logentry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId,
                           currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent,
                           currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(),
                           opText, "cap control", 0, 0, 0, currentCapBank->getIpAddress(),
                           currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString());
    if ( isStandardOp )
    {
        logentry.setEventSubtype( StandardOperation );
    }
    ccEvents.push_back( logentry );

    ccEvents.push_back( EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, "cap control", 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));

    enqueueEventLogEntries(ccEvents);

    currentCapBank->setUnsolicitedPendingFlag(false);
    currentCapBank->setIgnoreFlag(false);
    //store->removeCapbankFromUnsolicitedCapBankList(currentCapBank);

}



void CtiCapController::handleUnexpectedUnsolicitedMessaging(CtiCCCapBankPtr currentCapBank, CtiCCFeederPtr currentFeeder,
                                                  CtiCCSubstationBusPtr currentSubstationBus, CtiCCTwoWayPoints & twoWayPts)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    currentSubstationBus->setBusUpdatedFlag(true);
    {
        CTILOG_INFO(dout, "CBC reporting unexpected state change of: "<<twoWayPts.getPointValueByAttribute( Attribute::ControlPoint ) <<" CAPBANK: "<<
        currentCapBank->getPaoName() << " is "<< currentCapBank->getControlStatusText() <<
        " waiting for VAR change.");
    }

    string text = string("CBC Unsolicited Event!");
    string text1 = string("CBC State Change. ");
    string beforeVarsString = string("CBC Unsolicited-Unexpected ");
    beforeVarsString += (twoWayPts.getPointValueByAttribute( Attribute::ControlPoint ) == 0 ? "Open" : "Close");

    text1 += beforeVarsString;
    text1 += ", "+ twoWayPts.getLastControlText() + "! Mismatch Likely.";

    currentCapBank->setBeforeVarsString(beforeVarsString);
    currentCapBank->setAfterVarsString(twoWayPts.getLastControlText());
    currentCapBank->setPercentChangeString(" --- ");


    //send the cceventmsg.
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    enqueueEventLogEntry( EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlUnexpectedCBCStateReported, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text1, "cap control", 0, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), currentCapBank->getControlStatusQualityString()));

    currentCapBank->setUnsolicitedPendingFlag(false);
    currentCapBank->setIgnoreFlag(false);

}



/*---------------------------------------------------------------------------
    signalMessage

    Handles signal messages and updates substation bus tags.
---------------------------------------------------------------------------*/
void CtiCapController::signalMsg( const CtiSignalMsg & signal )
{
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        Cti::FormattedList list;

        list << "Signal Message received";
        list.add("Point ID")        << signal.getId();
        list.add("Tags")            << signal.getTags();
        list.add("Text")            << signal.getText();
        list.add("Additional Info") << signal.getAdditionalInfo();
        list.add("Message Time")    << signal.getMessageTime();

        CTILOG_DEBUG(dout, list);
    }
}

/*---------------------------------------------------------------------------
    sendMessageToDispatch

    Sends a cti message to dispatch, this is a way for other threads to use
    the CtiCapController's connection to dispatch.
---------------------------------------------------------------------------*/
void CtiCapController::sendMessageToDispatch( CtiMessage* message, Cti::CallSite cs )
{
    try
    {
        getDispatchConnection()->WriteConnQue(message, cs);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            getPorterConnection()->WriteConnQue(pilRequest, CALLSITE);
        }

        if (multiMsg != NULL)
        {
            if (multiMsg->getCount() > 0)
            {
                getDispatchConnection()->WriteConnQue(multiMsg, CALLSITE);
            }
            else
            {
                delete multiMsg;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCapController::sendCapBankRequestAndPoints( std::unique_ptr<CtiRequestMsg> pilRequest, CtiMultiMsg* multiMsg)
{
    getInstance()->manualCapBankControl(pilRequest.release(), multiMsg);
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
                getPorterConnection()->WriteConnQue(pilMultiMsg, CALLSITE);
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
                getDispatchConnection()->WriteConnQue(multiMsg, CALLSITE);
            }
            else
            {
                delete multiMsg;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


CtiConnection::Que_t CtiCapController::_inClientMsgQueue;

CtiConnection::Que_t& CtiCapController::getInClientMsgQueueHandle()
{
    return _inClientMsgQueue;
}

CtiPCPtrQueue< CtiMessage > &CtiCapController::getOutClientMsgQueueHandle()
{
    return _outClientMsgQueue;
}

void CtiCapController::submitEventLogEntry(const Cti::CapControl::EventLogEntry &event)
{
    getInstance()->enqueueEventLogEntry(event);
}

void CtiCapController::submitEventLogEntries(const EventLogEntries &events)
{
    getInstance()->enqueueEventLogEntries(events);
}

void CtiCapController::enqueueEventLogEntry(const Cti::CapControl::EventLogEntry &event)
{
    _eventLogs.putQueue(event);
}

void CtiCapController::enqueueEventLogEntries(const EventLogEntries &events)
{
    for each( const Cti::CapControl::EventLogEntry &event in events )
    {
        enqueueEventLogEntry(event);
    }
}

void CtiCapController::analyzeVerificationBusIvvc( CtiCCSubstationBusPtr currentSubstationBus,
                                                   const CtiTime &       currentDateTime,
                                                   CtiMultiMsg_vec &     pointChanges,
                                                   EventLogEntries &     ccEvents,
                                                   CtiMultiMsg_vec &     pilMessages,
                                                   CtiMultiMsg_vec &     capMessages )
{
    struct LocalScanState
    {
        enum class State
        {
            EntryPoint,
            PostControlWait,
            PostOpScanWait,
            PreOpScanWait
        };

        enum class Control
        {
            ThisBank,
            NextBank
        };

        State   state;
        Control next;
        CtiTime scanTime;
        CtiTime expirationTime;
    };

    static bool showOncePerVerification = true;

    if ( showOncePerVerification
            && _ALLOW_PARALLEL_TRUING
            && ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder
                || currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder ) )
    {
        showOncePerVerification = false;

        CTILOG_INFO( dout, "Parallel feeder verification (CAP_CONTROL_ALLOW_PARALLEL_TRUING) is unsupported for IVVC Bank Verification on bus: "
                                            << currentSubstationBus->getPaoName() );
    }

    const long busID = currentSubstationBus->getPaoId();

    currentSubstationBus->setLastVerificationCheck(currentDateTime);

    try
    {
        if (currentSubstationBus->isBusPerformingVerification())
        {
            static std::map<long, LocalScanState>   states;

            switch ( states[ busID ].state )
            {
                default:
                case LocalScanState::State::EntryPoint:
                {
                    if (currentSubstationBus->isVerificationAlreadyControlled() ||
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
                                    currentSubstationBus->setBusUpdatedFlag(true);
                            }
                            catch(...)
                            {
                                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                            }
                        }
                        else if(  currentSubstationBus->getWaitForReCloseDelayFlag() ||
                                ( !currentSubstationBus->capBankVerificationStatusUpdate(pointChanges, ccEvents)  &&
                                   currentSubstationBus->getCurrentVerificationCapBankId() != -1)  )
                        {
                            states[ busID ].state = LocalScanState::State::PostControlWait;
                            states[ busID ].next = LocalScanState::Control::ThisBank;
                            states[ busID ].expirationTime = currentDateTime;
                            states[ busID ].expirationTime.addSeconds( _POST_CONTROL_WAIT );
                        }
                        else
                        {
                            states[ busID ].state = LocalScanState::State::PostControlWait;
                            states[ busID ].next = LocalScanState::Control::NextBank;
                            states[ busID ].expirationTime = currentDateTime;
                            states[ busID ].expirationTime.addSeconds( _POST_CONTROL_WAIT );
                        }
                    }
                    break;
                }
                case LocalScanState::State::PostControlWait:
                {
                    if ( currentDateTime < states[ busID ].expirationTime )
                    {
                        break;      // waiting...
                    }

                    // _POST_CONTROL_WAIT reached, now scan for post-op voltages for delta voltage calculations

                    states[ busID ].state = LocalScanState::State::PostOpScanWait;
                    states[ busID ].scanTime = currentDateTime;
                    states[ busID ].expirationTime = currentDateTime;
                    states[ busID ].expirationTime.addMinutes( _SCAN_WAIT_EXPIRE );

                    CTILOG_DEBUG( dout, "Performing VERIFICATION Post-Op Scan on bus: " << currentSubstationBus->getPaoName() );

                    currentSubstationBus->scanAllMonitorPoints();
                }
                case LocalScanState::State::PostOpScanWait:
                {
                    if ( ! currentSubstationBus->areCapbankMonitorPointsNewerThan( states[ busID ].scanTime ) 
                         && ( currentDateTime < states[ busID ].expirationTime ) )
                    {
                        break;      // waiting...
                    }

                    // clear remaining scanInProgress flags on the bus

                    currentSubstationBus->clearMonitorPointsScanInProgress();
                                         
                    // update delta voltages

                    currentSubstationBus->updatePointResponseDeltas();

                    // issue new scan requests for new pre-op voltages

                    states[ busID ].state = LocalScanState::State::PreOpScanWait;
                    states[ busID ].scanTime = currentDateTime;
                    states[ busID ].expirationTime = currentDateTime;
                    states[ busID ].expirationTime.addMinutes( _SCAN_WAIT_EXPIRE );

                    CTILOG_DEBUG( dout, "Performing VERIFICATION Pre-Op Scan on bus: " << currentSubstationBus->getPaoName() );

                    currentSubstationBus->scanAllMonitorPoints();
                }
                case LocalScanState::State::PreOpScanWait:
                {
                    if ( ! currentSubstationBus->areCapbankMonitorPointsNewerThan( states[ busID ].scanTime ) 
                         && ( currentDateTime < states[ busID ].expirationTime ) )
                    {
                        break;      // waiting...
                    }

                    // clear remaining scanInProgress flags on the bus

                    currentSubstationBus->clearMonitorPointsScanInProgress();

                    try
                    {
                        if ( states[ busID ].next == LocalScanState::Control::ThisBank )
                        {
                            // issue return to original state control on this bank

                            if (currentSubstationBus->sendNextCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages))
                            {
                                currentSubstationBus->setWaitForReCloseDelayFlag(false);
                            }
                            else
                            {
                                currentSubstationBus->setWaitForReCloseDelayFlag(true);
                            }
                        }
                        else    // states[ busID ].next == LocalScanState::Control::NextBank
                        {
                            // issue control for next bank if applicable

                            if(currentSubstationBus->areThereMoreCapBanksToVerify(ccEvents))
                            {
                                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                {
                                        CTILOG_DEBUG(dout, "CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPaoName()<< "( "<<currentSubstationBus->getPaoId()<<" ) CB-"<<currentSubstationBus->getCurrentVerificationCapBankId());
                                }

                                currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                            }
                            else
                            {
                                showOncePerVerification = true;

                                //reset VerificationFlag
                                currentSubstationBus->setVerificationFlag(false);
                                currentSubstationBus->setBusUpdatedFlag(true);
                                capMessages.push_back(new VerifyBanks(currentSubstationBus->getPaoId(),currentSubstationBus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
                                capMessages.push_back(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()));
                                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                                {
                                   CTILOG_DEBUG(dout, "DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName()<< "( "<<currentSubstationBus->getPaoId()<<" ) ");
                                }
                            }
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }

                    states.erase( busID );      // reset state machine
                    break;
                }
            }
        }
        else
        {
            try
            {
                static std::map<long, LocalScanState>   localState;

                switch ( localState[ busID ].state )
                {
                    case LocalScanState::State::EntryPoint:     // issue scans...
                    {
                        localState[ busID ].state = LocalScanState::State::PreOpScanWait;
                        localState[ busID ].expirationTime.addMinutes( _SCAN_WAIT_EXPIRE );

                        CTILOG_DEBUG( dout, "Performing VERIFICATION Pre-Op Scan on bus: " << currentSubstationBus->getPaoName() );

                        currentSubstationBus->scanAllMonitorPoints();
                        return;
                    }
                    default:
                    case LocalScanState::State::PreOpScanWait:  // wait for up to _SCAN_WAIT_EXPIRE minutes for new data
                    {
                        if ( ! currentSubstationBus->areCapbankMonitorPointsNewerThan( localState[ busID ].scanTime ) 
                             && ( currentDateTime < localState[ busID ].expirationTime ) )
                        {
                            return;     // no -- keep waiting
                        }
                    }
                }

                // clear remaining scanInProgress flags on the bus

                currentSubstationBus->clearMonitorPointsScanInProgress();

                // falling through the switch means its time to do the verification
                //  we don't need the LocalScanState for this bus anymore

                localState.erase( busID );

                if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                {
                   CTILOG_DEBUG(dout, "Performing VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName());
                }
                int strategy = (long)currentSubstationBus->getVerificationStrategy();

                currentSubstationBus->setCapBanksToVerifyFlags(strategy, ccEvents);

                if(currentSubstationBus->areThereMoreCapBanksToVerify(ccEvents))
                {
                    try
                    {
                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                        {
                             CTILOG_DEBUG(dout, "CAP BANK VERIFICATION LIST:  SUB-" << currentSubstationBus->getPaoName()<<" CB-"<<currentSubstationBus->getCurrentVerificationCapBankId());
                        }
                        currentSubstationBus->startVerificationOnCapBank(currentDateTime, pointChanges, ccEvents, pilMessages);
                        currentSubstationBus->setBusUpdatedFlag(true);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                else
                {
                    try
                    {
                        showOncePerVerification = true;

                        //reset VerificationFlag
                        currentSubstationBus->setVerificationFlag(false);
                        currentSubstationBus->setBusUpdatedFlag(true);
                        capMessages.push_back(new VerifyBanks(currentSubstationBus->getPaoId(),currentSubstationBus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
                        capMessages.push_back(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()));

                        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
                        {
                           CTILOG_DEBUG(dout, "DISABLED VERIFICATION ON: subBusID: "<<currentSubstationBus->getPaoName());
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCapController::registerPointIDsForPointUpdates( const std::set<long> & pointIDs )
{
    _registry.addPoints( pointIDs );
}

void CtiCapController::unregisterPointIDsForPointUpdates( const std::set<long> & pointIDs )
{
    _registry.removePoints( pointIDs );
}

void CtiCapController::registerPaoForPointUpdates( const CapControlPao & pao )
{
    registerPointIDsForPointUpdates( pao.getPointRegistrationIds() );
}

void CtiCapController::unregisterPaoForPointUpdates( const CapControlPao & pao )
{
    unregisterPointIDsForPointUpdates( pao.getPointRegistrationIds() );
}

