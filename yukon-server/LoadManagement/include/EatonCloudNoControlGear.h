#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"


namespace Cti::LoadManagement {

class EatonCloudNoControlGear : public SmartGearBase,
                                public CtiLMProgramDirectGear
{
public:

    EatonCloudNoControlGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                         long           controlSeconds,
                         double       & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;

    double calculateLoadReduction( double groupCapacity ) const;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }

    bool performsControl() const override   { return false; }

};

}

