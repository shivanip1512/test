#pragma once

#include "utility.h"  //  defines autopsy()


namespace boost
{
    inline void assertion_failed(char const * expr, char const * function, char const * file, long line)
    {
        autopsy(file, line);
    }
} // namespace boost


#define BOOST_CHECK_INDEXED_EQUAL(i,x,y) \
    BOOST_CHECK_MESSAGE((x) == (y), "failed with index = " << (i)); \
    BOOST_CHECK_EQUAL((x), (y))

//  for use with shared_ptr<>
struct null_deleter
{
    void operator()(void const *) const  {}
};

