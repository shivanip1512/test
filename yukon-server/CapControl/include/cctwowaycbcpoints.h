#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

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


    CtiCCTwoWayPoints(long paoid, string paotype);
    CtiCCTwoWayPoints(const CtiCCTwoWayPoints& cap);

    virtual ~CtiCCTwoWayPoints();

    LONG getPAOId() const;

    string getLastControlText() ;
    LONG getLastControl() ;
    INT getLastControlReason() const;


    CtiCCTwoWayPoints& setPAOId(LONG paoId);

    CtiCCTwoWayPoints& setLastControlReason();


    LitePoint getPointByAttribute(const PointAttribute & attribute) const;
    int getPointIdByAttribute(const PointAttribute & attribute) const;
    double getPointValueByAttribute(PointAttribute pointAttribute);
    CtiTime getPointTimeStampByAttribute(PointAttribute attribute);


    BOOL setTwoWayPointId(CtiPointType_t pointtype, int offset, LONG pointId);
    BOOL setTwoWayStatusPointValue(LONG pointID, LONG value, CtiTime timestamp);
    BOOL setTwoWayAnalogPointValue(LONG pointID, LONG value, CtiTime timestamp);
    BOOL setTwoWayPulseAccumulatorPointValue(LONG pointID, LONG value, CtiTime timestamp);

    BOOL isLastControlReasonUpdated(LONG pointID, LONG reason );


    CtiCCTwoWayPoints& addAllCBCPointsToRegMsg(std::set<long>& pointList);
    BOOL isDirty();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);
    CtiCCTwoWayPoints* replicate() const;

    CtiCCTwoWayPoints& operator=(const CtiCCTwoWayPoints& right);

    int operator==(const CtiCCTwoWayPoints& right) const;
    int operator!=(const CtiCCTwoWayPoints& right) const;


    //Possible states
    static int Open;
    static int Closed;

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

    LONG _paoid;
    string _paotype;

    CtiTime _ovuvCountResetDate;
    CtiTime _lastOvUvDateTime;
    INT _lastControlReason;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

};

typedef CtiCCTwoWayPoints* CtiCCTwoWayPointsPtr;
