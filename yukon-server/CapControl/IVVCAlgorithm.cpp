#include "yukon.h"

#include "IVVCAlgorithm.h"
#include "GroupPointDataRequest.h"
#include "capcontroller.h"
#include "IVVCState.h"
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

void IVVCAlgorithm::execute(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy, bool allowScanning)
{
    //check state and do stuff based on that.
    IVVCState::State currentState = state->getState();

    CtiTime timeNow;
    //Note: Overload DispatchConnection::WriteConnQue for unit testing.
    DispatchConnectionPtr conn = CtiCapController::getInstance()->getDispatchConnection();

    switch (currentState)
    {
        case IVVCState::IVVC_WAIT:
        {
            //Check to make sure the points are setup correctly
            //Make sure we have all the data we need.
            int varPointId = subbus->getCurrentVarLoadPointId();
            int voltPointId = subbus->getCurrentVoltLoadPointId();
            int wattPointId = subbus->getCurrentWattLoadPointId();
            if (varPointId == 0 || voltPointId == 0 || wattPointId == 0)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " IVVC Algorithm cannot execute. Check to make sure the Var, Volt, and Watt points are setup for Subbus: " << subbus->getPaoName() << endl;
                return;
            }

            //save away start time.
            state->setTimeStamp(timeNow);

            // What points are we wanting?
            std::list<long> pointIds = determineWatchPoints(subbus, conn, false);

            // Make GroupRequest Here
            GroupRequestPtr request(new GroupPointDataRequest(conn));
            request->watchPoints(pointIds);

            //save away this request for later.
            state->setGroupRequest(request);

            if (state->isScannedRequest() == false)
            {
                //We did not Scan for these points. Ask dispatch for them instead.
                CtiCommandMsg* cmdMsg = new CtiCommandMsg();
                cmdMsg->setOperation(CtiCommandMsg::PointDataRequest);

                CtiCommandMsg::CtiOpArgList_t points;
                for each (long pointId in pointIds)
                {
                    points.push_back(pointId);
                }

                cmdMsg->setOpArgList(points);
                conn->WriteConnQue(cmdMsg);
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
                GroupRequestPtr request = state->getGroupRequest();
                if (request->isComplete() == true)
                {
                    //Check for stale data.
                    PointValueMap pointValues = request->getPointValues();
                    bool stale = checkForStaleData(pointValues,timeNow);

                    if (stale == true)
                    {
                        // If allowScanning is false. we will not scan here even though we are supposed to. (Unit testing)
                        determineWatchPoints(subbus, conn, allowScanning);
                        state->setScannedRequest(true);
                        state->setState(IVVCState::IVVC_WAIT);
                        break;
                    }
                    else
                    {
                        state->setState(IVVCState::IVVC_ANALYZE_DATA);
                    }
                }
                else
                {
                    CtiTime startTime = state->getTimeStamp();

                    if ((startTime + (_SCAN_WAIT_EXPIRE/*minutes*/*60)) < timeNow)
                    {
                        if (false)
                        {
                            //Tie in % online here later (if enough, go IVVC_ANALYZE_DATA)
                        }
                        else
                        {
                            //TODO: Kill Heartbeat and flip to Automode.
                            //waiting on confirmation on how to do this exactly...
                            state->setState(IVVCState::IVVC_WAIT);
                        }
                    }
                    break;//still waiting for complete
                }
            }
            else
            {
                //Is it time to control? (Analysis Interval)
                if (timeNow > state->getNextControlTime())
                {
                    //set next control time.
                    state->setNextControlTime(timeNow + strategy->getControlInterval());

                    GroupRequestPtr request = state->getGroupRequest();

                    if (request->isComplete() == true/*Tie in % online here later*/)
                    {
                        //Check for stale data.
                        PointValueMap pointValues = request->getPointValues();
                        bool stale = checkForStaleData(pointValues,timeNow);

                        if (stale == true)
                        {
                            //To be considered. Initiate scan if stale? like in 'on new data'
                            state->setState(IVVCState::IVVC_WAIT);
                            break;
                        }
                        else
                        {
                            state->setState(IVVCState::IVVC_ANALYZE_DATA);
                        }
                    }
                    else
                    {
                        //scan did not complete in the analysis interval... start over.
                        //Do we flip to auto and kill heartbeat like above?

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
            bool isPeakTime = true;     // we need to calculate this

            PointValueMap pointValues = state->getGroupRequest()->getPointValues();     // don't like repeating this here since we got them above

            /* calculate current power factor of the bus */

            // if we are here these IDs exist...
            long wattPointID = subbus->getCurrentWattLoadPointId();
            long varPointID = subbus->getCurrentVarLoadPointId();

            PointValueMap::iterator iter = pointValues.find(wattPointID);

            // what do we do if the var or watt point is stale / missing, etc.

            double wattValue = (iter != pointValues.end()) ? iter->second.value : 1.0;
            iter = pointValues.find(varPointID);
            double varValue = (iter != pointValues.end()) ? iter->second.value : 1.0;

            double PFBus = subbus->calculatePowerFactor(varValue, wattValue);

            /* calculate potential tap operation */

            int tapOp = calculateVte(pointValues,
                                     strategy->getLowerVoltLimit(isPeakTime),
                                            strategy->getLowerVoltLimit(isPeakTime) + 3.0, // FIX: where do we get this value from
                                     strategy->getUpperVoltLimit(isPeakTime),
                                     varPointID, wattPointID);

            /* calculate current flatness of the bus */

            double Vf = calculateVf(pointValues, varPointID, wattPointID);  // need to remove watt and var points

            /* calculate current weight of the bus */

            double currentBusWeight = calculateBusWeight(strategy->getVoltWeight(isPeakTime), Vf,
                                                         strategy->getPFWeight(isPeakTime), PFBus);

            /* calculate estimated bus weights etc from historical data */


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

                    bool isCapBankOpen = ( currentBank->getAssumedOrigVerificationState() == CtiCCCapBank::Open );

                    std::vector <CtiCCPointResponsePtr>& responses = currentBank->getPointResponse();

                    for ( std::vector <CtiCCPointResponsePtr>::iterator prb = responses.begin(), pre = responses.end(); prb != pre; ++prb )
                    {
                        CtiCCPointResponsePtr currentResponse = *prb;

                        deltas[ currentResponse->getBankId() ].value += ( ( isCapBankOpen ? 1.0 : -1.0 ) * currentResponse->getDelta() );
                    }

                    state->_estimated[currentBank->getPaoId()].capbank = currentBank;

                    /* calculate estimated flatness of the bus if current bank switches state */

                    state->_estimated[currentBank->getPaoId()].flatness = 
                        calculateVf(deltas, varPointID, wattPointID);  // need to remove watt and var points

                    /* calculate estimated power factor of the bus if current bank switches state */

                    state->_estimated[currentBank->getPaoId()].powerFactor = 
                        subbus->calculatePowerFactor(varValue, wattValue + ( ( isCapBankOpen ? -1.0 : 1.0 ) * currentBank->getBankSize() ) );

                    /* calculate estimated weight of the bus if current bank switches state */

                    state->_estimated[currentBank->getPaoId()].busWeight = 
                        calculateBusWeight(strategy->getVoltWeight(isPeakTime), state->_estimated[currentBank->getPaoId()].flatness,
                                           strategy->getPFWeight(isPeakTime), state->_estimated[currentBank->getPaoId()].powerFactor);
                }
            }

            long    operatePaoID = -1;
            double  minimumEstVf = std::numeric_limits<double>::max();

            for ( IVVCState::EstimatedDataMap::iterator eb = state->_estimated.begin(), ee = state->_estimated.end(); eb != ee; ++eb )
            {
                if ( eb->second.flatness < minimumEstVf )
                {
                    minimumEstVf = eb->second.flatness;
                    operatePaoID = eb->first;
                }
            }

            if ( ( currentBusWeight - strategy->getDecisionWeight(isPeakTime) ) > state->_estimated[operatePaoID].flatness )   // outside of the window.
            {
                CtiTime now;
                state->setTimeStamp(now);

                // record preoperation voltage values for the feeder our capbank is on

                std::vector <CtiCCPointResponsePtr>& responses = state->_estimated[operatePaoID].capbank->getPointResponse();

                for ( std::vector <CtiCCPointResponsePtr>::iterator prb = responses.begin(), pre = responses.end(); prb != pre; ++prb )
                {
                    (*prb)->setPreOpValue( pointValues[ (*prb)->getBankId() ].value );
                }

                state->_estimated[operatePaoID].operated = true;

                state->setControlledBankId( operatePaoID );

                //send cap bank command

                state->setState(IVVCState::IVVC_VERIFY_CONTROL_LOOP);
            }
            else
            {
                // TAP operation - use precalculated value from above

                CtiTime now;

                if ( tapOp != 0 )
                {
                    if ( ( now.seconds() - state->getLastTapOpTime().seconds() ) > 300 )    // which CPARM for the '300'?
                    {
                        state->setLastTapOpTime(now);

                        if ( tapOp == -1 )
                        {
                            //  send command to lower tap

                        }
                        else
                        {
                            //  send command to raise tap

                        }
                    }
                }
                state->setState(IVVCState::IVVC_WAIT);
                break;
            }
        }
        case IVVCState::IVVC_VERIFY_CONTROL_LOOP:
        {
            CtiCCSubstationBusStore* bus = CtiCCSubstationBusStore::getInstance();
            int bankId = state->getControlledBankId();


            CtiCCCapBankPtr bank = bus->getCapBankByPaoId(bankId);
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
            if (now > (controlTime+maxConfirmTime))
            {
                //update Bank state. (failed or questionable)
                state->setState(IVVCState::IVVC_WAIT);
                break;
            }

            CtiMultiMsg_vec pointChangeVec;
            CtiMultiMsg_vec ccEventVec;

            //Verify if we controlled
            bool bankIsVerified = subbus->capBankControlStatusUpdate(pointChangeVec,ccEventVec);

            if (bankIsVerified == true)
            {
                CtiTime now;
                state->setTimeStamp(now);
                state->setState(IVVCState::IVVC_POST_CONTROL_WAIT);
            }
            else
            {
                break;
            }
        }
        case IVVCState::IVVC_POST_CONTROL_WAIT:
        {
            CtiTime capBankOpTime = state->getTimeStamp();
            CtiTime now;
            if (now > (capBankOpTime + _POST_CONTROL_WAIT/*seconds*/))
            {
                GroupRequestPtr request(new GroupPointDataRequest(conn));
                std::list<long> pointIds = determineWatchPoints(subbus,conn,allowScanning);
                request->watchPoints(pointIds);

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
            GroupRequestPtr request = state->getGroupRequest();
            CtiTime scanTime = state->getTimeStamp();
            CtiTime now;

            if (request->isComplete() == true)
            {
                //TODO: record data
                state->setState(IVVCState::IVVC_WAIT);
            }
            else if ((scanTime + (_SCAN_WAIT_EXPIRE/*minutes*/*60)) < now)
            {
                //scan timed out.
                state->setState(IVVCState::IVVC_WAIT);
            }

            break;//never fall through past this
        }
        default:
        {
            //zomg
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
std::list<long> IVVCAlgorithm::determineWatchPoints(CtiCCSubstationBusPtr subbus, DispatchConnectionPtr conn, bool sendScan)
{
    std::list<long> pointIds;
    AttributeService attributeService;
    CtiCCSubstationBusStore* bus = CtiCCSubstationBusStore::getInstance();

    //the ltc.
    LitePoint point = attributeService.getPointByPaoAndAttribute(subbus->getLtcId(),PointAttribute::LtcVoltage);
    if (point.getPointType() == InvalidPointType)
    { //Continue or fail here?
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << "  LTC Voltage point not found for LTC id: " << subbus->getLtcId() << endl;
    }
    else
    {
        if (sendScan == true)
        {
            CtiCCCommand* ltcScan = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY,point.getPaoId());//RTU id?
            CtiCCExecutorFactory::createExecutor(ltcScan)->execute();
        }
        pointIds.push_back(point.getPointId());
    }

    //SubBus voltage point.
    long busId = subbus->getCurrentVoltLoadPointId();
    if (busId > 0)
    {
        //No scan available. In Case of Duke, this will come from the LTC scan anyways.
        pointIds.push_back(busId);
    }

    //SubBus watt point to calc power factor.
    busId = subbus->getCurrentWattLoadPointId();
    if (busId > 0)
    {
        pointIds.push_back(busId);
    }

    //SubBus var point to calc power factor.
    busId = subbus->getCurrentVarLoadPointId();
    if (busId > 0)
    {
        pointIds.push_back(busId);
    }

    //Feeder voltage points.
    CtiFeeder_vec feeders = subbus->getCCFeeders();
    for each (CtiCCFeederPtr feeder in feeders)
    {
        long feederVoltageId = feeder->getCurrentVoltLoadPointId();
        if (feederVoltageId > 0)
        {
            //No scan available. In Case of Duke, this will come from the LTC scan anyways.
            pointIds.push_back(feederVoltageId);
        }
    }

    //All two way cbc's
    std::vector<CtiCCCapBankPtr> banks = bus->getCapBanksByPaoIdAndType(subbus->getPaoId(),SubBus);
    for each (CtiCCCapBankPtr bank in banks)
    {
        //TODO: Check disabled (failed?) on bank and feeder (and sub?)
        if (sendScan == true)
        {
            CtiCCCommand* cbcScan = new CtiCCCommand(CtiCCCommand::SCAN_2WAY_DEVICE,bank->getControlDeviceId());
            CtiCCExecutorFactory::createExecutor(cbcScan)->execute();
        }
        pointIds.push_back(bank->getPaoId());
    }

    return pointIds;
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

