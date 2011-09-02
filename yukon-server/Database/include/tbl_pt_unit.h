#pragma once

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
