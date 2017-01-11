#include "precompiled.h"

#include "MetricIdLookup.h"
#include "PointAttribute.h"
#include "std_helper.h"

#include <boost/bimap.hpp>

namespace Cti {

MetricIdLookup::attribute_bimap MetricIdLookup::attributes;
std::map< Attribute, int > MetricIdLookup::attributeMagnitudes;
std::vector<AttributeNotFound> MetricIdLookup::unknownAttributes;

void MetricIdLookup::AddMetricForAttribute(const AttributeDescriptor &attributeDescriptor, const MetricId metric)
{
    attributes.insert(position(attributeDescriptor.attrib, metric));
    attributeMagnitudes.emplace(attributeDescriptor.attrib, attributeDescriptor.magnitude);
}

void MetricIdLookup::AddUnknownAttribute(const AttributeNotFound& ex)
{
    unknownAttributes.push_back(ex);
}

MetricIdLookup::MetricId MetricIdLookup::GetMetricId(const Attribute &attrib)
{
    try
    {
        return attributes.left.at(attrib);
    }
    catch( std::out_of_range )
    {
        throw MetricMappingNotFound(attrib);
    }
}

Attribute MetricIdLookup::GetAttribute(const MetricId metric)
{
    try
    {
        return attributes.right.at(metric);
    }
    catch( std::out_of_range )
    {
        throw AttributeMappingNotFound(metric);
    }
}

auto MetricIdLookup::GetAttributeDescription(const MetricId metric) -> AttributeDescriptor
{
    const auto attrib = GetAttribute(metric);
    return { attrib, attributeMagnitudes[attrib] };
}

std::vector<AttributeNotFound> MetricIdLookup::getUnknownAttributes()
{
    return unknownAttributes;
}


MetricMappingNotFound::MetricMappingNotFound(const Attribute &attrib)
    :   attribute(attrib)
{
    detail = "Metric ID not found for attribute " + attrib.getName();
}

const char* MetricMappingNotFound::what() const
{
    return detail.c_str();
}


AttributeMappingNotFound::AttributeMappingNotFound(const unsigned short metric)
    :   metricId(metric)
{
    detail = "Attribute mapping not found for metric ID " + std::to_string(metric);
}

const char* AttributeMappingNotFound::what() const
{
    return detail.c_str();
}

}
