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

BOOST_AUTO_TEST_CASE(test_Lookup_success)
{
    const auto metricId = Cti::MetricIdLookup::GetForAttribute(Attribute::DeliveredkWh);

    BOOST_CHECK_EQUAL(metricId, 1);
}

BOOST_AUTO_TEST_CASE(test_Lookup_failure)
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

BOOST_AUTO_TEST_SUITE_END()
