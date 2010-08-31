#pragma once

namespace Cti {
namespace CapControl {

class PointResponse
{
    public:
        PointResponse();
        PointResponse(long pointId, long bankId, double preOpValue, double delta);

        long getPointId();
        void setPointId(long pointId);

        long getBankId();
        void setBankId(long bankId);

        double getPreOpValue();
        void setPreOpValue(double preOpValue);

        double getDelta();
        void setDelta(double delta);

        void updateDelta(long nInAvg, double value);

        /**
         * These are overloaded to compare only the bankId and pointId
         * */
        bool operator==(const PointResponse& right) const;
        bool operator<(const PointResponse& right) const;

    private:
        long _pointId;
        long _bankId;
        double _preOpValue;
        double _delta;
};

}
}
