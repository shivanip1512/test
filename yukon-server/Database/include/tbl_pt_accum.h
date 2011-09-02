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


class IM_EX_CTIYUKONDB CtiTablePointAccumulator : public CtiMemDBObject
{

protected:
   /* Data Elements from Table PointAccumulator */
   LONG        _pointID;
   DOUBLE      _multiplier;
   DOUBLE      _dataOffset;

private:

public:

   CtiTablePointAccumulator();
   CtiTablePointAccumulator(const CtiTablePointAccumulator& aRef);
   virtual ~CtiTablePointAccumulator();

   CtiTablePointAccumulator& operator=(const CtiTablePointAccumulator& aRef);

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   DOUBLE getMultiplier() const;
   DOUBLE getDataOffset() const;
   LONG getPointID() const;
   static std::string getTableName();

   CtiTablePointAccumulator& setMultiplier(DOUBLE d);
   CtiTablePointAccumulator& setDataOffset(DOUBLE d);
   CtiTablePointAccumulator& setPointID(const LONG ptid);

};
