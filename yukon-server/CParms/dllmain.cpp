#include "yukon.h"
#include <windows.h>
#include <winreg.h>

#include <iostream>
using namespace std;

#include "cparms.h"

IM_EX_CPARM RWCString DefaultMasterConfigFileName("..\\config\\master.cfg");
IM_EX_CPARM RWCString ConfKeyRefreshRate("CONFIG_REFRESHRATE");

//The location and name of the configuration file is
//determined by a value in the registry
//the following is the name of the key
LPTSTR lpSubKey = "SOFTWARE\\Cannon Technologies\\CParms";
LPTSTR lpValueName = "Config";

IM_EX_CPARM CtiConfigParameters gConfigParms( RWCString() );   // Make the filename bool true on isNull()

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{

   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         BYTE buf[200];
         DWORD len = 200;

         HKEY key;

         if( RegOpenKeyEx( HKEY_LOCAL_MACHINE, lpSubKey, 0, KEY_READ, &key ) == ERROR_SUCCESS )
         {
            if( RegQueryValueEx( key, lpValueName, NULL, NULL, (unsigned char*)&buf, &len) == ERROR_SUCCESS )
            {
               DefaultMasterConfigFileName = (char*) buf;
            }

            RegCloseKey( key );
         }
         else
         {
            cout << "Unable to open subkey " << lpSubKey << endl;
            cout << " Configuration file is " << DefaultMasterConfigFileName << endl;
         }

         gConfigParms.setConfigFile(DefaultMasterConfigFileName).RefreshConfigParameters();

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
         break;
      }
   }
   return TRUE;
}

