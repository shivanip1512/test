#pragma once

namespace Cti {
namespace CapControl {

class PointResponse
{
    public:
        PointResponse(long pointId, long bankId, double preOpValue, double delta);

        long getPointId() const;
        long getBankId() const;
        double getPreOpValue() const;
        double getDelta() const;

        void updateDelta(long nInAvg, double value);
        void updatePreOpValue(double preOpValue);

    private:
        long _pointId;
        long _bankId;
        double _preOpValue;
        double _delta;
};

}
}
