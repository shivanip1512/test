#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;

#include "dlldefs.h"

#include <time.h>

#include "os2_2w32.h"
#include "dsm2.h"
#include "queues.h"
#include "porter.h"
// #include "device.h"
class DEVICE;
class CtiRoute;

#include "c_port_interface.h"

DLLEXPORT USHORT PrintLogEvent;

DLLEXPORT INT VConfigGetEqual (VERSACONFIG *a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT CheckUtilID (USHORT a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT GetUtilID (PUSHORT a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT GetStages (CtiRoute *a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}
DLLEXPORT INT RouteGetEqual (ROUTE *a)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}
DLLEXPORT INT ComErrorLogAdd (COMM_ERROR_LOG_STRUCT *a, ERRSTRUCT *b, USHORT c)
{
   // cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PortGetGT(PORT *PortRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PortGetFirst(PORT *PortRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}


DLLEXPORT INT DeviceGetEqual(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}


DLLEXPORT INT DeviceGetGT(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT DeviceGetNext(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT DeviceFastUpdate(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT DeviceLock(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT DeviceUnLock(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}


DLLEXPORT INT RemoteGetEqual(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteGetPortGT(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteGetPortFirst(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteGetPortEqual(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteGetGT(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteLock(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteUnLock(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteFastUpdate(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT RemoteGetFirst(REMOTE *RemoteRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}


DLLEXPORT INT DeviceGetFirst(DEVICE *DeviceRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PortFastUpdate(PORT *PortRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PortLock(PORT *PortRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PortUnLock(PORT *PortRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}


DLLEXPORT INT PointLock(CTIPOINT *PointRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PointUnLock(CTIPOINT *PointRecord)
{
   cout <<" Progress: " << __FILE__ << " " << __LINE__ << endl;
   return 0;
}

DLLEXPORT INT PointGetEqual(CTIPOINT *PointRecord)
{
   return 0;
}

DLLEXPORT INT PointFastUpdate(CTIPOINT *PointRecord)
{
   return 0;
}

DLLEXPORT INT PointGetDeviceTypeFirst(CTIPOINT *PointRecord)
{
   return 0;
}

DLLEXPORT INT PointGetDeviceTypeNext(CTIPOINT *PointRecord)
{
   return 0;
}

DLLEXPORT INT ControlGetEqual(CONTROLMAP *CtlRecord)
{
   return 0;
}


