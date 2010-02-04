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

extern ULONG _SCAN_WAIT_EXPIRE;
extern ULONG _POINT_AGE;
extern ULONG _POST_CONTROL_WAIT;

void IVVCAlgorithm::execute(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy)
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
                        determineWatchPoints(subbus, conn, true);
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
            // Check for all data
            // Check timestamps and quality
            // If all data present and ok Crunch numbers!

            if (/*Is an operation needed*/true)
            {
                if (/*CapBank Op*/true)
                {
                    //send cap bank command
                    state->setState(IVVCState::IVVC_CONTROLLED_LOOP);
                }
                else
                {
                    // Operate LTC. Check Delay

                    // No Verify, No Post Scan
                    state->setState(IVVCState::IVVC_WAIT);
                    break;
                }
            }
            else
            {
                // No Op?
                state->setState(IVVCState::IVVC_WAIT);
                break;
            }

        }
        case IVVCState::IVVC_CONTROLLED_LOOP:
        {
            //Verify if we controlled
            if (/*controlled*/true)
            {

                //Set Timestamp to cap bank operation time.
                CtiTime now;
                state->setTimeStamp(now);
                state->setState(IVVCState::IVVC_POST_CONTROL_WAIT);
            }
            else
            {
                if (/*timedout*/true) {
                    //update Bank state. (failed or questionable)
                    state->setState(IVVCState::IVVC_WAIT);
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
                //Create Group Request
                GroupRequestPtr request(new GroupPointDataRequest(conn));
                std::list<long> pointIds = determineWatchPoints(subbus,conn,true);
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

//Executors make unit testing impossible. Consider changing
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


double IVVCAlgorithm::calculateBusWeight(const double Kv, const double Vf, const double Kp, const double powerFactor)
{
    const double Pf = (100.0 * ( 1.0 - powerFactor ) );

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


double IVVCAlgorithm::calculatePowerFactor(const double KWattBus, const double KVarBus, const double bankSize)
{
    double adjKWatt = KWattBus + bankSize;

    return ( adjKWatt / std::sqrt( std::pow(adjKWatt, 2.0) + std::pow(KVarBus, 2.0) ) );
}

