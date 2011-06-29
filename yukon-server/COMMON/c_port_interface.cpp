#include "yukon.h"

#include <iostream>
using namespace std;

#include "dlldefs.h"

#include <time.h>

#include "os2_2w32.h"
#include "dsm2.h"
#include "queues.h"
#include "porter.h"
class CtiRoute;

#include "c_port_interface.h"

DLLEXPORT INT CheckUtilID (USHORT a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << Cti::endl;
   return 0;
}

DLLEXPORT INT GetUtilID (PUSHORT a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << Cti::endl;
   return 0;
}


