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
    static void AddUnknownAttribute(const std::string & attributeName);

    static MetricId  GetForAttribute (const Attribute &attrib);
    static MetricIds GetForAttributes(const std::set<Attribute> & attribs);

    static Attribute getAttribute(const MetricId metric);
    static AttributeDescriptor getAttributeDescription(const MetricId metric);

    static std::set<std::string> getUnknownAttributes();

private:
    using attribute_bimap = boost::bimap< Attribute, MetricId >;
    using position = attribute_bimap::value_type;

    static attribute_bimap attributes;
    static std::map< Attribute, int > attributeMagnitudes;
    static std::set<std::string> unknownAttributes;
};

struct IM_EX_CTIBASE MetricIdNotFound : std::exception
{
    std::string detail;

    MetricIdNotFound(const Attribute &attrib);

    const char* what() const override;
};

struct IM_EX_CTIBASE AttributeMappingNotFound : std::exception
{
    std::string detail;

    AttributeMappingNotFound(const unsigned short metricId);

    const char* what() const override;
};

}
