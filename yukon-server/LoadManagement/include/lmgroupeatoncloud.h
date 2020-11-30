
#pragma once

#include "SmartGroupBase.h"
#include "EatonCloudControlInterface.h"



class LMGroupEatonCloud : public SmartGroupBase,
                          public Cti::LoadManagement::EatonCloudControlInterface
{
public:

    DECLARE_COLLECTABLE( LMGroupEatonCloud );

    LMGroupEatonCloud( Cti::RowReader & rdr );

    virtual CtiLMGroupBase* replicate() const;

    virtual bool sendStopControl( bool stopImmediately ) override;
    virtual bool sendShedControl( long controlMinutes ) override;

    bool sendCycleControl( CycleControlParameters parameters ) override;
    bool sendTimeRefreshControl( long shedTimeSeconds ) override;
    bool sendNoControl() override;

    bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;

    std::size_t getFixedSize() const override   { return sizeof( *this ); }
};

typedef boost::shared_ptr<LMGroupEatonCloud> LMGroupEatonCloudPtr;

