#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/cstring.h>

#include "dlldefs.h"
#include "cparms.h"

void main(int argc, char **argv)
{
   if(argc < 2)
   {
      cout << " Please supply an integer argument " << endl;
      return;
   }
   #if 1
   RWCString base_dir = gConfigParms.getYukonBaseDir();
   cout << "Yukon base dir: " << base_dir << endl;

   for(int i = 0; i < atoi(argv[1]); i++)
   {
      gConfigParms.Dump();
      Sleep(5000);
   }
   #else
   CtiConfigParameters     Test(DefaultMasterConfigFileName);

   for(int i = 0; i < atoi(argv[1]); i++)
   {
      Test.Dump();
      Test.RefreshConfigParameters();

      Sleep(5000);
   }
   #endif
}


