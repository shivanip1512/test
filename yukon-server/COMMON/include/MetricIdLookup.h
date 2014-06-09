#pragma once

#include "PointAttribute.h"

#include <set>

namespace Cti {

class IM_EX_CTIBASE MetricIdLookup
{
public:
    typedef unsigned short MetricId;
    typedef std::set<MetricId> MetricIds;

    static void AddMetricForAttribute(const Attribute & attrib, const MetricId metric);

    static MetricId  GetForAttribute (const Attribute &attrib);
    static MetricIds GetForAttributes(const std::set<Attribute> & attribs);

private:
    static std::map<Attribute, MetricId> metrics;
};

}
