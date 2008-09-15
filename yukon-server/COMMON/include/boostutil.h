#ifndef __BOOST_UTIL
#define __BOOST_UTIL

#include "utility.h"

namespace boost
{
    inline void assertion_failed(char const * expr, char const * function, char const * file, long line)
    {
        autopsy(file, line);
    }
} // namespace boost

#endif

