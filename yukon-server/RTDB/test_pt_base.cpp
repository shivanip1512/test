#include <boost/test/unit_test.hpp>

#include "pt_base.h"

BOOST_AUTO_TEST_SUITE( test_pt_base )

struct test_CtiPointBase : CtiPointBase
{
    using CtiPointBase::setType;
};

BOOST_AUTO_TEST_CASE(test_pt_base_isNumeric)
{
    test_CtiPointBase p;

    p.setType(AnalogPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(PulseAccumulatorPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(DemandAccumulatorPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(CalculatedPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(AnalogOutputPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(SystemPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(StatusOutputPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(StatusPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(CalculatedStatusPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(InvalidPointType);
    BOOST_CHECK( ! p.isNumeric());
}

BOOST_AUTO_TEST_CASE(test_pt_base_isStatus)
{
    test_CtiPointBase p;

    p.setType(AnalogPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(PulseAccumulatorPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(DemandAccumulatorPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(CalculatedPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(AnalogOutputPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(SystemPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(StatusOutputPointType);
    BOOST_CHECK( p.isStatus());

    p.setType(StatusPointType);
    BOOST_CHECK( p.isStatus());

    p.setType(CalculatedStatusPointType);
    BOOST_CHECK( p.isStatus());

    p.setType(InvalidPointType);
    BOOST_CHECK( ! p.isStatus());
}

BOOST_AUTO_TEST_SUITE_END()
