#include "precompiled.h"

#include "ccareabase.h"
#include "ccutil.h"
#include "ccid.h"
#include "row_reader.h"
#include "ccsubstationbusstore.h"
#include "ccsubstation.h"

using Cti::CapControl::deserializeFlag;
using Cti::CapControl::calculatePowerFactor;


extern unsigned long _CC_DEBUG;

DEFINE_COLLECTABLE( CtiCCAreaBase, CTICCAREABASE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCAreaBase::CtiCCAreaBase()
    : Controllable(0),
      _voltReductionControlPointId(0),
      _voltReductionControlValue(0),
      _pfactor(0),
      _estPfactor(0),
      _ovUvDisabledFlag(false),
      _areaUpdatedFlag(false)
{
}

CtiCCAreaBase::CtiCCAreaBase(StrategyManager * strategyManager)
    : Controllable(strategyManager),
      _voltReductionControlPointId(0),
      _voltReductionControlValue(0),
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
    restoreStaticData(rdr);

    if ( hasDynamicData( rdr["additionalflags"] ) )
    {
        restoreDynamicData( rdr );
    }
}

CtiCCAreaBase::CtiCCAreaBase(const CtiCCAreaBase& base)
    : Controllable(base)
{
    operator=(base);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCAreaBase::~CtiCCAreaBase()
{
    if (!_subStationIds.empty())
    {
        _subStationIds.clear();
    }
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::operator=(const CtiCCAreaBase& right)
{
    Controllable::operator=(right);

    if( this != &right )
    {
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _pfactor = right._pfactor;
        _estPfactor = right._estPfactor;

        _voltReductionControlPointId = right._voltReductionControlPointId;
        _voltReductionControlValue = right._voltReductionControlValue;

        _subStationIds = right._subStationIds;

        _areaUpdatedFlag = right._areaUpdatedFlag;
    }
    return *this;
}


/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCAreaBase::restoreStaticData(Cti::RowReader& rdr)
{
    rdr["VoltReductionPointID"] >> _voltReductionControlPointId;
}

void CtiCCAreaBase::restoreDynamicData(Cti::RowReader& rdr)
{
    std::string flags;

    rdr["additionalflags"] >> flags;

    _ovUvDisabledFlag = deserializeFlag( flags, 0 );
    _areaUpdatedFlag  = deserializeFlag( flags, 3 );

    if ( _voltReductionControlPointId > 0 )
    {
        long controlValue;

        rdr["ControlValue"] >> controlValue;

        _voltReductionControlValue = controlValue;
    }
}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the controlPoint Id of the area
---------------------------------------------------------------------------*/
long CtiCCAreaBase::getVoltReductionControlPointId() const
{
    return _voltReductionControlPointId;
}

/*---------------------------------------------------------------------------
    getControlValue

    Returns the ControlValue flag of the area
---------------------------------------------------------------------------*/
bool CtiCCAreaBase::getVoltReductionControlValue() const
{
    return _voltReductionControlValue;
}

/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
bool CtiCCAreaBase::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

/*---------------------------------------------------------------------------
    getPFactor

    Returns the getPFactor of the area
---------------------------------------------------------------------------*/
double CtiCCAreaBase::getPFactor() const
{
    return _pfactor;
}
/*---------------------------------------------------------------------------
    getEstPFactor

    Returns the getEstPFactor of the area
---------------------------------------------------------------------------*/
double CtiCCAreaBase::getEstPFactor() const
{
    return _estPfactor;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the ControlPointId of the area
---------------------------------------------------------------------------*/
void CtiCCAreaBase::setVoltReductionControlPointId(long pointId)
{
    _voltReductionControlPointId = pointId;
}
/*---------------------------------------------------------------------------
    setControlValue

    Sets the ControlValue flag of the area
---------------------------------------------------------------------------*/
void CtiCCAreaBase::setVoltReductionControlValue(bool flag)
{
    _areaUpdatedFlag |= updateDynamicValue( _voltReductionControlValue, flag );
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the area
---------------------------------------------------------------------------*/
void CtiCCAreaBase::setOvUvDisabledFlag(bool flag)
{
    _areaUpdatedFlag |= updateDynamicValue( _ovUvDisabledFlag, flag );
}

/*---------------------------------------------------------------------------
    setPFactor

    Sets the PFactor of the area
---------------------------------------------------------------------------*/
void CtiCCAreaBase::setPFactor(double pfactor)
{
    _areaUpdatedFlag |= updateStaticValue( _pfactor, pfactor );
}
/*---------------------------------------------------------------------------
    setEstPFactor

    Sets the estPFactor of the area
---------------------------------------------------------------------------*/
void CtiCCAreaBase::setEstPFactor(double estpfactor)
{
    _areaUpdatedFlag |= updateStaticValue( _estPfactor, estpfactor );
}

/*---------------------------------------------------------------------------
    getAreaUpdatedFlag()

    Returns the getAreaUpdatedFlag() of the area
---------------------------------------------------------------------------*/
bool CtiCCAreaBase::getAreaUpdatedFlag() const
{
    return _areaUpdatedFlag;
}

/*---------------------------------------------------------------------------
    setAreaUpdatedFlag

    Sets the AreaUpdated flag of the area
---------------------------------------------------------------------------*/
void CtiCCAreaBase::setAreaUpdatedFlag(bool flag)
{
    _areaUpdatedFlag = flag;
}


bool CtiCCAreaBase::isSpecial() const
{
    return getPaoType() == "CCSPECIALAREA";
}



void CtiCCAreaBase::addSubstationId(long subId)
{
    _subStationIds.push_back(subId);
}

void CtiCCAreaBase::removeSubstationId(long subId)
{
    _subStationIds.erase(remove(_subStationIds.begin(), _subStationIds.end(), subId), _subStationIds.end());
}
            

void CtiCCAreaBase::updatePowerFactorData()
{
    double  totalWatts         = 0.0,
            totalVars          = 0.0,
            totalEstimatedVars = 0.0;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard( store->getMux() );

    for each ( long stationID in getSubstationIds() )
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

