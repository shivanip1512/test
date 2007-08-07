#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mcsvc
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_svc.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2007/08/07 21:04:32 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*---------------------------------------------------------------------------

        Filename:  mc_svc.h

        Programmer:  Aaron Lauinger

        Description:  Header file for CtiMCService

        Initial Date: 4/7/99

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTIMCSERVICE_H
#define CTIMCSERVICE_H

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
#endif

