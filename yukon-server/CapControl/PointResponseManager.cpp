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

    ret = _pointResponses.insert(pair<long,PointResponse>(pointResponse.getPointId(),pointResponse));

    return ret.second;
}

bool PointResponseManager::updatePointResponseDeltas(const CtiCCMonitorPoint& point)
{
    PointResponseMap::iterator itr = _pointResponses.find(point.getBankId());

    //Not Found Check
    if (itr == _pointResponses.end())
    {
        return false;
    }

    itr->updateDeltas(point.getNInAvg(),point.getValue());

    return true;
}

bool PointResponseManager::updatePointResponsePreOpValues(long pointId, double newValue)
{
    PointResponseMap::iterator itr = _pointResponses.find(pointId);

    //Not Found Check
    if (itr == _pointResponses.end())
    {
        return false;
    }

    itr->second.setPreOpValue(newValue);
    return true;
}

PointResponse PointResponseManager::getPointResponse(long pointId)
{
    PointResponseMap::iterator itr = _pointResponses.find(pointId);

    //I would like to change this to exception instead of returning a default value
    if (itr == _pointResponses.end())
    {
        return PointResponse();
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
