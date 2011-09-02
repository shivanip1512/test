#pragma once

#include "dlldefs.h"

// Forward Declarations

class CtiPortManager;
class CtiDeviceManager;
class CtiPointManager;

#include "elogger.h"


extern CtiPortManager      PortManager;
extern CtiDeviceManager    DeviceManager;
extern CtiPointManager     PorterPointManager;

IM_EX_CTIBASE INT CheckUtilID (USHORT);
IM_EX_CTIBASE INT GetUtilID (PUSHORT);


