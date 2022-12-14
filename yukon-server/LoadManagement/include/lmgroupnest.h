#pragma once

#include "SmartGroupBase.h"
#include "NestControlInterface.h"



class LMGroupNest : public SmartGroupBase,
                    public Cti::LoadManagement::NestControlInterface
{

public:

    DECLARE_COLLECTABLE( LMGroupNest );

    LMGroupNest( Cti::RowReader & rdr );

    virtual ~LMGroupNest();

    virtual CtiLMGroupBase* replicate() const;

    virtual bool sendStopControl( bool stopImmediately ) override;
    virtual bool sendShedControl( long controlMinutes ) override;

    bool sendCycleControl( long controlDurationSeconds ) override;

    bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;

    std::size_t getFixedSize() const override   { return sizeof( *this ); }
};

typedef boost::shared_ptr<LMGroupNest> LMGroupNestPtr;

