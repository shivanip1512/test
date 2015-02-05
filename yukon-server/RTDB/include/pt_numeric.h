#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include "row_reader.h"

#include <limits.h>
#include "yukon.h"
#include "dlldefs.h"
#include "pt_base.h"
#include "resolvers.h"
#include "tbl_pt_unit.h"

class IM_EX_PNTDB CtiPointNumeric : public CtiPointBase
{
   CtiTablePointUnit    _pointUnits;

public:

   typedef CtiPointBase       Inherited;

   CtiPointNumeric();

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   const CtiTablePointUnit &getPointUnits() const;

   virtual DOUBLE      getMultiplier() const;
   virtual DOUBLE      getDataOffset() const;

   virtual UINT adjustStaticTags(UINT &tag) const;

   virtual DOUBLE computeValueForUOM(DOUBLE Value) const;

};

typedef shared_ptr< CtiPointNumeric > CtiPointNumericSPtr;
