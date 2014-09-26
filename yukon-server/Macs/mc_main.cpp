#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   mcmain
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_main.cpp-arc  $
* REVISION     :  $Revision: 1.12.12.2 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc_main.cpp

        Programmer:   Aaron Lauinger

        Description:  Entry point for Metering and Control Server

        Initial Date: 4/7/99

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999
---------------------------------------------------------------------------*/

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "ctitime.h"
//#include <rw/toolpro/winsock.h>

#include "CServiceConfig.h"
#include "id_macs.h"
#include "mc_svc.h"
#include "ctibase.h"

#include <iostream>
#include <iterator>
#include <vector>
#include <algorithm>

#include <crtdbg.h>

#include "connection_base.h"
// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

using namespace std;

int main(int argc, char* argv[] )
{
    LPTSTR szName = MC_SERVICE_NAME;
    LPTSTR szDisplay = MC_SERVICE_DISPLAY_NAME;

    try
    {
        if( ! Cti::createExclusiveEvent("MC_EXCLUSION_EVENT") )
        {
            cerr << CompileInfo.project <<" is already running on this machine, exiting."<< endl;
            exit(-1);
        }
    }
    catch( const std::exception& e )
    {
        cerr << e.what() << endl;
        exit(-1);
    }

    // Hack to detect whether we are running as a service
    // or in a console
    if( Cti::setConsoleTitle(CompileInfo) )
    {
        if ( argc > 1 )
        {
            //Process command line
            if (strcmp(argv[1], "-install") == 0)
            {
                cout << CtiTime()  << " - Installing as a service..." << endl;
                CServiceConfig si(szName, szDisplay);
                si.Install(SERVICE_WIN32_OWN_PROCESS,
                           SERVICE_DEMAND_START,
                           NULL,
                           NULL,
                           NULL );
                return 0;
            }
            else if (strcmp(argv[1], "-auto") == 0)
            {
                cout << CtiTime()  << " - Installing as a service..." << endl;
                CServiceConfig si(szName, szDisplay);
                si.Install(SERVICE_WIN32_OWN_PROCESS,
                           SERVICE_AUTO_START,
                           NULL,
                           NULL,
                           NULL );
                return 0;
            }
            else
            if ( strcmp(argv[1], "-remove" ) == 0 )
            {
                cout << CtiTime()  << " - Removing service..." << endl;
                CServiceConfig si(szName, szDisplay);
                si.Remove();
                return 0;
            }
        }

        CtiMCService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );

        if ( argc > 2 )
            argv[1] = argv[2];

        service.RunInConsole(argc - 1, argv );
    }
    else
    {
        CtiMCService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiMCService, MACS)
        END_SERVICE_MAP
    }

    return 0;
}
