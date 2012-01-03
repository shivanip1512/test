#include "precompiled.h"

#include "AttributeService.h"
#include "LitePoint.h"
#include "PointAttribute.h"
#include "ccclientlistener.h"
#include "ccexecutor.h"
#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "ccoriginalparent.h"
#include "ccid.h"
#include "logger.h"
#include "utility.h"
#include "ccutil.h"
#include "ExecutorFactory.h"

#include "MsgChangeOpState.h"
#include "ExecChangeOpState.h"

#include "MsgCapControlServerResponse.h"

using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::PaoIdList;
using Cti::CapControl::createPorterRequestMsg;
using std::endl;

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;
extern BOOL _LOG_MAPID_INFO;
extern BOOL _LIMIT_ONE_WAY_COMMANDS;

void CtiCCCommandExecutor::setAttributeService(AttributeService* attributeService)
{
    if (_attributeService != NULL)
    {
        delete _attributeService;
        _attributeService = NULL;
    }
    _attributeService = attributeService;
}

void CtiCCCommandExecutor::execute()
{
    bool success = true;

    switch ( _command->getCommandId() )
    {
    case CapControlCommand::DISABLE_SUBSTATION_BUS:
        DisableSubstationBus(_itemId);
        break;

    case CapControlCommand::ENABLE_SUBSTATION_BUS:
        EnableSubstationBus(_itemId);
        break;

    case CapControlCommand::DISABLE_FEEDER:
        DisableFeeder();
        break;

    case CapControlCommand::ENABLE_FEEDER:
        EnableFeeder();
        break;

    case CapControlCommand::DISABLE_CAPBANK:
        DisableCapBank();
        break;

    case CapControlCommand::ENABLE_CAPBANK:
        EnableCapBank();
        break;

    case CapControlCommand::CONFIRM_OPEN:
        ConfirmOpen();
        break;

    case CapControlCommand::CONFIRM_CLOSE:
        ConfirmClose();
        break;

    case CapControlCommand::REQUEST_ALL_DATA:
        SendAllData();
        break;

    case CapControlCommand::RETURN_CAP_TO_ORIGINAL_FEEDER:
        ReturnCapToOriginalFeeder();
        break;

    case CapControlCommand::RETURN_FEEDER_TO_ORIGINAL_SUBBUS:
        ReturnFeederToOriginalSubBus();
        break;
    case CapControlCommand::RESET_DAILY_OPERATIONS:
        ResetDailyOperations();
        break;

    case CapControlCommand::CONFIRM_FEEDER:
        ConfirmFeeder();
        break;

    case CapControlCommand::RESET_SYSTEM_OP_COUNTS:
        ResetAllSystemOpCounts();
        break;

    case CapControlCommand::CONFIRM_SUBSTATIONBUS:
        ConfirmSubstationBus();
        break;

    case CapControlCommand::CONFIRM_AREA:
        ConfirmArea();
        break;
    case CapControlCommand::CONFIRM_SUBSTATION:
        ConfirmSubstation();
        break;

    case CapControlCommand::ENABLE_AREA:
        EnableArea();
        break;
    case CapControlCommand::DISABLE_AREA:
        DisableArea();
        break;

    case CapControlCommand::ENABLE_SUBSTATION:
        EnableSubstation();
        break;
    case CapControlCommand::DISABLE_SUBSTATION:
        DisableSubstation();
        break;

    case CapControlCommand::ENABLE_SYSTEM:
        EnableSystem();
        break;
    case CapControlCommand::DISABLE_SYSTEM:
        DisableSystem();
        break;
    case CapControlCommand::FLIP_7010_CAPBANK:
        Flip7010Device();
        break;
    case CapControlCommand::SEND_SYNC_CBC_CAPBANK_STATE:
    case CapControlCommand::SEND_OPEN_CAPBANK:
    case CapControlCommand::SEND_CLOSE_CAPBANK:
    case CapControlCommand::SEND_ENABLE_OVUV:
    case CapControlCommand::SEND_DISABLE_OVUV:
    case CapControlCommand::SEND_SCAN_2WAY_DEVICE:
    case CapControlCommand::SEND_ENABLE_TEMPCONTROL:
    case CapControlCommand::SEND_DISABLE_TEMPCONTROL:
    case CapControlCommand::SEND_ENABLE_VARCONTROL:
    case CapControlCommand::SEND_DISABLE_VARCONTROL:
    case CapControlCommand::SEND_ENABLE_TIMECONTROL:
    case CapControlCommand::SEND_DISABLE_TIMECONTROL:
        SendAllCapBankCommands();
        break;
    case CapControlCommand::SEND_TIME_SYNC:
        SendTimeSync();
        break;
    case CapControlCommand::AUTO_ENABLE_OVUV:
        AutoEnableOvUv();
        break;
    case CapControlCommand::AUTO_DISABLE_OVUV:
        AutoDisableOvUv();
        break;
    case CapControlCommand::VOLTAGE_REGULATOR_INTEGRITY_SCAN:
    case CapControlCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE:
    case CapControlCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE:
    case CapControlCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE:
    case CapControlCommand::VOLTAGE_REGULATOR_TAP_POSITION_LOWER:
    case CapControlCommand::VOLTAGE_REGULATOR_KEEP_ALIVE_ENABLE:
    case CapControlCommand::VOLTAGE_REGULATOR_KEEP_ALIVE_DISABLE:
        sendVoltageRegulatorCommands( _command->getCommandId() );
        break;

    case CapControlCommand::CHANGE_OPERATIONALSTATE:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Error: Command, " << _command->getCommandId() << ", not not handled by this executor!" << endl;
        }
        success = false;
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - unknown command type, " << _command->getCommandId() << ", in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        success = false;
        break;
    }

    long messageId = _command->getMessageId();
    long commandResult = CtiCCServerResponse::RESULT_SUCCESS;
    string commandResultText("Command was successful.");

    if (!success)
    {
        commandResult = CtiCCServerResponse::RESULT_COMMAND_REFUSED;
        commandResultText = string("Command Failed.");
    }

    CtiCCServerResponse* msg = new CtiCCServerResponse(messageId, commandResult, commandResultText);
    msg->setUser(_command->getUser());
    CtiCCExecutorFactory::createExecutor(msg)->execute();
}

void CtiCCCommandExecutor::EnableSubstation()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstation* station = store->findSubstationByPAObjectID(_itemId);
    if (station != NULL)
    {
        string text1 = string("Manual Enable Substation");
        string additional1 = string("Substation: ");
        additional1 += station->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, station->getPaoId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(station, false);
    }

    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }

    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds())))->execute();
}

void CtiCCCommandExecutor::DisableSubstation()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec& capMessages = multiCapMsg->getData();

    CtiCCSubstation* station = store->findSubstationByPAObjectID(_itemId);
    if (station != NULL)
    {
        string text1 = string("Manual Disable Substation");
        string additional1 = string("Substation: ");
        additional1 += station->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0,0, station->getPaoId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(station, true);
        station->checkForAndStopVerificationOnChildSubBuses(capMessages);
    }

    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }

    CtiMultiMsg_vec& temp = multiCapMsg->getData( );
    for (int i = 0; i < temp.size( ); i++)
    {
        CtiCCExecutorFactory::createExecutor((CtiMessage*)temp[i])->execute();
    }
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds())))->execute();
}

/*---------------------------------------------------------------------------
    EnableSubstation
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableSubstationBus(long subBusId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subBusId);

    if (currentSubstationBus == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Unable to enable Substation Bus with ID: " << subBusId << ". It was not found." << endl;
        }
        return;
    }

    currentSubstationBus->setReEnableBusFlag(FALSE);
    currentSubstationBus->setBusUpdatedFlag(TRUE);
    store->UpdatePaoDisableFlagInDB(currentSubstationBus, false);
    string text = string("Substation Bus Enabled");
    string additional = string("Bus: ");
    additional += currentSubstationBus->getPaoName();
    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += currentSubstationBus->getMapLocationId();
        additional += " (";
        additional += currentSubstationBus->getPaoDescription();
        additional += ")";
    }
    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

    if (!currentSubstationBus->getVerificationFlag())
    {
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
    }

    LONG stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
}

/*---------------------------------------------------------------------------
    DisableSubstation
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableSubstationBus(long subBusId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subBusId);

    if (currentSubstationBus == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Unable to disable Substation Bus with ID: " << subBusId << ". It was not found." << endl;
        }
        return;
    }

    currentSubstationBus->setReEnableBusFlag(FALSE);
    currentSubstationBus->setBusUpdatedFlag(TRUE);
    store->UpdatePaoDisableFlagInDB(currentSubstationBus, true);
    string text = string("Substation Bus Disabled");
    string additional = string("Bus: ");
    additional += currentSubstationBus->getPaoName();
    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += currentSubstationBus->getMapLocationId();
        additional += " (";
        additional += currentSubstationBus->getPaoDescription();
        additional += ")";
    }
    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

    if (!currentSubstationBus->getVerificationFlag())
    {
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
    }

    LONG stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
}

/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _itemId;
    BOOL found = FALSE;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPaoId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    store->UpdatePaoDisableFlagInDB(currentFeeder, false);
                    string text("Feeder Enabled");
                    string additional("Feeder: ");
                    additional += currentFeeder->getPaoName();
                    if (_LOG_MAPID_INFO)
                    {
                        additional += " MapID: ";
                        additional += currentFeeder->getMapLocationId();
                        additional += " (";
                        additional += currentFeeder->getPaoDescription();
                        additional += ")";
                    }
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                      <<".  Cannot perform ENABLE on Feeder: " << currentFeeder->getPaoName() << " PAOID: " << currentFeeder->getPaoId() << "."<<endl;
                }
                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    DisableFeeder
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _itemId;
    BOOL found = FALSE;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPaoId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    store->UpdatePaoDisableFlagInDB(currentFeeder, true);
                    string text("Feeder Disabled");
                    string additional("Feeder: ");
                    additional += currentFeeder->getPaoName();
                    if (_LOG_MAPID_INFO)
                    {
                        additional += " MapID: ";
                        additional += currentFeeder->getMapLocationId();
                        additional += " (";
                        additional += currentFeeder->getPaoDescription();
                        additional += ")";
                    }
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                      <<".  Cannot perform DISABLE on Feeder: " << currentFeeder->getPaoName() << " PAOID: " << currentFeeder->getPaoId() << "."<<endl;
                }
                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    EnableCapBank
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG capBankID = _itemId;
    BOOL found = FALSE;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPaoId() )
                {
                    if (!currentSubstationBus->getVerificationFlag())
                    {
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        store->UpdatePaoDisableFlagInDB(currentCapBank, false);
                        string text("Cap Bank Enabled");
                        string additional("Cap Bank: ");
                        additional += currentCapBank->getPaoName();
                        if (_LOG_MAPID_INFO)
                        {
                            additional += " MapID: ";
                            additional += currentCapBank->getMapLocationId();
                            additional += " (";
                            additional += currentCapBank->getPaoDescription();
                            additional += ")";
                        }
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                          << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                         <<".  Cannot perform ENABLE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                    }

                    found = TRUE;
                    break;
                }
            }
            if( found )
                break;
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    DisableCapBank
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG capBankID = _itemId;
    BOOL found = FALSE;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPaoId() )
                {
                    if (!currentSubstationBus->getVerificationFlag())
                    {
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        store->UpdatePaoDisableFlagInDB(currentCapBank, true);
                        string text("Cap Bank Disabled");
                        string additional("Cap Bank: ");
                        additional += currentCapBank->getPaoName();
                        if (_LOG_MAPID_INFO)
                        {
                            additional += " MapID: ";
                            additional += currentCapBank->getMapLocationId();
                            additional += " (";
                            additional += currentCapBank->getPaoDescription();
                            additional += ")";
                        }
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                          << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                         <<".  Cannot perform DISABLE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;

                    }
                    found = TRUE;
                    break;
                }
            }
            if( found )
                break;
        }
        if( found )
            break;
    }
}

void CtiCCCommandExecutor::syncCbcAndCapBankStates(long bankId)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    string commandName = " Sync CBC and CapBank States";

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if ( !capBank->isControlDeviceTwoWay() )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be two way. " << endl;
        return;
    }
    if ( capBank->getDisableFlag() )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName <<" command rejected, CapBank: "<< capBank->getPaoName()<<" is Disabled." << endl;
        return;
    }
    if ( capBank->isPendingStatus() )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName <<" command rejected, CapBank: "<< capBank->getPaoName()<<" is in a Pending State." << endl;
        return;
    }

    //Logging Action
    string text = string("Manual CBC/CapBank State Sync.  Adjusting CapBank State from: ");
    text += capBank->getControlStatusText() + " to ";
    text += ( capBank->getTwoWayPoints()->getPointValueByAttribute(PointAttribute::CapacitorBankState) ? "Close." : "Open.");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    capBank->setControlStatus(capBank->getTwoWayPoints()->getPointValueByAttribute(PointAttribute::CapacitorBankState));

    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);
    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capBankStateUpdate, seqId, capBank->getControlStatus(), text, _command->getUser()));

    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(subBus))->execute();
}

/*---------------------------------------------------------------------------
    Enable OV/UV
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::enableOvUv(long bankId,
                                      std::vector<CtiSignalMsg*>& signals,
                                      std::vector<CtiCCEventLogMsg*>& events,
                                      std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable OvUv";
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    bool cbc702 = false;
    if (stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        cbc702 = TRUE;
    }

    //Logging Action
    string text = string("Cap Bank OV/UV Enabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);
    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    capBank->setOvUvDisabledFlag(false);
    subBus->setBusUpdatedFlag(true);

    CtiRequestMsg* reqMsg = NULL;
    if (cbc702)
    {
        CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

        int offset = 1;
        unsigned char voltageValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::VoltageControl);

        /* Hardware Code
            struct
            {
                INT8U EnableVOltControl:1;//LSB
                INT8U VoltAlarmEn:1;
                INT8U Frequenct:1;
                INT8U unused:5;
            }VoltControl;
        */

        //Changing the LSB to 1
        voltageValue |= 0x01;

        //Send point update message with new value.
        string commandString = "putvalue analog " + CtiNumStr(offset).toString() + " " + CtiNumStr((int)voltageValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandString);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig ovuv enable");
    }
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}


/*---------------------------------------------------------------------------
    Disable OV/UV
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::disableOvUv(long bankId,
                                       std::vector<CtiSignalMsg*>& signals,
                                       std::vector<CtiCCEventLogMsg*>& events,
                                       std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable OvUv";
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    bool cbc702 = false;
    if (stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        cbc702 = TRUE;
    }

    //Logging Action
    string text = string("Cap Bank OV/UV Disabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command
    capBank->setOvUvDisabledFlag(true);
    subBus->setBusUpdatedFlag(true);

    CtiRequestMsg* reqMsg = NULL;
    if (cbc702)
    {
        CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

        int offset = 1;
        unsigned char voltageValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::VoltageControl);

        /* Hardware Code
            struct
            {
                INT8U EnableVOltControl:1;//LSB
                INT8U VoltAlarmEn:1;
                INT8U Frequenct:1;
                INT8U unused:5;
            }VoltControl;
        */

        //Zeroing LSB
        voltageValue &= 0xfe;

        //Send point update message with new value.
        string commandString = "putvalue analog " + CtiNumStr(offset).toString() + " " + CtiNumStr((int)voltageValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandString);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig ovuv disable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}

void CtiCCCommandExecutor::enableTempControl(long bankId,std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable Temp Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if (!stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be a 702x. " << endl;
        return;
    }

    //Logging Action
    string text = string("Cap Bank Temp Control Enabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    //Compute new value.
    CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

    /* Hardware Code
    struct
    {
        INT8U TimedCtrlEn :1;    //LSB
        INT8U SetONOFFCtrl: 1;
        INT8U SaturdayCtrlEn : 1;
        INT8U SundayCtrlEn  : 1;
        INT8U HolidayEn:1;
        INT8U unused: 2;
        INT8U TempCtrlEn : 1;
    }TimeTempFlags;
    */

    int offsetOne = 26;
    unsigned char seasonOneValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::TimeTempSeasonOne);

    //Changing the MSB to 1
    seasonOneValue |= 0x80;

    //Send point update message with new value.
    CtiRequestMsg* reqMsg = NULL;
    string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    int offsetTwo = 42;
    unsigned char seasonTwoValue = points->getPointValueByAttribute(PointAttribute::TimeTempSeasonTwo);

    //Changing the MSB to 1
    seasonTwoValue |= 0x80;

    string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

        return;
}

void CtiCCCommandExecutor::disableTempControl(long bankId,std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable Temp Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if (!stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be a 702x. " << endl;
        return;
    }

    //Logging Action
    string text = string("Cap Bank Temp Control Disabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    //Compute new value.
    CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

    int offsetOne = 26;
    unsigned char seasonOneValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::TimeTempSeasonOne);

    //Zeroing the MSB
    seasonOneValue &= 0x7f;

    //Send point update message with new value.
    CtiRequestMsg* reqMsg = NULL;
    string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    int offsetTwo = 42;
    unsigned char seasonTwoValue = points->getPointValueByAttribute(PointAttribute::TimeTempSeasonTwo);

    //Zeroing the MSB
    seasonTwoValue &= 0x7f;

    string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}

void CtiCCCommandExecutor::enableVarControl(long bankId,std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable Var Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if (!stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be a 702x. " << endl;
        return;
    }

    //Logging Action
    string text = string("Cap Bank Var Control Enabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    //Compute new value.
    CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

    int offset = 68;
    unsigned char varValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::VarControl);

    /* Hardware Code
        struct
        {
            INT8U EnableVARControl:1;        //LSB
            INT8U SamplingMethod:1;
            INT8U VARAlarmEnable:1;
            INT8U unused:5;
        }VARControl;
    */

    //Changing the LSB to 1
    varValue |= 0x01;

    //Send point update message with new value.
    CtiRequestMsg* reqMsg = NULL;
    string commandString = "putvalue analog " + CtiNumStr(offset).toString() + " " + CtiNumStr((int)varValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandString);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}

void CtiCCCommandExecutor::disableVarControl(long bankId,std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable Var Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if (!stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be a 702x. " << endl;
        return;
    }

    //Logging Action
    string text = string("Cap Bank Var Control Disabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    //Compute new value.
    CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

    int offset = 68;
    unsigned char varValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::VarControl);

    /* Hardware Code
        struct
        {
            INT8U EnableVARControl:1;        //LSB
            INT8U SamplingMethod:1;
            INT8U VARAlarmEnable:1;
            INT8U unused:5;
        }VARControl;
    */

    //Zeroing LSB
    varValue &= 0xfe;

    //Send point update message with new value.
    CtiRequestMsg* reqMsg = NULL;
    string commandString = "putvalue analog " + CtiNumStr(offset).toString() + " " + CtiNumStr((int)varValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandString);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}

void CtiCCCommandExecutor::enableTimeControl(long bankId,std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable Time Control";
    bool implemented = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if (!stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be a 702x. " << endl;
        return;
    }

    //Logging Action
    string text = string("Cap Bank Time Control Enabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    //Compute new value.
    CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

    /* Hardware Code
    struct
    {
        INT8U TimedCtrlEn :1;    //LSB
        INT8U SetONOFFCtrl: 1;
        INT8U SaturdayCtrlEn : 1;
        INT8U SundayCtrlEn  : 1;
        INT8U HolidayEn:1;
        INT8U unused: 2;
        INT8U TempCtrlEn : 1;
    }TimeTempFlags;
    */

    int offsetOne = 26;
    unsigned char seasonOneValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::TimeTempSeasonOne);

    //Changing the LSB to 1
    seasonOneValue |= 0x01;

    //Send point update message with new value.
    CtiRequestMsg* reqMsg = NULL;
    string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    int offsetTwo = 42;
    unsigned char seasonTwoValue = points->getPointValueByAttribute(PointAttribute::TimeTempSeasonTwo);

    //Changing the LSB to 1
    seasonTwoValue |= 0x01;

    string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}

void CtiCCCommandExecutor::disableTimeControl(long bankId,std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable Time Control";
    bool implemented = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Bank not found with id: " << bankId << endl;
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, Controller id of 0." << endl;
        return;
    }

    if (!stringContainsIgnoreCase( capBank->getControlDeviceType(),"CBC 702"))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << commandName << " command rejected, CBC must be a 702x. " << endl;
        return;
    }

    //Logging Action
    string text = string("Cap Bank Time Control Disabled");
    string additional = string("Cap Bank: ");
    additional += capBank->getPaoName();

    if (_LOG_MAPID_INFO)
    {
        additional += " MapID: ";
        additional += capBank->getMapLocationId();
        additional += " (";
        additional += capBank->getPaoDescription();
        additional += ")";
    }

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(new CtiCCEventLogMsg(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    //Compute new value.
    CtiCCTwoWayPoints* points = capBank->getTwoWayPoints();

    int offsetOne = 26;
    unsigned char seasonOneValue = (unsigned char)points->getPointValueByAttribute(PointAttribute::TimeTempSeasonOne);

    //Zeroing the LSB
    seasonOneValue &= 0xfe;

    //Send point update message with new value.
    CtiRequestMsg* reqMsg = NULL;
    string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    int offsetTwo = 42;
    unsigned char seasonTwoValue = points->getPointValueByAttribute(PointAttribute::TimeTempSeasonTwo);

    //Zeroing the LSB
    seasonTwoValue &= 0xfe;

    string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
    reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    reqMsg->setSOE(5);
    requests.push_back(reqMsg);

    return;
}

void CtiCCCommandExecutor::queueCapBankTimeSyncPilMessages(CtiMultiMsg_vec& pilMessages, CapBankList capBanks)
{
    static const std::string timeSyncCommand     = "putconfig timesync";
    static const std::string xcomTimeSyncCommand = "putconfig xcom timesync";

    for each( CtiCCCapBankPtr capBank in capBanks )
    {
        int controlID = capBank->getControlDeviceId();
        if( controlID > 0 )
        {
            if(capBank->isExpresscom())
            {
                pilMessages.push_back(createPorterRequestMsg(controlID, xcomTimeSyncCommand, _command->getUser()));
            }
            else
            {
                pilMessages.push_back(createPorterRequestMsg(controlID, timeSyncCommand, _command->getUser()));
            }
        }
    }
}

void CtiCCCommandExecutor::SendTimeSync()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG paoId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    Cti::CapControl::CapControlType type = store->determineTypeById(paoId);
    CapBankList capBanks = store->getCapBanksByPaoIdAndType(paoId, type);

    printOutEventLogsByIdAndType(paoId, type, " Time Sync", _command->getUser(), pointChanges, ccEvents);
    queueCapBankTimeSyncPilMessages(pilMessages, capBanks);

    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0 )
    {
        CtiCapController::getInstance()->confirmCapBankControl( multiPilMsg, multi );
    }
    else
    {
        delete multiPilMsg;
        delete multi;
    }

    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }
}

void CtiCCCommandExecutor::SendAllCapBankCommands()
{
    long paoId = _itemId;
    long controlID = 0;
    bool found = FALSE;
    string actionText = "";
    long action = CapControlCommand::UNDEFINED;

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    //Find the object type
    Cti::CapControl::CapControlType type = store->determineTypeById(paoId);

    //Special Case for SpecialAreas. If it is disabled, no commands go out.
    if (type == Cti::CapControl::SpecialArea)
    {
        CtiCCSpecialPtr sArea = store->findSpecialAreaByPAObjectID(paoId);

        if (sArea->getDisableFlag())
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Special Area is not enabled, will not set OVUV." << endl;
            }

            CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::RESP_UNSOLICITED,CtiCCServerResponse::RESULT_COMMAND_REFUSED, "Special Area is not enabled.");
            msg->setUser(_command->getUser());
            CtiCCExecutorFactory::createExecutor(msg)->execute();

            return;
        }
    }

    if (type == Cti::CapControl::Undefined)
    {
        //Error
        return;
    }

    //Main work of this function, split the command to every bank attached to paoId
    std::vector<CtiSignalMsg*> signals;
    std::vector<CtiCCEventLogMsg*> events;
    std::vector<CtiRequestMsg*> requests;

    CapBankList banks = store->getCapBanksByPaoIdAndType(paoId,type);
    for each(CtiCCCapBankPtr bank in banks)
    {
        CtiCCFeederPtr feeder = store->findFeederByPAObjectID(bank->getParentId());
        CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(feeder->getParentId());
        long bankId = bank->getPaoId();

        //Execute per capbank
        switch (_command->getCommandId())
        {
            case CapControlCommand::SEND_OPEN_CAPBANK:
            {
                actionText = string(" - Open");
                OpenCapBank(bankId);
                break;
            }
            case CapControlCommand::SEND_CLOSE_CAPBANK:
            {
                actionText = string(" - Close");
                CloseCapBank(bankId);
                break;
            }
            case CapControlCommand::SEND_ENABLE_OVUV:
            {
                actionText = string(" - Enable OvUv");
                enableOvUv(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_DISABLE_OVUV:
            {
                actionText = string(" - Disable OvUv");
                disableOvUv(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_ENABLE_TEMPCONTROL:
            {
                actionText = string(" - Enable Temperature Control");
                enableTempControl(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_DISABLE_TEMPCONTROL:
            {
                actionText = string(" - Disable Temperature Control");
                disableTempControl(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_ENABLE_VARCONTROL:
            {
                actionText = string(" - Enable Var Control");
                enableVarControl(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_DISABLE_VARCONTROL:
            {
                actionText = string(" - Disable Var Control");
                disableVarControl(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_ENABLE_TIMECONTROL:
            {
                actionText = string(" - Enable Time Control");
                enableTimeControl(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_DISABLE_TIMECONTROL:
            {
                actionText = string(" - Disable Time Control");
                disableTimeControl(bankId,signals,events,requests);
                break;
            }
            case CapControlCommand::SEND_SYNC_CBC_CAPBANK_STATE:
            {
                actionText = string(" - Sync CBC and CapBank States");
                syncCbcAndCapBankStates(bankId);
                break;
            }
            case CapControlCommand::SEND_SCAN_2WAY_DEVICE:
            {
                actionText = string(" - Integrity Scan");
                Scan2WayDevice(bankId);
                break;
            }
            default:
            {
                //This shouldn't happen since the main executor only calls this if it is an above case
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Send All Command Type: " << _command->getCommandId() << " is not supported."  << endl;
                }
                return;
            }
        }
    }

    //Bookwork time
    //This only has to happen for OVUV Currently. There are no flags to set for a scan.
    if (_command->getCommandId()  == CapControlCommand::SEND_ENABLE_OVUV || _command->getCommandId()  == CapControlCommand::SEND_DISABLE_OVUV)
    {
        //Setting the flag on the all parent objects related to the banks we just set.
        bool ovuvFlag = (_command->getCommandId()  == CapControlCommand::SEND_ENABLE_OVUV)?false:true;
        setParentOvUvFlags(paoId,type,ovuvFlag,modifiedSubsList);
    }

    //Happens for all send alls
    printOutEventLogsByIdAndType(paoId,type,actionText,_command->getUser(), pointChanges, ccEvents);

    for each(CtiCCEventLogMsg* message in events)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(message);
    }
    events.clear();

    for each(CtiRequestMsg* message in requests)
    {
        CtiCapController::getInstance()->manualCapBankControl(message,new CtiMultiMsg());
    }
    requests.clear();

    for each(CtiSignalMsg* message in signals)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(message);
    }
    signals.clear();

    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,
                                                                   CtiCCSubstationBusMsg::SubBusModified))->execute();

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->confirmCapBankControl(NULL, multi);
    }
    else
    {
        delete multi;
    }
    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }

}

void CtiCCCommandExecutor::setParentOvUvFlags(int paoId, Cti::CapControl::CapControlType type, bool ovuvFlag, CtiMultiMsg_vec& modifiedSubBuses)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    switch (type)
    {
        case Cti::CapControl::SpecialArea:
        {
            CtiCCSpecialPtr sArea = store->findSpecialAreaByPAObjectID(paoId);
            sArea->setOvUvDisabledFlag(ovuvFlag);

            PaoIdList* stationIds = sArea->getSubstationIds();
            for each(long stationId in *stationIds)
            {
                CtiCCSubstationPtr station = store->findSubstationByPAObjectID(stationId);
                station->setOvUvDisabledFlag(ovuvFlag);

                PaoIdList* subBusIds = station->getCCSubIds();
                for each(long subBusId in *subBusIds)
                {
                    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subBusId);
                    subBus->setOvUvDisabledFlag(ovuvFlag);
                    modifiedSubBuses.push_back(subBus);

                    CtiFeeder_vec feeders = subBus->getCCFeeders();
                    for each(CtiCCFeederPtr feeder in feeders)
                    {
                        feeder->setOvUvDisabledFlag(ovuvFlag);
                    }
                }
            }
            break;
        }
        case Cti::CapControl::Area:
        {
            CtiCCAreaPtr area = store->findAreaByPAObjectID(paoId);
            area->setOvUvDisabledFlag(ovuvFlag);

            PaoIdList* stationIds = area->getSubStationList();
            for each(long stationId in *stationIds)
            {
                CtiCCSubstationPtr station = store->findSubstationByPAObjectID(stationId);
                station->setOvUvDisabledFlag(ovuvFlag);

                PaoIdList* subBusIds = station->getCCSubIds();
                for each(long subBusId in *subBusIds)
                {
                    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subBusId);
                    subBus->setOvUvDisabledFlag(ovuvFlag);
                    modifiedSubBuses.push_back(subBus);

                    CtiFeeder_vec feeders = subBus->getCCFeeders();
                    for each(CtiCCFeederPtr feeder in feeders)
                    {
                        feeder->setOvUvDisabledFlag(ovuvFlag);
                    }
                }
            }
            break;
        }
        case Cti::CapControl::Substation:
        {
            CtiCCSubstationPtr station = store->findSubstationByPAObjectID(paoId);
            station->setOvUvDisabledFlag(ovuvFlag);

            PaoIdList* subBusIds = station->getCCSubIds();
            for each(long subBusId in *subBusIds)
            {
                CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subBusId);
                subBus->setOvUvDisabledFlag(ovuvFlag);
                modifiedSubBuses.push_back(subBus);

                CtiFeeder_vec feeders = subBus->getCCFeeders();
                for each(CtiCCFeederPtr feeder in feeders)
                {
                    feeder->setOvUvDisabledFlag(ovuvFlag);
                }
            }
            break;
        }
        case Cti::CapControl::SubBus:
        {
            CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(paoId);
            subBus->setOvUvDisabledFlag(ovuvFlag);
            modifiedSubBuses.push_back(subBus);

            CtiFeeder_vec feeders = subBus->getCCFeeders();
            for each(CtiCCFeederPtr feeder in feeders)
            {
                feeder->setOvUvDisabledFlag(ovuvFlag);
            }
            break;
        }
        case Cti::CapControl::Feeder:
        {
            CtiCCFeederPtr feeder = store->findFeederByPAObjectID(paoId);
            feeder->setOvUvDisabledFlag(ovuvFlag);

            CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(feeder->getParentId());
            subBus->setOvUvDisabledFlag(ovuvFlag);
            modifiedSubBuses.push_back(subBus);
            break;
        }
        case Cti::CapControl::CapBank:
        default:
        {
            //No parents need to be updated.
            break;
        }
    }
}

void CtiCCCommandExecutor::printOutEventLogsByIdAndType(int paoId, Cti::CapControl::CapControlType type, const string& actionText, const string& userName,
                                                        CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    string objectName;
    string objectType;
    int areaId,spAreaId;
    bool found = true;

    //Print out to the event logs what happened here.
    switch (type)
    {
        case Cti::CapControl::SpecialArea:
        {
            CtiCCSpecialPtr area = store->findSpecialAreaByPAObjectID(paoId);
            if (area == NULL)
            {
                found = false;
                break;
            }

            objectType = "SpecialArea";
            objectName = area->getPaoName();
            areaId = 0;
            spAreaId = area->getPaoId();

            break;
        }
        case Cti::CapControl::Area:
        {
            CtiCCAreaPtr area = store->findAreaByPAObjectID(paoId);
            if (area == NULL)
            {
                found = false;
                break;
            }

            objectType = "Area";
            objectName = area->getPaoName();
            areaId = area->getPaoId();
            spAreaId = 0;

            break;
        }
        case Cti::CapControl::Substation:
        {
            CtiCCSubstationPtr station = store->findSubstationByPAObjectID(paoId);
            if (station == NULL)
            {
                found = false;
                break;
            }

            objectType = "Substation";
            objectName = station->getPaoName();
            areaId = station->getSaEnabledFlag()?0:station->getParentId();
            spAreaId = station->getSaEnabledFlag()?station->getSaEnabledId():0;

            break;
        }
        case Cti::CapControl::SubBus:
        {
            CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(paoId);
            CtiCCSubstationPtr station = store->findSubstationByPAObjectID(subBus->getParentId());

            if (subBus == NULL || station == NULL)
            {
                found = false;
                break;
            }

            objectType = "SubBus";
            objectName = subBus->getPaoName();
            areaId = station->getSaEnabledFlag()?0:station->getParentId();
            spAreaId = station->getSaEnabledFlag()?station->getSaEnabledId():0;

            break;
        }
        case Cti::CapControl::Feeder:
        {
            CtiCCFeederPtr feeder = store->findFeederByPAObjectID(paoId);
            CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(feeder->getParentId());
            CtiCCSubstationPtr station = store->findSubstationByPAObjectID(subBus->getParentId());

            if (feeder == NULL || subBus == NULL || station == NULL)
            {
                found = false;
                break;
            }

            objectType = "Feeder";
            objectName = feeder->getPaoName();
            areaId = station->getSaEnabledFlag()?0:station->getParentId();
            spAreaId = station->getSaEnabledFlag()?station->getSaEnabledId():0;

            break;
        }
        case Cti::CapControl::CapBank:
        default:
        {
            found = false;
            break;
        }
    }

    if (found)
    {
        string text1 = objectType + string(": ");
        text1 += objectName;
        text1 += actionText;
        text1 += string(" All CapBanks");
        string additional1 = objectType + string(": ");
        additional1 += objectName;
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,userName));

        std::vector<CtiCCSubstationBusPtr> subBuses = store->getSubBusesByCapControlByIdAndType(paoId,type);
        for each(CtiCCSubstationBusPtr subBus in subBuses)
        {
            int stationId = subBus->getParentId();
            subBus->setEventSequence(subBus->getEventSequence() +1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, subBus->getPaoId(), 0, capControlManualCommand, subBus->getEventSequence(), 0, text1, userName));
        }
    }

    return;
}

bool CtiCCCommandExecutor::checkForCommandRefusal(CtiCCFeeder* feeder)
{
    CtiCCCapBank_SVector& ccCapBanks = feeder->getCCCapBanks();

    for(LONG k=0;k<ccCapBanks.size();k++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
        int status = currentCapBank->getControlStatus();
        if (status == CtiCCCapBank::OpenPending || status == CtiCCCapBank::ClosePending)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Feeder already has bank: "<<currentCapBank->getPaoName() <<" in a pending state." << endl;
            }

            CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::RESP_UNSOLICITED, CtiCCServerResponse::RESULT_COMMAND_REFUSED, string("Command Refused: " + currentCapBank->getPaoName() + " is already in a Pending State."));
            msg->setUser(_command->getUser());
            CtiCCExecutorFactory::createExecutor(msg)->execute();

            return true;
        }
    }

    return false;
}

/*---------------------------------------------------------------------------
    OpenCapBank
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::OpenCapBank(long bankId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCSubstationPtr parentStation = NULL;
    CtiCCSubstation_vec updatedStations;


    if (bankId != 0)
    {

        for(LONG i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankId == currentCapBank->getPaoId() )
                    {
                        found = TRUE;
                        controlID = currentCapBank->getControlDeviceId();
                        if(!currentCapBank->getSendAllCommandFlag() &&
                           checkForCommandRefusal(currentFeeder))
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Open Capbank Command Refused" << endl;
                            }
                            break;
                        }
                        if (!currentCapBank->getSendAllCommandFlag())
                            updatedSubs.push_back(currentSubstationBus);

                        updatedSubs.push_back(currentSubstationBus);

                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue(), currentFeeder->getOriginalParent().getOriginalParentId());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());

                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";
                                string additional("Sub: ");
                                additional += currentSubstationBus->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPaoDescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPaoDescription();
                                    additional += ")";
                                }
                                string text("Manual Open Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                                    ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }

                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( !savedBusRecentlyControlledFlag ||
                                    (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());

                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(),
                                                                        currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(),
                                                                        currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange,
                                                                        currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));

                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }


                            BOOL confirmImmediately = FALSE;
                            if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::TimeOfDayControlMethod)||
                                currentCapBank->getSendAllCommandFlag() )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) )
                            {
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) ||
                                     ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                            {
                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }

                            if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                            {
                                currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                currentFeeder->setRecentlyControlledFlag(TRUE);
                            }
                            else if (confirmImmediately)
                            {
                                currentCapBank->setSendAllCommandFlag(FALSE);
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, controlID);
                            }
                            currentSubstationBus->verifyControlledStatusFlags();
                            parentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (parentStation != NULL && currentSubstationBus->getRecentlyControlledFlag())
                            {
                                parentStation->setRecentlyControlledFlag(TRUE);
                                updatedStations.push_back(parentStation);
                            }
                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }
                        }
                        break;
                    }
                }
                if( found )
                {
                    break;
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Control Open received for BankID 0...Cannot control device."<<endl;
        }
    }


    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs))->execute();
        }
        if (updatedStations.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(updatedStations))->execute();
        }
    }
    else
    {
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}


/*---------------------------------------------------------------------------
    CloseCapBank
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::CloseCapBank(long bankId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCSubstationPtr parentStation = NULL;
    CtiCCSubstation_vec updatedStations;

    if (bankId != 0)
    {

        for(LONG i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankId == currentCapBank->getPaoId() )
                    {
                        found = TRUE;
                        controlID = currentCapBank->getControlDeviceId();

                        if(!currentCapBank->getSendAllCommandFlag() &&
                           checkForCommandRefusal(currentFeeder))
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Close Capbank Command Refused" << endl;
                            }
                            break;
                        }
                        if (!currentCapBank->getSendAllCommandFlag())
                            updatedSubs.push_back(currentSubstationBus);

                        currentCapBank->setSendAllCommandFlag(FALSE);


                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {

                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            //currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue(), currentFeeder->getOriginalParent().getOriginalParentId());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";
                                string additional("Sub: ");
                                additional += currentSubstationBus->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPaoDescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPaoDescription();
                                    additional += ")";
                                }
                                string text("Manual Close Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                BOOL confirmImmediately = FALSE;
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::TimeOfDayControlMethod) ||
                                    currentCapBank->getSendAllCommandFlag())
                                {
                                    confirmImmediately = TRUE;
                                }
                                else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) )
                                {
                                    if( savedFeederRecentlyControlledFlag ||
                                        ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                                    {
                                        confirmImmediately = TRUE;
                                    }
                                    if( _IGNORE_NOT_NORMAL_FLAG &&
                                        currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                    {
                                        confirmImmediately = TRUE;
                                    }
                                }
                                else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) ||
                                         ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                                {
                                    if( savedBusRecentlyControlledFlag ||
                                        ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                    {
                                        confirmImmediately = TRUE;
                                    }
                                    if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) &&
                                        _IGNORE_NOT_NORMAL_FLAG &&
                                        currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                    {
                                        confirmImmediately = TRUE;
                                    }
                                    if( (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod)) &&
                                        _IGNORE_NOT_NORMAL_FLAG &&
                                        (currentFeeder->getCurrentVarPointQuality() != NormalQuality) )
                                    {
                                        confirmImmediately = TRUE;
                                    }

                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                                  << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;

                                }

                                if (confirmImmediately)
                                {
                                    doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, controlID);
                                    currentCapBank->setSendAllCommandFlag(FALSE);

                                }
                                else
                                {

                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                    currentFeeder->setRecentlyControlledFlag(TRUE);

                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                    if( !savedBusRecentlyControlledFlag ||
                                        (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                                    {
                                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                                        if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                                            ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                                        {
                                            kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                            kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                        }
                                        else
                                        {
                                            kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                            kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                        }

                                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                        currentCapBank->setLastStatusChangeTime(CtiTime());
                                    }
                                    currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                    currentCapBank->setAfterVarsString(" --- ");
                                    currentCapBank->setPercentChangeString(" --- ");


                                    INT seqId = CCEventSeqIdGen();
                                    currentSubstationBus->setEventSequence(seqId);
                                    currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                    string stateInfo = currentCapBank->getControlStatusQualityString();
                                    LONG stationId, areaId, spAreaId;
                                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                    ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));

                            }


                            currentSubstationBus->verifyControlledStatusFlags();
                            parentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (parentStation != NULL && currentSubstationBus->getRecentlyControlledFlag())
                            {
                                parentStation->setRecentlyControlledFlag(TRUE);
                                updatedStations.push_back(parentStation);
                            }

                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }
                        }
                        break;
                    }
                }
                if( found )
                {
                    break;
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Control Close received for BankID 0...Cannot control device."<<endl;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs))->execute();
        }
        if (updatedStations.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(updatedStations))->execute();
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


void CtiCCCommandExecutor::ControlAllCapBanks(LONG paoId, int control)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCSpecialPtr spArea = store->findSpecialAreaByPAObjectID(paoId);
    CtiCCAreaPtr area = store->findAreaByPAObjectID(paoId);
    CtiCCSubstationPtr station = store->findSubstationByPAObjectID(paoId);
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(paoId);
    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(paoId);

    if (currentFeeder != NULL)
    {
        if (!currentFeeder->getDisableFlag())
        {

            ControlAllCapBanksByFeeder(currentFeeder->getPaoId(), control,
                                   pilMessages, pointChanges, ccEvents);
        }
    }
    else if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
    {

        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if (!currentFeeder->getDisableFlag())
            {
                ControlAllCapBanksByFeeder(currentFeeder->getPaoId(), control,
                                       pilMessages, pointChanges, ccEvents);
            }
        }
    }
    else if (station != NULL && !station->getDisableFlag())
    {
        Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
        while (iterBus  != station->getCCSubIds()->end())
        {
            LONG busId = *iterBus;
            iterBus++;
            currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
            {
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                    if (!currentFeeder->getDisableFlag() )
                    {
                        ControlAllCapBanksByFeeder(currentFeeder->getPaoId(), control,
                                               pilMessages, pointChanges, ccEvents);
                    }
                }
            }
        }
    }
    else if (area != NULL && !area->getDisableFlag())
    {
        PaoIdList::iterator subIter = area->getSubStationList()->begin();

        while (subIter != area->getSubStationList()->end())
        {
            station = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (station != NULL && !station->getDisableFlag())
            {
                Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                {
                    LONG busId = *iterBus;
                    iterBus++;
                    currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
                    {
                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                            if (!currentFeeder->getDisableFlag() )
                            {
                                ControlAllCapBanksByFeeder(currentFeeder->getPaoId(), control,
                                                       pilMessages, pointChanges, ccEvents);
                            }
                        }
                    }
                }
            }
        }

    }
    else if (spArea != NULL && !spArea->getDisableFlag())
    {
        PaoIdList::iterator subIter = spArea->getSubstationIds()->begin();

        while (subIter != spArea->getSubstationIds()->end())
        {
            station = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (station != NULL && !station->getDisableFlag())
            {
                Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                {
                    LONG busId = *iterBus;
                    iterBus++;
                    currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL && !currentSubstationBus->getDisableFlag())
                    {
                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                            if (!currentFeeder->getDisableFlag() )
                            {
                                ControlAllCapBanksByFeeder(currentFeeder->getPaoId(), control,
                                                       pilMessages, pointChanges, ccEvents);
                            }
                        }
                    }
                }
            }
        }

    }

    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;

    if (updatedSubs.size() > 0)
    {
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs))->execute();
    }
}


void CtiCCCommandExecutor::ControlAllCapBanksByFeeder(LONG feederId, int control, CtiMultiMsg_vec& pilMessages,
                                                      CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;

    BOOL found = FALSE;

    CtiCCSubstationBus_vec updatedSubs;

    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(feederId);
    if (currentFeeder != NULL)
    {
        CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
        if (currentSubstationBus != NULL)
        {
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( !currentCapBank->getDisableFlag() &&
                    currentCapBank->getControlDeviceId() > 0 &&
                    ( ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) ||
                      ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::StandAloneState)) )

                {
                    updatedSubs.push_back(currentSubstationBus);
                    if (!currentSubstationBus->getVerificationFlag() &&
                        currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                    {
                        controlID = currentCapBank->getControlDeviceId();
                        if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                            currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                        currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());
                        if (control == CtiCCCapBank::Close || control == CtiCCCapBank::CloseQuestionable ||
                            control == CtiCCCapBank::CloseFail || control == CtiCCCapBank::ClosePending )
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                   currentCapBank, TRUE);
                        if (control == CtiCCCapBank::Open || control == CtiCCCapBank::OpenQuestionable ||
                            control == CtiCCCapBank::OpenFail || control == CtiCCCapBank::OpenPending )
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                   currentCapBank, TRUE);

                        currentCapBank->setControlStatusQuality(CC_Normal);
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                        currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            char tempchar[80] = "";
                            char tempchar1[80] = "";
                            string additional("Sub: ");
                            additional += currentSubstationBus->getPaoName();
                            if (_LOG_MAPID_INFO)
                            {
                                additional += " MapID: ";
                                additional += currentSubstationBus->getMapLocationId();
                                additional += " (";
                                additional += currentSubstationBus->getPaoDescription();
                                additional += ")";
                            }
                            additional += string("  Feeder: ");
                            additional += currentFeeder->getPaoName();
                            if (_LOG_MAPID_INFO)
                            {
                                additional += " MapID: ";
                                additional += currentFeeder->getMapLocationId();
                                additional += " (";
                                additional += currentFeeder->getPaoDescription();
                                additional += ")";
                            }
                            string text = "Manual ";
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                                text += "Open Sent, Sub VarLoad = ";
                            else
                                text += "Close Sent, Sub VarLoad = ";
                            _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                            text += tempchar;
                            text += string(" Feeder VarLoad = ");
                            _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                            text += tempchar1;
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            DOUBLE kvarBefore = 0;
                            DOUBLE kvarAfter = 0;
                            DOUBLE kvarChange = 0;

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                            if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                                ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                            {
                                kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                            }
                            else
                            {
                                kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                            }

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(CtiTime());

                            currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                            currentCapBank->setAfterVarsString(" --- ");
                            currentCapBank->setPercentChangeString(" --- ");

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                            string stateInfo = currentCapBank->getControlStatusQualityString();
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));

                            if( controlID > 0 )
                            {
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending)
                                {
                                    CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control open");
                                    reqMsg->setSOE(2);
                                    pilMessages.push_back(reqMsg);
                                }
                                else //CLOSE
                                {
                                    CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control close");
                                    reqMsg->setSOE(2);
                                    pilMessages.push_back(reqMsg);
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                          << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                        }

                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));

                        }
                    }
                    else
                    {
                        if (currentSubstationBus->getVerificationFlag())
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                         <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                        }
                    }
                }
            }
            currentSubstationBus->verifyControlledStatusFlags();
        }
    }
}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Enable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPaoId(), 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(currentArea, false);
    }
    else
    {
        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            //CHECK ALL SUBSTATIONS ASSIGNED TO SPECIAL AREA FIRST,
            //To make sure they are not already on a conflicting different enabled Special Area
            BOOL refusalFlag = FALSE;
            string refusalText = "Special Area is not enabled!";
            PaoIdList::iterator subIter = currentSpArea->getSubstationIds()->begin();
            while (subIter != currentSpArea->getSubstationIds()->end())
            {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(*subIter);
                subIter++;
                if (currentSubstation != NULL)
                {
                    if (currentSubstation->getSaEnabledFlag())
                    {
                        refusalFlag = TRUE;
                        CtiCCSpecial* enabledSpArea = store->findSpecialAreaByPAObjectID(currentSubstation->getSaEnabledId());
                        if (enabledSpArea != NULL)
                        {
                            refusalText +=  "  Special Area: " + enabledSpArea->getPaoName() + " with Sub: " + currentSubstation->getPaoName() + " is already ENABLED";
                        }
                    }
                }
            }

            if (refusalFlag)
            {
                CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::RESP_UNSOLICITED, CtiCCServerResponse::RESULT_COMMAND_REFUSED, refusalText);
                msg->setUser(_command->getUser());
                CtiCCExecutorFactory::createExecutor(msg)->execute();
                if (currentSpArea->getDisabledStatePointId() != 0);
                {
                    //enable area refused.  disable flag = true.  This will sync the disable point.
                     CtiCapController::getInstance()->getDispatchConnection()->WriteConnQue(
                         new CtiPointDataMsg( currentSpArea->getDisabledStatePointId(), currentSpArea->getDisableFlag() ) ); // NormalQuality, StatusPointType
                }

            }
            else
            {

                string text1 = string("Manual Enable Special Area");
                string additional1 = string("Special Area: ");
                additional1 += currentSpArea->getPaoName();

                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                store->UpdatePaoDisableFlagInDB(currentSpArea, false);

                PaoIdList::iterator subIter = currentSpArea->getSubstationIds()->begin();

                while (subIter != currentSpArea->getSubstationIds()->end())
                {
                    CtiCCSubstationPtr currentSubstation = NULL;
                    currentSubstation = store->findSubstationByPAObjectID(*subIter);
                    subIter++;
                    if (currentSubstation != NULL)
                    {
                        if (!currentSubstation->getSaEnabledFlag())
                        {
                            currentSubstation->setSaEnabledFlag(TRUE);
                            currentSubstation->setSaEnabledId(areaId);
                        }
                    }
                }
            }
        }
    }
    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }
    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }
    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
}

/*---------------------------------------------------------------------------
    DisableArea
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec& capMessages = multiCapMsg->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Disable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL,0, currentArea->getPaoId(), 0,  0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(currentArea, true);
        currentArea->checkForAndStopVerificationOnChildSubBuses(capMessages);
    }
    else
    {
        CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());

        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            string text1 = string("Manual Disable Special Area");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPaoName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

            store->UpdatePaoDisableFlagInDB(currentSpArea, true);

            PaoIdList::iterator subIter = currentSpArea->getSubstationIds()->begin();

            while (subIter != currentSpArea->getSubstationIds()->end())
            {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(*subIter);
                subIter++;
                if (currentSubstation != NULL)
                {
                    if (currentSubstation->getSaEnabledFlag() &&
                        currentSubstation->getSaEnabledId() == areaId)
                    {
                        currentSubstation->setSaEnabledFlag(FALSE);
                    }
                }
            }
        }
    }

    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }
    CtiMultiMsg_vec& temp = multiCapMsg->getData( );
    for(int i=0;i<temp.size( );i++)
    {
        CtiCCExecutorFactory::createExecutor((CtiMessage*)capMessages[i])->execute();
    }
    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
}


void CtiCCCommandExecutor::AutoEnableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.

    LONG cmdId = _itemId;

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(cmdId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(cmdId);
    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(cmdId);
    CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(cmdId);
    if (currentArea != NULL || currentSpArea != NULL)
    {
        AutoEnableOvUvByArea();
        return;
    }
    if (currentStation != NULL)
    {
        AutoControlOvUvBySubstation(FALSE);
        return;
    }
    if (currentSubBus != NULL)
    {
        AutoControlOvUvBySubBus(FALSE);
        return;
    }
    return;
}

void CtiCCCommandExecutor::AutoDisableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.

    LONG cmdId = _itemId;

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(cmdId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(cmdId);
    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(cmdId);
    CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(cmdId);
    if (currentArea != NULL || currentSpArea != NULL)
    {
        AutoDisableOvUvByArea();
        return;
    }
    if (currentStation != NULL)
    {
        AutoControlOvUvBySubstation(TRUE);
        return;
    }
    if (currentSubBus != NULL)
    {
        AutoControlOvUvBySubBus(TRUE);
        return;
    }
    return;
}



/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::AutoEnableOvUvByArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);

    if (currentArea != NULL)
    {
        isValidIdFlag = TRUE;
        isAreaFlag = TRUE;

        string text1 = string("Auto Enable OvUv By Area Control Point");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPaoId(), 0, 0, 0, capControlEnableOvUv, 0, 1, text1, _command->getUser()));

    }
    else if (currentSpArea != NULL)
    {
        isValidIdFlag = TRUE;
        isAreaFlag = FALSE;

        string text1 = string("Auto Enable OvUv By Special Area Control Point");
        string additional1 = string("Special Area: ");
        additional1 += currentSpArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0,0, 0, 0, capControlEnableOvUv, 0, 1, text1, _command->getUser()));


    }
    else
        return;

    if (isValidIdFlag)
    {

        CtiCCSubstationPtr currentStation = NULL;
        CtiCCSubstationBusPtr currentSubstationBus = NULL;
        CtiCCFeederPtr currentFeeder = NULL;

        PaoIdList::iterator subIter;
        PaoIdList* stationList = NULL;

        if(isAreaFlag)
        {
            currentArea->setOvUvDisabledFlag(FALSE);

            subIter = currentArea->getSubStationList()->begin();
            stationList = currentArea->getSubStationList();
        }
        else
        {
            currentSpArea->setOvUvDisabledFlag(FALSE);

            subIter = currentSpArea->getSubstationIds()->begin();
            stationList = currentSpArea->getSubstationIds();
        }

        if (stationList == NULL)
        {
            return;
        }

        while (subIter != stationList->end() )
        {
            currentStation = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (currentStation != NULL)
            {
                currentStation->setOvUvDisabledFlag(FALSE);

                CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
                for(LONG i=0;i<ccSubstationBuses.size();i++)
                {
                     currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                    if (currentSubstationBus->getParentId() == currentStation->getPaoId())
                    {
                        string text1 = string("SubBus: ");
                        text1 += currentSubstationBus->getPaoName();
                        text1 += string(" Auto Enable OvUv");

                        currentSubstationBus->setOvUvDisabledFlag(FALSE);
                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                        if (isAreaFlag)
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPaoId(),  currentStation->getPaoId(),  currentSubstationBus->getPaoId(), 0, capControlEnableOvUv, currentSubstationBus->getEventSequence(), 1, text1, _command->getUser()));
                        else
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0,  currentStation->getPaoId(), currentSubstationBus->getPaoId(), 0, capControlEnableOvUv, currentSubstationBus->getEventSequence(), 1, text1, _command->getUser()));

                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                            currentFeeder->setOvUvDisabledFlag(FALSE);

                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                            for(LONG k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                                //limit auto individual commands to one way devices
                                if (!currentCapBank->isControlDeviceTwoWay() && _LIMIT_ONE_WAY_COMMANDS)
                                    continue;

                                if ( currentCapBank->getReEnableOvUvFlag() )
                                {
                                    currentCapBank->setReEnableOvUvFlag(FALSE);
                                    ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_ENABLE_OVUV, currentCapBank->getPaoId());
                                    actionMsg->setUser(_command->getUser());
                                    actionMulti->insert(actionMsg);
                                }
                            }

                        }
                        modifiedSubsList.push_back(currentSubstationBus);

                    }
                }
            }
        }

        if (actionMulti->getCount() > 0)
        {
           CtiCCExecutorFactory::createExecutor(actionMulti)->execute();
        }
        else
        {
            delete actionMulti;
        }

        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

        CtiCCExecutorFactory f;
        CtiCCExecutor *executor = NULL;
        if (isAreaFlag)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
        }
        else
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
        }
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ))->execute();
    }

}



/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::AutoDisableOvUvByArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    BOOL isValidIdFlag = FALSE; //is it a valid id passed in
    BOOL isAreaFlag = FALSE; // is it an area or special area.

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);

    if (currentArea != NULL)
    {
        isValidIdFlag = TRUE;
        isAreaFlag = TRUE;

        string text1 = string("Auto Disable OvUv By Area Control Point");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPaoId(), 0, 0, 0, capControlDisableOvUv, 0, 0, text1, _command->getUser()));

    }
    else if (currentSpArea != NULL)
    {
            isValidIdFlag = TRUE;
            isAreaFlag = FALSE;

            string text1 = string("Auto Disable OvUv By Special Area Control Point");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPaoName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlDisableOvUv, 0, 0, text1, _command->getUser()));

    }
    else
        return;

    if (isValidIdFlag)
    {
        CtiCCSubstationPtr currentStation = NULL;
        CtiCCSubstationBusPtr currentSubstationBus = NULL;
        CtiCCFeederPtr currentFeeder = NULL;


        PaoIdList::iterator subIter;
        PaoIdList* stationList = NULL;

        if(isAreaFlag)
        {
            currentArea->setOvUvDisabledFlag(TRUE);

            subIter = currentArea->getSubStationList()->begin();
            stationList = currentArea->getSubStationList();
        }
        else
        {
            currentSpArea->setOvUvDisabledFlag(TRUE);

            subIter = currentSpArea->getSubstationIds()->begin();
            stationList = currentSpArea->getSubstationIds();
        }

        if (stationList == NULL)
        {
            return;
        }

        while (subIter != stationList->end() )
        {
            currentStation = store->findSubstationByPAObjectID(*subIter);
            subIter++;

            if (currentStation != NULL)
            {
                currentStation->setOvUvDisabledFlag(TRUE);

                CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
                for(LONG i=0;i<ccSubstationBuses.size();i++)
                {
                    currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

                    if (currentSubstationBus->getParentId() == currentStation->getPaoId())
                    {
                        string text1 = string("SubBus: ");
                        text1 += currentSubstationBus->getPaoName();
                        text1 += string(" Auto Disable OvUv");

                        currentSubstationBus->setOvUvDisabledFlag(TRUE);
                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                        if (isAreaFlag)
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentArea->getPaoId(), currentStation->getPaoId(), currentSubstationBus->getPaoId(), 0, capControlDisableOvUv, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                        else
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, currentStation->getPaoId(), currentSubstationBus->getPaoId(), 0, capControlDisableOvUv, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));


                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                            currentFeeder->setOvUvDisabledFlag(TRUE);

                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                            for(LONG k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                                //limit auto individual commands to one way devices
                                if (!currentCapBank->isControlDeviceTwoWay() && _LIMIT_ONE_WAY_COMMANDS)
                                    continue;

                                if ( !currentCapBank->getOvUvDisabledFlag() )
                                {
                                    currentCapBank->setReEnableOvUvFlag(TRUE);
                                    ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_DISABLE_OVUV, currentCapBank->getPaoId());
                                    actionMsg->setUser(_command->getUser());
                                    actionMulti->insert(actionMsg);
                                }
                            }

                        }
                        modifiedSubsList.push_back(currentSubstationBus);

                    }
                }
            }
        }

        if (actionMulti->getCount() > 0)
        {
           CtiCCExecutorFactory::createExecutor(actionMulti)->execute();
        }
        else
        {
            delete actionMulti;
        }

        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        else
            delete eventMulti;
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        else
            delete multi;

        CtiCCExecutor *executor = NULL;
        if (isAreaFlag)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
        }
        else
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
        }
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ))->execute();
    }

}



/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::AutoControlOvUvBySubstation(BOOL disableFlag)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(subId);
    CtiCCSubstationBusPtr currentSubBus = NULL;

    if (currentStation != NULL)
    {

        string text1 = string("Auto ");
        if (disableFlag)
            text1 += "Disable OvUv By Substation Control Point";
        else
            text1 += "Enable OvUv By Substation Control Point";

        string additional1 = string("Substation: ");
        additional1 += currentStation->getPaoName();

        if (disableFlag)
        {
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPaoId(), 0, 0, capControlDisableOvUv, 0, 0, text1, _command->getUser()));
        }
        else
        {
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPaoId(), 0, 0, capControlEnableOvUv, 0, 1, text1, _command->getUser()));
        }


        PaoIdList::iterator busIter;
        if (disableFlag)
            currentStation->setOvUvDisabledFlag(TRUE);
        else
            currentStation->setOvUvDisabledFlag(FALSE);

        busIter = currentStation->getCCSubIds()->begin();

        while (busIter != currentStation->getCCSubIds()->end() )
        {
            currentSubBus = store->findSubBusByPAObjectID(*busIter);
            busIter++;

            if (currentSubBus != NULL)
            {
                string text1 = string("SubBus: ");
                text1 += currentSubBus->getPaoName();
                if (disableFlag)
                {
                    currentSubBus->setOvUvDisabledFlag(TRUE);
                    text1 += string(" Auto Disable OvUv");

                    currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPaoId(), currentSubBus->getPaoId(), 0, capControlDisableOvUv, currentSubBus->getEventSequence(), 0, text1, _command->getUser()));
                }
                else
                {
                    currentSubBus->setOvUvDisabledFlag(FALSE);
                    text1 += string(" Auto Enable OvUv");

                    currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPaoId(),  currentSubBus->getPaoId(), 0, capControlEnableOvUv, currentSubBus->getEventSequence(), 1, text1, _command->getUser()));

                }

                CtiFeeder_vec& ccFeeders = currentSubBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    if (disableFlag)
                        currentFeeder->setOvUvDisabledFlag(TRUE);
                    else
                        currentFeeder->setOvUvDisabledFlag(FALSE);


                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                        //limit auto individual commands to one way devices
                        if (!currentCapBank->isControlDeviceTwoWay() && _LIMIT_ONE_WAY_COMMANDS)
                            continue;

                        if (disableFlag)
                        {
                            if ( !currentCapBank->getOvUvDisabledFlag() )
                            {
                                currentCapBank->setReEnableOvUvFlag(TRUE);
                                ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_DISABLE_OVUV, currentCapBank->getPaoId());
                                actionMsg->setUser(_command->getUser());
                                actionMulti->insert(actionMsg);
                            }

                        }
                        else
                        {
                            if ( currentCapBank->getReEnableOvUvFlag() )
                            {
                                currentCapBank->setReEnableOvUvFlag(FALSE);
                                ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_ENABLE_OVUV, currentCapBank->getPaoId());
                                actionMsg->setUser(_command->getUser());
                                actionMulti->insert(actionMsg);
                            }

                        }
                    }

                }
                modifiedSubsList.push_back(currentSubBus);

            }
        }

        if (actionMulti->getCount() > 0)
        {
           CtiCCExecutorFactory::createExecutor(actionMulti)->execute();
        }
        else
        {
            delete actionMulti;
        }

        if (eventMulti->getCount() > 0)
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
        {
            delete eventMulti;
        }

        if (multi->getCount() > 0)
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        }
        else
        {
            delete multi;
        }

        CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ))->execute();
    }

}

/*---------------------------------------------------------------------------
    AutoEnableOvUv
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::AutoControlOvUvBySubBus(BOOL disableFlag)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());

    CtiCCSubstationPtr currentStation = NULL;
    CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(subId);

    if (currentSubBus != NULL)
    {
        currentStation = store->findSubstationByPAObjectID(currentSubBus->getParentId());
        if (currentStation != NULL)
        {
            string text1 = string("Auto ");
            if (disableFlag)
            {
                text1 += "Disable OvUv By SubBus Control Point";
                currentSubBus->setOvUvDisabledFlag(TRUE);

                currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPaoId(), currentSubBus->getPaoId(), 0, capControlDisableOvUv, currentSubBus->getEventSequence(), 0, text1, _command->getUser()));
            }
            else
            {
                text1 += "Enable OvUv By SubBus Control Point";
                currentSubBus->setOvUvDisabledFlag(FALSE);

                currentSubBus->setEventSequence(currentSubBus->getEventSequence() +1);
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, currentStation->getParentId(), currentStation->getPaoId(), currentSubBus->getPaoId(), 0, capControlEnableOvUv, currentSubBus->getEventSequence(), 1, text1, _command->getUser()));

            }
            string additional1 = string("SubBus: ");
            additional1 += currentSubBus->getPaoName();

            CtiFeeder_vec& ccFeeders = currentSubBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                if (disableFlag)
                    currentFeeder->setOvUvDisabledFlag(TRUE);
                else
                    currentFeeder->setOvUvDisabledFlag(FALSE);


                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                    //limit auto individual commands to one way devices
                    if (!currentCapBank->isControlDeviceTwoWay() && _LIMIT_ONE_WAY_COMMANDS)
                        continue;

                    if (disableFlag)
                    {
                        if ( !currentCapBank->getOvUvDisabledFlag() )
                        {
                            currentCapBank->setReEnableOvUvFlag(TRUE);
                            ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_DISABLE_OVUV, currentCapBank->getPaoId());
                            actionMsg->setUser(_command->getUser());
                            actionMulti->insert(actionMsg);
                        }

                    }
                    else
                    {
                        if ( currentCapBank->getReEnableOvUvFlag() )
                        {
                            currentCapBank->setReEnableOvUvFlag(FALSE);
                            ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_ENABLE_OVUV, currentCapBank->getPaoId());
                            actionMsg->setUser(_command->getUser());
                            actionMulti->insert(actionMsg);
                        }

                    }
                }

            }
            modifiedSubsList.push_back(currentSubBus);




            if (actionMulti->getCount() > 0)
            {
               CtiCCExecutorFactory::createExecutor(actionMulti)->execute();
            }
            else
            {
                delete actionMulti;
            }

            if (eventMulti->getCount() > 0)
            {
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
            }
            else
            {
                delete eventMulti;
            }

            if (multi->getCount() > 0)
            {
                CtiCapController::getInstance()->sendMessageToDispatch(multi);
            }
            else
            {
                delete multi;
            }

            CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ))->execute();
        }
    }

}

/*---------------------------------------------------------------------------
    EnableSystem
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableSystem()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    string text1 = string("Manual Enable System");
    string additional1 = string("CapControl SYSTEM ENABLE ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    for(LONG i=0;i<ccAreas.size();i++)
    {
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if (currentArea != NULL)
        {
            if (currentArea->getReEnableAreaFlag())
            {
                store->UpdatePaoDisableFlagInDB(currentArea, false);
            }
        }
    }

    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }
    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }


    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();

    SystemStatus* actionMsg = new SystemStatus(true);
    actionMsg->setUser(_command->getUser());

    CtiCCExecutorFactory::createExecutor(actionMsg)->execute();
}

/*---------------------------------------------------------------------------
    DisableSystem
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableSystem()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    string text1 = string("Manual Disable System");
    string additional1 = string("CapControl SYSTEM DISABLE ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    for(LONG i=0;i<ccAreas.size();i++)
    {
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if (currentArea != NULL)
        {
            if (!currentArea->getDisableFlag())
            {
                currentArea->setReEnableAreaFlag(TRUE);
                store->UpdatePaoDisableFlagInDB(currentArea, true);
            }
        }
    }

    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }
    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }


    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();

    SystemStatus* actionMsg = new SystemStatus(false);
    actionMsg->setUser(_command->getUser());

    CtiCCExecutorFactory::createExecutor(actionMsg)->execute();
}



/*---------------------------------------------------------------------------
    Scan2WayDevice
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::Flip7010Device()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _itemId;
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstationBus_vec updatedSubs;

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getPaoId() )
                {
                    found = TRUE;
                    if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 7010"))
                    {

                        updatedSubs.push_back(currentSubstationBus);

                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());

                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::Open )
                            {
                                //currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, TRUE);

                            }
                            else
                            {
                                //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, TRUE);

                            }
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";
                                string additional("Sub: ");
                                additional += currentSubstationBus->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPaoDescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPaoDescription();
                                    additional += ")";
                                }
                                string text("Manual Flip Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( !savedBusRecentlyControlledFlag ||
                                    (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());

                                }
                                DOUBLE kvarBefore = 0;

                                if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                                    ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                }
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);

                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text,
                                                                        _command->getUser(), kvarBefore, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo));
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }


                            BOOL confirmImmediately = FALSE;

                            if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::TimeOfDayControlMethod) )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) )
                            {
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) ||
                                     ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                            {
                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }

                            if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                            {
                                currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                currentFeeder->setRecentlyControlledFlag(TRUE);
                            }
                            else if (confirmImmediately)
                            {
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, currentCapBank->getControlDeviceId());
                            }
                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform Flip on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }
                        }
                        break;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - CBC Not 7010 Device Type.  Flip command not supported " << endl;
                    }

                }
            }
            if( found )
            {
                break;
            }
        }
        if( found )
        {
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control flip");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs))->execute();
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    Scan2WayDevice
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::Scan2WayDevice(long bankId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG cbcID = 0;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = NULL;

    if (bankId > 0)
    {
        CtiCCCapBankPtr currentCapBank = store->findCapBankByPAObjectID(bankId);

        if (currentCapBank != NULL)
        {   long subBusId = store->findSubBusIDbyCapBankID(currentCapBank->getPaoId());
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

            cbcID = currentCapBank->getControlDeviceId();

            if (currentSubstationBus != NULL && currentFeeder != NULL)
            {
                if (cbcID > 0 &&
                    stringContainsIgnoreCase(currentCapBank->getControlDeviceType(), "CBC 702"))
                {

                    string text = string("Manual Scan 2-Way CBC attached to CapBank: ");
                    text += currentCapBank->getPaoName();
                    string additional = string("CBC: ");
                    CHAR devID[20];
                    additional += string (ltoa(currentCapBank->getControlDeviceId(),devID,10));

                    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getControlPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlManualCommand, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));

                    CtiRequestMsg* reqMsg = createPorterRequestMsg(cbcID,"scan integrity");
                    reqMsg->setSOE(5);
                    CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

                    if (eventMulti->getCount() > 0)
                    {
                        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
                    }
                    else
                    {
                        delete eventMulti;
                    }
                }
                else
                {
                    delete multi;
                    delete eventMulti;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - CapBank Not attached to Feeder/Sub.  Cannot Scan Control Device " << endl;
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ConfirmSubstationBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subId);
    if (currentSubstationBus != NULL)
    {

        if (!currentSubstationBus->getVerificationFlag() &&
            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
        {

            string text1 = string("Manual Confirm Sub");
            string additional1 = string("Sub: ");
            additional1 += currentSubstationBus->getPaoName();
            if (_LOG_MAPID_INFO)
            {
                additional1 += " MapID: ";
                additional1 += currentSubstationBus->getMapLocationId();
                additional1 += " (";
                additional1 += currentSubstationBus->getPaoDescription();
                additional1 += ")";
            }
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                if (!currentFeeder->getDisableFlag())
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                        if (!currentCapBank->getDisableFlag() && currentCapBank->getControlDeviceId() > 0 &&
                            currentCapBank->getStatusPointId() > 0)
                        {

                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());


                            currentSubstationBus->setBusUpdatedFlag(TRUE);


                            char tempchar[80] = "";
                            char tempchar1[80] = "";
                            string additional = string("Sub: ");
                            additional += currentSubstationBus->getPaoName();
                            if (_LOG_MAPID_INFO)
                            {
                                additional += " MapID: ";
                                additional += currentSubstationBus->getMapLocationId();
                                additional += " (";
                                additional += currentSubstationBus->getPaoDescription();
                                additional += ")";
                            }
                            additional += string("  Feeder: ");
                            additional += currentFeeder->getPaoName();
                            if (_LOG_MAPID_INFO)
                            {
                                additional += " MapID: ";
                                additional += currentFeeder->getMapLocationId();
                                additional += " (";
                                additional += currentFeeder->getPaoDescription();
                                additional += ")";
                            }

                            string text = string("Manual Confirm ");
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail )
                            {
                                text += "Open Sent, Sub VarLoad = ";
                                //currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, TRUE);


                            }
                            else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                            {
                                text += "Close Sent, Sub VarLoad = ";
                                //currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, TRUE);
                            }
                            currentCapBank->setControlStatusQuality(CC_Normal);

                            _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                            text += tempchar;
                            text += string(" Feeder VarLoad = ");
                            _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                            text += tempchar1;
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(CtiTime());

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                            currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            ccEvents.push_back(eventMsg);

                            currentCapBank->setBeforeVarsString(" Confirm ");
                            currentCapBank->setAfterVarsString(" --- ");
                            currentCapBank->setPercentChangeString(" --- ");

                            if( controlID > 0 )
                            {
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending)
                                {
                                    CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control open");
                                    reqMsg->setSOE(2);
                                    pilMessages.push_back(reqMsg);
                                }
                                else //CLOSE
                                {
                                    CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control close");
                                    reqMsg->setSOE(2);
                                    pilMessages.push_back(reqMsg);
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }

                            doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);


                        }

                    }
                }
            }
            currentSubstationBus->verifyControlledStatusFlags();
        }
    }

    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;

}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ConfirmFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG fdrId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(fdrId);
    if (currentFeeder == NULL)
    {
        return;
    }
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

    if (currentSubstationBus == NULL)
    {
        return;
    }
    if ((currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None ||
         currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None) && !currentFeeder->getDisableFlag())
    {
        string text1 = string("Manual Confirm Feeder");
        string additional1 = string("Feeder: ");
        additional1 += currentFeeder->getPaoName();
        if (_LOG_MAPID_INFO)
        {
            additional1 += " MapID: ";
            additional1 += currentFeeder->getMapLocationId();
            additional1 += " (";
            additional1 += currentFeeder->getPaoDescription();
            additional1 += ")";
        }
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
        LONG stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
        ccEvents.push_back( new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));



        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(LONG k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

            if (!currentCapBank->getDisableFlag() && currentCapBank->getControlDeviceId() > 0 &&
                currentCapBank->getStatusPointId() > 0)
            {

                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                currentFeeder->setRecentlyControlledFlag(FALSE);
                controlID = currentCapBank->getControlDeviceId();

                if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                    currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                currentSubstationBus->setLastOperationTime(CtiTime());
                currentFeeder->setLastOperationTime(CtiTime());


                currentSubstationBus->setBusUpdatedFlag(TRUE);


                char tempchar[80] = "";
                char tempchar1[80] = "";
                string additional = string("Sub: ");
                additional += currentSubstationBus->getPaoName();
                if (_LOG_MAPID_INFO)
                {
                    additional += " MapID: ";
                    additional += currentSubstationBus->getMapLocationId();
                    additional += " (";
                    additional += currentSubstationBus->getPaoDescription();
                    additional += ")";
                }
                additional += string("  Feeder: ");
                additional += currentFeeder->getPaoName();
                if (_LOG_MAPID_INFO)
                {
                    additional += " MapID: ";
                    additional += currentFeeder->getMapLocationId();
                    additional += " (";
                    additional += currentFeeder->getPaoDescription();
                    additional += ")";
                }

                string text = string("Manual Confirm ");

                if (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail )
                {
                    text += "Open Sent, Sub VarLoad = ";
                    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                           currentCapBank, TRUE);


                }
                else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                {
                    text += "Close Sent, Sub VarLoad = ";
                    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                           currentCapBank, TRUE);
                }
                currentCapBank->setControlStatusQuality(CC_Normal);

                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                text += tempchar;
                text += string(" Feeder VarLoad = ");
                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                text += tempchar1;
                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                currentCapBank->setLastStatusChangeTime(CtiTime());

                INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                eventMsg->setActionId(currentCapBank->getActionId());
                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                ccEvents.push_back(eventMsg);

                currentCapBank->setBeforeVarsString(" Confirm ");
                currentCapBank->setAfterVarsString(" --- ");
                currentCapBank->setPercentChangeString(" --- ");

                if( controlID > 0 )
                {
                    if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending)
                    {
                        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control open");
                        reqMsg->setSOE(2);
                        pilMessages.push_back(reqMsg);
                    }
                    else //CLOSE
                    {
                        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control close");
                        reqMsg->setSOE(2);
                        pilMessages.push_back(reqMsg);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
                }

                doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);


            }

        }
        currentSubstationBus->verifyControlledStatusFlags();
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() <<  " - Manual Confirm Feeder: "<<currentFeeder->getPaoName() <<" inhibited due to: ";
        if (currentFeeder->getDisableFlag())
        {
            dout << "DisableFlag."<< endl;
        }
        else
        {
            dout << "No Control Method."<< endl;
        }
    }


    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    else
        delete eventMulti;

}

void CtiCCCommandExecutor::ConfirmArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* confirmMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);

    if (currentArea != NULL)
    {
        string text1 = string("Manual Confirm Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL,0, currentArea->getPaoId(), 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));


        Cti::CapControl::PaoIdList::const_iterator iter = currentArea->getSubStationList()->begin();
        while (iter != currentArea->getSubStationList()->end())
        {
            LONG stationId = *iter;
            CtiCCSubstation *station = store->findSubstationByPAObjectID(stationId);
            if (station != NULL)
            {
                Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
                while (iterBus  != station->getCCSubIds()->end())
                {
                    LONG busId = *iterBus;
                    CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                    if (currentSubstationBus != NULL)
                    {
                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            if (!currentSubstationBus->getDisableFlag() && !station->getDisableFlag())
                            {
                                ItemCommand* actionMsg = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, currentSubstationBus->getPaoId());
                                actionMsg->setUser(_command->getUser());
                                confirmMulti->insert(actionMsg);
                            }
                        }
                    }
                    iterBus++;
                }
            }
            iter++;
        }
        if (confirmMulti->getCount() > 0)
        {
            CtiCCExecutorFactory::createExecutor(confirmMulti)->execute();
            CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
        }
        else
        {
            delete confirmMulti;
        }

        if (eventMulti->getCount() > 0)
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
        {
            delete eventMulti;
        }

        if (multi->getCount() > 0)
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        }
        else
        {
            delete multi;
        }

    }
    else
    {
        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            //Make sure this special area is enabled, or kick back an error to the clients.
            if(currentSpArea->getDisableFlag())
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Special Area is not enabled, commands refused." << endl;
                }

                CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::RESP_UNSOLICITED, CtiCCServerResponse::RESULT_COMMAND_REFUSED, "Special Area is not enabled.");
                msg->setUser(_command->getUser());

                CtiCCExecutorFactory::createExecutor(msg)->execute();
            }
            else
            {

                string text1 = string("Manual Confirm Special Area");
                string additional1 = string("Special Area: ");
                additional1 += currentSpArea->getPaoName();

                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                Cti::CapControl::PaoIdList::const_iterator iter = currentSpArea->getSubstationIds()->begin();
                while (iter != currentSpArea->getSubstationIds()->end())
                {
                    LONG stationId = *iter;
                    CtiCCSubstation *station = store->findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {
                        Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
                        while (iterBus  != station->getCCSubIds()->end())
                        {
                            LONG busId = *iterBus;
                            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                            if (currentSubstationBus != NULL)
                            {
                                if (!currentSubstationBus->getVerificationFlag() &&
                                    currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                                {
                                    if (!currentSubstationBus->getDisableFlag() && !station->getDisableFlag())
                                    {
                                        ItemCommand* actionMsg = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, currentSubstationBus->getPaoId());
                                        actionMsg->setUser(_command->getUser());
                                        confirmMulti->insert(actionMsg);
                                    }
                                }
                            }
                            iterBus++;
                        }
                    }
                    iter++;
                }

                if (confirmMulti->getCount() > 0)
                {
                    CtiCCExecutorFactory::createExecutor(confirmMulti)->execute();
                    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
                }

                if (eventMulti->getCount() > 0)
                {
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
                }
                else
                {
                    delete eventMulti;
                }

                if (multi->getCount() > 0)
                {
                    CtiCapController::getInstance()->sendMessageToDispatch(multi);
                }
                else
                {
                    delete multi;
                }
            }
        }
    }
}

void CtiCCCommandExecutor::ConfirmSubstation()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* confirmMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstation* station = store->findSubstationByPAObjectID(_itemId);
    if (station != NULL)
    {
        string text1 = string("Manual Confirm Substation");
        string additional1 = string("Substation: ");
        additional1 += station->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, station->getParentId(), station->getPaoId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        Cti::CapControl::PaoIdList::const_iterator iterBus = station->getCCSubIds()->begin();
        while (iterBus  != station->getCCSubIds()->end())
        {
            LONG busId = *iterBus;
            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL)
            {
                if (!currentSubstationBus->getVerificationFlag() &&
                    currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                {
                    if (!currentSubstationBus->getDisableFlag())
                    {
                        ItemCommand* actionMsg = new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, currentSubstationBus->getPaoId());
                        actionMsg->setUser(_command->getUser());
                        confirmMulti->insert(actionMsg);
                    }
                }
            }
            iterBus++;
        }

        if (confirmMulti->getCount() > 0)
        {
            CtiCCExecutorFactory::createExecutor(confirmMulti)->execute();
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds())))->execute();
        }

        if (eventMulti->getCount() > 0)
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
        {
            delete eventMulti;
        }

        if (multi->getCount() > 0)
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
        }
        else
        {
            delete multi;
        }
    }
}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ConfirmOpen()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _itemId;
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    LONG savedControlStatus = -10;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstationBus_vec updatedSubs;

    if (bankID != 0)
    {

        for(LONG i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankID == currentCapBank->getPaoId() )
                    {
                        found = TRUE;
                        controlID = currentCapBank->getControlDeviceId();

                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);


                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            savedControlStatus = currentCapBank->getControlStatus();
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());

                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";
                                string additional("Sub: ");
                                additional += currentSubstationBus->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPaoDescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPaoDescription();
                                    additional += ")";
                                }



                                string text("Manual Confirm Open Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( ( !savedBusRecentlyControlledFlag ||
                                      (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) ) &&
                                     savedControlStatus != CtiCCCapBank::Open )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());
                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                eventMsg->setActionId(currentCapBank->getActionId());
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                ccEvents.push_back(eventMsg);

                                currentCapBank->setBeforeVarsString(" Confirm ");
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }


                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {

                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }

                            BOOL confirmImmediately = FALSE;
                            if( savedControlStatus == CtiCCCapBank::Open )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::TimeOfDayControlMethod) )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) )
                            {
                                LONG sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    CtiCCCapBank_SVector& banks = currentFeeder->getCCCapBanks();

                                    for(LONG l=0;l<banks.size();l++)
                                    {
                                        CtiCCCapBank* bank = (CtiCCCapBank*)banks[l];
                                        if (bank->getStatusPointId() > 0)
                                        {
                                            if ((bank->getControlDeviceId() != controlID && (bank->getControlStatus() == CtiCCCapBank::OpenPending || bank->getControlStatus() == CtiCCCapBank::ClosePending ) ) ||
                                                 savedControlStatus == CtiCCCapBank::OpenPending )
                                            {
                                                confirmImmediately = TRUE;
                                                break;
                                            }

                                        }
                                        //confirmImmediately = TRUE;
                                    }
                                }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) ||
                                     ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                            {
                                LONG sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();

                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }

                            if( !confirmImmediately )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                currentFeeder->setRecentlyControlledFlag(TRUE);
                            }
                            else
                            {
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, controlID);
                            }
                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }
                        }
                        break;
                    }
                }
                if( found )
                {
                    break;
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Control Open received for BankID 0...Cannot control device."<<endl;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
        {
            delete eventMulti;
        }

        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs, CtiCCSubstationBusMsg::SubBusAdded))->execute();
        }
    }
    else
    {
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    ConfirmClose
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ConfirmClose()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _itemId;
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    LONG savedControlStatus = -10;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstationBus_vec updatedSubs;


    if (bankID != 0)
    {
        for(LONG i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankID == currentCapBank->getPaoId() )
                    {
                        found = TRUE;
                        controlID = currentCapBank->getControlDeviceId();

                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);


                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            savedControlStatus = currentCapBank->getControlStatus();
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, TRUE);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";
                                string additional("Sub: ");
                                additional += currentSubstationBus->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPaoDescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPaoName();
                                if (_LOG_MAPID_INFO)
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPaoDescription();
                                    additional += ")";
                                }
                                string text("Manual Confirm Close Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( ( !savedBusRecentlyControlledFlag ||
                                      (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) ) &&
                                    savedControlStatus != CtiCCCapBank::Close )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());
                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                eventMsg->setActionId(currentCapBank->getActionId());
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                ccEvents.push_back(eventMsg);

                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {

                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }

                            BOOL confirmImmediately = FALSE;
                            if( savedControlStatus == CtiCCCapBank::Close )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::TimeOfDayControlMethod) )
                            {
                                confirmImmediately = TRUE;
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) )
                            {
                                LONG sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    CtiCCCapBank_SVector& banks = currentFeeder->getCCCapBanks();

                                    for(LONG l=0;l<banks.size();l++)
                                    {
                                        CtiCCCapBank* bank = (CtiCCCapBank*)banks[l];
                                        if (bank->getStatusPointId() > 0)
                                        {
                                            if ((bank->getControlDeviceId() != controlID && (bank->getControlStatus() == CtiCCCapBank::OpenPending || bank->getControlStatus() == CtiCCCapBank::ClosePending ) ) ||
                                                 savedControlStatus == CtiCCCapBank::ClosePending )
                                            {
                                                confirmImmediately = TRUE;
                                                break;
                                            }

                                        }
                                        //confirmImmediately = TRUE;
                                    }                            }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) ||
                                     ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                            {
                                LONG sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::SubstationBusControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = TRUE;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                                }
                                /*if (currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::None)
                                {
                                    confirmImmediately = TRUE;
                                }*/
                            }

                            if( !confirmImmediately )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                currentFeeder->setRecentlyControlledFlag(TRUE);
                            }
                            else
                            {
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, controlID);
                            }
                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() << endl;
                            }
                        }
                        break;
                    }
                }
                if( found )
                {
                    break;
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Confirm Close received for BankID 0...Cannot control device."<<endl;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = createPorterRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
        {
            delete eventMulti;
        }

        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs, CtiCCSubstationBusMsg::SubBusAdded))->execute();
        }
    }
    else
    {
        delete multi;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    doConfirmImmediately
---------------------------------------------------------------------------*/

void CtiCCCommandExecutor::doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, long controllerId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[currentSubstationBus->getLastFeederControlledPosition()];

    if( currentFeeder->getPaoId() == currentSubstationBus->getLastFeederControlledPAOId() )
    {
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(LONG k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
            if (controllerId == currentCapBank->getControlDeviceId())
            {
                currentFeeder->setRecentlyControlledFlag(FALSE);
                currentFeeder->setRetryIndex(0);
                if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    currentCapBank->setControlStatus(CtiCCCapBank::Close);
                    currentCapBank->setControlStatusQuality(CC_Normal);
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);

                        DOUBLE kvarBefore = 0;
                        DOUBLE kvarAfter = 0;
                        DOUBLE kvarChange = 0;

                        if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                            ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                        {
                            kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                            kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }
                        else
                        {
                            kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }


                        INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                        string stateInfo = currentCapBank->getControlStatusQualityString();
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close",
                                                                _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));

                        currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                        currentCapBank->setAfterVarsString(" Forced Confirm ");
                        currentCapBank->setPercentChangeString(" --- ");


                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                      << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                    }
                }
                else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                {
                    currentCapBank->setControlStatus(CtiCCCapBank::Open);
                    currentCapBank->setControlStatusQuality(CC_Normal);
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);

                        DOUBLE kvarBefore = 0;
                        DOUBLE kvarAfter = 0;
                        DOUBLE kvarChange = 0;

                        if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                            ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                        {
                            kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                            kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }
                        else
                        {
                            kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }

                        INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                        string stateInfo = currentCapBank->getControlStatusQualityString();
                        LONG stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open",
                                                                 _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));

                        currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                        currentCapBank->setAfterVarsString(" Forced Confirm ");
                        currentCapBank->setPercentChangeString(" --- ");

                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                      << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                    }
                }
                currentCapBank->setRetryOpenFailedFlag(FALSE);
                currentCapBank->setRetryCloseFailedFlag(FALSE);

            }
        }
        currentSubstationBus->setRecentlyControlledFlag(FALSE);

        for(int j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(int k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                    currentFeeder->setRecentlyControlledFlag(TRUE);
                }
            }
        }

        if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod) )
        {
            for(LONG x=0;x<ccFeeders.size();x++)
            {
                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                {
                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                    break;
                }
            }
        }
    }
    else
    {
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( currentFeeder->getPaoId() == currentSubstationBus->getLastFeederControlledPAOId() )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if (controllerId == currentCapBank->getControlDeviceId())
                    {
                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                                    ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }

                                INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close", _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId));
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" Forced Confirm ");
                                currentCapBank->setPercentChangeString(" --- ");

                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                        {

                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::IndividualFeederControlMethod) ||
                                    ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(), ControlStrategy::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }

                                INT actionId = ((currentCapBank->getActionId() != -1) ? currentCapBank->getActionId() : CCEventActionIdGen(currentCapBank->getStatusPointId()) );
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                LONG stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open",
                                                                        _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" Forced Confirm ");
                                currentCapBank->setPercentChangeString(" --- ");

                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" << endl;
                            }
                        }
                        currentCapBank->setRetryOpenFailedFlag(FALSE);
                        currentCapBank->setRetryCloseFailedFlag(FALSE);
                    }
                }
                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                currentFeeder->setRecentlyControlledFlag(FALSE);

                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod) )
                {
                    for(LONG x=0;x<ccFeeders.size();x++)
                    {
                        if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
}

/*---------------------------------------------------------------------------
    SendAllSubstationBuses
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::SendAllData()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses(CtiTime().seconds())), CtiCCSubstationBusMsg::AllSubBusesSent))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(*store->getCCGeoAreas(CtiTime().seconds())))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCCapBankStatesMsg(*store->getCCCapBankStates(CtiTime().seconds())))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(*(store->getCCSubstations(CtiTime().seconds())), CtiCCSubstationsMsg::AllSubsSent))->execute();

    // Send all Voltage Regulators
    VoltageRegulatorMessage * message = store->getVoltageRegulatorManager()->getVoltageRegulatorMessage(true);
    if ( message != 0 )
    {
        CtiCCExecutorFactory::createExecutor(message)->execute();
    }
}

/*---------------------------------------------------------------------------
    ReturnCapToOriginalFeeder
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ReturnCapToOriginalFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG bankId = _itemId;
    BOOL found = FALSE;
    LONG tempFeederId = 0;
    LONG movedCapBankId = bankId;
    LONG originalFeederId = 0;
    float capSwitchingOrder = 0.0;
    float closeOrder = 0.0;
    float tripOrder = 0.0;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    CtiCCSubstationBus* currentSubstationBus = NULL;
    CtiCCFeeder* currentFeeder = NULL;

    CtiCCCapBank* currentCapBank = store->findCapBankByPAObjectID(movedCapBankId);
    if (currentCapBank != NULL)
    {
        currentFeeder = store->findFeederByPAObjectID(currentCapBank->getParentId());
        if (currentFeeder != NULL)
        {
            currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
            if (currentSubstationBus != NULL && !currentSubstationBus->getVerificationFlag())
            {
                tempFeederId = currentFeeder->getPaoId();
                movedCapBankId = bankId;
                originalFeederId = currentCapBank->getOriginalParent().getOriginalParentId();
                capSwitchingOrder = currentCapBank->getOriginalParent().getOriginalSwitchingOrder();
                closeOrder = currentCapBank->getOriginalParent().getOriginalCloseOrder();
                tripOrder = currentCapBank->getOriginalParent().getOriginalTripOrder();
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                 <<".  Cannot perform RETURN CAP TO ORIGINAL FEEDER on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << "."<<endl;
            }
        }

    }


    //moveCapBank(TRUE, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder);
    if( tempFeederId > 0 && movedCapBankId > 0 && originalFeederId > 0 )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Move Cap Bank to original feeder PAO Id: " << movedCapBankId << endl;
        }
        moveCapBank(1, tempFeederId, movedCapBankId, originalFeederId, capSwitchingOrder, closeOrder, tripOrder);
    }
    else
    {
        if( tempFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << tempFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( originalFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << originalFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( movedCapBankId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank not found PAO Id: " << movedCapBankId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    ReturnFeederToOriginalSubBus
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ReturnFeederToOriginalSubBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG fdrId = _itemId;
    BOOL found = FALSE;
    LONG tempSubBusId = 0;
    LONG movedFeederId = fdrId;
    LONG originalSubBusId = 0;
    float fdrSwitchingOrder = 0.0;

    CtiCCSubstationBus* currentSubstationBus = NULL;
    CtiCCFeeder* currentFeeder = NULL;

    currentFeeder = store->findFeederByPAObjectID(fdrId);
    if (currentFeeder != NULL)
    {
        currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
        if (currentSubstationBus == NULL)
            return;
        if (!currentSubstationBus->getVerificationFlag())
        {
            tempSubBusId = currentFeeder->getParentId();
            movedFeederId = fdrId;
            originalSubBusId = currentFeeder->getOriginalParent().getOriginalParentId();
            fdrSwitchingOrder = currentFeeder->getOriginalParent().getOriginalSwitchingOrder();
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                             <<".  Cannot perform RETURN FEEDER on Fdr: " << currentFeeder->getPaoName() << " PAOID: " << currentFeeder->getPaoId() << "."<<endl;
        }
    }

    if( tempSubBusId > 0 && movedFeederId > 0 && originalSubBusId > 0 )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Move Feeder to original SubBus PAO Id: " << originalSubBusId << endl;
        }
        moveFeeder(true, tempSubBusId, movedFeederId, originalSubBusId, fdrSwitchingOrder);
    }
    else
    {
        if( tempSubBusId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - SubBus not found PAO Id: " << tempSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( originalSubBusId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - SubBus not found PAO Id: " << originalSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( movedFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << movedFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    ResetDailyOperations
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ResetDailyOperations()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();

    CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    LONG paoId = _itemId;
    BOOL found = FALSE;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    CtiCCSubstation* currentStation = store->findSubstationByPAObjectID(paoId);
    if (currentStation != NULL)
    {
        Cti::CapControl::PaoIdList::const_iterator iterBus = currentStation->getCCSubIds()->begin();
        while (iterBus  != currentStation->getCCSubIds()->end())
        {
            LONG busId = *iterBus;
            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL)
            {
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        currentCapBank->setCurrentDailyOperations(0);
                        currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                    }
                    currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                    currentFeeder->setMaxDailyOpsHitFlag(FALSE);
                }
                currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                    ccEvents.push_back(new CtiCCEventLogMsg(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                else
                    ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                found = TRUE;
            }
            iterBus++;
        }
        string text1 = string("Reset Daily Operations");
        string additional1 = string("Substation: ");
        additional1 += currentStation->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));



    }
    else
    {

        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(paoId);
        if (currentSubstationBus != NULL)
        {
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    currentCapBank->setCurrentDailyOperations(0);
                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                }
                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
            }
            currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);

            INT seqId = CCEventSeqIdGen();
            currentSubstationBus->setEventSequence(seqId);
            LONG stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                ccEvents.push_back(new CtiCCEventLogMsg(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
            else
                ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));


            string text1 = string("Reset Daily Operations");
            string additional1 = string("Substation Bus: ");
            additional1 += currentSubstationBus->getPaoName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            found = TRUE;

        }
        else
        {
            CtiCCFeeder* currentFeeder =  store->findFeederByPAObjectID(paoId);

            if( currentFeeder != NULL )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    currentCapBank->setCurrentDailyOperations(0);
                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                }

                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);

                INT seqId = CCEventSeqIdGen();
                LONG stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                if (currentFeeder->getDailyOperationsAnalogPointId() > 0)
                    ccEvents.push_back(new CtiCCEventLogMsg(0,currentFeeder->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentFeeder->getParentId(), currentFeeder->getPaoId(), capControlSetOperationCount, seqId, currentFeeder->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                else
                    ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentFeeder->getParentId(), currentFeeder->getPaoId(), capControlSetOperationCount, seqId, currentFeeder->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                string text1 = string("Reset Daily Operations");
                string additional1 = string("Feeder: ");
                additional1 += currentFeeder->getPaoName();

                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));

                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
                currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

                if (currentSubstationBus != NULL)
                {
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                }
                found = TRUE;

            }
            else
            {
                CtiCCCapBank* currentCapBank = store->findCapBankByPAObjectID(paoId);
                if( currentCapBank != NULL )
                {
                    currentFeeder = store->findFeederByPAObjectID(currentCapBank->getParentId());
                    currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

                    if (currentSubstationBus != NULL && currentFeeder != NULL)
                    {
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                            INT seqId = CCEventSeqIdGen();
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }
                        else
                        {
                            currentCapBank->setTotalOperations(0);
                        }
                        currentCapBank->setCurrentDailyOperations(0);
                        currentCapBank->setMaxDailyOpsHitFlag(FALSE);

                        string text1 = string("Reset Daily Operations");
                        string additional1 = string("CapBank: ");
                        additional1 += currentCapBank->getPaoName();

                        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));


                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        found = TRUE;
                    }
                }
            }

        }
    }

    if( found )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Reset Daily Operations for PAO Id: " << paoId << endl;
        }
        if( pointChanges.size() > 0 )
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multiDispatchMsg);
        }
        else
            delete multiDispatchMsg;
        if (ccEvents.size() > 0)
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
        else
            delete eventMulti;
    }
    else
    {
        delete multiDispatchMsg;
        delete eventMulti;

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Cannot find PAO Id: " << paoId << " in ResetDailyOperations() in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ResetAllSystemOpCounts()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _itemId;
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    string text1 = string("Manual System Reset All Op Counts");
    string additional1 = string("CapControl SYSTEM RESET OP COUNTS ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
    CtiCCSubstation_vec& ccStations = *store->getCCSubstations(CtiTime().seconds());
    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    for (int i = 0; i <ccAreas.size(); i++ )
    {
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if ( currentArea != NULL && ! currentArea->getDisableFlag())
        {
            PaoIdList::iterator subIter = currentArea->getSubStationList()->begin();

            while (subIter != currentArea->getSubStationList()->end())
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(*subIter);
                subIter++;

                if (currentStation != NULL)
                {
                    Cti::CapControl::PaoIdList::const_iterator iterBus = currentStation->getCCSubIds()->begin();
                    while (iterBus  != currentStation->getCCSubIds()->end())
                    {
                        LONG busId = *iterBus;
                        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                        if (currentSubstationBus != NULL)
                        {
                            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                            for(LONG j=0;j<ccFeeders.size();j++)
                            {
                                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                                for(LONG k=0;k<ccCapBanks.size();k++)
                                {
                                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                                    currentCapBank->setCurrentDailyOperations(0);
                                    currentCapBank->setTotalOperationsAndSendMsg(0, pointChanges);
                                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                                }
                                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
                            }
                            currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                                ccEvents.push_back(new CtiCCEventLogMsg(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                            else
                                ccEvents.push_back(new CtiCCEventLogMsg(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                            currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            found = TRUE;
                        }
                        iterBus++;
                    }
                }
            }
        }
    }


    if (eventMulti->getCount() > 0)
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        delete eventMulti;
    }

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
    }
    else
    {
        delete multi;
    }


   CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
   CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
   CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(ccSubstationBuses,CtiCCSubstationBusMsg::AllSubBusesSent ))->execute();

}

/*===========================================================================
    CtiCCCapBankMoveExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCCapBankMoveExecutor::execute()
{
    INT permanentFlag = _capMoveMsg->getPermanentFlag();
    LONG oldFeederId = _capMoveMsg->getOldFeederId();
    LONG movedCapBankId = _capMoveMsg->getItemId();
    LONG newFeederId = _capMoveMsg->getNewFeederId();
    float capSwitchingOrder = _capMoveMsg->getCapSwitchingOrder();
    float capCloseOrder = _capMoveMsg->getCloseOrder();
    float capTripOrder = _capMoveMsg->getTripOrder();

    bool result = moveCapBank(permanentFlag, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder, capCloseOrder, capTripOrder);

    long messageId = _capMoveMsg->getMessageId();
    long commandResult = CtiCCServerResponse::RESULT_SUCCESS;
    string commandResultText("Temporary Bank Move was successful.");

    if (!result)
    {
        commandResult = CtiCCServerResponse::RESULT_COMMAND_REFUSED;
        commandResultText = string("Temporary Bank Move was refused.");
    }

    CtiCCServerResponse* msg = new CtiCCServerResponse(messageId, commandResult, commandResultText);
    msg->setUser(_capMoveMsg->getUser());
    CtiCCExecutorFactory::createExecutor(msg)->execute();

}

/*---------------------------------------------------------------------------
    moveCapBank
---------------------------------------------------------------------------*/
bool CtiCCExecutor::moveCapBank(int permanentFlag, long oldFeederId, long movedCapBankId, long newFeederId, float capSwitchingOrder, float closeOrder, float tripOrder)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCFeeder* oldFeederPtr = NULL;
    CtiCCFeeder* newFeederPtr = NULL;
    CtiCCCapBank* movedCapBankPtr = NULL;
    CtiCCSubstationBus* oldFeederParentSub = NULL;
    CtiCCSubstationBus* newFeederParentSub = NULL;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    BOOL found = FALSE;
    BOOL verificationFlag = FALSE;

    oldFeederPtr = store->findFeederByPAObjectID(oldFeederId);
    if (oldFeederPtr != NULL)
    {
       oldFeederParentSub = store->findSubBusByPAObjectID(oldFeederPtr->getParentId());
       if (oldFeederParentSub != NULL)
       {
           if (oldFeederParentSub->getVerificationFlag())
           {
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< oldFeederParentSub->getPaoName() <<" PAOID: "<< oldFeederParentSub->getPaoId()
                                <<".  Cannot perform MOVE CAPBANK on Feeder: "<<oldFeederPtr->getPaoName()<<" PAOID: "<<oldFeederPtr->getPaoId()<<"."<<endl;
               }
               verificationFlag = TRUE;
               found = TRUE;
           }
           else
           {
               oldFeederParentSub->setBusUpdatedFlag(TRUE);
           }
       }
    }
    else
    {
        //Error Case.
        if (!verificationFlag)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << oldFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        return false;
    }

    newFeederPtr = store->findFeederByPAObjectID(newFeederId);
    if (newFeederPtr != NULL)
    {
       newFeederParentSub = store->findSubBusByPAObjectID(newFeederPtr->getParentId());
       if (newFeederParentSub != NULL)
       {
           if (newFeederParentSub->getVerificationFlag())
           {
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< newFeederParentSub->getPaoName() <<" PAOID: "<< newFeederParentSub->getPaoId()
                                <<".  Cannot perform MOVE CAPBANK on Feeder: "<<newFeederPtr->getPaoName()<<" PAOID: "<<newFeederPtr->getPaoId()<<"."<<endl;
               }
               verificationFlag = TRUE;
               found = TRUE;
           }
           else
           {
               newFeederParentSub->setBusUpdatedFlag(TRUE);
           }
       }
    }
    else
    {
        //Error Case.
        if (!verificationFlag)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << newFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }

        return false;
    }

    movedCapBankPtr = store->findCapBankByPAObjectID(movedCapBankId);
    if (movedCapBankPtr!=NULL && !verificationFlag)
    {
        //Remove the bank from the old feeder
        {
            CtiCCCapBank_SVector& oldFeederCapBanks = oldFeederPtr->getCCCapBanks();

            CtiCCCapBank_SVector::iterator itr = oldFeederCapBanks.begin();
            while (itr != oldFeederCapBanks.end())
            {
                if (*itr == movedCapBankPtr) {
                    itr = oldFeederCapBanks.erase( itr );
                }else
                    ++itr;
            }

            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdFeederIdMap, movedCapBankId);
            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap, movedCapBankId);

            //If permanent, set to 0 since there is no need to preserve the original orders
            if (!permanentFlag)
            {
                movedCapBankPtr->getOriginalParent().setOriginalParentId(oldFeederPtr->getPaoId());
                movedCapBankPtr->getOriginalParent().setOriginalSwitchingOrder(movedCapBankPtr->getControlOrder());
                movedCapBankPtr->getOriginalParent().setOriginalCloseOrder(movedCapBankPtr->getCloseOrder());
                movedCapBankPtr->getOriginalParent().setOriginalTripOrder(movedCapBankPtr->getTripOrder());
            }
            else
            {
                movedCapBankPtr->getOriginalParent().setOriginalParentId(0);
                movedCapBankPtr->getOriginalParent().setOriginalSwitchingOrder(0.0);
                movedCapBankPtr->getOriginalParent().setOriginalCloseOrder(0.0);
                movedCapBankPtr->getOriginalParent().setOriginalTripOrder(0.0);

            }

            movedCapBankPtr->setParentId(newFeederId);
            movedCapBankPtr->setDirty(true);
        }

        //Add the bank to the new feeder.
        {
            CtiCCCapBank_SVector& newFeederCapBanks = newFeederPtr->getCCCapBanks();

            movedCapBankPtr->setControlOrder(capSwitchingOrder);
            movedCapBankPtr->setCloseOrder(closeOrder);
            movedCapBankPtr->setTripOrder(tripOrder);

            //If permanent, reorder the banks to get rid of any '.' values.
            if (permanentFlag)
            {
                for (CtiCCCapBank_SVector::iterator itr = newFeederCapBanks.begin(); itr != newFeederCapBanks.end(); itr++)
                {
                    if((*itr)->getPaoId() == movedCapBankPtr->getPaoId())
                    {
                        newFeederCapBanks.erase(itr);
                        break;
                    }
                }

                //reorder new feeder.
                newFeederPtr->orderBanksOnFeeder();
                //reorder old feeder
                oldFeederPtr->orderBanksOnFeeder();
            }

            newFeederCapBanks.push_back(movedCapBankPtr);

            store->insertItemsIntoMap(CtiCCSubstationBusStore::CapBankIdFeederIdMap, &movedCapBankId, &newFeederId);
            long subBusId = store->findSubBusIDbyFeederID(newFeederId);
            if (subBusId != NULL)
            {
                store->insertItemsIntoMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap, &movedCapBankId, &subBusId);
            }

        }

        store->UpdateFeederBankListInDB(oldFeederPtr);
        store->UpdateFeederBankListInDB(newFeederPtr);

        {
            string typeString = (permanentFlag?"Permanent":"Temporary");

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual "
                 << typeString
                 << " Cap Bank with PAO Id: "
                 << movedCapBankPtr->getPaoId() << ", name: "
                 << movedCapBankPtr->getPaoName()
                 << ", was moved from feeder PAO Id: "
                 << oldFeederPtr->getPaoId() << ", name: "
                 << oldFeederPtr->getPaoName() << ", to feeder PAO Id: "
                 << newFeederPtr->getPaoId() << ", name: "
                 << newFeederPtr->getPaoName() << ", with order: "
                 << movedCapBankPtr->getControlOrder() << endl;
        }

        CtiMultiMsg_vec modifiedSubsList;
        modifiedSubsList.clear();
        oldFeederParentSub = store->findSubBusByPAObjectID(oldFeederPtr->getParentId());
        newFeederParentSub = store->findSubBusByPAObjectID(newFeederPtr->getParentId());

        if (newFeederParentSub != NULL)
        {
            modifiedSubsList.push_back(newFeederParentSub);
        }

        if (oldFeederParentSub != NULL)
        {
            modifiedSubsList.push_back(oldFeederParentSub);
        }

        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ))->execute();

        return true;
    }
    else
    {
        if (!verificationFlag)
        {
            if( movedCapBankPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank not found PAO Id: " << movedCapBankId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
    }

    return false;
}





/*===========================================================================
    CtiCCCapBankMoveExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCFeederMoveExecutor::execute()
{
    bool permanentFlag = _fdrMoveMsg->getPermanentFlag();
    long oldSubBusId = _fdrMoveMsg->getOldParentId();
    long movedFeederId = _fdrMoveMsg->getObjectId();
    long newSubBusId = _fdrMoveMsg->getNewParentId();
    float fdrSwitchingOrder = _fdrMoveMsg->getSwitchingOrder();

    moveFeeder(permanentFlag, oldSubBusId, movedFeederId, newSubBusId, fdrSwitchingOrder);
}

/*---------------------------------------------------------------------------
    moveCapBank
---------------------------------------------------------------------------*/
void CtiCCExecutor::moveFeeder(bool permanentFlag, long oldSubBusId, long movedFeederId, long newSubBusId, float fdrSwitchingOrder)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCSubstationBus* oldSubBusPtr = NULL;
    CtiCCSubstationBus* newSubBusPtr = NULL;
    CtiCCFeeder* movedFeederPtr = NULL;


    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    BOOL found = FALSE;
    BOOL verificationFlag = FALSE;

    movedFeederPtr = store->findFeederByPAObjectID(movedFeederId);
    if (movedFeederPtr == NULL)
        return;

    oldSubBusPtr = store->findSubBusByPAObjectID(oldSubBusId);
    if (oldSubBusPtr != NULL)
    {
        if (oldSubBusPtr->getVerificationFlag())
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< oldSubBusPtr->getPaoName() <<" PAOID: "<< oldSubBusPtr->getPaoId()
                             <<".  Cannot perform MOVE FEEDER: "<<movedFeederPtr->getPaoName()<<" PAOID: "<<movedFeederId<<"."<<endl;
            }
            verificationFlag = TRUE;
            found = TRUE;
        }
        else
        {
            oldSubBusPtr->setBusUpdatedFlag(TRUE);
        }
    }
    newSubBusPtr = store->findSubBusByPAObjectID(newSubBusId);
    if (newSubBusPtr != NULL)
    {
        if (newSubBusPtr->getVerificationFlag())
         {
             {
                 CtiLockGuard<CtiLogger> logger_guard(dout);
                 dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< newSubBusPtr->getPaoName() <<" PAOID: "<< newSubBusPtr->getPaoId()
                              <<".  Cannot perform MOVE FEEDER: "<<movedFeederPtr->getPaoName()<<" PAOID: "<<movedFeederId<<"."<<endl;
             }
             verificationFlag = TRUE;
             found = TRUE;
         }
         else
         {
             newSubBusPtr->setBusUpdatedFlag(TRUE);
         }
    }

    if( oldSubBusPtr!=NULL && newSubBusPtr!=NULL && movedFeederPtr!=NULL && !verificationFlag)
    {

        CtiFeeder_vec& oldFeeders = oldSubBusPtr->getCCFeeders();

        CtiFeeder_vec::iterator itr = oldFeeders.begin();
        while( itr != oldFeeders.end() )
        {
            if (*itr == movedFeederPtr) {
                itr = oldFeeders.erase( itr );
            }else
                ++itr;
        }

        store->removeItemsFromMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, movedFeederId);
        for (int i = 0; i < movedFeederPtr->getCCCapBanks().size(); i++)
        {
            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap,  movedFeederPtr->getCCCapBanks()[i]->getPaoId());
        }



        if( !permanentFlag )
        {
            movedFeederPtr->getOriginalParent().setOriginalParentId(oldSubBusPtr->getPaoId());
            movedFeederPtr->getOriginalParent().setOriginalSwitchingOrder(movedFeederPtr->getDisplayOrder());
        }
        else
        {
            movedFeederPtr->getOriginalParent().setOriginalParentId(0);
            movedFeederPtr->getOriginalParent().setOriginalSwitchingOrder(0.0);

        }

        movedFeederPtr->setParentId(newSubBusId);
        movedFeederPtr->setDisplayOrder(fdrSwitchingOrder);



        CtiFeeder_vec& newFeeders = newSubBusPtr->getCCFeeders();
        int insertPoint = newFeeders.size();
        int j = insertPoint;

        while (j > 0)
        {
            if (fdrSwitchingOrder <= ((CtiCCFeeder*)newFeeders.at(j-1))->getDisplayOrder())
            {
                insertPoint =  j - 1;
            }

            j--;
        }
        CtiFeeder_vec& ccF = newSubBusPtr->getCCFeeders();
        ccF.insert( ccF.begin()+insertPoint, movedFeederPtr );
        store->insertItemsIntoMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, &movedFeederId, &newSubBusId);
        for (int i = 0; i < movedFeederPtr->getCCCapBanks().size(); i++)
        {
            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap,  movedFeederPtr->getCCCapBanks()[i]->getPaoId());
        }

        store->UpdateFeederSubAssignmentInDB(oldSubBusPtr);
        store->UpdateFeederSubAssignmentInDB(newSubBusPtr);

        {
            string typeString = (permanentFlag?"Permanent":"Temporary");

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual "
                 << typeString
                 << " Feeder with PAO Id: "
                 << movedFeederPtr->getPaoId() << ", name: "
                 << movedFeederPtr->getPaoName()
                 << ", was moved from SubBus PAO Id: "
                 << oldSubBusPtr->getPaoId() << ", name: "
                 << oldSubBusPtr->getPaoName() << ", to SubBus PAO Id: "
                 << newSubBusPtr->getPaoId() << ", name: "
                 << newSubBusPtr->getPaoName() << ", with order: "
                 << movedFeederPtr->getDisplayOrder() << endl;
        }

        CtiMultiMsg_vec modifiedSubsList;
        modifiedSubsList.clear();
        modifiedSubsList.push_back(newSubBusPtr);
        modifiedSubsList.push_back(oldSubBusPtr);

        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ))->execute();
    }
    else
    {
        if (!verificationFlag)
        {
            if( oldSubBusPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - SubBus not found PAO Id: " << oldSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            if( newSubBusPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - SubBus not found PAO Id: " << newSubBusId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            if( movedFeederPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Feeder not found PAO Id: " << movedFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
    }
}



/*===========================================================================
    CtiCCClientMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCClientMsgExecutor::execute()
{
    CtiCCClientListener::getInstance()->BroadcastMessage(_ccMsg);
}


/*===========================================================================
    CtiCCForwardMsgToDispatchExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCForwardMsgToDispatchExecutor::execute()
{
    CtiCapController::getInstance()->sendMessageToDispatch(_ctiMessage->replicateMessage());
}

/*===========================================================================
    CtiCCPointDataMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCPointDataMsgExecutor::execute()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    long pointID = _pointDataMsg->getId();
    double value = _pointDataMsg->getValue();
    unsigned tags = _pointDataMsg->getTags();
    CtiTime& timestamp = _pointDataMsg->getTime();


    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        char tempchar[80];
        string outString = "Manual point value received from client, ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Val:";
        int precision = 3;
        _snprintf(tempchar,80,"%.*f",precision,value);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;
        outString += " Timestamp: ";
        outString += CtiTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << outString.c_str() << endl;
    }

    BOOL found = FALSE;
    BOOL logToCCEvent = FALSE;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(int i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);

        if( !found )
        {
            if( pointID == currentSubstationBus->getCurrentVarLoadPointId() ||
                pointID == currentSubstationBus->getEstimatedVarLoadPointId() )
            {
                found = TRUE;
                break;
            }

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(int k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getStatusPointId() == pointID )
                    {
                        if( currentCapBank->getControlStatus() != (LONG)value )
                        {
                            if( currentSubstationBus->getRecentlyControlledFlag() &&
                                currentFeeder->getLastCapBankControlledDeviceId() == currentCapBank->getPaoId() )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                                currentFeeder->setRecentlyControlledFlag(FALSE);
                                if( ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod) )
                                {
                                    for(LONG x=0;x<ccFeeders.size();x++)
                                    {
                                        if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                        {
                                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending)
                            {
                                currentFeeder->removeMaxKvar(currentCapBank->getPaoId());
                                logToCCEvent = TRUE;
                            }

                            currentCapBank->setBeforeVarsString(" Manual ");
                            currentCapBank->setAfterVarsString(" Manual ");
                            currentCapBank->setPercentChangeString(" --- ");

                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentCapBank->setControlStatus((LONG)value);
                            currentCapBank->setControlRecentlySentFlag(FALSE);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentCapBank->setTagsControlStatus((LONG)tags);
                            currentCapBank->setLastStatusChangeTime(timestamp);
                            currentCapBank->setPorterRetFailFlag(FALSE);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));

                            string text = "";
                            if (logToCCEvent)
                            {
                                text = string("Var: Cancelled by Pending Override, ");
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));

                            }
                            else
                            {
                                text = "CapBank: ";
                                text += currentCapBank->getPaoName();
                                text += " Manual State Change to ";
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);

                            }
                            text += currentCapBank->getControlStatusText();

                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _pointDataMsg->getUser() );
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());

                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);

                        }
                        currentCapBank->setIgnoreFlag(FALSE);
                        found = TRUE;
                        break;
                    }
                    else if( currentCapBank->getOperationAnalogPointId() == pointID )
                    {
                        /* Commenting this out, to reset disabled bank op counts.  makes sense.*/
                        //if (!currentCapBank->getDisableFlag())
                        {
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentCapBank->setTotalOperations((LONG) value);
                            currentCapBank->setCurrentDailyOperations((LONG) value);
                            currentCapBank->setLastStatusChangeTime(timestamp);

                            if ( currentCapBank->getMaxDailyOps() > 0)
                            {
                                if (value  < currentCapBank->getMaxDailyOps())
                                {
                                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                                }
                            }
                            else
                                currentCapBank->setMaxDailyOpsHitFlag(FALSE);

                            char tempchar[80] = "";
                            string text = "CapBank: ";
                            text+= currentCapBank->getPaoName();
                            text += " Operation Count Change";
                            string additional = "Value = ";
                            _snprintf(tempchar,80,"%.*f",1,value);
                            additional += tempchar;
                            LONG stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), text, _pointDataMsg->getUser() ));
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),text,additional,CapControlLogType,SignalEvent,_pointDataMsg->getUser()));
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
        else if( found )
        {
            break;
        }
    }
    _pointDataMsg->setTags(tags | TAG_POINT_FORCE_UPDATE);

    CtiCapController::getInstance()->sendMessageToDispatch(_pointDataMsg->replicateMessage());
}


/*===========================================================================
    CtiCCMultiMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCMultiMsgExecutor::execute()
{
    CtiMultiMsg_vec& msgs = _multiMsg->getData();
    while (msgs.size() > 0)
    {
        CtiMessage* message = (CtiMessage*)(msgs.back());
        msgs.pop_back();

        if( message != NULL )
        {
            CtiCCExecutorFactory::createExecutor(message)->execute();
        }
    }
}


/*===========================================================================
    CtiCCShutdown
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute

    Executes a shutdown on the server
---------------------------------------------------------------------------*/
void CtiCCShutdownExecutor::execute()
{

    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down cap controller thread..." << endl;
        }

        CtiCapController::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap controller thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down client listener thread..." << endl;
        }

        CtiCCClientListener::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Client listener thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down substation bus store..." << endl;
        }

        CtiCCSubstationBusStore::deleteInstance();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Substation bus store shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCCommandExecutor::sendVoltageRegulatorCommands( const LONG command )
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard( store->getMux() );

    std::string commandName("Voltage Regulator ");

    try
    {
        VoltageRegulatorManager::SharedPtr  regulator
            = store->getVoltageRegulatorManager()->getVoltageRegulator( _itemId );

        switch ( command )
        {
            case CapControlCommand::VOLTAGE_REGULATOR_INTEGRITY_SCAN:
            {
                commandName += "Integrity Scan";
                regulator->executeIntegrityScan();
                CtiCapController::getInstance()->sendEventLogMessage(
                    new CtiCCEventLogMsg( "Sent Integrity Scan", regulator->getPaoId(), capControlIvvcScanOperation ) );
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE:
            {
                commandName += "Remote Control Enable";
                regulator->executeEnableRemoteControl();
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE:
            {
                commandName += "Remote Control Disable";
                regulator->executeDisableRemoteControl();
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE:
            {
                commandName += "Raise Tap Position";
                regulator->executeTapUpOperation();
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_TAP_POSITION_LOWER:
            {
                commandName += "Lower Tap Position";
                regulator->executeTapDownOperation();
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_KEEP_ALIVE_ENABLE:
            {
                commandName += "Enable Keep Alive";
                regulator->executeEnableKeepAlive();
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_KEEP_ALIVE_DISABLE:
            {
                commandName += "Disable Keep Alive";
                regulator->executeDisableKeepAlive();
                break;
            }
            default:
            {
                 CtiLockGuard<CtiLogger> logger_guard(dout);
                 dout << CtiTime() << " - Voltage Regulator Command not implemented: " << command << endl;
            }
            break;
        }
    }
    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - " << commandName << " command failed.\n"
             << CtiTime() << " - ** " << noRegulator.what() << std::endl;
    }
    catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - " << commandName << " command failed.\n"
             << CtiTime() << " - ** " << missingAttribute.what() << std::endl;
    }
}

void SystemStatusExecutor::execute()
{
    CtiCCClientListener::getInstance()->BroadcastMessage(_systemStatus);
}

void DeleteItemExecutor::execute()
{
    CtiCCClientListener::getInstance()->BroadcastMessage(_deleteItem);
}

