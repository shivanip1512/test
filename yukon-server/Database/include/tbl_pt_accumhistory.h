#pragma once

#include "yukon.h"
#include <string>
#include "dbmemobject.h"
#include "row_reader.h"

#include "dlldefs.h"


class IM_EX_CTIYUKONDB CtiTablePointAccumulatorHistory : public CtiMemDBObject
{
protected:

   LONG           _pointID;
   ULONG          _previousPulseCount;
   ULONG          _presentPulseCount;

public:

   CtiTablePointAccumulatorHistory(LONG pid,
                                   ULONG prevpulsecount = 0,
                                   ULONG pulsecount = 0);

   CtiTablePointAccumulatorHistory(const CtiTablePointAccumulatorHistory& aRef);
   virtual ~CtiTablePointAccumulatorHistory();
   CtiTablePointAccumulatorHistory& operator=(const CtiTablePointAccumulatorHistory& aRef);

   bool operator==(const CtiTablePointAccumulatorHistory&) const;

   virtual std::string getTableName() const;

   virtual bool Insert();
   virtual bool Update();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

   LONG getPointID() const;
   CtiTablePointAccumulatorHistory& setPointID(LONG pointID);

   ULONG getPreviousPulseCount() const;
   CtiTablePointAccumulatorHistory& setPreviousPulseCount(ULONG pc);

   ULONG getPresentPulseCount() const;
   CtiTablePointAccumulatorHistory& setPresentPulseCount(ULONG pc);
};

