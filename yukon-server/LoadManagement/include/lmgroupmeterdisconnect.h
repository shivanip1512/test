#pragma once

#include "SmartGroupBase.h"
#include "MeterDisconnectControlInterface.h"



class LMGroupMeterDisconnect : public SmartGroupBase,
                               public Cti::LoadManagement::MeterDisconnectControlInterface
{

public:

    DECLARE_COLLECTABLE( LMGroupMeterDisconnect );

    LMGroupMeterDisconnect( Cti::RowReader & rdr );

    virtual CtiLMGroupBase* replicate() const;

    bool sendStopControl( bool stopImmediately ) override;
    bool sendShedControl( long controlMinutes ) override;

    bool sendControl( long controlSeconds ) override;

    bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;
};

typedef boost::shared_ptr<LMGroupMeterDisconnect> LMGroupMeterDisconnectPtr;

