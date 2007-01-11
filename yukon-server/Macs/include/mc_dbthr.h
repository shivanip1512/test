
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_dbthr
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_dbthr.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2007/01/11 21:58:23 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __MCDBTHR_H__
#define __MCDBTHR_H__

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

#endif
