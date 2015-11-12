#pragma once

#include "dlldefs.h"
#include "ctitime.h"
#include "version.h"
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

IM_EX_CTIBASE void identifyProject(const compileinfo_t &info);

IM_EX_CTIBASE bool setConsoleTitle(const compileinfo_t &info);

IM_EX_CTIBASE HANDLE createExclusiveEvent(const char *eventName);

IM_EX_CTIBASE HANDLE createExclusiveEvent(bool manualReset,
                                          bool initialState,
                                          const char *eventName);

IM_EX_CTIBASE bool isTimeToReportMemory(const CtiTime Now);

IM_EX_CTIBASE std::string reportPrivateBytes(const compileinfo_t &info);
IM_EX_CTIBASE std::string reportProcessTimes(const compileinfo_t &info);
IM_EX_CTIBASE std::string reportProcessorTimes();
IM_EX_CTIBASE double getCPULoad();

struct CallSite
{
    const char *file;
    const unsigned line;
};

//IM_EX_CTIBASE std::ostream &operator<<(std::ostream &o, const ::Cti::CallSite &cs);

//IM_EX_CTIBASE StreamBufferSink &operator<<(StreamBufferSink  &o, const ::Cti::CallSite &cs);

#define CALLSITE (::Cti::CallSite{__FILE__, __LINE__})

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

