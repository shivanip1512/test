/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_timing
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_timing.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:16 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_TIMING_H__
#define __TBL_PORT_TIMING_H__

#include <limits.h>
#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTablePortTimings : public CtiMemDBObject
{

protected:

   // PortTimings Table
   LONG           _portID;
   ULONG          _delays[LAST_DELAY];


private:

public:

   CtiTablePortTimings();

   CtiTablePortTimings(const CtiTablePortTimings& aRef);

   virtual ~CtiTablePortTimings();

   CtiTablePortTimings& operator=(const CtiTablePortTimings& aRef);

   CtiTablePortTimings&  setDelay(int Offset, int D);

   ULONG  getDelay(int Offset) const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();
};
#endif // #ifndef __TBL_PORT_TIMING_H__
