/*-----------------------------------------------------------------------------*
*
* File:   desolvers
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/desolvers.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:25:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "db_entry_defines.h"
#include "yukon.h"
#include "dlldefs.h"
#include "types.h"
#include "pointtypes.h"
#include <string>

using std::string;

IM_EX_CTIBASE string desolveScanType( LONG scanType );
IM_EX_CTIBASE string desolveStatisticsType( INT statType );
IM_EX_CTIBASE string desolveAmpUseType( INT ampUseType );
IM_EX_CTIBASE string desolveDeviceWindowType( LONG aType );
IM_EX_CTIBASE string desolvePAOCategory( INT aCategory );
IM_EX_CTIBASE string desolveDeviceType( INT aType );
IM_EX_CTIBASE string desolvePortType( INT aType );
IM_EX_CTIBASE string desolveRouteType( INT aType );
IM_EX_CTIBASE string desolveLoadManagementType( INT aType );
IM_EX_CTIBASE string desolveCapControlType( INT aType );


