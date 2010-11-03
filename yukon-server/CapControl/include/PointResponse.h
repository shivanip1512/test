#pragma once

namespace Cti {
namespace CapControl {

class PointResponse
{
    public:
        PointResponse(long pointId, long bankId, double preOpValue, double delta, bool staticDelta);

        long getPointId() const;
        long getBankId() const;
        double getPreOpValue() const;

        bool getStaticDelta() const;
        void setStaticDelta(bool staticDelta);

        double getDelta() const;
        void setDelta(double delta);

        void updateDelta(long nInAvg, double value);
        void updatePreOpValue(double preOpValue);

    private:
        long _pointId;
        long _bankId;
        double _preOpValue;
        double _delta;
        bool _staticDelta;
};

}
}
