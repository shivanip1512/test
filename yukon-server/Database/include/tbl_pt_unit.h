#pragma once

#include "row_reader.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "loggable.h"
#include "tbl_unitmeasure.h"

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


class IM_EX_CTIYUKONDB CtiTablePointUnit : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointUnit(const CtiTablePointUnit&);
    CtiTablePointUnit& operator=(const CtiTablePointUnit&);

protected:

   INT                  _unitID;            // FK constraint against table  UnitMeasure
   INT                  _decimalPlaces;     // Number of " " for display purposes.
   INT                  _decimalDigits;     // Maximum digits allowed (forced rollover).

   CtiTableUnitMeasure  _unitMeasure;

   INT                  _logFrequency;

public:

   CtiTablePointUnit();
   virtual ~CtiTablePointUnit();

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

   const CtiTableUnitMeasure& getUnitMeasure() const;
   CtiTableUnitMeasure& getUnitMeasure();

   void                 DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual std::string toString() const override;
   static std::string     getTableName();
};
