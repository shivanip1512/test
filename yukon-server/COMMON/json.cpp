#include "precompiled.h"

#include "MetricIdLookup.h"

#include "resource_helper.h"

#include "cajun/reader.h"

#include <sstream>

namespace Cti {

using namespace std::string_literals;

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

    struct MappingPopulator : json::ConstVisitor
    {
        virtual void Visit(const json::Null    &obj)  {}
        virtual void Visit(const json::Boolean &obj)  {}
        virtual void Visit(const json::String  &obj)  {}
        virtual void Visit(const json::Number  &obj)  {}
        virtual void Visit(const json::Array   &obj)  {}
        virtual void Visit(const json::Object  &obj)
        {
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
    };

    json::Array &mapping = root["metricMapping"];

    json::Array::const_iterator itr = mapping.Begin();

    for( ; itr != mapping.End(); ++itr )
    {
        itr->Accept(MappingPopulator());
    }
}

}
