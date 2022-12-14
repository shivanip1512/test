#pragma once

#include "ctitime.h"
#include "thread_register_data.h"
#include "thread_monitor.h"

#include <string>

namespace Cti {

class IM_EX_CTIBASE ThreadStatusKeeper
{
public:

    ThreadStatusKeeper(const std::string &threadName);

    /*
     * The destructor will call the tickle function using LogOut each time the
     * ThreadStatusKeeper object goes out of scope.
     */
    ~ThreadStatusKeeper();


    /*
     * This is the "default" call for monitor check. It will use the
     * awolComplain function, StandardMonitorTime (300 seconds), and the
     * Action behavior. This is the call that occurs the most frequently in other code.
     */
    bool monitorCheck();

    /*
     * This is an outlier funcion call for calls to monitorCheck with the
     * default behavior and function parameters, but a different timing parameter
     * other than StandardMonitorTime (300).
     *
     * NOTE: In most cases the 'timing' parameter will be passed as an enum value from CtiThreadMonitor::TickleTiming,
     * but there are a few outlying cases that we need to account for, so an int can be used rather than forcing the
     * parameter to be of the TickleTiming variety.
     */
    bool monitorCheck(int tickleInterval);

    /*
     * This is an outlier function call for calls to monitorCheck that
     * require the default Action behavior and StandardMonitorTime of 300,
     * but need to pass their own function pointer rather than using the
     * ThreadStatusKeeper's awolComplain function.
     */
    bool monitorCheck(CtiThreadRegData::BehaviorFunction fnPtr);

    /*
     * This is an outlier funcion call for calls to monitorCheck with the
     * default timing and function parameters, but a different behavior parameter
     * other than Action.
     */
    bool monitorCheck(CtiThreadRegData::Behaviors behavior);

    /*
     * This is an outlier funcion call for calls to monitorCheck with the
     * default function pointer parameter, but a different behavior parameter
     * other than Action and a different timing parameter.
     */
    bool monitorCheck(int tickleInterval, CtiThreadRegData::Behaviors behavior);

private:

    CtiTime _announceTime;
    CtiTime _tickleTime;

    std::string _threadName;

    /*
     * This is the core monitorCheck function declared private since it shouldn't be called outside of this class.
     * It is called by each of the other public monitorCheck functions that specify different parameters.
     */
    bool monitorCheck(CtiThreadRegData::Behaviors behavior, int tickleInterval, CtiThreadRegData::BehaviorFunction fnPtr);


    /*
     * Function used to shout to dispatch if the thread goes AWOL. This function is
     * private to the ThreadStatusKeeper class and is never called directly, but
     * rather is passed around as a function pointer inside of CtiThreadRegData objects
     * for use in a ThreadMonitor tickle() function call.
     */
    static void awolComplain( const std::string & who );
};

}