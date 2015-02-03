#include "precompiled.h"

#include "ThreadStatusKeeper.h"
#include "thread_monitor.h"
#include "logger.h"

using std::endl;

namespace Cti {

ThreadStatusKeeper::ThreadStatusKeeper(const std::string &threadName) :
    _threadName(threadName),
    _announceTime((unsigned long) 0),
    _tickleTime((unsigned long) 0)
{}

ThreadStatusKeeper::~ThreadStatusKeeper()
{
    ThreadMonitor.tickle( new CtiThreadRegData( GetCurrentThreadId(), _threadName, CtiThreadRegData::LogOut ) );
}

bool ThreadStatusKeeper::monitorCheck()
{
    return monitorCheck(CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::BehaviorFunction fnPtr)
{
    return monitorCheck(CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, fnPtr);
}

bool ThreadStatusKeeper::monitorCheck(int tickleInterval)
{
    return monitorCheck(CtiThreadRegData::Action, tickleInterval, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::Behaviors behavior)
{
    return monitorCheck(behavior, CtiThreadMonitor::StandardMonitorTime, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(int tickleInterval, CtiThreadRegData::Behaviors behavior)
{
    return monitorCheck(behavior, tickleInterval, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::Behaviors behavior, int tickleInterval, CtiThreadRegData::BehaviorFunction fnPtr)
{
    CtiTime now;
    bool retVal = (now > _tickleTime);

    if(retVal)
    {
        _tickleTime = nextScheduledTimeAlignedOnRate( now, CtiThreadMonitor::StandardTickleTime );
        if( now > _announceTime )
        {
            _announceTime = nextScheduledTimeAlignedOnRate( now, CtiThreadMonitor::StandardMonitorTime );
            CTILOG_INFO(dout, _threadName);
        }

        ThreadMonitor.tickle( new CtiThreadRegData( GetCurrentThreadId(), _threadName, behavior, tickleInterval, fnPtr, _threadName) );
    }

    return retVal;
}

void ThreadStatusKeeper::awolComplain( const std::string & who )
{
    CTILOG_WARN(dout, who << " periodic thread is AWOL");
}

}
