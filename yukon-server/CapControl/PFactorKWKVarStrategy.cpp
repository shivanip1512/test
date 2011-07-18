
#include "precompiled.h"

#include <cmath>
#include <cstdlib>

#include "PFactorKWKVarStrategy.h"


PFactorKWKVarStrategy::PFactorKWKVarStrategy()
    : ControlStrategy(),
    _peakTargetPF(0.0),
    _offpeakTargetPF(0.0),
    _peakMinBankOpen(0.0),
    _offpeakMinBankOpen(0.0),
    _peakMinBankClose(0.0),
    _offpeakMinBankClose(0.0)
{
    // empty!
}


PFactorKWKVarStrategy::~PFactorKWKVarStrategy()
{
    // empty!
}


void PFactorKWKVarStrategy::restoreParameters( const std::string &name, const std::string &type, const std::string &value )
{
    double newValue = std::atof( value.c_str() );

    if (name == "Target PF")
    {
        if (newValue > 100)
        {
            newValue = -(200 - newValue);
        }

        if (type == "PEAK")
        {
            _peakTargetPF = newValue;
        }
        else
        {
            _offpeakTargetPF = newValue;
        }
    }
    else if (name == "Min. of Bank Open")
    {
        newValue = -std::fabs(newValue);

        if (type == "PEAK")
        {
            _peakMinBankOpen = newValue;
        }
        else
        {
            _offpeakMinBankOpen = newValue;
        }
    }
    else if (name == "Min. of Bank Close")
    {
        newValue = std::fabs(newValue);

        if (type == "PEAK")
        {
            _peakMinBankClose = newValue;
        }
        else
        {
            _offpeakMinBankClose = newValue;
        }
    }
}


double PFactorKWKVarStrategy::getPeakLag() const
{
    return _peakMinBankClose;
}


double PFactorKWKVarStrategy::getOffPeakLag() const
{
    return _offpeakMinBankClose;
}


double PFactorKWKVarStrategy::getPeakLead() const
{
    return _peakMinBankOpen;
}


double PFactorKWKVarStrategy::getOffPeakLead() const
{
    return _offpeakMinBankOpen;
}


double PFactorKWKVarStrategy::getPeakPFSetPoint() const
{
    return _peakTargetPF;
}


double PFactorKWKVarStrategy::getOffPeakPFSetPoint() const
{
    return _offpeakTargetPF;
}


void PFactorKWKVarStrategy::setPeakLag(const double value)
{
    _peakMinBankClose = value;
}


void PFactorKWKVarStrategy::setOffPeakLag(const double value)
{
    _offpeakMinBankClose = value;
}


void PFactorKWKVarStrategy::setPeakLead(const double value)
{
    _peakMinBankOpen = value;
}


void PFactorKWKVarStrategy::setOffPeakLead(const double value)
{
    _offpeakMinBankOpen = value;
}


void PFactorKWKVarStrategy::setPeakPFSetPoint(const double value)
{
    _peakTargetPF = value;
}


void PFactorKWKVarStrategy::setOffPeakPFSetPoint(const double value)
{
    _offpeakTargetPF = value;
}


void PFactorKWKVarStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType PFactorKWKVarStrategy::getMethodType() const
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


const std::string PFactorKWKVarStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType PFactorKWKVarStrategy::getUnitType() const
{
    return ControlStrategy::PFactorKWKVar;
}


const std::string PFactorKWKVarStrategy::getControlUnits() const
{
    return ControlStrategy::PFactorKWKVarControlUnit;
}


void PFactorKWKVarStrategy::execute()
{
    // empty for now....
}

