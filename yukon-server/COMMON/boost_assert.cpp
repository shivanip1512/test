#include "precompiled.h"

#include <iostream>
#include "utility.h"  //  defines autopsy()

namespace boost
{
    void assertion_failed(char const * expr, char const * function, char const * file, long line)
    {
        autopsy(file, line);
    }

    void assertion_failed_msg(char const * expr, char const * msg, char const * function, char const * file, long line)
    {
        std::cerr << expr << ": " << msg << " at " << function << " (" << file << ":" << line << ")" << std::endl;
        autopsy(file, line);
    }
} // namespace boost

