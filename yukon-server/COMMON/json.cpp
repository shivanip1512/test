#include "precompiled.h"

#include "MetricIdLookup.h"
#include "resource_ids.h"
#include "resource_helper.h"
#include "resolvers.h"

#include "cajun/reader.h"

#include <sstream>

namespace Cti {

using namespace std::string_literals;

void parseMetricMappings    (const json::Array& metricMappings);
void parseAttributeOverrides(const json::Array& attributeOverrides);

void parseJsonFiles()
{
    DataBuffer raw = loadResourceFromLibrary( METRIC_ID_TO_ATTRIBUTE_MAPPING_ID, "JSON", "yukon-resource.dll" );

    std::istringstream stream(std::string(raw.begin(), raw.end()));

    json::Object root;

    try
    {
        json::Reader::Read(root, stream);
    }
    catch( json::Reader::ParseException & /*ex*/ )
    {
    }

    parseMetricMappings    (root["metricMapping"]);
    parseAttributeOverrides(root["attributeOverrides"]);
}

void parseMetricMappings(const json::Array& metricMappings)
{
    for( auto metricItr = metricMappings.Begin(); metricItr != metricMappings.End(); ++metricItr )
    {
        const json::Object& obj = *metricItr;

        try
        {
            json::String attributeName = obj["attribute"];
            json::Number metricId      = obj["metricId"];
            int attributeMagnitude = 0;

            auto itr = obj.Find("attributeMagnitude");

            if( itr != obj.End() )
            {
                json::Number magnitude = itr->element;

                attributeMagnitude = magnitude;
            }

            try
            {
                const Attribute &attribute = Attribute::Lookup(attributeName.Value());

                //  Yukon's attributes have implicit scaling (DEMAND is kW instead of W, for example).  
                //    attributeMagnitude describes the attribute's implicit scaling in powers of 10.
                MetricIdLookup::AddMetricForAttribute({attribute, attributeMagnitude}, metricId.Value());
            }
            catch (const AttributeNotFound&)
            {
                MetricIdLookup::AddUnknownAttribute(attributeName.Value());
            }
        }
        catch (const json::Exception& e)
        {
            std::cout << "Caught json::Exception: " << e.what() << std::endl << std::endl;
        }
    }
}

void parseAttributeOverrides(const json::Array& attributeOverrides)
{
    for( auto itr = attributeOverrides.Begin(); itr != attributeOverrides.End(); ++itr )
    {
        const json::Object& obj = *itr;

        try
        {
            json::String attributeName  = obj["attribute"];
            json::Number metricId       = obj["metricId"];
            json::Array  paoTypeStrings = obj["paoTypes"];

            std::set<DeviceTypes> paoTypes;

            for( auto paoTypeItr = paoTypeStrings.Begin(); paoTypeItr != paoTypeStrings.End(); ++paoTypeItr )
            {
                const auto paoType = resolveDeviceType(static_cast<const json::String&>(*paoTypeItr));

                if( paoType != TYPE_NONE )
                {
                    paoTypes.insert(paoType);
                }
            }

            try
            {
                const Attribute &attribute = Attribute::Lookup(attributeName.Value());

                //  Check to make sure the attribute has been added in the global list
                const auto globalMetricId = MetricIdLookup::GetMetricId(attribute, TYPE_NONE);

                //  Add the override for each of the pao types
                for( auto paoType : paoTypes )
                {
                    MetricIdLookup::AddAttributeOverride(attribute, metricId.Value(), paoType);
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
        catch (const json::Exception& e)
        {
            std::cout << "Caught json::Exception: " << e.what() << std::endl << std::endl;
        }
    }
}

}
