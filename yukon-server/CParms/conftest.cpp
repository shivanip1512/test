#include "yukon.h"

#include <winsock.h>

#include <iostream>
using namespace std;

#include <string.h>
#include <stdio.h>


#include "dlldefs.h"
#include "cparms.h"
#include "configparms.h"

int Delay = 1000;


void main(int argc, char **argv)
{
#define DO_DYNAMICALLY
#ifdef DO_DYNAMICALLY

   {
      char temp[80];

      cout << "Doing InitTestGlobals" << Cti::endl;
      HINSTANCE hLib = LoadLibrary(".\\cparms.dll");
      cout << "Got my library " << Cti::endl;


      if(hLib)
      {
         CPARM_DUMPCONFIG        fpDumpConfig  = (CPARM_DUMPCONFIG)GetProcAddress( hLib, "DumpConfigParms" );
         CPARM_ISCONFIG          fpisConfigOpt = (CPARM_ISCONFIG)GetProcAddress( hLib, "isConfigOpt" );
         CPARM_GETCONFIGSTRING   fpgetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

         // Dump all parameters
         (*fpDumpConfig)();
         cout << Cti::endl;

         // Selected search
         if((*fpisConfigOpt)("CONFIG_PARAM4"))
         {
            cout << "Seems to work FINE " << Cti::endl;
         }

         if((*fpgetAsString)("SLEEP_DELAY", temp, 64))
         {
            Delay = atoi(temp);
         }
         else
         {
            Delay = 1000;
         }

         if((*fpgetAsString)("DB_DEBUGLEVEL", temp, 64))
         {
            cout << "Configuration Parameter DB_DEBULEVEL found : " << temp << Cti::endl;
         }

         if((*fpgetAsString)("DISPATCH_MACHINE", temp, 64))
         {
            cout << "Configuration Parameter DISPATCH_MACHINE   found : " << temp << Cti::endl;
         }
         else
         {
            cout << "Configuration Parameter DISPATCH_MACHINE   failed : " << Cti::endl;
         }

         if((*fpgetAsString)("DB_RWDBDLL", temp, 64))
         {
            cout << "Configuration Parameter DB_RWDBDLL   found : " << temp << Cti::endl;
         }

         if((*fpgetAsString)("DB_SQLSERVER", temp, 64))
         {
            cout << "Configuration Parameter DB_SQLSERVER found : " << temp << Cti::endl;
         }

         if((*fpgetAsString)("DB_USERNAME", temp, 64))
         {
            cout << "Configuration Parameter DB_USERNAME  found : " << temp << Cti::endl;
         }

         if((*fpgetAsString)("DB_PASSWORD", temp, 64))
         {
            cout << "Configuration Parameter DB_PASSWORD  found : " << temp << Cti::endl;
         }

         if((*fpgetAsString)("VERSACOM_TYPE_FOUR_CONTROL", temp, 64))
         {
            cout << "Versacom Control Commands are Type 4 : " << temp << Cti::endl;
         }

         if((*fpgetAsString)("MODEM_CONNECTION_TIMEOUT", temp, 64))
         {
            cout << "Modem Connection Timeout is set to " << temp << " seconds" << Cti::endl;
         }

         Sleep(Delay);

         FreeLibrary(hLib);
      }

   }
#else

   Sleep(1000);

   CtiConfigParameters  *pConf = new CtiConfigParameters(DefaultMasterConfigFileName);

   pConf->Dump();

   delete pConf;

#endif
}

