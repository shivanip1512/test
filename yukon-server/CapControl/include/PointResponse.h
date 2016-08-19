#pragma once

namespace Cti {
namespace CapControl {

struct PointResponseKey
{
    long              deviceId;
    long              pointId;

    PointResponseKey(long dId,long pId) : deviceId(dId), pointId(pId) {}
    bool operator<(const PointResponseKey &rhs) const
    {
        return ( deviceId < rhs.deviceId || (deviceId == rhs.deviceId && pointId < rhs.pointId) );
    }
};

class PointResponse
{
public:

    PointResponse(long pointId, long deviceId, double preOpValue, double delta, bool staticDelta, long busId);
    PointResponse(const PointResponse& pr) = default;

    long getPointId() const;
    long getDeviceId() const;
    double getPreOpValue() const;
    long getSubBusId() const;

    bool getStaticDelta() const;
    void setStaticDelta(bool staticDelta);

    double getDelta() const;
    void setDelta(double delta);

    void updateDelta(long nInAvg, double value);
    void updatePreOpValue(double preOpValue);

    bool isDirty() const;
    void setDirty( const bool flag );

    PointResponse& operator=(const PointResponse& right) = default;
    bool operator != (const PointResponse& right) const;

private:

    long _pointId;
    long _deviceId;
    double _preOpValue;
    double _delta;
    bool _staticDelta;

    long _busId;

    bool _isDirty;
};

typedef  boost::shared_ptr<PointResponse> PointResponsePtr;

}
}
