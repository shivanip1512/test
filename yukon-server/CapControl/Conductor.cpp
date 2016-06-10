#include "precompiled.h"

#include "Conductor.h"
#include "database_reader.h"
#include "ccutil.h"

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
        _totalizedControlFlag( false )

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
        _totalizedControlFlag( false )

{
    restoreStaticData( rdr );

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

/*
    Mutators
*/
void Conductor::setCurrentVarLoadPointId( const long pointId )
{
    _currentVarLoadPointId = pointId;
}

void Conductor::setCurrentWattLoadPointId( const long pointId )
{
    _currentWattLoadPointId = pointId;
}

void Conductor::setCurrentVoltLoadPointId( const long pointId )
{
    _currentVoltLoadPointId = pointId;
}

void Conductor::setMapLocationId( const std::string & mapLocation )
{
    _mapLocationId = mapLocation;
}

void Conductor::setMultiMonitorFlag( const bool flag )
{
    _multiMonitorFlag = flag;
//    _dirty |= setVariableIfDifferent(_multiMonitorFlag, flag);    // WHY!!
}

void Conductor::setUsePhaseData( const bool flag )
{
    _usePhaseData = flag;
}

void Conductor::setPhaseBId( const long pointId )
{
    _phaseBid = pointId;
}

void Conductor::setPhaseCId( const long pointId )
{
    _phaseCid = pointId;
}

void Conductor::setTotalizedControlFlag( const bool flag )
{
    _totalizedControlFlag = flag;
}

