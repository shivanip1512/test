#pragma once

#include "dlldefs.h"
#include "ctitime.h"
#include "streamBuffer.h"

#include <windows.h>

namespace Cti {

struct compileinfo_t
{
   const char *project;
   const char *version;
   const char *details;
   const char *date;
};

IM_EX_CTIBASE void identifyExecutable(const compileinfo_t &info);
IM_EX_CTIBASE void identifyLibrary   (const compileinfo_t &info);

IM_EX_CTIBASE bool setConsoleTitle(const compileinfo_t &info);

IM_EX_CTIBASE HANDLE createExclusiveEvent(const char *eventName);

IM_EX_CTIBASE HANDLE createExclusiveEvent(bool manualReset,
                                          bool initialState,
                                          const char *eventName);

/* Called when we get an SEH exception.  Generates a minidump. */
#define ETN_MINIDUMP_EXCEPTION_FILTER \
static LONG WINAPI MinidumpExceptionFilter( LPEXCEPTION_POINTERS pExceptionPtrs ) \
{ \
    return Cti::MinidumpExceptionFilter( CompileInfo, pExceptionPtrs ); \
}


IM_EX_CTIBASE LONG WINAPI MinidumpExceptionFilter(const Cti::compileinfo_t &info, const LPEXCEPTION_POINTERS &pExceptionPtrs );

} // namespace Cti

extern Cti::compileinfo_t CompileInfo;

#ifndef BUILD_VERSION
#define BUILD_VERSION (untagged)
#endif

#ifndef BUILD_VERSION_DETAILS
#define BUILD_VERSION_DETAILS __TIMESTAMP__
#endif

// needed to turn a #define into a string
#define STRINGIZE( x ) #x

//  common info across all projects
#define SETCOMPILEINFO( x, y, z ) Cti::compileinfo_t CompileInfo = { x, STRINGIZE(y), STRINGIZE(z), __TIMESTAMP__ }

#define PROJECT_ID( x ) SETCOMPILEINFO( x, BUILD_VERSION, BUILD_VERSION_DETAILS )

