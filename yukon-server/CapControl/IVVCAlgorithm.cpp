#include "precompiled.h"

#include <boost/assign/list_of.hpp>
#include <boost/tuple/tuple.hpp>
#include <boost/range/numeric.hpp>
#include <boost/range/algorithm/set_algorithm.hpp>
#include <boost/range/algorithm/count_if.hpp>

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
#include "ccutil.h"
#include "std_helper.h"
#include "IVVCAnalysisMessage.h"
#include "database_util.h"
#include "database_reader.h"
#include "tbl_pt_alarm.h"

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
using Cti::CapControl::formatMapInfo;
using Cti::CapControl::eligibleForVoltageControl;

using namespace Cti::Messaging::CapControl;

extern unsigned long _SCAN_WAIT_EXPIRE;
extern unsigned long _POINT_AGE;
extern unsigned long _POST_CONTROL_WAIT;
extern unsigned long _IVVC_MIN_TAP_PERIOD_MINUTES;
extern unsigned long _CC_DEBUG;
extern unsigned long _IVVC_COMMS_RETRY_COUNT;
extern double _IVVC_NONWINDOW_MULTIPLIER;
extern bool _IVVC_STATIC_DELTA_VOLTAGES;
extern bool _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS;
extern unsigned long _REFUSAL_TIMEOUT;
extern unsigned long _MAX_KVAR;
extern long _MAXOPS_ALARM_CATID;

long GetDmvTestExecutionID( Cti::Database::DatabaseConnection & connection );
void updateDmvTestStatus( const long            executionID,
                          const long            testID,
                          const CtiTime         stopTime,
                          const std::string &   status );
unsigned validateTapOpSolution( const IVVCState::TapOperationZoneMap & tapOp );


bool processDmvScanData( IVVCStatePtr state, const std::string & busName );
PointValueMap getAllRegulatorsTapPositions( const long subbusId );


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
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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

/* 
    According to EASPRO-504 we want to disable the bus under a variety of conditions listed below.
 
    Operating Mode 1 (Locked Forward): A supported operating mode in Direct Tap or Set Point Control modes. If the
        REVERSE FLOW STATUS is FALSE then Yukon should issue heartbeats (if configured), execute IVVC analysis and issue
        Tap Raise/Lower or FORWARD SET POINT control commands. If the REVERSE FLOW STATUS is TRUE then Yukon should disable
        the associated bus.
 
    Operating Mode 2 (Locked Reverse): A supported operating mode in Set Point Control mode. If the REVERSE FLOW STATUS
        is TRUE then Yukon should issue heartbeats (if configured), execute IVVC analysis and issue REVERSE SET POINT control
        commands. A Regulator could be installed Backwards accidentally or could be installed on a permanently moved
        switchable feeder section resulting in a change in flow direction. CRITICALLY, the associated regulator point
        mapping has to place the right load and source side reg voltages in the correct voltage control zone. If the
        REVERSE FLOW STATUS is FALSE then Yukon should disable the associated bus.
 
    Operating Mode 3 (Reverse Idle): A supported operating mode in Set Point Control mode. If the REVERSE FLOW STATUS
        is FALSE then Yukon should issue heartbeats (if configured), execute IVVC analysis and issue FORWARD SET POINT
        control commands. If the REVERSE FLOW STATUS is TRUE then Yukon should disable the associated bus.
 
    Operating Mode 4 (Neutral Idle Mode): A supported operating mode in Set Point Control mode. If the REVERSE FLOW STATUS
        is FALSE then Yukon should issue heartbeats (if configured), execute IVVC analysis and issue control commands. If
        the REVERSE FLOW STATUS is TRUE then Yukon should disable the associated bus.
 
    Operating Mode 5 (Bi-Directional): A supported operating mode in Set Point Control mode. The orientation of the
        regulator is key. IF the regulator is installed Source to Load and the REVERSE FLOW STATUS is FALSE then Yukon
        should issue heartbeats (if configured), execute IVVC analysis and issue control commands. IF the regulator is
        installed Load to Source and the REVERSE FLOW STATUS is TRUE then Yukon should issue heartbeats (if configured),
        execute IVVC analysis and issue control commands. CRITICALLY, the associated regulator point mapping has to place
        the right load and source side reg voltages in the correct voltage control zone. A Regulator could be installed
        Backwards accidentally or permanently reconfigured.
 
    Operating Mode 6 (Cogeneration): A supported operating mode in Set Point Control mode. If the REVERSE FLOW
        STATUS is FALSE then Yukon should issue heartbeats (if configured), execute IVVC analysis and issue FORWARD
        SET POINT control commands. If the REVERSE FLOW STATUS is TRUE then Yukon should issue heartbeats (if configured),
        execute IVVC analysis and issue REVERSE SET POINT control commands.
 
    Operating Mode 7 (Reactive Bi-directional): Not supported by Yukon.
 
    Operating Mode 8 (Auto Determination): Not supported by Yukon. 
*/ 
std::string IVVCAlgorithm::handleReverseFlow( CtiCCSubstationBusPtr subbus )
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager       = store->getZoneManager();
    Zone::IdSet subbusZoneIds       = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for ( const auto ID : subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        using namespace Cti::CapControl;

        for ( const auto & mapping : zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                const ControlPolicy::ControlModes  configMode = regulator->getConfigurationMode();

                if ( regulator->getControlMode() == VoltageRegulator::ManualTap )
                {
                    switch ( configMode )
                    {
                        case ControlPolicy::LockedForward:
                        {
                            if ( regulator->isReverseFlowDetected() )
                            {
                                return "ManualTap Regulator: "
                                            + regulator->getPaoName()
                                            + ", in "
                                            + resolveControlMode( configMode )
                                            + " mode, detecting Reverse Flow.";
                            }
                            break;
                        }
                        default:
                        {
                            return "ManualTap Regulator: "
                                        + regulator->getPaoName()
                                        + " in "
                                        + resolveControlMode( configMode )
                                        + " mode, is unsupported by IVVC.";
                        }
                    }
                }
                else    // regulator is in SetPoint mode
                {
                    switch ( configMode )
                    {
                        case ControlPolicy::LockedForward:
                        case ControlPolicy::ReverseIdle:
                        case ControlPolicy::NeutralIdle:
                        case ControlPolicy::Bidirectional:
                        {
                            if ( regulator->isReverseFlowDetected() )
                            {
                                return "SetPoint Regulator: "
                                            + regulator->getPaoName()
                                            + ", in "
                                            + resolveControlMode( configMode )
                                            + " mode, detecting Reverse Flow.";
                            }
                            break;
                        }
                        case ControlPolicy::LockedReverse:
                        {
                            if ( ! regulator->isReverseFlowDetected() )
                            {
                                return "SetPoint Regulator: "
                                            + regulator->getPaoName()
                                            + ", in "
                                            + resolveControlMode( configMode )
                                            + " mode, detecting Forward Flow.";
                            }
                            break;
                        }
                        case ControlPolicy::Cogeneration:
                        {
                            // this mode doesn't depend on Reverse Flow

                            break;
                        }
                        default:
                        {
                            return "SetPoint Regulator: "
                                        + regulator->getPaoName()
                                        + " in "
                                        + resolveControlMode( configMode )
                                        + " mode, is unsupported by IVVC.";
                        }
                    }
                }
            }
            catch ( const NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const MissingAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }

    return "";
}

/*
    Get all the controllable (switched) banks on the bus and make sure they belong to one of the zones.
        If they don't, then disable them.
*/
bool IVVCAlgorithm::checkAllBanksAreInControlZones( CtiCCSubstationBusPtr subbus )
{
    // 1. Collect a mapping of all of the switched banks on the bus that are not already disabled.

    std::map<long, CtiCCCapBankPtr>     banks;

    for ( auto bank : subbus->getAllSwitchedCapBanks() )
    {
        if ( ! bank->getDisableFlag() )
        {
            banks.emplace(bank->getPaoId(), bank);
        }
    }
    
    // 2. Remove the ones from the mapping that are assigned to a zone on the bus.

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager       = store->getZoneManager();
    Zone::IdSet subbusZoneIds       = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for ( const auto ID : subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for ( auto bankID : zone->getBankIds() )
        {
            banks.erase( bankID );
        }
    }

    // 3. Disable any banks left in the mapping.

    CtiMultiMsg_vec     signals;
    EventLogEntries     events;

    for ( auto entry : banks )
    {
        CtiCCCapBankPtr bank = entry.second;
            
        CTILOG_INFO(dout, "IVVC Configuration: Bank: " << bank->getPaoName() << " on bus: " << subbus->getPaoName()
                            << " is not assigned to a control zone. Disabling the bank." );

        store->UpdatePaoDisableFlagInDB( bank, true );

        std::string additional = "CapBank: " + bank->getPaoName() + formatMapInfo( bank );

        if ( bank->getOperationAnalogPointId() > 0 )
        {
            auto pSig = std::make_unique<CtiSignalMsg>( bank->getOperationAnalogPointId(),
                                                        5,                /* soe */
                                                        "CapBank not assigned to IVVC Zone",
                                                        additional,
                                                        CapControlLogType,
                                                        _MAXOPS_ALARM_CATID,
                                                        Cti::CapControl::SystemUser,
                                                        TAG_ACTIVE_ALARM, /* tags */
                                                        0,                /* pri */
                                                        0,                /* millis */
                                                        bank->getCurrentDailyOperations() );

            pSig->setCondition( CtiTablePointAlarming::highReasonability );

            signals.push_back( pSig.release() );
        }

        // write to the event log

        {
            long    stationID, areaID, specialAreaID;

            {
                CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );
                store->getSubBusParentInfo( subbus, specialAreaID, areaID, stationID );
            }

            events.push_back(
                EventLogEntry( 0,
                               bank->getStatusPointId() > 0
                                    ? bank->getStatusPointId()
                                    : SYS_PID_CAPCONTROL,
                               specialAreaID, areaID, stationID,
                               bank->getParentId(),
                               subbus->getPaoId(),
                               capControlDisable,
                               subbus->getEventSequence(),
                               0,
                               "CapBank Disabled - Not assigned to IVVC Zone",
                               Cti::CapControl::SystemUser ) );
        }
    }

    if ( ! signals.empty() )
    {
        DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();

        sendPointChanges( dispatchConnection, signals );
    }

    if ( ! events.empty() )
    {
        CtiCapController::submitEventLogEntries( events );
    }

    return banks.size() > 0;
}


bool IVVCAlgorithm::checkAllZonesHaveExpectedBusses( CtiCCSubstationBusPtr subbus )
{
    // Retrieve a list of all bank IDs in the zone
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();

    auto zoneBankIds =
        boost::accumulate(
            zoneManager.getZoneIdsBySubbus( subbus->getPaoId() ) |
                boost::adaptors::transformed( [ &zoneManager ]( const long id ) { return zoneManager.getZone( id )->getBankIds(); }),
                std::set<long>(),
                []( std::set<long> & range1, std::set<long> & range2 ) { range1.insert( range2.begin(), range2.end() ); return range1; } );

    // Make sure all zone banks are on the subbus
    auto allZoneBanksOnSubbus = boost::range::includes( subbus->getAllCapBankIds(), zoneBankIds );

    if( ! allZoneBanksOnSubbus )
    {
        CTILOG_ERROR( dout, "IVVC zone hierarchy mismatch on Substation Bus " << subbus->getPaoName() << " [" << subbus->getPaoId() <<
                            "]: Devices may have been moved or deleted unexpectedly. Disabling this bus." );
    }

    return allZoneBanksOnSubbus;
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

    if ( checkAllBanksAreInControlZones( subbus ) )
    {
        state->setState(IVVCState::IVVC_WAIT);

        CTILOG_INFO(dout, "IVVC Configuration: Disabled unassigned banks on bus: " << subbus->getPaoName()
                            << ". Aborting current analysis." );
        return;
    }
    
    // If any banks on the zone are not found within the bus hierarchy, disable the bus
    if ( ! subbus->getDisableFlag() && ! checkAllZonesHaveExpectedBusses( subbus ) )
    {
        sendDisableRemoteControl( subbus );
        state->setState( IVVCState::IVVC_WAIT );

        CtiCCExecutorFactory::createExecutor( new ItemCommand( CapControlCommand::DISABLE_SUBSTATION_BUS,
                                                               subbus->getPaoId() ) )->execute();
        return;
    }
    
    if ( subbus->getRecentlyControlledFlag() )
    {
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

    if ( ! subbus->getDisableFlag() )
    {
        //  We need to handle any reverse flow conditions detected on the bus.
        if ( const auto reason = handleReverseFlow( subbus ); ! reason.empty() )
        {
            if ( _CC_DEBUG & CC_DEBUG_IVVC )
            {
                CTILOG_DEBUG( dout, "IVVC Algorithm: " << reason << " Disabling Bus: " << subbus->getPaoName() );
            }

            sendDisableRemoteControl( subbus );
            state->setState(IVVCState::IVVC_WAIT);

            /// disable the bus

            subbus->setReEnableBusFlag( false );
            subbus->setBusUpdatedFlag( true );
            store->UpdatePaoDisableFlagInDB( subbus, true );

            std::string text       = "Substation Bus Disabled - " + reason,
                        additional = "Bus: " + subbus->getPaoName() + formatMapInfo( subbus );

            CtiCapController::getInstance()->sendMessageToDispatch(
                new CtiSignalMsg( SYS_PID_CAPCONTROL,
                                  0,
                                  text,
                                  additional,
                                  CapControlLogType,
                                  SignalEvent,
                                  Cti::CapControl::SystemUser ),
                CALLSITE );

            if ( ! subbus->getVerificationFlag() )
            {
                INT seqId = CCEventSeqIdGen();
                subbus->setEventSequence( seqId );
            }

            long stationId, areaId, spAreaId;

            store->getSubBusParentInfo( subbus, spAreaId, areaId, stationId );

            CtiCapController::submitEventLogEntry(
                EventLogEntry( 0,
                               SYS_PID_CAPCONTROL,
                               spAreaId,
                               areaId,
                               stationId,
                               subbus->getPaoId(),
                               0,
                               capControlDisable,
                               subbus->getEventSequence(),
                               0,
                               text,
                               Cti::CapControl::SystemUser ) );

            return;
        }
    }

    // subbus is enabled
    // send regulator heartbeat messages as long as we are communicating
    if ( ! state->isCommsLost() )
    {
        sendKeepAlive( subbus );
    }

    stopDisabledDeviceHeartbeats( subbus );

    DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();

    switch ( state->getState() )
    {
        case IVVCState::DMV_TEST_SETUP:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            CTILOG_INFO( dout, "Preparing to execute DMV test: " << dmvTestSettings.TestName << " on Bus: " << subbus->getPaoName() );

            long    spAreaID,   // unused...
                    areaID,
                    substationID;

            store->getSubBusParentInfo( subbus, spAreaID, areaID, substationID );

            {
                Cti::Database::DatabaseConnection   connection;

                dmvTestSettings.ExecutionID = GetDmvTestExecutionID( connection );

                static const std::string sql =
                    "INSERT INTO "
                        "DmvTestExecution "
                    "VALUES "
                        "(?, ?, ?, ?, ?, ?, ?, ?)";

                Cti::Database::DatabaseWriter   writer( connection, sql );

                writer
                    << dmvTestSettings.ExecutionID
                    << dmvTestSettings.TestId
                    << areaID
                    << substationID
                    << subbus->getPaoId()
                    << timeNow
                    << Cti::Database::DatabaseWriter::Null
                    << Cti::Database::DatabaseWriter::Null
                        ;

                Cti::Database::executeWriter( writer, CALLSITE, Cti::Database::LogDebug::Enable );
            }

            state->setState( IVVCState::DMV_TEST_DATA_GATHERING_START );
            subbus->setDmvTestRunning(true);
            break;
        }
        case IVVCState::DMV_TEST_DATA_GATHERING_START:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            state->dataGatheringEndTime = timeNow + ( 60 * dmvTestSettings.IntervalDataGatheringDuration );    // IntervalDataGatheringDuration in minutes   

            state->dmvTestStatusMessage.clear();
            state->feasibilityData.clear();
            state->dmvRegulatorInAutoMode = false;

            state->setCommsRetryCount( 0 );

            state->setState( IVVCState::DMV_TEST_PRESCAN );
            break;
        }
        case IVVCState::DMV_TEST_PRESCAN:
        {
            // Scan all the devices on the bus to see if we can even bump the bus by the required step size.

            auto & dmvTestSettings = state->getDmvTestData();

            // What points are we wanting?
            std::set<PointRequest> pointRequests;

            bool shouldScan = allowScanning && state->isScannedRequest();
            if ( ! determineDmvWatchPoints( subbus, shouldScan, pointRequests, strategy->getMethodType(), state->dmvWattVarPointIDs ) )
            {
                // Configuration Error
                CTILOG_ERROR( dout, "Bus Configuration Error: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );

                state->dmvTestStatusMessage = "Bus Configuration Error";
                state->setState( IVVCState::DMV_TEST_END_TEST );
                break;
            }

            // Set the current and expiration times for the data archiving interval
            state->setTimeStamp( timeNow );
            state->setNextControlTime( timeNow + dmvTestSettings.DataArchivingInterval );

            // Make GroupRequest Here
            PointDataRequestPtr request( _requestFactory->createDispatchPointDataRequest( dispatchConnection ) );
            request->watchPoints( pointRequests );
            state->setGroupRequest( request );

            //ActiveMQ message here for System Refresh
            sendCapControlOperationMessage( CapControlOperationMessage::createRefreshSystemMessage( subbus->getPaoId(), timeNow ) );

            if ( _CC_DEBUG & CC_DEBUG_IVVC )
            {
                CTILOG_DEBUG( dout, "DMV Test: "
                                        << ( state->isScannedRequest() ? "Scanned ": "" )
                                        << "Group Request made on Bus: "
                                        << subbus->getPaoName() );
            }

            //reset this flag.  ?? Why we do this and what does this flag even do...
            state->setScannedRequest( false );

            state->setState( IVVCState::DMV_TEST_PRESCAN_LOOP );
            break;
        }
        case IVVCState::DMV_TEST_PRESCAN_LOOP:
        {
            if ( ! regulatorsReadyForDmvTest( state, subbus ) )
            {
                break;
            }

            if ( timeNow >= state->getNextControlTime() )
            {
                state->setState( IVVCState::DMV_TEST_POSTSCAN_PROCESSING );
            }
            break;
        }
        case IVVCState::DMV_TEST_POSTSCAN_PROCESSING:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            PointDataRequestPtr request = state->getGroupRequest();

            if ( ! processDmvScanData( state, subbus->getPaoName() ) )
            {
                // Didn't receive all the requested data so we can't determine if the bump size is possible.
                CTILOG_ERROR( dout, "Incomplete Data: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );
                CTILOG_INFO( dout, request->createStatusReport() );

                state->dmvTestStatusMessage = "Bus Incomplete Data";
                state->setState( IVVCState::DMV_TEST_END_TEST );
                break;
            }

            // update our voltage data min/max pair data structure
            // skip the BusPower watt and var points as they are not voltages...
            //      feasibilityData:     pointID -> { min, max } voltages

            for ( auto pointData : request->getPointValues() )
            {
                if ( state->dmvWattVarPointIDs.count( pointData.first ) == 0 )  // its a voltage point
                {
                    // if it is in the mapping, update the min and max voltages as necessary, if it isn't, add the current
                    //  value as both the min and max

                    if ( auto result = Cti::mapFind( state->feasibilityData, pointData.first ) )
                    {
                        auto & min_max_pair = *result;

                        if ( pointData.second.value < min_max_pair.first )
                        {
                            min_max_pair.first = pointData.second.value;
                        }
                        if ( pointData.second.value > min_max_pair.second )
                        {
                            min_max_pair.second = pointData.second.value;
                        }
                    }
                    else
                    {
                        state->feasibilityData[ pointData.first ] = { pointData.second.value, pointData.second.value };
                    }
                }
            }

            // what's our next state?

            state->setState( timeNow < state->dataGatheringEndTime
                                ? IVVCState::DMV_TEST_PRESCAN
                                : IVVCState::DMV_TEST_DECIDE_BUMP_DIRECTION );
            break;
        }
        case IVVCState::DMV_TEST_DECIDE_BUMP_DIRECTION:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            PointDataRequestPtr request = state->getGroupRequest();

            // do we have enough feasibilityData?
            //  if yes then we can proceed

            const unsigned  requestSize  = request->requestSize() - state->dmvWattVarPointIDs.size(),   // asked for voltage points
                            responseSize = state->feasibilityData.size();                               // received voltage points

            const double    percentComplete = ( 100.0 * responseSize ) / requestSize;

            CTILOG_INFO( dout, "Received  " << responseSize << " of " << requestSize << " requested voltage points ( "
                                << percentComplete << "% ). Minimum needed is " << dmvTestSettings.CommSuccessPercentage << "%." );

            if ( percentComplete < dmvTestSettings.CommSuccessPercentage )
            {
                // Didn't receive all the requested data so we can't determine if the bump size is possible.
                CTILOG_ERROR( dout, "Incomplete Feasibility Data: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );

                state->dmvTestStatusMessage = "Incomplete Feasibility Data";
                state->setState( IVVCState::DMV_TEST_END_TEST );
                break;
            }

            // crunch the numbers

            const bool isPeakTime = subbus->getPeakTimeFlag();
            const auto monitorSet = subbus->getAllMonitorPoints();

            bool    canBumpUp = true,
                    canBumpDown = true;

            for ( auto feasibility : state->feasibilityData )
            {
                double Vmax = strategy->getUpperVoltLimit( isPeakTime );
                double Vmin = strategy->getLowerVoltLimit( isPeakTime );

                // has monitor point
                if ( auto monitorPoint = Cti::mapFind( subbus->getAllMonitorPoints(), feasibility.first ) )
                {
                    if ( (*monitorPoint)->getOverrideStrategy() )
                    {
                        Vmax = (*monitorPoint)->getUpperBandwidth();
                        Vmin = (*monitorPoint)->getLowerBandwidth();
                    }                   
                }

                if ( feasibility.second.first - dmvTestSettings.StepSize < Vmin )  // undervoltage
                {
                    canBumpDown = false;

                    CTILOG_INFO( dout, "Feasibility -- PointID: " << feasibility.first
                                        << " - Bumping Down would create an under-voltage condition." );
                }
                if ( feasibility.second.second + dmvTestSettings.StepSize > Vmax ) // overvoltage
                {
                    canBumpUp = false;

                    CTILOG_INFO( dout, "Feasibility -- PointID: " << feasibility.first
                                        << " - Bumping Up would create an over-voltage condition." );
                }
            }

            if ( ! ( canBumpUp || canBumpDown ) )
            {
                // Bail out of the bump test since the StepSize is too big for the existing conditions

                CTILOG_ERROR( dout, "DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName()
                                    << ". Test StepSize is too large for the current bus conditions." );

                state->dmvTestStatusMessage = "StepSize Too Large";
                state->setState( IVVCState::DMV_TEST_END_TEST );
                break;
            }

            state->bumpDirection = ( canBumpUp ? IVVCState::Up : IVVCState::Down );

            state->setState( IVVCState::DMV_TEST_CALCULATE_BUMPS );
            break;
        }
        case IVVCState::DMV_TEST_CALCULATE_BUMPS:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            //  deal with the root zone regulator(s)...

            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            ZoneManager & zoneManager = store->getZoneManager();

            const long rootZoneId = zoneManager.getRootZoneIdForSubbus( subbus->getPaoId() );

            ZoneManager::SharedPtr  rootZone = zoneManager.getZone( rootZoneId );

            // get ready for some data

            state->_tapOps.clear();
            state->_undoTapOps.clear();
            state->storedSetPoints.clear();

            // temp maps for calculated data

            std::map<std::pair<Cti::CapControl::Phase, long>, double>   headAdjustments;

            // the root zone regulators

            for ( const auto & mapping : rootZone->getRegulatorIds() )
            {
                try
                {
                    const long regulatorID = mapping.second;

                    VoltageRegulatorManager::SharedPtr  regulator
                        = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

                    const double adjustment = regulator->requestVoltageChange(
                        ( state->bumpDirection == IVVCState::Up ? 1.0 : -1.0 ) * dmvTestSettings.StepSize,
                        Cti::CapControl::VoltageRegulator::Exclusive );

                    state->storedSetPoints[ regulatorID ] =
                        ( regulator->getControlMode() == VoltageRegulator::SetPoint )
                            ? regulator->getSetPointValue()
                            : adjustment;

                    headAdjustments[ mapping ]    = adjustment;     // scratchpad for downline reg lookup
                    state->_tapOps[ regulatorID ] = adjustment;     // record the head changes for the regulator
                }
                catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                {
                    CTILOG_EXCEPTION_ERROR( dout, noRegulator );
                }
            }

            // the rest of the downline regulators
            //      if direct tap driven do nothing
            //      if set point then record the current and set the new setpoint to the old + adjustment value...

            for ( const auto & zoneID : zoneManager.getAllChildrenOfZone( rootZoneId ) )
            {
                ZoneManager::SharedPtr  zone = zoneManager.getZone( zoneID );

                for ( const auto & mapping : zone->getRegulatorIds() )
                {
                    const auto currentRegulatorPhase = mapping.first;
                    const long regulatorID           = mapping.second;

                    VoltageRegulatorManager::SharedPtr  regulator
                        = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

                    if ( regulator->getControlMode() == VoltageRegulator::SetPoint )
                    {
                        state->storedSetPoints[ regulatorID ] = regulator->getSetPointValue();

                        // to determine the amount of adjustment we need to search 'actualAdjustments' to find the
                        //  root zone regulator on the same phase and then store its adjustment for the current regulator

                        for ( const auto lookup : headAdjustments )
                        {
                            const auto phase = lookup.first.first;

                            if ( phase == currentRegulatorPhase || phase == Cti::CapControl::Phase_Poly )
                            {
                                state->_tapOps[ regulatorID ] = lookup.second;
                            }
                        }
                    }
                }
            }

            state->setState( IVVCState::DMV_TEST_ISSUE_CONTROLS );

            // Verify that the regulators are in 'Remote' mode so we can issue controls to them, also
            //  verify regulators and regulator attributes we need are available.
            //  If we have some errors - clear out the tapOp mapping... (cancel pending operations)

            if ( ! regulatorsReadyForDmvTest( state, subbus ) )
            {
                state->_tapOps.clear();

                break;
            }

            if ( validateTapOpSolution( state->_tapOps ) > 0 )
            {
                state->_tapOps.clear();

                CTILOG_ERROR( dout, "DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName()
                                    << ". Cancelling Tap Operations.  One or more regulators is missing, or is missing a required attribute." );

                state->dmvTestStatusMessage = "Invalid Regulator Setup";
                state->setState( IVVCState::DMV_TEST_END_TEST );
            }
            break;
        }
        case IVVCState::DMV_TEST_ISSUE_CONTROLS:
        {
            // 5 second delay between consecutive tap ops on same regulator
            if ( ( state->_tapOpDelay + 5 ) < timeNow )
            {
                auto & dmvTestSettings = state->getDmvTestData();

                state->_tapOpDelay = timeNow;

                // Send the tap commands

                for ( const auto & operation : state->_tapOps )
                {
                    long   regulatorID       = operation.first;
                    double voltageAdjustment = operation.second;

                    try
                    {
                        if ( voltageAdjustment != 0 )
                        {
                            VoltageRegulatorManager::SharedPtr  regulator
                                = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

                            const double actualChange = regulator->adjustVoltage( voltageAdjustment );

                            state->_tapOps[ regulatorID ]     -= actualChange;
                            state->_undoTapOps[ regulatorID ] -= actualChange;

                            CTILOG_INFO( dout, "DMV Test '" << dmvTestSettings.TestName
                                            << " - Adjusting Voltage on Regulator: " << regulator->getPaoName() );
                        }
                    }
                    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                    {
                        CTILOG_EXCEPTION_ERROR( dout, noRegulator );
                    }
                    catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
                    {
                        if ( missingAttribute.complain() )
                        {
                            CTILOG_EXCEPTION_ERROR( dout, missingAttribute );
                        }
                    }
                }

                if ( ! hasTapOpsRemaining( state->_tapOps ) )   // are we done yet?
                {
                    state->setLastTapOpTime( timeNow );
                    state->setState( IVVCState::DMV_POST_BUMP_TEST_DATA_GATHERING_START );
                }
            }
            break;
        }
        case IVVCState::DMV_POST_BUMP_TEST_DATA_GATHERING_START:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            state->dataGatheringEndTime = timeNow + ( 60 * dmvTestSettings.IntervalDataGatheringDuration );    // IntervalDataGatheringDuration in minutes   

            state->setState( IVVCState::DMV_POST_BUMP_TEST_SCAN );
            break;
        }
        case IVVCState::DMV_POST_BUMP_TEST_SCAN:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            // What points are we wanting?
            std::set<PointRequest> pointRequests;

            bool shouldScan = allowScanning && state->isScannedRequest();
            if ( ! determineDmvWatchPoints( subbus, shouldScan, pointRequests, strategy->getMethodType(), state->dmvWattVarPointIDs ) )
            {
                // Configuration Error
                CTILOG_ERROR( dout, "Bus Configuration Error: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );

                state->dmvTestStatusMessage = "Bus Configuration Error";
                state->setState( IVVCState::DMV_TEST_RETURN_BUMP_ISSUE_CONTROLS );
                break;
            }

            // Set the current and expiration times for the data archiving interval
            state->setTimeStamp( timeNow );
            state->setNextControlTime( timeNow + dmvTestSettings.DataArchivingInterval );

            // Make GroupRequest Here
            PointDataRequestPtr request( _requestFactory->createDispatchPointDataRequest( dispatchConnection ) );
            request->watchPoints( pointRequests );
            state->setGroupRequest( request );

            //ActiveMQ message here for System Refresh
            sendCapControlOperationMessage( CapControlOperationMessage::createRefreshSystemMessage( subbus->getPaoId(), timeNow ) );

            if ( _CC_DEBUG & CC_DEBUG_IVVC )
            {
                CTILOG_DEBUG( dout, "DMV Test: "
                                        << ( state->isScannedRequest() ? "Scanned ": "" )
                                        << "Group Request made on Bus: "
                                        << subbus->getPaoName() );
            }

            //reset this flag.  ?? Why we do this and what does this flag even do...
            state->setScannedRequest( false );

            state->setState( IVVCState::DMV_POST_BUMP_TEST_SCAN_LOOP );
            break;
        }
        case IVVCState::DMV_POST_BUMP_TEST_SCAN_LOOP:
        {
            if ( ! regulatorsReadyForDmvTest( state, subbus ) )
            {
                break;
            }

            if ( timeNow >= state->getNextControlTime() )
            {
                state->setState( IVVCState::DMV_POST_BUMP_TEST_SCAN_PROCESSING );
            }
            break;
        }
        case IVVCState::DMV_POST_BUMP_TEST_SCAN_PROCESSING:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            PointDataRequestPtr request = state->getGroupRequest();

            if ( ! processDmvScanData( state, subbus->getPaoName() ) )
            {
                // Didn't receive all the requested data so we can't determine if the bump size is possible.
                CTILOG_ERROR( dout, "Incomplete Data: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );
                CTILOG_INFO( dout, request->createStatusReport() );

                state->dmvTestStatusMessage = "Bus Incomplete Data";
                state->setState( IVVCState::DMV_TEST_RETURN_BUMP_ISSUE_CONTROLS );
                break;
            }

            // what's our next state?

            state->setState( timeNow < state->dataGatheringEndTime
                                ? IVVCState::DMV_POST_BUMP_TEST_SCAN
                                : IVVCState::DMV_TEST_RETURN_BUMP_ISSUE_CONTROLS );
            break;
        }
        case IVVCState::DMV_TEST_RETURN_BUMP_ISSUE_CONTROLS:
        {
            // 5 second delay between consecutive tap ops on same regulator
            if ( ( state->_tapOpDelay + 5 ) < timeNow )
            {
                auto & dmvTestSettings = state->getDmvTestData();

                state->_tapOpDelay = timeNow;

                // Send the tap commands

                for ( const auto & operation : state->_undoTapOps )
                {
                    long   regulatorID       = operation.first;
                    double voltageAdjustment = operation.second;

                    try
                    {
                        if ( voltageAdjustment != 0 )
                        {
                            VoltageRegulatorManager::SharedPtr  regulator
                                = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

                            const double actualChange = regulator->adjustVoltage( voltageAdjustment );

                            state->_undoTapOps[ regulatorID ] -= actualChange;

                            CTILOG_INFO( dout, "DMV Test '" << dmvTestSettings.TestName
                                            << " - Adjusting Voltage on Regulator: " << regulator->getPaoName() );
                        }
                    }
                    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                    {
                        CTILOG_EXCEPTION_ERROR( dout, noRegulator );
                    }
                    catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
                    {
                        if ( missingAttribute.complain() )
                        {
                            CTILOG_EXCEPTION_ERROR( dout, missingAttribute );
                        }
                    }
                }

                if ( ! hasTapOpsRemaining( state->_undoTapOps ) )       // are we done yet?
                {
                    state->setLastTapOpTime( timeNow );

                    state->setState( ( state->dmvTestStatusMessage.size() > 0 )
                                        ? IVVCState::DMV_TEST_END_TEST          // some error condition got us here - go to end
                                        : IVVCState::DMV_RETURN_BUMP_TEST_DATA_GATHERING_START );   // continue on
                }
            }
            break;
        }
        case IVVCState::DMV_RETURN_BUMP_TEST_DATA_GATHERING_START:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            state->dataGatheringEndTime = timeNow + ( 60 * dmvTestSettings.IntervalDataGatheringDuration );    // IntervalDataGatheringDuration in minutes   

            state->setState( IVVCState::DMV_RETURN_BUMP_TEST_SCAN );
            break;
        }
        case IVVCState::DMV_RETURN_BUMP_TEST_SCAN:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            // What points are we wanting?
            std::set<PointRequest> pointRequests;

            bool shouldScan = allowScanning && state->isScannedRequest();
            if ( ! determineDmvWatchPoints( subbus, shouldScan, pointRequests, strategy->getMethodType(), state->dmvWattVarPointIDs ) )
            {
                // Configuration Error
                CTILOG_ERROR( dout, "Bus Configuration Error: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );

                state->dmvTestStatusMessage = "Bus Configuration Error";
                state->setState( IVVCState::DMV_TEST_END_TEST );
                break;
            }

            // Set the current and expiration times for the data archiving interval
            state->setTimeStamp( timeNow );
            state->setNextControlTime( timeNow + dmvTestSettings.DataArchivingInterval );

            // Make GroupRequest Here
            PointDataRequestPtr request( _requestFactory->createDispatchPointDataRequest( dispatchConnection ) );
            request->watchPoints( pointRequests );
            state->setGroupRequest( request );

            //ActiveMQ message here for System Refresh
            sendCapControlOperationMessage( CapControlOperationMessage::createRefreshSystemMessage( subbus->getPaoId(), timeNow ) );

            if ( _CC_DEBUG & CC_DEBUG_IVVC )
            {
                CTILOG_DEBUG( dout, "DMV Test: "
                                        << ( state->isScannedRequest() ? "Scanned ": "" )
                                        << "Group Request made on Bus: "
                                        << subbus->getPaoName() );
            }

            //reset this flag.  ?? Why we do this and what does this flag even do...
            state->setScannedRequest( false );

            state->setState( IVVCState::DMV_RETURN_BUMP_TEST_SCAN_LOOP );
            break;
        }
        case IVVCState::DMV_RETURN_BUMP_TEST_SCAN_LOOP:
        {
            if ( ! regulatorsReadyForDmvTest( state, subbus ) )
            {
                break;
            }

            if ( timeNow >= state->getNextControlTime() )
            {
                state->setState( IVVCState::DMV_RETURN_BUMP_TEST_SCAN_PROCESSING );
            }
            break;
        }
        case IVVCState::DMV_RETURN_BUMP_TEST_SCAN_PROCESSING:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            PointDataRequestPtr request = state->getGroupRequest();

            if ( ! processDmvScanData( state, subbus->getPaoName() ) )
            {
                // Didn't receive all the requested data so we can't determine if the bump size is possible.
                CTILOG_ERROR( dout, "Incomplete Data: DMV Test '" << dmvTestSettings.TestName
                                    << "' cannot execute on bus: " << subbus->getPaoName() );
                CTILOG_INFO( dout, request->createStatusReport() );

                state->dmvTestStatusMessage = "Bus Incomplete Data";
                state->setState( IVVCState::DMV_TEST_END_TEST );
                break;
            }

            // what's our next state?

            if ( timeNow < state->dataGatheringEndTime )
            {
                state->setState( IVVCState::DMV_RETURN_BUMP_TEST_SCAN );
                break;
            }
            
            state->dmvTestStatusMessage = "DMV Test Success";
            state->setState( IVVCState::DMV_TEST_END_TEST );
            break;
        }
        case IVVCState::DMV_TEST_END_TEST:
        {
            auto & dmvTestSettings = state->getDmvTestData();

            updateDmvTestStatus( dmvTestSettings.ExecutionID, dmvTestSettings.TestId, timeNow, state->dmvTestStatusMessage );

            // If we got here because of a regulator being in 'Auto' mode, we want to let go of the
            //  bus and return all of the devices to 'Local' control mode.
            if ( state->dmvRegulatorInAutoMode )
            {
                state->dmvRegulatorInAutoMode = false;

                sendDisableRemoteControl( subbus );
            }

            state->deleteDmvState();
            state->setState( IVVCState::IVVC_WAIT );
            subbus->setDmvTestRunning( false );
            break;
        }
        case IVVCState::IVVC_WAIT:
        {
            {   // shim for the bump test... we have test data and are not currently verifying any banks
                if ( state->hasDmvTestState() && ! subbus->getVerificationFlag() )
                {
                    state->setState( IVVCState::DMV_TEST_SETUP );                    
                    break;
                }
            }

            //Just in case a DMV test falls out of the DMV loop unexpectedly
            subbus->setDmvTestRunning(false);

            {   // shim for the scheduled capbank verification
                if ( subbus->getVerificationFlag() )
                {
                    executeBusVerification( state, subbus, strategy );
                    break;
                }
            }

            // toggle these flags so the log message prints again....
            state->setShowVarCheckMsg(true);
            state->setNextControlTime(CtiTime() + strategy->getControlInterval());

            //save away start time.
            state->setTimeStamp(timeNow);

            // What points are we wanting?
            std::set<PointRequest> pointRequests;

            bool shouldScan = allowScanning && state->isScannedRequest();
            if ( ! determineWatchPoints( subbus, shouldScan, pointRequests, strategy ) )
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
            // bail early to do the verification
            if ( subbus->getVerificationFlag() )
            {
                CTILOG_INFO( dout, "IVVC Algorithm: Aborting current analysis due to pending capbank verification: "
                                        << subbus->getPaoName() );

                state->setState( IVVCState::IVVC_WAIT );
                break;
            }

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

            ValidityCheckResults    result = hasValidData( request, timeNow, subbus, *strategy );

            if ( result != ValidityCheckResults::Valid )
            {
                if ( result != ValidityCheckResults::Invalid )
                {
                    // This means we tried to look up a capbank and failed, probably due to a store reload.

                    CTILOG_ERROR( dout, "IVVC Algorithm: Data validation issue -- aborting analysis." );

                    state->setState(IVVCState::IVVC_WAIT);
                }
                else    // result == ValidityCheckResults::Invalid
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
                }

                updateCommsState( subbus->getCommsStatePointId(), state->isCommsLost() );
                CTILOG_INFO(dout, request->createStatusReport());
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
                        Cti::CapControl::SystemUser ) );

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
                CTILOG_INFO(dout, request->createStatusReport());
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

            CtiCCCapBankPtr bank = store->findCapBankByPAObjectID(bankId);
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
                    state->setState( IVVCState::IVVC_WAIT );
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

            if ( ! determineWatchPoints( subbus, allowScanning, pointRequests, strategy ) )
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
                state->setState( IVVCState::IVVC_WAIT );
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

                unsigned errorCount = validateTapOpSolution( state->_tapOps );

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
                    long   regulatorID       = operation.first;
                    double voltageAdjustment = operation.second;

                    try
                    {
                        if ( voltageAdjustment != 0 )
                        {
                            VoltageRegulatorManager::SharedPtr  regulator
                                = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorID );

                            state->_tapOps[ regulatorID ] -= regulator->adjustVoltage( voltageAdjustment );

                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CTILOG_DEBUG(dout, "IVVC Algorithm: " << subbus->getPaoName()
                                                  << " - Adjusting Voltage on Regulator: " << regulator->getPaoName());
                            }

                            // jmoc -- do we need a new type of message for set point stuff?

                            sendIVVCAnalysisMessage(
                                IVVCAnalysisMessage::createTapOperationMessage( subbus->getPaoId(),
                                                                                ( voltageAdjustment > 0 )
                                                                                    ? Scenario_TapRaiseOperation
                                                                                    : Scenario_TapLowerOperation,
                                                                                timeNow,
                                                                                regulatorID ) );
                        }
                    }
                    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
                    {
                        CTILOG_EXCEPTION_ERROR(dout, noRegulator);
                    }
                    catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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
bool IVVCAlgorithm::determineWatchPoints(CtiCCSubstationBusPtr subbus, bool sendScan, std::set<PointRequest>& pointRequests, IVVCStrategy* strategy)
{
    bool configurationError = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();

    // Process each zones regulator, CBCs and extra voltage points

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for ( const Zone::IdSet::value_type & ID : subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        // Regulator(s)

        for ( const Zone::PhaseIdMap::value_type & mapping : zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr  regulator
                    = store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                long voltagePointId = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

                pointRequests.insert( PointRequest(voltagePointId, RegulatorRequestType, ! sendScan) );

                if ( sendScan )
                {
                    regulator->executeIntegrityScan( Cti::CapControl::SystemUser );
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
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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

        for ( const Zone::IdSet::value_type & ID : capbankIds )
        {
            if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( ID ) )
            {   
                // only care about enabled banks OR disabled banks that were disabled 
                //  because they have hit their max operation count for the day
                if ( eligibleForVoltageControl( *bank ) )
                {
                    for ( CtiCCMonitorPointPtr point : bank->getMonitorPoint() )
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
            }
            else
            {
                // we will be building a point request that won't contain the points for this capbank, which is 
                //  probably OK, it will just exclude this bank from the decision tree, as if it were disabled.

                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << ID
                                        << ". Possible BusStore reset in progress." );
            }
        }

        // Additional voltage points

        for ( const Zone::PhaseToVoltagePointIds::value_type & mapping : zone->getPointIds() )
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

    for (long ID : busVarPointIds)
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
        for ( CtiCCFeederPtr feeder : subbus->getCCFeeders() )
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
            for ( long varPoint : feeder->getCurrentVarLoadPoints() )
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


bool IVVCAlgorithm::operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection, IVVCStrategy* strategy)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool performedOperation = false;

    if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( bankId ) )
    {
        if ( CtiCCFeederPtr feeder = store->findFeederByPAObjectID( bank->getParentId() ) )
        {
            bool isCapBankOpen = (bank->getControlStatus() == CtiCCCapBank::Open ||
                                  bank->getControlStatus() == CtiCCCapBank::OpenQuestionable);

            bool isCapBankClosed = (bank->getControlStatus() == CtiCCCapBank::Close ||
                                    bank->getControlStatus() == CtiCCCapBank::CloseQuestionable);

            CtiMultiMsg_vec pointChanges;
            EventLogEntries ccEvents;

            if ( ! bank->checkMaxDailyOpCountExceeded( pointChanges, ccEvents ) )
            {
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
                    CtiCapController::getInstance()->getPorterConnection()->WriteConnQue(request, CALLSITE);

                    performedOperation = true;

                    sendIVVCAnalysisMessage(
                        IVVCAnalysisMessage::createCapbankOperationMessage( subbus->getPaoId(),
                                                                            isCapBankOpen
                                                                                ? Scenario_CapbankCloseOperation
                                                                                : Scenario_CapbankOpenOperation,
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
                }
                else    // if request is NULL we returned early from create(In|De)creaseVarRequest( ... ) - this only happens if we exceed KVar
                {
                    sendIVVCAnalysisMessage( IVVCAnalysisMessage::createExceedMaxKVarMessage( subbus->getPaoId(),
                                                                                              timestamp,
                                                                                              bankId,
                                                                                              _MAX_KVAR ) );
                }
            }
            else
            {
                CTILOG_DEBUG( dout, "IVVC Algorithm: CapBank: " << bank->getPaoName()
                                        << " was not operated due to MaxDailyOps setting." );
            }

            sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEvents);
        }
    }
    else
    {
        // OK, can't operate a bank we can't find, algorithm will abort.

        CTILOG_ERROR( dout, "IVVC Algoritm: Failed to find capbank with ID: " << bankId
                                << ". Possible BusStore reset in progress." );
    }

    return performedOperation;
}


void IVVCAlgorithm::sendKeepAlive(CtiCCSubstationBusPtr subbus)
{
    using namespace Cti::CapControl;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    // need to send each phase seperated by a small delay...
    // first send poly phase and phase A
    // wait at least 1 second, send B, wait at least 1 s, send C
    // send bank messages after regulator messages every time

    CtiTime Now;
    static CtiTime PreviousHeartbeat{ CtiTime::neg_infin };
    static const std::array<Phase, 3> PhaseArray = { Phase_A, Phase_B, Phase_C };
    static auto PhaseCounter = PhaseArray.size() - 1;

    if ( Now <= PreviousHeartbeat + 1 )
    {
        return;
    }

    PhaseCounter = ++PhaseCounter % PhaseArray.size();
    auto CurrentPhase = PhaseArray[ PhaseCounter ];

    for ( const auto & ID : subbusZoneIds )
    {
        // regulators

        ZoneManager::SharedPtr  zone = zoneManager.getZone( ID );

        for ( const auto & mapping : zone->getRegulatorIds() )
        {
            try
            {
                if ( mapping.first == Phase_Poly || mapping.first == CurrentPhase )
                {
                    VoltageRegulatorManager::SharedPtr regulator =
                            store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                    regulator->executePeriodicKeepAlive( SystemUser );
                }
            }
            catch ( const NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR( dout, noRegulator );
            }
            catch ( const MissingAttribute & missingAttribute )
            {
                if ( missingAttribute.complain() )
                {
                    CTILOG_EXCEPTION_ERROR( dout, missingAttribute );
                }
            }
        }


        // capbanks

        auto bankIDs = zone->getBankIds();

        decltype( bankIDs ) disabledBankIDs, heartbeatBankIDs;

        // If the feeder is disabled, we don't want to send a heartbeat to its child banks
        for ( const auto feeder : subbus->getCCFeeders() )
        {
            if ( feeder->getDisableFlag() )
            {
                auto feederBankIds = feeder->getAllCapBankIds();
                disabledBankIDs.insert( feederBankIds.begin(), feederBankIds.end() );
            }
        }

        boost::range::set_difference( bankIDs, disabledBankIDs, std::inserter( heartbeatBankIDs, heartbeatBankIDs.begin() ) );

        for ( const long bankID : heartbeatBankIDs )
        {
            if ( auto bank = store->findCapBankByPAObjectID( bankID ) )
            {
                if ( ! bank->getDisableFlag() )
                {
                    bank->executeSendHeartbeat( SystemUser );
                }
            }
            else
            {
                // Probably not a serious problem, we will retry this on the next CBC heartbeat period, until then we
                //  will remain in comms lost mode.

                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << bankID
                                        << ". Possible BusStore reset in progress - aborting analysis." );
            }
        }
    }

    PreviousHeartbeat = Now;
}

void IVVCAlgorithm::stopDisabledDeviceHeartbeats( CtiCCSubstationBusPtr subbus )
{
    // Stop heartbeat for all banks under a disabled feeder or individually disabled banks
    auto & feeders = subbus->getCCFeeders();

    for ( auto feeder : feeders ) 
    {
        auto & banks = feeder->getCCCapBanks();

        for ( auto bank : banks )
        {
            if ( feeder->getDisableFlag() || bank->getDisableFlag() )
            {
                bank->executeStopHeartbeat( Cti::CapControl::SystemUser );
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
        dispatchConnection->WriteConnQue(pointChangeMsg, CALLSITE);
    }
}

void IVVCAlgorithm::sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, const EventLogEntries &ccEvents)
{
    CtiCapController::submitEventLogEntries(ccEvents);
    sendPointChanges(dispatchConnection,pointChanges);
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
}

/**
 * @return bool :   true    - break out of the state machine.
 *                  false   - fall through to the next case.
 */
bool IVVCAlgorithm::busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection)
{
    bool isPeakTime = subbus->getPeakTimeFlag();    // Is it peak time according to the bus.

    PointValueMap pointValues = state->getGroupRequest()->getPointValues();

    // We need to check for any newly disabled things since the point request was created and remove
    //  the disabled objects voltage values.  We don't want to use those values when doing our computations.
    {
        // Grab a new pointRequest reflecting the current state of the bus objects.
        //  DO NOT SCAN the objects!  Otherwise ignore any generated errors.

        std::set<PointRequest> pointRequests;

        determineWatchPoints( subbus, false, pointRequests, strategy );

        // strip off the extra info and just get a set of point IDs we a care about.

        std::set<long> requestPointIDs;

        for ( auto entry : pointRequests )
        {
            requestPointIDs.insert( entry.pointId );
        }
    
        // Now search our PointValueMap of current data for any pointIDs that aren't present in the requestPointIDs set
        //  and remove those entries.

        for ( auto it = pointValues.begin(); it != pointValues.end(); )
        {
            if ( ! requestPointIDs.count( it->first ) )
            {
                CTILOG_INFO( dout, "IVVC: Removing invalid point request data with ID: " << it->first );

                it = pointValues.erase( it );
            }
            else
            {
                ++it;
            }
        }
    }

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

    std::set<long> reportedIds;

    // for each feeder
    for ( CtiCCFeederPtr currentFeeder : subbus->getCCFeeders() )
    {
        // for each capbank
        for ( CtiCCCapBankPtr currentBank : currentFeeder->getCCCapBanks() )
        {
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

            if ( currentBank->isSwitched() &&
                 ! currentBank->getDisableFlag() &&
                 ! currentBank->getLocalControlFlag() &&
                 ! currentBank->getIgnoreFlag() &&
                 (isCapBankOpen || isCapBankClosed) &&
                 (deltas.find(currentBank->getPointIdByAttribute( Attribute::Voltage)) != deltas.end()))
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
        if ( operateBank(operatePaoId, subbus, dispatchConnection, strategy) )
        {
            state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
        }
        else
        {
            state->setState(IVVCState::IVVC_WAIT);
        }
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
                bool disableTapOverride = false;

                PointValueMap zonePointValues;      // point values for stuff assigned to the current zone ID

                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                long voltagePointId = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

                PointValueMap::const_iterator found = pointValues.find( voltagePointId );   // lookup
                if ( found != pointValues.end() )
                {
                    zonePointValues.insert( *found );
                }

                // Capbanks

                for each ( const Zone::IdSet::value_type & capBankId in zone->getBankIds() )
                {
                    if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( capBankId ) )
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
                    else
                    {
                        // Our Vte calculation is going to be messed up because we are missing the banks monitor
                        //  points, so we disallow a tap operation in this particular zone.

                        disableTapOverride = true;

                        CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << capBankId
                                                << ". Possible BusStore reset in progress." );
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

                state->_tapOps[ regulator->getPaoId() ] = disableTapOverride
                    ? 0
                    : calculateVte(zonePointValues, strategy, subbus->getAllMonitorPoints(), isPeakTime, regulator);
            }
        }
        catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
        {
            CTILOG_EXCEPTION_ERROR(dout, noRegulator);
        }
        catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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

    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        // regulators

        for each ( const Zone::PhaseIdMap::value_type & mapping in zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                if ( regulator->getOperatingMode() == VoltageRegulator::RemoteMode )
                {
                    regulator->executeDisableRemoteControl( Cti::CapControl::SystemUser );
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }

        // capbanks

        for ( const long bankID : zone->getBankIds() )
        {
            if ( auto bank = store->findCapBankByPAObjectID( bankID ) )
            {
                bank->executeStopHeartbeat( Cti::CapControl::SystemUser );
            }
            else
            {
                // Not really a big deal here, the CBC will eventually fall out of SCADA override mode when
                //  the internal timer expires, rahter than exiting the mode immediately.

                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << bankID
                                        << ". Possible BusStore reset in progress." );
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
                Cti::CapControl::SystemUser) );

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
                    Cti::CapControl::SystemUser) );

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
                    Cti::CapControl::SystemUser) );
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

    ActiveMQConnectionManager::enqueueMessage( 
            OutboundQueue::IvvcAnalysisMessage, 
            std::unique_ptr<StreamableMessage>(message) );
}

void IVVCAlgorithm::updateCommsState( const long busCommsPointId, const bool isCommsLost ) const
{
    if (busCommsPointId > 0)
    {
        DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();

        dispatchConnection->WriteConnQue(
            new CtiPointDataMsg(busCommsPointId, isCommsLost ? 1.0 : 0.0), CALLSITE); // NormalQuality, StatusPointType
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
            else
            {
                // Not sure what to do here if we fail to find a capbank, at a minimum log it.

                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << ID
                                        << ". Possible BusStore reset in progress." );
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

            const long  pointID = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

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
                // tapping down so this should be negative and is Inclusive because we want to make sure we change
                //  the voltage by at least the requested amount.

                realVoltageChange[ phase ]  =
                    solution[ regulatorID ] =
                        regulator->requestVoltageChange( -theMaxVoltage, Cti::CapControl::VoltageRegulator::Inclusive );
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
            else
            {
                // Not sure what to do here if we fail to find a capbank, at a minimum log it.

                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << ID
                                        << ". Possible BusStore reset in progress." );
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

            const long  pointID = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

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
    catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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
IVVCAlgorithm::ValidityCheckResults IVVCAlgorithm::hasValidData( PointDataRequestPtr& request,
                                                                 const CtiTime & timeNow,
                                                                 const CtiCCSubstationBusPtr subbus,
                                                                 const IVVCStrategy & strategy )
{
    bool dataIsValid = true;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );
    PointValueMap pointValues = request->getPointValues();

    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        CTILOG_DEBUG(dout, "IVVC Algorithm: Analyzing Point Data Request Results.");
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

    for( const auto & ZoneId : subbusZoneIds )
    {
        if( strategy.getReportCommStatisticsByPhase() )
        {
            if( ! processZoneByPhase( request, timeNow, subbus, strategy, ZoneId, dataIsValid ) )
            {
                return ValidityCheckResults::MissingObject;
            }
        }
        else
        {
            if( ! processZoneByAggregate( request, timeNow, subbus, strategy, ZoneId, dataIsValid ) )
            {
                return ValidityCheckResults::MissingObject;
            }
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

    for ( const auto busVarPointId : subbus->getCurrentVarLoadPoints() )
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

    if ( strategy.getMethodType() == ControlStrategy::BusOptimizedFeeder )
    {
        for ( auto feeder : subbus->getCCFeeders() )
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

            for ( const auto feederVarPoint : feeder->getCurrentVarLoadPoints() )
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
                                            Scenario_RequiredPointCommsIncomplete,
                                            Scenario_RequiredPointCommsStale,
                                            timeNow,
                                            "BusPower" );

    return dataIsValid
            ? ValidityCheckResults::Valid
            : ValidityCheckResults::Invalid;
}


bool IVVCAlgorithm::processZoneByPhase( PointDataRequestPtr& request,
                                        const CtiTime & timeNow,
                                        const CtiCCSubstationBusPtr subbus,
                                        const IVVCStrategy & strategy,
                                        const long zoneId,
                                        bool & dataIsValid )
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    PointValueMap pointValues = request->getPointValues();

    auto zone = store->getZoneManager().getZone(zoneId);

    const Cti::CapControl::Phase phases[] =
    {
        Cti::CapControl::Phase_A,
        Cti::CapControl::Phase_B,
        Cti::CapControl::Phase_C,
        Cti::CapControl::Phase_Poly
    };

    int totalPoints = 0,
        missingPoints = 0,
        stalePoints = 0;

    for ( const auto phase : phases )
    {
        Zone::PhaseIdMap    regulatorSearch = zone->getRegulatorIds();

        Zone::PhaseIdMap::const_iterator    phase_ID_iterator, phase_ID_end;

        boost::tie( phase_ID_iterator, phase_ID_end ) = regulatorSearch.equal_range( phase );

        totalPoints = missingPoints = stalePoints = 0;      // reset stats

        for ( ; phase_ID_iterator != phase_ID_end; ++phase_ID_iterator )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr  regulator
                    = store->getVoltageRegulatorManager()->getVoltageRegulator( phase_ID_iterator->second );

                const long voltagePointId = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

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
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
            {
                if ( missingAttribute.complain() )
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }

        dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                                totalPoints, missingPoints, stalePoints,
                                                strategy.getRegulatorCommReportingPercentage(),
                                                Scenario_RequiredPointCommsIncomplete,
                                                Scenario_RequiredPointCommsStale,
                                                timeNow,
                                                "Regulator on Phase: " + Cti::CapControl::desolvePhase( phase ) );
        // capbank(s)...

        totalPoints = missingPoints = stalePoints = 0;      // reset stats

        for ( auto & ID : zone->getBankIds() )
        {
            if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( ID ) )
            {
                if ( ! bank->getDisableFlag() )     // only care about enabled banks
                {
                    for ( auto point : bank->getMonitorPoint() )
                    {
                        const long cbcPointID = point->getPointId();

                        if ( cbcPointID > 0 && point->getPhase() == phase )
                        {
                            findPointInRequest( cbcPointID, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
                        }
                    }
                }
            }
            else
            {
                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << ID
                    << ". Possible BusStore reset in progress." );

                // Abort the analysis without contributing to a comms lost condition

                return false; // ValidityCheckResults::MissingObject to be returned outside
            }
        }

        dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                                totalPoints, missingPoints, stalePoints,
                                                strategy.getCapbankCommReportingPercentage(),
                                                Scenario_RequiredPointCommsIncomplete,
                                                Scenario_RequiredPointCommsStale,
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
                                                strategy.getVoltageMonitorCommReportingPercentage(),
                                                Scenario_RequiredPointCommsIncomplete,
                                                Scenario_RequiredPointCommsStale,
                                                timeNow,
                                                "Other(s) on Phase: " + Cti::CapControl::desolvePhase( phase ) );
    }
    return true;
}


bool IVVCAlgorithm::processZoneByAggregate( PointDataRequestPtr& request,
                                            const CtiTime & timeNow,
                                            const CtiCCSubstationBusPtr subbus,
                                            const IVVCStrategy & strategy,
                                            const long zoneId,
                                            bool & dataIsValid )
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    PointValueMap pointValues = request->getPointValues();

    auto zone = store->getZoneManager().getZone(zoneId);

    int totalPoints = 0,
        missingPoints = 0,
        stalePoints = 0;

    Zone::PhaseIdMap    regulatorSearch = zone->getRegulatorIds();

    regulatorSearch.erase(Cti::CapControl::Phase_Unknown);

    for ( const auto regulatorPair : regulatorSearch )
    {
        try
        {
            VoltageRegulatorManager::SharedPtr  regulator
                = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorPair.second );

            const long voltagePointId = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

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
        catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
        {
            if ( missingAttribute.complain() )
            {
                CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
            }
        }
    }

    dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                            totalPoints, missingPoints, stalePoints,
                                            strategy.getRegulatorCommReportingPercentage(),
                                            Scenario_RequiredPointCommsIncomplete,
                                            Scenario_RequiredPointCommsStale,
                                            timeNow,
                                            "Regulator" );

    // capbank(s)...

    totalPoints = missingPoints = stalePoints = 0;      // reset stats

    for ( const auto ID : zone->getBankIds() )
    {
        if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( ID ) )
        {
            if ( ! bank->getDisableFlag() )     // only care about enabled banks
            {
                for (const auto point : bank->getMonitorPoint() )
                {
                    const long cbcPointID = point->getPointId();

                    if ( cbcPointID > 0 && point->getPhase() != Cti::CapControl::Phase_Unknown )
                    {
                        findPointInRequest( cbcPointID, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
                    }
                }
            }
        }
        else
        {
            CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << ID
                << ". Possible BusStore reset in progress." );

            // Abort the analysis without contributing to a comms lost condition

            return false; // ValidityCheckResults::MissingObject to be returned outside
        }
    }

    dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                            totalPoints, missingPoints, stalePoints,
                                            strategy.getCapbankCommReportingPercentage(),
                                            Scenario_RequiredPointCommsIncomplete,
                                            Scenario_RequiredPointCommsStale,
                                            timeNow,
                                            "CBC(s)" );

    // voltage monitor point(s)...

    totalPoints = missingPoints = stalePoints = 0;      // reset stats

    Zone::PhaseToVoltagePointIds    voltageMonitorSearch = zone->getPointIds();

    voltageMonitorSearch.erase( Cti::CapControl::Phase_Unknown );

    for ( const auto voltageMonitorPair : voltageMonitorSearch )
    {
        const long voltageMonitorPointID = voltageMonitorPair.second;

        if ( voltageMonitorPointID > 0 )
        {
            findPointInRequest( voltageMonitorPointID, pointValues, request, timeNow, totalPoints, missingPoints, stalePoints );
        }
    }

    dataIsValid &= analysePointRequestData( subbus->getPaoId(),
                                            totalPoints, missingPoints, stalePoints,
                                            strategy.getVoltageMonitorCommReportingPercentage(),
                                            Scenario_RequiredPointCommsIncomplete,
                                            Scenario_RequiredPointCommsStale,
                                            timeNow,
                                            "Other(s)" );

    return true;
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

    if ( request->isPointStale( pointID, timeNow - ( _POINT_AGE * 60 ) ) )
    {
        ++stalePoints;
    }
}


bool IVVCAlgorithm::analysePointRequestData( const long subbusID, const int totalPoints, const int missingPoints, 
                                             const int stalePoints, const double minimum, 
                                             const IVVCAnalysisScenarios & incompleteScenario,
                                             const IVVCAnalysisScenarios & staleScenario,
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


long GetDmvTestExecutionID( Cti::Database::DatabaseConnection & connection )
{
    static long executionID = -1;

    if ( executionID < 0 )
    {
        static const std::string sql =
            "SELECT "
                "COALESCE(MAX(ExecutionId) + 1, 0) AS ExecutionId "
            "FROM "
                "DmvTestExecution";

        Cti::Database::DatabaseReader    reader( connection, sql );

        reader.execute();

        if ( reader() )
        {
            reader[ "ExecutionId" ] >> executionID;
        }
    }

    return executionID++;
}

void updateDmvTestStatus( const long            executionID,
                          const long            testID,
                          const CtiTime         stopTime,
                          const std::string &   status )
{
    static const std::string sql =
        "UPDATE "
            "DmvTestExecution "
        "SET "
            "StopTime = ?, "
            "TestStatus = ? "
        "WHERE "
            "ExecutionId = ? "
            "AND DmvTestId = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       updater( connection, sql );

    updater
        << stopTime
        << status
        << executionID
        << testID
            ;

    Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug::Enable );
}


bool completionCheck( PointDataRequestPtr   request,
                      PointRequestType      requestType,
                      const double          thresholdPercentage )
{
    static const std::map<PointRequestType, std::string>    description
    {
        {   RegulatorRequestType,   "Regulator voltage"     },
        {   CbcRequestType,         "CBC voltage"           },
        {   OtherRequestType,       "additional voltage"    },
        {   BusPowerRequestType,    "bus telemetry"         }
    };

    if ( ! request->hasRequestType( requestType ) )
    {
        CTILOG_INFO( dout, "No " << description.at( requestType ) << " data is monitored in the point request." );

        return true;
    }
    
    const double complete = 100.0 * request->ratioComplete( requestType );

    if ( complete < thresholdPercentage )
    {
        CTILOG_ERROR( dout, "Incomplete Data: Have " << complete << "% of " << description.at( requestType ) << " data." );

        return false;
    }

    CtiTime   staleTime { CtiTime::now() - ( _POINT_AGE * 60 ) };

    auto collection = request->getPointValues( requestType );

    const auto staleCount = 
        boost::count_if( collection,
                            [ staleTime ]( const auto & entry )
                            {
                                return entry.second.timestamp <= staleTime;
                            } );

    const double nonStale = 100.0 * ( collection.size() - staleCount ) / collection.size();

    if ( nonStale < thresholdPercentage )
    {
        CTILOG_ERROR( dout, "Stale Data: Have " << nonStale << "% of " << description.at( requestType ) << " data." );

        return false;
    }

    CTILOG_INFO( dout, "Complete Data: Have " << complete << "% of " << description.at( requestType ) << " data." );

    return true;
}


bool processDmvScanData( IVVCStatePtr           state,
                         const std::string &    busName )
{
    bool    allGood = true;

    auto & dmvTestSettings = state->getDmvTestData();

    PointDataRequestPtr request = state->getGroupRequest();

    CTILOG_INFO( dout, "Processing Data: DMV Test '" << dmvTestSettings.TestName
                        << "' on bus: " << busName
                        << ". Minimum complete data percentage is "
                        << dmvTestSettings.CommSuccessPercentage << "%." );

    allGood &= completionCheck( request, RegulatorRequestType, dmvTestSettings.CommSuccessPercentage );
    allGood &= completionCheck( request, CbcRequestType,       dmvTestSettings.CommSuccessPercentage );
    allGood &= completionCheck( request, OtherRequestType,     dmvTestSettings.CommSuccessPercentage );
    allGood &= completionCheck( request, BusPowerRequestType,  dmvTestSettings.CommSuccessPercentage );

    auto dataToRecord = request->getPointValues();
    {
        // glue in the tap position data

        auto tapPositionData = getAllRegulatorsTapPositions( state->getPaoId() );

        dataToRecord.insert( tapPositionData.begin(), tapPositionData.end() );
    }

    // record the point data in the database
    {
        Cti::Database::DatabaseConnection   connection;

        std::string sql =
            "INSERT INTO DmvMeasurementData (ExecutionId, PointId, Timestamp, Quality, Value) "
            "SELECT "
                "? AS ExecutionId, "
                "? AS PointId, "
                "? AS Timestamp, "
                "? AS Quality, "
                "? AS Value ";

        if ( connection.getClientType() == Cti::Database::DatabaseConnection::ClientType::Oracle )
        {
            sql +=
            "FROM dual ";
        }

        sql +=
            "WHERE ? NOT IN ( "
                "SELECT D.TimeStamp "
                "FROM DmvMeasurementData D "
                "WHERE ? = D.PointId "
                "AND ? = D.ExecutionId "
            ")";

        Cti::Database::DatabaseWriter   writer( connection, sql );

        for ( auto [pointid, pointdata] : dataToRecord )
        {
            writer
                << dmvTestSettings.ExecutionID
                << pointid
                << pointdata.timestamp
                << pointdata.quality
                << pointdata.value
                << pointdata.timestamp
                << pointid
                << dmvTestSettings.ExecutionID
                ;

            try
            {
                Cti::Database::executeWriter( writer, CALLSITE, Cti::Database::LogDebug::Enable );
            }
            catch ( ... )
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
            }
        }
    }

    if ( ! allGood )
    {
        state->setCommsRetryCount( state->getCommsRetryCount() + 1 );

        if ( state->getCommsRetryCount() <= _IVVC_COMMS_RETRY_COUNT )
        {
            CTILOG_INFO( dout, "Incomplete Scan Data: DMV Test '" << dmvTestSettings.TestName
                                << "' on bus: " << busName
                                << ". Preparing scan retry "
                                << state->getCommsRetryCount() << " of " << _IVVC_COMMS_RETRY_COUNT );
        }

        return state->getCommsRetryCount() <= _IVVC_COMMS_RETRY_COUNT;
    }

    state->setCommsRetryCount( 0 );
    
    return true;
}

// Verify that the regulators and regulator attributes we need are available
unsigned validateTapOpSolution( const IVVCState::TapOperationZoneMap & tapOp )
{
    unsigned errorCount = 0;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    for ( const auto & operation : tapOp )
    {
        const long   regulatorId       = operation.first;
        const double voltageAdjustment = operation.second;

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
            CTILOG_EXCEPTION_ERROR( dout, noRegulator );
        }
        catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
        {
            errorCount++;
            if ( missingAttribute.complain() )
            {
                CTILOG_EXCEPTION_ERROR( dout, missingAttribute );
            }
        }
    }

    return errorCount;
}


bool IVVCAlgorithm::determineDmvWatchPoints( CtiCCSubstationBusPtr subbus,
                                             bool sendScan,
                                             std::set<PointRequest>& pointRequests,
                                             ControlStrategy::ControlMethodType strategyControlMethod,
                                             std::set<long> & dmvWattVarPointIDs )
{
    bool configurationError = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();

    // Process each zones regulator, CBCs and extra voltage points

    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for ( const Zone::IdSet::value_type & ID : subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        // Regulator(s)

        for ( const Zone::PhaseIdMap::value_type & mapping : zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr  regulator
                    = store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                long voltagePointId = regulator->getPointByAttribute( Attribute::Voltage ).getPointId();

                pointRequests.emplace( voltagePointId, RegulatorRequestType, false );

                if ( sendScan )
                {
                    regulator->executeIntegrityScan( Cti::CapControl::SystemUser );
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
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
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

        for ( const Zone::IdSet::value_type & ID : capbankIds )
        {
            if ( CtiCCCapBankPtr bank = store->findCapBankByPAObjectID( ID ) )
            {   
                if ( ! bank->getDisableFlag() )     // only care about enabled banks
                {
                    for ( CtiCCMonitorPointPtr point : bank->getMonitorPoint() )
                    {
                        if ( point->getPointId() > 0 )
                        {
                            pointRequests.emplace( point->getPointId(), CbcRequestType, false );
                        }
                    }
                    if ( sendScan )
                    {
                        CtiCCExecutorFactory::createExecutor( new ItemCommand( CapControlCommand::SEND_SCAN_2WAY_DEVICE,
                                                                                bank->getControlDeviceId() ) )->execute();
                    }
                }
            }
            else
            {
                // we will be building a point request that won't contain the points for this capbank, which is 
                //  probably OK, it will just exclude this bank from the decision tree, as if it were disabled.

                CTILOG_ERROR( dout, "IVVC Algorithm: Failed to find capbank with ID: " << ID
                                        << ". Possible BusStore reset in progress." );
            }
        }

        // Additional voltage points

        for ( const Zone::PhaseToVoltagePointIds::value_type & mapping : zone->getPointIds() )
        {
            pointRequests.emplace( mapping.second, OtherRequestType, false );
        }
    }

    dmvWattVarPointIDs.clear();

    // We need the bus watt, var points and attached voltage point.

    long busWattPointId = subbus->getCurrentWattLoadPointId();
    if (busWattPointId > 0)
    {
        pointRequests.emplace( busWattPointId, BusPowerRequestType, false );
        dmvWattVarPointIDs.insert( busWattPointId );
    }
    else
    {
        configurationError = true;

        if (!subbus->getDisableFlag())
        {
            CTILOG_ERROR(dout, "IVVC Configuration Error: Missing Watt Point on Bus: " << subbus->getPaoName());
        }
    }

    for ( long ID : subbus->getCurrentVarLoadPoints() )
    {
        if (ID > 0)
        {
            pointRequests.emplace( ID, BusPowerRequestType, false );
            dmvWattVarPointIDs.insert( ID );
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

    // get the bus voltage point
    //      a missing voltage point is not an error

    long busVoltagePointId = subbus->getCurrentVoltLoadPointId();
    if ( busVoltagePointId > 0 )
    {
        pointRequests.emplace( busVoltagePointId, BusPowerRequestType, false );
    }

    // We need the watt, var and voltage points for each feeder on the bus
    //  -- iff control method is bus optimized

    if ( strategyControlMethod == ControlStrategy::BusOptimizedFeeder )
    {
        for ( CtiCCFeederPtr feeder : subbus->getCCFeeders() )
        {
            // watt point
            long wattPoint = feeder->getCurrentWattLoadPointId();

            if (wattPoint > 0)
            {
                pointRequests.emplace( wattPoint, BusPowerRequestType, false );
                dmvWattVarPointIDs.insert( wattPoint );
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
            for ( long varPoint : feeder->getCurrentVarLoadPoints() )
            {
                if (varPoint > 0)
                {
                    pointRequests.emplace( varPoint, BusPowerRequestType, false );
                    dmvWattVarPointIDs.insert( varPoint );
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

            // get the feeder voltage point
            //      a missing voltage point is not an error

            long feederVoltagePointId = feeder->getCurrentVoltLoadPointId();
            if ( feederVoltagePointId > 0 )
            {
                pointRequests.emplace( feederVoltagePointId, BusPowerRequestType, false );
            }
        }
    }

    return ( ! configurationError );
}

PointValueMap getAllRegulatorsTapPositions( const long subbusId )
{
    PointValueMap   info;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    ZoneManager & zoneManager       = store->getZoneManager();

    Zone::IdSet subbusZoneIds   = zoneManager.getZoneIdsBySubbus(subbusId);

    for ( const Zone::IdSet::value_type & ID : subbusZoneIds )
    {
        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        for ( const Zone::PhaseIdMap::value_type & mapping : zone->getRegulatorIds() )
        {
            try
            {
                VoltageRegulatorManager::SharedPtr regulator =
                        store->getVoltageRegulatorManager()->getVoltageRegulator( mapping.second );

                long tapPositionPointID = regulator->getPointByAttribute( Attribute::TapPosition ).getPointId();

                if ( tapPositionPointID > 0 )
                {
                    info[ tapPositionPointID ] = regulator->getCompleteTapPosition();
                }
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
            catch ( const Cti::CapControl::MissingAttribute & missingAttribute )
            {
                if (missingAttribute.complain())
                {
                    CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
                }
            }
        }
    }

    return info;
}

bool IVVCAlgorithm::regulatorsReadyForDmvTest( IVVCStatePtr state, CtiCCSubstationBusPtr subbus )
{
    if ( allRegulatorsInRemoteMode( subbus->getPaoId() ) )
    {
        return true;
    }

    auto & dmvTestSettings = state->getDmvTestData();

    CTILOG_ERROR( dout, "DMV Test '" << dmvTestSettings.TestName
                        << "' cannot execute on bus: " << subbus->getPaoName()
                        << ". One or more regulators is in 'Auto' mode." );

    state->dmvTestStatusMessage = "Invalid Regulator mode";
    state->dmvRegulatorInAutoMode = true;
    state->setState( IVVCState::DMV_TEST_END_TEST );

    return false;
}


bool IVVCAlgorithm::executeBusVerification( IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy * strategy )
{
    CtiTime now;

    CtiMultiMsg_vec pointChanges,
                    pilMessages,
                    capMessages;

    EventLogEntries events;

    CtiCapController::getInstance()->analyzeVerificationBus( subbus,
                                                             now,
                                                             pointChanges,
                                                             events,
                                                             pilMessages,
                                                             capMessages );

    sendPointChangesAndEvents( CtiCapController::getInstance()->getDispatchConnection(),
                               pointChanges,
                               events );

    if ( ! pilMessages.empty() )
    {
        auto multiPilMsg = std::make_unique<CtiMultiMsg>();

        multiPilMsg->setData( pilMessages );

        CtiCapController::getInstance()->getPorterConnection()->WriteConnQue( multiPilMsg.release(), CALLSITE );
    }

    if ( ! capMessages.empty() )
    {
        for ( auto entry : capMessages )
        {
            CtiCCExecutorFactory::createExecutor( entry->replicateMessage() )->execute();
        }

        delete_container( capMessages );
    }

    return true;
}

