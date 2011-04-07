#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
                
class LMGroupDigiSEP : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( LMGroupDigiSEP )

    LMGroupDigiSEP();
    LMGroupDigiSEP(Cti::RowReader &rdr);
    LMGroupDigiSEP(const LMGroupDigiSEP& group);

    virtual ~LMGroupDigiSEP();
    
    virtual CtiLMGroupBase* replicate() const;

    LMGroupDigiSEP& operator=(const LMGroupDigiSEP& right);

    int operator==(const LMGroupDigiSEP& right) const;
    int operator!=(const LMGroupDigiSEP& right) const;

    virtual bool sendSEPCycleControl(long controlMinutes, long cyclePercent, long criticality, bool isTrueCycle, bool randomizeStart, bool randomizeStop);
    virtual bool sendStopControl(bool stopImmediately);

    //Unused
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

protected:
    void restore(Cti::RowReader &rdr);
};
