#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;

#include "macsparse.h"


void main(int argc, char **argv)
{
   MACSREQUEST Mycpy;

   if(argc < 2)
   {
      cout << "Need a string argument here" << endl;
   }

   CtiMACSParser  mp(argv[1]);

   mp.Dump();

   mp.replicateRequest(&Mycpy);

   // cout << Mycpy.Command << endl;


}
