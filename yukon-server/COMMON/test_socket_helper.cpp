#include "precompiled.h"

#include "boost_test_helpers.h"
#include "socket_helper.h"

#include <boost/test/unit_test.hpp>

BOOST_AUTO_TEST_SUITE(test_socket_helper)

BOOST_AUTO_TEST_CASE(test_SocketAddress_compare)
{
    /* Test IP address 127.0.0.1 */
    SOCKADDR_IN sa41 = { 
        AF_INET,    /* sin_family */
        htons(80),  /* sin_port */
        {           /* IN_ADDR sin_addr */
            127, 0, 0, 1
        }
    };

    /* Test IP address 127.0.0.1:0 */
    SOCKADDR_IN sa41p0 = {
        AF_INET,    /* sin_family */
        htons(0),   /* sin_port */
        {           /* IN_ADDR sin_addr */
            127, 0, 0, 1
        }
    };

    /* Test IP address 127.0.0.2 */
    SOCKADDR_IN sa42 = {
        AF_INET,    /* sin_family */
        htons(80),  /* sin_port */
        {           /* IN_ADDR sin_addr */
            127, 0, 0, 2
        }
    };

    /* Test IPv6 mapped IPv4 address 127.0.0.1 */
    SOCKADDR_IN6 sa4mapped1 = {
        AF_INET6,   /* sin6_family */
        htons(80),  /* sin6_port */
        0,          /* sin6_flowinfo */
        {           /* sin6_addr */
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0xff, 127, 0, 0, 1
        },
        0
    };

    /* Test IPv6 mapped IPv4 address 127.0.0.1 */
    SOCKADDR_IN6 sa4mapped1p0 = {
        AF_INET6,   /* sin6_family */
        htons(0),   /* sin6_port */
        0,          /* sin6_flowinfo */
        {           /* sin6_addr */
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0xff, 127, 0, 0, 1
        },
        0
    };
    /* Test IPv6 mapped IPv4 address 127.0.0.2 */
    SOCKADDR_IN6 sa4mapped2 = {
        AF_INET6,   /* sin6_family */
        htons(80),  /* sin6_port */
        0,          /* sin6_flowinfo */
        {           /* sin6_addr */
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff, 0xff, 127, 0, 0, 2
        },
        0
    };

    /* Test IPv6 address ::1 (localhost) */
    SOCKADDR_IN6 sa61 = {
        AF_INET6,   /* sin6_family */
        htons(80),  /* sin6_port */
        0,          /* sin6_flowinfo */
        {           /* sin6_addr */
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1
        },
        0
    };

    /* Test IPv6 address ::1 (localhost) */
    SOCKADDR_IN6 sa61p0 = {
        AF_INET6,   /* sin6_family */
        htons(0),   /* sin6_port */
        0,          /* sin6_flowinfo */
        {           /* sin6_addr */
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1
        },
        0
    };
    /* Test IPv6 address ::2 (not localhost) */
    SOCKADDR_IN6 sa62 = {
        AF_INET6,   /* sin6_family */
        htons(80),  /* sin6_port */
        0,          /* sin6_flowinfo */
        {           /* sin6_addr */
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2
        },
        0
    };

    Cti::SocketAddress addr41((SOCKADDR *)&sa41, sizeof sa41);
    Cti::SocketAddress addr41p0((SOCKADDR *)&sa41p0, sizeof sa41p0);
    Cti::SocketAddress addr42((SOCKADDR *)&sa42, sizeof sa42);
    Cti::SocketAddress addr4mapped1((SOCKADDR *)&sa4mapped1, sizeof sa4mapped1);
    Cti::SocketAddress addr4mapped1p0((SOCKADDR *)&sa4mapped1p0, sizeof sa4mapped1p0);
    Cti::SocketAddress addr4mapped2((SOCKADDR *)&sa4mapped2, sizeof sa4mapped2);
    Cti::SocketAddress addr61((SOCKADDR *)&sa61, sizeof sa61);
    Cti::SocketAddress addr61p0((SOCKADDR *)&sa61p0, sizeof sa61p0);
    Cti::SocketAddress addr62((SOCKADDR *)&sa62, sizeof sa62);

    /* Make sure what we created matches what we intended */
    BOOST_CHECK_EQUAL(addr41.toString(), "127.0.0.1:80");
    BOOST_CHECK_EQUAL(addr41p0.toString(), "127.0.0.1");
    BOOST_CHECK_EQUAL(addr42.toString(), "127.0.0.2:80");
    BOOST_CHECK_EQUAL(addr4mapped1.toString(), "[::ffff:127.0.0.1]:80");
    BOOST_CHECK_EQUAL(addr4mapped1p0.toString(), "::ffff:127.0.0.1");
    BOOST_CHECK_EQUAL(addr4mapped2.toString(), "[::ffff:127.0.0.2]:80");
    BOOST_CHECK_EQUAL(addr61.toString(), "[::1]:80");
    BOOST_CHECK_EQUAL(addr62.toString(), "[::2]:80");

    /* Compare IPv4 to IPv4 */
    BOOST_CHECK(addr41 == addr41);
    BOOST_CHECK(!(addr41 == addr42));
    BOOST_CHECK(!(addr42 == addr41));

    /* Compare IPv4 to IPv4:80 */
    BOOST_CHECK(!(addr41 == addr41p0));
    BOOST_CHECK(!(addr41p0 == addr41));

    /* Compare IPv4 to IPv6 mapped IPv4 */
    BOOST_CHECK((addr41 == addr4mapped1));
    BOOST_CHECK((addr4mapped1 == addr41));
    BOOST_CHECK(!(addr42 == addr4mapped1));
    BOOST_CHECK(!(addr4mapped1 == addr42));
    BOOST_CHECK((addr42 == addr4mapped2));
    BOOST_CHECK((addr4mapped2 == addr42));

    /* Compare IPv4:80 to IPv6 mapped IPv4 */
    BOOST_CHECK(!(addr4mapped1 == addr4mapped1p0));
    BOOST_CHECK(!(addr4mapped1p0 == addr4mapped1));

    /* Compare IPv6 to IPv6 */
    BOOST_CHECK((addr61 == addr61));
    BOOST_CHECK(!(addr61 == addr62));
    BOOST_CHECK(!(addr62 == addr61));

    /* Compare IPv4 to IPv4:80 */
    BOOST_CHECK(!(addr61 == addr61p0));
    BOOST_CHECK(!(addr61p0 == addr61));

    /* Compare IPv4 to non-mapped IPv6 */
    BOOST_CHECK(!(addr41 == addr61));
}

BOOST_AUTO_TEST_SUITE_END()

