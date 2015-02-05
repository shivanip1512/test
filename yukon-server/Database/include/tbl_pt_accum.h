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


class IM_EX_CTIYUKONDB CtiTablePointAccumulator : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
   LONG        _pointID;
   DOUBLE      _multiplier;
   DOUBLE      _dataOffset;

public:

   CtiTablePointAccumulator();
   virtual ~CtiTablePointAccumulator();

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual std::string toString() const override;

   DOUBLE getMultiplier() const;
   DOUBLE getDataOffset() const;
   LONG getPointID() const;
   static std::string getTableName();

   CtiTablePointAccumulator& setMultiplier(DOUBLE d);
   CtiTablePointAccumulator& setDataOffset(DOUBLE d);
   CtiTablePointAccumulator& setPointID(const LONG ptid);

};
