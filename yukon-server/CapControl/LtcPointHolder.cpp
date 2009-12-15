#include "yukon.h"

#include "LtcPointHolder.h"

LtcPointHolder::LtcPointHolder()
{

}

void LtcPointHolder::addPointOffset(std::string name, int offset, CtiPointType_t type, int pointId, double pointValue)
{
    Inherited::PointOffsetTypePair offsetType(offset,type);

    nameToOffset[name] = offsetType;
    pointIdToName[pointId] = name;

    Inherited::addPointOffset(offset,type,pointId,pointValue);
}

bool LtcPointHolder::getPointValueByName(std::string name, double& value)
{
    NameToOffsetMapItr result = nameToOffset.find(name);

    if (result == nameToOffset.end())
    {
        return false;
    }

    return Inherited::getPointValueByOffsetAndType(result->second.pointOffset, result->second.pointType, value);
}

void LtcPointHolder::updatePointValue(CtiPointDataMsg* message)
{
    PointIdToNameMapItr pointItr = pointIdToName.find(message->getId());
    if (pointItr != pointIdToName.end())
    {
        NameToOffsetMapItr nameItr = nameToOffset.find(pointItr->second);
        if (nameItr != nameToOffset.end())
        {
            Inherited::addPointOffset(nameItr->second.pointOffset,nameItr->second.pointType,message->getId(),message->getValue());
        }
    }
}
