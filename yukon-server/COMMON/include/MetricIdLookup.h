#pragma once

#include "Attribute.h"
#include "devicetypes.h"

#include <set>

namespace Cti {

//  forward declaration
struct IM_EX_CTIBASE MetricMappingNotFound;
        
class IM_EX_CTIBASE MetricIdLookup
{
public:
    using MetricId = unsigned short;
    using MetricIds = std::set<MetricId>;

    struct AttributeDescriptor
    {
        Attribute attrib;
        int magnitude;
    };

    static void AddMetricForAttribute(const AttributeDescriptor & attribute, const MetricId metric);
    static void AddAttributeOverride (const Attribute & attribute, const MetricId metric, const DeviceTypes type);
    static void AddUnknownAttribute (const AttributeNotFound & ex);  //  Attribute lookup failed during load
    static void AddUnmappedAttribute(const MetricMappingNotFound & ex);  //  Metric lookup for override failed during load

    static MetricId  GetMetricId (const Attribute &attrib, const DeviceTypes type);

    static std::optional<Attribute> FindAttribute(const MetricId metric, const DeviceTypes type);
    static Attribute GetAttribute(const MetricId metric, const DeviceTypes type);
    static AttributeDescriptor GetAttributeDescription(const MetricId metric, const DeviceTypes type);

    static std::vector<AttributeNotFound> getUnknownAttributes();
    static std::vector<MetricMappingNotFound>  getUnmappedAttributes();
    static std::set<unsigned short> getMappedMetricIds();

private:
    using attribute_bimap = boost::bimap< Attribute, MetricId >;
    using position = attribute_bimap::value_type;

    static attribute_bimap attributes;

    using pao_attributes = std::map< std::pair< DeviceTypes, MetricId >, Attribute >;
    using pao_metrics    = std::map< std::pair< DeviceTypes, Attribute >, MetricId >;

    static pao_attributes paoAttributes;
    static pao_metrics    paoMetrics;

    static std::map< Attribute, int > attributeMagnitudes;
    static std::vector<AttributeNotFound> unknownAttributes;
    static std::vector<MetricMappingNotFound>  unmappedAttributes;
};

struct IM_EX_CTIBASE MetricMappingNotFound : std::exception
{
    std::string detail;
    const Attribute& attribute;

    MetricMappingNotFound(const Attribute &attrib);

    const char* what() const override;
};

struct IM_EX_CTIBASE AttributeMappingNotFound : std::exception
{
    std::string detail;
    const unsigned short metricId;

    AttributeMappingNotFound(const unsigned short metric);

    const char* what() const override;
};

}
