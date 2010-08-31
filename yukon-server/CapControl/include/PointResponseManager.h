#pragma once

#include "PointResponse.h"
#include "ccmonitorpoint.h"

#include <map>
#include <vector>

namespace Cti {
namespace CapControl {

class PointResponseManager
{
    public:
        PointResponseManager();

        bool addPointResponse(PointResponse pointResponse);

        bool updatePointResponseDeltas(const CtiCCMonitorPoint& point);
        bool updatePointResponsePreOpValues(long pointId, double newValue);

        PointResponse getPointResponse(long pointId);
        std::vector<PointResponse> getPointResponses();

    private:
        typedef std::map<long,PointResponse> PointResponseMap;

        PointResponseMap _pointResponses;
};

}
}
