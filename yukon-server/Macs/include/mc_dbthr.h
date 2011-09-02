#pragma once

#include "mc.h"
#include "thread.h"
#include "guard.h"
#include "mgr_mcsched.h"

class CtiMCDBThread : public CtiThread
{
public:

    CtiMCDBThread(CtiMCScheduleManager& mgr, unsigned long interval = 2300);

    virtual ~CtiMCDBThread();

    virtual void run();

    //Interval is the length of time in millis between
    //database updates
    long getInterval() const;
    CtiMCDBThread& setInterval(long interval);
    void forceImmediateUpdate();

private:

    HANDLE _startUpdateNow;
    long _interval;
    CtiMCScheduleManager& _schedule_manager;

};
