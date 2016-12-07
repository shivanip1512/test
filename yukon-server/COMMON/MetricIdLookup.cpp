#include "precompiled.h"

#include "MetricIdLookup.h"
#include "PointAttribute.h"
#include "std_helper.h"

#include <boost/bimap.hpp>

namespace Cti {

MetricIdLookup::attribute_bimap MetricIdLookup::attributes;
std::map< Attribute, int > MetricIdLookup::attributeMagnitudes;
std::set<std::string> MetricIdLookup::unknownAttributes;

void MetricIdLookup::AddMetricForAttribute(const AttributeDescriptor &attributeDescriptor, const MetricId metric)
{
    attributes.insert(position(attributeDescriptor.attrib, metric));
    attributeMagnitudes.emplace(attributeDescriptor.attrib, attributeDescriptor.magnitude);
}

void MetricIdLookup::AddUnknownAttribute(const std::string& attributeName)
{
    unknownAttributes.insert(attributeName);
}

MetricIdLookup::MetricId MetricIdLookup::GetForAttribute(const Attribute &attrib)
{
    try
    {
        return attributes.left.at(attrib);
    }
    catch( std::out_of_range )
    {
        throw MetricIdNotFound(attrib);
    }
}

Attribute MetricIdLookup::getAttribute(const MetricId metric)
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

auto MetricIdLookup::getAttributeDescription(const MetricId metric) -> AttributeDescriptor
{
    const auto attrib = getAttribute(metric);
    return { attrib, attributeMagnitudes[attrib] };
}

std::set<std::string> MetricIdLookup::getUnknownAttributes()
{
    return unknownAttributes;
}

MetricIdLookup::MetricIds MetricIdLookup::GetForAttributes(const std::set<Attribute> & attribs)
{
    return boost::copy_range<MetricIds>(
                attribs | boost::adaptors::transformed(&GetForAttribute));
}


MetricIdNotFound::MetricIdNotFound(const Attribute &attrib)
{
    detail = "Metric ID not found for attribute " + attrib.getName();
}

const char* MetricIdNotFound::what() const
{
    return detail.c_str();
}


AttributeMappingNotFound::AttributeMappingNotFound(const unsigned short metricId)
{
    detail = "Attribute mapping not found for metric ID " + std::to_string(metricId);
}

const char* AttributeMappingNotFound::what() const
{
    return detail.c_str();
}

}
