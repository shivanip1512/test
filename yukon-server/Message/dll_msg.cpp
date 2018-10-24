#include "precompiled.h"

#include "module_util.h"
#include "dlldefs.h"
#include "amq_constants.h"
#include "amq_connection.h"


BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            Cti::identifyLibrary(CompileInfo);

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


namespace Cti {
namespace Messaging {

IM_EX_MSG std::unique_ptr<ActiveMQConnectionManager> gActiveMQConnection = std::make_unique<ActiveMQConnectionManager>();
}
}

