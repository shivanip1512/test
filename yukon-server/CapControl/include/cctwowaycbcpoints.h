#pragma once

#include "types.h"
#include "Attribute.h"
#include "PointValueHolder.h"
#include "LitePoint.h"
#include "LastControlReason.h"
#include "IgnoredControlReason.h"

namespace Cti {
    class RowReader;

namespace Database {
    class DatabaseConnection;
}
}

class CtiCCTwoWayPoints;


namespace Transport
{

struct TwoWayDynamicDataTransport
{
    TwoWayDynamicDataTransport( Cti::RowReader & rdr );

    long 
        DeviceID,
        LastControl,
        Condition,
        AnalogInputOne,
        RSSI,
        IgnoredReason,
        TotalOpCount,
        UvOpCount,
        OvOpCount,
        OvUvTrackTime,
        NeutralCurrentSensor,
        IPAddress,
        UDPPort;

    double
        Voltage,
        HighVoltage,
        LowVoltage,
        DeltaVoltage,
        Temp,
        UvSetPoint,
        OvSetPoint,
        NeutralCurrentAlarmSetPoint;

    bool
        RecloseBlocked,
        ControlMode,
        AutoVoltControl,
        OpFailedNeutralCurrent,
        NeutralCurrentFault,
        BadRelay,
        DailyMaxOps,
        VoltageDeltaAbnormal,
        TempAlarm,
        DSTActive,
        NeutralLockout,
        IgnoredIndicator;

    CtiTime 
        OvUvCountResetDate,
        LastOvUvDateTime;
};

}


class CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPoints( const long paoid, const std::string & paotype,
                       std::unique_ptr<LastControlReason>    lastControlReason,
                       std::unique_ptr<IgnoredControlReason> ignoredControlReason );

    long getPAOId() const;

    std::string getLastControlText();
    std::string getIgnoredControlText();
    bool controlRejectedByVoltageLimits();
    bool checkDeltaVoltageRejection();
    bool isControlAccepted();

    void setPAOId(long paoId);

    LitePoint getPointByAttribute( const Attribute & attribute ) const;
    long getPointIdByAttribute( const Attribute & attribute ) const;
    double getPointValueByAttribute( const Attribute & attribute, const double sentinel = 0 ) const;

    bool setTwoWayStatusPointValue( const long pointID, const long value, const CtiTime & timestamp );
    bool setTwoWayAnalogPointValue( const long pointID, const double value, const CtiTime & timestamp );
    bool setTwoWayPulseAccumulatorPointValue( const long pointID, const double value, const CtiTime & timestamp );

    void addAllCBCPointsToRegMsg( std::set<long> & pointList ) const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void setDynamicData( Transport::TwoWayDynamicDataTransport & transport,
                         const long cbcState,
                         const CtiTime & timestamp );

    virtual void assignTwoWayPointsAndAttributes( const std::vector<LitePoint> & points,
                                                  const std::map<Attribute, std::string> & overloads );

protected:

    std::unique_ptr<LastControlReason>      _lastControlReason;
    std::unique_ptr<IgnoredControlReason>   _ignoredControlReason;

    bool isTimestampNew( const long pointID, const CtiTime & timestamp );
    bool setTwoWayPointValue( const long pointID, const double value, const CtiPointType_t type, const CtiTime & timestamp );

    long _paoid;
    std::string _paotype;

    std::map<long, LitePoint>   _points;
    std::map<Attribute, long>   _attributeIds;

    PointValueHolder  _pointValues;

    CtiTime _ovuvCountResetDate;
    CtiTime _lastOvUvDateTime;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;
};

inline bool operator==( const CtiCCTwoWayPoints & lhs, const CtiCCTwoWayPoints & rhs )
{
    return lhs.getPAOId() == rhs.getPAOId();
}

inline bool operator!=( const CtiCCTwoWayPoints & lhs, const CtiCCTwoWayPoints & rhs )
{
    return ! ( lhs == rhs );
}


// ------------------------------


class CtiCCTwoWayPointsCbcDnp : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbcDnp( const long paoid, const std::string & paotype,
                             std::unique_ptr<LastControlReason>    lastControlReason,
                             std::unique_ptr<IgnoredControlReason> ignoredControlReason );
};


// ------------------------------


class CtiCCTwoWayPointsCbc702x : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbc702x( const long paoid, const std::string & paotype,
                              std::unique_ptr<LastControlReason>    lastControlReason,
                              std::unique_ptr<IgnoredControlReason> ignoredControlReason );
};


// ------------------------------


class CtiCCTwoWayPointsCbc802x : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbc802x( const long paoid, const std::string & paotype,
                              std::unique_ptr<LastControlReason>    lastControlReason,
                              std::unique_ptr<IgnoredControlReason> ignoredControlReason );
};


// ------------------------------


class CtiCCTwoWayPointsCbcLogical : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbcLogical( const long paoid, const std::string & paotype,
                                 std::unique_ptr<LastControlReason>    lastControlReason,
                                 std::unique_ptr<IgnoredControlReason> ignoredControlReason );

    void assignTwoWayPointsAndAttributes( const std::vector<LitePoint> & points,
                                          const std::map<Attribute, std::string> & overloads ) override;
};


// ------------------------------


struct CtiCCTwoWayPointsFactory
{
    static CtiCCTwoWayPoints * Create( const long paoID, const std::string & paoType );
};

