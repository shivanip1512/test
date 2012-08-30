#pragma once

#include "utility.h"  //  defines autopsy()


namespace boost
{
    inline void assertion_failed(char const * expr, char const * function, char const * file, long line)
    {
        autopsy(file, line);
    }
} // namespace boost


//  for use with shared_ptr<>
struct null_deleter
{
    void operator()(void const *) const  {}
};

