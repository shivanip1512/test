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
            //Create Group Request
            //Send Scans
            //Fall through
        }
        case IVVCState::IVVC_PRESCAN_LOOP :
        {
            // Control Interval and isComplete if statement
            if (/*controlInterval == 0*/true)
            {//On new data
                if (/*GroupRequest.isComplete()*/true)
                {
                    p->setState(IVVCState::IVVC_ANALYZE_DATA);
                }
                else
                {
                    break;//still waiting.
                }
            }
            else
            {
                if (/*ControlInterval has passed*/true)
                {
                    if (/*GroupRequest.isComplete()*/true)
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
                    p->setState(IVVCState::IVVC_POST_CONTROL_WAIT);
                }
                else
                {//Do we want to Post Control Wait on LTC ops too?
                    // Operate LTC.

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
        case IVVCState::IVVC_POST_CONTROL_WAIT:
        {
            if (/*If NOW > CPARM + CAPBANKOPTIME*/true)
            {
                p->setState(IVVCState::IVVC_CONTROLLED_LOOP);
            }
            else
            {
                break;
            }
        }
        case IVVCState::IVVC_CONTROLLED_LOOP:
        {
            //Verify if we controlled
            //Create Group Request
            //Send Scans
            break;
        }
        case IVVCState::IVVC_POSTSCAN_LOOP:
        {
            //timeout on this?
            if (/*GroupRequest.isComplete()*/true)
            {
                //record data
                p->setState(IVVCState::IVVC_WAIT);
            }
            else
            {
                break;//still waiting.
            }
            break;//never fall through past this
        }
    }//switch
}//execute
