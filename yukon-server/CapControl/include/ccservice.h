/*-----------------------------------------------------------------------------
    Filename:  ccservice.h

    Programmer:  Josh Wolberg

    Description: Header file for CtiCCService

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCSERVICE_H
#define CTICCSERVICE_H

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
    RWCString _config_file;

    CtiCapController* controller;
    CtiCCClientListener* clientListener;
};
#endif


