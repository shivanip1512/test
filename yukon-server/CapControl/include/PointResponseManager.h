#pragma once

#include "PointResponse.h"

class CtiCCMonitorPoint;


namespace Cti {

namespace Database
{ 

class DatabaseConnection;

}

namespace CapControl {

class PointResponseManager
{
public:

    PointResponseManager();

    /**
     * Adds PointResponse to
     *
     * @param pointResponse
     *
     * @return bool
     */
    bool addPointResponse(PointResponse pointResponse);

    /**
     * Sets new delta value and static flag in Point Response by
     * point Id.
     *
     * throws NotFoundException
     *
     * @param pointId
     * @param newDelta
     * @param staticDelta
     *
     * @return bool
     */
    bool handlePointResponseDeltaChange(long pointId, double newDelta, bool staticDelta);

    /**
     * Updates the Delta value for the monitor point with the update
     * algorithm
     *
     * throws NotFoundException
     *
     * @param monitorPoint
     *
     * @return bool
     */
    bool updatePointResponseDelta(const CtiCCMonitorPoint& monitorPoint, double maxDelta);

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

    void serializeUpdatedPointResponses( Cti::Database::DatabaseConnection & connection );

private:

    using PointResponseMap = std::map<long, PointResponse>;

    PointResponseMap _pointResponses;
};

}
}
