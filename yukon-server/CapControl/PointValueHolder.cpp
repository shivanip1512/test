#include "precompiled.h"

#include "PointValueHolder.h"

PointValueHolder::PointValueHolder()
{

}

void PointValueHolder::addPointValue(int pointId, double pointValue, CtiTime pointTime)
{
    ValueTimePair vt;

    vt.value = pointValue;
    vt.time = pointTime;

    _valueMap[pointId] = vt;
}

bool PointValueHolder::getPointValue(int pointId, double& value) const
{
    ValueMapItr itr = _valueMap.find(pointId);

    if (itr == _valueMap.end())
    {
        return false;
    }

    value = itr->second.value;
    return true;
}

bool PointValueHolder::getPointTime(int pointId, CtiTime& time) const
{
    ValueMapItr itr = _valueMap.find(pointId);

    if (itr == _valueMap.end())
    {
        return false;
    }

    time = itr->second.time;
    return true;
}

void PointValueHolder::updatePointValue(CtiPointDataMsg* message)
{
    addPointValue(message->getId(),message->getValue(),message->getTime());
}

PointValueHolder& PointValueHolder::operator=(const PointValueHolder& right)
{
    for each (const std::pair<int,ValueTimePair> itr in right._valueMap)
    {
        int pointId = itr.first;
        ValueTimePair newValue = itr.second;

        _valueMap[pointId] = newValue;
    }

    return *this;
}
