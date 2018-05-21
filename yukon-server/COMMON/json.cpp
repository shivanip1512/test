#include "precompiled.h"

#include "MetricIdLookup.h"
#include "resource_ids.h"
#include "resource_helper.h"
#include "resolvers.h"

#include <json.hpp>

#include <sstream>

namespace Cti {

using namespace std::string_literals;
using namespace nlohmann; // JSON namespace

void parseMetricMappings    (const json::array_t& metricMappings);
void parseAttributeOverrides(const json::array_t& attributeOverrides);

void parseJsonFiles()
{
    DataBuffer raw = loadResourceFromLibrary( METRIC_ID_TO_ATTRIBUTE_MAPPING_ID, "JSON", "yukon-resource.dll" );

    std::istringstream stream(std::string(raw.begin(), raw.end()));

    json root;

    try
    {
        root = json::parse(stream);
    }
    catch( json::exception & /*ex*/ )
    {
    }

    parseMetricMappings    (root["metricMapping"]);
    parseAttributeOverrides(root["attributeOverrides"]);
}

void parseMetricMappings(const json::array_t& metricMappings)
{
    json::string_t          attributeName;
    json::number_integer_t  metricId;
    int                     attributeMagnitude;

    for( auto metricItr = metricMappings.begin(); metricItr != metricMappings.end(); ++metricItr )
    {
        const json& obj = *metricItr;

        try
        {   
            attributeName      = obj.at("attribute").get<json::string_t>();
            metricId           = obj.at("metricId").get<json::number_integer_t>();
            attributeMagnitude = obj.value("attributeMagnitude", 0);

            try
            {
                const Attribute &attribute = Attribute::Lookup(attributeName);

                //  Yukon's attributes have implicit scaling (DEMAND is kW instead of W, for example).  
                //    attributeMagnitude describes the attribute's implicit scaling in powers of 10.
                MetricIdLookup::AddMetricForAttribute({attribute, attributeMagnitude }, metricId);
            }
            catch (const AttributeNotFound&)
            {
                MetricIdLookup::AddUnknownAttribute(attributeName);
            }
        }
        catch (const json::exception& e)
        {
            std::cout << "Caught json::Exception: " << e.what() << std::endl << std::endl;
        }
    }
}

void parseAttributeOverrides(const json::array_t& attributeOverrides)
{
    json::string_t          attributeName;
    json::number_integer_t  metricId;
    json::array_t           paoTypeStrings;

    for( auto itr = attributeOverrides.begin(); itr != attributeOverrides.end(); ++itr )
    {
        const json::object_t& obj = *itr;

        try
        {
            attributeName  = obj.at("attribute").get<json::string_t>();
            metricId       = obj.at("metricId") .get<json::number_integer_t>();
            paoTypeStrings = obj.at("paoTypes") .get<json::array_t>();

            std::set<DeviceTypes> paoTypes;

            for( auto paoTypeItr = paoTypeStrings.begin(); paoTypeItr != paoTypeStrings.end(); ++paoTypeItr )
            {
                const auto paoType = resolveDeviceType(static_cast<const json::string_t&>(*paoTypeItr));

                if( paoType != TYPE_NONE )
                {
                    paoTypes.insert(paoType);
                }
            }

            try
            {
                const Attribute &attribute = Attribute::Lookup(attributeName);

                //  Check to make sure the attribute has been added in the global list
                const auto globalMetricId = MetricIdLookup::GetMetricId(attribute, TYPE_NONE);

                //  Add the override for each of the pao types
                for( auto paoType : paoTypes )
                {
                    MetricIdLookup::AddAttributeOverride(attribute, metricId, paoType);
                }
            }
            catch (const AttributeNotFound& ex)
            {
                MetricIdLookup::AddUnknownAttribute(ex);
            }
            catch (const MetricMappingNotFound& ex)
            {
                MetricIdLookup::AddUnmappedAttribute(ex);
            }
        }
        catch (const json::exception& e)
        {
            std::cout << "Caught json::Exception: " << e.what() << std::endl << std::endl;
        }
    }
}

}
