#pragma once

#include "PointAttribute.h"

#include <set>

namespace Cti {

class IM_EX_CTIBASE MetricIdLookup
{
public:
    typedef unsigned short MetricId;
    typedef std::set<MetricId> MetricIds;
    typedef boost::bimap< Attribute, MetricId > attribute_bimap;
    typedef attribute_bimap::value_type position;

    static void AddMetricForAttribute(const Attribute & attrib, const MetricId metric);

    static MetricId  GetForAttribute (const Attribute &attrib);
    static MetricIds GetForAttributes(const std::set<Attribute> & attribs);

    static std::string getName(const MetricId metric);

private:
    static attribute_bimap attributes;
};

}
