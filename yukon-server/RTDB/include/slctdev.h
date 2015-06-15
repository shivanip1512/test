#pragma once

#include "dlldefs.h"
#include "row_reader.h"

class CtiDeviceBase;
class CtiRouteBase;
class SACommand;

IM_EX_DEVDB CtiDeviceBase *DeviceFactory(Cti::RowReader &rdr);
IM_EX_DEVDB CtiDeviceBase *createDeviceType(int type);
IM_EX_DEVDB CtiRouteBase  *RouteFactory(Cti::RowReader &rdr);

IM_EX_DEVDB bool isCarrierLPDeviceType(const int type);
IM_EX_DEVDB bool isDnpDeviceType(const int type);
