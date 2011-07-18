
#include "precompiled.h"

#include <cstdlib>

#include "MultiVoltStrategy.h"


MultiVoltStrategy::MultiVoltStrategy()
    : ControlStrategy(),
    _peakUpperVoltLimit(0.0),
    _offpeakUpperVoltLimit(0.0),
    _peakLowerVoltLimit(0.0),
    _offpeakLowerVoltLimit(0.0)
{
    // empty!
}


MultiVoltStrategy::~MultiVoltStrategy()
{
    // empty!
}


void MultiVoltStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
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


double MultiVoltStrategy::getPeakLag() const
{
    return _peakLowerVoltLimit;
}


double MultiVoltStrategy::getOffPeakLag() const
{
    return _offpeakLowerVoltLimit;
}


double MultiVoltStrategy::getPeakLead() const
{
    return _peakUpperVoltLimit;
}


double MultiVoltStrategy::getOffPeakLead() const
{
    return _offpeakUpperVoltLimit;
}


void MultiVoltStrategy::setPeakLag(const double value)
{
    _peakLowerVoltLimit = value;
}


void MultiVoltStrategy::setOffPeakLag(const double value)
{
    _offpeakLowerVoltLimit = value;
}


void MultiVoltStrategy::setPeakLead(const double value)
{
    _peakUpperVoltLimit = value;
}


void MultiVoltStrategy::setOffPeakLead(const double value)
{
    _offpeakUpperVoltLimit = value;
}


void MultiVoltStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType MultiVoltStrategy::getMethodType() const
{
    if ( _controlMethod == ControlStrategy::IndividualFeederControlMethod )
    {
        return ControlStrategy::IndividualFeeder;
    }
    if ( _controlMethod == ControlStrategy::SubstationBusControlMethod )
    {
        return ControlStrategy::SubstationBus;
    }
    if ( _controlMethod == ControlStrategy::BusOptimizedFeederControlMethod )
    {
        return ControlStrategy::BusOptimizedFeeder;
    }
    if ( _controlMethod == ControlStrategy::ManualOnlyControlMethod )
    {
        return ControlStrategy::ManualOnly;
    }

    return ControlStrategy::NoMethod;
}


const std::string MultiVoltStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType MultiVoltStrategy::getUnitType() const
{
    return ControlStrategy::MultiVolt;
}


const std::string MultiVoltStrategy::getControlUnits() const
{
    return ControlStrategy::MultiVoltControlUnit;
}


void MultiVoltStrategy::execute()
{
    // empty for now....
}

