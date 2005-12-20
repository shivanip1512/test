
#pragma warning( disable : 4786)
#ifndef __TBL_PT_ACCUM_H__
#define __TBL_PT_ACCUM_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_accum
*
* Class:  CtiTablePointAccumulator
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_accum.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTablePointAccumulator : public CtiMemDBObject
{

protected:
   /* Data Elements from Table PointAccumulator */
   LONG        _pointID;
   DOUBLE      _multiplier;
   DOUBLE      _dataOffset;

private:

public:

   CtiTablePointAccumulator();
   CtiTablePointAccumulator(const CtiTablePointAccumulator& aRef);
   virtual ~CtiTablePointAccumulator();

   CtiTablePointAccumulator& operator=(const CtiTablePointAccumulator& aRef);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   void DecodeDatabaseReader(RWDBReader &rdr);
   void dump() const;

   DOUBLE getMultiplier() const;
   DOUBLE getDataOffset() const;
   LONG getPointID() const;
   static string getTableName();

   CtiTablePointAccumulator& setMultiplier(DOUBLE d);
   CtiTablePointAccumulator& setDataOffset(DOUBLE d);
   CtiTablePointAccumulator& setPointID(const LONG ptid);

};
#endif // #ifndef __TBL_PT_ACCUM_H__
