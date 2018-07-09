#include "precompiled.h"

#include "CallSite.h"

namespace Cti {

CallSite::CallSite(const char * func_, const char * file_, const long line_) 
    :   func { func_ },
        filename { trimPath(file_) },
        fullpath { file_ },
        line { line_ }
{
}

const char * CallSite::trimPath(const char * file)
{
    auto pos = std::strrchr(file, '\\');

    return pos ? pos + 1 : file;
}

const char * CallSite::getFunction()     const { return func; }
const char * CallSite::getFilename() const { return filename; }
const char * CallSite::getFullPath() const { return fullpath; }
long         CallSite::getLine()     const { return line; }

}