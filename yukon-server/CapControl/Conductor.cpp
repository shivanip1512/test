#include "precompiled.h"

#include "Conductor.h"
#include "database_reader.h"
#include "ccutil.h"
#include "msg_pdata.h"
#include "pointdefs.h"
#include "pointtypes.h"

using Cti::CapControl::deserializeFlag;



Conductor::Conductor( StrategyManager * strategyManager )
    :   Controllable( strategyManager ),
        _currentVarLoadPointId( 0 ),
        _currentWattLoadPointId( 0 ),
        _currentVoltLoadPointId( 0 ),
        _multiMonitorFlag( false ),
        _usePhaseData( false ),
        _phaseBid( 0 ),
        _phaseCid( 0 ),
        _totalizedControlFlag( false ),
        _decimalPlaces( 0 ),
        _powerFactorPointId( 0 ),
        _powerFactorValue( -1.0 ),
        _estimatedPowerFactorPointId( 0 ),
        _estimatedPowerFactorValue( -1.0 ),
        _dailyOperationsAnalogPointId( 0 ),
        _currentDailyOperations( 0 )
{

}

Conductor::Conductor( Cti::RowReader & rdr, StrategyManager * strategyManager )
    :   Controllable( rdr, strategyManager ),
        _currentVarLoadPointId( 0 ),
        _currentWattLoadPointId( 0 ),
        _currentVoltLoadPointId( 0 ),
        _multiMonitorFlag( false ),
        _usePhaseData( false ),
        _phaseBid( 0 ),
        _phaseCid( 0 ),
        _totalizedControlFlag( false ),
        _decimalPlaces( 0 ),
        _powerFactorPointId( 0 ),
        _powerFactorValue( -1.0 ),
        _estimatedPowerFactorPointId( 0 ),
        _estimatedPowerFactorValue( -1.0 ),
        _dailyOperationsAnalogPointId( 0 ),
        _currentDailyOperations( 0 )
{
    restoreStaticData( rdr );

    if ( hasDynamicData( rdr["AdditionalFlags"] ) )
    {
        restoreDynamicData( rdr );
    }

    if ( ! rdr[ "DECIMALPLACES" ].isNull() ) 
    {
        long decimalPlaces;

        rdr["DECIMALPLACES"] >> decimalPlaces;

        setDecimalPlaces( decimalPlaces );
    }
}

void Conductor::restoreStaticData( Cti::RowReader & rdr )
{
    std::string flag;

    rdr["CurrentVarLoadPointID"]  >> _currentVarLoadPointId;

    if ( _currentVarLoadPointId > 0 )
    {
        addPointId( _currentVarLoadPointId );
    }

    rdr["CurrentWattLoadPointID"] >> _currentWattLoadPointId;

    if ( _currentWattLoadPointId > 0 )
    {
        addPointId( _currentWattLoadPointId );
    }

    rdr["MapLocationID"]          >> _mapLocationId;

    rdr["CurrentVoltLoadPointID"] >> _currentVoltLoadPointId;

    if ( _currentVoltLoadPointId > 0 )
    {
        addPointId( _currentVoltLoadPointId );
    }

    rdr["MultiMonitorControl"]    >> flag;

    _multiMonitorFlag             = deserializeFlag( flag );

    rdr["usephasedata"]           >> flag;

    _usePhaseData                 = deserializeFlag( flag );

    rdr["phaseb"]                 >> _phaseBid;
    rdr["phasec"]                 >> _phaseCid;

    if ( _usePhaseData )
    {
        if ( _phaseBid > 0 )
        {
            addPointId( _phaseBid );
        }

        if ( _phaseCid > 0 )
        {
            addPointId( _phaseCid );
        }
    }

    rdr["ControlFlag"]            >> flag;

    _totalizedControlFlag         = deserializeFlag( flag );
}

void Conductor::restoreDynamicData( Cti::RowReader & rdr )
{
    rdr["PowerFactorValue"]         >> _powerFactorValue;
    rdr["EstimatedPFValue"]         >> _estimatedPowerFactorValue;

    rdr["CurrentDailyOperations"]   >> _currentDailyOperations;
}

/*
    Accessors
*/
long Conductor::getCurrentVarLoadPointId() const
{
    return _currentVarLoadPointId;
}

long Conductor::getCurrentWattLoadPointId() const
{
    return _currentWattLoadPointId;
}

long Conductor::getCurrentVoltLoadPointId() const
{
    return _currentVoltLoadPointId;
}

const std::string & Conductor::getMapLocationId() const
{
    return _mapLocationId;
}

bool Conductor::getMultiMonitorFlag() const
{
    return _multiMonitorFlag;
}

bool Conductor::getUsePhaseData() const
{
    return _usePhaseData;
}

long Conductor::getPhaseBId() const
{
    return _phaseBid;
}

long Conductor::getPhaseCId() const
{
    return _phaseCid;
}

bool Conductor::getTotalizedControlFlag() const
{
    return _totalizedControlFlag;
}

long Conductor::getDecimalPlaces() const
{
    return _decimalPlaces;
}


void Conductor::setCurrentVarLoadPointId( const long pointId )
{
    updateStaticValue( _currentVarLoadPointId, pointId );
}

void Conductor::setCurrentWattLoadPointId( const long pointId )
{
    updateStaticValue( _currentWattLoadPointId, pointId );
}

void Conductor::setCurrentVoltLoadPointId( const long pointId )
{
    updateStaticValue( _currentVoltLoadPointId, pointId );
}

void Conductor::setMapLocationId( const std::string & mapLocation )
{
    updateStaticValue( _mapLocationId, mapLocation );
}

void Conductor::setMultiMonitorFlag( const bool flag )
{
    updateStaticValue( _multiMonitorFlag, flag );
}

void Conductor::setUsePhaseData( const bool flag )
{
    updateStaticValue( _usePhaseData, flag );
}

void Conductor::setPhaseBId( const long pointId )
{
    updateStaticValue( _phaseBid, pointId );
}

void Conductor::setPhaseCId( const long pointId )
{
    updateStaticValue( _phaseCid, pointId );
}

void Conductor::setTotalizedControlFlag( const bool flag )
{
    updateStaticValue( _totalizedControlFlag, flag );
}

void Conductor::setDecimalPlaces( const long places )
{
    updateStaticValue( _decimalPlaces, places );
}


// Power Factor

long Conductor::getPowerFactorPointId() const
{
    return _powerFactorPointId;
}

void Conductor::setPowerFactorPointId( const long pointId )
{
    updateStaticValue( _powerFactorPointId, pointId );
}

double Conductor::getPowerFactorValue() const
{
    return _powerFactorValue;
}

void Conductor::setPowerFactorValue( const double aValue )
{
    updateDynamicValue( _powerFactorValue, aValue );
}

long Conductor::getEstimatedPowerFactorPointId() const
{
    return _estimatedPowerFactorPointId;
}

void Conductor::setEstimatedPowerFactorPointId( const long pointId )
{
    updateStaticValue( _estimatedPowerFactorPointId, pointId );
}

double Conductor::getEstimatedPowerFactorValue() const
{
    return _estimatedPowerFactorValue;
}

void Conductor::setEstimatedPowerFactorValue( const double aValue )
{
    updateDynamicValue( _estimatedPowerFactorValue, aValue );
}

// Daily Operations

long Conductor::getDailyOperationsAnalogPointId() const
{
    return _dailyOperationsAnalogPointId;
}

void Conductor::setDailyOperationsAnalogPointId( const long pointId )
{
    updateStaticValue( _dailyOperationsAnalogPointId, pointId );
}

long Conductor::getCurrentDailyOperations() const
{
    return _currentDailyOperations;
}

void Conductor::setCurrentDailyOperations( const long operations )
{
    updateDynamicValue( _currentDailyOperations, operations );
}

void Conductor::setCurrentDailyOperationsAndSendMsg( const long operations, CtiMultiMsg_vec & pointChanges )
{
    if ( updateDynamicValue( _currentDailyOperations, operations ) )
    {
        if ( getDailyOperationsAnalogPointId() > 0 )
        {
            pointChanges.push_back( new CtiPointDataMsg( getDailyOperationsAnalogPointId(),
                                                         getCurrentDailyOperations(),
                                                         NormalQuality,
                                                         AnalogPointType ) );
        }
    }
}


