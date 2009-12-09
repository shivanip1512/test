#include "yukon.h"

#include "pointOffsetValueHolder.h"

PointOffestValueHolder::PointOffestValueHolder()
{

}

/**
 * Adds the point to the map based on type. If there is not a
 * map for the given type, it will return false.
 *
 *
 * @param offset
 * @param type
 * @param pointId
 * @param pointValue
 *
 * @return bool
 */
void PointOffestValueHolder::addPointOffset(int offset, CtiPointType_t type, int pointId, int pointValue)
{
    PointOffsetTypePair offsetType(offset,type);
    PointIdValuePair newData;

    newData.pointId = pointId;
    newData.pointValue = pointValue;

    _offsetMap[offsetType] = newData;
}

/**
 * Returns true if the Point Value was found.
 * Sets value to the value if it was was.
 *
 *
 * @param offset
 * @param type
 * @param value
 *
 * @return bool
 */
bool PointOffestValueHolder::getPointValueByOffsetAndType(int offset, CtiPointType_t type, int& value)
{
    PointOffsetTypePair offsetType(offset,type);
    OffsetMapItr idAndValue = _offsetMap.find(offsetType);

    if (idAndValue == _offsetMap.end())
    {
        return false;
    }

    value = idAndValue->second.pointValue;
    return true;
}

/**
 * Returns true if the Point ValueId was found.
 * Sets pointId to the pointId if it was was.
 *
 *
 * @param offset
 * @param type
 * @param pointId
 *
 * @return bool
 */
bool PointOffestValueHolder::getPointIdByOffsetAndType(int offset, CtiPointType_t type, int& pointId)
{
    PointOffsetTypePair offsetType(offset,type);
    OffsetMapItr idAndValue = _offsetMap.find(offsetType);

    if (idAndValue == _offsetMap.end())
    {
        return false;
    }

    pointId = idAndValue->second.pointId;
    return true;
}
