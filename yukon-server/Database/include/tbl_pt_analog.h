
#pragma warning( disable : 4786)
#ifndef __TBL_PT_ANALOG_H__
#define __TBL_PT_ANALOG_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_analog
*
* Class:  CtiTablePointAnalog;
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_analog.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/06/30 15:24:29 $
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


class IM_EX_CTIYUKONDB CtiTablePointAnalog : public CtiMemDBObject
{
protected:

   DOUBLE      _multiplier;
   DOUBLE      _dataOffset;
   DOUBLE      _deadband;
   //string   _transducerType;

private:

public:
   CtiTablePointAnalog();

   CtiTablePointAnalog(const CtiTablePointAnalog& aRef);
   virtual ~CtiTablePointAnalog();

   CtiTablePointAnalog& operator=(const CtiTablePointAnalog& aRef);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   void DecodeDatabaseReader(RWDBReader &rdr);
   void dump() const;

   DOUBLE      getMultiplier() const;
   DOUBLE      getDataOffset() const;
   DOUBLE      getDeadband() const;
   //string   getTransducerType() const;

   CtiTablePointAnalog& setMultiplier(DOUBLE d);
   CtiTablePointAnalog& setDataOffset(DOUBLE d);
   CtiTablePointAnalog& setDeadband(DOUBLE d);
   //CtiTablePointAnalog& setTransducerType(string &str);

};
#endif // #ifndef __TBL_PT_ANALOG_H__
