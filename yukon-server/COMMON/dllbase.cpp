/*-----------------------------------------------------------------------------*
*
* File:   dllbase
*
* Date:   2/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/dllbase.cpp-arc  $
* REVISION     :  $Revision: 1.27.2.2 $
* DATE         :  $Date: 2008/11/17 19:46:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <winbase.h>
#include <winsock.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <process.h>

#include <rw/toolpro/winsock.h>
#include <rw\thr\mutex.h>
#include <rw\ctoken.h>

#include "dsm2.h"
#include "os2_2w32.h"
#include "cticalls.h"
#include "configparms.h"
#include "dbaccess.h"
#include "ctinexus.h"
#include "logger.h"
#include "utility.h"

#include "thread_monitor.h"

using namespace std;

extern VOID PortPipeCleanup (ULONG Reason);
extern void freeUCTMemory(void);

// Global Exports....
IM_EX_CTIBASE CTINEXUS           PorterNexus;
IM_EX_CTIBASE RWMutexLock        coutMux;
IM_EX_CTIBASE CtiThreadMonitor   ThreadMonitor;

/*
 *  These are the Configuration Parameters for the Real Time Database
 */

string     dbDll("ora15d.dll");
string     dbName("yukon");
string     dbUser("yukon");
string     dbPassword("yukon");

IM_EX_CTIBASE string     VanGoghMachine("127.0.0.1");     // Connect locally if we don't know any better
IM_EX_CTIBASE string     NotificationMachine("127.0.0.1");     // Connect locally if we don't know any better
IM_EX_CTIBASE INT        NotificationPort = NOTIFICATIONNEXUS;
IM_EX_CTIBASE string     gLogDirectory("\\yukon\\server\\log");
IM_EX_CTIBASE bool          gLogPorts = false;
IM_EX_CTIBASE bool          gOptimizeVersacom = false;
IM_EX_CTIBASE bool          gDoPrefix = false;
IM_EX_CTIBASE bool          gCoalesceRippleBits = false;
IM_EX_CTIBASE INT           DebugLevel = 0;
IM_EX_CTIBASE INT           Double = {FALSE};
IM_EX_CTIBASE INT           useVersacomTypeFourControl = 0;  // Jeesh if you can't figure this out...
IM_EX_CTIBASE INT           ModemConnectionTimeout = 60;     // Modem Connection Timeout in seconds (60 def.)
IM_EX_CTIBASE int           gMaxDBConnectionCount = 10;      // Maximum number of DB connections to allow to remain open.
IM_EX_CTIBASE bool          gDNPVerbose = false;
IM_EX_CTIBASE UINT          gDNPInternalRetries = 2;
IM_EX_CTIBASE bool          gDNPOfflineNonUpdated = false;
IM_EX_CTIBASE int           gDefaultCommFailCount = 10;
IM_EX_CTIBASE int           gDefaultPortCommFailCount = 5;
IM_EX_CTIBASE unsigned char gMCT400SeriesSPID = 0xFF;
IM_EX_CTIBASE short         gSimulatePorts = 0;
IM_EX_CTIBASE set<long>     gSimulatedPorts;
IM_EX_CTIBASE set<long>     gScanForceDevices;

set<long> ForeignCCUPorts;

/*
 *  These are global to the ctibase, but
 */

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);

            PorterNexus.NexusState  = CTINEXUS_STATE_NULL;     // Make sure no one thinks we are connected
            InitYukonBaseGlobals();                            // Load up the config file.

            // Set default database connection params
            setDatabaseParams(dbDll, dbName, dbUser, dbPassword);

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
            freeUCTMemory();

            break;
        }
    }
    return TRUE;
}

DLLEXPORT void InitYukonBaseGlobals(void)
{
    string str;
    int int_val;

    if( !(str = gConfigParms.getValueAsString("DB_DEBUGLEVEL")).empty() )
    {
        char *eptr;
        DebugLevel = strtoul(str.c_str(), &eptr, 16);
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_DEBULEVEL found : " << str << endl;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_DEBULEVEL   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DISPATCH_MACHINE")).empty() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_MACHINE   found : " << str << endl;
        VanGoghMachine = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_MACHINE   failed : " << endl;
    }

    if((str = gConfigParms.getValueAsString("NOTIFICATION_MACHINE", "")) != "")
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter NOTIFICATION_MACHINE  found : " << str << endl;
        NotificationMachine = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter NOTIFICATION_MACHINE   failed : " << endl;
    }

    if((int_val = gConfigParms.getValueAsInt("NOTIFICATION_PORT", -1)) != -1)
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter NOTIFICATION_PORT   found : " << int_val << endl;
        NotificationPort = int_val;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter NOTIFICATION_PORT   failed : " << endl;
    }

    if(!(str = gConfigParms.getValueAsString("DB_TYPE")).empty() ||
       !(str = gConfigParms.getValueAsString("DB_RWDBDLL")).empty() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_RWDBDLL/DB_TYPE found : " << str << endl;

        dbDll = str;
        std::transform(str.begin(), str.end(), str.begin(), ::tolower);

        if(str == "oracle")
        {
            #ifdef _DEBUG
            dbDll = "ora15d.dll";
            #else
            dbDll = "ora12d.dll";
            #endif
        }
        else if(str == "mssql")
        {
            #ifdef _DEBUG
            dbDll = "msq15d.dll";
            #else
            dbDll = "msq12d.dll";
            #endif
        }
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_RWDBDLL   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_SQLSERVER")).empty() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_SQLSERVER found : " << str << endl;
        dbName = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_SQLSERVER   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_USERNAME")).empty() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_USERNAME  found : " << str << endl;
        dbUser = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_USERNAME   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("DB_PASSWORD")).empty() )
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_PASSWORD  found : " << str << endl;
        dbPassword = str;
    }
    else
    {
        if(DebugLevel & 0x0001) cout << "Configuration Parameter DB_PASSWORD   failed : " << endl;
    }

    if( !(str = gConfigParms.getValueAsString("VERSACOM_TYPE_FOUR_CONTROL")).empty() && (!stricmp("TRUE", str.c_str())))
    {
        useVersacomTypeFourControl = TRUE;
    }
    if(DebugLevel & 0x0001) cout << "Versacom Control Commands are " << ( useVersacomTypeFourControl ? "Type 4 : ": "Extended (NOT Type 4) : ") << endl;


    if( !(str = gConfigParms.getValueAsString("PORTER_COALESCE_RIPPLE")).empty() && (!stricmp("TRUE", str.c_str())))
    {
        gCoalesceRippleBits = true;
    }
    if(DebugLevel & 0x0001) cout << "Ripple Control Commands are " << ( gCoalesceRippleBits ? "coalesced ": "NOT coalesced ") << endl;


    if( !(str = gConfigParms.getValueAsString("YUKON_LOG_PORTS")).empty() && (!stricmp("TRUE", str.c_str())))
    {
        gLogPorts = true;
    }
    if(DebugLevel & 0x0001) cout << "Ports will " << ( gLogPorts ? "log to file" : "NOT log to file") << endl;

    if( !(str = gConfigParms.getValueAsString("YUKON_DNP_VERBOSE")).empty() && (!stricmp("TRUE", str.c_str())))
    {
        gDNPVerbose = true;
    }
    if(DebugLevel & 0x0001) cout << "DNP output is " << ( gDNPVerbose ? "verbose" : "quiet") << endl;

    if( !(str = gConfigParms.getValueAsString("YUKON_DNP_INTERNAL_RETRIES")).empty() )
    {
        gDNPInternalRetries = abs(atoi(str.c_str()));
        if(DebugLevel & 0x0001) cout << "DNP Internal Retries set to " << str << endl;
    }
    else
    {
        gDNPInternalRetries = 2;
        if(DebugLevel & 0x0001) cout << "DNP Internal Retries set to 2" << endl;
    }

    if( !(str = gConfigParms.getValueAsString("YUKON_DNP_OFFLINE_IS_NONUPDATED")).empty() && (!stricmp("TRUE", str.c_str())))
    {
        gDNPOfflineNonUpdated = true;
    }
    if(DebugLevel & 0x0001) cout << "DNP points interpret unset online flags as " << ( gDNPOfflineNonUpdated ? "non-updated" : "normal") << endl;


    //  this is the MCT 400 SPID
    if(!(str = gConfigParms.getValueAsString("YUKON_MCT400SERIESSPID")).empty())
    {
        gMCT400SeriesSPID = strtoul(str.c_str(), 0, 16) & 0xff;

        if( !gMCT400SeriesSPID )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - YUKON_MCT400SERIESSPID was read as 0, setting to 0xff **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            gMCT400SeriesSPID = 0xff;
        }
    }

    if( !(str = gConfigParms.getValueAsString("MODEM_CONNECTION_TIMEOUT")).empty() )
    {
        ModemConnectionTimeout = atoi(str.c_str());
        if(DebugLevel & 0x0001) cout << "Modem Connection Timeout is set to " << str << " seconds" << endl;
    }
    else
    {
        ModemConnectionTimeout = 60;
        if(DebugLevel & 0x0001) cout << "Modem Connection Timeout is set to 60 seconds" << endl;
    }

    gLogDirectory = gConfigParms.getValueAsPath("LOG_DIRECTORY", "server\\log");
    if(DebugLevel & 0x0001) cout << "Yukon Log Directory " << gLogDirectory << endl;

    if(gConfigParms.isOpt("PORTER_ADD_TAP_PREFIX"))
    {
        str = gConfigParms.getValueAsString("PORTER_ADD_TAP_PREFIX");

        if(!stricmp("TRUE", str.c_str()))
        {
            gDoPrefix = true;
        }
    }

    if( !(str = gConfigParms.getValueAsString("YUKON_DEFAULT_COMM_FAIL_COUNT")).empty() )
    {
        gDefaultCommFailCount = atoi(str.c_str());
    }
    else
    {
        gDefaultCommFailCount = 10;
    }
    if(DebugLevel & 0x0001) cout << " Default Yukon comm fail count is " << gDefaultCommFailCount << endl;

    if( !(str = gConfigParms.getValueAsString("YUKON_DEFAULT_PORT_COMM_FAIL_COUNT")).empty() )
    {
        gDefaultPortCommFailCount = atoi(str.c_str());
    }
    if(DebugLevel & 0x0001) cout << " Default Yukon PORT comm fail count is " << gDefaultPortCommFailCount << endl;

    if( !(str = gConfigParms.getValueAsString("YUKON_SCAN_FORCE")).empty() )
    {
        boost::char_separator<char> sep(",");
        Boost_char_tokenizer tok(str, sep);
        Boost_char_tokenizer::iterator tok_iter = tok.begin();

        string id_str;
        long   id;

        gScanForceDevices.clear();

        while( (tok_iter != tok.end()) && !(id_str = *tok_iter++).empty() )
        {
            if( id = atol(id_str.c_str()) )
            {
                gScanForceDevices.insert(id);
            }
        }
    }

    if( !(str = gConfigParms.getValueAsString("YUKON_SIMULATE_PORTS")).empty() )
    {
        //  Examples:
        //
        //  In a system with portids 15, 49, and 78:
        //
        //  YUKON_SIMULATE_PORTS    TRUE            - all ports are simulated           (15, 49, 78)
        //  YUKON_SIMULATE_PORTS    TRUE,15,78      - the listed ports are simulated    (15, 78)
        //  YUKON_SIMULATE_PORTS    TRUE,EXCLUDE,15 - all ports EXCEPT 15 are simulated (49, 78)

        boost::char_separator<char> sep(",");
        Boost_char_tokenizer tok(str, sep);
        Boost_char_tokenizer::iterator tok_iter = tok.begin();


        string id_str;
        long      id;

        gSimulatedPorts.clear();

        while( (tok_iter != tok.end()) && !(id_str = *tok_iter++).empty() )
        {
            //  if it's uninitialized
            if( gSimulatePorts == 0 && string_equal(id_str,"true") )
            {
                gSimulatePorts =  1;
            }
            //  if they want to exclude instead, make sure they already turned it on
            else if( gSimulatePorts == 1 && string_equal(id_str,"exclude") )
            {
                gSimulatePorts = -1;
            }
            else if( id = atol(id_str.c_str()) )
            {
                gSimulatedPorts.insert(id);
            }
        }

        //  if they don't say to include anything, include them all by default
        if( gSimulatePorts == 1 && gSimulatedPorts.size() == 0 )
        {
            gSimulatePorts = -1;
        }
    }
    if(DebugLevel & 0x0001)
    {
        cout << CtiTime() << " Ports " << (gSimulatePorts?("ARE"):("are NOT")) << " being simulated" << endl;

        if( gSimulatePorts )
        {
            if( gSimulatePorts > 0 )    cout << CtiTime() << " Simulated portids (" << gSimulatedPorts.size() << "): " << endl;
            else                        cout << CtiTime() << " Excluded portids (" << gSimulatedPorts.size() << "): " << endl;

            for( set<long>::const_iterator itr = gSimulatedPorts.begin(); itr != gSimulatedPorts.end(); itr++ )
            {
                if( itr != gSimulatedPorts.begin() )
                {
                    cout << ", ";
                }

                cout << *itr;
            }

            cout << endl;
        }
    }

    if( !(str = gConfigParms.getValueAsString("YUKON_FOREIGN_CCU_PORTS")).empty() )
    {
        boost::char_separator<char> sep(",");
        Boost_char_tokenizer tok(str, sep);
        Boost_char_tokenizer::iterator tok_iter = tok.begin();

        string id_str;
        long   id;

        ForeignCCUPorts.clear();

        while( (tok_iter != tok.end()) && !(id_str = *tok_iter++).empty() )
        {
            if( id = atol(id_str.c_str()) )
            {
                ForeignCCUPorts.insert(id);
            }
        }
    }
    /*
    if( !(str = gConfigParms.getValueAsString("DB_ERROR_IGNORE")).empty() )
    {
        //  Examples:
        //  DB_ERROR_IGNORE : 15,78      - the listed errors are ignored if they occur.  (15, 78)

        boost::char_separator<char> sep(", ");
        Boost_char_tokenizer tok(str, sep);
        Boost_char_tokenizer::iterator tok_iter = tok.begin();

        resetDBIgnore();

        string err_str;
        long   err;

        while( (tok_iter != tok.end()) && !(err_str = *tok_iter++).empty() )
        {
            if( err = atol(err_str.c_str()) )
            {
                addDBIgnore(err);
            }
        }
    }*/
}

DLLEXPORT INT getDebugLevel(void)
{
    return DebugLevel;
}

DLLEXPORT bool isDebugLudicrous(void)
{
    static const string debug_high = "DEBUG_GENERAL_HIGH";
    return gConfigParms.isTrue(debug_high);
}

DLLEXPORT INT isForeignCcuPort(INT portid)
{
    return ForeignCCUPorts.find(portid) != ForeignCCUPorts.end();
}




