#pragma once

#include "types.h"
#include "PointAttribute.h"
#include "PointValueHolder.h"
#include "LitePoint.h"
#include "LastControlReason.h"

namespace Cti {
    class RowReader;

namespace Database {
    class DatabaseConnection;
}
}

class CtiCCTwoWayPoints
{
public:
    CtiCCTwoWayPoints(long paoid, std::string paotype);

    long getPAOId() const;

    std::string getLastControlText();

    void setPAOId(long paoId);

    LitePoint getPointByAttribute(const PointAttribute & attribute) const;
    int getPointIdByAttribute(const PointAttribute & attribute) const;
    double getPointValueByAttribute(PointAttribute pointAttribute, const double sentinel = 0);


    bool setTwoWayPointId(CtiPointType_t pointtype, int offset, long pointId, int stateGroupId);
    bool setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp);
    bool setTwoWayAnalogPointValue(long pointID, double value, CtiTime timestamp);
    bool setTwoWayPulseAccumulatorPointValue(long pointID, double value, CtiTime timestamp);

    void addAllCBCPointsToRegMsg(std::set<long>& pointList);
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr, LONG cbcState, const CtiTime timestamp);

    void setLastControlReasonDecoder( std::unique_ptr<LastControlReason> && reason );

protected:

    typedef std::map<int, PointAttribute>   OffsetAttributeMappings;

    OffsetAttributeMappings _statusOffsetAttribute,
                            _analogOffsetAttribute,
                            _accumulatorOffsetAttribute;

private:

    typedef std::map<PointAttribute, LitePoint> AttributePoint;
    AttributePoint    _attributes;
    PointValueHolder  _pointValues;

    std::map <int, CtiPointType_t> _pointidPointtypeMap;

    PointAttribute getAttribute(int pointtype, int offset);

    bool isTimestampNew(long pointID, CtiTime timestamp);
    bool setTwoWayPointValue(long pointID, long value, CtiPointType_t type, CtiTime timestamp);

    virtual int encodeLastControlReasonForDB() = 0;
    virtual void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp ) = 0;
    virtual long getLastControlReasonStateGroupID() const = 0;

    std::unique_ptr<LastControlReason>  _lastControlReason;

    long _paoid;
    std::string _paotype;

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

    CtiCCTwoWayPointsCbcDnp(long paoid, std::string paotype);

private:

    int encodeLastControlReasonForDB() override;
    void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp ) override;
    long getLastControlReasonStateGroupID() const override;
};


// ------------------------------


class CtiCCTwoWayPointsCbc702x : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbc702x(long paoid, std::string paotype);

private:

    int encodeLastControlReasonForDB() override;
    void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp ) override;
    long getLastControlReasonStateGroupID() const override;
};


// ------------------------------


class CtiCCTwoWayPointsCbc802x : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbc802x(long paoid, std::string paotype);

private:

    int encodeLastControlReasonForDB() override;
    void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp ) override;
    long getLastControlReasonStateGroupID() const override;
};


// ------------------------------


struct CtiCCTwoWayPointsFactory
{
    static CtiCCTwoWayPoints * Create( const long paoID, const std::string & paoType );
};

