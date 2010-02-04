#include "yukon.h"

#include "IVVCAlgorithm.h"
#include "GroupPointDataRequest.h"
#include "capcontroller.h"
#include "IVVCState.h"
#include "msg_cmd.h"

#include <list>

extern ULONG _SCAN_WAIT_EXPIRE;
extern ULONG _POINT_AGE;

void IVVCAlgorithm::execute(IVVCStatePtr state, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy)
{
    //check state and do stuff based on that.
    IVVCState::State currentState = state->getState();

    CtiTime timeNow;

    switch (currentState)
    {
        case IVVCState::IVVC_WAIT:
        {
            //save away start time.
            state->setTimeStamp(timeNow);

            // What points are we wanting?
            std::list<long> pointIds;
            //TODO: figure out what points.

            // Make GroupRequest Here
            DispatchConnectionPtr conn = CtiCapController::getInstance()->getDispatchConnection();
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
                    bool stale = false;
                    PointValueMap pointValues = request->getPointValues();
                    for each (const PointValueMap::value_type& pv in pointValues)
                    {
                        //Tie in % online here later
                        if (pv.second.timestamp < (timeNow - (_SCAN_WAIT_EXPIRE/*minutes*/*60)))
                        {
                            stale = true;
                            break;//from loop
                        }
                    }

                    if (stale == true)
                    {
                        //TODO: Send scans here

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
                //Time to control? (Analysis Interval)
                if (timeNow > state->getNextControlTime())
                {
                    //set next control time.
                    state->setNextControlTime(timeNow + strategy->getControlInterval());

                    GroupRequestPtr request = state->getGroupRequest();

                    if (request->isComplete() == true/*Tie in % online here later*/)
                    {
                        /*****************WARNING COPY PASTED CODE: FIX ME*****************/
                        //Check for stale data.
                        bool stale = false;
                        PointValueMap pointValues = request->getPointValues();
                        for each (const PointValueMap::value_type& pv in pointValues)
                        {
                            //Tie in % online here later
                            if (pv.second.timestamp < (timeNow - (_SCAN_WAIT_EXPIRE/*minutes*/*60)))
                            {
                                stale = true;
                                break;//from loop
                            }
                        }

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
            if (/*If NOW > CPARM + CAPBANKOPTIME*/true)
            {
                //Create Group Request
                //Send Scans
                state->setState(IVVCState::IVVC_POSTSCAN_LOOP);
            }
            else
            {
                break;
            }
        }

        case IVVCState::IVVC_POSTSCAN_LOOP:
        {
            //timeout on this? scan_wait_expire
            if (/*GroupRequest.isComplete()*/true)
            {
                //record data
                state->setState(IVVCState::IVVC_WAIT);
            }
            else if (/*scan_wait_expire*/true)
            {
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
