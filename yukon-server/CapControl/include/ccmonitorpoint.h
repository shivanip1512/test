#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "ctitime.h"
#include "ctidate.h"
#include "ccutil.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCMonitorPoint : public RWCollectable
{

public:

  RWDECLARE_COLLECTABLE( CtiCCMonitorPoint )
    CtiCCMonitorPoint();
    CtiCCMonitorPoint(Cti::RowReader& rdr);
    CtiCCMonitorPoint(const CtiCCMonitorPoint& point);

    virtual ~CtiCCMonitorPoint();

    LONG getPointId() const;
    LONG getDeviceId() const;
    DOUBLE getValue() const;
    LONG getDisplayOrder() const;
    BOOL isScannable() const;
    LONG getNInAvg() const;
    DOUBLE getUpperBandwidth() const;
    DOUBLE getLowerBandwidth() const;

    CtiTime getTimeStamp() const;
    BOOL getScanInProgress() const;

    CtiCCMonitorPoint& setPointId(LONG pointId);
    CtiCCMonitorPoint& setDeviceId(LONG bankId);
    CtiCCMonitorPoint& setValue(DOUBLE value);
    CtiCCMonitorPoint& setDisplayOrder(LONG displayOrder);
    CtiCCMonitorPoint& setScannable(BOOL flag);
    CtiCCMonitorPoint& setNInAvg(LONG n);
    CtiCCMonitorPoint& setUpperBandwidth(DOUBLE upperBW);
    CtiCCMonitorPoint& setLowerBandwidth(DOUBLE lowerBW);

    CtiCCMonitorPoint& setTimeStamp(CtiTime timeStamp);
    CtiCCMonitorPoint& setScanInProgress(BOOL flag);

    boost::shared_ptr<CtiCCMonitorPoint> replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    BOOL isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void setDynamicData(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCMonitorPoint& operator=(const CtiCCMonitorPoint& right);

    int operator==(const CtiCCMonitorPoint& right) const;
    int operator!=(const CtiCCMonitorPoint& right) const;

    Cti::CapControl::Phase  getPhase() const;
    CtiCCMonitorPoint &     setPhase( const Cti::CapControl::Phase & phase );

private:

    Cti::CapControl::Phase  _phase;

    LONG _pointId;
    LONG _deviceId;

    LONG _displayOrder;
    BOOL _scannable;
    LONG _nInAvg;
    DOUBLE _upperBW;
    DOUBLE _lowerBW;
    DOUBLE _value;
    CtiTime _timeStamp;  //averaged value change.
    BOOL _scanInProgress;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(Cti::RowReader& rdr);


};

typedef boost::shared_ptr<CtiCCMonitorPoint> CtiCCMonitorPointPtr;
