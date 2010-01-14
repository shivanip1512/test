#include "yukon.h"

#include "PointValueHolder.h"

PointValueHolder::PointValueHolder()
{

}

void PointValueHolder::addPointValue(int pointId, double pointValue)
{
    _valueMap[pointId] = pointValue;
}

bool PointValueHolder::getPointValue(int pointId, double& value)
{
    ValueMapItr itr = _valueMap.find(pointId);

    if (itr == _valueMap.end())
    {
        return false;
    }

    value = itr->second;
    return true;
}

void PointValueHolder::updatePointValue(CtiPointDataMsg* message)
{
    addPointValue(message->getId(),message->getValue());
}
