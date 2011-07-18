
#include "precompiled.h"

#include <cstdlib>

#include "VoltStrategy.h"


VoltStrategy::VoltStrategy()
    : ControlStrategy(),
    _peakUpperVoltLimit(0.0),
    _offpeakUpperVoltLimit(0.0),
    _peakLowerVoltLimit(0.0),
    _offpeakLowerVoltLimit(0.0)
{
    // empty!
}


VoltStrategy::~VoltStrategy()
{
    // empty!
}


void VoltStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
{
    double newValue = std::atof( value.c_str() );

    if (name == "Upper Volt Limit")
    {
        if (type == "PEAK")
        {
            _peakUpperVoltLimit = newValue;
        }
        else
        {
            _offpeakUpperVoltLimit = newValue;
        }
    }
    else if (name == "Lower Volt Limit")
    {
        if (type == "PEAK")
        {
            _peakLowerVoltLimit = newValue;
        }
        else
        {
            _offpeakLowerVoltLimit = newValue;
        }
    }
}


double VoltStrategy::getPeakLag() const
{
    return _peakLowerVoltLimit;
}


double VoltStrategy::getOffPeakLag() const
{
    return _offpeakLowerVoltLimit;
}


double VoltStrategy::getPeakLead() const
{
    return _peakUpperVoltLimit;
}


double VoltStrategy::getOffPeakLead() const
{
    return _offpeakUpperVoltLimit;
}


void VoltStrategy::setPeakLag(const double value)
{
    _peakLowerVoltLimit = value;
}


void VoltStrategy::setOffPeakLag(const double value)
{
    _offpeakLowerVoltLimit = value;
}


void VoltStrategy::setPeakLead(const double value)
{
    _peakUpperVoltLimit = value;
}


void VoltStrategy::setOffPeakLead(const double value)
{
    _offpeakUpperVoltLimit = value;
}


void VoltStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}

const ControlStrategy::ControlMethodType VoltStrategy::getMethodType() const
{
    if( _controlMethod == ControlStrategy::IndividualFeederControlMethod )
    {
        return ControlStrategy::IndividualFeeder;
    }
    if( _controlMethod == ControlStrategy::SubstationBusControlMethod )
    {
        return ControlStrategy::SubstationBus;
    }
    if( _controlMethod == ControlStrategy::BusOptimizedFeederControlMethod )
    {
        return ControlStrategy::BusOptimizedFeeder;
    }
    if( _controlMethod == ControlStrategy::ManualOnlyControlMethod )
    {
        return ControlStrategy::ManualOnly;
    }

    return ControlStrategy::NoMethod;
}


const std::string VoltStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType VoltStrategy::getUnitType() const
{
    return ControlStrategy::Volts;
}


const std::string VoltStrategy::getControlUnits() const
{
    return ControlStrategy::VoltsControlUnit;
}


void VoltStrategy::execute()
{
    // empty for now....
}

