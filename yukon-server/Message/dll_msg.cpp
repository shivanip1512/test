#include "yukon.h"
/*
#include <winbase.h>
#include <winsock.h>
#include <stdio.h>
#include <string.h>
#include <process.h>

#include <rw\thr\mutex.h>

#include "dsm2.h"
#include "os2_2w32.h"
#include "cticalls.h"
#include "connection.h"

#include "dllbase.h"*/
#include "utility.h"
#include "dlldefs.h"
#include "cparms.h"
#include "amq_connection.h"


BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);

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

IM_EX_MSG ActiveMQConnectionManager gActiveMQConnection(gConfigParms.getValueAsString("ACTIVEMQ_BROKER_URI"));

}
}

