
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


const double IVVCStrategy::getUpperVoltLimit(const bool isPeak) const
{
    return isPeak ? _peakUpperVoltLimit : _offpeakUpperVoltLimit;
}


const double IVVCStrategy::getLowerVoltLimit(const bool isPeak) const
{
    return isPeak ? _peakLowerVoltLimit : _offpeakLowerVoltLimit;
}


const double IVVCStrategy::getTargetPF(const bool isPeak) const
{
    return isPeak ? _peakTargetPF : _offpeakTargetPF;
}


const double IVVCStrategy::getMinBankOpen(const bool isPeak) const
{
    return isPeak ? _peakMinBankOpen : _offpeakMinBankOpen;
}


const double IVVCStrategy::getMinBankClose(const bool isPeak) const
{
    return isPeak ? _peakMinBankClose : _offpeakMinBankClose;
}


const double IVVCStrategy::getVoltWeight(const bool isPeak) const
{
    return isPeak ? _peakVoltWeight : _offpeakVoltWeight;
}


const double IVVCStrategy::getPFWeight(const bool isPeak) const
{
    return isPeak ? _peakPFWeight : _offpeakPFWeight;
}


const double IVVCStrategy::getDecisionWeight(const bool isPeak) const
{
    return isPeak ? _peakDecisionWeight : _offpeakDecisionWeight;
}


void IVVCStrategy::registerUser(const int paoid)
{
    CtiLockGuard<CtiMutex> guard(_mapMutex);

    PaoToStateMap::iterator iter = _paoStateMap.find(paoid);

    if ( iter != _paoStateMap.end() )
    {
        iter->second.first++;       // increment reference count
    }
    else
    {
        _paoStateMap.insert(std::make_pair(paoid, std::make_pair(1, IVVCStatePtr(new IVVCState))));    
    }
}


void IVVCStrategy::unregisterUser(const int paoid)
{
    CtiLockGuard<CtiMutex> guard(_mapMutex);

    PaoToStateMap::iterator iter = _paoStateMap.find(paoid);

    if ( iter != _paoStateMap.end() )
    {
        iter->second.first--;           // decrement reference count

        if (iter->second.first == 0)    // if noone refers to me
        {
            _paoStateMap.erase(iter);   // remove from map
        }
    }
}


void IVVCStrategy::execute()
{
    std::list<IVVCStatePtr>     runList;

    {
        CtiLockGuard<CtiMutex> guard(_mapMutex);

        for (PaoToStateMap::iterator b = _paoStateMap.begin(), e = _paoStateMap.end(); b != e; ++b)
        {
            if ( true )     // if we are a CtiCCSubstationBus object - needs condition obviously!
            {
                runList.push_back( b->second.second );  // add our IVVCState object to the list of objects to execute.
            }
        }
    }
    // mutex unlocked

    for (std::list<IVVCStatePtr>::iterator b = runList.begin(), e = runList.end(); b != e; ++b)
    {

        IVVCAlgorithm::execute( *b );

    }

}

