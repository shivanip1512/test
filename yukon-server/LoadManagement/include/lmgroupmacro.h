#pragma once

#include "lmgroupbase.h"

class CtiLMGroupMacro : public CtiLMGroupBase
{
public:

    CtiLMGroupMacro();
    CtiLMGroupMacro(Cti::RowReader &rdr);

    virtual ~CtiLMGroupMacro();

    const std::vector< CtiLMGroupBase* > getChildren() const;
    void setChildren(const std::vector<CtiLMGroupBase*>& children);
    
    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const { return 0; }
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const { return 0; }
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const { return 0;}
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const { return 0; }

private:

    std::vector< CtiLMGroupBase* > _children;

protected:
    
    void restore(Cti::RowReader &rdr);
};
