#pragma once

#include "row_reader.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "loggable.h"

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


class IM_EX_CTIYUKONDB CtiTablePointAccumulator : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointAccumulator(const CtiTablePointAccumulator&);
    CtiTablePointAccumulator& operator=(const CtiTablePointAccumulator&);

protected:
   /* Data Elements from Table PointAccumulator */
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
