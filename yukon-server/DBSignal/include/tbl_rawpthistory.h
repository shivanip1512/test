#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "ctitime.h"

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"
#include "row_reader.h"
#include "database_connection.h"

class IM_EX_SIGNAL CtiTableRawPointHistory
{
protected:

   __int64     _changeID;
   LONG        _pointID;
   CtiTime      _time;
   INT         _millis;
   INT         _quality;
   DOUBLE      _value;

private:

public:

   CtiTableRawPointHistory(LONG     pid            = 0L,
                           INT      qual           = NormalQuality,
                           DOUBLE   val            = 0.0,
                           const CtiTime    &tme    = CtiTime(),
                           INT      millis         = 0,
                           __int64  cid            = ChangeIdGen()) :
      _changeID(cid),
      _pointID(pid),
      _quality(qual),
      _value(val),
      _time(tme)
   {
       setMillis(millis);
   }

   CtiTableRawPointHistory(const CtiTableRawPointHistory& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiTableRawPointHistory() {}

   CtiTableRawPointHistory& operator=(const CtiTableRawPointHistory& aRef);
   virtual RWBoolean operator<(const CtiTableRawPointHistory& aRef) const;

   virtual void Insert();
   virtual void Insert(Cti::Database::DatabaseConnection &conn);
   virtual void Restore();
   void RestoreMax();
   virtual std::string getTableName() const;

   virtual void DecodeDatabaseReader( Cti::RowReader& rdr );


   __int64                    getChangeID() const;
   LONG                       getPointID() const;
   CtiTime                     getTime() const;
   INT                        getMillis() const;
   INT                        getQuality() const;
   DOUBLE                     getValue() const;

   CtiTableRawPointHistory&   setChangeID(__int64 id);
   CtiTableRawPointHistory&   setPointID(LONG id);
   CtiTableRawPointHistory&   setTime(const CtiTime &rwt);
   CtiTableRawPointHistory&   setMillis(INT millis);
   CtiTableRawPointHistory&   setQuality(const INT &qual);
   CtiTableRawPointHistory&   setValue(const DOUBLE &val);
   CtiTableRawPointHistory&   setBookmark(const std::string &mark);
};
