
#pragma warning( disable : 4786)
#ifndef __TBL_PT_LIMIT_H__
#define __TBL_PT_LIMIT_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_limit.h
*
* Class:  CtiTablePointLimit
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_limit.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <float.h>

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>
#include <rw\rwtime.h>
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

#include "pointtypes.h"


class IM_EX_CTIYUKONDB CtiTablePointLimit : public CtiMemDBObject
{

protected:

   LONG                 _pointID;
   INT                  _limitNumber;
   DOUBLE               _highLimit;   // Values > Thsn this exceed the limit
   DOUBLE               _lowLimit;    // Values < than this exceed the limit
   INT                  _limitDuration;   // Number of seconds a limit must be exceeded prior to an alarm.

private:

public:

   CtiTablePointLimit();
   CtiTablePointLimit(const CtiTablePointLimit& aRef);
   virtual ~CtiTablePointLimit();


   CtiTablePointLimit& operator=(const CtiTablePointLimit& aRef);

   INT getLimitNumber() const;
   DOUBLE getHighLimit() const;
   DOUBLE getLowLimit() const;
   INT getLimitDuration() const;
   LONG getPointID() const;

   // setters
   CtiTablePointLimit& setLimitNumber( const INT lin );
   CtiTablePointLimit& setPointID(const LONG ptid);
   CtiTablePointLimit& setHighLimit(DOUBLE d);
   CtiTablePointLimit& setLowLimit(DOUBLE d);
   CtiTablePointLimit& setLimitDuration(const INT aInt);

   static RWCString getTableName();
   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   void DecodeDatabaseReader(RWDBReader &rdr);
   void dump() const;
};
#endif // #ifndef __TBL_PT_LIMIT_H__
