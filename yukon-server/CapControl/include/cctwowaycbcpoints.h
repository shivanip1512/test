#pragma once

#include "types.h"
#include "PointAttribute.h"
#include "PointValueHolder.h"
#include "LitePoint.h"

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

    virtual std::string getLastControlText() = 0;

    CtiCCTwoWayPoints& setPAOId(long paoId);

    LitePoint getPointByAttribute(const PointAttribute & attribute) const;
    int getPointIdByAttribute(const PointAttribute & attribute) const;
    double getPointValueByAttribute(PointAttribute pointAttribute, const double sentinel = 0);


    bool setTwoWayPointId(CtiPointType_t pointtype, int offset, long pointId);
    bool setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp);
    bool setTwoWayAnalogPointValue(long pointID, double value, CtiTime timestamp);
    bool setTwoWayPulseAccumulatorPointValue(long pointID, double value, CtiTime timestamp);

    CtiCCTwoWayPoints& addAllCBCPointsToRegMsg(std::set<long>& pointList);
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr, LONG cbcState, const CtiTime timestamp);

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
    PointAttribute getAnalogAttribute(int offset);
    PointAttribute getAccumulatorAttribute(int offset);
    PointAttribute getStatusAttribute(int offset);
    bool isTimestampNew(long pointID, CtiTime timestamp);

    virtual int encodeLastControlReasonForDB() = 0;
    virtual void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp ) = 0;

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

    virtual std::string getLastControlText();

private:

    virtual int encodeLastControlReasonForDB();
    virtual void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp );
};


// ------------------------------


class CtiCCTwoWayPointsCbc702x : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbc702x(long paoid, std::string paotype);

    virtual std::string getLastControlText();

private:

    virtual int encodeLastControlReasonForDB();
    virtual void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp );
};


// ------------------------------


class CtiCCTwoWayPointsCbc802x : public CtiCCTwoWayPoints
{
public:

    CtiCCTwoWayPointsCbc802x(long paoid, std::string paotype);

    virtual std::string getLastControlText();

private:

    virtual int encodeLastControlReasonForDB();
    virtual void decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp );

    static const std::vector<std::string>   lastControlDecoder;
};


// ------------------------------


struct CtiCCTwoWayPointsFactory
{
    static CtiCCTwoWayPoints * Create( const long paoID, const std::string & paoType );
};

