#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "cservice.h"

#define MAX_FDR_INTERFACES              25
#define CPARM_NAME_FDR_INTERFACES       "FDR_INTERFACES"
#define CPARM_NAME_SRV_DEPENDENCIES     "SERVICE_DEPENDENCIES"

static HANDLE iShutdown;

#define FDR_APPLICATION_NAME  "FDR Service"

class CtiFDRService : public CService
{
    public:
        DEBUG_INSTRUMENTATION;

        CtiFDRService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);
        virtual void Run();
    
        void RunInConsole(DWORD argc, LPTSTR* argv);

        DECLARE_SERVICE(CtiFDRService, FDR)
    
    protected:
        virtual void OnStop();
        virtual void Init();
        virtual void DeInit();
        virtual void ParseArgs(DWORD argc, LPTSTR* argv);
    
        void startInterfaces();
        void stopInterfaces();
    
        typedef struct 
        {
            int (FAR WINAPI *StartFunction)(void);
            int (FAR WINAPI *StopFunction)(void);
        } InterfaceList;

        /** Global definitions **/
        InterfaceList interfacesList[MAX_FDR_INTERFACES];

    private:
        bool    iGoodStatus;            //_ok;
        int     iInterfaceCount;
        
};
