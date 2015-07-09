#include "precompiled.h"

#include "module_util.h"
#include "utility.h"

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            Cti::identifyProject(CompileInfo);
            break;
        }
        case DLL_THREAD_ATTACH:
        {
            break;
        }
        case DLL_THREAD_DETACH:
        {
            break;
        }
        case DLL_PROCESS_DETACH:
        {
            break;
        }
    }

    return TRUE;
}

namespace boost
{
    void assertion_failed_msg(char const * expr, char const * msg, char const * function, char const * file, long line)
    {
        std::cerr << expr << ": " << msg << " at " << function << " (" << file << ":" << line << ")" << std::endl;
        autopsy(file, line);
    }
}

