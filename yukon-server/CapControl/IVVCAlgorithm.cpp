#include "yukon.h"

#include "IVVCAlgorithm.h"
#include "IVVCStrategy.h"
#include "capcontroller.h"
#include "msg_cmd.h"
#include "LitePoint.h"
#include "AttributeService.h"
#include "ccsubstationbusstore.h"
#include "PointResponse.h"
#include "Exceptions.h"

#include <list>
#include <limits>
#include <algorithm>
#include <cmath>
#include <cstdlib>
#include <sstream>

using Cti::CapControl::PointResponse;

using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorManager;

using Cti::CapControl::ZoneManager;
using Cti::CapControl::Zone;

using Cti::CapControl::PaoIdList;
using Cti::CapControl::PointIdList;

extern ULONG _SCAN_WAIT_EXPIRE;
extern ULONG _POINT_AGE;
extern ULONG _POST_CONTROL_WAIT;
extern ULONG _IVVC_MIN_TAP_PERIOD_MINUTES;
extern ULONG _CC_DEBUG;
extern ULONG _IVVC_KEEPALIVE;
extern ULONG _IVVC_COMMS_RETRY_COUNT;
extern double _IVVC_NONWINDOW_MULTIPLIER;
extern double _IVVC_BANKS_REPORTING_RATIO;
extern bool _IVVC_STATIC_DELTA_VOLTAGES;
extern bool _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS;
extern ULONG _REFUSAL_TIMEOUT;


IVVCAlgorithm::IVVCAlgorithm(const PointDataRequestFactoryPtr& factory)
    : _requestFactory(factory)
{
}

void IVVCAlgorithm::setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory)
{
    _requestFactory = factory;
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

        long regulatorID = zone->getRegulatorId();

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
                CtiLockGuard<CtiLogger> logger_guard(dout);

                dout << CtiTime() << " - IVVC Configuration Error. No Voltage Regulator attached to zone: " << zone->getName() << endl;
            }
        }
    }

    return (missingRegulatorCount == 0);
}


void IVVCAlgorithm::execute(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning)
{
    CtiTime timeNow;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    long subbusId = subbus->getPaoId();

    // Make sure all zones on the subbus have a regulator attached.

    if ( ! checkConfigAllZonesHaveRegulator(state, subbus) )
    {
        state->setShowNoRegulatorAttachedMsg(false);
        state->setState(IVVCState::IVVC_WAIT);

        return;
    }

    state->setShowNoRegulatorAttachedMsg(true);

    // subbus is disabled - reset the algorithm and bail

    if ( subbus->getDisableFlag() )
    {
        if (state->isShowBusDisableMsg())
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);

            dout << CtiTime() << " - IVVC Suspended for bus: " << subbus->getPaoName() << ". The bus is disabled." << endl;
        }
        state->setShowBusDisableMsg(false);
        state->setState(IVVCState::IVVC_WAIT);

        return;
    }

    state->setShowBusDisableMsg(true);

    // subbus is enabled
    // send LTC heartbeat as long as cbcs are communicating
    if ( (_IVVC_KEEPALIVE != 0) &&
         (timeNow > state->getNextHeartbeatTime()) &&
         ! state->isCbcCommsLost() )
    {
        sendKeepAlive(subbus);
        state->setNextHeartbeatTime(timeNow + _IVVC_KEEPALIVE);  // _IVVC_KEEPALIVE is in seconds!
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
            if ( ! determineWatchPoints( subbus, dispatchConnection, shouldScan, pointRequests ) )
            {
                // Configuration Error
                // Disable the bus so we don't try to run again. User Intervention required.
                subbus->setDisableFlag(true);
                subbus->setBusUpdatedFlag(true);

                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Configuration Error: Algorithm cannot execute." << endl;
                dout << CtiTime() << " - Disabling bus: " << subbus->getPaoName() << endl;

                return;
            }

            // Make GroupRequest Here
            PointDataRequestPtr request(_requestFactory->createDispatchPointDataRequest(dispatchConnection));
            request->watchPoints(pointRequests);

            //save away this request for later.
            state->setGroupRequest(request);

            if (state->isScannedRequest())
            {
                //We will be waiting for some scan responses in this request.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: Scanned Group Request made in IVVC State" << endl;
                }
            }
            else
            {
                //Regular request. Dispatch will send all current values to us.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: Group Request made in IVVC State" << endl;
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Analysis Interval: Control attempt." << endl;
            }

            //set next control time.
            state->setNextControlTime(timeNow + strategy->getControlInterval());

            PointDataRequestPtr request = state->getGroupRequest();

            //Check for stale data.
            IVVCState::CommsStatus  commsStatuses = checkForStaleData( request, timeNow );

            if ( commsStatuses.cbcsLost || commsStatuses.regulatorsLost || commsStatuses.voltagesLost )     // something is stale
            {
                //Not starting a new scan here. There should be retrys happening already.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: Analysis Interval: Stale Data." << endl;
                    request->reportStatusToLog();
                }

                if ( commsStatuses.voltagesLost )   // don't analyse but also don't go comms lost.
                {
                    state->setVoltageCommsRetryCount( state->getVoltageCommsRetryCount() + 1 );
                    if ( state->getVoltageCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT )
                    {
                        state->setState(IVVCState::IVVC_WAIT);
                        state->setVoltageCommsRetryCount( 0 );

                        if ( ! state->isVoltageCommsLost() )
                        {
                            state->setVoltageCommsLost( true );
                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - IVVC Algorithm: Analysis Interval: Missing one or more additional voltage points." << endl;
                            }
                        }
                    }
                }
                else
                {
                    state->setVoltageCommsRetryCount( 0 );
                    state->setVoltageCommsLost( false );
                }

                if ( commsStatuses.regulatorsLost ) // don't analyse but also don't go comms lost.
                {
                    state->setRegulatorCommsRetryCount( state->getRegulatorCommsRetryCount() + 1 );
                    if ( state->getRegulatorCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT )
                    {
                        state->setState(IVVCState::IVVC_WAIT);
                        state->setRegulatorCommsRetryCount( 0 );

                        if ( ! state->isRegulatorCommsLost() )
                        {
                            state->setRegulatorCommsLost( true );
                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - IVVC Algorithm: Analysis Interval: Missing one or more Voltage Regulator points." << endl;
                            }
                        }
                    }
                }
                else
                {
                    state->setRegulatorCommsRetryCount( 0 );
                    state->setRegulatorCommsLost( false );
                }

                if ( commsStatuses.cbcsLost )
                {
                    state->setCbcCommsRetryCount(state->getCbcCommsRetryCount() + 1);
                    if (state->getCbcCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT)
                    {
                        state->setState(IVVCState::IVVC_WAIT);
                        state->setCbcCommsRetryCount(0);

                        bool showCommsLostMsg = false;
                        if ( ! state->isCbcCommsLost() )
                        {
                            showCommsLostMsg = true;
                            state->setCbcCommsLost(true);
                            handleCbcCommsLost( state, subbus );
                        }

                        if ( showCommsLostMsg )
                        {
                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - IVVC Algorithm: Analysis Interval: Retried comms " << _IVVC_COMMS_RETRY_COUNT << " time(s). Setting Comms lost." << endl;
                            }
                        }

                        request->reportStatusToLog();
                    }
                }
                break;
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: Analysis Interval: Data OK, moving on to Analyze." << endl;
                }
                request->reportStatusToLog();
                state->setState(IVVCState::IVVC_ANALYZE_DATA);

                // clear various comms lost flags and counters

                state->setVoltageCommsRetryCount( 0 );
                state->setVoltageCommsLost( false );

                state->setRegulatorCommsRetryCount( 0 );
                state->setRegulatorCommsLost( false );

                state->setCbcCommsRetryCount(0);

                if ( state->isCbcCommsLost() )
                {
                    state->setCbcCommsLost(false);     // Write to the event log...

                    LONG stationId, areaId, spAreaId;
                    store->getSubBusParentInfo(subbus, spAreaId, areaId, stationId);

                    CtiMultiMsg* ccEvents = new CtiMultiMsg();
                    ccEvents->insert(
                        new CtiCCEventLogMsg(
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

                    sendEvents(dispatchConnection, ccEvents);
                }
            }
        }
        case IVVCState::IVVC_ANALYZE_DATA:
        {
            // If a voltage regulator is in 'Auto' mode we don't want to run the analysis.
            if ( ! allRegulatorsInRemoteMode(subbusId) )
            {
                if ( state->isShowRegulatorAutoModeMsg() )
                {
                    state->setShowRegulatorAutoModeMsg(false);

                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Analysis Suspended for bus: " << subbus->getPaoName()
                         << ". One or more Voltage Regulators are in 'Auto' mode." << endl;
                }
                return;
            }
            else
            {
                if ( ! state->isShowRegulatorAutoModeMsg() )
                {
                    state->setShowRegulatorAutoModeMsg(true);
        
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Analysis Resuming for bus: " << subbus->getPaoName() << endl;
                }
            }

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

                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Controlled bank with ID: " << bankId << " not found." << endl;
                break;
            }

            bool isControlled = false;

            if( bank->getControlStatus() == CtiCCCapBank::OpenPending ||
                bank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                isControlled = subbus->isAlreadyControlled();
            }
            else
            {
                state->setState(IVVCState::IVVC_WAIT);

                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Controlled bank: " << bank->getPaoName()  << " not in pending state.  Resetting IVVC Control loop." << endl;

                return;
            }

            //Are we passed Max confirmtime?

            CtiTime now;
            if (isControlled || (now > ( state->getTimeStamp() + strategy->getMaxConfirmTime() )))
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: isAlreadyControlled() true." << endl;
                }

                CtiMultiMsg_vec pointChanges;
                CtiMultiMsg* ccEventMsg = new CtiMultiMsg();
                CtiMultiMsg_vec &ccEvents = ccEventMsg->getData();

                //Update Control Status
                subbus->capBankControlStatusUpdate(pointChanges,ccEvents);
                sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEventMsg);
                subbus->setBusUpdatedFlag(true);
                state->setTimeStamp(now);

                if (isControlled)
                {
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - IVVC Algorithm: Post control wait." << endl;
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
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - IVVC Algorithm: Var Check failed." << endl;
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Post control wait. Creating Post Scan " << endl;
            }

            PointDataRequestPtr request(_requestFactory->createDispatchPointDataRequest(dispatchConnection));

            std::set<PointRequest> pointRequests;

            if ( ! determineWatchPoints( subbus, dispatchConnection, allowScanning, pointRequests ) )
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

            //We only check to make sure enough banks are reporting here. The
            if ((request->isComplete()))
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: Post Scan Complete. " << endl;
                }

                std::set<long> reportedControllers = state->getReportedControllers();

                if ( ! _IVVC_STATIC_DELTA_VOLTAGES )    // if we're not using static deltas - update them
                {
                    subbus->updatePointResponseDeltas(reportedControllers);
                }

                state->setState(IVVCState::IVVC_WAIT);
            }
            else if ((scanTime + (_SCAN_WAIT_EXPIRE/*minutes*/*60)) < now)
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Algorithm: Post Scan Timed out. " << endl;
                }
                //scan timed out.
                state->setState(IVVCState::IVVC_WAIT);
            }

            break;//never fall through past this
        }
        case IVVCState::IVVC_OPERATE_TAP:
        {
            // This state just sends TAP up/down messages

            // 5 second delay between consecutive tap ops on same regulator
            if ( ( state->_tapOpDelay + 5 ) < timeNow )
            {
                state->_tapOpDelay = timeNow;

                for each ( const IVVCState::TapOperationZoneMap::value_type & operation in state->_tapOps )
                {
                    long zoneID     = operation.first;
                    int  tapOpCount = operation.second;

                    if ( tapOpCount < 0 )
                    {
                        ZoneManager & zoneManager = store->getZoneManager();
                        long regulatorId = zoneManager.getZone(zoneID)->getRegulatorId();

                        if ( regulatorId > 0 )
                        {
                            CtiCCExecutorFactory::createExecutor(
                                new CtiCCCommand( CtiCCCommand::VOLTAGE_REGULATOR_TAP_POSITION_LOWER, regulatorId ) )->execute();

                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);

                                dout << CtiTime() << " - IVVC Algorithm: Lowering Tap on Voltage Regulator with ID: " << regulatorId << endl;
                            }
                        }
                        state->_tapOps[zoneID]++;
                    }
                    else if ( tapOpCount > 0 )
                    {
                        ZoneManager & zoneManager = store->getZoneManager();
                        long regulatorId = zoneManager.getZone(zoneID)->getRegulatorId();

                        if ( regulatorId > 0 )
                        {
                            CtiCCExecutorFactory::createExecutor(
                                new CtiCCCommand( CtiCCCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE, regulatorId ) )->execute();

                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);

                                dout << CtiTime() << " - IVVC Algorithm: Raising Tap on Voltage Regulator with ID: " << regulatorId << endl;
                            }
                        }
                        state->_tapOps[zoneID]--;
                    }
                    else    // tapOpCount == 0
                    {
                        // Nothing to do here
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: default. " << endl;
            }
        }
    }//switch
}//execute


IVVCState::CommsStatus IVVCAlgorithm::checkForStaleData(const PointDataRequestPtr& request, CtiTime timeNow)
{
    //These might be cparms later
    double regulatorRatio = 1.0;
    double otherRatio = 1.0;

    IVVCState::CommsStatus  commsStatuses;

    commsStatuses.cbcsLost       = checkForStaleData( request, timeNow, _IVVC_BANKS_REPORTING_RATIO, CbcRequestType);
    commsStatuses.regulatorsLost = checkForStaleData( request, timeNow, regulatorRatio, RegulatorRequestType);
    commsStatuses.voltagesLost   = checkForStaleData( request, timeNow, otherRatio, OtherRequestType);

    return commsStatuses;
}


bool IVVCAlgorithm::checkForStaleData(const PointDataRequestPtr& request, CtiTime timeNow, double desiredRatio, PointRequestType pointRequestType)
{
    double ratio = request->ratioComplete(pointRequestType);
    if (ratio < desiredRatio)
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - IVVC Algorithm: Incomplete data. Received " << ratio*100 << "%. Minimum was " << desiredRatio*100 << "%, for Request Type: " << pointRequestType << endl;
        }
        return true;
    }
    else
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - IVVC Algorithm: Enough data received for Request Type: " << pointRequestType << endl;
        }

        PointValueMap valueMap = request->getPointValues(pointRequestType);
        int currentData = 0;
        set<long> stalePoints;


        for each (const PointValueMap::value_type& pv in valueMap)
        {
            if (pv.second.timestamp > (timeNow - (_POINT_AGE*60)))
            {
                ++currentData;
            }
            else
            {
                stalePoints.insert(pv.first);
            }
        }

        ratio = currentData/(double)valueMap.size();
        if (ratio < desiredRatio)
        {
            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Stale data. Received updates for " << ratio*100 << "%. Minimum was " << desiredRatio*100 << "%, for Request Type: " << pointRequestType << endl;
            }
            return true;
        }
        else
        {
            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Data Current for Request Type: " << pointRequestType << endl;
            }

            //Removing stale points from the request.
            for each (long pointId in stalePoints)
            {
                request->removePointValue(pointId);
            }

        }
    }

    return false;
}

//sendScan must be false for unit tests.
bool IVVCAlgorithm::determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<PointRequest>& pointRequests)
{
    bool configurationError = false;

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();
    boost::shared_ptr<Cti::CapControl::VoltageRegulatorManager> regulatorManager = store->getVoltageRegulatorManager();

    try
    {
        // Process each zones regulator, CBCs and extra voltage points

        Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

        for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
        {
            ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

            // Regulator

            VoltageRegulatorManager::SharedPtr regulator = regulatorManager->getVoltageRegulator( zone->getRegulatorId() );

            long voltagePointId = regulator->getPointByAttribute(PointAttribute::Voltage).getPointId();

            pointRequests.insert( PointRequest(voltagePointId, RegulatorRequestType, ! sendScan) );

            if ( sendScan )
            {
                CtiCCExecutorFactory::createExecutor( new CtiCCCommand( CtiCCCommand::VOLTAGE_REGULATOR_INTEGRITY_SCAN,
                                                                        zone->getRegulatorId() ) )->execute();
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
                    CtiCCExecutorFactory::createExecutor( new CtiCCCommand( CtiCCCommand::SCAN_2WAY_DEVICE,
                                                                            bank->getControlDeviceId() ) )->execute();
                }
            }

            // Additional voltage points

            Zone::IdSet additionalVoltagePointIds = zone->getPointIds();

            for each ( const Zone::IdSet::value_type & ID in additionalVoltagePointIds )
            {
                pointRequests.insert( PointRequest(ID, OtherRequestType) );
            }
        }
    }
    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
    {
        configurationError = true;

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - ** " << noRegulator.what() << std::endl;
        }
    }
    catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
    {
        configurationError = true;

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - ** " << missingAttribute.what() << std::endl;
        }
    }

    // Bus and feeder voltage points are now attached to the zones and were loaded then.
    // We still need the bus watt and var points.

    long busWattPointId = subbus->getCurrentWattLoadPointId();
    PointIdList busVarPointIds = subbus->getCurrentVarLoadPoints();

    if (busWattPointId > 0)
    {
        pointRequests.insert( PointRequest(busWattPointId, OtherRequestType) );
    }
    else
    {
        configurationError = true;

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - IVVC Configuration Error: Missing Watt Point on Bus: " << subbus->getPaoName() << std::endl;
        }
    }

    for each (long ID in busVarPointIds)
    {
        if (ID > 0)
        {
            pointRequests.insert( PointRequest(ID, OtherRequestType) );
        }
        else
        {
            configurationError = true;

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Configuration Error: Missing Var Point on Bus: " << subbus->getPaoName() << std::endl;
            }
        }
    }

    return ( ! configurationError );
}


void IVVCAlgorithm::operateBank(long bankId, CtiCCSubstationBusPtr subbus, DispatchConnectionPtr dispatchConnection)
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
            CtiMultiMsg* ccEventMsg = new CtiMultiMsg();
            CtiMultiMsg_vec &ccEvents = ccEventMsg->getData();

            double beforeKvar = subbus->getCurrentVarLoadPointValue();
            double varValueA = subbus->getPhaseAValue();
            double varValueB = subbus->getPhaseBValue();
            double varValueC = subbus->getPhaseCValue();

            CtiRequestMsg* request = NULL;

            if (isCapBankOpen)
            {
                string text = feeder->createTextString(ControlStrategy::IntegratedVoltVarControlUnit,CtiCCCapBank::Close,feeder->getIVControl(),beforeKvar);
                request = feeder->createDecreaseVarRequest(bank,pointChanges,ccEvents,text,beforeKvar,varValueA,varValueB,varValueC);
            }
            else if (isCapBankClosed)
            {
                string text = feeder->createTextString(ControlStrategy::IntegratedVoltVarControlUnit,CtiCCCapBank::Open,feeder->getIVControl(),beforeKvar);
                request = feeder->createIncreaseVarRequest(bank,pointChanges,ccEvents,text,beforeKvar,varValueA,varValueB,varValueC);
            }
            else
            {
                //Check for a retry
            }

            if (request != NULL)
            {
                CtiTime time = request->getMessageTime();
                CtiConnectionPtr pilConn = CtiCapController::getInstance()->getPILConnection();
                pilConn->WriteConnQue(request);

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

                sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEventMsg);
            }
        }
    }
}


void IVVCAlgorithm::sendKeepAlive(CtiCCSubstationBusPtr subbus)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        long regulatorID = zoneManager.getZone(ID)->getRegulatorId();
        if (regulatorID > 0)
        {
            CtiCCExecutorFactory::createExecutor( new CtiCCCommand( CtiCCCommand::VOLTAGE_REGULATOR_KEEP_ALIVE,
                                                                    regulatorID ) )->execute();
        }
    }

    if( _CC_DEBUG & CC_DEBUG_IVVC )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - IVVC Algorithm: Voltage Regulator Keep Alive messages sent on bus: "
                          << subbus->getPaoName() << endl;
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

void IVVCAlgorithm::sendEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg* ccEventMsg)
{
    if ( ccEventMsg->getCount() > 0 )
    {
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(ccEventMsg->replicateMessage());
        delete ccEventMsg;
    }
        //Not calling processCCEventMsgs(). The control loop will end up calling this.
}

void IVVCAlgorithm::sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg* ccEvents)
{
    sendEvents(dispatchConnection,ccEvents);
    sendPointChanges(dispatchConnection,pointChanges);
}


bool IVVCAlgorithm::isVoltageRegulatorInRemoteMode(const long regulatorID) const
{
    double value = -1.0;

    try
    {
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

        VoltageRegulatorManager::SharedPtr regulator =
                store->getVoltageRegulatorManager()->getVoltageRegulator(regulatorID);

        LitePoint point = regulator->getPointByAttribute(PointAttribute::AutoRemoteControl);

        regulator->getPointValue( point.getPointId(), value );
    }
    catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - ** " << noRegulator.what() << std::endl;
    }
    catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - ** " << missingAttribute.what() << std::endl;
    }

    return (value != 0.0);  // Remote Mode
}


/**
 * @return bool :   true    - break out of the state machine.
 *                  false   - fall through to the next case.
 */
bool IVVCAlgorithm::busAnalysisState(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, DispatchConnectionPtr dispatchConnection)
{
    std::set<long> pointIdsToSkip;
    bool isPeakTime = subbus->getPeakTimeFlag();    // Is it peak time according to the bus.

    PointValueMap pointValues = state->getGroupRequest()->getPointValues();

    //calculate current power factor of the bus

    // Can't get here if these IDs don't exist
    long wattPointID = subbus->getCurrentWattLoadPointId();
    PointValueMap::iterator iter = pointValues.find(wattPointID);
    double wattValue = iter->second.value;
    pointValues.erase(wattPointID);

    double varValue = 0.0;

    PointIdList pointIds = subbus->getCurrentVarLoadPoints();
    for each (long pointId in pointIds)
    {
        iter = pointValues.find(pointId);
        varValue += iter->second.value;
        pointValues.erase(pointId);
    }//At this point we have removed the var and watt points. Only volt points remain.

    double PFBus = subbus->calculatePowerFactor(varValue, wattValue);

    //calculate current flatness of the bus
    double Vf = calculateVf(pointValues);

    //calculate current weight of the bus
    double currentBusWeight = calculateBusWeight(strategy->getVoltWeight(isPeakTime), Vf,
                                                 strategy->getPFWeight(isPeakTime), PFBus,
                                                 (strategy->getTargetPF(isPeakTime) / 100.0) );

    double targetPowerFactorVars = calculateTargetPFVars(strategy->getTargetPF(isPeakTime), wattValue);

    // Log our current measurement set and calculations
    if (_CC_DEBUG & CC_DEBUG_IVVC)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - IVVC Algorithm: Current measurement set." << endl;

        dout << "Voltages [ Point ID : Value ]" << endl;
        for ( PointValueMap::const_iterator b = pointValues.begin(), e = pointValues.end(); b != e; ++b )
        {
            dout << b->first << " : " << b->second.value << endl;
        }
        dout << "Subbus Watts        : " << wattValue << endl;
        dout << "Subbus VARs         : " << varValue << endl;
        dout << "Subbus Flatness     : " << Vf << endl;
        dout << "Subbus Power Factor : " << PFBus << endl;
        dout << "Subbus Weight       : " << currentBusWeight << endl;
        dout << "Target PF VARs      : " << targetPowerFactorVars << endl;
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
                currentBank->setIgnoreFlag(FALSE);
            }

            // if banks operational state isn't switched or if disabled
            // or not in one of the above 4 states we aren't eligible for control.

            if ( !stringCompareIgnoreCase(currentBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                 !currentBank->getLocalControlFlag() &&
                 !currentBank->getDisableFlag() &&
                 !currentBank->getIgnoreFlag() &&
                 (isCapBankOpen || isCapBankClosed) &&
                 (deltas.find(currentBank->getPointIdByAttribute(PointAttribute::CbcVoltage)) != deltas.end()))
            {

                std::vector<PointResponse> responses = currentBank->getPointResponses();

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
                    CtiLockGuard<CtiLogger> logger_guard(dout);

                    dout << CtiTime() << " - IVVC Algorithm: Estimated voltages if Capbank ID# "
                         << currentBank->getPaoId() << " is " << (isCapBankOpen ? "CLOSED" : "OPENED") << endl;

                    dout << "Estimated Voltages [ Point ID : Estimated Value ]" << endl;
                    for each (PointResponse currentResponse in responses)
                    {
                        if (deltas.find(currentResponse.getPointId()) != deltas.end())
                        {
                            dout << currentResponse.getPointId() << " : " << deltas[ currentResponse.getPointId() ].value << endl;
                        }
                    }
                }

                //calculate estimated flatness of the bus if current bank switches state
                state->_estimated[currentBank->getPaoId()].flatness = calculateVf(deltas);

                //calculate the VAR target window
                double varLowLimit   = targetPowerFactorVars - (currentBank->getBankSize() * (strategy->getMinBankOpen(isPeakTime) / 100.0));
                double varUpperLimit = targetPowerFactorVars + (currentBank->getBankSize() * (strategy->getMinBankClose(isPeakTime) / 100.0));

                //calculate estimated power factor of the bus if current bank switches state
                double estVarValue = varValue;
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

                state->_estimated[currentBank->getPaoId()].powerFactor = subbus->calculatePowerFactor(estVarValue, wattValue);

                //calculate estimated weight of the bus if current bank switches state
                state->_estimated[currentBank->getPaoId()].busWeight =
                    calculateBusWeight(strategy->getVoltWeight(isPeakTime), state->_estimated[currentBank->getPaoId()].flatness,
                                       (pfmodifier * strategy->getPFWeight(isPeakTime)), state->_estimated[currentBank->getPaoId()].powerFactor,
                                       (strategy->getTargetPF(isPeakTime) / 100.0));

                // Log our estimated calculations
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);

                    dout << "Estimated Subbus Watts        : " << wattValue << endl;
                    dout << "Estimated Subbus VARs         : " << estVarValue << endl;
                    dout << "Estimated Subbus Flatness     : " << state->_estimated[currentBank->getPaoId()].flatness << endl;
                    dout << "Estimated Subbus Power Factor : " << state->_estimated[currentBank->getPaoId()].powerFactor << endl;
                    dout << "Estimated Subbus Weight       : " << state->_estimated[currentBank->getPaoId()].busWeight << endl;
                }
            }
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: Error Updating PreOpValue for deltas. PointId not found: " << pointValuePair.first << endl;
            }
        }

        state->_estimated[operatePaoId].operated = true;
        state->setControlledBankId(operatePaoId);

        state->_estimated.clear();     // done with this data

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);

            dout << CtiTime() << " - IVVC Algorithm: Operating Capbank ID# " << operatePaoId << endl;
        }

        //send cap bank command
        operateBank(operatePaoId,subbus,dispatchConnection);

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

    if ( ( now.seconds() - state->getLastTapOpTime().seconds() ) <= (_IVVC_MIN_TAP_PERIOD_MINUTES * 60) )
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " IVVC Algorithm: Not Operating Voltage Regulators due to minimum tap period." << endl;
        }

        return;
    }

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool isPeakTime = subbus->getPeakTimeFlag();    // Is it peak time according to the bus.
    long subbusId = subbus->getPaoId();

    // get all zones on the subbus...

    ZoneManager & zoneManager = store->getZoneManager();
    Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus(subbusId);

    state->_tapOps.clear();

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        PointValueMap zonePointValues;      // point values for stuff assigned to the current zone ID

        ZoneManager::SharedPtr  zone = zoneManager.getZone(ID);

        // Get the voltage regulator for this zone

        try
        {
            VoltageRegulatorManager::SharedPtr regulator =
                    store->getVoltageRegulatorManager()->getVoltageRegulator( zone->getRegulatorId() );

            long voltagePointId = regulator->getPointByAttribute(PointAttribute::Voltage).getPointId();

            PointValueMap::const_iterator found = pointValues.find( voltagePointId );   // lookup
            if ( found != pointValues.end() )
            {
                zonePointValues.insert( *found );
            }
        }
        catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);

            dout << CtiTime() << " - ** " << noRegulator.what() << std::endl;
        }
        catch ( const Cti::CapControl::MissingPointAttribute & missingAttribute )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);

            dout << CtiTime() << " - ** " << missingAttribute.what() << std::endl;
        }

        // get our CapBanks for this zone

        // this guy is a helper for the individual bandwidths...
        std::map<long, CtiCCMonitorPointPtr>    _monitorMap;

        for each ( const Zone::IdSet::value_type & capBankId in zone->getBankIds() )
        {
            CtiCCCapBankPtr bank = store->getCapBankByPaoId( capBankId );
            if ( bank )
            {
                for each ( CtiCCMonitorPointPtr point in bank->getMonitorPoint() )
                {
                    PointValueMap::const_iterator iter = pointValues.find( point->getPointId() );   // lookup
                    if ( iter != pointValues.end() )    // found
                    {
                        zonePointValues.insert( *iter );

                        _monitorMap.insert( std::make_pair( point->getPointId(), point ) );
                    }
                }
            }
        }

        // Other voltage points in this zone

        for each ( const Zone::IdSet::value_type & pointId in zone->getPointIds() )
        {
            PointValueMap::const_iterator found = pointValues.find( pointId );  // lookup

            if ( found != pointValues.end() )
            {
                zonePointValues.insert( *found );
            }
        }

        state->_tapOps[ID] = calculateVte(zonePointValues, strategy, _monitorMap, isPeakTime);
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Algorithm: No Operation - one or more voltage regulators in 'Auto' mode." << endl;
            }
        }
    }
    else
    {
        state->setState(IVVCState::IVVC_WAIT);

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - IVVC Algorithm: No Tap Operations needed." << endl;
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
        tapOp[ID] -= tapOp[parentID];
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

    bool remoteMode = true;

    for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
    {
        long regulatorID = zoneManager.getZone(ID)->getRegulatorId();

        if (regulatorID > 0)
        {
            remoteMode &= isVoltageRegulatorInRemoteMode(regulatorID);
        }
    }

    return remoteMode;
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


int IVVCAlgorithm::calculateVte(const PointValueMap &voltages, IVVCStrategy* strategy,
                                const std::map<long, CtiCCMonitorPointPtr> & _monitorMap,
                                const bool isPeakTime)
{
    bool lowerTap = false;
    bool raiseTap = false;
    bool marginTap = true;

    if ( voltages.empty() )
    {
        return 0;
    }

    for ( PointValueMap::const_iterator b = voltages.begin(), e = voltages.end(); b != e; ++b )
    {
        double Vmax = strategy->getUpperVoltLimit(isPeakTime);
        double Vmin = strategy->getLowerVoltLimit(isPeakTime);
        double Vrm  = strategy->getLowerVoltLimit(isPeakTime) + strategy->getVoltageRegulationMargin(isPeakTime);

        if ( _IVVC_INDIVIDUAL_DEVICE_VOLTAGE_TARGETS )
        {
            std::map<long, CtiCCMonitorPointPtr>::const_iterator iter = _monitorMap.find( b->first );

            if ( iter != _monitorMap.end() )    // this is a capbank monitor point - use its bandwidth instead
            {
                Vmax = iter->second->getUpperBandwidth();
                Vmin = iter->second->getLowerBandwidth();
                Vrm  = iter->second->getLowerBandwidth() + strategy->getVoltageRegulationMargin(isPeakTime);
            }
        }

        if ( b->second.value > Vmax ) { lowerTap = true; }
        if ( b->second.value < Vmin ) { raiseTap = true; }
        if ( b->second.value <= Vrm ) { marginTap = false; }
    }

    return (( lowerTap || marginTap ) ? -1 : raiseTap ? 1 : 0);
}


double IVVCAlgorithm::calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor, const double targetPowerFactor)
{
    // convert from [-1.0, 1.0] to [0.0, 2.0]
    const double biasedTargetPF = (targetPowerFactor < 0.0) ? 2.0 + targetPowerFactor : targetPowerFactor;

    const double Pf = std::abs(100.0 * ( biasedTargetPF - powerFactor ) );

    const double a = 1.0;
    const double b = 1.0;
    const double c = 0.0;

    const double voltageWeight     = (Kv * (b * std::pow( Vf, a ) + c));

    const double e = 1.0;
    const double f = 1.0;
    const double g = 0.0;

    const double powerFactorWeight = (Kp * (e * std::pow( Pf, f ) + g));

    return (voltageWeight + powerFactorWeight);
}


void IVVCAlgorithm::handleCbcCommsLost(IVVCStatePtr state, CtiCCSubstationBusPtr subbus)
{
    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();

    // Switch the voltage regulators to auto mode.
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - IVVC Algorithm: Comms lost, sending remote control disable. " << endl;
        }

        ZoneManager & zoneManager = store->getZoneManager();
        Zone::IdSet subbusZoneIds = zoneManager.getZoneIdsBySubbus( subbus->getPaoId() );

        for each ( const Zone::IdSet::value_type & ID in subbusZoneIds )
        {
            long regulatorID = zoneManager.getZone(ID)->getRegulatorId();

            if (regulatorID > 0)
            {
                if ( isVoltageRegulatorInRemoteMode(regulatorID) )
                {
                    CtiCCExecutorFactory::createExecutor( new CtiCCCommand( CtiCCCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE,
                                                                            regulatorID ) )->execute();
                }
            }
        }
    }

    // Write to the event log.
    {
        LONG stationId, areaId, spAreaId;
        store->getSubBusParentInfo(subbus, spAreaId, areaId, stationId);

        CtiMultiMsg* ccEvents = new CtiMultiMsg();
        ccEvents->insert(
            new CtiCCEventLogMsg(
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

            ccEvents->insert(
                new CtiCCEventLogMsg(
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
            ccEvents->insert(
                new CtiCCEventLogMsg(
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

        sendEvents(dispatchConnection, ccEvents);
    }
}


/*
    tapOpCount can be positive or negative so we compute a sum of squares.  The result
    is
        == 0    iff the map has all 0's for tap operations
        != 0    if there exists at least one map element with non-zero tap operations
 */
bool IVVCAlgorithm::hasTapOpsRemaining(const IVVCState::TapOperationZoneMap & tapOp) const
{
    int sum_of_squares = 0;

    for each ( const IVVCState::TapOperationZoneMap::value_type & operation in tapOp )
    {
        int  tapOpCount = operation.second;

        sum_of_squares += ( tapOpCount * tapOpCount );
    }

    return ( sum_of_squares != 0 );
}

