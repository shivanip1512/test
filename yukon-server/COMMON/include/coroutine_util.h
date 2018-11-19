#pragma once

#include <boost/range/iterator_range.hpp>

#include <experimental/generator>

namespace Cti::Coroutines {

template <class Container>
using slice = boost::iterator_range<typename Container::const_iterator>;

template <class Container>
using slice_generator = std::experimental::generator<slice<Container>>;

template <class Container>
slice_generator<Container> chunked(Container& c, const size_t chunkSize)
{
    auto itr = cbegin(c);
    const auto end = cend(c);

    if( itr == end )
    {
        return;
    }

    if( chunkSize )
    {
        auto chunks = (size(c) - 1) / chunkSize;  //  Make sure we round down if we have an even multiple of chunkSize

        while( chunks-- )
        {
            const auto chunk_begin = itr;

            advance(itr, chunkSize);

            co_yield boost::make_iterator_range(chunk_begin, itr);
        }
    }

    co_yield boost::make_iterator_range(itr, end);
}

}