#pragma once

namespace Cti {
namespace CapControl {

class PointResponse
{
    public:
        PointResponse();
        PointResponse(long pointId, long bankId, double preOpValue, double delta);

        long getPointId() const;
        void setPointId(long pointId);

        long getBankId() const;
        void setBankId(long bankId);

        double getPreOpValue() const;
        void setPreOpValue(double preOpValue);

        double getDelta() const;
        void setDelta(double delta);

        void updateDelta(long nInAvg, double value);

    private:
        long _pointId;
        long _bankId;
        double _preOpValue;
        double _delta;
};

}
}
