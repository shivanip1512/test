
#include "precompiled.h"

#include <cstdlib>

#include "KVarStrategy.h"


KVarStrategy::KVarStrategy()
    : ControlStrategy(),
    _peakKVarLeading(0.0),
    _offpeakKVarLeading(0.0),
    _peakKVarLagging(0.0),
    _offpeakKVarLagging(0.0)
{
    // empty!
}

KVarStrategy::~KVarStrategy()
{
    // empty!
}


double KVarStrategy::getPeakLag() const
{
    return _peakKVarLagging;
}


double KVarStrategy::getOffPeakLag() const
{
    return _offpeakKVarLagging;
}


double KVarStrategy::getPeakLead() const
{
    return _peakKVarLeading;
}


double KVarStrategy::getOffPeakLead() const
{
    return _offpeakKVarLeading;
}


void KVarStrategy::setPeakLag(const double value)
{
    _peakKVarLagging = value;
}


void KVarStrategy::setOffPeakLag(const double value)
{
    _offpeakKVarLagging = value;
}


void KVarStrategy::setPeakLead(const double value)
{
    _peakKVarLeading = value;
}


void KVarStrategy::setOffPeakLead(const double value)
{
    _offpeakKVarLeading = value;
}


void KVarStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType KVarStrategy::getMethodType() const
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


const std::string KVarStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType KVarStrategy::getUnitType() const
{
    return ControlStrategy::KVar;
}


const std::string KVarStrategy::getControlUnits() const
{
    return ControlStrategy::KVarControlUnit;
}


void KVarStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
{
    double newValue = std::atof( value.c_str() );

    if (name == "KVAR Leading")
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


void KVarStrategy::execute()
{
    // empty for now....
}

