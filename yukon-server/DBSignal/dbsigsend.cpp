#include "yukon.h"
#include <windows.h>
#include "dllbase.h"
#include "tbl_signal.h"

void main(int argc, char** argv)
{

   if(argc >= 5)
   {
      CtiTableSignal sig(atoi(argv[1]),
                         RWTime().now(),
                         0,
                         RWCString(argv[2]),
                         RWCString(argv[3]),
                         0,
                         0,
                         0,
                         RWCString("dbsigsend"));

      sig.setPriority(atoi(argv[4]));
      sig.Insert();
   }
   else
   {
      cout << argv[0] << " parameters: PointId, Description, Action, Priority" << endl;

   }

   return;
}
