#include "precompiled.h"

#include "MetricIdLookup.h"
#include "PointAttribute.h"
#include "std_helper.h"

#include <boost/bimap.hpp>

namespace Cti {

MetricIdLookup::attribute_bimap MetricIdLookup::attributes;

void MetricIdLookup::AddMetricForAttribute(const Attribute &attrib, const MetricId metric)
{
    attributes.insert(position(attrib, metric));
}

MetricIdLookup::MetricId MetricIdLookup::GetForAttribute(const Attribute &attrib)
{
    return attributes.left.at(attrib);
}

std::string Cti::MetricIdLookup::getName(const MetricId metric)
{
    auto found = Cti::mapFind(attributes.right, metric);
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
