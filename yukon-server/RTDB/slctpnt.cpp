#pragma warning (disable : 4786)
/*-----------------------------------------------------------------------------*
*
* File:   slctpnt
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctpnt.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>

#include "rtdb.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctpnt.h"
#include "yukon.h"


DLLEXPORT BOOL isPointOnDeviceID(CtiPoint *pMemoryPoint, void* d)
{
   BOOL  bRet = FALSE;

   if(*((LONG*)d) == pMemoryPoint->getDeviceID())
   {
      bRet = TRUE;
   }

   return bRet;
}

DLLEXPORT BOOL isPoint(CtiPoint *pMemoryPoint, void* d)
{
   return TRUE;
}

DLLEXPORT BOOL isMemoryPoint(CtiPoint* pSp, void *arg)
{
   BOOL bRet = TRUE;

   return bRet;
}

DLLEXPORT BOOL isStatusPoint(CtiPoint* pSp, void *arg)
{
   BOOL bRet = FALSE;

   if(pSp->getType() == StatusPointType)
   {
      bRet = TRUE;
   }

   return bRet;
}

DLLEXPORT BOOL isCalcPoint(CtiPoint* pSp, void *arg)
{
   BOOL bRet = FALSE;

   if(pSp->getType() == CalculatedPointType)
   {
      bRet = TRUE;
   }

   return bRet;
}

DLLEXPORT BOOL isAnalogPoint(CtiPoint* pSp, void *arg)
{
   BOOL bRet = FALSE;

   if(pSp->getType() == AnalogPointType)
   {
      bRet = TRUE;
   }

   return bRet;
}

DLLEXPORT BOOL isAccumPoint(CtiPoint* pSp, void *arg)
{
   BOOL bRet = FALSE;

   if(pSp->getType() == PulseAccumulatorPointType ||
      pSp->getType() == DemandAccumulatorPointType )
   {
      bRet = TRUE;
   }

   return bRet;
}

DLLEXPORT BOOL isAPoint(CtiPointBase* pSp, void *arg)
{
   BOOL bRet = TRUE;

   return bRet;
}

DLLEXPORT BOOL isPointEqual(CtiPointBase* pSp, void *arg)
{
   LONG  id = *((LONG*)arg);  // Wha tis the ID of the pointI care for!
   BOOL  bRet = FALSE;

   if(pSp->getID() == id)  bRet = TRUE;

   return bRet;
}


