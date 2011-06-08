
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMSERVICE_H
#define CTILMSERVICE_H

#include "cservice.h"
#include "loadmanager.h"
#include "lmcontrolareastore.h"
#include "executor.h"
#include "clistener.h"
#include "dllBase.h"

class CtiLMService : public CService
{
public:
    CtiLMService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);
    virtual void Run();

    void RunInConsole(DWORD argc, LPTSTR* argv);

    DECLARE_SERVICE(CtiLMService, LOADMANAGEMENT)

protected:
   
    virtual void OnStop();
    virtual void Init();
    virtual void DeInit();
    virtual void ParseArgs(DWORD argc, LPTSTR* argv);

private:
    bool _quit;
    std::string _config_file;
};
#endif

