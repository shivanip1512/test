#pragma once

#include <windows.h>

#include <string>
#include <iostream>

#include "cservice.h"
#include "mc_server.h"
#include "logger.h"

static HANDLE hShutdown;

class CtiMCService : public CService
{
public:

    CtiMCService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);
    virtual void Run();

    void RunInConsole(DWORD argc, LPTSTR* argv);

    DECLARE_SERVICE(CtiMCService, MACS)

protected:

    virtual void OnStop();
    virtual void Init();
    virtual void DeInit();
    virtual void ParseArgs(DWORD argc, LPTSTR* argv);

private:

    CtiMCServer* _mc_server;
};
