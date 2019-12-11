
#pragma once

#include "lmgroupbase.h"
#include "GroupControlInterface.h"
#include "ecobeeControlInterface.h"



class LMGroupEcobee : public CtiLMGroupBase,
                      public Cti::LoadManagement::GroupControlInterface,
                      public Cti::LoadManagement::EcobeeControlInterface
{

public:

    DECLARE_COLLECTABLE( LMGroupEcobee );

    LMGroupEcobee( Cti::RowReader & rdr );

    virtual ~LMGroupEcobee();

    CtiLMGroupBase* replicate() const override;

    bool sendStopControl( bool stopImmediately ) override;
    bool sendShedControl( long controlMinutes ) override;

    bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;

    bool sendCycleControl( long dutyCycle,
                           long controlDurationSeconds,
                           bool rampInOption,
                           bool rampOutOption,
                           bool mandatory ) override;

    bool sendSetpointControl( long controlDurationSeconds,
                              bool temperatureOption,
                              bool mandatory,
                              long temperatureOffset ) override;


    //Unused
    CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const override;
    CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const override;
    CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const override;
    CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const override;
};

typedef boost::shared_ptr<LMGroupEcobee> LMGroupEcobeePtr;
