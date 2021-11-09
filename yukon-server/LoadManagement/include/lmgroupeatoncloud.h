
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
    bool sendNoControl( bool doRestore ) override;

    std::size_t getFixedSize() const override   { return sizeof( *this ); }

private:

    unsigned _vRelayID;
};

typedef boost::shared_ptr<LMGroupEatonCloud> LMGroupEatonCloudPtr;

