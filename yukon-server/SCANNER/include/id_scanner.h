#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   id_scanner
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/INCLUDE/id_scanner.h-arc  $
* REVISION     :  $Revision: 1.35 $
* DATE         :  $Date: 2002/09/13 15:08:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "utility.h"
#include "id_build.h"

#define PROJECT   "CTI Scanner"

CTICOMPILEINFO CompileInfo = {
   PROJECT,
   MAJORREVISION,
   MINORREVISION,
   BUILDNUMBER,
   BUILDTIMESTAMP
};

