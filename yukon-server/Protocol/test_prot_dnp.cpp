#define BOOST_AUTO_TEST_MAIN "Test DNP Protocol"

#include "yukon.h"

#include "dnp_object_internalindications.h"

#include <boost/test/auto_unit_test.hpp>


BOOST_AUTO_TEST_CASE(test_prot_dnp_object_internalindications)
{
    const void *Null = 0;

    {
        Cti::Protocol::DNP::InternalIndications iin(Cti::Protocol::DNP::InternalIndications::II_InternalIndications);

        BOOST_CHECK_EQUAL(80,    iin.getGroup());
        BOOST_CHECK_EQUAL( 1,    iin.getVariation());
        BOOST_CHECK_EQUAL( 0,    iin.getSerializedLen());
        BOOST_CHECK_EQUAL( 0,    iin.serialize(NULL));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(0,     iin.restore(NULL, 0));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(17,    iin.restore(NULL, 17));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(1,     iin.restoreBits(NULL, 0, 17));
        BOOST_CHECK_EQUAL(true,  iin.isValid());

        BOOST_CHECK_EQUAL(Null,  iin.getPoint(NULL));
    }

    {
        Cti::Protocol::DNP::InternalIndications iin(-1);

        BOOST_CHECK_EQUAL( 80,   iin.getGroup());
        BOOST_CHECK_EQUAL(255,   iin.getVariation());  //  getVariation returns an unsigned char
        BOOST_CHECK_EQUAL(  0,   iin.getSerializedLen());
        BOOST_CHECK_EQUAL(  0,   iin.serialize(NULL));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(  0,   iin.restore(NULL, 0));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL( 17,   iin.restore(NULL, 17));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(  1,   iin.restoreBits(NULL, 0, 17));
        BOOST_CHECK_EQUAL(true,  iin.isValid());

        BOOST_CHECK_EQUAL(Null,  iin.getPoint(NULL));
    }

    {
        Cti::Protocol::DNP::InternalIndications iin(2);

        BOOST_CHECK_EQUAL(80,    iin.getGroup());
        BOOST_CHECK_EQUAL( 2,    iin.getVariation());  //  getVariation returns an unsigned char
        BOOST_CHECK_EQUAL( 0,    iin.getSerializedLen());
        BOOST_CHECK_EQUAL( 0,    iin.serialize(NULL));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL( 0,    iin.restore(NULL, 0));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL(17,    iin.restore(NULL, 17));
        BOOST_CHECK_EQUAL(false, iin.isValid());

        BOOST_CHECK_EQUAL( 1,    iin.restoreBits(NULL, 0, 17));
        BOOST_CHECK_EQUAL(true,  iin.isValid());

        BOOST_CHECK_EQUAL(Null,  iin.getPoint(NULL));
    }
}
