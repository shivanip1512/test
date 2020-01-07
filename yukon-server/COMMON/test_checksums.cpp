#include <boost/test/unit_test.hpp>

#include "checksums.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_checksums )

BOOST_AUTO_TEST_CASE(test_md5_checksum)
{
    std::string content = "The quick brown fox jumped over the lazy dog.";

    auto result = Cti::calculateMd5Digest({ content.begin(), content.end() });

    const Cti::Md5Digest expected { 
        0x5c, 0x6f, 0xfb, 0xdd,
        0x40, 0xd9, 0x55, 0x6b,
        0x73, 0xa2, 0x1e, 0x63,
        0xc3, 0xe0, 0xe9, 0x04 };

    BOOST_CHECK_EQUAL_RANGES(result, expected);
}

BOOST_AUTO_TEST_SUITE_END()
