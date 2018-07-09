#include "precompiled.h"

#include <iostream>
#include "utility.h"  //  defines autopsy()

namespace boost
{
    void assertion_failed(char const * expr, char const * function, char const * file, long line)
    {
        autopsy(Cti::CallSite { function, file, line }, expr);
    }

    void assertion_failed_msg(char const * expr, char const * msg, char const * function, char const * file, long line)
    {
        autopsy(Cti::CallSite { function, file, line }, expr, msg);
    }
} // namespace boost

