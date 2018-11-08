#pragma once

#include <boost/range/iterator_range.hpp>

#include <experimental/generator>

namespace Cti::Coroutines {

template <class Container>
using slice = boost::iterator_range<typename Container::const_iterator>;

template <class Container>
using slice_generator = std::experimental::generator<slice<Container>>;

namespace impl {

template <class Container>
slice_generator<Container> chunked_impl(Container& c, const size_t chunkSize, bool generateEmptyChunk)
{
    auto itr = std::cbegin(c);

    if( std::empty(c) )
    {
        if( ! generateEmptyChunk )
        {
            return;
        }
    }
    else if( chunkSize )
    {
        auto chunks = (std::size(c) - 1) / chunkSize;  //  Make sure we round down if we have an even multiple of chunkSize

        while( chunks-- )
        {
            const auto chunk_begin = itr;

            std::advance(itr, chunkSize);

            co_yield boost::make_iterator_range(chunk_begin, itr);
        }
    }

    co_yield boost::make_iterator_range(itr, std::cend(c));
}

}

template <class Container>
auto make_chunks(Container& c, const size_t chunkSize)
{
    return impl::chunked_impl(c, chunkSize, true);
}

template <class Container>
auto chunked(Container& c, const size_t chunkSize)
{
    return impl::chunked_impl(c, chunkSize, false);
}

}