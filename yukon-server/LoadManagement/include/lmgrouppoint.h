/*---------------------------------------------------------------------------
        Filename:  lmgrouppoint.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupPoint
                        CtiLMGroupPoint

        Initial Date:  3/13/2002
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2002
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPPOINTIMPL_H
#define CTILMGROUPPOINTIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "msg_cmd.h"
#include "observe.h"
                
class CtiLMGroupPoint : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupPoint )

    CtiLMGroupPoint();
    CtiLMGroupPoint(RWDBReader& rdr);
    CtiLMGroupPoint(const CtiLMGroupPoint& grouppoint);

    virtual ~CtiLMGroupPoint();
    
    ULONG getDeviceIdUsage() const;
    ULONG getPointIdUsage() const;
    ULONG getStartControlRawState() const;

    CtiLMGroupPoint& setDeviceIdUsage(ULONG deviduse);
    CtiLMGroupPoint& setPointIdUsage(ULONG pointiduse);
    CtiLMGroupPoint& setStartControlRawState(ULONG startcontrolstate);

    void restorePointSpecificDatabaseEntries(RWDBReader& rdr);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount) const;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime) const;
    virtual CtiCommandMsg* createLatchingRequestMsg(ULONG rawState) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupPoint& operator=(const CtiLMGroupPoint& right);

    int operator==(const CtiLMGroupPoint& right) const;
    int operator!=(const CtiLMGroupPoint& right) const;

    /* Static Members */

private:

    ULONG _deviceidusage;
    ULONG _pointidusage;
    ULONG _startcontrolrawstate;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif

