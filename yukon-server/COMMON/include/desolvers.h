
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:37 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "db_entry_defines.h"
#include "yukon.h"
#include "dlldefs.h"
#include "types.h"
#include "pointtypes.h"

class RWCString;

IM_EX_CTIBASE RWCString desolveScanType( LONG scanType );
IM_EX_CTIBASE RWCString desolveStatisticsType( INT statType );
IM_EX_CTIBASE RWCString desolveAmpUseType( INT ampUseType );
IM_EX_CTIBASE RWCString desolveDeviceWindowType( LONG aType );
IM_EX_CTIBASE RWCString desolvePAOCategory( INT aCategory );
IM_EX_CTIBASE RWCString desolveDeviceType( INT aType );
IM_EX_CTIBASE RWCString desolvePortType( INT aType );
IM_EX_CTIBASE RWCString desolveRouteType( INT aType );
IM_EX_CTIBASE RWCString desolveLoadManagementType( INT aType );
IM_EX_CTIBASE RWCString desolveCapControlType( INT aType );


