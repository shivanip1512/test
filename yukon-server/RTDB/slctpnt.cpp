/*-----------------------------------------------------------------------------*
*
* File:   slctpnt
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctpnt.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>

#include "rtdb.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctpnt.h"


DLLEXPORT BOOL isPointOnDeviceID(CtiPoint *pMemoryPoint, void* d)
{
   BOOL  bRet = FALSE;

   if(*((LONG*)d) == pMemoryPoint->getDeviceID())
   {
      bRet = TRUE;
   }

   return bRet;
}

DLLEXPORT BOOL isPoint(CtiPoint *pSp, void* d)
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

   if(pSp->getType() == CalculatedPointType || pSp->getType() == CalculatedStatusPointType)
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

DLLEXPORT BOOL isPointEqual(CtiPointBase* pSp, void *arg)
{
   LONG  id = *((LONG*)arg);  // Wha tis the ID of the pointI care for!
   BOOL  bRet = FALSE;

   if(pSp->getID() == id)  bRet = TRUE;

   return bRet;
}


