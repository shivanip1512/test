#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
                
class CtiLMGroupEmetcon : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupEmetcon )

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

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupEmetcon& operator=(const CtiLMGroupEmetcon& right);

    int operator==(const CtiLMGroupEmetcon& right) const;
    int operator!=(const CtiLMGroupEmetcon& right) const;

    /* Static Members */

private:

    BOOL _refreshsent;

protected:
    void restore(Cti::RowReader &rdr);
};
