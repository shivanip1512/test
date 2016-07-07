#pragma once

#include "PointAttribute.h"

#include <set>

namespace Cti {

class IM_EX_CTIBASE MetricIdLookup
{
public:
    using MetricId = unsigned short;
    using MetricIds = std::set<MetricId>;

    static void AddMetricForAttribute(const Attribute & attrib, const MetricId metric);

    static MetricId  GetForAttribute (const Attribute &attrib);
    static MetricIds GetForAttributes(const std::set<Attribute> & attribs);

    static Attribute getAttribute(const MetricId metric);

private:
    using attribute_bimap = boost::bimap< Attribute, MetricId >;
    using position = attribute_bimap::value_type;

    static attribute_bimap attributes;
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
