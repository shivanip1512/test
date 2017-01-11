#include <boost/test/unit_test.hpp>

#include "MetricIdLookup.h"

BOOST_AUTO_TEST_SUITE( test_MetricIdLookup )

BOOST_AUTO_TEST_CASE(test_unknownAttributes)
{
    const auto unknowns = Cti::MetricIdLookup::getUnknownAttributes();

    if( ! unknowns.empty() )
    {
        BOOST_ERROR( "Metric ID to Attribute mapping has unknown attributes; "
                     "attributes may have been added to BuiltInAttributes.java without being added to PointAttribute.cpp and PointAttribute.h" );

        for( const auto& attributeNotFound : unknowns )
        {
            BOOST_ERROR( attributeNotFound.desc );
        }
    }
}

BOOST_AUTO_TEST_CASE(test_overrideFailures)
{
    const auto unmapped = Cti::MetricIdLookup::getUnmappedAttributes();

    for( const auto& metricIdNotFound : unmapped )
    {
        BOOST_ERROR( "Attribute override mapping has unmapped attribute " + metricIdNotFound.attribute.getName() + 
            " - attributes must be added to metricMapping before they can be overridden in attributeOverrides" );
    }
}

BOOST_AUTO_TEST_CASE(test_GetMetricId_success)
{
    const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::DeliveredkWh);

    BOOST_CHECK_EQUAL(metricId, 1);
}

BOOST_AUTO_TEST_CASE(test_GetForAttribute_failure)
{
    try
    {
        Cti::MetricIdLookup::GetMetricId(Attribute::CalcCpuUtilization);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::MetricMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Metric ID not found for attribute CALC_CPU_UTILIZATION" );
    }
}

BOOST_AUTO_TEST_CASE(test_getAttribute_success)
{
    const auto attrib = Cti::MetricIdLookup::GetAttribute(1);

    BOOST_CHECK(attrib == Attribute::DeliveredkWh);
    BOOST_CHECK_EQUAL(attrib.getName(), "DELIVERED_KWH");
}

BOOST_AUTO_TEST_CASE(test_getAttribute_failure)
{
    try
    {
        Cti::MetricIdLookup::GetAttribute(-1);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }
}

BOOST_AUTO_TEST_CASE(test_GetAttributeDescription_success)
{
    {
        const auto attribDesc = Cti::MetricIdLookup::GetAttributeDescription(1);

        BOOST_CHECK(attribDesc.attrib == Attribute::DeliveredkWh);
        BOOST_CHECK_EQUAL(attribDesc.attrib.getName(), "DELIVERED_KWH");
        BOOST_CHECK_EQUAL(attribDesc.magnitude, 3);
    }
    {
        const auto attribDesc = Cti::MetricIdLookup::GetAttributeDescription(80);

        BOOST_CHECK(attribDesc.attrib == Attribute::PowerFactor);
        BOOST_CHECK_EQUAL(attribDesc.attrib.getName(), "POWER_FACTOR");
        BOOST_CHECK_EQUAL(attribDesc.magnitude, 0);
    }
}

BOOST_AUTO_TEST_CASE(test_GetAttributeDescription_failure)
{
    try
    {
        Cti::MetricIdLookup::GetAttributeDescription(-1);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }
}

BOOST_AUTO_TEST_SUITE_END()
