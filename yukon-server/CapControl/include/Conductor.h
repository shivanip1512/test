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

    long getCurrentVarLoadPointId() const;
    long getCurrentWattLoadPointId() const;
    long getCurrentVoltLoadPointId() const;

    const std::string & getMapLocationId() const;
    bool getMultiMonitorFlag() const;

    bool getUsePhaseData() const;
    long getPhaseBId() const;
    long getPhaseCId() const;
    bool getTotalizedControlFlag() const;


    void setCurrentVarLoadPointId( const long pointId );
    void setCurrentWattLoadPointId( const long pointId );
    void setCurrentVoltLoadPointId( const long pointId );

    void setMapLocationId( const std::string & mapLocation );
    void setMultiMonitorFlag( const bool flag );

    void setUsePhaseData( const bool flag );
    void setPhaseBId( const long pointId );
    void setPhaseCId( const long pointId );
    void setTotalizedControlFlag( const bool flag );


protected:

    Conductor( const Conductor & condutor ) = default;
    Conductor & operator=( const Conductor & rhs ) = delete;


private:

    void restoreStaticData( Cti::RowReader & rdr );

    long _currentVarLoadPointId;
    long _currentWattLoadPointId;
    long _currentVoltLoadPointId;

    std::string _mapLocationId;
    bool _multiMonitorFlag;

    bool _usePhaseData;
    long _phaseBid;
    long _phaseCid;
    bool _totalizedControlFlag;

};

