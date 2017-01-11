#pragma once

#include "PointAttribute.h"

#include <set>

namespace Cti {

class IM_EX_CTIBASE MetricIdLookup
{
public:
    using MetricId = unsigned short;
    using MetricIds = std::set<MetricId>;

    struct AttributeDescriptor
    {
        Attribute attrib;
        int magnitude;
    };

    static void AddMetricForAttribute(const AttributeDescriptor & attribute, const MetricId metric);
    static void AddUnknownAttribute (const AttributeNotFound & ex);  //  Attribute lookup failed during load

    static MetricId  GetMetricId (const Attribute &attrib);

    static Attribute GetAttribute(const MetricId metric);
    static AttributeDescriptor GetAttributeDescription(const MetricId metric);

    static std::vector<AttributeNotFound> getUnknownAttributes();

private:
    using attribute_bimap = boost::bimap< Attribute, MetricId >;
    using position = attribute_bimap::value_type;

    static attribute_bimap attributes;
    static std::map< Attribute, int > attributeMagnitudes;
    static std::vector<AttributeNotFound> unknownAttributes;
};

struct IM_EX_CTIBASE MetricMappingNotFound : std::exception
{
    std::string detail;
    const Attribute& attribute;

    MetricMappingNotFound(const Attribute &attrib);

    const char* what() const override;
};

struct IM_EX_CTIBASE AttributeMappingNotFound : std::exception
{
    std::string detail;
    const unsigned short metricId;

    AttributeMappingNotFound(const unsigned short metric);

    const char* what() const override;
};

}
