
#include "yukon.h"

#include <cmath>
#include <cstdlib>

#include "IVVCStrategy.h"


IVVCStrategy::IVVCStrategy()
    : ControlStrategy(),
    _peakUpperVoltLimit(0.0),
    _offpeakUpperVoltLimit(0.0),
    _peakLowerVoltLimit(0.0),
    _offpeakLowerVoltLimit(0.0),
    _peakTargetPF(0.0),
    _offpeakTargetPF(0.0),
    _peakMinBankOpen(0.0),
    _offpeakMinBankOpen(0.0),
    _peakMinBankClose(0.0),
    _offpeakMinBankClose(0.0),
    _peakVoltWeight(0.0),
    _offpeakVoltWeight(0.0),
    _peakPFWeight(0.0),
    _offpeakPFWeight(0.0),
    _peakDecisionWeight(0.0),
    _offpeakDecisionWeight(0.0)
{
    // empty!
}


IVVCStrategy::~IVVCStrategy()
{
    // empty!
}


void IVVCStrategy::restoreParameters( const std::string &name, const std::string &type, const std::string &value )
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
    else if (name == "Target PF")
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
    else if (name == "Volt Weight")
    {
        if (type == "PEAK")
        {
            _peakVoltWeight = newValue;
        }
        else
        {
            _offpeakVoltWeight = newValue;
        }
    }
    else if (name == "PF Weight")
    {
        if (type == "PEAK")
        {
            _peakPFWeight = newValue;
        }
        else
        {
            _offpeakPFWeight = newValue;
        }
    }
    else if (name == "Decision Weight")
    {
        if (type == "PEAK")
        {
            _peakDecisionWeight = newValue;
        }
        else
        {
            _offpeakDecisionWeight = newValue;
        }
    }
}


const ControlStrategy::ControlMethodType IVVCStrategy::getMethodType() const
{
    return ControlStrategy::SubstationBus;
}


const std::string IVVCStrategy::getControlMethod() const
{
    return ControlStrategy::SubstationBusControlMethod;
}


void IVVCStrategy::setControlMethod(const std::string & method)
{
    // empty!
}


const ControlStrategy::ControlUnitType IVVCStrategy::getUnitType() const
{
    return ControlStrategy::IntegratedVoltVar;
}


const std::string IVVCStrategy::getControlUnits() const
{
    return ControlStrategy::IntegratedVoltVarControlUnit;
}


void IVVCStrategy::execute()
{
    // empty for now....
}

