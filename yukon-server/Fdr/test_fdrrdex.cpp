#include <boost/test/unit_test.hpp>

#include "fdrsocketinterface.h"
#include "fdrrdex.h"

BOOST_AUTO_TEST_SUITE( test_fdrRdex )

BOOST_AUTO_TEST_CASE( test_zero_init )
{
    constexpr auto bufferSize = sizeof RdexInterface_t;

    char * zeroed = new char[ bufferSize ];
    char * inited = new char[ bufferSize ];

    memset(zeroed, 0x00, bufferSize);

    memset(inited, 0xff, bufferSize);

    RdexInterface_t * ptr = reinterpret_cast<RdexInterface_t*>(inited);

    *ptr = {};

    BOOST_CHECK_EQUAL_COLLECTIONS(
        zeroed, zeroed + bufferSize,
        inited, inited + bufferSize);

    delete zeroed;
    delete inited;
}

BOOST_AUTO_TEST_SUITE_END()

