#pragma once

#include <list>
#include <string>

#include "LitePoint.h"
#include "Attribute.h"

/**
 * This Class is to hold the accessors for Attribute
 * information.
 *
 * It is to mirror the Java class in functionality but currently
 * only supports the using of Database tables to access the
 * attributes.
 *
 * When implemented this file should also be used to access the
 * deviceDefinition.xml file for Attribute information.
 */


class IM_EX_CTIBASE AttributeService
{
    public:
        using AttributeMapping = std::map<Attribute, LitePoint>;
        virtual AttributeMapping getPointsByPaoAndAttributes(int paoId, std::vector<Attribute>& AttributeList);

        // sacrilege
        virtual LitePoint getLitePointById(int pointId);

        virtual ~AttributeService() = default;

    private:

        std::vector<LitePoint> getLitePointsById(const std::vector<int>& pointIds);
};
