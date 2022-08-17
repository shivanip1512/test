#pragma once

#include "lmgroupbase.h"

class CtiLMGroupEmetcon : public CtiLMGroupBase
{

public:

DECLARE_COLLECTABLE( CtiLMGroupEmetcon );

    CtiLMGroupEmetcon();
    CtiLMGroupEmetcon(Cti::RowReader &rdr);
    CtiLMGroupEmetcon(const CtiLMGroupEmetcon& groupemet);

    virtual ~CtiLMGroupEmetcon();

    virtual CtiLMGroupBase& setGroupControlState(LONG controlstate);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime);

    CtiLMGroupEmetcon& operator=(const CtiLMGroupEmetcon& right);

    /* Static Members */

private:

    BOOL _refreshsent;

protected:
    void restore(Cti::RowReader &rdr);
};
