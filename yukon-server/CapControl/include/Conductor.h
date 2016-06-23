#pragma once

#include "Controllable.h"
#include "DynamicData.h"


namespace Cti
{
    class RowReader;
}

class StrategyManager;



class Conductor : public Controllable, public DynamicData
{
public:

    Conductor( StrategyManager * strategyManager = nullptr );
    Conductor( Cti::RowReader & rdr, StrategyManager * strategyManager );

    virtual ~Conductor() = default;

    // VAr

    long getCurrentVarLoadPointId() const;
    void setCurrentVarLoadPointId( const long pointId );
    long getCurrentVarPointQuality() const;
    void setCurrentVarPointQuality( const long quality );
    const CtiTime & getLastCurrentVarPointUpdateTime() const;
    void setLastCurrentVarPointUpdateTime( const CtiTime & aTime );

    long getEstimatedVarLoadPointId() const;
    void setEstimatedVarLoadPointId( const long pointId );
    double getEstimatedVarLoadPointValue() const;
    virtual void setEstimatedVarLoadPointValue( const double aValue );

    bool getUsePhaseData() const;
    void setUsePhaseData( const bool flag );
    long getPhaseBId() const;
    void setPhaseBId( const long pointId );
    long getPhaseCId() const;
    void setPhaseCId( const long pointId );
    bool getTotalizedControlFlag() const;
    void setTotalizedControlFlag( const bool flag );

    // Watt

    long getCurrentWattLoadPointId() const;
    void setCurrentWattLoadPointId( const long pointId );
    long getCurrentWattPointQuality() const;
    void setCurrentWattPointQuality( const long quality );
    const CtiTime & getLastWattPointTime() const;
    void setLastWattPointTime( const CtiTime & aTime );

    // Volt

    long getCurrentVoltLoadPointId() const;
    void setCurrentVoltLoadPointId( const long pointId );
    long getCurrentVoltPointQuality() const;
    void setCurrentVoltPointQuality( const long quality );
    const CtiTime & getLastVoltPointTime() const;
    void setLastVoltPointTime( const CtiTime & aTime );

    // Power Factor

    long getPowerFactorPointId() const;
    void setPowerFactorPointId( const long pointId );
    double getPowerFactorValue() const;
    void setPowerFactorValue( const double aValue );

    long getEstimatedPowerFactorPointId() const;
    void setEstimatedPowerFactorPointId( const long pointId );
    double getEstimatedPowerFactorValue() const;
    void setEstimatedPowerFactorValue( const double aValue );

    // Daily Operations

    long getDailyOperationsAnalogPointId() const;
    void setDailyOperationsAnalogPointId( const long pointId );
    long getCurrentDailyOperations() const;
    void setCurrentDailyOperations( const long operations );

    void setCurrentDailyOperationsAndSendMsg( const long operations, CtiMultiMsg_vec & pointChanges );

    // Integration

    long getIVCount() const;
    void setIVCount( const long aValue );
    double getIVControl() const;
    void setIVControl( const double aValue );
    double getIVControlTot() const;
    void setIVControlTot( const double aValue );
    long getIWCount() const;
    void setIWCount( const long aValue );
    double getIWControl() const;
    void setIWControl( const double aValue );
    double getIWControlTot() const;
    void setIWControlTot( const double aValue );

    // Parent Info

    long getParentId() const;
    void setParentId( const long parentId );
    const std::string & getParentName() const;
    void setParentName( const std::string & name );
    const std::string & getParentControlUnits() const;
    void setParentControlUnits( const std::string & units );

    // Misc

    const std::string & getMapLocationId() const;
    void setMapLocationId( const std::string & mapLocation );
    bool getMultiMonitorFlag() const;
    void setMultiMonitorFlag( const bool flag );
    long getDecimalPlaces() const;
    void setDecimalPlaces( const long places );


protected:

    Conductor( const Conductor & condutor ) = default;
    Conductor & operator=( const Conductor & rhs ) = delete;


private:

    void restoreStaticData( Cti::RowReader & rdr );
    void restoreDynamicData( Cti::RowReader & rdr );

    // VAr

    long    _currentVarLoadPointId;
    long    _currentVarPointQuality;
    CtiTime _lastCurrentVarPointUpdateTime;

    long    _estimatedVarLoadPointId;
    double  _estimatedVarLoadPointValue;

    bool _usePhaseData;
    long _phaseBid;
    long _phaseCid;
    bool _totalizedControlFlag;

    // Watt

    long    _currentWattLoadPointId;
    long    _currentWattPointQuality;
    CtiTime _lastWattPointTime;

    // Volt

    long    _currentVoltLoadPointId;
    long    _currentVoltPointQuality;
    CtiTime _lastVoltPointTime;

    // Power Factor

    long    _powerFactorPointId;
    double  _powerFactorValue;
    long    _estimatedPowerFactorPointId;
    double  _estimatedPowerFactorValue;

    // Daily Operations

    long    _dailyOperationsAnalogPointId;
    long    _currentDailyOperations;

    // Integration

    long    _iVCount;
    double  _iVControl;
    double  _iVControlTot;
    long    _iWCount;
    double  _iWControl;
    double  _iWControlTot;

    // Parent Info

    long        _parentId;
    std::string _parentName;
    std::string _parentControlUnits;

    // Misc
    std::string _mapLocationId;
    bool _multiMonitorFlag;
    long _decimalPlaces;
};

