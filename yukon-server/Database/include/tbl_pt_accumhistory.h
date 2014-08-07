#pragma once

#include "yukon.h"
#include <string>
#include "dbmemobject.h"
#include "row_reader.h"

#include "dlldefs.h"


class IM_EX_CTIYUKONDB CtiTablePointAccumulatorHistory : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointAccumulatorHistory(const CtiTablePointAccumulatorHistory&);
    CtiTablePointAccumulatorHistory& operator=(const CtiTablePointAccumulatorHistory&);

protected:

   LONG           _pointID;
   ULONG          _previousPulseCount;
   ULONG          _presentPulseCount;

public:

   CtiTablePointAccumulatorHistory(LONG pid,
                                   ULONG prevpulsecount = 0,
                                   ULONG pulsecount = 0);

   virtual ~CtiTablePointAccumulatorHistory();

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

