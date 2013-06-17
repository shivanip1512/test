#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSA205OR105 : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSA205OR105 )

    CtiLMGroupSA205OR105();
    CtiLMGroupSA205OR105(Cti::RowReader &rdr);
    CtiLMGroupSA205OR105(const CtiLMGroupSA205OR105& groupexp);

    virtual ~CtiLMGroupSA205OR105();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSA205OR105& operator=(const CtiLMGroupSA205OR105& right);

    int operator==(const CtiLMGroupSA205OR105& right) const;
    int operator!=(const CtiLMGroupSA205OR105& right) const;

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
};
