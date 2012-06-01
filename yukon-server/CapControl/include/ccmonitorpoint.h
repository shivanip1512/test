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

    long getPointId() const;
    long getDeviceId() const;
    double getValue() const;
    long getDisplayOrder() const;
    bool isScannable() const;
    long getNInAvg() const;
    double getUpperBandwidth() const;
    double getLowerBandwidth() const;
    bool getOverrideStrategy() const;

    CtiTime getTimeStamp() const;
    bool getScanInProgress() const;

    CtiCCMonitorPoint& setPointId(long pointId);
    CtiCCMonitorPoint& setDeviceId(long bankId);
    CtiCCMonitorPoint& setValue(double value);
    CtiCCMonitorPoint& setDisplayOrder(long displayOrder);
    CtiCCMonitorPoint& setScannable(bool flag);
    CtiCCMonitorPoint& setNInAvg(long n);
    CtiCCMonitorPoint& setUpperBandwidth(double upperBW);
    CtiCCMonitorPoint& setLowerBandwidth(double lowerBW);
    CtiCCMonitorPoint& getOverrideStrategy(bool overrideStrategy);

    CtiCCMonitorPoint& setTimeStamp(CtiTime timeStamp);
    CtiCCMonitorPoint& setScanInProgress(bool flag);

    boost::shared_ptr<CtiCCMonitorPoint> replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    bool isDirty() const;
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

    void updateNonDynamicData( const CtiCCMonitorPoint & rhs );

private:

    Cti::CapControl::Phase  _phase;

    bool _overrideStrategy;

    long _pointId;
    long _deviceId;

    long _displayOrder;
    bool _scannable;
    long _nInAvg;
    double _upperBW;
    double _lowerBW;
    double _value;
    CtiTime _timeStamp;  //averaged value change.
    bool _scanInProgress;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    void restore(Cti::RowReader& rdr);


};

typedef boost::shared_ptr<CtiCCMonitorPoint> CtiCCMonitorPointPtr;
