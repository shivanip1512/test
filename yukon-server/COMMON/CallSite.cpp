#include "precompiled.h"

#include "CallSite.h"

namespace Cti {

CallSite::CallSite(const char * func_, const char * file_, const unsigned line_) 
    :   func { func_ },
        file { trimPath(file_) },
        line { line }
{
}

const char * CallSite::trimPath(const char * file)
{
    auto pos = std::strrchr(file, '\\');

    return pos ? pos + 1 : file;
}

const char * CallSite::getFunc() const { return func; }
const char * CallSite::getFile() const { return file; }
unsigned     CallSite::getLine() const { return line; }

}