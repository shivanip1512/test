
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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/08/25 19:47:32 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "row_reader.h"

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

#include "tbl_unitmeasure.h"

class IM_EX_CTIYUKONDB CtiTablePointUnit : public CtiMemDBObject
{
protected:

   INT                  _unitID;            // FK constraint against table  UnitMeasure
   INT                  _decimalPlaces;     // Number of " " for display purposes.
   INT                  _decimalDigits;     // Maximum digits allowed (forced rollover).

   CtiTableUnitMeasure  _unitMeasure;

   INT                  _logFrequency;

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

   INT                  getCalcType() const;
   CtiTablePointUnit&   setCalcType(INT i);

   INT                  getLogFrequency() const;
   CtiTablePointUnit&   setLogFrequency(INT i);

   CtiTableUnitMeasure  getUnitMeasure() const;
   CtiTableUnitMeasure& getUnitMeasure();

   void                 DecodeDatabaseReader(Cti::RowReader &rdr);
   void                 dump() const;
   static std::string     getTableName();
};

#endif // #ifndef __TBL_PT_UNIT_H__
