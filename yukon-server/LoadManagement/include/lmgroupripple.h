#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
                
class CtiLMGroupRipple : public CtiLMGroupBase
{

public:

DECLARE_COLLECTABLE( CtiLMGroupRipple );

    CtiLMGroupRipple();
    CtiLMGroupRipple(Cti::RowReader &rdr);
    CtiLMGroupRipple(const CtiLMGroupRipple& groupripple);

    virtual ~CtiLMGroupRipple();
    
    LONG getShedTime() const;

    CtiLMGroupRipple& setShedTime(LONG shed);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime);

    CtiLMGroupRipple& operator=(const CtiLMGroupRipple& right);

    int operator==(const CtiLMGroupRipple& right) const;
    int operator!=(const CtiLMGroupRipple& right) const;

    /* Static Members */

private:

    LONG _shedtime;

    BOOL _refreshsent;

    void restore(Cti::RowReader &rdr);
};
