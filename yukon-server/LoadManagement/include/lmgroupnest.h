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

    bool sendCriticalCycleControl( long controlDurationSeconds ) override;

    bool sendStandardCycleControl( long controlDurationSeconds,
                                   int  prepOption,
                                   int  peakOption,
                                   int  postOption ) override;
};

typedef boost::shared_ptr<LMGroupNest> LMGroupNestPtr;

