#pragma warning( disable : 4786)
#pragma once

#include <list>
#include <string>

#include "LitePoint.h"

class IM_EX_CTIBASE AttributeService
{
    public:
        static std::list<LitePoint> getExtraPaoPoints(int paoId);
        static LitePoint getPointByPaoAndAttribute(int paoId, const std::string& attribute);

    private:

        static LitePoint getLitePointsById(int pointId);
        static std::list<LitePoint> getLitePointsById(const std::list<int>& pointIds);
};
