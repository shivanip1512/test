
#include "precompiled.h"

#include <cstdlib>

#include "MultiVoltVarStrategy.h"


MultiVoltVarStrategy::MultiVoltVarStrategy()
    : ControlStrategy(),
    _peakUpperVoltLimit(0.0),
    _offpeakUpperVoltLimit(0.0),
    _peakLowerVoltLimit(0.0),
    _offpeakLowerVoltLimit(0.0),
    _peakKVarLeading(0.0),
    _offpeakKVarLeading(0.0),
    _peakKVarLagging(0.0),
    _offpeakKVarLagging(0.0)
{
    // empty!
}


MultiVoltVarStrategy::~MultiVoltVarStrategy()
{
    // empty!
}


void MultiVoltVarStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
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
    else if (name == "KVAR Leading")
    {
        if (type == "PEAK")
        {
            _peakKVarLeading = newValue;
        }
        else
        {
            _offpeakKVarLeading = newValue;
        }
    }
    else if (name == "KVAR Lagging")
    {
        if (type == "PEAK")
        {
            _peakKVarLagging = newValue;
        }
        else
        {
            _offpeakKVarLagging = newValue;
        }
    }
}


double MultiVoltVarStrategy::getPeakLag() const
{
    return _peakLowerVoltLimit;
}


double MultiVoltVarStrategy::getOffPeakLag() const
{
    return _offpeakLowerVoltLimit;
}


double MultiVoltVarStrategy::getPeakLead() const
{
    return _peakUpperVoltLimit;
}


double MultiVoltVarStrategy::getOffPeakLead() const
{
    return _offpeakUpperVoltLimit;
}


double MultiVoltVarStrategy::getPeakVARLag() const
{
    return _peakKVarLagging;
}


double MultiVoltVarStrategy::getOffPeakVARLag() const
{
    return _offpeakKVarLagging;
}


double MultiVoltVarStrategy::getPeakVARLead() const
{
    return _peakKVarLeading;
}


double MultiVoltVarStrategy::getOffPeakVARLead() const
{
    return _offpeakKVarLeading;
}


void MultiVoltVarStrategy::setPeakLag(const double value)
{
    _peakLowerVoltLimit = value;
}


void MultiVoltVarStrategy::setOffPeakLag(const double value)
{
    _offpeakLowerVoltLimit = value;
}


void MultiVoltVarStrategy::setPeakLead(const double value)
{
    _peakUpperVoltLimit = value;
}


void MultiVoltVarStrategy::setOffPeakLead(const double value)
{
    _offpeakUpperVoltLimit = value;
}


void MultiVoltVarStrategy::setPeakVARLag(const double value)
{
    _peakKVarLagging = value;
}


void MultiVoltVarStrategy::setOffPeakVARLag(const double value)
{
    _offpeakKVarLagging = value;
}


void MultiVoltVarStrategy::setPeakVARLead(const double value)
{
    _peakKVarLeading = value;
}


void MultiVoltVarStrategy::setOffPeakVARLead(const double value)
{
    _offpeakKVarLeading = value;
}


void MultiVoltVarStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType MultiVoltVarStrategy::getMethodType() const
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


const std::string MultiVoltVarStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType MultiVoltVarStrategy::getUnitType() const
{
    return ControlStrategy::MultiVoltVar;
}


const std::string MultiVoltVarStrategy::getControlUnits() const
{
    return ControlStrategy::MultiVoltVarControlUnit;
}


void MultiVoltVarStrategy::execute()
{
    // empty for now....
}

