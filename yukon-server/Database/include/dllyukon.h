#pragma warning( disable : 4786)
#ifndef __DLLYUKON_H__
#define __DLLYUKON_H__

/*-----------------------------------------------------------------------------*
*
* File:   dllyukon
*
* Date:   10/5/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/dllyukon.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/11/18 16:44:33 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

DLLEXPORT void      ReloadStateNames(void);
DLLEXPORT RWCString ResolveStateName(LONG grpid, LONG rawValue);

#endif // #ifndef __DLLYUKON_H__
