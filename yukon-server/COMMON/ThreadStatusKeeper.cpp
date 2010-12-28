#include "yukon.h"

#include "ThreadStatusKeeper.h"

#include "thread_monitor.h"

namespace Cti {

ThreadStatusKeeper::ThreadStatusKeeper(const std::string &threadName) :
    _threadName(threadName),
    _announceTime((unsigned long) 0),
    _tickleTime((unsigned long) 0)
{}

/* 
 * The destructor will call the tickle function using LogOut each time the 
 * ThreadStatusKeeper object goes out of scope. 
 */
ThreadStatusKeeper::~ThreadStatusKeeper() 
{
    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), _threadName, CtiThreadRegData::LogOut ) );
}

/* 
 * This is the "default" call for monitor check. It will use the 
 * awolComplain function, StandardMonitorTime (300 seconds), and the 
 * Action behavior. This is the call that occurs the most frequently in other code.
 */
bool ThreadStatusKeeper::monitorCheck() 
{
    return monitorCheck(CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &ThreadStatusKeeper::awolComplain);
}

/*
 * This is an outlier function call for calls to monitorCheck that 
 * require the default Action behavior and StandardMonitorTime of 300, 
 * but need to pass their own function pointer rather than using the 
 * ThreadStatusKeeper's awolComplain function. 
 */
bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::behaviourFuncPtr fnPtr)
{
    return monitorCheck(CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, fnPtr);
}

/* 
 * This is an outlier funcion call for calls to monitorCheck with the 
 * default behavior and function parameters, but a different timing parameter 
 * other than StandardMonitorTime (300). 
 * 
 * NOTE: In most cases the 'timing' parameter will be passed as an enum value from CtiThreadMonitor::TickleTiming,
 * but there are a few outlying cases that we need to account for, so an int can be used rather than forcing the 
 * parameter to be of the TickleTiming variety.     
 */
bool ThreadStatusKeeper::monitorCheck(int tickleInterval)
{
    return monitorCheck(CtiThreadRegData::Action, tickleInterval, &ThreadStatusKeeper::awolComplain);
}

/* 
 * This is an outlier funcion call for calls to monitorCheck with the 
 * default timing and function parameters, but a different behavior parameter 
 * other than Action. 
 */
bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::Behaviours behavior)
{
    return monitorCheck(behavior, CtiThreadMonitor::StandardMonitorTime, &ThreadStatusKeeper::awolComplain);
}

/* 
 * This is an outlier funcion call for calls to monitorCheck with the 
 * function pointer parameter, but a different behavior parameter 
 * other than Action and a different timing parameter. 
 */
bool ThreadStatusKeeper::monitorCheck(int tickleInterval, CtiThreadRegData::Behaviours behavior)
{
    return monitorCheck(behavior, tickleInterval, &ThreadStatusKeeper::awolComplain);
}

/*
 * This is the core monitorCheck function declared private since it shouldn't be called outside of this class. 
 * It is called by each of the other public monitorCheck functions that specify different parameters. 
 */
bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::Behaviours behavior, int tickleInterval, CtiThreadRegData::behaviourFuncPtr fnPtr)
{
    CtiTime now;
    bool retVal = (now > _tickleTime);
    
    if(retVal)
    {
        _tickleTime = nextScheduledTimeAlignedOnRate( now, CtiThreadMonitor::StandardTickleTime );
        if( now > _announceTime )
        {
            _announceTime = nextScheduledTimeAlignedOnRate( now, CtiThreadMonitor::StandardMonitorTime );
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " "<< _threadName <<" TID: " << rwThreadId() << endl;
        }

        ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), _threadName, behavior, tickleInterval, fnPtr, new std::string(_threadName)) );
    }

    return retVal;
}

/*
 * This function is added solely for backwards compatability to allow previous 
 * code to force a tickle to dispatch in cases where the tickle time and announce 
 * time aren't relevant. This function should NOT be used in the future under 
 * any circumstances unless absolutely necessary since it bypasses the intended 
 * features of this class. 
 */
void ThreadStatusKeeper::forceTickle(CtiThreadRegData::Behaviours behavior, int tickleInterval)
{
    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), _threadName, behavior, tickleInterval ) );
}

/*
 * Funcion used to shout to dispatch if the thread goes AWOL. This function is 
 * private to the ThreadStatusKeeper class and is never called directly, but 
 * rather is passed around as a function pointer inside of CtiThreadRegData objects 
 * for use in a ThreadMonitor tickle() function call.
 */
void ThreadStatusKeeper::awolComplain( void *who )
{
    std::string *threadName = static_cast<std::string*>(who);
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << *threadName << " periodic thread is AWOL" << endl;
    }
    delete threadName;
}

}
