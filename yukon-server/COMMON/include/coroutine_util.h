#pragma once

#include <boost/range/iterator_range.hpp>

#include <experimental/generator>

namespace Cti {
namespace Coroutines {

template <class Container>
std::experimental::generator<boost::iterator_range<typename Container::const_iterator>> chunked(Container& c, const size_t chunkSize)
{
    auto itr = c.cbegin();
    auto end = c.cend();

    if( ! c.empty() && chunkSize )
    {
        auto chunks = (c.size() - 1) / chunkSize;  //  Make sure we round down if we have an even multiple of chunkSize

        while( chunks-- )
        {
            const auto chunk_begin = itr;

            std::advance(itr, chunkSize);

            co_yield boost::make_iterator_range(chunk_begin, itr);
        }
    }

    co_yield boost::make_iterator_range(itr, end);
}

}
}
