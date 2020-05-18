#pragma once

#include "SmartGroupBase.h"
#include "ItronControlInterface.h"



class LMGroupItron : public SmartGroupBase,
                     public Cti::LoadManagement::ItronControlInterface
{

public:

    DECLARE_COLLECTABLE( LMGroupItron );

    LMGroupItron( Cti::RowReader & rdr );

    virtual CtiLMGroupBase* replicate() const;

    virtual bool sendStopControl( bool stopImmediately ) override;
    virtual bool sendShedControl( long controlMinutes ) override;

    bool sendCycleControl( long controlDurationSeconds,
                           bool rampInOption,
                           bool rampOutOption,
                           long cycleOption,
                           long dutyCyclePercent,
                           long dutyCyclePeriod,
                           long criticality ) override;

    bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;

    std::size_t getFixedSize() const override   { return sizeof( *this ); }
};

typedef boost::shared_ptr<LMGroupItron> LMGroupItronPtr;

