#include "utility.h"
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   id_devdll
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/id_devdll.h-arc  $
* REVISION     :  $Revision: 1.35 $
* DATE         :  $Date: 2002/08/16 14:00:10 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#define PROJECT   "CTI Device RTDB DLL "

#define MAJORREVISION   2
#define MINORREVISION   32
#define BUILDNUMBER     0

CTICOMPILEINFO CompileInfo = {
   PROJECT,
   MAJORREVISION,
   MINORREVISION,
   BUILDNUMBER,
   __TIMESTAMP__
};

