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

#include <rw/thr/prodcons.h>

extern BOOL _LM_DEBUG;

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
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    if ( _loadManagerThread.isValid() && _loadManagerThread.requestCancellation() == RW_THR_ABORTED )
    {
        _loadManagerThread.terminate();

        if( _LM_DEBUG )
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

    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        if( _dispatchConnection!=NULL && _dispatchConnection->valid() )
        {
            _dispatchConnection->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
            _dispatchConnection->ShutdownConnection();
        }
        if( _pilConnection!=NULL && _pilConnection->valid() )
        {
            _pilConnection->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
            _pilConnection->ShutdownConnection();
        }
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
        CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
        registerForPoints(*store->getControlAreas(RWDBDateTime().seconds()));
        store->setReregisterForPoints(false);

        RWDBDateTime currentDateTime;
        RWOrdered controlAreaChanges;
        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
        CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
        while(TRUE)
        {
            currentDateTime.now();
            ULONG secondsFromBeginningOfDay = (currentDateTime.hour() * 3600) + (currentDateTime.minute() * 60) + currentDateTime.second();
            ULONG secondsFrom1901 = currentDateTime.seconds();

            if(_LM_DEBUG)
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
                for(UINT i=0;i<controlAreas.entries();i++)
                {
                    CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

                    try
                    {
                        if( currentControlArea->isManualControlReceived() )
                        {
                            currentControlArea->handleManualControl(secondsFrom1901, multiPilMsg,multiDispatchMsg);
                        }

                        if( !currentControlArea->getDisableFlag() && currentControlArea->isControlTime(secondsFromBeginningOfDay) )
                        {
                            if( ((currentControlArea->getControlInterval() > 0) && (currentControlArea->getNextCheckTime() <= currentDateTime)) ||
                                ((currentControlArea->getControlInterval() == 0) && (currentControlArea->getNewPointDataReceivedFlag()) ) )
                            {
                                DOUBLE loadReductionNeeded = currentControlArea->calculateLoadReductionNeeded();
                                if( loadReductionNeeded > 0.0 && currentControlArea->isPastMinResponseTime(secondsFrom1901) )
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

                            if( currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                                currentControlArea->getControlAreaState() == CtiLMControlArea::ActiveState )
                            {
                                if( currentControlArea->isControlStillNeeded() )
                                {
                                    //CtiLockGuard<CtiLogger> logger_guard(dout);
                                    //dout << RWTime() << " - Maintaining current load reduction in control area: " << currentControlArea->getPAOName() << "." << endl;
                                    if( currentControlArea->maintainCurrentControl(secondsFromBeginningOfDay,secondsFrom1901,multiPilMsg,multiDispatchMsg) )
                                    {
                                        currentControlArea->setUpdatedFlag(TRUE);
                                    }
                                }
                                else if( examinedControlAreaForControlNeededFlag &&
                                         currentControlArea->isPastMinResponseTime(secondsFrom1901) )
                                {
                                    if( currentControlArea->stopAllControl(multiPilMsg,multiDispatchMsg) )
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - Load reduction no longer needed at this time, stopped all control in control area: " << currentControlArea->getPAOName() << "." << endl;
                                        currentControlArea->setUpdatedFlag(TRUE);
                                    }
                                }
                            }
                            examinedControlAreaForControlNeededFlag = FALSE;
                        }
                        else if( !currentControlArea->isControlTime(secondsFromBeginningOfDay) &&
                                 (currentControlArea->getControlAreaState() == CtiLMControlArea::FullyActiveState ||
                                  currentControlArea->getControlAreaState() == CtiLMControlArea::ActiveState) )
                        {
                            if( currentControlArea->stopAllControl(multiPilMsg,multiDispatchMsg) )
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

                    try
                    {
                        if( currentControlArea->getUpdatedFlag() )
                        {
                            controlAreaChanges.insert(currentControlArea);
                            currentControlArea->setUpdatedFlag(FALSE);
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
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
                if( multiPilMsg->getCount() > 0 )
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                    multiPilMsg->setMessagePriority(13);
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
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            rwRunnable().serviceCancellation();
            rwRunnable().sleep( 500 );
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
CtiConnection* CtiLoadManager::getDispatchConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _dispatchConnection == NULL )
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
            if( _LM_DEBUG )
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
            if( _LM_DEBUG )
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

        //Connect to Dispatch
        _dispatchConnection = new CtiConnection( dispatch_port, dispatch_host );

        //Send a registration message to Dispatch
        CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("LoadManagement", 0, false );
        _dispatchConnection->WriteConnQue( registrationMsg );
    }

    return _dispatchConnection;
}

/*---------------------------------------------------------------------------
    getPILConnection

    Returns a connection to PIL, initializes if isn't created yet.
---------------------------------------------------------------------------*/
CtiConnection* CtiLoadManager::getPILConnection()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _pilConnection == NULL )
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
            if( _LM_DEBUG )
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
            if( _LM_DEBUG )
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

        //Connect to Pil
        _pilConnection = new CtiConnection( pil_port, pil_host );

        //Send a registration message to Pil
        CtiRegistrationMsg* registrationMsg = new CtiRegistrationMsg("LoadManagement", 0, false );
        _pilConnection->WriteConnQue( registrationMsg );
    }

    return _pilConnection;
}

/*---------------------------------------------------------------------------
    checkDispatch

    Reads off the Dispatch connection and handles messages accordingly.
---------------------------------------------------------------------------*/
void CtiLoadManager::checkDispatch(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    bool done = FALSE;
    do
    {
        CtiMessage* in = (CtiMessage*) getDispatchConnection()->ReadConnQue(0);

        if ( in != 0 )
        {
            parseMessage(in,secondsFrom1901);
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
void CtiLoadManager::checkPIL(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    bool done = FALSE;
    do
    {
        CtiMessage* in = (CtiMessage*) getPILConnection()->ReadConnQue(0);

        if ( in != 0 )
        {
            parseMessage(in,secondsFrom1901);
            delete in;
        }
        else
            done = TRUE;
    }
    while(!done);
}

/*---------------------------------------------------------------------------
    registerForPoints

    Registers for all points of the control areas.
---------------------------------------------------------------------------*/
void CtiLoadManager::registerForPoints(const RWOrdered& controlAreas)
{
    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Registering for point changes." << endl;
    }

    CtiPointRegistrationMsg* regMsg = new CtiPointRegistrationMsg();
    for(UINT i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

        RWOrdered& controlAreaTriggers = currentControlArea->getLMControlAreaTriggers();

        for(UINT j=0;j<controlAreaTriggers.entries();j++)
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

        for(UINT k=0;k<lmPrograms.entries();k++)
        {
            CtiLMProgramBase* currentProgram = (CtiLMProgramBase*)lmPrograms[k];
            if( currentProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                RWOrdered& lmGroups = ((CtiLMProgramDirect*)currentProgram)->getLMProgramDirectGroups();
                for(UINT l=0;l<lmGroups.entries();l++)
                {
                    CtiLMGroupBase* currentGroup = (CtiLMGroupBase*)lmGroups[l];
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
                }
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
                      dbChange->getDatabase() == ChangeStateGroupDb ) )
                {
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
            }
            break;
        case MSG_COMMAND:
            {
                /*if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Command Message received from Dispatch" << endl;
                }*/

                cmdMsg = (CtiCommandMsg*)message;
                if( cmdMsg->getOperation() == CtiCommandMsg::AreYouThere )
                {
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Replying to Are You There message." << endl;
                    }*/
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                        getDispatchConnection()->WriteConnQue(cmdMsg->replicateMessage());
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
    if( _LM_DEBUG )
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
    for(UINT i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

        RWOrdered& controlAreaTriggers = currentControlArea->getLMControlAreaTriggers();

        for(UINT j=0;j<controlAreaTriggers.entries();j++)
        {
            CtiLMControlAreaTrigger* currentTrigger = (CtiLMControlAreaTrigger*)controlAreaTriggers[j];

            if( currentTrigger->getPointId() == pointID )
            {
                if( timestamp > currentTrigger->getLastPointValueTimestamp() )
                {
                    currentTrigger->setLastPointValueTimestamp(timestamp);
                    currentControlArea->setNewPointDataReceivedFlag(TRUE);
                }
                currentTrigger->setPointValue(value);
                if( (currentTrigger->getProjectionType() != CtiLMControlAreaTrigger::NoneProjectionType && currentTrigger->getProjectionType() != "(none)")/*"(none)" is a hack*/ &&
                    currentTrigger->getTriggerType() != CtiLMControlAreaTrigger::StatusTriggerType )
                {
                    if( quality != NonUpdatedQuality &&
                        currentControlArea->getNewPointDataReceivedFlag() )
                    {
                        if( currentTrigger->getProjectionPointEntriesQueue().entries() < currentTrigger->getProjectionPoints() )
                        {//first reading plug in multiple copies
                            ULONG pass = 1;
                            ULONG pluggedIntervalDuration = currentTrigger->getProjectAheadDuration()/4;
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
                    }
                }
                else if( currentTrigger->getTriggerType() != CtiLMControlAreaTrigger::StatusTriggerType )
                {//make the projected value equal to the real value
                    currentTrigger->setProjectedPointValue(value);
                }
                currentControlArea->setUpdatedFlag(TRUE);
            }
            else if( currentTrigger->getPeakPointId() == pointID )
            {
                if( timestamp > currentTrigger->getLastPeakPointValueTimestamp() )
                {
                    currentTrigger->setLastPeakPointValueTimestamp(timestamp);
                }
                currentTrigger->setPeakPointValue(value);
                currentControlArea->setUpdatedFlag(TRUE);
            }
        }

        RWOrdered& lmPrograms = currentControlArea->getLMPrograms();

        for(UINT k=0;k<lmPrograms.entries();k++)
        {
            CtiLMProgramBase* currentProgram = (CtiLMProgramBase*)lmPrograms[k];
            if( currentProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
            {
                RWOrdered& lmGroups = ((CtiLMProgramDirect*)currentProgram)->getLMProgramDirectGroups();
                for(UINT l=0;l<lmGroups.entries();l++)
                {
                    CtiLMGroupBase* currentGroup = (CtiLMGroupBase*)lmGroups[l];
                    if( currentGroup->getHoursDailyPointId() == pointID )
                    {
                        currentGroup->setCurrentHoursDaily(value);
                        currentControlArea->setUpdatedFlag(TRUE);
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
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    porterReturn

    Handles porter return messages and updates the status of strategy cap
    bank controls.
---------------------------------------------------------------------------*/
void CtiLoadManager::porterReturnMsg( long deviceId, RWCString commandString, int status, RWCString resultString, ULONG secondsFrom1901 )
{
    /*if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Porter return received." << endl;
    }*/
}

/*---------------------------------------------------------------------------
    signalMessage

    Handles signal messages and updates strategy tags.
---------------------------------------------------------------------------*/
void CtiLoadManager::signalMsg( long pointID, unsigned tags, RWCString text, RWCString additional, ULONG secondsFrom1901 )
{
    /*if( _LM_DEBUG )
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
    }*/
}

/*---------------------------------------------------------------------------
    sendMessageToDispatch

    Sends a cti message to dispatch, this is a way for other threads to use
    the CtiLoadManager's connection to dispatch.
---------------------------------------------------------------------------*/
void CtiLoadManager::sendMessageToDispatch( CtiMessage* message )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    getDispatchConnection()->WriteConnQue(message);
}
