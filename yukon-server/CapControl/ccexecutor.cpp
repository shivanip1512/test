#include "precompiled.h"

#include "AttributeService.h"
#include "LitePoint.h"
#include "Attribute.h"
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
#include "database_util.h"
#include "database_connection.h"
#include "database_reader.h"
#include "IVVCState.h"
#include "IVVCStrategy.h"

using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::createPorterRequestMsg;
using Cti::CapControl::EventLogEntry;
using Cti::CapControl::EventLogEntries;
using Cti::CapControl::createBankOpenRequest;
using Cti::CapControl::createBankCloseRequest;
using Cti::CapControl::createBankFlipRequest;
using Cti::Messaging::CapControl::CapControlOperationMessage;
using Cti::CapControl::formatMapInfo;
using std::endl;
using std::string;

extern unsigned long _CC_DEBUG;
extern bool _IGNORE_NOT_NORMAL_FLAG;
extern unsigned long _SEND_TRIES;
extern bool _LIMIT_ONE_WAY_COMMANDS;

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
        sendVoltageRegulatorCommands( _command->getCommandId(), _command->getUser() );
        break;

    case CapControlCommand::CHANGE_OPERATIONALSTATE:
        CTILOG_ERROR(dout, "Command " << _command->getCommandId() << " not handled by this executor!");
        success = false;
        break;

    default:
        CTILOG_WARN(dout, "unknown command type, " << _command->getCommandId());
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationPtr station = store->findSubstationByPAObjectID(_itemId);
    if (station != NULL)
    {
        string text1 = string("Manual Enable Substation");
        string additional1 = string("Substation: ");
        additional1 += station->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0, 0, station->getPaoId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(station, false);
    }

    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
    }
    else
    {
        delete multi;
    }

    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(store->getCCSubstations()))->execute();
}

void CtiCCCommandExecutor::DisableSubstation()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;
    CtiMultiMsg_vec& capMessages = multiCapMsg->getData();

    CtiCCSubstationPtr station = store->findSubstationByPAObjectID(_itemId);
    if (station != NULL)
    {
        string text1 = string("Manual Disable Substation");
        string additional1 = string("Substation: ");
        additional1 += station->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0,0, station->getPaoId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(station, true);
        station->checkForAndStopVerificationOnChildSubBuses(capMessages);
    }

    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(store->getCCSubstations()))->execute();
}

/*---------------------------------------------------------------------------
    EnableSubstation
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableSubstationBus(long subBusId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subBusId);

    if (currentSubstationBus == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Unable to enable Substation Bus with ID: " << subBusId << ". It was not found.");
        }
        return;
    }

    currentSubstationBus->setReEnableBusFlag(false);
    currentSubstationBus->setBusUpdatedFlag(true);
    store->UpdatePaoDisableFlagInDB(currentSubstationBus, false);
    string text = string("Substation Bus Enabled");
    string additional = "Bus: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);

    if (!currentSubstationBus->getVerificationFlag())
    {
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
    }

    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
}

/*---------------------------------------------------------------------------
    DisableSubstation
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableSubstationBus(long subBusId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subBusId);

    if (currentSubstationBus == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Unable to disable Substation Bus with ID: " << subBusId << ". It was not found.");
        }
        return;
    }

    currentSubstationBus->setReEnableBusFlag(false);
    currentSubstationBus->setBusUpdatedFlag(true);
    store->UpdatePaoDisableFlagInDB(currentSubstationBus, true);
    string text = string("Substation Bus Disabled");
    string additional = "Bus: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);

    if (!currentSubstationBus->getVerificationFlag())
    {
        INT seqId = CCEventSeqIdGen();
        currentSubstationBus->setEventSequence(seqId);
    }

    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
}

/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long feederID = _itemId;
    bool found = false;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    for(long i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPaoId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentSubstationBus->setBusUpdatedFlag(true);
                    store->UpdatePaoDisableFlagInDB(currentFeeder, false);
                    string text("Feeder Enabled");
                    string additional = "Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);

                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    long stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                }
                else
                {
                    CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                      <<".  Cannot perform ENABLE on Feeder: " << currentFeeder->getPaoName() << " PAOID: " << currentFeeder->getPaoId() << ".");
                }
                found = true;
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long feederID = _itemId;
    bool found = false;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    for(long i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPaoId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentSubstationBus->setBusUpdatedFlag(true);
                    store->UpdatePaoDisableFlagInDB(currentFeeder, true);
                    string text("Feeder Disabled");
                    string additional = "Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);

                    long stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
                }
                else
                {
                    CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                      <<".  Cannot perform DISABLE on Feeder: " << currentFeeder->getPaoName() << " PAOID: " << currentFeeder->getPaoId() << ".");
                }
                found = true;
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long capBankID = _itemId;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    for(long i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(long k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPaoId() )
                {
                    if (!currentSubstationBus->getVerificationFlag())
                    {
                        currentSubstationBus->setBusUpdatedFlag(true);
                        store->UpdatePaoDisableFlagInDB(currentCapBank, false);
                        string text("Cap Bank Enabled");
                        string additional = "Cap Bank: " + currentCapBank->getPaoName() + formatMapInfo( currentCapBank );

                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            long stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                          << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);
                        }
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                         <<".  Cannot perform ENABLE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
                    }

                    return;
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    DisableCapBank
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long capBankID = _itemId;
    bool found = false;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    for(long i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(long k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPaoId() )
                {
                    if (!currentSubstationBus->getVerificationFlag())
                    {
                        currentSubstationBus->setBusUpdatedFlag(true);
                        store->UpdatePaoDisableFlagInDB(currentCapBank, true);
                        string text("Cap Bank Disabled");
                        string additional = "Cap Bank: " + currentCapBank->getPaoName() + formatMapInfo( currentCapBank );

                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);

                            long stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                          << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);
                        }
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                         <<".  Cannot perform DISABLE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");

                    }
                    return;
                }
            }
        }
    }
}

void CtiCCCommandExecutor::syncCbcAndCapBankStates(long bankId)
{

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    string commandName = " Sync CBC and CapBank States";

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    if ( !capBank->isControlDeviceTwoWay() )
    {
        CTILOG_WARN(dout, commandName << " command rejected, CBC must be two way. ");
        return;
    }
    if ( capBank->getDisableFlag() )
    {
        CTILOG_WARN(dout, commandName <<" command rejected, CapBank: "<< capBank->getPaoName()<<" is Disabled.");
        return;
    }
    if ( capBank->isPendingStatus() )
    {
        CTILOG_WARN(dout, commandName <<" command rejected, CapBank: "<< capBank->getPaoName()<<" is in a Pending State.");
        return;
    }

    int controlStatus = capBank->getTwoWayPoints().getPointValueByAttribute( Attribute::ControlPoint );

    if( controlStatus < 0 )
    {
        CTILOG_WARN(dout, commandName << " command rejected, CapBank: " << capBank->getPaoName()<<" received an invalid "
                    "control state value (" << controlStatus << "). Not adjusting the cap bank state.");
        return;
    }

    //Logging Action
    string text = string("Manual CBC/CapBank State Sync.  Adjusting CapBank State from: ");
    text += capBank->getControlStatusText() + " to ";
    text += ( controlStatus ? "Close." : "Open.");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    capBank->setControlStatus(controlStatus);

    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(capBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()), CALLSITE);
    CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, "Forced ccServer Update", TAG_POINT_FORCE_UPDATE), CALLSITE);

    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);
    CtiCapController::submitEventLogEntry(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capBankStateUpdate, seqId, capBank->getControlStatus(), text, _command->getUser()));

    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(subBus))->execute();
}

/*---------------------------------------------------------------------------
    Enable OV/UV
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::enableOvUv(long bankId,
                                      std::vector<CtiSignalMsg*>& signals,
                                      EventLogEntries &events,
                                      std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable OvUv";
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank OV/UV Enabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);
    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    capBank->setOvUvDisabledFlag(false);
    subBus->setBusUpdatedFlag(true);

    CtiRequestMsg* reqMsg = NULL;
    if (cbc702)
    {
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

        int offset = 1;
        unsigned char voltageValue = (unsigned char)points.getPointValueByAttribute( Attribute::VoltageControl );

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
}


/*---------------------------------------------------------------------------
    Disable OV/UV
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::disableOvUv(long bankId,
                                       std::vector<CtiSignalMsg*>& signals,
                                       EventLogEntries &events,
                                       std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable OvUv";
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank OV/UV Disabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command
    capBank->setOvUvDisabledFlag(true);
    subBus->setBusUpdatedFlag(true);

    CtiRequestMsg* reqMsg = NULL;
    if (cbc702)
    {
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

        int offset = 1;
        unsigned char voltageValue = (unsigned char)points.getPointValueByAttribute( Attribute::VoltageControl );

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
}

void CtiCCCommandExecutor::enableTempControl(long bankId,std::vector<CtiSignalMsg*>& signals, EventLogEntries &events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable Temp Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank Temp Control Enabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    CtiRequestMsg* reqMsg = NULL;

    if ( cbc702 )
    {
        //Compute new value.
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

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
        unsigned char seasonOneValue = (unsigned char)points.getPointValueByAttribute( Attribute::TimeTempControlSeasonOne );

        //Changing the MSB to 1
        seasonOneValue |= 0x80;

        //Send point update message with new value.
        string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
        reqMsg->setSOE(5);
        requests.push_back(reqMsg);

        int offsetTwo = 42;
        unsigned char seasonTwoValue = points.getPointValueByAttribute( Attribute::TimeTempControlSeasonTwo );

        //Changing the MSB to 1
        seasonTwoValue |= 0x80;

        string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig temp enable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);
}

void CtiCCCommandExecutor::disableTempControl(long bankId,std::vector<CtiSignalMsg*>& signals, EventLogEntries &events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable Temp Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank Temp Control Disabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    CtiRequestMsg* reqMsg = NULL;

    if ( cbc702 )
    {
        //Compute new value.
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

        int offsetOne = 26;
        unsigned char seasonOneValue = (unsigned char)points.getPointValueByAttribute( Attribute::TimeTempControlSeasonOne );

        //Zeroing the MSB
        seasonOneValue &= 0x7f;

        //Send point update message with new value.
        string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
        reqMsg->setSOE(5);
        requests.push_back(reqMsg);

        int offsetTwo = 42;
        unsigned char seasonTwoValue = points.getPointValueByAttribute( Attribute::TimeTempControlSeasonTwo );

        //Zeroing the MSB
        seasonTwoValue &= 0x7f;

        string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig temp disable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);
}

void CtiCCCommandExecutor::enableVarControl(long bankId,std::vector<CtiSignalMsg*>& signals, EventLogEntries &events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable Var Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank Var Control Enabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    CtiRequestMsg* reqMsg = NULL;

    if ( cbc702 )
    {
        //Compute new value.
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

        int offset = 68;
        unsigned char varValue = (unsigned char)points.getPointValueByAttribute( Attribute::VarControl );

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
        string commandString = "putvalue analog " + CtiNumStr(offset).toString() + " " + CtiNumStr((int)varValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandString);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig var enable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);
}

void CtiCCCommandExecutor::disableVarControl(long bankId,std::vector<CtiSignalMsg*>& signals, EventLogEntries &events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable Var Control";

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank Var Control Disabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    CtiRequestMsg* reqMsg = NULL;

    if ( cbc702 )
    {
        //Compute new value.
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

        int offset = 68;
        unsigned char varValue = (unsigned char)points.getPointValueByAttribute( Attribute::VarControl );

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
        string commandString = "putvalue analog " + CtiNumStr(offset).toString() + " " + CtiNumStr((int)varValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandString);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig var disable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);
}

void CtiCCCommandExecutor::enableTimeControl(long bankId,std::vector<CtiSignalMsg*>& signals, EventLogEntries &events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Enable Time Control";
    bool implemented = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank Time Control Enabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    CtiRequestMsg* reqMsg = NULL;

    if ( cbc702 )
    {
        //Compute new value.
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

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
        unsigned char seasonOneValue = (unsigned char)points.getPointValueByAttribute( Attribute::TimeTempControlSeasonOne );

        //Changing the LSB to 1
        seasonOneValue |= 0x01;

        //Send point update message with new value.
        string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
        reqMsg->setSOE(5);
        requests.push_back(reqMsg);

        int offsetTwo = 42;
        unsigned char seasonTwoValue = points.getPointValueByAttribute( Attribute::TimeTempControlSeasonTwo );

        //Changing the LSB to 1
        seasonTwoValue |= 0x01;

        string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig time enable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);
}

void CtiCCCommandExecutor::disableTimeControl(long bankId,std::vector<CtiSignalMsg*>& signals, EventLogEntries &events, std::vector<CtiRequestMsg*>& requests)
{
    string commandName = " Disable Time Control";
    bool implemented = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCCapBank* capBank = store->findCapBankByPAObjectID(bankId);
    if (capBank == NULL)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Bank not found with id: " << bankId);
        return;
    }

    long controllerId = capBank->getControlDeviceId();
    if (controllerId == 0)
    {
        CTILOG_WARN(dout, commandName << " command rejected, Controller id of 0.");
        return;
    }

    const bool cbc702 = stringContainsIgnoreCase( capBank->getControlDeviceType(), "CBC 702" );

    //Logging Action
    string text = string("Cap Bank Time Control Disabled");
    string additional = "Cap Bank: " + capBank->getPaoName() + formatMapInfo( capBank );

    signals.push_back(new CtiSignalMsg(capBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
    int subId = store->findSubBusIDbyCapBankID(bankId);
    int feederId = store->findFeederIDbyCapBankID(bankId);
    CtiCCSubstationBusPtr subBus = store->findSubBusByPAObjectID(subId);

    int seqId = CCEventSeqIdGen();
    subBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(subBus, spAreaId, areaId, stationId);

    events.push_back(EventLogEntry(0, capBank->getControlPointId(), spAreaId, areaId, stationId, subId, feederId, capControlEnableOvUv, seqId, 1, text, _command->getUser()));

    //Actual Command Work
    CtiRequestMsg* reqMsg = NULL;

    if ( cbc702 )
    {
        //Compute new value.
        CtiCCTwoWayPoints & points = capBank->getTwoWayPoints();

        int offsetOne = 26;
        unsigned char seasonOneValue = (unsigned char)points.getPointValueByAttribute( Attribute::TimeTempControlSeasonOne );

        //Zeroing the LSB
        seasonOneValue &= 0xfe;

        //Send point update message with new value.
        string commandStringOne = "putvalue analog " + CtiNumStr(offsetOne).toString() + " " + CtiNumStr((int)seasonOneValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringOne);
        reqMsg->setSOE(5);
        requests.push_back(reqMsg);

        int offsetTwo = 42;
        unsigned char seasonTwoValue = points.getPointValueByAttribute( Attribute::TimeTempControlSeasonTwo );

        //Zeroing the LSB
        seasonTwoValue &= 0xfe;

        string commandStringTwo = "putvalue analog " + CtiNumStr(offsetTwo).toString() + " " + CtiNumStr((int)seasonTwoValue).toString();
        reqMsg = createPorterRequestMsg(controllerId,commandStringTwo);
    }
    else
    {
        reqMsg = createPorterRequestMsg(controllerId,"putconfig time disable");
    }

    reqMsg->setSOE(5);
    requests.push_back(reqMsg);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long paoId = _itemId;
    long controlID = 0;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

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

    CtiCapController::submitEventLogEntries(ccEvents);
}

void CtiCCCommandExecutor::SendAllCapBankCommands()
{
    long paoId = _itemId;
    string actionText = "";

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();

    CtiCCSubstationBus_vec modifiedSubsList;
    modifiedSubsList.clear();

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();
    const auto& ccStations = store->getCCSubstations();

    //Find the object type
    Cti::CapControl::CapControlType type = store->determineTypeById(paoId);

    //Special Case for SpecialAreas. If it is disabled, no commands go out.
    if (type == Cti::CapControl::SpecialArea)
    {
        CtiCCSpecialPtr sArea = store->findSpecialAreaByPAObjectID(paoId);

        if (sArea->getDisableFlag())
        {
            CTILOG_INFO(dout, "Special Area is not enabled, will not set OVUV.");

            CtiCCServerResponse* msg = new CtiCCServerResponse(CtiCCServerResponse::RESP_UNSOLICITED,CtiCCServerResponse::RESULT_COMMAND_REFUSED, "Special Area is not enabled.");
            msg->setUser(_command->getUser());
            CtiCCExecutorFactory::createExecutor(msg)->execute();

            return;
        }
    }

    if (type == Cti::CapControl::Undefined)
    {
        CTILOG_ERROR( dout, "Trying to execute an unsupported PaoType with ID: " << paoId );
        return;
    }

    //Main work of this function, split the command to every bank attached to paoId
    std::vector<CtiSignalMsg*> signals;
    EventLogEntries events;
    std::vector<CtiRequestMsg*> requests;

    CapBankList banks = store->getCapBanksByPaoIdAndType(paoId,type);
    for ( CtiCCCapBankPtr bank : banks )
    {
        long bankId = bank->getPaoId();

        //Execute per capbank
        switch (_command->getCommandId())
        {
            case CapControlCommand::SEND_OPEN_CAPBANK:
            {
                actionText = string(" - Open");
                OpenCapBank(bankId, type != Cti::CapControl::CapBank);
                break;
            }
            case CapControlCommand::SEND_CLOSE_CAPBANK:
            {
                actionText = string(" - Close");
                CloseCapBank(bankId, type != Cti::CapControl::CapBank);
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
                CTILOG_WARN(dout, "Send All Command Type: " << _command->getCommandId() << " is not supported." );

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


    CtiCapController::submitEventLogEntries(events);

    for each(CtiRequestMsg* message in requests)
    {
        CtiCapController::getInstance()->manualCapBankControl(message,NULL);
    }
    requests.clear();

    for each(CtiSignalMsg* message in signals)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(message, CALLSITE);
    }
    signals.clear();

    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas()))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(ccStations))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(modifiedSubsList,
                                                                   CtiCCSubstationBusMsg::SubBusModified))->execute();

    EventLogEntries ccEvents;

    //Happens for all send alls
    printOutEventLogsByIdAndType(paoId,type,actionText,_command->getUser(), pointChanges, ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
    }
    else
    {
        delete multi;
    }

    CtiCapController::submitEventLogEntries(ccEvents);
}

void CtiCCCommandExecutor::setParentOvUvFlags(int paoId, Cti::CapControl::CapControlType type, bool ovuvFlag, CtiCCSubstationBus_vec& modifiedSubBuses)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    switch (type)
    {
        case Cti::CapControl::SpecialArea:
        {
            CtiCCSpecialPtr sArea = store->findSpecialAreaByPAObjectID(paoId);
            sArea->setOvUvDisabledFlag(ovuvFlag);

            for each(long stationId in sArea->getSubstationIds())
            {
                setParentOvUvFlags(stationId, Cti::CapControl::Substation, ovuvFlag, modifiedSubBuses);
            }
            break;
        }
        case Cti::CapControl::Area:
        {
            CtiCCAreaPtr area = store->findAreaByPAObjectID(paoId);
            area->setOvUvDisabledFlag(ovuvFlag);

            for each(long stationId in area->getSubstationIds())
            {
                setParentOvUvFlags(stationId, Cti::CapControl::Substation, ovuvFlag, modifiedSubBuses);
            }
            break;
        }
        case Cti::CapControl::Substation:
        {
            CtiCCSubstationPtr station = store->findSubstationByPAObjectID(paoId);
            station->setOvUvDisabledFlag(ovuvFlag);

            for each(long subBusId in station->getCCSubIds())
            {
                setParentOvUvFlags(subBusId, Cti::CapControl::SubBus, ovuvFlag, modifiedSubBuses);
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
                                                        CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

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
            ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, subBus->getPaoId(), 0, capControlManualCommand, subBus->getEventSequence(), 0, text1, userName));
        }
    }

    return;
}

bool CtiCCCommandExecutor::checkForCommandRefusal(CtiCCFeeder* feeder)
{
    CtiCCCapBank_SVector& ccCapBanks = feeder->getCCCapBanks();

    for(long k=0;k<ccCapBanks.size();k++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
        int status = currentCapBank->getControlStatus();
        if (status == CtiCCCapBank::OpenPending || status == CtiCCCapBank::ClosePending)
        {
            CTILOG_WARN(dout, "Feeder already has bank: "<<currentCapBank->getPaoName() <<" in a pending state.");

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
void CtiCCCommandExecutor::OpenCapBank(long bankId, bool confirmImmediately)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long controlID = 0;
    bool found = false;
    bool savedBusRecentlyControlledFlag = false;
    bool savedFeederRecentlyControlledFlag = false;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCSubstationPtr parentStation = NULL;
    CtiCCSubstation_vec updatedStations;

    CtiCCCapBank* operatingCapBank = NULL;

    if (bankId != 0)
    {

        for(long i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankId == currentCapBank->getPaoId() )
                    {
                        found = true;
                        operatingCapBank = currentCapBank;
                        controlID = currentCapBank->getControlDeviceId();
                        if(!confirmImmediately &&
                           checkForCommandRefusal(currentFeeder))
                        {
                            CTILOG_WARN(dout, "Open Capbank Command Refused");
                            break;
                        }
                        updatedSubs.push_back(currentSubstationBus);

                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(false);
                            currentFeeder->setRecentlyControlledFlag(false);

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, true);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                            currentSubstationBus->setBusUpdatedFlag(true);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue() );
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());

                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";

                                string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                                + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                                string text("Manual Open Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                double kvarBefore = 0;
                                double kvarAfter = 0;
                                double kvarChange = 0;

                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                     currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                                    ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder && !savedFeederRecentlyControlledFlag) )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());

                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                string stateInfo = currentCapBank->getControlStatusQualityString();
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                EventLogEntry logEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(),
                                                       currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(),
                                                       currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange,
                                                       currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo);
                                logEntry.setEventSubtype( ManualOperation );
                                ccEvents.push_back( logEntry );
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }


                            if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
                            {
                                confirmImmediately = true;
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                            {
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = true;
                                }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                                      currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder &&
                                    _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
                            }

                            if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                            {
                                currentSubstationBus->setRecentlyControlledFlag(true);
                                currentFeeder->setRecentlyControlledFlag(true);
                            }
                            else if (confirmImmediately)
                            {
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, controlID);
                            }
                            currentSubstationBus->verifyControlledStatusFlags();
                            parentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (parentStation != NULL && currentSubstationBus->getRecentlyControlledFlag())
                            {
                                parentStation->setRecentlyControlledFlag(true);
                                updatedStations.push_back(parentStation);
                            }
                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
                            }
                            else
                            {
                                CTILOG_WARN(dout, "No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
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
        CTILOG_WARN(dout, "Manual Control Open received for BankID 0...Cannot control device.");
    }


    if( controlID > 0 )
    {
        // Send the activeMQ command.
        Cti::CapControl::sendCapControlOperationMessage( CapControlOperationMessage::createOpenBankMessage( bankId, CtiTime() ) );

        std::unique_ptr<CtiRequestMsg> reqMsg = createBankOpenRequest(*operatingCapBank);

        reqMsg->setSOE(5);

        CtiCapController::sendCapBankRequestAndPoints(std::move(reqMsg), multi);

        CtiCapController::submitEventLogEntries(ccEvents);

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
        CTILOG_WARN(dout, "Could not create Porter Request Message");
    }
}


/*---------------------------------------------------------------------------
    CloseCapBank
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::CloseCapBank(long bankId, bool confirmImmediately)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long controlID = 0;
    bool found = false;
    bool savedBusRecentlyControlledFlag = false;
    bool savedFeederRecentlyControlledFlag = false;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCSubstationPtr parentStation = NULL;
    CtiCCSubstation_vec updatedStations;

    CtiCCCapBank* operatingCapBank = NULL;

    if (bankId != 0)
    {

        for(long i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankId == currentCapBank->getPaoId() )
                    {
                        found = true;
                        operatingCapBank = currentCapBank;
                        controlID = currentCapBank->getControlDeviceId();

                        if(!confirmImmediately &&
                           checkForCommandRefusal(currentFeeder))
                        {
                            CTILOG_WARN(dout, "Close Capbank Command Refused");
                            break;
                        }
                        updatedSubs.push_back(currentSubstationBus);

                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {

                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(false);
                            currentFeeder->setRecentlyControlledFlag(false);

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            //currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, true);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                            currentSubstationBus->setBusUpdatedFlag(true);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue() );
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";

                                string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                                + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                                string text("Manual Close Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                double kvarBefore = 0;
                                double kvarAfter = 0;
                                double kvarChange = 0;

                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
                                {
                                    confirmImmediately = true;
                                }
                                else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                                {
                                    if( savedFeederRecentlyControlledFlag ||
                                        ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                                    {
                                        confirmImmediately = true;
                                    }
                                    if( _IGNORE_NOT_NORMAL_FLAG &&
                                        currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                    {
                                        confirmImmediately = true;
                                    }
                                }
                                else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                                          currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                                {
                                    if( savedBusRecentlyControlledFlag ||
                                        ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                    {
                                        confirmImmediately = true;
                                    }
                                    if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus &&
                                         _IGNORE_NOT_NORMAL_FLAG &&
                                         currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                    {
                                        confirmImmediately = true;
                                    }
                                    if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder &&
                                         _IGNORE_NOT_NORMAL_FLAG &&
                                         (currentFeeder->getCurrentVarPointQuality() != NormalQuality) )
                                    {
                                        confirmImmediately = true;
                                    }

                                }
                                else
                                {
                                    CTILOG_WARN(dout, "Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                                  << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );

                                }

                                if (confirmImmediately)
                                {
                                    doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, controlID);
                                }
                                else
                                {

                                    currentSubstationBus->setRecentlyControlledFlag(true);
                                    currentFeeder->setRecentlyControlledFlag(true);

                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                    if ( !savedBusRecentlyControlledFlag ||
                                         ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder && !savedFeederRecentlyControlledFlag ) )
                                    {
                                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                                        if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                             currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                                    long stationId, areaId, spAreaId;
                                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                    EventLogEntry logEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(),
                                                           currentFeeder->getPaoId(), capControlCommandSent, currentSubstationBus->getEventSequence(),
                                                           currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange,
                                                           currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo);
                                    logEntry.setEventSubtype( ManualOperation );
                                    ccEvents.push_back( logEntry );

                                }
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));

                            }


                            currentSubstationBus->verifyControlledStatusFlags();
                            parentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
                            if (parentStation != NULL && currentSubstationBus->getRecentlyControlledFlag())
                            {
                                parentStation->setRecentlyControlledFlag(true);
                                updatedStations.push_back(parentStation);
                            }

                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {
                                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
                            }
                            else
                            {
                                CTILOG_WARN(dout, "No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
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
        CTILOG_WARN(dout, "Manual Control Close received for BankID 0...Cannot control device.");
    }

    if( controlID > 0 )
    {
        // Send the activeMQ command.
        Cti::CapControl::sendCapControlOperationMessage( CapControlOperationMessage::createCloseBankMessage( bankId, CtiTime() ) );

        std::unique_ptr<CtiRequestMsg> reqMsg = createBankCloseRequest(*operatingCapBank);

        reqMsg->setSOE(5);

        CtiCapController::sendCapBankRequestAndPoints(std::move(reqMsg), multi);

        CtiCapController::submitEventLogEntries(ccEvents);

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
        CTILOG_WARN(dout, "Could not create Porter Request Message");
    }
}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long areaId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Enable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0, currentArea->getPaoId(), 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(currentArea, false);
    }
    else
    {
        CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            //CHECK ALL SUBSTATIONS ASSIGNED TO SPECIAL AREA FIRST,
            //To make sure they are not already on a conflicting different enabled Special Area
            bool refusalFlag = false;
            string refusalText = "Special Area is not enabled!";
            for each (long subId in currentSpArea->getSubstationIds())
            {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(subId);
                if (currentSubstation != NULL)
                {
                    if (currentSubstation->getSaEnabledFlag())
                    {
                        refusalFlag = true;
                        CtiCCSpecialPtr enabledSpArea = store->findSpecialAreaByPAObjectID(currentSubstation->getSaEnabledId());
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
                         new CtiPointDataMsg(currentSpArea->getDisabledStatePointId(), currentSpArea->getDisableFlag()), CALLSITE); // NormalQuality, StatusPointType
                }

            }
            else
            {

                string text1 = string("Manual Enable Special Area");
                string additional1 = string("Special Area: ");
                additional1 += currentSpArea->getPaoName();

                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                store->UpdatePaoDisableFlagInDB(currentSpArea, false, true);

                 for each (long subId in currentSpArea->getSubstationIds())
                 {
                    CtiCCSubstationPtr currentSubstation = NULL;
                    currentSubstation = store->findSubstationByPAObjectID(subId);
                    if (currentSubstation != NULL)
                    {
                        if (!currentSubstation->getSaEnabledFlag())
                        {
                            currentSubstation->setSaEnabledFlag(true);
                            currentSubstation->setSaEnabledId(areaId);
                        }
                    }
                 }
            }
        }
    }

    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
    }
    else
    {
        delete multi;
    }
    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(ccAreas))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas()))->execute();
}

/*---------------------------------------------------------------------------
    DisableArea
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::DisableArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long areaId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;
    CtiMultiMsg_vec& capMessages = multiCapMsg->getData();


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Disable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL,0, currentArea->getPaoId(), 0,  0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        store->UpdatePaoDisableFlagInDB(currentArea, true);
        currentArea->checkForAndStopVerificationOnChildSubBuses(capMessages);
    }
    else
    {
        CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas();

        CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            string text1 = string("Manual Disable Special Area");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPaoName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

            store->UpdatePaoDisableFlagInDB(currentSpArea, true, true);

             for each (long subId in currentSpArea->getSubstationIds())
             {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(subId);
                if (currentSubstation != NULL)
                {
                    if (currentSubstation->getSaEnabledFlag() &&
                        currentSubstation->getSaEnabledId() == areaId)
                    {
                        currentSubstation->setSaEnabledFlag(false);
                    }
                }
            }
        }
    }

    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas()))->execute();
}
void CtiCCCommandExecutor::setAutoControlOvUvFlags(CtiCCSubstationBusPtr currentSubBus, bool disableFlag)
{
    currentSubBus->setOvUvDisabledFlag(disableFlag);
    CtiFeeder_vec& ccFeeders = currentSubBus->getCCFeeders();

    for(long j=0;j<ccFeeders.size();j++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

        currentFeeder->setOvUvDisabledFlag(disableFlag);

        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(long k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

            //limit auto individual commands to one way devices
            if (!currentCapBank->isControlDeviceTwoWay() && _LIMIT_ONE_WAY_COMMANDS)
                continue;

            if (disableFlag)
            {
                if ( !currentCapBank->getOvUvDisabledFlag() )
                {
                    currentCapBank->setReEnableOvUvFlag(true);
                }

            }
            else
            {
                if ( currentCapBank->getReEnableOvUvFlag() )
                {
                    currentCapBank->setReEnableOvUvFlag(false);
                }
            }
        }

    }

}

void CtiCCCommandExecutor::AutoEnableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long cmdId = _itemId;

    //Find the object type
    Cti::CapControl::CapControlType type = store->determineTypeById(cmdId);

    switch (type)
    {
        case Cti::CapControl::Area:
        {
            CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(cmdId);
            CTILOG_INFO(dout, "Auto Enable OvUv By Area Control Point for "<< currentArea->getPaoName());
            currentArea->setOvUvDisabledFlag(false);
            for each(long subId in currentArea->getSubstationIds()  )
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(subId);
                if (currentStation)
                {
                    currentStation->setOvUvDisabledFlag(false);
                    for each( CtiCCSubstationBusPtr currentSubBus in store->getSubBusesByStationId(currentStation->getPaoId()) )
                    {
                        setAutoControlOvUvFlags(currentSubBus, false);
                    }
                }
            }
            break;
        }
        case Cti::CapControl::SpecialArea:
        {
            CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(cmdId);
            CTILOG_INFO(dout, "Auto Enable OvUv By Special Area Control Point for "<< currentSpArea->getPaoName());
            currentSpArea->setOvUvDisabledFlag(false);
            for each(long subId in currentSpArea->getSubstationIds()  )
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(subId);
                if (currentStation)
                {
                    currentStation->setOvUvDisabledFlag(false);
                    for each( CtiCCSubstationBusPtr currentSubBus in store->getSubBusesByStationId(currentStation->getPaoId()) )
                    {
                        setAutoControlOvUvFlags(currentSubBus, false);
                    }
                }

            }
            break;
        }
        case Cti::CapControl::Substation:
        {
            CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(cmdId);
            currentStation->setOvUvDisabledFlag(false);
            CTILOG_INFO(dout, "Auto Enable OvUv By Substation Control Point for "<< currentStation->getPaoName());
            for each( CtiCCSubstationBusPtr currentSubBus in store->getSubBusesByStationId(currentStation->getPaoId()) )
            {
                setAutoControlOvUvFlags(currentSubBus, false);
            }
            break;
        }
        case Cti::CapControl::SubBus:
        {

            CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(cmdId);
            CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(currentSubBus->getParentId());
            CTILOG_INFO(dout, "Auto Enable OvUv By Substation Bus Control Point for "<< currentSubBus->getPaoName());
            setAutoControlOvUvFlags(currentSubBus, false);
            break;
        }
        default:
            break;
    }

    ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_ENABLE_OVUV, cmdId);
    actionMsg->setUser(_command->getUser());
    CtiCCExecutorFactory::createExecutor(actionMsg)->execute();

    return;
}

void CtiCCCommandExecutor::AutoDisableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long cmdId = _itemId;

    //Find the object type
    Cti::CapControl::CapControlType type = store->determineTypeById(cmdId);

    switch (type)
    {
        case Cti::CapControl::Area:
        {
            CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(cmdId);
            CTILOG_INFO(dout, "Auto Disable OvUv By Area Control Point for "<< currentArea->getPaoName());
            currentArea->setOvUvDisabledFlag(true);
            for each(long subId in currentArea->getSubstationIds() )
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(subId);
                if (currentStation)
                {
                    currentStation->setOvUvDisabledFlag(true);
                    for each( CtiCCSubstationBusPtr currentSubBus in store->getSubBusesByStationId(currentStation->getPaoId()) )
                    {
                        setAutoControlOvUvFlags(currentSubBus, true);
                    }
                }
            }
            break;
        }
        case Cti::CapControl::SpecialArea:
        {
            CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(cmdId);
            CTILOG_INFO(dout, "Auto Disable OvUv By Special Area Control Point for "<< currentSpArea->getPaoName());
            currentSpArea->setOvUvDisabledFlag(true);
            for each(long subId in currentSpArea->getSubstationIds()  )
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(subId);
                if (currentStation)
                {
                    currentStation->setOvUvDisabledFlag(true);
                    for each( CtiCCSubstationBusPtr currentSubBus in store->getSubBusesByStationId(currentStation->getPaoId()) )
                    {
                        setAutoControlOvUvFlags(currentSubBus, true);
                    }
                }

            }
            break;
        }
        case Cti::CapControl::Substation:
        {
            CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(cmdId);
            currentStation->setOvUvDisabledFlag(true);
            CTILOG_INFO(dout, "Auto Disable OvUv By Substation Control Point for "<< currentStation->getPaoName());
            for each( CtiCCSubstationBusPtr currentSubBus in store->getSubBusesByStationId(currentStation->getPaoId()) )
            {
                setAutoControlOvUvFlags(currentSubBus, true);
            }
            break;
        }
        case Cti::CapControl::SubBus:
        {

            CtiCCSubstationBusPtr currentSubBus = store->findSubBusByPAObjectID(cmdId);
            CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(currentSubBus->getParentId());
            CTILOG_INFO(dout, "Auto Disable OvUv By Substation Bus Control Point for "<< currentSubBus->getPaoName());
            setAutoControlOvUvFlags(currentSubBus, true);

            break;
        }
        default:
            break;
    }

    ItemCommand* actionMsg = new ItemCommand(CapControlCommand::SEND_DISABLE_OVUV, cmdId);
    actionMsg->setUser(_command->getUser());
    CtiCCExecutorFactory::createExecutor(actionMsg)->execute();

    return;
}


/*---------------------------------------------------------------------------
    EnableSystem
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::EnableSystem()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long areaId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    string text1 = string("Manual Enable System");
    string additional1 = string("CapControl SYSTEM ENABLE ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();
    for(long i=0;i<ccAreas.size();i++)
    {
        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
        if (currentArea != NULL)
        {
            if (currentArea->getReEnableAreaFlag())
            {
                store->UpdatePaoDisableFlagInDB(currentArea, false);
            }
        }
    }

    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long areaId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    string text1 = string("Manual Disable System");
    string additional1 = string("CapControl SYSTEM DISABLE ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();
    for(long i=0;i<ccAreas.size();i++)
    {
        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
        if (currentArea != NULL)
        {
            if (!currentArea->getDisableFlag())
            {
                currentArea->setReEnableAreaFlag(true);
                store->UpdatePaoDisableFlagInDB(currentArea, true);
            }
        }
    }

    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long controlID = 0;
    long bankID = _itemId;
    bool found = false;
    bool savedBusRecentlyControlledFlag = false;
    bool savedFeederRecentlyControlledFlag = false;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;
    CtiCCCapBank* currentCapBank = NULL;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();
    CtiCCSubstationBus_vec updatedSubs;

    for(long i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(long k=0;k<ccCapBanks.size();k++)
            {
                currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getPaoId() )
                {
                    found = true;
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
                            currentSubstationBus->setRecentlyControlledFlag(false);
                            currentFeeder->setRecentlyControlledFlag(false);
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
                                                                       currentCapBank, true);

                            }
                            else
                            {
                                //currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, true);

                            }
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                            currentSubstationBus->setBusUpdatedFlag(true);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";

                                string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                                + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                                string text("Manual Flip Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if ( !savedBusRecentlyControlledFlag ||
                                     ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder && !savedFeederRecentlyControlledFlag ) )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());

                                }
                                double kvarBefore = 0;

                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                     currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(),
                                                       capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text,
                                                       _command->getUser(), kvarBefore, 0, 0, currentCapBank->getIpAddress(), currentCapBank->getActionId(), stateInfo);
                                eventMsg.setEventSubtype( ManualFlipOperation );
                                ccEvents.push_back(eventMsg);
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }


                            bool confirmImmediately = false;

                            if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
                            {
                                confirmImmediately = true;
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                            {
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = true;
                                }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                                      currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus &&
                                     _IGNORE_NOT_NORMAL_FLAG &&
                                     currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder &&
                                     _IGNORE_NOT_NORMAL_FLAG &&
                                     currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
                            }

                            if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                            {
                                currentSubstationBus->setRecentlyControlledFlag(true);
                                currentFeeder->setRecentlyControlledFlag(true);
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
                                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform Flip on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
                            }
                            else
                            {
                                CTILOG_WARN(dout, "No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
                            }
                        }
                        break;
                    }
                    else
                    {
                        CTILOG_WARN(dout, "CBC Not 7010 Device Type.  Flip command not supported ");
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
        std::unique_ptr<CtiRequestMsg> reqMsg = createBankFlipRequest(*currentCapBank);

        reqMsg->setSOE(5);

        CtiCapController::sendCapBankRequestAndPoints(std::move(reqMsg), multi);

        CtiCapController::submitEventLogEntries(ccEvents);

        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs))->execute();
        }
    }
    else
    {
        CTILOG_WARN(dout, "Could not create Porter Request Message");
    }
}


/*---------------------------------------------------------------------------
    Scan2WayDevice
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::Scan2WayDevice(long bankId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long cbcID = 0;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

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
                if (cbcID > 0 && currentCapBank->isControlDeviceTwoWay())
                {

                    string text = string("Manual Scan 2-Way CBC attached to CapBank: ");
                    text += currentCapBank->getPaoName();
                    string additional = string("CBC: ");
                    CHAR devID[20];
                    additional += string (ltoa(currentCapBank->getControlDeviceId(),devID,10));

                    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                    long stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                    ccEvents.push_back(EventLogEntry(0, currentCapBank->getControlPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlManualCommand, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));

                    CtiRequestMsg* reqMsg = createPorterRequestMsg(cbcID,"scan integrity");
                    reqMsg->setSOE(5);
                    CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

                    CtiCapController::submitEventLogEntries(ccEvents);
                }
                else
                {
                    delete multi;
                }
            }
            else
            {
                CTILOG_WARN(dout, "CapBank Not attached to Feeder/Sub.  Cannot Scan Control Device ");
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long subId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subId);
    if (currentSubstationBus != NULL)
    {

        if (!currentSubstationBus->getVerificationFlag() &&
            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
        {

            string text1 = string("Manual Confirm Sub");

            string additional1 = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
            long stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                if (!currentFeeder->getDisableFlag())
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(long k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                        if (!currentCapBank->getDisableFlag() && currentCapBank->getControlDeviceId() > 0 &&
                            currentCapBank->getStatusPointId() > 0)
                        {

                            currentSubstationBus->setRecentlyControlledFlag(false);
                            currentFeeder->setRecentlyControlledFlag(false);
                            controlID = currentCapBank->getControlDeviceId();

                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());


                            currentSubstationBus->setBusUpdatedFlag(true);


                            char tempchar[80] = "";
                            char tempchar1[80] = "";

                            string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                            + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                            string text = string("Manual Confirm ");
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail )
                            {
                                text += "Open Sent, Sub VarLoad = ";
                                //currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, true);


                            }
                            else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                            {
                                text += "Close Sent, Sub VarLoad = ";
                                //currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, true);
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
                            long stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                            EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(),
                                                   capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                            currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                            eventMsg.setActionId(currentCapBank->getActionId());
                            eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                            eventMsg.setEventSubtype( ManualConfirmOperation );
                            ccEvents.push_back(eventMsg);

                            currentCapBank->setBeforeVarsString(" Confirm ");
                            currentCapBank->setAfterVarsString(" --- ");
                            currentCapBank->setPercentChangeString(" --- ");

                            if( controlID > 0 )
                            {
                                std::unique_ptr<CtiRequestMsg> reqMsg;

                                if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending)
                                {
                                    reqMsg = createBankOpenRequest(*currentCapBank);
                                }
                                else //CLOSE
                                {
                                    reqMsg = createBankCloseRequest(*currentCapBank);
                                }
                                reqMsg->setSOE(2);
                                pilMessages.push_back(reqMsg.release());
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Could not create Porter Request Message");
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

    CtiCapController::submitEventLogEntries(ccEvents);
}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ConfirmFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long fdrId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

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

        string additional1 = "Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
        long stationId, areaId, spAreaId;
        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
        ccEvents.push_back( EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));



        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(long k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

            if (!currentCapBank->getDisableFlag() && currentCapBank->getControlDeviceId() > 0 &&
                currentCapBank->getStatusPointId() > 0)
            {

                currentSubstationBus->setRecentlyControlledFlag(false);
                currentFeeder->setRecentlyControlledFlag(false);
                controlID = currentCapBank->getControlDeviceId();

                if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                    currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                currentSubstationBus->setLastOperationTime(CtiTime());
                currentFeeder->setLastOperationTime(CtiTime());


                currentSubstationBus->setBusUpdatedFlag(true);


                char tempchar[80] = "";
                char tempchar1[80] = "";

                string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                string text = string("Manual Confirm ");

                if (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail )
                {
                    text += "Open Sent, Sub VarLoad = ";
                    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                           currentCapBank, true);


                }
                else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                {
                    text += "Close Sent, Sub VarLoad = ";
                    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                           currentCapBank, true);
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
                long stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(),
                                       capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                eventMsg.setActionId(currentCapBank->getActionId());
                eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                eventMsg.setEventSubtype( ManualConfirmOperation );
                ccEvents.push_back(eventMsg);

                currentCapBank->setBeforeVarsString(" Confirm ");
                currentCapBank->setAfterVarsString(" --- ");
                currentCapBank->setPercentChangeString(" --- ");

                if( controlID > 0 )
                {
                    std::unique_ptr<CtiRequestMsg> reqMsg;

                    if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending)
                    {
                        reqMsg = createBankOpenRequest(*currentCapBank);
                    }
                    else //CLOSE
                    {
                        reqMsg = createBankCloseRequest(*currentCapBank);
                    }
                    reqMsg->setSOE(2);
                    pilMessages.push_back(reqMsg.release());
                }
                else
                {
                    CTILOG_WARN(dout, "Could not create Porter Request Message");
                }

                doConfirmImmediately(currentSubstationBus, pointChanges, ccEvents, controlID);


            }

        }
        currentSubstationBus->verifyControlledStatusFlags();
    }
    else
    {
        CTILOG_WARN(dout, "Manual Confirm Feeder: "<<currentFeeder->getPaoName() <<" inhibited due to: "
                    << (currentFeeder->getDisableFlag() ? "DisableFlag." : "No Control Method."));
    }


    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    else
    {
        delete multi;
        delete multiPilMsg;
    }

    CtiCapController::submitEventLogEntries(ccEvents);
}

void CtiCCCommandExecutor::ConfirmArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long areaId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* confirmMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);

    if (currentArea != NULL)
    {
        string text1 = string("Manual Confirm Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL,0, currentArea->getPaoId(), 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        for each(long stationId in currentArea->getSubstationIds())
        {
            CtiCCSubstationPtr station = store->findSubstationByPAObjectID(stationId);
            if (station != NULL)
            {
                for each (long busId in station->getCCSubIds())
                {
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
                }
            }
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

        CtiCapController::submitEventLogEntries(ccEvents);

        if (multi->getCount() > 0)
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
        }
        else
        {
            delete multi;
        }

    }
    else
    {
        CtiCCSpecialPtr currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            //Make sure this special area is enabled, or kick back an error to the clients.
            if(currentSpArea->getDisableFlag())
            {
                CTILOG_WARN(dout, "Special Area is not enabled, commands refused.");

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
                ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, currentSpArea->getPaoId(), 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

                for each (long stationId in currentSpArea->getSubstationIds())
                {
                    CtiCCSubstationPtr station = store->findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {
                        for each (long busId in station->getCCSubIds())
                        {
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
                        }
                    }
                }

                if (confirmMulti->getCount() > 0)
                {
                    CtiCCExecutorFactory::createExecutor(confirmMulti)->execute();
                    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas()))->execute();
                }

                CtiCapController::submitEventLogEntries(ccEvents);

                if (multi->getCount() > 0)
                {
                    CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* confirmMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationPtr station = store->findSubstationByPAObjectID(_itemId);
    if (station != NULL)
    {
        string text1 = string("Manual Confirm Substation");
        string additional1 = string("Substation: ");
        additional1 += station->getPaoName();

        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0, station->getParentId(), station->getPaoId(), 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        for each (long busId in station->getCCSubIds())
        {
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
        }

        if (confirmMulti->getCount() > 0)
        {
            CtiCCExecutorFactory::createExecutor(confirmMulti)->execute();
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(store->getCCSubstations()))->execute();
        }

        CtiCapController::submitEventLogEntries(ccEvents);

        if (multi->getCount() > 0)
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long controlID = 0;
    long bankID = _itemId;
    bool found = false;
    bool savedBusRecentlyControlledFlag = false;
    bool savedFeederRecentlyControlledFlag = false;
    long savedControlStatus = -10;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCCapBank* operatingCapBank = NULL;

    if (bankID != 0)
    {

        for(long i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankID == currentCapBank->getPaoId() )
                    {
                        found = true;
                        operatingCapBank = currentCapBank;
                        controlID = currentCapBank->getControlDeviceId();

                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(false);
                            currentFeeder->setRecentlyControlledFlag(false);


                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            savedControlStatus = currentCapBank->getControlStatus();
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       currentCapBank, true);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentSubstationBus->setBusUpdatedFlag(true);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());

                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";

                                string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                                + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                                string text("Manual Confirm Open Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( ( !savedBusRecentlyControlledFlag ||
                                      ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder && !savedFeederRecentlyControlledFlag) ) &&
                                     savedControlStatus != CtiCCCapBank::Open )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());
                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(),
                                                       capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                eventMsg.setActionId(currentCapBank->getActionId());
                                eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                                eventMsg.setEventSubtype( ManualConfirmOperation );
                                ccEvents.push_back(eventMsg);

                                currentCapBank->setBeforeVarsString(" Confirm ");
                                currentCapBank->setAfterVarsString(" --- ");
                                currentCapBank->setPercentChangeString(" --- ");

                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                            }


                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {

                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }

                            bool confirmImmediately = false;
                            if( savedControlStatus == CtiCCCapBank::Open )
                            {
                                confirmImmediately = true;
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
                            {
                                confirmImmediately = true;
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                            {
                                long sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    CtiCCCapBank_SVector& banks = currentFeeder->getCCCapBanks();

                                    for(long l=0;l<banks.size();l++)
                                    {
                                        CtiCCCapBank* bank = (CtiCCCapBank*)banks[l];
                                        if (bank->getStatusPointId() > 0)
                                        {
                                            if ((bank->getControlDeviceId() != controlID && (bank->getControlStatus() == CtiCCCapBank::OpenPending || bank->getControlStatus() == CtiCCCapBank::ClosePending ) ) ||
                                                 savedControlStatus == CtiCCCapBank::OpenPending )
                                            {
                                                confirmImmediately = true;
                                                break;
                                            }

                                        }
                                        //confirmImmediately = true;
                                    }
                                }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                                      currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                long sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();

                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus &&
                                     _IGNORE_NOT_NORMAL_FLAG &&
                                     currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder &&
                                     _IGNORE_NOT_NORMAL_FLAG &&
                                     currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
                            }

                            if( !confirmImmediately )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(true);
                                currentFeeder->setRecentlyControlledFlag(true);
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
                                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
                            }
                            else
                            {
                                CTILOG_WARN(dout, "No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
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
        CTILOG_WARN(dout, "Manual Control Open received for BankID 0...Cannot control device.");
    }

    if( controlID > 0 )
    {
        std::unique_ptr<CtiRequestMsg> reqMsg = createBankOpenRequest(*operatingCapBank);

        reqMsg->setSOE(5);

        CtiCapController::sendCapBankRequestAndPoints(std::move(reqMsg), multi);

        CtiCapController::submitEventLogEntries(ccEvents);

        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs, CtiCCSubstationBusMsg::SubBusAdded))->execute();
        }
    }
    else
    {
        delete multi;
        CTILOG_WARN(dout, "Could not create Porter Request Message");
    }
}

/*---------------------------------------------------------------------------
    ConfirmClose
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ConfirmClose()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long controlID = 0;
    long bankID = _itemId;
    bool found = false;
    bool savedBusRecentlyControlledFlag = false;
    bool savedFeederRecentlyControlledFlag = false;
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    long savedControlStatus = -10;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();
    CtiCCSubstationBus_vec updatedSubs;

    CtiCCCapBank* operatingCapBank = NULL;

    if (bankID != 0)
    {
        for(long i=0;i<ccSubstationBuses.size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( bankID == currentCapBank->getPaoId() )
                    {
                        found = true;
                        operatingCapBank = currentCapBank;
                        controlID = currentCapBank->getControlDeviceId();

                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() &&
                            currentSubstationBus->getStrategy()->getUnitType() != ControlStrategy::None)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(false);
                            currentFeeder->setRecentlyControlledFlag(false);


                            if (ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPaoId());
                            currentSubstationBus->setLastFeederControlled(currentFeeder->getPaoId());
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());
                            savedControlStatus = currentCapBank->getControlStatus();
                            store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       currentCapBank, true);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            currentSubstationBus->setBusUpdatedFlag(true);
                            currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                            currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                char tempchar[80] = "";
                                char tempchar1[80] = "";

                                string additional = "Sub: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus )
                                                + "  Feeder: " + currentFeeder->getPaoName() + formatMapInfo( currentFeeder );

                                string text("Manual Confirm Close Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( ( !savedBusRecentlyControlledFlag ||
                                      ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder && !savedFeederRecentlyControlledFlag) ) &&
                                    savedControlStatus != CtiCCCapBank::Close )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());
                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(),
                                                       capControlCommandSent, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser());
                                currentCapBank->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1);
                                eventMsg.setActionId(currentCapBank->getActionId());
                                eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                                eventMsg.setEventSubtype( ManualConfirmOperation );
                                ccEvents.push_back(eventMsg);

                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                            }

                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {

                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }

                            bool confirmImmediately = false;
                            if( savedControlStatus == CtiCCCapBank::Close )
                            {
                                confirmImmediately = true;
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
                            {
                                confirmImmediately = true;
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                            {
                                long sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                                if( savedFeederRecentlyControlledFlag ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                                    ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                                {
                                    CtiCCCapBank_SVector& banks = currentFeeder->getCCCapBanks();

                                    for(long l=0;l<banks.size();l++)
                                    {
                                        CtiCCCapBank* bank = (CtiCCCapBank*)banks[l];
                                        if (bank->getStatusPointId() > 0)
                                        {
                                            if ((bank->getControlDeviceId() != controlID && (bank->getControlStatus() == CtiCCCapBank::OpenPending || bank->getControlStatus() == CtiCCCapBank::ClosePending ) ) ||
                                                 savedControlStatus == CtiCCCapBank::ClosePending )
                                            {
                                                confirmImmediately = true;
                                                break;
                                            }

                                        }
                                        //confirmImmediately = true;
                                    }                            }
                                if( _IGNORE_NOT_NORMAL_FLAG &&
                                    currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus ||
                                      currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                            {
                                long sendRetries = currentSubstationBus->getControlSendRetries();
                                if (currentFeeder->getStrategy()->getUnitType() != ControlStrategy::None && currentFeeder->getStrategy()->getControlSendRetries() > 0)
                                    sendRetries = currentFeeder->getStrategy()->getControlSendRetries();
                                if( savedBusRecentlyControlledFlag ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                                    ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getStrategy()->getMaxConfirmTime()/(sendRetries+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::SubstationBus &&
                                     _IGNORE_NOT_NORMAL_FLAG &&
                                     currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder &&
                                     _IGNORE_NOT_NORMAL_FLAG &&
                                     currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                                {
                                    confirmImmediately = true;
                                }
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Unknown Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
                                /*if (currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::None)
                                {
                                    confirmImmediately = true;
                                }*/
                            }

                            if( !confirmImmediately )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(true);
                                currentFeeder->setRecentlyControlledFlag(true);
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
                                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                             <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
                            }
                            else
                            {
                                CTILOG_WARN(dout, "No Control Method: " << currentSubstationBus->getStrategy()->getControlMethod()
                                              << " PAOID: " << currentSubstationBus->getPaoId() << " Name: " << currentSubstationBus->getPaoName() );
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
        CTILOG_ERROR(dout, "Manual Confirm Close received for BankID 0...Cannot control device.");
    }

    if( controlID > 0 )
    {
        std::unique_ptr<CtiRequestMsg> reqMsg = createBankCloseRequest(*operatingCapBank);

        reqMsg->setSOE(5);

        CtiCapController::sendCapBankRequestAndPoints(std::move(reqMsg), multi);

        CtiCapController::submitEventLogEntries(ccEvents);

        if (updatedSubs.size() > 0)
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(updatedSubs, CtiCCSubstationBusMsg::SubBusAdded))->execute();
        }
    }
    else
    {
        delete multi;
        CTILOG_WARN(dout, "Could not create Porter Request Message");
    }
}

/*---------------------------------------------------------------------------
    doConfirmImmediately
---------------------------------------------------------------------------*/

void CtiCCCommandExecutor::doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, long controllerId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[currentSubstationBus->getLastFeederControlledPosition()];

    if( currentFeeder->getPaoId() == currentSubstationBus->getLastFeederControlledPAOId() )
    {
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(long k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
            if (controllerId == currentCapBank->getControlDeviceId())
            {
                currentFeeder->setRecentlyControlledFlag(false);
                currentFeeder->setRetryIndex(0);
                if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    currentCapBank->setControlStatus(CtiCCCapBank::Close);
                    currentCapBank->setControlStatusQuality(CC_Normal);
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);

                        double kvarBefore = 0;
                        double kvarAfter = 0;
                        double kvarChange = 0;

                        if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                             currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                        long stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                        ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close",
                                                                _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));

                        currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                        currentCapBank->setAfterVarsString(" Forced Confirm ");
                        currentCapBank->setPercentChangeString(" --- ");


                    }
                    else
                    {
                        CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                      << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
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

                        double kvarBefore = 0;
                        double kvarAfter = 0;
                        double kvarChange = 0;

                        if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                             currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                        long stationId, areaId, spAreaId;
                        store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                        ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open",
                                                                 _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));

                        currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                        currentCapBank->setAfterVarsString(" Forced Confirm ");
                        currentCapBank->setPercentChangeString(" --- ");

                    }
                    else
                    {
                        CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                      << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                    }
                }
                currentCapBank->setRetryOpenFailedFlag(false);
                currentCapBank->setRetryCloseFailedFlag(false);

            }
        }
        currentSubstationBus->setRecentlyControlledFlag(false);

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
                    currentSubstationBus->setRecentlyControlledFlag(true);
                    currentFeeder->setRecentlyControlledFlag(true);
                }
            }
        }

        if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
        {
            for(long x=0;x<ccFeeders.size();x++)
            {
                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                {
                    currentSubstationBus->setRecentlyControlledFlag(true);
                    break;
                }
            }
        }
    }
    else
    {
        for(long j=0;j<ccFeeders.size();j++)
        {
            currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( currentFeeder->getPaoId() == currentSubstationBus->getLastFeederControlledPAOId() )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
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

                                double kvarBefore = 0;
                                double kvarAfter = 0;
                                double kvarChange = 0;

                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                     currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close", _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId));
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" Forced Confirm ");
                                currentCapBank->setPercentChangeString(" --- ");

                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
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

                                double kvarBefore = 0;
                                double kvarAfter = 0;
                                double kvarChange = 0;

                                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder ||
                                     currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::BusOptimizedFeeder )
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
                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open",
                                                                        _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId, stateInfo));
                                currentCapBank->setBeforeVarsString(currentFeeder->createVarText(kvarBefore, 1.0));
                                currentCapBank->setAfterVarsString(" Forced Confirm ");
                                currentCapBank->setPercentChangeString(" --- ");

                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                                              << " PAOID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                            }
                        }
                        currentCapBank->setRetryOpenFailedFlag(false);
                        currentCapBank->setRetryCloseFailedFlag(false);
                    }
                }
                currentSubstationBus->setRecentlyControlledFlag(false);
                currentFeeder->setRecentlyControlledFlag(false);

                if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                {
                    for(long x=0;x<ccFeeders.size();x++)
                    {
                        if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                        {
                            currentSubstationBus->setRecentlyControlledFlag(true);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses()), CtiCCSubstationBusMsg::AllSubBusesSent))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(*store->getCCGeoAreas()))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCCapBankStatesMsg(*store->getCCCapBankStates()))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas()))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg((store->getCCSubstations()), CtiCCSubstationsMsg::AllSubsSent))->execute();

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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long bankId = _itemId;
    bool found = false;
    long tempFeederId = 0;
    long movedCapBankId = bankId;
    long originalFeederId = 0;
    float capSwitchingOrder = 0.0;
    float closeOrder = 0.0;
    float tripOrder = 0.0;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

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
                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                                 <<".  Cannot perform RETURN CAP TO ORIGINAL FEEDER on Cap Bank: " << currentCapBank->getPaoName() << " PAOID: " << currentCapBank->getPaoId() << ".");
            }
        }

    }


    //moveCapBank(true, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder);
    if( tempFeederId > 0 && movedCapBankId > 0 && originalFeederId > 0 )
    {
        CTILOG_INFO(dout, "Move Cap Bank to original feeder PAO Id: " << movedCapBankId);
        moveCapBank(1, tempFeederId, movedCapBankId, originalFeederId, capSwitchingOrder, closeOrder, tripOrder);
    }
    else
    {
        if( tempFeederId==0 )
        {
            CTILOG_WARN(dout, "Feeder not found PAO Id: " << tempFeederId);
        }
        if( originalFeederId==0 )
        {
            CTILOG_WARN(dout, "Feeder not found PAO Id: " << originalFeederId);
        }
        if( movedCapBankId==0 )
        {
            CTILOG_WARN(dout, "Cap Bank not found PAO Id: " << movedCapBankId);
        }
    }
}

/*---------------------------------------------------------------------------
    ReturnFeederToOriginalSubBus
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ReturnFeederToOriginalSubBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long fdrId = _itemId;
    bool found = false;
    long tempSubBusId = 0;
    long movedFeederId = fdrId;
    long originalSubBusId = 0;
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
            CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPaoName() <<" PAOID: "<< currentSubstationBus->getPaoId()
                             <<".  Cannot perform RETURN FEEDER on Fdr: " << currentFeeder->getPaoName() << " PAOID: " << currentFeeder->getPaoId() << ".");
        }
    }

    if( tempSubBusId > 0 && movedFeederId > 0 && originalSubBusId > 0 )
    {
        CTILOG_INFO(dout, "Move Feeder to original SubBus PAO Id: " << originalSubBusId);
        moveFeeder(true, tempSubBusId, movedFeederId, originalSubBusId, fdrSwitchingOrder);
    }
    else
    {
        if( tempSubBusId==0 )
        {
            CTILOG_WARN(dout, "SubBus not found PAO Id: " << tempSubBusId);
        }
        if( originalSubBusId==0 )
        {
            CTILOG_WARN(dout, "SubBus not found PAO Id: " << originalSubBusId);
        }
        if( movedFeederId==0 )
        {
            CTILOG_WARN(dout, "Feeder not found PAO Id: " << movedFeederId);
        }
    }
}

/*---------------------------------------------------------------------------
    ResetDailyOperations
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ResetDailyOperations()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();

    CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
    EventLogEntries ccEvents;

    long paoId = _itemId;
    bool found = false;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(paoId);
    if (currentStation != NULL)
    {
        for each (long busId in currentStation->getCCSubIds())
        {
            CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
            if (currentSubstationBus != NULL)
            {
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(long j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(long k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        currentCapBank->setCurrentDailyOperations(0);
                    }
                    currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                }
                currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
                long stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                    ccEvents.push_back(EventLogEntry(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                else
                    ccEvents.push_back(EventLogEntry(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                currentSubstationBus->setBusUpdatedFlag(true);
                found = true;
            }
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

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    currentCapBank->setCurrentDailyOperations(0);
                }
                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
            }
            currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);

            INT seqId = CCEventSeqIdGen();
            currentSubstationBus->setEventSequence(seqId);
            long stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                ccEvents.push_back(EventLogEntry(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
            else
                ccEvents.push_back(EventLogEntry(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));


            string text1 = string("Reset Daily Operations");
            string additional1 = string("Substation Bus: ");
            additional1 += currentSubstationBus->getPaoName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setBusUpdatedFlag(true);
            found = true;

        }
        else
        {
            CtiCCFeeder* currentFeeder =  store->findFeederByPAObjectID(paoId);

            if( currentFeeder != NULL )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    currentCapBank->setCurrentDailyOperations(0);
                }

                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);

                INT seqId = CCEventSeqIdGen();
                long stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                if (currentFeeder->getDailyOperationsAnalogPointId() > 0)
                    ccEvents.push_back(EventLogEntry(0,currentFeeder->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentFeeder->getParentId(), currentFeeder->getPaoId(), capControlSetOperationCount, seqId, currentFeeder->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                else
                    ccEvents.push_back(EventLogEntry(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentFeeder->getParentId(), currentFeeder->getPaoId(), capControlSetOperationCount, seqId, currentFeeder->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                string text1 = string("Reset Daily Operations");
                string additional1 = string("Feeder: ");
                additional1 += currentFeeder->getPaoName();

                pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));

                currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());

                if (currentSubstationBus != NULL)
                {
                    currentSubstationBus->setBusUpdatedFlag(true);
                }
                found = true;

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
                            long stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            ccEvents.push_back(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }
                        else
                        {
                            currentCapBank->setTotalOperations(0);
                        }
                        currentCapBank->setCurrentDailyOperations(0);

                        string text1 = string("Reset Daily Operations");
                        string additional1 = string("CapBank: ");
                        additional1 += currentCapBank->getPaoName();

                        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));


                        currentSubstationBus->setBusUpdatedFlag(true);
                        found = true;
                    }
                }
            }

        }
    }

    if( found )
    {
        CTILOG_INFO(dout, "Reset Daily Operations for PAO Id: " << paoId);
        if( pointChanges.size() > 0 )
        {
            CtiCapController::getInstance()->sendMessageToDispatch(multiDispatchMsg, CALLSITE);
        }
        else
            delete multiDispatchMsg;

        CtiCapController::submitEventLogEntries(ccEvents);
    }
    else
    {
        delete multiDispatchMsg;

        CTILOG_WARN(dout, "Cannot find PAO Id: " << paoId << " in ResetDailyOperations()");
    }
}


/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::ResetAllSystemOpCounts()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long areaId = _itemId;
    long controlID = 0;
    bool found = false;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    EventLogEntries ccEvents;

    string text1 = string("Manual System Reset All Op Counts");
    string additional1 = string("CapControl SYSTEM RESET OP COUNTS ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, 0, 0, 0, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();
    const CtiCCSubstation_vec& ccStations = store->getCCSubstations();
    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas();

    for (int i = 0; i <ccAreas.size(); i++ )
    {
        CtiCCAreaPtr currentArea = (CtiCCAreaPtr)ccAreas.at(i);
        if ( currentArea != NULL && ! currentArea->getDisableFlag())
        {
            for each (long stationId in currentArea->getSubstationIds())
            {
                CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(stationId);

                if (currentStation != NULL)
                {
                    for each (long busId in currentStation->getCCSubIds())
                    {
                        CtiCCSubstationBus* currentSubstationBus = store->findSubBusByPAObjectID(busId);
                        if (currentSubstationBus != NULL)
                        {
                            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                            for(long j=0;j<ccFeeders.size();j++)
                            {
                                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                                for(long k=0;k<ccCapBanks.size();k++)
                                {
                                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                                    currentCapBank->setCurrentDailyOperations(0);
                                    currentCapBank->setTotalOperationsAndSendMsg(0, pointChanges);
                                }
                                currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                            }
                            currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            long stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                                ccEvents.push_back(EventLogEntry(0,currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));
                            else
                                ccEvents.push_back(EventLogEntry(0,SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentSubstationBus->getCurrentDailyOperations(), "opCount adjustment", _command->getUser()));

                            currentSubstationBus->setBusUpdatedFlag(true);
                            found = true;
                        }
                    }
                }
            }
        }
    }


    CtiCapController::submitEventLogEntries(ccEvents);

    if (multi->getCount() > 0)
    {
        CtiCapController::getInstance()->sendMessageToDispatch(multi, CALLSITE);
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
    long oldFeederId = _capMoveMsg->getOldFeederId();
    long movedCapBankId = _capMoveMsg->getItemId();
    long newFeederId = _capMoveMsg->getNewFeederId();
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    // Source Feeder -- feeder we are moving from

    CtiCCFeederPtr  oldFeederPtr = store->findFeederByPAObjectID( oldFeederId );

    if ( ! oldFeederPtr )
    {
        CTILOG_WARN( dout, "Feeder not found with ID: " << oldFeederId );
        return false;
    }

    CtiCCSubstationBusPtr   oldFeederParentSub = store->findSubBusByPAObjectID( oldFeederPtr->getParentId() );

    if ( ! oldFeederParentSub )
    {
        CTILOG_WARN( dout, "Substation Bus not found with ID: " << oldFeederPtr->getParentId() );
        return false;
    }

    if ( oldFeederParentSub->getVerificationFlag() )
    {
        CTILOG_WARN( dout, "Cap Bank Verification is ENABLED on Substation Bus: "
                                << oldFeederParentSub->getPaoName() << " PAOID: " << oldFeederParentSub->getPaoId()
                                << ".  Cannot perform MOVE CAPBANK on Feeder: "
                                << oldFeederPtr->getPaoName() << " PAOID: " << oldFeederPtr->getPaoId() << "." );
        return false;
    }
    
    // Destination Feeder -- feeder we are moving to

    CtiCCFeederPtr  newFeederPtr = store->findFeederByPAObjectID( newFeederId );

    if ( ! newFeederPtr )
    {
        CTILOG_WARN( dout, "Feeder not found with ID: " << newFeederId );
        return false;
    }
        
    CtiCCSubstationBusPtr   newFeederParentSub = store->findSubBusByPAObjectID( newFeederPtr->getParentId() );

    if ( ! newFeederParentSub )
    {
        CTILOG_WARN( dout, "Substation Bus not found with ID: " << newFeederPtr->getParentId() );
        return false;
    }

    if ( newFeederParentSub->getVerificationFlag() )
    {
        CTILOG_WARN( dout, "Cap Bank Verification is ENABLED on Substation Bus: "
                                << newFeederParentSub->getPaoName() << " PAOID: " << newFeederParentSub->getPaoId()
                                << ".  Cannot perform MOVE CAPBANK on Feeder: "
                                << newFeederPtr->getPaoName() << " PAOID: " << newFeederPtr->getPaoId() << "." );
        return false;
    }

    // Bank we are moving

    CtiCCCapBankPtr movedCapBankPtr = store->findCapBankByPAObjectID( movedCapBankId );

    if ( ! movedCapBankPtr )
    {
        CTILOG_WARN( dout, "Cap Bank not found with ID: " << movedCapBankId );
        return false;
    }

    // If we're here, we have all of the pieces necessary to do the move

    // remove the bank from the old feeder
    {
        CtiCCCapBank_SVector& oldBankList = oldFeederPtr->getCCCapBanks();

        oldBankList.erase( std::remove( oldBankList.begin(), oldBankList.end(), movedCapBankPtr ), oldBankList.end() );

        store->removeItemsFromMap( CtiCCSubstationBusStore::CapBankIdFeederIdMap, movedCapBankId );
        store->removeItemsFromMap( CtiCCSubstationBusStore::CapBankIdSubBusIdMap, movedCapBankId );

        oldFeederParentSub->setBusUpdatedFlag( true );

        // Temp move, preserve the original orders
        if ( ! permanentFlag )
        {
            movedCapBankPtr->getOriginalParent().setOriginalParentId( oldFeederPtr->getPaoId() );
            movedCapBankPtr->getOriginalParent().setOriginalSwitchingOrder( movedCapBankPtr->getControlOrder() );
            movedCapBankPtr->getOriginalParent().setOriginalCloseOrder( movedCapBankPtr->getCloseOrder() );
            movedCapBankPtr->getOriginalParent().setOriginalTripOrder( movedCapBankPtr->getTripOrder() );
        }
        else    // Permanent, set to 0 since there is no need to preserve the original orders
        {
            movedCapBankPtr->getOriginalParent().setOriginalParentId( 0 );
            movedCapBankPtr->getOriginalParent().setOriginalSwitchingOrder( 0.0 );
            movedCapBankPtr->getOriginalParent().setOriginalCloseOrder( 0.0 );
            movedCapBankPtr->getOriginalParent().setOriginalTripOrder( 0.0 );
        }

        // YUK-17246
        //  If the bank is attached to a bus that is under IVVC control, we should remove the bank from its
        //  currently assigned control zone.
        if ( oldFeederParentSub->getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar )
        {
            // We are modifying an IVVC zone, so get the zone ID(s) we are changing first...

            std::vector<long>   zonesToReload;
            Cti::Database::DatabaseConnection   connection;

            {
                static const std::string sql =
                    "SELECT "
                        "M.ZoneId "
                    "FROM "
                        "CapBankToZoneMapping M "
                    "WHERE "
                        "M.DeviceId = ?";

                Cti::Database::DatabaseReader   rdr( connection, sql );

                rdr << movedCapBankId;

                rdr.execute();

                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                     CTILOG_INFO( dout, rdr.asString() );
                }

                while ( rdr() )
                {
                    zonesToReload.push_back( rdr["ZoneId"].as<long>() );
                }
            }

            // Now remove the bank from the zones
            {
                static const std::string sql =
                    "DELETE FROM "
                        "CapBankToZoneMapping "
                    "WHERE "
                        "DeviceId = ?";

                Cti::Database::DatabaseReader   rdr( connection, sql );

                rdr << movedCapBankId;

                rdr.execute();

                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                     CTILOG_INFO( dout, rdr.asString() );
                }
            }

            // Now issue zone reloads and DB changes.

            for ( auto zoneID : zonesToReload )
            {
                store->getZoneManager().reload( zoneID );

                auto dbChange = std::make_unique<CtiDBChangeMsg>( zoneID,
                                                                  ChangeIvvcZone,
                                                                  "CAPCONTROL",
                                                                  "CAPCONTROL",
                                                                  ChangeTypeUpdate );
                dbChange->setSource( CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE );

                CtiCapController::getInstance()->sendMessageToDispatch( dbChange.release(), CALLSITE );
            }
        }
    }

    // Add bank to new feeder
    {
        movedCapBankPtr->setParentId( newFeederId );
        movedCapBankPtr->setControlOrder( capSwitchingOrder );
        movedCapBankPtr->setCloseOrder( closeOrder );
        movedCapBankPtr->setTripOrder( tripOrder );
        movedCapBankPtr->setDirty( true );

        /**
         * Adding the bank to this feeder may not be necessary if it's
         * here as a result of a temp move and is being kept here
         * instead of moved to its original parent.
         */

        CtiCCCapBank_SVector& newBankList = newFeederPtr->getCCCapBanks();

        if ( std::none_of( newBankList.begin(), newBankList.end(), 
                           [ movedCapBankPtr ]( CtiCCCapBankPtr bank )
                           {
                               return bank == movedCapBankPtr;
                           } ) )
        {
            newBankList.push_back( movedCapBankPtr );
        }
        
        long subBusId = newFeederParentSub->getPaoId();

        store->insertItemsIntoMap( CtiCCSubstationBusStore::CapBankIdFeederIdMap, &movedCapBankId, &newFeederId );
        store->insertItemsIntoMap( CtiCCSubstationBusStore::CapBankIdSubBusIdMap, &movedCapBankId, &subBusId );

        newFeederParentSub->setBusUpdatedFlag( true );

        //If permanent, reorder the banks to get rid of any '.' values.

        if ( permanentFlag )
        {
            newFeederPtr->orderBanksOnFeeder();
            oldFeederPtr->orderBanksOnFeeder();
        }
    }

    store->UpdateFeederBankListInDB( oldFeederPtr );
    store->UpdateFeederBankListInDB( newFeederPtr );

    {
        const std::string typeString = ( permanentFlag ? "Permanent" : "Temporary" );

        CTILOG_INFO( dout, "Manual "
                             << typeString
                             << " Cap Bank with PAO Id: "
                             << movedCapBankPtr->getPaoId() << ", name: "
                             << movedCapBankPtr->getPaoName()
                             << ", was moved from feeder PAO Id: "
                             << oldFeederPtr->getPaoId() << ", name: "
                             << oldFeederPtr->getPaoName() << ", to feeder PAO Id: "
                             << newFeederPtr->getPaoId() << ", name: "
                             << newFeederPtr->getPaoName() << ", with order: "
                             << movedCapBankPtr->getControlOrder() );
    }

    CtiCCSubstationBus_vec modifiedSubsList
    {
        oldFeederParentSub,
        newFeederParentSub
    };

    CtiCCExecutorFactory::createExecutor(
        new CtiCCSubstationBusMsg( modifiedSubsList, CtiCCSubstationBusMsg::SubBusModified ) )->execute();

    return true;
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CtiCCSubstationBus* oldSubBusPtr = NULL;
    CtiCCSubstationBus* newSubBusPtr = NULL;
    CtiCCFeeder* movedFeederPtr = NULL;


    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    bool found = false;
    bool verificationFlag = false;

    movedFeederPtr = store->findFeederByPAObjectID(movedFeederId);
    if (movedFeederPtr == NULL)
        return;

    oldSubBusPtr = store->findSubBusByPAObjectID(oldSubBusId);
    if (oldSubBusPtr != NULL)
    {
        if (oldSubBusPtr->getVerificationFlag())
        {
            {
                CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on Substation Bus: "<< oldSubBusPtr->getPaoName() <<" PAOID: "<< oldSubBusPtr->getPaoId()
                             <<".  Cannot perform MOVE FEEDER: "<<movedFeederPtr->getPaoName()<<" PAOID: "<<movedFeederId<<".");
            }
            verificationFlag = true;
            found = true;
        }
        else
        {
            oldSubBusPtr->setBusUpdatedFlag(true);
        }
    }
    newSubBusPtr = store->findSubBusByPAObjectID(newSubBusId);
    if (newSubBusPtr != NULL)
    {
        if (newSubBusPtr->getVerificationFlag())
         {
             {
                 CTILOG_WARN(dout, "Cap Bank Verification is ENABLED on Substation Bus: "<< newSubBusPtr->getPaoName() <<" PAOID: "<< newSubBusPtr->getPaoId()
                              <<".  Cannot perform MOVE FEEDER: "<<movedFeederPtr->getPaoName()<<" PAOID: "<<movedFeederId<<".");
             }
             verificationFlag = true;
             found = true;
         }
         else
         {
             newSubBusPtr->setBusUpdatedFlag(true);
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

            CTILOG_INFO(dout, "Manual "
                 << typeString
                 << " Feeder with PAO Id: "
                 << movedFeederPtr->getPaoId() << ", name: "
                 << movedFeederPtr->getPaoName()
                 << ", was moved from SubBus PAO Id: "
                 << oldSubBusPtr->getPaoId() << ", name: "
                 << oldSubBusPtr->getPaoName() << ", to SubBus PAO Id: "
                 << newSubBusPtr->getPaoId() << ", name: "
                 << newSubBusPtr->getPaoName() << ", with order: "
                 << movedFeederPtr->getDisplayOrder());
        }

        CtiCCSubstationBus_vec modifiedSubsList;
        modifiedSubsList.clear();
        modifiedSubsList.push_back(newSubBusPtr);
        modifiedSubsList.push_back(oldSubBusPtr);

        CtiCCExecutorFactory::createExecutor( new CtiCCSubstationBusMsg( modifiedSubsList, CtiCCSubstationBusMsg::SubBusModified ))->execute();
    }
    else
    {
        if (!verificationFlag)
        {
            if( oldSubBusPtr==NULL )
            {
                CTILOG_WARN(dout, "SubBus not found PAO Id: " << oldSubBusId);
            }
            if( newSubBusPtr==NULL )
            {
                CTILOG_WARN(dout, "SubBus not found PAO Id: " << newSubBusId);
            }
            if( movedFeederPtr==NULL )
            {
                CTILOG_WARN(dout, "Feeder not found PAO Id: " << movedFeederId);
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
    CtiCCClientListener::getInstance().BroadcastMessage(_ccMsg);
}


/*===========================================================================
    CtiCCForwardMsgToDispatchExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/
void CtiCCForwardMsgToDispatchExecutor::execute()
{
    CtiCapController::getInstance()->sendMessageToDispatch(_ctiMessage->replicateMessage(), CALLSITE);
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
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long pointID = _pointDataMsg->getId();
    double value = _pointDataMsg->getValue();
    unsigned tags = _pointDataMsg->getTags();
    CtiTime& timestamp = _pointDataMsg->getTime();


    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        Cti::FormattedList list;

        list << "Manual point value received from client";
        list.add("ID")          << pointID;
        list.add("Val")         << value;
        list.add("Tags")        << tags;
        list.add("Timestamp")   << timestamp;

        CTILOG_DEBUG(dout, list);
    }

    bool found = false;
    bool logToCCEvent = false;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses();

    for(int i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);

        if( !found )
        {
            if( pointID == currentSubstationBus->getCurrentVarLoadPointId() ||
                pointID == currentSubstationBus->getEstimatedVarLoadPointId() )
            {
                found = true;
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
                            if( value < 0)
                            {
                                CTILOG_WARN(dout, "CapBank:" << currentCapBank->getPaoName() << " received an invalid "
                                         << "control state value (" << (long)value << "). Not adjusting the cap bank." );
                            }
                            else
                            {
                                if( (currentSubstationBus->getRecentlyControlledFlag() || currentSubstationBus->getPerformingVerificationFlag() )&&
                                    currentFeeder->getLastCapBankControlledDeviceId() == currentCapBank->getPaoId() )
                                {
                                    currentSubstationBus->setRecentlyControlledFlag(false);
                                    currentFeeder->setRecentlyControlledFlag(false);
                                    if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
                                    {
                                        for(long x=0;x<ccFeeders.size();x++)
                                        {
                                            if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                            {
                                                currentSubstationBus->setRecentlyControlledFlag(true);
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending)
                                {
                                    currentFeeder->removeMaxKvar(currentCapBank->getPaoId());
                                    logToCCEvent = true;
                                }

                                currentCapBank->setBeforeVarsString(" Manual ");
                                currentCapBank->setAfterVarsString(" Manual ");
                                currentCapBank->setPercentChangeString(" --- ");

                                currentSubstationBus->setBusUpdatedFlag(true);
                                currentCapBank->setControlStatus((LONG)value);
                                currentCapBank->setControlRecentlySentFlag(false);
                                currentFeeder->setRetryIndex(0);
                                currentCapBank->setControlStatusQuality(CC_Normal);
                                currentCapBank->setTagsControlStatus((LONG)tags);
                                currentCapBank->setLastStatusChangeTime(timestamp);
                                currentCapBank->setPorterRetFailFlag(false);
                                currentSubstationBus->figureEstimatedVarLoadPointValue();
                                if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType), CALLSITE);

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

                                long stationId, areaId, spAreaId;
                                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _pointDataMsg->getUser() );
                                eventMsg.setActionId(currentCapBank->getActionId());
                                eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());

                                CtiCapController::submitEventLogEntry(eventMsg);
                            }
                        }
                        currentCapBank->setIgnoreFlag(false);
                        found = true;
                        break;
                    }
                    else if( currentCapBank->getOperationAnalogPointId() == pointID )
                    {
                        /* Commenting this out, to reset disabled bank op counts.  makes sense.*/
                        //if (!currentCapBank->getDisableFlag())
                        {
                            currentSubstationBus->setBusUpdatedFlag(true);
                            currentCapBank->setTotalOperations((LONG) value);
                            currentCapBank->setCurrentDailyOperations((LONG) value);
                            currentCapBank->setLastStatusChangeTime(timestamp);

                            char tempchar[80] = "";
                            string text = "CapBank: ";
                            text+= currentCapBank->getPaoName();
                            text += " Operation Count Change";
                            string additional = "Value = ";
                            _snprintf(tempchar,80,"%.*f",1,value);
                            additional += tempchar;
                            long stationId, areaId, spAreaId;
                            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCapController::submitEventLogEntry(EventLogEntry(0, currentCapBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), text, _pointDataMsg->getUser() ));
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),text,additional,CapControlLogType,SignalEvent,_pointDataMsg->getUser()), CALLSITE);
                        }
                        found = true;
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

    CtiCapController::getInstance()->sendMessageToDispatch(_pointDataMsg->replicateMessage(), CALLSITE);
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
            CTILOG_DEBUG(dout, "Shutting down cap controller thread...");
        }

        CtiCapController::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Cap controller thread shutdown.");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Shutting down client listener thread...");
        }

        CtiCCClientListener::getInstance().stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Client listener thread shutdown.");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Shutting down substation bus store...");
        }

        // stop the BusStore threads...
        {
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

            store->stopThreads();
        }

        CtiCCSubstationBusStore::deleteInstance();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Substation bus store shutdown.");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCCommandExecutor::sendVoltageRegulatorCommands( const long command, const std::string & user )
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

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
                regulator->executeIntegrityScan( user );
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE:
            {
                commandName += "Remote Control Enable";
                regulator->executeEnableRemoteControl( user );
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE:
            {
                commandName += "Remote Control Disable";
                regulator->executeDisableRemoteControl( user );
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE:
            {
                commandName += "Raise Tap Position";
                regulator->executeTapUpOperation( user );
                break;
            }
            case CapControlCommand::VOLTAGE_REGULATOR_TAP_POSITION_LOWER:
            {
                commandName += "Lower Tap Position";
                regulator->executeTapDownOperation( user );
                break;
            }
            default:
            {
                CTILOG_INFO(dout, "Voltage Regulator Command not implemented: " << command);
                break;
            }
        }
    }
    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
    {
        CTILOG_EXCEPTION_ERROR(dout, noRegulator, commandName << " command failed");
    }
    catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
    {
        CTILOG_EXCEPTION_ERROR(dout, missingAttribute, commandName << " command failed");
    }
}

void SystemStatusExecutor::execute()
{
    CtiCCClientListener::getInstance().BroadcastMessage(_systemStatus);
}

void DeleteItemExecutor::execute()
{
    CtiCCClientListener::getInstance().BroadcastMessage(_deleteItem);
}


/* 
    Preps the bus to run the associated Bump Test
*/
void TriggerDmvTestExecutor::execute()
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long busID = _message->getBusID();
    const std::string testName = _message->getDmvTestName();

    if ( CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( busID ) )
    {
        IVVCStrategy * ivvcStrat = dynamic_cast<IVVCStrategy *>( bus->getStrategy().get() );

        CTILOG_INFO( dout, "Preparing to execute DMV Test: '" << testName << "' on Substation Bus: " << bus->getPaoName() );

        if( ! ivvcStrat )
        {
            CTILOG_DEBUG( dout, "Unable to execute DMV Test: '" << testName << "' - Substation Bus: " << bus->getPaoName() << " does not have an IVVC Strategy assigned." );
            return;
        }

        static const string sql = "SELECT "
                                  "    DmvTestId AS TestId, "
                                  "    DmvTestName AS TestName, "
                                  "    DataArchivingInterval, "
                                  "    IntervalDataGatheringDuration, "
                                  "    StepSize, "
                                  "    CommSuccessPercentage "
                                  "FROM DmvTest "
                                  "WHERE DmvTestName = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr( connection, sql );

        rdr << testName;

        rdr.execute();

        std::unique_ptr<DmvTestData> testData;
        
        if( ! rdr() ) 
        {
            CTILOG_DEBUG(dout, "Unable to execute DMV Test: '" << testName << "' - Test parameters could not be found in the database.");
            return;
        }

        testData = std::make_unique<DmvTestData>();

        testData->TestId                        = rdr["TestId"].as<long>();
        testData->TestName                      = rdr["TestName"].as<string>();
        testData->DataArchivingInterval         = rdr["DataArchivingInterval"].as<long>();
        testData->IntervalDataGatheringDuration = rdr["IntervalDataGatheringDuration"].as<long>();
        testData->StepSize                      = rdr["StepSize"].as<double>();
        testData->CommSuccessPercentage         = rdr["CommSuccessPercentage"].as<long>();

        bool testRunning = ! ivvcStrat->setDmvTestExecution( busID, std::move( testData ) );

        if( testRunning )
        {
            CTILOG_ERROR( dout, "Could not schedule DMV Test on Substation Bus: " << bus->getPaoName() << ".  A DMV Test is already being executed." );
        }
    }
    else
    {
        if ( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG( dout, "Unable to execute DMV Test: '" << testName << "' - Substation Bus with ID: " << busID << " was not found." );
        }
    }
}

