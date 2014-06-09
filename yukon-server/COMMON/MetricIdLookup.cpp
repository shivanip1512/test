#include "precompiled.h"

#include "MetricIdLookup.h"
#include "PointAttribute.h"

namespace Cti {

std::map<Attribute, MetricIdLookup::MetricId> MetricIdLookup::metrics;

void MetricIdLookup::AddMetricForAttribute(const Attribute &attrib, const MetricId metric)
{
    metrics[attrib] = metric;
}

MetricIdLookup::MetricId MetricIdLookup::GetForAttribute(const Attribute &attrib)
{
    return metrics[attrib];
}

MetricIdLookup::MetricIds MetricIdLookup::GetForAttributes(const std::set<Attribute> & attribs)
{
    MetricIds result;

    std::transform(
            attribs.begin(),
            attribs.end(),
            std::inserter(result, result.begin()),
            &GetForAttribute);

    return result;
}

}
