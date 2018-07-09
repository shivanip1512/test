#pragma once

#include "dlldefs.h"

namespace Cti {

struct IM_EX_CTIBASE CallSite
{
    CallSite(const char * func, const char * file, const long line);

    const char * getFunction() const;
    const char * getFilename() const;
    const char * getFullPath() const;
    long getLine() const;

protected:
    static const char * trimPath(const char * file);

private:
    const char *func;
    const char *filename;
    const char *fullpath;
    const long line;
};

}

#define CALLSITE (::Cti::CallSite{__FUNCTION__, __FILE__, __LINE__})