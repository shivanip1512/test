
#include "precompiled.h"

#include <cmath>
#include <cstdlib>

#include "PFactorKWKQStrategy.h"


PFactorKWKQStrategy::PFactorKWKQStrategy()
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


PFactorKWKQStrategy::~PFactorKWKQStrategy()
{
    // empty!
}


void PFactorKWKQStrategy::restoreParameters( const std::string &name, const std::string &type, const std::string &value )
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


double PFactorKWKQStrategy::getPeakLag() const
{
    return _peakMinBankClose;
}


double PFactorKWKQStrategy::getOffPeakLag() const
{
    return _offpeakMinBankClose;
}


double PFactorKWKQStrategy::getPeakLead() const
{
    return _peakMinBankOpen;
}


double PFactorKWKQStrategy::getOffPeakLead() const
{
    return _offpeakMinBankOpen;
}


double PFactorKWKQStrategy::getPeakPFSetPoint() const
{
    return _peakTargetPF;
}


double PFactorKWKQStrategy::getOffPeakPFSetPoint() const
{
    return _offpeakTargetPF;
}


void PFactorKWKQStrategy::setPeakLag(const double value)
{
    _peakMinBankClose = value;
}


void PFactorKWKQStrategy::setOffPeakLag(const double value)
{
    _offpeakMinBankClose = value;
}


void PFactorKWKQStrategy::setPeakLead(const double value)
{
    _peakMinBankOpen = value;
}


void PFactorKWKQStrategy::setOffPeakLead(const double value)
{
    _offpeakMinBankOpen = value;
}


void PFactorKWKQStrategy::setPeakPFSetPoint(const double value)
{
    _peakTargetPF = value;
}


void PFactorKWKQStrategy::setOffPeakPFSetPoint(const double value)
{
    _offpeakTargetPF = value;
}


void PFactorKWKQStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType PFactorKWKQStrategy::getMethodType() const
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


const std::string PFactorKWKQStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType PFactorKWKQStrategy::getUnitType() const
{
    return ControlStrategy::PFactorKWKQ;
}


const std::string PFactorKWKQStrategy::getControlUnits() const
{
    return ControlStrategy::PFactorKWKQControlUnit;
}


void PFactorKWKQStrategy::execute()
{
    // empty for now....
}

