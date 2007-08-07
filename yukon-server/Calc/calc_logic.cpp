#include "yukon.h"

#include <windows.h>
#include <rw/thr/thrutil.h>
#include <rw/collstr.h>
#include <rw/toolpro/winsock.h>

#include "dbaccess.h"
//  #include "monitor.h"
#include "CServiceConfig.h"
#include "rtdb.h"
#include "ctibase.h"
#include "cparms.h"
#include "dbghelp.h"
#include "clrdump.h"
#include "configparms.h"

#include "calclogicsvc.h"

using namespace std;

int install( DWORD dwStart = SERVICE_DEMAND_START );
int remove( void );

LPTSTR szServiceName = "CALCLOGIC";
LPTSTR szDisplayName = "Yukon Calc-Logic Service";

int main( int argc, char *argv[] )
{
    RWWinSockInfo sock_init;        // global declare for winsock

    HANDLE hExclusion;
    RegisterFilter( L"CALC_LOGIC_MAIN.DMP", MiniDumpWithDataSegs );
    // If set console does not fail then we are running as a console application
    if( (hExclusion = OpenEvent(EVENT_ALL_ACCESS, FALSE, "CalcLogic")) != NULL )
    {
       // Oh no, calc_logic is running on this machine already.
       CloseHandle(hExclusion);
       cout << CompileInfo.project << " is already running!" << endl;
       exit(-1);
    }

    hExclusion = CreateEvent(NULL, TRUE, FALSE, "CalcLogic");

    if( hExclusion == (HANDLE)NULL )
    {
       cout << "Couldn't create CalcLogic event!" << endl;
       exit(-1);
    }

    if( setConsoleTitle(CompileInfo) )
    {
        //  Process command line
        //  Process command line
        if( argc > 1 && strcmp(argv[1], "-install") == 0 )
        {
            return install(SERVICE_DEMAND_START);
        }
        else if( argc > 1 && strcmp(argv[1], "-auto") == 0 )
        {
            return install(SERVICE_AUTO_START);
        }
        else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
        {
            return remove();
        }
        else if( argc > 1 && strcmp(argv[1], "-version" ) == 0 )
        {
            cout << CompileInfo.project << " [Version " << CompileInfo.version << "]" << endl;
        }
        else
        {
            CtiCalcLogicService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

            service.RunInConsole( argc, argv );
        }
    }
    else
    {
        // run as a service
        CtiCalcLogicService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiCalcLogicService, CALCLOGIC)
        END_SERVICE_MAP
    }

    return 0;
}

int install( DWORD dwStart )
{
    cout << CtiTime( ) << " - Installing as a service..." << endl;

    string depends;

    string str;
    char var[128];

    strcpy(var, "SERVICE_DEPENDENCIES");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        depends = str;
        cout << "Service is dependent on the following services:" << endl
             << str << endl;
    }
    else
    {
        cout << "Unable to obtain '" << var << "' value from cparms." << endl;
        cout << "Couldn't locate any services that this service is to be dependent upon" << endl
             << "installing anyway" << endl;
    }

    /*
    // Attempt to determine any services we are going to be dependent on
    HINSTANCE hLib = LoadLibrary( "cparms.dll" );
    if( hLib )
    {
        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        if( (*fpGetAsString)( "SERVICE_DEPENDENCIES", depend, 1000 ) )
        {
            cout << "Service is dependent on the following services:" << endl
                 << depend << endl;
        }
        else
        {
            depend[0] = NULL;
            cout << "Couldn't locate any services that this service is to be dependent upon" << endl
                 << "installing anyway" << endl;
        }
    }*/

    cout << CtiTime( )  << " - Installing " << szDisplayName << "..." << endl;

    /*char* tmp = str;

    //replace whitespace with '\0'
    while( (tmp = strchr( tmp, ' ')) != NULL )
        *tmp = '\0';
    //check whether or not we found dependencies
    if( depend[0] == NULL )
        tmp = NULL;
    else
        tmp = depend;*/

    CServiceConfig si(szServiceName, szDisplayName);

    si.Install( SERVICE_WIN32_OWN_PROCESS,
                dwStart,
                NULL, // Should use depends in future
                NULL, // Use LocalSystem Account
                NULL);

    return 0;
}

int remove( void )
{
    cout << CtiTime( )  << " - Removing " << szDisplayName << "..." << endl;
    CServiceConfig si(szServiceName, szDisplayName);
    si.Remove( );

    return 0;
}
