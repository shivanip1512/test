#include <boost/test/unit_test.hpp>

#include "MetricIdLookup.h"

BOOST_AUTO_TEST_SUITE( test_MetricIdLookup )

BOOST_AUTO_TEST_CASE(test_unknownAttributes)
{
    const auto unknowns = Cti::MetricIdLookup::getUnknownAttributes();

    for( const auto& attributeName : unknowns )
    {
        BOOST_ERROR( "Metric ID to Attribute mapping has unknown attribute " + attributeName + 
                     " - attributes may have been added to BuiltInAttributes.java without being added to PointAttribute.cpp and PointAttribute.h" );
    }
}

BOOST_AUTO_TEST_CASE(test_GetForAttribute_success)
{
    const auto metricId = Cti::MetricIdLookup::GetForAttribute(Attribute::DeliveredkWh);

    BOOST_CHECK_EQUAL(metricId, 1);
}

BOOST_AUTO_TEST_CASE(test_GetForAttribute_failure)
{
    try
    {
        Cti::MetricIdLookup::GetForAttribute(Attribute::CalcCpuUtilization);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::MetricIdNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Metric ID not found for attribute CALC_CPU_UTILIZATION" );
    }
}

BOOST_AUTO_TEST_CASE(test_getAttribute_success)
{
    const auto attrib = Cti::MetricIdLookup::getAttribute(1);

    BOOST_CHECK(attrib == Attribute::DeliveredkWh);
    BOOST_CHECK_EQUAL(attrib.getName(), "DELIVERED_KWH");
}

BOOST_AUTO_TEST_CASE(test_getAttribute_failure)
{
    try
    {
        Cti::MetricIdLookup::getAttribute(-1);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }
}

BOOST_AUTO_TEST_CASE(test_getAttributeDescription_success)
{
    {
        const auto attribDesc = Cti::MetricIdLookup::getAttributeDescription(1);

        BOOST_CHECK(attribDesc.attrib == Attribute::DeliveredkWh);
        BOOST_CHECK_EQUAL(attribDesc.attrib.getName(), "DELIVERED_KWH");
        BOOST_CHECK_EQUAL(attribDesc.magnitude, 3);
    }
    {
        const auto attribDesc = Cti::MetricIdLookup::getAttributeDescription(80);

        BOOST_CHECK(attribDesc.attrib == Attribute::PowerFactor);
        BOOST_CHECK_EQUAL(attribDesc.attrib.getName(), "POWER_FACTOR");
        BOOST_CHECK_EQUAL(attribDesc.magnitude, 0);
    }
}

BOOST_AUTO_TEST_CASE(test_getAttributeDescription_failure)
{
    try
    {
        Cti::MetricIdLookup::getAttributeDescription(-1);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }
}

BOOST_AUTO_TEST_SUITE_END()
