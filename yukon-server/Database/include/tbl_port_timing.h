#pragma once

#include <limits.h>
#include "row_reader.h"
#include <limits.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTablePortTimings : public CtiMemDBObject
{

protected:

   // PortTimings Table
   ULONG          _delays[LAST_DELAY];

private:

public:

   CtiTablePortTimings();

   CtiTablePortTimings(const CtiTablePortTimings& aRef);

   virtual ~CtiTablePortTimings();

   CtiTablePortTimings& operator=(const CtiTablePortTimings& aRef);

   CtiTablePortTimings&  setDelay(int Offset, int D);

   ULONG  getDelay(int Offset) const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();
};
