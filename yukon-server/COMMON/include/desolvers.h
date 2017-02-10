#pragma once

#include "dlldefs.h"
#include "pointtypes.h"
#include "pointdefs.h"

#include <string>

IM_EX_CTIBASE std::string desolveScanType( long scanType );
IM_EX_CTIBASE std::string desolveDeviceWindowType( long aType );
IM_EX_CTIBASE std::string desolveDeviceType( int aType );
IM_EX_CTIBASE const std::string &desolvePointType( int aType );
IM_EX_CTIBASE const std::string desolvePointQuality( PointQuality_t quality );
IM_EX_CTIBASE std::string desolveControlType( CtiControlType_t type );


