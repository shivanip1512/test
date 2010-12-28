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
    ~ThreadStatusKeeper();

    bool monitorCheck();
    bool monitorCheck(int tickleInterval); 
    bool monitorCheck(CtiThreadRegData::behaviourFuncPtr fnPtr);
    bool monitorCheck(CtiThreadRegData::Behaviours behavior);
    bool monitorCheck(int tickleInterval, CtiThreadRegData::Behaviours behavior);

    void forceTickle(CtiThreadRegData::Behaviours behavior, int tickleInterval);

private:

    CtiTime _announceTime;
    CtiTime _tickleTime;

    std::string _threadName;

    bool monitorCheck(CtiThreadRegData::Behaviours behavior, int tickleInterval, CtiThreadRegData::behaviourFuncPtr fnPtr);

    static void awolComplain( void *who );
};

}