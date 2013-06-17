#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmgroupbase.h"
                
class CtiLMGroupVersacom : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupVersacom )

    CtiLMGroupVersacom();
    CtiLMGroupVersacom(Cti::RowReader &rdr);
    CtiLMGroupVersacom(const CtiLMGroupVersacom& groupversa);

    virtual ~CtiLMGroupVersacom();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupVersacom& operator=(const CtiLMGroupVersacom& right);

    int operator==(const CtiLMGroupVersacom& right) const;
    int operator!=(const CtiLMGroupVersacom& right) const;

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
};
