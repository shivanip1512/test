#include "yukon.h"
#include "IVVCAlgorithm.h"

void IVVCAlgorithm::execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy)
{
    //check state and do stuff based on that.
    IVVCState::State currentState = p->getState();

    switch (currentState)
    {
        default:
        case IVVCState::IVVC_WAIT:
        {
            //Set Timestamp
            p->setState(IVVCState::IVVC_PRESCAN_LOOP);

            //GroupRequest

            if (/*scanned == */true)
            {
                //Not requesting from dispatch
                //scanned = false;
            }
            else
            {
                //Request Points from Dispatch
            }

            // jump to Prescan
        }
        case IVVCState::IVVC_PRESCAN_LOOP :
        {
            // Control Interval and isComplete if statement
            if (/*controlInterval == 0*/true)
            {//On new data
                if (/*GroupRequest.isComplete()*/true)
                {
                    if(/*stale*/true)
                    {
                        //Send scans
                        //scanned = true;
                        //State to Wait
                        break;
                    }
                    else
                    {
                        p->setState(IVVCState::IVVC_ANALYZE_DATA);
                    }
                }
                else
                {
                    //Are we Timedout break
                    //yes: check % online
                    //if yes go control
                    //if no. turn LTC to Auto and stop heartbeart. State = wait
                    break;//still waiting.
                }
            }
            else
            {
                if (/*ControlInterval has passed*/true)
                {
                    //To be considered. Initiate scan if stale? like in 'on new data'
                    if (/*GroupRequest.isComplete() || > % online */true)
                    {
                        p->setState(IVVCState::IVVC_ANALYZE_DATA);
                    }
                    else
                    {
                        p->setState(IVVCState::IVVC_WAIT);
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
                    p->setState(IVVCState::IVVC_CONTROLLED_LOOP);
                }
                else
                {
                    // Operate LTC. Check Delay

                    // No Verify, No Post Scan
                    p->setState(IVVCState::IVVC_WAIT);
                    break;
                }
            }
            else
            {
                // No Op?
                p->setState(IVVCState::IVVC_WAIT);
                break;
            }

        }
        case IVVCState::IVVC_CONTROLLED_LOOP:
        {
            //Verify if we controlled
            if (/*controlled*/true)
            {
                p->setState(IVVCState::IVVC_POST_CONTROL_WAIT);
            }
            else
            {
                if (/*timedout*/true) {
                    //update Bank state. (failed or questionable)
                    p->setState(IVVCState::IVVC_WAIT);
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
                p->setState(IVVCState::IVVC_POSTSCAN_LOOP);
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
                p->setState(IVVCState::IVVC_WAIT);
            }
            else if (/*scan_wait_expire*/true)
            {
                p->setState(IVVCState::IVVC_WAIT);
            }

            break;//never fall through past this
        }
    }//switch
}//execute
