#include "yukon.h"

#include <iostream>

using namespace std;

#include "shlwapi.h"
#include "cparms.h"
#include "boost/scoped_array.hpp"

IM_EX_CPARM CtiConfigParameters gConfigParms;

string getYukonBase();

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    if( ul_reason_for_call == DLL_PROCESS_ATTACH )
    {
        gConfigParms.setYukonBase(getYukonBase());
        gConfigParms.RefreshConfigParameters();
    }

    return TRUE;
}

string getYukonBase()
{
    char yukon_base_env[MAX_PATH];

    DWORD result = GetEnvironmentVariable("YUKON_BASE", yukon_base_env, MAX_PATH);

    PathUnquoteSpaces(yukon_base_env);
    PathRemoveBlanks(yukon_base_env);

    if( !result )
    {
        if( GetLastError() == ERROR_ENVVAR_NOT_FOUND )
        {
            cout << "Environment variable YUKON_BASE is missing" << endl;
        }
        else
        {
            cout << "Environment variable YUKON_BASE is empty" << endl;
        }
    }
    else if( result >= MAX_PATH )
    {
        cout << "Environment variable YUKON_BASE is too long (" << result << ")" << endl;
    }
    else if( !PathIsDirectory(yukon_base_env) )
    {
        cout << "Environment variable YUKON_BASE is not a directory" << endl;
    }
    else
    {
        return yukon_base_env;
    }

    const char *yukon_base_default = "c:\\yukon";

    cout << "YUKON_BASE defaulting to " << yukon_base_default << endl;

    return yukon_base_default;
}

