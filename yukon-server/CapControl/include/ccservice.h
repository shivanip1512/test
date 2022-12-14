#pragma once

#include "cservice.h"

class CtiCapController;
class CtiCCClientListener;


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
    std::string _config_file;

    CtiCapController* controller;
    CtiCCClientListener* clientListener;
};


