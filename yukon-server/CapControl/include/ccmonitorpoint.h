#pragma once

#include "ctitime.h"
#include "ccutil.h"

#include <boost/shared_ptr.hpp>
#include <boost/noncopyable.hpp>


namespace Cti {
    class RowReader;
namespace Database {
    class DatabaseConnection;
}
}


class CtiCCMonitorPoint : private boost::noncopyable
{
public:

    CtiCCMonitorPoint(Cti::RowReader& rdr);

    long getPointId() const;
    long getDeviceId() const;
    long getDisplayOrder() const;
    bool isScannable() const;
    long getNInAvg() const;
    double getUpperBandwidth() const;
    double getLowerBandwidth() const;
    bool getOverrideStrategy() const;
    Cti::CapControl::Phase  getPhase() const;

    std::string getDeviceName() const;
    std::string getPointName() const;

    std::string getIdentifier() const;

    double getValue() const;
    CtiTime getTimeStamp() const;
    bool getScanInProgress() const;

    void setValue(const double value);
    void setTimeStamp(const CtiTime & timeStamp);
    void setScanInProgress(const bool flag);

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

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

    std::string _deviceName,
                _pointName;

    // dynamic data
    double _value;
    CtiTime _timeStamp;
    bool _scanInProgress;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);
};

typedef boost::shared_ptr<CtiCCMonitorPoint> CtiCCMonitorPointPtr;

