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

    bool sendCycleControl( long controlDurationSeconds ) override;

    bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;
};

typedef boost::shared_ptr<LMGroupItron> LMGroupItronPtr;

