#include "precompiled.h"

#include "MetricIdLookup.h"

#include "resource_helper.h"

#include "cajun/reader.h"

#include <sstream>

namespace Cti {

using namespace std::string_literals;

void parseMetricMappings(const json::Array& metricMappings);

void parseJsonFiles()
{
    DataBuffer raw = loadResourceFromLibrary( Resource_MetricIdToAttributeMapping, "JSON", "yukon-resource.dll" );

    std::istringstream stream(std::string(raw.begin(), raw.end()));

    json::Object root;

    try
    {
        json::Reader::Read(root, stream);
    }
    catch( json::Reader::ParseException & /*ex*/ )
    {
    }

    parseMetricMappings(root["metricMapping"]);
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

}
