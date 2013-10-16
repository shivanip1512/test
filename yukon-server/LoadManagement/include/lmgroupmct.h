#pragma once

#include <rw/collect.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "lmgroupemetcon.h"

class CtiLMGroupMCT : public CtiLMGroupEmetcon
{

public:

DECLARE_COLLECTABLE( CtiLMGroupMCT );

    typedef CtiLMGroupEmetcon Inherited;

    CtiLMGroupMCT();
    CtiLMGroupMCT(Cti::RowReader &rdr);
    CtiLMGroupMCT(const CtiLMGroupMCT& groupmct);

    virtual ~CtiLMGroupMCT();

    virtual CtiLMGroupBase* replicate() const;
    /*virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime);*/

    CtiLMGroupMCT& operator=(const CtiLMGroupMCT& right);

    int operator==(const CtiLMGroupMCT& right) const;
    int operator!=(const CtiLMGroupMCT& right) const;

    /* Static Members */
};
