#pragma warning( disable : 4786)
#ifndef __TBL_PT_ACCUMHISTORY_H__
#define __TBL_PT_ACCUMHISTORY_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_accumhistory
*
* Class:  CtiTablePointAccumulatorHistory
* Date:   8/16/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_accumhistory.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <rw\cstring.h>
#include <rw\rwtime.h>
#include <rw/db/datetime.h>

#include "dbmemobject.h"
#include "yukon.h"
#include "dlldefs.h"

class IM_EX_CTIYUKONDB CtiTablePointAccumulatorHistory : public CtiMemDBObject
{
protected:

   LONG           _pointID;
   ULONG          _previousPulseCount;
   ULONG          _presentPulseCount;

private:

public:
   CtiTablePointAccumulatorHistory(LONG pid,
                                   ULONG prevpulsecount = 0,
                                   ULONG pulsecount = 0);

   CtiTablePointAccumulatorHistory(const CtiTablePointAccumulatorHistory& aRef);
   virtual ~CtiTablePointAccumulatorHistory();
   CtiTablePointAccumulatorHistory& operator=(const CtiTablePointAccumulatorHistory& aRef);

   operator==(const CtiTablePointAccumulatorHistory&) const;

   virtual RWCString getTableName() const;

   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

   LONG getPointID() const;
   CtiTablePointAccumulatorHistory& setPointID(LONG pointID);

   ULONG getPreviousPulseCount() const;
   CtiTablePointAccumulatorHistory& setPreviousPulseCount(ULONG pc);

   ULONG getPresentPulseCount() const;
   CtiTablePointAccumulatorHistory& setPresentPulseCount(ULONG pc);
};


#endif // #ifndef __TBL_PT_ACCUMHISTORY_H__
