#pragma once

#include <map>

#include "msg_cmd.h"
#include "msg_ptreg.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"
#include "AttributeService.h"
#include "PointValueHolder.h"
#include "LitePoint.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCTwoWayPoints
{
public:
    CtiCCTwoWayPoints(long paoid, std::string paotype);

    long getPAOId() const;

    std::string getLastControlText() ;
    CtiCCTwoWayPoints& setPAOId(long paoId);

    LitePoint getPointByAttribute(const PointAttribute & attribute) const;
    int getPointIdByAttribute(const PointAttribute & attribute) const;
    double getPointValueByAttribute(PointAttribute pointAttribute);


    bool setTwoWayPointId(CtiPointType_t pointtype, int offset, long pointId);
    bool setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp);
    bool setTwoWayAnalogPointValue(long pointID, long value, CtiTime timestamp);
    bool setTwoWayPulseAccumulatorPointValue(long pointID, long value, CtiTime timestamp);

    CtiCCTwoWayPoints& addAllCBCPointsToRegMsg(std::set<long>& pointList);
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr, LONG cbcState, const CtiTime timestamp);

    CtiCCTwoWayPoints& operator=(const CtiCCTwoWayPoints& right);

    int operator==(const CtiCCTwoWayPoints& right) const;
    int operator!=(const CtiCCTwoWayPoints& right) const;

private:

    typedef std::map<PointAttribute, LitePoint> AttributePoint;
    AttributePoint    _attributes;
    PointValueHolder  _pointValues;

    std::map <int, PointAttribute> _statusOffsetAttribute;
    std::map <int, PointAttribute> _analogOffsetAttribute;
    std::map <int, PointAttribute> _accumulatorOffsetAttribute;
    std::map <int, CtiPointType_t> _pointidPointtypeMap;

    PointAttribute getAttribute(int pointtype, int offset);
    PointAttribute getAnalogAttribute(int offset);
    PointAttribute getAccumulatorAttribute(int offset);
    PointAttribute getStatusAttribute(int offset);
    bool isTimestampNew(long pointID, CtiTime timestamp);

    long _paoid;
    std::string _paotype;

    CtiTime _ovuvCountResetDate;
    CtiTime _lastOvUvDateTime;
    INT _lastControlReason;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;
};

typedef CtiCCTwoWayPoints* CtiCCTwoWayPointsPtr;
