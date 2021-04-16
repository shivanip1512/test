#pragma once

#include "Policy.h"


namespace Cti           {
namespace CapControl    {

struct ControlPolicy : Policy
{
    enum ControlModes
    {
        LockedForward,
        LockedReverse,
        ReverseIdle,
        Bidirectional,
        NeutralIdle,
        Cogeneration,
        ReactiveBidirectional,
        BiasBidirectional,
        BiasCogeneration,
        ReverseCogeneration
    };

    virtual Action TapUp() = 0;
    virtual Action TapDown() = 0;

    virtual Action setSetPointValue( const double newSetPoint ) = 0;
    virtual Action AdjustSetPoint( const double changeAmount ) = 0;

    virtual double getSetPointValue() = 0;
    virtual double getSetPointBandwidth() = 0;
    virtual long getTapPosition() = 0;

    virtual PointValue getCompleteTapPosition() = 0;

    ControlModes getControlMode();

    bool inReverseFlow() const;

    bool isPowerFlowIndeterminate() const;
    bool isControlPowerFlowReverse() const;

    bool getStatusPointValue( const Attribute & attribute, const bool defaultValue ) const;

    Attribute getSetPointAttribute();
    Attribute getBandwidthAttribute();
};

std::string resolveControlMode( ControlPolicy::ControlModes mode );

}
}

