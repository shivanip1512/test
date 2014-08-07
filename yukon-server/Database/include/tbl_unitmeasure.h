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


class IM_EX_CTIYUKONDB CtiTableUnitMeasure : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableUnitMeasure(const CtiTableUnitMeasure&);
    CtiTableUnitMeasure& operator=(const CtiTableUnitMeasure&);

protected:

   //string   _uomName;
   int         _calcType;
   //string   _longName;
   //string   _formula;

public:
   CtiTableUnitMeasure();
   virtual ~CtiTableUnitMeasure();

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   //string getUOMName() const;
   int       getCalcType() const;
   //string getLongName() const;
   //string getFormula() const;
};
