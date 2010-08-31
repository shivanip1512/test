#include "yukon.h"

#include "IVVCAlgorithm.h"
#include "IVVCStrategy.h"
#include "capcontroller.h"
#include "msg_cmd.h"
#include "LitePoint.h"
#include "AttributeService.h"
#include "ccsubstationbusstore.h"
#include "PointResponse.h"

#include <list>
#include <limits>
#include <algorithm>
#include <cmath>
#include <cstdlib>

using Cti::CapControl::PointResponse;

extern ULONG _SCAN_WAIT_EXPIRE;
extern ULONG _POINT_AGE;
extern ULONG _POST_CONTROL_WAIT;
extern ULONG _IVVC_MIN_TAP_PERIOD_MINUTES;
extern ULONG _CC_DEBUG;
extern ULONG _IVVC_KEEPALIVE;
extern ULONG _IVVC_COMMS_RETRY_COUNT;
extern double _IVVC_NONWINDOW_MULTIPLIER;
extern double _IVVC_BANKS_REPORTING_RATIO;

IVVCAlgorithm::IVVCAlgorithm(const PointDataRequestFactoryPtr& factory)
    : _requestFactory(factory)
{
}

void IVVCAlgorithm::setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory)
{
    _requestFactory = factory;
}

void IVVCAlgorithm::execute(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning)
{
    CtiTime timeNow;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    LoadTapChangerPtr ltcPtr = store->findLtcById(subbus->getLtcId());

    if ((subbus->getLtcId() == 0) || !ltcPtr)
    {
        if ( state->isShowNoLtcAttachedMsg() )           // show message?
        {
            state->setShowNoLtcAttachedMsg(false); // toggle flag to only show message once.
            state->setState(IVVCState::IVVC_WAIT); // reset algorithm

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Configuration Error. No Ltc attached to subbus: " << subbus->getPaoName() << endl;
        }

        return;
    }

    //This would be better if we had some method of executing a function at a scheduled time.
    //Have the Ltc update its flags
    ltcPtr->updateFlags();

    //Show this message again next time it happens
    state->setShowNoLtcAttachedMsg(true);

    if ( ! subbus->getDisableFlag() )
    {
        //Is it time to send a keep alive?
        if ((_IVVC_KEEPALIVE != 0) && (timeNow > state->getNextHeartbeatTime()) && !state->isCommsLost())
        {
            sendKeepAlive(subbus);
            state->setNextHeartbeatTime(timeNow + _IVVC_KEEPALIVE);  // _IVVC_KEEPALIVE is in seconds!
        }

        //If we reloaded or first run. Make sure we don't have a bank pending. If we do attempt to update it's status
        if (state->isFirstPass())
        {
            state->setFirstPass(false);

            for each (CtiCCCapBankPtr bank in store->getCapBanksByPaoIdAndType(subbus->getPaoId(),SubBus))
            {
                if (bank->getControlStatus() == CtiCCCapBank::OpenPending ||
                    bank->getControlStatus() == CtiCCCapBank::ClosePending)
                {
//                    state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
                    break;
                }
            }
        }

        state->setShowBusDisableMsg(true);

    }
    else
    {
        if (state->isShowBusDisableMsg())
        {
            state->setState(IVVCState::IVVC_WAIT);
            state->setShowBusDisableMsg(false);

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - IVVC Suspended for bus: " << subbus->getPaoName() << ". The bus is disabled." << endl;
        }
        return;
    }

    DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();
    IVVCState::State currentState = state->getState();

    switch (currentState)
    {
        case IVVCState::IVVC_DISABLED:
        {
            bool remainDisabled = false;
            bool remoteMode = isLtcInRemoteMode(subbus->getLtcId());

            if (!remoteMode)
            {
                if (state->isShowLtcAutoModeMsg())// show message?
                {
                    state->setShowLtcAutoModeMsg(false);

                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - IVVC Suspended for bus: " << subbus->getPaoName() << ". LTC with ID: " << subbus->getLtcId() << " is in Auto mode." << endl;
                }
                remainDisabled = true;
            }
            else
            {
                state->setShowLtcAutoModeMsg(true);
            }

            //We are disabled. Check if we should enable.
            if (remainDisabled)
            {
                break;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - IVVC Resuming for bus: " << subbus->getPaoName() << endl;

                //Set state
                state->setShowLtcAutoModeMsg(true);
                state->setShowBusDisableMsg(true);
                state->setState(IVVCState::IVVC_WAIT);
            }
        }
        case IVVCState::IVVC_WAIT:
        {
            if (subbus->getDisableFlag())// bail early
            {
                state->setState(IVVCState::IVVC_DISABLED);
                return;
            }

            bool remoteMode = isLtcInRemoteMode(subbus->getLtcId());

            if (!remoteMode)// If we are in 'Auto' mode we don't want to run the algorithm.
            {
                state->setState(IVVCState::IVVC_DISABLED);
                return;
            }

            // toggle these flags so the log message prints again....
            state->setShowVarCheckMsg(true);
            state->setNextControlTime(CtiTime() + strategy->getControlInterval());

            //Check to make sure the points are setup correctly
            //Make sure we have all the data we need.
            int varPointId = subbus->getCurrentVarLoadPointId();
            int voltPointId = subbus->getCurrentVoltLoadPointId();
            int wattPointId = subbus->getCurrentWattLoadPointId();
            if (varPointId == 0 || voltPointId == 0 || wattPointId == 0)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " IVVC Algorithm cannot execute. Check to make sure the Var, Volt, and Watt points are setup for Subbus: " << subbus->getPaoName() << endl;
                dout << CtiTime() << " Setting " << subbus->getPaoName() << " to disabled." << endl;
                //Disable the bus so we don't try to run again. User Intervention required.
                subbus->setDisableFlag(true);
                state->setState(IVVCState::IVVC_DISABLED);
                subbus->setBusUpdatedFlag(true);
                return;
            }

            //save away start time.
            state->setTimeStamp(timeNow);

            // What points are we wanting?
            bool shouldScan = allowScanning && state->isScannedRequest();
            std::set<PointRequest> pointRequests;
            determineWatchPoints(subbus,dispatchConnection,shouldScan,pointRequests);

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
                    dout << CtiTime() << " IVVC Algorithm: Scanned Group Request made in IVVC State" << endl;
                }
            }
            else
            {
                //Regular request. Dispatch will send all current values to us.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Group Request made in IVVC State" << endl;
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
            if (timeNow > state->getNextControlTime())
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Analysis Interval: Control attempt." << endl;
                }

                //set next control time.
                state->setNextControlTime(timeNow + strategy->getControlInterval());

                PointDataRequestPtr request = state->getGroupRequest();

                //Check for stale data.
                if (checkForStaleData(request,timeNow))
                {
                    //Not starting a new scan here. There should be retrys happening already.
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " IVVC Algorithm: Analysis Interval: Stale Data." << endl;
                        request->reportStatusToLog();
                    }

                    state->setCommsRetryCount(state->getCommsRetryCount() + 1);
                    if (state->getCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT)
                    {
                        state->setCommsRetryCount(0);
                        state->setState(IVVCState::IVVC_COMMS_LOST);
                        request->reportStatusToLog();

                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC Algorithm: Analysis Interval: Retried comms " << _IVVC_COMMS_RETRY_COUNT << " time(s). Setting Comms lost." << endl;
                        }
                    }
                    break;
                }
                else
                {
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " IVVC Algorithm: Analysis Interval: Data ok moving on to Analyze." << endl;
                    }
                    request->reportStatusToLog();
                    state->setState(IVVCState::IVVC_ANALYZE_DATA);
                    state->setCommsLost(false);

                    {   // Write to the event log...
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
            else
            {
                //Not time to control yet.
                break;
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
                //Log Error
                state->setState(IVVCState::IVVC_WAIT);
                break;
            }

            //Are we passed Max confirmtime?
            CtiTime now;
            CtiTime controlTime = state->getTimeStamp();
            long maxConfirmTime = strategy->getMaxConfirmTime();
            bool isControlled = subbus->isAlreadyControlled();
            if (isControlled || (now > (controlTime+maxConfirmTime)))
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: isAlreadyControlled() true." << endl;
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
                        dout << CtiTime() << " IVVC Algorithm: Post control wait." << endl;
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
                        dout << CtiTime() << " IVVC Algorithm: Var Check failed." << endl;
                    }
                }
                break;
            }
        }
        case IVVCState::IVVC_POST_CONTROL_WAIT:
        {
            CtiTime capBankOpTime = state->getTimeStamp();
            CtiTime now;
            if (now > (capBankOpTime + _POST_CONTROL_WAIT/*seconds*/))
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Post control wait. Creating Post Scan " << endl;
                }
                PointDataRequestPtr request(_requestFactory->createDispatchPointDataRequest(dispatchConnection));

                std::set<PointRequest> pointRequests;
                determineWatchPoints(subbus,dispatchConnection,allowScanning,pointRequests);
                request->watchPoints(pointRequests);

                state->setTimeStamp(now);
                state->setGroupRequest(request);
                state->setState(IVVCState::IVVC_POSTSCAN_LOOP);
            }
            else
            {
                break;
            }
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
                    dout << CtiTime() << " IVVC Algorithm: Post Scan Complete. " << endl;
                }

                std::set<long> reportedControllers = state->getReportedControllers();

                subbus->updatePointResponseDeltas(reportedControllers);

                state->setState(IVVCState::IVVC_WAIT);
            }
            else if ((scanTime + (_SCAN_WAIT_EXPIRE/*minutes*/*60)) < now)
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Post Scan Timed out. " << endl;
                }
                //scan timed out.
                state->setState(IVVCState::IVVC_WAIT);
            }

            break;//never fall through past this
        }
        case IVVCState::IVVC_COMMS_LOST:
        {
            //Switch to LTC to automode.
            //save state so we know to re-enable remote when we start up again.
            bool remoteMode = isLtcInRemoteMode(subbus->getLtcId());

            if (remoteMode)
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Comms lost, sending remote control disable. " << endl;
                }
                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_DISABLE,subbus->getLtcId()))->execute();

            }
            state->setState(IVVCState::IVVC_WAIT);
            state->setCommsLost(true);

            {   // Write to the event log...
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

                // capControlIvvcMissingPoint

                std::set<long> missingIds = state->getGroupRequest()->getMissingPoints();
                for each (long ID in missingIds)
                {
                    ccEvents->insert(
                        new CtiCCEventLogMsg(
                                0,
                                SYS_PID_CAPCONTROL,
                                spAreaId,
                                areaId,
                                stationId,
                                subbus->getPaoId(),
                                0,
                                capControlIvvcMissingPoint,
                                0,
                                ID,
                                "IVVC Missing Point Response",
                                "cap control") );
                }

                std::set<long> rejectedIds = state->getGroupRequest()->getRejectedPoints();
                for each (long ID in rejectedIds)
                {
                    ccEvents->insert(
                        new CtiCCEventLogMsg(
                                0,
                                SYS_PID_CAPCONTROL,
                                spAreaId,
                                areaId,
                                stationId,
                                subbus->getPaoId(),
                                0,
                                capControlIvvcRejectedPoint,
                                0,
                                ID,
                                "IVVC Rejected Point Response",
                                "cap control") );
                }

                sendEvents(dispatchConnection, ccEvents);
            }
            break;
        }
        default:
        {
            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " IVVC Algorithm: default. " << endl;
            }
        }
    }//switch
}//execute

bool IVVCAlgorithm::checkForStaleData(const PointDataRequestPtr& request, CtiTime timeNow)
{
    //These might be cparms later
    double ltcRatio = 1.0;
    double otherRatio = 1.0;

    if (checkForStaleData(request,timeNow,ltcRatio,LtcRequestType))
    {
        return true;
    }

    if (checkForStaleData(request,timeNow,otherRatio,OtherRequestType))
    {
        return true;
    }

    if (checkForStaleData(request,timeNow,_IVVC_BANKS_REPORTING_RATIO,CbcRequestType))
    {
        return true;
    }

    return false;
}

bool IVVCAlgorithm::checkForStaleData(const PointDataRequestPtr& request, CtiTime timeNow, double desiredRatio, PointRequestType pointRequestType)
{
    double ratio = request->ratioComplete(pointRequestType);
    if (ratio < desiredRatio)
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " IVVC Algorithm: Incomplete data. Received " << ratio*100 << "%. Minimum was " << desiredRatio*100 << "%, for Request Type: " << pointRequestType << endl;
        }
        return true;
    }
    else
    {
        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " IVVC Algorithm: Enough data received for Request Type: " << pointRequestType << endl;
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
                dout << CtiTime() << " IVVC Algorithm: Stale data. Received updates for " << ratio*100 << "%. Minimum was " << desiredRatio*100 << "%, for Request Type: " << pointRequestType << endl;
            }
            return true;
        }
        else
        {
            if (_CC_DEBUG & CC_DEBUG_IVVC)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " IVVC Algorithm: Data Current for Request Type: " << pointRequestType << endl;
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
void IVVCAlgorithm::determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<PointRequest>& pointRequests)
{
    AttributeService attributeService;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    //The ltc.
    LitePoint point = attributeService.getPointByPaoAndAttribute(subbus->getLtcId(),PointAttribute::LtcVoltage);
    if (point.getPointType() == InvalidPointType)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " LTC Voltage point not found for LTC id: " << subbus->getLtcId() << endl;
    }
    else
    {
        PointRequest pr(point.getPointId(),LtcRequestType);

        if (sendScan == true)
        {
            pr.requestLatestValue = false;
            CtiCCCommand* ltcScan = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY,subbus->getLtcId());
            CtiCCExecutorFactory::createExecutor(ltcScan)->execute();
        }

        pointRequests.insert(pr);
    }

    //SubBus voltage point.
    long pointId = subbus->getCurrentVoltLoadPointId();
    if (pointId > 0)
    {
        //No scan available. Request value from dispatch
        PointRequest pr(pointId,OtherRequestType);
        pointRequests.insert(pr);
    }

    //SubBus watt point to calc power factor.
    pointId = subbus->getCurrentWattLoadPointId();
    if (pointId > 0)
    {
        PointRequest pr(pointId,OtherRequestType);
        pointRequests.insert(pr);
    }

    //SubBus var point(s) to calc power factor.
    list<long> varPointIds = subbus->getCurrentVarLoadPoints();
    for each (long varPointId in varPointIds)
    {
        if (varPointId > 0)
        {
            PointRequest pr(varPointId,OtherRequestType);
            pointRequests.insert(pr);
        }
    }

    //Feeder voltage points.
    CtiFeeder_vec feeders = subbus->getCCFeeders();
    for each (CtiCCFeederPtr feeder in feeders)
    {
        long feederVoltageId = feeder->getCurrentVoltLoadPointId();
        if (feederVoltageId > 0)
        {
            //No scan available. Request values from dispatch
            PointRequest pr(feederVoltageId,OtherRequestType);
            pointRequests.insert(pr);
        }
    }

    //All two way cbc's
    std::vector<CtiCCCapBankPtr> banks = store->getCapBanksByPaoIdAndType(subbus->getPaoId(),SubBus);
    for each (CtiCCCapBankPtr bank in banks)
    {
        int cbcId = bank->getControlDeviceId();
        int voltId = bank->getTwoWayPoints()->getVoltageId();

        PointRequest pr(voltId,CbcRequestType);

        //TODO: Check disabled (failed?) on bank and feeder (and sub?)
        if (sendScan == true)
        {
            pr.requestLatestValue = false;
            CtiCCCommand* cbcScan = new CtiCCCommand(CtiCCCommand::SCAN_2WAY_DEVICE,cbcId);
            CtiCCExecutorFactory::createExecutor(cbcScan)->execute();
        }

        if (voltId > 0)
        {
            pointRequests.insert(pr);
        }
    }
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


int IVVCAlgorithm::calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax)
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
    const long ltcId = subbus->getLtcId();

    if( _CC_DEBUG & CC_DEBUG_IVVC )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - LTC Keep Alive messages sent." << endl;
    }
    CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_KEEP_ALIVE, ltcId))->execute();

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


bool IVVCAlgorithm::isLtcInRemoteMode(const long ltcId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    LoadTapChangerPtr ltc = store->findLtcById(ltcId);

    LitePoint remotePoint = ltc->getAutoRemotePoint();

    double value = -1.0;
    ltc->getPointValue(remotePoint.getPointId(), value);

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

    std::list<long> pointIds = subbus->getCurrentVarLoadPoints();
    for each (long pointId in pointIds)
    {
        iter = pointValues.find(pointId);
        varValue += iter->second.value;
        pointValues.erase(pointId);
    }//At this point we have removed the var and watt points. Only volt points remain.

    double PFBus = subbus->calculatePowerFactor(varValue, wattValue);

    //calculate potential tap operation
    int tapOp = calculateVte(pointValues,
                             strategy->getLowerVoltLimit(isPeakTime),
                             strategy->getLowerVoltLimit(isPeakTime) + strategy->getVoltageRegulationMargin(isPeakTime),
                             strategy->getUpperVoltLimit(isPeakTime));

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

        dout << CtiTime() << " IVVC Algorithm: Current measurement set." << endl;

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

            // if banks operational state isn't switched or if disabled
            // or not in one of the above 4 states we aren't eligible for control.

            if ( !stringCompareIgnoreCase(currentBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                 !currentBank->getLocalControlFlag() &&
                 !currentBank->getDisableFlag() &&
                 (isCapBankOpen || isCapBankClosed) &&
                 (deltas.find(currentBank->getTwoWayPoints()->getVoltageId()) != deltas.end()))
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

                    dout << CtiTime() << " IVVC Algorithm: Estimated voltages if Capbank ID# "
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
         ( currentBusWeight - strategy->getDecisionWeight(isPeakTime) ) > state->_estimated[operatePaoId].busWeight )
    {
        CtiTime now;
        state->setTimeStamp(now);

        // record preoperation voltage values for the feeder our capbank is on
        for each (PointValueMap::value_type pointValuePair in pointValues)
        {
            state->_estimated[operatePaoId].capbank->updatePointResponsePreOpValues(pointValuePair.first,pointValuePair.second.value);
        }

        state->_estimated[operatePaoId].operated = true;
        state->setControlledBankId(operatePaoId);

        state->_estimated.clear();     // done with this data

        if (_CC_DEBUG & CC_DEBUG_IVVC)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);

            dout << CtiTime() << " IVVC Algorithm: Operating Capbank ID# " << operatePaoId << endl;
        }

        //send cap bank command
        operateBank(operatePaoId,subbus,dispatchConnection);

        state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
    }
    else
    {
        state->_estimated.clear();     // done with this data

        // TAP operation - use precalculated value from above

        CtiTime now;

        if ( tapOp != 0 )
        {
            if ( ( now.seconds() - state->getLastTapOpTime().seconds() ) > (_IVVC_MIN_TAP_PERIOD_MINUTES * 60) )
            {
                state->setLastTapOpTime(now);

                if ( isLtcInRemoteMode(subbus->getLtcId()) )    // only move the tap if we are in 'remote' mode
                {
                    if ( tapOp == -1 )
                    {
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);

                            dout << CtiTime() << " IVVC Algorithm: Lowering Tap" << endl;
                        }

                        //  send command to lower tap
                        CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_TAP_POSITION_LOWER,subbus->getLtcId()))->execute();
                    }
                    else
                    {
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);

                            dout << CtiTime() << " IVVC Algorithm: Raising Tap" << endl;
                        }

                        //  send command to raise tap
                        CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_TAP_POSITION_RAISE,subbus->getLtcId()))->execute();
                    }
                }
                else
                {
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " IVVC Algorithm: Not Operating LTC due to remote mode." << endl;
                    }
                }
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Not Operating LTC due to No Op period." << endl;
                }
            }
        }
        else
        {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: No LTC Operation needed." << endl;
                }
        }
        state->setState(IVVCState::IVVC_WAIT);
    }

    return true;
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

