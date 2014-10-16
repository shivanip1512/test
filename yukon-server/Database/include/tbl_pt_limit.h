#pragma once

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "row_reader.h"
#include "loggable.h"
#include "pointtypes.h"

#include <float.h>
#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


#define  LIMIT_IN_RANGE    0
#define  LIMIT_EXCEEDS_LO  1
#define  LIMIT_EXCEEDS_HI  2
#define  LIMIT_SETUP_ERROR 3


class IM_EX_CTIYUKONDB CtiTablePointLimit : public CtiMemDBObject, public Cti::Loggable
{

protected:

   LONG                 _pointID;
   INT                  _limitNumber;
   DOUBLE               _highLimit;   // Values > than this exceed the limit
   DOUBLE               _lowLimit;    // Values < than this exceed the limit
   INT                  _limitDuration;   // Number of seconds a limit must be exceeded prior to an alarm.

private:

public:

   CtiTablePointLimit(long pointid, int limitnumber);
   CtiTablePointLimit(Cti::RowReader &rdr);
   CtiTablePointLimit(const CtiTablePointLimit& aRef);
   virtual ~CtiTablePointLimit();


   CtiTablePointLimit& operator=(const CtiTablePointLimit &aRef);
   bool operator<(const CtiTablePointLimit &aRef) const;
   bool operator==(const CtiTablePointLimit &rhs) const;

   INT    getLimitNumber()   const;
   DOUBLE getHighLimit()     const;
   DOUBLE getLowLimit()      const;
   INT    getLimitDuration() const;
   LONG   getPointID()       const;

   static std::string getTableName();
   static void getSQL(std::string &sql, LONG pointID, LONG paoID, const std::set<long> &pointIds = std::set<long>());
   virtual std::string toString() const override;
};
