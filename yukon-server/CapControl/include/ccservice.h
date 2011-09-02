#pragma once

#include "ccclientlistener.h"
#include "cservice.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "dllBase.h"

class CtiCCService : public CService
{
public:
    CtiCCService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);
    virtual void Run();

    void RunInConsole(DWORD argc, LPTSTR* argv);

    DECLARE_SERVICE(CtiCCService, CAPCONTROL)

protected:
   
    virtual void OnStop();
    virtual void Init();
    virtual void DeInit();
    virtual void ParseArgs(DWORD argc, LPTSTR* argv);

private:
    bool _quit;
    string _config_file;

    CtiCapController* controller;
    CtiCCClientListener* clientListener;
};


