#include "yukon.h"

#include "PointResponseManager.h"

#include <utility>
#include <algorithm>

using std::vector;
using std::map;
using std::pair;


namespace Cti {
namespace CapControl {

PointResponseManager::PointResponseManager()
{

}

bool PointResponseManager::addPointResponse(PointResponse pointResponse)
{
    pair<PointResponseMap::iterator,bool> ret;

    ret = _pointResponses.insert(std::make_pair(pointResponse.getPointId(),pointResponse));

    return ret.second;
}

bool PointResponseManager::updatePointResponseDelta(const CtiCCMonitorPoint& point)
{
    PointResponseMap::iterator itr = _pointResponses.find(point.getBankId());

    //Not Found Check
    if (itr == _pointResponses.end())
    {
        throw NotFoundException();
    }

    itr->second.updateDelta(point.getNInAvg(),point.getValue());

    return true;
}

bool PointResponseManager::updatePointResponsePreOpValue(long pointId, double newValue)
{
    PointResponseMap::iterator itr = _pointResponses.find(pointId);

    //Not Found Check
    if (itr == _pointResponses.end())
    {
        throw NotFoundException();
    }

    itr->second.updatePreOpValue(newValue);
    return true;
}

PointResponse PointResponseManager::getPointResponse(long pointId)
{
    PointResponseMap::iterator itr = _pointResponses.find(pointId);

    //I would like to change this to exception instead of returning a default value
    if (itr == _pointResponses.end())
    {
        throw NotFoundException();
    }

    return itr->second;
}

std::vector<PointResponse> PointResponseManager::getPointResponses()
{
    vector<PointResponse> pointResponses;

    pointResponses.reserve(_pointResponses.size());

    for each (PointResponseMap::value_type itr in _pointResponses)
    {
        pointResponses.push_back(itr.second);
    }

    return pointResponses;
}

}
}
