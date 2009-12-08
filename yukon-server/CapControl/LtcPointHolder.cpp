#include "yukon.h"

#include "LtcPointHolder.h"

LtcPointHolder::LtcPointHolder()
{

}

void LtcPointHolder::addPointOffset(std::string name, int offset, CtiPointType_t type, int pointId, int pointValue)
{
    Inherited::PointOffsetTypePair offsetType(offset,type);

    nameToOffset[name] = offsetType;

    Inherited::addPointOffset(offset,type,pointId,pointValue);
}

bool LtcPointHolder::getPointValueByName(std::string name, int& value)
{
    NameToOffsetMapItr result = nameToOffset.find(name);

    if (result == nameToOffset.end())
    {
        return false;
    }

    return Inherited::getPointValueByOffsetAndType(result->second.pointOffset, result->second.pointType, value);
}
