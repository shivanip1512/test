#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/toolpro/neterr.h>

#include "collectable.h"
#include "pil_conmgr.h"
#include "pil_exefct.h"

#include "msg_cmd.h"

#include <rw\thr\mutex.h>
#include "dlldefs.h"
DLLIMPORT extern RWMutexLock coutMux;



// Not a thing here either...
