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

    bool setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp);
    bool setTwoWayAnalogPointValue(long pointID, double value, CtiTime timestamp);
    bool setTwoWayPulseAccumulatorPointValue(long pointID, double value, CtiTime timestamp);

    void addAllCBCPointsToRegMsg(std::set<long>& pointList);
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr, LONG cbcState, const CtiTime timestamp);

    void assignTwoWayPointsAndAttributes( const std::vector<LitePoint> & points,
                                          const std::map<Attribute, std::string> & overloads );
    void assignTwoWayPoint( const LitePoint & point );

protected:

    std::unique_ptr<LastControlReason>      _lastControlReason;
    std::unique_ptr<IgnoredControlReason>   _ignoredControlReason;

private:

    bool isTimestampNew(long pointID, CtiTime timestamp);
    bool setTwoWayPointValue(long pointID, double value, CtiPointType_t type, CtiTime timestamp);

    long _paoid;
    std::string _paotype;

    PointValueHolder  _pointValues;

    std::map <int, CtiPointType_t> _pointidPointtypeMap;

    using AttributeToPointInfo = std::map<Attribute, LitePoint>;

    AttributeToPointInfo    _attributes;

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


struct CtiCCTwoWayPointsFactory
{
    static CtiCCTwoWayPoints * Create( const long paoID, const std::string & paoType );
};

