#include "utility.h"
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   id_tcpdll
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/id_tcpdll.h-arc  $
* REVISION     :  $Revision: 1.35 $
* DATE         :  $Date: 2002/09/06 21:28:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "utility.h"
#include "id_build.h"


#define PROJECT   "CTI TCPSup DLL "

CTICOMPILEINFO CompileInfo = {
   PROJECT,
   MAJORREVISION,
   MINORREVISION,
   BUILDNUMBER,
   __TIMESTAMP__
};

