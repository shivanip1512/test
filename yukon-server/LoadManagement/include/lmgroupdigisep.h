#pragma once

#include "lmgroupbase.h"
#include "GroupControlInterface.h"
#include "SepControlInterface.h"

class LMGroupDigiSEP : public CtiLMGroupBase, public Cti::LoadManagement::GroupControlInterface, public Cti::LoadManagement::SEPControlInterface
{

public:

DECLARE_COLLECTABLE( LMGroupDigiSEP );

    LMGroupDigiSEP();
    LMGroupDigiSEP(Cti::RowReader &rdr);
    LMGroupDigiSEP(const LMGroupDigiSEP& group);

    virtual ~LMGroupDigiSEP();

    virtual CtiLMGroupBase* replicate() const;

    LMGroupDigiSEP& operator=(const LMGroupDigiSEP& right);

    virtual bool sendSEPCycleControl(long controlMinutes, long cyclePercent, long criticality, bool isTrueCycle, bool randomizeStart, bool randomizeStop);
    virtual bool sendSEPTempOffsetControl(long controlMinutes, long heatOffset, long coolOffset, bool isCelsius, long criticality, bool randomizeStart, bool randomizeStop);
    virtual bool sendStopControl(bool stopImmediately);
    virtual bool sendShedControl(long controlMinutes);

    virtual bool doesStopRequireCommandAt(const CtiTime &currentTime) const;

    //Unused
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

protected:
    void restore(Cti::RowReader &rdr);
};

typedef boost::shared_ptr<LMGroupDigiSEP> LMGroupDigiSEPPtr;
