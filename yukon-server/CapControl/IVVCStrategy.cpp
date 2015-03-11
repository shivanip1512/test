
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
    _offpeakMaxConsecutiveCapBankOps(2),
    _lowVoltageViolationBandwidth(3.0),
    _highVoltageViolationBandwidth(1.0),
    _emergencyLowVoltageViolationCost(-150.0),
    _lowVoltageViolationCost(-10.0),
    _highVoltageViolationCost(70.0),
    _emergencyHighVoltageViolationCost(300.0),
    _powerFactorCorrectionBandwidth(0.02),
    _powerFactorCorrectionCost(20.0),
    _powerFactorCorrectionMaxCost(2.0),
    _regulatorCommReportingPercentage(100.0),
    _capbankCommReportingPercentage(100.0),
    _voltageMonitorCommReportingPercentage(100.0),
    _controlMethod(SubstationBusControlMethod)
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
    else if (name == "Low Voltage Violation")
    {
        if (type == "BANDWIDTH")
        {
            _lowVoltageViolationBandwidth = newValue;
        }
        else if (type == "COST")
        {
            _lowVoltageViolationCost = newValue;
        }
        else    // type == "EMERGENCY_COST"
        {
            _emergencyLowVoltageViolationCost = newValue;
        }
    }
    else if (name == "High Voltage Violation")
    {
        if (type == "BANDWIDTH")
        {
            _highVoltageViolationBandwidth = newValue;
        }
        else if (type == "COST")
        {
            _highVoltageViolationCost = newValue;
        }
        else    // type == "EMERGENCY_COST"
        {
            _emergencyHighVoltageViolationCost = newValue;
        }
    }
    else if (name == "Power Factor Correction")
    {
        if (type == "BANDWIDTH")
        {
            _powerFactorCorrectionBandwidth = newValue;
        }
        else if (type == "COST")
        {
            _powerFactorCorrectionCost = newValue;
        }
        else    // type == "MAX_COST"
        {
            _powerFactorCorrectionMaxCost = newValue;
        }
    }
    else if (name == "Comm Reporting Percentage")
    {
        if (type == "REGULATOR")
        {
            _regulatorCommReportingPercentage = newValue;
        }
        else if (type == "CAPBANK")
        {
            _capbankCommReportingPercentage = newValue;
        }
        else if (type == "VOLTAGE_MONITOR")
        {
            _voltageMonitorCommReportingPercentage = newValue;
        }
        else
        {
            // ignore rogue db entry...
        }
    }
}


const unsigned IVVCStrategy::getMaxConsecutiveCapBankOps(const bool isPeak) const
{
    return isPeak ? _peakMaxConsecutiveCapBankOps : _offpeakMaxConsecutiveCapBankOps;
}


/* 
    Currently only 2 supported options for the control method:
        SubstationBus       -- the default
        BusOptimizedFeeder
 
        Future enhancements will also allow IndividualFeeder
*/
const ControlStrategy::ControlMethodType IVVCStrategy::getMethodType() const
{
    if ( _controlMethod == BusOptimizedFeederControlMethod )
    {
        return BusOptimizedFeeder;
    }

    return SubstationBus;
}


const std::string IVVCStrategy::getControlMethod() const
{
    return _controlMethod;
}


void IVVCStrategy::setControlMethod(const std::string & method)
{
    _controlMethod = SubstationBusControlMethod;
    
    if ( method == BusOptimizedFeederControlMethod )
    {
        _controlMethod = BusOptimizedFeederControlMethod;
    }
}


const ControlStrategy::ControlUnitType IVVCStrategy::getUnitType() const
{
    return IntegratedVoltVar;
}


const std::string IVVCStrategy::getControlUnits() const
{
    return IntegratedVoltVarControlUnit;
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


const double IVVCStrategy::getLowVoltageViolationBandwidth() const
{
    return _lowVoltageViolationBandwidth;
}


const double IVVCStrategy::getHighVoltageViolationBandwidth() const
{
    return _highVoltageViolationBandwidth;
}


const double IVVCStrategy::getEmergencyLowVoltageViolationCost() const
{
    return _emergencyLowVoltageViolationCost;
}


const double IVVCStrategy::getLowVoltageViolationCost() const
{
    return _lowVoltageViolationCost;
}


const double IVVCStrategy::getHighVoltageViolationCost() const
{
    return _highVoltageViolationCost;
}


const double IVVCStrategy::getEmergencyHighVoltageViolationCost() const
{
    return _emergencyHighVoltageViolationCost;
}


const double IVVCStrategy::getPowerFactorCorrectionBandwidth() const
{
    return _powerFactorCorrectionBandwidth;
}


const double IVVCStrategy::getPowerFactorCorrectionCost() const
{
    return _powerFactorCorrectionCost;
}


const double IVVCStrategy::getPowerFactorCorrectionMaxCost() const
{
    return _powerFactorCorrectionMaxCost;
}


const double IVVCStrategy::getRegulatorCommReportingPercentage() const
{
    return _regulatorCommReportingPercentage;
}


const double IVVCStrategy::getCapbankCommReportingPercentage() const
{
    return _capbankCommReportingPercentage;
}


const double IVVCStrategy::getVoltageMonitorCommReportingPercentage() const
{
    return _voltageMonitorCommReportingPercentage;
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
//            _paoStateMap.erase(iter);   // remove from map
        }
    }
}

/*
        This whole mess needs to be revisited at some point.  The commented out erase() call above and
    the map copy below are a band-aid on some deeper architectural issues.  The issue is that during a
    database reload, we first dump all the ccmaps - this calls the destructor on them, which in turn
    calls unregisterControllable() above.  This deletes the state for that particular object (bus, feeder).
    Then we finally go to reload the strategies - we backup the existing ones and reload new ones
    from the db, but the state in the backups was already destroyed during the ccmap clearing - so no
    state is actually backed up.  Hence - no state restored.  This band-aid removes the deletion of
    the state when the associated object is destroyed - it also just does a brute force copy of the saved
    states into the new strategies.  Drawbacks - the register/unregisterControllable parallelism is
    broken (new without corresponding erase()/delete) and the bulk copy may maintain states that are no
    longer active or assigned.
 
    Additional notes:
    Q. Why is there a reference count with the states.  Each object should have its own state that is not
        shared with any other object.
    Q. An IVVC strategy is a bus level strategy - but during the cascade we assign it to feeders too - this
        creates state objects for the feeders too - this is undesirable since you can't run IVVC on the
        feeder level.  Wasting space...
    Q. ???
*/

void IVVCStrategy::restoreStates(const ControlStrategy * backup)
{
    const IVVCStrategy* p = dynamic_cast<const IVVCStrategy*>( backup );

    if (p)
    {
        // brute force copy from p to local map --

        _paoStateMap = p->_paoStateMap;
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
                //Warning bad things attached to this strategy  -- like a feeder!
            }
        }
    }
    // mutex unlocked

    for (std::list<IVVCStatePtr>::iterator b = runList.begin(), e = runList.end(); b != e; ++b)
    {

        busPtr = store->findSubBusByPAObjectID( (*b)->getPaoId());
        if (busPtr != NULL && busPtr->getStrategyId() == getStrategyId())
        {
            try
            {
                _ivvcAlgorithm.execute(*b, busPtr, this, true); 
            }
            catch ( ... )
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
            }
        }
        else
        {
            //debug
        }
    }

}
