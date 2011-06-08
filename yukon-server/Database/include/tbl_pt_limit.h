
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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/08 14:17:03 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <float.h>

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "row_reader.h"

#include "pointtypes.h"

#define  LIMIT_IN_RANGE    0
#define  LIMIT_EXCEEDS_LO  1
#define  LIMIT_EXCEEDS_HI  2
#define  LIMIT_SETUP_ERROR 3

class IM_EX_CTIYUKONDB CtiTablePointLimit : public CtiMemDBObject
{

protected:

   LONG                 _pointID;
   INT                  _limitNumber;
   DOUBLE               _highLimit;   // Values > than this exceed the limit
   DOUBLE               _lowLimit;    // Values < than this exceed the limit
   INT                  _limitDuration;   // Number of seconds a limit must be exceeded prior to an alarm.

private:

public:

   CtiTablePointLimit(long pointid, int limitnumber);
   CtiTablePointLimit(Cti::RowReader &rdr);
   CtiTablePointLimit(const CtiTablePointLimit& aRef);
   virtual ~CtiTablePointLimit();


   CtiTablePointLimit& operator=(const CtiTablePointLimit &aRef);
   bool operator<(const CtiTablePointLimit &aRef) const;
   bool operator==(const CtiTablePointLimit &rhs) const;

   INT    getLimitNumber()   const;
   DOUBLE getHighLimit()     const;
   DOUBLE getLowLimit()      const;
   INT    getLimitDuration() const;
   LONG   getPointID()       const;

   static std::string getTableName();
   static void getSQL(std::string &sql, LONG pointID, LONG paoID, const std::set<long> &pointIds = std::set<long>());
   void dump() const;
};
#endif // #ifndef __TBL_PT_LIMIT_H__
