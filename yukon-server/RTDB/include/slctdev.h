#pragma once

#include "dev_base.h"
#include "dlldefs.h"
#include "rte_base.h"
#include "row_reader.h"

class SACommand;

IM_EX_DEVDB CtiDeviceBase *DeviceFactory(Cti::RowReader &rdr);
IM_EX_DEVDB CtiDeviceBase *createDeviceType(int type);
IM_EX_DEVDB CtiRouteBase  *RouteFactory(Cti::RowReader &rdr);

IM_EX_DEVDB bool isCarrierLPDeviceType(const int type);
