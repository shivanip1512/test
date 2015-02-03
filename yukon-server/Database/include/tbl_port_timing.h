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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePortTimings(const CtiTablePortTimings&);
    CtiTablePortTimings& operator=(const CtiTablePortTimings&);

protected:

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
