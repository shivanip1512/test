/*-----------------------------------------------------------------------------*
*
* File:   pt_numeric
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_numeric.h-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_NUMERIC_H__
#define __PT_NUMERIC_H__
#pragma warning( disable : 4786)

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
protected:
   CtiTablePointUnit    _pointUnits;

   INT                  _rateOfChange;

protected:

public:

   typedef CtiPointBase       Inherited;

   CtiPointNumeric();
   CtiPointNumeric(const CtiPointNumeric& aRef);
   virtual ~CtiPointNumeric();

   CtiPointNumeric& operator=(const CtiPointNumeric& aRef);
   
   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   INT getRateOfChange() const;
   CtiPointNumeric setRateOfChange(INT rate);

   CtiTablePointUnit   getPointUnits() const;
   CtiTablePointUnit&  getPointUnits();

   virtual DOUBLE      getMultiplier() const;
   virtual DOUBLE      getDataOffset() const;

   virtual void        setMultiplier(DOUBLE d);
   virtual void        setDataOffset(DOUBLE d);

   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;

   virtual void DumpData();

   virtual DOUBLE computeValueForUOM(DOUBLE Value) const;

};

typedef CtiPointNumeric    CtiPointPseudoAnalog;
typedef CtiPointNumeric    CtiPointCalculated;

typedef shared_ptr< CtiPointNumeric > CtiPointNumericSPtr;

#endif // #ifndef __PT_NUMERIC_H__

