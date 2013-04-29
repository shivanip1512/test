#include <boost/test/unit_test.hpp>

#include "ccutil.h"
#include "pointdefs.h"  //  for point qualities

BOOST_AUTO_TEST_SUITE( test_ccutil )

BOOST_AUTO_TEST_CASE( test_resolvePhase )
{
    using namespace Cti::CapControl;

    BOOST_CHECK_EQUAL( resolvePhase("A"), Phase_A );
    BOOST_CHECK_EQUAL( resolvePhase("B"), Phase_B );
    BOOST_CHECK_EQUAL( resolvePhase("C"), Phase_C );
    BOOST_CHECK_EQUAL( resolvePhase("*"), Phase_Poly );
    BOOST_CHECK_EQUAL( resolvePhase("Q"), Phase_Unknown );
    BOOST_CHECK_EQUAL( resolvePhase("Z"), Phase_Unknown );
}

BOOST_AUTO_TEST_CASE( test_desolvePhase )
{
    using namespace Cti::CapControl;

    BOOST_CHECK_EQUAL( "A", desolvePhase(Phase_A) );
    BOOST_CHECK_EQUAL( "B", desolvePhase(Phase_B) );
    BOOST_CHECK_EQUAL( "C", desolvePhase(Phase_C) );
    BOOST_CHECK_EQUAL( "*", desolvePhase(Phase_Poly) );
    BOOST_CHECK_EQUAL( "?", desolvePhase(Phase_Unknown) );
}

BOOST_AUTO_TEST_CASE( test_isQualityOk )
{
    using Cti::CapControl::isQualityOk;

    BOOST_CHECK_EQUAL( 0, UnintializedQuality );
    BOOST_CHECK( ! isQualityOk(0) );

    BOOST_CHECK_EQUAL( 1, InitDefaultQuality );
    BOOST_CHECK( ! isQualityOk(1) );

    BOOST_CHECK_EQUAL( 2, InitLastKnownQuality );
    BOOST_CHECK( ! isQualityOk(2) );

    BOOST_CHECK_EQUAL( 3, NonUpdatedQuality );
    BOOST_CHECK( ! isQualityOk(3) );

    BOOST_CHECK_EQUAL( 4, ManualQuality );
    BOOST_CHECK(   isQualityOk(4) );

    BOOST_CHECK_EQUAL( 5, NormalQuality );
    BOOST_CHECK(   isQualityOk(5) );

    BOOST_CHECK_EQUAL( 6, ExceedsLowQuality );
    BOOST_CHECK( ! isQualityOk(6) );

    BOOST_CHECK_EQUAL( 7, ExceedsHighQuality );
    BOOST_CHECK( ! isQualityOk(7) );

    BOOST_CHECK_EQUAL( 8, AbnormalQuality );
    BOOST_CHECK( ! isQualityOk(8) );

    BOOST_CHECK_EQUAL( 9, UnknownQuality );
    BOOST_CHECK( ! isQualityOk(9) );

    BOOST_CHECK_EQUAL( 10, InvalidQuality );
    BOOST_CHECK( ! isQualityOk(10) );

    BOOST_CHECK_EQUAL( 11, PartialIntervalQuality );
    BOOST_CHECK( ! isQualityOk(11) );

    BOOST_CHECK_EQUAL( 12, DeviceFillerQuality );
    BOOST_CHECK( ! isQualityOk(12) );

    BOOST_CHECK_EQUAL( 13, QuestionableQuality );
    BOOST_CHECK( ! isQualityOk(13) );

    BOOST_CHECK_EQUAL( 14, OverflowQuality );
    BOOST_CHECK( ! isQualityOk(14) );

    BOOST_CHECK_EQUAL( 15, PowerfailQuality );
    BOOST_CHECK( ! isQualityOk(15) );

    BOOST_CHECK_EQUAL( 16, UnreasonableQuality );
    BOOST_CHECK( ! isQualityOk(16) );

    BOOST_CHECK_EQUAL( 17, ConstantQuality );
    BOOST_CHECK( ! isQualityOk(17) );

    BOOST_CHECK_EQUAL( 18, EstimatedQuality );
    BOOST_CHECK( ! isQualityOk(18) );
}

BOOST_AUTO_TEST_SUITE_END()
