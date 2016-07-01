#pragma once

#include "Controllable.h"
#include "DynamicData.h"
#include "regression.h"
#include "cctypes.h"


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
    double getRawCurrentVarLoadPointValue() const;
    virtual double getCurrentVarLoadPointValue() const;
    void setCurrentVarLoadPointValue( const double aValue, const CtiTime & timestamp );
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

    double getPhaseAValue() const;
    void setPhaseAValue( const double aValue, const CtiTime & timestamp );
    double getPhaseBValue() const;
    void setPhaseBValue( const double aValue, const CtiTime & timestamp );
    double getPhaseCValue() const;
    void setPhaseCValue( const double aValue, const CtiTime & timestamp );

    double getPhaseAValueBeforeControl() const;
    void setPhaseAValueBeforeControl( const double aValue );
    double getPhaseBValueBeforeControl() const;
    void setPhaseBValueBeforeControl( const double aValue );
    double getPhaseCValueBeforeControl() const;
    void setPhaseCValueBeforeControl( const double aValue );

    double getVarValueBeforeControl() const;
    void setVarValueBeforeControl( const double aValue );

    Cti::CapControl::PointIdVector getCurrentVarLoadPoints() const;

    // Watt

    long getCurrentWattLoadPointId() const;
    void setCurrentWattLoadPointId( const long pointId );
    double getRawCurrentWattLoadPointValue() const;
    virtual double getCurrentWattLoadPointValue() const;
    void setCurrentWattLoadPointValue( const double aValue );
    long getCurrentWattPointQuality() const;
    void setCurrentWattPointQuality( const long quality );
    const CtiTime & getLastWattPointTime() const;
    void setLastWattPointTime( const CtiTime & aTime );

    // Volt

    long getCurrentVoltLoadPointId() const;
    void setCurrentVoltLoadPointId( const long pointId );
    double getRawCurrentVoltLoadPointValue() const;
    virtual double getCurrentVoltLoadPointValue() const;
    void setCurrentVoltLoadPointValue( const double aValue );
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

    // Regression

    const CtiRegression & getRegression();
    const CtiRegression & getRegressionA();
    const CtiRegression & getRegressionB();
    const CtiRegression & getRegressionC();

    // Misc

    const std::string & getMapLocationId() const;
    void setMapLocationId( const std::string & mapLocation );
    bool getMultiMonitorFlag() const;
    void setMultiMonitorFlag( const bool flag );
    long getDecimalPlaces() const;
    void setDecimalPlaces( const long places );

    bool getNewPointDataReceivedFlag() const;
    void setNewPointDataReceivedFlag( const bool flag );

    bool getRecentlyControlledFlag() const;
    void setRecentlyControlledFlag( const bool flag );

    const CtiTime & getLastOperationTime() const;
    void setLastOperationTime( const CtiTime & aTime );

    long getEventSequence() const;
    void setEventSequence( const long sequence );

    bool getWaiveControlFlag() const;
    void setWaiveControlFlag( const bool flag );

    double getKVARSolution() const;
    void setKVARSolution( const double aValue );

    const std::string & getSolution() const;
    void setSolution( const std::string & text );

    double getTargetVarValue() const;
    void setTargetVarValue( const double aValue );

    long getCurrentVerificationCapBankOrigState() const;
    void setCurrentVerificationCapBankState( const long state );

protected:

    Conductor( const Conductor & condutor ) = default;
    Conductor & operator=( const Conductor & rhs ) = delete;

private:

    void restoreStaticData( Cti::RowReader & rdr );
    void restoreDynamicData( Cti::RowReader & rdr );

    void updateRegression( CtiRegression & regression, const std::string & label,
                           const double aValue, const CtiTime & timestamp );

    // VAr

    long    _currentVarLoadPointId;
    double  _currentVarLoadPointValue;
    long    _currentVarPointQuality;
    CtiTime _lastCurrentVarPointUpdateTime;

    long    _estimatedVarLoadPointId;
    double  _estimatedVarLoadPointValue;

    bool _usePhaseData;
    long _phaseBid;
    long _phaseCid;
    bool _totalizedControlFlag;

    double _phaseAvalue;
    double _phaseBvalue;
    double _phaseCvalue;

    double _phaseAvalueBeforeControl;
    double _phaseBvalueBeforeControl;
    double _phaseCvalueBeforeControl;

    double _varValueBeforeControl;

    // Watt

    long    _currentWattLoadPointId;
    double  _currentWattLoadPointValue;
    long    _currentWattPointQuality;
    CtiTime _lastWattPointTime;

    // Volt

    long    _currentVoltLoadPointId;
    double  _currentVoltLoadPointValue;
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

    // Regression

    CtiRegression _regression;
    CtiRegression _regressionA;
    CtiRegression _regressionB;
    CtiRegression _regressionC;

    // Misc
    std::string _mapLocationId;
    bool _multiMonitorFlag;
    long _decimalPlaces;

    bool _newPointDataReceivedFlag;
    bool _recentlyControlledFlag;

    CtiTime _lastOperationTime;

    long   _eventSequence;

    bool _waiveControlFlag;

    double _kvarSolution;

    std::string _solution;

    double _targetVarValue;

    long _currentCapBankToVerifyAssumedOrigState;
};

