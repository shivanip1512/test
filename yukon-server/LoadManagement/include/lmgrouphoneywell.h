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

    ~LMGroupHoneywell() override;

    CtiLMGroupBase* replicate() const override;

    bool sendStopControl(bool stopImmediately) override;
    bool sendShedControl(long controlMinutes) override;

    bool doesStopRequireCommandAt(const CtiTime & currentTime) const override;

    bool sendCycleControl( const long programID,
                           const long dutyCycle,
                           const long controlDurationSeconds,
                           const bool rampInOutOption ) override;

    bool sendSetpointControl( const long programID,
                              const bool temperatureOption,
                              const bool mandatory,
                              const int  temperatureOffset,
                              const int  controlDurationSeconds ) override;

    //Unused
    CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const override;
    CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const override;
    CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const override;
    CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const override;

};

typedef boost::shared_ptr<LMGroupHoneywell> LMGroupHoneywellPtr;