#include "precompiled.h"

#include <boost/assign/list_of.hpp>
#include <boost/tuple/tuple.hpp>

#include "IVVCAlgorithm.h"
#include "IVVCStrategy.h"
#include "capcontroller.h"
#include "msg_cmd.h"
#include "LitePoint.h"
#include "AttributeService.h"
#include "ccsubstationbusstore.h"
#include "PointResponse.h"
#include "Exceptions.h"
#include "ExecutorFactory.h"
#include "MsgVerifyBanks.h"
#include "amq_connection.h"
#include "IVVCAnalysisMessage.h"
#include "ccutil.h"
#include "std_helper.h"

using Cti::CapControl::PaoIdVector;
using Cti::CapControl::PointIdVector;
using Cti::CapControl::PointResponse;
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::Zone;
using Cti::CapControl::ZoneManager;
using Cti::CapControl::sendCapControlOperationMessage;
using Cti::CapControl::EventLogEntry;
using Cti::CapControl::EventLogEntries;

using namespace Cti::Messaging::CapControl;

extern unsigned long _SCAN_WAIT_EXPIRE;
extern unsigned long _POINT_AGE;
extern unsigned long _POST_CONTROL_WAIT;
extern unsigned long _IVVC_MIN_TAP_PERIOD_MINUTES;
extern unsigned long _CC_DEBUG;
extern unsigned long _IVVC_COMMS_RETRY_COUNT;
extern double _IVVC_NONWINDOW_MULTIPLIER;
extern double _IVVC_BANKS_REPORTING_RATIO;
extern double _IVVC_REGULATOR_REPORTING_RATIO;
extern double _IVVC_VOLTAGEMONITOR_REPORTING_RATIO;
extern bool _IVVC_STATIC_DELTA_VOLTAGES;
extern bool _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS;
extern unsigned long _REFUSAL_TIMEOUT;
extern unsigned long _MAX_KVAR;


IVVCAlgorithm::IVVCAlgorithm(const PointDataRequestFactoryPtr& factory)
    : _requestFactory(factory)
{
}

void IVVCAlgorithm::setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory)
{
    _requestFactory = factory;
}


bool IVVCAlgorithm::checkBusHasAtLeastOneZone(IVVCStatePtr state, CtiCCSubstationBusPtr subbus)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager = store->getZoneManager();

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    unsigned zoneCount = subbusZoneIds.size();

    if ( zoneCount == 0 )
    {
        if ( state->isShowNoZonesOnBusMsg() )
        {
            CTILOG_ERROR(dout, "IVVC Configuration Error. No Zones created on bus: " << subbus->getPaoName());
        }
    }

    return ( zoneCount > 0 );
}


bool IVVCAlgorithm::checkConfigAllZonesHaveRegulator(IVVCStatePtr state, CtiCCSubstationBusPtr subbus)
{
    unsigned missingRegulatorCount = 0;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager = store->getZoneManager();

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            long regulatorID = mapping.second;

            VoltageRegulatorManager::SharedPtr regulator;

            try
            {
                regulator = store->getVoltageRegulatorManager()->getVoltageRegulator(regulatorID);

                regulator->updateFlags( ((_IVVC_MIN_TAP_PERIOD_MINUTES * 60) / 2) );
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                ++missingRegulatorCount;

                if ( state->isShowNoRegulatorAttachedMsg() )
                {
                    CTILOG_ERROR(dout, "IVVC Configuration Error. No Voltage Regulator attached to zone: " << zone->getName());
                }
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }

    return (missingRegulatorCount == 0);
}


/*
    No manual tap configured regulators downstream of a set point configured regulator
*/
bool IVVCAlgorithm::checkZoneRegulatorsInProperConfig(IVVCStatePtr state, CtiCCSubstationBusPtr subbus)
{
    unsigned badRegulatorConfig = 0;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager = store->getZoneManager();

    const long rootZoneId = zoneManager.getRootZoneIdForSubbus( subbus->getPaoId() );

    Zone::IdSet allChildren = zoneManager.getAllChildrenOfZone( rootZoneId );

    for each ( const Zone::IdSet::value_type & ID in allChildren )
    {
        ZoneManager::SharedPtr  zone        = zoneManager.getZone( ID );
        ZoneManager::SharedPtr  parentZone  = zoneManager.getZone( zone->getParentId() );

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            const long childRegulatorID = mapping.second;

            for each ( const Zone::PhaseIdMap::value_type & parentMapping in parentZone->getRegulatorIds() )
            {
                const long parentRegulatorID = parentMapping.second;

                try
                {
                    VoltageRegulatorManager::SharedPtr  parentRegulator
                        = store->getVoltageRegulatorManager()->getVoltageRegulator( parentRegulatorID );

                    VoltageRegulatorManager::SharedPtr  childRegulator
                        = store->getVoltageRegulatorManager()->getVoltageRegulator( childRegulatorID );

                    if ( parentRegulator->getControlMode() == VoltageRegulator::SetPoint &&
                         childRegulator->getControlMode() == VoltageRegulator::ManualTap )
                    {
                        badRegulatorConfig++;

                        if ( state->showZoneRegulatorConfigMsg )
                        {
                            CTILOG_ERROR(dout, "IVVC Configuration Error. Manual Tap configured regulator: " << childRegulator->getPaoName()
                                                << " downstream from a Set Point configured regulator: " << parentRegulator->getPaoName());
                        }
                    }
                }
                catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                {
                    CTILOG_EXCEPTION_ERROR(dout, noRegulator);
                }
            }
        }
    }

    return ( badRegulatorConfig == 0 );
}


#if 0
void IVVCAlgorithm::tapOpZoneNormalization(const long parentID, const ZoneManager & zoneManager, IVVCState::TapOperationZoneMap &tapOp )
{
    Zone::IdSet allChildren = zoneManager.getAllChildrenOfZone(parentID);

    for each ( const Zone::IdSet::value_type & ID in allChildren )
    {
        ZoneManager::SharedPtr  parentZone  = zoneManager.getZone(parentID);
        ZoneManager::SharedPtr  zone        = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            Zone::PhaseIdMap    parentMapping = parentZone->getRegulatorIds();

            if ( parentZone->isGangOperated() )
            {
                tapOp[ mapping.second ] -= tapOp[ parentMapping[ Cti::CapControl::Phase_Poly ] ];
            }
            else
            {
                // normalize parent to child on the same phase

                if ( parentMapping.find( mapping.first ) != parentMapping.end() )
                {
                    tapOp[ mapping.second ] -= tapOp[ parentMapping[ mapping.first ] ];
                }
            }
        }
    }

    Zone::IdSet immediateChildren = zoneManager.getZone(parentID)->getChildIds();

    for each ( const Zone::IdSet::value_type & ID in immediateChildren )
    {
        tapOpZoneNormalization( ID, zoneManager, tapOp );   // recursion!!!
    }
}
#endif

/**
 * Checks to see if the subbus or any of the parents of the
 * subbus are disabled.
 *
 * @param state The IVVC state.
 * @param subbus The subbus object.
 *
 * @return bool - true if the subbus or any of the subbus'
 *         parents are disabled, false otherwise.
 */
bool IVVCAlgorithm::isBusInDisabledIvvcState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    long spAreaId, areaId, substationId;
    store->getSubBusParentInfo(subbus, spAreaId, areaId, substationId);

    CtiCCAreaPtr area = store->findAreaByPAObjectID(areaId);
    CtiCCSubstationPtr sub = store->findSubstationByPAObjectID(substationId);

    std::string culprit;

    if ( area != NULL && area->getDisableFlag() )
    {
        // Area is disabled.
        culprit = "area";
    }
    else if ( sub != NULL && sub->getDisableFlag() )
    {
        // Substation is disabled.
        culprit = "substation";
    }
    else if ( subbus->getDisableFlag() && !subbus->getVerificationFlag() )
    {
        // subbus is disabled - reset the algorithm and bail
        culprit = "bus";
    }

    if ( !culprit.empty() )
    {
        if ( state->isShowBusDisableMsg() )
        {
            CTILOG_INFO(dout, "IVVC Suspended for bus: " << subbus->getPaoName() << ". The " << culprit << " is disabled.");

            sendIVVCAnalysisMessage(IVVCAnalysisMessage::createSubbusDisabledMessage(subbus->getPaoId(), CtiTime::now()));
            sendDisableRemoteControl( subbus );
        }
        state->setShowBusDisableMsg(false);
        state->setState(IVVCState::IVVC_WAIT);
        return true;
    }

    return false;
}

void IVVCAlgorithm::execute(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning)
{
    CtiTime timeNow;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    long subbusId = subbus->getPaoId();

    // Make sure there is at least one zone
    if ( ! checkBusHasAtLeastOneZone( state, subbus ) )
    {
        state->setShowNoZonesOnBusMsg(false);
        state->setState(IVVCState::IVVC_WAIT);

        return;
    }

    state->setShowNoZonesOnBusMsg(true);

    // Make sure all zones on the subbus have a regulator attached.
    if ( ! checkConfigAllZonesHaveRegulator(state, subbus) )
    {
        state->setShowNoRegulatorAttachedMsg(false);
        state->setState(IVVCState::IVVC_WAIT);

        return;
    }

    state->setShowNoRegulatorAttachedMsg(true);

    // Make sure all Manual Tap configured regulators are upstream from Set Point configured regulators
    if ( ! checkZoneRegulatorsInProperConfig(state, subbus) )
    {
        state->showZoneRegulatorConfigMsg = false;
        state->setState(IVVCState::IVVC_WAIT);

        return;
    }

    state->showZoneRegulatorConfigMsg = true;

    if ( subbus->getRecentlyControlledFlag() )
    {
        state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
        if (CtiCCCapBankPtr pendingBank = subbus->getPendingCapBank())
        {
            state->setControlledBankId(pendingBank->getPaoId());
            state->setTimeStamp(subbus->getLastOperationTime());
        }
    }
    else if ( isBusInDisabledIvvcState(state, subbus) )
    {
        return;
    }

    if (!state->isShowBusDisableMsg())
    {
        sendIVVCAnalysisMessage(IVVCAnalysisMessage::createSubbusEnabledMessage(subbus->getPaoId(), timeNow));
    }
    state->setShowBusDisableMsg(true);

    // subbus is enabled
    // send regulator heartbeat messages as long as we are communicating
    if ( ! state->isCommsLost() )
    {
        sendKeepAlive(subbus);
    }

    DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();

    switch ( state->getState() )
    {
        case IVVCState::IVVC_WAIT:
        {
            // toggle these flags so the log message prints again....
            state->setShowVarCheckMsg(true);
            state->setNextControlTime(CtiTime() + strategy->getControlInterval());

            //save away start time.
            state->setTimeStamp(timeNow);

            // What points are we wanting?
            std::set<PointRequest> pointRequests;

            bool shouldScan = allowScanning && state->isScannedRequest();
            if ( ! determineWatchPoints( subbus, dispatchConnection, shouldScan, pointRequests, strategy ) )
            {
                // Configuration Error
                // Disable the bus so we don't try to run again. User Intervention required.
                subbus->setDisableFlag(true);
                subbus->setBusUpdatedFlag(true);

                CTILOG_ERROR(dout, "IVVC Configuration Error: Algorithm cannot execute. Disabling bus: " << subbus->getPaoName());

                return;
            }

            // Make GroupRequest Here
            PointDataRequestPtr request(_requestFactory->createDispatchPointDataRequest(dispatchConnection));
            request->watchPoints(pointRequests);

            //ActiveMQ message here for System Refresh
            sendCapControlOperationMessage( CapControlOperationMessage::createRefreshSystemMessage( subbus->getPaoId(), CtiTime() ) );

            //save away this request for later.
            state->setGroupRequest(request);

            if (state->isScannedRequest())
            {
                //We will be waiting for some scan responses in this request.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Scanned Group Request made in IVVC State");
                }
            }
            else
            {
                //Regular request. Dispatch will send all current values to us.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Group Request made in IVVC State");
                }
            }

            //reset this flag.
            state->setScannedRequest(false);
            //fall through to IVVC_PRESCAN_LOOP (no break)
            state->setState(IVVCState::IVVC_PRESCAN_LOOP);
        }
        case IVVCState::IVVC_PRESCAN_LOOP:
        {
            //Is it time to control? (Analysis Interval)
            if (timeNow <= state->getNextControlTime())
            {
                break;  // Not yet...
            }

            // Time to control...

            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Analysis Interval: Control attempt.");
            }

            //set next control time.
            state->setNextControlTime(timeNow + strategy->getControlInterval());

            PointDataRequestPtr request = state->getGroupRequest();

            if ( ! hasValidData( request, timeNow, subbus, strategy ) )   // invalid data
            {
                //Not starting a new scan here. There should be retrys happening already.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName()
                         << "  Analysis Interval: Invalid Data.");
                }

                state->setCommsRetryCount(state->getCommsRetryCount() + 1);
                if (state->getCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT)
                {
                    state->setState(IVVCState::IVVC_WAIT);
                    state->setCommsRetryCount(0);

                    if ( ! state->isCommsLost() )
                    {
                        state->setCommsLost(true);

                        handleCommsLost( state, subbus );

                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName() << "  Analysis Interval: Retried comms " << _IVVC_COMMS_RETRY_COUNT << " time(s). Setting Comms lost.");
                        }
                    }
                }

                updateCommsState( subbus->getCommsStatePointId(), state->isCommsLost() );
                request->reportStatusToLog();
                break;
            }
            else if ( state->isCommsLost() )    // Currently good data but previously were comms lost
            {
                CTILOG_INFO(dout, "IVVC Analysis Resuming for bus: " << subbus->getPaoName());

                state->setState(IVVCState::IVVC_WAIT);
                state->setCommsRetryCount(0);

                state->setCommsLost(false);

                long stationId, areaId, spAreaId;
                store->getSubBusParentInfo(subbus, spAreaId, areaId, stationId);

                updateCommsState( subbus->getCommsStatePointId(), state->isCommsLost() );

                // Write to the event log...
                CtiCapController::submitEventLogEntry(
                   EventLogEntry(
                        0,
                        SYS_PID_CAPCONTROL,
                        spAreaId,
                        areaId,
                        stationId,
                        subbus->getPaoId(),
                        0,
                        capControlIvvcCommStatus,
                        0,
                        1,
                        "IVVC Comms Restored",
                        "cap control") );

                break;
            }
            else if ( ! allRegulatorsInRemoteMode(subbusId) )   // At least one regulator in 'Auto' mode
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName()
                         << "  Analysis Interval: One or more Voltage Regulators are in 'Auto' mode.");
                }

                sendIVVCAnalysisMessage(IVVCAnalysisMessage::createRegulatorAutoModeMessage(subbus->getPaoId(), timeNow));

                state->setCommsRetryCount(state->getCommsRetryCount() + 1);
                if (state->getCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT)
                {
                    if ( ! state->isCommsLost() )
                    {
                        state->setCommsLost(true);

                        state->setState(IVVCState::IVVC_WAIT);
                        state->setCommsRetryCount(0);

                        handleCommsLost( state, subbus );

                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName() << "  Analysis Interval: Retried comms " << _IVVC_COMMS_RETRY_COUNT << " time(s). Setting Comms lost.");
                        }
                    }
                }

                updateCommsState( subbus->getCommsStatePointId(), state->isCommsLost() );
                request->reportStatusToLog();
                break;
            }
            else    // all good...
            {
                state->setState(IVVCState::IVVC_ANALYZE_DATA);
                state->setCommsRetryCount(0);

                updateCommsState( subbus->getCommsStatePointId(), state->isCommsLost() );

                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Analysis Interval: Data OK, moving on to Analyze.");
                }
            }
        }
        case IVVCState::IVVC_ANALYZE_DATA:
        {
            if ( busAnalysisState(state, subbus, strategy, dispatchConnection) )
            {
                break;
            }
        }
        case IVVCState::IVVC_VERIFY_CONTROL_LOOP:
        {
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

            int bankId = state->getControlledBankId();

            CtiCCCapBankPtr bank = store->getCapBankByPaoId(bankId);
            if (bank == NULL)
            {
                state->setState(IVVCState::IVVC_WAIT);

                CTILOG_WARN(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Controlled bank with ID: " << bankId << " not found.");
                break;
            }

            bool isControlled = false;

            if( bank->getControlStatus() == CtiCCCapBank::OpenPending ||
                bank->getControlStatus() == CtiCCCapBank::ClosePending ||
                bank->getPerformingVerificationFlag() )
            {
                isControlled = subbus->isAlreadyControlled();
            }
            else
            {
                state->setState(IVVCState::IVVC_WAIT);

                CTILOG_INFO(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Controlled bank: " << bank->getPaoName()  << " not in pending state.  Resetting IVVC Control loop.");

                return;
            }

            //Are we passed Max confirmtime?

            CtiTime now;
            if (isControlled || (now > ( state->getTimeStamp() + strategy->getMaxConfirmTime() )))
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  isAlreadyControlled() true.");
                }

                CtiMultiMsg_vec pointChanges;
                EventLogEntries ccEvents;

                //Update Control Status
                bool result = subbus->capBankControlStatusUpdate(pointChanges, ccEvents);
                if (subbus->getPerformingVerificationFlag())
                {
                    if( bank->isFailedStatus() )
                    {
                        state->_verification.failureCount++;
                    }
                    else
                    {
                        state->_verification.successCount++;
                    }

                    if (result)
                    {
                        setupNextBankToVerify(state, subbus, ccEvents);
                    }
                }


                sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEvents);
                subbus->setBusUpdatedFlag(true);
                state->setTimeStamp(now);

                if (isControlled && !bank->isFailedStatus())
                {
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Post control wait.");
                    }
                    state->setState(IVVCState::IVVC_POST_CONTROL_WAIT);
                }
                else
                {
                    state->setState(IVVCState::IVVC_WAIT);
                }
            }
            else
            {
                if ( state->isShowVarCheckMsg() )      // only print once each time through
                {
                    state->setShowVarCheckMsg(false);

                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Var Check failed.");
                    }
                }
                break;
            }
        }
        case IVVCState::IVVC_POST_CONTROL_WAIT:
        {
            CtiTime now;
            if (now <= (state->getTimeStamp() + _POST_CONTROL_WAIT/*seconds*/))
            {
                break;
            }

            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Post control wait. Creating Post Scan ");
            }

            PointDataRequestPtr request(_requestFactory->createDispatchPointDataRequest(dispatchConnection));

            std::set<PointRequest> pointRequests;

            if ( ! determineWatchPoints( subbus, dispatchConnection, allowScanning, pointRequests, strategy ) )
            {
                // Do we want to bail here?
            }

            request->watchPoints(pointRequests);

            state->setTimeStamp(now);
            state->setGroupRequest(request);
            state->setState(IVVCState::IVVC_POSTSCAN_LOOP);
        }
        case IVVCState::IVVC_POSTSCAN_LOOP:
        {
            PointDataRequestPtr request = state->getGroupRequest();
            CtiTime scanTime = state->getTimeStamp();
            CtiTime now;

            const bool requestIsComplete = request->isComplete();
            const bool scanHasTimedOut   = ( scanTime + ( 60 * _SCAN_WAIT_EXPIRE ) ) < now;     // _SCAN_WAIT_EXPIRE is in minutes

            if ( requestIsComplete || scanHasTimedOut )
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName()
                         << " - Post Scan " << ( requestIsComplete ? "Complete" : "Timed Out" ));
                }

                // Update the point response data with whatever data we got back from the post scan
                //  --- only if we're not using static deltas

                if ( ! _IVVC_STATIC_DELTA_VOLTAGES )
                {
                    subbus->updatePointResponseDeltas( state->getReportedControllers() );
                }
                state->setState(IVVCState::IVVC_WAIT);
            }
            break;
        }
        case IVVCState::IVVC_OPERATE_TAP:
        {
            // This state just sends TAP up/down messages

            // 5 second delay between consecutive tap ops on same regulator
            if ( ( state->_tapOpDelay + 5 ) < timeNow )
            {
                state->_tapOpDelay = timeNow;

                // Verify that the regulators and regulator attributes we need are available

                unsigned errorCount = 0;

                for each ( const IVVCState::TapOperationZoneMap::value_type & operation in state->_tapOps )
                {
                    long   regulatorId       = operation.first;
                    double voltageAdjustment = operation.second;

                    try
                    {
                        if ( voltageAdjustment != 0 )
                        {
                            VoltageRegulatorManager::SharedPtr  regulator
                                = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorId );

                            regulator->canExecuteVoltageRequest( voltageAdjustment );
                        }
                    }
                    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                    {
                        errorCount++;
                        CTILOG_EXCEPTION_ERROR(dout, noRegulator);
                    }
                    catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
                    {
                        errorCount++;
                        if (missingAttribute.complain())
                        {
                            CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                        }
                    }
                }

                // If we have some errors - clear out the tapOp mapping... (cancel pending operations)

                if ( errorCount != 0 )
                {
                    state->_tapOps.clear();

                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<" - Cancelling Tap Operations.  One or more regulators is missing, or is missing a required attribute.");
                    }
                }

                // Send the tap commands

                for each ( const IVVCState::TapOperationZoneMap::value_type & operation in state->_tapOps )
                {
                    long   regulatorId       = operation.first;
                    double voltageAdjustment = operation.second;

                    try
                    {
                        if ( voltageAdjustment != 0 )
                        {
                            VoltageRegulatorManager::SharedPtr  regulator
                                = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorId );

                            state->_tapOps[ regulatorId ] -= regulator->adjustVoltage( voltageAdjustment );

                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName()
                                                  << " - Adjusting Voltage on Regulator: " << regulator->getPaoName());
                            }

                            // jmoc -- do we need a new type of message for set point stuff?

                            sendIVVCAnalysisMessage(
                                IVVCAnalysisMessage::createTapOperationMessage( subbus->getPaoId(),
                                                                                ( voltageAdjustment > 0 )
                                                                                    ? IVVCAnalysisMessage::Scenario_TapRaiseOperation
                                                                                        : IVVCAnalysisMessage::Scenario_TapLowerOperation,
                                                                                timeNow,
                                                                                regulatorId ) );
                        }
                    }
                    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                    {
                        CTILOG_EXCEPTION_ERROR(dout, noRegulator);
                    }
                    catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
                    {
                        if (missingAttribute.complain())
                        {
                            CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                        }
                    }
                }

                if ( ! hasTapOpsRemaining(state->_tapOps) ) // are we done yet?
                {
                    state->setLastTapOpTime( timeNow );
                    state->setState(IVVCState::IVVC_WAIT);
                }
            }
            break;
        }
        default:
        {
            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  default. ");
            }
        }
    }//switch
}//execute


//sendScan must be false for unit tests.
bool IVVCAlgorithm::determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<PointRequest>& pointRequests, IVVCStrategy* strategy)
{
    bool configurationError = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();

    // Process each zones regulator, CBCs and extra voltage points

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        // Regulator(s)

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr  regulator
                    = store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                long voltagePointId = regulator->getPointByAttribute(PointAttribute::VoltageY).getPointId();

                pointRequests.insert( PointRequest(voltagePointId, RegulatorRequestType, ! sendScan) );

                if ( sendScan )
                {
                    regulator->executeIntegrityScan();
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                configurationError = true;

                if ( ! subbus->getDisableFlag() )
                {
                    CTILOG_EXCEPTION_ERROR(dout, noRegulator);
                }
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                configurationError = true;

                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }

        // 2-way CBCs

        Zone::IdSet capbankIds = zone->getBankIds();

        for each ( const Zone::IdSet::value_type & ID in capbankIds )
        {
            CtiCCCapBankPtr bank    = store->findCapBankByPAObjectID(ID);

            for each ( CtiCCMonitorPointPtr point in bank->getMonitorPoint() )
            {
                if ( point->getPointId() > 0 )
                {
                    pointRequests.insert( PointRequest(point->getPointId(), CbcRequestType, ! sendScan) );
                }
            }
            if ( sendScan )
            {
                CtiCCExecutorFactory::createExecutor( new ItemCommand( CapControlCommand::SEND_SCAN_2WAY_DEVICE,
                                                                        bank->getControlDeviceId() ) )->execute();
            }
        }

        // Additional voltage points

        for each ( const Zone::PhaseToVoltagePointIds::value_type & mapping in zone->getPointIds() )
        {
            pointRequests.insert( PointRequest(mapping.second, OtherRequestType) );
        }
    }

    // Bus and feeder voltage points are now attached to the zones and were loaded then.
    // We still need the bus watt and var points.

    long busWattPointId = subbus->getCurrentWattLoadPointId();
    PointIdVector busVarPointIds = subbus->getCurrentVarLoadPoints();

    if (busWattPointId > 0)
    {
        pointRequests.insert( PointRequest(busWattPointId, BusPowerRequestType) );
    }
    else
    {
        configurationError = true;

        if (!subbus->getDisableFlag())
        {
            CTILOG_ERROR(dout, "IVVC Configuration Error: Missing Watt Point on Bus: " << subbus->getPaoName());
        }
    }

    for each (long ID in busVarPointIds)
    {
        if (ID > 0)
        {
            pointRequests.insert( PointRequest(ID, BusPowerRequestType) );
        }
        else
        {
            configurationError = true;

            if (!subbus->getDisableFlag())
            {
                CTILOG_ERROR(dout, "IVVC Configuration Error: Missing Var Point on Bus: " << subbus->getPaoName());
            }
        }
    }

    // We need the watt and var points for each feeder on the bus
    //  -- iff control method is bus optimized

    if ( strategy->getMethodType() == ControlStrategy::BusOptimizedFeeder )
    {
        for each ( CtiCCFeederPtr feeder in subbus->getCCFeeders() )
        {
            // watt point
            long wattPoint = feeder->getCurrentWattLoadPointId();

            if (wattPoint > 0)
            {
                pointRequests.insert( PointRequest(wattPoint, BusPowerRequestType) );
            }
            else
            {
                configurationError = true;

                if (!subbus->getDisableFlag())
                {
                    CTILOG_ERROR(dout, "IVVC Configuration Error: Missing Watt Point on Feeder: " << feeder->getPaoName());
                }
            }

            // var point(s)
            for each ( long varPoint in feeder->getCurrentVarLoadPoints() )
            {
                if (varPoint > 0)
                {
                    pointRequests.insert( PointRequest(varPoint, BusPowerRequestType) );
                }
                else
                {
                    configurationError = true;

                    if (!subbus->getDisableFlag())
                    {
                        CTILOG_ERROR(dout, "IVVC Configuration Error: Missing Var Point on Feeder: " << feeder->getPaoName());
                    }
                }
            }
        }
    }

    return ( ! configurationError );
}


void IVVCAlgorithm::operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection, IVVCStrategy* strategy)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiCCCapBankPtr bank = store->getCapBankByPaoId(bankId);
    if (bank != NULL)
    {
        long feederId = bank->getParentId();
        CtiCCFeederPtr feeder = store->findFeederByPAObjectID(feederId);
        if (feeder != NULL)
        {
            bool isCapBankOpen = (bank->getControlStatus() == CtiCCCapBank::Open ||
                                  bank->getControlStatus() == CtiCCCapBank::OpenQuestionable);

            bool isCapBankClosed = (bank->getControlStatus() == CtiCCCapBank::Close ||
                                    bank->getControlStatus() == CtiCCCapBank::CloseQuestionable);

            CtiMultiMsg_vec pointChanges;
            EventLogEntries ccEvents;

            double beforeKvar = subbus->getCurrentVarLoadPointValue();
            double varValueA = subbus->getPhaseAValue();
            double varValueB = subbus->getPhaseBValue();
            double varValueC = subbus->getPhaseCValue();

            if ( strategy->getMethodType() == ControlStrategy::BusOptimizedFeeder )
            {
                // Use feeder values instead of bus values for call to create(In/De)creaseVarRequest()

                beforeKvar = feeder->getCurrentVarLoadPointValue();
                varValueA  = feeder->getPhaseAValue();
                varValueB  = feeder->getPhaseBValue();
                varValueC  = feeder->getPhaseCValue();
            }

            CtiRequestMsg* request = NULL;

            if (isCapBankOpen)
            {
                std::string text = feeder->createTextString(ControlStrategy::IntegratedVoltVarControlUnit,CtiCCCapBank::Close,feeder->getIVControl(),beforeKvar);
                request = feeder->createDecreaseVarRequest(bank,pointChanges,ccEvents,text,beforeKvar,varValueA,varValueB,varValueC);

            }
            else if (isCapBankClosed)
            {
                std::string text = feeder->createTextString(ControlStrategy::IntegratedVoltVarControlUnit,CtiCCCapBank::Open,feeder->getIVControl(),beforeKvar);
                request = feeder->createIncreaseVarRequest(bank,pointChanges,ccEvents,text,beforeKvar,varValueA,varValueB,varValueC);
            }
            else
            {
                //Check for a retry
            }

            CtiTime timestamp;

            if (request != NULL)
            {
                CtiTime time = request->getMessageTime();
                CtiCapController::getInstance()->getPorterConnection()->WriteConnQue(request);

                sendIVVCAnalysisMessage(
                    IVVCAnalysisMessage::createCapbankOperationMessage( subbus->getPaoId(),
                                                                        isCapBankOpen
                                                                            ? IVVCAnalysisMessage::Scenario_CapbankCloseOperation
                                                                            : IVVCAnalysisMessage::Scenario_CapbankOpenOperation,
                                                                        timestamp,
                                                                        bankId ) );

                subbus->setLastOperationTime(time);
                subbus->setLastFeederControlled(feeder->getPaoId());
                subbus->setCurrentDailyOperationsAndSendMsg(subbus->getCurrentDailyOperations() + 1, pointChanges);
                subbus->figureEstimatedVarLoadPointValue();
                subbus->setBusUpdatedFlag(true);
                subbus->setVarValueBeforeControl(beforeKvar);
                subbus->setRecentlyControlledFlag(true);
                feeder->setLastOperationTime(time);

                if( subbus->getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(subbus->getEstimatedVarLoadPointId(),subbus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }

                sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEvents);
            }
            else    // if request is NULL we returned early from create(In|De)creaseVarRequest( ... ) - this only happens if we exceed KVar
            {
                sendIVVCAnalysisMessage( IVVCAnalysisMessage::createExceedMaxKVarMessage( subbus->getPaoId(),
                                                                                          timestamp,
                                                                                          bankId,
                                                                                          _MAX_KVAR ) );
            }
        }
    }
}


void IVVCAlgorithm::sendKeepAlive(CtiCCSubstationBusPtr subbus)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    // need to send each phase seperated by a small delay...
    // first send poly phase and phase A
    // wait 1 second, send B, wait 1 s, send C

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                if ( mapping.first == Cti::CapControl::Phase_Poly || mapping.first == Cti::CapControl::Phase_A )
                {
                    VoltageRegulatorManager::SharedPtr regulator =
                            store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                    if ( regulator->executePeriodicKeepAlive() )
                    {
                        if( _CC_DEBUG & CC_DEBUG_IVVC )
                        {
                            CTILOG_DEBUG(dout, "IVVC Algorithm: Voltage Regulator Keep Alive messages sent on bus: "
                                              << subbus->getPaoName());
                        }
                    }
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }

    Sleep(1000);    // wait 1 second between phases

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                if ( mapping.first == Cti::CapControl::Phase_B )
                {
                    VoltageRegulatorManager::SharedPtr regulator =
                            store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                    if ( regulator->executePeriodicKeepAlive() )
                    {
                        if( _CC_DEBUG & CC_DEBUG_IVVC )
                        {
                            CTILOG_DEBUG(dout, "IVVC Algorithm: Voltage Regulator Keep Alive messages sent on bus: "
                                              << subbus->getPaoName());
                        }
                    }
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }

    Sleep(1000);    // wait 1 second between phases

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                if ( mapping.first == Cti::CapControl::Phase_C )
                {
                    VoltageRegulatorManager::SharedPtr regulator =
                            store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                    if ( regulator->executePeriodicKeepAlive() )
                    {
                        if( _CC_DEBUG & CC_DEBUG_IVVC )
                        {
                            CTILOG_DEBUG(dout, "IVVC Algorithm: Voltage Regulator Keep Alive messages sent on bus: "
                                              << subbus->getPaoName());
                        }
                    }
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }
}


void IVVCAlgorithm::sendPointChanges(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges)
{
    if ( pointChanges.size() > 0 )
    {
        CtiMultiMsg* pointChangeMsg = new CtiMultiMsg();
        pointChangeMsg->setData(pointChanges);
        dispatchConnection->WriteConnQue(pointChangeMsg);
    }
}

void IVVCAlgorithm::sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, const EventLogEntries &ccEvents)
{
    CtiCapController::submitEventLogEntries(ccEvents);
    sendPointChanges(dispatchConnection,pointChanges);
}


bool IVVCAlgorithm::busVerificationAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection)
{
    if (!subbus->getVerificationFlag())
    {
        return false;
    }

    CtiTime now;
    state->setTimeStamp(now);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiMultiMsg_vec pointChanges;
    CtiMultiMsg* pilMessages = new CtiMultiMsg();
    EventLogEntries ccEvents;
    CtiMultiMsg_vec &pilMsg = pilMessages->getData();
    if (!subbus->getPerformingVerificationFlag())
    {
        subbus->setLastVerificationCheck(now);

        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
           CTILOG_DEBUG(dout, "IVVC Algorithm: "<< subbus->getPaoName() <<" Performing Verification.");
        }
        subbus->setCapBanksToVerifyFlags((long)subbus->getVerificationStrategy(), ccEvents);
        setupNextBankToVerify(state, subbus, ccEvents);
    }
    PointValueMap pointValues = state->getGroupRequest()->getPointValues();
    pointValues.erase(subbus->getCurrentWattLoadPointId());
    PointIdVector pointIds = subbus->getCurrentVarLoadPoints();
    for each (long pointId in pointIds)
    {
        pointValues.erase(pointId);
    }//At this point we have removed the var and watt points. Only volt points remain.

    CtiCCCapBankPtr currentBank = store->getCapBankByPaoId(subbus->getCurrentVerificationCapBankId());;


    state->_estimated[currentBank->getPaoId()].capbank = currentBank;
    // record preoperation voltage values for the feeder our capbank is on
    for each (PointValueMap::value_type pointValuePair in pointValues)
    {
        try
        {
            state->_estimated[currentBank->getPaoId()].capbank->updatePointResponsePreOpValue(pointValuePair.first,pointValuePair.second.value);
        }
        catch (NotFoundException& e)
        {
            CTILOG_ERROR(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Error Updating PreOpValue for deltas. PointId not found: " << pointValuePair.first);
        }
    }

    state->_estimated[currentBank->getPaoId()].operated = true;
    state->setControlledBankId(currentBank->getPaoId());

    state->_estimated.clear();     // done with this data

    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Operating Capbank: " << currentBank->getPaoName());
    }

    if (state->_verification.successCount == 0 && state->_verification.failureCount == 0)
    {
        subbus->startVerificationOnCapBank(now, pointChanges, ccEvents, pilMsg);
    }
    else
    {
        if (subbus->sendNextCapBankVerificationControl(now, pointChanges, ccEvents, pilMsg))
        {
            subbus->setWaitForReCloseDelayFlag(false);
        }
        else
        {
            subbus->setWaitForReCloseDelayFlag(true);
        }
    }
    if (pilMsg.size() > 0)
    {
        CtiCapController::getInstance()->getPorterConnection()->WriteConnQue(pilMessages);
        sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEvents);
    }

    state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);

    return true;

}

void IVVCAlgorithm::setupNextBankToVerify(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, EventLogEntries &ccEvents)
{
    if(subbus->areThereMoreCapBanksToVerify(ccEvents))
    {
        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
                CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<" Performing Verification on CapBankId: "<< subbus->getCurrentVerificationCapBankId());
        }

        state->_verification = IVVCState::VerificationHelper(subbus->getCurrentVerificationCapBankId());
    }
    else
    {
        //reset VerificationFlag
        subbus->setVerificationFlag(false);
        subbus->setBusUpdatedFlag(true);
        CtiCCExecutorFactory::createExecutor(new VerifyBanks(subbus->getPaoId(),subbus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION))->execute();
        CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::ENABLE_SUBSTATION_BUS, subbus->getPaoId()))->execute();
        if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
        {
           CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() << " has completed Verification.");
        }
    }
    return;
}

/**
 * @return bool :   true    - break out of the state machine.
 *                  false   - fall through to the next case.
 */
bool IVVCAlgorithm::busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection)
{
    if ( busVerificationAnalysisState(state, subbus, strategy, dispatchConnection) )
    {
        return true;
    }

    bool isPeakTime = subbus->getPeakTimeFlag();    // Is it peak time according to the bus.

    PointValueMap pointValues = state->getGroupRequest()->getPointValues();

    //calculate current power factor of the bus

    WattVArValues               wattVarData;
    std::vector<WattVArValues>  powerFactorData;    // index 0 is the bus - 1 to N are N feeders

    wattVarData.paoId     = subbus->getPaoId();
    wattVarData.wattValue = 0.0;
    wattVarData.varValue  = 0.0;

    // Can't get here if these IDs don't exist
    long    wattPointID = subbus->getCurrentWattLoadPointId();

    PointValueMap::iterator iter = pointValues.find(wattPointID);

    if ( iter != pointValues.end() )
    {
        wattVarData.wattValue = iter->second.value;
        pointValues.erase(wattPointID);
    }
    else    // this should never happen but if it does -- reset the state machine and bail out.
    {
        CTILOG_ERROR(dout, "IVVC: " << subbus->getPaoName()
                 << " - Missing Watt point response.  Aborting analysis.");

        state->setState(IVVCState::IVVC_WAIT);
        state->setCommsRetryCount(0);

        return true;
    }

    for each (long pointId in subbus->getCurrentVarLoadPoints() )
    {
        iter = pointValues.find(pointId);

        if ( iter != pointValues.end() )
        {
            wattVarData.varValue += iter->second.value;
            pointValues.erase(pointId);
        }
        else    // this should never happen but if it does -- reset the state machine and bail out.
        {
            CTILOG_ERROR(dout, "IVVC: " << subbus->getPaoName()
                     << " - Missing Var point response.  Aborting analysis.");

            state->setState(IVVCState::IVVC_WAIT);
            state->setCommsRetryCount(0);

            return true;
        }
    }

    powerFactorData.push_back( wattVarData );   // index 0  ---  subbus watt and var data

    // consider feeder watt and var point iff control method is bus optimized

    if ( strategy->getMethodType() == ControlStrategy::BusOptimizedFeeder )
    {
        for each ( CtiCCFeederPtr feeder in subbus->getCCFeeders() )
        {
            wattVarData.paoId     = feeder->getPaoId();
            wattVarData.wattValue = 0.0;
            wattVarData.varValue  = 0.0;

            // watt point
            long wattPoint = feeder->getCurrentWattLoadPointId();

            PointValueMap::iterator iter = pointValues.find(wattPoint);

            if ( iter != pointValues.end() )
            {
                wattVarData.wattValue = iter->second.value;
                pointValues.erase(wattPoint);
            }
            else    // this should never happen but if it does -- reset the state machine and bail out.
            {
                CTILOG_ERROR(dout, "IVVC: " << feeder->getPaoName()
                         << " - Missing Watt point response.  Aborting analysis.");

                state->setState(IVVCState::IVVC_WAIT);
                state->setCommsRetryCount(0);

                return true;
            }

            // var point(s)
            for each ( long varPoint in feeder->getCurrentVarLoadPoints() )
            {
                iter = pointValues.find(varPoint);

                if ( iter != pointValues.end() )
                {
                    wattVarData.varValue += iter->second.value;
                    pointValues.erase(varPoint);
                }
                else    // this should never happen but if it does -- reset the state machine and bail out.
                {
                    CTILOG_ERROR(dout, "IVVC: " << feeder->getPaoName()
                         << " - Missing Var point response.  Aborting analysis.");

                    state->setState(IVVCState::IVVC_WAIT);
                    state->setCommsRetryCount(0);

                    return true;
                }
            }

            powerFactorData.push_back( wattVarData );   // index 1 - N  ---  feeder watt and var data
        }
    }

    // At this point we have removed the var and watt points. Only volt points remain.

    // report voltages
    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        Cti::StreamBuffer s;

        s << "IVVC Algorithm: " << subbus->getPaoName() << " - Current measurement set.";

        Cti::FormattedList list;

        list << "Voltages [ Point ID : Value ]";
        for ( PointValueMap::const_iterator b = pointValues.begin(), e = pointValues.end(); b != e; ++b )
        {
            list.add(CtiNumStr(b->first)) << b->second.value;
        }

        CTILOG_DEBUG(dout, s << list);
    }

    // report bus stuff

    double targetPowerFactorVars = calculateTargetPFVars( strategy->getTargetPF(isPeakTime), powerFactorData[0].wattValue );
    double Vf = calculateVf( pointValues );
    double violationCost = calculateVoltageViolation( pointValues, strategy, isPeakTime );
    double PFBus = Cti::CapControl::calculatePowerFactor( powerFactorData[0].varValue, powerFactorData[0].wattValue );

    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        Cti::FormattedList list;

        list.add("Target PF VARs")       << targetPowerFactorVars;
        list.add("Subbus Flatness")      << Vf;
        list.add("Subbus ViolationCost") << violationCost;
        list.add("Subbus Watts")         << powerFactorData[0].wattValue;
        list.add("Subbus VARs")          << powerFactorData[0].varValue;
        list.add("Subbus Power Factor")  << PFBus;

        CTILOG_DEBUG(dout, list);
    }

    //  bus weight == ( voltage weight * flatness ) + voltage violation cost + .......

    double currentBusWeight = ( strategy->getVoltWeight(isPeakTime) * Vf ) + violationCost;

    // add in bus power factor component

    double pfCalc = strategy->getPFWeight( isPeakTime ) * calculatePowerFactorCost( PFBus, strategy, isPeakTime );

    double powerFactorComponentMultiTap = pfCalc;

    currentBusWeight += pfCalc;

    // consider feeder watt and var point iff control method is bus optimized

    if ( strategy->getMethodType() == ControlStrategy::BusOptimizedFeeder )
    {
        // report feeder stuff

        for ( int i = 1 ; i < powerFactorData.size() ; i++ )
        {
            double PFFeeder = Cti::CapControl::calculatePowerFactor( powerFactorData[i].varValue, powerFactorData[i].wattValue );

            // add in each feeder power factor component

            pfCalc = feederPFCorrectionCalculator( PFFeeder, strategy, isPeakTime )
                        * calculatePowerFactorCost( PFFeeder, strategy, isPeakTime );

            powerFactorComponentMultiTap += pfCalc;
            currentBusWeight += pfCalc;

            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                Cti::FormattedList list;

                list.add("Feeder Watts")        << powerFactorData[i].wattValue;
                list.add("Feeder VARs")         << powerFactorData[i].varValue;
                list.add("Feeder Power Factor") << PFFeeder;

                CTILOG_DEBUG(dout, list);
            }
        }
    }

    // Log our current bus weight

    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        Cti::FormattedList list;

        list.add("Subbus Weight") << currentBusWeight;

        CTILOG_DEBUG(dout, list);
    }

    //calculate estimated bus weights etc from historical data

    CtiFeeder_vec& feeders = subbus->getCCFeeders();

    // for each feeder
    std::set<long> reportedIds;

    for ( CtiFeeder_vec::iterator fb = feeders.begin(), fe = feeders.end(); fb != fe; ++fb )
    {
        CtiCCFeederPtr currentFeeder = *fb;
        CtiCCCapBank_SVector& capbanks =  currentFeeder->getCCCapBanks();


        // for each capbank
        for ( CtiCCCapBank_SVector::iterator cb = capbanks.begin(), ce = capbanks.end(); cb != ce; ++cb )
        {
            CtiCCCapBankPtr currentBank = *cb;

            PointValueMap deltas(pointValues);  // copy our pointValues map

            bool isCapBankOpen = (currentBank->getControlStatus() == CtiCCCapBank::Open ||
                                  currentBank->getControlStatus() == CtiCCCapBank::OpenQuestionable);

            bool isCapBankClosed = (currentBank->getControlStatus() == CtiCCCapBank::Close ||
                                    currentBank->getControlStatus() == CtiCCCapBank::CloseQuestionable);

            if ( currentBank->getIgnoreFlag() &&
                 CtiTime::now() > CtiTime( currentBank->getLastStatusChangeTime().seconds() + (_REFUSAL_TIMEOUT * 60) ) )
            {
                currentBank->setIgnoreFlag(false);
            }

            // if banks operational state isn't switched or if disabled
            // or not in one of the above 4 states we aren't eligible for control.

            if ( ciStringEqual(currentBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                 !currentBank->getLocalControlFlag() &&
                 !currentBank->getDisableFlag() &&
                 !currentBank->getIgnoreFlag() &&
                 (isCapBankOpen || isCapBankClosed) &&
                 (deltas.find(currentBank->getPointIdByAttribute(PointAttribute::CbcVoltage)) != deltas.end()))
            {
                std::vector<PointResponse> responses = subbus->getPointResponsesForDevice( currentBank->getPaoId() );

                for each (PointResponse currentResponse in responses)
                {
                    if (deltas.find(currentResponse.getPointId()) != deltas.end())
                    {
                        reportedIds.insert(currentResponse.getPointId());
                        deltas[ currentResponse.getPointId() ].value += ( ( isCapBankOpen ? 1.0 : -1.0 ) * currentResponse.getDelta() );
                    }
                }

                state->_estimated[currentBank->getPaoId()].capbank = currentBank;

                // Log our estimated voltage changes if bank is operated.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    Cti::StreamBuffer s;

                    s << "IVVC Algorithm: "<<subbus->getPaoName() <<"  Estimated voltages if Capbank ID# "
                         << currentBank->getPaoId() << " is " << (isCapBankOpen ? "CLOSED" : "OPENED");

                    Cti::FormattedList list;

                    list << "Estimated Voltages [ Point ID : Estimated Value ]";
                    for each (PointResponse currentResponse in responses)
                    {
                        if (deltas.find(currentResponse.getPointId()) != deltas.end())
                        {
                            list.add(CtiNumStr(currentResponse.getPointId())) << deltas[ currentResponse.getPointId() ].value;
                        }
                    }

                    CTILOG_DEBUG(dout, s << list);
                }

                // report estimated bus stuff

                // calculate estimated flatness and violation costs
                state->_estimated[currentBank->getPaoId()].flatness = calculateVf(deltas);
                state->_estimated[currentBank->getPaoId()].voltViolation = calculateVoltageViolation( deltas, strategy, isPeakTime );

                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    Cti::FormattedList list;

                    list.add("Estimated Subbus Flatness")      << state->_estimated[currentBank->getPaoId()].flatness;
                    list.add("Estimated Subbus ViolationCost") << state->_estimated[currentBank->getPaoId()].voltViolation;

                    CTILOG_DEBUG(dout, list);
                }

                //calculate the VAR target window
                double varLowLimit   = targetPowerFactorVars - (currentBank->getBankSize() * (strategy->getMinBankOpen(isPeakTime) / 100.0));
                double varUpperLimit = targetPowerFactorVars + (currentBank->getBankSize() * (strategy->getMinBankClose(isPeakTime) / 100.0));

                //calculate estimated power factor of the bus if current bank switches state
                double estVarValue = powerFactorData[0].varValue;
                double pfmodifier  = 1.0;

                if ( isCapBankOpen )
                {
                   estVarValue -= currentBank->getBankSize();     // reduce the estmated vars....

                   if ( estVarValue < varLowLimit)
                   {
                      pfmodifier = _IVVC_NONWINDOW_MULTIPLIER;
                   }
                }
                else
                {
                   estVarValue += currentBank->getBankSize();     // increase the estmated vars....

                   if ( estVarValue > varUpperLimit)
                   {
                      pfmodifier = _IVVC_NONWINDOW_MULTIPLIER;
                   }
                }

                state->_estimated[currentBank->getPaoId()].powerFactor = Cti::CapControl::calculatePowerFactor(estVarValue, powerFactorData[0].wattValue);

                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    Cti::FormattedList list;

                    list.add("Estimated Subbus VARs")         << estVarValue;
                    list.add("Estimated Subbus Power Factor") << state->_estimated[currentBank->getPaoId()].powerFactor;

                    CTILOG_DEBUG(dout, list);
                }

                //  bus weight == ( voltage weight * flatness ) + voltage violation cost + .......

                state->_estimated[currentBank->getPaoId()].busWeight =
                    ( strategy->getVoltWeight(isPeakTime) * state->_estimated[currentBank->getPaoId()].flatness )
                        + state->_estimated[currentBank->getPaoId()].voltViolation;

                // add in bus power factor component

                state->_estimated[currentBank->getPaoId()].busWeight +=
                    pfmodifier * strategy->getPFWeight(isPeakTime)
                        * calculatePowerFactorCost( state->_estimated[currentBank->getPaoId()].powerFactor, strategy, isPeakTime );

                // consider feeder watt and var point iff control method is bus optimized

                if ( strategy->getMethodType() == ControlStrategy::BusOptimizedFeeder )
                {
                    // report feeder stuff

                    for ( int i = 1 ; i < powerFactorData.size() ; i++ )
                    {
                        CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

                        // is the current bank on the _currentVectorOfStuff[i] feeder ?

                        double feederVarValue = powerFactorData[i].varValue;

                        if ( powerFactorData[i].paoId == store->findFeederIDbyCapBankID( currentBank->getPaoId() ) )
                        {
                            feederVarValue += isCapBankOpen ? -currentBank->getBankSize() : currentBank->getBankSize();
                        }

                        double PFFeeder = Cti::CapControl::calculatePowerFactor( feederVarValue, powerFactorData[i].wattValue );

                        // add in each feeder power factor component

                        state->_estimated[currentBank->getPaoId()].busWeight +=
                            pfmodifier * feederPFCorrectionCalculator( PFFeeder, strategy, isPeakTime )
                                * calculatePowerFactorCost( PFFeeder, strategy, isPeakTime );

                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            Cti::FormattedList list;

                            list.add("Estimated Feeder VARs")         << feederVarValue;
                            list.add("Estimated Feeder Power Factor") << PFFeeder;

                            CTILOG_DEBUG(dout, list);
                        }
                    }
                }

                // Log our estimated calculations
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    Cti::FormattedList list;

                    list.add("Estimated Subbus Weight") << state->_estimated[currentBank->getPaoId()].busWeight;

                    CTILOG_DEBUG(dout, list);
                }
            }
        }
    }

    // check for any potential multi-tap operation potential

    const bool canDoMultiTap = checkForMultiTapOperation( pointValues, subbus->getAllMonitorPoints(), strategy, isPeakTime );

    double multiTapEstBw = powerFactorComponentMultiTap;

    if ( canDoMultiTap )
    {
        PointValueMap multiTapVoltages( pointValues );      // copy data

        state->_tapOps.clear();

        // we now have a tap solution for our over-voltage

        calculateMultiTapOperation( multiTapVoltages, subbus, strategy, state->_tapOps );

        // calculate the bus weight

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            Cti::StreamBuffer s;

            s << "IVVC Algorithm: " << subbus->getPaoName() << " - Estimated voltages for Multi-Tap solution.";

            Cti::FormattedList list;

            list << "Estimated Voltages [ Point ID : Estimated Value ]";
            for ( PointValueMap::const_iterator b = multiTapVoltages.begin(), e = multiTapVoltages.end(); b != e; ++b )
            {
                list.add(CtiNumStr(b->first)) << b->second.value;
            }
            list << "Tap Operations [ Regulator ID : Operation ]";
            for ( IVVCState::TapOperationZoneMap::const_iterator b = state->_tapOps.begin(), e = state->_tapOps.end(); b != e; ++b )
            {
                list.add(CtiNumStr(b->first)) << b->second;
            }
            CTILOG_DEBUG(dout, s << list);
        }

        double mt_Vf = calculateVf( multiTapVoltages );
        double mt_violationCost = calculateVoltageViolation( multiTapVoltages, strategy, isPeakTime );

        multiTapEstBw += ( strategy->getVoltWeight(isPeakTime) * mt_Vf ) + mt_violationCost;

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            Cti::FormattedList list;

            list.add("Estimated Subbus Flatness")      << mt_Vf;
            list.add("Estimated Subbus ViolationCost") << mt_violationCost;
            list.add("Estimated Subbus Weight")        << multiTapEstBw;

            CTILOG_DEBUG(dout, list);
        }
    }

    //Store away the ids that responded for the POST SCAN LOOP state to use when it records deltas
    state->setReportedControllers(reportedIds);

    // Search for the minimum bus weight

    long    operatePaoId = -1;
    double  minimumEstBw = std::numeric_limits<double>::max();

    for ( IVVCState::EstimatedDataMap::iterator eb = state->_estimated.begin(), ee = state->_estimated.end(); eb != ee; ++eb )
    {
        if ( eb->second.busWeight <= minimumEstBw )
        {
            minimumEstBw = eb->second.busWeight;
            operatePaoId = eb->first;
        }
    }

    if ( canDoMultiTap
         && ( ( currentBusWeight - strategy->getDecisionWeight(isPeakTime) ) > multiTapEstBw )
         && ( minimumEstBw > multiTapEstBw ) )
    {
        // if there are tap ops remaining, set state to IVVC_OPERATE_TAP as long as all regulators are in 'remote' mode.
        if ( hasTapOpsRemaining(state->_tapOps) )
        {
            if ( allRegulatorsInRemoteMode(subbus->getPaoId()) )
            {
                state->setState( IVVCState::IVVC_OPERATE_TAP );
            }
            else
            {
                state->setState(IVVCState::IVVC_WAIT);

                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  No Operation - one or more voltage regulators in 'Auto' mode.");
                }
            }
        }
        else
        {
            state->setState(IVVCState::IVVC_WAIT);

            sendIVVCAnalysisMessage( IVVCAnalysisMessage::createNoTapOpNeededMessage( subbus->getPaoId(), CtiTime::now() ) );

            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  No Tap Operations needed.");
            }
        }
        return true;
    }

    if ( ( operatePaoId != -1 ) &&
         ( currentBusWeight - strategy->getDecisionWeight(isPeakTime) ) > state->_estimated[operatePaoId].busWeight &&
           state->getConsecutiveCapBankOps() < strategy->getMaxConsecutiveCapBankOps(isPeakTime) )
    {
        state->setConsecutiveCapBankOps( 1 + state->getConsecutiveCapBankOps() );

        CtiTime now;
        state->setTimeStamp(now);

        // record preoperation voltage values for the feeder our capbank is on
        for each (PointValueMap::value_type pointValuePair in pointValues)
        {
            try
            {
                state->_estimated[operatePaoId].capbank->updatePointResponsePreOpValue(pointValuePair.first,pointValuePair.second.value);
            }
            catch (NotFoundException& e)
            {
                CTILOG_ERROR(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Error Updating PreOpValue for deltas. PointId not found: " << pointValuePair.first);
            }
        }

        state->_estimated[operatePaoId].operated = true;
        state->setControlledBankId(operatePaoId);

        state->_estimated.clear();     // done with this data

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Operating Capbank ID# " << operatePaoId);
        }

        //send cap bank command
        operateBank(operatePaoId,subbus,dispatchConnection, strategy);

        state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
    }
    else
    {
        state->setConsecutiveCapBankOps( 0 );

        state->_estimated.clear();     // done with this data

        tapOperation(state, subbus, strategy, pointValues);
    }

    return true;
}


/*
   TAP operation stuff...
*/
void IVVCAlgorithm::tapOperation(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy,
                                 const PointValueMap & pointValues)
{
    state->setState(IVVCState::IVVC_WAIT);

    CtiTime now;

    bool isPeakTime = subbus->getPeakTimeFlag();    // Is it peak time according to the bus.
    long subbusId = subbus->getPaoId();

    if ( ( now.seconds() - state->getLastTapOpTime().seconds() ) <= (_IVVC_MIN_TAP_PERIOD_MINUTES * 60) )
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  Not Operating Voltage Regulators due to minimum tap period.");
        }

        sendIVVCAnalysisMessage( IVVCAnalysisMessage::createNoTapOpMinTapPeriodMessage( subbusId, now, _IVVC_MIN_TAP_PERIOD_MINUTES ) );

        return;
    }

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    // get all zones on the subbus...

    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus(subbusId);

    state->_tapOps.clear();

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        try
        {
            // grab the voltage regulator(s)

            for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
            {
                PointValueMap zonePointValues;      // point values for stuff assigned to the current zone ID

                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                long voltagePointId = regulator->getPointByAttribute(PointAttribute::VoltageY).getPointId();

                PointValueMap::const_iterator found = pointValues.find( voltagePointId );   // lookup
                if ( found != pointValues.end() )
                {
                    zonePointValues.insert( *found );
                }

                // Capbanks

                for each ( const Zone::IdSet::value_type & capBankId in zone->getBankIds() )
                {
                    CtiCCCapBankPtr bank = store->getCapBankByPaoId( capBankId );
                    if ( bank )
                    {
                        for each ( CtiCCMonitorPointPtr point in bank->getMonitorPoint() )
                        {
                            //  if our zone is gang operated we grab all bank monitor points despite their phase
                            //  if phase operated zone we grab only the bank monitor points on the same phase as our regulator

                            if ( zone->isGangOperated() || point->getPhase() == regulator->getPhase() )
                            {
                                PointValueMap::const_iterator iter = pointValues.find( point->getPointId() );   // lookup
                                if ( iter != pointValues.end() )    // found
                                {
                                    zonePointValues.insert( *iter );
                                }
                            }
                        }
                    }
                }

                // Other voltage points in this zone

                for each ( const Zone::PhaseToVoltagePointIds::value_type & mapping in zone->getPointIds() )
                {
                    //  if our zone is gang operated we grab all points despite their phase
                    //  if phase operated zone we grab only the points on the same phase as our regulator

                    if ( zone->isGangOperated() || mapping.first == regulator->getPhase() )
                    {
                        PointValueMap::const_iterator found = pointValues.find( mapping.second );  // lookup

                        if ( found != pointValues.end() )
                        {
                            zonePointValues.insert( *found );
                        }
                    }
                }

                state->_tapOps[ regulator->getPaoId() ] = calculateVte(zonePointValues, strategy, subbus->getAllMonitorPoints(), isPeakTime, regulator);
            }
        }
        catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
        {
            CTILOG_EXCEPTION_ERROR(dout, noRegulator);
        }
        catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
        {
            if (missingAttribute.complain())
            {
                CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
            }

        }
    }

    long rootZoneId = zoneManager.getRootZoneIdForSubbus(subbusId);

    tapOpZoneNormalization( rootZoneId, zoneManager, state->_tapOps );

    // if there are tap ops remaining, set state to IVVC_OPERATE_TAP as long as all regulators are in 'remote' mode.
    if ( hasTapOpsRemaining(state->_tapOps) )
    {
        if ( allRegulatorsInRemoteMode(subbusId) )
        {
            state->setState( IVVCState::IVVC_OPERATE_TAP );
        }
        else
        {
            state->setState(IVVCState::IVVC_WAIT);

            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  No Operation - one or more voltage regulators in 'Auto' mode.");
            }
        }
    }
    else
    {
        state->setState(IVVCState::IVVC_WAIT);

        sendIVVCAnalysisMessage( IVVCAnalysisMessage::createNoTapOpNeededMessage( subbusId, now ) );

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CTILOG_DEBUG(dout, "IVVC Algorithm: "<<subbus->getPaoName() <<"  No Tap Operations needed.");
        }
    }
}


/*
   Takes a WATT value and a desired POWER FACTOR value [ range: -100.0 to 100.0 ].
   Returns the VARs required to produce the given PF with the given Watts.
*/
double IVVCAlgorithm::calculateTargetPFVars(const double targetPF, const double wattValue)
{
   // Normalize the power factor.

   double pf = std::abs( targetPF ) / 100.0;

   // Do some range checking and validation

   if ( pf > 1.0 )
   {
       pf = 1.0;    // Clamp inside the range [0.0 , 1.0].
   }

   double vars = std::numeric_limits<double>::max();    // No power factor implies no watts and/or infinite vars.

   if ( pf != 0.0 )
   {
       vars = ( wattValue / pf ) * std::sqrt( 1.0 - std::pow( pf, 2.0 ) );
   }

   // If we have a leading power factor our VARs are negative.  Lagging VARs are positive.

   return (targetPF < 0.0) ? -vars : vars;
}


void IVVCAlgorithm::tapOpZoneNormalization(const long parentID, const ZoneManager & zoneManager, IVVCState::TapOperationZoneMap &tapOp )
{
    Zone::IdSet allChildren = zoneManager.getAllChildrenOfZone(parentID);

    for each ( const Zone::IdSet::value_type & ID in allChildren )
    {
        ZoneManager::SharedPtr  parentZone  = zoneManager.getZone(parentID);
        ZoneManager::SharedPtr  zone        = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            Zone::PhaseIdMap    parentMapping = parentZone->getRegulatorIds();

            if ( parentZone->isGangOperated() )
            {
                tapOp[ mapping.second ] -= tapOp[ parentMapping[ Cti::CapControl::Phase_Poly ] ];
            }
            else
            {
                // normalize parent to child on the same phase

                if ( parentMapping.find( mapping.first ) != parentMapping.end() )
                {
                    tapOp[ mapping.second ] -= tapOp[ parentMapping[ mapping.first ] ];
                }
            }
        }
    }

    Zone::IdSet immediateChildren = zoneManager.getZone(parentID)->getChildIds();

    for each ( const Zone::IdSet::value_type & ID in immediateChildren )
    {
        tapOpZoneNormalization( ID, zoneManager, tapOp );   // recursion!!!
    }
}


bool IVVCAlgorithm::allRegulatorsInRemoteMode(const long subbusId) const
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager       = store->getZoneManager();

    Zone::IdSet subbusZoneIds   = zoneManager.getZoneIdsBySubbus(subbusId);

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                if ( regulator->getOperatingMode() != VoltageRegulator::RemoteMode )
                {
                    return false;
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }

    return true;
}


double IVVCAlgorithm::calculateVf(const PointValueMap &voltages)
{
    double   totalSum   = 0.0;
    double   minimum    = std::numeric_limits<double>::max();
    unsigned totalCount = 0;

    if ( voltages.empty() )
    {
        return std::numeric_limits<double>::max();
    }

    for ( PointValueMap::const_iterator b = voltages.begin(), e = voltages.end(); b != e; ++b )
    {
        totalSum += b->second.value;
        minimum = std::min( minimum, b->second.value );
        totalCount++;
    }

    return ( ( totalSum / totalCount ) - minimum  );
}


double IVVCAlgorithm::calculateVte(const PointValueMap &voltages, IVVCStrategy* strategy,
                                   const std::map<long, CtiCCMonitorPointPtr> & _monitorMap,
                                   const bool isPeakTime,
                                   Cti::CapControl::VoltageRegulatorManager::SharedPtr  regulator)
{
    double overVmax   = 0.0;
    double overVrm    = std::numeric_limits<double>::max();
    double underVmin  = 0.0;
    bool   allowTapUp = true;

    if ( voltages.empty() )
    {
        return 0.0;
    }

    for each ( const PointValueMap::value_type & item in voltages )
    {
        double Vmax = strategy->getUpperVoltLimit(isPeakTime);
        double Vmin = strategy->getLowerVoltLimit(isPeakTime);
        double Vrm  = strategy->getLowerVoltLimit(isPeakTime) + strategy->getVoltageRegulationMargin(isPeakTime);

        if ( boost::optional<CtiCCMonitorPointPtr> monitorLookup = Cti::mapFind( _monitorMap, item.first ) )
        {
            const CtiCCMonitorPoint & monitor = **monitorLookup;

            if ( monitor.getOverrideStrategy() )
            {
                Vmax = monitor.getUpperBandwidth();
                Vmin = monitor.getLowerBandwidth();
                Vrm  = monitor.getLowerBandwidth() + strategy->getVoltageRegulationMargin(isPeakTime);
            }
        }

        const double voltage = item.second.value;

        overVmax  = std::max( overVmax, voltage - Vmax );
        overVrm   = std::min( overVrm, voltage - Vrm );
        underVmin = std::max( underVmin, Vmin - voltage );

        // If the voltage is close to Vmax such that a single TapUp would push us over it, we disallow it.
        if ( ( voltage + regulator->getVoltageChangePerTap() ) >= Vmax )
        {
            allowTapUp = false;
        }
    }

    if ( overVmax > 0.0 || overVrm >= 0.0 )
    {
        return regulator->requestVoltageChange( -std::max( overVmax, overVrm ) );
    }

    if ( underVmin > 0.0 && allowTapUp )
    {
        return regulator->requestVoltageChange( underVmin );
    }

    return 0.0;
}


double IVVCAlgorithm::voltageViolationCalculator(const double voltage, const IVVCStrategy * strategy, const bool isPeakTime)
{
    double cost = 0.0;

    if ( voltage < strategy->getLowerVoltLimit(isPeakTime) )
    {
        const double kneePoint = strategy->getLowerVoltLimit(isPeakTime) - strategy->getLowVoltageViolationBandwidth();

        cost = ( voltage <= kneePoint )
            ? strategy->getEmergencyLowVoltageViolationCost() * ( voltage - kneePoint )
                - strategy->getLowVoltageViolationCost() * strategy->getLowVoltageViolationBandwidth()
            : strategy->getLowVoltageViolationCost() * ( voltage - strategy->getLowerVoltLimit(isPeakTime) );
    }

    if ( voltage >= strategy->getUpperVoltLimit(isPeakTime) )
    {
        const double kneePoint = strategy->getUpperVoltLimit(isPeakTime) + strategy->getHighVoltageViolationBandwidth();

        cost = ( voltage >= kneePoint )
            ? strategy->getEmergencyHighVoltageViolationCost() * ( voltage - kneePoint )
                + strategy->getHighVoltageViolationCost() * strategy->getHighVoltageViolationBandwidth()
            : strategy->getHighVoltageViolationCost() * ( voltage - strategy->getUpperVoltLimit(isPeakTime) );
    }

    return cost;
}


static bool pointValueComparator(const PointValueMap::value_type & v1, const PointValueMap::value_type & v2)
{
    return v1.second.value < v2.second.value;
}


double IVVCAlgorithm::calculateVoltageViolation(const PointValueMap & voltages,
                                                const IVVCStrategy * strategy, const bool isPeakTime)
{
    if ( voltages.empty() )
    {
        return 0.0;
    }

    PointValueMap::const_iterator minElement = std::min_element( voltages.begin(), voltages.end(), pointValueComparator );
    PointValueMap::const_iterator maxElement = std::max_element( voltages.begin(), voltages.end(), pointValueComparator );

    return voltageViolationCalculator( minElement->second.value, strategy, isPeakTime )
            + voltageViolationCalculator( maxElement->second.value, strategy, isPeakTime );
}


double IVVCAlgorithm::feederPFCorrectionCalculator( const double actualFeederPF,
                                                    const IVVCStrategy * strategy, const bool isPeakTime ) const
{
    return std::min( strategy->getPowerFactorCorrectionMaxCost(),
                     std::max( 0.0,
                               strategy->getPowerFactorCorrectionCost()
                                * ( calculatePowerFactorCost( actualFeederPF, strategy, isPeakTime ) / 100.0
                                        - strategy->getPowerFactorCorrectionBandwidth() ) ) );
}


double IVVCAlgorithm::calculatePowerFactorCost( const double powerFactor,
                                                const IVVCStrategy * strategy, const bool isPeakTime ) const
{
    // get the target power factor from the strategy
    // convert from strategy representation [-100.0, 100.0] to [0.0, 200.0]

    double biasedTargetPF = strategy->getTargetPF(isPeakTime);
    if ( biasedTargetPF < 0.0 )
    {
        biasedTargetPF += 200.00;
    }

    // compute the power factor cost

    return std::abs( biasedTargetPF - ( 100.0 * powerFactor ) );
}


// Is any voltage value >= its upper bandwidth (or the strategies if it doesn't override it)
bool IVVCAlgorithm::checkForMultiTapOperation( const PointValueMap & voltages,
                                               const std::map<long, CtiCCMonitorPointPtr> & _monitorMap,
                                               IVVCStrategy * strategy,
                                               const bool isPeakTime ) const
{
    for ( PointValueMap::const_iterator b = voltages.begin(), e = voltages.end(); b != e; ++b )
    {
        double Vmax = strategy->getUpperVoltLimit(isPeakTime);

        std::map<long, CtiCCMonitorPointPtr>::const_iterator iter = _monitorMap.find( b->first );

        if ( iter != _monitorMap.end() )    // monitor point exists - use its bandwidth settings instead
        {
            const CtiCCMonitorPoint &   monitor = *iter->second;

            if ( monitor.getOverrideStrategy() )
            {
                Vmax = monitor.getUpperBandwidth();
            }
        }

        if ( b->second.value >= Vmax )
        {
            return true;
        }
    }

    return false;
}


void IVVCAlgorithm::sendDisableRemoteControl( CtiCCSubstationBusPtr subbus )
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        CTILOG_DEBUG(dout, "IVVC Algorithm: "<< subbus->getPaoName() << " - Sending remote control disable. ");
    }

    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                if ( regulator->getOperatingMode() == VoltageRegulator::RemoteMode )
                {
                    regulator->executeDisableRemoteControl();
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }
}


void IVVCAlgorithm::handleCommsLost(IVVCStatePtr state, CtiCCSubstationBusPtr subbus)
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    // Switch the voltage regulators to auto mode.
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName() << " - Comms lost.");
        }

        sendDisableRemoteControl( subbus );
    }

    // Write to the event log.
    {
        long stationId, areaId, spAreaId;
        store->getSubBusParentInfo(subbus, spAreaId, areaId, stationId);

        EventLogEntries ccEvents;
        ccEvents.push_back(
            EventLogEntry(
                0,
                SYS_PID_CAPCONTROL,
                spAreaId,
                areaId,
                stationId,
                subbus->getPaoId(),
                0,
                capControlIvvcCommStatus,
                0,
                0,
                "IVVC Comms Lost",
                "cap control") );

        PointValueMap rejectedPoints = state->getGroupRequest()->getRejectedPointValues();
        std::set<long> missingIds = state->getGroupRequest()->getMissingPoints();

        for each (const PointValueMap::value_type& pv in rejectedPoints)
        {
            std::ostringstream  eventText;

            eventText
                << "IVVC Rejected Point Response - Quality: 0x"
                << std::hex << pv.second.quality << std::dec
                << " - Timestamp: "
                << pv.second.timestamp;

            ccEvents.push_back(
                EventLogEntry(
                    0,
                    pv.first,
                    spAreaId,
                    areaId,
                    stationId,
                    subbus->getPaoId(),
                    0,
                    capControlIvvcRejectedPoint,
                    0,
                    pv.second.value,
                    eventText.str(),
                    "cap control") );

            // remove the rejected point Ids the set of missingIds to reduce log entries.
            missingIds.erase( pv.first );
        }

        for each (long ID in missingIds)
        {
            ccEvents.push_back(
                EventLogEntry(
                    0,
                    ID,
                    spAreaId,
                    areaId,
                    stationId,
                    subbus->getPaoId(),
                    0,
                    capControlIvvcMissingPoint,
                    0,
                    0,
                    "IVVC Missing Point Response",
                    "cap control") );
        }

        DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();

        CtiCapController::submitEventLogEntries(ccEvents);
    }
}


bool IVVCAlgorithm::hasTapOpsRemaining(const IVVCState::TapOperationZoneMap & tapOp) const
{
    for each ( const IVVCState::TapOperationZoneMap::value_type & operation in tapOp )
    {
        if ( operation.second != 0 )
        {
            return true;
        }
    }

    return false;
}


void IVVCAlgorithm::sendIVVCAnalysisMessage( Cti::Messaging::CapControl::IVVCAnalysisMessage * message )
{
    using namespace Cti::Messaging;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    StreamableMessage::auto_type msg( message );
    ActiveMQConnectionManager::enqueueMessage( OutboundQueue::IvvcAnalysisMessage, msg );
}

void IVVCAlgorithm::updateCommsState( const long busCommsPointId, const bool isCommsLost ) const
{
    if (busCommsPointId > 0)
    {
        DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();

        dispatchConnection->WriteConnQue(
            new CtiPointDataMsg( busCommsPointId, isCommsLost ? 1.0 : 0.0 ) ); // NormalQuality, StatusPointType
    }
}


void IVVCAlgorithm::calculateMultiTapOperation( PointValueMap & voltages,
                                                CtiCCSubstationBusPtr subbus,
                                                IVVCStrategy * strategy,
                                                IVVCState::TapOperationZoneMap & solution )
{
    ZoneManager & zoneManager = CtiCCSubstationBusStore::getInstance()->getZoneManager();

    long rootZoneId = zoneManager.getRootZoneIdForSubbus( subbus->getPaoId() );

    std::map<Cti::CapControl::Phase, double>    offsets =
        boost::assign::map_list_of( Cti::CapControl::Phase_A, 0.0 )
                                  ( Cti::CapControl::Phase_B, 0.0 )
                                  ( Cti::CapControl::Phase_C, 0.0 )
                                  ( Cti::CapControl::Phase_Poly, 0.0 );

    calculateMultiTapOperationHelper( rootZoneId, voltages, offsets, subbus, strategy, solution );
}


double IVVCAlgorithm::getVmaxForPoint( const long pointID, CtiCCSubstationBusPtr subbus, IVVCStrategy * strategy ) const
{
    const bool isPeakTime = subbus->getPeakTimeFlag();
    const std::map<long, CtiCCMonitorPointPtr> & monitorMap = subbus->getAllMonitorPoints();

    double Vmax = strategy->getUpperVoltLimit(isPeakTime);

    std::map<long, CtiCCMonitorPointPtr>::const_iterator iter = monitorMap.find( pointID );

    if ( iter != monitorMap.end() )    // monitor point exists - use its bandwidth settings instead
    {
        const CtiCCMonitorPoint &   monitor = *iter->second;

        if ( monitor.getOverrideStrategy() )
        {
            Vmax = monitor.getUpperBandwidth();
        }
    }

    return Vmax;
}


void IVVCAlgorithm::updateMaxOvervoltages( const long pointID,
                                           const Cti::CapControl::Phase & phase,
                                           const double Vmax,
                                           std::map<Cti::CapControl::Phase, double> cumulativeOffsets,
                                           PointValueMap & voltages,
                                           std::map<Cti::CapControl::Phase, double> & maxOverages )
{
    PointValueMap::iterator pointValue = voltages.find( pointID );

    if ( pointValue != voltages.end() )
    {
        pointValue->second.value += cumulativeOffsets[ phase ];

        if ( pointValue->second.value > Vmax )
        {
            maxOverages[ phase ] = std::max( maxOverages[ phase ], pointValue->second.value - Vmax );
        }
    }
}


void IVVCAlgorithm::calculateMultiTapOperationHelper( const long zoneID,
                                                      PointValueMap & voltages,
                                                      std::map<Cti::CapControl::Phase, double> cumulativeVoltageOffsets,
                                                      CtiCCSubstationBusPtr subbus,
                                                      IVVCStrategy * strategy,
                                                      IVVCState::TapOperationZoneMap & solution )
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    try
    {
        ZoneManager::SharedPtr zone = store->getZoneManager().getZone( zoneID );

        std::map<Cti::CapControl::Phase, double>    maxOvervoltages =
            boost::assign::map_list_of( Cti::CapControl::Phase_A, 0.0 )
                                      ( Cti::CapControl::Phase_B, 0.0 )
                                      ( Cti::CapControl::Phase_C, 0.0 )
                                      ( Cti::CapControl::Phase_Poly, 0.0 );

// 1.   add incoming (negative) cumulativeVoltageOffsets to all points in the zone by phase
// 2.   scan zone for overvoltages by phase

        // Capbanks

        for each ( const Zone::IdSet::value_type & ID in zone->getBankIds() )
        {
            if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( ID ) )
            {
                for each ( CtiCCMonitorPointPtr point in bank->getMonitorPoint() )
                {
                    const long                      pointID = point->getPointId();
                    const Cti::CapControl::Phase    phase   = point->getPhase();

                    updateMaxOvervoltages( pointID,
                                           phase,
                                           getVmaxForPoint( pointID, subbus, strategy ),
                                           cumulativeVoltageOffsets,
                                           voltages,
                                           maxOvervoltages );
                }
            }
        }

        // Additional Voltage Points

        for each ( const Zone::PhaseToVoltagePointIds::value_type & entry in zone->getPointIds() )
        {
            const long                      pointID = entry.second;
            const Cti::CapControl::Phase    phase   = entry.first;

            updateMaxOvervoltages( pointID,
                                   phase,
                                   getVmaxForPoint( pointID, subbus, strategy ),
                                   cumulativeVoltageOffsets,
                                   voltages,
                                   maxOvervoltages );
        }

        // Voltage Regulators

        for each ( const Zone::PhaseIdMap::value_type & entry in zone->getRegulatorIds() )
        {
            const long                      regulatorID = entry.second;
            const Cti::CapControl::Phase    phase       = entry.first;

            VoltageRegulatorManager::SharedPtr regulator =
                store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

            const long  pointID = regulator->getPointByAttribute( PointAttribute::VoltageY ).getPointId();

            updateMaxOvervoltages( pointID,
                                   phase,
                                   getVmaxForPoint( pointID, subbus, strategy ),
                                   cumulativeVoltageOffsets,
                                   voltages,
                                   maxOvervoltages );
        }

// 3.   if (overvoltage)  calculate # of taps and change in voltage

        // if gang operated find the maximum of the phase overvoltages and use it to compute the tap count
        //  else just compute a tap count for each phase...

        std::map<Cti::CapControl::Phase, double>    realVoltageChange =
            boost::assign::map_list_of( Cti::CapControl::Phase_A, 0.0 )
                                      ( Cti::CapControl::Phase_B, 0.0 )
                                      ( Cti::CapControl::Phase_C, 0.0 )
                                      ( Cti::CapControl::Phase_Poly, 0.0 );

        for each ( const Zone::PhaseIdMap::value_type & entry in zone->getRegulatorIds() )
        {
            const long                      regulatorID = entry.second;
            const Cti::CapControl::Phase    phase       = entry.first;

            VoltageRegulatorManager::SharedPtr regulator =
                store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

            double theMaxVoltage = maxOvervoltages[ phase ];

            if ( zone->isGangOperated() )
            {
                // maximum overvoltage across all phases

                for each ( const std::map<Cti::CapControl::Phase, double>::value_type & entry in maxOvervoltages )
                {
                    theMaxVoltage = std::max( theMaxVoltage, entry.second );
                }
            }

            if ( theMaxVoltage > 0.0 )
            {
                // tapping down so this should be negative and is an emergency voltage adjustment

                realVoltageChange[ phase ]  =
                    solution[ regulatorID ] =
                        regulator->requestVoltageChange( -theMaxVoltage, true );
            }

            if ( zone->isGangOperated() )
            {
                // cascade the polyphase delta to each phase

                realVoltageChange[ Cti::CapControl::Phase_A ] =
                realVoltageChange[ Cti::CapControl::Phase_B ] =
                realVoltageChange[ Cti::CapControl::Phase_C ] =
                    realVoltageChange[ Cti::CapControl::Phase_Poly ];
            }
        }

// 4.   subtract tap solution voltage from all members of the zone.

        // Capbanks

        for each ( const Zone::IdSet::value_type & ID in zone->getBankIds() )
        {
            if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( ID ) )
            {
                for each ( CtiCCMonitorPointPtr point in bank->getMonitorPoint() )
                {
                    const long                      pointID = point->getPointId();
                    const Cti::CapControl::Phase    phase   = point->getPhase();

                    if ( boost::optional<PointValue> pv = Cti::mapFind( voltages, pointID ) )
                    {
                        pv->value += realVoltageChange[ phase ];
                    }
                }
            }
        }

        // Additional Voltage Points

        for each ( const Zone::PhaseToVoltagePointIds::value_type & entry in zone->getPointIds() )
        {
            const long                      pointID = entry.second;
            const Cti::CapControl::Phase    phase   = entry.first;

            if ( boost::optional<PointValue> pv = Cti::mapFind( voltages, pointID ) )
            {
                pv->value += realVoltageChange[ phase ];
            }
        }

        // Voltage Regulators

        for each ( const Zone::PhaseIdMap::value_type & entry in zone->getRegulatorIds() )
        {
            const long                      regulatorID = entry.second;
            const Cti::CapControl::Phase    phase       = entry.first;

            VoltageRegulatorManager::SharedPtr regulator =
                store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

            const long  pointID = regulator->getPointByAttribute( PointAttribute::VoltageY ).getPointId();

            if ( boost::optional<PointValue> pv = Cti::mapFind( voltages, pointID ) )
            {
                pv->value += realVoltageChange[ phase ];
            }
        }

// 5.   recursion!!!

        const Cti::CapControl::Phase phases[] =
        {
            Cti::CapControl::Phase_A,
            Cti::CapControl::Phase_B,
            Cti::CapControl::Phase_C,
            Cti::CapControl::Phase_Poly
        };

        for each ( const Cti::CapControl::Phase phase in phases )
        {
            cumulativeVoltageOffsets[ phase ] += realVoltageChange[ phase ];
        }

        for each ( const Zone::IdSet::value_type & ID in zone->getChildIds() )
        {
            calculateMultiTapOperationHelper( ID, voltages, cumulativeVoltageOffsets, subbus, strategy, solution );
        }
    }
    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
    {
        CTILOG_EXCEPTION_ERROR(dout, noRegulator);
    }
    catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
    {
        if (missingAttribute.complain())
        {
            CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
        }
    }
}


/*
    This guy will run a validation check on the received point data request.
        We are looking for non-stale data and that all of the required BusPower points are present and accounted for.

    Now the validity is on a per zone per phase basis...
*/
bool IVVCAlgorithm::hasValidData( PointDataRequestPtr& request, CtiTime timeNow, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy )
{
    bool dataIsValid = true;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );
    PointValueMap pointValues = request->getPointValues();

    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        CTILOG_DEBUG(dout, "IVVC Algorithm: Analysing Point Data Request Results.");
    }

    const Cti::CapControl::Phase phases[] =
    {
        Cti::CapControl::Phase_A,
        Cti::CapControl::Phase_B,
        Cti::CapControl::Phase_C,
        Cti::CapControl::Phase_Poly
    };

    // Statistics collected

    int totalPoints     = 0,
        missingPoints   = 0,
        stalePoints     = 0;

    // Process each zone individually

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        // voltage regulator(s)...

        for each ( const Cti::CapControl::Phase phase in phases )
        {
            totalPoints = missingPoints = stalePoints = 0;      // reset stats

            Zone::PhaseIdMap    regulatorSearch = zone->getRegulatorIds();

            Zone::PhaseIdMap::const_iterator    phase_ID_iterator, phase_ID_end;

            boost::tie( phase_ID_iterator, phase_ID_end ) = regulatorSearch.equal_range( phase );

            for ( ; phase_ID_iterator != phase_ID_end; ++phase_ID_iterator )
            {
                try
                {
                    VoltageRegulatorManager::SharedPtr  regulator
                        = store->getVoltageRegulatorManager()->getVoltageRegulator( phase_ID_iterator->second );

                    const long voltagePointId = regulator->getPointByAttribute(PointAttribute::VoltageY).getPointId();

                    if ( voltagePointId > 0 )
                    {
                        findPointInRequest( voltagePointId, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
                    }
                }
                catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                {
                    if ( ! subbus->getDisableFlag() )
                    {
                        CTILOG_EXCEPTION_ERROR(dout, noRegulator);
                    }
                }
                catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
                {
                    if ( missingAttribute.complain() )
                    {
                        CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                    }
                }
            }

            dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                                    totalPoints, missingPoints, stalePoints,
                                                    strategy->getRegulatorCommReportingPercentage(),
                                                    IVVCAnalysisMessage::Scenario_VoltageRegulatorCommsIncomplete,
                                                    IVVCAnalysisMessage::Scenario_VoltageRegulatorCommsStale,
                                                    timeNow,
                                                    "Regulator on Phase: " + Cti::CapControl::desolvePhase( phase ) );
            // capbank(s)...

            totalPoints = missingPoints = stalePoints = 0;      // reset stats

            for each ( const Zone::IdSet::value_type & ID in zone->getBankIds() )
            {
                CtiCCCapBankPtr bank    = store->findCapBankByPAObjectID( ID );

                for each ( CtiCCMonitorPointPtr point in bank->getMonitorPoint() )
                {
                    const long cbcPointID = point->getPointId();

                    if ( cbcPointID > 0 && point->getPhase() == phase )
                    {
                        findPointInRequest( cbcPointID, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
                    }
                }
            }

            dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                                    totalPoints, missingPoints, stalePoints,
                                                    strategy->getCapbankCommReportingPercentage(),
                                                    IVVCAnalysisMessage::Scenario_CBCCommsIncomplete,
                                                    IVVCAnalysisMessage::Scenario_CBCCommsStale,
                                                    timeNow,
                                                    "CBC(s) on Phase: " + Cti::CapControl::desolvePhase( phase ) );

            // voltage monitor point(s)...

            totalPoints = missingPoints = stalePoints = 0;      // reset stats

            Zone::PhaseToVoltagePointIds    voltageMonitorSearch = zone->getPointIds();

            std::pair< Zone::PhaseToVoltagePointIds::const_iterator, Zone::PhaseToVoltagePointIds::const_iterator >
                filterResult = voltageMonitorSearch.equal_range( phase );

            for ( ; filterResult.first != filterResult.second; ++filterResult.first )
            {
                const long voltageMonitorPointID = filterResult.first->second;

                if ( voltageMonitorPointID > 0 )
                {
                    findPointInRequest( voltageMonitorPointID, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
                }
            }

            dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                                    totalPoints, missingPoints, stalePoints,
                                                    strategy->getVoltageMonitorCommReportingPercentage(),
                                                    IVVCAnalysisMessage::Scenario_VoltageMonitorCommsIncomplete,
                                                    IVVCAnalysisMessage::Scenario_VoltageMonitorCommsStale,
                                                    timeNow,
                                                    "Other(s) on Phase: " + Cti::CapControl::desolvePhase( phase ) );
        }
    }

    // check that all BusPower points exist

    totalPoints = missingPoints = stalePoints = 0;      // reset stats

    const long busWattPointId = subbus->getCurrentWattLoadPointId();

    if ( busWattPointId > 0 )
    {
        findPointInRequest( busWattPointId, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
    }
    else
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CTILOG_DEBUG(dout, "IVVC Configuration Error: Missing Watt Point on Bus: " << subbus->getPaoName());
        }
    }

    for each ( const long busVarPointId in subbus->getCurrentVarLoadPoints() )
    {
        if ( busVarPointId > 0 )
        {
            findPointInRequest( busVarPointId, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
        }
        else
        {
            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CTILOG_DEBUG(dout, "IVVC Configuration Error: Missing Var Point on Bus: " << subbus->getPaoName());
            }
        }
    }

    // check the feeder watt and var points exist and are in the request
    //  -- iff control method is bus optimized

    if ( strategy->getMethodType() == ControlStrategy::BusOptimizedFeeder )
    {
        for each ( CtiCCFeederPtr feeder in subbus->getCCFeeders() )
        {
            const long feederWattPointId = feeder->getCurrentWattLoadPointId();

            if ( feederWattPointId > 0 )
            {
                findPointInRequest( feederWattPointId, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CTILOG_DEBUG(dout, "IVVC Configuration Error: Missing Watt Point on Feeder: " << feeder->getPaoName());
                }
            }

            for each ( const long feederVarPoint in feeder->getCurrentVarLoadPoints() )
            {
                if ( feederVarPoint > 0 )
                {
                    findPointInRequest( feederVarPoint, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
                }
                else
                {
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CTILOG_DEBUG(dout, "IVVC Configuration Error: Missing Var Point on Feeder: " << feeder->getPaoName());
                    }
                }
            }
        }
    }

    dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                            totalPoints, missingPoints, stalePoints,
                                            100.0,
                                            IVVCAnalysisMessage::Scenario_RequiredPointCommsIncomplete,
                                            IVVCAnalysisMessage::Scenario_RequiredPointCommsStale,
                                            timeNow,
                                            "BusPower" );

    return dataIsValid;
}


void IVVCAlgorithm::findPointInRequest( const long pointID,
                                        const PointValueMap & pointValues,
                                        PointDataRequestPtr & request,
                                        const CtiTime & timeNow,
                                        int & totalPoints, int & missingPoints, int & stalePoints )
{
    PointValueMap::const_iterator   pt = pointValues.find( pointID );

    totalPoints++;
    if ( pt == pointValues.end() )
    {
        missingPoints++;
    }
    else if ( pt->second.timestamp <= ( timeNow - ( _POINT_AGE * 60 ) ) )
    {
        stalePoints++;
        request->removePointValue( pointID );
    }
}


bool IVVCAlgorithm::analysePointRequestData( const long subbusID,
                                             const int totalPoints, const int missingPoints, const int stalePoints,
                                             const double minimum,
                                             const int incompleteScenario,  const int staleScenario,
                                             const CtiTime & timeNow, const std::string & type )
{
    bool isValid = true;

    if ( totalPoints > 0 )
    {
        std::string logMessage = "Data Current";

        const double percentComplete    = ( 100.0 * ( totalPoints - missingPoints ) ) / totalPoints;
        const double percentNonStale    = ( 100.0 * ( totalPoints - ( missingPoints + stalePoints ) ) ) / totalPoints;

        if ( percentComplete < minimum )
        {
            isValid = false;

            logMessage = "Incomplete data. Received ";
            logMessage += CtiNumStr( percentComplete );
            logMessage += "%. Minimum was ";
            logMessage += CtiNumStr( minimum );
            logMessage += "%";

            sendIVVCAnalysisMessage( IVVCAnalysisMessage::createCommsRatioMessage( subbusID, incompleteScenario, timeNow, percentComplete, minimum ) );
        }
        else if ( percentNonStale < minimum )
        {
            isValid = false;

            logMessage = "Stale data. Received ";
            logMessage += CtiNumStr( percentNonStale );
            logMessage += "%. Minimum was ";
            logMessage += CtiNumStr( minimum );
            logMessage += "%";

            sendIVVCAnalysisMessage( IVVCAnalysisMessage::createCommsRatioMessage( subbusID, staleScenario, timeNow, percentNonStale, minimum ) );
        }

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CTILOG_DEBUG(dout, "IVVC Algorithm: " << logMessage << " for Request Type: " << type);
        }
    }

    return isValid;
}

