#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   id_prtdll
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/id_prtdll.h-arc  $
* REVISION     :  $Revision: 1.38 $
* DATE         :  $Date: 2002/10/08 20:13:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "utility.h"
#include "id_build.h"

#define PROJECT   "CTI Port RTDB DLL "

CTICOMPILEINFO CompileInfo = {
   PROJECT,
   MAJORREVISION,
   MINORREVISION,
   BUILDNUMBER,
   __TIMESTAMP__
};

