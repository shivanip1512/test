#include "precompiled.h"

#include "Conductor.h"
#include "database_reader.h"
#include "ccid.h"
#include "ccutil.h"
#include "msg_pdata.h"
#include "pointdefs.h"
#include "pointtypes.h"

using Cti::CapControl::deserializeFlag;



Conductor::Conductor( StrategyManager * strategyManager )
    :   Controllable( strategyManager ),
        _currentVarLoadPointId( 0 ),
        _currentVarPointQuality( NormalQuality ),
        _lastCurrentVarPointUpdateTime( gInvalidCtiTime ),
        _estimatedVarLoadPointId( 0 ),
        _estimatedVarLoadPointValue( 0 ),
        _usePhaseData( false ),
        _phaseBid( 0 ),
        _phaseCid( 0 ),
        _totalizedControlFlag( false ),
        _phaseAvalueBeforeControl( 0 ),
        _phaseBvalueBeforeControl( 0 ),
        _phaseCvalueBeforeControl( 0 ),
        _currentWattLoadPointId( 0 ),
        _currentWattPointQuality( NormalQuality ),
        _lastWattPointTime( gInvalidCtiTime ),
        _currentVoltLoadPointId( 0 ),
        _currentVoltPointQuality( NormalQuality ),
        _lastVoltPointTime( gInvalidCtiTime ),
        _powerFactorPointId( 0 ),
        _powerFactorValue( -1.0 ),
        _estimatedPowerFactorPointId( 0 ),
        _estimatedPowerFactorValue( -1.0 ),
        _dailyOperationsAnalogPointId( 0 ),
        _currentDailyOperations( 0 ),
        _iVCount( 0 ),
        _iVControl( 0 ),
        _iVControlTot( 0 ),
        _iWCount( 0 ),
        _iWControl( 0 ),
        _iWControlTot( 0 ),
        _parentId( 0 ),
        _parentName( "none" ),
        _multiMonitorFlag( false ),
        _decimalPlaces( 0 ),
        _newPointDataReceivedFlag( false ),
        _recentlyControlledFlag( false ),
        _lastOperationTime( gInvalidCtiTime ),
        _eventSequence( 0 ),
        _waiveControlFlag( false ),
        _kvarSolution( 0 ),
        _solution( "IDLE" ),
        _targetVarValue( 0 )
{

}

Conductor::Conductor( Cti::RowReader & rdr, StrategyManager * strategyManager )
    :   Controllable( rdr, strategyManager ),
        _currentVarLoadPointId( 0 ),
        _currentVarPointQuality( NormalQuality ),
        _lastCurrentVarPointUpdateTime( gInvalidCtiTime ),
        _estimatedVarLoadPointId( 0 ),
        _estimatedVarLoadPointValue( 0 ),
        _usePhaseData( false ),
        _phaseBid( 0 ),
        _phaseCid( 0 ),
        _totalizedControlFlag( false ),
        _phaseAvalueBeforeControl( 0 ),
        _phaseBvalueBeforeControl( 0 ),
        _phaseCvalueBeforeControl( 0 ),
        _currentWattLoadPointId( 0 ),
        _currentWattPointQuality( NormalQuality ),
        _lastWattPointTime( gInvalidCtiTime ),
        _currentVoltLoadPointId( 0 ),
        _currentVoltPointQuality( NormalQuality ),
        _lastVoltPointTime( gInvalidCtiTime ),
        _powerFactorPointId( 0 ),
        _powerFactorValue( -1.0 ),
        _estimatedPowerFactorPointId( 0 ),
        _estimatedPowerFactorValue( -1.0 ),
        _dailyOperationsAnalogPointId( 0 ),
        _currentDailyOperations( 0 ),
        _iVCount( 0 ),
        _iVControl( 0 ),
        _iVControlTot( 0 ),
        _iWCount( 0 ),
        _iWControl( 0 ),
        _iWControlTot( 0 ),
        _parentId( 0 ),
        _parentName( "none" ),
        _multiMonitorFlag( false ),
        _decimalPlaces( 0 ),
        _newPointDataReceivedFlag( false ),
        _recentlyControlledFlag( false ),
        _lastOperationTime( gInvalidCtiTime ),
        _eventSequence( 0 ),
        _waiveControlFlag( false ),
        _kvarSolution( 0 ),
        _solution( "IDLE" ),
        _targetVarValue( 0 )
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
    std::string flags;

    rdr["CurrentVarPointQuality"]   >> _currentVarPointQuality;
    rdr["LastCurrentVarUpdateTime"] >> _lastCurrentVarPointUpdateTime;

    rdr["EstimatedVarPointValue"]   >> _estimatedVarLoadPointValue;

    rdr["PhaseAValueBeforeControl"] >> _phaseAvalueBeforeControl;
    rdr["PhaseBValueBeforeControl"] >> _phaseBvalueBeforeControl;
    rdr["PhaseCValueBeforeControl"] >> _phaseCvalueBeforeControl;

    rdr["CurrentWattPointQuality"]  >> _currentWattPointQuality;
    rdr["LastWattPointTime"]        >> _lastWattPointTime;

    rdr["CurrentVoltPointQuality"]  >> _currentVoltPointQuality;
    rdr["LastVoltPointTime"]        >> _lastVoltPointTime;

    rdr["PowerFactorValue"]         >> _powerFactorValue;
    rdr["EstimatedPFValue"]         >> _estimatedPowerFactorValue;

    rdr["CurrentDailyOperations"]   >> _currentDailyOperations;

    rdr["iVCount"]                  >> _iVCount;
    rdr["iVControlTot"]             >> _iVControlTot;
    rdr["iWCount"]                  >> _iWCount;
    rdr["iWControlTot"]             >> _iWControlTot;

    rdr["NewPointDataReceivedFlag"] >> flags;

    _newPointDataReceivedFlag       = deserializeFlag( flags );

    rdr["RecentlyControlledFlag"]   >> flags;

    _recentlyControlledFlag         = deserializeFlag( flags );

    rdr["LastOperationTime"]        >> _lastOperationTime;

    rdr["EventSeq"]                 >> _eventSequence;

    rdr["WaiveControlFlag"]         >> flags;

    _waiveControlFlag               = deserializeFlag( flags );

    rdr["KvarSolution"]             >> _kvarSolution;


}

// VAr

long Conductor::getCurrentVarLoadPointId() const
{
    return _currentVarLoadPointId;
}

void Conductor::setCurrentVarLoadPointId( const long pointId )
{
    updateStaticValue( _currentVarLoadPointId, pointId );
}

long Conductor::getCurrentVarPointQuality() const
{
    return _currentVarPointQuality;
}

void Conductor::setCurrentVarPointQuality( const long quality )
{
    updateDynamicValue( _currentVarPointQuality, quality );
}

const CtiTime & Conductor::getLastCurrentVarPointUpdateTime() const
{
    return _lastCurrentVarPointUpdateTime;
}

void Conductor::setLastCurrentVarPointUpdateTime( const CtiTime & aTime )
{
    updateDynamicValue( _lastCurrentVarPointUpdateTime, aTime );
}

long Conductor::getEstimatedVarLoadPointId() const
{
    return _estimatedVarLoadPointId;
}

void Conductor::setEstimatedVarLoadPointId( const long pointId )
{
    updateStaticValue( _estimatedVarLoadPointId, pointId );
}

double Conductor::getEstimatedVarLoadPointValue() const
{
    return _estimatedVarLoadPointValue;
}

void Conductor::setEstimatedVarLoadPointValue( const double aValue )
{
    updateDynamicValue( _estimatedVarLoadPointValue, aValue );
}

bool Conductor::getUsePhaseData() const
{
    return _usePhaseData;
}

void Conductor::setUsePhaseData( const bool flag )
{
    updateStaticValue( _usePhaseData, flag );
}

long Conductor::getPhaseBId() const
{
    return _phaseBid;
}

void Conductor::setPhaseBId( const long pointId )
{
    updateStaticValue( _phaseBid, pointId );
}

long Conductor::getPhaseCId() const
{
    return _phaseCid;
}

void Conductor::setPhaseCId( const long pointId )
{
    updateStaticValue( _phaseCid, pointId );
}

bool Conductor::getTotalizedControlFlag() const
{
    return _totalizedControlFlag;
}

void Conductor::setTotalizedControlFlag( const bool flag )
{
    updateStaticValue( _totalizedControlFlag, flag );
}

double Conductor::getPhaseAValueBeforeControl() const
{
    return _phaseAvalueBeforeControl;
}

void Conductor::setPhaseAValueBeforeControl( const double aValue )
{
    updateDynamicValue( _phaseAvalueBeforeControl, aValue );
}

double Conductor::getPhaseBValueBeforeControl() const
{
    return _phaseBvalueBeforeControl;
}

void Conductor::setPhaseBValueBeforeControl( const double aValue )
{
    updateDynamicValue( _phaseBvalueBeforeControl, aValue );
}

double Conductor::getPhaseCValueBeforeControl() const
{
    return _phaseCvalueBeforeControl;
}

void Conductor::setPhaseCValueBeforeControl( const double aValue )
{
    updateDynamicValue( _phaseCvalueBeforeControl, aValue );
}

// Watt

long Conductor::getCurrentWattLoadPointId() const
{
    return _currentWattLoadPointId;
}

void Conductor::setCurrentWattLoadPointId( const long pointId )
{
    updateStaticValue( _currentWattLoadPointId, pointId );
}

long Conductor::getCurrentWattPointQuality() const
{
    return _currentWattPointQuality;
}

void Conductor::setCurrentWattPointQuality( const long quality )
{
    updateDynamicValue( _currentWattPointQuality, quality );
}

const CtiTime & Conductor::getLastWattPointTime() const
{
    return _lastWattPointTime;
}

void Conductor::setLastWattPointTime( const CtiTime & aTime )
{
    updateDynamicValue( _lastWattPointTime, aTime );
}

// Volt

long Conductor::getCurrentVoltLoadPointId() const
{
    return _currentVoltLoadPointId;
}

void Conductor::setCurrentVoltLoadPointId( const long pointId )
{
    updateStaticValue( _currentVoltLoadPointId, pointId );
}

long Conductor::getCurrentVoltPointQuality() const
{
    return _currentVoltPointQuality;
}

void Conductor::setCurrentVoltPointQuality( const long quality )
{
    updateDynamicValue( _currentVoltPointQuality, quality );
}

const CtiTime & Conductor::getLastVoltPointTime() const
{
    return _lastVoltPointTime;
}

void Conductor::setLastVoltPointTime( const CtiTime & aTime )
{
    updateDynamicValue( _lastVoltPointTime, aTime );
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

// Integration

long Conductor::getIVCount() const
{
    return _iVCount;
}

void Conductor::setIVCount( const long aValue )
{
    updateDynamicValue( _iVCount, aValue );
}

double Conductor::getIVControl() const
{
    return _iVControl;
}

void Conductor::setIVControl( const double aValue )
{
    updateStaticValue( _iVControl, aValue );
}

double Conductor::getIVControlTot() const
{
    return _iVControlTot;
}

void Conductor::setIVControlTot( const double aValue )
{
    updateDynamicValue( _iVControlTot, aValue );
}

long Conductor::getIWCount() const
{
    return _iWCount;
}

void Conductor::setIWCount( const long aValue )
{
    updateDynamicValue( _iWCount, aValue );
}

double Conductor::getIWControl() const
{
    return _iWControl;
}

void Conductor::setIWControl( const double aValue )
{
    updateStaticValue( _iWControl, aValue );
}

double Conductor::getIWControlTot() const
{
    return _iWControlTot;
}

void Conductor::setIWControlTot( const double aValue )
{
    updateDynamicValue( _iWControlTot, aValue );
}

// Parent Info

long Conductor::getParentId() const
{
    return _parentId;
}

void Conductor::setParentId( const long parentId )
{
    updateStaticValue( _parentId, parentId );
}

const std::string & Conductor::getParentName() const
{
    return _parentName; 
}

void Conductor::setParentName( const std::string & name )
{
    updateStaticValue( _parentName, name );             // bus and feeder had this as dynamic... why?
}

const std::string & Conductor::getParentControlUnits() const
{
    return _parentControlUnits;
}

void Conductor::setParentControlUnits( const std::string & units )
{
    updateStaticValue( _parentControlUnits, units );    // bus and feeder had this as dynamic... why?
}

// Misc

const std::string & Conductor::getMapLocationId() const
{
    return _mapLocationId;
}

void Conductor::setMapLocationId( const std::string & mapLocation )
{
    updateStaticValue( _mapLocationId, mapLocation );
}

bool Conductor::getMultiMonitorFlag() const
{
    return _multiMonitorFlag;
}

void Conductor::setMultiMonitorFlag( const bool flag )
{
    updateStaticValue( _multiMonitorFlag, flag );
}

long Conductor::getDecimalPlaces() const
{
    return _decimalPlaces;
}

void Conductor::setDecimalPlaces( const long places )
{
    updateStaticValue( _decimalPlaces, places );
}

bool Conductor::getNewPointDataReceivedFlag() const
{
    return _newPointDataReceivedFlag;
}

void Conductor::setNewPointDataReceivedFlag( const bool flag )
{
    updateDynamicValue( _newPointDataReceivedFlag, flag );
}

bool Conductor::getRecentlyControlledFlag() const
{
    return _recentlyControlledFlag;
}

void Conductor::setRecentlyControlledFlag( const bool flag )
{
    updateDynamicValue( _recentlyControlledFlag, flag );
}

const CtiTime & Conductor::getLastOperationTime() const
{
    return _lastOperationTime;
}

void Conductor::setLastOperationTime( const CtiTime & aTime )
{
    updateDynamicValue( _lastOperationTime, aTime );
}

long Conductor::getEventSequence() const
{
    return _eventSequence;
}

void Conductor::setEventSequence( const long sequence )
{
    updateDynamicValue( _eventSequence, sequence );
}

bool Conductor::getWaiveControlFlag() const
{
    return _waiveControlFlag;
}

void Conductor::setWaiveControlFlag( const bool flag )
{
    updateDynamicValue( _waiveControlFlag, flag );
}

double Conductor::getKVARSolution() const
{
    return _kvarSolution;
}

void Conductor::setKVARSolution( const double aValue )
{
    updateDynamicValue( _kvarSolution, aValue );
}

const std::string & Conductor::getSolution() const
{
    return _solution;
}

void Conductor::setSolution( const std::string & text )
{
    updateStaticValue( _solution, text );   // feeder had this as dynamic - but not stored in dynamic table..?
}

double Conductor::getTargetVarValue() const
{
    return _targetVarValue;
}

void Conductor::setTargetVarValue( const double aValue )
{
    updateStaticValue( _targetVarValue, aValue );   // feeder had this as dynamic - but not stored in dynamic table..?
}

