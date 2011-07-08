#include "yukon.h"

#include <iostream>
using namespace std;


#include "dlldefs.h"
#include "cparms.h"

void main(int argc, char **argv)
{
   if(argc < 2)
   {
      cout << " Please supply an integer argument " << Cti::endl;
      return;
   }
   #if 1
   string yukon_base = gConfigParms.getYukonBase();
   cout << "Yukon base: " << yukon_base << Cti::endl;

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


