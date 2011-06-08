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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


DLLEXPORT void      ReloadStateNames(void);
DLLEXPORT std::string ResolveStateName(LONG grpid, LONG rawValue);

#endif // #ifndef __DLLYUKON_H__
