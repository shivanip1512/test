#pragma once

#include "row_reader.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "loggable.h"

#include <limits.h>


class IM_EX_CTIYUKONDB CtiTableUnitMeasure : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
   int         _calcType;

public:
   CtiTableUnitMeasure();
   virtual ~CtiTableUnitMeasure();

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual std::string toString() const override;

   int       getCalcType() const;
};
