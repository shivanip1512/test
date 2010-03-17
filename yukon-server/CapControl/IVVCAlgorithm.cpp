#include "yukon.h"

#include "IVVCAlgorithm.h"
#include "IVVCStrategy.h"
#include "capcontroller.h"
#include "msg_cmd.h"
#include "LitePoint.h"
#include "AttributeService.h"
#include "ccsubstationbusstore.h"

#include <list>
#include <limits>
#include <algorithm>
#include <cmath>
#include <cstdlib>

extern ULONG _SCAN_WAIT_EXPIRE;
extern ULONG _POINT_AGE;
extern ULONG _POST_CONTROL_WAIT;
extern ULONG _IVVC_MIN_TAP_PERIOD_MINUTES;
extern ULONG _CC_DEBUG;
extern bool _IVVC_ANALYZE_BYPASS;
extern ULONG _IVVC_KEEPALIVE;
extern ULONG _IVVC_COMMS_RETRY_COUNT;


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


    if ( ! isLtcInRemoteMode( subbus->getLtcId() ) )    // If we are in 'Auto' mode we don't want to run the algorithm.
    {
        if ( state->getLtcAutoModeMsg() )           // show message?
        {
            state->setLtcAutoModeMsg(false);        // toggle flag to only show message once.
            state->setState(IVVCState::IVVC_WAIT);  // reset algorithm

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - LTC ID: " << subbus->getLtcId() << " is in Auto mode." << endl;
        }

        return;
    }

    // show message once the next time we enter Auto mode.
    state->setLtcAutoModeMsg(true);


    if ( ! subbus->getDisableFlag() )
    {
        //Is it time to send a keep alive?
        if ((_IVVC_KEEPALIVE != 0) && (timeNow > state->getNextHeartbeatTime()))
        {
            sendKeepAlive(subbus);
            state->setNextHeartbeatTime(timeNow + _IVVC_KEEPALIVE);
        }

        //If we reloaded or first run. Make sure we don't have a bank pending. If we do attempt to update it's status
        if (state->isFirstPass())
        {
            state->setFirstPass(false);
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

            for each (CtiCCCapBankPtr bank in store->getCapBanksByPaoIdAndType(subbus->getPaoId(),SubBus))
            {
                if (bank->getControlStatus() == CtiCCCapBank::OpenPending ||
                    bank->getControlStatus() == CtiCCCapBank::ClosePending)
                {
                    state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
                    break;
                }
            }
        }
    }

    DispatchConnectionPtr dispatchConnection = CtiCapController::getInstance()->getDispatchConnection();
    IVVCState::State currentState = state->getState();

    switch (currentState)
    {
        case IVVCState::IVVC_WAIT:
        {
            if ( subbus->getDisableFlag() )     // bail early
            {
                if ( state->getShowBusDisableMsg() )      // only print once each time through
                {
                    state->setShowBusDisableMsg(false);

                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << subbus->getPaoName() << " is disabled." << endl;
                }

                return;
            }

            // toggle these flags so the log message prints again....
            state->setShowVarCheckMsg(true);
            state->setShowBusDisableMsg(true);

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
                subbus->setBusUpdatedFlag(true);
                return;
            }

            //save away start time.
            state->setTimeStamp(timeNow);

            // What points are we wanting?
            bool shouldScan = allowScanning && state->isScannedRequest();
            std::set<long> requestPoints;
            std::set<long> pointIds;
            determineWatchPoints(subbus,dispatchConnection,shouldScan,pointIds,requestPoints);

            // Make GroupRequest Here
            PointDataRequestPtr request(_requestFactory->createDispatchPointDataRequest(dispatchConnection));
            request->watchPoints(pointIds,requestPoints);

            //save away this request for later.
            state->setGroupRequest(request);

            if (state->isScannedRequest())
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Scanned Group Request made in IVVC State" << endl;
                }
            }
            else
            {
                //Regular request. Ask dispatch for the current points values.
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Group Request made in IVVC State" << endl;
                }

                //We did not Scan for these points. Ask dispatch for them instead.
                CtiCommandMsg* cmdMsg = new CtiCommandMsg();
                cmdMsg->setOperation(CtiCommandMsg::PointDataRequest);

                CtiCommandMsg::CtiOpArgList_t points;
                for each (long pointId in pointIds)
                {
                    points.push_back(pointId);
                }

                cmdMsg->setOpArgList(points);
                dispatchConnection->WriteConnQue(cmdMsg);
            }

            //reset this flag.
            state->setScannedRequest(false);
            //fall through to IVVC_PRESCAN_LOOP (no break)
            state->setState(IVVCState::IVVC_PRESCAN_LOOP);
        }
        case IVVCState::IVVC_PRESCAN_LOOP:
        {
            long controlInterval = strategy->getControlInterval();

            if (controlInterval == 0)
            {//On new data
                PointDataRequestPtr request = state->getGroupRequest();
                if (request->isComplete())
                {
                    if (_CC_DEBUG & CC_DEBUG_IVVC)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " IVVC Algorithm: On New Data: Request Complete" << endl;
                    }
                    //Check for stale data.
                    PointValueMap pointValues = request->getPointValues();
                    bool stale = checkForStaleData(pointValues,timeNow);

                    if (stale == true)
                    {
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC Algorithm: On New Data: Stale data, resetting for scan" << endl;
                        }
                        request->reportStatusToLog();
                        state->setScannedRequest(true);
                        state->setState(IVVCState::IVVC_WAIT);
                        break;
                    }
                    else
                    {
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC Algorithm: On New Data: Data ok, moving on" << endl;
                        }
                        state->setState(IVVCState::IVVC_ANALYZE_DATA);
                    }
                }
                else
                {
                    CtiTime startTime = state->getTimeStamp();

                    if ((startTime + (_SCAN_WAIT_EXPIRE/*minutes*/*60)) < timeNow)
                    {
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC Algorithm: On New Data: Request did not complete." << endl;
                        }
                        if (false)
                        {
                            //Tie in % online here later (if enough, go IVVC_ANALYZE_DATA)
                        }
                        else
                        {
                            state->setCommsRetryCount(state->getCommsRetryCount() + 1);
                            if (state->getCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT)
                            {
                                state->setCommsRetryCount(0);
                                state->setState(IVVCState::IVVC_COMMS_LOST);
                                request->reportStatusToLog();

                                if (_CC_DEBUG & CC_DEBUG_IVVC)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " IVVC Algorithm: On New Data: Setting Comms Lost. " << endl;
                                }
                            }
                            break;
                        }
                    }
                    else
                    {
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC Algorithm: On New Data: Waiting for Request to complete." << endl;
                        }
                    }
                    break;
                }
            }
            else
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

                    // Tie in % online here later
                    if (request->isComplete())
                    {
                        //Check for stale data.
                        PointValueMap pointValues = request->getPointValues();
                        bool stale = checkForStaleData(pointValues,timeNow);

                        if (stale == true)
                        {
                            //To be considered. Initiate scan if stale? like in 'on new data'

                            state->setCommsRetryCount(state->getCommsRetryCount() + 1);
                            if (state->getCommsRetryCount() >= _IVVC_COMMS_RETRY_COUNT)
                            {
                                state->setCommsRetryCount(0);
                                state->setState(IVVCState::IVVC_COMMS_LOST);
                                request->reportStatusToLog();

                                if (_CC_DEBUG & CC_DEBUG_IVVC)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " IVVC Algorithm: Analysis Interval: Stale Data Setting Comms lost." << endl;
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
                            state->setState(IVVCState::IVVC_ANALYZE_DATA);
                        }
                    }
                    else
                    {
                        //scan did not complete in the analysis interval... start over.
                        //Do we flip to auto and kill heartbeat like above?
                        if (_CC_DEBUG & CC_DEBUG_IVVC)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " IVVC Algorithm: Analysis Interval: Scan never completed in time. Resetting to WAIT." << endl;
                        }
                        request->reportStatusToLog();
                        state->setState(IVVCState::IVVC_WAIT);
                        break;
                    }
                }
                else
                {
                    //Not time to control yet.
                    break;
                }
            }
        }
        case IVVCState::IVVC_ANALYZE_DATA:
        {
            //We have good data. Make sure we are in remote mode
            if (state->isRemoteMode() == false)
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Analyze: Setting remote mode." << endl;
                }
                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_ENABLE,subbus->getLtcId()))->execute();
                state->setRemoteMode(true);
            }

            //NOT REAL CODE REMOVE THIS BEFORE Release
            //This is here to bypass the data analysis and pick the first open bank to control.
            if (_IVVC_ANALYZE_BYPASS == true)
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Analyze: operating first bank not controlled. DEBUG MODE." << endl;
                }
                long operatePaoId = 0;

                for each(CtiCCFeederPtr feeder in subbus->getCCFeeders())
                {
                    for each(CtiCCCapBankPtr bank in feeder->getCCCapBanks())
                    {
                        if (bank->getControlStatus() == CtiCCCapBank::Open)
                        {
                            operatePaoId = bank->getPaoId();
                            break;
                        }
                    }

                    if (operatePaoId != 0)
                    {
                        break;
                    }
                }
                state->setControlledBankId(operatePaoId);
                operateBank(operatePaoId,subbus,dispatchConnection);
                state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
                break;
            }
            else
            {
                bool isPeakTime = subbus->getPeakTimeFlag();    // Is it peak time according to the bus.

                PointValueMap pointValues = state->getGroupRequest()->getPointValues();

                //calculate current power factor of the bus

                // Can't get here if these IDs don't exist

                long wattPointID = subbus->getCurrentWattLoadPointId();
                long varPointID = subbus->getCurrentVarLoadPointId();

                PointValueMap::iterator iter = pointValues.find(wattPointID);
                double wattValue = iter->second.value;

                iter = pointValues.find(varPointID);
                double varValue = iter->second.value;

                double PFBus = subbus->calculatePowerFactor(varValue, wattValue);

                //calculate potential tap operation

                int tapOp = calculateVte(pointValues,
                                         strategy->getLowerVoltLimit(isPeakTime),
                                         strategy->getLowerVoltLimit(isPeakTime) + strategy->getVoltageRegulationMargin(isPeakTime),
                                         strategy->getUpperVoltLimit(isPeakTime),
                                         varPointID, wattPointID);

                //calculate current flatness of the bus

                double Vf = calculateVf(pointValues, varPointID, wattPointID);  // need to remove watt and var points

                //calculate current weight of the bus

                double currentBusWeight = calculateBusWeight(strategy->getVoltWeight(isPeakTime), Vf,
                                                             strategy->getPFWeight(isPeakTime), PFBus);

                // Log our current measurement set and calculations
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);

                    dout << CtiTime() << " IVVC Algorithm: Current measurement set." << endl;

                    dout << "Voltages [ Point ID : Value ]" << endl;
                    for ( PointValueMap::const_iterator b = pointValues.begin(), e = pointValues.end(); b != e; ++b )
                    {
                        if ( b->first != varPointID && b->first != wattPointID)     // Ignore the watt and var points.
                        {
                            dout << b->first << " : " << b->second.value << endl;
                        }
                    }
                    dout << "Subbus Watts        : " << wattValue << endl;
                    dout << "Subbus VARs         : " << varValue << endl;
                    dout << "Subbus Flatness     : " << Vf << endl;
                    dout << "Subbus Power Factor : " << PFBus << endl;
                    dout << "Subbus Weight       : " << currentBusWeight << endl;
                }

                //calculate estimated bus weights etc from historical data

                CtiFeeder_vec& feeders = subbus->getCCFeeders();

                // for each feeder

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
                             (isCapBankOpen || isCapBankClosed) )
                        {
                            std::vector <CtiCCPointResponsePtr>& responses = currentBank->getPointResponse();

                            for ( std::vector <CtiCCPointResponsePtr>::iterator prb = responses.begin(), pre = responses.end(); prb != pre; ++prb )
                            {
                                CtiCCPointResponsePtr currentResponse = *prb;

                                deltas[ currentResponse->getPointId() ].value += ( ( isCapBankOpen ? 1.0 : -1.0 ) * currentResponse->getDelta() );
                            }

                            state->_estimated[currentBank->getPaoId()].capbank = currentBank;

                            // Log our estimated voltage changes if bank is operated.
                            if (_CC_DEBUG & CC_DEBUG_IVVC)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);

                                dout << CtiTime() << " IVVC Algorithm: Estimated voltages if Capbank ID# "
                                     << currentBank->getPaoId() << " is " << (isCapBankOpen ? "CLOSED" : "OPENED") << endl;

                                dout << "Estimated Voltages [ Point ID : Estimated Value ]" << endl;
                                for ( std::vector <CtiCCPointResponsePtr>::iterator prb = responses.begin(), pre = responses.end(); prb != pre; ++prb )
                                {
                                    CtiCCPointResponsePtr currentResponse = *prb;

                                    dout << currentResponse->getPointId() << " : " << deltas[ currentResponse->getPointId() ].value << endl;
                                }
                            }

                            //calculate estimated flatness of the bus if current bank switches state

                            state->_estimated[currentBank->getPaoId()].flatness =
                                calculateVf(deltas, varPointID, wattPointID);  // need to remove watt and var points

                            //calculate estimated power factor of the bus if current bank switches state

                            double estVarValue = varValue + ( ( isCapBankOpen ? -1.0 : 1.0 ) * currentBank->getBankSize() );

                            state->_estimated[currentBank->getPaoId()].powerFactor =
                                subbus->calculatePowerFactor(estVarValue, wattValue);

                            //calculate estimated weight of the bus if current bank switches state

                            state->_estimated[currentBank->getPaoId()].busWeight =
                                calculateBusWeight(strategy->getVoltWeight(isPeakTime), state->_estimated[currentBank->getPaoId()].flatness,
                                                   strategy->getPFWeight(isPeakTime), state->_estimated[currentBank->getPaoId()].powerFactor);

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

                    std::vector <CtiCCPointResponsePtr>& responses = state->_estimated[operatePaoId].capbank->getPointResponse();

                    for ( std::vector <CtiCCPointResponsePtr>::iterator prb = responses.begin(), pre = responses.end(); prb != pre; ++prb )
                    {
                        CtiCCPointResponsePtr currentResponse = *prb;

                        currentResponse->setPreOpValue( pointValues[ currentResponse->getPointId() ].value );
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
                    break;
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
                    }
                    state->setState(IVVCState::IVVC_WAIT);
                    break;
                }
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
                CtiMultiMsg_vec ccEvents;

                //Update Control Status
                subbus->capBankControlStatusUpdate(pointChanges,ccEvents);
                sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEvents);
                subbus->setBusUpdatedFlag(true);
                state->setTimeStamp(now);

                if (isControlled)
                {
                    state->setState(IVVCState::IVVC_POST_CONTROL_WAIT);
                }
                else
                {
                    state->setState(IVVCState::IVVC_WAIT);
                }
            }
            else
            {
                if ( state->getShowVarCheckMsg() )      // only print once each time through
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

                std::set<long> pointIds;
                std::set<long> requestPoints;
                determineWatchPoints(subbus,dispatchConnection,allowScanning,pointIds,requestPoints);
                request->watchPoints(pointIds,requestPoints);

                state->setTimeStamp(now);
                state->setGroupRequest(request);
                state->setState(IVVCState::IVVC_POSTSCAN_LOOP);
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Post control wait. Waiting." << endl;
                }
                break;
            }
        }
        case IVVCState::IVVC_POSTSCAN_LOOP:
        {
            PointDataRequestPtr request = state->getGroupRequest();
            CtiTime scanTime = state->getTimeStamp();
            CtiTime now;

            if (request->isComplete())
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Post Scan Complete. " << endl;
                }

                // Record data
                CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
                int bankId = state->getControlledBankId();
                CtiCCCapBankPtr bank = store->getCapBankByPaoId(bankId);
                if (bank == NULL)
                {
                    //Log Error
                    state->setState(IVVCState::IVVC_WAIT);
                    break;
                }

                // IS THIS RIGHT?
                subbus->updatePointResponseDeltas();

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
            if (state->isRemoteMode())
            {
                if (_CC_DEBUG & CC_DEBUG_IVVC)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " IVVC Algorithm: Comms lost, sending remote control disable. " << endl;
                }
                CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_DISABLE,subbus->getLtcId()))->execute();
                state->setRemoteMode(false);
            }
            state->setState(IVVCState::IVVC_WAIT);
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

bool IVVCAlgorithm::checkForStaleData(const PointValueMap& pointValues, CtiTime timeNow)
{
    for each (const PointValueMap::value_type& pv in pointValues)
    {
        //Tie in % online here later
        if (pv.second.timestamp < (timeNow - (_POINT_AGE/*minutes*/*60)))
        {
            return true;
        }
    }

    return false;
}

//sendScan must be false for unit tests.
void IVVCAlgorithm::determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan, std::set<long>& pointIds, std::set<long>& requestPoints)
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
        if (sendScan == true)
        {
            CtiCCCommand* ltcScan = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY,subbus->getLtcId());
            CtiCCExecutorFactory::createExecutor(ltcScan)->execute();
        }
        pointIds.insert(point.getPointId());
    }

    //SubBus voltage point.
    long pointId = subbus->getCurrentVoltLoadPointId();
    if (pointId > 0)
    {
        //No scan available. Request value from dispatch
        requestPoints.insert(pointId);
        pointIds.insert(pointId);
    }

    //SubBus watt point to calc power factor.
    pointId = subbus->getCurrentWattLoadPointId();
    if (pointId > 0)
    {
        requestPoints.insert(pointId);
        pointIds.insert(pointId);
    }

    //SubBus var point to calc power factor.
    pointId = subbus->getCurrentVarLoadPointId();
    if (pointId > 0)
    {
        requestPoints.insert(pointId);
        pointIds.insert(pointId);
    }

    //Feeder voltage points.
    CtiFeeder_vec feeders = subbus->getCCFeeders();
    for each (CtiCCFeederPtr feeder in feeders)
    {
        long feederVoltageId = feeder->getCurrentVoltLoadPointId();
        if (feederVoltageId > 0)
        {
            //No scan available. Request values from dispatch
            requestPoints.insert(pointId);
            pointIds.insert(feederVoltageId);
        }
    }

    //All two way cbc's
    std::vector<CtiCCCapBankPtr> banks = store->getCapBanksByPaoIdAndType(subbus->getPaoId(),SubBus);
    for each (CtiCCCapBankPtr bank in banks)
    {
        int cbcId = bank->getControlDeviceId();
        //TODO: Check disabled (failed?) on bank and feeder (and sub?)
        if (sendScan == true)
        {
            CtiCCCommand* cbcScan = new CtiCCCommand(CtiCCCommand::SCAN_2WAY_DEVICE,cbcId);
            CtiCCExecutorFactory::createExecutor(cbcScan)->execute();
        }

        int voltId = bank->getTwoWayPoints()->getVoltageId();
        if (voltId > 0)
        {
            pointIds.insert(voltId);
        }
    }
}

double IVVCAlgorithm::calculateVf(const PointValueMap &voltages, const long varPointID, const long wattPointID)
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
        // Need to ignore the watt and var points that are passed in.

        if ( b->first != varPointID && b->first != wattPointID)
        {
            totalSum += b->second.value;
            minimum = std::min( minimum, b->second.value );
            totalCount++;
        }
    }

    return ( ( totalSum / totalCount ) - minimum  );
}


int IVVCAlgorithm::calculateVte(const PointValueMap &voltages, const double Vmin, const double Vrm, const double Vmax,
                                const long varPointID, const long wattPointID)
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
        // Need to ignore the watt and var points that are passed in.

        if ( b->first != varPointID && b->first != wattPointID)
        {
            if ( b->second.value > Vmax ) { lowerTap = true; }
            if ( b->second.value < Vmin ) { raiseTap = true; }
            if ( b->second.value <= Vrm ) { marginTap = false; }
        }
    }

    return (( lowerTap || marginTap ) ? -1 : raiseTap ? 1 : 0);
}


double IVVCAlgorithm::calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor)
{
    const double Pf = std::abs(100.0 * ( 1.0 - powerFactor ) );

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
            CtiMultiMsg_vec ccEvents;

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

                sendPointChangesAndEvents(dispatchConnection,pointChanges,ccEvents);
            }
        }
    }
}

void IVVCAlgorithm::sendKeepAlive(CtiCCSubstationBusPtr subbus)
{
    const long ltcId = subbus->getLtcId();

    if ( isLtcInRemoteMode(ltcId) )
    {
        if( _CC_DEBUG & CC_DEBUG_IVVC )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - LTC Keep Alive messages sent." << endl;
        }
        CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::LTC_KEEP_ALIVE, ltcId))->execute();
    }
    else
    {
        if( _CC_DEBUG & CC_DEBUG_IVVC )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - LTC Keep Alive NOT sent. LTC is in Local Mode." << endl;
        }
    }
}

void IVVCAlgorithm::sendPointChangesAndEvents(DispatchConnectionPtr dispatchConnection, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents)
{
    CtiMultiMsg* ccEventMsg = new CtiMultiMsg();
    ccEventMsg->setData(ccEvents);
    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(ccEventMsg);
    //Not calling processCCEventMsgs(). The control loop will end up calling this.

    CtiMultiMsg* pointChangeMsg = new CtiMultiMsg();
    pointChangeMsg->setData(pointChanges);
    dispatchConnection->WriteConnQue(pointChangeMsg);
}


bool IVVCAlgorithm::isLtcInRemoteMode(const long ltcId)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    LoadTapChangerPtr ltc = store->findLtcById(ltcId);

    LitePoint remotePoint = ltc->getAutoRemotePoint();

    double value = -1.0;
    ltc->getPointValue(remotePoint.getPointId(), value);

    return (value == 0.0);  // Remote Mode
}

