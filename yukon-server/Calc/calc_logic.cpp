#include "precompiled.h"

#include "dbaccess.h"
//  #include "monitor.h"
#include "CServiceConfig.h"
#include "rtdb.h"
#include "dllbase.h"
#include "cparms.h"
#include "dbghelp.h"
#include "calclogicsvc.h"
#include "connection_base.h"
#include "logManager.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

using namespace std;

int install( DWORD dwStart = SERVICE_DEMAND_START );
int remove( void );

LPTSTR szServiceName = "CALCLOGIC";
LPTSTR szDisplayName = "Yukon Calc-Logic Service";

int main( int argc, char *argv[] )
{
    try
    {
        if( ! Cti::createExclusiveEvent("CalcLogic") )
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

    // If set console does not fail then we are running as a console application
    if( Cti::setConsoleTitle(CompileInfo) )
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

    cout << CtiTime( )  << " - Installing " << szDisplayName << "..." << endl;

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
