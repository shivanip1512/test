#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSADigitalORGolay : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSADigitalORGolay )

    CtiLMGroupSADigitalORGolay();
    CtiLMGroupSADigitalORGolay(Cti::RowReader &rdr);
    CtiLMGroupSADigitalORGolay(const CtiLMGroupSADigitalORGolay& groupexp);

    virtual ~CtiLMGroupSADigitalORGolay();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSADigitalORGolay& operator=(const CtiLMGroupSADigitalORGolay& right);

    int operator==(const CtiLMGroupSADigitalORGolay& right) const;
    int operator!=(const CtiLMGroupSADigitalORGolay& right) const;

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
};
