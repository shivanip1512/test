#include "yukon.h"

#include "ThreadStatusKeeper.h"

#include "thread_monitor.h"

namespace Cti {

ThreadStatusKeeper::ThreadStatusKeeper(const std::string &threadName) :
    _threadName(threadName),
    _announceTime((unsigned long) 0),
    _tickleTime((unsigned long) 0)
{}

ThreadStatusKeeper::~ThreadStatusKeeper()
{
    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), _threadName, CtiThreadRegData::LogOut ) );
}

bool ThreadStatusKeeper::monitorCheck()
{
    return monitorCheck(CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::behaviourFuncPtr fnPtr)
{
    return monitorCheck(CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, fnPtr);
}

bool ThreadStatusKeeper::monitorCheck(int tickleInterval)
{
    return monitorCheck(CtiThreadRegData::Action, tickleInterval, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(CtiThreadRegData::Behaviours behavior)
{
    return monitorCheck(behavior, CtiThreadMonitor::StandardMonitorTime, &ThreadStatusKeeper::awolComplain);
}

bool ThreadStatusKeeper::monitorCheck(int tickleInterval, CtiThreadRegData::Behaviours behavior)
{
    return monitorCheck(behavior, tickleInterval, &ThreadStatusKeeper::awolComplain);
}

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
