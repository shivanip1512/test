#pragma once

namespace Cti {
namespace CapControl {

class PointResponse
{
    public:
        PointResponse(long pointId, long deviceId, double preOpValue, double delta, bool staticDelta, long busId);
        PointResponse(const PointResponse& pr);

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


        PointResponse& operator=(const PointResponse& right);
        bool operator != (const PointResponse& right) const;
    private:
        long _pointId;
        long _deviceId;
        double _preOpValue;
        double _delta;
        bool _staticDelta;

        long _busId;


};

typedef  boost::shared_ptr<PointResponse> PointResponsePtr;

}
}
