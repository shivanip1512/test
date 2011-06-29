#include "yukon.h"

#include "dllbase.h"
#include "tbl_signal.h"

using std::string;

void main(int argc, char** argv)
{

   if(argc >= 5)
   {
      CtiTableSignal sig(atoi(argv[1]),
                         CtiTime(),
                         0,
                         string(argv[2]),
                         string(argv[3]),
                         0,
                         0,
                         0,
                         string("dbsigsend"));

      sig.setPriority(atoi(argv[4]));
      sig.Insert();
   }
   else
   {
      std::cout << argv[0] << " parameters: PointId, Description, Action, Priority" << Cti::endl;

   }

   return;
}
