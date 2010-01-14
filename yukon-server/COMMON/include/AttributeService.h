#pragma warning( disable : 4786)
#pragma once

#include <list>
#include <string>

#include "LitePoint.h"

/**
 * This Class is to hold the accessors for Attribute
 * information.
 *
 * It is to mirror the Java class in functionality but currently
 * only supports the using the Database tables to access the
 * attributes.
 *
 * When implemented this file should also be used to access the
 * deviceDefinition.xml file for Attribute information.
 */


class IM_EX_CTIBASE AttributeService
{
    public:
        static std::list<LitePoint> getExtraPaoPoints(int paoId);
        static LitePoint getPointByPaoAndAttribute(int paoId, const std::string& attribute);

    private:

        static LitePoint getLitePointsById(int pointId);
        static std::list<LitePoint> getLitePointsById(const std::list<int>& pointIds);
};
