/*-----------------------------------------------------------------------------*
*
* File:   selectors
*
* Class:
* Date:   8/31/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/slctpnt.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:18 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __SLCTPNT_H__
#define __SLCTPNT_H__
#pragma warning (disable : 4786)

#include <rw/db/db.h>

#include "dlldefs.h"
#include "pt_base.h"

IM_EX_PNTDB BOOL isPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isPointOnDeviceID(CtiPoint*,void*);
IM_EX_PNTDB BOOL isStatusPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isAnalogPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isAccumPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isCalcPoint(CtiPoint*,void*);

IM_EX_PNTDB BOOL isPointEqual(CtiPoint* pSp, void *arg);
IM_EX_PNTDB BOOL isAPoint(CtiPoint* pSp, void *arg);

#endif // #ifndef __SLCTPNT_H__
