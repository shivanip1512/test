#include "precompiled.h"

#include "dbaccess.h"
#include "connection.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_signal.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_dbchg.h"
#include "pointtypes.h"
#include "loadmanager.h"
#include "lmcontrolareastore.h"
#include "lmcontrolareatrigger.h"
#include "tbl_lmprogramhistory.h"
#include "executor.h"
#include "dllbase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "lmprogramdirect.h"
#include "clistener.h"
#include "utility.h"
#include "debug_timer.h"
#include "module_util.h"
#include "amq_constants.h"
#include "thread_monitor.h"
#include "GlobalSettings.h"
#include "logManager.h"
#include "ServiceMetricReporter.h"
#include "MessageCounter.h"
#include "LMScheduledMessageHolder.h"

#include <time.h>
#include <map>
#include <set>
#include <queue>

using namespace std;

extern ULONG _LM_DEBUG;
extern BOOL _LM_POINT_EVENT_LOGGING;

set<long> _CHANGED_GROUP_LIST;
set<long> _CHANGED_CONTROL_AREA_LIST;
set<long> _CHANGED_PROGRAM_LIST;
LMScheduledMessageHolder gScheduledStopMessages;

unsigned int _HISTORY_PROGRAM_ID = 0;
unsigned int _HISTORY_GROUP_ID = 0;

queue<CtiTableLMProgramHistory> _PROGRAM_HISTORY_QUEUE;

/* The singleton instance of CtiLoadManager */
CtiLoadManager* CtiLoadManager::_instance = NULL;

/*---------------------------------------------------------------------------
  getInstance

  Returns the single instance of CtiLoadManager
  ---------------------------------------------------------------------------*/
CtiLoadManager* CtiLoadManager::getInstance()
{
    if( _instance == NULL )
        _instance = CTIDBG_new CtiLoadManager();

    return _instance;
}


/*---------------------------------------------------------------------------
  Constructor

  Private to ensure that only one CtiLoadManager is available through the
  instance member function
  ---------------------------------------------------------------------------*/
CtiLoadManager::CtiLoadManager()
: control_loop_delay(500), control_loop_inmsg_delay(0), control_loop_outmsg_delay(0)
{
}

/*---------------------------------------------------------------------------
  Destructor

  Private to ensure that the single instance of CtiLoadManager is not
  deleted
  ---------------------------------------------------------------------------*/
CtiLoadManager::~CtiLoadManager()
{
}

/*---------------------------------------------------------------------------
  start

  Starts the controller thread
  ---------------------------------------------------------------------------*/
void CtiLoadManager::start()
{
    CTILOG_DEBUG(dout, "Starting");

    _loadManagerThread = boost::thread( &CtiLoadManager::controlLoop, this );

    CTILOG_DEBUG(dout, "Running");
}

/*---------------------------------------------------------------------------
  stop

  Stops the controller thread
  ---------------------------------------------------------------------------*/
void CtiLoadManager::stop()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Shutting down load manager thread...");
    }

    try
    {
        _loadManagerThread.interrupt();

        if (!_loadManagerThread.timed_join(boost::posix_time::seconds(20))) 
        {
            CTILOG_WARN( dout, "Load Manager thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _loadManagerThread.native_handle(), EXIT_SUCCESS );
        }
    }
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Load manager thread shutdown.");
    }

    try
    {
        CtiLockGuard<CtiCriticalSection>  guard(_mutex);
        if( _dispatchConnection.get() != NULL && _dispatchConnection->valid() )
        {
            _dispatchConnection->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
        }
        _dispatchConnection.reset(); // Note this delete will block for the message above to go out.
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        CtiLockGuard<CtiCriticalSection>  guard(_mutex);
        if( _porterConnection.get() != NULL && _porterConnection->valid() )
        {
            _porterConnection->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
        }
        _porterConnection.reset(); // Note this delete will block for the message above to go out.
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        CtiLockGuard<CtiCriticalSection>  guard(_mutex);
        if( _notificationConnection.get()!=NULL && _notificationConnection->valid() )
        {
            _notificationConnection->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
        }
        _notificationConnection.reset(); // Note this delete will block for the message above to go out.
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


/*---------------------------------------------------------------------------
  controlLoop

  Decides when to control banks, update statuses, and send messages to
  other related applications (server programs: pil, dispatch;
  client: load management client in TDC).
  --------------------------------------------------------------------------*/
void CtiLoadManager::controlLoop()
{

    try
    {
        CTILOG_INFO(dout, "LoadManager Thread started");

        CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
        {
            registerForPoints(*store->getControlAreas(CtiTime()));
            store->setReregisterForPoints(false);
        }

        CtiTime currentDateTime;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg      = new CtiMultiMsg();
        CtiMultiMsg* multiNotifMsg    = new CtiMultiMsg();

        CtiLMExecutorFactory executorFactory;

        ThreadMonitor.start(CtiThreadMonitor::LoadManager);

        long pointID = ThreadMonitor.getProcessPointID();
        Cti::ServiceMetrics::MetricReporter metricReporter {
            Cti::ServiceMetrics::CpuPointOffsets::LoadManager,
            Cti::ServiceMetrics::MemoryPointOffsets::LoadManager,
            "LoadManagement",
            LOAD_MANAGEMENT_APPLICATION_NAME };

        CtiTime NextThreadMonitorReportTime;
        CtiTime nextCPULoadReportTime;

        //remember when the last control area messages were sent
        time_t last_ca_msg_sent = 0;

        const int HOURLY_SCHEDULE = 3600;
        CtiTime next_dynamic_data_store_time = nextScheduledTimeAlignedOnRate(CtiTime::now(), HOURLY_SCHEDULE);

        loadControlLoopCParms();

        // Fire up the notification server
        getNotificationConnection();

        Cti::MessageCounter mc("Load Control Client Listener");

        while( TRUE )
        {
            long main_wait = control_loop_delay;
            bool received_message = false;

            boost::this_thread::sleep( boost::posix_time::milliseconds( 250 ) );

            while( CtiMessage *msg = CtiLMClientListener::getInstance().getQueue(main_wait) )
            {
                mc.increment();

                CtiLMExecutor* executor = executorFactory.createExecutor(msg);
                try
                {
                    executor->Execute();
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "executor failed in main thread");
                }
                delete executor;
                //Shorten how long to wait in case a message was processed to improve response time
                main_wait = control_loop_inmsg_delay;
                received_message = true;
            }

            {
                CtiTime prevDateTime = currentDateTime;
                currentDateTime = CtiTime();
                LONG secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();

                dout->poke();  //  called 4x/second (see sleep at top of loop)

                if(pointID != 0)
                {
                    if(CtiTime::now() > NextThreadMonitorReportTime)
                    {
                        NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                        getDispatchConnection()->WriteConnQue(
                            CTIDBG_new CtiPointDataMsg( pointID, ThreadMonitor.getState(), NormalQuality,
                            StatusPointType, ThreadMonitor.getString().c_str() ), CALLSITE );
                    }
                }

                metricReporter.reportCheck(CompileInfo, *getDispatchConnection());

                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    if( (currentDateTime.seconds()%1800) == 0 )//every five minutes tell the user if the manager thread is still alive
                    {
                        CTILOG_INFO(dout, "Load Manager thread pulse");
                    }
                }

                boost::this_thread::interruption_point();

                vector<CtiLMControlArea*>& controlAreas = *store->getControlAreas(currentDateTime);

                try
                {
                    if( store->getReregisterForPoints() )
                    {
                        registerForPoints(controlAreas);
                        store->setReregisterForPoints(false);
                    }
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    checkDispatch(currentDateTime);
                    checkPorter(currentDateTime);
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                BOOL examinedControlAreaForControlNeededFlag = FALSE;

                CtiTime controlAreaStart;
                for( LONG i=0;i<controlAreas.size();i++ )
                {
                    CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

                    try
                    {
                        currentControlArea->handleNotification(currentDateTime, multiNotifMsg);

                        if( currentControlArea->isManualControlReceived() )
                        {
                            currentControlArea->handleManualControl(currentDateTime, multiPilMsg,multiDispatchMsg, multiNotifMsg);
                        }

                        if( !currentControlArea->getDisableFlag() ) // how about control area windows?
                        {
                            currentControlArea->updateTimedPrograms(secondsFromBeginningOfDay);
                            currentControlArea->handleTimeBasedControl(currentDateTime, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg, multiNotifMsg);
                        }

                        if( !currentControlArea->getDisableFlag() && currentControlArea->isControlTime(secondsFromBeginningOfDay) )
                        {
                            if( currentControlArea->isTriggerCheckNeeded(currentDateTime) )
                            {
                                // Do we need to reduce load?
                                DOUBLE loadReductionNeeded = currentControlArea->calculateLoadReductionNeeded();
                                if( loadReductionNeeded > 0.0 &&
                                    currentControlArea->isPastMinResponseTime(currentDateTime) )
                                {
                                    if( currentControlArea->getControlAreaState() != CtiLMControlArea::FullyActiveState )
                                    {
                                        CTILOG_INFO(dout, "Attempting to reduce load in control area: " << currentControlArea->getPAOName() << ".");
                                        if( currentControlArea->getControlInterval() != 0 ||
                                            currentControlArea->isThresholdTriggerTripped() )
                                        {
                                            currentControlArea->reduceControlAreaLoad(loadReductionNeeded,secondsFromBeginningOfDay,currentDateTime,multiPilMsg,multiDispatchMsg, multiNotifMsg);
                                        }
                                        else
                                        {
                                            //Special Case: if only a status trigger is tripped and the control interval is 0,
                                            //then we need to start all the programs as if they were started manually.
                                            currentControlArea->manuallyStartAllProgramsNow(secondsFromBeginningOfDay,currentDateTime,multiPilMsg,multiDispatchMsg, multiNotifMsg);
                                            // Potentially this control area is ready for manual control
                                            // If so lets do it now otherwise we have to wait for the top
                                            // of the main loop which could be a while
                                            if( currentControlArea->isManualControlReceived() )
                                            {
                                                currentControlArea->handleManualControl(currentDateTime, multiPilMsg,multiDispatchMsg, multiNotifMsg);
                                            }
                                        }

                                        //currentControlArea->setUpdatedFlag(TRUE);
                                    }
                                    else if( currentControlArea->isThresholdTriggerTripped() )
                                    {
                                        //all load reducing programs are currently running
                                        //can not reduce any more demand
                                        CTILOG_INFO(dout, "All load reducing programs are currently running for control area: " << currentControlArea->getPAOName() << " can not reduce any more load.");
                                    }
                                }
                                else
                                {
                                    if (currentControlArea->getControlInterval() == 0 &&
                                        currentControlArea->getControlAreaState() == CtiLMControlArea::InactiveState &&
                                        currentControlArea->hasStatusTrigger() &&
                                        !currentControlArea->isStatusTriggerTripped() )
                                    {
                                        currentControlArea->manuallyStopAllProgramsNow(secondsFromBeginningOfDay,currentDateTime,multiPilMsg,multiDispatchMsg, multiNotifMsg, false);
                                        currentControlArea->clearManualControlReceivedFlags();
                                    }
                                }

                                // See if we can restore some load
                                // The idea here is to stop some control
                                // First, if any programs are below their program restore offset take them first
                                // Second, if none of the programs stopped because of their restore offsets, go by stop priorities
                                if( currentControlArea->getControlAreaState() != CtiLMControlArea::InactiveState &&
                                    currentControlArea->isPastMinResponseTime(currentDateTime) )
                                {
                                    if( currentControlArea->stopProgramsBelowThreshold(currentDateTime, multiPilMsg, multiDispatchMsg, multiNotifMsg) )
                                    {
                                        //don't have to do anything, just don't take any stop priorities
                                    }
                                    else if( currentControlArea->hasStatusTrigger() &&
                                             !currentControlArea->isStatusTriggerTripped() &&
                                             (!currentControlArea->hasThresholdTrigger() ||
                                              (!currentControlArea->getRequireAllTriggersActiveFlag() && !currentControlArea->isThresholdTriggerTripped())) &&
                                             currentControlArea->getControlInterval() == 0 )   // Only stop them manually if there is no control interval!
                                    {
                                        currentControlArea->manuallyStopAllProgramsNow(secondsFromBeginningOfDay, currentDateTime, multiPilMsg, multiDispatchMsg, multiNotifMsg, true);
                                        // Potentially this control area is ready for manual control
                                        // If so lets do it now otherwise we have to wait for the top
                                        // of the main loop which could be a while
                                        if( currentControlArea->isManualControlReceived() )
                                        {
                                            currentControlArea->handleManualControl(currentDateTime, multiPilMsg,multiDispatchMsg, multiNotifMsg);
                                        }
                                    }
                                    else if( currentControlArea->shouldReduceControl() )
                                    {
                                        currentControlArea->reduceControlAreaControl(currentDateTime, multiPilMsg, multiDispatchMsg, multiNotifMsg);
                                    }
                                }

                                if( currentControlArea->getControlInterval() == 0 )
                                {
                                    currentControlArea->setNewPointDataReceivedFlag(FALSE);
                                }
                                else
                                {
                                    currentControlArea->figureNextCheckTime(currentDateTime);
                                }
                                examinedControlAreaForControlNeededFlag = TRUE;
                            }

                            //This ends up refreshing any control necessary
                            if( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                                currentControlArea->getControlAreaState() == CtiLMControlArea::PartiallyActiveState )
                            {
                                if( currentControlArea->maintainCurrentControl(secondsFromBeginningOfDay,currentDateTime,multiPilMsg,multiDispatchMsg,multiNotifMsg,examinedControlAreaForControlNeededFlag) )
                                {
                                    currentControlArea->setUpdatedFlag(TRUE);
                                }
                            }

                            if( currentControlArea->getControlAreaState() == CtiLMControlArea::AttemptingControlState &&
                                !currentControlArea->isControlStillNeeded() )
                            {
                                currentControlArea->setControlAreaState(CtiLMControlArea::InactiveState);
                            }
                            examinedControlAreaForControlNeededFlag = FALSE;
                        }
                        else if( !currentControlArea->isControlTime(secondsFromBeginningOfDay) &&
                                 (currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                                  currentControlArea->getControlAreaState() == CtiLMControlArea::PartiallyActiveState) )
                        {
                            if( currentControlArea->stopAllControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentDateTime) )
                            {
                                CTILOG_INFO(dout, "Left controllable time window in control area: " << currentControlArea->getPAOName() << ", stopping all control.");
                                currentControlArea->setUpdatedFlag(TRUE);
                            }
                        }
                    }
                    catch( ... )
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                CtiTime controlAreaStop;

                if( _LM_DEBUG & LM_DEBUG_TIMING && controlAreaStop.seconds() - controlAreaStart.seconds() > 2 )
                {
                    CTILOG_DEBUG(dout, "Control area loop took: "
                         << controlAreaStop.seconds() - controlAreaStart.seconds() << " seconds " );
                }

                //Send out any scheduled stop messages that are ready to go
                while( auto message = gScheduledStopMessages.getAvailableMessage(CtiTime::now()) )
                {
                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CTILOG_INFO(dout, "Sending delayed message Unique id " << message->OptionsField() <<  " Groupid " << message.get()->DeviceId());
                    }
                    
                    multiPilMsg->insert(message.release());
                }

                try
                {
                    if( multiDispatchMsg->getCount() > 0 )
                    {
                        multiDispatchMsg->resetTime();                              // CGP 5/21/04 Update its time to current time.
                        getDispatchConnection()->WriteConnQue(multiDispatchMsg, CALLSITE);
                        multiDispatchMsg = CTIDBG_new CtiMultiMsg();
                    }
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    if( multiPilMsg->getCount() > 0 )
                    {
                        multiPilMsg->setMessagePriority(13);
                        multiPilMsg->resetTime();                       // CGP 5/21/04 Update its time to current time.
                        getPorterConnection()->WriteConnQue(multiPilMsg, CALLSITE);
                        multiPilMsg = CTIDBG_new CtiMultiMsg();
                    }
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    if( multiNotifMsg->getCount() > 0 )
                    {
                        multiNotifMsg->setMessagePriority(13);
                        multiNotifMsg->resetTime();                       // CGP 5/21/04 Update its time to current time.
                        getNotificationConnection()->WriteConnQue(multiNotifMsg, CALLSITE);
                        multiNotifMsg = CTIDBG_new CtiMultiMsg();
                    }
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                try
                {
                    // Only send control area changes so often to avoid overwhelming the system
                    // if we just received a client message then do it anyways however for good response
                    time_t now = std::time(NULL);
                    if( received_message || now > (last_ca_msg_sent + (control_loop_outmsg_delay/1000.0)) ) /* delay is in millis */
                    {
                        typedef set<long>::iterator ChangeListIter;
                        CtiMultiMsg *multi = CTIDBG_new CtiMultiMsg();
                        int tempCount = 0;

                        for( ChangeListIter changeIter = _CHANGED_GROUP_LIST.begin(); changeIter != _CHANGED_GROUP_LIST.end(); changeIter++ )
                        {
                            CtiLMGroupPtr tempGroup = store->getLMGroup(*changeIter);

                            if( tempGroup )
                            {
                                multi->insert(CTIDBG_new CtiLMDynamicGroupDataMsg(tempGroup));
                                tempCount++;
                            }
                            else
                            {
                                CTILOG_INFO(dout, "No Group Found for group in change list! " << *changeIter);
                            }
                        }

                        if( _LM_DEBUG & LM_DEBUG_CLIENT && tempCount > 0 )
                        {
                            CTILOG_DEBUG(dout, "Found " << tempCount << " dirty dynamic groups to send to clients");
                        }
                        tempCount = 0;

                        for( ChangeListIter changeIter = _CHANGED_PROGRAM_LIST.begin(); changeIter != _CHANGED_PROGRAM_LIST.end(); changeIter++ )
                        {
                            CtiLMProgramBaseSPtr tempProgram = store->getLMProgram(*changeIter);

                            if( tempProgram )
                            {
                                multi->insert(CTIDBG_new CtiLMDynamicProgramDataMsg(boost::static_pointer_cast<CtiLMProgramDirect>(tempProgram)));
                                tempProgram->createControlStatusPointUpdates(multiDispatchMsg);
                                tempCount++;
                            }
                            else
                            {
                                CTILOG_INFO(dout, "No Program Found for program in change list with id: " << *changeIter);
                            }
                        }
                        if( _LM_DEBUG & LM_DEBUG_CLIENT && tempCount > 0 )
                        {
                            CTILOG_DEBUG(dout, "Found " << tempCount << " dirty programs to send to clients");
                        }
                        tempCount = 0;

                        for(ChangeListIter  changeIter = _CHANGED_CONTROL_AREA_LIST.begin(); changeIter != _CHANGED_CONTROL_AREA_LIST.end(); changeIter++ )
                        {
                            CtiLMControlArea* tempControlArea = store->getLMControlArea(*changeIter);

                            if( tempControlArea != NULL )
                            {
                                multi->insert(CTIDBG_new CtiLMDynamicControlAreaDataMsg(tempControlArea));
                                tempControlArea->createControlStatusPointUpdates(multiDispatchMsg);
                                tempCount++;
                            }
                            else
                            {
                                CTILOG_WARN(dout, "No Control Area Found for id in change list ID: " << *changeIter);
                            }
                        }
                        if( _LM_DEBUG & LM_DEBUG_CLIENT && tempCount > 0 )
                        {
                            CTILOG_DEBUG(dout, "Found " << tempCount << " dirty control areas to send to clients");
                        }

                        if( multi->getCount() > 0 )
                        {
                            if( _LM_DEBUG & LM_DEBUG_OUT_MESSAGES )
                            {
                                CTILOG_DEBUG(dout, *multi);
                            }
                            sendMessageToClients(multi);
                            multi = 0;
                        }
                        else
                        {
                            delete multi;
                            multi = 0;
                        }

                        tempCount = 0;
                        while( !_PROGRAM_HISTORY_QUEUE.empty() )
                        {
                            tempCount ++;
                            _PROGRAM_HISTORY_QUEUE.front().Insert();
                            _PROGRAM_HISTORY_QUEUE.pop();
                        }

                        if( _LM_DEBUG & LM_DEBUG_DATABASE && tempCount > 0 )
                        {
                            CTILOG_DEBUG(dout, "Inserted " << tempCount << " history rows");
                        }

                        _CHANGED_CONTROL_AREA_LIST.clear();
                        _CHANGED_PROGRAM_LIST.clear();
                        _CHANGED_GROUP_LIST.clear();
                    }
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }


                if( next_dynamic_data_store_time < CtiTime::now() )
                {
                    next_dynamic_data_store_time = nextScheduledTimeAlignedOnRate(CtiTime::now(), HOURLY_SCHEDULE);
                    CtiLMControlAreaStore::getInstance()->dumpAllDynamicData();
                }
            }

            boost::this_thread::interruption_point();

        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
  Getdispatchconnection

  Returns a connection to Dispatch, initializes if isn't created yet.
  ---------------------------------------------------------------------------*/
boost::shared_ptr<CtiClientConnection> CtiLoadManager::getDispatchConnection()
{
    try
    {
        if( ! _dispatchConnection || ! _dispatchConnection->isConnectionUsable() )
        {
            //Connect to Dispatch
            _dispatchConnection.reset( CTIDBG_new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::dispatch ));
            _dispatchConnection->setName("LM to Dispatch");
            _dispatchConnection->start();

            //Send a registration message to Dispatch
            CtiRegistrationMsg* registrationMsg = CTIDBG_new CtiRegistrationMsg("LoadManagement", 0, false );
            _dispatchConnection->WriteConnQue(registrationMsg, CALLSITE);
        }

        return _dispatchConnection;
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        return boost::shared_ptr<CtiClientConnection>();
    }
}

/*---------------------------------------------------------------------------
  getPorterConnection

  Returns a connection to Porter, initializes if isn't created yet.
  ---------------------------------------------------------------------------*/
boost::shared_ptr<CtiClientConnection> CtiLoadManager::getPorterConnection()
{
    try
    {
        if( ! _porterConnection || ! _porterConnection->isConnectionUsable() )
        {
            //Connect to Porter
            _porterConnection.reset( CTIDBG_new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::porter ));
            _porterConnection->setName("LM to Porter");
            _porterConnection->start();

            //Send a registration message to Porter
            CtiRegistrationMsg* registrationMsg = CTIDBG_new CtiRegistrationMsg("LoadManagement", 0, false );
            _porterConnection->WriteConnQue(registrationMsg, CALLSITE);
        }

        return _porterConnection;
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        return boost::shared_ptr<CtiClientConnection>();
    }
}

/*---------------------------------------------------------------------------
  getNotificationConnection

  Returns a connection to the Notification Server
  ---------------------------------------------------------------------------*/
boost::shared_ptr<CtiClientConnection> CtiLoadManager::getNotificationConnection()
{
    try
    {
        if( ! _notificationConnection || ! _notificationConnection->isConnectionUsable() )
        {
            //Connect to Porter
            _notificationConnection.reset( CTIDBG_new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::notification ));
            _notificationConnection->setName("LM to Notification");
            _notificationConnection->start();
        }

        return _notificationConnection;
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        return boost::shared_ptr<CtiClientConnection>();
    }
}

/*---------------------------------------------------------------------------
  checkDispatch

  Reads off the Dispatch connection and handles messages accordingly.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::checkDispatch(CtiTime currentTime)
{
    bool done = FALSE;
    Cti::MessageCounter mc("Dispatch->LoadControl");

    do
    {
        try
        {
            CtiMessage* in = getDispatchConnection()->ReadConnQue(0);

            if( in != NULL )
            {
                mc.increment();

                parseMessage(in, currentTime);
                delete in;
            }
            else
                done = TRUE;
        }
        catch( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
    while( !done );
}

/*---------------------------------------------------------------------------
  checkPIL

  Reads off the PIL connection and handles messages accordingly.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::checkPorter(CtiTime currentTime)
{
    bool done = FALSE;
    Cti::MessageCounter mc("Porter->LoadControl");

    do
    {
        try
        {
            CtiMessage* in = getPorterConnection()->ReadConnQue(0);

            if( in != NULL )
            {
                mc.increment();

                parseMessage(in, currentTime);
                delete in;
            }
            else
                done = TRUE;
        }
        catch( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
    while( !done );
}

/*---------------------------------------------------------------------------
  registerForPoints

  Registers for all points of the control areas.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::registerForPoints(const vector<CtiLMControlArea*>& controlAreas)
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Registering for point changes.");
    }

    auto regMsg = std::make_unique<CtiPointRegistrationMsg>();

    for( LONG i=0;i<controlAreas.size();i++ )
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

        vector<CtiLMControlAreaTrigger*>& controlAreaTriggers = currentControlArea->getLMControlAreaTriggers();

        for( LONG j=0;j<controlAreaTriggers.size();j++ )
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)controlAreaTriggers.at(j);

            if( currentTrigger->getPointId() > 0 )
            {
                regMsg->insert(currentTrigger->getPointId());
            }
            if( currentTrigger->getPeakPointId() > 0 )
            {
                regMsg->insert(currentTrigger->getPeakPointId());
            }
            if( currentTrigger->getThresholdPointId() > 0 )
            {
                regMsg->insert(currentTrigger->getThresholdPointId());
            }
        }

        vector<CtiLMProgramBaseSPtr>& lmPrograms = currentControlArea->getLMPrograms();

        for( LONG k=0;k<lmPrograms.size();k++ )
        {
            CtiLMProgramBaseSPtr currentProgram = (CtiLMProgramBaseSPtr)lmPrograms[k];
            if( currentProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                CtiLMGroupVec groups  = boost::static_pointer_cast<CtiLMProgramDirect>(currentProgram)->getLMProgramDirectGroups();
                for( CtiLMGroupIter iter = groups.begin(); iter != groups.end(); iter++ )
                {
                    CtiLMGroupPtr currentGroup  = *iter;
                    if( currentGroup->getHoursDailyPointId() > 0 )
                    {
                        regMsg->insert(currentGroup->getHoursDailyPointId());
                    }
                    if( currentGroup->getHoursMonthlyPointId() > 0 )
                    {
                        regMsg->insert(currentGroup->getHoursMonthlyPointId());
                    }
                    if( currentGroup->getHoursSeasonalPointId() > 0 )
                    {
                        regMsg->insert(currentGroup->getHoursSeasonalPointId());
                    }
                    if( currentGroup->getHoursAnnuallyPointId() > 0 )
                    {
                        regMsg->insert(currentGroup->getHoursAnnuallyPointId());
                    }
                    if( currentGroup->getControlStatusPointId() > 0 )
                    {
                        regMsg->insert(currentGroup->getControlStatusPointId());
                    }
                }
            }
        }
    }
    try
    {
        getDispatchConnection()->WriteConnQue(std::move(regMsg), CALLSITE);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    regMsg = NULL;
}

/*---------------------------------------------------------------------------
  parseMessage

  Reads off the Dispatch connection and handles messages accordingly.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::parseMessage( CtiMessage *message, CtiTime currentTime )
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
            dbChange = (CtiDBChangeMsg*)message;
            if( dbChange->getSource() != CtiLMControlAreaStore::LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE &&
                ( ( dbChange->getDatabase() == ChangePAODb &&
                    (resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_DEVICE ||
                     resolvePAOCategory(dbChange->getCategory()) == PAO_CATEGORY_LOAD_MANAGEMENT) ) ||
                  dbChange->getDatabase() == ChangePointDb ||
                  dbChange->getDatabase() == ChangeStateGroupDb ||
                  dbChange->getDatabase() == ChangeLMConstraintDb ||
                  dbChange->getDatabase() == ChangeSeasonScheduleDb ||
                  dbChange->getDatabase() == ChangeHolidayScheduleDb) )
            {
                if( dbChange->getTypeOfChange() == ChangeTypeDelete &&
                    resolvePAOType(dbChange->getCategory(),dbChange->getObjectType()) == TYPE_LM_CONTROL_AREA )
                {
                    CtiLMControlAreaStore::getInstance()->setWasControlAreaDeletedFlag(true);
                }
                CtiLMControlAreaStore::getInstance()->setValid(false);
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
            pData = (CtiPointDataMsg*)message;
            pointDataMsg( pData->getId(), pData->getValue(), pData->getQuality(), pData->getTags(), pData->getTime(), currentTime );
        }
        break;
    case MSG_PCRETURN:
        {
            pcReturn = (CtiReturnMsg*)message;

            if( _LM_DEBUG & LM_DEBUG_EXTENDED )
            {
                CTILOG_DEBUG(dout, "Porter return received");
            }
            if( pcReturn->Status() &&
                pcReturn->Status() != ClientErrors::DeviceInhibited &&
                pcReturn->Status() != ClientErrors::RemoteInhibited &&
                pcReturn->Status() != ClientErrors::PortInhibited )
            {
                CTILOG_INFO(dout, *pcReturn);
            }
        }
        break;
    case MSG_COMMAND:
        {
            if( _LM_DEBUG & LM_DEBUG_EXTENDED )
            {
                CTILOG_DEBUG(dout, "Command Message received from Dispatch");
            }

            cmdMsg = (CtiCommandMsg*)message;
            if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
            {
                if( _LM_DEBUG & LM_DEBUG_EXTENDED )
                {
                    CTILOG_DEBUG(dout, "Replying to Are You There message.");
                }
                try
                {
                    getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage(), CALLSITE);
                }
                catch( ... )
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }
            else
            {
                CTILOG_INFO(dout, "Command Message with type = "
                << cmdMsg->getOperation() << ") not supported from Dispatch" );
            }
        }
        break;
    case MSG_MULTI:
        {
            msgMulti = (CtiMultiMsg*)message;
            CtiMultiMsg_vec& temp = msgMulti->getData();
            string timerOutput = " Multi loading " + CtiNumStr(temp.size()) + " messages";
            Cti::Timing::DebugTimer debugTime(timerOutput, _LM_DEBUG & LM_DEBUG_TIMING, 2);
            for( i=0;i<temp.size( );i++ )
            {
                parseMessage(temp[i], currentTime);
            }
        }
        break;
    case MSG_SIGNAL:
        {
            signal = (CtiSignalMsg*)message;
            signalMsg( signal->getId(), signal->getTags(), signal->getText(), signal->getAdditionalInfo() );
        }
        break;
    case MSG_TAG:
        break; //we don't care.
    default:
        {
            CTILOG_WARN(dout, "Unknown message type " << message->isA());
        }
    }
    return;
}

/*---------------------------------------------------------------------------
  pointChange

  Handles point data messages and updates strategy point values.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::pointDataMsg( long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, CtiTime currentTime )
{
    if( _LM_DEBUG & LM_DEBUG_POINT_DATA )
    {
        Cti::FormattedList list;

        list << "Point Data";
        list.add("ID")      << pointID;
        list.add("Val")     << value;
        list.add("Time")    << timestamp;

        CTILOG_DEBUG(dout, list);
    }

    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();

    vector<CtiLMControlArea*> controlAreas = store->findControlAreasByPointID(pointID);
    for( LONG i=0;i<controlAreas.size();i++ )
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

        vector<CtiLMControlAreaTrigger*>& controlAreaTriggers = currentControlArea->getLMControlAreaTriggers();

        for( LONG j=0;j<controlAreaTriggers.size();j++ )
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)controlAreaTriggers.at(j);

            if ( pointID != 0 && ( quality == ManualQuality || quality == NormalQuality ) )
            {
                if( currentTrigger->getPointId() == pointID )
                {
                    currentControlArea->setUpdatedFlag(TRUE);

                    string text = ("");
                    bool isNewData = false;
                    string additional = ("");
                    if( timestamp > currentTrigger->getLastPointValueTimestamp() )
                    {
                        isNewData = true;
                        currentTrigger->setLastPointValueTimestamp(timestamp);
                        currentControlArea->setNewPointDataReceivedFlag(TRUE);
                        if( _LM_POINT_EVENT_LOGGING &&
                            ( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                              currentControlArea->getControlAreaState() == CtiLMControlArea::PartiallyActiveState ) )
                        {
                            char tempchar[80] = "";
                            text += "Current Trigger Value: ";
                            _snprintf(tempchar,80,"%.*f",3,value);
                            text += tempchar;
                        }
                    }

                    if( currentTrigger->getPointValue() != value )
                    {
                        currentTrigger->setPointValue(value);
                    }

                    if( (!ciStringEqual(currentTrigger->getProjectionType(), CtiLMControlAreaTrigger::NoneProjectionType) &&
                         !ciStringEqual(currentTrigger->getProjectionType(), "(none)"))/*"(none)" is a hack*/ &&
                         !ciStringEqual(currentTrigger->getTriggerType(), CtiLMControlAreaTrigger::StatusTriggerType) )
                    {
                        if( quality != NonUpdatedQuality && isNewData )
                        {
                            if( currentTrigger->getProjectionPointEntriesQueue().size() < currentTrigger->getProjectionPoints() )//first reading plug in multiple copies
                            {
                                LONG pass = 1;
                                LONG pluggedIntervalDuration = currentTrigger->getProjectAheadDuration()/4;
                                while( currentTrigger->getProjectionPointEntriesQueue().size() < currentTrigger->getProjectionPoints() )
                                {
                                    CtiTime pluggedTimestamp(timestamp.seconds() - (pluggedIntervalDuration * (currentTrigger->getProjectionPoints()-pass)));
                                    currentTrigger->getProjectionPointEntriesQueue().push_back( CtiLMProjectionPointEntry(value,pluggedTimestamp) );
                                    pass++;
                                }
                            }
                            else//normal case
                            {
                                currentTrigger->getProjectionPointEntriesQueue().push_back( CtiLMProjectionPointEntry(value,timestamp) );
                            }
                            currentTrigger->calculateProjectedValue();
                            if( _LM_POINT_EVENT_LOGGING &&
                                ( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                                  currentControlArea->getControlAreaState() == CtiLMControlArea::PartiallyActiveState ) )
                            {
                                char tempchar[80] = "";
                                additional += "Projected Value: ";
                                _snprintf(tempchar,80,"%.*f",3,currentTrigger->getProjectedPointValue());
                                additional += tempchar;
                            }
                        }
                    }
                    else if( !ciStringEqual(currentTrigger->getTriggerType(),CtiLMControlAreaTrigger::StatusTriggerType))//make the projected value equal to the real value
                    {
                        currentTrigger->setProjectedPointValue(value);
                    }

                    if( _LM_POINT_EVENT_LOGGING && text.length() > 0 )
                    {
                        try
                        {
                            getDispatchConnection()->WriteConnQue(
                                CTIDBG_new CtiSignalMsg(currentTrigger->getPointId(), 0, text, additional, GeneralLogType, SignalEvent), CALLSITE);
                            CTILOG_INFO(dout, text << ", " << additional << " (" << currentControlArea->getPAOName() << ")");
                        }
                        catch( ... )
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }
                    }
                }

                if( currentTrigger->getPeakPointId() == pointID )
                {
                    currentControlArea->setUpdatedFlag(TRUE);

                    if( value > currentTrigger->getPeakPointValue() )
                    {
                        currentTrigger->setLastPeakPointValueTimestamp(timestamp);
                        currentTrigger->setPeakPointValue(value);
                    }

                    if( currentTrigger->getThresholdKickPercent() > 0 )
                    {
                        DOUBLE oldThreshold = currentTrigger->getThreshold();
                        LONG thresholdKickOffset = currentTrigger->getThresholdKickPercent();
                        LONG amountOverKickValue = currentTrigger->getPeakPointValue() - currentTrigger->getThreshold() - thresholdKickOffset;

                        if( amountOverKickValue > 0 )
                        {
                            currentTrigger->setThreshold( currentTrigger->getThreshold() + amountOverKickValue );
                            CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentControlArea, currentTrigger);
                            currentControlArea->setUpdatedFlag(TRUE);
                            {
                                char tempchar[80] = "";
                                string text = ("Automatic Threshold Kick Up");
                                string additional = ("Threshold for Trigger: " + CtiNumStr(currentTrigger->getTriggerNumber()));
                                additional += tempchar;
                                additional += " changed in LMControlArea: " + currentControlArea->getPAOName();
                                additional += " PAO ID: " + CtiNumStr(currentControlArea->getPAOId());
                                additional += tempchar;
                                additional += " old threshold: " + CtiNumStr(oldThreshold, 3);
                                additional += tempchar;
                                additional += " new threshold: " + CtiNumStr(currentTrigger->getThreshold(), 3);
                                additional += tempchar;
                                additional += " changed because peak point value: " + CtiNumStr(currentTrigger->getPeakPointValue(), 1);
                                additional += tempchar;
                                additional += " current point value: " + CtiNumStr(currentTrigger->getPointValue(), 1);
                                additional += tempchar;
                                CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                                CTILOG_INFO(dout, text << ", " << additional);
                            }
                        }
                    }
                }

                if ( currentTrigger->getThresholdPointId() == pointID &&
                     currentTrigger->getTriggerType() == CtiLMControlAreaTrigger::ThresholdPointTriggerType )
                {
                    currentControlArea->setUpdatedFlag(TRUE);

                    if ( value != currentTrigger->getThreshold() )
                    {
                        double oldThreshold = currentTrigger->getThreshold();

                        currentTrigger->setThreshold(value);
                        currentControlArea->setNewPointDataReceivedFlag(TRUE);

                        CtiLMControlAreaStore::getInstance()->UpdateTriggerInDB(currentControlArea, currentTrigger);

                        if ( _LM_POINT_EVENT_LOGGING )
                        {
                            std::string text("Updated Threshold");

                            std::ostringstream stream;

                            stream
                                << "Threshold for Trigger: "     << currentTrigger->getTriggerNumber()
                                << " changed in LMControlArea: " << currentControlArea->getPAOName()
                                << " PAO ID: "                   << currentControlArea->getPAOId()
                                << " old threshold: "            << oldThreshold
                                << " new threshold: "            << currentTrigger->getThreshold();

                            std::string additional( stream.str() );

                            CtiLoadManager::getInstance()->sendMessageToDispatch(
                                new CtiSignalMsg(currentTrigger->getThresholdPointId(), 0, text, additional, GeneralLogType, SignalEvent));

                            CTILOG_INFO(dout, text << ", " << additional);
                        }
                    }
                }
            }
        }
    }

    CtiLMGroupPtr lm_group = store->findGroupByPointID(pointID);
    if( lm_group.get() != 0 )   //we know this point is associated with this group,
    {
        //figure out how and deal with it
        if( lm_group->getHoursDailyPointId() == pointID )
        {
            CtiTime now;
            struct tm now_tm, timestamp_tm;

            now.extract(&now_tm);
            timestamp.extract(&timestamp_tm);

            long nowDaysSince1900 = (now_tm.tm_year*365) + now_tm.tm_yday;
            long timestampDaysSince1900 = (timestamp_tm.tm_year*365) + timestamp_tm.tm_yday;

            if( nowDaysSince1900 == timestampDaysSince1900 )//i.e. is this daily control history from today or from some previous day
            {
                lm_group->setCurrentHoursDaily(value);
            }
            else
            {
                lm_group->setCurrentHoursDaily(0.0);
            }
        }
        if( lm_group->getHoursMonthlyPointId() == pointID )
        {
            lm_group->setCurrentHoursMonthly(value);
        }
        if( lm_group->getHoursSeasonalPointId() == pointID )
        {
            lm_group->setCurrentHoursSeasonal(value);
        }
        if( lm_group->getHoursAnnuallyPointId() == pointID )
        {
            lm_group->setCurrentHoursAnnually(value);
        }
        if( lm_group->getControlStatusPointId() == pointID )
        {
            // Did this group just go from active to inactive?
            if( CtiLMGroupBase::ActiveState == lm_group->getGroupControlState() &&
                CtiLMGroupBase::InactiveState == value )
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Load Group: " << lm_group->getPAOName() << " has gone control complete." );
                }
                lm_group->setControlCompleteTime(currentTime);
            }
            lm_group->setGroupControlState(value);
        }

    }
}

/*---------------------------------------------------------------------------
  signalMessage

  Handles signal messages and updates strategy tags.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::signalMsg( long pointID, unsigned tags, string text, string additional )
{
    if( _LM_DEBUG & LM_DEBUG_EXTENDED )
    {
        Cti::FormattedList list;

        list << "Signal Message received";
        list.add("Point ID")    << pointID;
        list.add("Tags")        << tags;
        list.add("Text")        << text;
        list.add("Additional Info") << additional;

        CTILOG_DEBUG(dout, list);
    }
}

/*---------------------------------------------------------------------------
  sendMessageToDispatch

  Sends a cti message to dispatch, this is a way for other threads to use
  the CtiLoadManager's connection to dispatch.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToDispatch( CtiMessage* message )
{
    CtiLockGuard<CtiCriticalSection>  guard(_mutex);
    try
    {
        getDispatchConnection()->WriteConnQue(message, CALLSITE);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
  sendMessageToPIL

  Sends a cti message to pil, this is a way for other threads to use
  the CtiLoadManager's connection to pil.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToPIL( CtiMessage* message )
{
    CtiLockGuard<CtiCriticalSection>  guard(_mutex);
    try
    {
        message->resetTime();                       // CGP 5/21/04 Update its time to current time.
        getPorterConnection()->WriteConnQue(message, CALLSITE);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
  sendMessageToNotification

  Sends a cti message to the notification server
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToNotification(std::unique_ptr<CtiMessage>&& message)
{
    CtiLockGuard<CtiCriticalSection>  guard(_mutex);
    try
    {
        message->resetTime();                       // CGP 5/21/04 Update its time to current time.
        getNotificationConnection()->WriteConnQue(std::move(message), CALLSITE);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


/*---------------------------------------------------------------------------
  sendMessageToClients

  Sends a cti message to all the attached clients. This consumes the message.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToClients( CtiMessage* message )
{
    CtiLockGuard<CtiCriticalSection>  guard(_mutex);
    try
    {
        CtiLMClientListener::getInstance().BroadcastMessage(message);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*
 * loadControlLoopCParms
 * initialize control loop delay cparms that control behavior of the main loop
 * these cparms are optional.
 */
void CtiLoadManager::loadControlLoopCParms()
{
    string str;
    char var[128];

    strcpy(var, "LOAD_MANAGEMENT_CONTROL_LOOP_NORMAL_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        control_loop_delay = atoi(str.c_str());
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }

    strcpy(var, "LOAD_MANAGEMENT_CONTROL_LOOP_INMSG_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        control_loop_inmsg_delay = atoi(str.c_str());
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }

    strcpy(var, "LOAD_MANAGEMENT_CONTROL_LOOP_OUTMSG_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        control_loop_outmsg_delay = atoi(str.c_str());
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
}
