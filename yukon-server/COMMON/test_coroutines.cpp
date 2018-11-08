#include <boost/test/unit_test.hpp>

#include "boost_test_helpers.h"

#include "coroutine_util.h"
#include "utility.h"

#include <boost/range/algorithm_ext/iota.hpp>

namespace std {
    std::ostream& operator<<(std::ostream &os, const std::vector<int> &v) {
        return os << Cti::join(v, ",");
    }
}

BOOST_AUTO_TEST_SUITE(test_coroutines)

BOOST_AUTO_TEST_CASE(test_chunked)
{
    //  Call the fill constructor (size_t, _Ty) to initialize with 20 elements of value 0.
    //    Note that you cannot use the {} constructor, since it prefers the initializer-list constructor and hides the fill constructor.
    std::vector<int> range( 20, 0 );

    //  Fill the range with values 0-19
    boost::range::iota(range, 0);

    auto make_chunk_vector = [](auto& generator) {
        std::vector<std::vector<int>> chunks;

        for( const auto& chunk : generator )
        {
            chunks.emplace_back(chunk.begin(), chunk.end());
        }

        return chunks;
    };

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(std::vector<int>{}, 1000));

        const std::vector<std::vector<int>> expected;

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::make_chunks(std::vector<int>{}, 1000));

        const std::vector<std::vector<int>> expected;

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 1000));

        const std::vector<std::vector<int>> expected{
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 20));

        const std::vector<std::vector<int>> expected{
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 19));

        const std::vector<std::vector<int>> expected{
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 },
            { 19 }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 10));

        const std::vector<std::vector<int>> expected{
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 5));

        const std::vector<std::vector<int>> expected{
            { 0, 1, 2, 3, 4 }, { 5, 6, 7, 8, 9 }, { 10, 11, 12, 13, 14 }, { 15, 16, 17, 18, 19 }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 1));

        const std::vector<std::vector<int>> expected{
            //  Force the first element to be a vector, otherwise they collapse to elements in a single vector
            { std::vector<int>{ 0 }, { 1 }, { 2 }, { 3 }, { 4 }, { 5 }, { 6 }, { 7 }, { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 }, { 14 }, { 15 }, { 16 }, { 17 }, { 18 }, { 19 } }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }

    {
        const auto chunked = make_chunk_vector(Cti::Coroutines::chunked(range, 0));

        const std::vector<std::vector<int>> expected{
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }
        };

        BOOST_CHECK_EQUAL_RANGES(chunked, expected);
    }
}

BOOST_AUTO_TEST_SUITE_END()