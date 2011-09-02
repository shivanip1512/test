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


class IM_EX_CTIYUKONDB CtiTableUnitMeasure : public CtiMemDBObject
{
protected:

   //string   _uomName;
   int         _calcType;
   //string   _longName;
   //string   _formula;

private:

public:
   CtiTableUnitMeasure();

   CtiTableUnitMeasure(const CtiTableUnitMeasure& aRef);
   virtual ~CtiTableUnitMeasure();

   CtiTableUnitMeasure& operator=(const CtiTableUnitMeasure& aRef);

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   //string getUOMName() const;
   int       getCalcType() const;
   //string getLongName() const;
   //string getFormula() const;
};
