#include "precompiled.h"

#include "ExecVerification.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"
#include "cccapbank.h"
#include "ccsubstationbus.h"
#include "ccarea.h"
#include "ccfeeder.h"
#include "ccutil.h"
#include "ExecutorFactory.h"
#include "IVVCStrategy.h"

extern unsigned long _CC_DEBUG;

using std::endl;
using std::string;
using Cti::CapControl::CapControlType;
using Cti::CapControl::ConvertIntToVerificationStrategy;
using Cti::CapControl::EventLogEntry;
using Cti::CapControl::formatMapInfo;

VerificationExecutor::VerificationExecutor(VerifyBanks* command)
{
    _deviceId = command->getItemId();
    _verifyType = command->getCommandId();
    _disableOvUv = command->getDisableOvUv();
    _userName = command->getUser();
    _bankId = 0;
}

VerificationExecutor::VerificationExecutor(VerifyInactiveBanks* command)
{
    _deviceId = command->getItemId();
    _verifyType = command->getCommandId();
    _disableOvUv = command->getDisableOvUv();
    _userName = command->getUser();
    _inactiveTime = command->getBankInactiveTime();
    _bankId = 0;
}

VerificationExecutor::VerificationExecutor(VerifySelectedBank* command)
{
    _deviceId = command->getItemId();
    _verifyType = command->getCommandId();
    _disableOvUv = command->getDisableOvUv();
    _userName = command->getUser();
    _bankId = command->getBankId();
}

void VerificationExecutor::execute()
{
    VerificationAction action = convertVerificationCommand();

    switch (action)
    {
        case VERIFY_START:
            startVerification();
            break;

        case VERIFY_STOP:
            stopVerification();
            break;

        case VERIFY_FORCE_STOP:
            //true force substation bus verification to stop immediately
            stopVerification(true);
            break;
    }
}

VerificationExecutor::VerificationAction VerificationExecutor::convertVerificationCommand()
{
    VerificationAction ret;

    switch (_verifyType)
    {
        case CapControlCommand::VERIFY_ALL_BANK:
        case CapControlCommand::VERIFY_FAILED_QUESTIONABLE_BANK:
        case CapControlCommand::VERIFY_FAILED_BANK:
        case CapControlCommand::VERIFY_QUESTIONABLE_BANK:
        case CapControlCommand::VERIFY_INACTIVE_BANKS:
        case CapControlCommand::VERIFY_SELECTED_BANK:
        case CapControlCommand::VERIFY_STAND_ALONE_BANK:
            ret = VERIFY_START;
            break;
        case CapControlCommand::EMERGENCY_STOP_VERIFICATION:
            ret = VERIFY_FORCE_STOP;
            break;
        case CapControlCommand::STOP_VERIFICATION:
        default:
            ret = VERIFY_STOP;
            break;
    }

    return ret;
}

void VerificationExecutor::startVerification()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    long busID = _deviceId;
    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(busID);

    if (currentSubstationBus == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled. SubBus not found with Id: " << busID);
        }
        return;
    }

    if (currentSubstationBus->getDisableFlag())
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled on SubBus: "
                 << currentSubstationBus->getPaoName()
                 << " due to DisableFlag.");
        }
        return;
    }

    if (currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::None)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled on SubBus: "
                 << currentSubstationBus->getPaoName()
                 << " due to Control Strategy not being set.");
        }
        return;
    }

    // We want to abort the verification if the bus has a pending or is currently running a DMV test
    //
    if ( currentSubstationBus->getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar )
    {
        IVVCStrategy * ivvc = dynamic_cast<IVVCStrategy *>( currentSubstationBus->getStrategy().get() );

        if ( ivvc && ivvc->hasPendingDmvTest( busID ) )
        {
            if ( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG( dout, "Verification Not Enabled on SubBus: " << currentSubstationBus->getPaoName()
                                    << " due to pending or running DMV test.");
            }
            return;
        }
    }

    CtiCCSubstationPtr currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
    if (currentStation == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled on SubBus: "
                << currentSubstationBus->getPaoName()
                << " due to missing parent Substation.");
        }
        return;
    }

    if (currentStation->getDisableFlag())
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled on SubBus: "
                << currentSubstationBus->getPaoName()
                << " due to Substation: "
                << currentStation->getPaoName()
                << " DisableFlag");
        }
        return;
    }

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
    if (currentArea == NULL)
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled on SubBus: "
                << currentSubstationBus->getPaoName()
                << " due to missing parent Area.");
        }
        return;
    }


    if (currentArea->getDisableFlag())
    {
        if (_CC_DEBUG & CC_DEBUG_STANDARD)
        {
            CTILOG_DEBUG(dout, "Verification Not Enabled on SubBus: "
                 << currentSubstationBus->getPaoName()
                 << " due to Area: "
                 << currentArea->getPaoName()
                 << " DisableFlag");
        }
        return;
    }


    if (currentSubstationBus->getVerificationFlag())
    {
        if (currentSubstationBus->getPerformingVerificationFlag() &&
            _verifyType < (int)currentSubstationBus->getVerificationStrategy())
        {
            currentSubstationBus->setOverlappingVerificationFlag( true );
            currentSubstationBus->setVerificationStrategy( ConvertIntToVerificationStrategy(_verifyType) );
            currentSubstationBus->setVerificationDisableOvUvFlag(_disableOvUv);
            currentSubstationBus->setCapBankInactivityTime(_inactiveTime);
            currentSubstationBus->setBusUpdatedFlag(true);
           //store->UpdateBusVerificationFlagInDB(currentSubstationBus);
            string text = currentSubstationBus->getVerificationString();
            text += " Enabled";
            string additional = "Bus: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_userName), CALLSITE);

            INT seqId = CCEventSeqIdGen();
            currentSubstationBus->setEventSequence(seqId);
            long stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));
        }
    }

    //Passed all tests, perform verification!

    //Check to see if sub is already being operated on.  Set flag, which will finish out normal control
    //before verification controls start.
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Verification Start Message received from client for SubBus: "<<currentSubstationBus->getPaoName()<<" ("<<currentSubstationBus->getPaoId()<<")");
    }

    CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
    for(long j=0;j<ccFeeders.size();j++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(long k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending)
            {
                currentSubstationBus->setWaitToFinishRegularControlFlag(true);
                string text =  "Verification will be delayed for Sub: ";
                text += currentSubstationBus->getPaoName();
                text += ", Cap Bank: ";
                text += currentCapBank->getPaoName();
                text += " is being controlled.";


              INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
                long stationId, areaId, spAreaId;
                store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));

                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Verification will be delayed until Current Cap Bank Control is completed.");
                }

                break;
            }
        }
    }

    currentSubstationBus->setVerificationFlag(true);
    currentSubstationBus->setVerificationStrategy( ConvertIntToVerificationStrategy(_verifyType));
    currentSubstationBus->setVerificationDisableOvUvFlag(_disableOvUv);
    currentSubstationBus->setCapBankInactivityTime(_inactiveTime);
    CtiCCCapBankPtr bank = store->findCapBankByPAObjectID(_bankId);
    if (bank)
    {
        bank->setSelectedForVerificationFlag(true);
    }

    currentSubstationBus->setBusUpdatedFlag(true);

    string text = currentSubstationBus->getVerificationString();
    text += " Starting";
    string additional = "Bus: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_userName), CALLSITE);

    INT seqId = CCEventSeqIdGen();
    currentSubstationBus->setEventSequence(seqId);
    long stationId, areaId, spAreaId;
    store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
    CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));

    if (currentSubstationBus->getVerificationDisableOvUvFlag())
    {
        CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::AUTO_DISABLE_OVUV, currentSubstationBus->getPaoId()))->execute();
    }

    ItemCommand* cmdMsg = new ItemCommand(CapControlCommand::DISABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId());
    cmdMsg->setUser(_userName);

    CtiCCExecutorFactory::createExecutor(cmdMsg)->execute();
}

void VerificationExecutor::stopVerification(bool forceStopImmediately)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    CapControlType type = store->determineTypeById(_deviceId);
    CtiCCSubstationBus_vec buses = store->getAllSubBusesByIdAndType(_deviceId, type);

    for each (CtiCCSubstationBus* currentSubstationBus in buses)
    {
        if (!forceStopImmediately && currentSubstationBus->getPerformingVerificationFlag())
        {
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));
                if (currentFeeder->getPerformingVerificationFlag())
                {

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(long k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        if (!currentCapBank->isPendingStatus() &&
                            (currentCapBank->getVCtrlIndex() <=0 || currentCapBank->getVCtrlIndex() >= 5) )
                        {
                            currentCapBank->setVerificationFlag(false);
                            currentCapBank->setPerformingVerificationFlag(false);
                            currentCapBank->setVerificationDoneFlag(false);
                            //wouldn't hurt to set this.
                            currentCapBank->setVCtrlIndex(0);
                        }
                    }
                }
                else
                {
                    currentFeeder->resetVerificationFlags();
                }
            }
            string text = currentSubstationBus->getVerificationString();
            text += " Stopping after currentBank";
            string additional = "Bus: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_userName), CALLSITE);

            long stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text, "cap control"));

            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "Verification Stop Message received from client.  Current Cap Bank Verification will complete first.");
            }

        }
        else
        {
            if (forceStopImmediately && currentSubstationBus->getPerformingVerificationFlag())
            {
                CTILOG_INFO(dout, "Emergency Verification Stop Message received from client. Current Cap Bank Verification will not complete.");

                CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, currentSubstationBus->getPaoId()))->execute();
            }
            currentSubstationBus->setVerificationFlag(false);
            currentSubstationBus->setPerformingVerificationFlag(false);
            currentSubstationBus->setOverlappingVerificationFlag( false );
            currentSubstationBus->setVerificationDoneFlag(false);
            currentSubstationBus->setWaitToFinishRegularControlFlag(false);

            if (currentSubstationBus->getVerificationDisableOvUvFlag())
            {
                CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::AUTO_ENABLE_OVUV, currentSubstationBus->getPaoId()))->execute();
                currentSubstationBus->setVerificationDisableOvUvFlag(false);
            }

            currentSubstationBus->setBusUpdatedFlag(true);

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));
                currentFeeder->resetVerificationFlags();
            }

            string text = currentSubstationBus->getVerificationString();
            text += " Stopping";
            string additional = "Bus: " + currentSubstationBus->getPaoName() + formatMapInfo( currentSubstationBus );

            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_userName), CALLSITE);

            long stationId, areaId, spAreaId;
            store->getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlDisableVerification, currentSubstationBus->getEventSequence(), 0, text, "cap control"));

        }
    }
}


