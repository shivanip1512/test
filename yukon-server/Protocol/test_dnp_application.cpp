#include <boost/test/unit_test.hpp>

#include "dnp_application.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_dnp_application )

BOOST_AUTO_TEST_CASE( test_restoreObjectBlocks )
{
    using Cti::Protocols::DNP::ApplicationLayer;

    {
        Cti::Test::byte_str input = "3C 02 06 3C 03 06 3C 04 06";

        auto blocks = ApplicationLayer::restoreObjectBlocks(input.data(), input.size());

        BOOST_REQUIRE_EQUAL(blocks.size(), 3);

        auto itr = blocks.begin();

        {
            const auto &block_ptr = *itr++;

            BOOST_REQUIRE(block_ptr);

            const auto &block = *block_ptr;

            BOOST_CHECK_EQUAL(60, block.getGroup());
            BOOST_CHECK_EQUAL( 2, block.getVariation());
            BOOST_CHECK(block.empty());
        }
        {
            const auto &block_ptr = *itr++;

            BOOST_REQUIRE(block_ptr);

            const auto &block = *block_ptr;

            BOOST_CHECK_EQUAL(60, block.getGroup());
            BOOST_CHECK_EQUAL( 3, block.getVariation());
            BOOST_CHECK(block.empty());
        }
        {
            const auto &block_ptr = *itr++;

            BOOST_REQUIRE(block_ptr);

            const auto &block = *block_ptr;

            BOOST_CHECK_EQUAL(60, block.getGroup());
            BOOST_CHECK_EQUAL( 4, block.getVariation());
            BOOST_CHECK(block.empty());
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
