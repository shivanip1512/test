#include "precompiled.h"

#include "MetricIdLookup.h"
#include "PointAttribute.h"
#include "std_helper.h"

namespace Cti {

std::map<Attribute, MetricIdLookup::MetricId> MetricIdLookup::attributes2Metrics;
std::map<MetricIdLookup::MetricId, Attribute> MetricIdLookup::metrics2Attributes;

void MetricIdLookup::AddMetricForAttribute(const Attribute &attrib, const MetricId metric)
{
    attributes2Metrics[attrib] = metric;
    metrics2Attributes.emplace(metric, attrib);
}

MetricIdLookup::MetricId MetricIdLookup::GetForAttribute(const Attribute &attrib)
{
    return attributes2Metrics[attrib];
}

std::string Cti::MetricIdLookup::getName(const MetricId metric)
{
    auto found = Cti::mapFind(metrics2Attributes, metric);
    if (found)
    {
        return found->getName();
    }
    else
    {
        return std::to_string(metric);
    }
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
