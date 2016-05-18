#pragma once

#include "PointAttribute.h"

#include <set>

namespace Cti {

class IM_EX_CTIBASE MetricIdLookup
{
public:
    using MetricId = unsigned short;
    using MetricIds = std::set<MetricId>;
    using attribute_bimap = boost::bimap< Attribute, MetricId >;
    using position = attribute_bimap::value_type;

    static void AddMetricForAttribute(const Attribute & attrib, const MetricId metric);

    static MetricId  GetForAttribute (const Attribute &attrib);
    static MetricIds GetForAttributes(const std::set<Attribute> & attribs);

    static std::string getName(const MetricId metric);

private:
    static attribute_bimap attributes;
};

}
