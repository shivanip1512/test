/*-----------------------------------------------------------------------------
  Filename:  loadmanager.cpp

  Programmer:  Josh Wolberg

  Description:    Source file for CtiLoadManager
  Once started CtiLoadManager is reponsible
  for determining if and when to run the
  schedules provided by the CtiLMControlAreaStore.

  Initial Date:  2/12/2001

  COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
  -----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

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
#include "loadmanager.h"
#include "lmcontrolareastore.h"
#include "lmcontrolareatrigger.h"
#include "executor.h"
#include "ctibase.h"
#include "yukon.h"
#include "netports.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "lmprogramdirect.h"
#include "clistener.h"

#include <rw/thr/prodcons.h>

extern ULONG _LM_DEBUG;
extern BOOL _LM_POINT_EVENT_LOGGING;

/* The singleton instance of CtiLoadManager */
CtiLoadManager* CtiLoadManager::_instance = NULL;

/*---------------------------------------------------------------------------
  getInstance

  Returns the single instance of CtiLoadManager
  ---------------------------------------------------------------------------*/
CtiLoadManager* CtiLoadManager::getInstance()
{
    if ( _instance == NULL )
        _instance = new CtiLoadManager();

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

    _dispatchConnection = NULL;
    _pilConnection = NULL;
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
    RWThreadFunction threadfunc = rwMakeThreadFunction( *this, &CtiLoadManager::controlLoop );
    _loadManagerThread = threadfunc;
    threadfunc.start();
}

/*---------------------------------------------------------------------------
  stop

  Stops the controller thread
  ---------------------------------------------------------------------------*/
void CtiLoadManager::stop()
{
    try
    {
        if ( _loadManagerThread.isValid() && _loadManagerThread.requestCancellation() == RW_THR_ABORTED )
        {
            _loadManagerThread.terminate();

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Forced to terminate." << endl;
            }
        }
        else
        {
            _loadManagerThread.requestCancellation();
            _loadManagerThread.join();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*----------------------------------------------------------------------------
  handleMessage

  Handles any CtiMessages received from a client in the main thread.
----------------------------------------------------------------------------*/
void CtiLoadManager::handleMessage(CtiMessage* msg)
{
    _main_queue.putQueue(msg);
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
        CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
        registerForPoints(*store->getControlAreas(RWDBDateTime().seconds()));
        store->setReregisterForPoints(false);
    }


    RWDBDateTime currentDateTime;
    RWOrdered controlAreaChanges;
    CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();

    CtiMessage* msg = NULL;
    CtiLMExecutorFactory executorFactory;

    //remember when the last control area messages were sent
    time_t last_ca_msg_sent = 0;

    loadControlLoopCParms();

    while(TRUE)
    {
    long main_wait = control_loop_delay;
    bool received_message = false;

        while( (msg = _main_queue.getQueue(main_wait)) != NULL )
        {
            CtiLMExecutor* executor = executorFactory.createExecutor(msg);
            try
            {
                executor->Execute();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << RWTime() << " **Checkpoint** " <<  " Caught '...' executing executor in main thread." << __FILE__ << "(" << __LINE__ << ")" << endl;
            }
            delete executor;
        //Shorten how long to wait in case a message was processed to improve response time
        main_wait = control_loop_inmsg_delay;
        received_message = true;
        }

    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

        RWDBDateTime prevDateTime = currentDateTime;
        currentDateTime.now();
        LONG secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
        ULONG secondsFrom1901 = currentDateTime.seconds();

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            if( (secondsFrom1901%1800) == 0 )
            {//every five minutes tell the user if the manager thread is still alive
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Load Manager thread pulse" << endl;
            }
        }

        rwRunnable().serviceCancellation();

        RWOrdered& controlAreas = *store->getControlAreas(secondsFrom1901);

        try
        {
            if( store->getReregisterForPoints() )
            {
                registerForPoints(controlAreas);
                store->setReregisterForPoints(false);
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        try
        {
            checkDispatch(secondsFrom1901);
            checkPIL(secondsFrom1901);
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        BOOL examinedControlAreaForControlNeededFlag = FALSE;
        if( controlAreas.entries() > 0 )
        {
            for(LONG i=0;i<controlAreas.entries();i++)
            {
                CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

                try
                {
                    currentControlArea->handleNotification(secondsFrom1901, multiPilMsg, multiDispatchMsg);

                    if( currentControlArea->isManualControlReceived() )
                    {
                        currentControlArea->handleManualControl(secondsFrom1901, multiPilMsg,multiDispatchMsg);
                    }

//                    if( !currentControlArea->getDisableFlag() ) // how about control area windows?
                    {
                        currentControlArea->updateTimedPrograms(secondsFromBeginningOfDay);
                        currentControlArea->handleTimeBasedControl(secondsFrom1901, secondsFromBeginningOfDay, multiPilMsg, multiDispatchMsg);
                    }

                    if( !currentControlArea->getDisableFlag() && currentControlArea->isControlTime(secondsFromBeginningOfDay) )
                    {
                        if( currentControlArea->isTriggerCheckNeeded(secondsFrom1901) )
                        {
                            // Do we need to reduce load?
                            DOUBLE loadReductionNeeded = currentControlArea->calculateLoadReductionNeeded();
                            if( loadReductionNeeded > 0.0 &&
                                currentControlArea->isPastMinResponseTime(secondsFrom1901) )
                            {
                                if( currentControlArea->getControlAreaState() != CtiLMControlArea::FullyActiveState )
                                {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Attempting to reduce load in control area: " << currentControlArea->getPAOName() << "." << endl;
                                }
                                if( currentControlArea->getControlInterval() != 0 ||
                                    currentControlArea->isThresholdTriggerTripped() )
                                {
                                    currentControlArea->reduceControlAreaLoad(loadReductionNeeded,secondsFromBeginningOfDay,secondsFrom1901,multiPilMsg,multiDispatchMsg);
                                }
                                else
                                {
                                    //Special Case: if only a status trigger is tripped and the control interval is 0,
                                    //then we need make the control area fully active.  If some of the programs are disabled
                                    //or out of their control windows they will not be controlled
                                    currentControlArea->takeAllAvailableControlAreaLoad(secondsFromBeginningOfDay,secondsFrom1901,multiPilMsg,multiDispatchMsg);
                                }
                                currentControlArea->setUpdatedFlag(TRUE);
                                }
                                else if( currentControlArea->isThresholdTriggerTripped() )
                                {
                                    //all load reducing programs are currently running
                                    //can not reduce any more demand
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - All load reducing programs are currently running for control area: " << currentControlArea->getPAOName() << " can not reduce any more load." << endl;
                                }
                            }
                            //Can we let off some load? (increase/stop)
                            if(currentControlArea->getControlAreaState() != CtiLMControlArea::InactiveState &&
                               currentControlArea->shouldReduceControl() &&
                               currentControlArea->isPastMinResponseTime(secondsFrom1901))
                            {
                                currentControlArea->reduceControlAreaControl(secondsFrom1901, multiPilMsg, multiDispatchMsg);
                            }



                            if( currentControlArea->getControlInterval() == 0 )
                            {
                                currentControlArea->setNewPointDataReceivedFlag(FALSE);
                            }
                            else
                            {
                                currentControlArea->figureNextCheckTime(secondsFrom1901);
                            }
                            examinedControlAreaForControlNeededFlag = TRUE;
                        }

                        //This ends up refreshing any control necessary
                        if( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                            currentControlArea->getControlAreaState() == CtiLMControlArea::ActiveState )
                        {
//                            if( currentControlArea->isControlStillNeeded() )
                            {
                                //CtiLockGuard<CtiLogger> logger_guard(dout);
                                //dout << RWTime() << " - Maintaining current load reduction in control area: " << currentControlArea->getPAOName() << "." << endl;
                                if( currentControlArea->maintainCurrentControl(secondsFromBeginningOfDay,secondsFrom1901,multiPilMsg,multiDispatchMsg,examinedControlAreaForControlNeededFlag) )
                                {
                                    currentControlArea->setUpdatedFlag(TRUE);
                                }
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
                              currentControlArea->getControlAreaState() == CtiLMControlArea::ActiveState) )
                    {
                        if( currentControlArea->stopAllControl(multiPilMsg,multiDispatchMsg, secondsFrom1901) )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Left controllable time window in control area: " << currentControlArea->getPAOName() << ", stopping all control." << endl;
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
#ifdef _CHANGED_HOW_CA_GET_SENT
                try
                {
               if( currentControlArea->getUpdatedFlag() )
                    {
                        currentControlArea->createControlStatusPointUpdates(multiDispatchMsg);
                        controlAreaChanges.insert(currentControlArea);
                        currentControlArea->setUpdatedFlag(FALSE);
            }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
#endif
            }
        }

        try
        {
            if( multiDispatchMsg->getCount() > 0 )
            {
                /*{
                  CtiLockGuard<CtiLogger> logger_guard(dout);
                  dout << RWTime() << " - Sending multi message to Dispatch." << endl;
                  }*/

                multiDispatchMsg->resetTime();                              // CGP 5/21/04 Update its time to current time.
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
            if( multiPilMsg->getCount() > 0 )
            {
                multiPilMsg->setMessagePriority(13);
                multiPilMsg->resetTime();                       // CGP 5/21/04 Update its time to current time.
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
        // Only send control area changes so often to avoid overwhelming the system
        // if we just received a client message then do it anyways however for good response
        time_t now = time(NULL);
        if(received_message || now > (last_ca_msg_sent + control_loop_outmsg_delay))
        {
        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

            if( currentControlArea->getUpdatedFlag() )
                    {
                        currentControlArea->createControlStatusPointUpdates(multiDispatchMsg);
                        controlAreaChanges.insert(currentControlArea);
                        currentControlArea->setUpdatedFlag(FALSE);
            }
        }

        CtiLMExecutorFactory f;
                CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(controlAreaChanges));

                try
                {
                    executor->Execute();
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                delete executor;

        store->dumpAllDynamicData();
        last_ca_msg_sent = now;
                controlAreaChanges.clear();
        }

#ifdef _BUNG__
            if( controlAreaChanges.entries() > 0 )
            {
                store->dumpAllDynamicData();
                CtiLMExecutorFactory f;
                CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(controlAreaChanges));

                try
                {
                    executor->Execute();
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                delete executor;

                controlAreaChanges.clear();
                    }
#endif

        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    rwRunnable().serviceCancellation();

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
  Getdispatchconnection

  Returns a connection to Dispatch, initializes if isn't created yet.
  ---------------------------------------------------------------------------*/
CtiConnection* CtiLoadManager::getDispatchConnection()
{
    try
    {
        if( _dispatchConnection == NULL || (_dispatchConnection != NULL && _dispatchConnection->verifyConnection()) )
        {
            //Set up the defaults
            INT dispatch_port = VANGOGHNEXUS;
            RWCString dispatch_host = "127.0.0.1";

            RWCString str;
            char var[128];

            strcpy(var, "DISPATCH_MACHINE");
            if( !(str = gConfigParms.getValueAsString(var)).isNull() )
            {
                dispatch_host = str;
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << var << ":  " << str << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            strcpy(var, "DISPATCH_PORT");
            if( !(str = gConfigParms.getValueAsString(var)).isNull() )
            {
                dispatch_port = atoi(str);
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << var << ":  " << str << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            //throw away the old connection if there was one that couldn't be verified
            if( _dispatchConnection != NULL && _dispatchConnection->verifyConnection() )
            {
                delete _dispatchConnection;
                _dispatchConnection = NULL;
            }

            if( _dispatchConnection == NULL )
            {
                //Connect to Dispatch
                _dispatchConnection = new CtiConnection( dispatch_port, dispatch_host );

                //Send a registration message to Dispatch
                CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("LoadManagement", 0, false );
                _dispatchConnection->WriteConnQue( registrationMsg );
            }
        }

        return _dispatchConnection;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

        return NULL;
    }
}

/*---------------------------------------------------------------------------
  getPILConnection

  Returns a connection to PIL, initializes if isn't created yet.
  ---------------------------------------------------------------------------*/
CtiConnection* CtiLoadManager::getPILConnection()
{
    try
    {
        if( _pilConnection == NULL || (_pilConnection != NULL && _pilConnection->verifyConnection()) )
        {
            //Set up the defaults
            INT pil_port = PORTERINTERFACENEXUS;
            RWCString pil_host = "127.0.0.1";

            RWCString str;
            char var[128];

            strcpy(var, "PIL_MACHINE");
            if( !(str = gConfigParms.getValueAsString(var)).isNull() )
            {
                pil_host = str;
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << var << ":  " << str << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            strcpy(var, "PIL_PORT");
            if( !(str = gConfigParms.getValueAsString(var)).isNull() )
            {
                pil_port = atoi(str);
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << var << ":  " << str << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
            }

            if( _pilConnection != NULL && _pilConnection->verifyConnection() )
            {
                delete _pilConnection;
                _pilConnection = NULL;
            }

            if( _pilConnection == NULL )
            {
                //Connect to Pil
                _pilConnection = new CtiConnection( pil_port, pil_host );

                //Send a registration message to Pil
                CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("LoadManagement", 0, false );
                _pilConnection->WriteConnQue( registrationMsg );
            }
        }

        return _pilConnection;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

        return NULL;
    }
}

/*---------------------------------------------------------------------------
  checkDispatch

  Reads off the Dispatch connection and handles messages accordingly.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::checkDispatch(ULONG secondsFrom1901)
{
    bool done = FALSE;
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
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    while(!done);
}

/*---------------------------------------------------------------------------
  checkPIL

  Reads off the PIL connection and handles messages accordingly.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::checkPIL(ULONG secondsFrom1901)
{
    bool done = FALSE;
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
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    while(!done);
}

/*---------------------------------------------------------------------------
  registerForPoints

  Registers for all points of the control areas.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::registerForPoints(const RWOrdered& controlAreas)
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Registering for point changes." << endl;
    }

    CtiPointRegistrationMsg* regMsg;

    string simple_registration = gConfigParms.getValueAsString("LOAD_MANAGEMENT_SIMPLE_REGISTRATION", "false");
    if(simple_registration == "true" || simple_registration == "TRUE")
    { //register for all points
        regMsg = new CtiPointRegistrationMsg(REG_ALL_PTS_MASK);
    }
    else
    { //register for each point specifically
        regMsg = new CtiPointRegistrationMsg();

        for(LONG i=0;i<controlAreas.entries();i++)
        {
            CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

            RWOrdered& controlAreaTriggers = currentControlArea->getLMControlAreaTriggers();

            for(LONG j=0;j<controlAreaTriggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[j];

                if( currentTrigger->getPointId() > 0 )
                {
                    regMsg->insert(currentTrigger->getPointId());
                }
                if( currentTrigger->getPeakPointId() > 0 )
                {
                    regMsg->insert(currentTrigger->getPeakPointId());
                }
            }

            RWOrdered& lmPrograms = currentControlArea->getLMPrograms();

            for(LONG k=0;k<lmPrograms.entries();k++)
            {
                CtiLMProgramBase* currentProgram = (CtiLMProgramBase*)lmPrograms[k];
                if( currentProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                {
                    CtiLMGroupVec groups  = ((CtiLMProgramDirect*)currentProgram)->getLMProgramDirectGroups();
                    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
                    {
                        CtiLMGroupPtr currentGroup  = *i;
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
    }
    try
    {
        getDispatchConnection()->WriteConnQue(regMsg);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    regMsg = NULL;
}

/*---------------------------------------------------------------------------
  parseMessage

  Reads off the Dispatch connection and handles messages accordingly.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::parseMessage(RWCollectable *message, ULONG secondsFrom1901)
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
    }
    break;
    case MSG_POINTDATA:
    {
        pData = (CtiPointDataMsg*)message;
        pointDataMsg( pData->getId(), pData->getValue(), pData->getQuality(), pData->getTags(), pData->getTime(), secondsFrom1901 );
    }
    break;
    case MSG_PCRETURN:
    {
        pcReturn = (CtiReturnMsg*)message;
        porterReturnMsg( pcReturn->DeviceId(), pcReturn->CommandString(), pcReturn->Status(), pcReturn->ResultString(), secondsFrom1901 );
        if( pcReturn->Status() != NORMAL &&
            pcReturn->Status() != DEVICEINHIBITED &&
            pcReturn->Status() != REMOTEINHIBITED &&
            pcReturn->Status() != PORTINHIBITED )
        {
            pcReturn->dump();
        }
    }
    break;
    case MSG_COMMAND:
    {
        if( _LM_DEBUG & LM_DEBUG_EXTENDED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Command Message received from Dispatch" << endl;
        }

        cmdMsg = (CtiCommandMsg*)message;
        if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
        {
            if( _LM_DEBUG & LM_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Replying to Are You There message." << endl;
            }
            try
            {
                getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage());
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Command Message with type = "
                 << cmdMsg->getOperation() << ") not supported from Dispatch" << endl;
        }
    }
    break;
    case MSG_MULTI:
    {
        msgMulti = (CtiMultiMsg*)message;
        RWOrdered& temp = msgMulti->getData();
        for(i=0;i<temp.entries( );i++)
        {
            parseMessage(temp[i], secondsFrom1901);
        }
    }
    break;
    case MSG_SIGNAL:
    {
        signal = (CtiSignalMsg*)message;
        signalMsg( signal->getId(), signal->getTags(), signal->getText(), signal->getAdditionalInfo(), secondsFrom1901 );
    }
    break;
    case MSG_TAG:
        break; //we don't care.
    default:
    {
        char tempstr[64] = "";
        _itoa(message->isA(),tempstr,10);
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - message->isA() = " << tempstr << endl;
        dout << RWTime() << " - Unknown message type: parseMessage(RWCollectable *message) in controller.cpp" << endl;
    }
    }
    return;
}

/*---------------------------------------------------------------------------
  pointChange

  Handles point data messages and updates strategy point values.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::pointDataMsg( long pointID, double value, unsigned quality, unsigned tags, RWTime& timestamp, ULONG secondsFrom1901 )
{
    if( _LM_DEBUG & LM_DEBUG_POINT_DATA )
    {
        char tempchar[80];
        RWCString outString = "Point Data, ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Val:";
        int precision = 3;
        _snprintf(tempchar,80,"%.*f",precision,value);
        outString += tempchar;
        outString += " Time: ";
        outString += RWDBDateTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << outString.data() << endl;
    }

    CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();

    RWOrdered& controlAreas = (*store->getControlAreas(secondsFrom1901));
    for(LONG i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

        RWOrdered& controlAreaTriggers = currentControlArea->getLMControlAreaTriggers();

        for(LONG j=0;j<controlAreaTriggers.entries();j++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[j];

            if( currentTrigger->getPointId() == pointID )
            {
                RWCString text = RWCString("");
                RWCString additional = RWCString("");
                if( timestamp > currentTrigger->getLastPointValueTimestamp() )
                {
                    currentTrigger->setLastPointValueTimestamp(timestamp);
                    currentControlArea->setNewPointDataReceivedFlag(TRUE);
                    if( _LM_POINT_EVENT_LOGGING &&
                        ( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                          currentControlArea->getControlAreaState() == CtiLMControlArea::ActiveState ) )
                    {
                        char tempchar[80] = "";
                        text += "Current Trigger Value: ";
                        _snprintf(tempchar,80,"%.*f",3,value);
                        text += tempchar;
                    }
                }
                currentTrigger->setPointValue(value);

                //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                if( (currentTrigger->getProjectionType().compareTo(CtiLMControlAreaTrigger::NoneProjectionType,RWCString::ignoreCase) && currentTrigger->getProjectionType().compareTo("(none)",RWCString::ignoreCase))/*"(none)" is a hack*/ &&
                    currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) )
                {//This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                    if( quality != NonUpdatedQuality &&
                        currentControlArea->getNewPointDataReceivedFlag() )
                    {
                        if( currentTrigger->getProjectionPointEntriesQueue().entries() < currentTrigger->getProjectionPoints() )
                        {//first reading plug in multiple copies
                            LONG pass = 1;
                            LONG pluggedIntervalDuration = currentTrigger->getProjectAheadDuration()/4;
                            while( currentTrigger->getProjectionPointEntriesQueue().entries() < currentTrigger->getProjectionPoints() )
                            {
                                RWTime pluggedTimestamp(timestamp.seconds() - (pluggedIntervalDuration * (currentTrigger->getProjectionPoints()-pass)));
                                currentTrigger->getProjectionPointEntriesQueue().insert( CtiLMProjectionPointEntry(value,pluggedTimestamp) );
                                pass++;
                            }
                        }
                        else
                        {//normal case
                            currentTrigger->getProjectionPointEntriesQueue().insert( CtiLMProjectionPointEntry(value,timestamp) );
                        }
                        currentTrigger->calculateProjectedValue();
                        if( _LM_POINT_EVENT_LOGGING &&
                            ( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                              currentControlArea->getControlAreaState() == CtiLMControlArea::ActiveState ) )
                        {
                            char tempchar[80] = "";
                            additional += "Projected Value: ";
                            _snprintf(tempchar,80,"%.*f",3,currentTrigger->getProjectedPointValue());
                            additional += tempchar;
                        }
                    }
                }
                //This IS supposed to be != so don't add a ! at the beginning like the other compareTo calls!!!!!!!!!!!
                else if( currentTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) )
                {//make the projected value equal to the real value
                    currentTrigger->setProjectedPointValue(value);
                }
                currentControlArea->setUpdatedFlag(TRUE);

                if( _LM_POINT_EVENT_LOGGING && text.length() > 0 )
                {
                    try
                    {
                        getDispatchConnection()->WriteConnQue(new CtiSignalMsg(currentTrigger->getPointId(),0,text,additional,GeneralLogType,SignalEvent));
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }

            if( currentTrigger->getPeakPointId() == pointID )
            {
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
            RWCString text = RWCString("Automatic Threshold Kick Up");
            RWCString additional = RWCString("Threshold for Trigger: ");
            _snprintf(tempchar,80,"%d",currentTrigger->getTriggerNumber());
            additional += tempchar;
            additional += " changed in LMControlArea: ";
            additional += currentControlArea->getPAOName();
            additional += " PAO ID: ";
            _snprintf(tempchar,80,"%d",currentControlArea->getPAOId());
            additional += tempchar;
            additional += " old threshold: ";
            _snprintf(tempchar,80,"%.*f",3,oldThreshold);
            additional += tempchar;
            additional += " new threshold: ";
            _snprintf(tempchar,80,"%.*f",3,currentTrigger->getThreshold());
            additional += tempchar;
            additional += " changed because peak point value: ";
            _snprintf(tempchar,80,"%.*f",1,currentTrigger->getPointValue());
            additional += tempchar;
            CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
            {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << text << ", " << additional << endl;
            }
            }
            }
        }

                currentControlArea->setUpdatedFlag(TRUE);
            }
        }
        CtiLMGroupPtr lm_group = store->findGroupByPointID(pointID);
        if(lm_group.get() != 0)
        {   //we know this point is associated with this group,
            //figure out how and deal with it
            if( lm_group->getHoursDailyPointId() == pointID )
            {
                RWTime now;
                struct tm now_tm, timestamp_tm;

                now.extract(&now_tm);
                timestamp.extract(&timestamp_tm);

                long nowDaysSince1900 = (now_tm.tm_year*365) + now_tm.tm_yday;
                long timestampDaysSince1900 = (timestamp_tm.tm_year*365) + timestamp_tm.tm_yday;

                if( nowDaysSince1900 == timestampDaysSince1900  )
                {//i.e. is this daily control history from today or from some previous day
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
                lm_group->setGroupControlState(value);
            }

            if(lm_group->isDirty())
            {
                currentControlArea->setUpdatedFlag(TRUE);
            }
        }
#ifdef _BUNG_
        RWOrdered& lmPrograms = currentControlArea->getLMPrograms();

        for(LONG k=0;k<lmPrograms.entries();k++)
        {
            CtiLMProgramBase* currentProgram = (CtiLMProgramBase*)lmPrograms[k];
            if( currentProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                RWOrdered& lmGroups = ((CtiLMProgramDirect*)currentProgram)->getLMProgramDirectGroups();
                for(LONG l=0;l<lmGroups.entries();l++)
                {
                    CtiLMGroupBase* currentGroup = (CtiLMGroupBase*)lmGroups[l];
                    if( currentGroup->getHoursDailyPointId() == pointID )
                    {
                        RWTime now;
                        struct tm now_tm, timestamp_tm;

                        now.extract(&now_tm);
                        timestamp.extract(&timestamp_tm);

                        long nowDaysSince1900 = (now_tm.tm_year*365) + now_tm.tm_yday;
                        long timestampDaysSince1900 = (timestamp_tm.tm_year*365) + timestamp_tm.tm_yday;

                        if( nowDaysSince1900 == timestampDaysSince1900  )
                        {//i.e. is this daily control history from today or from some previous day
                            currentGroup->setCurrentHoursDaily(value);
                            currentControlArea->setUpdatedFlag(TRUE);
                        }
                        else
                        {
                            if( currentGroup->getCurrentHoursDaily() != 0 )
                                currentControlArea->setUpdatedFlag(TRUE);
                            currentGroup->setCurrentHoursDaily(0.0);
                        }
                    }
                    if( currentGroup->getHoursMonthlyPointId() == pointID )
                    {
                        currentGroup->setCurrentHoursMonthly(value);
                        currentControlArea->setUpdatedFlag(TRUE);
                    }
                    if( currentGroup->getHoursSeasonalPointId() == pointID )
                    {
                        currentGroup->setCurrentHoursSeasonal(value);
                        currentControlArea->setUpdatedFlag(TRUE);
                    }
                    if( currentGroup->getHoursAnnuallyPointId() == pointID )
                    {
                        currentGroup->setCurrentHoursAnnually(value);
                        currentControlArea->setUpdatedFlag(TRUE);
                    }
                    if( currentGroup->getControlStatusPointId() == pointID )
                    {
                        currentGroup->setGroupControlState(value);
                        currentControlArea->setUpdatedFlag(TRUE);
                    }
                }
            }
        }
#endif
    }
}

/*---------------------------------------------------------------------------
  porterReturn

  Handles porter return messages and updates the status of strategy cap
  bank controls.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::porterReturnMsg( long deviceId, RWCString commandString, int status, RWCString resultString, ULONG secondsFrom1901 )
{
    if( _LM_DEBUG & LM_DEBUG_EXTENDED )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Porter return received." << endl;
    }
}

/*---------------------------------------------------------------------------
  signalMessage

  Handles signal messages and updates strategy tags.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::signalMsg( long pointID, unsigned tags, RWCString text, RWCString additional, ULONG secondsFrom1901 )
{
    if( _LM_DEBUG & LM_DEBUG_EXTENDED )
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
}

/*---------------------------------------------------------------------------
  sendMessageToDispatch

  Sends a cti message to dispatch, this is a way for other threads to use
  the CtiLoadManager's connection to dispatch.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToDispatch( CtiMessage* message )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    try
    {
        getDispatchConnection()->WriteConnQue(message);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
  sendMessageToPIL

  Sends a cti message to pil, this is a way for other threads to use
  the CtiLoadManager's connection to pil.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToPIL( CtiMessage* message )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    try
    {
        message->resetTime();                       // CGP 5/21/04 Update its time to current time.
        getPILConnection()->WriteConnQue(message);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
  sendMessageToClients

  Sends a cti message to all the attached clients.
  ---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToClients( CtiMessage* message )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    try
    {
        CtiLMClientListener::getInstance()->BroadcastMessage(message);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*
 * loadControlLoopCParms
 * initialize control loop delay cparms that control behavior of the main loop
 * these cparms are optional.
 */
void CtiLoadManager::loadControlLoopCParms()
{
    RWCString str;
    char var[128];

    strcpy(var, "LOAD_MANAGEMENT_CONTROL_LOOP_NORMAL_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
    control_loop_delay = atoi(str);
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << var << ":  " << str << endl;
    }
    }

    strcpy(var, "LOAD_MANAGEMENT_CONTROL_LOOP_INMSG_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
    control_loop_inmsg_delay = atoi(str);
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << var << ":  " << str << endl;
    }
    }

    strcpy(var, "LOAD_MANAGEMENT_CONTROL_LOOP_OUTMSG_DELAY");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
    control_loop_outmsg_delay = atoi(str);
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << var << ":  " << str << endl;
    }
    }
}
