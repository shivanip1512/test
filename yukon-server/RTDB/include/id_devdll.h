#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   id_devdll
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/id_devdll.h-arc  $
* REVISION     :  $Revision: 1.38 $
* DATE         :  $Date: 2002/10/08 20:13:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "utility.h"
#include "id_build.h"

#define PROJECT   "CTI Device RTDB DLL "

CTICOMPILEINFO CompileInfo = {
   PROJECT,
   MAJORREVISION,
   MINORREVISION,
   BUILDNUMBER,
   __TIMESTAMP__
};

