#pragma warning( disable : 4786 )

#ifndef __MGR_POINT_H__
#define __MGR_POINT_H__
/*************************************************************************
 *
 * mgr_route.h      7/7/99
 *
 *****
 *
 * The class which owns and manages route real time database
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_point.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:29 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/

#include <rw/db/connect.h>

#include "dlldefs.h"
#include "rtdb.h"
#include "pt_base.h"
#include "slctpnt.h"

/*
 *  The following functions may be used to create sublists for the points in our database.
 */

class IM_EX_PNTDB CtiPointManager : public CtiRTDB<CtiPoint>
{
private:

   // Inherit "List" from Parent

   // RWDBConnection conn;

public:
   CtiPointManager();
   virtual ~CtiPointManager();


   virtual void RefreshList(BOOL (*fn)(CtiPointBase*,void*) = isAPoint, void *d = NULL);
   virtual void RefreshList( LONG );

   virtual void RefreshPoints(RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
   void RefreshPointLimits(RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
   void RefreshCalcElements(RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
   void RefreshAlarming();

   virtual void DumpList(void);
   virtual void DeleteList(void);

   CtiPoint* getControlOffsetEqual(LONG pao, INT Offset);
   CtiPoint* getOffsetTypeEqual(LONG pao, INT Offset, INT Type);
   CtiPoint* getEqual(LONG Pt);
   CtiPoint* getEqualByName(LONG pao, RWCString pname);

};

#endif                  // #ifndef __MGR_POINT_H__
