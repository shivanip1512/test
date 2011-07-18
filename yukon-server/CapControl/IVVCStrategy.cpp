
#include "precompiled.h"

#include <cmath>
#include <cstdlib>

#include "IVVCStrategy.h"
#include "ccutil.h"
#include "ccsubstationbusstore.h"

IVVCStrategy::IVVCStrategy(const PointDataRequestFactoryPtr& factory)
    : ControlStrategy(),
    _peakUpperVoltLimit(130.0),
    _offpeakUpperVoltLimit(130.0),
    _peakLowerVoltLimit(110.0),
    _offpeakLowerVoltLimit(110.0),
    _peakTargetPF(100.0),
    _offpeakTargetPF(100.0),
    _peakMinBankOpen(80.0),
    _offpeakMinBankOpen(80.0),
    _peakMinBankClose(80.0),
    _offpeakMinBankClose(80.0),
    _peakVoltWeight(1.0),
    _offpeakVoltWeight(1.0),
    _peakPFWeight(1.0),
    _offpeakPFWeight(1.0),
    _peakDecisionWeight(1.0),
    _offpeakDecisionWeight(1.0),
    _peakVoltageRegulationMargin(1.0),
    _offpeakVoltageRegulationMargin(1.0),
    _ivvcAlgorithm(factory),
    _peakMaxConsecutiveCapBankOps(2),
    _offpeakMaxConsecutiveCapBankOps(2)
{
}


IVVCStrategy::~IVVCStrategy()
{
    // empty!
}

void IVVCStrategy::setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory)
{
    _ivvcAlgorithm.setPointDataRequestFactory(factory);
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
    else if (name == "Voltage Regulation Margin")
    {
        if (type == "PEAK")
        {
            _peakVoltageRegulationMargin = newValue;
        }
        else
        {
            _offpeakVoltageRegulationMargin = newValue;
        }
    }
    else if (name == "Max Consecutive CapBank Ops.")
    {
        if (type == "PEAK")
        {
            _peakMaxConsecutiveCapBankOps = static_cast<unsigned>(newValue);
        }
        else
        {
            _offpeakMaxConsecutiveCapBankOps = static_cast<unsigned>(newValue);
        }
    }
}


const unsigned IVVCStrategy::getMaxConsecutiveCapBankOps(const bool isPeak) const
{
    return isPeak ? _peakMaxConsecutiveCapBankOps : _offpeakMaxConsecutiveCapBankOps;
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


const double IVVCStrategy::getVoltageRegulationMargin(const bool isPeak) const
{
    return isPeak ? _peakVoltageRegulationMargin : _offpeakVoltageRegulationMargin;
}


/**
 * These five are overloaded to return the proper messaging
 * values.  It depends on the _isPeakTime flag being set to the
 * proper value.
 */
double IVVCStrategy::getPeakLag() const
{
    return getLowerVoltLimit(getPeakTimeFlag());
}


double IVVCStrategy::getOffPeakLag() const
{
    return getMinBankClose(getPeakTimeFlag());
}


double IVVCStrategy::getPeakLead() const
{
    return getUpperVoltLimit(getPeakTimeFlag());
}


double IVVCStrategy::getOffPeakLead() const
{
    return getMinBankOpen(getPeakTimeFlag());
}


double IVVCStrategy::getPeakPFSetPoint() const
{
    return getTargetPF(getPeakTimeFlag());
}

/*********/

void IVVCStrategy::registerControllable(const long paoid)
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


void IVVCStrategy::unregisterControllable(const long paoid)
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


void IVVCStrategy::restoreStates(const ControlStrategy * backup)
{
    const IVVCStrategy* p = dynamic_cast<const IVVCStrategy*>( backup );

    if (p)
    {
        for (PaoToStateMap::iterator b = _paoStateMap.begin(), e = _paoStateMap.end(); b != e; ++b)
        {
            PaoToStateMap::const_iterator target = p->_paoStateMap.find( b->first );

            if ( target != p->_paoStateMap.end() )
            {
                target->second.second->setFirstPass(true);
                b->second.second = target->second.second;
            }
        }
    }
}


void IVVCStrategy::execute()
{
    std::list<IVVCStatePtr>     runList;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiCCSubstationBusPtr busPtr = NULL;

    {
        CtiLockGuard<CtiMutex> guard(_mapMutex);

        for (PaoToStateMap::iterator b = _paoStateMap.begin(), e = _paoStateMap.end(); b != e; ++b)
        {
            int paoId = b->first;
            Cti::CapControl::CapControlType type = store->determineTypeById(paoId);

            if ( type == Cti::CapControl::SubBus )     // if we are a CtiCCSubstationBus object - needs condition obviously!
            {
                b->second.second->setPaoId(paoId);
                runList.push_back( b->second.second );  // add our IVVCState object to the list of objects to execute.
            }
            else
            {
                //Warning bad things attached to this strategy
            }
        }
    }
    // mutex unlocked

    for (std::list<IVVCStatePtr>::iterator b = runList.begin(), e = runList.end(); b != e; ++b)
    {

        busPtr = store->findSubBusByPAObjectID( (*b)->getPaoId());
        if (busPtr != NULL)
        {
            _ivvcAlgorithm.execute( *b, busPtr, this, true);
        }
        else
        {
            //debug
        }
    }

}
