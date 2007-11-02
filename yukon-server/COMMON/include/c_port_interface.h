#ifndef __C_PORT_INTERFACE_H__
#define __C_PORT_INTERFACE_H__

#include "dlldefs.h"

// Forward Declarations

class CtiPortManager;
class CtiDeviceManager;
class CtiPointManager;
class CONTROLMAP;

//#ifdef __cplusplus
//extern "C" {
//#endif

#include "group.h"
#include "elogger.h"
#include "alarmlog.h"


extern CtiPortManager      PortManager;
extern CtiDeviceManager    DeviceManager;
extern CtiPointManager     PorterPointManager;

IM_EX_CTIBASE INT PortGetGT(PORT *PortRecord);
IM_EX_CTIBASE INT PortGetFirst(PORT *PortRecord);
IM_EX_CTIBASE INT PortFastUpdate(PORT *PortRecord);
IM_EX_CTIBASE INT PortLock(PORT *PortRecord);
IM_EX_CTIBASE INT PortUnLock(PORT *PortRecord);

IM_EX_CTIBASE INT DeviceGetEqual(DEVICE *DeviceRecord);
IM_EX_CTIBASE INT DeviceGetNext(DEVICE *DeviceRecord);
IM_EX_CTIBASE INT DeviceGetGT(DEVICE *DeviceRecord);
IM_EX_CTIBASE INT DeviceFastUpdate(DEVICE *DeviceRecord);
IM_EX_CTIBASE INT DeviceLock(DEVICE *DeviceRecord);
IM_EX_CTIBASE INT DeviceUnLock(DEVICE *DeviceRecord);
IM_EX_CTIBASE INT DeviceGetFirst(DEVICE *DeviceRecord);

IM_EX_CTIBASE INT RemoteGetEqual(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteGetPortGT(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteGetPortFirst(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteGetPortEqual(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteGetGT(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteLock(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteUnLock(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteFastUpdate(REMOTE *RemoteRecord);
IM_EX_CTIBASE INT RemoteGetFirst(REMOTE *RemoteRecord);
// IM_EX_CTIBASE INT RemoteGetPortRemoteEqual(REMOTE *RemoteRecord);


IM_EX_CTIBASE INT PointGetEqual(CTIPOINT *PointRecord);
IM_EX_CTIBASE INT PointFastUpdate(CTIPOINT *PointRecord);
IM_EX_CTIBASE INT PointGetDeviceTypeFirst(CTIPOINT *PointRecord);
IM_EX_CTIBASE INT PointGetDeviceTypeNext(CTIPOINT *PointRecord);

IM_EX_CTIBASE INT ControlGetEqual(CONTROLMAP *CtlRecord);

IM_EX_CTIBASE INT VConfigGetEqual (VERSACONFIG *);
IM_EX_CTIBASE INT CheckUtilID (USHORT);
IM_EX_CTIBASE INT GetUtilID (PUSHORT);
IM_EX_CTIBASE INT RouteGetEqual (ROUTE *);
IM_EX_CTIBASE INT ComErrorLogAdd (COMM_ERROR_LOG_STRUCT *, ERRSTRUCT *, USHORT);


IM_EX_CTIBASE extern USHORT       PrintLogEvent;

//#ifdef __cplusplus
//}
//#endif

#endif      //#ifndef __C_PORT_INTERFACE_H__


