#pragma once

#include <limits.h>
#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTablePortTimings : public CtiMemDBObject, private boost::noncopyable
{
   // PortTimings Table
   ULONG          _delays[LAST_DELAY];

public:

   CtiTablePortTimings();
   virtual ~CtiTablePortTimings();

   CtiTablePortTimings&  setDelay(int Offset, int D);

   ULONG  getDelay(int Offset) const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();
};
