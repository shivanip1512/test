
#pragma warning( disable : 4786)
#ifndef __TBL_PT_UNIT_H__
#define __TBL_PT_UNIT_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_unit
*
* Class:  CtiTablePointUnit
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_unit.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/07/13 21:14:11 $
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

#include "tbl_unitmeasure.h"

class IM_EX_CTIYUKONDB CtiTablePointUnit : public CtiMemDBObject
{
protected:

   LONG                 _pointID;
   INT                  _unitID;            // FK constraint against table  UnitMeasure
   INT                  _decimalPlaces;     // Number of " " for display purposes.
   INT                  _decimalDigits;     // Maximum digits allowed (forced rollover).
   DOUBLE               _highReasonablityLimit;
   DOUBLE               _lowReasonablityLimit;

   CtiTableUnitMeasure  _unitMeasure;

   INT                  _calcType;          // What calculation is required to compute the units defined above.
   INT                  _logFrequency;
   DOUBLE               _defaultValue;

private:

public:

   CtiTablePointUnit();
   CtiTablePointUnit(const CtiTablePointUnit& aRef);
   virtual ~CtiTablePointUnit();


   CtiTablePointUnit&   operator=(const CtiTablePointUnit& aRef);

   INT                  getUnitID() const;
   CtiTablePointUnit&   setUnitID(const INT &id);

   INT                  getDecimalPlaces() const;
   CtiTablePointUnit&   setDecimalPlaces(const INT &id);
   INT                  getDecimalDigits() const;
   CtiTablePointUnit&   setDecimalDigits(const INT &digits);
   DOUBLE               getHighReasonabilityLimit() const;
   CtiTablePointUnit&   setHighReasonabilityLimit(DOUBLE d);
   DOUBLE               getLowReasonabilityLimit() const;
   CtiTablePointUnit&   setLowReasonabilityLimit(DOUBLE d);

   INT                  getCalcType() const;
   CtiTablePointUnit&   setCalcType(INT i);

   INT                  getLogFrequency() const;
   CtiTablePointUnit&   setLogFrequency(INT i);

   DOUBLE               getDefaultValue() const;
   CtiTablePointUnit&   setDefaultValue(DOUBLE d);

   LONG                 getPointID() const;
   CtiTablePointUnit&   setPointID( LONG pid );

   CtiTableUnitMeasure  getUnitMeasure() const;
   CtiTableUnitMeasure& getUnitMeasure();

   static void          getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   void                 DecodeDatabaseReader(RWDBReader &rdr);
   void                 dump() const;
   static string     getTableName();
};

#endif // #ifndef __TBL_PT_UNIT_H__
