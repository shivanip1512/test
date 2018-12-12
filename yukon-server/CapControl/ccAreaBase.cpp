#include "precompiled.h"

#include "ccareabase.h"
#include "ccutil.h"
#include "ccid.h"
#include "row_reader.h"
#include "ccsubstationbusstore.h"
#include "ccsubstation.h"

using Cti::CapControl::deserializeFlag;
using Cti::CapControl::calculatePowerFactor;
using Cti::CapControl::PaoIdVector;

DEFINE_COLLECTABLE( CtiCCAreaBase, CTICCAREABASE_ID )

/*
    Constructors
*/
CtiCCAreaBase::CtiCCAreaBase(StrategyManager * strategyManager)
    :   Controllable(strategyManager),
        _voltReductionControlPointId(0),
        _voltReductionControlValue(false),
        _pfactor(0),
        _estPfactor(0),
        _ovUvDisabledFlag(false),
        _areaUpdatedFlag(false)
{
}

CtiCCAreaBase::CtiCCAreaBase(Cti::RowReader& rdr, StrategyManager * strategyManager)
    :   Controllable(rdr, strategyManager),
        _voltReductionControlPointId(0),
        _voltReductionControlValue(false),
        _pfactor(-1),
        _estPfactor(-1),
        _ovUvDisabledFlag(false),
        _areaUpdatedFlag(false)
{
    restoreStaticData( rdr );
    if ( hasDynamicData( rdr["additionalflags"] ) )
    {
        restoreDynamicData( rdr );
    }
}

/*
    Restores the static data portion of the area from the given Reader
*/
void CtiCCAreaBase::restoreStaticData(Cti::RowReader& rdr)
{
    rdr["VoltReductionPointID"] >> _voltReductionControlPointId;

    if ( _voltReductionControlPointId > 0 )
    {
        addPointId( _voltReductionControlPointId );
    }
}

/*
    Restores the dynamic data portion of the area from the given Reader
*/
void CtiCCAreaBase::restoreDynamicData(Cti::RowReader& rdr)
{
    std::string flags;

    rdr["additionalflags"] >> flags;

    _ovUvDisabledFlag = deserializeFlag( flags, Index_OvUvDisabled );
    _areaUpdatedFlag  = deserializeFlag( flags, Index_AreaUpdated );

    if ( _voltReductionControlPointId > 0 )
    {
        long controlValue;

        rdr["ControlValue"] >> controlValue;

        _voltReductionControlValue = controlValue;
    }
}

/*
    Accessors
*/
long CtiCCAreaBase::getVoltReductionControlPointId() const
{
    return _voltReductionControlPointId;
}

bool CtiCCAreaBase::getVoltReductionControlValue() const
{
    return _voltReductionControlValue;
}

bool CtiCCAreaBase::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

bool CtiCCAreaBase::getAreaUpdatedFlag() const
{
    return _areaUpdatedFlag;
}

double CtiCCAreaBase::getPFactor() const
{
    return _pfactor;
}

double CtiCCAreaBase::getEstPFactor() const
{
    return _estPfactor;
}

/*
    Mutators
        * static data should be updated through updateStaticValue().
        * dynamic data should be updated through updateDynamicValue() to maintain the state of the _dirty
            flag ensuring proper DB serialization.
        * anything that changes a value on the web display should set _areaUpdatedFlag to make sure the
            area message is sent to the UI.
*/

void CtiCCAreaBase::setVoltReductionControlPointId(const long pointId)
{
    updateStaticValue( _voltReductionControlPointId, pointId );
}

void CtiCCAreaBase::setVoltReductionControlValue(const bool flag)
{
    _areaUpdatedFlag |= updateDynamicValue( _voltReductionControlValue, flag );
}

void CtiCCAreaBase::setOvUvDisabledFlag(const bool flag)
{
    _areaUpdatedFlag |= updateDynamicValue( _ovUvDisabledFlag, flag );
}

void CtiCCAreaBase::setAreaUpdatedFlag(const bool flag)
{
    updateStaticValue( _areaUpdatedFlag, flag );
}

void CtiCCAreaBase::setPFactor(const double pfactor)
{
    _areaUpdatedFlag |= updateStaticValue( _pfactor, pfactor );
}

void CtiCCAreaBase::setEstPFactor(const double estpfactor)
{
    _areaUpdatedFlag |= updateStaticValue( _estPfactor, estpfactor );
}

/*
    Area sub-type determiner...
*/
bool CtiCCAreaBase::isSpecial() const
{
    return getPaoType() == "CCSPECIALAREA";
}

/*
    Maintain the collection of paoIDs of the areas attached substations
*/
void CtiCCAreaBase::addSubstationId(const long subId)
{
    _subStationIds.push_back(subId);
}

void CtiCCAreaBase::removeSubstationId(const long subId)
{
    _subStationIds.erase( std::remove( _subStationIds.begin(), _subStationIds.end(), subId ),
                          _subStationIds.end() );
}

PaoIdVector CtiCCAreaBase::getSubstationIds() const
{
    return _subStationIds;
}
          
/*
    The power factor values for the area are computed via the aggregate watt and var values for
        each attached substation.
*/
void CtiCCAreaBase::updatePowerFactorData()
{
    double  totalWatts         = 0.0,
            totalVars          = 0.0,
            totalEstimatedVars = 0.0;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

    for ( long stationID : getSubstationIds() )
    {
        if ( CtiCCSubstationPtr station = store->findSubstationByPAObjectID( stationID ) )
        {
            double  watts         = 0.0,
                    vars          = 0.0,
                    estimatedVars = 0.0;

            station->getPowerFactorData( watts, vars, estimatedVars );

            totalWatts         += watts;
            totalVars          += vars;
            totalEstimatedVars += estimatedVars;
        }
    }

    setPFactor( calculatePowerFactor( totalVars, totalWatts ) );
    setEstPFactor( calculatePowerFactor( totalEstimatedVars, totalWatts ) );
}

void CtiCCAreaBase::getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) const
{
    insertPointRegistration( registrationIDs, getVoltReductionControlPointId() );
}

