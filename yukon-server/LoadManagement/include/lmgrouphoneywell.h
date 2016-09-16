#pragma once

#include "lmgroupbase.h"
#include "GroupControlInterface.h"
#include "honeywellControlInterface.h"


class LMGroupHoneywell : public CtiLMGroupBase,
    public Cti::LoadManagement::GroupControlInterface,
    public Cti::LoadManagement::HoneywellControlInterface
{

public:

    DECLARE_COLLECTABLE(LMGroupHoneywell);

    LMGroupHoneywell(Cti::RowReader & rdr);

    virtual ~LMGroupHoneywell();

    virtual CtiLMGroupBase* replicate() const;

    virtual bool sendStopControl(bool stopImmediately);
    virtual bool sendShedControl(long controlMinutes);

    virtual bool doesStopRequireCommandAt(const CtiTime & currentTime) const;

    virtual bool sendCycleControl(long dutyCycle,
                                  long controlDurationSeconds,
                                  bool rampInOption,
                                  bool rampOutOption);

    //Unused
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

};

typedef boost::shared_ptr<LMGroupHoneywell> LMGroupHoneywellPtr;