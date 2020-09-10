#include "precompiled.h"

#include "MetricIdLookup.h"
#include "Attribute.h"
#include "std_helper.h"

#include <boost/bimap.hpp>
#include <boost/range/adaptor/map.hpp>
#include <boost/range/algorithm/set_algorithm.hpp>

namespace Cti {

MetricIdLookup::attribute_bimap MetricIdLookup::attributes;
std::map< Attribute, int > MetricIdLookup::attributeMagnitudes;
std::vector<AttributeNotFound> MetricIdLookup::unknownAttributes;
std::vector<MetricMappingNotFound> MetricIdLookup::unmappedAttributes;

MetricIdLookup::pao_attributes MetricIdLookup::paoAttributes;
MetricIdLookup::pao_metrics    MetricIdLookup::paoMetrics;

void MetricIdLookup::AddMetricForAttribute(const AttributeDescriptor &attributeDescriptor, const MetricId metric)
{
    attributes.insert(position(attributeDescriptor.attrib, metric));
    attributeMagnitudes.emplace(attributeDescriptor.attrib, attributeDescriptor.magnitude);
}

void MetricIdLookup::AddAttributeOverride(const Attribute& attribute, const MetricId metric, const DeviceTypes type)
{
    if( ! attributes.left.count(attribute) )
    {
        throw MetricMappingNotFound(attribute);
    }

    paoAttributes.emplace(std::make_pair(type, metric), attribute);
    paoMetrics.emplace(std::make_pair(type, attribute), metric);
}

void MetricIdLookup::AddUnknownAttribute(const AttributeNotFound& ex)
{
    unknownAttributes.push_back(ex);
}

void MetricIdLookup::AddUnmappedAttribute(const MetricMappingNotFound& ex)
{
    unmappedAttributes.push_back(ex);
}

MetricIdLookup::MetricId MetricIdLookup::GetMetricId(const Attribute &attrib, const DeviceTypes type)
{
    //  First, look for the pao-specific override
    if( auto metricId = mapFind(paoMetrics, {type, attrib}) )
    {
        return *metricId;
    }

    //  Then look for a global instance
    try
    {
        return attributes.left.at(attrib);
    }
    catch( std::out_of_range )
    {
        throw MetricMappingNotFound(attrib);
    }
}

std::optional<Attribute> MetricIdLookup::FindAttribute(const MetricId metric, const DeviceTypes type)
{
    //  First, look for the pao-specific override
    if( auto attribute = mapFind(paoAttributes, { type, metric }) )
    {
        return *attribute;
    }

    //  Then look for a global instance
    if( auto attribute = mapFind(attributes.right, metric) )
    {
        return *attribute;
    }

    return std::nullopt;
}

Attribute MetricIdLookup::GetAttribute(const MetricId metric, const DeviceTypes type)
{
    //  First, look for the pao-specific override
    if( auto attribute = FindAttribute(metric, type) )
    {
        return *attribute;
    }

    throw AttributeMappingNotFound(metric);
}

auto MetricIdLookup::GetAttributeDescription(const MetricId metric, const DeviceTypes type) -> AttributeDescriptor
{
    const auto attrib = GetAttribute(metric, type);
    return { attrib, attributeMagnitudes[attrib] };
}

std::vector<AttributeNotFound> MetricIdLookup::getUnknownAttributes()
{
    return unknownAttributes;
}

std::vector<MetricMappingNotFound> MetricIdLookup::getUnmappedAttributes()
{
    return unmappedAttributes;
}

std::set<unsigned short> MetricIdLookup::getMappedMetricIds()
{
    const auto globalMetrics = boost::copy_range<std::set<MetricId>>(attributes.left | boost::adaptors::map_values);
    const auto paoOverrides  = boost::copy_range<std::set<MetricId>>(paoMetrics | boost::adaptors::map_values);

    std::set<MetricId> combinedMetrics;

    boost::range::set_union(globalMetrics, paoOverrides, std::inserter(combinedMetrics, combinedMetrics.begin()));

    return combinedMetrics;
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
