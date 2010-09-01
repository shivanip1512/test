#pragma once

#include "Exceptions.h"
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

        /**
         * Updates the Delta value for the monitor point.
         *
         * throws NotFoundException
         *
         * @param monitorPoint
         *
         * @return bool
         */
        bool updatePointResponseDelta(const CtiCCMonitorPoint& monitorPoint);

        /**
         * Updates the PreOpValue for the PointResponse with the passed
         * in pointId.
         *
         * throws NotFoundException
         *
         * @param pointId
         * @param newValue
         *
         * @return bool
         */
        bool updatePointResponsePreOpValue(long pointId, double newValue);

        /**
         * Returns the PointResponse with the pointId.
         *
         * throws NotFoundException
         *
         * @param pointId
         *
         * @return PointResponse
         */
        PointResponse getPointResponse(long pointId);

        /**
         * Returns all PointResponses.
         *
         * @return std::vector<PointResponse>
         */
        std::vector<PointResponse> getPointResponses();

    private:
        typedef std::map<long,PointResponse> PointResponseMap;

        PointResponseMap _pointResponses;
};

}
}
