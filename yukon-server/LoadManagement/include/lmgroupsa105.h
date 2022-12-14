#pragma once

#include "lmgroupbase.h"

class CtiLMGroupSA105 : public CtiLMGroupBase
{
public:

DECLARE_COLLECTABLE( CtiLMGroupSA105 );

    CtiLMGroupSA105();
    CtiLMGroupSA105(Cti::RowReader &rdr);
    CtiLMGroupSA105(const CtiLMGroupSA105& groupexp);

    virtual ~CtiLMGroupSA105();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    CtiLMGroupSA105& operator=(const CtiLMGroupSA105& right);

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
};
