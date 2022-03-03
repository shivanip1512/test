#pragma once

#include "SmartGroupBase.h"
#include "GroupControlInterface.h"
#include "honeywellControlInterface.h"


class LMGroupHoneywell : public SmartGroupBase,
                         public Cti::LoadManagement::HoneywellControlInterface
{
public:

    DECLARE_COLLECTABLE(LMGroupHoneywell);

    LMGroupHoneywell(Cti::RowReader & rdr);

    ~LMGroupHoneywell() override;

    CtiLMGroupBase* replicate() const override;

    bool sendStopControl(bool stopImmediately) override;
    bool sendShedControl(long controlMinutes) override;

    bool sendCycleControl( const long programID,
                           const long dutyCycle,
                           const long controlDurationSeconds,
                           const bool mandatory,
                           const bool rampInOutOption ) override;

    bool sendSetpointControl( const long programID,
                              const bool temperatureOption,
                              const bool mandatory,
                              const int  temperatureOffset,
                              const int  controlDurationSeconds ) override;

    std::size_t getFixedSize() const override   { return sizeof( *this ); }
};

typedef boost::shared_ptr<LMGroupHoneywell> LMGroupHoneywellPtr;