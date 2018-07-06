#pragma once

#include "dlldefs.h"

namespace Cti {

struct IM_EX_CTIBASE CallSite
{
    CallSite(const char * func, const char * file, const unsigned line);

    const char * getFunc() const;
    const char * getFile() const;
    unsigned getLine() const;

protected:
    static const char * trimPath(const char * file);

private:
    const char *func;
    const char *file;
    const unsigned line;
};

}

#define CALLSITE (::Cti::CallSite{__FUNCTION__, __FILE__, __LINE__})