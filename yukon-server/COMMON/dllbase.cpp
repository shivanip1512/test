
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dllbase
*
* Date:   2/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/dllbase.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/05/02 17:02:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/



#include <windows.h>
#include <winbase.h>
#include <winsock.h>
#include <stdio.h>
#include <string.h>
#include <process.h>

#include <rw/toolpro/winsock.h>
#include <rw/db/db.h>
#include <rw\thr\mutex.h>
#include <rw\cstring.h>

#include "dsm2.h"
#include "yukon.h"
#include "os2_2w32.h"
#include "cticalls.h"
#include "configparms.h"
#include "dbaccess.h"
#include "ctinexus.h"
#include "logger.h"
#include "utility.h"

extern VOID PortPipeCleanup (ULONG Reason);

// Global Exports....
IM_EX_CTIBASE CTINEXUS        PorterNexus;
IM_EX_CTIBASE RWMutexLock     coutMux;

RWDBDatabase *sqlDatabase = NULL;

/*
 *  These are the Configuration Parameters for the Real Time Database
 */

IM_EX_CTIBASE RWCString       dbDll("ora15d.dll");
IM_EX_CTIBASE RWCString       dbName("yukon");
IM_EX_CTIBASE RWCString       dbUser("yukon");
IM_EX_CTIBASE RWCString       dbPassword("yukon");
IM_EX_CTIBASE RWCString       VanGoghMachine("127.0.0.1");     // Connect locally if we don't know any better
IM_EX_CTIBASE RWCString       gSMTPServer("mail");
IM_EX_CTIBASE RWCString       gEmailFrom;
IM_EX_CTIBASE RWCString       gLogDirectory("\\yukon\\server\\log");
IM_EX_CTIBASE bool            gLogPorts = false;
IM_EX_CTIBASE bool            gOptimizeVersacom = false;
IM_EX_CTIBASE bool            gDoPrefix = false;
IM_EX_CTIBASE bool            gCoalesceRippleBits = false;
IM_EX_CTIBASE INT             DebugLevel = 0;
IM_EX_CTIBASE INT             Double = {FALSE};
IM_EX_CTIBASE INT             useVersacomTypeFourControl = 0;  // Jeesh if you can't figure this out...
IM_EX_CTIBASE INT             ModemConnectionTimeout = 60;     // Modem Connection Timeout in seconds (60 def.)
IM_EX_CTIBASE int             gMaxDBConnectionCount = 5;       // Maximum number of DB connections to allow to remain open.

/*
 *  These are global to the ctibase, but
 */

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
    case DLL_PROCESS_ATTACH:
        {
            // cout << "CTIBase DLL initializing!" << endl;
            identifyProject(CompileInfo);
            PorterNexus.NexusState  = CTINEXUS_STATE_NULL;     // Make sure no one thinks we are connected
            InitYukonBaseGlobals();                            // Load up the config file.

            // Set default database connection params
            setDatabaseParams(0, dbDll, dbName, dbUser, dbPassword);
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
            PortPipeCleanup(0);                                // Get that connection closed (if open)!
            // releaseDatabase();
            break;
        }
    }
    return TRUE;
}

DLLEXPORT bool OptimizeVersacomConfiguration()
{
    return gOptimizeVersacom;
}

DLLEXPORT void InitYukonBaseGlobals(void)
{
    RWCString str;

    if( !(str = gConfigParms.getValueAsString("DB_DEBUGLEVEL")).isNull() )
    {
        char *eptr;
        DebugLevel = strtoul(str.data(), &eptr, 16);
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_DEBULEVEL found : " << str << endl;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_DEBULEVEL   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DISPATCH_MACHINE")).isNull() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_MACHINE   found : " << str << endl;
        VanGoghMachine = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_MACHINE   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_RWDBDLL")).isNull() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_RWDBDLL   found : " << str << endl;
        dbDll = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_RWDBDLL   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_SQLSERVER")).isNull() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_SQLSERVER found : " << str << endl;
        dbName = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_SQLSERVER   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_USERNAME")).isNull() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_USERNAME  found : " << str << endl;
        dbUser = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_USERNAME   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_PASSWORD")).isNull() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_PASSWORD  found : " << str << endl;
        dbPassword = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_PASSWORD   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("VERSACOM_TYPE_FOUR_CONTROL")).isNull() && (!stricmp("TRUE", str.data())))
    {
        useVersacomTypeFourControl = TRUE;
    }
    if(DebugLevel & 0x0001) cout << "Versacom Control Commands are " << ( useVersacomTypeFourControl ? "Type 4 : ": "Extended (NOT Type 4) : ") << endl;


    if( !(str = gConfigParms.getValueAsString("PORTER_COALESCE_RIPPLE")).isNull() && (!stricmp("TRUE", str.data())))
    {
        gCoalesceRippleBits = true;
    }
    if(DebugLevel & 0x0001) cout << "Ripple Control Commands are " << ( gCoalesceRippleBits ? "coalesced ": "NOT coalesced ") << endl;


    if( !(str = gConfigParms.getValueAsString("YUKON_LOG_PORTS")).isNull() && (!stricmp("TRUE", str.data())))
    {
        gLogPorts = true;
    }
    if(DebugLevel & 0x0001) cout << "Ports will " << ( gLogPorts ? "log to file" : "NOT log to file") << endl;

    if( !(str = gConfigParms.getValueAsString("OPTIMIZE_VERSACOM_CONFIGURATION")).isNull() && (!stricmp("TRUE", str.data())))
    {
        gOptimizeVersacom = true;
    }
    if(DebugLevel & 0x0001) cout << "Versacom configuration " << ( gOptimizeVersacom ? "will " : "will NOT ") << "be optimized (section,class,division) "<< endl;



    if( !(str = gConfigParms.getValueAsString("MAX_DBCONNECTION_COUNT")).isNull() )
    {
        gMaxDBConnectionCount = atoi(str.data());
        if(DebugLevel & 0x0001) cout << "Max of " << gMaxDBConnectionCount << " RWDB connections allowed" << endl;
    }
    else
    {
        gMaxDBConnectionCount = 5;
        if(DebugLevel & 0x0001) cout << "Max of " << gMaxDBConnectionCount << " RWDB connections allowed" << endl;
    }

    if( !(str = gConfigParms.getValueAsString("MODEM_CONNECTION_TIMEOUT")).isNull() )
    {
        ModemConnectionTimeout = atoi(str.data());
        if(DebugLevel & 0x0001) cout << "Modem Connection Timeout is set to " << str << " seconds" << endl;
    }
    else
    {
        ModemConnectionTimeout = 60;
        if(DebugLevel & 0x0001) cout << "Modem Connection Timeout is set to 60 seconds" << endl;
    }

    if( !(str = gConfigParms.getValueAsString("YUKON_SMTP_SERVER")).isNull() )
    {
        gSMTPServer = str;
        if(DebugLevel & 0x0001) cout << "SMTP Server set to " << gSMTPServer << endl;
    }
    else
    {
        gSMTPServer = "mail";
        if(DebugLevel & 0x0001) cout << "SMTP Server defaulted to " << gSMTPServer << endl;
        if(DebugLevel & 0x0001) cout << "  Use YUKON_SMTP_SERVER to set SMTP Server" << endl;
    }

    if( !(str = gConfigParms.getValueAsString("YUKON_EMAIL_FROM")).isNull() )
    {
        gEmailFrom = str;
        if(DebugLevel & 0x0001) cout << "Sent email is from " << gEmailFrom << endl;
    }
    else
    {
        CHAR buf[UNLEN+1];
        DWORD len = sizeof(buf)-1;

        if(!GetUserName(buf, &len))
        {
            gEmailFrom = RWCString("notification@yukon_master.com");
        }
        else
        {
            gEmailFrom = RWCString(buf);
        }

        if(DebugLevel & 0x0001) cout << "Sent email from address defaulted to current logged user" << gEmailFrom << endl;
        if(DebugLevel & 0x0001) cout << "  Use YUKON_EMAIL_FROM to set reply address" << endl;
    }

    if( !(str = gConfigParms.getValueAsString("LOG_DIRECTORY")).isNull() )
    {
        gLogDirectory = str;
        if(DebugLevel & 0x0001) cout << "Yukon Log Directory " << gLogDirectory << endl;
    }
    else
    {
        gLogDirectory = "\\yukon\\server\\log";
        if(DebugLevel & 0x0001) cout << "Yukon Log Directory " << gLogDirectory << endl;
        if(DebugLevel & 0x0001) cout << "  Use LOG_DIRECTORY to set Log Directory" << endl;
    }

    if(gConfigParms.isOpt("PORTER_ADD_TAP_PREFIX"))
    {
        str = gConfigParms.getValueAsString("PORTER_ADD_TAP_PREFIX");

        if(!stricmp("TRUE", str.data()))
        {
            gDoPrefix = true;
        }
    }
}

DLLEXPORT INT getDebugLevel(void)
{
    return DebugLevel;
}






