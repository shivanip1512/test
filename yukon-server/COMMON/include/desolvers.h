#pragma once

#include "dlldefs.h"
#include <string>

IM_EX_CTIBASE std::string desolveScanType( long scanType );
IM_EX_CTIBASE std::string desolveDeviceWindowType( long aType );
IM_EX_CTIBASE std::string desolveDeviceType( int aType );
IM_EX_CTIBASE const std::string &desolvePointType( int aType );


