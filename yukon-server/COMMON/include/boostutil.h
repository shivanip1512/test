#pragma once

#include <iostream>
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

namespace boost
{
    inline void assertion_failed_msg(char const * expr, char const * msg, char const * function, char const * file, long line)
    {
        std::cerr << expr << ": " << msg << " at " << function << " (" << file << ":" << line << ")" << std::endl;
        autopsy(file, line);
    }
}


