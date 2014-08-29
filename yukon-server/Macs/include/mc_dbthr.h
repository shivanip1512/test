#pragma once

#include "mc.h"
#include "thread.h"
#include "guard.h"
#include "mgr_mcsched.h"

class CtiMCDBThread : public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiMCDBThread(const CtiMCDBThread&);
    CtiMCDBThread& operator=(const CtiMCDBThread&);

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
