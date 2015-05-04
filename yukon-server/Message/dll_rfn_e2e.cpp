#include "precompiled.h"

#include "module_util.h"
#include "dlldefs.h"
#include "rfn_e2e_messenger.h"


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


namespace Cti {
namespace Messaging {
namespace Rfn {

IM_EX_RFN_E2E std::unique_ptr<E2eMessenger> gE2eMessenger(new E2eMessenger);

}
}
}

