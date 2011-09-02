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


class IM_EX_CTIYUKONDB CtiTablePointAnalog : public CtiMemDBObject
{
protected:

   DOUBLE      _multiplier;
   DOUBLE      _dataOffset;
   DOUBLE      _deadband;
   //string   _transducerType;

private:

public:
   CtiTablePointAnalog();

   CtiTablePointAnalog(const CtiTablePointAnalog& aRef);
   virtual ~CtiTablePointAnalog();

   CtiTablePointAnalog& operator=(const CtiTablePointAnalog& aRef);

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   DOUBLE      getMultiplier() const;
   DOUBLE      getDataOffset() const;
   DOUBLE      getDeadband() const;
   //string   getTransducerType() const;

   CtiTablePointAnalog& setMultiplier(DOUBLE d);
   CtiTablePointAnalog& setDataOffset(DOUBLE d);
   CtiTablePointAnalog& setDeadband(DOUBLE d);
   //CtiTablePointAnalog& setTransducerType(string &str);

};
