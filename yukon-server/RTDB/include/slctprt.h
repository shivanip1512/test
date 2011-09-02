#pragma once

#include "dlldefs.h"
#include "dev_base.h"
#include "rte_base.h"
#include "pt_base.h"
#include "port_base.h"

IM_EX_PRTDB CtiPort*       PortFactory(Cti::RowReader &rdr);
